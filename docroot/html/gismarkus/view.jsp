<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import = "com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import = "com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>

<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %>
<portlet:defineObjects />

This is the <b>Markus Upload</b> portlet in View mode.

<h1>My Library</h1>
Select the output file which has MARKUS results (.html ext).
 
<% 
final int fileCount = DLFileEntryLocalServiceUtil.getDLFileEntriesCount();
List<DLFileEntry> documents = DLFileEntryLocalServiceUtil.getDLFileEntries(0, fileCount);
List<DLFileEntry> htmlDocuments = new ArrayList<DLFileEntry>();

for(int i=0;i<documents.size();i++) {
	if(documents.get(i).getExtension().equals("html")) {
		htmlDocuments.add(documents.get(i));
	}
}
%>


<portlet:actionURL name="parseHtmlforLocations" var="parseHtmlforLocationsURL">
</portlet:actionURL>
 
<aui:form action="<%= parseHtmlforLocationsURL %>" method="post">
	<% for ( int i = 0; i < htmlDocuments.size(); i++ ) {
		%>
	<aui:input type="checkbox"  
				name="<%= \"document-\" + Long.toString(htmlDocuments.get(i).getFileEntryId()) %>" label="<%=htmlDocuments.get(i).getTitle() %>">
	</aui:input>
	<% 
	}
	%>
	<aui:input type="hidden" name="doumentListSize" value="<%= documents.size() %>"></aui:input>
	<aui:input type="submit" name="" value="Get Locations" />
</aui:form>


