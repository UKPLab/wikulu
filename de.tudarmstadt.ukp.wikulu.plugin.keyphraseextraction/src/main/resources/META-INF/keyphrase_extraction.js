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
function extractKeyphrases(pluginClass) {
	setBusy(true);
	
	var jsonString = '{"class" : "' + pluginClass + '" ,' +
		' "arguments" : { "text" : "' + encodeURIComponent(WikiAdapter.getDocumentText()) + '" }}';
	
	Wikulu.perform(jsonString, showKeyphrases);
}

function showKeyphrases(keyphrases) {
	setBusy(false);
		
	var jsonObject = eval('(' + keyphrases + ')');
	var foundKeyphrases = jsonObject.keyphrases;
	var numberOfKeyphrases = foundKeyphrases.length;
	
	if(foundKeyphrases.length == 0) {
		return;
	}
	
	setPluginMenuVisibility(true);
	
	for(var i=0; i<foundKeyphrases.length; i++)
	{
		//matches all keyphrases and one sign after keyphrases(for plural words)
		var re = "\\b(\\s|[^a-zA-Z])" + foundKeyphrases[i] + "([a-zA-Z]|[:punct:]){0,1}\\b";
		
		//replace matched word with highlighted class
		var f = function(match){ return ' <span id="' + i + '" class="keyphrase" style="background-color: yellow;">'+ match.replace(' ', '')+'</span>';}
		
		$(WikiAdapter.contentIdentifier).each(function(){
			replacer(re, f, $(this).get(0));
		});
	}
	
	pluginMenuMakeSlider(1, numberOfKeyphrases, numberOfKeyphrases, numberOfKeyphrasesChanged);
}

function numberOfKeyphrasesChanged(value) {
	$(".keyphrase").each(function(index) {
		if(this.id < value) {
			$(this).css('background-color', 'yellow');
		} else {
			$(this).css('background-color', 'transparent');
		}
	});
}
