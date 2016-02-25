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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.LinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;

/**
 * This class is the implementation link extractor for MediaWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiLinkExtractor implements LinkExtractor {
	/**
	 * Source text in MediaWiki syntax.
	 */
	private String text;
	/**
	 * List of extracted internal links.
	 */
	private List<ExtractedLink> internalLinks = new ArrayList<ExtractedLink>();
	/**
	 * List of extracted external links.
	 */
	private List<ExtractedLink> externalLinks = new ArrayList<ExtractedLink>();

	/**
	 * Constructs an instance of MediaWikiLinkExtractor.
	 * @param text source text in MediaWiki syntax
	 */
	public MediaWikiLinkExtractor(String text) {
		this.text = text;

		extractInternalLinks();
		extractExternalLinks();
	}

	/**
	 * Extract internal links
	 */
	private void extractInternalLinks() {
		// TODO: <nowiki> syntax is not yet handled
		// TODO: Refactoring as submethods for clarity.
		if (this.text == null)
		{
			return;
		}
		
		Pattern p = Pattern
				.compile("(?:\\[\\[([^\\]\\|]+?)\\s*(?:\\|\\s*([^\\]]*))?\\]\\])"); // #check
																					// consistency
																					// for
																					// every
																					// version#
		
		Matcher m = p.matcher(this.text);

		for (int start = 0; m.find(start);) {
			String pageName = m.group(1);
			String text = m.group(2);

			// handle if it is external link
			String hrefPart = m.group(1);
			if (hrefPart.toLowerCase().startsWith("image:")) {
				// image syntax (not internal link) => exclude this
				
				// internal link in image information will also be extracted
				if (m.start() + 1 < m.end()) {
					start = m.start() + 1;
				} else {
					start = m.end();
				}
				continue;
			} else if (hrefPart.toLowerCase().contains("://")
					|| hrefPart.toLowerCase().startsWith("category:")
					|| hrefPart.matches(":?[^:\\s]+:[^\\s].*")) {
				// other than namespace other than "main" namespace will be excluded
				start = m.end();
				continue;
			}
			
			String href = new MediaWikiLanguage().toInternalHref(hrefPart);

			// category references start with ':' but are not referenced that
			// way in the text
			if (pageName.startsWith(":")) {
				pageName = pageName.substring(1);
			}

			if (text == null || text.trim().length() == 0) {
				text = pageName;
			}
			
			int anchorStart = pageName.indexOf("#");
			// renaming to better readable title
			if (anchorStart > 0) {
				pageName = pageName.substring(0, anchorStart)+": "+pageName.substring(anchorStart+1);
			}
			
			// filter anchor link
			if (anchorStart != 0) {
				ExtractedLink link = new ExtractedLink(text, href);
				link.setBegin(m.start());
				link.setEnd(m.end());
				this.internalLinks.add(link);
			}
			
			start = m.end();
		}
	}

	/**
	 * Extract external links
	 */
	private void extractExternalLinks() {
		if (this.text == null)
		{
			return;
		}
		
		String prefix = "(?:(?:http)|(?:https)|(?:ftp))://";

		Pattern p = Pattern.compile("(?:(\\[(" + prefix
				+ "[^\\[\\]\\|\\s]+)(?:(?:\\s)([^\\]]*))?\\s*\\])|(" + prefix
				+ "[^\\s\\|]+))");
		Matcher m = p.matcher(this.text);

		for (; m.find();) {
			String href;
			String altText;

			if (m.group(1) != null) {
				// for explicit external link syntax 
				
				href = m.group(2);
				altText = m.group(3);

				if (altText == null || altText.trim().length() == 0) {
					altText = href;
				}
			} else {
				// for automatic detection case
				
				href = m.group(4);
				altText = m.group(4);
			}

			ExtractedLink link = new ExtractedLink(altText, href);
			link.setBegin(m.start());
			link.setEnd(m.end());

			this.externalLinks.add(link);
		}
	}

	@Override
	public List<ExtractedLink> getExternalLinks() {
		return externalLinks;
	}

	@Override
	public List<ExtractedLink> getInternalLinks() {
		return internalLinks;
	}
}
