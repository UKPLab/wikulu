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
 * JavaScript control for Wiki-Sniffer
 * 
 * @author Fabian Lagonda Tamin
 * @depends WikiSniffer.js, jquery-1.3.2.js, ui.core.js, ui.draggable.js, wikisniffer.css 
 */

/*
 * Main parameters:
 * - mainURL: main URL where this sniffer should be activated.
 * - apiURL: wiki API URL.
 * - wikiSyntax: wiki syntax.
 * - keyphrasesURL: URL for search keyphrases.
 * - monitorNode: root in DOM element, where links inside it will be monitored for sniffing.
 * - monitorPath: which path will be included to be monitored.
 * - titleNode: CSS selector to select the title of the page in the DOM.
 * - enableExtraSummary: if it is enabled, LexRank summarizer will be activated, extra summary will be added after the main summary.
 * - enableInternalLinks: to enable or disable table of contents feature.
 * - enableInternalLinks: to enable or disable internal links feature.
 * - enableBacklinks: to enable or disable backlinks feature.
 * - enableExternalLinks: to enable or disable external links feature.
 * - enableImages: to enable or disable images feature.
 */ 

// Wikipedia
/*
 * JavaScript control for Wiki-Sniffer
 * 
 * @author Fabian Lagonda Tamin
 * @depends WikiSniffer.js, jquery-1.3.2.js, ui.core.js, ui.draggable.js, wikisniffer.css 
 */

/*
 * Main parameters:
 * - mainURL: main URL where this sniffer should be activated.
 * - apiURL: wiki API URL.
 * - wikiSyntax: wiki syntax.
 * - keyphrasesURL: URL for search keyphrases.
 * - monitorNode: root in DOM element, where links inside it will be monitored for sniffing.
 * - monitorPath: which path will be included to be monitored.
 * - titleNode: CSS selector to select the title of the page in the DOM.
 * - enableExtraSummary: if it is enabled, LexRank summarizer will be activated, extra summary will be added after the main summary.
 * - enableInternalLinks: to enable or disable table of contents feature.
 * - enableInternalLinks: to enable or disable internal links feature.
 * - enableBacklinks: to enable or disable backlinks feature.
 * - enableExternalLinks: to enable or disable external links feature.
 * - enableImages: to enable or disable images feature.
 */ 

// Wikipedia
var mainURL = "http://wiki.bildungsserver.de";
var viewURL = "http://wiki.bildungsserver.de/index.php";
var apiURL = "http://wiki.bildungsserver.de/api.php";
var wikiSyntax = "MediaWiki";
var keyphraseURL = "http://wiki.bildungsserver.de/index.php?title=Spezial%3ASuche&redirs=1&fulltext=Volltext&ns0=1&search=";
var monitorNode = "";
var monitorPath = "/index.php/";
var titleNode = "#firstHeading";
var enableExtraSummary = false;
var enableHeadings = true;
var enableInternalLinks = true;
var enableBacklinks = false;
var enableExternalLinks = true;
var enableImages = true; 

// TWiki
//var mainURL = "http://mrburns.tk.informatik.tu-darmstadt.de";
//var apiURL = "http://mrburns.tk.informatik.tu-darmstadt.de/twiki/bin/rest/WikuluPlugin/";
//var wikiSyntax = "TWiki";
//var keyphraseURL = "http://www.cs.wisc.edu/twiki/bin/view/TWiki/TagMeSearch?tag=";
//var monitorNode = "#patternMainContents";
//var monitorPath = "/twiki/bin/view/";
//var titleNode = "h1";

// interesting to test this. of course keyphrases are not chosen correctly, and so are lexrank.
//var mainURL = "http://de.wikipedia.org/wiki/";
//var apiURL = "http://de.wikipedia.org/w/api.php";
//var wikiSyntax = "MediaWiki";
//var keyphraseURL = "http://de.wikipedia.org/w/index.php?title=Special%3ASearch&redirs=1&fulltext=Search&ns0=1&search=";
//var monitorNode = "";
//var monitorPath = "/wiki/";
//var titleNode = "#firstHeading";


//This is interesting but 
//this wiki.d-addicts.com does not support Wiki API, some functionality likes heading, backlinks, internal links does not work.
//var mainURL = "http://wiki.d-addicts.com/";
//var apiURL = "http://wiki.d-addicts.com/api.php";
//var wikiSyntax = "MediaWiki";
//var keyphraseURL = "http://www.google.com/custom?domains=wiki.d-addicts.com&sitesearch=wiki.d-addicts.com&sa=Go&client=pub-2588814452908402&forid=1&ie=UTF-8&oe=ISO-8859-1&cof=GALT%3A%23008000%3BGL%3A1%3BDIV%3A%23336699%3BVLC%3A663399%3BAH%3Acenter%3BBGC%3AFFFFFF%3BLBGC%3AFFFFFF%3BALC%3A0000FF%3BLC%3A0000FF%3BT%3A000000%3BGFNT%3A0000FF%3BGIMP%3A0000FF%3BFORID%3A1%3B&hl=en&q=";
//var monitorNode = "#content";
//var monitorPath = "/";
//var titleNode = "#firstHeading";

var enableExtraFeatures = (enableInternalLinks || enableExternalLinks || enableBacklinks || enableImages);
var mPosX = 0; 		// x-axis of mouse cursor position
var mPosY = 0;		// y-axis of mouse cursor position
var mSpaceX = 2;	// x space between cursor and snippet position
var mSpaceY = 10;	// y space between cursor and snippet position
var uid = 0;		// unique ID of the sniffer
var countZ = 1;		// z-axis of the snippet

var myNavigator = navigator.appName;
var internetExplorerID = "Microsoft Internet Explorer";
var isIE = (myNavigator == internetExplorerID);
if (isIE) {
	mSpaceX = -1;
	mSpaceY = 1;
}

/**
 * Snippet init function. This function will be executed after page load.
 */
$(document).ready(function() {
	
	
	if (location.href.indexOf(mainURL, 0) > -1) {
		detectMousePosition();
		$("body").append("<div id=\"ws_area\"><\/div>");
	
		var title = $(titleNode).html();
		if ($(titleNode) != null) {
			$(titleNode).html("<a href=\""+location.pathname+"\" title=\"Link to this page\">"+title+"<\/a>");		
		}
		
		monitorLink(monitorNode, null);
	}
});

/**
 * 
 * Detects mouse position, updating mPosX and mPosY.
 * @return -
 */
function detectMousePosition() {
	$("body").append("<div id=\"ws_mouse\" style=\"position:fixed; top:4px; left:4px; z-index:1200\"><\/div>");
	$().mousemove(function(e){
		mPosX = e.pageX+mSpaceX;
		mPosY = e.pageY+mSpaceY;
    });
}

/**
 * Disables auto-close for sdNodes (to hold state (GRG) => disable autocollapse)
 * @param sdNodes nodes container
 * @return -
 */
function disableAutoClose(sdNodes) {
	sdNodes.nodeHoldButton.removeClass("ws_autoclose_on");
	sdNodes.nodeHoldButton.addClass("ws_autoclose_off");
	sdNodes.autoClose = false;
	
	disableAutoCollapse(sdNodes);
}

/**
 * Enables auto-close for sdNodes
 * @param sdNodes nodes container
 * @return -
 */
function enableAutoClose(sdNodes) {
	sdNodes.nodeHoldButton.removeClass("ws_autoclose_off");
	sdNodes.nodeHoldButton.addClass("ws_autoclose_on");
	sdNodes.autoClose = true;
	sdNodes.isLockByChild = false; // !!!!!!!!!!! FIXME
}

/**
 * disable auto-collapse for sdNodes
 * @param sdNodes nodes container
 * @return -
 */
function disableAutoCollapse(sdNodes) {
	sdNodes.nodeCollapseButton.removeClass("ws_autocollapse_on");
	sdNodes.nodeCollapseButton.addClass("ws_autocollapse_off");
	sdNodes.autoCollapse = false;
}

/**
 * Enables auto-collapse for sdNodes
 * @param sdNodes nodes container
 * @return -
 */
function enableAutoCollapse(sdNodes) {
	sdNodes.nodeCollapseButton.removeClass("ws_autocollapse_off");
	sdNodes.nodeCollapseButton.addClass("ws_autocollapse_on");
	sdNodes.autoCollapse = true;
	
	enableAutoClose(sdNodes);
}

/**
 * Monitors and handles event in links
 * @param rootNode root node to monitor
 * @param sdNodes nodes container
 * @return -
 */
function monitorLink(rootNode, sdNodes) {
	if (rootNode == "") {
		rootNode = "html";
	}
	// handle link for mouse over
	$(rootNode).find("a:not(.ws_excluded_link)").each(function(i) {
		var node = $(this);
		var link = new String(node.attr("href"));
		var sniffer = new Sniffer(sdNodes);
		
		if (link.indexOf(monitorPath) == 0 && !(link.toLowerCase().indexOf("/wiki/file:") == 0)) {
			// set tool-tip about whether a link is sniffable {
			var title = node.attr("title");
			if (title == null) title = "";
			if (title == "") {
				title = "Hover this link to sniff this article";
			} else {
				title = "Hover this link to sniff \"" + title + "\" article";
			}
			node.attr("title", title);
			// }
			
			var hovertimeout;
			var cancelHover = function() {
				// If mouseout happens before the delay ends
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
			
			// monitor if mouse hovers the link
			node.hover(
				function() {
					node.mousemove(function() {
						// if mouse move cancel stop
						cancelStop();
						cancelHover();
						var onmousestop = function() {
							hovertimeout = setTimeout(function(e) {
								hoverLink(node, sniffer);
							}, 0);
						};
						
						// execute stop thread after defined period
						stop = setTimeout(onmousestop, 500);
					});
				},
				function() {
					// cancel stop thread if mouse leaves the link
					cancelStop();
				}
			);
			
			node.mouseout(
				function() {
					cancelHover();
				}
			);
		}
	});
	
	sniffer = null;
}

/**
 * Creates sniffer for filtered hovered link
 * @param hrefNode
 * @param sniffer
 * @return -
 */
function hoverLink(hrefNode, sniffer) {
	uid = new Date().getTime();
	
	// locking parent if child is opened {
	var parentSnifferSdNodes = sniffer.parentSnifferSdNodes;
	if (parentSnifferSdNodes != null) {
		parentSnifferSdNodes.isLockByChild = true;
	}
	// locking parent }
	
	var visited = hrefNode.find(".ws_visited");
	if (visited.html() == null) {
		sniffer.initialize(hrefNode, mPosX, mPosY, uid, keyphraseURL);
		sniffer.execute();
	} else {
		sniffer.reopenDialog(mPosX, mPosY);
	}
	
}

/**
 * Class Sniffer: represents Wiki-Sniffer object.
 * @param parentSnifferSdNodes parent sniffer if exists, otherwise should be assign with <code>null</code>.
 * @return -
 */
function Sniffer(parentSnifferSdNodes) {
	/**
	 * parent sniffer nodes container
	 */
	this.parentSnifferSdNodes = parentSnifferSdNodes;
	/**
	 * x-axis of the sniffer
	 */
	this.mPosX;
	/**
	 * y-axis of the sniffer
	 */
	this.mPosY;
	/**
	 * unique ID of the sniffer
	 */
	this.uid;
	/**
	 * href node of the snippet
	 */
	this.hrefNode;
	/**
	 * href link of the snippet
	 */
	this.hrefLink;
	/**
	 * search URL for the tags or categories
	 */
	this.tagSearch;
	/**
	 * To this node will be sniffer dialog appended
	 */
	this.appendToNode; 
	
	/**
	 * Sniffer dialog is appended to another window
	 */
	this.isAppended;
	
	/**
	 * Initializes parameters of a new sniffer.
	 * @param hrefNode href node of the sniffer
	 * @param mPosXVar x-axis of mouse cursor in initialization
	 * @param mPosYVar y-axis of mouse cursor in initialization
	 * @param uid unique ID of the sniffer
	 * @param tagSearch search URL for the tags or categories
	 * @return -
	 */
	this.initialize = function(hrefNode, mPosXVar, mPosYVar, uid, tagSearch) {
		this.mPosX = mPosXVar;
		this.mPosY = mPosYVar;
		this.uid = uid;
		this.hrefNode = hrefNode;
		this.hrefLink = this.hrefNode.attr("href");
		this.tagSearch = tagSearch;
		this.isAppended = false;
	};
	
	
	this.initializeAppended = function(hrefNode, appendToNode, uid) {
		this.appendToNode = appendToNode;
		this.uid = uid;
		this.hrefNode = hrefNode;
		this.hrefLink = this.hrefNode.attr("href");
		//TODO: tagsearch!
		this.tagSearch = keyphraseURL;
		this.isAppended = true;
		this.executeAppendToWindow();
		
	};
	
	this.executeAppendToWindow = function() {
		var targetLink = new String(location.protocol+"//"+document.domain+this.hrefNode.attr("href"));
		
		// set Title
		this.title = "<a href=\""+this.hrefLink+"\" class=\"ws_excluded_link tooltip_title\" title=\"Click or drag: click to view page; drag to move\">"+this.hrefNode.text()/*+" <img src=\"/ukp_static/img/wikisniffer/external.png\" style=\"vertical-align:middle;\">"*/+"<span>click to go or drag to move<\/span><\/a>";
		
		this.addSniffer(this.hrefNode);
		this.dNode = $("#ws_drag_dialog_"+this.uid);
		this.setTitle(this.dNode.find(".ws_title"));
		this.createDialog();
		
		var thisSniffer = this;
		var dNode = this.dNode;
		WikiSniffer.getWikiSniffer(targetLink, apiURL, wikiSyntax,
								   enableExtraSummary, enableHeadings, enableInternalLinks,
								   enableExternalLinks, enableBacklinks, enableImages, 
								   {callback: function(val) {thisSniffer.extractAnnotation(val, dNode);}});
		WikiSniffer.getKeyphrasesWikiSniffer(targetLink, apiURL, wikiSyntax,
								   {callback: function(val) {thisSniffer.extractKeyphrases(val, dNode);}});
		//WikiSniffer.getBacklinksWikiSniffer(targetLink, "http://en.wikipedia.org/w/api.php", "MediaWiki",
		//						   {callback: function(val) {thisSniffer.extractBacklinks(val, dNode);}});
	};
	
	
	
	/**
	 * the title of the sniffer as HTML 
	 */
	this.title;
	/**
	 * the root node of the sniffer
	 */
	this.dNode;
	/**
	 * container of nodes of this snippet; sniffer's library of sniffer's nodes 
	 */
	this.sdNodes;
	/**
	 * DWR WikiSniffer object which contains WikiSniffer data
	 */
	this.mainSniffer = null;
	
	/**
	 * Initializes for creating the snippet dialog and initiates callback methods for obtaining the snippet data.
	 * @return -
	 */
	this.execute = function() {
		var targetLink = new String(location.protocol+"//"+document.domain+this.hrefNode.attr("href"));
		
		// set Title
		this.title = "<a href=\""+this.hrefLink+"\" class=\"ws_excluded_link tooltip_title\" title=\"Click or drag: click to view page; drag to move\">"+this.hrefNode.text()/*+" <img src=\"/ukp_static/img/wikisniffer/external.png\" style=\"vertical-align:middle;\">"*/+"<span>click to go or drag to move<\/span><\/a>";
		
		this.addSniffer(this.hrefNode);
		this.dNode = $("#ws_drag_dialog_"+this.uid);
		this.setTitle(this.dNode.find(".ws_title"));
		this.createDialog();
		
		var thisSniffer = this;
		var dNode = this.dNode;
		WikiSniffer.getWikiSniffer(targetLink, viewURL, apiURL, wikiSyntax,
								   enableExtraSummary, enableHeadings, enableInternalLinks,
								   enableExternalLinks, enableBacklinks, enableImages, 
								   {callback: function(val) {thisSniffer.extractAnnotation(val, dNode);}});
		WikiSniffer.getKeyphrasesWikiSniffer(targetLink, viewURL, apiURL, wikiSyntax,
								   {callback: function(val) {thisSniffer.extractKeyphrases(val, dNode);}});
		//WikiSniffer.getBacklinksWikiSniffer(targetLink, "http://en.wikipedia.org/w/api.php", "MediaWiki",
		//						   {callback: function(val) {thisSniffer.extractBacklinks(val, dNode);}});
	};
	
	/**
	 * Adds HTML skeleton of the sniffer to the page. 
	 * @return -
	 */
	this.addSniffer = function() {
		var uid = this.uid;
		var oddOrEven = new OddOrEven();
		
		var snifferHTML="";
		snifferHTML += "<div id=\"ws_drag_dialog_"+uid+"\" class=\"ws_drag_dialog\">";
		snifferHTML += "		<div class=\"ws_frontlayer\">";
		if(!this.isAppended){
		snifferHTML += "		<table style=\"border-bottom: solid; border-width: 1px; border-bottom-color: #aaaaaa; border-collapse: collapse; width: 100%\">";
		snifferHTML += "			<tr class=\"ws_frame\">";
		snifferHTML += "				<td class=\"ws_buttons_skin\">";
		snifferHTML += "					<table class=\"ws_buttons\">";
		snifferHTML += "						<tr>";
		snifferHTML += "						<td class=\"ws_hold_button ws_autoclose_on tooltip_autoclose\" title=\"Autoclose: on\/off\">&nbsp;<span>click to disable or enable autoclose<\/span><\/td>";
		snifferHTML += "						<td class=\"ws_close_button tooltip_close\" title=\"Click to close\">&nbsp;<span>click to close<\/span><\/td>";
		snifferHTML += "						<td class=\"ws_collapse_button ws_autocollapse_off\" title=\"Autocollapse: on\/off\">&nbsp;<\/td>";
		snifferHTML += "						<\/tr>";
		snifferHTML += "					<\/table>";
		snifferHTML += "				<\/td>";
		snifferHTML += "				<td class=\"ws_title\"><\/td>";
		snifferHTML += "			<\/tr>";
		snifferHTML += "		<\/table>";
		}
		snifferHTML += "		<div class=\"ws_collapsable\">";
		snifferHTML += "		<div class=\"ws_loading\"><table class=\"ws_loading_table\"><tr><td class=\"ws_loading_cell\"><table><tr><td width=\"80\"><div class=\"ws_progressbar_skin\"><div class=\"ws_progressbar\"><\/div><\/div><\/td><td>Wiki-Sniffer: <b>loading contents<\/b>...<\/td><\/tr><\/table><\/td><\/tr><\/table><\/div>";
		snifferHTML += "		<div class=\"ws_content\" style=\"display: none;\">";
		snifferHTML += "			<div class=\"ws_switcher\">";
		snifferHTML += "				<table>";
		snifferHTML += "					<tr>";
		snifferHTML += "						<td colspan=2 style=\"\">";
		snifferHTML += "							<table style=\"border-collapse: collapse\">";
		snifferHTML += "								<tr class=\"ws_nav\">";
		snifferHTML += "									<td style=\"padding-right: 12px;\"><div class=\"ws_summary_nav ws_switcher_nav\" title=\"Click to show summary\">summary<\/div><\/td>";
		if (enableHeadings) {
		snifferHTML += "									<td style=\"padding-right: 12px;\"><div class=\"ws_toc_nav ws_switcher_nav\"  title=\"Click to show table of contents\">table of contents<\/div><\/td>";
		}
		if (enableExtraFeatures) {
		snifferHTML += "									<td style=\"padding-right: 12px;\"><div class=\"ws_more_action_nav ws_switcher_nav\"  title=\"Choose an item on the submenu\"><div class=\"ws_switcher_sub_nav\">more actions...<\/div>";
		snifferHTML += "										<div class=\"ws_more_action_menu\">";
		if (enableInternalLinks) {
		snifferHTML += "											<div class=\"ws_related_articles_nav "+oddOrEven.get()+" ws_selectable\" title=\"Click to show related articles to this page\">related articles<\/div>";
		}
		if (enableBacklinks) {
		snifferHTML += "											<div class=\"ws_backlinks_nav "+oddOrEven.get()+" ws_selectable\">what links here<\/div>";
		}
		if (enableExternalLinks) {
		snifferHTML += "											<div class=\"ws_external_links_nav "+oddOrEven.get()+" ws_selectable\" title=\"Click to show external links of this page\">external links<\/div>";
		}
		if (enableImages) {
		snifferHTML += "											<div class=\"ws_images_nav "+oddOrEven.get()+" ws_selectable\" title=\"Click to show images in this page\">images<\/div>";
		}
		snifferHTML += "										<\/div>";
		snifferHTML += "									<\/div><\/td>";
		}
		snifferHTML += "								<\/tr>";
		snifferHTML += "							<\/table>";
		snifferHTML += "						<\/td>";
		snifferHTML += "					<\/tr>";
		snifferHTML += "				<\/table>";
		snifferHTML += "				<div class=\"ws_switched_contents\">";
		snifferHTML += "				<\/div>";
		snifferHTML += "				<div class=\"ws_displayed_content\">";
		snifferHTML += "					<div class=\"ws_summary_content ws_selection\">";
		snifferHTML += "					<\/div>";
		snifferHTML += "					<div class=\"ws_toc_content ws_selection\">";
		snifferHTML += "					<\/div>";
		snifferHTML += "					<div class=\"ws_backlinks_content ws_selection\">";
		snifferHTML += "						<div class=\"ws_explicit_title\">What links here:<\/div>";
		snifferHTML += "						<div class=\"ws_backlinks_content_internal\">";
		snifferHTML += "						<\/div>";
		snifferHTML += "					<\/div>";
		snifferHTML += "					<div class=\"ws_external_links_content ws_selection\">";
		snifferHTML += "						<div class=\"ws_explicit_title\">External Links:<\/div>";
		snifferHTML += "						<div class=\"ws_external_links_content_internal\">";
		snifferHTML += "						<\/div>";
		snifferHTML += "					<\/div>";
		snifferHTML += "					<div class=\"ws_related_articles_content ws_selection\">";
		snifferHTML += "						<div class=\"ws_explicit_title\">Related to this article:<\/div>";
		snifferHTML += "						<div class=\"ws_related_articles_content_internal\">";
		snifferHTML += "						<\/div>";
		snifferHTML += "					<\/div>";
		snifferHTML += "					<div class=\"ws_images_content ws_selection\">";
		snifferHTML += "						<div class=\"ws_explicit_title\">Images in this article:<\/div>";
		snifferHTML += "						<div class=\"ws_images_content_internal\">";
		snifferHTML += "						<\/div>";
		snifferHTML += "					<\/div>";
		snifferHTML += "				<\/div>";
		snifferHTML += "			<\/div>";
		snifferHTML += "			<div class=\"ws_tags\">";
		snifferHTML += "			<\/div>";
		snifferHTML += "			<div class=\"ws_keyphrases\">";
		snifferHTML += "				<div class=\"ws_keyphrases_title\">Keywords or Keyphrases [<span class=\"ws_keyphrases_toggle\"><span class=\"ws_keyphrases_toggle_button\"  title=\"Click to hide keywords or keyphrases\">hide<\/span><span class=\"ws_keyphrases_toggle_button\" style=\"display: none\"  title=\"Click to show keywords or keyphrases\">show<\/span><\/span>]<\/div>";
		snifferHTML += "				<div class=\"ws_keyphrases_content\">";
		snifferHTML += "				<\/div>";
		snifferHTML += "			<\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "		<\/div>";
		snifferHTML += "	<\/div>";
		
		if(this.isAppended){
			$(this.appendToNode).append(snifferHTML)
		}else{
			$("#ws_area").append(snifferHTML);
		}
		this.hrefNode.append("<div class=\"ws_visited\">"+this.uid+"<\/div>");
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
			this.nodeBacklayer = this.nodeDialog.find(".ws_backlayer");
			this.nodeFrontlayer = this.nodeDialog.find(".ws_frontlayer");
			this.nodeTitle = this.nodeDialog.find(".ws_title");
			this.nodeSummaryNav = this.nodeDialog.find(".ws_summary_nav");
			this.nodeTocNav = this.nodeDialog.find(".ws_toc_nav");
			this.nodeMoreActionNav = this.nodeDialog.find(".ws_more_action_nav");
			this.nodeExternalLinksNav = this.nodeDialog.find(".ws_external_links_nav");
			this.nodeBacklinksNav = this.nodeDialog.find(".ws_backlinks_nav");
			this.nodeRelatedArticlesNav = this.nodeDialog.find(".ws_related_articles_nav");
			this.nodeImagesNav = this.nodeDialog.find(".ws_images_nav");
			this.nodeMoreActionMenu = this.nodeMoreActionNav.find(".ws_more_action_menu");
			this.nodeSummaryContent = this.nodeDialog.find(".ws_summary_content");
			this.nodeTocContent = this.nodeDialog.find(".ws_toc_content");
			this.nodeExternalLinksContent = this.nodeDialog.find(".ws_external_links_content");
			this.nodeRelatedArticlesContent = this.nodeDialog.find(".ws_related_articles_content");
			this.nodeBacklinksContent = this.nodeDialog.find(".ws_backlinks_content");
			this.nodeImagesContent = this.nodeDialog.find(".ws_images_content");
			this.nodeDisplayedContent = this.nodeDialog.find(".ws_displayed_content");
			this.nodeKeyphrases = this.nodeDialog.find(".ws_keyphrases");
			this.nodeKeyphrasesToggle = this.nodeKeyphrases.find(".ws_keyphrases_toggle");
			this.nodeKeyphrasesToggleButton = this.nodeKeyphrases.find(".ws_keyphrases_toggle_button");
			this.nodeKeyphrasesContent = this.nodeKeyphrases.find(".ws_keyphrases_content");
			this.nodeCloseButton = this.nodeDialog.find(".ws_close_button");
			this.nodeHoldButton = this.nodeDialog.find(".ws_hold_button");
			this.nodeCollapseButton = this.nodeDialog.find(".ws_collapse_button");
			this.nodeCollapsable = this.nodeDialog.find(".ws_collapsable");
			this.autoClose;
			this.autoCollapse;
			this.switchFadeInterval = 120;
			this.isLockByChild = false;
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
	 * Closes the snippet dialog.
	 * @return -
	 */
	this.closeDialog = function() {
		this.sdNodes.isLockByChild = false; // !!!!!!!!!!! FIXME
		var parentSdNodes = this.parentSnifferSdNodes;
		if (parentSdNodes != null) {
			parentSdNodes.isLockByChild = false; // !!!!!!!!!!! FIXME
		}
		
		this.sdNodes.nodeDialog.fadeOut(240);
		
		// reset autoclose & autocollapse state.
		enableAutoClose(this.sdNodes);
		disableAutoCollapse(this.sdNodes);
		
		// unbind events.
//		this.dNode.unbind();
//		this.dNode.find("*").unbind();
//		alert("unbind:" + this.dNode);
	};
	
	/**
	 * Detects mouse away for easy reversal of opening snippet and for fast closing mechanism.
	 * This method has incompatibility issue with IE.
	 * @return -
	 */
	this.detectMouseAwayOnce = function() {
		var thisSniffer = this;
		// detecting if mouse go away from snippet
		var recursion = function() {
			$().one("mousemove", function() {
				if (thisSniffer != null) {
					var x = thisSniffer.sdNodes.nodeDialog.css("left");
					var y = thisSniffer.sdNodes.nodeDialog.css("top");
					var xPlus = parseInt(x.replace(/px/gi, ""))+mSpaceX;
					var yPlus = parseInt(y.replace(/px/gi, ""))+mSpaceY;
					x = x.substring(0, x.length-2);
					y = y.substring(0, y.length-2);
					if (mPosX < x || mPosY < y) {
						if (thisSniffer.sdNodes.autoClose && !thisSniffer.sdNodes.autoCollapse) {
							thisSniffer.closeDialog();
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
	 * Reopens or repositions the snippet dialog.
	 * @param mPosXVar initial x-axis at reopening the snippet dialog
	 * @param mPosYVar initial y-axis at reopening the snippet dialog
	 * @return -
	 */
	this.reopenDialog = function(mPosXVar, mPosYVar) {
		this.mPosX = mPosXVar;
		this.mPosY = mPosYVar;
		
		// set dialog position
		this.sdNodes.nodeDialog.css("left", this.mPosX);
		this.sdNodes.nodeDialog.css("top", this.mPosY);
		this.sdNodes.nodeDialog.css("opacity", "1");
		
		// handle z-index
		this.sdNodes.nodeDialog.css("z-index", ++countZ);
		
		this.dNode.fadeIn(240);
		this.detectMouseAwayOnce();
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
		// }
		
		// handle navigation for content selection {
		var selectNav = function(selectedNavNode, selectedContentNode, fadeInterval) {
			sdNodes.nodeDisplayedContent.fadeOut(fadeInterval, function() {
				sdNodes.nodeDisplayedContent.find(".ws_selection").removeClass("ws_selected_content");
				sdNodes.nodeDisplayedContent.find(".ws_selection").addClass("ws_unselected_content");
				selectedContentNode.removeClass("ws_unselected_content");
				selectedContentNode.addClass("ws_selected_content");
				
				sdNodes.nodeDisplayedContent.fadeIn(fadeInterval);
				
				sdNodes.nodeDialog.find(".ws_nav div").removeClass("ws_selected_nav");
				selectedNavNode.addClass("ws_selected_nav");
			});
		};
		
		var selectNavWithDefaultFading = function(selectedNavNode, selectedContentNode) {
			selectNav(selectedNavNode, selectedContentNode, sdNodes.switchFadeInterval);
		};

		selectNav(sdNodes.nodeSummaryNav, sdNodes.nodeSummaryContent, 0);

		sdNodes.nodeSummaryNav.click(function() {
			selectNavWithDefaultFading(sdNodes.nodeSummaryNav, sdNodes.nodeSummaryContent);
		});
		
		if (enableHeadings) {
			sdNodes.nodeTocNav.click(function() {
				selectNavWithDefaultFading(sdNodes.nodeTocNav, sdNodes.nodeTocContent);
				
	//			// set ToC
	//			var targetNode = sdNodes.nodeDialog.find(".ws_toc_content");
	//			if (!targetNode.hasClass("done")) {
	//				thisSniffer.setToc(thisSniffer.mainSniffer, targetNode);
	//				targetNode.addClass("done");
	//			}
			});
		}
		
		if (enableExtraFeatures)
		sdNodes.nodeMoreActionNav.hover(function() {
			sdNodes.nodeMoreActionMenu.css("display", "table-cell");
			sdNodes.nodeMoreActionNav.mouseleave(function() {
				sdNodes.nodeMoreActionMenu.css("display", "none");
			});
		});
		
		if (enableExternalLinks) {
			sdNodes.nodeExternalLinksNav.click(function() {
				selectNavWithDefaultFading(sdNodes.nodeExternalLinksNav, sdNodes.nodeExternalLinksContent);
				
	//			// set External Links
	//			var targetNode = sdNodes.nodeDialog.find(".ws_external_links_content_internal");
	//			if (!targetNode.hasClass("done")) {
	//				thisSniffer.setExternalLinks(thisSniffer.mainSniffer, targetNode);
	//				targetNode.addClass("done");
	//			}
			});
		}
		
		if (enableInternalLinks) {
			sdNodes.nodeRelatedArticlesNav.click(function() {
				selectNavWithDefaultFading(sdNodes.nodeRelatedArticlesNav, sdNodes.nodeRelatedArticlesContent);
				
	//			// set Internal Links
	//			var targetNode = sdNodes.nodeDialog.find(".ws_related_articles_content_internal")
	//			if (!targetNode.hasClass("done")) {
	//				thisSniffer.setInternalLinks(thisSniffer.mainSniffer, targetNode);
	//				monitorLink(targetNode, sdNodes);
	//				targetNode.addClass("done");
	//			}
			});
		}
		
		if (enableBacklinks) {
			sdNodes.nodeBacklinksNav.click(function() {
				selectNavWithDefaultFading(sdNodes.nodeBacklinksNav, sdNodes.nodeBacklinksContent);
				
//				// set Backlinks
//				var targetNode = sdNodes.nodeDialog.find(".ws_backlinks_content_internal");
//				if (!targetNode.hasClass("done")) {
//					thisSniffer.setBacklinks(thisSniffer.mainSniffer, targetNode);
//					targetNode.addClass("done");
//					monitorLink(targetNode);
//				}
			});
		}
		
		if (enableImages) {
			sdNodes.nodeImagesNav.click(function() {
				selectNavWithDefaultFading(sdNodes.nodeImagesNav, sdNodes.nodeImagesContent);
				
	//			// set Images
	//			var targetNode = sdNodes.nodeDialog.find(".ws_images_content_internal");
	//			if (!targetNode.hasClass("done")) {
	//				thisSniffer.setImages(thisSniffer.mainSniffer, targetNode);
	//				targetNode.addClass("done");
	//			}
			});
		}

		sdNodes.nodeKeyphrasesToggle.click(function() {
			sdNodes.nodeKeyphrasesToggleButton.toggle();
			sdNodes.nodeKeyphrasesContent.slideToggle(240);
		});
		
		// handle navigation for content selection }
		
		// handle mouse {
		// handle close button
		sdNodes.nodeCloseButton.click(function() {
			thisSniffer.closeDialog();
		});
		if(!this.isAppended)
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
			
			if (sdNodes.autoCollapse && !sdNodes.isLockByChild) {
				sdNodes.nodeDialog.css("z-index", ++countZ);
				sdNodes.nodeCollapsable.slideDown("normal");
			}
			
			sdNodes.nodeFrontlayer.one("mouseleave", function() {
				timeoutLeave = setTimeout(function() {	
					if (sdNodes.autoClose && !sdNodes.autoCollapse && !sdNodes.isLockByChild) {
						thisSniffer.closeDialog();
					} else {
						if (sdNodes.autoCollapse && !sdNodes.isLockByChild) {
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
		
		sdNodes.nodeDisplayedContent.hover(
			function() {
				cancelLeaveContent();
				sdNodes.nodeDisplayedContent.mousedown(function() {
					cancelStop();
					cancelHover();
				});
				sdNodes.nodeDisplayedContent.mousemove(function() {
					// if mouse move cancel stop
					cancelStop();
					cancelHover();
					var onmousestop = function() {
						hovertimeout = setTimeout(function(e) {
							sdNodes.nodeDisplayedContent.animate({
								maxHeight: "330px"
							});
						}, 0);
					};
					
					// execute stop thread after defined period
					stop = setTimeout(onmousestop, 360);
				});
			}, function() {
				cancelStop();
			}
		);
		
		sdNodes.nodeDragger.hover(function() {
			// cancel stop thread if mouse leaves the link
			leaveContent = setTimeout(function() {
				cancelStop();
				sdNodes.nodeDisplayedContent.animate({
					maxHeight: "200px"
				});
			}, 360);
		});
		
		// handle autozoom }
		// handle mouse }
	};
	
	/**
	 * Creates the draggable dialog.
	 * @return -
	 */
	this.createDialog = function() {
		this.initializeNode();
		var thisSniffer = this;
		var sdNodes = this.sdNodes;
		
		// set dialog position
		if(!this.isAppended){
			sdNodes.nodeDialog.css("left", this.mPosX);
			sdNodes.nodeDialog.css("top", this.mPosY);
			
			// create draggable
			sdNodes.nodeDialog.draggable({
				opacity: 0.85, 
				handle: sdNodes.nodeDragger
			});
			
			// handle z-index
			sdNodes.nodeDialog.css("z-index", ++countZ);
		}
		
		
		
		this.monitorEvents();
		thisSniffer = null;
	};
	
	// set title to dialog
	this.setTitle = function(node) {
		node.html(this.title);
	};
	
	// set annotation to dialog
	this.extractAnnotation = function(val, dNode) {
		mainSniffer = val;
		// set Summary
		this.setSummary(val, dNode.find(".ws_summary_content"));
		// set Tags
		this.setTags(val, dNode.find(".ws_tags"));
		
		if (enableHeadings) {
			// set ToC
			var targetNode = dNode.find(".ws_toc_content");
			if (!targetNode.hasClass("done")) {
				this.setToc(mainSniffer, targetNode);
				targetNode.addClass("done");
			}
		}
		
		if (enableInternalLinks) {
			// set Internal Links
			var targetNode = dNode.find(".ws_related_articles_content_internal")
			if (!targetNode.hasClass("done")) {
				this.setInternalLinks(mainSniffer, targetNode);
				targetNode.addClass("done");
			}
		}
		
		if (enableExternalLinks) {
			// set External Links
			var targetNode = dNode.find(".ws_external_links_content_internal");
			if (!targetNode.hasClass("done")) {
				this.setExternalLinks(mainSniffer, targetNode);
				targetNode.addClass("done");
			}
		}
		
		if (enableBacklinks) {
			// set Backlinks
			var targetNode = dNode.find(".ws_backlinks_content_internal");
			if (!targetNode.hasClass("done")) {
				this.setBacklinks(mainSniffer, targetNode);
				targetNode.addClass("done");
			}
		}
		
		if (enableImages) {
			// set Images
			var targetNode = dNode.find(".ws_images_content_internal");
			if (!targetNode.hasClass("done")) {
				this.setImages(mainSniffer, targetNode);
				targetNode.addClass("done");
			}
		}
		
		dNode.find(".ws_loading").css("display", "none");
		dNode.find(".ws_content").css("display", "block");
		
		monitorLink(dNode, this.sdNodes);
	};
	
	// set annotation to dialog
	this.extractKeyphrases = function(val, dNode) {
		// set Keyphrases
		this.setKeyphrases(val, dNode.find(".ws_keyphrases_content"));
	};
	
	/**
	 * Sets tags to dialog.
	 * @param val WikiSniffer object
	 * @param node root node of tag
	 * @return -
	 */
	this.setTags = function(val, node) {
		var tagNode = node;
		var tags = val.tags;
		
		var tagText = "";
		var i;
		
		// handle tags
		if (tags.length > 0) {
			tagText += "Categories: "
		}
		
		var limit = 5;
		var condition = function(i) {
			i < tags.length;
		};
		for (i = 0; i < tags.length;) {
			tagText += "<a href=\""+tags[i].href+"\">"+tags[i].text+"</a>";
			
			i++;
			if (i < tags.length) {	
				if (i == limit) {
					tagText += "<span class=\"ws_more_tags\" style=\"display: none;\">";
				}
				
				tagText += " ∙ ";
			}
		}
		if (tags.length > limit) {
			tagText += "<\/span>";
			tagText += " <span class=\"ws_tags_toggle\"><span class=\"ws_tags_toggle_button\" title=\"Click to show more categories\">more_»<\/span><span class=\"ws_tags_toggle_button\" style=\"display: none\"  title=\"Click to show hide some categories\">«_less<\/span><\/span>";
		}
		
		tagNode.html(tagText);
		
		var nodeTagsToggle = tagNode.find(".ws_tags_toggle");
		var nodeTagsToggleButton = tagNode.find(".ws_tags_toggle_button");
		var nodeMoreTags = tagNode.find(".ws_more_tags");
		
		var sdNodes = this.sdNodes;
		if (nodeTagsToggle.html() != null) {
			nodeTagsToggle.click(function() {
				nodeTagsToggleButton.toggle();
				nodeMoreTags.slideToggle(240);
			});
		}
	};
	
	/**
	 * Sets table of contents to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the ToC's content should be put
	 * @return -
	 */
	this.setToc = function(val, node) {
		var tocGen = {
			headings: val.headings,
		    depth: 1,
		    firstLevel: null,
		    currentLevel: null,
		    sourceCurrentLevel: null,
			hrefLink: this.hrefLink,
		    execute: function () {
				var content = "";
				
		        if (this.headings.length > 0) {
		        	// begin with headings[0]
		        	this.firstLevel = this.headings[0].level;
		        	this.tocCurrentLevel = this.firstLevel;
		        	this.sourceCurrentLevel = this.firstLevel;
		        	
		        	var tip = "Click to go to this section";
					var headingTitle = this.headings[0].text;
		        	content += "<ul><li>"+"<a href=\""+this.hrefLink+"#"+headingTitle.replace(/ /g,"_")+"\" class=\"ws_excluded_link\" title=\""+tip+"\">"+headingTitle+"<\/a>";
		        	
		        	// handle heading level from headings[1] to headings[n].
		        	for (var i = 1; i < this.headings.length; i++) {
		        		var newHeadingLevel = this.headings[i].level;
		        		var lastHeadingLevel = this.tocCurrentLevel;
		        		
		        		var tocLevelChange = newHeadingLevel - lastHeadingLevel;
		        		var levelChange = newHeadingLevel - this.sourceCurrentLevel;
						
						headingTitle = this.headings[i].text;
						headingTitleInHref = "<a href=\""+this.hrefLink+"#"+headingTitle.replace(/ /g,"_")+"\" class=\"ws_excluded_link\" title=\""+tip+"\">"+headingTitle+"<\/a>";
		        		if (levelChange > 0) {
		        			// new heading is sub heading.
		        			this.tocCurrentLevel++;
		        			this.sourceCurrentLevel = newHeadingLevel;
		        			
		        			content += "<ul><li>";
		        			
							
		        			content += headingTitleInHref;
		        		} else if (levelChange == 0) {
		        			// new heading is in the same level as previous.
		        			content += "</li><li>"+headingTitleInHref;
		        		} else {
		        			// previous heading is deeper level than the new one.
		        			for (var k = tocLevelChange; k < 0; k++) {
		        				content += "</li></ul>";
		        			}
		        			
		        			this.tocCurrentLevel = newHeadingLevel;
		        			this.sourceCurrentLevel = newHeadingLevel;
		        			content += "</li><li>"+headingTitleInHref;
		        		}
		        	}
		        	
		        	// close li & lu for headings[0]
		        	for (; this.currentLevel >= this.firstLevel; this.currentLevel--) {
		        		content += "</li></ul>";
		        	}
		        }
		        
				if (content == "") {
					content = "<div class=\"ws_empty_result\">This article does not have table of contents.<\div>"
				}
				
		        // add headings to TOC-region.
		        node.html(content);
		    }
		};
		tocGen.execute();
	};
	
	/**
	 * Sets external links to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the external links' content should be put
	 * @return -
	 */
	this.setExternalLinks = function(val, node) {
		var content = "";
		var links = val.externalLinks;
		var i;
		for (i = 0; i < links.length; i++) {
			content += "<li><a href=\""+links[i].href+"\" class=\"ws_external_link_item\">"+links[i].text+"</a></li>\n";
		}
		
		if (content == "") {
			content = "<div class=\"ws_empty_result\">No external link is found in this article.<\div>"
		} else {
			content = "<ul>\n"+ content +"</ul>";
		}
		
		node.html(content);
	};
	
	/**
	 * Sets internal links to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the internal links' content should be put
	 * @return -
	 */
	this.setInternalLinks = function(val, node) {
		var content = "";
		var links = val.internalLinks;
		var i;
		for (i = 0; i < links.length; i++) {
			content += "<li><a href=\""+links[i].href+"\">"+links[i].text+"</a></li>\n";
		}
		
		if (content == "") {
			content = "<div class=\"ws_empty_result\">No article is used by this article.<\div>";
		} else {
			content = "<ul>\n"+content+"</ul>";
		}
		
		node.html(content);
	};
	
	/**
	 * Sets backlinks to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the backlinks' content should be put
	 * @return -
	 */
	this.setBacklinks = function(val, node) {
		var content = "";
		var links = val.backlinks;
		var i;
		for (i = 0; i < links.length; i++) {
			content += "<li><a href=\""+links[i].href+"\">"+links[i].text+"</a></li>\n";
		}
		
		if (content == "") {
			content = "<div class=\"ws_empty_result\">No article links here.</div>"
		} else {
			content = "<ul>\n"+content+"</ul>";
		}
		
		node.html(content);
	};
	
	/**
	 * Sets images to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the backlinks' content should be put
	 * @return -
	 */
	this.setImages = function(val, node) {
		var content = "";
		var links = val.images;
		var i;
		for (i = 0; i < links.length; i++) {
			content += "<li><img src=\""+links[i].href+"\" alt=\""+links[i].description+"\" style=\"max-width:250px\"><br />"+links[i].description+"</li>\n";
		}
		
		if (content == "") {
			content = "<div class=\"ws_empty_result\">No image is found in this articles.</div>"
		} else {
			content = "<ul>\n"+content+"</ul>";
		}
		
		node.html(content);
	};
	
	/**
	 * Sets summary to dialog.
	 * @param val WikiSniffer object
	 * @param node the node where the backlinks' content should be put
	 * @return -
	 */
	this.setSummary = function(val, node) {
		var summary = val.summary;
		
		var images = val.images;
		if (images.length > 0) {
			summary = "<img src=\""+images[0].href+"\" style=\"float:right;margin-left:10px;margin-top:5px;max-width:77px\">"+summary;
		}
		
		if (enableExtraSummary) {
			// extra summary {
			var extraSummary = val.extraSummary;
			
			if (extraSummary.length > 0){
				summary += "<div class=\"ws_extra_summary\"><span class=\"ws_extra_summary_content\" style=\"display:none\"><p>"+extraSummary+"<\/p><\/span><span class=\"ws_extra_summary_toggle\" title=\"Click to show longer summary\">more_»<\/span><span class=\"ws_extra_summary_toggle\" style=\"display:none\" title=\"Click to show less summary\">«_less<\/span><div>";
			}
			
			summary = "<div style=\"text-align:justify\">"+summary+"</div>";
			
			node.html(summary);
			var nodeExtraSummary = this.sdNodes.nodeDialog.find(".ws_extra_summary");
			var nodeExtraSummaryContent = this.sdNodes.nodeDialog.find(".ws_extra_summary_content");
			var nodeExtraSummaryToggle = this.sdNodes.nodeDialog.find(".ws_extra_summary_toggle");
			
			if (nodeExtraSummary.html() != null) {
				nodeExtraSummaryToggle.click(function() {
					nodeExtraSummaryToggle.toggle();
					var speed = 600;
					nodeExtraSummaryContent.slideToggle(speed);
				});
			}
			// extra summary }
		} else {
			node.html(summary);
		}
	};
	
	/**
	 * Sets keywords/keyphrases to dialog
	 * @param val WikiSniffer object
	 * @param node the node where the keyphrases' content should be put
	 * @return -
	 */
	this.setKeyphrases = function(val, node) {
		var keyphrases = val;
		var kx = new Array();
		var pct = 75.0 / keyphrases.length;
		var j = 0;
		for (var i = 0; i < keyphrases.length; i++) {
			kx[i] = new Array();
			kx[i][0] = keyphrases[i];
			kx[i][1] = 150-j;
			j=j+pct;
		}
		
		var sortedKx = kx.sort(function(a, b) {
			if (a[0] < b[0]) return -1;
			else if (a[0] == b[0]) return 0;
			else return 1;
		});
		
		var content = "";
		if (sortedKx.length > 0) {
			for (var i = 0; i < sortedKx.length;) {
				var keyphrase = sortedKx[i][0];
				content += "<span style=\"font-size:"+sortedKx[i][1]+"%\"><a href=\""+this.tagSearch+escape(sortedKx[i][0])+"\" title=\"Click to search for &quot;"+keyphrase+"&quot;\" class=\"ws_keyphrase\">"+keyphrase+"<\/a><\/span>";
				
				i++;
				if (i < sortedKx.length) {
					content += " ∙ ";
				}
			}
			content += "";
		} else {
			content += "<div class=\"ws_empty_result\">No keyphrases for this article.</div>";
		}
		node.html(content);
	};
	
	/**
	 * Class that saves the state of even and odd. 
	 * @return -
	 */
	function OddOrEven() {
		this.odd = "ws_switcher_odd_nav";
		this.even = "ws_switcher_even_nav";
		
		this.status = "ws_switcher_even_nav";
		
		/**
		 * Decides to use odd or even style after method get is called. 
		 * @return even style or odd style
		 */
		this.get = function() {
			if (this.status == this.even) {
				this.status = this.odd;
			} else {
				this.status = this.even;
			}
			
			return this.status;
		};
	}
}