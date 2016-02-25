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
package de.tudarmstadt.ukp.wikulu.plugin.qademo;

import java.io.IOException;
import java.util.Vector;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.qa_demo.QADemo;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

public class QADemoPlugin
	extends Plugin
{
	QADemo qademo;

	private final Logger logger = LoggerFactory.getLogger(QADemoPlugin.class);
	// Parameters for demo initialization
	private final String server = (getParameter("server"));
	private final String user= (getParameter("user"));
	private final String password= (getParameter("password"));
	private final String indexfiledb = (getParameter("indexfiledb"));
	private final String wortschatzdb = (getParameter("wortschatzdb"));
	private final String wordnetfredb = (getParameter("wordnetfredb"));



	public QADemoPlugin()
	{
//		Vector<String> def_datasets = new Vector<String>();
//		def_datasets.add("wikipedia");
//		def_datasets.add("faq");????
		
//		def_datasets.add("yatxt");????//TODO:!!!
//		def_datasets.add("yaxml");??//TODO:!!!
		
//		def_datasets.add("ppt");
//		def_datasets.add("answerbag");
//		Vector<String> def_fields = new Vector<String>();
//		def_fields.add("Question");
//		def_fields.add("Answer");
//		int def_results = 10;
//		qademo = new QADemo(false, server, user, password, indexfiledb, wortschatzdb, wordnetfredb, def_results, def_datasets, def_fields);
	}


	private final String DATASET_TAG ="dataset";
	private final String SFIELDS_TAG ="sfields";
	private final String OUTPUT_TAG ="output";
	private final String PARAMS_TAG ="params";
	private final String QUESTION_TAG ="question";
	private final String NUM_ANSWERS_TAG ="num_answers";
	
	
	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException
	{
		JSONObject arguments = new JSONObject(text);
		
		String question = super.unescapeString(arguments.getString(QUESTION_TAG));
		JSONObject params = arguments.getJSONObject(PARAMS_TAG);
		
		Vector<String> def_datasets = new Vector<String>();
		if (params.has(DATASET_TAG)){
			JSONArray dataset = params.getJSONArray(DATASET_TAG);
			for (int i=0; i< dataset.length(); i++)
				def_datasets.add(dataset.getString(i));
		}
		
		Vector<String> def_fields = new Vector<String>();
		if (params.has(SFIELDS_TAG)){
			JSONArray sfields = params.getJSONArray(SFIELDS_TAG);
			for (int i=0; i< sfields.length(); i++)
				def_fields.add(sfields.getString(i));
		}
		
		JSONArray def_results_arr = params.getJSONArray(NUM_ANSWERS_TAG);
		int def_results = def_results_arr.getInt(0);
		
		boolean output = false;
		if (params.has(OUTPUT_TAG)) {
			output = true;
		}
		
		qademo = new QADemo(output, server, user, password, indexfiledb, wortschatzdb, wordnetfredb, def_results, def_datasets, def_fields);
		// get question text:
		//text = super.unescapeString(arguments.getString("text"));
		// get params
		// get datasets
		// get fields
		// get nr of results
		// get "show intermediate info"
		//String html = qademo.process(question);
		//System.out.println("Output: "+html);
		//return "html";
		return qademo.process(question); //actual entry
//		return text + "\n" + params;
	}

	/*
	 * public static void main(String[] args) throws Exception { QADemoPlugin
	 * demo = new QADemoPlugin(); String answers =
	 * demo.run("How many polar bears?"); System.out.print(answers); }
	 */

}
