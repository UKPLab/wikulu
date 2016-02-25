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
package de.tudarmstadt.ukp.wikulu;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class provides the interaction with the MediaWiki system. All methods
 * specific to the wiki system like moving/deleting pages, splitting them ...
 * i.e. all methods that need to change the wikis content need to be implemented
 * here.
 * 
 * @author hoffart
 * 
 */
public class MediaWiki extends Wikulu {

	public MediaWiki() throws MalformedURLException {
		String mwWikiURL = config.getString("wikulu.targetPlatformBaseURL") + "" + config.getString("wikulu.targetPlatformWikiURL");
		//String mwAPIURL = config.getString("wikulu.targetPlatformAPIURL");
		wiki = new de.tudarmstadt.ukp.wikiapi.MediaWiki(new URL(this.getTargetPlatformApiUrl()), new URL(mwWikiURL));
	}

	@Override
	public String getTitleInWikiSyntax(String title) {
		return "\n\n=="+title+"==\n\n";
	}
	
	
	@Override
	public String createLink(String word, String linkCandidateName){
		String title = wiki.getTitleForURL(linkCandidateName);
		return "[["+title+"|"+word+"]]";
	}
}
