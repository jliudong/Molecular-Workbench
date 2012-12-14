<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<%
	String userID = request.getParameter("userid");
	boolean isAdmin = "admin".equalsIgnoreCase(userID);
	boolean isTeacher = "true".equals(request.getParameter("teacher"));
	boolean isStudent = "true".equals(request.getParameter("student"));
	boolean isProfile = "true".equals(request.getParameter("profile"));
	boolean isHome = "true".equals(request.getParameter("home"));
	String firstName = request.getParameter("firstname");
	String lastName = request.getParameter("lastname");
	String institution = request.getParameter("institution");
	String state = request.getParameter("state");
	String country = request.getParameter("country");
	if (isTeacher) out.print("<p><b>"+Resource.get("navigator_jsp_17")+"</b></p>");
%>
<br>
<%
	if (isAdmin) {
%>
	<table width="90%">
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/newsletter.gif">&nbsp;<a
			href="newsletter.jsp?client=mw"><font size="3"><%=Resource.get("navigator_jsp_1")%></font></a></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/report.gif">&nbsp;<a
			href="reportspace.jsp?client=mw"><font size="3"><%=Resource.get("navigator_jsp_2")%></font></a></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/comment.gif">&nbsp;<a
			href="comments.jsp?client=mw"><font size="3"><%=Resource.get("navigator_jsp_3")%></font></a></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/model.gif">&nbsp;<a
			href="allmodels.jsp?client=mw"><font size="3"><%=Resource.get("navigator_jsp_4")%></font></a></td>
	</tr>
	</table>
<%
	} else {
%>
	<table width="90%">
<%
		if (!isHome) {
%>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">&nbsp;
		<font size=3><a href="home.jsp?client=mw"><%=Resource.get("navigator_jsp_5")%></a></font></td>
	</tr>
<%
		}
		if (!isProfile) {
%>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/profile.gif">&nbsp;
		<font size=3><a href="profile.jsp?client=mw"><%=Resource.get("navigator_jsp_6")%></a></font></td>
	</tr>
<%
		}
%>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/model.gif">&nbsp;
		<font size=3><a href="mymodelspace.jsp?client=mw&author=<%=userID%>">
		<%=Resource.get("navigator_jsp_7")%></a></font></td>
	</tr>
<%
		if(isStudent) {
%>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/model.gif">&nbsp;
		<font size=3><a href="classmodelspace.jsp?client=mw&userid=<%=userID%>">
		<%=Resource.get("navigator_jsp_8")%></a></font></td>
	</tr>
<%
		}
%>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/report.gif">&nbsp;
		<font size=3><a href="myreportspace.jsp?client=mw&author=<%=userID%>">
		<%=Resource.get("navigator_jsp_9")%></a></font></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/comment.gif">&nbsp;
		<font size=3><a href="mycomments.jsp?client=mw&author=<%=userID%>">
		<%=Resource.get("navigator_jsp_10")%></a></font></td>
	</tr>
	</table>
<%
		if (isTeacher) {
			out.print("<br><p><b>"+Resource.get("navigator_jsp_11")+"</b></p><br>");
%>
	<table width="90%">
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder.gif">&nbsp;
		<font size=3><a href="myclasses.jsp?client=mw&worktype=report&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_12")%></a></font></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder2.gif">&nbsp;
		<font size=3><a href="mystudents.jsp?client=mw&worktype=report&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_13")%></a></font></td>
	</tr>
	</table>
<%
			out.print("<br><p><b>"+Resource.get("navigator_jsp_14")+"</b></p><br>");
%>
	<table width="90%">
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder.gif">&nbsp;
		<font size=3><a href="myclasses.jsp?client=mw&worktype=model&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_12")%></a></font></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder2.gif">&nbsp;
		<font size=3><a href="mystudents.jsp?client=mw&worktype=model&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_13")%></a></font></td>
	</tr>
	</table>
<%
			out.print("<br><p><b>"+Resource.get("navigator_jsp_15")+"</b></p><br>");
%>
	<table width="90%">
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder.gif">&nbsp;
		<font size=3><a href="myclassinfo.jsp?client=mw&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_12")%></a></font></td>
	</tr>
	<tr bgcolor="white">
		<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/folder2.gif">&nbsp;
		<font size=3><a href="mystudentinfo.jsp?client=mw&teacher=<%=userID%>">
		<%=Resource.get("navigator_jsp_13")%></a></font></td>
	</tr>
	</table>
<%
		}
	}
%>

<br>
<p><b><%=Resource.get("navigator_jsp_16")%></b></p>
<br>
<table border="1" bgcolor="#EEF3F6" width="90%">
	<tr>
		<td>
		<font size="2">
		&nbsp;<%=Utilities.isEmptyEntry(firstName)? "" : firstName%>
		&nbsp;<%=Utilities.isEmptyEntry(lastName)? "" : lastName%>
		</font>
		</td>
	</tr>
<%
	if (!Utilities.isEmptyEntry(institution)) {
%>
	<tr>
		<td><font size="2">&nbsp;<%=institution%></font></td>
	</tr>
<%
	}
	if (!Utilities.isEmptyEntry(state)) {
%>
	<tr>
		<td><font size="2">&nbsp;<%=state%></font></td>
	</tr>
<%
	}
	if (!Utilities.isEmptyEntry(country)) {
%>
	<tr>
		<td><font size="2">&nbsp;<%=country%></font></td>
	</tr>
<%
	}
%>
</table>

<br>
<br>
