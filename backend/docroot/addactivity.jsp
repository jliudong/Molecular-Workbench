<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.ListIterator" />
<jsp:directive.page import="org.concord.mwbackend.business.Activity" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="activitySpace"
	class="org.concord.mwbackend.web.ActivitySpace" scope="request" />
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
	if (user != null && user.getType() == Person.TEACHER) {
		List classes = classSpace.findClasses(user.getUserID());
%>

<form method=POST action="<%=jspName%>">
<table border="0" width="100%" cellpadding="10">

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=2 align=LEFT>
		<b><font size="4" color="white"><%=Resource.get("home_jsp_AddActivity") %></font></b>
		</th>
	</tr>
	<tr>
		<td colspan="2">
		<font size=3>
		<%=Resource.get("addactivity_jsp_TipsFActiv") %>
		</font>
		</td>
	</tr>
	<tr>
		<td colspan=2>
		<font size=3><%=Resource.get("addactivity_jsp_Address") %><input name="url" size="40" type="text">&nbsp;&nbsp;&nbsp;
		<%=Resource.get("mymodelspace_jsp_Title") %><input name="title" type="text">&nbsp;&nbsp;&nbsp;
		<%=Resource.get("addactivity_jsp_Class") %>
		<select name="class">
			<option value="None"><%=Resource.get("addactivity_jsp_None") %></option>
			<%
			if (classes != null && !classes.isEmpty()) {
				String s = null;
				for (Iterator it = classes.iterator(); it.hasNext();) {
					s = (String) it.next();
					out.println("<option value=" + s + ">" + s + "</option>");
				}
			}
			%>
		</select>&nbsp;&nbsp;&nbsp;</font>
		<br><br><p><font size=3>
		<%=Resource.get("addactivity_jsp_PasteMethod") %></font>
		</td>
	</tr>
	<tr>
		<td>
		<textarea name="instruction" cols="60" rows="10"></textarea>
		
		</td>
	</tr>
	<tr>
		<td colspan=2>
		<input value=<%=Resource.get("addactivity_jsp_Add") %> type="submit" name="addactivity">
		</td>
	</tr>
	<tr>
		<td colspan=2>
<%
		String url = request.getParameter("url");
		String title = request.getParameter("title");
		String instruction = request.getParameter("instruction");
		String classID = request.getParameter("class");
		if(url !=null){
			if (activitySpace.create(url, title, instruction, user, classID)) {
				
				out.println("<p>"+Resource.get("addactivity_jsp_activity") +"<b>" + title + "</b>"+ Resource.get("addactivity_jsp_successadd")+"</p>");
				response.setHeader("action", "back");
			} else {
				out.println("<font color=red><b>"+Resource.get("addactivity_jsp_typeurl") +"("
						+ url + ") "+Resource.get("addactivity_jsp_exist") + Resource.get("addactivity_jsp_retype")+"</b></font>");
			}
		}
		List c = activitySpace.findActivities(user.getUserID(), null);
		if (c != null && !c.isEmpty()) {
			out.println("<font size=3><p>"+Resource.get("addactivity_jsp_MCA")+"<ol>");
			Activity a = null;
			for (ListIterator it = c.listIterator(c.size()); it.hasPrevious();) {
				a = (Activity) it.previous();
				if(a.getClassID() != null) {
					out.println("<li><b>" + a.getClassID() + "</b>: "+ a.getTitle() + ": <a href=" + a.getUrl() + ">" + a.getUrl() + "</a></li>");
				} else {
					out.println("<li>" + a.getTitle() + ": <a href=" + a.getUrl() + ">" + a.getUrl() + "</a></li>");					
				}
			}
			out.println("</ol><p><a href=activitymanager.jsp?client=mw&teacher=user.getUserID() >"+Resource.get("home_jsp_ManageActivity")+"</a>.</font>");
		}
	} else {
%>
		<%=Resource.get("addactivity_jsp_MST") %>
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
