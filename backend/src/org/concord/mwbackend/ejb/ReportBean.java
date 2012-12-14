package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="Report" display-name="Name for Report" description="Description for Report"
 *           jndi-name="ejb/Report" type="CMP" cmp-version="2.x" view-type="local"
 *           primkey-field="id"
 * @ejb.finder signature="java.util.Collection findByAuthor(java.lang.String userID)" query="SELECT
 *             OBJECT(s) FROM Report s WHERE s.userID = ?1 AND s.deleted = FALSE ORDER BY s.id DESC"
 * @ejb.finder signature="java.util.Collection findByAuthor(java.lang.String userID,
 *             java.lang.Boolean trash)" query="SELECT OBJECT(s) FROM Report s WHERE s.userID = ?1
 *             AND s.deleted = ?2 ORDER BY s.id DESC"
 * @ejb.finder signature="java.util.Collection findByTeacher(java.lang.String teacher,
 *             java.lang.Boolean trash)" query="SELECT OBJECT(s) FROM Report s WHERE s.teacher = ?1
 *             AND s.deleted = ?2 ORDER BY s.id DESC"
 * @ejb.finder signature="java.util.Collection findBetween(long startTimeMillis, long
 *             endTimeMillis)" query="SELECT OBJECT(o) FROM Report o WHERE o.timeMillis BETWEEN ?1
 *             AND ?2 ORDER BY o.id DESC"
 * @ejb.finder signature="java.util.Collection findLatest()" query="SELECT MAX(o.id) FROM Report o"
 * @ejb.finder signature="java.util.Collection findId(int id)" query="SELECT OBJECT(o) FROM Report o
 *             WHERE o.id = ?1"
 * @ejb.finder signature="java.util.Collection findIdBetween(int startId, int endId)" query="SELECT
 *             OBJECT(o) FROM Report o WHERE o.id BETWEEN ?1 AND ?2 ORDER BY o.id DESC"
 * @ejb.finder signature="java.util.Collection findByKeyword(java.lang.String keyword)"
 *             query="SELECT OBJECT(o) FROM Report o WHERE o.title LIKE ?1 ORDER BY o.id DESC"
 * @ejb.finder signature="java.util.Collection findByKeywordAndTeacher(java.lang.String keyword,
 *             java.lang.String teacher)" query="SELECT OBJECT(o) FROM Report o WHERE o.title LIKE
 *             ?1 AND o.teacher = ?2 ORDER BY o.id DESC"
 * @ejb.finder signature="java.util.Collection findByKeywordAndStudent(java.lang.String keyword,
 *             java.lang.String student)" query="SELECT OBJECT(o) FROM Report o WHERE o.title LIKE
 *             ?1 AND o.userID = ?2 ORDER BY o.id DESC"
 */
public abstract class ReportBean implements EntityBean {

	public ReportBean() {
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
	 * Getter for CMP Field url
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
	 * Getter for CMP Field timeMillis
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Long getTimeMillis();

	/**
	 * Setter for CMP Field timeMillis
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTimeMillis(Long value);

	/**
	 * Getter for CMP Field userID
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUserID();

	/**
	 * Setter for CMP Field userID
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setUserID(String value);

	/**
	 * Getter for CMP Field teacher
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getTeacher();

	/**
	 * Setter for CMP Field teacher
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTeacher(String value);

	/**
	 * Getter for CMP Field deleted
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Boolean getDeleted();

	/**
	 * Setter for CMP Field deleted
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setDeleted(Boolean value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public Integer ejbCreate(String url, String title, String userID, String teacher)
			throws CreateException {
		setId(0); // in the MySQL database, Id is set to "auto increment".
		setUrl(url);
		setTitle(title);
		setUserID(userID);
		setDeleted(Boolean.FALSE);
		setTeacher(teacher);
		setTimeMillis(System.currentTimeMillis());
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String url, String title, String userID, String teacher)
			throws CreateException {
	}

}
