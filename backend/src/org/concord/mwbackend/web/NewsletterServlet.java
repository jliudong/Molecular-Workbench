package org.concord.mwbackend.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.util.Utilities;

/**
 * Servlet Class
 * 
 * @web.servlet name="Newsletter" display-name="Name for Newsletter" description="Description for
 *              Newsletter"
 * @web.servlet-mapping url-pattern="/newsletter"
 */
public class NewsletterServlet extends HttpServlet {

	private UserFinder finder;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		final String group = request.getParameter("group");
		String subject = request.getParameter("subject");
		if (subject != null)
			subject = URLDecoder.decode(subject, "UTF-8");
		String content = request.getParameter("content");
		if (content != null)
			content = URLDecoder.decode(content, "UTF-8");
		final String subject2 = subject;
		final String content2 = content;
		Thread t = new Thread() {
			public void run() {
				sendNewsletter(group, subject2, content2);
			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		PrintWriter out = response.getWriter();
		out.println("<html><body face=\"Verdana\">");
		out.println("<h2>Receipt</h2>");
		out.println("<p>The newsletter is being sent by the server.");
		out.println("You can leave this page and continue to work.</p>");
		out.println("<p><b>Please do not reload this page.</b>");
		out.println("Reloading this page will result in duplicate email.</p>");
		out.println("</body></html>");
	}

	private void sendNewsletter(String group, String subject, String content) {
		if (finder == null)
			finder = new UserFinder();
		List list = null;
		if ("All".equals(group)) {
			list = finder.findAllEmail();
		} else if ("Teachers".equals(group)) {
			list = finder.findTeacherEmail();
		}
		if (list == null)
			return;
		int n = list.size();
		String addr = null;
		for (int i = 0; i < n; i++) {
			addr = (String) list.get(i);
			// if we send the whole array, then everyone will see other's
			// email addresses
			Utilities.email("Molecular Workbench Newsletter: " + subject, content,
					new String[] { addr }, "text/plain");
		}
	}
}
