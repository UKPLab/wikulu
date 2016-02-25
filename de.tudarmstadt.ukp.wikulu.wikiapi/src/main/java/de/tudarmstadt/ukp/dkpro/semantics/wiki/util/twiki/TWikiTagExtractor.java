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
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.TagExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;

/**
 * This class is the implementation of tag extractor for TWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class TWikiTagExtractor implements TagExtractor {
	/**
	 * List of tags
	 */
	private List<ExtractedLink> tags = new ArrayList<ExtractedLink>();
	
	/**
	 * Constructs an instance of TWikiTagExtractor.
	 * @param documentURI document URI
	 */
	public TWikiTagExtractor(String documentURI) {
		extractTags(documentURI);
	}

	/**
	 * Extracts tags from HTML TWiki article.
	 * @param documentURI document URI
	 */
	private void extractTags(String documentURI) {
		Parser parser;
		try {
			parser = new Parser(documentURI);
			
			// set filter for tag node. Tag node has "tagMeControl" as class
			// and has parent, which has attribute name "tagmeshow". 
			NodeList nodeList = parser.parse(new AndFilter(
					new HasAttributeFilter("class", "tagMeControl"),
					new HasParentFilter(new HasAttributeFilter("name", "tagmeshow"))));
			
			if (nodeList != null) {				
				// The last 2 nodes are not tag node. 
				for (int i = 0; i < nodeList.size() - 2; i++) {
					Node tagNode = nodeList.elementAt(i);
					
					// The first child should be hrefNode, which contains tag links and name.
					Node hrefNode = tagNode.getFirstChild();
					if (hrefNode instanceof LinkTag) {
						LinkTag linkTagNode = (LinkTag) hrefNode;
						
						// the first child should be the tag name as TextNode.
						Node tagNameNodeCandidate = hrefNode.getFirstChild();
						if (tagNameNodeCandidate != null) {
							if (tagNameNodeCandidate instanceof TextNode) {
								TextNode tagNameNode = (TextNode) tagNameNodeCandidate;
								
								// add new tag
								this.tags.add(new ExtractedLink(tagNameNode.getText(), linkTagNode.getLink()));							
							}
						}
					}
				}
			}
			
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<ExtractedLink> getTags() {
		return this.tags;
	}
}
