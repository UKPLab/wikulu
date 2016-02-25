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
package de.tudarmstadt.ukp.wikulu.plugin.travelogue;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.JDOMException;

import de.tudarmstadt.ukp.dkpro.core.annotator.XMLTextExtractor;
import de.tudarmstadt.ukp.dkpro.core.type.PlainText;
import de.tudarmstadt.ukp.dkpro.geocoding.route.Pipeline;
import de.tudarmstadt.ukp.dkpro.geocoding.travelogue.Travelogue;
import de.tudarmstadt.ukp.dkpro.geocoding.travelogue.TravelogueExecution;
import de.tudarmstadt.ukp.dkpro.geocoding.travelogue.TravelogueLanguage;
import de.tudarmstadt.ukp.dkpro.geocoding.type.Location;
import de.tudarmstadt.ukp.dkpro.geocoding.type.LocationText;

public class TraveloguePlugin
	extends de.tudarmstadt.ukp.wikulu.core.plugin.Plugin
{

	/**
	 * Makes a html text valid so that the XMLTextExtractor can parse it
	 * 
	 * @param htmlText
	 *            HTML text
	 * @return valid XML text
	 * @see Copied from TopicSegmentationPlugin.run()
	 */
	public static String makeHTMLvalidForXMLTextExtractor(String htmlText)
	{
		String content = htmlText;
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
			String sufix = content.substring(endTagPos + 1, content.length());
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
			String sufix = content.substring(endTagPos + 1, content.length());
			content = prefix + " />" + sufix;
		}
		return content;

	}

	private List<Location> route = new LinkedList<Location>();
	private Collection<LocationText> texts = new LinkedList<LocationText>();
	private JCas plainView;

	@Override
	public String run(String text)
		throws AnalysisEngineProcessException, ResourceInitializationException,
		JSONException, ResourceConfigurationException, IOException,
		CASException, JDOMException
	{
		JSONObject arguments = new JSONObject(text);
		String travelogueText = super.unescapeString(arguments
				.getString("text"));

		travelogueText = makeHTMLvalidForXMLTextExtractor(travelogueText);
		Pipeline prepareTextPipeline = new Pipeline();
		prepareTextPipeline.setText(travelogueText, "English");
		JCas xmlText = prepareTextPipeline.getJCas().createView(
				XMLTextExtractor.VIEW_XML);
		xmlText.setDocumentLanguage("English");
		xmlText.setDocumentText(travelogueText);
		prepareTextPipeline.addAE(XMLTextExtractor.class);
		prepareTextPipeline.run();
		JCas plainTravelogueJCas = prepareTextPipeline.getJCas();

		plainView = plainTravelogueJCas.getView(XMLTextExtractor.VIEW_PLAIN);
		String plainTravelogueText = plainView.getDocumentText();
		Travelogue travelogue = new Travelogue(plainTravelogueText);
		TravelogueExecution tE = new TravelogueExecution(travelogue);
		tE.setLanguage(TravelogueLanguage.English);
		tE.setAlgorithmName("TravelingSalesmanRoute");
		tE.setParameters("textDistance=0.6");
		try {
			tE.execute();
			route = tE.getRoute();
			texts = tE.getLocationTexts();

			JSONObject result = new JSONObject();

			result.append("route", createJSONRoute());
			result.append("texts", createJSONTexts(plainView));

			System.out.println(result.toString());
			return result.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	private PlainText plainText = null;
	private FSIterator<Annotation> plainTextIter = null;

	private JSONArray createJSONTexts(JCas plainView)
		throws JSONException
	{
		plainTextIter = plainView.getAnnotationIndex(PlainText.type).iterator();
		plainText = null;
		JSONArray textArray = new JSONArray();
		int index = 0;
		for (LocationText lText : texts) {
			textArray.put(index, getLocationInformation(lText));
			index++;

		}
		return textArray;
	}

	private JSONObject getLocationInformation(LocationText lt)
		throws JSONException
	{
		int[] offset = new int[] { lt.getBegin(), lt.getEnd() };
		int[] internalOffset = new int[2];
		String[] paths = new String[2];
		boolean next = plainText == null;
		while (!next || plainTextIter.hasNext()) {
			if (next) {
				plainText = (PlainText) plainTextIter.next();
			}
			int plainTextBegin = plainText.getBegin();
			int plainTextEnd = plainText.getEnd();
			boolean end = false;
			for (int n = 0; n < 2; n++) {
				if (offset[n] >= plainTextBegin && offset[n] <= plainTextEnd) {
					internalOffset[n] = offset[n] - plainTextBegin;
					paths[n] = plainText.getOriginalXPath() + "/";
					if (n == 1) {
						end = true;
					}
				}
			}
			if (end) {
				break;
			}
			next = true;
		}

		// String commonPath = StringUtils.getCommonPrefix(new String[] {
		// paths[0], paths[1] });
		// commonPath = commonPath.substring(0, commonPath.lastIndexOf("/"));

		JSONObject locationTextInfo = new JSONObject();
		locationTextInfo.append("name", lt.getLocation().getCoveredText());
		locationTextInfo.append("plainBegin", lt.getBegin());
		locationTextInfo.append("plainEnd", lt.getEnd());
		locationTextInfo.append("beginPath", paths[0]);
		locationTextInfo.append("endPath", paths[1]);
		locationTextInfo.append("beginOffset", internalOffset[0]);
		locationTextInfo.append("endOffset", internalOffset[1]);
		locationTextInfo.append("id", getLocationId(lt.getLocation()));

		return locationTextInfo;
	}

	private String getLocationId(Location location)
	{
		return String.valueOf(route.indexOf(location));
	}

	private JSONArray createJSONRoute()
		throws JSONException
	{
		JSONArray routeArray = new JSONArray();
		int index = 0;
		for (Location loc : route) {
			JSONObject location = createJSLocation(loc);
			routeArray.put(index, location);
			index++;
		}
		return routeArray;
	}

	private JSONObject createJSLocation(Location loc)
		throws JSONException
	{
		JSONObject location = new JSONObject();
		location.append("name", loc.getCoveredText());
		location.append("start", loc.getBegin());
		location.append("end", loc.getEnd());
		location.append("id", getLocationId(loc));
		return location;
	}

	public JSONArray createRouteHTMLCode(JCas jCas)
		throws JSONException
	{

		JSONArray textArray = new JSONArray();
		int index = 0;
		for (LocationText loc : texts) {
			JSONObject location = new JSONObject();
			location.append("name", loc.getLocation().getCoveredText());
			location.append("start",
					calculateHTMLPosition(loc.getBegin(), jCas));
			location.append("end", calculateHTMLPosition(loc.getEnd(), jCas));
			location.append("id", getLocationId(loc.getLocation()));
			textArray.put(index, location);
			index++;
		}
		return textArray;
		// return htmlText;
	}

	public int calculateHTMLPosition(int position, JCas jcas)
	{
		return position - 6;
	}
}
