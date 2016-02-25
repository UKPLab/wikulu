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
 * This class represents extracted Wiki component which is extracted by using an extractor.
 * @author Fabian L. Tamin
 *
 */
public class ExtractedWikiComponent {
	/**
	 * Begin index of the Wiki component in source text
	 */
	protected int begin;
	/**
	 * End index of the Wiki component in source text
	 */
	protected int end;

	/**
	 * Get the begin index of the Wiki component in source text.
	 * @return begin index of the Wiki component in source text
	 */
	public int getBegin() {
		return begin;
	}
	
	/**
	 * Sets the begin index of the Wiki component in source text.
	 * @param begin begin index of the Wiki component in source text
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}
	
	/**
	 * Gets end index of the Wiki component in source text.
	 * @return end index of the source text
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Sets end index of the Wiki component in source text
	 * @param end end index of the Wiki component in source text
	 */
	public void setEnd(int end) {
		this.end = end;
	}
}
