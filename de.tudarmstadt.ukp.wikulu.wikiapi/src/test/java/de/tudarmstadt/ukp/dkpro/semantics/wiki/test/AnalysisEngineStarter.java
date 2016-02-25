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

import java.io.IOException;
import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.PageMetadata;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.TextFileIO;
import de.tudarmstadt.ukp.wikiapi.TWiki;
import de.tudarmstadt.ukp.wikiapi.type.TWikiPage;

/**
 * This class starts Analysis Engine with defined configuration.
 * @author Fabian L. Tamin
 *
 */
public class AnalysisEngineStarter {
	/**
	 * Starter method.
	 * @param args
	 * @throws InvalidXMLException
	 * @throws IOException
	 * @throws ResourceInitializationException
	 * @throws AnalysisEngineProcessException
	 * @throws CASException 
	 */
	public static void main(String[] args) throws InvalidXMLException, IOException, ResourceInitializationException, AnalysisEngineProcessException, CASException {
		// Create Analysis Engine.
		ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();
		URL descriptor = resMgr.resolveRelativePath("desc/aggregate/MainAAEDescriptor.xml");
		AnalysisEngineDescription description = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(new XMLInputSource(descriptor));
		
		// Override parameter values in the descriptor. 
		ConfigurationParameterSettings confParam = description.getAnalysisEngineMetaData().getConfigurationParameterSettings();
		confParam.setParameterValue("WikiSyntax", "MediaWiki");
		confParam.setParameterValue("DocumentTitle", "Beagle");
		confParam.setParameterValue("DocumentId", "");
		confParam.setParameterValue("DocumentUri", "http://en.wikipedia.org/wiki/Beagle");
		confParam.setParameterValue("CollectionId", "");
		confParam.setParameterValue("WikiApiUri", "http://en.wikipedia.org/w/api.php");
		
		AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(description, resMgr, null);
		
		// Create CAS.
		JCas jcas = analysisEngine.newJCas();
		jcas.setDocumentText(TextFileIO.readInput("data/mediawiki/beagle.mediawiki", "UTF-8"));
		jcas.setDocumentLanguage("en");

		
		JCas htmlTextView = jcas.createView("html");
		//TODO what is viewURL here?
		//String htmlText = new TWiki(new URL("http://localhost/twiki/bin/rest/WikuluPlugin"), null).getHTMLPageContent("http://mrburns.tk.informatik.tu-darmstadt.de/twiki/bin/view/Wikulu/WolframAlpha");
		String htmlText = new TWikiPage(new TWiki(new URL("http://localhost/twiki/bin/rest/WikuluPlugin"), null), "Wikulu/WolframAlpha").getHtmlContent();
//		TextFileIO.writeOutput(htmlText, "M:/ajrkeif/dokoi/data/stoit/(eclipse)/WikiAPI/data/out/htmlText.html", "UTF-8");
		htmlTextView.setDocumentText(htmlText);
		
		
		analysisEngine.process(jcas);
		
		// Test
		FSIndex ai = jcas.getAnnotationIndex(PageMetadata.type);
		FSIterator i = ai.iterator();
		for (; i.hasNext(); ) {
			PageMetadata pm = (PageMetadata) i.next();
			System.out.println("WikiSyntax: "+pm.getWikiSyntax());
			System.out.println("DocumentTitle: "+pm.getDocumentTitle());
			System.out.println("DocumentId: "+pm.getDocumentId());
			System.out.println("DocumentUri: "+pm.getDocumentUri());
			System.out.println("CollectionId: "+pm.getCollectionId());
		}
		
		ai = jcas.getAnnotationIndex(Heading.type);
		i = ai.iterator();
		System.out.println();
		System.out.println("======Heading======");
		for (; i.hasNext(); ) {
			Heading pm = (Heading) i.next();
			System.out.println("["+pm.getLevel()+"]: "+pm.getText());
		}
		
		ai = jcas.getAnnotationIndex(Tag.type);
		i = ai.iterator();
		System.out.println();
		System.out.println("======Tag======");
		for (; i.hasNext(); ) {
			Tag pm = (Tag) i.next();
			System.out.println("["+pm.getText()+"]: "+pm.getHref());
		}
		
		ai = jcas.getAnnotationIndex(ExternalLink.type);
		i = ai.iterator();
		System.out.println();
		System.out.println("======ExternalLink======");
		for (; i.hasNext(); ) {
			ExternalLink pm = (ExternalLink) i.next();
			System.out.println("["+pm.getText()+"]: "+pm.getHref());
		}
		
		ai = jcas.getAnnotationIndex(InternalLink.type);
		i = ai.iterator();
		System.out.println();
		System.out.println("======InternalLink======");
		for (; i.hasNext(); ) {
			InternalLink pm = (InternalLink) i.next();
			System.out.println("["+pm.getText()+"]: "+pm.getHref());
		}
		
		ai = jcas.getAnnotationIndex(Backlink.type);
		i = ai.iterator();
		System.out.println();
		System.out.println("======Backlink======");
		for (; i.hasNext(); ) {
			Backlink pm = (Backlink) i.next();
			System.out.println("["+pm.getText()+"]: "+pm.getHref());
		}
	}
}
