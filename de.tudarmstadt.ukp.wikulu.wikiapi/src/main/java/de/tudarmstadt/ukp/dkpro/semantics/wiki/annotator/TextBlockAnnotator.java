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
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.TextBlock;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.MylynWikiTextTextBlockBuilder;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiTextNoiseFilterPreTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedTextBlock;

/**
 * This is a class for extracting textblock annotations from the documents.
 * @author Fabian L. Tamin
 *
 */
public class TextBlockAnnotator extends JCasAnnotator_ImplBase {
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		long time1 = System.currentTimeMillis();
		
		// get document text
		String text = aJCas.getDocumentText();		
		
		// get page metadata
		PageMetadata pm = (PageMetadata) aJCas.getAnnotationIndex(PageMetadata.type).iterator().next();
		
		// filtering text
		if (WikiSyntax.valueByAlias(pm.getWikiSyntax()) == WikiSyntax.MEDIAWIKI) {
			text = new MediaWikiTextNoiseFilterPreTextBlockExtractor(text).getFilteredText();
		}
		
		MarkupParser parser = new MarkupParser(ServiceLocator.getInstance().getMarkupLanguage(pm.getWikiSyntax()));
		MylynWikiTextTextBlockBuilder builder = new MylynWikiTextTextBlockBuilder();
		parser.setBuilder(builder);
		parser.parse(text);
		
		for (ExtractedTextBlock e : builder.getBlocks()) {
			TextBlock annotation = new TextBlock(aJCas);
			annotation.setBegin(e.getBegin());
			annotation.setEnd(e.getEnd());
			annotation.setText(e.getText());
			annotation.setBlockType(e.getBlockType().toString());
			annotation.addToIndexes();
		}
		
		try {
			JCas plainTextView = aJCas.createView("PlainText");
			
			StringBuffer sbuf = new StringBuffer();
			
			List<ExtractedTextBlock> blocks = builder.getBlocks();
			for (ExtractedTextBlock e : blocks) {
				// this is a limiter (for performance only) {
				if (sbuf.length() > 12000) {
					break;
				}
				// }
				
				if (e.getBlockType().toLowerCase().startsWith("heading")) {
					sbuf.append("\n"+e.getText()+"\n");
				} else {
					sbuf.append(e.getText()+"\n");
				}
			}
			
			plainTextView.setDocumentText(sbuf.toString());
		} catch (CASException e1) {
			e1.printStackTrace();
		}
		
		long time2 = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
		System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+pm.getWikiSyntax());
	}

}
