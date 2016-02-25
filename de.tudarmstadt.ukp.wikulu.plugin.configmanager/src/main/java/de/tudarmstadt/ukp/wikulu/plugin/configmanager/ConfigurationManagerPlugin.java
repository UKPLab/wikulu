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
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;

import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;
import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 * This class manages configuration(read, write) of another plugins
 * 
 * @author a_vovk
 * 
 */
public class ConfigurationManagerPlugin
	extends Plugin
{

	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException
	{
		JSONObject arguments = new JSONObject(text);

		// Command
		String command = arguments.getString("command");

		String output = null;
		if (command.equals("read_config")) {
			output = ConfigurationReader.readConfig(arguments
					.getString("plugin"));
		}
		else if (command.equals("write_config")) {

			File confFile = WikuluPluginLoader.getInstance().getPluginLocation(
					arguments.getString("plugin"));
			Boolean isWritten = ConfigurationWriter.writeConfig(confFile,
					arguments.getString("plugin"),
					arguments.getString("paramname"),
					arguments.getString("paramvalue"));
			output = isWritten.toString(); 
		}
		else {
			// TODO: exception? command doesnot exists
			return null;
		}

		return output;

	}

}
