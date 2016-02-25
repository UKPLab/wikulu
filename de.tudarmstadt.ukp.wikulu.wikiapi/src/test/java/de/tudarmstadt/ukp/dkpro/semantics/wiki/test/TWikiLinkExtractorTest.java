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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.test;

import java.util.List;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiLinkExtractorUsingWikitext;

/**
 * Small test for TWikiLinkExtractor
 * @author Fabian L. Tamin
 *
 */
public class TWikiLinkExtractorTest {
	/**
	 * Tests application.
	 * @param args parameter is unused
	 */
	public static void main(String[] args) {
		String text = "IniApa kamu aAda <noautolink>DiMana ABG aDuDa</noautolink> H2O PowerPoint !PowerActi\nBulBul";
		text += "\n[[bolbi]] [[bolbia]] [[http://amc.com kata]] [[roli][kataila]]";
		text += "\n[[bolbi bulbo]] [[http://amc.com kata][lolapi]] https://www.aa.ca, a";
		System.out.println(text);
		
		TWikiLinkExtractorUsingWikitext t = new TWikiLinkExtractorUsingWikitext(text);
		
		List<ExtractedLink> ex = t.getExternalLinks();
		List<ExtractedLink> in = t.getInternalLinks();
		
		for (ExtractedLink e : in) {
			System.out.println("[intern][" + e.getHref() + "][" + e.getText() + "]");
		}
		
		for (ExtractedLink e : ex) {
			System.out.println("[extern][" + e.getHref() + "][" + e.getText() + "]");
		}
	}
}
