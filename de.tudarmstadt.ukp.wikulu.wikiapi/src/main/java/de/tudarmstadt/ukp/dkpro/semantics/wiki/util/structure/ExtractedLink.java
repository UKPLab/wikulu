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
 * This class represents extracted link which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedLink extends ExtractedWikiComponent {
	/**
	 * Link's alternative text.
	 */
	protected String text;
	/**
	 * Link's href.
	 */
	protected String href;

	/**
	 * Constructs an instance of extracted links.
	 * @param text link's alternative text
	 * @param href link's href
	 */
	public ExtractedLink(String text, String href) {
		this.href = href;
		this.text = text;
	}

	/**
	 * Gets link's href.
	 * @return link's href
	 */
	public String getHref() {
		return href;
	}
	
	/**
	 * Sets link's href.
	 * @param href link's href
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * Gets link's alternative text.
	 * @return link's alternative text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets link's alternative text.
	 * @param text link's alternative text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "[link]["+text+"]["+href+"]";
	}
}
