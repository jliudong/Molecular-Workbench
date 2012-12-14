<jsp:directive.page import="java.util.Iterator" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.Map" />
<jsp:directive.page import="org.concord.mwbackend.business.Person" />
<jsp:directive.page import="org.concord.mwbackend.util.MwConstants" />
<jsp:directive.page import="org.concord.mwbackend.util.Utilities" />
<jsp:directive.page import="org.concord.mwbackend.web.Resource" />
<jsp:useBean id="userFinder"
	class="org.concord.mwbackend.web.UserFinder" scope="request" />
<jsp:useBean id="reportSpace"
	class="org.concord.mwbackend.web.ReportSpace" scope="request" />

<html>
<body bgcolor="white" face="Trebuchet MS">

<center>
	<img src="<%=MwConstants.PUBLIC_ROOT%>webres/mwbanner3.gif">
</center>

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
	Person user = userFinder.findAuthorizedUser(userID, password);
	String teacher = request.getParameter("teacher");
	String[] teacherList = new String[]{
					"eberg", "Berg, Erik, Belmont MA",
					"cbrownsberger", "Brownsberger, Carolyn, Belmont MA",
					"slijek", "Lijek, Suzanne, Belmont MA",
					"pppartridge", "Partridge, Pat, Belmont MA",
					"klindsay", "Lindsay, Kirsten, Hockaday TX",
					"kaspfam5", "Kasparian, Janet, Portsmouth RI",
					"murphyl", "Murphy, Lisa, Portsmouth RI",
					"noblen", "Noble, Nycole, Portsmouth RI",
					"stevensj2", "Stevens, Joan, Portsmouth RI",
					"pguellnitz", "Guellnitz, Pete, Belmont MA",
					"arivera", "Rivera, Amanda, Belmont MA",
					"rsansom", "Sansom, Rebecca, Belmont MA",
					"sawilliams", "Williams, Stacy, Belmont MA",
					"breinan", "Breinan, Howie, Glastonbury CT",			
					"bfinazzo", "Finazzo, Brandi, Hockaday TX",
					"blawson", "Lawson, Beverly, Hockaday TX",
					"bpatrizi", "Patrizi, Bob, Hockaday TX",
					"beebek", "Beebe, Mickey, Portsmouth RI",
					"holsteint", "Holstein, Tom, Portsmouth RI",
					"perryl", "Perry, Leanne, Portsmouth RI",
					"TinaJohn", "John, Tina, Belmont MA",
					"akendall", "Kendall, Amber, Belmont MA",
					"jloosmann", "Loosmann, John, Belmont MA",
					"plohstreter", "Lohstreter, Pete, Hockaday TX",
					"rtaylor", "Taylor, Richard, Hockaday TX",
					"rwyatt", "Wyatt, Rachel, Hockaday TX",
					"amarale", "Amaral, Elaine, Portsmouth RI",
					"innisd", "Innis, Dave, Portsmouth RI",
					"rutkiewiczr", "Rutkiewicz, Bob, Portsmouth RI",
					"jschoonover", "Schoonover, Jeff, Portsmouth RI"
	};
	String keyword = "";
	String title = request.getParameter("keyword");
	if (title != null && title.trim().length() > 0) {
		String[] t = title.split("\\s+");
		keyword = "%";
		for(int i = 0; i < t.length; i++) {
			if(t[i].trim().equals("")) continue;
			keyword += t[i] + "%";
		}
	}
	String[] activityList=new String[]{
			"Atoms and Energy",
			"Heat and Temperature",
			"Electrostatics",
			"Atomic Structure",
			"Newton's Laws",
			"Excited States and Photons",
			"Spectroscopy",
			"-------------",
			"Gas Laws",
			"Phase Change",
			"Intermolecular Attractions",
			"Van Der Waals",
			"Molecular Geometry",
			"Solubility",
			"Chemical Bonds",
			"Chemical Reactions",
			"-------------",
			"Diffusion, Osmosis, and Active Transport",
			"Celluar Respiration",
			"Four Levels of Protein Structure",
			"Molecular Recognition",
			"Lipids and Carbohydrates",
			"Proteins and Nucleic Acids",
			"DNA to Protein",
			"Photosynthesis"
	};
%>

<form method=POST action="<%=jspName%>">
<table border="0" width="100%" cellpadding="10">

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<th colspan=6 align=LEFT>
		<b><font size="4" color="white"><%=Resource.get("reportsearch_jsp_1")%></font></b>
		</th>
	</tr>
	<tr>
		<td colspan=6>
			<select name="teacher">
	<% for(int i = 0; i < teacherList.length / 2; i++) {
		 if(teacherList[i*2].equals(teacher)) {
	%>
			<option value="<%=teacherList[i*2]%>" selected="selected"><%=teacherList[i*2+1]%></option>
	<%   } else {%>
			<option value="<%=teacherList[i*2]%>"><%=teacherList[i*2+1]%></option>
	<%   }
	  }
	%>
			</select>
			<select name="keyword">
	<% for(int i = 0; i < activityList.length; i++) {%>
	<%    if (activityList[i].equals(title)) {%>
			<option value="<%=activityList[i]%>" selected="selected"><%=activityList[i]%></option>
	<%    } else { %>
			<option value="<%=activityList[i]%>"><%=activityList[i]%></option>
	<%    }
	   }
	%>
			</select>
			<input value="<%=Resource.get("reportsearch_jsp_2")%>" type="submit" name="reportsearch">
		</td>
	</tr>
<%
	if (user != null && user.getUserID().equals("admin")) {
		List list = reportSpace.search(keyword, teacher, null);
		if(list != null && !list.isEmpty()) {
%>
	<tr>
		<td colspan=6>
    	<%=Resource.get("reportsearch_jsp_5")%><%=list.size()%><%=Resource.get("reportsearch_jsp_6")%> "<b><%=teacher%></b>"<%=Resource.get("reportsearch_jsp_7")%> "<b><%=title%></b>"<%=Resource.get("reportsearch_jsp_8")%>.
		</td>
	</tr>
	<tr bgcolor="<%=MwConstants.COLUMN_BAR_COLOR%>" align="left">
		<td width=20><i><font size="3">#</font></i></td>
		<td><i><font size="3"><%=Resource.get("reportsearch_jsp_9")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportsearch_jsp_10")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportsearch_jsp_7")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportsearch_jsp_11")%></font></i></td>
		<td><i><font size="3"><%=Resource.get("reportsearch_jsp_12")%></font></i></td>
	</tr>
<%
			if (list != null) {
				for (Iterator it = list.iterator(); it.hasNext();) {
					Map map = (Map) it.next();
					String student = (String) map.get("author");
					String fullname = (String) map.get("fullname");
					String teacherID = (String) map.get("teacher");
					teacher = (String) map.get("teacher_fullname");
					String school = (String) map.get("school");
					String url = (String) map.get("url");
%>
	<tr valign="top" align="left">
		<td width=20 bgcolor="<%=MwConstants.ROW_COLOR%>"><font size="3"><%=map.get("id")%></font></td>
		<td>&#160;<font size="3"><a href="report/<%=url%>"><%=map.get("title")%></a></font></td>
		<td>&nbsp;<font size="3"><a href="myreportspace.jsp?client=mw&author=<%=student%>"><%=fullname%>(<%=student%>)</a></font></td>
		<td>&nbsp;<font size="3"><a href="mystudentreports.jsp?client=mw&teacher=<%=teacherID%>"><%=teacher%>(<%=teacherID%>)</a></font></td>
		<td><font size="3"><%=school%></font></td>
		<td><font size="3"><%=map.get("time")%></font></td>
	</tr>
<%
				}
			}
		}
	} else {
%>
	<tr>
		<td colspan=6><%=Resource.get("reportsearch_jsp_13")%></td>
	</tr>
<%
	}
%>

	<tr bgcolor="<%=MwConstants.TOOLBAR_COLOR%>">
		<td colspan=6></td>
	</tr>

</table>
</form>

<%@ include file="footer.jsp"%>

</body>
</html>
