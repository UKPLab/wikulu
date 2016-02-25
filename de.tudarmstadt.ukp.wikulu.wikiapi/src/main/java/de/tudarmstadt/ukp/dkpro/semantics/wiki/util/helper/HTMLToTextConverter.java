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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * This class is a helper class for converting HTML to plain text
 * @author Fabian L. Tamin
 *
 */
public class HTMLToTextConverter {
	/**
	 * Gets plain text of the paragraphs in the HTML source.
	 * @param html HTML source to investigate
	 * @return plain text.
	 * @throws ParserException It is returned in HTML cannot be parsed.
	 */
	public static String getPlainTextOfParagraphs(String html) throws ParserException {
		NodeList nodes;
		Parser parser = new Parser(html);
		nodes = parser.parse(new TagNameFilter("p"));

		StringBuffer text = new StringBuffer();
		for (int size = nodes.size(), i=0; i < size; i++) {
			Node node = nodes.elementAt(i);
			Lexer lexer = new Lexer(node.toHtml());
			traverseWithLexer(text, lexer);
			
			text.append("\n");
		}
		
		return text.toString();
	}

	/**
	 * Gets plain text from a HTML source
	 * @param html HTML text
	 * @return plain text
	 * @throws ParserException It is returned in HTML cannot be parsed.
	 */
	public static String getPlainText(String html) throws ParserException {
		StringBuffer text = new StringBuffer();
		Lexer lexer = new Parser(html).getLexer();
		traverseWithLexer(text, lexer);
		
		return text.toString();
	}

	/**
	 * Traverses each node in HTML to extract the text.
	 * @param text container of plain text (as a result) where the result should be put
	 * @param lexer lexer with HTML text insides
	 * @throws ParserException It is returned if it cannot be parsed correctly.
	 */
	private static void traverseWithLexer(StringBuffer text, Lexer lexer)
			throws ParserException {
		for (Node lexerNode = lexer.nextNode();
			 lexerNode != null;
		 	 lexerNode = lexer.nextNode()) {
			if (lexerNode instanceof TextNode) {
				text.append(((TextNode) lexerNode).getText());
			}
		}
	}
}
