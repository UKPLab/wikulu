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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.controller;

import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Backlink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.ExternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Heading;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Image;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.InternalLink;
import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Tag;

/**
 * This is the controller interface between back-end and front-end components.
 * @author Fabian L. Tamin
 *
 */
public interface WikiController {
	
	/**
	 * Gets Wiki syntax.
	 * @return Wiki syntax.
	 */
	public String getWikiSyntax();

	/**
	 * Gets the first paragraph of filtered HTML text for main summary.
	 * @return the first paragraph of filtered HTML text for main summary.
	 */
	public String getArticleText();
	
	/**
	 * Gets document URI (article URL).
	 * @return document URI.
	 */
	public String getDocumentUri();
	
	/**
	 * Gets Wiki API URI configuration.
	 * @return Wiki API URI.
	 */
	public String getWikiApiUri();
	
	/**
	 * Gets list of annotated headings.
	 * @return list of annotated headings.
	 */
	public List<Heading> getHeadings();
	
	/**
	 * Gets list of annotated images.
	 * @return list of annotated images.
	 */
	public List<Image> getImages();
	
	/**
	 * Gets list of annotated internal links.
	 * @return list of annotated internal links.
	 */
	public Set<LinkSetElementCapsule<InternalLink>> getInternalLinks();
	
	/**
	 * Gets list of annotated external links.
	 * @return list of annotated external links.
	 */
	public Set<LinkSetElementCapsule<ExternalLink>> getExternalLinks();
	
	/**
	 * Gets list of annotated backlinks.
	 * @return list of annotated backlinks.
	 */
	public Set<LinkSetElementCapsule<Backlink>> getBacklinks();
	
	/**
	 * Gets list of annotated tags.
	 * @return list of annotated tags.
	 */
	public Set<LinkSetElementCapsule<Tag>> getTags();
}
