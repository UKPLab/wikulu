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
function wikifyThis(pluginClass) {
	setBusy(true);
	// json object with text(pure text) to find keyphrases
	var jsonString = '{"class" : "' + pluginClass + '" ,' +
		' "arguments" : { "text" : "' + encodeURIComponent(WikiAdapter.getDocumentText()) + '" }}';
	Wikulu.perform(jsonString, showLinks);
}

function showLinks(keyphrases) {
	setBusy(false);
	
	var jsonObject = eval('(' + keyphrases + ')');
	
	var foundKeyphrases = jsonObject.keyphrases;
	var prefix = jsonObject.prefix;
	var keyphraseCount = jsonObject.count;
	
	var titlesToCheck="";
	var counter = 0;	
	for(var i=0; i<foundKeyphrases.length; i++)
	{
		
		// matches all keyphrases and one sign after keyphrases(for plural
		// words)
		var re = new RegExp("\\b("+foundKeyphrases[i]+"[a-zA-Z]{0,1})\\b", "i");

		var anchor = prefix+foundKeyphrases[i];
		var isReplaced = $(WikiAdapter.contentIdentifier).replaceInDOM(re, function(match){ return ' <a href="'+anchor+'">' + match + '</a>';}, ["a"]);
		if(isReplaced > 0)
			counter++;
		if (counter === keyphraseCount)
			break;
		
	}
}


