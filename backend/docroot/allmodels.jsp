<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="modelSpace"
	class="org.concord.mwbackend.web.ModelSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<table width="100%" cellpadding="5">
	<tr bgcolor="<%=MwConstants.BANNER_COLOR%>">
		<td align="left"><b><font size="4" color="white"><%=Resource.get("allmodels_jsp_AllModels") %></font></b></td>
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
	if (modelSpace.getUserFinder().findAdmin(userID, password) != null) {
		String queryString = request.getQueryString();
		String batch = request.getParameter("batch");
		int q = batch == null ? 1 : Integer.valueOf(batch).intValue();
		int latest = modelSpace.getLatestId();
		List list = modelSpace.getItemsIdBetween(latest - q*50 + 1, latest - (q-1)*50);
		if (q > 1) {
			String prev = Utilities.getBaseURL(request) + "?"
			+ queryString.replaceFirst("batch=" + q, "batch=" + (q - 1));
%>
<font size="3">[ <a href="<%=prev%>"><%=Resource.get("allmodels_jsp_NewModels") %></a> ]</font>
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
<font size="3">[ <a href="<%=next%>"><%=Resource.get("allmodels_jsp_OlderModels") %></a> ]</font>

<table width="100%" cellpadding="5" cellspacing="2">
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="left">
		<font size="3">
		<%=Resource.get("allmodels_jsp_ClickView") %></font></th>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><font size="3">#</font></i></td>
		<td><i><font size="3"><%=Resource.get("modelspace_jsp_MView") %></font></i></td>
		<td><i><font size="3">Privacy</font></i></td>
		<td><i><font size="3"><%=Resource.get("register_jsp_Student") %></font></i></td>
		<td><i><font size="3"><%=Resource.get("register_jsp_Teacher") %></font></i></td>
		<td><i><font size="3"><%=Resource.get("classview_jsp_class") %></font></i></td>
		<td><i><font size="3"><%=Resource.get("mymodelspace_jsp_Time") %>:</font></i></td>
	</tr>
<%
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				String student = (String) map.get("author");
				String privacy = (String) map.get("privacy");
				String teacher = (String) map.get("teacher");
				String klass = (String) map.get("klass");
				String url = (String) map.get("url");
%>
	<tr valign="top" align="left">
		<td bgcolor="<%=MwConstants.ROW_COLOR%>"><font size="3"><%=map.get("id")%></font></td>
		<td>&#160;<font size="3"><a href="model/<%=url%>"><%=map.get("title")%></a></font></td>
		<td>&#160;<font size="3"><%=privacy%></font></td>
		<td>&nbsp;<font size="3"><a href="mymodelspace.jsp?client=mw&author=<%=student%>"><%=student%></a></font></td>
<%
     			if("None".equals(teacher)) {
%>
		<td>&nbsp;<font size="3"><%=teacher%></font></td>
<%
     			} else {
%>
		<td>&nbsp;<font size="3"><a href="mystudentmodels.jsp?client=mw&teacher=<%=teacher%>"><%=teacher%></a></font></td>
<%
     			}
				if("null".equals(klass) || klass == null) {
%>
		<td><font size="3"><%=klass%></font></td>
<%
				} else {
%>
		<td><font size="3"><a href="classmodelspace.jsp?client=mw&class=<%=klass%>"><%=klass%></a></font></td>
<%
				}
%>
		<td><font size="3"><%=map.get("time")%></font></td>
	</tr>
<%
			}
		}
%>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="center"><font size="2"><%=Resource.get("allmodels_jsp_ScrollBack") %></font></th>
	</tr>
</table>

<%
	}
%>

</center>

<%@ include file="footer.jsp"%>
</body>
</html>
