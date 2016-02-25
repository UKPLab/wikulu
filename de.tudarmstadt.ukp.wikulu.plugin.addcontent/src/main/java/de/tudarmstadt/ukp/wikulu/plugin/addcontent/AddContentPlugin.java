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
package de.tudarmstadt.ukp.wikulu.plugin.addcontent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.AnalysisEngineFactory;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.ir.type.Query;
import de.tudarmstadt.ukp.dkpro.ir.type.SearchResult;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;
import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;

/**
 *
 *
 * @author C.Deeg
 *
 */
public class AddContentPlugin
	extends Plugin
{

	private final Logger logger = LoggerFactory.getLogger(AddContentPlugin.class);

	private AnalysisEngine luceneSearcher;

	public AddContentPlugin()
	{
		//this.config = PropertyResourceBundle.getBundle("wikulu");
		try {
			String indexPath = WikuluPluginLoader.LUCENE_INDEX_PATH;

			AnalysisEngineDescription desc = AnalysisEngineFactory.createPrimitiveDescription(LuceneSearcher.class, LuceneSearcher.PARAM_INDEX_PATH, indexPath);

			luceneSearcher = AnalysisEngineFactory.createPrimitive(desc);
		}
		catch (ResourceInitializationException e) {
			logger.error("Error initializing Search web service", e);
		}
	}

	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException
	{
		JSONObject arguments = new JSONObject(text);

		// use luceneSearch to populate the results
		JCas jcas = luceneSearcher.newJCas();
		jcas.setDocumentLanguage("en");

		DocumentMetaData md = new DocumentMetaData(jcas);
		md.setDocumentId("tmp"); // Set a temporary ID as Lucene searcher needs
									// one.

		Query q = new Query(jcas);
		q.setQuery(super.unescapeString(arguments.getString("query")).toLowerCase());
		q.addToIndexes();

		luceneSearcher.process(jcas);

		List<String> results = new ArrayList<String>();

		FSIterator<Annotation> resultsIter = jcas.getAnnotationIndex(
				SearchResult.type).iterator();

		while (resultsIter.hasNext()) {
			SearchResult buf = (SearchResult) resultsIter.next();
			results.add(buf.getDocId());
		}

		String[] matches = results.toArray(new String[results.size()]);

		if (matches == null) {
			matches = new String[0];
		}

		int resultMaxCount = Integer.parseInt(getParameter("search.count"));

		int resultCount = (matches.length > resultMaxCount) ? resultMaxCount
				: matches.length;

		List<String> resultMatches = new LinkedList<String>();
		JSONObject resultJson = new JSONObject();

		for (int i = 0; i < resultCount; i++) {
			resultMatches.add(matches[i]);
		}

		resultJson.put("matches", resultMatches);

		return resultJson.toString();
	}

}
