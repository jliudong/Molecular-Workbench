package org.concord.mwbackend.web;

import javax.servlet.http.HttpServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.business.Person;

/**
 * Servlet Class
 * 
 * @web.servlet name="Login" display-name="Login" description="Login"
 * @web.servlet-mapping url-pattern="/login"
 * @web.ejb-ref name="ejb/Login" type="Session" home="org.concord.mwbackend.interfaces.LoginHome"
 *              remote="org.concord.mwbackend.interfaces.Login" description="Reference to the Login
 *              EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/Login" jndi-name="ejb/Login"
 */
public class LoginServlet extends HttpServlet {

	private UserFinder idChecker;

	public LoginServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		idChecker = new UserFinder();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userID = req.getParameter("userid");
		if (userID != null)
			userID = URLDecoder.decode(userID, "UTF-8");
		String password = req.getParameter("password");
		if (password != null)
			password = URLDecoder.decode(password, "UTF-8");
		Person user = findAuthorizedUser(userID, password);
		String client = req.getParameter("client");
		if ("mw".equals(client))
			respondToMw(resp, user);
		sendBack(resp, userID, user != null);
	}

	private Person findAuthorizedUser(String userID, String password) {
		if (idChecker == null)
			return null;
		return idChecker.findAuthorizedUser(userID, password);
	}

	private void respondToMw(HttpServletResponse resp, Person u) throws IOException {
		boolean b = u != null;
		if (b) {
			String s = u.getUserID() + ";Password=" + u.getPassword() + ";Email=" + u.getEmailAddress() + ";FirstName="
					+ u.getFirstName() + ";LastName=" + u.getLastName() + ";Institution=" + u.getInstitution()
					+ ";State=" + u.getState() + ";Country=" + u.getCountry();
			Cookie cookie = new Cookie("User", URLEncoder.encode(s, "UTF-8"));
			cookie.setMaxAge(30 * 24 * 3600); // expire after 30 days
			cookie.setDomain("concord.org");
			resp.addCookie(cookie);
		} else {
			Cookie cookie = new Cookie("User", "");
			resp.addCookie(cookie);
		}
		resp.setHeader("action", "back");
	}

	private void sendBack(HttpServletResponse resp, String name, boolean b) {
		resp.setContentType("text/html");
		PrintWriter out = null;
		try {
			out = resp.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		out.println("<html><head><title>Login</title></head><body>");
		out.println("<h1>Login</h1>");
		if (b) {
			out.println("<p>Welcome, " + name + "!</p>");
		} else {
			out.println("<p><font color=#ff0000>Your ID and password are not correct.</font> Please try again.</p>");
		}
		out.println("</body></html>");
		out.close();
	}

}
