<jsp:directive.page import="javax.servlet.http.Cookie" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<%
	String userID = null;
	String password = null;
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	userID = request.getParameter("userid");
	password = request.getParameter("password");
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
	boolean isAdmin = "admin".equalsIgnoreCase(userID);
	Person user = null;
	if (isAdmin) {
		user = userFinder.findAdmin(userID, password);
	} else {
		user = userFinder.findAuthorizedUser(userID, password);
	}
	if (user == null)
		isAdmin = false;
%>
<table width="100%">
	<tr bgcolor="<%=MwConstants.BANNER_COLOR%>">
	<td width="25%" align="left"><b><font size="4" color="white">
<%
	if (isAdmin) {
		out.print("Adminstration");
	} else {
		out.print(user != null ? userID + Resource.get("home_jsp_SMvSpace") : Resource.get("home_jsp_MySpace"));
	}
%>
	</font></b></td>
	<td align="right">
<%
	if (user != null) {
%>
		&nbsp;<a href="<%=jspName%>?client=mw&action=logout">
		<font color="white"><b><%=Resource.get("home_jsp_logout") %></b></font></a> 
<%
 	}
%>
	</td>
	</tr>
	<tr>
	<td colspan="2" align="center">
		<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner1.gif">
	</td>
	</tr>
	<tr>
	<td valign="top" align="left" bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
	<p>
<%
	String action = request.getParameter("action");
	if ("logout".equals(action)) {
		userFinder.removeUserCookie(response);
	}
	else {
		if (user == null) {
%>

	<form action="<%=jspName%>" method="GET">
		<table>
			<tr>
				<td><font size=3><%=Resource.get("register_jsp_UserId") %></font></td>
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
				<font size=3>
				<a href="forgot.jsp"><%=Resource.get("myhome_jsp_FYP") %></a>
				<br>
				<br>
				<a href="register.jsp?client=mw"><%=Resource.get("myhome_jsp_NewUser") %></a>
				</font>
				</td>
			</tr>
		</table>
	</form>

<%
		} else {
			userFinder.setUserID(userID);
			userFinder.setPassword(password);
			if ("login".equals(action)) {
				userFinder.setUserCookie(response);
			}
%> 
	<jsp:include page="navigator.jsp" flush="true">
		<jsp:param name="home" value="true" />
		<jsp:param name="userid" value="<%=userID%>" />
		<jsp:param name="firstname" value="<%=user.getFirstName()%>" />
		<jsp:param name="lastname" value="<%=user.getLastName()%>" />
		<jsp:param name="institution" value="<%=user.getInstitution()%>" />
		<jsp:param name="state" value="<%=user.getState()%>" />
		<jsp:param name="country" value="<%=user.getCountry()%>" />
		<jsp:param name="teacher" value="<%=user.getType() == Person.TEACHER%>" />
		<jsp:param name="student" value="<%=user.getType() == Person.STUDENT%>" />
	</jsp:include>
	</td>
<%
		}
%>
	<td valign="top">
		<table width="100%">
<%
		if (isAdmin) {
%>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
				<th align="left"><%=Resource.get("home_jsp_Welcome") %> Administrator!</th>
			</tr>
<%
		} else {
			if(user != null) {
%>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
				<th align="left"><%=Resource.get("home_jsp_Welcome") %> <%=user.getFirstName()%>!</th>
			</tr>
<%
			} else {
%>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
				<th align="left"><%=Resource.get("home_jsp_WhyRegister")%></th>
			</tr>
			<tr bgcolor="white">
				<td><%@ include file="whyregister.html"%></td>
			</tr>
<%
			}
		}
		if(user != null) {
			if (user.getType() == Person.TEACHER) {
%>
			<tr bgcolor="white">
			<td><font size=3>
			<%=Resource.get("home_jsp_OwnSpace") %> <%=Resource.get("home_jsp_CreateClass") %>
            <a href="addactivity.jsp?client=mw">
            <%=Resource.get("home_jsp_SelectActivities") %></a>. <%=Resource.get("home_jsp_GoTo") %> 
            <a href="public/part1/index.cml"><%=Resource.get("home_jsp_ActivityCenter") %></a> 
            <%=Resource.get("home_jsp_LookFor") %>. <%=Resource.get("home_jsp_Advantage") %>
            <br><br>
            <b><%=Resource.get("home_jsp_Note") %></b>
            <%=Resource.get("home_jsp_Method") %>
			</font><br><br></td>
			</tr>

			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("home_jsp_MyTools") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td>
			<font size=3>
			<br>
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="classcreator.jsp?client=mw"><%=Resource.get("home_jsp_CreateClasses") %></a>
			&nbsp;&nbsp;
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="classmanager.jsp?client=mw&teacher=<%=user.getUserID()%>">
				<%=Resource.get("home_jsp_ManageClasses") %></a>
			&nbsp;&nbsp;
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="contact.jsp?client=mw"><%=Resource.get("home_jsp_Contact") %></a>
			<br><br>
			</font>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("myhome_jsp_MCIS") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td><%@ include file="mci.html"%></td>
			</tr>			
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("myhome_jsp_STA") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td><%@ include file="sam.html"%></td>
			</tr>			
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<td></td>
			</tr>
<%
			} else {
				if (isAdmin) {
%>
			<tr bgcolor="white">
			<td>
			<font size=3>
			<%=Resource.get("myhome_jsp_ALA") %>
			<br>
			<br>
			<%=Resource.get("myhome_jsp_DONOT") %>
			<br><br>
			</font>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
				<th align="left"><%=Resource.get("myhome_jsp_WHATDO") %></th>
			</tr>
			<tr>
			<td>
			<font size=3><%=Resource.get("myhome_jsp_SSR") %></font><br>
			<blockquote>
				<font size=3>
				&#10122; <a href="reportanalyzer.jsp?client=mw">General</a><br>
				&#10123; <a href="reportsearch.jsp?client=mw">By teacher and activity</a>
				</font>
			</blockquote>
			</td>
			</tr>
<%
				} else {
%>
			<tr bgcolor="white">
			<td>
			<font size=3>This is your own space in the Molecular Workbench. From here 
			you can access and manage the models and reports you have 
			submitted.</font><br><br>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("home_jsp_MyTools") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td>
			<font size=3>
			<br>
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="contact.jsp?client=mw"><%=Resource.get("home_jsp_Contact") %></a>
			<br><br>
			</font>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("myhome_jsp_MCIS") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td><%@ include file="mci.html"%></td>
			</tr>			
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("myhome_jsp_STA") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td><%@ include file="sam.html"%></td>
			</tr>			
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<td></td>
			</tr>
<%
				}
			}
		}
%>
		</table>
	</td>
	</tr>
</table>
<%
	}
%>

</center>

<%@ include file="footer.jsp"%>

</body>
</html>
