package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ActivityLocal;
import org.concord.mwbackend.interfaces.ActivityLocalHome;

/**
 * @ejb.bean name="ActivityManager" display-name="Name for ActivityManager" description="Description
 *           for ActivityManager" jndi-name="ejb/ActivityManager" type="Stateless"
 *           view-type="remote"
 */
public class ActivityManagerBean implements SessionBean {

	private ActivityLocalHome activityHome;

	public ActivityManagerBean() {
	}

	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbPassivate() throws EJBException, RemoteException {
	}

	public void ejbRemove() throws EJBException, RemoteException {
	}

	public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
	}

	/**
	 * Default create method
	 * 
	 * @throws CreateException
	 * @ejb.create-method
	 */
	public void ejbCreate() throws CreateException {
	}

	private void initActivityHome() {
		if (activityHome == null) {
			try {
				InitialContext ctx = new InitialContext();
				activityHome = (ActivityLocalHome) ctx.lookup(ActivityLocalHome.JNDI_NAME);
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
	public boolean create(String url, String title, String instruction, Person teacher, String classID)
			throws CreateException {
		initActivityHome();
		if (activityHome == null)
			return false;
		activityHome.create(url, title, instruction, teacher.getUserID(), classID);
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public Collection findActivities(String teacherID) throws CreateException {
		initActivityHome();
		if (activityHome == null)
			return null;
		try {
			return activityHome.findByUser(teacherID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public Collection findActivities(String teacherID, String classID) throws CreateException {
		initActivityHome();
		if (activityHome == null)
			return null;
		try {
			return activityHome.findByUserAndClass(teacherID, classID);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean removeActivity(int id) throws CreateException {
		initActivityHome();
		if (activityHome == null)
			return false;
		ActivityLocal c = null;
		try {
			c = activityHome.findByPrimaryKey(id);
		} catch (FinderException e) {
			e.printStackTrace();
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

}
