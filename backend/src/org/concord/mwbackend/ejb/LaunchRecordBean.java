package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="LaunchRecord" display-name="Name for LaunchRecord" description="Description for
 *           LaunchRecord" jndi-name="ejb/LaunchRecord" type="CMP" cmp-version="2.x"
 *           view-type="local" primkey-field="id"
 * @ejb.finder signature="java.util.Collection findRecordsAfter(long timeMillis)" query="SELECT
 *             OBJECT(o) FROM LaunchRecord o WHERE o.timeMillis > ?1 ORDER BY o.timeMillis DESC"
 */
public abstract class LaunchRecordBean implements EntityBean {

	public LaunchRecordBean() {
		// TODO Auto-generated constructor stub
	}

	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbLoad() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbRemove() throws RemoveException, EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void ejbStore() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void setEntityContext(EntityContext arg0) throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void unsetEntityContext() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Getter for CMP Field id
	 * 
	 * @ejb.pk-field
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Integer getId();

	/**
	 * Setter for CMP Field id
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setId(Integer value);

	/**
	 * Getter for CMP Field userName
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUserName();

	/**
	 * Setter for CMP Field userName
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setUserName(String value);

	/**
	 * Getter for CMP Field clientHost
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getClientHost();

	/**
	 * Setter for CMP Field clientHost
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setClientHost(String value);

	/**
	 * Getter for CMP Field ipAddress
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getIpAddress();

	/**
	 * Setter for CMP Field ipAddress
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setIpAddress(String value);

	/**
	 * Getter for CMP Field os
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getOs();

	/**
	 * Setter for CMP Field os
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setOs(String value);

	/**
	 * Getter for CMP Field javaVersion
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getJavaVersion();

	/**
	 * Setter for CMP Field javaVersion
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setJavaVersion(String value);

	/**
	 * Getter for CMP Field mwVersion
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getMwVersion();

	/**
	 * Setter for CMP Field mwVersion
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setMwVersion(String value);

	/**
	 * Getter for CMP Field jwsLaunch
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getJwsLaunch();

	/**
	 * Setter for CMP Field jwsLaunch
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setJwsLaunch(String value);

	/**
	 * Getter for CMP Field timeMillis
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract long getTimeMillis();

	/**
	 * Setter for CMP Field timeMillis
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTimeMillis(long value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public Integer ejbCreate(String userName, String os, String clientHost, String clientIpAddress,
			String javaVersion, String mwVersion, String jwsLaunch) throws CreateException {
		setId(0); // in the MySQL database, Id is set to "auto increment".
		setUserName(userName);
		setOs(os);
		setClientHost(clientHost);
		setIpAddress(clientIpAddress);
		setJavaVersion(javaVersion);
		setMwVersion(mwVersion);
		setJwsLaunch(jwsLaunch);
		setTimeMillis(System.currentTimeMillis());
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String userName, String os, String clientHost,
			String clientIpAddress, String javaVersion, String mwVersion, String jwsLaunch)
			throws CreateException {
	}

}
