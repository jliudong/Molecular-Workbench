package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="Activity" display-name="Name" description="Description" jndi-name="ejb/Activity"
 *           type="CMP" cmp-version="2.x" view-type="local" primkey-field="id"
 * 
 * @ejb.finder signature="java.util.Collection findByUser(java.lang.String userID)" query="SELECT
 *             OBJECT(s) FROM Activity s WHERE s.userID = ?1 ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findByUserAndClass(java.lang.String userID,
 *             java.lang.String classID)" query="SELECT OBJECT(s) FROM Activity s WHERE s.userID =
 *             ?1 AND s.classID = ?2 ORDER BY s.id DESC"
 */
public abstract class ActivityBean implements EntityBean {

	public ActivityBean() {
	}

	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbLoad() throws EJBException, RemoteException {
	}

	public void ejbPassivate() throws EJBException, RemoteException {
	}

	public void ejbRemove() throws RemoveException, EJBException, RemoteException {
	}

	public void ejbStore() throws EJBException, RemoteException {
	}

	public void setEntityContext(EntityContext arg0) throws EJBException, RemoteException {
	}

	public void unsetEntityContext() throws EJBException, RemoteException {
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
	 * Getter for CMP Field title
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getTitle();

	/**
	 * Setter for CMP Field title
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTitle(String value);

	/**
	 * Getter for CMP Field Body
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract byte[] getInstruction();

	/**
	 * Setter for CMP Field Body
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setInstruction(byte[] value);

	/**
	 * Getter for CMP Field title
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUrl();

	/**
	 * Setter for CMP Field url
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setUrl(String value);

	/**
	 * Setter for CMP Field UserID
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setUserID(String value);

	/**
	 * Getter for CMP Field UserID
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUserID();

	/**
	 * Setter for CMP Field UserID
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setClassID(String value);

	/**
	 * Getter for CMP Field UserID
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getClassID();

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public Integer ejbCreate(String url, String title, String instruction, String userID,
			String classID) throws CreateException {
		setId(0); // in the MySQL database, Id is set to "auto increment".
		setUrl(url);
		setTitle(title);
		if (instruction != null)
			setInstruction(instruction.getBytes());
		setUserID(userID);
		setClassID(classID);
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String url, String title, String instruction, String userID,
			String classID) throws CreateException {
	}

}
