package org.concord.mwbackend.web;

import javax.servlet.http.HttpServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.business.Person;

/**
 * Servlet Class
 * 
 * @web.servlet name="Classmate" display-name="Classmate" description="Classmate"
 * @web.servlet-mapping url-pattern="/classmate"
 */
public class ClassmateServlet extends HttpServlet {

	private UserFinder idChecker;

	public ClassmateServlet() {
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
		if (user != null) {
			List classmates = idChecker.findClassmates(user);
			if (classmates == null || classmates.isEmpty()) {
				sendBack(resp, "");
			} else {
				String s = "";
				Person p = null;
				for (Iterator it = classmates.iterator(); it.hasNext();) {
					p = (Person) it.next();
					s += p.getUserID() + ":" + p.getFullName() + ",";
				}
				sendBack(resp, s.substring(0, s.length() - 1));
			}
		}
	}

	private Person findAuthorizedUser(String userID, String password) {
		if (idChecker == null)
			return null;
		return idChecker.findAuthorizedUser(userID, password);
	}

	private void sendBack(HttpServletResponse resp, String content) {
		resp.setContentType("text/plain");
		PrintWriter out = null;
		try {
			out = resp.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		out.println(content);
		out.close();
	}

}
