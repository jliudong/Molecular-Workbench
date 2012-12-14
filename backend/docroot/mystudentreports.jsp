<html>

<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="reportSpace"
	class="org.concord.mwbackend.web.ReportSpace" scope="request" />

<body bgcolor="white" face="Trebuchet MS">

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>
<br>

<%
	String userID = null;
	Cookie[] cookies = request.getCookies();
	if (cookies != null && cookies.length > 0) {
		String cn = null;
		for (int i = 0; i < cookies.length; i++) {
			cn = cookies[i].getName();
			if ("userid".equals(cn)) {
				userID = cookies[i].getValue();
			}
		}
	}
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	String teacher = request.getParameter("teacher");
	boolean isMe = false;
	if (teacher != null) {
		isMe = teacher.equals(userID);
		List list = reportSpace.getStudentReports(teacher);
		int n = list != null ? list.size() : 0;
%>

<form action="<%=jspName%>" method="POST">

<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left"
			bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<p><b>

<%
		if (isMe) {
%>
		<%=Resource.get("mystudentreports_jsp_1")%>
<%
 		} else {
%>
		<%=teacher%><%=Resource.get("mystudentreports_jsp_2")%>
<%
 		}
%>
		</b></p>
		<p>
		<table width="90%">
			<tr bgcolor="#fefefe">
				<td>
				<img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">
				<font size=3>&nbsp;<a href="home.jsp?client=mw"><%=Resource.get("mystudentreports_jsp_3")%></a></font>
				</td>
			</tr>
		</table>
		<br>
		<br>
		<p>
<%
		Person a = reportSpace.findPerson(teacher);
		if (a != null)
			out.print(a.getInstitution() + "<br>" + a.getState() + "<br>" + a.getCountry());
%>
		</p>
		</td>
		<td width="85%"><!-- begin the central area -->
		<p><font size=3><%=Resource.get("mystudentreports_jsp_4")%>
<%
		if (isMe) {
%>
		<%=Resource.get("mystudentreports_jsp_5")%>
<%
		} else {
%>
		<%=teacher%><%=Resource.get("mystudentreports_jsp_6")%>
<%
		}
%>
		<%=Resource.get("mystudentreports_jsp_7")%><%=n%><%=Resource.get("mystudentreports_jsp_8")%> </font></p>
		<table width="100%">
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left"></th>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
<%
		if (isMe) {
%>
				<td><i><font size=3><%=Resource.get("mystudentreports_jsp_9")%></font></i></td>
<%
		}
%>
				<td><i><font size=3>#</font></i></td>
				<td><i><font size=3><%=Resource.get("mystudentreports_jsp_10")%></font></i></td>
				<td><i><font size=3><%=Resource.get("mystudentreports_jsp_11")%></font></i></td>
				<td><i><font size=3><%=Resource.get("mystudentreports_jsp_12")%></font></i></td>
			</tr>
<%
		if (n > 0) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
%>
			<tr valign="top">
<%
				String id = (String) map.get("id");
				if (isMe) {
%>
				<td><input type="checkbox" name="id" value="<%=id%>"></td>
<%
				}
%>
				<td><font size=3><%=id%></font></td>
<%
				String url = (String) map.get("url");
%>
				<td><font size=3><a href="report/<%=url%>"><%=map.get("title")%></a></font></td>
				<td><font size=3><%=map.get("author")%></font></td>
				<td><font size=3><%=map.get("time")%></font></td>
			</tr>
<%
			}
		}
%>
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left"></th>
			</tr>
		</table>
		</td>
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
