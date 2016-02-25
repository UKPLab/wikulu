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
package de.tudarmstadt.ukp.wikulu.core.wpl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.tudarmstadt.ukp.wikulu.datastore.IndexCreator;

/**
 * 
 * @author C.Deeg
 * 
 */
public class WikuluPluginLoader
{
	/** An instance of this class. **/
	private static WikuluPluginLoader wpl;

	// Path to lucene index for all plugins
	public static final String LUCENE_INDEX_PATH = IndexCreator
			.getLuceneIndexDirectory();

	/** A list with the names of all found plugins. **/
	private String[] pluginList;

	private String[] pluginInformationList;

	private String[] pluginScriptsContent;

	private String[] pluginJSFileNames;
	
	private String[] pluginCSSFileNames;

	private HashMap<String, HashMap<String, String>> pluginParams;

	private HashMap<String, File> pluginLocations;

	private HashMap<String, String> wikiSnifferVars;

	private ResourceBundle config;

	/** The constructor of this class. **/
	private WikuluPluginLoader()
	{
		wikiSnifferVars = new HashMap<String, String>();
		try {
			this.listPlugins();
			this.readProperties();
		}
		catch (IOException e) {
			System.err.println("Wrong");
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a new object of this class or {@link #wpl}.
	 * 
	 * @return an instance of this class
	 */
	public static WikuluPluginLoader getInstance()
	{
		if (wpl == null) {
			wpl = new WikuluPluginLoader();
		}
		return wpl;
	}

	/**
	 * Reads Content of plugin.js into string
	 * 
	 * @param r
	 *            resourse to read
	 * @return content as string representation
	 * @throws IOException
	 */
	private String getPluginScriptContent(Resource r)
		throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				r.getInputStream()));
		String line = null;
		StringBuffer content = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			content.append(line);
			content.append("\n");
		}
		return content.toString();
	}

	/**
	 * List content of the plugins folder and store all found plugins in
	 * {@link #pluginList}.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * 
	 */
	private void listPlugins()
		throws IOException, JSONException
	{
		PathMatchingResourcePatternResolver pm = new PathMatchingResourcePatternResolver();
		Resource[] res = pm.getResources("classpath*:/META-INF/**/plugin.js"); // works

		
		ArrayList<String> pluginIds = new ArrayList<String>();
		ArrayList<String> pluginInfos = new ArrayList<String>();
		ArrayList<String> pluginScripts = new ArrayList<String>();
		ArrayList<String> pluginJSFileNames = new ArrayList<String>();

		this.pluginParams = new HashMap<String, HashMap<String, String>>();
		this.pluginLocations = new HashMap<String, File>();

		if (res != null && res.length > 0) {
			for (Resource re : res) {
				// get plugin content from plugin.js
				String content = readJSONStream(re.getInputStream());
				// create JSONObject from content
				JSONObject jObject = new JSONObject(content);
				// get plugin ID
				String pluginID = jObject.getString("id");

				File pluginConfFile = new File("conf/" + pluginID
						+ "/plugin.js");

				new File("conf/" + pluginID).mkdirs();
				// if file exists, read it, if not,
				// create a new one with the content of the
				// default settings file
				if (pluginConfFile.exists()) {
					InputStream ins = new BufferedInputStream(new FileInputStream(pluginConfFile));
					content = readJSONStream(ins);
					jObject = new JSONObject(content);
				} else {
					pluginConfFile.createNewFile();

					FileWriter writer = new FileWriter(pluginConfFile);
					jObject.write(writer);
					writer.close();
				}
				
				// save plugin content
				pluginInfos.add(content);
				// save plugin id
				pluginIds.add(pluginID);

				// parse and save plugin parameters
				parsePluginParameters(jObject);
				// save plugin location
				this.pluginLocations.put(pluginID, pluginConfFile);
				// get javascripts content
				String desc = re.getURL().toString();
				String javascr = desc.replaceAll("plugin.js", "*.js");
				Resource[] js = pm.getResources(javascr);
				if (js.length > 0) {
					for (Resource rs : js) {
						if (!rs.getURL().toString().endsWith("plugin.js")) {
							pluginJSFileNames.add(rs.getFilename());
							// System.out.println("SCRIPT ADDED:"+rs.getFilename());
							pluginScripts.add(this.getPluginScriptContent(rs));
						}
					}
				}

			}

		}
		
		Resource[] resCSS = pm.getResources("classpath*:/META-INF/css/**/*.css"); // works

		ArrayList<String> pluginCSSFileNames = new ArrayList<String>();
		for (Resource rc : resCSS) {
			pluginCSSFileNames.add(rc.getFilename());
		}
		
		this.pluginCSSFileNames = pluginCSSFileNames.toArray(new String[pluginCSSFileNames.size()]);
		
		this.pluginList = pluginIds.toArray(new String[pluginIds.size()]);

		this.pluginInformationList = pluginInfos.toArray(new String[pluginInfos
				.size()]);
		this.pluginScriptsContent = pluginScripts
				.toArray(new String[pluginScripts.size()]);
		this.pluginJSFileNames = pluginJSFileNames
				.toArray(new String[pluginJSFileNames.size()]);

	}

	/**
	 * Reads resource file into string
	 * 
	 * @param in
	 *            stream to read(plugin.js)
	 * @return content of resource
	 * @throws IOException
	 */
	private String readJSONStream(InputStream in)
		throws IOException
	{
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader buf = new BufferedReader(isr);
		String line;
		StringBuffer content = new StringBuffer();
		while ((line = buf.readLine()) != null) {
			content.append(line);
		}
		return content.toString();
	}

	/**
	 * Parse Plugin parameters from JSONObject to Map<PluginClassName,
	 * <ParameterName, ParameterValue>>
	 * 
	 * @param jObject
	 *            JSONObject to parse
	 * @throws JSONException
	 */
	private void parsePluginParameters(JSONObject jObject)
		throws JSONException
	{
		// get parameters
		JSONArray params = jObject.getJSONArray("params");

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		for (int i = 0; i < params.length(); i++) {
			JSONObject obj = params.getJSONObject(i);
			paramsMap.put(obj.getString("name"), obj.getString("value"));
		}
		this.pluginParams.put(jObject.getString("java_class"), paramsMap);
	}

	/**
	 * Returns the content of the plugin.js files of all found plugins.
	 * 
	 * @return a String array with the information about the plugins
	 */
	public String[] getInformation()
	{
		return this.pluginInformationList;
	}

	/**
	 * Gets all javascript code from all plugins
	 * 
	 * @return Array of JavaScripts of plugins
	 */
	@Deprecated
	public String[] getPluginScripts()
	{
		return this.pluginScriptsContent;
	}

	/**
	 * Gets the names of all javascript files in all plugins
	 * 
	 * @return list of file names
	 */
	public String[] getPluginJavaScriptFileNames()
	{
		return this.pluginJSFileNames;
	}
	
	
	public String[] getPluginCSSScriptFileNames()
	{
		return this.pluginCSSFileNames;
	}

	/**
	 * Get ParameterPairs (Name, Value) for a plugin
	 * 
	 * @param pluginName
	 *            plugin name
	 * @return paramaterpair(name, value)
	 */
	public HashMap<String, String> getPluginParameters(String pluginName)
	{
		return this.pluginParams.get(pluginName);
	}

	/**
	 * Returns the list with the plugins' names(plugin id).
	 * 
	 * @return a String array with the relative path of all found plugins
	 */
	public String[] getPlugins()
	{
		return this.pluginList;
	}

	/**
	 * Gets plugin file Object by pluginID
	 * 
	 * @param pluginId
	 *            id of a plugin to get
	 * @return file object of a plugin
	 */
	public File getPluginLocation(String pluginId)
	{
		return this.pluginLocations.get(pluginId);
	}

	/**
	 * Gets all values from wikulu.properties and stores them in a HashMap.
	 * 
	 */
	private void readProperties()
	{
		this.config = PropertyResourceBundle.getBundle("wikulu");
		for (String key : this.config.keySet()) {
			wikiSnifferVars.put(key, this.config.getString(key));
			//System.out.println(key + " " + this.config.getString(key));
		}
	}

	/**
	 * Return the HashMap with all WikiSniffer properties.
	 * 
	 * @return the HashMap object with the properties
	 */
	public HashMap<String, String> getPropertiesFromFile(String prefix)
	{
		HashMap<String, String> values = new HashMap<String, String>();
		// TODO better: iterate over Entry objects?
		for (String key : this.wikiSnifferVars.keySet()) {
			if (key.startsWith(prefix + ".")) {
				values.put(key.replace(prefix + ".", ""),
						this.wikiSnifferVars.get(key));
			}
		}
		return values;
	}

}
