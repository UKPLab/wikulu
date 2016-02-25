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
 * This class filters text from undesired characters. This filter is used in the beginning before annotating process.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiTextNoiseFilter {
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
	public MediaWikiTextNoiseFilter(String originalText) {
		this.originalText = originalText;
		if (originalText == null){
			this.filteredText = "";
		} else {
			filter();
		}
	}

	/**
	 * Filters text, replaces and removes some tags.
	 */
	private void filter() {
		String filteredText = this.originalText;
		
		// replacing <br /> to line break
		String lineBreakPattern = "<br\\s*/>";
		filteredText = filteredText.replaceAll(lineBreakPattern, "\n");

		// removing template pattern
		filteredText = new TemplatePatternRemover(filteredText).getText();

		// removing tag <ref>
		String refPattern = "(:?<ref(:?\\s+[^\\<]*)?>[^\\<]*</ref>)|(:?<ref(:?\\s+[^\\<]*/\\s*)?>)";
		filteredText = filteredText.replaceAll(refPattern, "");
		
		// removing tag <noinclude>
		String niPattern = "(:?<noinclude(:?\\s+[^\\<]*)?>[^\\<]*</noinclude>)|(:?<noinclude(:?\\s+[^\\<]*/\\s*)?>)";
		filteredText = filteredText.replaceAll(niPattern, "");

		this.filteredText = filteredText;
	}
	
	/**
	 * Gets filtered text.
	 * @return filtered text
	 */
	public String getFilteredText() {
		return filteredText;
	}
	
	/**
	 * A nested class to handle template in MediaWiki wikitext.
	 * @author Fabian L. Tamin
	 *
	 */
	private class TemplatePatternRemover {
		private String text;

		/**
		 * Constructs an instance of this class.
		 * @param text text to work on
		 */
		public TemplatePatternRemover(String text) {
			this.text = text;
			run();
		}

		/**
		 * Filters the template in the text 
		 */
		private void run() {
			int openerPosition = 0;
			int closerPosition = -1;
			
			String text = this.text;
			
			String opener = "{{";
			String closer = "}}";
			
			openerPosition = text.indexOf(opener, openerPosition);
			
			for (openerPosition = text.indexOf(opener, openerPosition);
				 openerPosition > -1;
				 openerPosition = text.indexOf(opener, openerPosition)) {
				closerPosition = text.indexOf(closer, openerPosition + opener.length()-1);
				
				for (int nestedOpenerPosition = text.indexOf(opener, openerPosition + opener.length()-1);
					 nestedOpenerPosition > -1 && nestedOpenerPosition < closerPosition;
					 nestedOpenerPosition = text.indexOf(opener, nestedOpenerPosition + opener.length()-1)) {
					closerPosition = text.indexOf(closer, closerPosition + closer.length()-1);
				}
				
				if (closerPosition > -1) {
					text = text.substring(0, openerPosition)+text.substring(closerPosition+closer.length());
				}
			}
			
			this.text = text;
		}
		
		/**
		 * Gets the filtered text.
		 * @return the filtered text
		 */
		public String getText() {
			return text;
		}
	}
}
