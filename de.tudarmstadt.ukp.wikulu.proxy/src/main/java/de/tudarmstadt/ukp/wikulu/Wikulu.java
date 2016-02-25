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
package de.tudarmstadt.ukp.wikulu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;
import de.tudarmstadt.ukp.wikulu.datastore.IndexCreator;

/**
 * The children of this class are wrapped by the DWR JavaScript layer, and thus
 * all public methods are exposed to the browser.
 *
 */
public abstract class Wikulu
{

	final Logger logger = LoggerFactory.getLogger(Wikulu.class);

	protected Wiki wiki;

	protected ResourceBundle config;

	public Wikulu()
	{
		config = PropertyResourceBundle.getBundle("wikulu");

		logger.info("Loaded Wikulu.");
	}

	/**
	 * Exposed for access by JavaScript
	 *
	 * @return String representing the target platform
	 */
	public String getTargetPlatform()
	{
		return config.getString("wikulu.targetWikiPlatform");
	}

	/**
	 * Exposed for access by JavaScript
	 *
	 * @return String representing the target platform api URL
	 * @throws MalformedURLException
	 */
	public String getTargetPlatformApiUrl()
	{
		return config.getString("wikulu.targetPlatformBaseURL") + "" + config.getString("wikulu.targetPlatformAPIURL");
	}

	/**
	 * Creates wiki link with url "linkCandidateName" for text "word"
	 *
	 * @param word
	 *            link text
	 * @param linkCandidateName
	 *            url of the link
	 * @return syntex correct wiki link
	 */
	public abstract String createLink(String word, String linkCandidateName);

	public abstract String getTitleInWikiSyntax(String title);

	/**
	 * Returns an instance of the class pluginClassName.
	 *
	 * @param pluginClassName
	 *            the name of the plugin's class
	 * @return
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	private Object getObjectForPluginName(String pluginClassName, String authData)
		throws MalformedURLException
	{
		Object o = null;
		try {
			pluginClassName = pluginClassName.replace(".class", "").replace("/", ".");
			Class pluginClass = Class.forName(pluginClassName);
			Constructor[] con = pluginClass.getConstructors();
			logger.info("Creating object for class " + pluginClassName);
			if (con[0].getParameterTypes().length == 0) {
				o = pluginClass.newInstance();
			}
			else {
				Class[] args = new Class[] { Class
						.forName("de.tudarmstadt.ukp.wikiapi.Wiki") };
				Constructor c = pluginClass.getConstructor(args);
				String aUrl = this.wiki.getApiURL().toExternalForm();
				String vUrl = this.wiki.getViewUrl().toExternalForm();
				if (aUrl.contains("mrburns") && !aUrl.contains("tu-darmstadt")) {
					URL newApiURL = new URL(aUrl.replaceAll("mrburns",
							"mrburns.tk.informatik.tu-darmstadt.de"));
					URL newViewURL = new URL(vUrl.replaceAll("mrburns",
							"mrburns.tk.informatik.tu-darmstadt.de"));
					this.wiki.setApiURL(newApiURL);
					this.wiki.setViewUrl(newViewURL);
				}
				this.wiki.setAuthData(authData);
				//System.out.println("AuthData: " + authData);
				o = c.newInstance(this.wiki);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * Invokes the run() method of a plugin's java class with the given
	 * arguments.
	 *
	 * @param plugin
	 *            the object of the plugins' class
	 * @param args
	 *            the arguments needed by the run() method
	 * @return the return value of the run method
	 */
	@SuppressWarnings("unchecked")
	private String invokeRunMethod(Object plugin, String arg)
	{
		String json = null;
		Class x = plugin.getClass();
		Method runMethod;
		try {
			runMethod = x.getMethod("run", String.class);
			json = (String) runMethod.invoke(plugin, arg);
			logger.info("run method returned: " + json);
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * Determines the plugin that should be called and executes it's run method.
	 *
	 * @param nameAndArgs
	 *            a JSON string containing the plugin's name and the arguments
	 *            for it's run method
	 * @return the result of the plugin's run method
	 * @throws JSONException
	 * @throws IOException
	 */
	public String perform(String nameAndArgs)
		throws JSONException, IOException
	{
		String runResult = null;
		System.out.println();
		JSONObject information = new JSONObject(nameAndArgs);

		if (information != null) {

				String authData = null;
				if(information.has("authData")) {
					authData = information.getString("authData");
				}
				try{
					Object pluginObject = this.getObjectForPluginName(information.getString("class")+".class", authData);
					// BUG
					JSONObject args = new JSONObject(information
							.getString("arguments"));
					logger.info("Calling run method with arguments: " + args.toString());
					runResult = this.invokeRunMethod(pluginObject, args.toString());
				} catch(MalformedURLException e) {
					e.printStackTrace();
				}

		}
		// runResult will be the run methods return value or null if
		// the JSON string was incorrect
		return runResult;
	}

	/**
	 * Create new index for wiki using authorization data
	 * @param auth authorization data
	 */
	public void createIndex(String auth) {
		try {
			IndexCreator creator = new IndexCreator();
			creator.createIndex(auth);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the content of the plugin folder and returns the names of all
	 * plugins.
	 *
	 * @return the names of all found plugins as a string array
	 */
	public String[] getPluginNames()
	{
		WikuluPluginLoader wpl = WikuluPluginLoader.getInstance();
		return wpl.getPlugins();
	}

	/**
	 * Gets the preferences for all existing plugins from plugin.js files.
	 *
	 * @return the content of all plugin.js files as strings
	 */
	public String[] getPluginInformation()
	{
		WikuluPluginLoader wpl = WikuluPluginLoader.getInstance();
		return wpl.getInformation();
	}
}
