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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 * Writes new parameters into configuration file and memory
 * @author a_vovk
 *
 */
public class ConfigurationWriter
{
	/**
	 * Writes parameters to the file
	 * @param confFile 
	 * @param pluginId
	 * @param paramName
	 * @param value
	 * @return
	 */
	public static boolean writeConfig(File confFile, String pluginId,
			String paramName, String value)
	{
		try {
			JSONObject objToWrite = changeParamater(WikuluPluginLoader
					.getInstance().getInformation(), pluginId, paramName, value);
			// replace param with new value
			HashMap<String, String> params = WikuluPluginLoader.getInstance()
					.getPluginParameters(objToWrite.getString("java_class"));
			params.put(paramName, value);

			FileWriter writer = new FileWriter(confFile);
			objToWrite.write(writer);
			writer.close();
		}
		catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Change parameter of a plugin
	 * @param pluginContent list of all plugins with parameters
	 * @param pluginId id of a plugin
	 * @param paramName name of a parameter to change
	 * @param value new value of a parameter
	 * @return
	 * @throws JSONException
	 */
	protected static JSONObject changeParamater(String[] pluginContent,
			String pluginId, String paramName, String value)
		throws JSONException
	{
		for (int i = 0; i < pluginContent.length; i++) {
			JSONObject tmp = new JSONObject(pluginContent[i]);
			if (pluginId.equals(tmp.getString("id"))) {
				if(paramName.equals("menu")) {
					tmp.put("menu", value);
					pluginContent[i] = tmp.toString();
					return tmp;
				} else{
					JSONArray jArray = tmp.getJSONArray("params");
					for (int j = 0; j < jArray.length(); j++) {
						JSONObject jParam = jArray.getJSONObject(j);
						if (jParam.getString("name").equals(paramName)) {
							jParam.put("value", value);
							// set new content for object in memory
							pluginContent[i] = tmp.toString();
							return tmp;
						}
					}
				}
			}
		}
		return null;
	}
}
