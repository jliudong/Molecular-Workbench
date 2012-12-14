<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="reportSpace"
	class="org.concord.mwbackend.web.ReportSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>
	<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
</center>

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
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	if (userID == null)
		userID = request.getParameter("userid");
	if (password == null)
		password = request.getParameter("password");
	Person user = userFinder.findAuthorizedUser(userID, password);
%>

<form method=POST action="<%=jspName%>">
<table border="0" width="100%" cellpadding="10">

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=6 align=LEFT>
		<b><font size="4" color="white"><%=Resource.get("reportanalyzer_jsp_1")%> 
		<b><font color="red" size="4"><%=Resource.get("reportanalyzer_jsp_2")%> </font></b></font></b>
		</th>
	</tr>
	<tr>
		<td colspan=6>
		<table width=100%>
			<tr>
			<td>
			<%=Resource.get("reportanalyzer_jsp_3")%> <br>
			<input name="keyword" type="text" value="">
			</td>
			<td>---</td>
			<td>
			<%=Resource.get("reportanalyzer_jsp_1")%> <br>
			<input name="teacher" type="text" value="">
			</td>
			<td><%=Resource.get("reportanalyzer_jsp_5")%> </td>
			<td>
			<%=Resource.get("reportanalyzer_jsp_6")%> <br>
			<input name="student" type="text" value="">
			</td>
			<td>
			</td>
			</tr>
			<tr>
			<td>
			</td>
			<td>---</td>
			<td>
			<%=Resource.get("reportanalyzer_jsp_7")%> <br>
			<input name="teacherfirst" type="text" value="">
			</td>
			<td><%=Resource.get("reportanalyzer_jsp_8")%> </td>
			<td>
			<%=Resource.get("reportanalyzer_jsp_9")%> <br>
			<input name="teacherlast" type="text" value="">
			</td>
			<td>
			<br><input value="<%=Resource.get("reportanalyzer_jsp_10")%> " type="submit" name="reportanalyzer">
			</td>
			</tr>
		</table>
		</td>
	</tr>
<%
	if (user != null && user.getUserID().equals("admin")) {
		String s = request.getParameter("keyword");
		if (s != null && s.trim().length() > 0) {
			String[] t = s.split("\\s+");
			String keyword = "%";
			for(int i = 0; i < t.length; i++) {
				if(t[i].trim().equals("")) continue;
				keyword += t[i] + "%";
			}
			String teacher = request.getParameter("teacher");
			String student = request.getParameter("student");
			if(teacher == null && student == null) {
				String teacherFirstName = request.getParameter("teacherfirst");
				String teacherLastName = request.getParameter("teacherlast");
				if(teacherFirstName != null) {
					teacher = userFinder.findUserIDByName(Person.TEACHER, teacherFirstName, teacherLastName);
				} else {
					teacher = userFinder.findUserIDByLastName(Person.TEACHER, teacherLastName);					
				}
				if(teacher == null) teacher = teacherLastName;
			}
			List list = reportSpace.search(keyword, teacher, student);
			if(list != null && !list.isEmpty()) {
%>
	<tr>
		<td colspan=6>
    	<%=Resource.get("reportanalyzer_jsp_11")%>  <%=list.size()%> <%=Resource.get("reportanalyzer_jsp_12")%> <b><%=s%></b>, 
    	<%=Resource.get("reportanalyzer_jsp_13")%>  <b><%=student%></b>, <%=Resource.get("reportanalyzer_jsp_14")%>  <b><%=teacher%></b>.
		</td>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td width=20><i><font size="3">#</font></i></td>
		<td><i><font size="3"><%=Resource.get("reportanalyzer_jsp_15")%> </font></i></td>
		<td><i><font size="3"><%=Resource.get("reportanalyzer_jsp_16")%> </font></i></td>
		<td><i><font size="3"><%=Resource.get("reportanalyzer_jsp_17")%> </font></i></td>
		<td><i><font size="3"><%=Resource.get("reportanalyzer_jsp_18")%> </font></i></td>
		<td><i><font size="3"><%=Resource.get("reportanalyzer_jsp_19")%> </font></i></td>
	</tr>
<%
				if (list != null) {
					for (Iterator it = list.iterator(); it.hasNext();) {
						Map map = (Map) it.next();
						student = (String) map.get("author");
						String fullname = (String) map.get("fullname");
						String teacherID = (String) map.get("teacher");
						teacher = (String) map.get("teacher_fullname");
						String school = (String) map.get("school");
						String url = (String) map.get("url");
%>
	<tr valign="top" align="left">
		<td width=20 bgcolor="<%=MwConstants.ROW_COLOR%>"><font size="3"><%=map.get("id")%></font></td>
		<td>&#160;<font size="3"><a href="report/<%=url%>"><%=map.get("title")%></a></font></td>
		<td>&nbsp;<font size="3"><a href="myreportspace.jsp?client=mw&author=<%=student%>"><%=fullname%>(<%=student%>)</a></font></td>
		<td>&nbsp;<font size="3"><a href="mystudentreports.jsp?client=mw&teacher=<%=teacherID%>"><%=teacher%>(<%=teacherID%>)</a></font></td>
		<td><font size="3"><%=school%></font></td>
		<td><font size="3"><%=map.get("time")%></font></td>
	</tr>
<%
					}
				}
			}
		}
	} else {
%>
	<tr>
		<td colspan=6><%=Resource.get("reportanalyzer_jsp_20")%> </td>
	</tr>
<%
	}
%>

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<td colspan=6></td>
	</tr>

</table>
</form>

<%@ include file="footer.jsp"%>

</body>
</html>
