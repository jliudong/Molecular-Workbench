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
import org.concord.mwbackend.interfaces.ModelLocal;
import org.concord.mwbackend.interfaces.ModelLocalHome;
import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.util.MwConstants;
import org.concord.mwbackend.util.Utilities;

/**
 * @ejb.bean name="ModelManager" display-name="Name for ModelManager" description="Description for
 *           ModelManager" jndi-name="ejb/ModelManager" type="Stateless" view-type="remote"
 */
public class ModelManagerBean extends ItemManagerBean {

	public ModelManagerBean() {
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

	private ModelLocalHome getModelHome() throws NamingException {
		InitialContext ctx = new InitialContext();
		Object obj = ctx.lookup(ModelLocalHome.JNDI_NAME);
		return (ModelLocalHome) obj;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean receive(String zipFile, int zipSize, final String url, final String title,
			String userID, final String privacy) throws CreateException {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		if (modelHome == null)
			return false;
		final Person p = findPerson(userID);
		if (p != null) {
			modelHome.create(zipFile, zipSize, url, title, userID, p.getTeacher(), p.getKlass(),
					privacy);
			Thread t = new Thread(new Runnable() {
				public void run() {
					emailBack(p, url, title);
					emailNotification(p, url, title, privacy);
				}
			});
			t.setName("Send email after receiving a model");
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
		return true;
	}

	private void emailBack(Person p, String url, String title) {
		if (p.getEmailAddress() == null || p.getEmailAddress().indexOf("@") == -1)
			return;
		String subject = "Your submission to Molecular Workbench: " + title;
		String content = null;
		try {
			content = "<html><p>Hi "
					+ p.getFirstName()
					+ ":</p><p>Your submission entitled with \"<b>"
					+ title
					+ "</b>\" is now available online. You can click "
					+ "<a href=\""
					+ MwConstants.HOST
					+ "tmp.jnlp?type=model&address="
					+ URLEncoder.encode(url, "UTF-8")
					+ "\">"
					+ "here</a> to view it"
					+ ".<p>Thank you for using the Molecular Workbench software.</p><p>Sincerely yours,</p><p>The MW Team, Concord Consortium<br>Visit us at: <a href=\"http://mw.concord.org/modeler/\">http://mw.concord.org</a></p></html>";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Utilities.email(subject, content, new String[] { p.getEmailAddress() }, "text/html");
	}

	private void emailNotification(Person p, String url, String title, String privacy) {
		if ("private".equals(privacy))
			return;
		List<Person> list = getPersonsToNotifyByNewModel(privacy, p);
		// System.out.println(p + "-->" + list);
		if (list == null)
			return;
		String subject = "New submission to Molecular Workbench: " + title;
		String content = null;
		try {
			content = "<html><p>"
					+ p.getUserID()
					+ " just submitted a new item entitled with \"<b>"
					+ title
					+ "</b>\". You can click "
					+ "<a href=\""
					+ MwConstants.HOST
					+ "tmp.jnlp?type=model&address="
					+ URLEncoder.encode(url, "UTF-8")
					+ "\">"
					+ "here</a> to view it"
					+ ".<p>You received this email because you chose in your MW profile that you would like to be notified when someone submits a new model or activity. If you do not wish to receive this notification any more, please <a href=http://mw2.concord.org/tmp.jnlp?address=http://mw2.concord.org/profile.jsp?client=mw>change your profile</a>.</p><p>Sincerely yours,</p><p>The MW Team, Concord Consortium<br>Visit us at: <a href=\"http://mw.concord.org/modeler/\">http://mw.concord.org</a></p></html>";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (Person x : list) {
			if (x.getUserID().equals(p.getUserID()) || x.getEmailAddress() == null
					|| x.getEmailAddress().indexOf("@") == -1)
				continue;
			// if we send the whole array, then everyone will see other's
			// email addresses, so we have to send one by one
			Utilities.email(subject, content, new String[] { x.getEmailAddress() }, "text/html");
		}
	}

	private List<Person> getPersonsToNotifyByNewModel(String privacy, Person p) {
		initUserHome();
		if (userHome != null) {
			Collection c = null;
			try {
				c = userHome.findPersonsToNotifyForNewModel(Boolean.TRUE);
			} catch (FinderException e) {
				e.printStackTrace();
			}
			if (c != null) {
				List<Person> list = new ArrayList<Person>();
				for (Iterator it = c.iterator(); it.hasNext();) {
					UserLocal u = (UserLocal) it.next();
					if (u.getUserID().equals(p.getUserID()))
						continue;
					if ("teacher".equals(privacy)) {
						if (!u.getUserID().equals(p.getTeacher()))
							continue;
					} else if ("class".equals(privacy)) {
						switch (u.getType().intValue()) {
						case Person.STUDENT:
							if (u.getKlass() == null
									|| (u.getKlass() != null && !u.getKlass().equals(p.getKlass())))
								continue;
							break;
						case Person.TEACHER:
							if (!u.getUserID().equals(p.getTeacher()))
								continue;
							break;
						case Person.OTHER:
							continue;
						}
					}
					list.add(new Person(u));
				}
				return list;
			}
		}
		return null;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public ModelLocal getByUrl(String url) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		ModelLocal m = null;
		try {
			m = modelHome.findByUrl(url);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		return m;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getPublishedItemsBy(String userID) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findPublishedByAuthor(userID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getNonPrivateItemsBy(String userID) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findNonPrivateByAuthor(userID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getAllItemsBy(String userID, boolean trash) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findAllByAuthor(userID, trash);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getPublishedItemsBetween(long startTimeMillis, long endTimeMillis) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findPublishedBetween(startTimeMillis, endTimeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getAllItemsBetween(long startTimeMillis, long endTimeMillis) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findAllBetween(startTimeMillis, endTimeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public int getLatestPK() {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return 0;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findLatest();
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
	public List getItemsIdBetween(int startId, int endId) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findIdBetween(startId, endId);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getClassModels(String klass, boolean forTeacher) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = forTeacher ? modelHome.findByClassForTeacher(klass) : modelHome.findByClass(klass);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getStudentModels(String teacher) {
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = modelHome.findByTeacher(teacher);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getModelList(c);
	}

	private List getModelList(Collection c) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		List<Map> list = new ArrayList<Map>(c.size());
		for (Iterator it = c.iterator(); it.hasNext();) {
			ModelLocal m = (ModelLocal) it.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", m.getId() + "");
			map.put("privacy", m.getPrivacy());
			map.put("author", m.getUserID());
			map.put("url", m.getUrl());
			map.put("title", m.getTitle());
			map.put("zipfile", m.getZipFile());
			map.put("zipsize", m.getZipSize() + "");
			map.put("teacher", m.getTeacher());
			map.put("klass", m.getKlass());
			map.put("time", dateFormat.format(new Date(m.getTimeMillis())));
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
		ModelLocalHome modelHome = null;
		try {
			modelHome = getModelHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		ModelLocal m = null;
		try {
			m = modelHome.findByPrimaryKey(pk);
		} catch (FinderException e) {
			e.printStackTrace();
			return false;
		}
		if (m != null) {
			// if(b) m.remove();
			// do not remove it from the database immediately.
			// If marked deleted, it will be in the trash can.
			m.setDeleted(b);
			return true;
		}
		return false;
	}

}
