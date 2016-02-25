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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class ConfigWriterTest
	extends ConfigurationWriter
{

	private ArrayList<String> pluginContent;

	@Before
	public void getPluginScriptContent()

	{
		try{
			pluginContent = new ArrayList<String>();
			for (int i = 1; i < 3; i++) {
				BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/plugin"+i+".js"));
				String line = null;
				StringBuffer content = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					content.append(line);
					content.append("\n");
				}
				pluginContent.add(content.toString());
			}
			
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}

	
	@Test
	public void writeTest1() {
		assertTrue(checkWriteParam("keyphrase_extraction", "keyphraseMaxCount", "20"));
		assertTrue(checkWriteParam("keyphrase_extraction", "keyphraseMaxCount", "35"));
	}
	
	@Test
	public void writeTest2() {
		assertTrue(checkWriteParam("add_content", "lucene.index.path", "tester"));
		assertTrue(checkWriteParam("add_content", "search.count", "somecount"));
	}
	
	
	@Test
	public void writeTest3() {
		
  
//        Resource res = new FileSystemResource("src/test/resources/plugin2.js");
//        writeConfig(res, "add_content", "lucene.index.path", "check");
//        
//		assertTrue(checkWriteParam("add_content", "lucene.index.path", "tester"));
//		assertTrue(checkWriteParam("add_content", "search.count", "somecount"));
	}
	
	private boolean checkWriteParam(String pluginId, String paramName, String value)
	{
		try {
			JSONObject obj = changeParamater(pluginContent.toArray(new String[pluginContent.size()]), pluginId, paramName, value);
			
			
			return checkCorrectness(obj, paramName, value);
		}
		catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	
	private boolean checkCorrectness(JSONObject obj, String paramName, String value) throws JSONException {
		JSONArray array = obj.getJSONArray("params");
		
		for (int i = 0; i < array.length(); i++) {
			
			if (array.getJSONObject(i).getString("name").equals(paramName))	{
				return array.getJSONObject(i).getString("value").equals(value);
				
			}
		}
		
		return false;
	}
}
