package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="Model" display-name="Name for Model" description="Description for Model"
 *           jndi-name="ejb/Model" type="CMP" cmp-version="2.x" view-type="local" primkey-field="id"
 * 
 * @ejb.finder signature="java.util.Collection findAllByAuthor(java.lang.String userID,
 *             java.lang.Boolean trash)" query="SELECT OBJECT(s) FROM Model s WHERE s.userID = ?1
 *             AND s.deleted = ?2 ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findPublishedByAuthor(java.lang.String userID)"
 *             query="SELECT OBJECT(s) FROM Model s WHERE s.userID = ?1 AND (s.privacy='public' OR
 *             s.privacy IS NULL) AND s.deleted = FALSE ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findNonPrivateByAuthor(java.lang.String userID)"
 *             query="SELECT OBJECT(s) FROM Model s WHERE s.userID = ?1 AND (s.privacy='public' OR
 *             s.privacy='class' OR s.privacy='teacher' OR s.privacy IS NULL) AND s.deleted = FALSE
 *             ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findByTeacher(java.lang.String teacher)"
 *             query="SELECT OBJECT(s) FROM Model s WHERE s.teacher = ?1 AND (s.privacy='public' OR
 *             s.privacy='class' OR s.privacy='teacher' OR s.privacy IS NULL) AND s.deleted = FALSE
 *             ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findByClass(java.lang.String klass)" query="SELECT
 *             OBJECT(s) FROM Model s WHERE s.klass = ?1 AND s.deleted = FALSE AND
 *             (s.privacy='class' OR s.privacy='public' OR s.privacy IS NULL) ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findByClassForTeacher(java.lang.String klass)"
 *             query="SELECT OBJECT(s) FROM Model s WHERE s.klass = ?1 AND s.deleted = FALSE AND
 *             (s.privacy='class' OR s.privacy='teacher' OR s.privacy='public' OR s.privacy IS NULL)
 *             ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findPublishedBetween(long startTimeMillis, long
 *             endTimeMillis)" query="SELECT OBJECT(s) FROM Model s WHERE s.timeMillis BETWEEN ?1
 *             AND ?2 AND s.deleted = FALSE AND (s.privacy IS NULL OR s.privacy='public') ORDER BY
 *             s.id DESC"
 * 
 * @ejb.finder signature="java.util.Collection findAllBetween(long startTimeMillis, long
 *             endTimeMillis)" query="SELECT OBJECT(s) FROM Model s WHERE s.timeMillis BETWEEN ?1
 *             AND ?2 AND s.deleted = FALSE ORDER BY s.id DESC"
 * 
 * @ejb.finder signature="org.concord.mwbackend.interfaces.ModelLocal findByUrl(java.lang.String
 *             url)" query="SELECT OBJECT(s) FROM Model s WHERE s.url= ?1"
 * 
 * @ejb.finder signature="java.util.Collection findLatest()" query="SELECT MAX(s.id) FROM Model s"
 * 
 * @ejb.finder signature="java.util.Collection findIdBetween(int startId, int endId)" query="SELECT
 *             OBJECT(s) FROM Model s WHERE s.id BETWEEN ?1 AND ?2 AND s.deleted = FALSE ORDER BY
 *             s.id DESC"
 */
public abstract class ModelBean implements EntityBean {

	public ModelBean() {
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
	 * Getter for CMP Field zipFile
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getZipFile();

	/**
	 * Setter for CMP Field zipFile
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setZipFile(String value);

	/**
	 * Getter for CMP Field zipSize
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Integer getZipSize();

	/**
	 * Setter for CMP Field zipSize
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setZipSize(Integer value);

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
	 * Setter for CMP Field teacher
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setTeacher(String value);

	/**
	 * Getter for CMP Field teacher
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getTeacher();

	/**
	 * Setter for CMP Field klass
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setKlass(String value);

	/**
	 * Getter for CMP Field klass
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getKlass();

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
	 * Getter for CMP Field privacy
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getPrivacy();

	/**
	 * Setter for CMP Field privacy
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setPrivacy(String value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public Integer ejbCreate(String zipFile, int zipSize, String url, String title, String userID,
			String teacher, String klass, String privacy) throws CreateException {
		setId(0); // in the MySQL database, Id is set to "auto increment".
		setZipFile(zipFile);
		setZipSize(zipSize);
		setUrl(url);
		setTitle(title);
		setUserID(userID);
		setTeacher(teacher);
		setKlass(klass);
		setDeleted(Boolean.FALSE);
		setTimeMillis(System.currentTimeMillis());
		setPrivacy(privacy);
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String zipFile, int zipSize, String url, String title, String userID,
			String teacher, String klass, String privacy) throws CreateException {
	}

}
