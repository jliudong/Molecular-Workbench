package org.concord.mwbackend.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.interfaces.Receptionist;
import org.concord.mwbackend.interfaces.ReceptionistHome;
import org.concord.mwbackend.util.MwConstants;

/**
 * Servlet Class
 * 
 * @web.servlet name="Receptionist" display-name="Name for Receptionist" description="Description
 *              for Receptionist"
 * @web.servlet-mapping url-pattern="/reception"
 * @web.servlet-init-param name="A parameter" value="A value"
 * @web.ejb-ref name="ejb/Receptionist" type="Session"
 *              home="org.concord.mwbackend.interfaces.ReceptionistHome"
 *              remote="org.concord.mwbackend.interfaces.Receptionist" description="Reference to the
 *              Receptionist EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/Receptionist" jndi-name="ejb/Receptionist"
 */
public class ReceptionistServlet extends HttpServlet {

	private ReceptionistHome home;
	private String clientHost, clientIpAddress;

	public ReceptionistServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(ReceptionistHome.COMP_NAME);
			home = (ReceptionistHome) PortableRemoteObject.narrow(ref, ReceptionistHome.class);
		} catch (NamingException e) {
			throw new ServletException("Lookup failure: ");
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String xff = req.getHeader("x-forwarded-for");
		if (xff == null) {
			clientIpAddress = req.getRemoteAddr();
		} else {
			String[] ip = xff.split(",");
			clientIpAddress = ip[0];
		}
		try {
			InetAddress inet = InetAddress.getByName(clientIpAddress);
			clientHost = inet == null ? clientIpAddress : inet.getHostName();
		} catch (UnknownHostException e) {
			clientHost = clientIpAddress;
		}

		resp.setContentType("text/html");

		String queryString = req.getQueryString();
		if (queryString != null) {
			if (queryString.startsWith("day")) {
				getLatestRecords(req, resp);
			} else {
				addRecord(req, resp);
			}
		}

	}

	private void getLatestRecords(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int i = 1;
		String days = req.getParameter("day");
		if (days != null) {
			try {
				i = Integer.parseInt(days);
			} catch (Exception e) {
				// ignore
			}
		}
		StringBuffer sb = new StringBuffer(
				"<html><body><img src=\"http://mw.concord.org/modeler/cclogo.gif\" align=\"right\"><h2>Molecular Workbench</h2>");
		StringBuffer sb2 = new StringBuffer(
				"<table width=\"100%\" border=\"1\"><tr align=\"center\"><td><i>User Name</i></td><td><i>IP Host</i></td><td><i>IP Number</i></td><td><i>OS</i></td><td><i>Java</i></td><td><i>MW Version</i></td><td><i>Java Web Start</i></td><td><i>Time</i></td></tr>");
		String n = null;
		try {
			Receptionist bean = home.create();
			List list = bean.getRecordsAfter(System.currentTimeMillis() - i
					* MwConstants.MILLISECONDS_OF_A_DAY);
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				if (n == null)
					n = (String) map.get("id");
				sb2.append("<tr valign=\"top\">");
				sb2.append("<td><font size=\"-2\">" + map.get("username") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("host") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("ip") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("os") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("javaversion") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("mwversion") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("jws") + "</font></td>");
				sb2.append("<td><font size=\"-2\">" + map.get("time") + "</font></td>");
				sb2.append("</tr>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("(<i>Launched " + n + " times since May 1, 2007; excluding CC</i>)");
		sb.append("<font face=\"Verdana\" size=\"2\">");
		sb.append("<p>Last " + i + " days</p>");
		sb.append(sb2);
		sb.append("</table></font></body></html>");
		PrintWriter writer = resp.getWriter();
		writer.println(sb.toString());
	}

	private void addRecord(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.println("<html><head><title>");
		out.println("Reception");
		out.println("</title></head>");
		out.println("<body>");
		out.println("<h1>");
		out.println("Welcome");
		out.println("</h1>");
		try {
			Receptionist bean = home.create();
			String userName = req.getParameter("username");
			String os = req.getParameter("os");
			String javaVersion = req.getParameter("javaversion");
			String mwVersion = req.getParameter("mwversion");
			String jwsFlag = req.getParameter("jws");
			bean.addRecord(userName, os, clientHost, clientIpAddress, javaVersion, mwVersion,
					jwsFlag);
			bean.remove();
			out.println("<p>");
			out.print(userName + "@" + clientHost);
			out.println("</p>");
		} catch (Exception e) {
			out.println(e.getMessage());
			e.printStackTrace(out);
		} finally {
			out.println("</body></html>");
			out.close();
		}
	}

}