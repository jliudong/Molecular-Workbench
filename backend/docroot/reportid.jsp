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
		<b><font size="4" color="white"><%=Resource.get("reportid_jsp_1")%></font></b>
		</th>
	</tr>
	<tr>
		<td colspan=6>
		<table width=100%>
			<tr>
			<td><font size=3><%=Resource.get("reportid_jsp_2")%></font></td>
			<td><input name="id" type="text" value=""></td>
			<td><input value="<%=Resource.get("reportid_jsp_3")%>" type="submit" name="reportid"></td>
			</tr>
		</table>
		</td>
	</tr>
<%
	if (user != null && user.getUserID().equals("admin")) {
		String s = request.getParameter("id");
		if (s != null && s.trim().length() > 0) {
			int id = -1;
			try {
				id = Integer.parseInt(s);
			} catch(NumberFormatException e) {
			}
			List list = null;
			int max = reportSpace.getLatestId();
			if(id >= 0 && id <= max) list = reportSpace.getItemWithId(id);
			if(list != null && !list.isEmpty()) {
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td width=20><i><font size="3">#</font></i></td>
		<td><i><font size="3"><%=Resource.get("reportid_jsp_4")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportid_jsp_5")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportid_jsp_6")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportid_jsp_7")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportid_jsp_8")%></font></i></td>
	</tr>
<%
				String student, teacher;
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
			} else {
				out.println("Report #" + s + " does not exist.");
			}
		}
	} else {
%>
	<tr>
		<td colspan=6><%=Resource.get("reportid_jsp_9")%></td>
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
