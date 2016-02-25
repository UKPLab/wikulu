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
package de.tudarmstadt.ukp.dkpro.core.annotator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.tudarmstadt.ukp.dkpro.core.type.PlainText;


public class XMLTextExtractorTest
{
	@Test
	public void xmlTextExtractorTest()
		throws Exception
	{
		String testDocument = "<tag1 cs=\"dsadd\">This is a test <tag2 csd=\"dasdasdaf asdsadd\">file</tag2>. <tag3>This is</tag3> another one to extract keywords from.</tag1>";
		String documentLanguage = "en";

		AnalysisEngine ae = createPrimitive(XMLTextExtractor.class);

		JCas jcas = ae.newJCas();
		JCas xmlView = jcas.createView(XMLTextExtractor.VIEW_XML);
		xmlView.setDocumentText(testDocument);
		xmlView.setDocumentLanguage(documentLanguage);

		ae.process(jcas);

		FSIterator plainTextIter = jcas.getView(XMLTextExtractor.VIEW_PLAIN)
				.getAnnotationIndex(PlainText.type).iterator();

		int i = 0;

		while (plainTextIter.hasNext()) {

			PlainText text = (PlainText) plainTextIter.next();
			System.out.println(text.getCoveredText());
			switch (i) {
			case 0:
				assertEquals("This is a test ", text.getCoveredText());
				// assertEquals(6, text.getOriginalOffset());
				break;
			case 1:
				assertEquals("file", text.getCoveredText());
				// assertEquals(27, text.getOriginalOffset());
				break;
			case 2:
				assertEquals(". ", text.getCoveredText());
				// assertEquals(38, text.getOriginalOffset());
				break;
			case 3:
				assertEquals("This is", text.getCoveredText());
				// assertEquals(46, text.getOriginalOffset());
				break;
			case 4:
				assertEquals(" another one to extract keywords from.",
						text.getCoveredText());
				// assertEquals(60, text.getOriginalOffset());
				break;
			}
			//
			i++;
		}

		assertEquals(5, i);
	}

	@Test
	public void xpathMarkTest()
		throws Exception
	{
		xpathMark("<html>Text1 <div> Text2 </div>Text3<div> Text4</div><div/><p>Text5<div>Text6</div>Text7</p>Text8  </html>");
		xpathMark("<html> Text2 <div>Text3</div><div> Text4</div><div/>Text5<p><div>Text6</div></p>Text8  </html> ");
		xpathMark("<html> Text2 <div>Text3</div><div> Text4</div><div/>Text5<p><div>Text6</div>Text7</p>Text8  </html>  ");
	}

	private void xpathMark(String testDocument)
		throws Exception
	{
		// String testDocument =
		// "<html>Text1 <div> Text2 </div>Text3<div> Text4</div><div/><p>Text5<div>Text6</div>Text7</p>Text8  </html>";
		String documentLanguage = "en";
		AnalysisEngine ae = createPrimitive(XMLTextExtractor.class);

		JCas jcas = ae.newJCas();
		JCas xmlView = jcas.createView(XMLTextExtractor.VIEW_XML);
		xmlView.setDocumentText(testDocument);
		xmlView.setDocumentLanguage(documentLanguage);

		ae.process(jcas);

		FSIterator plainTextIter = jcas.getView(XMLTextExtractor.VIEW_PLAIN)
				.getAnnotationIndex(PlainText.type).iterator();

		// int i = 0;
		HashMap<String, String> map = new HashMap<String, String>();

		while (plainTextIter.hasNext()) {
			PlainText text = (PlainText) plainTextIter.next();
			map.put(text.getOriginalXPath(), text.getCoveredText());
		}
		assertTrue("Xpath Test", xpathCompareTest(map, testDocument));
		// assertEquals(5, i);
	}

	private boolean xpathCompareTest(HashMap<String, String> map, String text)
		throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder;
		XPath xpath = XPathFactory.newInstance().newXPath();
		builder = factory.newDocumentBuilder();
		byte[] stringBytes = text.getBytes("UTF-8");
		Document doc = builder.parse(new ByteArrayInputStream(stringBytes));
		for (String i : map.keySet()) {
			NodeList result = (NodeList) xpath.evaluate(i, doc,
					XPathConstants.NODESET);
			assertEquals(1, result.getLength());
			if (!result.item(0).getNodeValue().equals(map.get(i))) {
				return false;
			}

		}
		return true;
	}
}
