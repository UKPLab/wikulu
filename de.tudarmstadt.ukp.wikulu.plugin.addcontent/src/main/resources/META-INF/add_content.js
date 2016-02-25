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

function search(query) {
	if (query == "")
		return;
	var jsonString = '{ "class" : "'+pluginidGlobal+'" , "arguments" : { "query" : "' + encodeURIComponent(query) + '" } }';
	Wikulu.perform(jsonString, showExtendedSearchResults);
}

function extendedSearch(query) {
	if (query == "")
		return;
		
	// only search if the a word was typed completely to reduce load
	if ((query.charAt(query.length-1) == " ")
			|| query.charAt(query.length-1) == ".")
	{
		setBusy(true);
		var jsonString = '{ "class" : "'+pluginidGlobal+'" , "arguments" : { "query" : "' + encodeURIComponent(query) + '" } }';
		Wikulu.perform(jsonString, showExtendedSearchResults);
	}
}

function showExtendedSearchArea(pluginid) {
	pluginidGlobal = pluginid;
	id = WikiAdapter.contentIdentifier;
	
	$(id).empty();
	
	$(id).append("<div id=\"wikulu_content\" />");

	$("#wikulu_content").append("<div id=\"wikulu_extended_search\">" +
			"<h2>Search/Add Content</h2>" +
			"<form name=\"wikulu_extended_search_form\">" +
			"<textarea onKeyUp=\"extendedSearch(document.wikulu_extended_search_form.wikulu_query.value)\" name=\"wikulu_query\" rows=\"10\" cols=\"30\" /><br />" +
			"<button style=\"margin-top:5px\">Create Article</button>" +
			"</form>" +
			"</div>");
	$("#wikulu_content").append("<div id=\"wikulu_extended_search_results\" />");
}

function showSearchResults(matches)
{
	id = WikiAdapter.contentIdentifier;
	
	$(id).empty();
	
	$(id).append("<h2>Wikulu Search Results</h2>");
	$(id).append("<ul />");
	
	var jsonObject = eval('(' + matches + ')');
	var matched = jsonObject.matches;
	
	for(var i=0; i<matched.length;i++) {
		var content = "<li>"+matches[i]+"</li>";
		$(id+" ul").append(content);
	}
}

function showExtendedSearchResults(matches) 
{	
	setBusy(false);
	
	id = "#wikulu_extended_search_results";
	
	$(id).empty();
	
	$(id).append("<h2>Related Pages</h2>");
	
	var jsonObject = eval('(' + matches + ')');
	var matched = jsonObject.matches;
	
	if (matched.length == 0) {
		$(id).append("<p>None found.</p>");
	}
	else
	{
		$(id).append("<ul />");
	
		for (var i=0;i<matched.length;i++)
		{
			var url = matched[i];
			var urlParts = url.split("/");
			var title = urlParts[urlParts.length-1];
			
			var content = "<li><a href="+url+">"+title+"</a></li>";
			$(id+" ul").append(content);
		}
	}
}