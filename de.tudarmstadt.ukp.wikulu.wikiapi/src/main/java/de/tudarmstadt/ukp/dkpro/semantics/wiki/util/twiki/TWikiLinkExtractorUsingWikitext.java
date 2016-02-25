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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.LinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;

/**
 * This class is the implementation of tag extractor for TWiki syntax. This implementation uses wikitext as its implementation
 * @author Fabian L. Tamin
 *
 */
public class TWikiLinkExtractorUsingWikitext implements LinkExtractor {

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
	public TWikiLinkExtractorUsingWikitext(String text) {
		// TODO: Method is to big, should be extracted for better clarity.
		ArrayList<Token> noAutoLinks = getNoAutoLinkTokens(text);
		
		// group 1, 2 (content in 1st bracket), 3 (content in 2nd bracket)
		String specificLink = "(?:(?:^)|(?:[^!]))(\\[\\[([^\\n\\]]+)\\]\\[([^\\n]+)\\]\\])";
		// group 4, 5 (parameter)
		String forceLink = "(?:(?:^)|(?:[^!]))(\\[\\[([^\\n\\[]+)\\]\\])";
		// group 6
		String wikiWord = "(?:(?:^)|(?:\\s))(\\p{Upper}\\p{Alnum}*[\\p{Lower}\\p{Digit}]\\p{Alnum}*\\p{Upper}\\p{Alnum}*)";
		// group 7
		String autoExternalLink = "(?:(?:^)|(?:\\s))((?:(?:https?)|(?:ftp))://[^\\s\\n]*\\p{Alnum})";
		String pattern = "(?:"+specificLink+")|(?:"+forceLink+")|(?:"+wikiWord+")|(?:"+autoExternalLink+")";
		
		int specificLinkGroup = 1;
		int forceLinkGroup = specificLinkGroup+3;
		int wikiWordGroup = forceLinkGroup+2;
		int autoExternalLinkGroup = wikiWordGroup+1;
		
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		for (; m.find(); ) {
			String wikiText = "";
			String href = "";
			String altText = "";
			String pageName = "";
			
			Pattern pPrefix = Pattern.compile("^((https?)|(ftp))://");
			if ((wikiText = m.group(specificLinkGroup)) != null) {
				// specific link
				
				href = m.group(specificLinkGroup+1);
				altText = m.group(specificLinkGroup+2);
				int begin = m.start(specificLinkGroup);
				int end = m.end(specificLinkGroup);
				if (pPrefix.matcher(href).find()) {
					
					addExternalLink(altText, href, begin, end);
				} else {
					pageName = href;
					href = new TWikiLanguage().toInternalHref(href);
					
					addInternalLink(pageName, href, begin, end);
				}
			} else if ((wikiText = m.group(forceLinkGroup)) != null) {
				// forcelink / another external link syntax
				
				String param = m.group(forceLinkGroup+1);
				int begin = m.start(forceLinkGroup);
				int end = m.end(forceLinkGroup);
				if (pPrefix.matcher(param).find()) {
					// external link
					
					int spaceIndex = param.indexOf(" ");
					if (spaceIndex != -1) {
						href = param.substring(0, spaceIndex);
						altText = param.substring(spaceIndex+1, param.length());
					} else {
						href = param;
						altText = param;
					}
					
					addExternalLink(altText, href, begin, end); 
				} else {
					// internal link
					
					altText = param;
					pageName = param.replaceAll("\\s", "");
					href = new TWikiLanguage().toInternalHref(pageName);
					
					addInternalLink(pageName, href, begin, end);
				}
			} else if ((wikiText = m.group(wikiWordGroup)) != null) {
				// wikiword
				
				int begin = m.start(wikiWordGroup);
				int end = m.end(wikiWordGroup);
								
				boolean add = true;
				
				noAutoLinks = updateNoAutoLinks(noAutoLinks, begin);
				if (!noAutoLinks.isEmpty()) {
					Token token = noAutoLinks.get(0);
					if (token.begin > begin && token.end < end) {
						add = false;				
					}
				}
				
				if (add) {
					href = new TWikiLanguage().toInternalHref(wikiText); // FIXME: link is not correct
					//TODO: Replacing TWiki variables
					altText = wikiText;
					pageName = wikiText;
					
					addInternalLink(pageName, href, begin, end);		
				}
				
			} else if ((wikiText = m.group(autoExternalLinkGroup)) != null) {
				// autoExternalLink
				int begin = m.start(autoExternalLinkGroup);
				int end = m.end(autoExternalLinkGroup);
				
				boolean add = true;
				
				noAutoLinks = updateNoAutoLinks(noAutoLinks, begin);
				if (!noAutoLinks.isEmpty()) {
					Token token = noAutoLinks.get(0);
					if (token.begin > begin && token.end < end) {
						add = false;
					}
				}
				
				if (add) {
					href = wikiText;
					altText = wikiText;
					
					addExternalLink(altText, href, m.start(autoExternalLinkGroup), m.end(autoExternalLinkGroup));					
				}
			}
		}
	}

	/**
	 * Updates list of no autolink regions.
	 * All autolink regions, which are before begin index will be removed
	 * @param noAutoLinks the list to work on
	 * @param begin begin index (exclusive)
	 * @return updated no autolink list
	 */
	private ArrayList<Token> updateNoAutoLinks(ArrayList<Token> noAutoLinks, int begin) {
		for (boolean isTrue = true; isTrue && noAutoLinks.size() > 0; ){
			if (noAutoLinks.get(0).end < begin) {
				noAutoLinks.remove(0);
			} else {
				isTrue = false;
			}
		}
		
		return noAutoLinks;
	}

	/**
	 * Gets list of no autolink tokens.
	 * @param text text where tokens should be found
	 * @return list of no autolink tokens
	 */
	private ArrayList<Token> getNoAutoLinkTokens(String text) {
		ArrayList<Token> tokens = new ArrayList<Token>(); 
		String noAutoLink = "<noautolink>[^\\r]+</noautolink>";
		
		Pattern p = Pattern.compile(noAutoLink);
		Matcher m = p.matcher(text);
		
		for (; m.find(); ) {
			tokens.add(new Token(m.start(), m.end()));
		}
		return tokens;
	}

	/**
	 * Adds external link to the list.
	 * @param altText display text of the link
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
	 * @param text display text of the link
	 * @param href link's HREF
	 * @param pageName original page name of the article
	 * @param begin begin index in Wiki text source
	 * @param end end index in Wiki text source
	 */
	private void addInternalLink(String text, String href, int begin, int end) {
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

	/**
	 * This nested class represents annotation token capsulation.
	 * @author Fabian L. Tamin
	 *
	 */
	private static class Token {
		/**
		 * Begin index in Wiki text source.
		 */
		private int begin;
		/**
		 * End index in Wiki text source.
		 */
		private int end;
		
		/**
		 * Constructs an instance of Token
		 * @param begin begin index in Wiki text source.
		 * @param end end index in Wiki text source.
		 */
		public Token(int begin, int end) {
			this.begin = begin;
			this.end = end; 
		}
	}
}
