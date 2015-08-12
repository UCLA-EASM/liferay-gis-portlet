<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<%-- <%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %> --%>
<portlet:defineObjects />
<body>

<div id="cloud"></div>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<h2>Results</h2>
<%String[] words_list = null; 
int[] freq_list =null;%>
<% 
int resultSize = Integer.parseInt(renderRequest.getParameter("resultSize"));
for(int i=0;i<resultSize;i++)  {
%> 
	<h4> <%=renderRequest.getParameter("title"+i) %></h4>
	<p> <%words_list= (String[])renderRequest.getParameterValues("WordArray"+i); %> </p>
	<p><%System.out.println(words_list[0]); %></p>
	<p> File Count:<%=renderRequest.getParameter("filecounts"+i) %> </p>
	<p>Size:<%=words_list[1] %> Second: <%=words_list[2] %></p>
	<br/>
	<%freq_list= new int[words_list.length];
	String temp[] =	(String[])renderRequest.getParameterValues("frequency_count"+i); 
	int j=0;
	for(String s:temp){
		freq_list[j++]=Integer.parseInt(s)*2;
		System.out.println(freq_list[j-1]);
	}
	
	%>
<% 
} 
%>
<%int k=0; %>
<script src="<%=request.getContextPath()%>/js/d3.js"></script>
<script src="<%=request.getContextPath()%>/js/d3.layout.cloud.js"></script>
<script>
  var fill = d3.scale.category20();
  var colArray = new Array();
  <% for (int i=0; i<words_list.length; i++) { %>
  colArray[<%= i %>] = "<%=words_list[i]%>"; 
  <%//System.out.println(words_list[i]);%>
  <% } %>
  d3.layout.cloud().size([300, 300])
  .words(colArray.map(function(d) {
        return {text: d, size: 10 + Math.random() * 90};
      }))
      .padding(5)
      .rotate(function() { return ~~(Math.random() * 2) * 45; })
      .font("Impact")
      .fontSize(function(d) { return d.size; })
      .on("end", draw)
      .start();

  function draw(words) {
    d3.select("body").append("svg")
        .attr("width", 500)
        .attr("height", 500)
      .append("g")
        .attr("transform", "translate(150,150)")
      .selectAll("text")
        .data(words)
      .enter().append("text")
        .style("font-size", function(d) { return d.size + "px"; })
        .style("font-family", "Impact")
        .style("fill", function(d, i) { return fill(i); })
        .attr("text-anchor", "middle")
        .attr("transform", function(d) {
          return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
        })
        .text(function(d) { return d.text; });
  }
</script>

</body>