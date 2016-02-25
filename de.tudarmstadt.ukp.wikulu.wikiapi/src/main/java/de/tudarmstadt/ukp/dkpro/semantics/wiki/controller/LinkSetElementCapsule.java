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

import de.tudarmstadt.ukp.dkpro.semantics.wiki.type.Link;

/**
 * This class capsulate annotated link to make it comparable while adding to set.
 * @author Fabian L. Tamin
 *
 */
public class LinkSetElementCapsule<T extends Link> implements Comparable<LinkSetElementCapsule<T>> {

	/**
	 * Capsulated link.
	 */
	private T link;

	/**
	 * Constructs an instance of LinkSetElementCapsule
	 * @param link
	 */
	public LinkSetElementCapsule(T link) {
		this.link = link;
	}
	
	/**
	 * Gets capsulated link.
	 * @return capsulated link
	 */
	public T getLink() {
		return link;
	}

	@Override
	public int compareTo(LinkSetElementCapsule<T> o) {
		return compareLinkText(o);
	}

	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LinkSetElementCapsule<?>) {
			LinkSetElementCapsule<? extends Link> l = (LinkSetElementCapsule<?>) o;
			return (this.compareLinkText(l) == 0);
		} else {
			return false;
		}
	}
	
	/**
	 * Compares link's text of this object with the link's text of object o. Comparison is context insensitive.  
	 * @param o another link set element capsule with contains link object
	 * @return negative value (lower the o's), zero (equal), or positive value (higher than o's)
	 */
	private int compareLinkText(LinkSetElementCapsule<? extends Link> o) {
		return this.link.getText().toLowerCase().compareTo(o.link.getText().toLowerCase());
	}
}
