<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<%-- <%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %> --%>
<portlet:defineObjects />


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<h2>Results</h2>
<% 
int resultSize = Integer.parseInt(renderRequest.getParameter("resultSize"));
for(int i=0;i<resultSize;i++)  {
%> 
	<h4> <%=renderRequest.getParameter("title"+i) %></h4>
	<p> <%=renderRequest.getParameter("result"+i) %> </p>
	<p> File Count:<%=renderRequest.getParameter("filecounts"+i) %> </p>
	<br/>
<% 
} 
%>
