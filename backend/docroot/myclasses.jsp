<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="classSpace"
	class="org.concord.mwbackend.web.ClassSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>

<%
 	String teacher = request.getParameter("teacher");
 	if (teacher == null) {
 		out.print(Resource.get("myclasses_jsp_PTI"));
 	} else {
 		String workType = request.getParameter("worktype");
 		List klasses = classSpace.findClasses(teacher);
 %>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<table width="100%" border="0" cellpadding="4" cellspacing="2">

<%
		if("report".equals(workType)) {
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><font size="2">ID</font></i></td>
		<td><i><font size="2"><%=Resource.get("myclassinfo_jsp_Name") %></font></i></td>
		<td><i><font size="2"><%=Resource.get("myclassinfo_jsp_School") %></font></i></td>
		<td><i><font size="2"><%=Resource.get("classview_jsp_Reports") %></font></i></td>
	</tr>
<%
			if (klasses != null) {
				String klass = null;
				for (Iterator i = klasses.iterator(); i.hasNext();) {
					klass = (String) i.next();
					List list = userFinder.findStudentsOfClass(klass, teacher);
%>
	<tr bgcolor="<%=MwConstants.ROW_COLOR%>">
		<th colspan="8" align="left"><%=klass%> (<%=list.size()%> <%=Resource.get("register_jsp_Student") %>)</th>
	</tr>
<%
					for (Iterator it = list.iterator(); it.hasNext();) {
						Person p = (Person) it.next();
%>
	<tr>
		<td><font size=3><img src="<%=MwConstants.PUBLIC_ROOT%>webres/smile.gif">&nbsp;<%=p.getUserID()%></font></td>
		<td><font size=3><a href="mailto:<%=p.getEmailAddress()%>?subject=Re:Your+Molecular+Workbench+work"><%=p.getFullName()%></a></font></td>
		<td><font size=3><%=p.getInstitution()%></font></td>
		<td><font size=3><img src="<%=MwConstants.PUBLIC_ROOT%>webres/link.gif">&nbsp;
		<a href="myreportspace.jsp?client=mw&author=<%=p.getUserID()%>"><%=Resource.get("myclasses_jsp_CHF") %> <%=p.getUserID()%><%=Resource.get("myclasses_jsp_SR") %></a></font></td>
	</tr>
<%
					}
				}
			}
		} else {
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<th colspan="8">
			<font size="2"><%=Resource.get("myclasses_jsp_VMC") %></font>
		</th>
	<tr>
<%
			if (klasses != null) {
				String klass = null;
				for (Iterator i = klasses.iterator(); i.hasNext();) {
					klass = (String) i.next();
%>
	<tr>
		<td>
		<font size=4><img src="<%=MwConstants.PUBLIC_ROOT%>webres/link.gif">&nbsp;
		<a href="classmodelspace.jsp?client=mw&class=<%=klass%>"><%=klass%></a></font>
		</td>
	</tr>
<%
				}
			}
		}
%>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="center">
		<th colspan="8">
			<font size="2">&nbsp;<a href="home.jsp?client=mw"><%=Resource.get("activitymanage_jsp_RMWH") %></a></font>
		</th>
	</tr>
</table>

<%
	}
%>

</center>

</body>
</html>
