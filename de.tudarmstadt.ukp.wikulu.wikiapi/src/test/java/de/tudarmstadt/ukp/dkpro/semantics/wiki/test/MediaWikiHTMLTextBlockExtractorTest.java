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

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.TextFileIO;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiHTMLTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHTMLTextBlock;

/**
 * This is a class which includes a small test for <code>MediaWikiHTMLTextBlockExtractor</code>.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiHTMLTextBlockExtractorTest {
	/**
	 * A small test for <code>MediaWikiHTMLTextBlockExtractor</code>.
	 * @param args
	 */
	public static void main(String[] args) {
		String docu = TextFileIO.readInput("data/html/Saint_Seiya.html", "UTF-8");
		MediaWikiHTMLTextBlockExtractor m = new MediaWikiHTMLTextBlockExtractor(docu);
		
		List<ExtractedHTMLTextBlock> b = m.getHTMLTextBlocks();
		for (ExtractedHTMLTextBlock extractedHTMLTextBlock : b) {
			System.out.println(extractedHTMLTextBlock.getBegin()+", "+extractedHTMLTextBlock.getEnd());
			System.out.println(extractedHTMLTextBlock);
		}
	}
}
