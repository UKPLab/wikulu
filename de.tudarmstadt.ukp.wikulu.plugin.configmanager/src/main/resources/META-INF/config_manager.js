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
//list of all plugins
var pluginList="";
//current choosed plugin to show
var currentPluginId="";

var pluginIdGlobal;
var rawRow = "<tr id='configtableRowUNKNOWN'>";
var waitForTextSimDatabaseUpdate = false;

/**
 * Gets plugin to show
 * @param pluginid
 */
function getAvailableConfigs(pluginId) {
	setBusy(true);
	pluginIdGlobal = pluginId;
	// create JSONString for reading names of all plugins
	var jsonString = '{ "class" : "'+pluginId+'" , "arguments" : { "command" : "read_config", "plugin" : "all"   }   }';
	Wikulu.perform(jsonString, showListOfPlugins);
}

//content of the page, that was replaced
var pageCache = "";
//title of the page, that was replaced
var titleCache = "";

//selector for wiki content
var id = WikiAdapter.contentIdentifier;
//selectot for wiki title
var title = WikiAdapter.titleIdentifier;

/**
 * Show all plugins in list
 * @param plugins list of plugins
 */
function showListOfPlugins(plugins) {
	
	
	plugins = unescape(plugins);
	if(pluginList != plugins) {
		pageCache = $(id).html();
		titleCache = $(title).text();
		
	}
	if(title != "") {
		//title has only mediawiki
		$(title).text("Configuration manager");
	}
	pluginList = plugins;
	
	setBusy(false);
	
	//NEW
	var jsonObject = eval('(' + unescape(plugins) + ')');
	var foundedPlugins = jsonObject.plugins;
	
	$(id).empty();
	
	$(id).append("<div id=\"wikulu_plugin_list\" style=\"margin-left: 10px\" />");
	
	
	for(var i=0; i<foundedPlugins.length; i++)
	{
		$("#wikulu_plugin_list").append("<div><a href=\"javascript:void(null)\" onClick=\"getPluginParameters('" + jsonObject.plugins[i].id + "');\">"+ jsonObject.plugins[i].name +" </a></div>");
	}
	
	//indexcreator -datastore config
	$("#wikulu_plugin_list").append("<div><a href=\"javascript:void(null)\" onClick=\"showDatastoreConfig();\">Datastore</a></div>");
	
	
	// back Button
	$("#wikulu_plugin_list").append("<div style=\"margin-top: 10px\"><a href=\"javascript:void(null)\" onClick=\"backButton();\"> Back </a></div>");
	
	
	//$("#wikulu_content").append("</div>");
}

/**
 * Back button(to the first page)
 */
function backButton() {
	$(id).empty();
	$(id).append(pageCache);
	$(title).text(titleCache);
	
}


/**
 * Show configuration of datastore "plugin"
 */
function showDatastoreConfig() {
	$(id).empty();
	$(title).text("Datastore");
	
	
	
	$(id).append("<div id=\"wikulu_plugin_config\" />");
	
	$("#wikulu_plugin_config").append("<div id=\"wikulu_plugin_properties\" />");
	$("#wikulu_plugin_properties").append("<h2>Datastore: </h2>");
	$("#wikulu_plugin_properties").append("<input type=\"button\" onClick=\"createIndex();\" value=\"CreateIndex\">");
	
	$("#wikulu_plugin_config").append("<div><a href=\"javascript:void(null)\" onClick=\"showListOfPlugins('" + escape(pluginList) + "');\"> Back </a></div>");
	
	
}

function createIndex() {
	Wikulu.createIndex('{ "authData" : "'+WikiAdapter.getAuthJSONObject()+'"}',ack);
}

function ack() {
	alert('Index creation accomplished!');
}



/**
 * Get plugin parameters
 * @param pluginId id of a plugin to read
 */
function getPluginParameters(pluginId) {
	setBusy(true);
	
	currentPluginId = pluginId;
	var jsonString = '{"class" : "'+pluginIdGlobal+'" , "arguments" : { "command" : "read_config", "plugin" : "'+pluginId+'"}}';
	Wikulu.perform(jsonString, showPluginParameters);
}


function showPluginParameters(parameters) {
	setBusy(false);
	
	var jsonObject = eval('(' + parameters + ')');
//	var pluginConf = jsonObject.parameters;
	
	$(id).empty();
	$(title).text(jsonObject.name);
	
	
	$(id).append("<div id=\"wikulu_plugin_config\" />");
	
	
	var isMenu = jsonObject.menu.toString() === 'true' ? true : false;
	 
	// Unit Properties
	$("#wikulu_plugin_config").append("<div id=\"wikulu_plugin_properties\" />");
	$("#wikulu_plugin_properties").append("<h2>Plugin Properties: </h2>");
	$("#wikulu_plugin_properties").append("<form name=\"plugin_properties_form\" onsubmit=\"return changeProperties();\">" +
										  "" +
										  "   <label for=\"p_name\" id=\"p_prop_name_id\">Name : "+jsonObject.name+"</label> <br/>" +
									  	  "   <label for=\"p_id\" id=\"p_prop_id_id\">Plugin Id : "+jsonObject.id+"</label> <br/>" +
									  	  "   <label for=\"p_author\" id=\"p_prop_author_id\">Author :"+jsonObject.author+"</label> <br/>" +
									  	  "   <label for=\"p_version\" id=\"p_prop__version_id\">Version :"+jsonObject.version+"</label> <br/>" +
	
									  	  "   <label for=\"show_menu\" id=\"p_prop_menu_label_id\">Show in menu : </label>" +
									  	  "   <select name=\"p_prop_menu_id\" id=\"p_prop_menu_id\"><option>"+isMenu+"</option><option>"+!isMenu +"</option></select>" +
	
									      "   <br />" +
									      "   <input type=\"button\" onClick=\"changeProperties();\" value=\"Change Value\">" +
									  	  "" +
									  	  "</form>");
	//
	
	if(jsonObject.editable.toString() === 'false')
		$('#p_prop_menu_id').attr('disabled', 'disabled');
	
	// Plugin Parameters
	$("#wikulu_plugin_config").append("<div id=\"wikulu_plugin_parameters\" />");
	$("#wikulu_plugin_parameters").append("<h2>Plugin Parameters: </h2>");
	$("#wikulu_plugin_parameters").append(createParameterView(jsonObject.params));  
    //
	
	// back Button
	$("#wikulu_plugin_config").append("<div><a href=\"javascript:void(null)\" onClick=\"showListOfPlugins('" + escape(pluginList) + "');\"> Back </a></div>");
	
	
	//hide all error labels
	$('.value_errors').hide();
	
}

function createParameterView(paramProp) {
	
	var outHTML ="";
	var func_name="";
	
	for ( var j = 0; j < paramProp.length; j++) {
		outHTML += "<div><h3>Parameter: "+paramProp[j].showname+" </h3></div>";
		outHTML += "<div>Description: "+paramProp[j].description+" </div>";
		
		
		if(paramProp[j].type == "bool") {
			outHTML += "<div> Value: <select  \"name=\"param_"+j+"_value\" >";
			outHTML += "<option value =\""+paramProp[j].value+"\">"+paramProp[j].value+"</option><option value=\""+!paramProp[j].value+"\">"+!paramProp[j].value+"</option></select> </div>"
			func_name ="changeAndSave("+j+",\'"+paramProp[j].name+"\');";
		}else if(paramProp[j].type == "enum") {
			outHTML += "<div>Value: <select name=\"param_"+j+"_value\" >";
			for ( var i = 0; i < paramProp[j].enum_def.length; i++) {
				if(paramProp[j].value == paramProp[j].enum_def[i].value) {
					outHTML += "<option value=\""+paramProp[j].enum_def[i].name+"\" selected>"+paramProp[j].enum_def[i].value+"</option>";
				}
				outHTML += "<option value=\""+paramProp[j].enum_def[i].name+"\">"+paramProp[j].enum_def[i].value+"</option>";
			}
			outHTML += "</select> </div>";
			func_name ="changeAndSave("+j+",\'"+paramProp[j].name+"\');";
		}else if(paramProp[j].type == "int") {
			outHTML += "<div>Value: <input type=\"text\" id=\"param_value_id_"+j+"\" name=\"param_"+j+"_value\" size=\"30\" value=\""+paramProp[j].value+"\" />" +
					   "<div id=\"param_error_id_"+j+"\" class=\"value_errors\" style=\"color: red;\"> Value is not integer!</div>" +
					   " </div>";
			func_name ="checkIntValueAndSave("+j+",\'"+paramProp[j].name+"\');";
		}else if(paramProp[j].type == "string") {
			outHTML += "<div>Value: <input type=\"text\" id=\"param_value_id_"+j+"\" name=\"param_"+j+"_value\" size=\"30\" value=\""+paramProp[j].value+"\" />" +
			  	       " </div>";
			func_name ="changeAndSave("+j+",\'"+paramProp[j].name+"\');";
			
		}else if(paramProp[j].type == "float") {
			outHTML += "<div>Value: <input type=\"text\" id=\"param_value_id_"+j+"\" name=\"param_"+j+"_value\" size=\"30\" value=\""+paramProp[j].value+"\" />" +
			   		   "<div id=\"param_error_id_"+j+"\" class=\"value_errors\" style=\"color: red;\"> Value is not float!</div>" +
			   		   " </div>";
			func_name ="checkFloatValueAndSave("+j+",\'"+paramProp[j].name+"\');";
		} else if(paramProp[j].type == "list") {
			outHTML += buildTableForParameters(paramProp[j].value);
			// buttons for adding algorithms and updating/deleting the database
			outHTML += "<input type=\"button\" onClick=\"addRowToTable()\" value=\"Add Row\">";
			func_name = "checkTableValuesAndSave(" + j + ", \'"+ paramProp[j].name + "\');";
		} else if(paramProp[j].type == "button") {
			outHTML += "<input type=\"button\" title=\"" + paramProp[j].description + "\" onClick=\"" + paramProp[j].value + ";\" value=\"" + paramProp[j].showname + "\">";
		} else {
			outHTML +="<div> Not Valid parameter Type!</div>";
		}
		if(paramProp[j].type != "button"){
			outHTML += "<input type=\"button\" onClick=\""+func_name+"\" value=\"Change Value\">";
		}
		
		
	}
	
	
	return outHTML;
}

function addRowToTable() {
	var tableSize = $("#configtable tr").length - 1;
	var findUnusedId = 1;
	while(findUnusedId == 1) {
		if($("#configtableRow"+tableSize).html() != null) {
			tableSize += 1;
		} else {
			findUnusedId = 0;
		}
	}
	var newTableRow = rawRow.replace(/UNKNOWN/g, tableSize);
	$("#configtable").append(newTableRow);
}

function deleteTableRow(rowId) {
	if($("#configtable tr").length > 2) {
		$(rowId).remove();
	} else {
		alert("The table has to contain at least one row!");
	}
}

function buildTableForParameters(value) {
	// builds table from multiple values
	outHTML = "<table id='configtable'>";
	value = eval('(' + value + ')');
	
	var headerConfig = value.definition;
	value = value.values;
	var columnNumber = 0;
	headerNames = new Array(columnNumber);
	var rowNumber = value.length;
	
	//generate header
	var headerHTML = "<tr>";
	for(var key in headerConfig) {
		headerHTML += "<th>" + key + "</th>";
		headerNames.push(key);
		columnNumber += 1;
	}
	headerHTML += "<th>Delete</th>";
	headerHTML += "</tr>";
	
	// TODO: if no values are given yet,
	// display empty row
	/*if(value.length == 0) {
		
	}*/
	
	// generate rows
	var rowHTML= "";
	for(var y=0; y<value.length;y++) {
		var valueList = value[y];
		rowHTML += "<tr id='configtableRow" + y + "'>";
		for(var x=0;x<columnNumber; x++) {
			if(headerConfig[headerNames[x]].toLowerCase() == "bool") {
				// append checkbox
				rowHTML += '<td><input type="checkbox" name="value' + y + '" value="value' + y + '"';
				if(valueList[x] == "true") {
					rowHTML += ' checked';
				}
				rowHTML += '></td>';
				if(y == 0) {
					rawRow += '<td><input type="checkbox" name="valueUNKNOWN" value="valueUNKNOWN"></td>';
				}
			} else if(headerConfig[headerNames[x]].toLowerCase() == "dropdown") {
				var droppy = '<td><select name="value' + y + '">';
				var selectedVal = "";
				var valAr = valueList[x];
				var buffer = "";
				for(var l=0; l<valAr.length; l++) {
					if(valAr[l].charAt(0) == '!') {
						selectedVal = valAr[l].substring(1);
						droppy += '<option value="' + selectedVal + '">' + selectedVal + '</option>';
					} else {
						buffer += '<option value="' + valAr[l] + '">' + valAr[l] + '</option>';
					}
				}
				droppy += buffer;
				droppy += '</select></td>';
				
				if(y == 0) {
					rawRow += droppy;
				}
				
				rowHTML += droppy;
			} else {
				rowHTML += '<td><input type="text" size="30" value="' + valueList[x] + '"></td>';
				if(y == 0) {
					rawRow += '<td><input type="text" size="30" value=""></td>';
				}
			}
		}
		rowHTML += '<td><input type="button" onClick="deleteTableRow(configtableRow' + y + ')" value="Delete"></td>';
		if(y == 0) {
			rawRow += '<td><input type="button" onClick="deleteTableRow(configtableRowUNKNOWN)" value="Delete"></td>';
			rawRow += '</tr>'
		}
		rowHTML += "</tr>";
	}
	outHTML += headerHTML + rowHTML + "</table>";
	rawRow = rawRow.replace(/\'/g, "\'");
	return outHTML;
}

function checkTableValuesAndSave(id, paramName) {
	// - check if bool==true/false, int=[0-9], float like checkFloat
	// - string -> just save
	// - row length same as column header length
	var headers = new Array();
	var headerTypes = new Array();
	var valuesJson = '"values" : [';
	var tableDefJson = '"definition" : {';
	$("#configtable tr").each(function(index, tr) {
		// i = index
		// tr = HTMLTableRowObject
		if(index == 0) {
			// table header
			$(tr).children("th").each(function(i, th) {
				headers.push($(th).text());
			});
			headers.pop(); // remove last header, it's always "Delete"
		} else {
			// ordinary row
			var jsonElement = "";
			$(tr).children("td").each(function(i,td) {
				var data2 = $(td).children()[0];
				var inType = data2.type;
				if(inType == "checkbox") {
					if(index == 1) {
						// first ordinary row -> save data types
						headerTypes.push("bool");
					}
					if($(data2).attr("checked")) {
						jsonElement = jsonElement + '"true", ';
					} else {
						jsonElement = jsonElement + '"false", ';
					}
				} else if(inType == "button") {
					// do nothing, this is the delete button
				} else if(inType == "select-one") {
					var selc = $(data2).find(":selected").text();
					if(index == 1) {
						headerTypes.push("dropdown");
					}
					var buff = '[';
					$(data2).children().each(function(ind, opt) {
						if($(opt).html() == selc) {
							buff += '"!' + $(opt).html() + '", ';
						} else {
							buff += '"' + $(opt).html() + '", ';
						}
					});
					buff = buff.replace(/\,\s$/g, "");
					buff += ']';

					jsonElement = jsonElement + buff + ', ';
				} else {
					if(index == 1) {
						// first ordinary row -> save data types
						headerTypes.push("string");
					}
					jsonElement = jsonElement + '"' + $(data2).val() + '", ';
				}
			});
			jsonElement = jsonElement.replace(/\,\s$/g, "");
			valuesJson = valuesJson + '[' + jsonElement + '], ';
		}
	});
	valuesJson = valuesJson.replace(/\,\s$/g, "");
	valuesJson = valuesJson + ']';
	for(var x=0; x<headers.length;x++) {
		tableDefJson = tableDefJson + '"' + headers[x] + '" : "' + headerTypes[x] + '"';
		if(x < headers.length-1) {
			tableDefJson = tableDefJson + ", ";
		}
	}
	tableDefJson = tableDefJson + '}';
	outJson = tableDefJson + ', ' + valuesJson;
	outJson = outJson.replace(/\"/g,"'");
	outJson = "{" + outJson +"}";
	// DEBUG
	//alert(tableDefJson);
	//alert(valuesJson);
	//alert(outJson);
	if(confirm("Update database now?")) {
		// yes
		// TODO don't reload until the updating process finished
		//updateTextSimDatabase('de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin');
		waitForTextSimDatabaseUpdate = true;
	}
	// STOPPOINT
	saveValue(paramName, outJson);
}

function changeProperties() {
	setBusy(true);
	
	var menu_val = $("#p_prop_menu_id").val();
	
	
	// create JSONString for write menu
	var jsonString = '{ "class" : "'+pluginIdGlobal+'" , "arguments" : { "command" : "write_config", "plugin" : "'+currentPluginId+'", "paramname" : "menu","paramvalue" : "'+menu_val+'" }   }';
	Wikulu.perform(jsonString, propSaved);
	
}


function propSaved(isWritten) {
	setBusy(false);
	window.location.reload();
	if(waitForTextSimDatabaseUpdate) {
		updateTextSimDatabase('de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin');
		waitForTextSimDatabaseUpdate = false;
	}
	
	//if(isWritten === 'true')
		//TODO:
}

function paramSaved(isWritten) {
	setBusy(false);
	window.location.reload();
	if(waitForTextSimDatabaseUpdate) {
		updateTextSimDatabase('de.tudarmstadt.ukp.wikulu.plugin.textsimilarity.TextSimilarityPlugin');
		waitForTextSimDatabaseUpdate = false;
	}
	
	//if(isWritten === 'true')
		//TODO:
}

function checkIntValueAndSave(id, paramName) {
	
	var regExp = /^\d+$/;
	if(checkExpression(id, regExp)) {
		saveValue(paramName, $("#param_value_id_"+id).val());
	}
	
	
}


function checkFloatValueAndSave(id, paramName) {
	var regExp = /^([+-]?((([0-9]+(\.)?)|([0-9]*\.[0-9]+))([eE][+-]?[0-9]+)?))$/;
	if(checkExpression(id, regExp)) {
		saveValue(paramName, $("#param_value_id_"+id).val());
	} 

	
}

function checkExpression(id, regexp) {
	$('#param_error_id_'+id).hide();
	var val = $("#param_value_id_"+id).val();
	if(!val.match(regexp)){
		$('#param_error_id_'+id).show();  
		$("#param_value_id_"+id).focus();
		return false;
	}
	return true;
}

function changeAndSave(id, paramName) {
	saveValue(paramName, $("#param_value_id_"+id).val());
}

function saveValue(paramName, value) {
	setBusy(true);
	var jsonString = '{"class" : "'+pluginIdGlobal+'" , "arguments" : { "command" : "write_config", "plugin" : "'+currentPluginId+'", "paramname" : "'+paramName+'", "paramvalue" : "'+value+'"}}';
	Wikulu.perform(jsonString, paramSaved);

	
}


