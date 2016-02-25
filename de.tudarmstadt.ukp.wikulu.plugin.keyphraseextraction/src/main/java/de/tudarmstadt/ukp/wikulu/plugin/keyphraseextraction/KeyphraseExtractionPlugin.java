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
package de.tudarmstadt.ukp.wikulu.plugin.keyphraseextraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.knallgrau.utils.textcat.TextCategorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.dkpro.semantics.keyphrases.wrapper.CooccurrenceGraphExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.type.Keyphrase;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

/**
 * This class provides the keyphrase extraction. It is exposed as a webservice
 * and can be accessed using the {@link KeyphraseExtractorClient}.
 * 
 * @author hoffart
 * 
 */
public class KeyphraseExtractionPlugin
	extends Plugin
{

	// Logger
	private final Logger logger = LoggerFactory
			.getLogger(KeyphraseExtractionPlugin.class);

	// Array with languages and ISO abbreviations of languages
	private static final String[][] languages = { { "german", "de" },
			{ "english", "en" }, { "french", "fr" }, { "spanish", "es" },
			{ "italian", "it" }, { "swedish", "sv" }, { "polish", "pl" },
			{ "dutch", "nl" }, { "norwegian", "no" }, { "finnish", "fi" },
			{ "albanian", "sq" }, { "slovakian", "sk" }, { "slovenian", "sl" },
			{ "danish", "da" }, { "hungarian", "hu" } };
	
	// parameter keyphraseMaxCount
	private final int keyphraseMaxCount = Integer
	.valueOf(getParameter("keyphraseMaxCount"));

	// Map with languages and ISO abbreviations of languages
	private final Map<String, String> langName2ISO;
	// KeyPhraseEctractor
	//private AnalysisEngine keyphraseExtractor;
	// TextCategorizer - language recognizer
	private final TextCategorizer categorizer;

	/**
	 * Initial setup of KeyphraseExtractor
	 */
	public KeyphraseExtractionPlugin()
	{
		// HashMap with languages (Language is a key and value is an ISO
		// abbreviation of this language)
		langName2ISO = new HashMap<String, String>();
		for (String[] langPair : languages) {
			langName2ISO.put(langPair[0], langPair[1]);
		}
		// TextCategorizer to identify the language
		categorizer = new TextCategorizer();

	}

	/**
	 * Extracts the keyphrases using DKPro components
	 * 
	 * @param text
	 *            Text to extract the keyphrases from
	 * @return Extracted keyphrases
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 * @throws IOException 
	 */
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, IOException
	{
		JSONObject arguments = new JSONObject(text);

		// identify language
		String unescapeText = super.unescapeString(arguments.getString("text"));
		String result = categorizer.categorize(unescapeText);
		String lang = langName2ISO.get(result);
		// create appropriate descriptor (works only with en or de lang)
		CooccurrenceGraphExtractor cooccurrenceGraphExtractor = new CooccurrenceGraphExtractor();
		cooccurrenceGraphExtractor.setLanguage(lang);
		List<Keyphrase> keyphrases = null;
		try {
			keyphrases = cooccurrenceGraphExtractor.extract(unescapeText);
		}
		catch (IOException e) {
			logger.error("Error initializing keyphrase extraction uima pipeline", e);
		}

	
		keyphrases = filterAndSortKeyphrases(keyphrases);

		String[] keyphraseStrings = new String[keyphrases.size()];

		for (int i = 0; i < keyphrases.size(); i++) {
			keyphraseStrings[i] = keyphrases.get(i).getKeyphrase();
		}

		int keyphraseCount = (keyphraseStrings.length > keyphraseMaxCount) ? keyphraseMaxCount
				: keyphraseStrings.length;

		List<String> keyphr = new LinkedList<String>();

		for (int i = 0; i < keyphraseCount; i++) {
			keyphr.add(keyphraseStrings[i]);
			//System.out.println("Keyphrase Nr." + i + ": " + keyphraseStrings[i]);
		}

		JSONObject jsonReturn = new JSONObject();

		jsonReturn.put("keyphrases", keyphr);
		jsonReturn.put("keyphraseMaxCount", keyphraseMaxCount);
		return jsonReturn.toString();
	}

	/**
	 * Filters and sorts keyphrases
	 * 
	 * @param jcas
	 *            jcas with keyphrases
	 * @return sorted List with keyphrases
	 */
	static List<Keyphrase> filterAndSortKeyphrases(List<Keyphrase> keyphrases)
	{

		// sort the keyphrases
		Collections.sort(keyphrases, new KeyphraseComparator());

		// filter the keyphrases
		// remove keyphrases with less than three characters
		List<Keyphrase> filteredKeyphrases = new ArrayList<Keyphrase>();
		Set<String> uniqueKeyphrases = new HashSet<String>();
		for (Keyphrase keyphrase : keyphrases) {
			String keyphraseString = keyphrase.getKeyphrase();

			if (keyphraseString.length() < 3) {
				continue;
			}

			if (!uniqueKeyphrases.contains(keyphraseString)) {
				uniqueKeyphrases.add(keyphraseString);
				filteredKeyphrases.add(keyphrase);
			}
		}

		return filteredKeyphrases;
	}

}

/**
 * Comparator for comparison of Keyphrases
 * 
 * @author a_vovk
 * 
 */
class KeyphraseComparator
	implements Comparator<Keyphrase>
{
	@Override
	public int compare(Keyphrase k1, Keyphrase k2)
	{
		double score1 = k1.getScore();
		double score2 = k2.getScore();

		if (score1 < score2) {
			return 1;
		}
		else if (score1 > score2) {
			return -1;
		}
		else {
			return 0;
		}
	}
}