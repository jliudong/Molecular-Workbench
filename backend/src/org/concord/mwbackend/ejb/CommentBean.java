package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="Comment" display-name="Name for Comment" description="Description for Comment"
 *           jndi-name="ejb/Comment" type="CMP" cmp-version="2.x" primkey-field="id"
 *           view-type="local"
 * @ejb.finder signature="java.util.Collection findByUrl(java.lang.String url)" query="SELECT
 *             OBJECT(c) FROM Comment c WHERE c.url = ?1 ORDER BY c.id DESC"
 * @ejb.finder signature="java.util.Collection findCommentsAfter(long timeMillis)" query="SELECT
 *             OBJECT(c) FROM Comment c WHERE c.timeMillis > ?1 ORDER BY c.id DESC"
 * @ejb.finder signature="java.util.Collection findBetween(long startTimeMillis, long
 *             endTimeMillis)" query="SELECT OBJECT(c) FROM Comment c WHERE c.timeMillis BETWEEN ?1
 *             AND ?2 ORDER BY c.id DESC"
 * @ejb.finder signature="java.util.Collection findByUser(java.lang.String userID)" query="SELECT
 *             OBJECT(c) FROM Comment c WHERE c.userID = ?1 ORDER BY c.id DESC"
 */
public abstract class CommentBean implements EntityBean {

	public CommentBean() {
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

	public void setEntityContext(EntityContext ctx) throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	public void unsetEntityContext() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Getter for CMP Field Id
	 * 
	 * @ejb.pk-field
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Integer getId();

	/**
	 * Setter for CMP Field Id
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setId(Integer value);

	/**
	 * Getter for CMP Field Url
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUrl();

	/**
	 * Setter for CMP Field Url
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setUrl(String value);

	/**
	 * Getter for CMP Field Title
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getTitle();

	/**
	 * Setter for CMP Field Title
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
	public abstract byte[] getBody();

	/**
	 * Setter for CMP Field Body
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setBody(byte[] value);

	/**
	 * Getter for CMP Field Ip
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getIp();

	/**
	 * Setter for CMP Field Ip
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setIp(String value);

	/**
	 * Getter for CMP Field TimeMillis
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract long getTimeMillis();

	/**
	 * Setter for CMP Field TimeMillis
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTimeMillis(long value);

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
	public abstract void setUserID(String value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public Integer ejbCreate(String url, String title, String body, String userID, String ip)
			throws CreateException {
		setId(0); // in the MySQL database, Id is set to "auto increment".
		setUrl(url);
		setTitle(title);
		setBody(body.getBytes());
		setIp(ip);
		setUserID(userID);
		setTimeMillis(System.currentTimeMillis());
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String url, String title, String body, String userID, String ip)
			throws CreateException {
	}

}