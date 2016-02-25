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
package de.tudarmstadt.ukp.wikulu.plugin.configmanager;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 * This class can read parameters for plugins 
 * @author a_vovk
 * 
 */
public class ConfigurationReader
{

	/**
	 * Read plugin paramters
	 * @param configToRead config  to read
	 * @return json string with parameters
	 * @throws JSONException
	 */
	public static String readConfig(String configToRead)
		throws JSONException
	{
		String out;

		if (configToRead.equals("all")) {
			// Get List of All Plugins
			out = readListOfPlugins();
		}
		else {
			// Get all parameters of a appropriate plugin
			out = readPluginParameters(configToRead);

		}

		return out;
	}

	/**
	 * Reads the list of all plugins in the system
	 * @return json string with all plugins (id, name)
	 * @throws JSONException
	 */
	private static String readListOfPlugins()
		throws JSONException
	{
		JSONObject jsonReturn = new JSONObject();
		String[] pluginContents = WikuluPluginLoader.getInstance()
				.getInformation();

		for (String i : pluginContents) {
			JSONObject jObject = new JSONObject(i);
			JSONObject descObject = new JSONObject();
			descObject.put("id", jObject.getString("id"));
			descObject.put("name", jObject.getString("name"));
			jsonReturn.accumulate("plugins", descObject);
		}
		return jsonReturn.toString();
	}

	/**
	 * Read parameters of a plugin
	 * @param paramId id of a plugin to read
	 * @return json string with plugin parameters 
	 * @throws JSONException
	 */
	private static String readPluginParameters(String paramId)
		throws JSONException
	{
		JSONObject jsonReturn = new JSONObject();
		String[] pluginContents = WikuluPluginLoader.getInstance()
				.getInformation();

		for (String i : pluginContents) {
			JSONObject jObject = new JSONObject(i);
			if (jObject.getString("id").equals(paramId)) {
				jsonReturn = new JSONObject(i);
				break;
			}
		}
		return jsonReturn.toString();
	}

}
