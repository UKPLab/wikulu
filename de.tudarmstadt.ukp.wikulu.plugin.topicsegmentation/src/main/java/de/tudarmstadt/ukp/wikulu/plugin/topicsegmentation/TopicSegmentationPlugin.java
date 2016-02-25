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
package de.tudarmstadt.ukp.wikulu.plugin.topicsegmentation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.AnalysisEngineFactory;


import de.tudarmstadt.ukp.dkpro.core.annotator.XMLTextExtractor;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.tudarmstadt.ukp.dkpro.core.type.PlainText;
import de.tudarmstadt.ukp.dkpro.semantics.segmentation.C99segmenter;
import de.tudarmstadt.ukp.dkpro.semantics.type.Segment;
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;
import de.tudarmstadt.ukp.wikulu.core.plugin.Plugin;
import de.tudarmstadt.ukp.wikulu.exception.SectionNotFoundException;
import de.tudarmstadt.ukp.wikulu.util.DiffPatchMatch;

public class TopicSegmentationPlugin
	extends Plugin
{
	final Logger logger = LoggerFactory
			.getLogger(TopicSegmentationPlugin.class);

	private static final String SEGMENT_MARKER_CLASS = "ukp_topic_segment_marker";

	private AnalysisEngine textSegmenter;

	public TopicSegmentationPlugin(Wiki wiki)
	{
		super(wiki);
		try {
			AnalysisEngineDescription segmenter = AnalysisEngineFactory.createPrimitiveDescription(StanfordSegmenter.class);

			AnalysisEngineDescription topicSegmenter = AnalysisEngineFactory.createPrimitiveDescription(
					C99segmenter.class);

			AnalysisEngineDescription desc = AnalysisEngineFactory
					.createAggregateDescription(segmenter, topicSegmenter);

			textSegmenter = AnalysisEngineFactory.createAggregate(desc);
		}
		catch (ResourceInitializationException e) {
			logger.error("Error initializing Topic Segmentation web service", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, CASException, IOException,
		ResourceConfigurationException, JDOMException
	{
		JSONObject arguments = new JSONObject(text);
		// if arguments has a key "url", call confirmTopicSegment
		if (arguments.has("url")) {
			// call confirmTopicSegment:
			// get plain text
			String urlString = super.unescapeString(arguments.getString("url"));
			Page page = this.wiki.getPageForURL(new URL(urlString));
			String plainText = page.getPlainContentFromWikiSyntax();

			// get Wiki content for the current page
			// String wikiText = wiki.getWikiPageContent(urlString);
			String wikiText = page.getWikiSyntaxContent();

			// get content words around offset in plain text

			// match to Wiki markup
			// best guess for location in wiki text is
			// the relative offset in the plain text
			int offsetInPlainText = arguments.getInt("offset");
			float relativeOffset = ((float) offsetInPlainText / (float) plainText
					.length());
			int guessedOffsetInWiki = Math.round(wikiText.length()
					* relativeOffset);

			// String offsetSurrounding =
			// plainText.substring(offsetInPlainText-10,
			// offsetInPlainText+10);
			String title = super.unescapeString(arguments.getString("title"));
			int offsetInWiki;
			try {
				offsetInWiki = getOffsetInWiki(wikiText, plainText,
						offsetInPlainText, guessedOffsetInWiki);
				if (title.isEmpty()) {
					title = "New Section";
				}

				insertSectionInWikiPage(urlString, title, offsetInWiki);
				return "true";
			}
			catch (SectionNotFoundException e) {
				logger.info("Could not find a corresponding offset in the wiki markup.");
				return "false";
			}
		}
		else {
			// call getTopicSuggestions:
			String content = super.unescapeString(arguments.getString("text"));
			// change all <br> tags to <br/>
			// innerHtml = innerHtml.replace("<br>", "<br/>");
			content = content.replace("&nbsp;", "&#160;");
			content = "<root>" + content + "</root>";
			int pos = -1, endTagPos = 0;
			// for img tag
			while (true) {

				pos = content.indexOf("<img", pos + 1);
				if (pos == -1) {
					break;
				}
				endTagPos = content.indexOf(">", pos);
				String prefix = content.substring(0, endTagPos);
				String sufix = content.substring(endTagPos + 1,
						content.length());
				content = prefix + " />" + sufix;
			}
			pos = -1;
			endTagPos = 0;
			// for strange <br> tags
			while (true) {

				pos = content.indexOf("<br", pos + 1);
				if (pos == -1) {
					break;
				}
				endTagPos = content.indexOf(">", pos);
				String prefix = content.substring(0, endTagPos);
				String sufix = content.substring(endTagPos + 1,
						content.length());
				content = prefix + " />" + sufix;
			}

			// create needed components
			AnalysisEngine xmlTextExtractor = createXMLTextExtractor();
			// TopicSegmenter segmenter = new TopicSegmenter();

			// extract and annotate plain text using the XMLTextExtractor
			JCas jcas = xmlTextExtractor.newJCas();
			JCas xmlView = jcas.createView(XMLTextExtractor.VIEW_XML);
			xmlView.setDocumentLanguage("en");
			xmlView.setDocumentText(content);
			xmlTextExtractor.process(xmlView);

			// call the topic segmentation service to detect possible segments
			// in
			// the plain text
			JCas plainView = jcas.getView(XMLTextExtractor.VIEW_PLAIN);
			String plainText = plainView.getDocumentText();
			// Integer[] offsetsFromSegmenter = segmenter
			// .getSegmentOffsets(plainText);

			// initialize CAS
			JCas jcas2 = textSegmenter.newJCas();

			jcas2.setDocumentText(plainText);
			jcas2.setDocumentLanguage("en");

			// segment text
			textSegmenter.process(jcas2);

			List<Integer> segmentOffsets = new ArrayList<Integer>();

			FSIterator segmentIter = jcas2.getAnnotationIndex(Segment.type)
					.iterator();

			// skip the first segment - we always take the beginning of a
			// segment
			// as the boundary
			if (segmentIter.hasNext()) {
				segmentIter.next();
			}

			while (segmentIter.hasNext()) {
				Segment segment = (Segment) segmentIter.next();
				segmentOffsets.add(segment.getBegin());
			}

			Integer[] offsetsFromSegmenter = segmentOffsets
					.toArray(new Integer[segmentOffsets.size()]);

			String[][] xpathAndContent = extractCSSAndContent(
					offsetsFromSegmenter, plainView);
			JSONArray all = new JSONArray();
			for (int i = 0; i < xpathAndContent.length; i++) {
				ArrayList<String> nextContent = new ArrayList<String>();
				String buf = xpathAndContent[i][0];
				Pattern digitPattern = Pattern.compile("(\\d+)"); // Decrements
																	// each
				// number by 1
				Matcher matcher = digitPattern.matcher(buf);
				StringBuffer result = new StringBuffer();
				while (matcher.find()) {
					matcher.appendReplacement(result, String.valueOf(Integer
							.parseInt(matcher.group(1)) - 1));
				}
				matcher.appendTail(result);
				nextContent.add(result.toString());
				nextContent.add(xpathAndContent[i][1]);
				all.put(nextContent);
			}

			return all.toString();
		}
	}

	@SuppressWarnings("unchecked")
	private String getTitleInWikiSyntax(String title)
	{
		// TODO: move this method to superclass?
		// this method is implemented by subclasses of Wikulu
		String wikiClassName = this.wiki.getClass().getName();
		String result = null;

		String fullPath = wikiClassName.replace("de.tudarmstadt.ukp.wikiapi.",
				"de.tudarmstadt.ukp.wikulu.");
		try {
			Class c = Class.forName(fullPath);
			Object o = c.newInstance();
			Class[] params = new Class[] { String.class };
			Method m = c.getMethod("getTitleInWikiSyntax", params);
			result = (String) m.invoke(o, new Object[] { title });
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Creates the AnalysisEngine needed for extracting and annotating the plain
	 * text from XML
	 * 
	 * @return AnalysisEngine for the XMLTextExtractor component
	 */
	private AnalysisEngine createXMLTextExtractor()
	{
		AnalysisEngine xmlTextExtractor = null;

		try {
			ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();

			URL descriptor = resMgr
					.resolveRelativePath("desc/annotator/XMLTextExtractor.xml");

			AnalysisEngineDescription description = UIMAFramework
					.getXMLParser().parseAnalysisEngineDescription(
							new XMLInputSource(descriptor));
			// AnalysisEngineMetaData metaData =
			// description.getAnalysisEngineMetaData();

			xmlTextExtractor = UIMAFramework.produceAnalysisEngine(description,
					resMgr, null);
		}
		catch (IOException e) {
			System.err.println("Error initializing servlet:"
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		catch (InvalidXMLException e) {
			System.err.println("Error initializing servlet:"
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		catch (ResourceInitializationException e) {
			System.err.println("Error initializing servlet:"
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}

		assert (xmlTextExtractor != null);

		return xmlTextExtractor;
	}

	/**
	 * Extracts CSS path and content to change
	 * 
	 * @param offsets
	 *            offsets in which marker must be inserted
	 * @param plainView
	 *            plainView(with plain text)
	 * @return array of css path and content
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private String[][] extractCSSAndContent(Integer[] offsets, JCas plainView)
		throws IOException
	{

		String[][] out = new String[offsets.length][2];
		FSIterator plainTextIter = plainView.getAnnotationIndex(PlainText.type)
				.iterator();

		for (int i = 0; i < offsets.length; i++) {
			while (plainTextIter.hasNext()) {
				PlainText text = (PlainText) plainTextIter.next();
				// System.out.println(text.getBegin());
				// System.out.println(text.getEnd());
				// System.out.println(text.getCoveredText());
				// System.out.println(text.getOriginalXPath());
				// for last off
				if ((plainTextIter.hasNext() == false)
						&& ((text.getEnd() == offsets[i]))) {
					out[i][0] = convertXpathtoCSS(text.getOriginalXPath()
							.substring(9));
					out[i][1] = insertMarker(text, offsets[i]);
				}
				if ((text.getBegin() <= offsets[i])
						&& (text.getEnd() > offsets[i])) {
					// erase root tag
					out[i][0] = convertXpathtoCSS(text.getOriginalXPath()
							.substring(9));
					out[i][1] = insertMarker(text, offsets[i]);
					break;
				}

			}
		}

		return out;

	}

	/**
	 * Converts xPath expression to CSS expression
	 * 
	 * @param xPath
	 *            expression
	 * @return css expression
	 */
	private String convertXpathtoCSS(String xPath)
	{
		xPath = xPath.replaceAll("/", " > ");
		xPath = xPath.replaceAll("\\[", ":eq(");
		xPath = xPath.replaceAll("\\]", ")");
		return xPath;
	}

	/**
	 * Insert marker in plain text
	 * 
	 * @param text
	 *            text in which insert
	 * @param offset
	 *            where to insert
	 * @return new string with marker
	 */
	private String insertMarker(PlainText text, Integer offset)
	{
		String beforeMarker = text.getCoveredText().substring(0,
				offset - text.getBegin());
		String afterMarker = text.getCoveredText().substring(
				offset - text.getBegin());
		String titleID = "segment_title_" + offset;
		String segmentMarker = "<div class=\"" + SEGMENT_MARKER_CLASS + "\">"
				+ "Title: " + "<input " + "placeholder=\"Section title...\" "
				+ "type=\"text\" " + "id=\"" + titleID + "\" "
				+ " style=\"margin-right:10px\" />"
				+ "<button onclick=\"javascript:confirmTopicSegment(" + offset
				+ ")\">confirm</button>" + "</div>";

		return beforeMarker + segmentMarker + afterMarker;
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
				if (threshold == 1)
					throw new SectionNotFoundException();
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

	/**
	 * This method is called by confirmTopicSegment. It writes the section
	 * heading to the wiki database
	 * 
	 * @param urlString
	 *            URL to the page where to add the section
	 * @param title
	 *            The new section title
	 * @param offset
	 *            The offset in the page where the section should be located
	 * @throws JDOMException
	 * @throws IOException
	 */
	protected void insertSectionInWikiPage(String urlString, String title,
			int offset)
		throws JDOMException, IOException
	{
		Page page = this.wiki.getPageForURL(new URL(urlString));

		// get wiki text
		String wikiText = page.getWikiSyntaxContent();

		// move offset to the next sentence boundary - TODO this is a hack for
		// the demo,
		// and should not be needed anymore later
		int cleanedOffset = offset;

		while ((cleanedOffset < wikiText.length())
				&& (wikiText.charAt(cleanedOffset) != '.')) {
			cleanedOffset++;
		}

		// move behind '.'
		cleanedOffset += 2;

		// insert section at the right offset
		String beforeTitle = wikiText.substring(0, cleanedOffset);
		String afterTitle = wikiText
				.substring(cleanedOffset, wikiText.length());

		String modifiedText = beforeTitle + getTitleInWikiSyntax(title)
				+ afterTitle;

		// write modified text back to the wiki
		this.wiki.writePage(urlString, modifiedText);
	}

}
