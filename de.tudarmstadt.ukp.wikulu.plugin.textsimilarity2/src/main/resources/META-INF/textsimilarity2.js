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
var textSimPluginID;

function showDemo(pluginid)
{
	textSimPluginID = pluginid;
	
	setBusy(true);

	// Load Boostrap
	$.getScript("/ukp_static/js/lib/bootstrap/js/bootstrap.min.js")
		.done(function(script, textStatus) {
			$("<link/>", {
			   rel: "stylesheet",
			   type: "text/css",
			   href: "/ukp_static/js/lib/bootstrap/css/bootstrap.min.css"
			}).appendTo("head");
			$.getScript("/ukp_static/js/lib/jquery.tablesorter.min.js");
		//alert("hooray");
		})
		.fail(function(jqxhr, settings, exception) {
			alert("Failed to load Bootstrap!");
		});

	// Main Configuration Panel
	
	var d = "<h2>DKPro Similarity Demo</h2>";
	
	d += '<form id="textsimilarity">';
	d += '<fieldset>';
	d += '<legend>Configuration</legend>';
	
	d += '<table>';
	d += '<tr>';
	d += '<td style="width:170px;">Choose a dataset:</td>';
	d += '<td><select id="dataset" style="width:400px; outline:none;">';
	d += '<option value="li" onclick="$(\'#freetext\').hide();">30 Sentence Pairs (Li et al., 2006)</option>';
	d += '<option value="semeval-2012-msrpar" onclick="$(\'#freetext\').hide();">SemEval-2012 MSRpar (Agirre et al., 2012)</option>';
	d += '<option value="semeval-2012-msrvid" onclick="$(\'#freetext\').hide();">SemEval-2012 MSRvid (Agirre et al., 2012)</option>';
	d += '<option value="semeval-2012-onwn" onclick="$(\'#freetext\').hide();">SemEval-2012 ON-WN (Agirre et al., 2012)</option>';
	d += '<option value="custom" onclick="$(\'#freetext\').show();">Free Text</option>';
	d += '</select></td>';
	d += '</tr>';
	
	d += '<tr id="freetext">';
	d += '<td></td>';
	d += '<td><textarea rows="5" id="text1" style="min-width:400px; width:400px;" placeholder="Enter text 1"></textarea>&nbsp;&nbsp;&nbsp;';
	d += '<textarea rows="5" style="min-width:400px; width:400px;" id="text2" placeholder="Enter text 2"></textarea><br>';
	d += '<span class="help-inline">Pick an example:</span>&nbsp;&nbsp;&nbsp;';
	d += '<select id="example" style="width:400px; outline:none;">';
	d += '<option value="none" onclick="selectExample(0);" selected></option>';
	d += '<option value="example2" onclick="selectExample(1);">A gem is a jewel or stone that is used in jewellery</option>';
	d += '<option value="example1" onclick="selectExample(2);">A girl is styling her hair</option>';	
	d += '</select>';
	d += '</td></tr>';
	
	d += '<tr>';
	d += '<td>Choose a measure:</td>';
	d += '<td><select id="measure" style="width:400px; outline:none;">';
	d += '<optgroup label="String-based Similarity Measures">';
	d += '<option value="GreedyStringTiling">Greedy String Tiling</option>';
	d += '<option value="LongestCommonSubsequence">Longest Common Subsequence</option>';
	d += '<option value="LongestCommonSubstring">Longest Common Substring</option>';
	d += '<option value="Jaro">Jaro Distance</option>';
	d += '<option value="JaroWinkler">Jaro-Winkler Distance</option>';
	d += '<option value="Levenshtein">Levenshtein Distance</option>';
	d += '<option value="WordNGramContainment_2">Word n-grams (Containment, 2)</option>';
	d += '<option value="WordNGramContainment_3">Word n-grams (Containment, 3)</option>';
	d += '<option value="WordNGramContainment_4">Word n-grams (Containment, 4)</option>';
	d += '<option value="WordNGramContainment_5">Word n-grams (Containment, 5)</option>';
	d += '<option value="WordNGramJaccard_2">Word n-grams (Jaccard, 2)</option>';
	d += '<option value="WordNGramJaccard_3">Word n-grams (Jaccard, 3)</option>';
	d += '<option value="WordNGramJaccard_4">Word n-grams (Jaccard, 4)</option>';
	d += '<option value="WordNGramJaccard_5">Word n-grams (Jaccard, 5)</option>';
	//d += '<option value="levenshtein">Levenshtein</option>';
	d += '</optgroup>';
	d += '<optgroup label="Semantic Similarity Measures">';
	d += '<option value="ESA_Wiktionary">Explicit Semantic Analysis (Wiktionary)</option>';
	d += '<option value="ESA_WordNet">Explicit Semantic Analysis (WordNet)</option>';
	d += '</optgroup>';
	d += '<optgroup label="Structural Similarity Measures">';
	d += '<option value="StopwordNGramContainment_3">Stopword n-grams (Containment, 3)</option>';
	d += '<option value="StopwordNGramContainment_4">Stopword n-grams (Containment, 4)</option>';
	d += '<option value="StopwordNGramContainment_5">Stopword n-grams (Containment, 5)</option>';
	d += '<option value="PosNGramContainment_3">Part-of-speech n-grams (Containment, 3)</option>';
	d += '<option value="PosNGramContainment_4">Part-of-speech n-grams (Containment, 4)</option>';
	d += '<option value="PosNGramContainment_5">Part-of-speech n-grams (Containment, 5)</option>';
	d += '</optgroup>';
	d += '<optgroup label="Stylistic Similarity Measures">';
	d += '<option value="TTR">Type-Token Ratio</option>';
	d += '<option value="SequentialTTR">Sequential Type-Token Ratio</option>';
	d += '<option value="FunctionWordFrequencies">Function Word Frequencies</option>';
	d += '</optgroup>';
	d += '<optgroup label="Phonetic Similarity Measures">';
	d += '<option value="DoubleMetaphone">Double Metaphone</option>';
	d += '<option value="Soundex">Soundex</option>';
	d += '</optgroup>';
	d += '</select></td>';
	d += '</tr></table>';
	
	d += "<div style='padding-top:30px;padding-left:5px;padding-bottom:20px;'>";
	
	d += '<a id="btnRun" href="javascript:run();" class="btn btn-primary" style="text-decoration:none; outline:none; display:inline;"><i class="icon-play icon-white"></i> Run</a> ';
	
	d += '<a id="btnReset" href="javascript:reset();" class="btn" style="text-decoration:none; outline:none; display:inline;">Reset</a>';
	
	d += '<div class="progress progress-striped active input-mini" id="progress" style="margin-left:170px;margin-top:-20px;width:200px;">';
	d += '<div class="bar" style="width:100%;"></div>';
	d += '</div>';
		
	d += "</div>";
	
	d += '</fieldset>';
	d += '</form>';
	
	d += '<div id="results"></div>';
	
	d += "<div style='padding:50px'></div>";
	
	$(WikiAdapter.contentIdentifier).html(d);
	
	$("#freetext").hide();
	$("#progress").hide();
	
	setBusy(false);
	
}

function run()
{
	$("#progress").show();
	$("#btnRun").addClass("disabled");
	$("#btnReset").addClass("disabled");
	
	if ($("#dataset").val() == "custom")
	{
		runFreeText($("#text1").val(), $("#text2").val(), $("#measure").val(), showResults);
	} else {
		runOnDataset($("#dataset").val(), $("#measure").val(), showResults);
	}
}

function runFreeText(text1, text2, measure, callback)
{
	var json = '{ "class" : "' + textSimPluginID + '", ' +
		'"arguments" : { ' +
			'"text1" : "' + text1 + '" , ' +
			'"text2" : "' + text2 + '" , ' +
			'"measure" : "' + measure + '" }}';
	
	Wikulu.perform(json, callback);
}

function runOnDataset(dataset, measure, callback)
{
	var json = '{ "class" : "' + textSimPluginID + '", ' +
		'"arguments" : { ' +
			'"dataset" : "' + dataset + '" , ' +
			'"measure" : "' + measure + '" }}';
	
	Wikulu.perform(json, callback);
}

function showResults(html)
{
	if(html == null) {
		alert("No results available!");
		return;
	}
	
	var d = '<legend>Results</legend>';
	
	$("#results").html(d + html);
	$("table#results").tablesorter({ sortList: [[4,1]] });
	
	setBusy(false);
	$("#progress").hide();
	$("#btnRun").removeClass("disabled");
	$("#btnReset").removeClass("disabled");
}

function selectExample(id)
{
	if (id == 0)
	{
		$("#text1").val("");
		$("#text2").val("");
	}
	else if (id == 1)
	{
		$("#text1").val("A gem is a jewel or stone that is used in jewellery.");
		$("#text2").val("A jewel is a precious stone used to decorate valuable things that you wear, such as rings or necklaces.");
	}
	else if (id == 2)
	{
		$("#text1").val("A girl is styling her hair.");
		$("#text2").val("The woman brushes the hair.");
	}
}

function reset()
{
	$("#dataset").val("li");
	$("#freetext").hide();
	$("#measure").val("greedystringtiling");
	$("#results").html("");
	$("#text1").val("");
	$("#text2").val("");
	$("#example").val("none");
}
