<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="classSpace" class="org.concord.mwbackend.web.ClassSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<%
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
 	String teacher = request.getParameter("teacher");
 	if (teacher == null) {
 		out.print(Resource.get("myclasses_jsp_PTI"));
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
	 		List klasses = classSpace.findClasses(teacher, false);
 	 		boolean updated = false;
 			String action = request.getParameter("action");
 	 		if ("update".equals(action)) {
 				if (klasses != null) {
 					String oldCid = null;
 					String newCid = null;
 					search:
 					for (Iterator i = klasses.iterator(); i.hasNext();) {
 						oldCid = (String) i.next();
 		 	 			newCid = request.getParameter(oldCid);
 		 	 			if(newCid == null || newCid.trim().equals("")) continue;
 		 	 			if(newCid.equals(oldCid)) continue;
 		 	 			newCid = newCid.trim();
 		 	 			int n = newCid.length();
 		 	 			for (int k = 0; k < n; k++) {
 		 	 				char c = newCid.charAt(k);
 		 	 				boolean b = (c >= 'a' && c <= 'z')
 		 	 						 || (c >= 'A' && c <= 'Z')
 		 	 						 || (c >= '0' && c <= '9') || c == '_';
 		 	 				if (!b) {
 		 	 					out.println("<p><font color=red><b>"+Resource.get("classmanage_jsp_CIE")+" " + newCid + "<br>"+Resource.get("classcreator_jsp_IDconsist")+"</b></font></p>");
 		 	 					break search;
 		 	 				}
 		 	 			}
 		 	 			if(userFinder.renameClass(oldCid, newCid, teacher)) {
	 		 	 			classSpace.rename(oldCid, newCid);
	 						updated = true;
 		 	 			}
 					}
 					Map map = request.getParameterMap();
 					for(Iterator it = map.keySet().iterator(); it.hasNext();){
 						Object o = it.next();
 						if("delete".equals(o)) {
 							String[] t = (String[]) map.get(o);
 							if(t != null && t.length > 0) {
 								for(int i = 0; i < t.length; i++) {
 									if(t[i] != null) {
 										if(classSpace.remove(t[i])) updated = true;
 									}
 								}
 							}
 						}
 						else if("archive".equals(o)) {
 							String[] t = (String[]) map.get(o);
 							if(t != null && t.length > 0) {
 								for(int i = 0; i < t.length; i++) {
 									if(t[i] != null) {
 										if(classSpace.archive(t[i], true)) updated = true;
 									}
 								}
 							}
 						}
 						else if("unarchive".equals(o)) {
 							String[] t = (String[]) map.get(o);
 							if(t != null && t.length > 0) {
 								for(int i = 0; i < t.length; i++) {
 									if(t[i] != null) {
 										if(classSpace.archive(t[i], false)) updated = true;
 									}
 								}
 							}
 						}
 					}
 				}
 				if(updated) klasses = classSpace.findClasses(teacher, false);
 	 		}
 %>

<form action="<%=jspName%>" method="POST">
	<input type="hidden" name="userid" value="<%=userID%>">
	<input type="hidden" name="password" value="<%=password%>">
	<input type="hidden" name="teacher" value="<%=teacher%>">

<table width="100%" border="0" cellpadding="4" cellspacing="2">
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<th align="left" colspan="5"><%=Resource.get("classmanage_jsp_function") %></th>
	</tr>
<%
			if (klasses != null) {
				String klass = null;
				for (Iterator i = klasses.iterator(); i.hasNext();) {
					klass = (String) i.next();
					List list = userFinder.findStudentsOfClass(klass, teacher);
					if(list.isEmpty()) {
%>
	<tr>
		<td align="left"><%=klass%> (<%=list.size()%> <%=Resource.get("register_jsp_Student") %>)</td>
		<td><%=Resource.get("classmanage_jsp_Rename") %></td>
		<td><input type="text" name="<%=klass%>" value="">*</td>
		<td><%=Resource.get("mymodelspace_jsp_Delete") %></td>
		<td><input type="checkbox" name="delete" value="<%=klass%>"></td>
	</tr>
<%
					} else {
%>
	<tr>
		<td align="left"><%=klass%> (<%=list.size()%> <%=Resource.get("register_jsp_Student") %>)</td>
		<td><%=Resource.get("classmanage_jsp_Rename") %></td>
		<td><input type="text" name="<%=klass%>" value="">*</td>
		<td><%=Resource.get("classmanage_jsp_Archive") %> &#8224;</td>
		<td><input type="checkbox" name="archive" value="<%=klass%>"></td>
	</tr>
<%
					}
				}
			}
%>
	<tr>
		<td align="left" colspan="5">
		<font size="2">* <%=Resource.get("classmanage_jsp_TipRule") %></font><br>
		<font size="2">&#8224; <%=Resource.get("classmanage_jsp_NoDel") %></font>
		</td>
	</tr>
<%
			klasses = classSpace.findClasses(teacher, true);
			if (!klasses.isEmpty()) {
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<th align="left" colspan="5"><%=Resource.get("classmanage_jsp_archiveclass") %></th>
	</tr>
<%
				String klass = null;
				for (Iterator i = klasses.iterator(); i.hasNext();) {
					klass = (String) i.next();
%>
	<tr>
		<td align="left"><%=klass%></td>
		<td><%=Resource.get("classmanage_jsp_unarchive") %>:</td>
		<td><input type="checkbox" name="unarchive" value="<%=klass%>"></td>
	</tr>
<%
				}
			}
%>
	<tr bgcolor="<%=MwConstants.ROW_COLOR%>">
		<td align="center" colspan="5">
			<input value=<%=Resource.get("profile_jsp_Update") %> name="update" type="submit"><br>
<%
			if(updated) out.println("<b>"+Resource.get("activitymanage_jsp_UpdateSuccess")+"</b>");
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
