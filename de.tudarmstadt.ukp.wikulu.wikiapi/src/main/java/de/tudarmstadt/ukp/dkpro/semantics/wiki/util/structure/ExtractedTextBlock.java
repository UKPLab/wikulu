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
 * This class represents text block which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedTextBlock extends ExtractedWikiComponent {
	
	/**
	 * WikiText as plain text
	 */
	protected String plainText;
	/**
	 * Block's type
	 */
	protected String type;
	
	/**
	 * Constructs an extracted backlink instance.
	 * @param plainText WikiText as plain text
	 * @param type block's type
	 */
	public ExtractedTextBlock(String plainText, String type) {
		this.plainText = plainText;
		this.type = type;
	}
	
	/**
	 * Sets plain text of the block.
	 * @param plainText WikiText as plain text
	 */
	public void setText(String plainText) {
		this.plainText = plainText;
	}
	
	/**
	 * Gets plain text of the block.
	 * @return return WikiText as plain text
	 */
	public String getText() {
		return plainText;
	}
	
	/**
	 * Sets the block's type.
	 * @param type block's type
	 */
	public void setBlockType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the block's type.
	 * @return the block's type
	 */
	public String getBlockType() {
		return type;
	}
}
