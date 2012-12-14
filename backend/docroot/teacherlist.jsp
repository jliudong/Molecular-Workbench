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
<head>
<title><%=Resource.get("teacherlist_jsp_1")%></title>
</head>
<body face="Trebuchet MS">

<center>
<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>

<table>

<%
	List list = userFinder.findTeachers();
	int n = 0;
	if (list != null) {
		n = list.size();
%>
	<tr>
		<td colspan=6><i><b><%=n%></b> <%=Resource.get("teacherlist_jsp_2")%></i></td>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><font size="3">ID</font></i></td>
		<td><i><font size="3"><%=Resource.get("teacherlist_jsp_3")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("teacherlist_jsp_4")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("teacherlist_jsp_5")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("teacherlist_jsp_6")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("teacherlist_jsp_7")%></font></i></td>
	</tr>
<%
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		Person p = null;
		for (Iterator it = list.iterator(); it.hasNext();) {
			p = (Person) it.next();
%>
	<tr valign="top" align="left">
		<td><font size="3"><%=p.getUserID()%></font></td>
		<td><font size="3"><a href="mailto:<%=p.getEmailAddress()%>"><%=p.getFullName()%></a></font></td>
		<td><font size="3"><%=p.getInstitution()%></font></td>
		<td><font size="3"><%=p.getState()%></font></td>
		<td><font size="3"><%=p.getCountry()%></font></td>
		<td><font size="3"><%=dateFormat.format(new Date(p.getMemberSince()))%></font></td>
	</tr>
	
<%
		}
	}
%>

</table>

</center>

<%@ include file="footer.jsp"%>

</body>
</html>
