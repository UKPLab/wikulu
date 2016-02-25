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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.controller;

import java.net.MalformedURLException;
import java.net.URL;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.wikiapi.TWiki;

/**
 * Implementation controller class for TWiki configuration.
 * @author Fabian L. Tamin
 *
 */
public class TWikiController extends WikiAbstractionController {
	
	/**
	 * Constructs an instance of TWikiController.
	 * @param documentUri document URI
	 * @param wikiViewUrl Wiki View URI
	 * @param wikiApiUrl Wiki API URI
	 * @param enableHeadings whether headings is enable
	 * @param enableInternalLinks whether internal links is enable
	 * @param enableExternalLinks whether external links is enable
	 * @param enableBacklinks whether backlinks is enable
	 * @param enableImages whether images is enable
	 */
	public TWikiController(String documentUri, String wikiViewUrl, String wikiApiUrl,
			boolean enableHeadings, boolean enableInternalLinks,
			boolean enableExternalLinks, boolean enableBacklinks, boolean enableImages) {
		super(documentUri, wikiViewUrl, wikiApiUrl,
			  enableHeadings, enableInternalLinks,
			  enableExternalLinks, enableBacklinks, enableImages);
	}

	@Override
	protected void setWikiSyntax() {
		this.wikiSyntax = "TWiki";
	}
	
	@Override
	protected void setWiki() {
		this.wikiSyntax = "TWiki";
		
		try {
			this.wiki = new TWiki(new URL(this.wikiApiUrl), new URL(this.wikiViewUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setFilteredWikiText() {
		try {
			String rawURL = this.documentUri+"?raw=on";
			Parser parser = new Parser(rawURL);
			NodeList nodeList = parser.parse(new TagNameFilter("textarea"));
			if (nodeList.size() > 0) {
				Node node = nodeList.elementAt(0);
				String text = node.getFirstChild().toHtml();
				this.filteredWikiText = text;
			} else {
				this.filteredWikiText = "";
			}
		} catch (ParserException e) {
			e.printStackTrace();
			this.filteredWikiText = "";
		}
	}
}
