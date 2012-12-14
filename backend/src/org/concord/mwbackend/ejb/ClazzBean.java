package org.concord.mwbackend.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

/**
 * @ejb.bean name="Clazz" display-name="Name for Clazz" description="Description for Clazz"
 *           jndi-name="ejb/Clazz" type="CMP" cmp-version="2.x" view-type="local"
 *           primkey-field="clazzID"
 * @ejb.finder signature="java.util.Collection findClasses(java.lang.String teacherID,
 *             java.lang.Boolean archived)" query="SELECT OBJECT(c) FROM Clazz c WHERE c.teacherID =
 *             ?1 AND c.archived = ?2"
 */
public abstract class ClazzBean implements EntityBean {

	public ClazzBean() {
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

	public void setEntityContext(EntityContext ctx) throws EJBException, RemoteException {
	}

	public void unsetEntityContext() throws EJBException, RemoteException {
	}

	/**
	 * Getter for CMP Field ClazzID
	 * 
	 * 
	 * @ejb.pk-field
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getClazzID();

	/**
	 * Setter for CMP Field ClazzID
	 * 
	 * @ejb.interface-method view-type="remote"
	 */
	public abstract void setClazzID(String value);

	/**
	 * Getter for CMP Field TeacherID
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract String getTeacherID();

	/**
	 * Setter for CMP Field TeacherID
	 * 
	 * @ejb.interface-method view-type="remote"
	 */
	public abstract void setTeacherID(String value);

	/**
	 * Getter for CMP Field archived
	 * 
	 * 
	 * @ejb.persistent-field
	 * @ejb.interface-method view-type="local"
	 */
	public abstract Boolean getArchived();

	/**
	 * Setter for CMP Field archived
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setArchived(Boolean value);

	/**
	 * Create method
	 * 
	 * @ejb.create-method view-type = "local"
	 */
	public String ejbCreate(String clazzID, String teacherID, boolean archived)
			throws CreateException {
		setClazzID(clazzID);
		setTeacherID(teacherID);
		setArchived(archived);
		return null;
	}

	/**
	 * Post Create method
	 */
	public void ejbPostCreate(String clazzID, String teacherID, boolean archived)
			throws CreateException {
	}

	/**
	 * Getter for CMR Relationship
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.relation name = "Class-Students" role-name = "class-students" target-ejb = "User"
	 *               target-role-name = "students-class"
	 */
	public abstract Collection getStudents();

	/**
	 * Setter for CMR Relationship
	 * 
	 * @ejb.interface-method view-type="local"
	 */
	public abstract void setStudents(Collection value);

}