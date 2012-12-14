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

import org.concord.mwbackend.interfaces.ModelManager;
import org.concord.mwbackend.interfaces.ModelManagerHome;
import org.concord.mwbackend.util.MwConstants;
import org.concord.mwbackend.util.Unzipper;
import org.concord.mwbackend.util.Utilities;

/**
 * Servlet Class
 * 
 * @web.servlet name="ModelReceiver" display-name="Name for ModelReceiver" description="Description
 *              for ModelReceiver"
 * @web.servlet-mapping url-pattern="/modelupload"
 * @web.ejb-ref name="ejb/ModelManager" type="Session"
 *              home="org.concord.mwbackend.interfaces.ModelManagerHome"
 *              remote="org.concord.mwbackend.interfaces.ModelManager" description="Reference to the
 *              ModelManager EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/ModelManager" jndi-name="ejb/ModelManager"
 */
public class ModelReceiverServlet extends HttpServlet {

	private final static String CONTEXT = "mw/model";
	private ModelManagerHome home;

	public ModelReceiverServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(ModelManagerHome.COMP_NAME);
			home = (ModelManagerHome) PortableRemoteObject.narrow(obj, ModelManagerHome.class);
		} catch (NamingException e) {
			throw new ServletException("ModelManager lookup failed.");
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String queryString = req.getQueryString();
		if (queryString == null)
			return;
		String sendback = null;
		// WARNING!!! uploading a binary stream is a special case: calling
		// getParameter() will cause reading the stream that follows
		if (queryString.startsWith("client=mw&action=upload")) {
			File tmpzip = new File(MwConstants.HOME_DIRECTORY + CONTEXT + "/"
					+ System.currentTimeMillis() + ".zip");
			Utilities.save(req.getInputStream(), tmpzip);
			String path = unzip(tmpzip);
			String zipFile = req.getParameter("zipfile");
			int zipSize = (int) Math.round((float) tmpzip.length() / 1024.0f);
			if (zipSize == 0)
				zipSize = 1;
			String userID = req.getParameter("userid");
			String privacy = req.getParameter("privacy");
			String title = req.getParameter("title");
			String firstPage = req.getParameter("firstpage");
			String url = Utilities.getFileName(path) + "/";
			if (firstPage != null) {
				url += firstPage;
			} else {
				url += Utilities.changeExtension(zipFile, "cml");
			}
			String filename = "";
			if (title == null || title.trim().equals("")) {
				filename = "Untitled";
			} else {
				int n = Math.min(title.length(), 20);
				char c;
				for (int i = 0; i < n; i++) {
					c = title.charAt(i);
					if (c >= 'A' && c <= 'z')
						filename += c;
				}
			}
			filename += "_" + Utilities.currentTimeToDashedString() + ".zip";
			Utilities.copy(tmpzip, new File(MwConstants.HOME_DIRECTORY + path, filename));
			System.gc(); // it seems we must call this method in order to delete
			tmpzip.delete();
			ModelManager bean = null;
			try {
				bean = home.create();
				bean.receive(filename, zipSize, url, title, userID, privacy);
				bean.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendback = "<html><body><font face=\"Verdana\">" + zipFile
					+ " was successfully uploaded.</font></body></html>";
		}
		if (sendback != null) {
			resp.setContentType("text/html");
			PrintWriter writer = null;
			try {
				writer = resp.getWriter();
				writer.println(sendback);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null)
					writer.close();
			}
		}
	}

	private String unzip(File tmpzip) throws FileNotFoundException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpzip));
		String path = CONTEXT + "/" + Utilities.currentTimeToHexString();
		Unzipper.unzip(zis, new File(MwConstants.HOME_DIRECTORY + path));
		return path;
	}

}
