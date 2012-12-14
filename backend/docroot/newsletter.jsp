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
	String group = request.getParameter("group");
	Person admin = userFinder.findAdmin(userID, password);
%>

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<%
	if (admin != null) {
%>

<form action="newsletter" method=POST>
<table width="100%" border="0" cellpadding=5>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=2 align=LEFT><b><font size="4" color="white"><%=Resource.get("newsletter_jsp_1")%></font></b></th>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("newsletter_jsp_2")%></td>
		<td valign=TOP><select name="group">
<%
		if ("Teachers".equals(group)) {
%>
			<option value="All"><%=Resource.get("newsletter_jsp_3")%></option>
			<option value="Teachers" selected><%=Resource.get("newsletter_jsp_4")%></option>
<%
		} else {
%>
			<option value="All" selected><%=Resource.get("newsletter_jsp_3")%></option>
			<option value="Teachers"><%=Resource.get("newsletter_jsp_4")%></option>
<%
		}
%>
		</select></td>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("newsletter_jsp_5")%></td>
		<td><input name="subject" type="text"></td>
	</tr>
	<tr>
		<td width="20%" valign=TOP><%=Resource.get("newsletter_jsp_6")%></td>
		<td valign=TOP><textarea name="content" cols="60" rows="10"></textarea>
		<font color="#888888" size="2"> <br>
		<b><%=Resource.get("newsletter_jsp_7")%></b> <%=Resource.get("newsletter_jsp_8")%> </font></td>
	</tr>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=2 align=CENTER><input type=SUBMIT value="<%=Resource.get("newsletter_jsp_9")%>" name="newsletter"></th>
	</tr>
</table>
</form>
<%
	}
%>
</center>

<%@ include file="footer.jsp"%>

</body>
</html>
