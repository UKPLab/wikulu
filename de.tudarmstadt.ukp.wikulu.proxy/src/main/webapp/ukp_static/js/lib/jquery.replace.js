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
// Gary Haran => gary@talkerapp.com
// This code is released under MIT licence

var replacer = function(finder, replacement, element, blackList) {
			    if (!finder || typeof replacement === 'undefined') {
			      return
			    }
			    var regex = (typeof finder == 'string') ? new RegExp(finder, 'gi') : finder;
			    
			    var childNodes = element.childNodes;
			    var len = childNodes.length;
			    
			    var list = typeof blackList == 'undefined' ? 'html,head,style,title,link,meta,script,object,iframe,pre,a,' : blackList ;
			    
			    while (len--) {
			      var node = childNodes[len];
			      
			      //console.info(node.nodeName.toLowerCase());
			      //console.info(list);
			      //console.info(list.indexOf(node.nodeName.toLowerCase()) === -1);
			      if (node.nodeType === 1 && true || (list.indexOf(node.nodeName.toLowerCase()) === -1)) {
			        replacer(finder, replacement, node, list);
			      }
			      
			      if (node.nodeType !== 3 || !regex.test(node.data)) {
			        continue;
			      }
			      
			      var frag = (function(){
			        var html = node.data.replace(regex, replacement);
			        var wrap = document.createElement('span');
			        var frag = document.createDocumentFragment();
			        
			        wrap.innerHTML = html;
			        
			        while (wrap.firstChild) {
			          frag.appendChild(wrap.firstChild);
			        }
			        
			        return frag;
			      })();
			      
			      var parent = node.parentNode;
			      parent.insertBefore(frag, node);
			      parent.removeChild(node);
			    }
			  }

/* (function($){
  var replacer = function(finder, replacement, element, blackList) {
    if (!finder || typeof replacement === 'undefined') {
      return
    }
    var regex = (typeof finder == 'string') ? new RegExp(finder, 'gi') : finder;
    
    var childNodes = element.childNodes;
    var len = childNodes.length;
    
    var list = typeof blackList == 'undefined' ? 'html,head,style,title,link,meta,script,object,iframe,pre,a,' : blackList ;
    
    while (len--) {
      var node = childNodes[len];
      
      //console.info(node.nodeName.toLowerCase());
      //console.info(list);
      //console.info(list.indexOf(node.nodeName.toLowerCase()) === -1);
      if (node.nodeType === 1 && true || (list.indexOf(node.nodeName.toLowerCase()) === -1)) {
        replacer(finder, replacement, node, list);
      }
      
      if (node.nodeType !== 3 || !regex.test(node.data)) {
        continue;
      }
      
      var frag = (function(){
        var html = node.data.replace(regex, replacement);
        var wrap = document.createElement('span');
        var frag = document.createDocumentFragment();
        
        wrap.innerHTML = html;
        
        while (wrap.firstChild) {
          frag.appendChild(wrap.firstChild);
        }
        
        return frag;
      })();
      
      var parent = node.parentNode;
      parent.insertBefore(frag, node);
      parent.removeChild(node);
    }
  }
  
  $.fn.replace = function(finder, replacement, blackList) {
    return this.each(function(){
      replacer(finder, replacement, $(this).get(0), blackList);
    });
  }
})(jQuery); */