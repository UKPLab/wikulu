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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.HTMLTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.HTMLBlockType;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHTMLTextBlock;

/**
 * This class is the implementation of HTML text block extractor for MediaWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiHTMLTextBlockExtractor implements HTMLTextBlockExtractor {

	/**
	 * list of extracted HTML text blocks
	 */
	private List<ExtractedHTMLTextBlock> extractedHTMLTextBlocks = new ArrayList<ExtractedHTMLTextBlock>();

	/**
	 * relative position of the relevant DOM node element to the HTML document's begin.
	 */
	private int relativePosition = 0;
	
	/**
	 * Constructs an instance of this class. 
	 * @param document HTML document
	 */
	public MediaWikiHTMLTextBlockExtractor(String document) {
		try {
			Parser parser = new Parser(document);
			int endLimit = 12000;
			int htmlSize = 0;
			
			NodeList nodeList = parser.parse(new HasAttributeFilter("id", "bodyContent"));
			if (nodeList != null && nodeList.size() > 0) {
				Node firstElement = nodeList.elementAt(0);
				parser = new Parser(firstElement.toHtml());
				nodeList = firstElement.getChildren();
				this.relativePosition = firstElement.getStartPosition();
				
				if (nodeList != null) {
					for (int i = 0, size = nodeList.size(); i < size; i++) {					
						Node node = nodeList.elementAt(i);
						if (node instanceof TagNode) {
							TagNode tagNode = (TagNode) node;
							if (tagNode.getTagName().equalsIgnoreCase("p")) {
								if (htmlSize <= endLimit) {
									boolean isSmallText = false;
									// only add if it contains text {
									// filtering using Lexer
									NodeList childrenNodes = node.getChildren();
									String text = childrenNodes.toHtml();
									if (childrenNodes.size() == 1) {
										Node child = childrenNodes.elementAt(0);
										if (child instanceof Span) {
											Span spanNode = (Span) child;
											String style = spanNode.getAttribute("style");
											if (style != null && style.contains("font-size: small")) {
												isSmallText = true;
											}
										}
									}
									
									if (isSmallText) {
										continue;
									}
									
									boolean containsText = false;
									Lexer lexer = new Lexer(text);
									for (Node lexerNode = lexer.nextNode(); lexerNode != null; lexerNode = lexer.nextNode()) {
										if (lexerNode instanceof TextNode) {
											containsText = true;
											break;
										}
									}
									
									if (containsText) {
										htmlSize = addHTMLTextBlock(htmlSize, node, HTMLBlockType.PARAGRAPH.toString());
									}
								} else {
									htmlSize = addHTMLTextBlock(htmlSize, node, HTMLBlockType.PARAGRAPH.toString());
								}
							} else if (tagNode.getTagName().equalsIgnoreCase("ul")) {
								htmlSize = addHTMLTextBlock(htmlSize, node, HTMLBlockType.LIST.toString());
							}
						}
					}
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds HTML Block to the list.
	 * @param htmlSize the size of the added HTML
	 * @param node the node to add
	 * @param type the type of the block
	 * @return the new size of the added HTML after adding.
	 */
	private int addHTMLTextBlock(int htmlSize, Node node, String type) {
		ExtractedHTMLTextBlock extractedHTMLTextBlock = new ExtractedHTMLTextBlock();
		String htmlText = node.toHtml();
		extractedHTMLTextBlock.setBegin(this.relativePosition + node.getStartPosition());
		extractedHTMLTextBlock.setEnd(this.relativePosition + node.getEndPosition());
		extractedHTMLTextBlock.setText(htmlText);
		extractedHTMLTextBlock.setBlockType(type);
		this.extractedHTMLTextBlocks.add(extractedHTMLTextBlock);
		htmlSize += htmlText.length();
		return htmlSize;
	}

	@Override
	public List<ExtractedHTMLTextBlock> getHTMLTextBlocks() {
		return this.extractedHTMLTextBlocks;
	}
}
