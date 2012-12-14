<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<html>
<head>
<title><%=Resource.get("forgot_jsp_forgotpw") %></title>
</head>

<body face="Trebuchet MS">

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"><br>

<form method="POST" action="register?action=forgot">
<table cellpadding="10" border="0" width="100%" cellspacing="2">
	<tr>
		<th colspan="3" align="left" bgcolor="#f0f0f0">
		<%=Resource.get("forgot_jsp_forgotIPW") %>
		</th>
	</tr>
	<tr>

	</tr>
	<tr>
		<td><%=Resource.get("forgot_jsp_TypeYE") %></td>
		<td><input name="email" type="text"></td>
		<td><input name="forgot" type="submit" value=<%=Resource.get("forgot_jsp_SendIdPw") %>></td>
	</tr>
	<tr>
		<td colspan="3">
		<p><%=Resource.get("forgot_jsp_HYtip") %></p>
		<br>

		<p><%=Resource.get("forgot_jsp_INR") %> <font><a href="register.jsp?client=mw"><%=Resource.get("forgot_jsp_ReRegister") %></a></font><font>
		<%=Resource.get("forgot_jsp_Tipve") %></font>
		</p>
		</td>
	</tr>
	<tr>

	</tr>
	<tr>

	</tr>
	<tr>
		<td colspan="3" align="center" bgcolor="#f0f0f0"></td>
	</tr>
</table>
</form>

</center>

<%@ include file="footer.jsp"%>
</body>
</html>
