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
 * Main JavaScript for the Wikulu
 */

/* init */
$(document).ready( function() {
	// set error handler
	dwr.engine.setErrorHandler(wikuluErrorHandler);
	
	// create interface elements
	WikiAdapter.init();
});


function perform(pluginClass, runFunc) {
	runFunc(pluginClass);
}

/*
 * Helpers
 */
function loadJS(file) {
	var fileref = document.createElement('script')
	fileref.setAttribute("type", "text/javascript")
	fileref.setAttribute("src", file)

	if (typeof fileref != "undefined")
		document.getElementsByTagName("someFunctionhead")[0].appendChild(fileref)
}

function loadCSS(file) {
	var fileref = document.createElement("link")
	fileref.setAttribute("rel", "stylesheet")
	fileref.setAttribute("type", "text/css")
	fileref.setAttribute("href", file)

	if (typeof fileref != "undefined")
		document.getElementsByTagName("head")[0].appendChild(fileref)
}

 function resolver() {
	 return 'http://www.w3.org/1999/xhtml';
}
 
 
 function findAndReplace(searchText, replacement, searchNode) {
	    if (!searchText || typeof replacement === 'undefined') {
	        // Throw error here if you want...
	        return;
	    }
	    var regex = typeof searchText === 'string' ?
	                new RegExp(searchText, 'g') : searchText,
	        childNodes = (searchNode || document.body).childNodes,
	        cnLength = childNodes.length,
	        excludes = 'html,head,style,title,link,meta,script,object,iframe';
	    while (cnLength--) {
	        var currentNode = childNodes[cnLength];
	        if (currentNode.nodeType === 1 &&
	            (excludes + ',').indexOf(currentNode.nodeName.toLowerCase() + ',') === -1) {
	            arguments.callee(searchText, replacement, currentNode);
	        }
	        if (currentNode.nodeType !== 3 || !regex.test(currentNode.data) ) {
	            continue;
	        }
	        var parent = currentNode.parentNode,
	            frag = (function(){
	                var html = currentNode.data.replace(regex, replacement),
	                    wrap = document.createElement('div'),
	                    frag = document.createDocumentFragment();
	                wrap.innerHTML = html;
	                while (wrap.firstChild) {
	                    frag.appendChild(wrap.firstChild);
	                }
	                return frag;
	            })();
	        parent.insertBefore(frag, currentNode);
	        parent.removeChild(currentNode);
	    }
	}

/*
 * Error handling
 */
function wikuluErrorHandler(data) {
	
	alert("Error: " + data);
	setBusy(false);
}