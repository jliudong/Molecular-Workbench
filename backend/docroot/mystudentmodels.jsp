<html>

<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="modelSpace"
	class="org.concord.mwbackend.web.ModelSpace" scope="request" />

<body bgcolor="white" face="Trebuchet MS">

<center>

<font size="2">

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
<br>
<br>

<%
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	String teacher = request.getParameter("teacher");
	if (teacher != null) {
		List list = modelSpace.getStudentModels(teacher);
		int n = list != null ? list.size() : 0;
%>

<form action="<%=jspName%>" method="POST">

<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left" bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<p><b><%=Resource.get("mystudentmodels_jsp_1")%></b></p>
		<p>
		<table width="90%">
			<tr bgcolor="#fefefe">
				<td>
				<img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">
				<font size=3>&nbsp;<a href="home.jsp?client=mw"><%=Resource.get("mystudentmodels_jsp_2")%></a></font>
				</td>
			</tr>
		</table>
		<br>
		<br>
		<p>
<%
		Person a = modelSpace.findPerson(teacher);
		if (a != null)
			out.print(a.getInstitution() + "<br>" + a.getState() + "<br>" + a.getCountry());
%>
		</p>
		</td>
		<td width="85%"><!-- begin the central area -->
		<p><%=Resource.get("mystudentmodels_jsp_3")%> <%=n%> <%=Resource.get("mystudentmodels_jsp_4")%></p>
		<table width="100%">
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left"></th>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
				<td><i><font size=3> <%=Resource.get("mystudentmodels_jsp_5")%></font></i></td>
				<td><i><font size=3>#</font></i></td>
				<td><i><font size=3> <%=Resource.get("mystudentmodels_jsp_6")%></font></i></td>
				<td><i><font size=3> <%=Resource.get("mystudentmodels_jsp_7")%></font></i></td>
				<td><i><font size=3> <%=Resource.get("mystudentmodels_jsp_8")%></font></i></td>
			</tr>
<%
		if (n > 0) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
%>
			<tr valign="top">
<%
				String id = (String) map.get("id");
%>
				<td><input type="checkbox" name="id" value="<%=id%>"></td>
				<td><font size=3><%=id%></font></td>
<%
				String url = (String) map.get("url");
%>
				<td><font size=3><a href="model/<%=url%>"><%=map.get("title")%></a></font></td>
				<td><font size=3><%=map.get("author")%></font></td>
				<td><font size=3><%=map.get("time")%></font></td>
			</tr>
<%
			}
		}
%>
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left"></th>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>
<%
	}
%>

</font>

</center>

<%@ include file="footer.jsp"%>

</body>
</html>
