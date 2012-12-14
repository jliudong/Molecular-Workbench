<jsp:directive.page import="javax.servlet.http.Cookie" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS" marginwidth=5 marginheight=5>

<%
	String userID = request.getParameter("userid");
	String password = request.getParameter("password");
	String onreport = request.getParameter("onreport");
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	Cookie[] cookies = request.getCookies();
	if (cookies != null && cookies.length > 0) {
		String cn = null;
		for (int i = 0; i < cookies.length; i++) {
			cn = cookies[i].getName();
			if ("userid".equals(cn)) {
				if (userID == null)
					userID = cookies[i].getValue();
			}
			else if ("password".equals(cn)) {
				if (password == null)
					password = cookies[i].getValue();
			}
		}
	}
	Person user = userFinder.findAuthorizedUser(userID, password);
	if (user == null) {
%>

<font size=4><b><%=Resource.get("login_jsp_TIP") %></b></font>

<br><br>

<form action="<%=jspName%>" method="GET">
	<table width=100%>
		<tr>
			<td><font size=3><%=Resource.get("login_jsp_UI") %></font></td>
		</tr>
		<tr>
			<td><input type="text" name="userid" size="20"></td>
		</tr>
		<tr>
			<td><font size=3><%=Resource.get("login_jsp_Password") %></font></td>
		</tr>
		<tr>
			<td><input type="password" name="password" size="20"></td>
		</tr>
		<tr>
			<td><input value=<%=Resource.get("home_jsp_Login") %> name="login" type="submit"></td>
		</tr>
		<tr>
			<td></td>
		</tr>
		<tr>
			<td>
			<font size=3><b><%=Resource.get("register_jsp_FIP") %></b>
			<br><br>
<%
		if("yes".equalsIgnoreCase(onreport)) {
%>
			<%=Resource.get("login_jsp_CFY") %>
<%
		} else {
%>
			<%=Resource.get("login_jsp_Click") %> <a href="forgot.jsp"><%=Resource.get("login_jsp_Here") %></a> 
			<%=Resource.get("login_jsp_RecIP") %>		
<%
		}
%>
			</font>
			</td>
		</tr>
	</table>
</form>

<%
	} else {
		String action = request.getParameter("action");
		userFinder.setUserID(userID);
		userFinder.setPassword(password);
		if ("login".equals(action)) {
			userFinder.setUserCookie(response);
		} else if("logout".equals(action)) {
			userFinder.removeUserCookie(response);
		}
%>
<p><%=Resource.get("mymodelspace_jsp_Hi") %>, <%=user.getFirstName()%>:</p>
<p><%=Resource.get("login_jsp_LRW") %></p>
<%
	}
%>

</body>
</html>
