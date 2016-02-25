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

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.TagExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;

/**
 * This class is the implementation tag extractor for MediaWiki syntax.
 * In MediaWiki the term tag is known as category.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiTagExtractor implements TagExtractor {
	/**
	 * List of tags
	 */
	private List<ExtractedLink> tags = new ArrayList<ExtractedLink>();
	
	/**
	 * Constructs an instance of MediaWikiTagExtractor.
	 * @param text source text in MediaWiki syntax
	 */
	public MediaWikiTagExtractor(String text) {
		Pattern p = Pattern.compile("(?:\\[\\[(Category:([^\\]\\|]+?))\\s*(?:\\|\\s*([^\\]]*))?\\]\\])",
									Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(text);
		for (; m.find(); ) {
			String href = new MediaWikiLanguage().toInternalHref(m.group(1));
			String tag = m.group(2);
			
			ExtractedLink e = new ExtractedLink(tag, href);
			e.setBegin(m.start());
			e.setEnd(m.end());
			this.tags.add(e);
		}
	}

	@Override
	public List<ExtractedLink> getTags() {
		return this.tags;
	}
}
