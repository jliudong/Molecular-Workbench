package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.interfaces.UserLocalHome;
import org.concord.mwbackend.util.Utilities;

/**
 * @ejb.bean name="Registration" display-name="Molecular Workbench Registration"
 *           description="Registration to the Molecular Workbench" jndi-name="ejb/Registration"
 *           type="Stateless" view-type="remote"
 */
public class RegistrationBean implements SessionBean {

	private final static String SIGNATURE = "<p>This is an automated message. Please do not reply.</p><p><br><br>Sincerely yours,<br><br>The MW Team, Concord Consortium</p><p>Visit us at: <a href=\"http://mw.concord.org/modeler/\">http://mw.concord.org</a></p>";
	private UserLocalHome userHome;

	public RegistrationBean() {
		// TODO Auto-generated constructor stub
	}

	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Default create method
	 * 
	 * @throws CreateException
	 * @ejb.create-method
	 */
	public void ejbCreate() throws CreateException {
		initUserHome();
	}

	private void initUserHome() {
		if (userHome == null) {
			try {
				InitialContext ctx = new InitialContext();
				userHome = (UserLocalHome) ctx.lookup(UserLocalHome.JNDI_NAME);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean register(Person user) {
		if (user == null)
			return false;
		if (user.getUserID().trim().equals("") || user.getPassword().trim().equals("")
				|| user.getEmailAddress().trim().equals(""))
			return false;
		try {
			openAccount(user);
		} catch (CreateException e) {
			e.printStackTrace();
			return false;
		}
		Utilities.email("Welcome, " + getPreferredName(user), "<html><body><p>Dear "
				+ getPreferredName(user)
				+ ":</p><p>Thank you for registering with the Molecular Workbench.</p>"
				+ "<p><ul><li>Your user ID: " + user.getUserID() + "</li><li>Your password: "
				+ user.getPassword() + "</li></ul></p>" + SIGNATURE + "</body></html>",
				new String[] { user.getEmailAddress() }, "text/html");
		return true;
	}

	private void openAccount(Person user) throws CreateException {
		if (userHome != null)
			userHome.create(user);
	}

	private String getPreferredName(Person user) {
		String s = user.getFirstName();
		return s != null && s.length() > 0 ? s : user.getUserID();
	}

	private String getPreferredName(UserLocal user) {
		String s = user.getFirstName();
		return s != null && s.length() > 0 ? s : user.getUserID();
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean emailPassword(String email) {
		UserLocal[] users = findUser(email);
		if (users == null || users.length == 0)
			return false;
		for (UserLocal u : users) {
			Utilities.email("Your password for Molecular Workbench", "<html><body><p>Dear "
					+ getPreferredName(u)
					+ ":</p><p>Below is the information you requested:</p><p><ul><li>User ID: "
					+ u.getUserID() + "</li><li>Password:" + u.getPassword() + "</li><ul></p>"
					+ SIGNATURE + "</body></html>", new String[] { email }, "text/html");
		}
		return true;
	}

	private UserLocal[] findUser(String email) {
		if (userHome != null) {
			Collection<?> users = null;
			try {
				users = userHome.findByEmail(email);
			} catch (FinderException e) {
				e.printStackTrace();
				return null;
			}
			UserLocal[] u = new UserLocal[users.size()];
			return (UserLocal[]) users.toArray(u);
		}
		return null;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public void updateProfile(Person user) {
		if (userHome != null) {
			UserLocal u = null;
			try {
				u = userHome.findByPrimaryKey(user.getUserID());
			} catch (FinderException e) {
				e.printStackTrace();
			}
			if (u != null) {
				u.setPassword(user.getPassword());
				u.setFirstName(user.getFirstName());
				u.setLastName(user.getLastName());
				u.setEmail(user.getEmailAddress());
				u.setInstitution(user.getInstitution());
				u.setState(user.getState());
				u.setCountry(user.getCountry());
				u.setType(user.getType());
				u.setTeacher(user.getTeacher());
				u.setKlass(user.getKlass());
				u.setNotifyModel(user.getNotifyModel());
				u.setNotifyReport(user.getNotifyReport());
				u.setAcceptNewsletter(user.getAcceptNewsletter());
			}
		}
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<Person> findTeachers() {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findByType(Person.TEACHER);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<Person> list = new ArrayList<Person>();
		if (c != null) {
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				list.add(new Person(u));
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<String> findTeacherEmail() {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findByType(Person.TEACHER);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<String> list = new ArrayList<String>();
		if (c != null) {
			String email = null;
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				email = u.getEmail();
				if (list.contains(email))
					continue;
				if (email != null && email.indexOf("@") != -1)
					list.add(email);
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<Person> findStudents(String teacher) {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findStudents(teacher);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<Person> list = new ArrayList<Person>();
		if (c != null) {
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				list.add(new Person(u));
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<Person> findStudentsOfClass(String klass, String teacher) {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findStudentsOfClass(klass, teacher);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<Person> list = new ArrayList<Person>();
		if (c != null) {
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				list.add(new Person(u));
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean renameClass(String oldCid, String newCid, String teacher) {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findStudentsOfClass(oldCid, teacher);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		if (c == null)
			return false;
		for (Iterator it = c.iterator(); it.hasNext();) {
			UserLocal u = (UserLocal) it.next();
			u.setKlass(newCid);
		}
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<String> findAllEmail() {
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findAll();
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<String> list = new ArrayList<String>();
		if (c != null) {
			String email = null;
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				email = u.getEmail();
				if (list.contains(email))
					continue;
				if (email != null && email.indexOf("@") != -1)
					list.add(email);
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public Person findUser(String userID, String password) {
		if (userID == null || password == null || userHome == null)
			return null;
		UserLocal u = null;
		try {
			u = userHome.findByPrimaryKey(userID);
		} catch (FinderException e) {
			return null;
		}
		if (u != null) {
			String pwd = u.getPassword();
			if (pwd != null && pwd.equals(password))
				return new Person(u);
		}
		return null;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public String findTeacher(String userID) {
		if (userID == null || userHome == null)
			return null;
		UserLocal u = null;
		try {
			u = userHome.findByPrimaryKey(userID);
		} catch (FinderException e) {
			return null;
		}
		if (u != null) {
			return u.getTeacher();
		}
		return null;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public int findType(String userID) {
		if (userID == null || userHome == null)
			return -1;
		UserLocal u = null;
		try {
			u = userHome.findByPrimaryKey(userID);
		} catch (FinderException e) {
			return -1;
		}
		if (u != null && u.getType() != null)
			return u.getType().intValue();
		return -1;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<Person> findUserByName(int type, String firstName, String lastName) {
		if ((firstName == null && lastName == null) || userHome == null)
			return null;
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findByName(type, firstName, lastName);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<Person> list = new ArrayList<Person>();
		if (c != null) {
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				list.add(new Person(u));
			}
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List<Person> findUserByLastName(int type, String lastName) {
		if (lastName == null || userHome == null)
			return null;
		Collection c = null;
		if (userHome != null) {
			try {
				c = userHome.findByLastName(type, lastName);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		List<Person> list = new ArrayList<Person>();
		if (c != null) {
			for (Iterator it = c.iterator(); it.hasNext();) {
				UserLocal u = (UserLocal) it.next();
				list.add(new Person(u));
			}
		}
		return list;
	}

}
