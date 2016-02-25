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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.HTMLToTextConverter;



/**
 * This class is a helper class for generating plain text for the input of keyphrase extractor.
 * @author Fabian L. Tamin
 *
 */
public class PlainTextForKeyphrasesExtractionExtractor {
	/**
	 * Generates plain text for the input of keyphrase extractor.
	 * @param documentURL the URL of the document to encode.
	 * @param limit after this limit, the text will be cut.
	 * @param wikiSyntax the wiki syntax
	 * @return
	 */
	public static String getText(String documentURL, int limit, String wikiSyntax) {
		String text;
		
		
		try {
			Parser p = new Parser(documentURL);
			
			NodeFilter nodeFilter;
			if (WikiSyntax.valueByAlias(wikiSyntax).equals(WikiSyntax.MEDIAWIKI)) {
				nodeFilter = new HasAttributeFilter("id", "bodyContent");
				text = HTMLToTextConverter.getPlainTextOfParagraphs(p.parse(nodeFilter).toHtml());
			} else if (WikiSyntax.valueByAlias(wikiSyntax).equals(WikiSyntax.TWIKI)) {
				nodeFilter = new HasAttributeFilter("id", "patternMainContents");
				text = HTMLToTextConverter.getPlainText(p.parse(nodeFilter).toHtml());
			} else {
				nodeFilter = new TagNameFilter("body");
				text = HTMLToTextConverter.getPlainTextOfParagraphs(p.parse(nodeFilter).toHtml());
			}
			
			// cutting the text after the limit is reached by finding the next full stop followed by empty spaced.
			if (text.length() > limit) {
				int cutterPoint = text.indexOf(". ", limit);
				if (cutterPoint > -1) {
					text = text.substring(0, cutterPoint+1); 
				}
			}
		} catch (ParserException e) {
			text = "";
			e.printStackTrace();
		}
		
		return text;
	}
}
