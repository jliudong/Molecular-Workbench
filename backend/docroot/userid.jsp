<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<html>
<head>
<title>What user ID should you use?</title>
</head>
<body face="Trebuchet MS" bgcolor="white">

<table width="100%" bgcolor="white" cellpadding="10" border="0">

	<tr>
		<td bgcolor="#dedede">
		<h1><%=Resource.get("register_jsp_Instruction") %></h1>
		</td>
	</tr>

	<tr>
		<td>

		<h3><%=Resource.get("userid_jsp_WUI") %></h3>

		<p><%=Resource.get("userid_jsp_WHAT") %> <a href="register.jsp?client=mw"><%=Resource.get("userid_jsp_RePa") %></a> 
		<%=Resource.get("userid_jsp_sothat") %></p>

		<h3><%=Resource.get("userid_jsp_WSYU") %></h3>

		<p><%=Resource.get("userid_jsp_Detail") %></p>
		<br>
		<p><i><%=Resource.get("userid_jsp_notTeacher") %></i></p>
		</td>
	</tr>
	<tr>
		<td bgcolor="#dedede">
		<p><%=Resource.get("userid_jsp_NGB") %><a href="register.jsp?client=mw"><%=Resource.get("userid_jsp_RePa") %></a>.</p>
		</td>
	</tr>

</table>

<%@ include file="footer.jsp"%>
</body>
</html>
