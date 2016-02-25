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
package de.tudarmstadt.ukp.wikiproxy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 * This OutputStream looks for the correct place to insert JavaScripts to the HTML.
 * .js and .css files can be added to arbitrary html pages in the
 * getInjectionString() method.
 *
 * @author hoffart
 *
 */
public class InjectionWrappedOutputStream extends ServletOutputStream {
	private final DataOutputStream stream;
	private boolean found = false;
	InjectionServletResponse response;
	HttpServletRequest request;
	Pattern headPatern = Pattern.compile("<head.*?>",Pattern.CASE_INSENSITIVE);

	public InjectionWrappedOutputStream(OutputStream output, InjectionServletResponse response, HttpServletRequest request) {
		stream = new DataOutputStream(output);
		this.response = response;
		this.request = request;
	}

	@Override
	public void write(int b) throws IOException {
		stream.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		stream.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		//get encoding
		String enc = response.getCharacterEncoding()==null?"UTF-8":response.getCharacterEncoding().toUpperCase();
		boolean inject = false;

		// only inject in html content, ignore google stuff
		if (request != null && !request.getServerName().contains("pagead2.googlesyndication.com") &&
				(response.getContentType() != null && response.getContentType().contains("text/html")))
		{
			inject = true;
		}

		if (!found && inject) {
			String s = new String(b, 0, len, enc);
			Matcher headMatcher = headPatern.matcher(s);
			if (headMatcher.find()) {
				String match = headMatcher.group();
				String injection = getInjectionString();
				s = s.replaceFirst(match, match + injection);
				found = true;
				byte[] out = s.getBytes(enc);
				stream.write(out, off, out.length);
			} else {
				stream.write(b, off, len);
			}
		} else {
			//System.out.println(new String(b));
			stream.write(b, off, len);
		}
	}

	/**
	 * This method creates the string that is to be injected
	 *
	 * @return String of .css and .js files to be injected
	 */
	public String getInjectionString() {
		List<String> scripts = new ArrayList<String>();
		List<String> styles = new ArrayList<String>();
		
		
		// jQuery
		scripts.add("/ukp_static/js/lib/jquery-1.5.min.js");
		scripts.add("/ukp_static/js/lib/jquery.json-1.3.js");
		scripts.add("/ukp_static/js/lib/jquery.imageadder.js");
		scripts.add("/ukp_static/js/lib/jquery.cookie.js");
		scripts.add("/ukp_static/js/lib/jquery-ui-1.8.9.min.js");
		scripts.add("/ukp_static/js/lib/jquery.replace.js");
		scripts.add("/ukp_static/js/lib/jquery.replaceInDOM.js");

		ResourceBundle resource = InjectionService.properties;
		// our stuff
//		if(InjectionProxy.properties==null){
//			resource= DirectProxy.properties;
//		}else{
//			resource= InjectionProxy.properties;
//		}
		String targetWiki = resource.getString("wikulu.targetWikiPlatform");

		scripts.add("/ukp_static/js/wikulu.js");

		ArrayList<String> pluginScripts = new ArrayList<String>();

		WikuluPluginLoader wpl = WikuluPluginLoader.getInstance();
		String[] pScripts = wpl.getPluginJavaScriptFileNames();

		for(String content : pScripts) {
			pluginScripts.add("/plugins/js/" + content);
		}
		
		
		
		
		if(resource.getString("wikulu.simpleAdapter").equals("yes")) {
			//String testing = "/ukp_static/js/wikulu/"+targetWiki.toLowerCase()+"simpleadapter.js";
			scripts.add("/ukp_static/js/wikulu/"+targetWiki+"simpleadapter.js");
		}else{
			scripts.add("/ukp_static/js/wikulu/ui.js");
			scripts.add("/ukp_static/js/wikulu/"+targetWiki+"adapter.js");
		}

		//scripts.add("/ukp_static/js/wikisniffer/sniffercontrol.js");

		// DWR
		scripts.add("/ukp_dwr/interface/Wikulu.js");
		scripts.add("/ukp_dwr/interface/WikiSniffer.js");
		scripts.add("/ukp_dwr/engine.js");

		// Styles
		styles.add("/ukp_static/css/style.css");
		styles.add("/ukp_static/yui/assets/skins/sam/container.css");
		styles.add("/ukp_static/yui/fonts/fonts-min.css");
		styles.add("/ukp_static/yui/assets/skins/sam/button.css");
		//styles.add("/ukp_static/jquery/themes/base/ui.all.css");
		styles.add("/ukp_static/css/jquery-ui-twiki-theme/jquery-ui-1.8.9.custom.css");
		styles.add("/ukp_static/css/jquery-ui-qademo/jquery-ui-1.8.13.custom.css");
		styles.add("/ukp_static/css/qademo.css");
		styles.add("/ukp_static/css/wikisniffer/wikisniffer.css");
		styles.add("/ukp_static/css/suggestlinks.css");

		StringBuffer inject = new StringBuffer();

		for (String script : scripts) {
			// TODO externalize prefix
			inject.append("<script src=\"" + script + "\" type=\"text/javascript\"></script>\n");
		}

		for(String pluginScript : pluginScripts) {
			inject.append("<script src=\"" + pluginScript + "\" type=\"text/javascript\"></script>\n");
		}
		
		
		
		
		for(String cssToLoad : this.getCssToLoad()) {
			System.out.println("STYLE TO LOAD:! "+cssToLoad);
			inject.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssToLoad + "\" />\n");
		}
		
		
		HashMap<String, String> wikiSnifferProperties = wpl.getPropertiesFromFile("wikisniffer");
		StringBuilder jsVariables = new StringBuilder();
		String wsMainUrl = "";
		String propertyValue = "";
		jsVariables.append("<script type = \"text/javascript\">\n");
		Set<Map.Entry<String, String>> map = wikiSnifferProperties.entrySet();
		for(Map.Entry<String, String> entry : map) {
			if(entry.getKey().endsWith("mainURL")) {
				wsMainUrl = entry.getValue();
				break;
			}
		}
		for(Map.Entry<String, String> entry : map) {
			if(entry.getKey().endsWith("mainURL")) {
				//wsMainUrl = entry.getValue();
				propertyValue = entry.getValue();
			} else if(entry.getKey().endsWith("URL")) {
				propertyValue = wsMainUrl + entry.getValue();
			} else {
				propertyValue = entry.getValue();
			}
			jsVariables.append("var " + entry.getKey() + " = \"" + propertyValue + "\";\n");
		}
		jsVariables.append("</script>\n");
		
		inject.append(jsVariables.toString());
		for (String css : styles) {
			inject.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + css + "\" />\n");
		}
		return inject.toString();
	}
	
	
	private ArrayList<String> getCssToLoad() {
		ArrayList<String> cssScripts = new ArrayList<String>();
		
		String[] cssScriptFiles = WikuluPluginLoader.getInstance().getPluginCSSScriptFileNames();

		for(String cssFile : cssScriptFiles) {
			cssScripts.add("/plugins/css/" + cssFile);
		}
		return cssScripts;
		
	}
}
