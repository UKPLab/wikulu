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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.HTMLTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.HTMLBlockType;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.HTMLToTextConverter;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHTMLTextBlock;

/**
 * This class is the implementation of HTML text block extractor for TWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class TWikiHTMLTextBlockExtractor implements HTMLTextBlockExtractor {

	/**
	 * list of extracted HTML text blocks
	 */
	private List<ExtractedHTMLTextBlock> extractedHTMLTextBlocks = new ArrayList<ExtractedHTMLTextBlock>();
	
	/**
	 * relative position of the relevant DOM node element to the HTML document's begin.
	 */
	private int relativePosition = 0;
	
	/**
	 * temporary variable which contains HTML text
	 */
	private StringBuffer nodeBuffer = new StringBuffer();
	/**
	 * temporary start point of the block
	 */
	private int startPoint = -1;
	/**
	 * temporary end point of the block
	 */
	private int endPoint= -1;
	/**
	 * temporary flag to show whether nodeBuffer has been read 
	 */
	private boolean hasBeenRead = true;
	
	/**
	 * Constructs an instance of this class. 
	 * @param document HTML document
	 */
	public TWikiHTMLTextBlockExtractor(String document) {
		try {
			Parser parser = new Parser(document);
			
			NodeList nodeList = parser.parse(new HasAttributeFilter("class", "patternTopic"));
			
			if (nodeList != null && nodeList.size() > 0) {
				Node firstNode = nodeList.elementAt(0);
				parser = new Parser(firstNode.toHtml());
				nodeList = firstNode.getChildren();
				this.relativePosition = firstNode.getStartPosition();
				
				if (nodeList != null) {
					for (int i = 0, size = nodeList.size(); i < size; i++) {				
						Node node = nodeList.elementAt(i);
//						System.out.println(i+":"+node.getClass()+":::"+node.toHtml()+"::chil::"+node.getChildren());
						
						if (node instanceof TextNode) {
							TextNode textNode = (TextNode) node;
							
							putInBuffer(node, textNode.getText(), false);
						} else if (node instanceof TagNode) {
							TagNode tagNode = (TagNode) node;
							
							if (tagNode.getTagName().equalsIgnoreCase("p") ||
								tagNode.getTagName().equalsIgnoreCase("ul") ||
								tagNode.getTagName().equalsIgnoreCase("table")) {
								
								addAndResetNodeBuffer();
								
								if (tagNode.getTagName().equalsIgnoreCase("p")) {
									String text = HTMLToTextConverter.getPlainText(tagNode.toHtml());
									
									if (text.trim().length() > 0) {
										addHTMLTextBlock(HTMLBlockType.PARAGRAPH.toString(), node.toHtml(), node);
									}
								} else if (tagNode.getTagName().equalsIgnoreCase("ul")) {
									addHTMLTextBlock(HTMLBlockType.LIST.toString(), node.toHtml(), node);
								}
							} else if (!tagNode.getTagName().equalsIgnoreCase("table") && !tagNode.getTagName().matches("(H\\d)")) {
								String text = HTMLToTextConverter.getPlainText(node.toHtml());
								
								putInBuffer(tagNode, text, true);
							}
						}
					}
					
					addAndResetNodeBuffer();
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ExtractedHTMLTextBlock> getHTMLTextBlocks() {
		return this.extractedHTMLTextBlocks;
	}

	/**
	 * Puts new information in the buffer. The HTML in the node is only added if the condition is fulfilled.
	 * @param node the node to investigate
	 * @param text the node as plain text
	 * @param trim whether the text should be trimmed
	 */
	private void putInBuffer(Node node, String text, boolean trim) {
		boolean fulfiled;
		if (trim) {
			fulfiled = text.trim().length() > 0;
		} else {
			fulfiled = text.length() > 0;
		}
		
		if (fulfiled) {
			if (this.hasBeenRead) {
				this.startPoint = node.getStartPosition();
				this.hasBeenRead = false;
			}
			
			this.endPoint = node.getEndPosition();
			this.nodeBuffer.append(node.toHtml());
		}
	}

	/**
	 * Adds the content in the nodeBuffer to the list if the nodeBuffer has not been read,
	 * and it reset the state of nodeBuffer and hasBeenRead flag. 
	 */
	private void addAndResetNodeBuffer() {
		if (!hasBeenRead) {
			addHTMLTextBlock(HTMLBlockType.PARAGRAPH.toString(),
							 "<p>"+nodeBuffer.toString()+"</p>",
							 this.startPoint+this.relativePosition,
							 this.endPoint+this.relativePosition);
		}
		
		this.nodeBuffer = new StringBuffer();
		this.hasBeenRead = true;
	}

	/**
	 * Adds HTML Block to the list.
	 * @param type the type of HTML block
	 * @param text the text to add
	 * @param startPoint the start point of the text in the HTML document 
	 * @param endPoint the end point of the text in the HTML document
	 */
	private void addHTMLTextBlock(String type, String text, int startPoint, int endPoint) {
		ExtractedHTMLTextBlock extractedHTMLTextBlock = new ExtractedHTMLTextBlock();
		extractedHTMLTextBlock.setText(text);
		extractedHTMLTextBlock.setBegin(startPoint);
		extractedHTMLTextBlock.setEnd(endPoint);
		extractedHTMLTextBlock.setBlockType(type);
		this.extractedHTMLTextBlocks.add(extractedHTMLTextBlock);
	}
	
	/**
	 * Adds HTML block to the list.
	 * @param type the type of HTML block
	 * @param text the text to add
	 * @param node the reference node to the text
	 */
	private void addHTMLTextBlock(String type, String text, Node node) {
		addHTMLTextBlock(type,
						 text,
						 node.getStartPosition()+this.relativePosition,
						 node.getEndPosition()+this.relativePosition);
	}
}
