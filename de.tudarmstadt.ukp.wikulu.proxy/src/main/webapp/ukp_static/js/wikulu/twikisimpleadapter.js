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
/*
 * Adapter for the TWiki system.
 */

// create WikiAdapter class
if (typeof this['WikiAdapter'] == 'undefined') WikiAdapter = {};

var mainCSS = "/ukp_static/css/style.css";

// set and export content identifier
WikiAdapter.contentIdentifier = ".patternContent";

//set and export title of tha page identifier
WikiAdapter.titleIdentifier = ".twikiContentHeader";

var resultsArea;

WikiAdapter.init = function()
{
	// avoid conflicts with another plugins used $
	// jQuery.noConflict(); 
	
	// dynamically load the correct css file
	loadCSS(mainCSS);
			
	// create the Wikulu menu
	Wikulu.getPluginInformation(extendWikuluMenu);
	
	setBusy(false);
}

function extendWikuluMenu(pData) {
	$("#patternLeftBarContents").prepend("<ul id=\"wikuluMenu\">" +
		"</ul><p> </p><hr/>");
	
	var plugins = new Array();
	for(var i=0; i<pData.length; i++) {
		plugins[i] = eval('(' + pData[i] + ')');
	}
	
	//bublesort for menuitems(there are not so much items in menu(3-7), therefor
	//we can use here bublesort)
	var n = plugins.length -1;
	for(var i=0; i <= n; i++) {
		for(var j=n; j > i; j--) {
			 if (plugins[j-1].priority > plugins[j].priority) {
				 buf = plugins[j];
				 plugins[j] = plugins[j-1];
				 plugins[j-1] = buf;
			 }
		}
	}
		 
	//show menu items
	for(var i=0; i<plugins.length; i++) {
		if(plugins[i].menu == "true") {
			$("#wikuluMenu").append("<li><span style=\"white-space: nowrap;\">" +
					"<a href=\"javascript:void(null)\" onClick=\"perform('" + plugins[i].java_class + "', " + plugins[i].run_method + ");\">" + 
					plugins[i].name + "</a></span></li>");
		}else {
			//perform
			if(plugins[i].run_method != "")
				window[plugins[i].run_method](plugins[i].java_class);
				
		}
	}
	

	
	// this doesn't work properly sometimes...
	var mylist = $("#wikuluMenu");
	// sorting doesn't work
	var listitems = mylist.children('li').get();
	listitems.sort(function(a, b) {
		var compA = $(a).id;
		var compB = $(b).id;
	   return (compA < compB) ? -1 : (compA > compB) ? 1 : 0;
	})
	
	$.each(listitems, function(idx, itm) {$("#wikuluMenu").append(itm);});
	$("#wikuluMenu").prepend("<li><strong>Wikulu</strong><img src=\"/ukp_static/img/ajax-loader.gif\"/ id=\"ukp_simple_busy_indicator\"></li>");
	setBusy(false);
}

/*
 * Content access
 */

/**
 * Extracts the content text using the HTML as basis
 */
WikiAdapter.getDocumentText = function() {
	var text = $(WikiAdapter.contentIdentifier).text();
	return text;
}

WikiAdapter.getHtmlContent = function() {
	var html = $(WikiAdapter.contentIdentifier).html();
	return html;
}

/*
 * Content manipulation
 */
WikiAdapter.setDocumentContent = function(documentContent) {
	$(WikiAdapter.contentIdentifier).html(documentContent);
}

WikiAdapter.getAuthJSONObject = function() {
	var JSESSIONID = $.cookie('JSESSIONID');
	var TWIKISID = $.cookie('TWIKISID');
	var jsonString = 'JSESSIONID=' + JSESSIONID + ';TWIKISID='+ TWIKISID +'';
	return jsonString;
};


/*
 * Helpers
 */ 

/**
 * @returns Current page title
 */
WikiAdapter.getCurrentPageTitle = function() {
	var path_tokens = location.pathname.split("/");
	var title  = path_tokens[path_tokens.length-1];
	return title;
}

function setBusy(flag) {
	if (flag == true)
	{
		$("#ukp_simple_busy_indicator").show();
	}
	else
	{
		$("#ukp_simple_busy_indicator").hide();
	}
}
