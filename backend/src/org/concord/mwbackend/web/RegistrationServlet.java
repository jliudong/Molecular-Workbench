package org.concord.mwbackend.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.Registration;
import org.concord.mwbackend.interfaces.RegistrationHome;

/**
 * Servlet Class
 * 
 * @web.servlet name="Registration" display-name="Molecular Workbench Registration"
 *              description="Registration to Molecular Workbench"
 * @web.servlet-mapping url-pattern="/register"
 * @web.servlet-init-param name="A parameter" value="A value"
 * @web.ejb-ref name="ejb/Registration" type="Session"
 *              home="org.concord.mwbackend.interfaces.RegistrationHome"
 *              remote="org.concord.mwbackend.interfaces.Registration" description="Reference to the
 *              Registration EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/Registration" jndi-name="ejb/Registration"
 * @web.ejb-ref name="ejb/ClassManager" type="Session"
 *              home="org.concord.mwbackend.interfaces.ClassManagerHome"
 *              remote="org.concord.mwbackend.interfaces.ClassManager" description="Reference to the
 *              ClassManager EJB"
 * @jboss.ejb-ref-jndi ref-name="ejb/ClassManager" jndi-name="ejb/ClassManager"
 */
public class RegistrationServlet extends HttpServlet {

	private RegistrationHome home;

	public RegistrationServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(RegistrationHome.COMP_NAME);
			home = (RegistrationHome) PortableRemoteObject.narrow(ref, RegistrationHome.class);
		} catch (NamingException e) {
			throw new ServletException("Lookup failure: ");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html");

		String action = req.getParameter("action");

		if ("register".equals(action)) {

			PrintWriter out = resp.getWriter();
			out.println("<html><head><title>");
			out.println(""+Resource.get("RegistrationServlet_jsp_1")+"");
			out.println("</title></head>");
			out.println("<body face=\"Verdana\">");

			String userID = getParameter(req, "userid");
			String password = getParameter(req, "password");
			String email = getParameter(req, "email");
			String firstName = getParameter(req, "firstname");
			String lastName = getParameter(req, "lastname");
			String institution = getParameter(req, "institution");
			String state = getParameter(req, "state");
			String country = getParameter(req, "country");
			String type = getParameter(req, "type");
			String teacher = getParameter(req, "teacher");
			String klass = getParameter(req, "classid");
			String notifyModel = getParameter(req, "notifymodel");
			String notifyReport = getParameter(req, "notifyreport");
			String acceptNewsletter = getParameter(req, "acceptnewsletter");
			String returnAddress = getParameter(req, "returnaddress");
			String onreport = getParameter(req, "onreport");
			Person user = new Person(userID, email, firstName, lastName, institution, state, country);
			user.setNotifyModel("yes".equals(notifyModel));
			user.setNotifyReport("yes".equals(notifyReport));
			user.setAcceptNewsletter("yes".equals(acceptNewsletter));
			user.setPassword(password);
			if (teacher != null)
				user.setTeacher(teacher);
			if (klass != null)
				user.setKlass(klass);
			if (type != null)
				user.setType(Integer.valueOf(type).intValue());
			try {
				Registration bean = home.create();
				boolean b = bean.register(user);
				bean.remove();
				if (b) {
					String s = userID + ";Password=" + password + ";Email=" + email + ";FirstName=" + firstName
							+ ";LastName=" + lastName + ";Institution=" + institution + ";State=" + state + ";Country="
							+ country + ";Teacher=" + teacher + ";ClassID=" + klass;
					Cookie cookie = new Cookie("User", URLEncoder.encode(s, "UTF-8"));
					cookie.setMaxAge(30 * 24 * 3600); // expire after 30 days
					cookie.setDomain("concord.org");
					resp.addCookie(cookie);
					out.println("<h1>"+Resource.get("RegistrationServlet_jsp_1")+"</h1>");
					out.println("<p>");
					out.print(""+Resource.get("RegistrationServlet_jsp_2")+" ");
					out.print(firstName != null && firstName.length() > 0 ? firstName : userID);
					out.print(" : ");
					out.println("<br>");
					out.println(""+Resource.get("RegistrationServlet_jsp_3")+" [" + email
							+ "] ");
					out.println("</p>");
					out.println("<p>");
					if ("yes".equalsIgnoreCase(onreport)) {
						out.println(""+Resource.get("RegistrationServlet_jsp_4")+"");
					} else {
						if (returnAddress == null) {
							out
									.println(""+Resource.get("RegistrationServlet_jsp_5")+" <a href=\"home.jsp?client=mw\">"+Resource.get("RegistrationServlet_jsp_16")+"</a> "+Resource.get("RegistrationServlet_jsp_6")+"");
						} else {
							out.println("<a href=\"" + returnAddress + "\">"+Resource.get("RegistrationServlet_jsp_7")+"</a>");
						}
					}
					out.println("</p>");
				} else {
					out.println("<h1>"+Resource.get("RegistrationServlet_jsp_8")+"</h1>");
					out.print("<p><font color=red><b>");
					out.print(userID + " "+Resource.get("RegistrationServlet_jsp_9")+"</b></font> "+Resource.get("RegistrationServlet_jsp_10")+"");
					out.println("</p>");
					out.println("<p>");
					out.println("<a href=\"register.jsp?client=mw\">"+Resource.get("RegistrationServlet_jsp_11")+"</a>");
					out.println("</p>");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				e.printStackTrace(out);
			} finally {
				out.println("</body></html>");
				out.close();
			}

		} else if ("forgot".equals(action)) {

			PrintWriter out = resp.getWriter();
			out.println("<html><head><title>"+Resource.get("RegistrationServlet_jsp_12")+"</title></head>");
			out.println("<body>");
			out.println("<h1>"+Resource.get("RegistrationServlet_jsp_13")+"</h1>");

			try {
				boolean success = false;
				Registration bean = home.create();
				String email = req.getParameter("email");
				if (email != null && email.trim().length() > 0)
					success = bean.emailPassword(email);
				bean.remove();

				if (success) {
					out.println("<p>");
					out.print(""+Resource.get("RegistrationServlet_jsp_14")+" " + email);
					out.println("</p>");
				} else {
					out.println("<p>");
					out.println(""+Resource.get("RegistrationServlet_jsp_15")+" [" + email + "]");
					out.println("</p>");
				}

			} catch (Exception e) {
				out.println(e.getMessage());
				e.printStackTrace(out);
			} finally {
				out.println("</body></html>");
				out.close();
			}
		}

	}

	private String getParameter(HttpServletRequest r, String s) {
		String t = r.getParameter(s);
		if (t != null)
			try {
				t = URLDecoder.decode(t, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		return t;

	}

}
