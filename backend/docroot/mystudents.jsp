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
 		out.print("Please provide a teacher ID.");
 	} else {
 		String workType = request.getParameter("worktype");
%>

<center>
<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<table width="100%" border="0" cellpadding="4" cellspacing="2">
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i>ID</i></td>
		<td><i><%=Resource.get("mystudents_jsp_1")%></i></td>
		<td><i><%=Resource.get("mystudents_jsp_2")%></i></td>
		<td><i><%=Resource.get("mystudents_jsp_3")%></i></td>
<%
		if("model".equals(workType)) {
%>
		<td><i><%=Resource.get("mystudents_jsp_4")%></i></td>
<%
		} else {
%>
		<td><i><%=Resource.get("mystudents_jsp_5")%></i></td>
<%
		}
%>
	</tr>
<%
		List list = userFinder.findStudents(teacher);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Person p = (Person) it.next();
%>
	<tr>
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/smile.gif">&nbsp;<%=p.getUserID()%></td>
		<td><a href="mailto:<%=p.getEmailAddress()%>?subject=Re:Your+Molecular+Workbench+work"><%=p.getFullName()%></a></td>
		<td><%=p.getKlass()%></td>
		<td><%=p.getInstitution()%></td>
<%
			if("model".equals(workType)) {
%>
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/link.gif">&nbsp;
		<a href="mymodelspace.jsp?client=mw&author=<%=p.getUserID()%>"><%=Resource.get("mystudents_jsp_6")%> <%=p.getUserID()%><%=Resource.get("mystudents_jsp_7")%></a></td>
<%
			} else {
%>
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/link.gif">&nbsp;
		<a href="myreportspace.jsp?client=mw&author=<%=p.getUserID()%>"><%=Resource.get("mystudents_jsp_6")%><%=p.getUserID()%><%=Resource.get("mystudents_jsp_8")%></a></td>
<%
			}
%>
	</tr>
<%
		}
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="center">
		<th colspan="9">&nbsp; <a href="home.jsp?client=mw"><%=Resource.get("mystudents_jsp_9")%></a></th>
	</tr>
</table>
</center>

<%
	}
%>

</font>

</body>
</html>
