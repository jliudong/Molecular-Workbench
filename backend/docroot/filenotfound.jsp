<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<html>
<head>
<title>File not accessible</title>
</head>

<body face="Trebuchet MS">

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"><br>

<table cellpadding="10" border="0" width="100%" cellspacing="2">
	<tr>
		<th colspan="3" align="left" bgcolor="#f0f0f0">
		<%=Resource.get("filenotfound_jsp_FNA") %>
		</th>
	</tr>
	<tr>
		<td>
		<p><%=Resource.get("filenotfound_jsp_reason") %></p>
		<br>
		<br>
		<p><a href="modelspace.jsp?client=mw"><%=Resource.get("filenotfound_jsp_GMS") %></a> | <a href="home.jsp?client=mw">
		<%=Resource.get("filenotfound_jsp_GMA") %></a></p>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="center" bgcolor="#f0f0f0"></td>
	</tr>
</table>
</center>

<%@ include file="footer.jsp"%>
</body>
</html>
