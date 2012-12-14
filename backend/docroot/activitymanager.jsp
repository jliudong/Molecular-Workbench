<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.ListIterator" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Activity" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="activitySpace" class="org.concord.mwbackend.web.ActivitySpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<%
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
 	String teacher = request.getParameter("teacher");
 	if (teacher == null) {
 		out.print("Please provide a teacher ID.");
 	} else {
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
 	 	if (userID == null) userID = request.getParameter("userid");
 	 	if (password == null) password = request.getParameter("password");
 	 	Person user = userFinder.findAuthorizedUser(userID, password);
 	 	if (user == null) {
 	 		out.print("<font color=red>"+Resource.get("classmanage_jsp_NAU")+"</font>");
 	 	} else {
	 		List activities = activitySpace.findActivities(teacher, null);
 	 		boolean updated = false;
 			String action = request.getParameter("action");
 	 		if ("update".equals(action)) {
 				if (activities != null) {
 					Map map = request.getParameterMap();
 					for(Iterator it = map.keySet().iterator(); it.hasNext();){
 						Object o = it.next();
 						if("delete".equals(o)) {
 							String[] t = (String[]) map.get(o);
 							if(t != null && t.length > 0) {
 								for(int i = 0; i < t.length; i++) {
 									if(t[i] != null) {
 										if(activitySpace.remove(Integer.parseInt(t[i]))) updated = true;
 									}
 								}
 							}
 						}
 					}
 				}
 				if(updated) activities = activitySpace.findActivities(teacher, null);
 	 		}
 %>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<form action="<%=jspName%>" method="POST">
	<input type="hidden" name="userid" value="<%=userID%>">
	<input type="hidden" name="password" value="<%=password%>">
	<input type="hidden" name="teacher" value="<%=teacher%>">

<table width="100%" border="0" cellpadding="4" cellspacing="2">
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<th align="left" colspan="5"><%=Resource.get("activitymanage_jsp_RAFA") %></th>
	</tr>
<%
			if (activities != null && !activities.isEmpty()) {
				Activity a = null;
				int index = 0;
				for (ListIterator i = activities.listIterator(activities.size()); i.hasPrevious();) {
					a = (Activity) i.previous();
					index++;
%>
	<tr>
	    <td><%=index%>. Class <%=a.getClassID() != null? a.getClassID() : "None"%></td>
	    <td><%=a.getTitle()%></td>
	    <td><a href="<%=a.getUrl()%>"><%=a.getUrl()%></a></td>
		<td><%=Resource.get("mymodelspace_jsp_Delete") %></td>
		<td><input type="checkbox" name="delete" value="<%=a.getID()%>"></td>
	</tr>
<%
				}
			}
%>
	<tr bgcolor="<%=MwConstants.ROW_COLOR%>">
		<td align="center" colspan="5">
			<input value=<%=Resource.get("profile_jsp_Update") %> name="update" type="submit"><br>
<%
			if(updated) {
				response.setHeader("action", "back");
				out.println("<b>"+Resource.get("activitymanage_jsp_UpdateSuccess")+"</b>");
			}
%>
		</td>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="center">
		<th colspan="5">
			<font size="2">&nbsp;<a href="home.jsp?client=mw"><%=Resource.get("activitymanage_jsp_RMWH") %></a></font>
		</th>
	</tr>
<%
		}
%>
</table>

<%
	}
%>

</form>

</center>

</body>
</html>
