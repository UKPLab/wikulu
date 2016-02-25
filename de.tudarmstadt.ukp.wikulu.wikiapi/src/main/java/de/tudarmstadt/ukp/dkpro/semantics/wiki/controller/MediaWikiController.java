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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiTextNoiseFilter;
import de.tudarmstadt.ukp.wikiapi.MediaWiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;

/**
 * Implementation controller class for MediaWiki configuration. 
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiController extends WikiAbstractionController {

	/**
	 * Constructs an instance of MediaWikiController.
	 * @param documentUri document URI
	 * @param wikiViewUrl Wiki View URL
	 * @param wikiApiUrl Wiki API URL
	 * @param enableHeadings whether headings is enable
	 * @param enableInternalLinks whether internal links is enable
	 * @param enableExternalLinks whether external links is enable
	 * @param enableBacklinks whether backlinks is enable
	 * @param enableImages whether images is enable
	 */
	public MediaWikiController(String documentUri, String wikiViewUrl, String wikiApiUrl,
			boolean enableHeadings, boolean enableInternalLinks,
			boolean enableExternalLinks, boolean enableBacklinks, boolean enableImages) {
		super(documentUri, wikiViewUrl, wikiApiUrl,
			  enableHeadings, enableInternalLinks,
			  enableExternalLinks, enableBacklinks, enableImages);
	}

	@Override
	protected void setWikiSyntax() {
		this.wikiSyntax = "MediaWiki";
	}
	
	@Override
	protected void setWiki() {
		try {
			this.wiki = new MediaWiki(new URL(this.wikiApiUrl),
									  new URL(this.wikiViewUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setMainAnalysisEngine() {
		super.setMainAnalysisEngine();
	}
	
	@Override
	protected void setFilteredWikiText() {
		try {
			Page page = wiki.getPageForURL(new URL(documentUri));
			this.filteredWikiText = new MediaWikiTextNoiseFilter(page.getWikiSyntaxContent()).getFilteredText();
		} catch (IOException e) {
			this.filteredWikiText = "";
			e.printStackTrace();
		}
	}
}
