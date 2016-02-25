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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.controller;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.BacklinkAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.HTMLTextBlockAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.HeadingAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.ImageAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.LinkAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.PageMetadataAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.annotator.TagAnnotator;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant.WikiSyntax;
import de.tudarmstadt.ukp.wikiapi.Wiki;
import de.tudarmstadt.ukp.wikiapi.type.Page;

/**
 * This class implements template pattern for an abstraction of controller
 * class.
 * 
 * @author Fabian L. Tamin
 * 
 */
public abstract class WikiAbstractionController
	implements WikiController
{
	/**
	 * Wiki object.
	 * 
	 * @see Wiki
	 */
	protected Wiki wiki;

	/**
	 * filtered WikiText
	 */
	protected String filteredWikiText;
	/**
	 * WikiSyntax type
	 * 
	 * @see WikiSyntax
	 */
	protected String wikiSyntax = "";
	/**
	 * Document title
	 */
	protected String documentTitle = "";
	/**
	 * Document ID
	 */
	protected String documentId = "";
	/**
	 * Document URI
	 */
	protected String documentUri = "";
	/**
	 * Collection ID
	 */
	protected String collectionId = "";
	/**
	 * Wiki View URL: current used wiki view url
	 */
	protected String wikiViewUrl = "";
	/**
	 * Wiki API URL: current used wiki API link
	 */
	protected String wikiApiUrl = "";

	/**
	 * Main Analysis Engine for UIMA
	 */
	private AnalysisEngine mainAnalysisEngine;
	/**
	 * Text block Analysis Engine for UIMA
	 */
	private AnalysisEngine textBlockAnalysisEngine;
	/**
	 * Analysis Engine Description for UIMA
	 */
	private AnalysisEngineDescription description;
	/**
	 * Resource Manager for UIMA
	 */
	private ResourceManager resMgr;
	/**
	 * whether headings is enable
	 */
	private boolean enableHeadings;
	/**
	 * whether outgoing links is enable
	 */
	private boolean enableLinks;
	/**
	 * whether backlinks is enable
	 */
	private boolean enableBacklinks;
	/**
	 * whether images is enable
	 */
	private boolean enableImages;
	/**
	 * JCAS of Analysis Engine
	 */
	private JCas jCas;
	//private String plainTextForKeyphraseExtractor;
	//private HTMLForSummarizationExtractor htmlForSummarization;

	/**
	 * This constructor is the template of controller constructor
	 * implementation.
	 * 
	 * @param documentUri
	 *            document URI
	 * @param wikiApiUrl
	 *            Wiki API URI
	 * @param enableHeadings
	 *            whether headings is enable
	 * @param enableInternalLinks
	 *            whether internal links is enable
	 * @param enableExternalLinks
	 *            whether external links is enable
	 * @param enableBacklinks
	 *            whether backlinks is enable
	 * @param enableImages
	 *            whether images is enable
	 */
	public WikiAbstractionController(String documentUri, String wikiViewUrl,
			String wikiApiUrl, boolean enableHeadings,
			boolean enableInternalLinks, boolean enableExternalLinks,
			boolean enableBacklinks, boolean enableImages)
	{
		System.out.println("-Start WikiAbstractionController WikiAPI-");

		/*StackTraceElement[] blah = Thread.currentThread().getStackTrace();
		for(StackTraceElement e : blah) {
			System.out.println(e.getClassName());
		}*/
		
		this.documentUri = documentUri;
		this.wikiViewUrl = wikiViewUrl;
		this.wikiApiUrl = wikiApiUrl;

		this.enableHeadings = enableHeadings;
		this.enableLinks = (enableInternalLinks || enableExternalLinks);
		this.enableBacklinks = enableBacklinks;
		this.enableImages = enableImages;

		setWikiSyntax();
		setWiki();
		setFilteredWikiText();
	}

	/**
	 * set wiki text
	 */
	protected abstract void setFilteredWikiText();

	/**
	 * Sets WikiSyntax attribute.
	 */
	protected abstract void setWikiSyntax();

	/**
	 * Sets Wiki attribute.
	 */
	protected abstract void setWiki();

	private void createAggregateDesc()
	{
		try {
			AnalysisEngineDescription imgDesc = createPrimitiveDescription(
					ImageAnnotator.class, "isEnabled", this.enableImages);
			AnalysisEngineDescription headingDesc = createPrimitiveDescription(
					HeadingAnnotator.class, "isEnabled", this.enableHeadings);
			AnalysisEngineDescription tagDesc = createPrimitiveDescription(TagAnnotator.class);
			AnalysisEngineDescription backLinkDesc = createPrimitiveDescription(
					BacklinkAnnotator.class, "isEnabled", this.enableBacklinks,
					"source", "wikiapi", "limit", 50);
			AnalysisEngineDescription pageMetadataDesc = createPrimitiveDescription(
					PageMetadataAnnotator.class, "WikiSyntax", this.wikiSyntax,
					"DocumentTitle", this.documentTitle, "DocumentId",
					this.documentId, "DocumentUri", this.documentUri,
					"CollectionId", this.collectionId, "WikiApiUri",
					this.wikiApiUrl);
			AnalysisEngineDescription htmlTextBlockDesc = createPrimitiveDescription(HTMLTextBlockAnnotator.class);
			AnalysisEngineDescription linkDesc = createPrimitiveDescription(
					LinkAnnotator.class, "isEnabled", this.enableLinks);

			AggregateBuilder aggBuilder = new AggregateBuilder();
			aggBuilder.add(pageMetadataDesc);
			aggBuilder.add(headingDesc);

			aggBuilder.add(linkDesc, "html", "html");
			aggBuilder.add(tagDesc);
			aggBuilder.add(htmlTextBlockDesc, "html", "html");

			aggBuilder.add(backLinkDesc);
			aggBuilder.add(imgDesc, "html", "html");

			this.description = aggBuilder.createAggregateDescription();

			this.mainAnalysisEngine = aggBuilder.createAggregate();
		}
		catch (ResourceInitializationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sets Analysis Engine to the object
	 */
	protected void setMainAnalysisEngine()
	{
		long time1 = System.currentTimeMillis();
		long time2;
		try {

			this.createAggregateDesc();
			// Create CAS.
			this.jCas = this.mainAnalysisEngine.newJCas();

			this.jCas.setDocumentText(this.filteredWikiText);
			this.jCas.setDocumentLanguage("en");

			JCas htmlTextView = this.jCas.createView("html");
			Page page = wiki.getPageForURL(new URL(documentUri));
			String htmlText = page.getHtmlContent();
			htmlTextView.setDocumentText(htmlText);

			time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2 - time1)
					+ " configure analysis engine");

			time1 = time2;
			this.mainAnalysisEngine.process(this.jCas);
			time2 = System.currentTimeMillis();
			System.out.println(formatter.format(time2 - time1)
					+ " processing analysis engine");
		}
		catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets Analysis Engine to the object
	 */
	protected void setTextBlockAnalysisEngine()
	{
		long time1 = System.currentTimeMillis();
		long time2;
		// Create Analysis Engine.
		this.resMgr = UIMAFramework.newDefaultResourceManager();
		URL descriptor;
		try {
			descriptor = resMgr
					.resolveRelativePath("desc/aggregate/TextBlockAAEDescriptor.xml");
			this.description = UIMAFramework.getXMLParser()
					.parseAnalysisEngineDescription(
							new XMLInputSource(descriptor));

			// Override parameter values in the descriptor.
			ConfigurationParameterSettings confParam = this.description
					.getAnalysisEngineMetaData()
					.getConfigurationParameterSettings();
			confParam.setParameterValue("WikiSyntax", this.wikiSyntax);
			confParam.setParameterValue("DocumentTitle", this.documentTitle); // TODO
			// empty
			confParam.setParameterValue("DocumentId", this.documentId); // TODO
			// empty
			confParam.setParameterValue("DocumentUri", this.documentUri);
			confParam.setParameterValue("CollectionId", this.collectionId); // TODO
			// empty
			confParam.setParameterValue("WikiApiUri", this.wikiApiUrl);

			this.textBlockAnalysisEngine = UIMAFramework.produceAnalysisEngine(
					this.description, resMgr, null);

			// Create CAS.
			this.jCas = this.textBlockAnalysisEngine.newJCas();

			this.jCas.setDocumentText(this.filteredWikiText);
			// this.jCas.setDocumentLanguage("en");

			time2 = System.currentTimeMillis();
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:S");
			System.out.println(formatter.format(time2 - time1)
					+ " configure textblock analysis engine");

			time1 = time2;
			this.textBlockAnalysisEngine.process(this.jCas);
			time2 = System.currentTimeMillis();
			System.out.println(formatter.format(time2 - time1)
					+ " processing textblock analysis engine");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (InvalidXMLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getWikiSyntax()
	{
		return this.wikiSyntax;
	}

	@Override
	public String getDocumentUri()
	{
		return this.documentUri;
	}

	@Override
	public String getWikiApiUri()
	{
		return this.wikiApiUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Heading> getHeadings()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex = this.jCas
				.getAnnotationIndex(Heading.type);
		List<Heading> list = new ArrayList<Heading>();

		for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
			list.add((Heading) i.next());
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Image> getImages()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex;
		List<Image> list = new ArrayList<Image>();

		try {
			annotationIndex = this.jCas.getAnnotationIndex(Image.type);

			for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
				list.add((Image) i.next());
			}
		}
		catch (CASRuntimeException e) {
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<LinkSetElementCapsule<Backlink>> getBacklinks()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex = this.jCas
				.getAnnotationIndex(Backlink.type);
		Set<LinkSetElementCapsule<Backlink>> set = new TreeSet<LinkSetElementCapsule<Backlink>>();

		for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
			set.add(new LinkSetElementCapsule<Backlink>((Backlink) i.next()));
		}

		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<LinkSetElementCapsule<ExternalLink>> getExternalLinks()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex = this.jCas
				.getAnnotationIndex(ExternalLink.type);
		Set<LinkSetElementCapsule<ExternalLink>> set = new TreeSet<LinkSetElementCapsule<ExternalLink>>();

		for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
			set.add(new LinkSetElementCapsule<ExternalLink>((ExternalLink) i
					.next()));
		}

		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<LinkSetElementCapsule<InternalLink>> getInternalLinks()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex = this.jCas
				.getAnnotationIndex(InternalLink.type);
		Set<LinkSetElementCapsule<InternalLink>> set = new TreeSet<LinkSetElementCapsule<InternalLink>>();

		for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
			InternalLink link = (InternalLink) i.next();
			set.add(new LinkSetElementCapsule<InternalLink>(link));
		}

		return set;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<LinkSetElementCapsule<Tag>> getTags()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		AnnotationIndex annotationIndex = this.jCas
				.getAnnotationIndex(Tag.type);
		Set<LinkSetElementCapsule<Tag>> set = new TreeSet<LinkSetElementCapsule<Tag>>();

		for (FSIterator i = annotationIndex.iterator(); i.hasNext();) {
			set.add(new LinkSetElementCapsule<Tag>((Tag) i.next()));
		}

		return set;
	}

	@Override
	public String getArticleText()
	{
		if (this.mainAnalysisEngine == null) {
			setMainAnalysisEngine();
		}

		String out = "";
		try {
			System.out.println("URI: " + documentUri);
			Page page = this.wiki.getPageForURL(new URL(documentUri));
			out = page.getPlainContentFromWikiSyntax();
			// since text contains weird quotation marks, remove them
			// this code looks weird, but it works
			/*Pattern p = Pattern.compile("&.{2}quo;");
			Matcher m = p.matcher(text);
			out = m.replaceAll("");*/
			// this.plainTextForKeyphraseExtractor = out;
			

			return out;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return out;
	}

}