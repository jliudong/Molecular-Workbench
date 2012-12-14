<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="modelSpace"
	class="org.concord.mwbackend.web.ModelSpace" scope="request" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>
<font size="2">

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"> <!-- banner -->
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
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	if (userID == null)
		userID = request.getParameter("userid");
	if (password == null)
		password = request.getParameter("password");
	String action = request.getParameter("action");
	if ("delete".equals(action) || "undelete".equals(action)) {
		String[] id = request.getParameterValues("id");
		if (id != null && id.length > 0) {
			int[] iArray = new int[id.length];
			for (int i = 0; i < id.length; i++) {
				iArray[i] = Integer.valueOf(id[i]).intValue();
			}
			modelSpace.setDeleted("delete".equals(action), iArray);
			response.setHeader("action", "back");
		}
	}

	Person user = null;
	if (userID != null) {
		user = modelSpace.findPerson(userID);
		if (user != null) {
			if (!user.getPassword().equals(password)) {
				user = null;
			}
		}
	}
	boolean trash = false;
	String author = request.getParameter("author");
	if (author != null) {
		boolean isMe = user == null ? false : author.equals(user.getUserID());
		String type = request.getParameter("type");
		trash = "trash".equals(type);
		List list = null;
		if(isMe) {
			list = modelSpace.getAllItemsBy(author, trash);
		} else {
			if( user != null && user.getType() == Person.TEACHER && user.getUserID().equals(userFinder.findTeacher(author))) {
				list = modelSpace.getNonPrivateItemsBy(author);	
			} else {
				list = modelSpace.getPublishedItemsBy(author);
			}
		}
		int n = list != null ? list.size() : 0;
		if (isMe) { // add a form
%>

<form action="<%=jspName%>" method="POST">

<%
		}
%>

<table width="100%">
	<tr>
		<td width="15%" valign="top" align="left"
			bgcolor="<%=MwConstants.SIDEBAR_COLOR%>">
		<p><b><!-- side bar -->
<%
 		if (isMe) {
 			out.print(trash ? Resource.get("mymodelspace_jsp_Trash") : Resource.get("mymodelspace_jsp_MyModels"));
 		} else {
 			out.print(author + Resource.get("mymodelspace_jsp_sModels"));
 		}
%>
		</b></p>
		<p>
		<table width="90%">
			<tr bgcolor="white">
				<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/earth.gif">&nbsp;
				<font size=3><a href="modelspace.jsp?client=mw&action=list">
				<%=Resource.get("mymodelspace_jsp_AllModels")%></a></font></td>
			</tr>
			<tr bgcolor="white">
				<td>
<%
		if (isMe) {
			if (trash) {
%>
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/model.gif">&nbsp;
			<font size=3><a href="<%=jspName%>?client=mw&author=<%=author%>">
			<%=Resource.get("mymodelspace_jsp_MyModels")%></a></font>
<%
 			} else {
%>
			<img src="<%=MwConstants.PUBLIC_ROOT%>webres/trash.gif">&nbsp;
			<font size=3><a href="<%=jspName%>?client=mw&author=<%=author%>&type=trash">
			<%=Resource.get("mymodelspace_jsp_Trash")%></a></font>
<%
 			}
 		}
%>
				</td>
			</tr>
<%
		if (isMe) {
%>
			<tr bgcolor="white">
				<td><img src="<%=MwConstants.PUBLIC_ROOT%>webres/house.gif">&nbsp;
				<font size=3><a href="home.jsp?client=mw"><%=Resource.get("mymodelspace_jsp_MyHome")%></a></font></td>
			</tr>
<%
		}
%>
			<tr><td></td></tr>
		</table>
		</td>
		<td width="85%"><!-- begin the central area -->
		<font size=3><p>
<%
		if (isMe) {
			if (trash) {
%>
			<%=Resource.get("mymodelspace_jsp_Hi")%> <b><%=user.getFirstName()%></b>, <%=Resource.get("mymodelspace_jsp_TrashBeforeN")%> <%=n%> <%=Resource.get("mymodelspace_jsp_items")%>.
<%
			} else {
%>
			<%=Resource.get("mymodelspace_jsp_Hi")%> <b><%=user.getFirstName()%></b>, <%=Resource.get("mymodelspace_jsp_submitBeforeN")%> <%=n%> <%=Resource.get("mymodelspace_jsp_items")%>.
<%
			}
		} else {
%>
		<%=Resource.get("mymodelspace_jsp_WeFound") %> <%=n%> <%=Resource.get("mymodelspace_jsp_Tiao") %> <b><%=author%> <%=Resource.get("mymodelspace_jsp_PM") %></b>.
<%
		}
%>
		</p></font>
		<table width="100%">
			<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
				<th colspan="6" align="left">
<%
		if (isMe) {
			if (trash) {
%>
			<input value=<%=Resource.get("mymodelspace_jsp_UnDelete")%> name="undelete" type="submit">
<%
 			} else {
%>
			<input value=<%=Resource.get("mymodelspace_jsp_Delete")%> name="delete" type="submit">
<%
 			}
 		}
%>
				</th>
			</tr>
			<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
<%
		if (isMe) out.print("<td><i><font size=3>"+Resource.get("mymodelspace_jsp_Select")+"</font></i></td>");
%>
				<td><i><font size=3>#</font></i></td>
<%
		if (isMe) out.print("<td><i><font size=3>"+Resource.get("mymodelspace_jsp_privacy")+"</font></i></td>");
%>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Title")%></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Download")%></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Size")%></font></i></td>
				<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Time")%></font></i></td>
			</tr>
<%
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
%>
			<tr valign="top">
<%
				String id = (String) map.get("id");
				String privacy = (String) map.get("privacy");
				if(privacy == null) privacy = "public";
				if (isMe) {
%>
				<td><input type="checkbox" name="id" value="<%=id%>"></td>
<%
				}
%>
				<td><font size=3><%=id%></font></td>
<%				if (isMe) {
					if("public".equals(privacy)) {
						out.print("<td><font size=3 color=green>" + privacy + "</font></td>");
					}
					else if("private".equals(privacy)) {
						out.print("<td><font size=3 color=red>" + privacy + "</font></td>");
					}
					else if("class".equals(privacy)) {
						out.print("<td><font size=3 color=#000080>" + user.getKlass() + "</font></td>");
					}
					else if("teacher".equals(privacy)) {
						out.print("<td><font size=3 color=gray>" + user.getTeacher() + "</font></td>");
					}
					else {
						out.print("<td><font size=3>" + privacy + "</font></td>");						
					}
				}
				String url = (String) map.get("url");
%>
				<td>
				<font size=3>&#160;<a href="model/<%=url%>"><%=map.get("title")%></a></font>
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
<%
		if (isMe) {
			if (trash) {
%>
			<input value=<%=Resource.get("mymodelspace_jsp_UnDelete")%> name="undelete" type="submit">
<%
 			} else {
%>
			<input value=<%=Resource.get("mymodelspace_jsp_Delete")%> name="delete" type="submit">
<%
 			}
 		}
%>
				</th>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%
		if (isMe) {
%>
</form>
<%
		}
	}
%>

</font>
</center>

<%@ include file="footer.jsp"%>

</body>
</html>
