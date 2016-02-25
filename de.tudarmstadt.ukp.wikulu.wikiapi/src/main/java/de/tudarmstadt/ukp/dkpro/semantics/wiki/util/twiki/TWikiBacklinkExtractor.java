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
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.BacklinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedBacklink;

/**
 * This class is the implementation of backlink extractor for TWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class TWikiBacklinkExtractor implements BacklinkExtractor {
	
	/**
	 * a list of extracted backlinks 
	 */
	private List<ExtractedBacklink> backlinks = new ArrayList<ExtractedBacklink>();
	
	/**
	 * Constructs an instance of TWikiBacklinkExtractor.
	 * @param documentURI document URI
	 */
	public TWikiBacklinkExtractor(String documentURI) {
		extractBacklinks(getBacklinkURL(documentURI));
	}
	
	/**
	 * Gets backlink URL.
	 * @param documentURI document URI
	 * @return backlink URL of given article URL
	 */
	public String getBacklinkURL(String documentURI) {
		return documentURI.replaceFirst("/view/", "/oops/")+"?template=backlinksweb";
	}

	@Override
	public List<ExtractedBacklink> getBacklinks() {
		return this.backlinks;
	}

	/**
	 * Extract article from backlinks search results page to the article.
	 * @param backlinkUrl backlink URL
	 */
	private void extractBacklinks(String backlinkUrl) {
		Parser parser;
		try {
			parser = new Parser(backlinkUrl);
			
			// set filter for tag node. Tag node has "patternSearchResult" as class.
			NodeList nodeList = parser.parse(new HasAttributeFilter("class", "patternSearchResult"));
			
			if (nodeList != null) {				
				// Extract backlink from each node 
				for (int i = 0; i < nodeList.size(); i++) {
					Node backlinkNode = nodeList.elementAt(i);
					// prevent runtime exception.
					// Exception occurs if the TWiki's configuration is incompatible with this extractor. 
					try {
						// HrefNode is 3rd generation of backlinkNode.
						Node hrefNode = backlinkNode.getFirstChild().getFirstChild().getFirstChild();						
						if (hrefNode instanceof LinkTag) {
							LinkTag backlinkHrefNode = (LinkTag) hrefNode;
							// the first child should be <b>, the next sibling is the alternative backlink text.
							Node backlinkTextCandidate = hrefNode.getFirstChild().getNextSibling();
							
							if (backlinkTextCandidate != null) {
								if (backlinkTextCandidate instanceof TextNode) {
									TextNode backlinkTextNode = (TextNode) backlinkTextCandidate;
									
									// add new backlink
									this.backlinks.add(new ExtractedBacklink(backlinkTextNode.getText(), backlinkHrefNode.getLink()));							
								}
							}
						}
					} catch (Exception e) {
					}
				}
			}
			
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

}
