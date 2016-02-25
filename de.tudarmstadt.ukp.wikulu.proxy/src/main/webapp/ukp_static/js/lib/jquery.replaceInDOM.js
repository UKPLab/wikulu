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
 * Replace matched text in DOM with replacement
 * This function uses iterative DFS algo for traversing the DOM tree
 * 
 * @param search : text(regexp) to replace
 * @param replace : replace with
 * @param excludeNodes : array of node names to exclude from traversing(example: '["a", "div", "span"]');
 * @return number of replacements(or if -1 then input parameters error)
 * @author Artem Vovk
 * @email: vovk.artem@gmail.com
 */
jQuery.fn.replaceInDOM = function(search, replace, excludeNodes) {
	// check input parameters
	if (typeof search === 'undefined' || typeof replace === 'undefined') {
		// error
		return -1;
	}

	// Nodes to exclude
	var excludeN = (typeof excludeNodes === 'undefined') ? [] : excludeNodes;
	
	// create regex
	var searchRegex = (typeof search === 'string') ? new RegExp(search, 'gi')
			: search;
		
	// create queue for DFS algo
	var queue = [];
	// get children of current node and fill queue
	$.each(this, function(i, val) {
		queue[i] = val;
	});

	// replace counter
	var counter = 0;

	// DFS
	while (queue.length !== 0) {
		// extract first element
		var node = queue.shift();

		// Do not search in excludeNodes
		if ($.inArray(node.nodeName.toLowerCase(), excludeN) !== -1) {
			continue;
		}

		// if element ->get children
		if (node.nodeType === 1) {
			// get children of node and add to queue
			queue = Array.prototype.slice.call(node.childNodes).concat(queue);
		}

		// if not text or doesn't match continue
		if (node.nodeType !== 3 || !searchRegex.test(node.data)) {
			continue;
		}

		// create wrap element and fill it with replaced text
		var wrapDiv = document.createElement('div');
		wrapDiv.innerHTML = node.data.replace(searchRegex, replace);

		var parent = node.parentNode;
		// insert children of wrapper 
		while (wrapDiv.firstChild) {
			// insert before old node
			parent.insertBefore(wrapDiv.firstChild, node);
		}
		// remove old node
		parent.removeChild(node);
		// inc counter
		counter++;

		// if regexp global modifier is not set -> match first occurence
		if (searchRegex.global == false)
			return counter;
	}
	return counter;

};
