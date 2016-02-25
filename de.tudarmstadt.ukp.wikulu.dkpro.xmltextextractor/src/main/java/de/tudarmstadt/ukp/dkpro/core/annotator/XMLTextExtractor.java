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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.type.PlainText;


/**
 * This annotator takes XML formatted content as input. It creates a second view
 * for storing the textual content of the XML, while keeping the offsets of the
 * text in the original XML structure. This is important to map results back to
 * the original file.
 *
 * @author hoffart
 *
 */
public class XMLTextExtractor
	extends JCasAnnotator_ImplBase
{

	public static final String VIEW_XML = "XmlView";
	public static final String VIEW_PLAIN = "PlainView";

	@Override
	public void process(JCas cas)
		throws AnalysisEngineProcessException
	{
		String xmlText;
		try {
			xmlText = cas.getView(VIEW_XML).getDocumentText();
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

		// create the new view containing the extracted structure
		JCas plainView;
		try {
			plainView = cas.createView(VIEW_PLAIN);
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

		// remove unnecessary tags, keep text content with minimal structure
		XMLPlainTextParser parser = new XMLPlainTextParser();
		parser.parse(xmlText, plainView);
	}

	/**
	 * Parses the XML content using SAX, storing the textual content in the
	 * VIEW_PLAIN while keeping the offsets in the original xml document
	 *
	 * @author hoffart
	 *
	 */
	private static class XMLPlainTextParser
	{
		private JCas cas;

		private int startPos = 0;

		/**
		 * Parses the xml, storing the textual content in the plainView
		 *
		 * @param xmlContent
		 *            XML content to parse
		 * @param plainView
		 *            CAS to store the parsed content in
		 */
		public void parse(String xmlContent, JCas plainView)
		{

			try {

				// wrap String in InputStream
				byte[] stringBytes = xmlContent.getBytes("UTF-8");

				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder;
				XPath xpath = XPathFactory.newInstance().newXPath();
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new ByteArrayInputStream(
						stringBytes));
				NodeList nodeSet = (NodeList) xpath.evaluate("/node()", doc,
						XPathConstants.NODESET);
				// extract plain text
				StringBuffer text = new StringBuffer();
				for (int i = 0; i < nodeSet.getLength(); i++) {
					text.append(nodeSet.item(i).getTextContent());
				}

				plainView.setDocumentText(text.toString());
				cas = plainView;
				//

				// make xpath annotation
				markDOMText(nodeSet, "");
			}
			catch (SAXException e) {
				System.err.println("SAX Parser error: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}
			catch (IOException e) {
				System.err.println("IO Exception: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
				System.err.println("Configuration error: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}
			catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Recursive goes through DOM Nodes and marks all found text with XPath
		 * annotation(DFS - Depth-first search) !!! count of nodes starts with 0
		 * not with 1 as in xpath
		 *
		 * @param nodeList
		 *            next nodelist to examinate
		 * @param path
		 *            current Node path
		 */
		private void markDOMText(NodeList nodeList, String path)
		{
			HashMap<String, Integer> xNames = new HashMap<String, Integer>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String name = node.getNodeName();
				if (xNames.containsKey(name)) {
					xNames.put(name, xNames.get(name) + 1);
				}
				else {
					// count of nodes starts with 0 not with 1 as in xpath
					//
					xNames.put(name, 1);
				}
				if (name.equals("#text")) {
					int endPos = startPos + node.getTextContent().length();
					PlainText xPathText = new PlainText(cas, startPos, endPos);
					startPos = endPos;
					xPathText.setOriginalXPath(path + "/text()" + "["
							+ xNames.get("#text") + "]");
					xPathText.addToIndexes();
					// map.put(path+"/text()"+"["+xNames.get("#text")+"]",
					// node.getTextContent());
				}
				else {
					if (node.getChildNodes().getLength() != 0) {
						// recursion
						markDOMText(node.getChildNodes(), path + "/" + name
								+ "[" + xNames.get(name) + "]");
					}
				}

			}
		}
	}
}
