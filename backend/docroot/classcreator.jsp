<jsp:directive.page import="java.util.Collection" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="classSpace"
	class="org.concord.mwbackend.web.ClassSpace" scope="request" />

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
		<th colspan=2 align=LEFT>
		<b><font size="4" color="white"><%=Resource.get("classcreator_jsp_CNC") %></font></b>
		</th>
	</tr>
	<tr>
		<td colspan="2">
		<%=Resource.get("classcreator_jsp_TNC") %> 
		(<font color="#888888" size="2"><%=Resource.get("classcreator_jsp_TR") %></font>):
		</td>
	</tr>
	<tr>
		<td colspan=2>
		<input name="classid" type="text">
		&nbsp;&nbsp;&nbsp;
		<input value=<%=Resource.get("classcreator_jsp_Create") %> type="submit" name="classcreator">
		</td>
	</tr>
	<tr>
		<td colspan=2>
<%
	if (user != null && user.getType() == Person.TEACHER) {
		String s = request.getParameter("classid");
		if (s != null && s.trim().length() > 0) {
			boolean inputOK = true;
			s = s.trim();
			int n = s.length();
			for (int i = 0; i < n; i++) {
				char c = s.charAt(i);
				boolean b = (c >= 'a' && c <= 'z')
						 || (c >= 'A' && c <= 'Z')
						 || (c >= '0' && c <= '9') || c == '_';
				if (!b) {
				    out.println("<p><font color=red><b>"+Resource.get("classcreator_jsp_IDconsist")+"</b></font></p>");
                    inputOK = false;
					break;
				}
			}
			if (inputOK) {
				if (classSpace.create(s, user)) {
					out.println("<p>"+Resource.get("classcreator_jsp_CreateSuccess")+"</p>");
					//response.setHeader("action", "back");
				} else {
					out.println("<font color=red><b>"+Resource.get("classcreator_jsp_ReType")+"</b></font>");
				}
			}
		}
		Collection c = classSpace.findClasses(user.getUserID());
		if (c != null && !c.isEmpty()) {
			out.println("<br><br><p>"+Resource.get("classcreator_jsp_existclass")+"");
			String image = MwConstants.PUBLIC_ROOT + "webres/link.gif";
			String cid = null;
			for (Iterator it = c.iterator(); it.hasNext();) {
				cid = (String) it.next();
				out.println("<img src="+ image
						+ ">&nbsp;<a href=classview.jsp?client=mw&teacher="
						+ user.getUserID() + "&classid=" + cid + ">" + cid + "</a>&nbsp;");
			}
			out.println("<br><br><p><a href=classmanager.jsp?client=mw&teacher="
					+ user.getUserID() + ">"+Resource.get("home_jsp_ManageClasses")+"</a>.");
		}
	} else {
%>
		Sorry, you are not permitted to create a class. Make sure that you
		have registered as a teacher.
<%
	}
%>
		</td>
	</tr>

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<td colspan=2></td>
	</tr>

</table>
</form>

<%@ include file="footer.jsp"%>

</body>
</html>
