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
package de.tudarmstadt.ukp.wikulu.plugin.wikify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;

public class WikifyPlugin
	extends Plugin
{

	// Logger
	private final Logger logger = LoggerFactory.getLogger(WikifyPlugin.class);
	// Mac titles count per one mediawiki query
	private static final int MAX_TITLES_COUNT = 50;

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
	private CooccurrenceGraphExtractor keyphraseExtractor;
	// TextCategorizer - language recognizer
	private final TextCategorizer categorizer;
	// Text language
	private String lang;

	/**
	 * Initial setup of WikifyPlugin
	 */
	public WikifyPlugin(Wiki wiki)
	{
		super(wiki);
		System.out.println("WORKS!!");
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
	 */
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException
	{
		JSONObject arguments = new JSONObject(text);

		// identify language
		String unescapeText = super.unescapeString(arguments.getString("text"));
		String result = categorizer.categorize(unescapeText);
		System.out.println("#### RESULT IS: " + result);
		lang = langName2ISO.get(result);
		System.out.println("#### LANG IS: " + lang);

		// create appropriate descriptor (works only with en or de lang)
		List<Keyphrase> keyphrases = null;
		try {
			keyphraseExtractor = new CooccurrenceGraphExtractor();
			keyphraseExtractor.setLanguage(lang);
			keyphrases = keyphraseExtractor.extract(unescapeText);
		}
		catch (IOException e) {
			logger.error("Error initializing Keyphrase web service", e);
		}

		keyphrases = filterAndSortKeyphrases(keyphrases);

		String[] keyphraseStrings = new String[keyphrases.size()];

		// store keyphrases to check
		for (int i = 0; i < keyphrases.size(); i++) {
			keyphraseStrings[i] = keyphrases.get(i).getKeyphrase();
		}

		// set max keyphrase count
		int keyphraseCount = (keyphraseStrings.length > keyphraseMaxCount) ? keyphraseMaxCount
				: keyphraseStrings.length;

		// Fill array with keyphrases to send as a query(one query can contain
		// max. 50 titles)
		// Use list of string, each string in this list contains max 50 titles
		// separated with "|"
		// example : Test|Bank|Apple|Table
		List<String> titlesToCheck = new ArrayList<String>();
		StringBuilder titles = new StringBuilder();
		int j = 1;
		for (int i = 0; i < keyphraseStrings.length; i++) {
			if (keyphraseStrings.length - 1 == i
					|| j == WikifyPlugin.MAX_TITLES_COUNT) {
				titles.append(keyphraseStrings[i]);
				titlesToCheck.add(titles.toString());
				titles = new StringBuilder();
				j = 1;
			}
			else {
				titles.append(keyphraseStrings[i] + "|");
			}
			j++;
		}
		//

		// Check titles for existence
		List<String> realTitles = new ArrayList<String>();
		for (String i : titlesToCheck) {
			realTitles.addAll(getRealTitles(i));
			if (realTitles.size() >= keyphraseCount)
				break;
		}

		// create output json string
		JSONObject jsonReturn = new JSONObject();

		jsonReturn.put("keyphrases", realTitles);
		jsonReturn.put("prefix", "http://" + lang + ".wikipedia.org/wiki/");
		jsonReturn.put("count", keyphraseCount);
		return jsonReturn.toString();
	}

	/**
	 * Check existence of keyphrases in wikipedia titles
	 * 
	 * @param titlesToCheck
	 *            titles to check
	 * @return titles that exists in wikipedia
	 */
	private List<String> getRealTitles(String titlesToCheck)
	{
		// output list
		List<String> keyphr = new LinkedList<String>();
		try {
			// request titles status from wikipedia
			URL wikiAPI = new URL("http://" + lang
					+ ".wikipedia.org/w/api.php?");

			URLConnection connection = wikiAPI.openConnection();
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write("action=query&titles=" + titlesToCheck
					+ "&format=json");
			writer.flush();
			writer.close();
			//

			// read response
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line = null;
			// System.out.println("response:");
			StringBuffer output = new StringBuffer();
			// save response in stringbuffer
			while ((line = reader.readLine()) != null)
				output.append(line);
			reader.close();
			//

			// Check existence
			JSONObject a = new JSONObject(output.toString());
			JSONObject query = (JSONObject) a.get("query");
			JSONObject pages = (JSONObject) query.get("pages");

			Iterator<?> keys = pages.keys();

			while (keys.hasNext()) {
				Integer nextInt = Integer.parseInt((String) keys.next());
				if (nextInt > -1) {
					JSONObject existedPage = (JSONObject) pages.get(nextInt
							.toString());
					keyphr.add(existedPage.getString("title"));

				}

			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return keyphr;
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
