<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>


<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import = "com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import = "com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>

<portlet:defineObjects />
This is the <b> GIS Portlet </b> Page 1

<% 
final int fileCount = DLFileEntryLocalServiceUtil.getDLFileEntriesCount();
List<DLFileEntry> documents = DLFileEntryLocalServiceUtil.getDLFileEntries(0, fileCount);
List<DLFileEntry> textDocuments = new ArrayList<DLFileEntry>();

for(int i=0;i<documents.size();i++) {
	if(documents.get(i).getExtension().equals("txt")) {
		textDocuments.add(documents.get(i));
	}
}
%>
<portlet:actionURL name="loadFirstLines" var="loadFirstLinesURL">
</portlet:actionURL>

<aui:form action="<%= loadFirstLinesURL %>" method="post">
	<% for ( int i = 0; i < textDocuments.size(); i++ ) {
		%>
	<aui:input type="checkbox"  
				name="<%= \"document-\" + Long.toString(textDocuments.get(i).getFileEntryId()) %>" label="<%=textDocuments.get(i).getTitle() %>">
	</aui:input>
	<% 
	}
	%>
	<aui:input type="hidden" name="doumentListSize" value="<%= documents.size() %>"></aui:input>
	<aui:input type="submit" name="" value="Find Cities" />
</aui:form>

<style type="text/css">
#map_canvas {
    height: 500px;
    width: 400px;
}
</style>
 
<aui:script>
var A=new AUI();
var map;
var infowindow;
var geocoder;
var address = "UCLA";
var markers=[];
var input1;
function initialize() {
  var pyrmont = new google.maps.LatLng(-33.8665433, 151.1956316);
  geocoder = new google.maps.Geocoder();
  map = new google.maps.Map(document.getElementById('map_canvas'), {
    center: pyrmont,
    zoom: 15,
    mapTypeId : google.maps.MapTypeId.ROADMAP
  });

  
  //infowindow = new google.maps.InfoWindow();
  //var service = new google.maps.places.PlacesService(map);
  //service.nearbySearch(request, callback);
  codeAddress();
  
}

function callback(results, status) {
  if (status == google.maps.places.PlacesServiceStatus.OK) {
    for (var i = 0; i < results.length; i++) {
      createMarker(results[i]);
    }
  }
}

function codeAddress() {
geocoder.geocode( { 'address': address}, function(results, status) {
    if (status == google.maps.GeocoderStatus.OK) {
      map.setCenter(results[0].geometry.location);
      for (var i = 0; i < results.length; i++) {
    	  var place = results[i];
          createMarker(results[i]);
      }
      
      // var marker = new google.maps.Marker({
       //map: map,
       //position: results[0].geometry.location
     //});
   
   
    } 
    else {
     //initialize2();
    }
  });
}
  

function createMarker(place) {
  var placeLoc = place.geometry.location;
  markers = new google.maps.Marker({
    map: map,
    position: place.geometry.location
  });

  google.maps.event.addListener(markers, 'click', function() {
    infowindow.setContent(place.name);
    infowindow.open(map, this);
  });
}

function addmark(place){
	// To add the marker to the map, call setMap();
	
  markers=[];
  input1 = A.one("#<portlet:namespace/>search_loc").get("value");
  if(input1.length<2){
	  input1="UCLA";
  }
  address=input1;
  initialize();
  codeAddress();
  //markers.push(marker);
	//marker.setMap(map);
}

function <portlet:namespace />loadScript() {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = "http://maps.google.com/maps/api/js?sensor=false&callback=initialize";
    document.body.appendChild(script);
}

window.onload = <portlet:namespace />loadScript;
</aui:script>

<%-- <div id="map_canvas"></div>

<aui:input id="search_loc" name="search_loc:" type="text"></aui:input>
<aui:button name="submit_f" value="Submit:" onClick="addmark('Diddy Riese')"></aui:button>
--%>

