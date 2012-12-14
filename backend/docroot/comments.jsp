2<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="commentSpace"
	class="org.concord.mwbackend.web.CommentSpace" scope="request" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<font size="3">

<table width="100%" cellpadding="5">
	<tr bgcolor="<%=MwConstants.BANNER_COLOR%>">
		<td align="left">
			<b><font size="4" color="white"><%=Resource.get("comments_jsp_1")%></font></b>
		</td>
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
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	if (userFinder.findAdmin(userID, password) != null) {
		String action = request.getParameter("action");
		if ("delete".equals(action)) {
			String[] id = request.getParameterValues("id");
			if (id != null && id.length > 0) {
				int[] iArray = new int[id.length];
				for (int i = 0; i < id.length; i++) {
					iArray[i] = Integer.valueOf(id[i]).intValue();
				}
				commentSpace.deleteComment(iArray);
				response.setHeader("action", "back");
			}
		}
		String queryString = request.getQueryString();
		String batch = request.getParameter("batch");
		int q = batch == null ? 1 : Integer.valueOf(batch).intValue();
		List list = commentSpace.getCommentsOfQuarter(q);
		if (q > 1) {
			String prev = Utilities.getBaseURL(request) + "?"
			+ queryString.replaceFirst("batch=" + q, "batch=" + (q - 1));
%>

[ <a href="<%=prev%>"><%=Resource.get("comments_jsp_14")%></a> ]

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
		int count = list == null ? 0 : list.size();
%>

[ <a href="<%=next%>"><%=Resource.get("comments_jsp_2")%></a> ]

<p><%=Resource.get("comments_jsp_3")%> <%=count%>&nbsp;<%=Resource.get("comments_jsp_4")%><br>
<%=Resource.get("comments_jsp_5")%>
</p>

<form action="<%=jspName%>" method="POST">

<table width="100%" cellpadding="5" cellspacing="2">
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="8" align="left">
			<input value="<%=Resource.get("comments_jsp_6")%>" name="delete" type="submit">
		</th>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><%=Resource.get("comments_jsp_7")%></i></td>
		<td><i>#</i></td>
		<td><i><%=Resource.get("comments_jsp_8")%></i></td>
		<td><i><%=Resource.get("comments_jsp_9")%></i></td>
		<td><i><%=Resource.get("comments_jsp_10")%></i></td>
		<td><i><%=Resource.get("comments_jsp_11")%></i></td>
		<td><i><%=Resource.get("comments_jsp_12")%></i></td>
	</tr>
<%
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				String id = (String) map.get("id");
%>
	<tr valign="top" align="left">
		<td><input type="checkbox" name="id" value="<%=id%>"></td>
		<td><%=id%></td>
		<td><a href="<%=map.get("url")%>"><%=Resource.get("comments_jsp_13")%></a></td>
<%
				String email = (String) map.get("email");
                if (email != null && !email.trim().equals("")) {
%>
		<td><a href="mailto:<%=email%>"><%=map.get("userID")%></a></td>
<%
                } else {
%>
		<td><%=map.get("userID")%></td>
<%                	
                }
%>
		<td bgcolor="<%=MwConstants.ROW_COLOR%>"><%=map.get("title")%></td>
		<td><%=map.get("body")%></td>
		<td><%=map.get("time")%></td>
	</tr>
<%
			}
		}
%>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="8" align="left">
			<input value="<%=Resource.get("comments_jsp_6")%>" name="delete" type="submit">
		</th>
	</tr>
</table>
</form>

<p><%=Resource.get("comments_jsp_15")%></p>

<%
	}
%>

</font>

</center>

<%@ include file="footer.jsp"%>
</body>
</html>
