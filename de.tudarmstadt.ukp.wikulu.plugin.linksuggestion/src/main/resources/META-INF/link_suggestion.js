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
var pluginIdGlobal;

function suggestLinks(pluginid) {
	setBusy(true);
	pluginIdGlobal = pluginid;
	var jsonString = ' { "class" : "'+pluginid+'" , "arguments" : { "url" : "' + encodeURIComponent(location.href) + '" }, "authData" : "'+WikiAdapter.getAuthJSONObject()+'" } ';
	Wikulu.perform(jsonString, showSuggestedLinks);

}

function confirmLinkCandidate(linkCandidate, word, wordNumber){
	var jsonString = ' { "class" : "'+pluginIdGlobal+'" , "authData" : "'+WikiAdapter.getAuthJSONObject()+'", "arguments" : { "location" : "' + location.href + '" , "candidate" : "' + linkCandidate + '", "word" : "' + word + '" , "wordnumber" : "' + wordNumber + '"} } ';
	Wikulu.perform(jsonString, {
		  callback:function() { location.reload(true); }
	});
}

function showSuggestedLinks(words) {
	setBusy(false);
	var json = eval('(' + words +')');
	if(json == null) {
		alert("Anchor Links were not found!");
		return;
	}
	var jsonWords = json.anchors;
	
	
	$(WikiAdapter.contentIdentifier).removeAddedImage();
	//add image to each word in words
	//for (var i=0;i<words.length;i++)
	for (var i=0;i<jsonWords.length;i++)
	{
		//var upperCaseWords = jsonWords[i].toUpperCase();
		$(WikiAdapter.contentIdentifier).each(function() { jQuery.addTextImage(this, jsonWords[i]); });
		image_link_num = -1;
		//$(WikiAdapter.contentIdentifier).each(function() { $.highlight(this, upperCaseWords); });
	}
	$(WikiAdapter.contentIdentifier).removeImageFromLink();
	
	//TODO: this is hack for demo, removes second plus problem
	$(".wikiSuggestLink").find(".wikiSuggestLink").removeClass("wikiSuggestLink").children().removeClass();
	
	
	//add click event
		$(".wikiSuggestLink").click(function(e){
			//1) Link, intern, extern?
			//2) suchwort
			//3) position
			createSugLinksDialog($(this),e);
		});
	
	$(".wikiLink").hover(
				  function () {
				    $(this).parent().find(".wikiLinkText").addClass("wikiSuggestLinkHover");
				  },
				  function () {
				    $(this).parent().find(".wikiLinkText").removeClass("wikiSuggestLinkHover");
				  }
				);
	
}



