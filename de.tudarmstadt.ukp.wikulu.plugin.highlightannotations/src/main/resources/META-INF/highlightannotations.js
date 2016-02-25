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
function activateAnnotatorMenu(pluginid) {
	setBusy(true);
	jsonString = '{"class" : "' + pluginid + '" ,' +
    	' "arguments" : { "text" : "' + encodeURIComponent(WikiAdapter.getDocumentText()) + '" }}';
	Wikulu.perform(jsonString, showAnnotatorMenu);
}

function showAnnotatorMenu(jsonString) {
	setBusy(false);
	json = eval('(' + jsonString + ')');
	var types = json.types;
	var annotations = json.annotations;
	var newText = "";
	var text = $(WikiAdapter.contentIdentifier).html();
	
	// NEW
	var position = 0;
	var substring = "";
	
	setPluginMenuVisibility(true);
	
	// add available types to menu
	for(var x=0; x<types.length; x++) {
		var name = types[x].name;
		var color = types[x].color;
		$("#fixedPluginMenu").append('<li style=\'display: inline; list-style-type: none; margin-right: 20px;\'>' +
			'<input type="checkbox" name="annotationType" value="' + name + 
			'" onclick="highlightAnnotations(\'' + name + '\', \'' + color + '\')">' +
			'<span style=\'background-color:' + color + '\';>' + name + '</span></li>');
	}
	
	for(var y=0; y<annotations.length; y++) {
		var part="";
		var type = annotations[y].typeName;
		var nextWord = annotations[y].word;
		var color = annotations[y].color;
		
		spanBegin = '<span class=\"' + type + '\" style=\"background-color: transparent;\">';
		spanEnd = '</span>';
		wordConst = spanBegin + nextWord + spanEnd;
		
		var tagString = "";
		while(text.indexOf("<") == 0 || (text.indexOf("<") == 1 && text.charAt(0) == " ")) {
			tagString = text.substring(0, text.indexOf(">")+1);
			text = text.substring(text.indexOf(">")+1, text.length);
			newText = newText + tagString;
			tagString = "";
		}
		
		position = text.indexOf(nextWord);
		substring = text.substring(0,position+nextWord.length);
		text = text.substring(position+nextWord.length, text.length); // cut text
		
		substring = substring.replace(nextWord, wordConst);

		newText = newText + substring;
	}
	
	WikiAdapter.setDocumentContent(newText);
}

function highlightAnnotations(typeName, typeColor) {
	var str = '.' + typeName;
	if($(str).css('background-color') == 'transparent') {
		$(str).css('background-color', typeColor);
	} else {
		$(str).css('background-color', 'transparent');
	}
}