package org.concord.mwbackend.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.interfaces.ModelLocal;
import org.concord.mwbackend.interfaces.ModelManager;
import org.concord.mwbackend.interfaces.ModelManagerHome;
import org.concord.mwbackend.util.MwConstants;

/**
 * Servlet Class
 * 
 * @web.servlet name="JnlpGenerator" display-name="Name for JnlpGenerator" description="Description
 *              for JnlpGenerator"
 * @web.servlet-mapping url-pattern="/tmp.jnlp"
 */
public class JnlpGeneratorServlet extends HttpServlet {

	private ModelManagerHome modelManagerHome;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private boolean isModelDeleted(String address) {
		if (modelManagerHome == null) {
			try {
				Context ctx = new InitialContext();
				Object obj = ctx.lookup(ModelManagerHome.COMP_NAME);
				modelManagerHome = (ModelManagerHome) PortableRemoteObject.narrow(obj,
						ModelManagerHome.class);
			} catch (NamingException e) {
				e.printStackTrace();
				return false;
			}
		}
		ModelManager bean;
		try {
			bean = modelManagerHome.create();
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		} catch (CreateException e) {
			e.printStackTrace();
			return false;
		}
		ModelLocal model = null;
		try {
			model = bean.getByUrl(address);
			if (model == null)
				return false;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		return model.getDeleted();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			ServletException {

		String version = req.getParameter("version");
		String type = req.getParameter("type");
		String address = null;
		String queryString = req.getQueryString();
		if (queryString != null && queryString.startsWith("address=")) {
			address = queryString.substring(8);
		} else {
			address = req.getParameter("address");
		}
		boolean isDeleted = false;
		if (address != null) {
			address = URLDecoder.decode(address, "UTF-8");
			if (!address.toLowerCase().startsWith("http://") && !address.toLowerCase().startsWith("https://")) {
				// this resource is assumed to be in the MW Space
				if (type == null) {
					address = MwConstants.HOST + address;
				} else {
					if (type.equals("model")) {
						isDeleted = isModelDeleted(address);
					}
					address = MwConstants.HOST + type + "/" + address;
				}
			}
		} else {
			address = MwConstants.PUBLIC_ROOT + "index.cml";
		}

		resp.setContentType("application/x-java-jnlp-file");
		PrintWriter out = resp.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		if ("1".equals(version)) {
			out.println("<jnlp spec=\"1.0+\" codebase=\"http://mw.concord.org/modeler/\">");
		} else {
			out.println("<jnlp spec=\"1.0+\" codebase=\"" + MwConstants.PUBLIC_ROOT + "\">");
		}
		out.println("<information>");
		out.println("<title>Molecular Workbench</title>");
		out.println("<vendor>Concord Consortium, Inc.</vendor>");
		out.println("<homepage href=\"index.html\"/>");
		out
				.println("<description>The Advanced Educational Modeling Laboratory</description>");
		out.println("<icon href=\"mwlogo.gif\"/>");
		out.println("<offline-allowed/>");
		//out.println("<shortcut online=\"true\"><desktop/><menu/></shortcut>");
		out.println("</information>");
		out.println("<resources>");
		out.println("<j2se version=\"1.5+\"/>");
		out.println("<jar href=\"lib/workbench.jar\"/>");
		out.println("<property name=\"apple.awt.brushMetalLook\" value=\"true\"/>");
		out.println("<property name=\"apple.laf.useScreenMenuBar\" value=\"true\"/>");
		out.println("</resources>");
		out.println("<security>");
		out.println("<all-permissions/>");
		out.println("</security>");
		out.println("<application-desc main-class=\"org.concord.modeler.ModelerLauncher\">");
		out.println("<argument>remote</argument>");
		if (isDeleted) {
			out.println("<argument>" + MwConstants.HOST + "filenotfound.jsp?client=mw</argument>");
		} else {
			address = address.replaceAll(" ", "%20");
			out.println("<argument>" + address + "</argument>");
		}
		out.println("</application-desc>");
		out.println("</jnlp>");
		out.close();

	}

}
