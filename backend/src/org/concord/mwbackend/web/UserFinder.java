package org.concord.mwbackend.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.Registration;
import org.concord.mwbackend.interfaces.RegistrationHome;

public class UserFinder {

	private String userID, password;
	private RegistrationHome home;

	public UserFinder() {
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(RegistrationHome.COMP_NAME);
			home = (RegistrationHome) PortableRemoteObject.narrow(ref, RegistrationHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static void setUserCookie(Person u, HttpServletResponse resp, boolean b)
			throws IOException {
		if (u != null) {
			String s = u.getUserID() + ";Password=" + u.getPassword() + ";Email="
					+ u.getEmailAddress() + ";FirstName=" + u.getFirstName() + ";LastName="
					+ u.getLastName() + ";Klass=" + u.getKlass() + ";Institution="
					+ u.getInstitution() + ";State=" + u.getState() + ";Country=" + u.getCountry()
					+ ";Teacher=" + u.getTeacher();
			Cookie cookie = new Cookie("User", URLEncoder.encode(s, "UTF-8"));
			cookie.setMaxAge(60); // if set to 0, delete the cookie
			cookie.setDomain("concord.org");
			resp.addCookie(cookie);
			if (b)
				resp.setHeader("action", "login");
		}
	}

	public static void removeUserCookie(HttpServletResponse resp, boolean b) throws IOException {
		Cookie cookie = new Cookie("User", "");
		resp.addCookie(cookie);
		if (b)
			resp.setHeader("action", "logout");
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Person findAuthorizedUser(String usr, String pwd) {
		try {
			Registration bean = home.create();
			Person p = bean.findUser(usr, pwd);
			bean.remove();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List findAllEmail() {
		try {
			Registration bean = home.create();
			List list = bean.findAllEmail();
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List findTeachers() {
		try {
			Registration bean = home.create();
			List list = bean.findTeachers();
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					if (o1 instanceof Person && o2 instanceof Person) {
						Person p1 = (Person) o1;
						Person p2 = (Person) o2;
						return p1.getUserID().toLowerCase().compareTo(p2.getUserID().toLowerCase());
					}
					return o1.toString().compareTo(o2.toString());
				}
			});
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List findTeacherEmail() {
		try {
			Registration bean = home.create();
			List list = bean.findTeacherEmail();
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String findTeacher(String student) {
		try {
			Registration bean = home.create();
			String s = bean.findTeacher(student);
			bean.remove();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int findType(String userID) {
		try {
			Registration bean = home.create();
			int type = bean.findType(userID);
			bean.remove();
			return type;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public String findUserIDByName(int type, String firstName, String lastName) {
		try {
			Registration bean = home.create();
			List c = bean.findUserByName(type, firstName, lastName);
			bean.remove();
			if (c == null || c.isEmpty())
				return null;
			return ((Person) c.get(0)).getUserID();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String findUserIDByLastName(int type, String lastName) {
		try {
			Registration bean = home.create();
			List c = bean.findUserByLastName(type, lastName);
			bean.remove();
			if (c == null || c.isEmpty())
				return null;
			return ((Person) c.get(0)).getUserID();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List findStudents(String teacher) {
		try {
			Registration bean = home.create();
			List list = bean.findStudents(teacher);
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List findStudentsOfClass(String klass, String teacher) {
		try {
			Registration bean = home.create();
			List list = bean.findStudentsOfClass(klass, teacher);
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean renameClass(String oldCid, String newCid, String teacher) {
		try {
			Registration bean = home.create();
			boolean b = bean.renameClass(oldCid, newCid, teacher);
			bean.remove();
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List findClassmates(Person person) {
		try {
			Registration bean = home.create();
			List list = bean.findStudentsOfClass(person.getKlass(), person.getTeacher());
			if (list != null && !list.isEmpty()) {
				Person p = null;
				for (Iterator it = list.iterator(); it.hasNext();) {
					p = (Person) it.next();
					if (p.equals(person))
						it.remove();
				}
			}
			bean.remove();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Person findAdmin(String userID, String password) {
		if (userID == null || password == null)
			return null;
		if (!"admin".equalsIgnoreCase(userID))
			return null;
		return findAuthorizedUser(userID, password);
	}

	public void setUserCookie(HttpServletResponse resp) throws IOException {
		Person u = findAuthorizedUser(userID, password);
		setUserCookie(u, resp, true);
	}

	public void removeUserCookie(HttpServletResponse resp) throws IOException {
		removeUserCookie(resp, true);
	}

	public void updateProfile(Person user) {
		try {
			Registration bean = home.create();
			bean.updateProfile(user);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
