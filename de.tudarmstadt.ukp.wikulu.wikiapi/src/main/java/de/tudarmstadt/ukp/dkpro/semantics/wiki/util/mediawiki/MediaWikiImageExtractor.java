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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.mediawiki;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.ImageExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedImage;

/**
 * This class is the implementation of image extractor for MediaWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class MediaWikiImageExtractor implements ImageExtractor {

	/**
	 * Source text in HTML text
	 */
	private String text;
	
	/**
	 * List of extracted images
	 */
	private List<ExtractedImage> images = new ArrayList<ExtractedImage>();
	
	private Set<String> imageHrefs = new HashSet<String>(); 
	
	/**
	 * Constructs an instance of MediaWikiImageExtractor.
	 * @param text source text in HTML syntax
	 */
	public MediaWikiImageExtractor(String text) {
		this.text = text;
		
		extractImages();
	}
	
	/**
	 * Extracts images from source text, puts it in list of images. 
	 */
	private void extractImages() {
		Parser parser;
		try {
			parser = new Parser(text);
			
			// mbox and portal images are excluded
			NodeList nodeList = parser.parse(new AndFilter(
					new HasParentFilter(new HasAttributeFilter("id", "content"), true), new AndFilter(
					new NotFilter(new HasParentFilter(new OrFilter(
							new CssSelectorNodeFilter(".mbox-image"), new OrFilter(
							new CssSelectorNodeFilter(".mbox-imageright"),
							new HasAttributeFilter("class", "noprint tright portal"))), true)), new AndFilter(
					new TagNameFilter("a"),
					new HasAttributeFilter("class", "image")
					))));
			
			if (nodeList != null) {
				for (int i = 0, size = nodeList.size(); i < size; i++) {
					if (nodeList.elementAt(i) instanceof LinkTag) {
						// getting image info from the link tag
						LinkTag linkTag = (LinkTag) nodeList.elementAt(i);
						String link = linkTag.getLink();
						String description = extractDescription(link);
						String imgHref;
						
						Parser imgParser = new Parser(linkTag.toHtml());
						NodeList imgNodes = imgParser.parse(new TagNameFilter("img"));
						
						// add image into the list
						if (imgNodes != null && imgNodes.size() > 0) {
							if (imgNodes.elementAt(0) instanceof ImageTag) {
								ImageTag imageTag = (ImageTag) imgNodes.elementAt(0);
								imgHref = imageTag.getImageURL();
								
								if (imageHrefs.add(imgHref)) {
									this.images.add(new ExtractedImage(description, imgHref));
								}
							}
						}
						
					}
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts description from a giving link.
	 * @param link URI for image file
	 * @return description
	 */
	private String extractDescription(String link) {
		String description = link.replaceFirst(".*File:", "");
		int extensionStartIndex = description.lastIndexOf(".");
		description = description.substring(0, extensionStartIndex);
		try {
			description = URLDecoder.decode(description, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		description = description.replaceAll("_", " ");
		
		return description;
	}

	@Override
	public List<ExtractedImage> getImages() {
		return this.images;
	}

}
