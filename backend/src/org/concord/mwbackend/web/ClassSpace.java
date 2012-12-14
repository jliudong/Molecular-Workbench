package org.concord.mwbackend.web;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ClassManager;
import org.concord.mwbackend.interfaces.ClassManagerHome;
import org.concord.mwbackend.interfaces.ClazzLocal;

public class ClassSpace {

	private ClassManagerHome home;

	public ClassSpace() {
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(ClassManagerHome.COMP_NAME);
			home = (ClassManagerHome) PortableRemoteObject.narrow(ref, ClassManagerHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public boolean create(String classID, Person teacher) {
		ClassManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return false;
		boolean classIDExists = false;
		try {
			classIDExists = manager.doesClassExist(classID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (classIDExists)
			return false;
		try {
			manager.create(classID, teacher);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		} catch (CreateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List findClasses(String teacherID) {
		return findClasses(teacherID, false);
	}

	public List findClasses(String teacherID, boolean archived) {
		ClassManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return null;
		Collection classes = null;
		try {
			classes = manager.findClasses(teacherID, archived);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (CreateException e) {
			e.printStackTrace();
			return null;
		}
		if (classes != null) {
			List<String> classIDs = new ArrayList<String>();
			for (Iterator it = classes.iterator(); it.hasNext();) {
				classIDs.add(((ClazzLocal) it.next()).getClazzID());
			}
			return classIDs;
		}
		return null;
	}

	public boolean rename(String oldID, String newID) {
		ClassManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return false;
		boolean success = false;
		try {
			success = manager.renameClass(oldID, newID);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return success;
	}

	public boolean remove(String cid) {
		ClassManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return false;
		boolean success = false;
		try {
			success = manager.removeClass(cid);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return success;
	}

	public boolean archive(String cid, boolean b) {
		ClassManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return false;
		boolean success = false;
		try {
			success = manager.archiveClass(cid, b);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return success;
	}

}
