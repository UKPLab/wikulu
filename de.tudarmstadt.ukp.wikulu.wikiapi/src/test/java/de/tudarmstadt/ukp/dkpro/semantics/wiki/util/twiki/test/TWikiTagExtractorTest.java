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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.test;

import java.util.List;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.TextFileIO;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiTagExtractor;

/**
 * This class does a litle test of <code>TWikiTagExtractor</code>.
 * @author Fabian L. Tamin
 *
 */
public class TWikiTagExtractorTest {
	/**
	 * Does a little test.
	 * @param args parameter is unused
	 */
	public static void main(String[] args) {
		TWikiTagExtractor t = new TWikiTagExtractor(TextFileIO.readInput("data/html/prototype.html", "UTF-8"));
//		t = new TWikiTagExtractor("http://twiki.org/cgi-bin/view/Codev/TWikiSecurityAlerts");
//		t = new TWikiTagExtractor("https://twiki.cern.ch/twiki/bin/view/CMS/WebHome");
		List<ExtractedLink> list = t.getTags();
		for (ExtractedLink extractedLink : list) {
			System.out.println(extractedLink.getText()+": "+extractedLink.getHref());
		}
	}
}
