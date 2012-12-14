<jsp:directive.page import="java.util.Collection" />
<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="classSpace" class="org.concord.mwbackend.web.ClassSpace" scope="request" />
<jsp:useBean id="userFinder" class="org.concord.mwbackend.web.UserFinder" scope="request" />

<html>

<body face="Trebuchet MS">


<center>

<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif"></center>

<%!
private boolean isReservedWord(String s) {
		return "admin".equalsIgnoreCase(s) || "user".equalsIgnoreCase(s) || "none".equalsIgnoreCase(s);
}
%>

<%
	String userID = request.getParameter("userid");
	String password = request.getParameter("password");
	String password2 = request.getParameter("password2");
	String email = request.getParameter("email");
	String email2 = request.getParameter("email2");
	String firstName = request.getParameter("firstname");
	String lastName = request.getParameter("lastname");
	String institution = request.getParameter("institution");
	String state = request.getParameter("state");
	String country = request.getParameter("country");
	String type = request.getParameter("type");
	String teacher = request.getParameter("teacher");
	String returnAddress = request.getParameter("returnaddress");
	String onreport = request.getParameter("onreport");

	boolean inputOK = true;
	String msg = null;

	// validating user id
	if (userID == null || userID.length() == 0) {
		msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_1")+"</font></p>";
		inputOK = false;
	} else {
		if (isReservedWord(userID)) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_2")+" " + userID + " "+Resource.get("registerlaststep_jsp_3")+"</font></p>";
			inputOK = false;
		} else {
			int n = userID.length();
			for (int i = 0; i < n; i++) {
				char c = userID.charAt(i);
				boolean b = (c >= 'a' && c <= 'z')
						|| (c >= 'A' && c <= 'Z')
						|| (c >= '0' && c <= '9') || c == '_';
				if (!b) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_4")+"</font></p>";
					inputOK = false;
					break;
				}
			}
		}
	}

	// validating email address
	if (inputOK) {
		if (email == null || email.indexOf("@") == -1) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_5")+"</font></p>";
			inputOK = false;
		} else if (!email.equals(email2)) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_6")+"</font></p>";
			inputOK = false;
		}
	}

	// validating password
	if (inputOK) {
		if (password == null) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_7")+"</font></p>";
			inputOK = false;
		} else if (!password.equals(password2)) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_8")+"</font></p>";
			inputOK = false;
		} else if (password.length() < 6) {
			msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_9")+"</font></p>";
			inputOK = false;
		} else {
			int n = password.length();
			for (int i = 0; i < n; i++) {
				char c = password.charAt(i);
				boolean b = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
				if (!b) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_10")+"</font></p>";
					inputOK = false;
					break;
				}
			}
		}
	}

	// checking student required information
	if (inputOK) {
		if (type != null) {
			int t = Integer.valueOf(type).intValue();
			if(t == Person.STUDENT) {
				if (firstName == null || firstName.length() == 0) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_11")+"</font></p>";
					inputOK = false;
				} else if (lastName == null || lastName.length() == 0) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_12")+"</font></p>";
					inputOK = false;
				} else if (institution == null || institution.length() == 0) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_13")+"</font></p>";
					inputOK = false;
				} else if (teacher == null || teacher.length() == 0 || "None".equals(teacher)) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_14")+"</font></p>";
					inputOK = false;				
				} else {
					int i = userFinder.findType(teacher);
					if(i != Person.TEACHER) {
						msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_15")+"<b>"+teacher+"</b> "+Resource.get("registerlaststep_jsp_16")+"</font></p>";
						inputOK = false;
					}
				}
			} else {
				if (teacher != null && !teacher.trim().equals("")) {
					msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_17")+"</font></p>";
					inputOK = false;
				} else if(t == Person.TEACHER) {
					if(institution == null || institution.length() == 0) {
						msg = "<p><font color=red>"+Resource.get("registerlaststep_jsp_18")+"</font></p>";
						inputOK = false;
					}
				}
			}
		}
	}

	if (inputOK) {
%>

<form method="post" action="register?action=register">
	<input type="hidden" name="userid" value="<%=userID%>">
	<input type="hidden" name="password" value="<%=password%>">
	<input type="hidden" name="email" value="<%=email%>">
	<input type="hidden" name="firstname" value="<%=firstName%>">
	<input type="hidden" name="lastname" value="<%=lastName%>">
	<input type="hidden" name="institution" value="<%=institution%>">
	<input type="hidden" name="state" value="<%=state%>">
	<input type="hidden" name="country" value="<%=country%>">
	<input type="hidden" name="type" value="<%=type%>">
	<input type="hidden" name="teacher" value="<%=teacher%>">

<%
		if (returnAddress != null) {
%>
	<input type="hidden" name="returnaddress" value="<%=returnAddress%>">
<%
		}
		if (onreport != null) {
%>
	<input type="hidden" name="onreport" value="<%=onreport%>">
<%
		}
%>

<center>
<table cellspacing="2" width="100%" border="0" cellpadding="2">
	<tr>
		<td bgcolor="<%=MwConstants.BANNER_COLOR%>" colspan="2">
		<h3><font color="white">&#160;<%=Resource.get("registerlaststep_jsp_19")%></font></h3>
		</td>
	</tr>
<%
		if (type != null && Integer.valueOf(type).intValue() == Person.STUDENT) {
			Collection c = classSpace.findClasses(teacher);
			if (c != null && !c.isEmpty()) {
%>
	<tr>
		<td><%=Resource.get("registerlaststep_jsp_20")%></td>
		<td><select name="classid">
<%
				String cid = null;
				for (Iterator it = c.iterator(); it.hasNext();) {
					cid = (String) it.next();
%>
			<option value="<%=cid%>"><%=cid%></option>
<%
				}
%>
		</select></td>
	</tr>
<%
			}
		}
%>
	<tr>
		<td><%=Resource.get("registerlaststep_jsp_21")%><br><hr align="left" width="50%">
		<font color="#888888" size="2"><b><%=Resource.get("registerlaststep_jsp_22")%></b></font></td>
		<td><select name="acceptnewsletter">
			<option value="yes"><%=Resource.get("registerlaststep_jsp_23")%></option>
			<option value="no" selected><%=Resource.get("registerlaststep_jsp_24")%></option>
		</select></td>
	</tr>
	<tr>
		<td><%=Resource.get("registerlaststep_jsp_25")%><br><hr align="left" width="50%">
		<font color="#888888" size="2"><b><%=Resource.get("registerlaststep_jsp_26")%></b></font></td>
		<td><select name="notifymodel">
			<option value="yes"><%=Resource.get("registerlaststep_jsp_23")%></option>
			<option value="no" selected><%=Resource.get("registerlaststep_jsp_24")%></option>
		</select></td>
	</tr>
<%
		if (type != null && Integer.valueOf(type).intValue() == Person.TEACHER) {
%>
	<tr>
		<td><%=Resource.get("registerlaststep_jsp_27")%><br><hr align="left" width="50%">
		<font color="#888888" size="2"><b><%=Resource.get("registerlaststep_jsp_28")%></b></font></td>
		<td><select name="notifyreport">
			<option value="yes"><%=Resource.get("registerlaststep_jsp_23")%></option>
			<option value="no" selected><%=Resource.get("registerlaststep_jsp_24")%></option>
		</select></td>
	</tr>
<%
		}
%>
	<tr>
		<td colspan=2>
		
		</td>
	</tr>
	<tr>
		<td colspan=2><input value=<%=Resource.get("registerlaststep_jsp_29")%> type="submit" name="register"></td>
	</tr>
</table>
</center>

</form>
<%
	} else {
		if("yes".equalsIgnoreCase(onreport)) {
			out.print("<center>"+msg+"<p><a href=\"register.jsp?client=mw&onreport=yes\">"+Resource.get("registerlaststep_jsp_30")+"</a></p></center>");			
		} else {
			out.print("<center>"+msg+"<p><a href=\"register.jsp?client=mw\">"+Resource.get("registerlaststep_jsp_30")+"</a></p></center>");
		}
	}
%>


<%@ include file="footer.jsp"%>

</body>
</html>
