<jsp:directive.page import="java.text.DateFormat" />
<jsp:directive.page import="java.util.Date" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<font size="3">

<center>

<%
 	String teacher = request.getParameter("teacher");
 	String klass = request.getParameter("classid");
 	if (teacher == null || klass == null) {
 		out.print(Resource.get("classview_jsp_alert"));
 	} else {
 		DateFormat dateFormat = DateFormat.getDateTimeInstance
 			(DateFormat.SHORT, DateFormat.SHORT);
		List list = userFinder.findStudentsOfClass(klass, teacher);
 %>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">

<br>
<p><b><%=Resource.get("classview_jsp_class") %> <%=klass%> (<%=list.size()%> students)</b></p>
<table width="100%" border="0" cellpadding="4" cellspacing="2">
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i>ID</i></td>
		<td><i><%=Resource.get("login_jsp_Password") %></i></td>
		<td><i><%=Resource.get("myclassinfo_jsp_Name") %></i></td>
		<td><i><%=Resource.get("myclassinfo_jsp_Email") %></i></td>
		<td><i><%=Resource.get("myclassinfo_jsp_School") %></i></td>
		<td><i><%=Resource.get("myclassinfo_jsp_RTime") %></i></td>
		<td><i><%=Resource.get("classview_jsp_Reports") %></i></td>
		<td><i><%=Resource.get("classview_jsp_Models") %></i></td>
	</tr>
<%
		for (Iterator it = list.iterator(); it.hasNext();) {
			Person p = (Person) it.next();
%>
	<tr>
		<td><%=p.getUserID()%></td>
		<td><%=p.getPassword()%></td>
		<td><%=p.getFullName()%></td>
		<td><a href="mailto:<%=p.getEmailAddress()%>?subject=Re:Your+Molecular+Workbench+work"><%=p.getEmailAddress()%></a></td>
		<td><%=p.getInstitution()%></td>
		<td><%=dateFormat.format(new Date(p.getMemberSince()))%></td>
		<td>&nbsp;<a href="myreportspace.jsp?client=mw&author=<%=p.getUserID()%>"><%=p.getUserID()%>'s reports</a></td>
		<td>&nbsp;<a href="mymodelspace.jsp?client=mw&author=<%=p.getUserID()%>"><%=p.getUserID()%>'s models</a></td>
	</tr>
<%
		}
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="center">
		<th colspan="8">
		&nbsp;<a href="home.jsp?client=mw"><%=Resource.get("activitymanage_jsp_RMWH") %></a>
		</th>
	</tr>

</table>

</center>

<%
	}
%>

</font>

</body>
</html>
