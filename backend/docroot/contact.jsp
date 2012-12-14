<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<%
	String userID = null;
	String password = null;
	Cookie[] cookies = request.getCookies();
	if (cookies != null && cookies.length > 0) {
		String cn = null;
		for (int i = 0; i < cookies.length; i++) {
			cn = cookies[i].getName();
			if ("userid".equals(cn)) {
				userID = cookies[i].getValue();
			} else if ("password".equals(cn)) {
				password = cookies[i].getValue();
			}
		}
	}
	if (userID == null)
		userID = request.getParameter("userid");
	if (password == null)
		password = request.getParameter("password");
	Person user = userFinder.findAuthorizedUser(userID, password);
%>

<center>
<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"><br>

<form action="contact" method=POST>
<table width="100%" border="0" cellpadding=5>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=2 align=LEFT>
			<b><font size="4" color="white"><%=Resource.get("contact_jsp_1")%></font></b>
		</th>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("contact_jsp_2")%></td>
		<td valign=TOP>
<%
	if (user != null) {
%>
			<input type="text" name="name" value="<%=user.getPreferredName()%>">
<%
	} else {
%>
			<input type="text" name="name">
<%
 	}
%>
		</td>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("contact_jsp_3")%></td>
		<td valign=TOP>
<%
	if (user != null) {
%>
			<input type="text" name="email" value="<%=user.getEmailAddress()%>">
<%
	} else {
%>
			<input type="text" name="email">
<%
 	}
%>
			<p><font color="#888888" size="2"><%=Resource.get("contact_jsp_4")%></font></p>
		</td>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("contact_jsp_5")%></td>
		<td valign=TOP>
		<select name="subject">
			<option value="Questions" selected><%=Resource.get("contact_jsp_6")%></option>
			<option value="Technical Help"><%=Resource.get("contact_jsp_7")%></option>
			<option value="Comments"><%=Resource.get("contact_jsp_8")%></option>
		</select>
		</td>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("contact_jsp_9")%></td>
		<td valign=TOP>
		<textarea name="content" cols="60" rows="10"></textarea>
		<font color="#888888" size="2">
		<br><b><%=Resource.get("contact_jsp_11")%></b><br>
		<%=Resource.get("contact_jsp_12")%>
		</font>
		<blockquote>
		<font color="#888888" size="2">
		1. <%=Resource.get("contact_jsp_12")%><br>
		2. <%=Resource.get("contact_jsp_13")%> (<a href="http://java.com"><%=Resource.get("contact_jsp_14")%></a>)?<br>
		3. <%=Resource.get("contact_jsp_15")%><br>
		4. <%=Resource.get("contact_jsp_16")%><br>
		5. <%=Resource.get("contact_jsp_17")%>
		</font>
		</blockquote>
		</td>
	</tr>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=2 align=CENTER><input type=SUBMIT value="<%=Resource.get("contact_jsp_18")%>" name="contact"></th>
	</tr>
</table>
</form>
</center>

<%@ include file="footer.jsp"%>

</body>
</html>
