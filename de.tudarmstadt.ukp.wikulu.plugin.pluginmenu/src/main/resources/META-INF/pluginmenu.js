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
jQuery(document).ready(function() {
	// menu div name: fixedPluginMenu
	createPluginMenu();
});

function createPluginMenu() {
	var divDescription = '<div id="fixedPluginMenu" style="visibility:hidden; position: fixed; padding: 10px; bottom: 0; left: 0; border-top: 1px solid #dadada; z-index:1; background-color:#f6f6f6;"></div>';
	jQuery("body").append(divDescription);
}

function setPluginMenuVisibility(visible) {
	// set visible if plugin really needs this menu
	if(jQuery("#fixedPluginMenu").length) {
		if(visible == true) {
			jQuery("#fixedPluginMenu").remove();
			createPluginMenu();
			jQuery("#fixedPluginMenu").css('visibility', 'visible');
		} else {
			jQuery("#fixedPluginMenu").css('visibility', 'hidden');
		}
		
	}
}

function pluginMenuMakeSlider(aMin, aMax, aValue, bind_function) {
	jQuery("#fixedPluginMenu").html("");
	jQuery("#fixedPluginMenu").append("<div id='slideAmount' style='float:center;'>" + aValue + "</div>");
	jQuery("#fixedPluginMenu").css({ 'height' : '5em', 'width' : '12em'});													// else the div is maybe 1px * 1px
	jQuery("#fixedPluginMenu").append('<div id="fixedPluginMenu_slider" style="margin:5px;"></div>' +						// make separate div for slider
			'<div style="float:left;">' + aMin + '</div><div style="float:right;">' + aMax + '</div>');
	jQuery("#fixedPluginMenu_slider").slider({ min: aMin, max: aMax, value: aValue });
	bind_function(aValue);																									// update the div
	jQuery("#fixedPluginMenu_slider").bind("slide", function(event, ui) {
		// update label and run function provided by plugin
		jQuery("#slideAmount").html(ui.value);
		bind_function(ui.value);
	});
}