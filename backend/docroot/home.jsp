<jsp:directive.page import="java.util.ListIterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="javax.servlet.http.Cookie" />
<jsp:directive.page import="org.concord.mwbackend.business.Activity" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="activitySpace" class="org.concord.mwbackend.web.ActivitySpace" scope="request" />

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
		<font color="white"><b><%=Resource.get("home_jsp_logout")%></b></font></a> 
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
				<td><font size=3><%=Resource.get("home_jsp_userid")%>:</font></td>
			</tr>
			<tr>
				<td><input type="text" name="userid" size="20"></td>
			</tr>
			<tr>
				<td><font size=3><%=Resource.get("home_jsp_password")%>:</font></td>
			</tr>
			<tr>
				<td><input type="password" name="password" size="20"></td>
			</tr>
			<tr>
				<td><input value=<%=Resource.get("home_jsp_Login")%> name="login" type="submit"></td>
			</tr>
			<tr>
				<td></td>
			</tr>
			<tr>
				<td>
				<font size=3>
				<a href="forgot.jsp"><%=Resource.get("home_jsp_Forget")%></a>
				<br>
				<br>
				<a href="register.jsp?client=mw"><%=Resource.get("home_jsp_NewUser")%></a>
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
				<th align="left"><%=Resource.get("home_jsp_Welcome")%> Administrator!</th>
			</tr>
<%
		} else {
			if(user != null) {
%>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
				<th align="left"><%=Resource.get("home_jsp_Welcome")%> <%=user.getFirstName()%>!</th>
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
			</font><br><br>
			<font size=3>
			<%=Resource.get("home_jsp_AnyQuestion") %> <a href="contact.jsp?client=mw"><%=Resource.get("home_jsp_Contact") %></a>.
			</font>
			<br><br>
			</td>
			</tr>

			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("home_jsp_MyTools") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td>
			<br>
			<font size=3>
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="classcreator.jsp?client=mw"><%=Resource.get("home_jsp_CreateClasses") %></a>
			&nbsp;&nbsp;
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/class.gif">&nbsp;
				<a href="classmanager.jsp?client=mw&teacher=<%=user.getUserID()%>">
				<%=Resource.get("home_jsp_ManageClasses") %></a>
			&nbsp;&nbsp;
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/star_yellow.gif">&nbsp;
				<a href="addactivity.jsp?client=mw"><%=Resource.get("home_jsp_AddActivity") %></a>
			&nbsp;&nbsp;
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/star_yellow.gif">&nbsp;
				<a href="activitymanager.jsp?client=mw&teacher=<%=user.getUserID()%>">
				<%=Resource.get("home_jsp_ManageActivity") %></a>
			&nbsp;&nbsp;
			<br><br>
			</font>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("home_jsp_CurrentActivity") %></p></th>
			</tr>
			<tr>
			<td>
			<ol>
			<%
	 		List activities = activitySpace.findActivities(user.getUserID(), null);
			if(activities != null && !activities.isEmpty()){
				Activity a = null;
				for (ListIterator i = activities.listIterator(activities.size()); i.hasPrevious();) {
					a = (Activity) i.previous();
					if(a.getClassID().equals("None")) {
						out.println("<li><i><a href=" + a.getUrl() + ">" + a.getTitle()+ "</a></i></li>");
					} else {
						out.println("<li><b>" + a.getClassID() + "</b>: <i><a href=" + a.getUrl() + ">" + a.getTitle() + "</a></i></li>");
					}
					out.println("<font size=3><b>"+Resource.get("home_jsp_Instruction")+"</b> " + a.getInstruction() + "</font><br><br>");
				}
			} else {
				out.println(Resource.get("home_jsp_NoneActivity"));
			}
			%>
			</ol>
			</td>
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
				&#10122; <a href="reportanalyzer.jsp?client=mw"><%=Resource.get("home_jsp_general") %></a><br>
				&#10123; <a href="reportsearch.jsp?client=mw"><%=Resource.get("home_jsp_TAC") %></a><br>
				&#10124; <a href="reportsearch2.jsp?client=mw"><%=Resource.get("home_jsp_TAA") %></a><br>
				&#10125; <a href="reportid.jsp?client=mw"><%=Resource.get("home_jsp_byID") %></a>
				</font>
			</blockquote>
			<font size=3><%=Resource.get("home_jsp_ST") %></font><br>
			<blockquote>
				<font size=3>
				&#10122; <a href="teacherlist.jsp?client=mw"><%=Resource.get("home_jsp_LAT") %></a><br>
				</font>
			</blockquote>
			</td>
			</tr>
<%
				} else {
%>
			<tr bgcolor="white">
			<td>
			<font size=3><%=Resource.get("home_jsp_StudentSpace") %> <%=Resource.get("home_jsp_LFM") %>
			<a href="public/student/index.cml"><%=Resource.get("home_jsp_LOM") %></a>
			<%=Resource.get("home_jsp_LFA") %>
			<a href="public/part1/index.cml"><%=Resource.get("home_jsp_ActivityCenter") %></a>. 
			<%=Resource.get("home_jsp_HAQ") %><a href="contact.jsp?client=mw"><%=Resource.get("home_jsp_Contact") %></a>.
			<br><br></font>
			</td>
			</tr>
				<% if (user.getType() == Person.STUDENT) { %>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<th align="left"><p><%=Resource.get("home_jsp_SBT") %></p></th>
			</tr>
			<tr bgcolor="white">
			<td>
			<ol>
			<%
			String teacher = user.getTeacher();
			String classID = user.getKlass();
			if(teacher != null) {
		 		List activities = activitySpace.findActivities(teacher, null);
				if(activities != null && !activities.isEmpty()){
					Activity a = null;
					for (ListIterator i = activities.listIterator(activities.size()); i.hasPrevious();) {
						a = (Activity) i.previous();
						if("None".equals(a.getClassID()) || a.getClassID().equals(classID)) {
							out.println("<li><i><a href=" + a.getUrl() + ">" + a.getTitle() + "</a></i></li>");
							out.println("<font size=3><b>"+Resource.get("home_jsp_Instruction")+"</b> " + a.getInstruction() + "</font><br><br>");
						}
					}
				} else {
					out.println(Resource.get("home_jsp_TNS"));
				}
			}
			%>
			</ol>
			</td>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>">
			<td></td>
			</tr>
<%
					}
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
