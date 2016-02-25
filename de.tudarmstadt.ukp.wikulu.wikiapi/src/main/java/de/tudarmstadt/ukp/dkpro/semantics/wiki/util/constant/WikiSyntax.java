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
package de.tudarmstadt.ukp.dkpro.semantics.wiki.util.constant;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an enumeration for various Wiki syntaxes.<br />
 * Languages, which are currently supported:
 * <ul>
 * <li>MEDIAWIKI (alias: <code>MediaWiki</code>)</li>
 * <li>TWIKI (alias: <code>TWiki</code>)</li>
 * </ul> 
 * @author Fabian L. Tamin
 *
 */
public enum WikiSyntax {
	/**
	 * Represents MediaWiki syntax.
	 */
	MEDIAWIKI, 
	/**
	 * Represents TWiki syntax.
	 */
	TWIKI;
	
	/**
	 * This is a mapping for Wiki syntax alias to this enumeration.  
	 */
	private static Map<String, WikiSyntax> aliasMap = new HashMap<String, WikiSyntax>();
	
	/**
	 * Initializes mapping of aliases to their enumeration representation.
	 */
	private static void makeMap() {
		aliasMap = new HashMap<String, WikiSyntax>(2);
		aliasMap.put("MediaWiki", MEDIAWIKI);
		aliasMap.put("TWiki", TWIKI);
	}
	
	/**
	 * Gets enumeration value by alias.
	 * @param alias enumeration alias
	 * @return Wiki syntax enumeration
	 */
	public static WikiSyntax valueByAlias(String alias) {
		makeMap();
		WikiSyntax w = aliasMap.get(alias);
		if (w != null) {
			return w;
		} else {
			throw new IllegalArgumentException("No enum const has alias "+alias);
		}
	}
}
