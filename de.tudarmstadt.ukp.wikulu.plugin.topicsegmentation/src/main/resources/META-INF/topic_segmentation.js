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
var pluginidGlobal;

function extractTopicSegments(pluginid) {
	setBusy(true);
	//NEW
	pluginidGlobal = pluginid;
	var jsonString = '{ "class" : "'+pluginid+'" , "arguments" : { "text" : "' + encodeURIComponent(WikiAdapter.getHtmlContent()) + '" } }';
	Wikulu.perform(jsonString, showTopicSegmentation);
	
	//OLD	
	// the actual java method getTopicOffsets in the Wikulu class is called
	//Wikulu.getTopicSuggestions(WikiAdapter.getHtmlContent(), showTopicSegmentation);
}

function confirmTopicSegment(offsetInPlainText) {
	// get title
	var titleInputId = "segment_title_"+offsetInPlainText;
	var title = document.getElementById(titleInputId).value;
	
	//NEW
	var jsonString = ' { "class" : "'+pluginidGlobal+'" , "authData" : "'+WikiAdapter.getAuthJSONObject()+'", "arguments" : { "url" : "' + encodeURIComponent(location.href) + '", "offset" : "' + offsetInPlainText + '", "title" : "' + encodeURIComponent(title) + '" } } ';
	Wikulu.perform(jsonString, showTopicSgementConfirmation);
	
	//OLD
	//Wikulu.confirmTopicSegment(location.href, offsetInPlainText, title, showTopicSgementConfirmation);
}

function showTopicSgementConfirmation(success) {
	if(success == "true") {
		// simply reload the page to show success
		window.location.reload();
	}
}

function showTopicSegmentation(content) {
	setBusy(false);
	var jsonContent = eval('(' + content + ')');
	
	for(var i = 0; i < jsonContent.length;i++){
		if(jsonContent[i][0] == null)
			break;
		var patt = new RegExp("\\d+");
		var path =jsonContent[i][0];
		var pos =path.search(/ > text()/);
		var exp =path.substring(0, pos);
		var exp2 =path.substring(pos);
		var num = patt.exec(exp2);
		
		var t3 = exp[1];
		//place to replace
		var change = $(WikiAdapter.contentIdentifier).find(exp).contents().filter(function(){ return this.nodeType == 3; }).eq(num);
		//replace with marker
		change.replaceWith(jsonContent[i][1]);
		// disable topicSegmentationButton
		$("#topicSegmentationButton-button").attr("disabled","disabled");
	}
}