package org.concord.mwbackend.ejb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ReportLocal;
import org.concord.mwbackend.interfaces.ReportLocalHome;
import org.concord.mwbackend.util.MwConstants;
import org.concord.mwbackend.util.Utilities;

/**
 * @ejb.bean name="ReportManager" display-name="Name for ReportManager" description="Description for
 *           ReportManager" jndi-name="ejb/ReportManager" type="Stateless" view-type="remote"
 */
public class ReportManagerBean extends ItemManagerBean {

	public ReportManagerBean() {
		// TODO Auto-generated constructor stub
	}

	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
		// TODO Auto-generated method stub
	}

	/**
	 * Default create method
	 * 
	 * @throws CreateException
	 * @ejb.create-method
	 */
	public void ejbCreate() throws CreateException {
		// TODO Auto-generated method stub
	}

	private ReportLocalHome getReportHome() throws NamingException {
		InitialContext ctx = new InitialContext();
		Object obj = ctx.lookup(ReportLocalHome.JNDI_NAME);
		return (ReportLocalHome) obj;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean receive(final String url, final String title, String userID)
			throws CreateException {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		if (reportHome == null)
			return false;
		final Person p = findPerson(userID);
		if (p != null) {
			reportHome.create(url, title, userID, p.getTeacher());
			Thread t = new Thread(new Runnable() {
				public void run() {
					emailBack(p, url, title);
					notifyTeacher(p, url, title);
				}
			});
			t.setName("Send email after receiving a report");
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
		return true;
	}

	private void emailBack(Person p, String url, String title) {
		if (p.getEmailAddress() == null || p.getEmailAddress().indexOf("@") == -1)
			return;
		String subject = "Re: " + title;
		String content = null;
		try {
			content = "<html><p>Hi "
					+ p.getFirstName()
					+ ":</p><p>Your report entitled with : <b>"
					+ title
					+ "</b> is now available online. You can click "
					+ "<a href=\""
					+ MwConstants.HOST
					+ "tmp.jnlp?type=report&address="
					+ URLEncoder.encode(url, "UTF-8")
					+ "\">"
					+ "here</a> to view it"
					+ ".<p>Thank you for using the Molecular Workbench software.</p><p>Sincerely yours,</p><p>The MW Team, Concord Consortium<br>Visit us at: <a href=\"http://mw.concord.org/modeler/\">http://mw.concord.org</a></p></html>";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Utilities.email(subject, content, new String[] { p.getEmailAddress() }, "text/html");
	}

	private void notifyTeacher(Person p, String url, String title) {
		String teacher = p.getTeacher();
		if (teacher == null)
			return;
		Person t = findPerson(teacher);
		if (t == null || t.getEmailAddress() == null || t.getEmailAddress().indexOf("@") == -1)
			return;
		if (!t.getNotifyReport())
			return;
		String subject = "New report by your student: " + title;
		String content = null;
		try {
			content = "<html><p>Hi "
					+ t.getFirstName()
					+ ":</p><p>Your student "
					+ p.getFullName()
					+ " (ID: "
					+ p.getUserID()
					+ ") just submitted a report entitled with : <b>"
					+ title
					+ "</b>. You can click "
					+ "<a href=\""
					+ MwConstants.HOST
					+ "tmp.jnlp?type=report&address="
					+ URLEncoder.encode(url, "UTF-8")
					+ "\">"
					+ "here</a> to view it directly, or click <a href=\""
					+ MwConstants.HOST
					+ "tmp.jnlp?address=home.jsp?client=mw"
					+ "\">"
					+ "here</a> to log into your MW account to view it with all other reports.<p>Thank you for using the Molecular Workbench software.</p><p>Sincerely yours,</p><p>The MW Team, Concord Consortium<br>Visit us at: <a href=\"http://mw.concord.org/modeler/\">http://mw.concord.org</a></p></html>";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Utilities.email(subject, content, new String[] { t.getEmailAddress() }, "text/html");
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getItemsBy(String userID) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findByAuthor(userID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getItemsBy(String userID, boolean trash) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findByAuthor(userID, trash);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getItemsBetween(long startTimeMillis, long endTimeMillis) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findBetween(startTimeMillis, endTimeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public int getLatestPK() {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return 0;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findLatest();
		} catch (FinderException e) {
			e.printStackTrace();
			return 0;
		}
		if (c == null || c.isEmpty())
			return 0;
		Object[] o = c.toArray();
		return ((Double) o[0]).intValue();
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getItemWithId(int id) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findId(id);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getItemsIdBetween(int startId, int endId) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findIdBetween(startId, endId);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getStudentReports(String teacher) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = reportHome.findByTeacher(teacher, false);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List search(String keyword, String teacher, String student) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			if (teacher == null && student == null && keyword != null) {
				c = reportHome.findByKeyword(keyword);
			} else if (student == null && teacher != null && keyword != null) {
				c = reportHome.findByKeywordAndTeacher(keyword, teacher);
			} else if (student != null && keyword != null) {
				c = reportHome.findByKeywordAndStudent(keyword, student);
			}
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getReportList(c);
	}

	private List getReportList(Collection c) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		List<Map> list = new ArrayList<Map>(c.size());
		for (Iterator it = c.iterator(); it.hasNext();) {
			ReportLocal r = (ReportLocal) it.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", r.getId() + "");
			map.put("url", r.getUrl());
			map.put("title", r.getTitle());
			map.put("teacher", r.getTeacher());
			if (r.getTeacher() != null && !r.getTeacher().equals("None")) {
				Person p = findPerson(r.getTeacher());
				if (p != null) {
					map.put("teacher_fullname", p.getPreferredName());
				}
			}
			if (r.getUserID() != null) {
				map.put("author", r.getUserID());
				Person p = findPerson(r.getUserID());
				if (p != null) {
					map.put("fullname", p.getPreferredName());
					map.put("school", p.getInstitution());
				}
			}
			map.put("time", dateFormat.format(new Date(r.getTimeMillis())));
			list.add(map);
		}
		return list;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean setDeleted(int pk, boolean b) {
		ReportLocalHome reportHome = null;
		try {
			reportHome = getReportHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		ReportLocal report = null;
		try {
			report = reportHome.findByPrimaryKey(pk);
		} catch (FinderException e) {
			e.printStackTrace();
			return false;
		}
		if (report != null) {
			report.setDeleted(b);
			return true;
		}
		return false;
	}

}
