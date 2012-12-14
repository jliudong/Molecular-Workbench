package org.concord.mwbackend.ejb;

import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.interfaces.UserLocalHome;

public abstract class ItemManagerBean implements SessionBean {

	UserLocalHome userHome;

	void initUserHome() {
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
	public Person findPerson(String userID) {
		initUserHome();
		if (userHome != null) {
			UserLocal u = null;
			try {
				u = userHome.findByPrimaryKey(userID);
			} catch (FinderException e) {
				return null;
			}
			if (u != null)
				return new Person(u);
		}
		return null;
	}

	public abstract boolean setDeleted(int pk, boolean b);

}
