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
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.MylynWikiTextHeadingsBuilder;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHeading;

/**
 * This is a class for extracting heading annotations from the documents.
 * @author Fabian L. Tamin
 * 
 */
public class HeadingAnnotator extends JCasAnnotator_ImplBase {
	
	/**
	 * whether this annotator is enabled
	 */
	private Boolean isEnabled;
	
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
			
			// get document text
			String text = aJCas.getDocumentText();
			
			// get page metadata
			PageMetadata pm = (PageMetadata) aJCas.getAnnotationIndex(PageMetadata.type).iterator().next();
			
			// extract heading
			List<ExtractedHeading> headings = extractHeadings(text, pm);
			
			// create heading annotations
			for (ExtractedHeading e : headings) {
				Heading annotation = new Heading(aJCas);
				annotation.setBegin(e.getBegin());
				annotation.setEnd(e.getEnd());
				annotation.setText(e.getText());
				annotation.setLevel(e.getLevel());
				annotation.addToIndexes();
			}
			
			long time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2-time1)+" "+this.getClass().getSimpleName()+" for "+pm.getWikiSyntax());
		}
	}

	/**
	 * Extracts the heading from the text by using information of page metadata.
	 * @param text source text
	 * @param pm page metadata annotation
	 * @return a list of extracted headings.
	 */
	private List<ExtractedHeading> extractHeadings(String text, PageMetadata pm) {
		MarkupParser parser = new MarkupParser(ServiceLocator.getInstance().getMarkupLanguage(pm.getWikiSyntax()));
		MylynWikiTextHeadingsBuilder builder = new MylynWikiTextHeadingsBuilder();
		parser.setBuilder(builder);
		List<ExtractedHeading> headings;
		try {
			parser.parse(text);		// FIXME BUG issue in Mylyn, sometimes parsing TWiki is unsuccessful.	
			headings = builder.getHeadings();
		} catch (Exception e) {
//			e.printStackTrace();
			headings = new ArrayList<ExtractedHeading>();
		}
		return headings;
	}

}
