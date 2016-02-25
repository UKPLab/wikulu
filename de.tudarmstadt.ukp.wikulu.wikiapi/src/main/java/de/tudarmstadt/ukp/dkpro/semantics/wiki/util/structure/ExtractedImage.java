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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure;

/**
 * This class represents extracted images which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedImage extends ExtractedWikiComponent {
	/**
	 * Image description
	 */
	protected String description;
	
	/**
	 * Link's href
	 */
	protected String href;
	
	/**
	 * Constructs an instance of ExtractedImages.
	 * @param description Image description
	 * @param href Link's href
	 */
	public ExtractedImage(String description, String href) {
		this.description = description;
		this.href = href;
	}
	
	/**
	 * Gets image description
	 * @return image description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets image description.
	 * @param description Image description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets link's href.
	 * @return Link's href
	 */
	public String getHref() {
		return href;
	}
	
	/**
	 * Sets link's href.
	 * @param href Link's href
	 */
	public void setHref(String href) {
		this.href = href;
	}
}
