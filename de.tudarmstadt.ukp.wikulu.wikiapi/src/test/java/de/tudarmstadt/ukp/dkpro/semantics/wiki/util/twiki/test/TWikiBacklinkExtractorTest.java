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

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedBacklink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiBacklinkExtractor;

/**
 * This class does a little test for <code>TWikiBacklinkExtractorTest</code>
 * @author Fabian L. Tamin
 *
 */
public class TWikiBacklinkExtractorTest {
	/**
	 * Does a little test.
	 * @param args parameter is unused.
	 */
	public static void main(String[] args) {
	//	TWikiBacklinkExtractor t = new TWikiBacklinkExtractor(TextFileIO.readInput("data/html/prototype_backlink.html", "UTF-8"));
	//	TWikiBacklinkExtractor t = new TWikiBacklinkExtractor("http://twiki.org/cgi-bin/view/Codev/TWikiSecurityAlerts");
	//	TWikiBacklinkExtractor t = new TWikiBacklinkExtractor("https://twiki.cern.ch/twiki/bin/view/CMS/WebHome");
	//	TWikiBacklinkExtractor t = new TWikiBacklinkExtractor("http://wiki.nix.hu/cgi-bin/twiki/view/DebianOnWl500gx/WebPreferences");
		TWikiBacklinkExtractor t = new TWikiBacklinkExtractor("https://www.seegrid.csiro.au/twiki/bin/view/CGIModel/WebHome");
		
		System.out.println(t.getBacklinkURL("https://maggie.tk.informatik.tu-darmstadt.de/wiki/bin/view/Teaching/FabianTaminPrototype"));
		System.out.println("https://maggie.tk.informatik.tu-darmstadt.de/wiki/bin/oops/Teaching/FabianTaminPrototype?template=backlinksweb");
		System.out.println();
		
		List<ExtractedBacklink> list = t.getBacklinks();
		for (ExtractedBacklink extractedLink : list) {
			System.out.println(extractedLink.getText()+": "+extractedLink.getHref());
		}
	}
}
