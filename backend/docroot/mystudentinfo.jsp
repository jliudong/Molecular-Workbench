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
<%
 	String teacher = request.getParameter("teacher");
 	if (teacher == null) {
 		out.print(Resource.get("mystudentinfo_jsp_1"));
 	} else {
%>

<center>
<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<blockquote>
<font size="3"><%=Resource.get("mystudentinfo_jsp_2")%></font>
</blockquote>

<table width="100%" border="0" cellpadding="4" cellspacing="2">
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i>ID</i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_3")%></i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_4")%></i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_5")%></i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_6")%></i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_7")%></i></td>
		<td><i><%=Resource.get("mystudentinfo_jsp_8")%></i></td>
	</tr>
<%
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		List list = userFinder.findStudents(teacher);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Person p = (Person) it.next();
%>
	<tr>
		<td><%=p.getUserID()%></td>
		<td><%=p.getPassword()%></td>
		<td><%=p.getFullName()%></td>
		<td><%=p.getKlass()%></td>
		<td><a href="mailto:<%=p.getEmailAddress()%>?subject=Re:Your+Molecular+Workbench+work"><%=p.getEmailAddress()%></a></td>
		<td><%=p.getInstitution()%></td>
		<td><%=dateFormat.format(new Date(p.getMemberSince()))%></td>
	</tr>
<%
		}
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="center">
		<th colspan="9">&nbsp; <a href="home.jsp?client=mw"><%=Resource.get("mystudentinfo_jsp_9")%></a></th>
	</tr>
</table>
</center>

<%
	}
%>

</font>

</body>
</html>
