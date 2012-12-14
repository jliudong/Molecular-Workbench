package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.concord.mwbackend.interfaces.LaunchRecordLocal;
import org.concord.mwbackend.interfaces.LaunchRecordLocalHome;

/**
 * @ejb.bean name="Receptionist" display-name="Name for Receptionist" description="Description for
 *           Receptionist" jndi-name="ejb/Receptionist" type="Stateless" view-type="remote"
 */
public class ReceptionistBean implements SessionBean {

	private static ResourceBundle userExclusion;

	public ReceptionistBean() {
		userExclusion = ResourceBundle
				.getBundle("org.concord.mwbackend.ejb.resource.UserExclusion");
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

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public void addRecord(String userName, String os, String clientHost, String clientIpAddress,
			String javaVersion, String mwVersion, String jwsFlag) {
		// exclude ourselves
		if (clientHost.toLowerCase().endsWith("concord.org"))
			return;
		for (Enumeration<String> e = userExclusion.getKeys(); e.hasMoreElements();) {
			if (userName.equalsIgnoreCase(userExclusion.getString(e.nextElement())))
				return;
		}
		try {
			LaunchRecordLocalHome home = getLaunchRecordHome();
			home.create(userName, os, clientHost, clientIpAddress, javaVersion, mwVersion, jwsFlag);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
	}

	private LaunchRecordLocalHome getLaunchRecordHome() throws NamingException {
		InitialContext ctx = new InitialContext();
		Object obj = ctx.lookup(LaunchRecordLocalHome.JNDI_NAME);
		return (LaunchRecordLocalHome) obj;
	}

	/**
	 * Business method
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public List getRecordsAfter(long timeMillis) {
		LaunchRecordLocalHome home = null;
		try {
			home = getLaunchRecordHome();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
		Collection<?> c = null;
		try {
			c = home.findRecordsAfter(timeMillis);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null || c.isEmpty())
			return null;
		List<Map> list = new ArrayList<Map>(c.size());
		for (Iterator it = c.iterator(); it.hasNext();) {
			LaunchRecordLocal record = (LaunchRecordLocal) it.next();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", record.getId().toString());
			map.put("username", record.getUserName());
			map.put("host", record.getClientHost());
			map.put("ip", record.getIpAddress());
			map.put("os", record.getOs());
			map.put("javaversion", record.getJavaVersion());
			map.put("mwversion", record.getMwVersion());
			map.put("jws", record.getJwsLaunch());
			map.put("time", new Date(record.getTimeMillis()).toString());
			list.add(map);
		}
		return list;
	}
}
