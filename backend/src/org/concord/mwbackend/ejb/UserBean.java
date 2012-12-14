package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

import org.concord.mwbackend.business.Person;

/**
 * @ejb.bean name="User" display-name="Name for User" description="Description for User"
 *           jndi-name="ejb/User" type="CMP" cmp-version="2.x" view-type="local"
 *           primkey-field="userID"
 * @ejb.finder signature="java.util.Collection findAll()" query="SELECT OBJECT(u) FROM User u"
 * @ejb.finder signature="java.util.Collection findByEmail(java.lang.String email)" query="SELECT
 *             OBJECT(u) FROM User u WHERE u.email = ?1"
 * @ejb.finder signature="java.util.Collection findByName(java.lang.Integer type, java.lang.String
 *             firstName, java.lang.String lastName)" query="SELECT OBJECT(u) FROM User u WHERE
 *             u.type = ?1 AND u.firstName = ?2 AND u.lastName = ?3"
 * @ejb.finder signature="java.util.Collection findByLastName(java.lang.Integer type,
 *             java.lang.String lastName)" query="SELECT OBJECT(u) FROM User u WHERE u.type = ?1 AND
 *             u.lastName = ?2"
 * @ejb.finder signature="java.util.Collection findByType(java.lang.Integer type)" query="SELECT
 *             OBJECT(u) FROM User u WHERE u.type = ?1 ORDER BY u.userID ASC"
 * @ejb.finder signature="java.util.Collection findStudents(java.lang.String teacher)" query="SELECT
 *             OBJECT(u) FROM User u WHERE u.teacher = ?1 ORDER BY u.userID ASC"
 * @ejb.finder signature="java.util.Collection findStudentsOfClass(java.lang.String klass,
 *             java.lang.String teacher)" query="SELECT OBJECT(u) FROM User u WHERE u.klass = ?1 AND
 *             u.teacher = ?2 ORDER BY u.userID ASC"
 * @ejb.finder signature="java.util.Collection findPersonsToNotifyForNewModel(java.lang.Boolean
 *             notifyModel)" query="SELECT OBJECT(u) FROM User u WHERE u.notifyModel = ?1"
 */
public abstract class UserBean implements EntityBean {

	public UserBean() {
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
	 * Getter for CMP Field UserID
	 * 
	 * 
	 * @ejb.pk-field
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getUserID();

	/**
	 * Setter for CMP Field UserID
	 * 
	 * @ejb.interface-method view-type="remote"
	 */
	public abstract void setUserID(String value);

	/**
	 * Getter for CMP Field Password
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getPassword();

	/**
	 * Setter for CMP Field Password
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setPassword(String value);

	/**
	 * Getter for CMP Field email
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getEmail();

	/**
	 * Setter for CMP Field email
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setEmail(String value);

	/**
	 * Getter for CMP Field MemberSince
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract long getMemberSince();

	/**
	 * Setter for CMP Field MemberSince
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setMemberSince(long value);

	/**
	 * Getter for CMP Field firstName
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getFirstName();

	/**
	 * Setter for CMP Field firstName
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setFirstName(String value);

	/**
	 * Getter for CMP Field lastName
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getLastName();

	/**
	 * Setter for CMP Field lastName
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setLastName(String value);

	/**
	 * Getter for CMP Field institution
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getInstitution();

	/**
	 * Setter for CMP Field institution
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setInstitution(String value);

	/**
	 * Getter for CMP Field state
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getState();

	/**
	 * Setter for CMP Field state
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setState(String value);

	/**
	 * Getter for CMP Field country
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getCountry();

	/**
	 * Setter for CMP Field country
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setCountry(String value);

	/**
	 * Getter for CMP Field Type
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Integer getType();

	/**
	 * Setter for CMP Field Type
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setType(Integer value);

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
	 * Getter for CMP Field klass
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getKlass();

	/**
	 * Setter for CMP Field klass
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setKlass(String value);

	/**
	 * Getter for CMP Field notifyModel
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Boolean getNotifyModel();

	/**
	 * Setter for CMP Field notifyModel
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setNotifyModel(Boolean value);

	/**
	 * Getter for CMP Field notifyReport
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Boolean getNotifyReport();

	/**
	 * Setter for CMP Field notifyReport
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setNotifyReport(Boolean value);

	/**
	 * Getter for CMP Field acceptNewsletter
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Boolean getAcceptNewsletter();

	/**
	 * Setter for CMP Field acceptNewsletter
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setAcceptNewsletter(Boolean value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public String ejbCreate(Person user) throws CreateException {
		setUserID(user.getUserID());
		setPassword(user.getPassword());
		setEmail(user.getEmailAddress());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setInstitution(user.getInstitution());
		setState(user.getState());
		setCountry(user.getCountry());
		setMemberSince(System.currentTimeMillis());
		setType(user.getType());
		setTeacher(user.getTeacher());
		setKlass(user.getKlass());
		setNotifyModel(user.getNotifyModel());
		setNotifyReport(user.getNotifyReport());
		setAcceptNewsletter(user.getAcceptNewsletter());
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(Person user) throws CreateException {
	}

	/**
	 * Getter for CMR Relationship
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.relation name = "User-Comment" role-name = "user-has-comments" target-ejb = "Comment"
	 *               target-role-name = "Comment-User" target-multiple = "no"
	 */
	public abstract Collection getComments();

	/**
	 * Setter for CMR Relationship
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setComments(Collection value);

}