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
var popupsList = new Array();
/**
 * Append container for the popup menu to the DOM
 */
$(document).ready(function() {
	$("body").append("<div id=\"sl_popup_area\"><\/div>");
	
	
	
});


/**
 * Create Suggested Links dialog window
 * @param node
 * @param e
 * @return
 */
function createSugLinksDialog(node, e){
	var visited = $(node).find(".sl_visited");
	//$(node).addClass("sl_sugg_links_view_choosed");
	var suggestLinks = new SuggestLinks();
	if (visited.html() == null) {
		suggestLinks.initialize(node, e.pageX, e.pageY);
		suggestLinks.execute();
		popupsList.push(suggestLinks);
	}else{
		for (var i = 0; i < popupsList.length; ++i) {
			var buf = popupsList[i];
			if(buf.uid == visited.html()){
				suggestLinks = buf;
				break;
			}
			
		}
		suggestLinks.reopenDialog(e.pageX, e.pageY)	
	}
	
	
}


/**
 * Class SuggestLinks
 *
 */
function SuggestLinks() {
	
	/**
	 * x-axis of the popup
	 */
	this.mPosX;
	/**
	 * y-axis of the popup
	 */
	this.mPosY;
	/**
	 * unique ID of the popup
	 */
	this.uid;
	/**
	 * Node of the word links of which must be suggested
	 */
	this.wordNode;
	/**
	 * the title of the popup window as HTML 
	 */
	this.title;
	/**
	 * the root node of the popup
	 */
	this.dNode;
	/**
	 * container of nodes of this popup;
	 */
	this.sdNodes;
	/**
	 * Word links of which must be suggested
	 */
	this.word
	/**
	 * The number of word that has been clicked
	 */
	this.wordNumber
			
	
	/**
	 * Set title to the dialog
	 */
	this.setTitle = function(node) {
		node.html(this.title);
	};
	
	
	/**
	 * Initializes parameters of a new popup window.
	 * @param wordNode Node of the word links of which must be suggested
	 * @param mPosXVar x-axis of mouse cursor in initialization
	 * @param mPosYVar y-axis of mouse cursor in initialization
	 */
	this.initialize = function(wordNode, mPosXVar, mPosYVar) {
		this.mPosX = mPosXVar;
		this.mPosY = mPosYVar;
		//generate new uid
		this.uid = new Date().getTime();
		this.wordNode = wordNode;
		this.word = $('.wikiLinkText', wordNode).text();
		this.wordNumber = this.wordNode.attr("num");
		
	};
		
	
	/**
	 * Initializes for creating the popup dialog and initiates callback methods for obtaining the suggested links data.
	 */
	this.execute = function() {
				
		// set title name to the object
		this.title = 'Suggested Links for word: '+this.word;
		
		//create new popup
		this.createPopup(this.wordNode);
		
		//get new created dialog
		this.dNode = $("#sl_drag_dialog_"+this.uid);
		
		//set title to dialog
		this.setTitle(this.dNode.find(".sl_title"));
		
		this.createDialog();
		
		var thisObject = this;
		
		//set created Dialog
		var dNode = this.dNode;
		
		
		var jsonString = ' { "class" : "de.tudarmstadt.ukp.wikulu.plugin.linksuggestion.LinkSuggestionPlugin" , "arguments" : { "anchor" : "' + this.word + '"} } ';
		Wikulu.perform(jsonString, 
				{callback: function(val) {thisObject.showLinks(val, dNode)}}
		);
		
		//return;
		
		//OLD
		//Wikulu.getLinkTargets(window.location.href, this.word,
		//										{callback: function(val) {thisObject.showLinks(val, dNode)}});
		
		
	};
	
	
	/**
	 * Closes the popup dialog.
	 */
	this.closeDialog = function() {
					
		this.sdNodes.nodeDialog.fadeOut(240);
		//enableAutoClose(this.sdNodes);
		//disableAutoCollapse(this.sdNodes);
		
	};

	/**
	 * Detects mouse away for easy reversal of opening dialog and for fast closing mechanism.
	 * This method has incompatibility issue with IE.
	 */
	this.detectMouseAwayOnce = function() {
		var thisPopup = this;
		// detecting if mouse go away from snippet
		var recursion = function() {
			$().one("mousemove", function() {
				if (thisPopup != null) {
					var x = thisPopup.sdNodes.nodeDialog.css("left");
					var y = thisPopup.sdNodes.nodeDialog.css("top");
					var xPlus = parseInt(x.replace(/px/gi, ""))+mSpaceX;
					var yPlus = parseInt(y.replace(/px/gi, ""))+mSpaceY;
					x = x.substring(0, x.length-2);
					y = y.substring(0, y.length-2);
					if (mPosX < x || mPosY < y) {
						if (thisPopup.sdNodes.autoClose && !thisPopup.sdNodes.autoCollapse) {
							thisPopup.closeDialog();
						}
					} else if (((mPosX >= x) && (mPosX < xPlus)) || ((mPosY >= y) && (mPosY < yPlus))) {
						// this line is incompatible with IE
						if (!isIE) {
							recursion();
						}
					}
				}
			});
		};
		recursion();
	};
	
	/**
	 * Creates the draggable dialog.
	 * @return -
	 */
	this.createDialog = function() {
		this.initializeNode();
		
		var sdNodes = this.sdNodes;
		
		
		// set dialog position : 4 types
		this.setDialogPosition(this.mPosX, this.mPosY);
		
		
		// create draggable
		sdNodes.nodeDialog.draggable({
			opacity: 0.85, 
			handle: sdNodes.nodeDragger
		});
		
		// handle z-index
		sdNodes.nodeDialog.css("z-index", ++countZ);
		
		this.monitorEvents();
		
	};
	
	this.setDialogPosition = function(PosX, PosY) {
		//Detect screen size
		var scrWidth = screen.width;
		var scrHeight = screen.height;
		
		if(PosX+570 > scrWidth){
			//show on the left
			if(PosY+200 > scrHeight){
				//show top
				this.sdNodes.nodeDialog.css("left", PosX - 570);
				this.sdNodes.nodeDialog.css("top", PosY - 200);
			}else{
				//show down
				this.sdNodes.nodeDialog.css("left", PosX - 570);
				this.sdNodes.nodeDialog.css("top", PosY);
			}
		}else{
			//show on the right
			if(PosY+200 > scrHeight){
				//show top
				this.sdNodes.nodeDialog.css("left", PosX);
				this.sdNodes.nodeDialog.css("top", PosY - 200);
			}else{
				//show down
				this.sdNodes.nodeDialog.css("left", PosX);
				this.sdNodes.nodeDialog.css("top", PosY);
			}
		}
	};
	
	/**
	 * Reopens or repositions the dialog.
	 * @param mPosXVar initial x-axis at reopening the dialog
	 * @param mPosYVar initial y-axis at reopening the dialog
	 */
	this.reopenDialog = function(mPosXVar, mPosYVar) {
		this.mPosX = mPosXVar;
		this.mPosY = mPosYVar;
		
		// set dialog position
		this.setDialogPosition(this.mPosX, this.mPosY);
		this.sdNodes.nodeDialog.css("opacity", "1");
		
		// handle z-index
		this.sdNodes.nodeDialog.css("z-index", ++countZ);
		
		this.dNode.fadeIn(240);
		this.detectMouseAwayOnce();
	};
	
	/**
	 * Show suggested links in popup menu
	 * @param val
	 * @param node root Node of the popup menu -> sl_drag_dialog
	 */
	this.showLinks = function(val, node){
		if(val == null) {
			node.find(".sl_links").css("font-size", "larger").html("There are no suggestions!");
			
			node.find(".sl_loading").css("display", "none");
			node.find(".sl_content_frame").css("display", "block");
			node.css("width", "auto");
			return;
		}
			
		var json = eval('(' + val +')');
		var jsonTargets = json.targets;
		
		
		var innerHtml="";
		if(jsonTargets == null || jsonTargets.length == 0){
			node.find(".sl_links").css("font-size", "larger").html("There are no suggestions!");
			
			node.find(".sl_loading").css("display", "none");
			node.find(".sl_content_frame").css("display", "block");
			node.css("width", "auto");
			return;
		}
			
		//generate uid
		var uid_d = new Date().getTime();
		
		for (i in jsonTargets){
			//extract title
			var linkTitle = /([^\/]*)$/; 
			linkTitle.exec(jsonTargets[i]);
			linkTitle = RegExp.$1;
			if(linkTitle.length > 20)
				linkTitle = linkTitle.substring(0,20)+"...";
			innerHtml += "<div class=\"sl_sugg_links_block\"><span class=\"sl_sugg_links_view\" id=\""+uid_d+"_"+i+"\">"+linkTitle+"</span>" +
					"<div class=\"sl_sugglinks_right_block\"><a href=\""+jsonTargets[i]+"\" class=\"sl_sugg_links\" id=\"links_"+uid_d+"_"+i+"\"></a><span class=\"sl_sugg_links_add\" link=\""+jsonTargets[i]+"\"></span></div></div>";
			
		}
		

				
		//add links to popup
		node.find(".sl_links").html(innerHtml);
		node.find(".sl_loading").css("display", "none");
		node.find(".sl_content_frame").css("display", "block");
		
		//add click event
		var thisObject = this;
		$(".sl_sugg_links_view", node).click(function(e){
			var view_id = $(this).attr("id");
			
			//make bold currently selected link
			$(".sl_sugg_links_view").removeClass("sl_sugg_links_view_choosed");
			$(this).addClass("sl_sugg_links_view_choosed");
			thisObject.viewSuggestedLinkSniffer(view_id);
		});
		
		var wordNode = this.wordNode;
		//add click event for confirm link button
		$(".sl_sugg_links_add", node).click(function(e){
			//TODO: get Linknam from parameteres
			//
			var linkCandidate =$(this).attr("link");
			var attrNum = $(wordNode).attr("num");
			var suchWort = $('.wikiLinkText', wordNode).text();
			confirmLinkCandidate(linkCandidate, suchWort, attrNum);
			
		});
			
		//TODO:
		//var keyphraseURL = "http://en.wikipedia.org/w/index.php?title=Special%3ASearch&redirs=1&fulltext=Search&ns0=1&search=";
		
		//load first finded link
		$("#"+uid_d+"_"+0).addClass("sl_sugg_links_view_choosed");
		this.viewSuggestedLinkSniffer(uid_d+"_"+0);
		//
		
};
	
	this.viewSuggestedLinkSniffer = function(view_id) {
		var suggestLinks = new Sniffer(null);
		var visited = $("#links_"+view_id).find(".ws_visited");
		this.sdNodes.nodeContent.children().hide();
		
		
		
		if (visited.html() == null) {
			var uid_d = new Date().getTime();
			suggestLinks.initializeAppended($("#links_"+view_id), this.sdNodes.nodeContent, view_id);
			//change css class ws_drag_dialog, position to static, for resizing divs
			//$("#ws_drag_dialog_"+view_id).css("position", "static");
			$("#ws_drag_dialog_"+view_id).removeClass("ws_drag_dialog");
			$("#ws_drag_dialog_"+view_id).addClass("sl_appended_sniffer");
			$(".ws_keyphrases","#ws_drag_dialog_"+view_id).removeClass("ws_keyphrases").addClass("sl_keyphrases");
			
			
		} 
		//caching
		$("#ws_drag_dialog_"+view_id).fadeIn(240);
		
		
		
		
		//suggestLinks.initializeAppended($(linkToView).parent().find(".sl_sugg_links"), this.sdNodes.nodeContent, uid_d);
	}
	
	
	
	/**
	 * Adds HTML skeleton of the popup menu to the page. 
	 * @return -
	 */
	this.createPopup = function() {
		var uid = this.uid;
			
		var snifferHTML="";
		snifferHTML += "<div id=\"sl_drag_dialog_"+uid+"\" class=\"sl_drag_dialog\">";
		snifferHTML += "		<div class=\"ws_frontlayer\">";
		snifferHTML += "		<table style=\"border-bottom: solid; border-width: 1px; border-bottom-color: #aaaaaa; border-collapse: collapse; width: 100%\">";
		snifferHTML += "			<tr class=\"ws_frame\">";
		snifferHTML += "				<td class=\"ws_buttons_skin\">";
		snifferHTML += "					<table class=\"ws_buttons\">";
		snifferHTML += "						<tr>";
		snifferHTML += "						<td class=\"ws_hold_button ws_autoclose_off tooltip_autoclose\" title=\"Autoclose: on\/off\">&nbsp;<span>click to disable or enable autoclose<\/span><\/td>";
		snifferHTML += "						<td class=\"ws_close_button tooltip_close\" title=\"Click to close\">&nbsp;<span>click to close<\/span><\/td>";
		snifferHTML += "						<\/tr>";
		snifferHTML += "					<\/table>";
		snifferHTML += "				<\/td>";
		snifferHTML += "				<td class=\"sl_title\"><\/td>";
		snifferHTML += "			<\/tr>";
		snifferHTML += "		<\/table>";
		snifferHTML += "		<div class=\"ws_collapsable\">";
		snifferHTML += "		<div class=\"sl_loading\"><div class=\"ws_loading_img\"></\div><div class=\"ws_loading_text\">Suggest Links: <b>loading contents<\/b>...<\/div><\/div>";
		snifferHTML += "		<div class=\"sl_content_frame\" style=\"display: none;\">";
		snifferHTML += "			<div class=\"sl_links\"><\/div>";
		snifferHTML += "			<div class=\"sl_content\"><\/div>";
		//snifferHTML += "			<div style=\"clear: both;\"><\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "	<\/div>";
		
		$("#sl_popup_area").append(snifferHTML);
		//$().html(snifferHTML);
		
		this.wordNode.append("<div class=\"sl_visited\">"+this.uid+"<\/div>");
	};
	
	/**
	 * Initializes sdNodes as the library of nodes. 
	 * @return -
	 */
	this.initializeNode = function() {
		// copy object variable dNode to local variable for further use in nested object.
		var dNode = this.dNode;
		
		// initialize node
		var sdNodes = new function() { 
			this.nodeDialog = dNode;
			this.nodeDragger = this.nodeDialog.find(".ws_frame");
			this.nodeContent = this.nodeDialog.find(".sl_content");
			this.nodeBacklayer = this.nodeDialog.find(".ws_backlayer");
			this.nodeFrontlayer = this.nodeDialog.find(".ws_frontlayer");
			this.nodeTitle = this.nodeDialog.find(".sl_title");
			this.nodeDisplayedContent = this.nodeDialog.find(".ws_displayed_content");
			this.nodeCloseButton = this.nodeDialog.find(".ws_close_button");
			this.nodeHoldButton = this.nodeDialog.find(".ws_hold_button");
			this.nodeCollapseButton = this.nodeDialog.find(".ws_collapse_button");
			this.nodeCollapsable = this.nodeDialog.find(".ws_collapsable");
			this.autoClose;
			this.autoCollapse;
			this.switchFadeInterval = 120;
			
		};
		if (sdNodes.nodeHoldButton.hasClass("ws_autoclose_on")) {
			sdNodes.autoClose = true;
		} else if (sdNodes.nodeHoldButton.hasClass("ws_autoclose_off")) {
			sdNodes.autoClose = false;
		}
		
		if (sdNodes.nodeCollapseButton.hasClass("ws_autocollapse_on")) {
			sdNodes.autoCollapse = true;
		} else if (sdNodes.nodeHoldButton.hasClass("ws_autocollapse_off")) {
			sdNodes.autoCollapse = false;
		}
		
		this.sdNodes = sdNodes;
	};
	
	
	
	
	
	
	
	/**
	 * Monitors mouse events on the snippet.
	 * @return -
	 */
	this.monitorEvents = function() {
		var thisSniffer = this;
		var sdNodes = this.sdNodes;
		
		// moveup and save scroll since opera reset scrollbar after z-index is updated
		var moveUpAndSaveScroll = function() {
			var saveScroll = { scrollTop: sdNodes.nodeDisplayedContent.attr('scrollTop'), scrollLeft: sdNodes.nodeDisplayedContent.attr('scrollLeft') };
			sdNodes.nodeDialog.css("z-index", ++countZ);
			sdNodes.nodeDisplayedContent.attr(saveScroll);
		}
		
		sdNodes.nodeDialog.click(function() {
			moveUpAndSaveScroll();
		});
		
		sdNodes.nodeDragger.mousedown(function() {
			moveUpAndSaveScroll();
		});

		
		// handle mouse {
		// handle close button
		sdNodes.nodeCloseButton.click(function() {
			thisSniffer.closeDialog();
		});
		
		thisSniffer.detectMouseAwayOnce(); 
		
		// handle if mouse enters dialog
		var timeoutLeave;
		var cancelLeave = function() {
			if (timeoutLeave != null) {
				clearTimeout(timeoutLeave);
			}
		}
		
		sdNodes.nodeFrontlayer.mousemove(function() {
			cancelLeave();
		});
		
		sdNodes.nodeFrontlayer.mouseenter(function() {
			cancelLeave();
			
			if (sdNodes.autoCollapse) {
				sdNodes.nodeDialog.css("z-index", ++countZ);
				sdNodes.nodeCollapsable.slideDown("normal");
			}
			
			sdNodes.nodeFrontlayer.one("mouseleave", function() {
				timeoutLeave = setTimeout(function() {	
					if (sdNodes.autoClose && !sdNodes.autoCollapse) {
						thisSniffer.closeDialog();
					} else {
						if (sdNodes.autoCollapse) {
							sdNodes.nodeCollapsable.slideUp("normal");
						}
					}	
				}, 360);
			});
		});
		
		// handle hold button
		sdNodes.nodeHoldButton.click(function() {
			if (sdNodes.autoClose) {
				disableAutoClose(sdNodes);
			} else {
				enableAutoClose(sdNodes);
			}
		});
		
		// handle collapse button
		sdNodes.nodeCollapseButton.click(function() {
			if (sdNodes.autoCollapse) {
				disableAutoCollapse(sdNodes);
			} else {
				enableAutoCollapse(sdNodes);
			}
		});
		
		
		// handle autozoom {
		// monitor if mouse hovers the content
		var hovertimeout;
		var cancelHover = function() {
			// If mouseout happens before the delay ends,
			// cancel hover thread
			if (hovertimeout != null) {
				clearTimeout(hovertimeout);
			}
		};
		
		var stop;
		var cancelStop = function() {
			if (stop != null) {
				clearTimeout(stop);
			}
		};
		
		var leaveContent;
		var cancelLeaveContent = function() {
			if (leaveContent != null) {
				clearTimeout(leaveContent);
			}
		};

	};
	
}