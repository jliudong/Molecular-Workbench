<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="reportSpace"
	class="org.concord.mwbackend.web.ReportSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<table width="100%" cellpadding="5">
	<tr bgcolor="<%=MwConstants.BANNER_COLOR%>">
		<td align="left"><b><font size="4" color="white"><%=Resource.get("reportspace_jsp_1")%></font></b></td>
	</tr>
</table>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"><br>

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
	if (reportSpace.getUserFinder().findAdmin(userID, password) != null) {
		String queryString = request.getQueryString();
		String batch = request.getParameter("batch");
		int q = batch == null ? 1 : Integer.valueOf(batch).intValue();
		int latest = reportSpace.getLatestId();
		List list = reportSpace.getItemsIdBetween(latest - q*50 + 1, latest - (q-1)*50);
		if (q > 1) {
			String prev = Utilities.getBaseURL(request) + "?"
			+ queryString.replaceFirst("batch=" + q, "batch=" + (q - 1));
%>
<font size="3">[ <a href="<%=prev%>"><%=Resource.get("reportspace_jsp_2")%></a> ]</font>
<%
		}
		String next = Utilities.getBaseURL(request) + "?";
		if (queryString == null) {
			next += "client=mw&batch=" + (q + 1);
		} else {
			if (queryString.indexOf("batch=") != -1) {
				next += queryString.replaceFirst("batch=" + q, "batch=" + (q + 1));
			} else {
				next += queryString + "&batch=" + (q + 1);
			}
		}
%>
<font size="3">[ <a href="<%=next%>"><%=Resource.get("reportspace_jsp_3")%></a> ]</font>

<table width="100%" cellpadding="5" cellspacing="2">
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="left">
		<font size="3">
		<%=Resource.get("reportspace_jsp_4")%></font></th>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><font size="3">#</font></i></td>
		<td><i><font size="3"><%=Resource.get("reportspace_jsp_5")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportspace_jsp_6")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportspace_jsp_7")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportspace_jsp_8")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportspace_jsp_9")%></font></i></td>
	</tr>
<%
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				String student = (String) map.get("author");
				String fullname = (String) map.get("fullname");
				String teacher = (String) map.get("teacher");
				String school = (String) map.get("school");
				String url = (String) map.get("url");
%>
	<tr valign="top" align="left">
		<td bgcolor="<%=MwConstants.ROW_COLOR%>"><font size="3"><%=map.get("id")%></font></td>
		<td>&#160;<font size="3"><a href="report/<%=url%>"><%=map.get("title")%></a></font></td>
		<td>&nbsp;<font size="3"><a href="myreportspace.jsp?client=mw&author=<%=student%>"><%=fullname%></a></font></td>
		<td>&nbsp;<font size="3"><a href="mystudentreports.jsp?client=mw&teacher=<%=teacher%>"><%=teacher%></a></font></td>
		<td><font size="3"><%=school%></font></td>
		<td><font size="3"><%=map.get("time")%></font></td>
	</tr>
<%
			}
		}
%>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="center"><font size="2"><%=Resource.get("reportspace_jsp_10")%></font></th>
	</tr>
</table>

<%
	}
%>

</center>

<%@ include file="footer.jsp"%>
</body>
</html>
