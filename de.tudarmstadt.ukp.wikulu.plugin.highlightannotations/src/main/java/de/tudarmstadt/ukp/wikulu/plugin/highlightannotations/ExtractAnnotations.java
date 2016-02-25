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
package de.tudarmstadt.ukp.wikulu.plugin.highlightannotations;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.uimafit.factory.AnalysisEngineFactory;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerTT4JBase;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

public class ExtractAnnotations extends Plugin {
	
	private AnalysisEngine highlighter;
	
	private String typePrefix; 
	
	public ExtractAnnotations() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String run(String text) throws AnalysisEngineProcessException,
			ResourceInitializationException, JSONException,
			ResourceConfigurationException, IOException, CASException,
			JDOMException {
		/**
		 * Suggested output:
		 * 
		 * {
		 * 		{
		 * 			"name" : "NN",
		 * 			"color" : "yellow",
		 * 			"words" : 	[
		 * 							{
		 * 								"word" : "blah",
		 * 								"begin" : "1"
		 * 							}
		 * 						]
		 * 		}
		 * }
		 */
		JSONObject json = new JSONObject(text);
		
		JSONObject results = new JSONObject();
		
		JSONArray annos = new JSONArray();
		JSONObject types = new JSONObject();
		//JSONArray types = new JSONArray();
		
		String unescapedText = super.unescapeString(json.getString("text"));
		
		// analyze and process the text
		AnalysisEngine segm = AnalysisEngineFactory.createPrimitive(BreakIteratorSegmenter.class);
		highlighter = AnalysisEngineFactory.createPrimitive(
				TreeTaggerPosLemmaTT4J.class,
				TreeTaggerTT4JBase.PARAM_LANGUAGE_CODE, "en");
		
		JCas jcas = highlighter.newJCas();
		jcas.setDocumentText(unescapedText);
		jcas.setDocumentLanguage("en");
		
		segm.process(jcas);
		highlighter.process(jcas);
		
		typePrefix = getParameter("typePrefix");
		Iterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		
		
		while(iter.hasNext()) {
			Annotation anno = iter.next();
			Type t = anno.getType();
			String typeName = t.getShortName();
			String typePref = t.getName().split(typeName)[0];
			if(typePref.equals(typePrefix)) {
				if(!types.has(typeName)) {
					JSONObject desc = new JSONObject();
					desc.put("name", typeName);
					desc.put("color", getParameter(typeName));
					types.put(typeName, desc);
				}
				
				JSONObject annot = new JSONObject();
				annot.put("word", anno.getCoveredText());
				annot.put("typeName", typeName);
				annot.put("color", getParameter(typeName));
				annos.put(annot);
			}
		}
		
		JSONArray typeArray = new JSONArray();
		
		Iterator iterator = types.keys();
		while(iterator.hasNext()) {
			String key = (String) iterator.next();
			typeArray.put(types.getJSONObject(key));
		}

		
		results.put("types", typeArray);
		results.put("annotations", annos);
		return results.toString();
	}

}
