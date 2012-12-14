package org.concord.mwbackend.web;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Class
 * 
 * @web.servlet name="Admin" display-name="Name for Admin" description="Description for Admin"
 * @web.servlet-mapping url-pattern="/admin"
 */
public class AdminServlet extends HttpServlet {

	private UserFinder finder;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String userID = request.getParameter("userid");
		if (userID != null)
			userID = URLDecoder.decode(userID, "UTF-8");
		String password = request.getParameter("password");
		if (password != null)
			password = URLDecoder.decode(password, "UTF-8");
		if (finder == null)
			finder = new UserFinder();
		boolean b = finder.findAdmin(userID, password) != null;
		response.setStatus(b ? HttpServletResponse.SC_OK : HttpServletResponse.SC_UNAUTHORIZED);
	}

}
