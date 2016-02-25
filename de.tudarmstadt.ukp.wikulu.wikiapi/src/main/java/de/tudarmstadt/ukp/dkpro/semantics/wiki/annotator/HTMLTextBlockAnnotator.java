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
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.HTMLTextBlock;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.HTMLTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiHTMLTextBlockExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHTMLTextBlock;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiHTMLTextBlockExtractor;

/**
 * This is a class for extracting HTML blocks from the documents.
 * @author Fabian L. Tamin
 *
 */
public class HTMLTextBlockAnnotator extends JCasAnnotator_ImplBase {

	/**
	 * List of extracted HTML text blocks.
	 */
	private List<ExtractedHTMLTextBlock> htmlTextBlocks;
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		long time1 = System.currentTimeMillis();
		
		// get page metadata
		PageMetadata pm;
		try {
			JCas initialView = aJCas.getView("_InitialView");
			pm = (PageMetadata) initialView.getAnnotationIndex(PageMetadata.type).iterator().next();
			String syntax = pm.getWikiSyntax();
			
			JCas htmlView = aJCas.getView("html");
			
			// extract HTML text blocks
			extractHTMLTextBlocks(syntax, htmlView);
			
			// Create HTMLText annotations
			for (ExtractedHTMLTextBlock e : this.htmlTextBlocks) {
				HTMLTextBlock annotation = new HTMLTextBlock(initialView);
				
				annotation.setText(e.getText());
				annotation.setBlockType(e.getBlockType());
				annotation.setBegin(e.getBegin());
				annotation.setEnd(e.getEnd());
				annotation.addToIndexes();
			}
			
			long time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+pm.getWikiSyntax());
		} catch (CASRuntimeException e1) {
			e1.printStackTrace();
		} catch (CASException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Extracts HTML text blocks from HTML view respectively to the wiki syntax. 
	 * @param syntax 
	 * @param htmlView
	 */
	private void extractHTMLTextBlocks(String syntax, JCas htmlView) {
		String htmlText = htmlView.getDocumentText();
		if (WikiSyntax.valueByAlias(syntax) == WikiSyntax.MEDIAWIKI) {
			HTMLTextBlockExtractor e = new MediaWikiHTMLTextBlockExtractor(htmlText);
			this.htmlTextBlocks = e.getHTMLTextBlocks();
		} else if (WikiSyntax.valueByAlias(syntax) == WikiSyntax.TWIKI) {
			HTMLTextBlockExtractor e = new TWikiHTMLTextBlockExtractor(htmlText);
			this.htmlTextBlocks = e.getHTMLTextBlocks();
		} else {
			this.htmlTextBlocks = new ArrayList<ExtractedHTMLTextBlock>();
		}
	}
}
