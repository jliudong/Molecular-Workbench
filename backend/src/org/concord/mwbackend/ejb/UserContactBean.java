package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.interfaces.UserLocalHome;

/**
 * @ejb.bean name="UserContact" display-name="Name for UserContact" description="Description for
 *           UserContact" jndi-name="ejb/UserContact" type="Stateless" view-type="remote"
 */
public class UserContactBean implements SessionBean {

	private UserLocalHome userHome;

	public UserContactBean() {
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
	public boolean authenticate(String userID, String password) {
		initUserHome();
		if (userHome != null) {
			UserLocal u = null;
			try {
				u = userHome.findByPrimaryKey(userID);
			} catch (FinderException e) {
				return false;
			}
			if (u != null) {
				return password.equals(u.getPassword());
			}
		}
		return false;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public String getEmail(String userID) {
		initUserHome();
		if (userHome != null) {
			UserLocal u = null;
			try {
				u = userHome.findByPrimaryKey(userID);
			} catch (FinderException e) {
				return null;
			}
			if (u != null) {
				return u.getEmail();
			}
		}
		return null;
	}

}
