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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.ImageExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiImageExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedImage;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiImageExtractor;

/**
 * This is a class for extracting link annotations from the documents.
 * @author Fabian L. Tamin
 *
 */
public class ImageAnnotator extends JCasAnnotator_ImplBase {
	
	/**
	 * whether this annotator is enabled
	 */
	private Boolean isEnabled;

	/**
	 * List of extracted images.
	 */
	private List<ExtractedImage> images;
	
	@Override
	public void initialize(UimaContext context)
	throws ResourceInitializationException {
		super.initialize(context);
		
		this.isEnabled = (Boolean) context.getConfigParameterValue("isEnabled");
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (this.isEnabled) {
			long time1 = System.currentTimeMillis();
			
			// get page metadata
			PageMetadata pm;
			try {
				JCas initialView = aJCas.getView("_InitialView");
				pm = (PageMetadata) initialView.getAnnotationIndex(PageMetadata.type).iterator().next();
				
				String syntax = pm.getWikiSyntax();
				JCas htmlView = aJCas.getView("html");
				
				// extract images 
				extractImages(syntax, htmlView);
				
				// set image annotations
				for (ExtractedImage e : this.images) {
					Image annotation = new Image(initialView);
					
					annotation.setDescription(e.getDescription());
					annotation.setHref(e.getHref());
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
	}

	/**
	 * Extracts images from HTML View respectively to the wiki syntax.
	 * @param syntax wiki syntax
	 * @param htmlView JCas of HTML view
	 */
	private void extractImages(String syntax, JCas htmlView) {
		String htmlText = htmlView.getDocumentText();
		if (WikiSyntax.valueByAlias(syntax) == WikiSyntax.MEDIAWIKI) {
			ImageExtractor e = new MediaWikiImageExtractor(htmlText);
			this.images = e.getImages();
		} else if (WikiSyntax.valueByAlias(syntax) == WikiSyntax.TWIKI) {
			ImageExtractor e = new TWikiImageExtractor(htmlText);
			this.images = e.getImages();
		} else {
			this.images = new ArrayList<ExtractedImage>();
		}
	}

}
