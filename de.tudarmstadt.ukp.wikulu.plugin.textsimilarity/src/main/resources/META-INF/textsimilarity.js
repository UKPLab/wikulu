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

function showSimilarityValues(pluginid) {
	pluginIdGlobal = pluginid;
	// create new div to display results etc.
	var newDiv = '<div id="similaritypluginhelper"><table border="0" id="similarities" style="width:50em"></table></div>';
	$(WikiAdapter.contentIdentifier).append(newDiv);
	updateSimilarityMethod("NONE");
}

function updateSimilarityMethod(value) {
	setBusy(true);
	var urlParts = location.href.split('/');
	jsonString = '{"class" : "'+pluginIdGlobal+'",' +
		' "arguments" : { "method" : "' + value + '",' +
		' "url" : "' + urlParts[urlParts.length-2] + '/' + urlParts[urlParts.length-1] + '" ,' +
		'"text" : "' + encodeURIComponent(WikiAdapter.getDocumentText()) + '" }}';
	Wikulu.perform(jsonString, processSimilarityResults);
}

function updateTextSimDatabase(vari) {
	setBusy(true);
	var urlParts = location.href.split('/');
	jsonString = '{"class" : "'+vari+'",' +
		' "arguments" : { "method" : "UPDATE", "url" : "' + urlParts[urlParts.length-2] + '/' + urlParts[urlParts.length-1] + '"}}';
	Wikulu.perform(jsonString, similarityDatabaseAltered);
}

function deleteTextSimDatabase(vari) {
	// TODO pluginIdGlobal is ConfigManager if
	// no other method from TextSim is called before!
	setBusy(true);
	if(confirm("Delete database?")) {
		jsonString = '{"class" : "'+vari+'",' +
			' "arguments" : { "method" : "DELETE",}}';
		Wikulu.perform(jsonString, similarityDatabaseAltered);
	} else {
		setBusy(false);
	}
}

function similarityDatabaseAltered(jsonString) {
	setBusy(false);
	alert(jsonString);
}

function processSimilarityResults(jsonString) {
	setBusy(false);
	var json = eval('(' + jsonString + ')');
	
	// if something went wrong (i.e., the Java class threw an exception),
	// the JSON object might be null
	if(json == null) {
		alert("No results available, please check log for errors!");
		return;
	}
	
	var content = json.content;
	var methods = json.compare_methods;
	var wikiLinkUrl = json.url;
	
	// if this is the first call, generate the dropdown menu
	// and select the default compare method (Cosine)
	if(!$("#comparedropdown").length) {
		// add the dropdown menus to the new div
		var dropdown = '<br />Compare Method: <select id="comparedropdown" onchange="updateSimilarityMethod(this.value);"></select>' +
			'Sort: <select id="sortmethod" onchange="sortComparisonResults(this.value)"></select>';
		$("#similaritypluginhelper").append(dropdown);
		
		for(var x=0; x<methods.length; x++) {
			$("#comparedropdown").append('<option value="' + methods[x] + '">' + methods[x] + '</option>');
		}
		
		// select the default value
		$("#comparedropdown").val("de.tudarmstadt.ukp.relatedness.baseline.RandomBaselineComparator");
		
		// possible sorting methods for the results
		$("#sortmethod").append('<option value="alphabetical_0">alphabetical</option>');
		$("#sortmethod").append('<option value="alphabetical_1">alphabetical reversed</option>');
		$("#sortmethod").append('<option value="score_1">highest first</option>');
		$("#sortmethod").append('<option value="score_0">lowest first</option>');
	}
	
	
	// reset the table and add the new similarity scores
	$('#similarities').html("");
	
	for(var x=0; x<content.length; x++) {
		$('#similarities').append('<tr id="' + content[x].rank + ':' + content[x].alph_rank + '" class="comparisonResult" style="width:100%"><th style="width:50%"><a href="' + wikiLinkUrl + '' + content[x].url +'">' + content[x].url + '</a></th><td style="width:' + parseInt(content[x].similarity) + '%; background-color:orange; display:block; height:100%;">' + parseInt(content[x].similarity) + '%</tr>');
	}
	
	// sort by rank (highest first)
	$("#sortmethod").val("score_1");
	sortComparisonResults("score_1");
	
	var scrollToPos = $("#similaritypluginhelper").offset();
	window.scrollTo(scrollToPos.left, scrollToPos.top);
}

function sortComparisonResults(value) {
	var splitted = value.split("_");
	value = splitted[0];
	var reverse = splitted[1];
	
	var children = $(".comparisonResult");
	var sortArray = new Array(children.size());
	
	if(value == "alphabetical") {
		// get the second part of the id (-> alph_rank)
		for(var x=0; x<children.size(); x++) {
			sortArray[$(children[x]).attr('id').split(':')[1]] = $(children[x]);
		}
		
	} else if(value == "score") {
		// get the first part of the id (-> rank)
		for(var x=0; x<children.size(); x++) {
			sortArray[$(children[x]).attr('id').split(':')[0]] = $(children[x]);
		}
	}
	
	// reset the table
	$('#similarities').html("");
	
	// inverse the array if necessary
	if(reverse == 1) {
		for(var y=children.size()-1; y>=0; y--) {
			$('#similarities').append(sortArray[y]);
		}
	} else {
		for(var y=0; y<children.size(); y++) {
			$('#similarities').append(sortArray[y]);
		}
	}
}