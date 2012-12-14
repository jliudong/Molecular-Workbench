<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />

<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="reportSpace"
	class="org.concord.mwbackend.web.ReportSpace" scope="request" />
<jsp:setProperty name="reportSpace" property="userID" />
<jsp:setProperty name="reportSpace" property="password" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>
<font size="2">

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>
<br>

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
	if (userID == null) {
%>

<p><img src="<%=MwConstants.PUBLIC_ROOT%>webres/stopsign.gif"></p>
<p><font size="3" color="red"><%=Resource.get("myreportspace_jsp_1")%>.</font></p>
<p><font size="3"><%=Resource.get("myreportspace_jsp_4")%><a href="home.jsp?client=mw"><b>here</b></a> <%=Resource.get("myreportspace_jsp_2")%>.</font></p>
<p><font size="3"><%=Resource.get("myreportspace_jsp_5")%><b><a href="register.jsp?client=mw"><%=Resource.get("myreportspace_jsp_3")%>!</a></b></font></p>

<%
	} else {
 		if (password == null)
 			password = request.getParameter("password");
 		String action = request.getParameter("action");
 		if ("delete".equals(action) || "undelete".equals(action)) {
 			String[] id = request.getParameterValues("id");
 			if (id != null && id.length > 0) {
 				int[] iArray = new int[id.length];
 				for (int i = 0; i < id.length; i++) {
 					iArray[i] = Integer.valueOf(id[i]).intValue();
 				}
 				reportSpace.setDeleted("delete".equals(action), iArray);
 				response.setHeader("action", "back");
 			}
 		}
 		Person user = reportSpace.findPerson(userID);
 		if (user != null) {
 			if (!user.getPassword().equals(password)) {
		 		user = null;
 			}
 		}
 		boolean trash = false;
 		String author = request.getParameter("author");
 		if (author == null)
 			author = userID;
 		if (author != null) {
 			String type = request.getParameter("type");
 			trash = "trash".equals(type);
 			List list = reportSpace.getItemsBy(author, trash);
 			int n = list != null ? list.size() : 0;
 			boolean isMe = user == null ? false : author.equals(user.getUserID());
 			if (isMe) { // add a form
%>

<form action="<%=jspName%>" method="POST">

<%
			}
%>

<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left" bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<p><b><!-- side bar -->

<%
 			if (isMe) {
 				out.print(trash ? Resource.get("myreportspace_jsp_6") : Resource.get("myreportspace_jsp_7"));
 			} else {
 				out.print(author + Resource.get("myreportspace_jsp_8"));
 			}
%>

		</b></p>
		<p>
		<table width="90%">
			<tr bgcolor="white">
				<td>
<%
			if (isMe) {
				if (trash) {
%>
				<img src="<%=MwConstants.PUBLIC_ROOT%>webres/report.gif">&nbsp;
				<font size=3><a href="<%=jspName%>?client=mw&author=<%=author%>">
				<%=Resource.get("myreportspace_jsp_7")%></a></font>
<%
				} else {
%>
				<img src="<%=MwConstants.PUBLIC_ROOT%>webres/trash.gif">&nbsp;
				<font size=3><a href="<%=jspName%>?client=mw&author=<%=author%>&type=trash">
				<%=Resource.get("myreportspace_jsp_6")%></a></font>
<%
 				}
 			}
%>
				</td>
<%
			if (isMe) {
%>
			<tr bgcolor="white">
				<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">&nbsp;
				<font size=3><a href="home.jsp?client=mw"><%=Resource.get("myreportspace_jsp_9")%></a></font></td>
			</tr>
<%
			}
%>
			<tr><td></td></tr>
		</table>
		</td>
		<td width="85%"><!-- begin the central area -->
		<p><font size=3>
<%
			if (isMe) {
				if (trash) {
%>

				<%=Resource.get("myreportspace_jsp_10")%> <b><%=user.getFirstName()%></b>, <%=Resource.get("myreportspace_jsp_11")%> <%=n%> <%=Resource.get("myreportspace_jsp_12")%>

<%
				} else {
%>
				<%=Resource.get("myreportspace_jsp_10")%> <b><%=user.getFirstName()%></b>, <%=Resource.get("myreportspace_jsp_13")%> <%=n%><%=Resource.get("myreportspace_jsp_14")%> 
<%
				}
			} else {
%>
			<%=Resource.get("myreportspace_jsp_11")%><%=n%><%=Resource.get("myreportspace_jsp_21")%>  <b><%=author%></b><%=Resource.get("myreportspace_jsp_15")%> .
<%
			}
%>
		</font></p>
		<table width="100%">
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left">
<%
			if (isMe) {
				if (trash) {
%>
				<input value="<%= Resource.get("myreportspace_jsp_19")%>" name="undelete" type="submit">
<%
				} else {
%>
				<input value="<%= Resource.get("myreportspace_jsp_20")%>" name="delete" type="submit">
<%
 				}
 			}
%>
				</th>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
<%
			if (isMe) out.print("<td><i><font size=3>"+Resource.get("myreportspace_jsp_18")+"</font></i></td>");
%>
				<td><i><font size=3>#</font></i></td>
				<td><i><font size=3><%=Resource.get("myreportspace_jsp_16")%> </font></i></td>
				<td><i><font size=3><%=Resource.get("myreportspace_jsp_17")%></font></i></td>
			</tr>
<%
			if (list != null) {
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
				<td><font size=3>&#160;<a href="report/<%=url%>"><%=map.get("title")%></a></font></td>
				<td><font size=3><%=map.get("time")%></font></td>
			</tr>
<%
				}
			}
%>
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left">
<%
			if (isMe) {
				if (trash) {
%>
					<input value="<%= Resource.get("myreportspace_jsp_19")%>" name="undelete" type="submit">
<%
				} else {
%>
					<input value="<%= Resource.get("myreportspace_jsp_20")%>" name="delete" type="submit">
<%
 				}
 			}
%>
				</th>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%
			if (isMe) {
%>
</form>

<%
			}
		}
	}
%>

</font>
</center>

<%@ include file="footer.jsp"%>
</body>
</html>
