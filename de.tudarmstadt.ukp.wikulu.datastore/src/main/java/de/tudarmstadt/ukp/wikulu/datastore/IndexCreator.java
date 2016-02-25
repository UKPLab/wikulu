/*******************************************************************************
 * Copyright 2010-2016 Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *******************************************************************************/
package de.tudarmstadt.ukp.wikulu.datastore;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class Used to initialize config parameters and start index process
 * 
 * @author a_vovk
 * 
 */
public class IndexCreator
{
	// wiki base url
	private String baseURL = null;
	// url to the wiki
	private String wikiURL = null;
	// directory to make index
	private String targetDirectory = null;
	// stream with stop words
	private InputStream stopWordsStream = null;
	// type of wiki: twiki, mediawiki
	private String wikiType = null;
	// url to the wiki api
	private String apiURL = null;
	// type of lucene directory to use: RAM or FS(File System)
	private String directoryType;
	
	// TWIKI
	// only for twiki
	private String webName = null;
	// only for twiki
	private String excludedPagePrefix = null;
	// only for twiki
	//private String login = null;
	// only for twiki
	//private String pass = null;
	// only for twiki
	private String viewBasePath = null;
	//
	private String authData;

	// Command line options
	private Options cliOpt = null;

	public static ResourceBundle properties;
	
	static {
		properties = PropertyResourceBundle.getBundle("wikulu");	
	}
	
	public static String getLuceneIndexDirectory(){
		return new File(properties.getString("index.targetDirectory")).getAbsolutePath();
	}
	
	

	public static void main(String[] args)
		throws FileNotFoundException
	{
		IndexCreator index = new IndexCreator();

		// Check CLI if there are some arguments
		if (args.length != 0)
			index.initCLIArguments(args);

		// Start index creation
		index.createIndex();
	}

	/**
	 * Gets all config parameters from properties file
	 */
	public IndexCreator()
		throws FileNotFoundException
	{
		baseURL = properties.getString("wikulu.targetPlatformBaseURL");
		wikiURL = baseURL + properties.getString("wikulu.targetPlatformWikiURL");
		apiURL = baseURL + properties.getString("wikulu.targetPlatformAPIURL");
		
		targetDirectory = properties.getString("index.targetDirectory");
		
		stopWordsStream = this.getClass().getResourceAsStream(properties.getString("index.stopwordFile"));
		
//		stopWordsStream = new FileInputStream(
//				properties.getString("index.wiki.stopwordFile"));

		wikiType = properties.getString("wikulu.targetWikiPlatform");
		webName = properties.getString("index.twiki.webName");
		excludedPagePrefix = properties
				.getString("index.twiki.excludedPagePrefix");
		if (excludedPagePrefix.equals("null")) {
			excludedPagePrefix = null;
		}
//		login = properties.getString("index.twiki.login");
//		pass = properties.getString("index.twiki.pass");
		directoryType = properties.getString("index.directoryType");
	}

	/**
	 * Method's that creates index according to wikitype
	 */
	public void createIndex()
	{
		if (wikiType.equals("twiki")) {
			try {
				new TWikiIndexCreator(wikiURL, apiURL, targetDirectory,
						stopWordsStream, webName, excludedPagePrefix, authData,
						viewBasePath, directoryType).createIndex();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				new MediaWikiIndexCreator(wikiURL, apiURL, targetDirectory,
						stopWordsStream, directoryType).createIndex();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	public void createIndex(String authData) {
		try {
			
			JSONObject jAuth = new JSONObject(authData);
			String cookie = jAuth.getString("authData");
			this.authData = cookie;
			createIndex();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}

	private void addCLIOption(String opt, String longOpt, boolean hasArg,
			String description, String argName)
	{
		Option option = new Option(opt, longOpt, hasArg, description);
		option.setArgName(argName);
		cliOpt.addOption(option);
	}

	/**
	 * Initialize and get's CLI Arguments
	 * 
	 * @param args
	 */
	public void initCLIArguments(String[] args)
	{

		// Command line arguments
		// -tw Type of wiki to use - TWiki(don't user together with -mw)
		// -mw Type of wiki to use - MediaWiki(don't user together with -tw)
		// -w The webspaces to use(Only for TWiki)
		// -a The URL to wiki API
		// -u The URL to use to create index!
		// -e The prefix to exclude(Only for TWiki)
		// -d The directory to create index
		// -h Prints help information
		// -gp Prints all parametrs for index creation(parameters from
		// index.properties)
		// -p The password to use(only for twiki)
		// -l The login to use(only for twiki)
		// -v View base path(only for twiki)
		cliOpt = new Options();
		cliOpt.addOption("h", "help", false, "Prints help information");
		cliOpt.addOption("gp", "getparams", false,
				"Prints all parametrs for index creation(parameters from index.properties)");
		cliOpt.addOption("tw", "twiki", false,
				"Type of wiki to use - TWiki(don't user together with -mw)");
		cliOpt.addOption("mw", "mwiki", false,
				"Type of wiki to use - MediaWiki(don't user together with -tw)");

		// web
		addCLIOption("w", "web", true, "The webspaces to use(Only for TWiki)",
				"names");
		// url
		addCLIOption("u", "url", true, "The URL to use to create index!",
				"link");
		// exclpref
		addCLIOption("e", "exclpref", true,
				"The prefix to exclude(Only for TWiki)", "prefix");
		// directory
		addCLIOption("d", "dir", true, "The directory to create index",
				"direcotry");
		// password
		addCLIOption("p", "pass", true, "The password to use(only for twiki)",
				"password");
		// login
		addCLIOption("l", "login", true, "The login to use(only for twiki)",
				"login");
		// viewbasepath
		addCLIOption("v", "vbpath", true, "View base path(only for twiki)",
				"path");
		// api URL
		addCLIOption("a", "apiurl", true, "The API URL of the wiki", "apiurl");
		
		// directory Type
		addCLIOption("dt", "dirtype", true, "The Type of Lucene Directory to Use(ram or fs)", "dirtype");

		CommandLineParser parser = new PosixParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(cliOpt, args);
			if (line.hasOption("tw") && line.hasOption("mw")) {
				throw new ParseException(
						"The options tw and mw can't be used together!");
			}
			if (line.hasOption("tw")) {
				this.wikiType = "twiki";
			}
			if (line.hasOption("mw")) {
				this.wikiType = "mediawiki";
			}
			if (line.hasOption("w")) {
				this.webName = line.getOptionValue("w");
			}
			if (line.hasOption("a")) {
				this.wikiURL = line.getOptionValue("a");
			}
			if (line.hasOption("u")) {
				this.wikiURL = line.getOptionValue("u");
			}
			if (line.hasOption("e")) {
				this.excludedPagePrefix = line.getOptionValue("e");
			}
			if (line.hasOption("d")) {
				this.targetDirectory = line.getOptionValue("d");
			}
			if (line.hasOption("p")) {
//				this.pass = line.getOptionValue("p");
			}
			if (line.hasOption("v")) {
				this.viewBasePath = line.getOptionValue("v");
			}
			if (line.hasOption("l")) {
//				this.login = line.getOptionValue("l");
			}
			if (line.hasOption("dt")) {
				this.directoryType = line.getOptionValue("dt");
			}
			if (line.hasOption("h")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("indexcreator", cliOpt);
				System.exit(0);
			}
			if (line.hasOption("gp")) {
				System.out.println("Configuaration parameters:");
				System.out.println("Type of wiki: " + this.wikiType);
				System.out.println("URL to use to create the index: "
						+ this.wikiURL);
				System.out.println("API URL to use to create the index: "
						+ this.apiURL);
				System.out.println("Directory to store the index: "
						+ this.targetDirectory);
				System.out
						.println("WebSpaces from which to create the index(for twiki): "
								+ this.webName);
				System.out.println("Page prefix to exclude(for twiki): "
						+ this.excludedPagePrefix);
				System.exit(0);
			}

		}
		catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.err.println("The default parameters will be used!");
		}
		//
	}
}
