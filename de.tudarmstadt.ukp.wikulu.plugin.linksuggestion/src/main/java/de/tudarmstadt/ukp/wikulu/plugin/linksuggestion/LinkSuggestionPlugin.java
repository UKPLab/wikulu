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
package de.tudarmstadt.ukp.wikulu.plugin.linksuggestion;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.semantics.keyphrases.wrapper.CooccurrenceGraphExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.linkdiscovery.annotator.LuceneLinkTargetSearcherRanker;
import de.tudarmstadt.ukp.dkpro.semantics.type.Keyphrase;
import de.tudarmstadt.ukp.dkpro.semantics.type.LinkTarget;
import de.tudarmstadt.ukp.dkpro.semantics.type.MultiLinkAnchor;
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;
import de.tudarmstadt.ukp.wikulu.core.wpl.WikuluPluginLoader;
import de.tudarmstadt.ukp.wikulu.exception.SectionNotFoundException;
import de.tudarmstadt.ukp.wikulu.util.DiffPatchMatch;

/**
 *
 *
 * @author C.Deeg
 *
 */
public class LinkSuggestionPlugin
	extends Plugin
{

	private final Logger logger = LoggerFactory
			.getLogger(LinkSuggestionPlugin.class);

	AnalysisEngine anchorCandidateDiscover;

	AnalysisEngine linkTargetDiscover;

	public LinkSuggestionPlugin(Wiki wiki)
	{
		super(wiki);


		System.out.println("TOTAL MEMORY:"+ Runtime.getRuntime().totalMemory());
		System.out.println("MAX MEMORY:"+ Runtime.getRuntime().maxMemory());
		System.out.println("FREE MEMORY:" + Runtime.getRuntime().freeMemory());

		String indexPath = WikuluPluginLoader.LUCENE_INDEX_PATH;

		//System.out.println(indexPath);

		try {
			//TODO: Language
			// anchor discovery
			CooccurrenceGraphExtractor cooccurrenceGraphExtractor = new CooccurrenceGraphExtractor();
			anchorCandidateDiscover = cooccurrenceGraphExtractor.getKeyphraseEngine();

			// target discovery
			AnalysisEngineDescription desc = AnalysisEngineFactory
					.createPrimitiveDescription(
							LuceneLinkTargetSearcherRanker.class,
							LuceneLinkTargetSearcherRanker.PARAM_LUCENE_INDEX,
							indexPath);

			linkTargetDiscover = AnalysisEngineFactory.createPrimitive(desc);
		}
		catch (ResourceInitializationException e) {
			logger.error("Error initializing link discovery uima pipeline", e);
		}
		catch (IOException e) {
			logger.error("Error initializing link discovery uima pipeline", e);
		}
	}

	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException
	{
		JSONObject argumentString = new JSONObject(super.unescapeString(text));


		if (argumentString.has("url")) {

			// Wikulu method: getLinkAnchorCandidates

			Page page = wiki.getPageForURL(new URL(super
					.unescapeString(argumentString.getString("url"))));

			// List<String> anchors =
			// linkDiscovery.discoverAnchors(page.getPlainContentFromWikiSyntax());
			List<String> anchors = new ArrayList<String>();

			// using our keyphrase extraction method
			// this can be enhanced using all noun phrases + document titles
			// as anchor candidates and then ranking them using tf.idf
			// this needs two more databases (holding the titles and the tf.idf
			// scores)!

			// initialize CAS

			JCas jcas = anchorCandidateDiscover.newJCas();

			String notReallyPlainText = page.getPlainContentFromWikiSyntax();
			notReallyPlainText = notReallyPlainText.replace("\n", " ");
			/*notReallyPlainText = notReallyPlainText.replace("\\s!.{*}", "");
			notReallyPlainText = notReallyPlainText.replace("[[.*][", "");
			notReallyPlainText = notReallyPlainText.replace("]]", "");*/
			jcas.setDocumentText(notReallyPlainText);
			jcas.setDocumentLanguage("en");

			// extract keywords
			anchorCandidateDiscover.process(jcas);

			List<String> keyphrases = filterAndSortKeyphrases(jcas);

			// Take only the first 30 possible keyphrases
			int maxKeyphrases = Math.min(keyphrases.size(), 30);
			for (int i = 0; i < maxKeyphrases; i++) {
				anchors.add(keyphrases.get(i));
			}

			Set<String> uniqueAnchors = new HashSet<String>();

			for (String i : anchors) {
				// uniqueAnchors.add(i.toUpperCase());
				if (!page.getArticleUrl().getPath().endsWith(i)) {
					uniqueAnchors.add(i.toUpperCase());
				}
			}
			// avoid duplicates

			JSONObject json = new JSONObject();
			json.put("anchors", uniqueAnchors);
			return json.toString();

		}
		else if (argumentString.has("location")) {

			// Wikulu method: confirmLinkCandidate

			// String word must be case sensitive
			String urlString = argumentString.getString("location");
			String linkCandidateName = argumentString.getString("candidate");
			String word = argumentString.getString("word");
			int wordNumber = argumentString.getInt("wordnumber");

			// Regex for word
			String regex = "\\b" + word.toLowerCase() + "\\b";
			Pattern p = Pattern.compile(regex);

			// get plain text
			Page page = wiki.getPageForURL(new URL(urlString));
			String plainText = page.getPlainContentFromWikiSyntax()
					.toLowerCase();

			// find position in plaintext
			Matcher matcher = p.matcher(plainText);
			int pos = -1;
			for (int i = 0; i < wordNumber + 1; i++) {
				if (matcher.find()) {
					pos = matcher.start();
				}
				else {
					pos = -1;
				}

			}

			JSONObject returnValue = new JSONObject();

			// No word was found
			if (pos == -1) {
				returnValue.put("boolvalue", false);
				return returnValue.toString();
			}
			// get Wiki content for the current page
			String wikiText = page.getWikiSyntaxContent();
			String wikiTextLowerCase = page.getWikiSyntaxContent()
					.toLowerCase();

			// Compute offset in Wiki from offset in plain text using
			// diffpatchmatch
			float relativeOffset = ((float) pos / (float) plainText.length());
			int guessedOffsetInWiki = Math.round(wikiText.length()
					* relativeOffset);
			int offsetInWiki = 0;
			try {
				offsetInWiki = getOffsetInWiki(wikiTextLowerCase, plainText,
						pos, guessedOffsetInWiki);
			}
			catch (SectionNotFoundException e) {
				e.printStackTrace();
			}

			// Get's precise position of word in wiki text

			matcher = p.matcher(wikiTextLowerCase.substring(offsetInWiki));
			int possibleAfterPosition = -1;
			if (matcher.find()) {
				possibleAfterPosition = matcher.start();
			}

			matcher = p.matcher(wikiTextLowerCase.substring(0, offsetInWiki));
			int possibleBeforePosition = -1;
			while (matcher.find()) {
				possibleBeforePosition = matcher.start();
			}

			// int possibleAfterPosition =
			// wikiTextLowerCase.indexOf(word.toLowerCase(), offsetInWiki);
			// int possibleBeforePosition = wikiTextLowerCase.substring(0,
			// offsetInWiki).lastIndexOf(word.toLowerCase());
			int realOffestInWiki;
			if (possibleAfterPosition - offsetInWiki > offsetInWiki
					- possibleBeforePosition) {
				realOffestInWiki = possibleBeforePosition;
			}
			else {
				realOffestInWiki = offsetInWiki + possibleAfterPosition;
			}

			// TODO get MediaWiki/TWiki class which extends Wikulu and call
			// createLink
			// Creates new wikitext with link
//			PathMatchingResourcePatternResolver pm = new PathMatchingResourcePatternResolver();
//			String pattern = "classpath*:/de/tudarmstadt/**/"
//					+ this.wiki.getClass().getSimpleName() + ".class";
//
//			Resource[] foundClasses = pm.getResources(pattern);
//			String createLinkResult = null;
//			if (foundClasses.length > 0) {
//				for (Resource cl : foundClasses) {
//					String[] parts = cl.getURL().toString().split("/classes/");
//					String source;
//					if (parts.length == 1) {
//						String[] sources = parts[0].split("!/");
//						source = sources[1];
//						source = source.replace(
//								System.getProperty("file.separator"), ".");
//						System.out.println("#### CLASS TO LOAD IS: " + source);
//					}
//					else {
//						System.out.println("##### CLASS FOUND WAS: "
//								+ cl.getURL().toString());
//						source = parts[1];
//						// replace the file (not path!) separators with periods
//						source = source.replace(
//								System.getProperty("file.separator").charAt(0),
//								'.');
//					}
//					// Object classObject = null;
//					source = source.replace(".class", "");
//					Class wClass;
//					try {
//						wClass = Class.forName(source);
//						if (!wClass.getSuperclass().getName()
//								.contains("Wikulu")) {
//							continue;
//						}
//						// Constructor con = wClass.getConstructors()[0];
//						Object classObject = wClass.newInstance();
//
//						Class[] paramTypes = new Class[] { String.class,
//								String.class };
//						Object[] args = new Object[] { word, linkCandidateName };
//						Method m = wClass.getMethod("createLink", paramTypes);
//
//						createLinkResult = (String) m.invoke(classObject, args);
//					}
//					catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//					catch (InstantiationException e) {
//						e.printStackTrace();
//					}
//					catch (IllegalAccessException e) {
//						e.printStackTrace();
//					}
//					catch (SecurityException e) {
//						e.printStackTrace();
//					}
//					catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					}
//					catch (IllegalArgumentException e) {
//						e.printStackTrace();
//					}
//					catch (InvocationTargetException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			if (createLinkResult == null) {
//				return null;
//			}
			// String wikiTextNew = wikiText.substring(0,
			// realOffestInWiki)+createLinkResult+wikiText.substring(realOffestInWiki+word.length());
			// Creates new wikitext with link
			String wikiTextNew = wikiText.substring(0, realOffestInWiki)
					+ wiki.createLink(word, linkCandidateName)
					+ wikiText.substring(realOffestInWiki + word.length());

			// writes wiki page
			wiki.writePage(urlString, wikiTextNew);

			// System.out.println("Done!");
			returnValue.put("boolvalue", true);
			return returnValue.toString();
		} else if (argumentString.has("anchor")) {
			List<String> anchors = discoverTargetsForAnchor(argumentString.getString("anchor"));
			JSONObject returnValue = new JSONObject();
			for(String i: anchors) {
				;
//				wiki.getViewUrl()
				//TODO: hack for wikipedia
				//String hack = i.replace("http://localhost", "/w");
				//returnValue.accumulate("targets", hack);
				returnValue.append("targets", wiki.getRelativeURL(i));
			}

			return returnValue.toString();
		}
		return null;
	}

	private List<String> filterAnchorsWithoutTargets(
			List<Keyphrase> keyphrases) throws ResourceInitializationException, AnalysisEngineProcessException {
		List<String> filteredKeyphrases = new ArrayList<String>();

		JCas jcas = linkTargetDiscover.newJCas();
		jcas.setDocumentLanguage("en");
		String text = "";
		for(Keyphrase keyphrase : keyphrases){
			text += keyphrase.getCoveredText();
			text += " - ";
		}
		jcas.setDocumentText(text);

		int offset = 0;
		for(Keyphrase keyphrase : keyphrases){
			MultiLinkAnchor mla = new MultiLinkAnchor(jcas);
			mla.setBegin(offset);
			mla.setEnd(offset + keyphrase.getCoveredText().length());
			mla.addToIndexes();
			offset += keyphrase.getCoveredText().length() + 3;
		}
		

		linkTargetDiscover.process(jcas);
		
		for(MultiLinkAnchor mla : JCasUtil.select(jcas, MultiLinkAnchor.class)){
			if(mla.getLinkTargets().size() > 0){
				filteredKeyphrases.add(mla.getCoveredText());
			}
		}

		return filteredKeyphrases;
	}

	public List<String> discoverTargetsForAnchor(String anchor)
		throws ResourceInitializationException, AnalysisEngineProcessException
	{

		List<String> targets = new ArrayList<String>();

		JCas jcas = linkTargetDiscover.newJCas();
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText(anchor);

		MultiLinkAnchor mla = new MultiLinkAnchor(jcas);
		mla.setBegin(0);
		mla.setEnd(anchor.length());
		mla.addToIndexes();

		linkTargetDiscover.process(jcas);

		FSArray linkTargets = mla.getLinkTargets();

		if (linkTargets != null) {
			for (int i = 0; i < linkTargets.size(); i++) {
				LinkTarget target = (LinkTarget) linkTargets.get(i);
				if(target !=null) {
					targets.add(target.getTargetName());
				}
			}
		}

		return targets;
	}

	@SuppressWarnings("unchecked")
	private List<String> filterAndSortKeyphrases(JCas jcas) throws AnalysisEngineProcessException, ResourceInitializationException
	{

		// get a set of all keyphrases
		List<Keyphrase> keyphrases = new ArrayList<Keyphrase>();
		FSIterator keyphraseIter = jcas.getAnnotationIndex(Keyphrase.type)
				.iterator();
		while (keyphraseIter.hasNext()) {
			keyphrases.add((Keyphrase) keyphraseIter.next());
		}

		
		// sort the keyphrases
		Collections.sort(keyphrases, new KeyphraseComparator());

		//Should only keyphrases with links be returned?
		List<String> keyphraseStrings= filterAnchorsWithoutTargets(keyphrases);
		
		HashSet<String> uniqueKeyphrases = new HashSet<String>(keyphraseStrings);

		return new ArrayList<String> (uniqueKeyphrases);
	}

	private class KeyphraseComparator
		implements Comparator<Keyphrase>
	{
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

	/*
	 * Uses a text matching algorithm to find the best guess for the offset in
	 * the wiki markup.
	 */
	private int getOffsetInWiki(String wikiText, String plainText,
			int offsetInPlainText, int guessedOffsetInWiki)
		throws SectionNotFoundException
	{
		int surroundingSize = 10;

		DiffPatchMatch dpm = new DiffPatchMatch();
		float threshold = dpm.Match_Threshold;

		int offsetInWiki = -1;
		int counter = -1;

		while (offsetInWiki == -1) {

			String offsetSurrounding = plainText.substring(offsetInPlainText
					- surroundingSize, offsetInPlainText + surroundingSize);
			offsetInWiki = dpm.match_main(wikiText, offsetSurrounding,
					guessedOffsetInWiki);
			counter++;
			if (counter % 2 == 1) {
				if (threshold == 1) {
					throw new SectionNotFoundException();
				}
				// Increase threshold by 0.1
				threshold += 0.1f;
				dpm.Match_Threshold = threshold;
			}
			else {
				// Increase surrounding size by 5
				surroundingSize += 5;
			}
		}

		return offsetInWiki;
	}

}
