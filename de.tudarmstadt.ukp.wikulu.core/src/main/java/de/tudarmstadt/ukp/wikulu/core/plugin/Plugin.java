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
package de.tudarmstadt.ukp.wikulu.core.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.jdom.JDOMException;

import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 * The abstract class for plugins. IMPORTANT: Your plugin's class name has to be
 * similar to your plugin's folder name: Example: Your plugin's folder name is
 * "add_content". Your plugin's class name has to be (!) "AddContentPlugin".
 * 
 * 
 * NOTE: Please use just ONE constructor; if you use both, the first found
 * constructor is used. If your has more than one function and one of them needs
 * to access the Wiki API, use {@link #Plugin(Wiki)}. If your plugin doesn't use
 * any Wiki functions, use {@link #Plugin()}.
 * 
 * @author C.Deeg
 * 
 */
public abstract class Plugin
{
	/**
	 * The wiki to use for Wiki API functions. Automatically set by
	 * {@link #Plugin(Wiki)}.
	 */
	protected Wiki wiki;

	protected HashMap<String, String> parameters;

	
	
	/**
	 * The constructor for plugins which don't need to use the Wiki API.
	 * Initialize Parameters HashMap
	 */
	public Plugin()
	{
		this.parameters = WikuluPluginLoader.getInstance().getPluginParameters(
				this.getClass().getCanonicalName());
	}

	/**
	 * Use this constructor if your plugin needs access to the Wiki API. The
	 * variable is automatically (and correctly) set by Wikulu. Don't set it
	 * manually!
	 * 
	 * Call super(wiki) to access {@link #wiki} easily.
	 * 
	 * @param wiki
	 *            the Wiki to work with
	 */
	public Plugin(Wiki wiki)
	{
		this();
		this.wiki = wiki;
	}

	/**
	 * The main method of a plugin. This method is automatically called when
	 * starting the plugin. Implement your plugin's function here (call your
	 * main class). If your plugin has more than one function, check the keys of
	 * your JSON string to determine which function should be executed (take a
	 * look at TopicSegmentationPlugin).
	 * 
	 * NOTE: text may contain encoded arguments (JSON used in JavaScript has
	 * problems with single/double quotes, so you have to encode the text).
	 * Don't forget to decode them with {@link #unescapeString(String)}.
	 * 
	 * @param text
	 *            a JSON string containing the arguments for your plugin
	 * @return a string containing the results of your plugin's function. Please
	 *         use JSON instead of Lists or Arrays.
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 * @throws JSONException
	 * @throws ResourceConfigurationException
	 * @throws IOException
	 * @throws CASException
	 * @throws JDOMException
	 */
	public abstract String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException;

	/**
	 * Decodes escaped characters in a string. The JSON String the run method
	 * receives should have encoded characters due to JSON exceptions. Use this
	 * method to get the original string.
	 * 
	 * @param escapedString
	 *            the string to unescape
	 * @return the unescaped input string with
	 */
	protected String unescapeString(String escapedString)
	{
		String normalString = null;
		try {
			normalString = URLDecoder.decode(escapedString, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return normalString;
	}
	
	
	/**
	 * Gets parameter value by name
	 * @param name name of the parameter
	 * @return value of the parameter
	 */
	protected String getParameter(String name) {
		return this.parameters.get(name);
	}
}
