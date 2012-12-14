package org.concord.mwbackend.web;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.interfaces.UserContact;
import org.concord.mwbackend.interfaces.UserContactHome;

/**
 * Servlet Class
 * 
 * @web.servlet name="UserContact" display-name="Name for UserContact" description="Description for
 *              UserContact"
 * @web.servlet-mapping url-pattern="/auth"
 * @web.servlet-init-param name="A parameter" value="A value"
 * @web.ejb-ref name="ejb/UserContact" type="Session"
 *              home="org.concord.mwbackend.interfaces.UserContactHome"
 *              remote="org.concord.mwbackend.interfaces.UserContact" description="Reference to the
 *              UserContact EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/UserContact" jndi-name="ejb/UserContact"
 */
public class AuthenticationServlet extends HttpServlet {

	private UserContactHome home;

	public AuthenticationServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(UserContactHome.COMP_NAME);
			home = (UserContactHome) PortableRemoteObject.narrow(ref, UserContactHome.class);
		} catch (Exception e) {
			throw new ServletException("Lookup failure: ");
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String userID = req.getParameter("userid");
		if (userID != null)
			userID = URLDecoder.decode(userID, "UTF-8");
		String password = req.getParameter("password");
		if (password != null)
			password = URLDecoder.decode(password, "UTF-8");
		boolean b = false;
		UserContact bean = null;
		try {
			bean = home.create();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (bean != null) {
			b = bean.authenticate(userID, password);
			try {
				bean.remove();
			} catch (RemoveException e) {
				e.printStackTrace();
			}
		}
		resp.setStatus(b ? HttpServletResponse.SC_OK : HttpServletResponse.SC_UNAUTHORIZED);
	}

}
