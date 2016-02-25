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
function createMenu() {
	var menu = new YAHOO.widget.Panel("ukpmenu", {
		close:false,
		constraintoviewport: false,
		visible:true,
		draggable:false,
		underlay:"shadow",
		zIndex: 5000
	});
	
	menu.setHeader("<div class=\"title\">Wikulu</div><div id=\"ukp_busy_indicator\" />");
	menu.setBody("<div id=\"ukp_menu_button_area\"></div>");
//			"<div id=\"ukp_menu_search\">" +
//			"<form action=\"javascript:search(document.getElementById('ukp_menu_search_query').value)\">" +
//			"<input id=\"ukp_menu_search_query\" type=\"text\" size=\"18\" />" +
//			"<button type=\"submit\">search</button>" +
//			"</form>" +
//			"</div>");
	
	// create buttons
	var keyphraseExtractionbutton = new YAHOO.widget.Button({
		id: "keyphraseExtractionButton",
		type :"push",
		label :"Highlight Keyphrases",
		container :"ukp_menu_button_area",
		onclick: {fn: extractKeyphrases }
	});
	
	var topicSegmentationButton = new YAHOO.widget.Button({
		id: "topicSegmentationButton",
		type :"push",
		label :"Suggest Sections",
		container :"ukp_menu_button_area",
		onclick: {fn: extractTopicSegments }
	});
	
	var summarizeButton = new YAHOO.widget.Button({
		id: "summarizeButton",
		type :"push",
		label :"Summarize",
		container :"ukp_menu_button_area",
		onclick: {fn: summarize}
	});
	
	var suggestLinksButton = new YAHOO.widget.Button({
		id: "suggestLinksButton",
		type :"push",
		label :"Suggest Links",
		container :"ukp_menu_button_area",
		onclick: {fn: suggestLinks}
	});
	
	var extendedSearchButton = new YAHOO.widget.Button({
		id: "extendedSearchButton",
		type: "push",
		label: ">>",
		container: "ukp_menu_button_area",
		onclick: {fn: showExtendedSearchArea}
	})
	
	menu.render(document.body);
	
	// hide the busy indicator
	setBusy(false);
}

function createResultArea() {
	var results = new YAHOO.widget.Panel("ukpresults", {
		close:true,
		constraintoviewport:true,
		visible:false,
		draggable:true,
		zIndex:5000,
		underlay:"none"
	});

	results.setHeader("");
	results.setBody("");
	results.render(document.body);
	return results;
}

/**
 * Hides or shows the busy indicator depending on the flag
 * 
 * @param flag	true to show, false to hide
 */
function setBusy(flag) {
	if (flag == true)
	{
		$("#ukp_busy_indicator").show();
	}
	else
	{
		$("#ukp_busy_indicator").hide();
	}
}