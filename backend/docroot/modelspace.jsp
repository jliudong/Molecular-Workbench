<jsp:directive.page import="java.io.UnsupportedEncodingException" />
<jsp:directive.page import="java.net.URLDecoder" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />

<jsp:useBean id="modelSpace"
	class="org.concord.mwbackend.web.ModelSpace" scope="request" />
<jsp:setProperty name="modelSpace" property="userID" />
<jsp:setProperty name="modelSpace" property="password" />

<html xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

<body bgcolor="white" face="Trebuchet MS">

<center>

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
			} else if ("User".equals(cn)) {
				// If the client runs on Java 5 and launches via Java Web Start, 
				// there is a chance that the cookie will take the following form:
				// JSESSIONID=xxx; User=xxx
				// instead of 
				// userid=xxx; password=xxx
				// and somehow, some properties set through the HttpResponse are
				// also retained (as part of the session?).
				// The following code handles such a situation, which hasn't been
				// found on clients using Java 6.
				String s = cookies[i].getValue();
				if (s != null) {
					try {
						s = URLDecoder.decode(s, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					String[] sa = s.split(";");
					for (int n = 0; n < sa.length; n++) {
						int k = sa[n].indexOf("=");
						if (k >= 0) {
							String s1 = sa[n].substring(0, k).trim();
							String s2 = sa[n].substring(k + 1).trim();
							if ("password".equalsIgnoreCase(s1)) {
								password = s2;
							}
						} else {
							userID = sa[n];
						}
					}
				}
			}
		}
	}
%>

<table width="100%" cellpadding="5">
	<tr bgcolor="<%=MwConstants.BANNER_COLOR%>">
		<td align="left">
			<b><font size="4" color="white"><%=Resource.get("modelspace_jsp_MWMspace") %></font></b>
		</td>
	</tr>
	<tr>
		<td align="right">
<%
	String jspName = Utilities.getFileName(Utilities.getBaseURL(request));
	String action = request.getParameter("action");
	if ("logout".equals(action)) {
		modelSpace.getUserFinder().removeUserCookie(response);
	} else {
		if (userID == null)
			userID = request.getParameter("userid");
	}
	if (password == null)
		password = request.getParameter("password");
	Person user = null;
	if (password != null && userID != null) {
		user = modelSpace.findPerson(userID);
		if (user != null && !password.equals(user.getPassword())) {
			user = null;
		}
		if (user == null)
			modelSpace.getUserFinder().removeUserCookie(response);
	}
	if (user != null) {
		out.print("<font size=3>"+Resource.get("home_jsp_Welcome") + userID);
		modelSpace.setUserID(userID);
		modelSpace.setPassword(password);
		if ("login".equals(action)) {
			modelSpace.getUserFinder().setUserCookie(response);
		}
%> 

<font size=3>
| <a href="home.jsp?client=mw"><%=Resource.get("mymodelspace_jsp_MyHome") %></a> 
| <a href="mymodelspace.jsp?client=mw&author=<%=userID%>"><%=Resource.get("mymodelspace_jsp_MyModels") %></a>
| <a href="<%=jspName%>?client=mw&action=logout"><%=Resource.get("home_jsp_logout") %></a>
</font>

<%
	} else {
%>
		<form action="<%=jspName%>" method="GET">
		<table>
			<tr>
				<td><font size=3><%=Resource.get("register_jsp_UserId") %></font></td>
				<td><input type="text" name="userid" size="10"></td>
				<td><font size=3><%=Resource.get("login_jsp_Password") %></font></td>
				<td><input type="password" name="password" size="10"></td>
				<td><input value="Login" name="login" type="submit"></td>
			</tr>
			<tr>
				<td colspan="5" align="right">
					<a href="forgot.jsp?client=mw"><font size=3><%=Resource.get("modelspace_jsp_FPassword") %></font></a>
				</td>
			</tr>
		</table>
		</form>
<%
	}
%>
		</td>
	</tr>
</table>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"><br>

<%
	String queryString = request.getQueryString();
	String batch = request.getParameter("batch");
	int q = batch == null ? 1 : Integer.valueOf(batch).intValue();
	List list = modelSpace.getPublishedItemsOfDays(q, 90);
	if (q > 1) {
		String prev = Utilities.getBaseURL(request)
		+ "?"
		+ queryString.replaceFirst("batch=" + q, "batch=" + (q - 1));
%>

<font size=3>[ <a href="<%=prev%>"><%=Resource.get("modelspace_jsp_NewDays") %></a> ]</font>

<%
	}
	String next = Utilities.getBaseURL(request) + "?";
	if (queryString == null) {
		next += "client=mw&batch=" + (q + 1);
	} else {
		if (queryString.indexOf("batch=") != -1) {
			next += queryString.replaceFirst("batch=" + q, "batch=" + (q + 1));
		} else {
			next += queryString + "&batch=" + (q + 1);
		}
	}
	int count = list == null ? 0 : list.size();
%>

<font size=3>[ <a href="<%=next%>"><%=Resource.get("modelspace_jsp_OlderDays") %></a> ]</font>

<table width="100%" cellpadding="2" cellspacing="2">
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="left">
			<font size=3><%=Resource.get("mymodelspace_jsp_TrashBeforeN") %> 
			<%=count%>&nbsp;<%=Resource.get("modelspace_jsp_PIP") %>
			<%=Resource.get("modelspace_jsp_CNS") %></font>
		</th>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><font size=3>#</font></i></td>
		<td><i><font size=3><%=Resource.get("modelspace_jsp_MView") %></font></i></td>
		<td><i><font size=3><%=Resource.get("modelspace_jsp_Author") %></font></i></td>
		<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Download") %></font></i></td>
		<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Size") %></font></i></td>
		<td><i><font size=3><%=Resource.get("mymodelspace_jsp_Time") %></font></i></td>
	</tr>
<%
	if (list != null) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map map = (Map) it.next();
			String author = (String) map.get("author");
			String zip = (String) map.get("zipfile");
			String url = (String) map.get("url");
%>
	<tr valign="top" align="left">
		<td bgcolor="<%=MwConstants.ROW_COLOR%>"><font size=3><%=map.get("id")%></font></td>
		<td><font size=3>&#160;<a href="model/<%=url%>"> <%=map.get("title")%></a></font></td>
<%
			if (author == null) {
%>
		<td>Anonymous</td>
<%
			} else {
%>
		<td><font size=3>&#160;<a href="mymodelspace.jsp?client=mw&author=<%=author%>"><%=author%></a>
<%
				if (author.equals(userID)) out.print("&#8224;");
%>
		</font></td>
<%
			}
%>
		<td><font size=2>&#160;<a href="model/<%=Utilities.getParent(url) + zip%>"><%=zip%></a></font></td>
		<td><font size=2><%=map.get("zipsize") + " KB"%></font></td>
		<td><font size=2><%=map.get("time")%></font></td>
	</tr>
<%
		}
	}
%>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="center">
			<font size=2><%=Resource.get("modelspace_jsp_SCROLLView") %></font>
		</th>
	</tr>
</table>

</center>

<%@ include file="footer.jsp"%>

</body>
</html>
