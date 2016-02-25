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
package de.tudarmstadt.ukp.wikiapi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiXMLUtils {
		
	public String getPlainTextFromHTML(String htmlContent) throws SAXException, IOException, ParserConfigurationException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		XMLPlainTextExtractionHandler extractionHandler = new XMLPlainTextExtractionHandler();
		
		// wrap String in InputStream
		byte[] stringBytes = htmlContent.getBytes("UTF-8");
		ByteArrayInputStream bais  = new ByteArrayInputStream(stringBytes);

		// parse with extractionHandler to get complete text all at once
		parser.parse(bais, extractionHandler);
		String plainText = extractionHandler.getText();

		return plainText;
	}
	
	/**
	 * This handler only extracts the plain text by stripping all tags.
	 * 
	 * @author hoffart
	 *
	 */
	class XMLPlainTextExtractionHandler extends DefaultHandler {
		private StringBuffer buffer;
	
		public XMLPlainTextExtractionHandler() {
			this.buffer = new StringBuffer();
		}
	
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			buffer.append(new String(ch, start, length));
		}
	
		/**
		 * @return The conmplete textual content of the XML file
		 */
		public String getText() {
			return buffer.toString();
		}
	}
	
	/**
	 * Transforms the JDom element to a String representation
	 * 
	 * @param element	JDom element to transform
	 * @return	String representation of the passed element
	 * @throws IOException
	 */
	public String elementAsString(Element element) throws IOException
	{
		XMLOutputter outputter = new XMLOutputter();
		
		StringWriter writer = new StringWriter();
		
		outputter.output(element, writer);
		
		return writer.getBuffer().toString();
	}	
}
