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
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.BacklinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki.MediaWikiBacklinkExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedBacklink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki.TWikiBacklinkExtractor;

/**
 * This is a class for extracting backlink annotations from documents.
 * @author Fabian L. Tamin
 *
 */
public class BacklinkAnnotator extends JCasAnnotator_ImplBase {
	
	/**
	 * whether this annotator is enabled
	 */
	private boolean isEnabled;
	/**
	 * source of backlinks
	 */
	private String source;
	/**
	 * limit of number of backlinks
	 */
	private int limit;
	
	/**
	 * List of extracted backlinks.
	 */
	private List<ExtractedBacklink> backlinks;
	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		
		this.isEnabled = (Boolean) context.getConfigParameterValue("isEnabled");
		this.source = (String) context.getConfigParameterValue("source");
		this.limit = (Integer) context.getConfigParameterValue("limit");
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (this.isEnabled) {
			long time1 = System.currentTimeMillis();
			
			// get page metadata
			PageMetadata pm = (PageMetadata) aJCas.getAnnotationIndex(PageMetadata.type).iterator().next();
			
			// Extract backlinks
			extractBacklinks(pm);
			
			// Create backlink annotations
			for (ExtractedBacklink e : this.backlinks) {
				Backlink annotation = new Backlink(aJCas);
				annotation.setText(e.getText());
				annotation.setHref(e.getHref());
				annotation.addToIndexes();
			}
			
			long time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+pm.getWikiSyntax());
		}
	}

	/**
	 * Extracts backlinks by using page metadata.
	 * @param pm page metadata
	 */
	private void extractBacklinks(PageMetadata pm) {
		String wikiSyntax = pm.getWikiSyntax();
		if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.MEDIAWIKI) {
			BacklinkExtractor e = new MediaWikiBacklinkExtractor(pm.getDocumentUri(),
																 pm.getWikiViewUrl(),
																 pm.getWikiApiUrl(),
																 this.source,
																 this.limit);
			this.backlinks = e.getBacklinks();
		} else if (WikiSyntax.valueByAlias(wikiSyntax) == WikiSyntax.TWIKI) {
			BacklinkExtractor e = new TWikiBacklinkExtractor(pm.getDocumentUri());
			this.backlinks = e.getBacklinks();
		} else {
			this.backlinks = new ArrayList<ExtractedBacklink>();
		}
	}

}
