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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.util.structure.ExtractedHeading;

/**
 * This class is used to extract heading from Wiki text. It uses Mylyn facility, so it supports both MediaWiki and TWiki syntax.
 * <p>
 * Known issues as trade-off of using Mylyn parser:
 * <ul>
 * <li>WikiText which is nested in heading are not extracted correctly.</li>
 * </ul>
 * </p> 
 * @author Fabian L. Tamin
 *
 */
public class MylynWikiTextHeadingsBuilder extends NoOpDocumentBuilder implements HeadingExtractor{
	/**
	 * List of extracted headings.
	 */
	private List<ExtractedHeading> headings = new ArrayList<ExtractedHeading>();

	/**
	 * Current heading. If there is no heading to be worked on, so heading = <code>null</code>.
	 */
	private ExtractedHeading heading = null;
	
	@Override
	public void characters(String text) {
		if (this.heading != null) {
			this.heading.setText(this.heading.getText()+text);
		}
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		this.heading = new ExtractedHeading(level, "");
	}

	@Override
	public void endHeading() {
		this.heading.setBegin(getLocator().getLineDocumentOffset());
		this.heading.setEnd(heading.getBegin()+getLocator().getLineLength());
		this.headings.add(heading);
		this.heading = null;
	}
	
	@Override
	public List<ExtractedHeading> getHeadings() {
		return headings;
	}
}
