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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;

/**
 * This is a class records the information from configuration parameters as page metadata.
 * @author Fabian L. Tamin
 * 
 */
public class PageMetadataAnnotator extends JCasAnnotator_ImplBase {

	private String wikiSyntax;
	private String documentTitle;
	private String documentId;
	private String documentUri;
	private String collectionId;
	private String wikiViewUrl;
	private String wikiApiUrl;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		
		this.wikiSyntax = (String) context.getConfigParameterValue("WikiSyntax");
		this.documentTitle = (String) context.getConfigParameterValue("DocumentTitle");
		this.documentId = (String) context.getConfigParameterValue("DocumentId");
		this.documentUri = (String) context.getConfigParameterValue("DocumentUri");
		this.collectionId = (String) context.getConfigParameterValue("CollectionId");
		this.wikiViewUrl = (String) context.getConfigParameterValue("WikiViewUrl");
		this.wikiApiUrl = (String) context.getConfigParameterValue("WikiApiUrl");
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {		
		long time1 = System.currentTimeMillis();
		
		PageMetadata p = new PageMetadata(aJCas);
		
		p.setWikiSyntax(this.wikiSyntax);
		p.setDocumentTitle(this.documentTitle);
		p.setDocumentId(this.documentId);
		p.setDocumentUri(this.documentUri);
		p.setCollectionId(this.collectionId);
		p.setWikiViewUrl(this.wikiViewUrl);
		p.setWikiApiUrl(this.wikiApiUrl);
		
		p.addToIndexes();
		
		long time2 = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
		System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+p.getWikiSyntax());
	}

}
