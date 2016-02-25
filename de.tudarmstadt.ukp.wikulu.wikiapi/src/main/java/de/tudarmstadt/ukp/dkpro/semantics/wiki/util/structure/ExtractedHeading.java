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
 * This class represents heading which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedHeading extends ExtractedWikiComponent {
	
	/**
	 * Heading's level 
	 */
	protected int level;
	/**
	 * Heading's text
	 */
	protected String text;
	
	/**
	 * Construct an extracted heading instance.
	 * @param level heading's level
	 * @param text heading's text
	 */
	public ExtractedHeading(int level, String text) {
		this.level = level;
		this.text = text;
	}
	
	/**
	 * Gets heading's level.
	 * @return heading's level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets heading's level.
	 * @param level heading's level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets heading's text.
	 * @return heading's text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets heading's text.
	 * @param text heading's text
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "[heading "+level+"]:["+text+"]";
	}
}
