<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="modelSpace"
	class="org.concord.mwbackend.web.ModelSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center><font size="2"> <img
	src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"> <!-- banner -->
<br>
<br>

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
	if (userID == null)
		userID = request.getParameter("userid");
	if (password == null)
		password = request.getParameter("password");

	Person user = null;
	if (userID != null) {
		user = modelSpace.findPerson(userID);
		if (user != null) {
			if (!user.getPassword().equals(password)) {
				user = null;
			}
		}
	}
	String klass = request.getParameter("class");
	if(klass == null && user != null) {
		klass = user.getKlass();
	}
	if (user != null && klass != null) {
		boolean isTeacher = user.getType() == Person.TEACHER;
		List list = modelSpace.getClassModels(klass, isTeacher);
		int n = list != null ? list.size() : 0;
%>

<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left"
			bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<p><b><!-- side bar -->
<%
		 out.print(klass + Resource.get("mymodelspace_jsp_sModels"));
%>
		</b></p>
		<p>
		<table width="90%">
			<tr bgcolor="white">
				<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/model.gif">&nbsp;
				<font size=3><a href="mymodelspace.jsp?client=mw&author=<%=userID%>">
				<%=Resource.get("mymodelspace_jsp_MyModels") %></a></font></td>
			</tr>
			<tr bgcolor="white">
				<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">&nbsp;
				<font size=3><a href="home.jsp?client=mw"><%=Resource.get("mymodelspace_jsp_MyHome") %></a></font></td>
			</tr>
			<tr>
				<td></td>
			</tr>
		</table>
		</td>
		<td width="85%"><!-- begin the central area -->
		<p><font size=3> <%=Resource.get("mymodelspace_jsp_Hi") %> <b><%=user.getFirstName()%></b>, <%=Resource.get("classmodelspace_jsp_below") %>
		<%=n%> <%=Resource.get("classmodelspace_jsp_TS") %> <b><%=klass%></b><%=Resource.get("classmodelspace_jsp_CSS") %></font>
		</p>
		<table width="100%">
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left">
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
				<td><i><font size=3>#</font></i></td>
				<td><i><font size=3><%=Resource.get("modelspace_jsp_Author") %></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Title") %></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Download") %></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Size") %></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Time") %></font></i></td>
			</tr>
<%
		if (list != null) {
			boolean markTeacher = false;
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
%>
			<tr valign="top">
<%
				String id = (String) map.get("id");
				if (isTeacher){
					markTeacher = "teacher".equals(map.get("privacy"));
				} else {
					markTeacher = false;
				}
				if(markTeacher) {
%>
				<td><font size=3 color=red><%=id%>&#10023;</font></td>
<%
				} else {
%>
				<td><font size=3><%=id%></font></td>
<%
				}
				String author = (String) map.get("author");
%>
				<td><font size=3><%=author%></font></td>
<%
				String url = (String) map.get("url");
%>
				<td><font size=3>&#160;<a href="model/<%=url%>"><%=map.get("title")%></a></font>
				</td>
<%
						String zip = (String) map.get("zipfile");
						String parent = Utilities.getParent(url);
%>
				<td><font size=2>&#160;<a href="model/<%=(parent + zip)%>"><%=zip%></a></font></td>
				<td><font size=2><%=map.get("zipsize") + " KB"%></font></td>
				<td><font size=2><%=map.get("time")%></font></td>
			</tr>
<%
			}
		}
%>
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left">
				<font size=2>&#10023;: <%=Resource.get("classmodelspace_jsp_YSS") %></font>
				</th>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%
	}
%> </font></center>

<%@ include file="footer.jsp"%>

</body>
</html>
