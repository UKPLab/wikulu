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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.twiki;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.ImageExtractor;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedImage;

/**
 * This class is the implementation image extractor for TWiki syntax.
 * @author Fabian L. Tamin
 *
 */
public class TWikiImageExtractor implements ImageExtractor {
	/**
	 * Source text in HTML text
	 */
	private String text;
	
	/**
	 * List of extracted images
	 */
	private List<ExtractedImage> images = new ArrayList<ExtractedImage>();
	
	/**
	 * Constructs an instance of TWikiImageExtractor.
	 * @param text source text in HTML syntax
	 */
	public TWikiImageExtractor(String text) {
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
			
			NodeList nodeList = parser.parse(new AndFilter(
					new HasParentFilter(new HasAttributeFilter("id", "patternMainContents"), true),
					new TagNameFilter("img")
			));
			
			if (nodeList != null) {
				for (int i = 0, size = nodeList.size(); i < size; i++) {
					if (nodeList.elementAt(i) instanceof ImageTag) {
						ImageTag imageTag = (ImageTag) nodeList.elementAt(i);
						String description = imageTag.getAttribute("alt");
						String imgHref = imageTag.getImageURL();
						if (description == null || description.equals("")) {
							description = imgHref;
						}
						
						try {
							// filter small images
							int w = Integer.parseInt(imageTag.getAttribute("width"));
							int h = Integer.parseInt(imageTag.getAttribute("height"));
							if (w > 16 && h > 16) {
								this.images.add(new ExtractedImage(description, imgHref));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ExtractedImage> getImages() {
		return this.images;
	}

}
