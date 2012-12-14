package org.concord.mwbackend.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.util.Utilities;

/**
 * Servlet Class
 * 
 * @web.servlet name="Contact" display-name="Name for Contact" description="Description for Contact"
 * @web.servlet-mapping url-pattern="/contact"
 */
public class ContactServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String name = request.getParameter("name");
		if (name != null) {
			name = URLDecoder.decode(name, "UTF-8");
		}
		String email = request.getParameter("email");
		if (email != null)
			email = URLDecoder.decode(email, "UTF-8");
		String subject = request.getParameter("subject");
		if (subject != null)
			subject = URLDecoder.decode(subject, "UTF-8");
		String content = request.getParameter("content");
		if (content != null)
			content = URLDecoder.decode(content, "UTF-8");
		String[] addresses = new String[] { "qxie@concord.org" };
		Utilities.email("Molecular Workbench: " + subject, content + "\n\nSent by: " + name,
				addresses, "text/plain", email);
		PrintWriter out = response.getWriter();
		out.println("<html><body face=\"Verdana\">");
		out.println("<h2>"+Resource.get("ContactServlet_java_Receipt")+"</h2>");
		out
				.println("<p>"+Resource.get("mymodelspace_jsp_Hi")
						+ name
						+ ":</p><p>"+Resource.get("ContactServlet_java_Over")+"</p>");
		out.println("<br><br><p>The MW Team, Concord Consortium</p>");
		out.println("</body></html>");
	}
}
