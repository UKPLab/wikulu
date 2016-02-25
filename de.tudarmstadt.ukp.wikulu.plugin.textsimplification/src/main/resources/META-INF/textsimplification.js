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
function simplify(pluginid) {
	setBusy(true);
	
	var jsonString = '{"class" : "'+pluginid+'",' + 
		' "arguments" : { "text" : "'+ encodeURIComponent(WikiAdapter.getDocumentText())+'" }}';
	
    Wikulu.perform(jsonString, showSimplifiedText);
}

function showSimplifiedText(simpleText) {
	setBusy(false);
	
	$(WikiAdapter.contentIdentifier).html("");
	$(WikiAdapter.contentIdentifier).append("<h1>Simple Text</h2>");
	$(WikiAdapter.contentIdentifier).append("<p>"+simpleText+"<br /><br /></p>");
}