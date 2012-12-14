<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<html>
<head>
<title>Registration</title>
</head>
<body face="Trebuchet MS">

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<form method="post" action="registerlaststep.jsp">
<%
    response.setCharacterEncoding("GBK");
%>

<%
	String returnAddress = request.getParameter("returnaddress");
	if (returnAddress != null) {
%>
	<input type="hidden" name="returnaddress" value="<%=returnAddress%>">
<%
	}
	String onreport = request.getParameter("onreport");
	if (onreport != null) {
%>
	<input type="hidden" name="onreport" value="<%=onreport%>">
<%
	}
%>

<table cellspacing="2" width="100%" border="0" cellpadding="2">
	<tr>
		<td bgcolor="<%=MwConstants.BANNER_COLOR%>" colspan="4">
		<h3><font color="white">&#160;<%=Resource.get("register_jsp_SignI") %></font></h3>
		</td>
	</tr>
	<tr>
		<td colspan="4">
		<hr>

		</td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_UserId") %> * <font size="2">(<a href="userid.jsp?client=mw"><%=Resource.get("register_jsp_Instruction") %></a>)</font></td>
		<td><input name="userid" type="text"></td>
		<td><%=Resource.get("register_jsp_Iam") %> *</td>
		<td><select name="type">
			<option value="<%=Person.STUDENT%>"><%=Resource.get("register_jsp_Student") %>
			<option value="<%=Person.TEACHER%>"><%=Resource.get("register_jsp_Teacher") %>
			<option value="<%=Person.OTHER%>"><%=Resource.get("register_jsp_Other") %>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td valign=top><font color="#888888" size="2"><%=Resource.get("register_jsp_UserRule") %></font></td>
		<td></td>
		<td valign=top><font color="#888888" size="2"><%=Resource.get("register_jsp_SelectTeacher") %><br>
		<%=Resource.get("register_jsp_SelectNone") %></font></td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_Email") %> *</td>
		<td><input name="email" type="text"></td>
		<td><%=Resource.get("register_jsp_ReEmail") %> *</td>
		<td><input name="email2" type="text"></td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_SetPassword") %> *</td>
		<td><input name="password" type="password"></td>
		<td><%=Resource.get("register_jsp_RePassword") %> *</td>
		<td><input name="password2" type="password"></td>
	</tr>
	<tr>
		<td></td>
		<td><font color="#888888" size="2"><%=Resource.get("register_jsp_PasswordRules") %><br><%=Resource.get("register_jsp_PasswordRules2") %></font></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td colspan="4"><font color="#888888" size="2"><b>*: <%=Resource.get("register_jsp_RequiredUser") %></b></font></td>
	</tr>
	<tr>
		<td colspan="4">
		<hr>

		</td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_FirstName") %> #</td>
		<td><input type="text" name="firstname"></td>
		<td><%=Resource.get("register_jsp_LastName") %> #</td>
		<td><input type="text" name="lastname"></td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_IS") %> #</td>
		<td><input type="text" name="institution"></td>
		<td><%=Resource.get("register_jsp_TI") %> %</td>
		<td><input type="text" name="teacher"></td>
	</tr>
	<tr>
		<td></td>
		<td></td>
		<td></td>
		<td><font color="#000000" size="3"><b><%=Resource.get("register_jsp_STT") %><br>
		<%=Resource.get("register_jsp_NCS") %><br>
		<%=Resource.get("register_jsp_NonStudent") %></b></font></td>
	</tr>
	<tr>
		<td><%=Resource.get("register_jsp_SP") %></td>
		<td><input type="text" name="state"></td>
		<td><%=Resource.get("register_jsp_Country") %></td>
		<td><input type="text" name="country"></td>
	</tr>
	<tr>
		<td colspan="4"><font color="#888888" size="2"><b><%=Resource.get("register_jsp_RST") %></b></font></td>
	</tr>
	<tr>
		<td colspan="4">
		<hr>

		</td>
	</tr>
	<tr>
		<td><input value=<%=Resource.get("register_jsp_Continue") %> type="submit" name="register">
		</td>
		<td colspan="3"><font><a href="forgot.jsp?client=mw"><%=Resource.get("register_jsp_FIP") %></a></font></td>
	</tr>
</table>
</form>
</center>
<%@ include file="footer.jsp"%>
</body>
</html>
