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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.tudarmstadt.ukp.wikiapi.TWiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;

/**
 * This class gives an API for TWiki lucene index creation.
 *
 * @author a_vovk
 *
 */
public class TWikiIndexCreator
	extends WikiIndexCreator
{

	// viewURL for TWiki
	private String viewURL = null;
	// List of webNames to consider for indexing
	private final ArrayList<String> webNames = new ArrayList<String>();
	// PagePrefix to exclude
	private String excludedPagePrefix = null;
	// sessionID
	private String authData;
	
	

	/**
	 * Sets excluded Page Prefix
	 *
	 * @param excludedPagePrefix
	 */
	public void setExcludedPagePrefix(String excludedPagePrefix)
	{
		this.excludedPagePrefix = excludedPagePrefix;
	}

	/**
	 * Parse webName string and save results into webNames
	 *
	 * @param webName
	 *            string with web names to parse
	 */
	public void setWebName(String webName)
	{
		webNames.clear();
		if (webName.equals("null")) {
			webName = null;
		}
		else {
			// "([\\w]*)=([\\w]*);"
			Pattern params = Pattern.compile("([\\w]+)[;]*");
			Matcher matcher = params.matcher(webName.trim());
			while (matcher.find()) {
				webNames.add(matcher.group(1));
			}
		}
	}

	/**
	 * Page filter
	 *
	 * @param pageURL
	 * @param excludedPagePrefix
	 * @return
	 */
	private boolean pageIsExcluded(String pageURL, String excludedPagePrefix)
	{
		if (excludedPagePrefix == null) {
			return false;
		}
		String[] parts = pageURL.split("/");
		return parts[parts.length - 1].startsWith(excludedPagePrefix);
	}

	/**
	 * Constructor
	 *
	 * @param wikiURL
	 *            URL to the wiki page
	 * @param apiURL
	 *            URL to the wiki api
	 * @param targetDirectory
	 *            directory for index files
	 * @param stopWordsStream
	 *            Stream of stopwords
	 * @param webName
	 *            WebName to use
	 * @param excludedPagePrefix
	 *            pagePrefix to exclude
	 * @param login
	 *            auth. info.
	 * @param pass
	 *            auth. info.
	 * @param viewBasePath
	 *            basePath of TWiki
	 * @throws IOException 
	 */
	public TWikiIndexCreator(String wikiURL, String apiURL,
			String targetDirectory, InputStream stopWordsStream,
			String webName, String excludedPagePrefix, String authData, String viewBasePath,
			String dirType) throws IOException
	{
		// init
		super(wikiURL, apiURL, targetDirectory, stopWordsStream, dirType);
		this.setWebName(webName);
		this.excludedPagePrefix = excludedPagePrefix;
		this.authData = authData;
		this.viewURL = wikiURL;
		
	}

	@Override
	public void createIndex()
	{
		try {
			// FIXME this constructor does not longer exist
			// TWiki wiki = new TWiki(new URL(getWikiURL()), login, pass,
			// viewBasePath);
			TWiki wiki;
			if (authData == null) {
				wiki = new TWiki(new URL(this.apiURL), new URL(this.wikiURL));
			}
			else {
				wiki = new TWiki(new URL(this.apiURL), new URL(this.wikiURL), this.authData);
			}
//			IndexWriter writer = null;
			int counter = 0;
			
					
			
			List<Page> pages = new ArrayList<Page>();
			if (webNames.size() == 0) {
				pages = wiki.getPages();
			}
			else {
				// add all pages from webNames
				for (String i : webNames) {
					pages.addAll(wiki.getPagesForSpace(i));
				}
			}
			// int testCounter =0;

			for (int i = 0; i < pages.size(); i++) {
				counter++;
				String url = pages.get(i).getArticleUrl().toString();
				// do not index ecluded pages
				if (!pageIsExcluded(url, excludedPagePrefix)) {
					String pageContent = null;
					try {
						pageContent = pages.get(i)
								.getPlainContentFromWikiSyntax();
						indexPage(url, pageContent);
					}
					catch (IOException e) {
						e.printStackTrace();
						logger.warning("Page " + i + " not found!");
					}
					catch (StringIndexOutOfBoundsException e) {
						logger.warning("StringOutOfBound");
					}
					logger.info(counter + " pages were indexed!");
				}
			}
			closeWriter();
			logger.info("Process is finished!");
		}
		catch (CorruptIndexException e1) {
			e1.printStackTrace();
		}
		catch (LockObtainFailedException e1) {
			e1.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
