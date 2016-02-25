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

/**
 * This class filters text from undesired characters. This filter is used before processing text block annotator.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiTextNoiseFilterPreTextBlockExtractor {
	/**
	 * Text before filtering.
	 */
	private String originalText;
	/**
	 * Text after filtering.
	 */
	private String filteredText;

	/**
	 * Constructs an instance of TextNoiseFilter.
	 * @param originalText text to filter
	 */
	public MediaWikiTextNoiseFilterPreTextBlockExtractor(String originalText) {
		this.originalText = originalText;
		filter();
	}

	/**
	 * Filters text, replaces and removes some tags.
	 */
	private void filter() {
		String filteredText = this.originalText;
	
		String excludedLinkPattern = "\\[\\[[^\\s]+\\:[^\\n]+\\]\\]";
		filteredText = filteredText.replaceAll(excludedLinkPattern, "\n");

		this.filteredText = filteredText;
	}
	
	/**
	 * Gets filtered text.
	 * @return filtered text
	 */
	public String getFilteredText() {
		return filteredText;
	}
}
