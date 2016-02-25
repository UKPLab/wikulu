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
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.LinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;

/**
 * This class is the implementation of tag extractor for TWiki syntax. This implementation uses HTML as its source.
 * @author Fabian L. Tamin
 *
 */
public class TWikiLinkExtractor implements LinkExtractor {

	/**
	 * List of extracted external links
	 */
	private List<ExtractedLink> externalLinks = new ArrayList<ExtractedLink>();
	/**
	 * List of extracted internal links
	 */
	private List<ExtractedLink> internalLinks = new ArrayList<ExtractedLink>();
	
	/**
	 * Constructs an instance of TWikiLinkExtractor
	 * @param text source text in TWiki syntax
	 */
	public TWikiLinkExtractor(String text) {
		Parser parser;
		try {
			parser = new Parser(text);
			NodeList nodeList =  parser.parse(new AndFilter(
					new HasParentFilter(new HasAttributeFilter("class", "patternTopic"), true),
					new TagNameFilter("A")));
			
			for (int i = 0, size = nodeList.size(); i < size; i++) {
				Node node = nodeList.elementAt(i);
				if (node instanceof LinkTag) {
					LinkTag link = (LinkTag) node;
					String linkText = link.getLinkText();
					String href = link.getAttribute("href");
					
					if (href != null) {
						if (href.startsWith("/")) {
							addInternalLink(linkText, href, node.getStartPosition(), node.getEndPosition());
						} else {
							addExternalLink(linkText, href, node.getStartPosition(), node.getEndPosition());
						}
					}
				}
			}
			
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Adds external link to the list.
	 * @param text display text of the link
	 * @param href link's HREF
	 * @param begin begin index in Wiki text source
	 * @param end end index in Wiki text source
	 */
	private void addExternalLink(String altText, String href, int begin, int end) {
		ExtractedLink e = new ExtractedLink(altText, href);
		e.setBegin(begin);
		e.setEnd(end);
		this.externalLinks.add(e);
	}

	/**
	 * Adds internal link to the list.
	 * @param altText display text of the link
	 * @param href link's HREF
	 * @param begin begin index in Wiki text source
	 * @param end end index in Wiki text source
	 */
	private void addInternalLink(String text, String href,
			int begin, int end) {
		ExtractedLink e = new ExtractedLink(text, href);
		e.setBegin(begin);
		e.setEnd(end);
		this.internalLinks.add(e);
	}
	
	@Override
	public List<ExtractedLink> getExternalLinks() {
		return this.externalLinks;
	}

	@Override
	public List<ExtractedLink> getInternalLinks() {	
		return this.internalLinks;
	}
}
