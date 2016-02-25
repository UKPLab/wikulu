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
package de.tudarmstadt.ukp.wikisniffer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.LinkSetElementCapsule;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.MediaWikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.TWikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.controller.WikiController;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.helper.HTMLToTextConverter;
import de.tudarmstadt.ukp.wikulu.plugin.keyphraseextraction.KeyphraseExtractionPlugin;
import de.tudarmstadt.ukp.wikulu.plugin.summarization.SummarizationPlugin;

/**
 * This class is a junction between façade class wikiController in WikiApi project and JavaScript object.
 * This class will be converted by DWR to JavaScript in the runtime. 
 * @author Fabian L. Tamin
 *
 */
public class WikiSnifferDWR {
	/**
	 * WikiController of this class
	 */
	private WikiController wikiController;
	
	/**
	 * document URI
	 */
	private String documentUri;
	/**
	 * wiki View URI
	 */
	private String wikiViewUrl;
	/**
	 * wiki API URI
	 */
	private String wikiApiUrl;
	/**
	 * wiki syntax
	 */
	private String wikiSyntax;
	/**
	 * whether headings is enable
	 */
	private boolean enableHeadings;
	/**
	 * whether internal links is enable
	 */
	private boolean enableInternalLinks;
	/**
	 * whether external links is enable
	 */
	private boolean enableExternalLinks;
	/**
	 * whether backlinks is enable
	 */
	private boolean enableBacklinks;
	/**
	 * whether images is enable
	 */
	private boolean enableImages;
	/**
	 * a list of headings
	 */
	private List<HeadingDWR> headings = new ArrayList<HeadingDWR>();
	/**
	 * a list of tags
	 */
	private List<LinkDWR> tags = new ArrayList<LinkDWR>();
	/**
	 * a list of internal links
	 */
	private List<LinkDWR> internalLinks = new ArrayList<LinkDWR>();
	/**
	 * a list of backlinks
	 */
	private List<LinkDWR> backlinks = new ArrayList<LinkDWR>();
	/**
	 * a list of external links
	 */
	private List<LinkDWR> externalLinks = new ArrayList<LinkDWR>();
	/**
	 * a list of images
	 */
	private List<ImageDWR> images = new ArrayList<ImageDWR>();
	/**
	 * a list of keyphrases
	 */
	private List<String> keyphrases = new ArrayList<String>();
	/**
	 * the main summary
	 */
	private String summary = "";
	/**
	 * an extra summary
	 */
	private String extraSummary = "";
	
	/**
	 * configuration for using NLP services
	 */
	private ResourceBundle config;
	
	/**
	 * Constructs an instance of this class.
	 * @param documentUri document URI or URL
	 * @param wikiApiUrl wiki API URI
	 * @param wikiSyntax wiki syntax
	 * @param enableExtraSummary whether extra summary is enable
	 * @param enableHeadings whether headings is enable
	 * @param enableInternalLinks whether internal links is enable
	 * @param enableExternalLinks whether external links is enable
	 * @param enableBacklinks whether backlinks is enable
	 * @param enableImages whether images is enable
	 * @return this object itself.
	 */
	public WikiSnifferDWR getWikiSniffer(String documentUri, String wikiViewUrl, String wikiApiUrl, String wikiSyntax,
										 boolean enableExtraSummary,
										 boolean enableHeadings,
										 boolean enableInternalLinks,
										 boolean enableExternalLinks,
										 boolean enableBacklinks,
										 boolean enableImages) {
		this.documentUri = documentUri;
		this.wikiViewUrl = wikiViewUrl;
		this.wikiApiUrl = wikiApiUrl;
		this.wikiSyntax = wikiSyntax;
		this.enableHeadings = enableHeadings;
		this.enableInternalLinks = enableInternalLinks;
		this.enableExternalLinks = enableExternalLinks;
		this.enableBacklinks = enableBacklinks;
		this.enableImages = enableImages;
		
		this.config = PropertyResourceBundle.getBundle("wikulu");
		
		initalizeWikiController();
		if (enableHeadings) {
			extractHeadings();
		}
		if (enableInternalLinks) {
			extractInternalLinks();
		}
		if (enableExternalLinks) {
			extractExternalLinks();
		}
		if (enableBacklinks) {
			extractBacklinks();
		}
		if (enableImages) {
			extractImages();
		}
		
		extractTags();
		extractSummary();
		if (enableExtraSummary) {
			extractExtraSummary();
		}
		
		return this;
	}

//	public WikiSnifferDWR getBacklinksWikiSniffer(String documentUri, String wikiApiUrl, String wikiSyntax) {
//	this.documentUri = documentUri;
//	this.wikiApiUri = wikiApiUrl;
//	this.wikiSyntax = wikiSyntax;
//	
//	this.config = PropertyResourceBundle.getBundle("wikulu");
//	
//	initalizeWikiController();
//	extractBacklinks();
//	
//	try {
//		extractSummary();
//	} catch (RemoteException e) {
//		summary = e.getMessage();
//	}
//	
//	return this;
//}
	
	/**
	 * Gets the list of extracted keyphrases.  
	 * @param documentUri document URI or URL
	 * @param wikiApiUrl wiki api URI
	 * @param wikiSyntax wiki syntax
	 * @return the list of extracted keyphrases.
	 */
	public List<String> getKeyphrasesWikiSniffer(String documentUri, String wikiViewUrl, String wikiApiUrl, String wikiSyntax) {		
		this.documentUri = documentUri;
		this.wikiViewUrl = wikiViewUrl;
		this.wikiApiUrl = wikiApiUrl;
		this.wikiSyntax = wikiSyntax;
		this.config = PropertyResourceBundle.getBundle("wikulu");
		
		initalizeWikiController();
		extractKeyphrases();
		
		return getKeyphrases();
	}

	/**
	 * Gets the list of headings.
	 * @return the list of headings.
	 */
	public List<HeadingDWR> getHeadings() {
		return headings;
	}

	/**
	 * Gets the list of backlinks.
	 * @return the list of backlinks.
	 */
	public List<LinkDWR> getBacklinks() {
		return backlinks;
	}

	/**
	 * Gets the list of external links.
	 * @return the list of external links.
	 */
	public List<LinkDWR> getExternalLinks() {
		return externalLinks;
	}

	/**
	 * Gets the list of internal links.
	 * @return the list of internal links.
	 */
	public List<LinkDWR> getInternalLinks() {
		return internalLinks;
	}

	/**
	 * Gets the list of tags.
	 * @return the list of tags.
	 */
	public List<LinkDWR> getTags() {
		return tags;
	}
	
	/**
	 * Gets the list of images.
	 * @return the list of images.
	 */
	public List<ImageDWR> getImages() {
		return images;
	}
	
	/**
	 * Gets the list of keyphrases.
	 * @return the list of keyphrases.
	 */
	public List<String> getKeyphrases() {
		return keyphrases;
	}
	
	/**
	 * Gets the main summary.
	 * @return the list of summary.
	 */
	public String getSummary() {
		return summary;
	}
	
	/**
	 * Gets the extra sequel of summary.
	 * @return extra sequel of summary.
	 */
	public String getExtraSummary() {
		return extraSummary;
	}
	
	/**
	 * Extracts tags using wikiController.
	 */
	private void extractTags() {
		System.out.println("extracting tags");
		Set<LinkSetElementCapsule<Tag>> links = this.wikiController.getTags();
		this.tags = new ArrayList<LinkDWR>();
		
		for (LinkSetElementCapsule<Tag> capsule : links) {
			Tag link = capsule.getLink();
			LinkDWR e = new LinkDWR();
			e.href = link.getHref();
			e.text = link.getText();
			this.tags.add(e);
		}
	}

	/**
	 * Extracts backlinks using wikiController.
	 */
	private void extractBacklinks() {
		System.out.println("extracting backlinks");
		Set<LinkSetElementCapsule<Backlink>> links = this.wikiController.getBacklinks();
		this.backlinks = new ArrayList<LinkDWR>();
		
		for (LinkSetElementCapsule<Backlink> capsule : links) {
			Backlink link = capsule.getLink();
			LinkDWR e = new LinkDWR();
			e.href = link.getHref();
			e.text = link.getText();
			this.backlinks.add(e);
		}
	}

	/**
	 * Extracts external links using wikiController.
	 */
	private void extractExternalLinks() {
		System.out.println("extracting external links");
		Set<LinkSetElementCapsule<ExternalLink>> links = this.wikiController.getExternalLinks();
		this.externalLinks = new ArrayList<LinkDWR>();
		
		for (LinkSetElementCapsule<ExternalLink> capsule : links) {
			ExternalLink link = capsule.getLink();
			LinkDWR e = new LinkDWR();
			e.href = link.getHref();
			e.text = link.getText();
			this.externalLinks.add(e);
		}
	}

	/**
	 * Extracts internal links using wikiController.
	 */
	private void extractInternalLinks() {
		System.out.println("extracting internal links");
		Set<LinkSetElementCapsule<InternalLink>> links = this.wikiController.getInternalLinks();
		this.internalLinks = new ArrayList<LinkDWR>();
		
		for (LinkSetElementCapsule<InternalLink> capsule : links) {
			InternalLink link = capsule.getLink();
			LinkDWR e = new LinkDWR();
			e.href = link.getHref();
			e.text = link.getText();
			this.internalLinks.add(e);
		}
	}
	
	/**
	 * Extracts images using wikiController.
	 */
	private void extractImages() {
		System.out.println("extracting images");
		List<Image> images = this.wikiController.getImages();
		this.images = new ArrayList<ImageDWR>();
		
		if (images != null) {
			for (Image image : images) {
				ImageDWR e = new ImageDWR();
				e.description = image.getDescription();
				e.href = image.getHref();
				
				this.images.add(e);
			}
		}
	}

	/**
	 * Extracts headings using wikiController.
	 */
	private void extractHeadings() {
		System.out.println("extracting headings");
		List<Heading> headings = this.wikiController.getHeadings();
		this.headings = new ArrayList<HeadingDWR>();
		
		for (Heading heading : headings) {
			HeadingDWR e = new HeadingDWR();
			e.level = heading.getLevel();
			e.text = heading.getText();
			
			this.headings.add(e);
		}
	}
	
	/**
	 * Extracts the main summary using wikiController.
	 */
	private void extractSummary() {
		System.out.println("Extracting simple summary");
		try {
			this.summary = HTMLToTextConverter.getPlainTextOfParagraphs(this.wikiController.getArticleText());
		}
		catch (ParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.summary = this.summary.replace("\"", "\'").replace("\n", "");
		this.summary = this.summary.replaceAll("&*;", "");
		SummarizationPlugin sp = new SummarizationPlugin();
		String jsonString = "{ \"text\" : \"" + this.summary + "\" }";
		String summary = "";
		String summaryString;
		if(this.summary.length() > 0) {
			try {
				summaryString = sp.run(jsonString);
				JSONObject summaryJson = new JSONObject(summaryString);
				JSONArray sentences = summaryJson.getJSONArray("summary").getJSONArray(0);
				for(int x=0; x<sentences.length(); x++) {
					summary += sentences.getString(x);
				}
			} catch (Exception e) {
				e.printStackTrace();
				summary = "";
			} 
		} else {
			summary = "";
		}
		//this.extractExtraSummary();
		this.summary = summary.replace("[", "").replace("]", "");
	}
	
	/**
	 * Extracts extra sequel of summary using LexRank NLP Service.
	 */
	private void extractExtraSummary() {
		String text;
		System.out.println("Extracting extra summary");
		try {
			text = HTMLToTextConverter.getPlainTextOfParagraphs(this.wikiController.getArticleText());
		} catch (ParserException e) {
			text = "";
			e.printStackTrace();
		}
		SummarizationPlugin summarizer = new SummarizationPlugin();
		String jsonString = "{ \"text\" : \"" + text + "\" }";
		
		String summaryString;
		String summary;
		if (text.length() > 0) {
			try {
				summaryString = summarizer.run(jsonString);
				JSONObject summaryJson = new JSONObject(summaryString);
				summary = summaryJson.getString("summary");
			} catch (Exception e) {
				// TODO: DO NOT PRINT STACK TRACES!
				//e.printStackTrace();
				summary = "";
			}
		} else {
			summary = "";
		}
		this.extraSummary = summary.replace("[", "").replace("]", "");
	}

	/**
	 * Extracts keyphrases using TextRank NLP service.
	 */
	private void extractKeyphrases() {
		KeyphraseExtractionPlugin extractor = new KeyphraseExtractionPlugin();
		
		try {
			// switch comments with the next line to use either plain text from wikitext or HTML.
//			String text = wikiController.getFilteredWikiTextAsPlainText(); 
			String text = wikiController.getArticleText();
			
			List<String> keyphrases = new LinkedList<String>();
			
			if (!text.isEmpty()) {	 
				
				String jsonStr = "{ \"text\" : \"" + URLEncoder.encode(text, "UTF-8") + "\" }";
				JSONObject keyphrasesJson = new JSONObject(extractor.run(jsonStr));
				JSONArray array = keyphrasesJson.getJSONArray("keyphrases");
				for(int x=0; x<array.length(); x++) {
					keyphrases.add(array.getString(x));
				}
				
				if (keyphrases != null) {
					int keyphraseMaxCount = Integer.parseInt(config.getString("wikulu.keyphrases.count"));
					
					int keyphraseCount = (keyphrases.size() > keyphraseMaxCount) ? keyphraseMaxCount : keyphrases.size();
					
					keyphrases = keyphrases.subList(0, keyphraseCount);
				}
			}
			
			this.keyphrases = keyphrases;
		} catch (Exception e) {
			keyphrases = new ArrayList<String>();
			keyphrases.add(e.getMessage());
		}
	}

	/**
	 * Initializes the wikiController before using it.
	 */
	private void initalizeWikiController() {
		if (this.wikiSyntax.equals("MediaWiki")) {
			this.wikiController = new MediaWikiController(this.documentUri, this.wikiViewUrl, this.wikiApiUrl,
					enableHeadings, enableInternalLinks,
					enableExternalLinks, enableBacklinks, enableImages);
		} else if (this.wikiSyntax.equals("TWiki")) {
			this.wikiController = new TWikiController(this.documentUri, this.wikiViewUrl, this.wikiApiUrl,
					enableHeadings, enableInternalLinks,
					enableExternalLinks, enableBacklinks, enableImages);
		} else {
			throw new IllegalArgumentException("Only MediaWiki and TWiki are allowed for Wiki syntax");
		}
	}
	
	// nested classes
	/**
	 * DWR class that represents Heading.
	 * @author Fabian L. Tamin
	 * 
	 */
	public static class HeadingDWR {
		/**
		 * heading level
		 */
		public int level;
		/**
		 * text in the heading
		 */
		public String text;
	}
	
	/**
	 * DWR class that represents Link.
	 * @author Fabian L. Tamin
	 *
	 */
	public static class LinkDWR {
		/**
		 * href of the link
		 */
		public String href;
		/**
		 * display text of the link 
		 */
		public String text;
	}
	
	/**
	 * DWR class that represents Image.
	 * @author Fabian L. Tamin
	 *
	 */
	public static class ImageDWR {
		/**
		 * image description
		 */
		public String description;
		/**
		 * href of the image
		 */
		public String href;
	}
}
