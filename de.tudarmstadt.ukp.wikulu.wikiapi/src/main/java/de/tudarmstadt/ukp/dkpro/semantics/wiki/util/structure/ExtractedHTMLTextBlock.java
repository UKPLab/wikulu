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
 * This class represents HTMLTextBlock which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedHTMLTextBlock extends ExtractedWikiComponent {
	/**
	 * HTML text in the block
	 */
	protected String text;
	/**
	 * The type of the block
	 */
	protected String blockType;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}
	
	public String getBlockType() {
		return this.blockType;
	}
	
	@Override
	public String toString() {
		return "[HTMLTextBlock]["+blockType+"]\n"+text+"\n";
	}

}
