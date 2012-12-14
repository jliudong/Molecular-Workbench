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

import org.concord.mwbackend.business.Activity;
import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ActivityManager;
import org.concord.mwbackend.interfaces.ActivityManagerHome;
import org.concord.mwbackend.interfaces.ActivityLocal;

public class ActivitySpace {

	private ActivityManagerHome home;

	public ActivitySpace() {
		try {
			Context ctx = new InitialContext();
			Object ref = ctx.lookup(ActivityManagerHome.JNDI_NAME);
			home = (ActivityManagerHome) PortableRemoteObject
					.narrow(ref, ActivityManagerHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public boolean create(String url, String title, String instruction, Person teacher,
			String classID) {
		ActivityManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return false;
		try {
			manager.create(url, title, instruction, teacher, classID);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		} catch (CreateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List findActivities(String teacherID, String classID) {
		ActivityManager manager = null;
		try {
			manager = home.create();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		if (manager == null)
			return null;
		Collection activities = null;
		try {
			if (classID == null) {
				activities = manager.findActivities(teacherID);
			} else {
				activities = manager.findActivities(teacherID, classID);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (CreateException e) {
			e.printStackTrace();
			return null;
		}
		if (activities != null) {
			List<Activity> list = new ArrayList<Activity>();
			for (Iterator it = activities.iterator(); it.hasNext();) {
				ActivityLocal al = (ActivityLocal) it.next();
				list.add(new Activity(al.getId(), al.getUrl(), al.getTitle(),
						al.getInstruction() == null ? null : new String(al.getInstruction()), al
								.getUserID(), al.getClassID()));
			}
			return list;
		}
		return null;
	}

	public boolean remove(int id) {
		ActivityManager manager = null;
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
			success = manager.removeActivity(id);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return success;
	}

}
