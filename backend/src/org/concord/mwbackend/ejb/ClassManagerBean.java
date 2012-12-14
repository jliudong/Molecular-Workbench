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
import org.concord.mwbackend.interfaces.ClazzLocal;
import org.concord.mwbackend.interfaces.ClazzLocalHome;

/**
 * @ejb.bean name="ClassManager" display-name="Name for ClassManager" description="Description for
 *           ClassManager" jndi-name="ejb/ClassManager" type="Stateless" view-type="remote"
 */
public class ClassManagerBean implements SessionBean {

	private ClazzLocalHome classHome;

	public ClassManagerBean() {
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

	private void initClassHome() {
		if (classHome == null) {
			try {
				InitialContext ctx = new InitialContext();
				classHome = (ClazzLocalHome) ctx.lookup(ClazzLocalHome.JNDI_NAME);
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
	public boolean create(String classID, Person teacher) throws CreateException {
		initClassHome();
		if (classHome == null)
			return false;
		classHome.create(classID, teacher.getUserID(), false);
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public Collection findClasses(String teacherID, boolean archived) throws CreateException {
		initClassHome();
		if (classHome == null)
			return null;
		try {
			return classHome.findClasses(teacherID, archived);
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
	public boolean doesClassExist(String classID) throws CreateException {
		initClassHome();
		if (classHome == null)
			return false;
		try {
			return classHome.findByPrimaryKey(classID) != null;
		} catch (FinderException e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean renameClass(String oldID, String newID) throws CreateException {
		initClassHome();
		if (classHome == null)
			return false;
		ClazzLocal c = null;
		try {
			c = classHome.findByPrimaryKey(oldID);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (c == null)
			return false;
		String tid = c.getTeacherID();
		try {
			c.remove();
		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		} catch (RemoveException e) {
			e.printStackTrace();
			return false;
		}
		classHome.create(newID, tid, false);
		return true;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean removeClass(String id) throws CreateException {
		initClassHome();
		if (classHome == null)
			return false;
		ClazzLocal c = null;
		try {
			c = classHome.findByPrimaryKey(id);
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

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean archiveClass(String id, boolean b) throws CreateException {
		initClassHome();
		if (classHome == null)
			return false;
		ClazzLocal c = null;
		try {
			c = classHome.findByPrimaryKey(id);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (c == null)
			return false;
		c.setArchived(b);
		return true;
	}

}
