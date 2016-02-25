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
package de.tudarmstadt.ukp.wikulu.plugin.summarization;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.semantics.summarization.teaching.NodeDegreeSentenceSummaryAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.summarization.teaching.SentencePairAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.summarization.teaching.SummaryWriter;
import de.tudarmstadt.ukp.dkpro.semantics.summarization.teaching.TfidfAnnotatorInline;
import de.tudarmstadt.ukp.dkpro.semantics.summarization.teaching.TfidfAnnotatorInline.KnownTypes;
import de.tudarmstadt.ukp.dkpro.semantics.type.Summary;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;


public class SummarizationPlugin
	extends Plugin
{	
	private final Logger logger = LoggerFactory.getLogger(SummarizationPlugin.class);
	
	private AnalysisEngine summarizer;
	

	public SummarizationPlugin()
	{
		try {   
			AnalysisEngineDescription desc = createAggregateDescription(
	                createPrimitiveDescription(BreakIteratorSegmenter.class),
	                createPrimitiveDescription(TfidfAnnotatorInline.class,
	                	TfidfAnnotatorInline.TYPE_TO_ANNOTATE, KnownTypes.Token.toString()), 
	                createPrimitiveDescription(SentencePairAnnotator.class,
	                	SentencePairAnnotator.SINGLE_GRAPH, true),                
	                createPrimitiveDescription(
            			NodeDegreeSentenceSummaryAnnotator.class,
                        NodeDegreeSentenceSummaryAnnotator.COMPRESSION_RATIO, 0.2f,
                        NodeDegreeSentenceSummaryAnnotator.SINGLE_GRAPH, true),
                    createPrimitiveDescription(SummaryWriter.class)
	        ); 		
			
			summarizer = createAggregate(desc); 
		}
		catch (ResourceInitializationException e) {
			logger.error("Error initializing Summarizer web service", e);
		}
	}

	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException, JSONException
	{
		JSONObject arguments = new JSONObject(text);
		JSONObject result = new JSONObject();
		
		// initialize CAS
        JCas jcas = null;
		jcas = summarizer.newJCas();
		
        jcas.setDocumentText(super.unescapeString(arguments.getString("text")));
        jcas.setDocumentLanguage("en");
		
        // extract keywords
		summarizer.process(jcas);
		
		Summary summary = JCasUtil.selectSingle(jcas, Summary.class);
		FSArray sentences = summary.getSentences();

		//List<String> resultSentences = new LinkedList<String>();
		JSONArray resultSentences = new JSONArray();
		
		int i;
		for (i = 0; i < sentences.size(); i++)
		{
			Sentence sentence = (Sentence) sentences.get(i);
			//sb.append("<li>" + sentence.getCoveredText() + "</li>");
			resultSentences.put(sentence.getCoveredText());
			
		}
		
		int defaultNumber = Integer.parseInt(getParameter("numberOfSentences"));
		if(defaultNumber > i)
			defaultNumber = i;
		
		result.append("summary", resultSentences);
		result.append("default_number", defaultNumber);
		result.append("maximum_number", i);
		return result.toString();
	}
}
