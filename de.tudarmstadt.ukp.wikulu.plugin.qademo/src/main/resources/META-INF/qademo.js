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
var qa_demo_params = "";

var pluginIdGlobal;

function qademo(pluginid) {
	var id = WikiAdapter.contentIdentifier;
	var title = WikiAdapter.titleIdentifier;
	pluginIdGlobal = pluginid;

	var fontLink = $("<link>");
	fontLink.attr({
		type : 'text/css',
		rel : 'stylesheet',
		href : 'http://fonts.googleapis.com/css?family=Merriweather'
	});

	$("head").append(fontLink);

	$(title).text("QA Demo");

	$(id).empty();

	var strVar = "";
	strVar += "<div align=\"center\" style=\"\">";
	strVar += "	";
	strVar += "	<div style=\"border-width:0px; border-style:solid;border-color: #79B7E7;padding: 0.5em; width: 490px; position:relative\">";
	strVar += "		";
	strVar += "		<div id=\"randomQuestion\" style=\"\"><p id=\"qademo\">QA Demo<\/p><img src=\"\/ukp_static\/img\/qademo\/help.png\"\/><\/div>";
	strVar += "		<div class=\"qafont qaSampleQuestions\" style=\"font-size:26px;position: absolute; top: 180px;left : -10px;\">Who is Aristotle?<\/div>";
	strVar += "		<div class=\"qafont qaSampleQuestions\" style=\"font-size:16px;position: absolute; top: 120px;left : 320px;\">What is mashup?<\/div>";
	strVar += "		<div class=\"qafont qaSampleQuestions\" style=\"font-size:20px;position: absolute; top: 160px;left : 320px;\">What is dvd ripper?<\/div>";
	strVar += "		<div class=\"qafont qaSampleQuestions\" style=\"font-size:13px;position: absolute; top: 120px;left : -10px;\">How long can polar bears live?<\/div>";
	strVar += "		<div class=\"qafont qaSampleQuestions\" style=\"font-size:12px;position: absolute; top: 60px;left : 220px;\">Why do people like Subway sandwiches?<\/div>";
	strVar += "		";
	strVar += "        <\/br>";
	strVar += "		<\/br>";
	strVar += "        <div class=\"qa ui-widget\" >";
	strVar += "			<form name=\"qa\" action=\"\">";
	strVar += "				<p class=\"qafont\" style=\"font-size: 16px\"> Question:";
	strVar += "					<input id=\"questionbox\" style=\"width: 300px;font-family: 'Merriweather'\"\/>";
	strVar += "					<input id=\"answerbutton\" type=\"button\" value=\"answer\" style=\"font-size: 14px; height: 25px; width: 70px;margin-left: 10px\"\/>	";
	strVar += "				<\/p>	";
	strVar += "			<\/form>";
	strVar += "        <\/div>";
	strVar += " ";
	strVar += "		<div class=\"qa\" style=\"width: 500px\">";
	strVar += "			<div id=\"accordion\" > ";
	strVar += "				<h3 style=\"height: 30px; margin-bottom: 0em;\"><a href=\"#\" style=\"font-size: 14px; color: #2E6E9E\">Parameters:<\/a><\/h3> ";
	strVar += "				<div> ";
	strVar += "					<form id=\"qa-params\">";
	strVar += "						<p class=\"qafont\" id=\"data_set\"> <b>Data sets:<\/b> Yahoo <input id=\"yaxml\" name=\"dataset\" type=\"checkbox\" value=\"yaxml\"\/>  Answerbag <input name=\"dataset\" type=\"checkbox\" value=\"answerbag\"\/> FAQ <input name=\"dataset\" type=\"checkbox\" value=\"faq\"\/>  Wikipedia <input name=\"dataset\" type=\"checkbox\" value=\"wikipedia\" checked \/> PPT <input name=\"dataset\" type=\"checkbox\" value=\"ppt\"\/> <\/p>";
	strVar += "";
	strVar += "						<p class=\"qafont\"> <b>Search fields:<\/b> Question\/Title <input name=\"sfields\" type=\"checkbox\" value=\"Question\" checked \/> Answer\/Document <input name=\"sfields\" type=\"checkbox\" value=\"Answer\"\/><\/p>";
	strVar += "						<p class=\"qafont\"> <b>Number of Answers:<\/b> <select name=\"num_answers\"><option value=\"1\">1<\/option><option value=\"2\">2<\/option><option value=\"3\">3<\/option><option value=\"10\">10<\/option><option value=\"15\">15<\/option><option value=\"20\">20<\/option><option value=\"30\">30<\/option><\/select><\/p>";
	strVar += "							<p class=\"qafont\"> <b>Output intermediate results:<\/b> <input id=\"output\" type=\"checkbox\" name=\"output\" value=\"true\" \/>";
	strVar += "";
	strVar += "					<\/form> ";
	strVar += "				<\/div> ";
	strVar += "			<\/div>";
	strVar += "";
	strVar += "		<\/div>";
	strVar += "		";
	strVar += "	<\/div>";
	strVar += "		<div>";
	strVar += "			<div id=\"answerBox\" > ";
	strVar += "				<h3 class=\"qafont\" style=\"font-size: 14px;color: #2E6E9E;height: 20px\">Answers:<\/h3>";
	strVar += "				<div align=\"justify\" id=\"answers\"> ";
	strVar += "";
	strVar += "				<\/div> ";
	strVar += "			<\/div>";
	strVar += "";
	strVar += "		<\/div>";
	strVar += "<\/div>";

	$(id).append(strVar);

	$(function() {

		$("#accordion").accordion({
			collapsible : true
		});
		$("#answerbutton").button();

	});

	$("#yaxml").click(function() {
		if($('#yaxml').attr('checked')) {
			$("#data_set").append("<input id=\"hid_ya\" name=\"dataset\" type=\"hidden\" value=\"yatxt\"\/>");
		}else {
			$("#hid_ya").remove();
		}
		
		
	});
	
	$("#qa-params input[name=sfields]").click(function() {
		if($("#qa-params input[name=sfields]:checked").length < 1) {
			$(this).attr('checked', true);
		} 
		
		
	});
	
	$("#qa-params input[name=dataset]").click(function() {
		if($("#qa-params input[name=dataset]:checked").length < 1) {
			$(this).attr('checked', true);
		} 
		
		
	});
	
	
	
	
	$("#answerbutton").click(function() {

		sendQuestion();

	});

	$('#qa-params').submit(function() {
		var params =JSON.stringify($(this).serializeObject());
		console.log(params);
		qa_demo_params = params;
		return false;
	});

	$(".qaSampleQuestions").click(function() {

		$("#questionbox").val($(this).text());

	});

}

function sendQuestion() {
	setBusy(true);
	var question = $("#questionbox").val();

	$("#qa-params").submit();
	
	
	
	var jsonString = '{"class" : "'+pluginIdGlobal+'",'
			+ ' "arguments" : { "question" : "' + encodeURIComponent(question)
			+ '", "params" : '+qa_demo_params+'  }}';

	console.log(jsonString);
			
	Wikulu.perform(jsonString, showAnswers);
}

$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = [ this.value ];
		}
	});
	return o;
};

function showAnswers(answerHtml) {
	setBusy(false);
	var id = WikiAdapter.contentIdentifier;
	var body = $(answerHtml.match(/<body[\s\S]*?>([\s\S]*?)<\/body>/i)[1]);
	
	$("#answers").append(body);
	//$(id).append("<ul id=\"qaAnswers\">");
//	for ( var i = 0; i < answers.length; i++) {
//		$("#qaAnswers").append("<li>" + answers[i] + "</li>");
//	}
}




function findClass(e, cls) {
  if (typeof e == 'string') e = document.getElementById(e);
  if (e.className == '')
    return false;
  return new RegExp('\\b' + cls + '\\b').test(e.className);
}
function removeClass(e, cls) {
  if (typeof e == 'string') e = document.getElementById(e);
  if (e && findClass(e, cls))
    e.className = e.className.replace((e.className.indexOf(' ' + cls) >= 0 ? ' ' + cls : cls), '');
}
function addClass(e, cls) {
  if (typeof e == 'string') e = document.getElementById(e);
  if (e && !findClass(e, cls))
    e.className += (e.className ? ' ' : '') + cls;
}
function replaceClass(e, clsSource, clsReplace) {
  if (typeof e == 'string') e = document.getElementById(e);
  removeClass(e, clsSource);
  addClass(e, clsReplace);
}
function toggleClass(e, clsOn, clsOff) {
  if (typeof e == 'string') e = document.getElementById(e);
  if (findClass(e, clsOn))
    replaceClass(e, clsOn, clsOff);
  else
    replaceClass(e, clsOff, clsOn);
}
function replaceClasses(p, tag, cls, clsSource, clsReplace) {
  var allTags = p.getElementsByTagName(tag);
  for (var i = 0; i < allTags.length; i++)
    if (findClass(allTags[i], cls))
      replaceClass(allTags[i], clsSource, clsReplace);
}
function toggleClasses(p, tag, cls, clsOn, clsOff) {
  var allTags = p.getElementsByTagName(tag);
  for (var i = 0; i < allTags.length; i++)
    if (findClass(allTags[i], cls))
      toggleClass(allTags[i], clsOn, clsOff);
}

function toggleView(eid) {
  replaceClass('viewListItem_textView', 'enabled', 'disabled');
  replaceClass('viewListItem_listView', 'enabled', 'disabled');
  replaceClass('viewListItem_linkView', 'enabled', 'disabled');
  replaceClass('viewListItem_' + eid, 'disabled', 'enabled');
  replaceClass('textView', 'shown', 'hidden');
  replaceClass('listView', 'shown', 'hidden');
  replaceClass('linkView', 'shown', 'hidden');
  replaceClass(eid, 'hidden', 'shown');
}

function toggleTopic(eid) {
  toggleClass('topicListItem' + eid, 'enabled', 'disabled');
  toggleClass('topicHeaderItem' + eid, 'shown', 'hidden');
  var p = document.getElementById('textView');
  toggleClasses(p, 'span', 'sentenceTopic' + eid, 'shown', 'hidden');
  p = document.getElementById('listView');
  toggleClasses(p, 'span', 'nGramTopic' + eid, 'shown', 'hidden');
  p = document.getElementById('linkView');
  toggleClasses(p, 'span', 'linkTopic' + eid, 'shown', 'hidden');
}
function expandAllTopics() {
  var p = document.getElementById('topicList');
  replaceClasses(p, 'li', 'topicListItem', 'disabled', 'enabled');
  p = document.getElementById('topicHeader');
  replaceClasses(p, 'span', 'topicHeaderItem', 'hidden', 'shown');
  p = document.getElementById('textView');
  replaceClasses(p, 'span', 'sentenceTopic', 'hidden', 'shown');
  p = document.getElementById('listView');
  replaceClasses(p, 'span', 'nGramTopic', 'hidden', 'shown');
  p = document.getElementById('linkView');
  replaceClasses(p, 'span', 'linkTopic', 'hidden', 'shown');
}
function collapseAllTopics() {
  var p = document.getElementById('topicList');
  replaceClasses(p, 'li', 'topicListItem', 'enabled', 'disabled');
  p = document.getElementById('topicHeader');
  replaceClasses(p, 'span', 'topicHeaderItem', 'shown', 'hidden');
  p = document.getElementById('textView');
  replaceClasses(p, 'span', 'sentenceTopic', 'shown', 'hidden');
  p = document.getElementById('listView');
  replaceClasses(p, 'span', 'nGramTopic', 'shown', 'hidden');
  p = document.getElementById('linkView');
  replaceClasses(p, 'span', 'linkTopic', 'shown', 'hidden');
}

function toggleSource(eid) {
  toggleClass('sourceListItem_' + eid, 'enabled', 'disabled');
  var p = document.getElementById('textView');
  toggleClasses(p, 'span', 'sentenceSource_' + eid, 'shown', 'hidden');
  p = document.getElementById('listView');
  toggleClasses(p, 'span', 'nGramSource_' + eid, 'shown', 'hidden');
  p = document.getElementById('linkView');
  toggleClasses(p, 'span', 'linkSource_' + eid, 'shown', 'hidden');
}
function expandAllSources() {
  var p = document.getElementById('sourceList');
  replaceClasses(p, 'li', 'sourceListItem', 'disabled', 'enabled');
  p = document.getElementById('textView');
  replaceClasses(p, 'span', 'sentenceSource', 'hidden', 'shown');
  p = document.getElementById('listView');
  replaceClasses(p, 'span', 'nGramSource', 'hidden', 'shown');
  p = document.getElementById('linkView');
  replaceClasses(p, 'span', 'linkSource', 'hidden', 'shown');
}
function collapseAllSources() {
  var p = document.getElementById('sourceList');
  replaceClasses(p, 'li', 'sourceListItem', 'enabled', 'disabled');
  p = document.getElementById('textView');
  replaceClasses(p, 'span', 'sentenceSource', 'shown', 'hidden');
  p = document.getElementById('listView');
  replaceClasses(p, 'span', 'nGramSource', 'shown', 'hidden');
  p = document.getElementById('linkView');
  replaceClasses(p, 'span', 'linkSource', 'shown', 'hidden');
}

function toggleDetail(baseCls, eid) {
  toggleClass(baseCls + eid, 'shown', 'hidden');
}
function expandAllDetails() {
  var p = document.getElementById('textView');
  replaceClasses(p, 'div', 'sentenceDetail', 'hidden', 'shown');
  p = document.getElementById('listView');
  replaceClasses(p, 'div', 'nGramDetail', 'hidden', 'shown');
  p = document.getElementById('linkView');
  replaceClasses(p, 'div', 'linkDetail', 'hidden', 'shown');
}
function collapseAllDetails() {
  var p = document.getElementById('textView');
  replaceClasses(p, 'div', 'sentenceDetail', 'shown', 'hidden');
  p = document.getElementById('listView');
  replaceClasses(p, 'div', 'nGramDetail', 'shown', 'hidden');
  p = document.getElementById('linkView');
  replaceClasses(p, 'div', 'linkDetail', 'shown', 'hidden');
}

function getStyleClass(cls) {
  for (var s = 0; s < document.styleSheets.length; s++) {
    if(document.styleSheets[s].rules) {
      for (var r = 0; r < document.styleSheets[s].rules.length; r++)
        if (document.styleSheets[s].rules[r].selectorText == '.' + cls)
          return document.styleSheets[s].rules[r];
    } else 
    if(document.styleSheets[s].cssRules) {
      for (var r = 0; r < document.styleSheets[s].cssRules.length; r++)
        if (document.styleSheets[s].cssRules[r].selectorText == '.' + cls)
          return document.styleSheets[s].cssRules[r];
    }
  }
  return null;
}
function toggleHighlights() {
  toggleClass(this, 'enabled', 'disabled');
  var isActive = findClass(this, 'enabled');
  var c = getStyleClass('highlight1');
  if (c) c.style.background = (isActive ? 'yellow' : 'transparent');
  c = getStyleClass('highlight2');
  if (c) c.style.background = (isActive ? 'magenta' : 'transparent');
  c = getStyleClass('highlight3');
  if (c) c.style.background = (isActive ? 'lime' : 'transparent');
}



