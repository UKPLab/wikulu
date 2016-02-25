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
document.write("<script type=\"text/javascript\" src=\"http://maps.google.com/maps/api/js?sensor=false\"></script>");
var littleGoogleMapsWindow = null;
var routeGoogleMapsWindow = null;
var markerPath = "";
var routeDrawer = null;
var twikiMenuWidth = 250;
var twikiHeaderHeight = 70;
var drawingX = twikiMenuWidth;
var drawingY = twikiHeaderHeight;
var drawingWidth = window.innerWidth - drawingX;
var drawingHeight = window.innerHeight - drawingY;
var mapWindowWidth = drawingWidth / 2;
var mapWindowHeight = drawingHeight;
var mapWindowX = drawingX + (drawingWidth - mapWindowWidth) - 25;
var mapWindowY = drawingY + (drawingHeight - mapWindowHeight) - 5;
var documentWidth = mapWindowX - drawingX - 10;

$(document).ready(function() {
	littleGoogleMapsWindow = new googleMapsWindow(150,150,0);
	routeGoogleMapsWindow = new googleMapsWindow(mapWindowWidth,mapWindowHeight,100);
	routeGoogleMapsWindow.moveTo(mapWindowX, mapWindowY);
});

function runTravelogue(pluginid) {
	setBusy(true);
	var jsonString = '{ "class" : "TraveloguePlugin" , "arguments" : { "text" : "' + encodeURIComponent(WikiAdapter.getHtmlContent()) + '" } }';
	Wikulu.perform(jsonString, showTravelogueResult);
}


function showTravelogueResult(content) {
	setBusy(false);
	var jsonContent = eval("("+content+")");

	var route = jsonContent["route"][0];
	var texts = jsonContent["texts"][0];
	
	var oldPlainText = WikiAdapter.getDocumentText();
	var newText = "";

	var routeInformation = new Array();
	for (var n = 0; n < route.length; n++) {
		routeInformation[n] = {"name" : route[n].name[0],
		                       "id" : route[n].id[0]};
	}
	$(WikiAdapter.contentIdentifier).width(documentWidth);
	
	for (var n = 0; n < texts.length; n++) {
		var beginPath = texts[n].beginPath[0];
		var endPath = texts[n].endPath[0];
		var beginOffset = texts[n].beginOffset[0];
		var endOffset = texts[n].endOffset[0];
		var plainBegin = texts[n].plainBegin[0];
		var plainEnd = texts[n].plainEnd[0];
		var location = texts[n].name[0];
		partText = oldPlainText.substring(plainBegin,plainEnd);
		partText = partText.replace(location,"<span style=\"background-color:#FFFF00;font-weight:bold\" onmouseover=\"showLittleWindow('"+location+"')\" onmouseout=\"closeWindow()\">"+location+"</span>");
		newText += "<div id=\"view-"+texts[n].id[0]+"\" style=\"cursor:pointer;\" onClick=\"selectLocation(" + texts[n].id[0]+ ",0,false)\">"+partText+"</div>";
	}
	WikiAdapter.setDocumentContent(newText.replace("\n", "<br />\n"));

	routeGoogleMapsWindow.drawRoute(routeInformation);
	routeGoogleMapsWindow.open();
}


function GetMousePosition(callback){
	window.document.onmousemove = function (event) {
		window.document.onmousemove = null;
		if(!event) {
			event = window.event;
		}
		var body = (window.document.compatMode && window.document.compatMode == "CSS1Compat") ?  window.document.documentElement : window.document.body;
		var top = event.pageY ? event.pageY - body.scrollTop : event.clientY - body.clientTop;//+ body.scrollTop ;
		var left =  event.pageX ? event.pageX - body.scrollLeft: event.clientX - body.clientLeft; //+ body.scrollLeft;
		callback(left,top);
	}
}


function showLittleWindow(name) {
	littleGoogleMapsWindow.setPositionToTown(name);
	GetMousePosition(setWindowToMouse)
}

function setWindowToMouse(left, top) {
	littleGoogleMapsWindow.moveTo(left+50,top-75);
	littleGoogleMapsWindow.open();
}

function closeWindow() {
	littleGoogleMapsWindow.close();
}

function isAlreadyReplaced(replaceArray, replaceExpr) {
	for (var n = 0; n < replaceArray.length; n++) {
		if (replaceArray[n] == replaceExpr)
			return true;
	}
	replaceArray[replaceArray.length] = replaceExpr;
	return false;
}



function googleMapsWindow(width, height, textwidth, renderObject) {
	var self = this;
	this.geocoder = new google.maps.Geocoder();
	this.window = new dialogWindow(width, height, undefined, renderObject);
	this.directionsDisplay = new google.maps.DirectionsRenderer();
	var latlng = new google.maps.LatLng(49.877315,8.656021);
	var myOptions = {
		zoom: 10,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	
	if (textwidth == undefined) {
		textwidth = 0;
	}
	
	//the Map DIV
	this.theMapDiv = document.createElement("div");
	this.theMapDiv.style.cssFloat = "right";
	this.theMapDiv.style.width = (width-((textwidth!=0) ? textwidth+10 : 0))+"px";
	this.theMapDiv.style.height = "100%";
	this.theMapDiv.style.padding = "0px";
	this.theMapDiv.style.margin = "0px";
	
	this.theRouteDiv = null;
	if (textwidth!=0) {
		// the Route DIV
		this.theRouteDiv = document.createElement("div");
		this.theRouteDiv.style.cssFloat = "left";
		this.theRouteDiv.style.width = textwidth+"px";
		this.theRouteDiv.style.height = "100%";
		this.theRouteDiv.style.padding = "0px";
		this.theRouteDiv.style.margin = "0px";
		this.theRouteDiv.style.overflow = "scroll";
		this.theRouteDiv.innerHTML = "&nbsp;";
		this.window.getRenderElement().appendChild(this.theRouteDiv);
	}
	
	this.window.getRenderElement().appendChild(this.theMapDiv);
	
	this.window.getRenderElement().style.backgroundColor = "#FFFFFF";
	
	this.map = new google.maps.Map(this.theMapDiv, myOptions);
	this.directionsDisplay.setMap(this.map);
	this.routeDrawer = new drawRoute(this.map, this.directionsDisplay);
	
	this.open = function() {
		this.window.open();
	};
	
	this.close = function() {
		this.window.close();
	};
	
	this.setPositionToTown = function(name) {
		this.geocoder.geocode(
			{address : name},
			function (result,state) {
				if (state == 'OK') {
					var latlng = result[0].geometry.location;
					self.map.panTo(latlng);
					new google.maps.Marker({map : self.map, position : latlng, visible : true});
				}
			});
		
	};
	
	this.setPositionToLocation = function(latlng) {
		self.map.panTo(latlng);
	}
	
	this.moveTo = function(x,y) {
		this.window.moveTo(x,y);
	};
	
	this.drawRoute = function(route) {
		if (this.theRouteDiv!=null) {
			var table = "<table style=\"width:100%\">";
			for (var n = 0; n < route.length; n++) {
				table+="<tr><td id=\"route-" + n
									+ "\" style=\"cursor:pointer;background-color:"
									+ "#FFFFFF"
									+ "\" onClick=\"selectLocation(" + n
									+ ",1,false)\">" + route[n].name + "</td></tr>";
			}
			table+="</table>";
			this.theRouteDiv.innerHTML = table;
		}
		this.routeDrawer.draw(route);
	};
}

var dialogWindow_counter = 1;
function dialogWindow(width, height, title, elementToAdd) {
	this.id = dialogWindow_counter;
	dialogWindow_counter++;
	var self = this;
	this.isMoving = false;
	this.moveOffsetX = 0;
	this.moveOffsetY = 0;
	var divMode = (elementToAdd!=undefined);
	
	this.mainElement = document.createElement('div');
	this.mainElement.id = "dialogWindowID_"+this.id;
	this.mainElement.style.top = "0px";
	this.mainElement.style.left = "0px";
	this.mainElement.style.width = width+((typeof(width)=='string') ? "" : "px");
	this.mainElement.style.height = height+((typeof(width)=='string') ? "" : "px");
	this.mainElement.style.border = "1px solid #000000";
	if (!divMode) {
		this.mainElement.style.position = "fixed";
	}
	this.mainElement.style.visibility = "hidden";
	this.mainElement.style.zIndex = 100;
	if (divMode) {
		elementToAdd.appendChild(this.mainElement);
	} else {
		document.body.appendChild(this.mainElement);
	}
	if (title!=undefined) {
		this.header = document.createElement('div');
		this.header.id = "dialogWindowID_title_"+this.id;
		this.header.innerHTML = "<b>"+title+"</b>";
		this.header.style.backgroundColor = "#AACCFF";
		this.header.style.cursor = "move";
		this.header.style.height = "40px;";
		this.header.style.padding = "12px";
		this.header.onmousedown = function () {
			self.isMoving = true;
			self.moveOffsetX = null;
			self.moveOffsetY = null;
		}
		this.header.onmouseup = function () {
			self.isMoving = false;
		}
		this.header.onmouseout = function () {
			self.isMoving = false;
		}
		this.header.onmousemove = function (event) {
			if (self.isMoving) {
				if(!event) {
					event = window.event;
				}
				var body = (window.document.compatMode && window.document.compatMode == "CSS1Compat") ?  window.document.documentElement : window.document.body;
				var topOffset = event.pageY ? event.pageY : event.clientY + body.scrollTop - body.clientTop;
				var leftOffset =  event.pageX ? event.pageX : event.clientX + body.scrollLeft  - body.clientLeft;

				if (self.moveOffsetX==null) {
					self.moveOffsetX = leftOffset - extractNumber(self.mainElement.style.left);
					self.moveOffsetY = topOffset - extractNumber(self.mainElement.style.top);
				}
				self.moveTo(leftOffset-self.moveOffsetX,topOffset-self.moveOffsetY);
				clearSelection();
			}
		}
		this.mainElement.appendChild(this.header);
	}
	this.renderElement = document.createElement('div');
	this.renderElement.id = "dialogWindowID_main_"+this.id;
	this.renderElement.innerHTML = "&nbsp;";
	this.renderElement.style.width = "100%";
	this.renderElement.style.height = (height - ((title==undefined) ? 0 : 40))+"px";
	this.renderElement.backgroundColor = "#FFFFFF";
	this.mainElement.appendChild(this.renderElement);
	//$("body").append("<div id=\"dialogWindowID_"+dialogWindow_counter+"\" style=\"position:fixed;visibility:hidden \">&nbsp;<\/div>");
	//this.mainElement = document.getElementById();
	
	this.open = function() {
		this.mainElement.style.visibility = "visible";
	};
	
	this.close = function() {
		this.mainElement.style.visibility = "hidden";
	};
	
	this.getRenderElement = function() {
		return this.renderElement;
	};
	
	this.moveTo = function(x,y) {
		this.mainElement.style.top = y+"px";
		this.mainElement.style.left = x+"px";
	};
}

function extractNumber(size) {
	var number = size.substring(0,size.length-2);
	return parseInt(number);
}

/**
 * Entfernt die Selektion im Dokument
 * @return void
 */
function clearSelection() {
	var sel;
	if(document.selection && document.selection.empty){
		document.selection.empty();
	} else if(window.getSelection) {
		sel=window.getSelection();
		if(sel && sel.removeAllRanges)
		sel.removeAllRanges() ;
	}
}

var mapMarkers = new Array();
var locationPositions = new Array();

/**
 * Legt ein 'Objekt' an auf ein directionsDisplay-Objekt
 * @param directionsDisplayObject (directionsDisplay) - Objekt auf dem die Route gezeichnet werden soll
 */
function drawRoute(map,directionsDisplayObject) {
	//Zeigt auf dieses Objekt. Ist wichtig, bei Callbacks
	var self = this;
	//Alle Position der Locations
	//var locationPositions = new Array();
	//Alle Marker
	//var mapMarkers = new Array();
	//map
	var map = map;
	//Display, auf dem gezeichnet werden soll
	this.directionsDisplay = directionsDisplayObject;
	
	this.geocoder = new google.maps.Geocoder();
	
	//Initialisiert einen DirectionsService um die Routen berechnen zu lassen
	this.directionsService = new google.maps.DirectionsService();
	
	//Die aktuell zu bearbeitende Route
	this.routeArray = null;
	
	//Die aktuelle Postion im routeArray
	this.routeArrayPos = 0;
	
	//Das bisherige Ergebnis der Berechnung
	this.goalResult = null;
	
	this.goalBounds = null;
	this.indexCounter = 0;
	
	//Ob die Berechnung der Route schon abgeschlossen ist
	this.finish = true;
	
	//Aktueller RoutenRequest (wird benötigt, falls der Server wegen einem OVER_QUERY_LIMIT einen Fehler zurückgibt, dass man die Anfrage nochmal zeitverzögert aufrufen kann)
	this.request = null;

	//Zeit/2, die standartmäßig gewartet werden soll, wenn ein OVER_QUERY_LIMIT-Fehler auftritt
	this.waittime = 400;
	
	/**
	 * Fängt an eine Route berechnen zu lassen und zeichnet sie anschließend
	 * @param route array(string) - Array der Route die gezeichnet werden soll. Sollte entweder Strings enthalten oder LatLng-Objekte.
	 */
	this.draw = function(route) {
		if (this.finish) {
			this.routeArray = route;
			this.goalArray = new Array();
			this.finish = false;
			this.routeArrayPos = 0;
			this.goalBounds = null;
			this.indexCounter = 0;
			mapMarkers = new Array();
			locationPositions = new Array();
			this.getInformation();
		} else
			alert("Objekt ist noch am Arbeiten! Nutzen Sie objekt.finish um zu überprüfen, ob sie bereits fertig ist!");
	};
	
	
	/**
	 * Wird von drawRoute() aufgerufen und versucht Ergebnisse von GoogleMaps über eine Abfolge von max. 10 Punkten zu bekommen und löscht die ersten 9 bearbeiteten aus dem routeArray.
	 */
	this.getInformation = function() {
		if (this.finish) {
			alert("Die Funktion wurde auf illegale Weise aufgerufen. Benutzen sie objekt.drawRoute(route) um eine Route zeichnen zu lassen!");
			return;
		}
//		if (this.routeArray[this.routeArrayPos].length==1) {
//			var latlng = this.routeArray[this.routeArrayPos][0];
//			var tempArray = new Array();
//			tempArray[0] = {
//				bounds: new google.maps.LatLngBounds(latlng,latlng),
//				legs: [{
//					start_location: latlng,
//					end_location: null,
//					steps: new Array()
//				}]
//			}
//			var temp = {
//				routes: tempArray	
//			};
//			this.routeCallback(temp, google.maps.DirectionsStatus.OK);
//			return;
//		}
		if (this.routeArray.length==1) {
			return;
		}
		var start = this.routeArray.shift();//Startpunkt rausholen
		var end = this.routeArray[0];//Endpunkt herausholen, aber drinnen lassen für eventuelle weitere Berechnungen
		this.request = {
   			origin:start.name, 
   			destination:end.name,
   			travelMode: google.maps.DirectionsTravelMode.DRIVING,
			unitSystem: google.maps.DirectionsUnitSystem.METRIC
		};//Request erstellen
		this.waittime = 400;//Die Standartzeit/2, die bei einem OVER_QUERY_LIMIT (zuviele Anfragen in einer Zeitspanne) gewartet werden soll
		this.callRouteService();
	};
	
	/**
	 * Ruft den Routen-Service von GoogleMaps auf mit dem aktuellen Request (this.request)
	 */
	this.callRouteService = function() {
		this.directionsService.route(this.request, function(result,status) { self.routeCallback(result,status); } );//Route berechnen lassen
	};
	
	/**
	 * Zeichnet die Route auf der GoogleMaps-Karte
	 * @return void
	 */
	this.drawRouteOnMap = function () {
		var array = this.goalResult.routes[0].legs;
		for (var n = 0; n<array.length;n++) {//Jeden Schritt durchgehen
			this.addMarker(array[n].start_location);//Marker setzen
			this.drawRouteSteps(array[n].steps);//Route zwischen zwei Orten zeichnen lassen
		}
		if (this.routeArray.length<=1 && array[n-1].end_location!=null) {
		//if (n-1>=0)//Endpunkt auch einzeichnen, falls vorhanden
			this.addMarker(array[n-1].end_location);
		}
	};
	
	/**
	 * Zeichnet die Route an Hand der Schritte
	 * @param steps Schritte
	 * @return void
	 */
	this.drawRouteSteps = function(steps) {
		if (steps.length==0)
			return;
		var temp = new Array();
		for (var n= 0;n<steps.length;n++) {
			temp = temp.concat(steps[n].path);
		}
		var step = new google.maps.Polyline({
		    path: temp,
		    strokeColor: "#0000FF",
		    strokeOpacity: 0.4,
		    strokeWeight: 5
		  });
		step.setMap(map);
	};
	
	/**
	 * Fügt einen Marker auf der Karte hinzu
	 * @param location Ort des Markers
	 * @return void
	 */
	this.addMarker = function (location) {
		var marker = new google.maps.Marker({
		      position: location, 
		      map: map,
		      title: ""
		      //icon: "/"+markerPath+this.indexCounter+".png"
		  });
		locationPositions[this.indexCounter] = location;
		mapMarkers[this.indexCounter] = marker;
		google.maps.event.addListener(marker, 'click', new Function("", "selectLocation("+this.indexCounter+", 2, false);"));
		this.indexCounter++;
	};
	
	/**
	 * Wartet auf die Antwort vom GoogleMaps-Server
	 * @param result (DirectionResult) - Ergebnis vom Server
	 * @param status - OK = Anfrage konnte ausgeführt werden. Weiteres unter http://code.google.com/intl/de-DE/apis/maps/documentation/javascript/services.html#DirectionsStatus
	 */
	this.routeCallback = function(result, status) {
		if (this.finish) {
			alert("Die Funktion wurde auf illegale Weise aufgerufen. Benutzen sie objekt.drawRoute(route) um eine Route zeichnen zu lassen!");
			return;
		}
		if (status == google.maps.DirectionsStatus.OK) {//Wenn der Status sagt, dass die Daten alle okay waren
			this.goalResult = result;
			this.drawRouteOnMap();
			if (this.goalBounds==null)
				this.goalBounds = result.routes[0].bounds;
			else
				this.goalBounds = this.goalBounds.union(result.routes[0].bounds);//Die Bounds anpassen
			if (this.routeArray.length>1)//Wenn noch mehr als 1 Element im Array ist (1 muss ja immer noch drin sein, da es ja der alte End- und neue Startpunkt ist)
				this.getInformation();//dann nächstes Ergebnis holen
			else {
				if (this.routeArray.length<=1) {
					map.fitBounds(this.goalBounds);
					this.finish = true;//Finish auf true setzen
				} else
					this.getInformation();//dann nächstes Ergebnis holen
			}
		} else if (status == google.maps.DirectionsStatus.OVER_QUERY_LIMIT) {//Wenn zuviele Anfragen innerhalb einer Zeitspanne geschickt wurden:
			this.waittime = this.waittime*2;//Wartezeit verdoppeln
			if (this.waittime>5000) {//Falls länger als 5 Sekunden gewartet werden muss -> Abbrechen
				alert("Die Ausführung der Routeberechnung dauert zu lange > 5s");
				return;
			} else//Ansonsten verzögert die Anfrage nochmal ausführen
				setTimeout(function () {self.callRouteService();}, this.waittime);
		} else if (status == google.maps.DirectionsStatus.ZERO_RESULTS) {
			this.geocoder.geocode(
					{address : name},
					function (result,state) {
						if (state == 'OK') {
							var latlng = result[0].geometry.location;
							self.addMarker(latlng)
						} else if (state == 'ZERO_RESULTS') {
							locationPositions[self.indexCounter] = null;
							mapMarkers[self.indexCounter] = null;
							self.indexCounter++;
						} else {
							alert(state);
						}
						self.getInformation();
					});
			//this.getInformation();//dann nächstes Ergebnis holen
		} else if (status == google.maps.DirectionsStatus.NOT_FOUND) {//----- ab jetzt kommen nur noch die einzelnen Fehlermeldungen -------
			alert("Anfrage konnte nicht ausgeführt werden: NOT_FOUND");
		} else if (status == google.maps.DirectionsStatus.MAX_WAYPOINTS_EXCEEDED) {//Sollte nicht auftreten, da ich dies ja gerade umgehe
			alert("Anfrage konnte nicht ausgeführt werden: MAX_WAYPOINTS_EXCEEDED");
		} else if (status == google.maps.DirectionsStatus.INVALID_REQUEST) {
			alert("Anfrage konnte nicht ausgeführt werden: INVALID_REQUEST");
		} else if (status == google.maps.DirectionsStatus.REQUEST_DENIED) {
			alert("Anfrage konnte nicht ausgeführt werden: REQUEST_DENIED");
		} else if (status == google.maps.DirectionsStatus.UNKNOWN_ERROR) {
			alert("Anfrage konnte nicht ausgeführt werden: UNKNOWN_ERROR");
		}
	};
}

var selectedID1 = null;
var selectedID2 = null;
var selectedID3 = -1;

/**
 * Wenn ein Ort markiert werden soll
 * @param id Position des Ortes in der Route
 * @param callID 0=Auswahl kam vom Text, 1=Auswahl kam von der Route, 2=Auswahl kam von der Karte
 * @param zoom true=Soll die Karte gezoomt werden
 * @return void
 */
function selectLocation(id, callID, zoom) {
	//Alles setzen für die Textauswahl
	if (selectedID1!=null) {
		selectedID1.style.backgroundColor = "#FFFFFF";
		selectedID1.style.borderTop = "0px solid #FFFFFF";
		selectedID1.style.borderBottom = "0px solid #FFFFFF";
	}
	selectedID1 = document.getElementById("view-"+id);
	selectedID1.style.backgroundColor = "#FFB70B";
	selectedID1.style.borderTop = "1px solid #000000";
	selectedID1.style.borderBottom = "1px solid #000000";
	if (callID!=0) {//Nur zum Textscrollen, wenn die Auswahl nicht von ihm kam
//		document.getElementById('travelogueContent').scrollTop = selectedID1.offsetTop;
		//selectedID1.focus();
		window.scrollTo(0, selectedID1.offsetTop);
	}
	
	//Alles setzen für die Routenauswahl
	if (selectedID2!=null) {
		selectedID2.style.border = "0px solid #FFFFFF";
		selectedID2.style.backgroundColor = "#FFFFFF";
	}
	selectedID2 = document.getElementById("route-"+id);
	selectedID2.style.border = "1px solid #000000";
	selectedID2.style.backgroundColor = "#FFB70B";
//	if (callID!=1) {//Nur zum aktuellen Ort scrollen, wenn die Auswahl nicht von der Route kam
//		document.getElementById('routeViewer').scrollTop = selectedID2.offsetTop;
//	}
	
	//Alles setzen für die Karte
	if (callID!=2 && locationPositions[id]!=null) {//Ort nur zentrieren, wenn die Auswahl nicht von der Karte kam
		routeGoogleMapsWindow.setPositionToLocation(locationPositions[id]);
	}
}