package org.concord.mwbackend.web;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.interfaces.CommentManager;
import org.concord.mwbackend.interfaces.CommentManagerHome;
import org.concord.mwbackend.util.MwConstants;

/**
 * Servlet Class
 * 
 * @web.servlet name="CommentManager" display-name="Name for CommentManager"
 *              description="Description for CommentManager"
 * @web.servlet-mapping url-pattern="/comment"
 * @web.servlet-init-param name="A parameter" value="A value"
 * @web.ejb-ref name="ejb/CommentManager" type="Session"
 *              home="org.concord.mwbackend.interfaces.CommentManagerHome"
 *              remote="org.concord.mwbackend.interfaces.CommentManager" description="Reference to
 *              the CommentManager EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/CommentManager" jndi-name="ejb/CommentManager"
 */
public class CommentManagerServlet extends HttpServlet {

	private CommentManagerHome home;

	public CommentManagerServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(CommentManagerHome.COMP_NAME);
			home = (CommentManagerHome) PortableRemoteObject.narrow(ref, CommentManagerHome.class);
		} catch (NamingException e) {
			throw new ServletException("Lookup failure: ");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String queryString = req.getQueryString();
		if (queryString == null) {
			// a fancy way of passing objects directly from the client
			Object[] objects = null;
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(req.getInputStream());
				objects = (Object[]) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
			if (objects == null || objects.length == 0)
				return;
			String requestType = (String) objects[0];
			if (requestType == null)
				return;
			if (requestType.equalsIgnoreCase("submit")) {
				submit(req, resp, objects);
			}
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {

		String week = req.getParameter("week");
		if (week != null) {
			getLatestComments(req, resp);
			return;
		}

		String address = req.getParameter("address");
		if (address != null) {
			getCommentsOnPage(req, resp, URLDecoder.decode(address, "UTF-8"));
			return;
		}

		String client = req.getParameter("client");
		if ("mw".equals(client)) {
			String action = req.getParameter("action");
			if ("table".equals(action)) {
				tabulateComments(req, resp);
			} else if ("delete".equals(action)) {
				String id = req.getParameter("primarykey");
				if (id != null) {
					deleteComment(Integer.valueOf(id), req, resp);
				}
			} else if ("view".equals(action)) {
				String id = req.getParameter("primarykey");
				if (id != null) {
					viewComment(Integer.valueOf(id), req, resp);
				}
			}
		}

	}

	private static String getIpAddress(HttpServletRequest request) {
		String xff = request.getHeader("x-forwarded-for");
		return xff == null ? request.getRemoteAddr() : xff;
	}

	private void submit(HttpServletRequest req, HttpServletResponse resp, Object[] objects) {
		String url = (String) objects[1];
		String title = (String) objects[2];
		String body = (String) objects[3];
		String user = (String) objects[4];
		String ip = getIpAddress(req);
		boolean success = false;
		try {
			CommentManager bean = home.create();
			success = bean.comment(url, title, body, user, ip);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.setStatus(success ? HttpServletResponse.SC_CREATED
				: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private void getCommentsOnPage(HttpServletRequest req, HttpServletResponse resp, String url) {

		StringBuffer sb = new StringBuffer(
				"<html><body><font face=\"Verdana\" size=\"2\"><h2>"+Resource.get("CommentManagerServlet_java_Discussion")+"</h2>");
		StringBuffer sb2 = new StringBuffer(
				"<table width=\"100%\" border=1><tr bgcolor=#D6DEEC><td><i>"+Resource.get("CommentManagerServlet_java_Who")+"</i></td><td><i>"+Resource.get("CommentManagerServlet_java_Subject")+"</i></td><td><i>"+Resource.get("CommentManagerServlet_java_Content")+"</i></td></tr>");

		List list = null;
		try {
			CommentManager bean = home.create();
			list = bean.getComments(url);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				String userID = (String) map.get("userID");
				String state = (String) map.get("state");
				String country = (String) map.get("country");
				sb2.append("<tr valign=\"top\">");
				sb2.append("<td><img src=\"http://mw.concord.org/modeler/man.gif\">&nbsp;<b>"
						+ (userID != null ? userID : "Anonymous") + "</b><font size=\"-1\"><br>");
				if (state != null)
					sb2.append(state + ", ");
				if (country != null)
					sb2.append(country);
				resp.setCharacterEncoding("GBK");
				sb2.append("<br>" + map.get("time"));
//				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String datetime = df.format(new java.util.Date());
//				sb2.append("<br>" + datetime);
				sb2.append("</font></td>");
				sb2.append("<td>" + map.get("title") + "</td>");
				sb2.append("<td>" + map.get("body") + "</td>");
				sb2.append("</tr>");
			}

		}

		int n = list == null ? 0 : list.size();
		sb.append("(<i>" +Resource.get("CommentManagerServlet_java_OTP")+ n + Resource.get("CommentManagerServlet_java_comment") + "</i>)");
		sb.append(sb2);
		sb.append("</table></font></body></html>");

		String str = new String(sb);

		try {
			resp.setContentType("text/html");
			PrintWriter writer = resp.getWriter();
			if (n == 0) {
				writer
						.println("<html><body><font face=\"Verdana\">"+Resource.get("CommentManagerServlet_java_NCP")+"</font></body></html>");
			} else {
				writer.println(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getLatestComments(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		int i = 1;
		String weeks = req.getParameter("week");
		if (weeks != null) {
			try {
				i = Integer.parseInt(weeks);
			} catch (Exception e) {
			}
		}

		StringBuffer sb = new StringBuffer(
				"<html><body><font face=\"Verdana\" size=\"2\"><h2>"+Resource.get("CommentManagerServlet_java_CIL") + i
						+Resource.get("CommentManagerServlet_java_weeks")+"</h2>");
		StringBuffer sb2 = new StringBuffer(
				"<table width=\"100%\" border=\"1\"><tr align=\"center\"><td><i>"+Resource.get("CommentManagerServlet_java_Who")+"</i></td><td><i>"+Resource.get("CommentManagerServlet_java_Page")+"</i></td><td><i>"+Resource.get("CommentManagerServlet_java_Subject")+"</i></td><td><i>"+Resource.get("CommentManagerServlet_java_Content")+"</i></td></tr>");

		List list = null;
		try {
			CommentManager bean = home.create();
			list = bean.getCommentsAfter(System.currentTimeMillis() - i
					* MwConstants.MILLISECONDS_OF_A_WEEK);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				sb2.append("<tr valign=\"top\">");
				sb2.append("<td><img src=\"http://mw.concord.org/modeler/man.gif\">&nbsp;<b>"
						+ map.get("userID") + "</b><font size=\"-1\">");
				sb2.append("<br>" + map.get("country"));
				sb2.append("<br>" + map.get("time"));
				sb2.append("</font></td>");
				sb2.append("<td>" + map.get("url") + "</td>");
				sb2.append("<td>" + map.get("title") + "</td>");
				sb2.append("<td>" + map.get("body") + "</td>");
				sb2.append("</tr>");
			}

		}

		int n = list == null ? 0 : list.size();
		sb.append("(<i>" + Resource.get("CommentManagerServlet_java_CIL") +i + Resource.get("CommentManagerServlet_java_ZD") + n + Resource.get("CommentManagerServlet_java_comment") +"</i>)");
		sb.append(sb2);
		sb.append("</table></font></body></html>");

		String str = new String(sb);

		try {
			resp.setContentType("text/html");
			PrintWriter writer = resp.getWriter();
			if (n == 0) {
				writer.println("<html><body><font face=\"Verdana\">"+ Resource.get("CommentManagerServlet_java_CIL") + i
						+ Resource.get("CommentManagerServlet_java_ZHOU") + Resource.get("CommentManagerServlet_java_NOComment") + "</font></body></html>");
			} else {
				writer.println(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void tabulateComments(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		List list = null;
		try {
			CommentManager bean = home.create();
			list = bean.getCommentsAfter(System.currentTimeMillis() - 8
					* MwConstants.MILLISECONDS_OF_A_WEEK);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					Resource.get("CommentManagerServlet_java_FailGetComment"));
			return;
		}
		if (list == null || list.isEmpty()) {
			return;
		}

		Vector<Vector> colData = new Vector<Vector>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map map = (Map) it.next();
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(Integer.valueOf((String) map.get("id")));
			rowData.add((String) map.get("url"));
			rowData.add((String) map.get("title"));
			rowData.add((String) map.get("userID"));
			rowData.add((String) map.get("ip"));
			rowData.add((String) map.get("time"));
			colData.add(rowData);
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(resp.getOutputStream()));
			out.writeObject(colData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private void deleteComment(int pk, HttpServletRequest req, HttpServletResponse resp) {
		boolean success = false;
		try {
			CommentManager bean = home.create();
			success = bean.deleteComment(pk);
		} catch (Exception e) {
			try {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Failed in getting comment #" + pk);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return;
		}
		resp.setContentType("text/html");
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.println(success ? "The selected comment #" + pk
					+ " was deleted from the database." : "The selected comment #" + pk
					+ " cannot be deleted.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private void viewComment(int pk, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String text = null;
		try {
			CommentManager bean = home.create();
			text = bean.getComment(pk);
		} catch (CreateException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Failed in getting comment #" + pk);
			e.printStackTrace();
			return;
		}
		OutputStream sos = null;
		try {
			sos = resp.getOutputStream();
			resp.setContentType("text/html");
			sos.write(text.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Failed in getting comment #" + pk);
		} finally {
			if (sos != null) {
				try {
					sos.close();
				} catch (IOException e) {
				}
			}
		}

	}

}
