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
function summarize(pluginid) {
	setBusy(true);
	
	var jsonString = '{"class" : "'+pluginid+'",' + 
		' "arguments" : { "text" : "'+ encodeURIComponent(WikiAdapter.getDocumentText())+'" }}';
		
    Wikulu.perform(jsonString, showSummary);
}

function showSummary(jsonString) {
	setBusy(false);
	var json = eval('(' + jsonString + ')');
	var summary = json.summary[0];
	var defaultNumber = json.default_number;
	var max = json.maximum_number;
	
	//TODO test this
	
	$(WikiAdapter.contentIdentifier).html("");
	$(WikiAdapter.contentIdentifier).append("<h1>Summary</h2><p>");
	for(var x=0; x<summary.length; x++) {
		if(x < defaultNumber) {
			$(WikiAdapter.contentIdentifier).append('<li id="' + x + '" class="summarySentence" style="visibility:visible;">' + summary[x] + '</li>');
		} else {
			$(WikiAdapter.contentIdentifier).append('<li id="' + x + '" class="summarySentence" style="visibility:hidden;">' + summary[x] + '</li>');
		}
		
	}
	setPluginMenuVisibility(true);
	pluginMenuMakeSlider(1, max, defaultNumber, numberOfSentencesChanged);
	
	$(WikiAdapter.contentIdentifier).append("<br /><br /></p>");
}

function numberOfSentencesChanged(value) {
	// TODO test this
	$(".summarySentence").each(function(index){
		if(this.id < value) {
			// display sentence if not already displayed
			if($(this).css('visibility') == 'hidden') {
				$(this).css('visibility', 'visible');
			}
		} else {
			// "remove" sentence
			if($(this).css('visibility') == 'visible') {
				$(this).css('visibility', 'hidden');
			}
		}
	});
}