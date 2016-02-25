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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.BacklinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedBacklink;
import de.tudarmstadt.ukp.wikiapi.MediaWiki;

/**
 * This class is the implementation of backlink extractor for MediaWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiBacklinkExtractor implements BacklinkExtractor {

	/**
	 * limit of number of backlinks
	 */
	private int limit;
	
	/**
	 * list of extracted backlinks
	 */
	private List<ExtractedBacklink> backlinks = new ArrayList<ExtractedBacklink>();

	/**
	 * parent path of the document (without slash at the end)
	 */
	private String parent;
	/**
	 * wiki View URI
	 */
	private String wikiViewUrl;
	/**
	 * wiki API URI
	 */
	private String wikiApiUrl;

	/**
	 * Constructs an instance of MediaWikiBacklinkExtractor.
	 * @param documentUri document/page/article URI
	 * @param wikiApiUrl wiki API URI
	 * @param source source of backlinks
	 * @param limit limit of number of backlinks
	 */
	public MediaWikiBacklinkExtractor(String documentUri, String wikiViewUrl, String wikiApiUrl, String source, int limit) {
		this.limit = limit;
		this.wikiViewUrl = wikiViewUrl;
		this.wikiApiUrl = wikiApiUrl;
		try {
			URL url = new URL(documentUri);
			String path = url.getPath();
			this.parent = path.substring(0, path.lastIndexOf("/"));

			if (source.equalsIgnoreCase("wikiapi")) {
				extractUsingWikiAPI(documentUri);			
			} else {
				extractUsingWhatLinksHereURL(documentUri);			
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			this.parent = "";
		} 
		
		
	}

	/**
	 * Extracts backlinks using what links here HTML page.
	 * @param documentUri URI or URL of the document
	 */
	private void extractUsingWhatLinksHereURL(String documentUri) {
		String whatLinksHereURL = wikiApiUrl.substring(0, wikiApiUrl.lastIndexOf("/")+1)+
								  "index.php?title=Special:WhatLinksHere"+
								  documentUri.substring(documentUri.lastIndexOf("/"))+
								  "&limit="+this.limit;
		Parser parser;
		try {
			parser = new Parser(whatLinksHereURL);
			NodeList nodeList = parser.parse(new AndFilter(new AndFilter(
					new HasParentFilter(new HasAttributeFilter("id", "mw-whatlinkshere-list"), true),
					new HasParentFilter(new TagNameFilter("li"))),
					new TagNameFilter("a")
					));
			
			for (int i = 0, size = nodeList.size(); i < size; i++) {
				Node node = nodeList.elementAt(i);
				
				if (node instanceof LinkTag) {
					LinkTag linkTag = (LinkTag) node;
					String linkText = linkTag.getLinkText();
					String href = linkTag.getAttribute("href");
					
					if (linkText != null && href != null) {
						this.backlinks.add(new ExtractedBacklink(linkText, href));
					}
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts backlinks using wiki API.
	 * @param documentUri URI or URL of the document
	 */
	private void extractUsingWikiAPI(String documentUri) {
		MediaWiki m;
 		Set<String> backlinks;
		try {
			m = new MediaWiki(new URL(wikiApiUrl),
								new URL(wikiViewUrl));
			backlinks = m.getBacklinks(documentUri, this.limit);
		} catch (MalformedURLException e1) {
			backlinks = new TreeSet<String>();
		} catch (IOException e) {
			backlinks = new TreeSet<String>();
		}

		for (String pageName : backlinks) {
			try {
				this.backlinks.add(new ExtractedBacklink(pageName,
								   this.parent+"/"+URLEncoder.encode(pageName.replaceAll(" ", "_"), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<ExtractedBacklink> getBacklinks() {
		return backlinks;
	}

}
