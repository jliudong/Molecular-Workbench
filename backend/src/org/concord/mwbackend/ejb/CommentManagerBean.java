package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.CommentLocal;
import org.concord.mwbackend.interfaces.CommentLocalHome;
import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.interfaces.UserLocalHome;

/**
 * @ejb.bean name="CommentManager" display-name="Name for CommentManager" description="Description
 *           for CommentManager" jndi-name="ejb/CommentManager" type="Stateless" view-type="remote"
 */
public class CommentManagerBean implements SessionBean {

	private UserLocalHome userHome;
	private CommentLocalHome commentHome;

	public CommentManagerBean() {
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
		// TODO Auto-generated method stub
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

	private void initCommentHome() {
		if (commentHome == null) {
			try {
				InitialContext ctx = new InitialContext();
				commentHome = (CommentLocalHome) ctx.lookup(CommentLocalHome.JNDI_NAME);
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
	public boolean comment(String url, String title, String body, String userID, String ip)
			throws CreateException {
		initCommentHome();
		if (commentHome == null)
			return false;
		commentHome.create(url, title, body, userID, ip);
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public String getComment(int pk) {
		initCommentHome();
		if (commentHome == null)
			return null;
		CommentLocal c = null;
		try {
			c = commentHome.findByPrimaryKey(pk);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null)
			return null;
		return new String(c.getBody());
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean deleteComment(int pk) {
		initCommentHome();
		if (commentHome == null)
			return false;
		CommentLocal c = null;
		try {
			c = commentHome.findByPrimaryKey(pk);
		} catch (FinderException e) {
			e.printStackTrace();
			return false;
		}
		if (c == null)
			return false;
		try {
			c.remove();
		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		} catch (RemoveException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getComments(String url) {
		initCommentHome();
		if (commentHome == null)
			return null;
		Collection<?> c = null;
		try {
			c = commentHome.findByUrl(url);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getCommentList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getCommentsByUser(String userID) {
		initCommentHome();
		if (commentHome == null)
			return null;
		Collection<?> c = null;
		try {
			c = commentHome.findByUser(userID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getCommentList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getCommentsAfter(long timeMillis) {
		initCommentHome();
		if (commentHome == null)
			return null;
		Collection<?> c = null;
		try {
			c = commentHome.findCommentsAfter(timeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getCommentList(c);
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getCommentsBetween(long startTimeMillis, long endTimeMillis) {
		initCommentHome();
		if (commentHome == null)
			return null;
		Collection<?> c = null;
		try {
			c = commentHome.findBetween(startTimeMillis, endTimeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		return getCommentList(c);
	}

	private List getCommentList(Collection c) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		List<Map> list = new ArrayList<Map>(c.size());
		for (Iterator it = c.iterator(); it.hasNext();) {
			CommentLocal cmt = (CommentLocal) it.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", cmt.getId() + "");
			map.put("url", cmt.getUrl());
			map.put("title", cmt.getTitle());
			map.put("body", new String(cmt.getBody()));
			map.put("ip", cmt.getIp());
			map.put("time", dateFormat.format(new Date(cmt.getTimeMillis())));
			String userID = cmt.getUserID();
			if (userID != null) {
				initUserHome();
				if (userHome != null) {
					UserLocal u = null;
					try {
						u = userHome.findByPrimaryKey(userID);
					} catch (FinderException e) {
						e.printStackTrace();
					}
					if (u != null) {
						map.put("userID", u.getUserID());
						map.put("email", u.getEmail());
						map.put("state", u.getState());
						map.put("country", u.getCountry());
						switch (u.getType()) {
						case Person.STUDENT:
							map.put("type", "Student");
							break;
						case Person.TEACHER:
							map.put("type", "Teacher");
							break;
						case Person.OTHER:
							map.put("type", "Other");
							break;
						}
					}
				}
			}
			list.add(map);
		}
		return list;
	}

}
