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
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.LinkSetElementCapsule;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.MediaWikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.TWikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.WikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;

/**
 * A class for testing <code>MediaWikiController</code> and <code>TWikiController</code>.
 * @author Fabian L. Tamin
 *
 */
public class ControllerTest {
	
	/**
	 * Does the entire test.
	 * @param args
	 */
	public static void main(String[] args) {
		mediaWiki();
		tWiki();
	}

	/**
	 * Does the test for TWiki.
	 */
	public static void tWiki() {
		WikiController w = new TWikiController("http://www.cs.wisc.edu/twiki/bin/view/TWiki/TWikiSite",
											   "http://mrburns.tk.informatik.tu-darmstadt.de/twiki/bin/view/",
				                               "http://mrburns.tk.informatik.tu-darmstadt.de/twiki/bin/rest/WikuluPlugin/",
				                               true, true, true, true, true);
		printResult(w);
	}
	
	/**
	 * Does the test for MediaWiki.
	 */
	public static void mediaWiki() {
		// to test redirection: test Cute.
		WikiController w = new MediaWikiController("http://en.wikipedia.org/wiki/Enzyme",
												   "http://en.wikipedia.org/wiki/",
				                                   "http://en.wikipedia.org/w/api.php",
				                                   true, true, true, true, true);
		printResult(w);
	}

	/**
	 * Print the result in console.
	 * @param w <code>WikiController</code> object
	 */
	private static void printResult(WikiController w) {
		List<Heading> lhe = w.getHeadings();
		for (Heading heading : lhe) {
			System.out.println("[HE"+heading.getLevel()+"]:"+heading.getText());
		}
		
		List<Image> lim = w.getImages();
		for (Image image : lim) {
			System.out.println("[IM]:"+image.getDescription()+" :: "+image.getHref());
		}
		
		Set<LinkSetElementCapsule<Backlink>> sbl = w.getBacklinks();
		for (LinkSetElementCapsule<Backlink> linkSetElementCapsule : sbl) {
			Backlink bl = linkSetElementCapsule.getLink();
			System.out.println("[BL]:"+bl.getText()+" :: "+bl.getHref());
		}
		
		Set<LinkSetElementCapsule<InternalLink>> sil = w.getInternalLinks();
		for (LinkSetElementCapsule<InternalLink> linkSetElementCapsule : sil) {
			InternalLink il = linkSetElementCapsule.getLink();
			System.out.println("[il]:"+il.getText()+" :: "+il.getHref());
		}
		
		Set<LinkSetElementCapsule<ExternalLink>> sel = w.getExternalLinks();
		for (LinkSetElementCapsule<ExternalLink> linkSetElementCapsule : sel) {
			ExternalLink el = linkSetElementCapsule.getLink();
			System.out.println("[EL]:"+el.getText()+" :: "+el.getHref());
		}
		
		Set<LinkSetElementCapsule<Tag>> sta = w.getTags();
		for (LinkSetElementCapsule<Tag> linkSetElementCapsule : sta) {
			Tag ta = linkSetElementCapsule.getLink();
			System.out.println("[TA]:"+ta.getText()+" :: "+ta.getHref());
		}
		
		System.out.println();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
	}
}
