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
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.LinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiLinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiLinkExtractor;

/**
 * This is a class for extracting link annotations from the documents.
 * @author Fabian L. Tamin
 *
 */
public class LinkAnnotator extends JCasAnnotator_ImplBase {
	
	/**
	 * whether this annotator is enabled
	 */
	private Boolean isEnabled;
	/**
	 * List of internal links.
	 */
	private List<ExtractedLink> internalLinks = new ArrayList<ExtractedLink>();
	/**
	 * List of external links.
	 */
	private List<ExtractedLink> externalLinks = new ArrayList<ExtractedLink>();
	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		this.isEnabled = (Boolean) context.getConfigParameterValue("isEnabled");
		
		if (isEnabled == null) {
			isEnabled = true;
		}
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (this.isEnabled) {
			long time1 = System.currentTimeMillis();
			
			
			try {
				JCas initialView = aJCas.getView("_InitialView");
				JCas htmlView = aJCas.getView("html");
			
				// get page metadata
				PageMetadata pm = (PageMetadata) initialView.getAnnotationIndex(PageMetadata.type).iterator().next();
	
				// get document text
				String initialViewDocument = initialView.getDocumentText();
				String htmlDocument = htmlView.getDocumentText();
				
				// extracting links
				extractLinks(initialViewDocument, htmlDocument, pm, aJCas);
				
				// annotate internal links
				for (ExtractedLink link : this.internalLinks) {
					InternalLink annotation = new InternalLink(initialView);
					annotation.setBegin(link.getBegin());
					annotation.setEnd(link.getEnd());
					annotation.setText(link.getText());
					annotation.setHref(link.getHref());
					annotation.addToIndexes();
				}
				
				// annotate extracted links
				for (ExtractedLink e : this.externalLinks) {
					ExternalLink annotation = new ExternalLink(initialView);
					annotation.setBegin(e.getBegin());
					annotation.setEnd(e.getEnd());
					annotation.setText(e.getText());
					annotation.setHref(e.getHref());
					annotation.addToIndexes();
				}
				
			} catch (CASException e1) {
				e1.printStackTrace();
			}
			
			
			long time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName());
		}
	}

	/**
	 * Extracts links from text.
	 * @param wikitext source text as wikitext
	 * @param htmlText source text as HTML
	 * @param pm page metadata annotation
	 */
	private void extractLinks(String wikitext, String htmlText, PageMetadata pm, JCas aJCas) {
		String wikiSyntax = pm.getWikiSyntax();
		if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.MEDIAWIKI) {
			LinkExtractor e = new MediaWikiLinkExtractor(wikitext);
			this.internalLinks = e.getInternalLinks();
			this.externalLinks = e.getExternalLinks();
		} else if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.TWIKI) {
			LinkExtractor e = new TWikiLinkExtractor(htmlText);
			this.internalLinks = e.getInternalLinks();
			this.externalLinks = e.getExternalLinks();
		} else {
			this.internalLinks = new ArrayList<ExtractedLink>();
			this.externalLinks = new ArrayList<ExtractedLink>();
		}
	}

}
