package org.concord.mwbackend.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.interfaces.ReportManager;
import org.concord.mwbackend.interfaces.ReportManagerHome;
import org.concord.mwbackend.util.MwConstants;
import org.concord.mwbackend.util.Unzipper;
import org.concord.mwbackend.util.Utilities;

/**
 * Servlet Class
 * 
 * @web.servlet name="ReportReceiver" display-name="Name for ReportReceiver"
 *              description="Description for ReportReceiver"
 * @web.servlet-mapping url-pattern="/reportupload"
 * @web.servlet-init-param name="A parameter" value="A value"
 * @web.ejb-ref name="ejb/ReportManager" type="Session"
 *              home="org.concord.mwbackend.interfaces.ReportManagerHome"
 *              remote="org.concord.mwbackend.interfaces.ReportManager" description="Reference to
 *              the ReportManager EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/ReportManager" jndi-name="ejb/ReportManager"
 */
public class ReportReceiverServlet extends HttpServlet {

	private final static String CONTEXT = "mw/report";
	private ReportManagerHome home;

	public ReportReceiverServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(ReportManagerHome.COMP_NAME);
			home = (ReportManagerHome) PortableRemoteObject.narrow(obj, ReportManagerHome.class);
		} catch (NamingException e) {
			throw new ServletException("ReportManager lookup failed.");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String queryString = req.getQueryString();
		if (queryString == null)
			return;
		String sendback = null;
		// WARNING!!! uploading a binary stream is a special case: calling
		// getParameter() will cause reading the stream that follows
		if (queryString.startsWith("client=mw&action=upload")) {
			File tmpzip = new File(MwConstants.HOME_DIRECTORY + CONTEXT + "/" + System.currentTimeMillis() + ".zip");
			Utilities.save(req.getInputStream(), tmpzip);
			String path = unzip(tmpzip);
			sendback = "<html><body><font face=Verdana>"+Resource.get("ReportReceiverServlet_jsp_SMvSpace")+"</font></body></html>";
			String title = req.getParameter("title");
			String userID = req.getParameter("userid");
			String collaborator = req.getParameter("collaborator");
			String filename = req.getParameter("filename");
			String url = Utilities.getFileName(path) + "/" + (filename == null ? "Untitled.cml" : filename);
			try {
				ReportManager bean = home.create();
				bean.receive(url, title, userID);
				if (collaborator != null) { // create a record for collaborators
					String[] s = collaborator.split(",");
					for (String x : s) {
						x = x.trim();
						if (!x.equals(""))
							bean.receive(url, title, x);
					}
				}
				bean.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sendback != null) {
			resp.setContentType("text/html");
			try {
				PrintWriter writer = resp.getWriter();
				writer.println(sendback);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String unzip(File tmpzip) throws FileNotFoundException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpzip));
		String path = CONTEXT + "/" + Utilities.currentTimeToHexString();
		Unzipper.unzip(zis, new File(MwConstants.HOME_DIRECTORY + path));
		System.gc(); // it seems we must call this method in order to delete
		tmpzip.delete();
		return path;
	}

}
