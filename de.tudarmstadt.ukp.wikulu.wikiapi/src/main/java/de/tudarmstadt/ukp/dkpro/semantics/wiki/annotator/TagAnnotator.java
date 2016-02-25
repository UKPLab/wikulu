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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.TagExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiTagExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiTagExtractor;

/**
 * This is a class for extracting tag annotations from the documents.
 * @author Fabian L. Tamin
 *
 */
public class TagAnnotator extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		long time1 = System.currentTimeMillis();
		
		// get document text
		String text = aJCas.getDocumentText();		
		
		// get page metadata
		PageMetadata pm = (PageMetadata) aJCas.getAnnotationIndex(PageMetadata.type).iterator().next();
		
		// extract tag
		List<ExtractedLink> tags = extractTags(text, pm);
		
		// annotate tag
		for (ExtractedLink e : tags) {
			Tag annotation = new Tag(aJCas);
			annotation.setBegin(e.getBegin());
			annotation.setEnd(e.getEnd());
			annotation.setText(e.getText());
			annotation.setHref(e.getHref());
			annotation.addToIndexes();
		}
		
		long time2 = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
		System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+pm.getWikiSyntax());
	}

	/**
	 * Extracts tags from the text by using page metadata information.
	 * @param text source text
	 * @param pm page metadata annotation
	 * @return A list of extracted tags.
	 */
	private List<ExtractedLink> extractTags(String text, PageMetadata pm) {
		List<ExtractedLink> tags;
		String wikiSyntax = pm.getWikiSyntax();
		if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.MEDIAWIKI) {
			TagExtractor e = new MediaWikiTagExtractor(text);
			tags = e.getTags();
		} else if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.TWIKI) {
			TagExtractor e = new TWikiTagExtractor(pm.getDocumentUri());
			tags = e.getTags();
		} else {
			tags = new ArrayList<ExtractedLink>();
		}
		return tags;
	}

}
