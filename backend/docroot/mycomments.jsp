<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:directive.page import="org.concord.mwbackend.business.TimeUtil" />
<jsp:useBean id="commentSpace"
	class="org.concord.mwbackend.web.CommentSpace" scope="request" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center><font size="2"> <img
	src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"> <br>
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
    String jspName = Utilities.getFileName(Utilities
            .getBaseURL(request));
    if (userID == null)
        userID = request.getParameter("userid");
    if (password == null)
        password = request.getParameter("password");
    Person user = userFinder.findAuthorizedUser(userID, password);
    if (user == null) {
%> <img src="<%=MwConstants.PUBLIC_ROOT%>webres/stopsign.gif"> <font
	size="3" color="red"><%=Resource.get("mycomments_jsp_1")%></font> <font
	size="3"><%=Resource.get("mycomments_jsp_2")%><a
	href="home.jsp?client=mw"><b><%=Resource.get("mycomments_jsp_3")%></b></a><%=Resource.get("mycomments_jsp_4")%></font>
<font size="3"><%=Resource.get("mycomments_jsp_5")%> <b><a
	href="register.jsp?client=mw"><%=Resource.get("mycomments_jsp_6")%></a></b></font>

<%
    } else {
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String[] id = request.getParameterValues("id");
            if (id != null && id.length > 0) {
                int[] iArray = new int[id.length];
                for (int i = 0; i < id.length; i++) {
                    iArray[i] = Integer.valueOf(id[i]).intValue();
                }
                commentSpace.deleteComment(iArray);
                response.setHeader("action", "back");
            }
        }

        List list = commentSpace.getCommentsBy(userID);
        int n = list != null ? list.size() : 0;
%> <%=Resource.get("mycomments_jsp_7")%> <%=n%> <%=Resource.get("mycomments_jsp_8")%>
<br>

<form action="<%=jspName%>" method="POST">
<table width="100%" cellpadding="5" cellspacing="2">
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="left"><input
			value=<%=Resource.get("mycomments_jsp_9")%> name="delete"
			type="submit"></th>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td><i><%=Resource.get("mycomments_jsp_10")%></i></td>
		<td><i>#</i></td>
		<td><i><%=Resource.get("mycomments_jsp_11")%></i></td>
		<td><i><%=Resource.get("mycomments_jsp_12")%></i></td>
		<td><i><%=Resource.get("mycomments_jsp_13")%></i></td>
		<td><i><%=Resource.get("mycomments_jsp_14")%></i></td>
	</tr>
	<%
	    if (list != null) {
	            for (Iterator it = list.iterator(); it.hasNext();) {
	                Map map = (Map) it.next();
	                String id = (String) map.get("id");
	%>
	<tr valign="top">
		<td><input type="checkbox" name="id" value=<%=id%>></td>
		<td><%=id%></td>
		<td><a href="<%=map.get("url")%>"><%=Resource.get("mycomments_jsp_15")%></a></td>
		<td bgcolor="<%=MwConstants.ROW_COLOR%>"><%=map.get("title")%></td>
		<td><%=map.get("body")%></td>
		<td>
		<%
		    response.setCharacterEncoding("gbk");%> <%=(String) map.get("time")%></td>
	</tr>
	<%
	            }
	        }
	%>
	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan="6" align="left"><input
			value=<%=Resource.get("mycomments_jsp_9")%> name="delete"
			type="submit"></th>
	</tr>
</table>
</form>

<%
    }
%> </font></center>

<%@ include file="footer.jsp"%>
</body>
</html>
