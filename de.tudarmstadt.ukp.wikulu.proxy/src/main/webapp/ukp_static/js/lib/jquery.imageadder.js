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
Added images to arbitrary terms
*/
var image_link_num = -1;
$(function() {
		 
 jQuery.addTextImage = document.body.createTextRange ? 

 
/*
Version for IE using TextRanges.
*/
  function(node, te) {
		 
   var r = document.body.createTextRange();
   r.moveToElementText(node);
   
   for (var i = 0; r.findText(te); i++) {
	   var uid = new Date().getTime();
    r.pasteHTML('<span class="wikiSuggestLink" id="wikiID_'+uid+'" num="'+i+'"><span class="wikiLinkText">' +  r.text + '</span><span class="wikiLink"/><\/span>');
    r.collapse(false);
   }
  }

 :

/*
 Version for Mozilla and Opera using span tags.
*/
  function(node, te) {
	
   var pos, skip, spannode, middlebit, endbit, middleclone, linknode, textnode;
   skip = 0;
   
   if (node.nodeType == 3) {
	   //re = new RegExp("\\b"+te+"[a-z]{0,1}\\b");
	//Is text is not in word boundaries -> break;
//	var checkWord = new RegExp("^[\\w\\s]*$");
//	var m = checkWord.exec(te);
//	if (m == null)
//		return skip;
	   var re;
	try {
		re = new RegExp("\\b"+te+"\\b");
	}catch(err) {
		return skip;
	}   
	
    pos = node.data.toUpperCase().search(re);
    if (pos >= 0) {
    	image_link_num = image_link_num + 1;
     // spann Node	
     spannode = document.createElement('span');
     spannode.className = 'wikiSuggestLink';
     var uid = new Date().getTime();
     spannode.setAttribute('id','wikiID_'+uid);
     spannode.setAttribute('num', image_link_num);
     //
     
     // Word
     middlebit = node.splitText(pos);
     endbit = middlebit.splitText(te.length);
     middleclone = middlebit.cloneNode(true);
     //
     
     //link Node
     linknode = document.createElement('span');
     linknode.className = 'wikiLink';
     //
     //text Node
     textnode = document.createElement('span');
     textnode.className = 'wikiLinkText'; 
     
     //
     textnode.appendChild(middleclone)
     spannode.appendChild(textnode);
     spannode.appendChild(linknode);
     middlebit.parentNode.replaceChild(spannode, middlebit);
     skip = 1;
    }
   }
   else if (node.nodeType == 1 && node.childNodes && !/(script|style)/i.test(node.tagName)) {
    for (var i = 0; i < node.childNodes.length; ++i) {
     i += jQuery.addTextImage(node.childNodes[i], te);
    }
   }
   return skip;
  }

 ;
});

//removes all tags, which has been added
jQuery.fn.removeAddedImage = function() {
 this.find("span.wikiSuggestLink").each(function() {
  with (this.parentNode) {
   replaceChild(this.firstChild.firstChild, this);
   normalize();
  }
 });
 
 return this;
};


//removes added images from tags which have tag <a> as parent
jQuery.fn.removeImageFromLink = function() {
	this.find("span.wikiSuggestLink").each(function() {
		if($(this).parents("a").length > 0){
			with (this.parentNode) {
				replaceChild(this.firstChild.firstChild, this);
				normalize();
			}
		}
});
	
	 
return this;
};
