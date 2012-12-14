<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.UserFinder" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="classSpace"
	class="org.concord.mwbackend.web.ClassSpace" scope="request" />

<html>

<body bgcolor="white" face="Trebuchet MS">

<center><img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"></center>

<font size="2">

<%
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
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
 	if (user == null) {
%>

<br>
<center>
<p>
<b>
<%
		if (userID == null) {
%>
		Please <a href="home.jsp?client=mw">log in</a> to change your profile.
<%
		} else {
%>
		<font color="red">Error in finding your profile!</font>
<%
		}
%>
</b>
</p>
</center>

<%
 	} else {
 		boolean passwordError = false;
 		boolean updated = false;
 		String action = request.getParameter("action");
 		String newPassword = request.getParameter("passwordnew");
 		String newPassword2 = request.getParameter("passwordnew2");
 		if ("update".equals(action)) {
 			if (newPassword != null && newPassword.equals(newPassword2)) {
 				String firstName = request.getParameter("firstname");
 				String lastName = request.getParameter("lastname");
 				String email = request.getParameter("email");
 				String institution = request.getParameter("institution");
 				String state = request.getParameter("state");
 				String country = request.getParameter("country");
 				String type = request.getParameter("type");
 				String teacher = request.getParameter("teacher");
 				String klass = request.getParameter("classid");
 				String notifyModel = request.getParameter("notifymodel");
 				String notifyReport = request.getParameter("notifyreport");
 				String acceptNewsletter = request.getParameter("acceptnewsletter");
 				user.setPassword(newPassword);
 				user.setFirstName(firstName);
 				user.setLastName(lastName);
 				user.setEmailAddress(email);
 				user.setInstitution(institution);
 				user.setState(state);
 				user.setCountry(country);
 				user.setNotifyModel("yes".equals(notifyModel));
 				user.setNotifyReport("yes".equals(notifyReport));
 				user.setAcceptNewsletter("yes".equals(acceptNewsletter));
 				if (type != null) user.setType(Integer.valueOf(type).intValue());
 				if (teacher != null) user.setTeacher(teacher);
 				if (klass != null) user.setKlass(klass);
 				userFinder.updateProfile(user);
 				UserFinder.setUserCookie(user, response, false);
 				updated = true;
 			} else {
 				passwordError = true;
 			}
 		}
%>

<form action="<%=jspName%>" method="POST">

<table border="0" width="100%">
	<tr>
		<td valign="top" width="25%" bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<jsp:include page="navigator.jsp" flush="true">
			<jsp:param name="profile" value="true" />
			<jsp:param name="userid" value="<%=userID%>" />
			<jsp:param name="firstname" value="<%=user.getFirstName()%>" />
			<jsp:param name="lastname" value="<%=user.getLastName()%>" />
			<jsp:param name="institution" value="<%=user.getInstitution()%>" />
			<jsp:param name="state" value="<%=user.getState()%>" />
			<jsp:param name="country" value="<%=user.getCountry()%>" />
			<jsp:param name="teacher" value="<%=user.getType() == Person.TEACHER%>" />
		</jsp:include></td>
		<td width="75%" valign="top">
		<table border="0" width="100%" cellpadding="5">
			<tr>
				<th bgcolor="<%=MwConstants.TOOLBAR_COLOR%>" colspan="2"
					align="left"><font color="white">
<%
		if (updated) {
%>
		<%=Resource.get("profile_jsp_UpdateSuc") %>
<%
 		} else {
			if (passwordError) {
%>
			<%=Resource.get("profile_jsp_FixBelow") %>
<%
 			} else {
%>
			<%=Resource.get("profile_jsp_MyPro") %>
<%
 			}
 		}
%>
				</font></th>
			</tr>
			<tr>
				<td><%=Resource.get("login_jsp_UI") %></td>
				<td><%=user.getUserID()%>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_Iam") %>:</td>
				<td><select name="type">
<%
		switch (user.getType()) {
			case Person.STUDENT:
				out.print("<option value=" + Person.STUDENT + " selected>"+Resource.get("register_jsp_Student")+"</option>");
				out.print("<option value=" + Person.TEACHER + ">"+Resource.get("register_jsp_Teacher")+"</option>");
				out.print("<option value=" + Person.OTHER + ">"+Resource.get("register_jsp_Other")+"</option>");
				break;
			case Person.TEACHER:
				out.print("<option value=" + Person.STUDENT + ">"+Resource.get("register_jsp_Student")+"</option>");
				out.print("<option value=" + Person.TEACHER + " selected>"+Resource.get("register_jsp_Teacher")+"</option>");
				out.print("<option value=" + Person.OTHER + ">"+Resource.get("register_jsp_Other")+"</option>");
				break;
			case Person.OTHER:
				out.print("<option value=" + Person.STUDENT + ">"+Resource.get("register_jsp_Student")+"</option>");
				out.print("<option value=" + Person.TEACHER + ">"+Resource.get("register_jsp_Teacher")+"</option>");
				out.print("<option value=" + Person.OTHER + " selected>"+Resource.get("register_jsp_Other")+"</option>");
				break;
		}
%>
				</select></td>
			</tr>
<%
		if (user.getType() == Person.STUDENT) {
			List list = userFinder.findTeachers();
			List klasses = classSpace.findClasses(user.getTeacher());
%>
			<tr>
				<td><%=Resource.get("profile_jsp_MyTeacher") %></td>
				<td><select name="teacher">
					<option value="None">None</option>
<%
			if (list != null) {
				Person p = null;
				String s = null;
				for (Iterator it = list.iterator(); it.hasNext();) {
					p = (Person) it.next();
					s = p.getUserID();
					if (s.equals(user.getTeacher())) {
%>
					<option value="<%=s%>" selected="selected"><%=s%></option>
<%
					} else {
%>
					<option value="<%=s%>"><%=s%></option>
<%
					}
				}
			}
%>
				</select></td>
			</tr>
			<tr>
				<td><%=Resource.get("profile_jsp_Class") %></td>
				<td><select name="classid">
					<option value="None">None</option>
<%
			if (klasses != null) {
				String s = null;
				for (Iterator it = klasses.iterator(); it.hasNext();) {
					s = (String) it.next();
					if (s.equals(user.getKlass())) {
%>
					<option value="<%=s%>" selected="selected"><%=s%></option>
<%
					} else {
%>
					<option value="<%=s%>"><%=s%></option>
<%
					}
				}
			}
%>
				</select></td>
			</tr>
<%
		}
%>
			<tr>
<%
		if (passwordError) {
%>
				<td><font color="red"><b>Password:</b></font></td>
				<td><input type="password" name="passwordnew" value="<%=newPassword%>"></td>
<%
		} else {
%>
				<td><%=Resource.get("login_jsp_Password") %></td>
				<td><input type="password" name="passwordnew" value="<%=user.getPassword()%>"></td>
<%
		}
%>
			</tr>
			<tr>
<%
		if (passwordError) {
%>
				<td><font color="red"><b><%=Resource.get("profile_jsp_CPW") %></b></font></td>
				<td><input type="password" name="passwordnew2" value="<%=newPassword2%>"></td>
<%
		} else {
%>
				<td><%=Resource.get("profile_jsp_CPW") %></td>
				<td><input type="password" name="passwordnew2" value="<%=user.getPassword()%>"></td>
<%
		}
%>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_FirstName") %></td>
				<td><input type="text" name="firstname"
					value="<%=(user.getFirstName()!=null && !user.getFirstName().equals("null"))? user.getFirstName() : "" %>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_LastName") %></td>
				<td><input type="text" name="lastname"
					value="<%=(user.getLastName()!=null && !user.getLastName().equals("null"))? user.getLastName() : "" %>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_ReEmail") %></td>
				<td><input type="text" name="email"
					value="<%=user.getEmailAddress()%>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_IS") %></td>
				<td><input type="text" name="institution"
					value="<%=(user.getInstitution()!=null && !user.getInstitution().equals("null"))? user.getInstitution() : "" %>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_SP") %></td>
				<td><input type="text" name="state"
					value="<%=(user.getState()!=null && !user.getState().equals("null"))? user.getState() : "" %>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("register_jsp_Country") %></td>
				<td><input type="text" name="country"
					value="<%=(user.getCountry()!=null && !user.getCountry().equals("null"))? user.getCountry() : "" %>"></td>
			</tr>
			<tr>
				<td><%=Resource.get("profile_jsp_MSN") %></td>
				<td><select name="notifymodel">
<%
		if (user.getNotifyModel()) {
%>
					<option value="yes" selected><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no"><%=Resource.get("profile_jsp_No") %></option>
<%
		} else {
%>
					<option value="yes"><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no" selected><%=Resource.get("profile_jsp_No") %></option>
<%
		}
%>
				</select></td>
			</tr>
<%
		if (user.getType() == Person.TEACHER) {
%>
			<tr>
				<td><%=Resource.get("profile_jsp_SSRN") %></td>
				<td><select name="notifyreport">
<%
			if (user.getNotifyReport()) {
%>
					<option value="yes" selected><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no"><%=Resource.get("profile_jsp_No") %></option>
<%
			} else {
%>
					<option value="yes"><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no" selected><%=Resource.get("profile_jsp_No") %></option>
<%
			}
%>
				</select></td>
			</tr>
<%
		}
%>
			<tr>
				<td><%=Resource.get("profile_jsp_SNM") %></td>
				<td><select name="acceptnewsletter">
<%
		if (user.getAcceptNewsletter()) {
%>
					<option value="yes" selected><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no"><%=Resource.get("profile_jsp_No") %></option>
<%
		} else {
%>
					<option value="yes"><%=Resource.get("profile_jsp_Yes") %></option>
					<option value="no" selected><%=Resource.get("profile_jsp_No") %></option>
<%
		}
%>
				</select></td>
			</tr>
			<tr>
				<th bgcolor="<%=MwConstants.TOOLBAR_COLOR%>" colspan="2"
					align="left"><input value=<%=Resource.get("profile_jsp_Update") %> name="update" type="submit"></th>
			</tr>
		</table>
		</td>
	</tr>
</table>

</form>

<%
	}
%>

</font>

<%@ include file="footer.jsp"%>

</body>
</html>
