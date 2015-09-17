<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<%-- <%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %> --%>
<portlet:defineObjects />


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<h2>Locations Results</h2>
<% 
int locationsResultSize = Integer.parseInt(renderRequest.getParameter("locationsResultSize"));
for(int i=0;i<locationsResultSize;i++)  {
%> 
	<p> <%=renderRequest.getParameter("location"+ i) %> </p>
	<br/>
<% 
} 
%>
