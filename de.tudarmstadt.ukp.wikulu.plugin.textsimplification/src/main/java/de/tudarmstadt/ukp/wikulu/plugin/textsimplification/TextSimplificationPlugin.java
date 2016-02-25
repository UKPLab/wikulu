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
package de.tudarmstadt.ukp.wikulu.plugin.textsimplification;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;


import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.semantics.simplification.SentenceSimplifierAnnotatorHM;
import de.tudarmstadt.ukp.dkpro.semantics.type.SimplifiedSentence;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

public class TextSimplificationPlugin
	extends Plugin
{

	private final Logger logger = LoggerFactory.getLogger(TextSimplificationPlugin.class);
	
	private AnalysisEngine m_simplifier;
    private JCas m_jcas = null;


	public TextSimplificationPlugin()
	{
		try {   
			        AnalysisEngineDescription aeSenToken = createPrimitiveDescription(
			                de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter.class
			        );
			        
			        AnalysisEngineDescription aeParser = createPrimitiveDescription(
			                StanfordParser.class,
			                StanfordParser.PARAM_MODEL_LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/core/stanfordnlp/lib/lexparser-en-pcfg.ser.gz",
			                StanfordParser.PARAM_CREATE_PENN_TREE_STRING, true
			        );
			        
			        AnalysisEngineDescription aeSS = createPrimitiveDescription(
			                SentenceSimplifierAnnotatorHM.class
			        );
			        
			        AnalysisEngineDescription aeAgg = createAggregateDescription(
			                aeSenToken,
			                aeParser,
			                aeSS
			        );
			        
			       m_simplifier = createAggregate(aeAgg);
			       m_jcas = m_simplifier.newJCas();
	        
		} catch (ResourceInitializationException e) {
			logger.error("Error initializing text simplifier web service", e);
		}
	}
	
	
	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException
	{
		 JSONObject arguments = new JSONObject(text);
		 String plainText = super.unescapeString(arguments.getString("text"));
		 
		 m_jcas.setDocumentLanguage("en");
        m_jcas.setDocumentText(plainText);
        	
        m_simplifier.process(m_jcas);
        
        StringBuilder sbSimplifiedText = new StringBuilder();
        sbSimplifiedText.append("<ol>\n");
        
	      for (Sentence sentence : JCasUtil.select(m_jcas, Sentence.class)) {
	            for (SimplifiedSentence simpleSentences : JCasUtil.selectCovered(SimplifiedSentence.class, sentence)) {
	            	StringArray arrSimpleSentences = simpleSentences.getSimpleSentences();
	            	for(int i = 0; i < arrSimpleSentences.size(); ++ i){
	            		sbSimplifiedText.append("<li>" + arrSimpleSentences.get(i) + "</li>\n");
	            	}
	            }
	      }

	     sbSimplifiedText.append("</ol>");
	     m_jcas.reset();
	      return sbSimplifiedText.toString();
	}

}
