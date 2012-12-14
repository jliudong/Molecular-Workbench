package org.concord.mwbackend.web;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ModelManager;
import org.concord.mwbackend.interfaces.ModelManagerHome;
import org.concord.mwbackend.util.MwConstants;

public class ModelSpace extends MwSpace {

	public final static String MODEL_SPACE = "Model Space";

	private ModelManagerHome modelManagerHome;

	public ModelSpace() {
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(ModelManagerHome.COMP_NAME);
			modelManagerHome = (ModelManagerHome) PortableRemoteObject.narrow(obj,
					ModelManagerHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Person findPerson(String userID) {
		Person p = null;
		try {
			ModelManager bean = modelManagerHome.create();
			p = bean.findPerson(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public List getPublishedItemsBy(String userID) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getPublishedItemsBy(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getNonPrivateItemsBy(String userID) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getNonPrivateItemsBy(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getAllItemsBy(String userID, boolean trash) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = trash ? bean.getAllItemsBy(userID, true) : bean.getAllItemsBy(userID, false);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getPublishedItemsOfDays(int q, int days) {
		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - q * days * MwConstants.MILLISECONDS_OF_A_DAY;
		long endTimeMillis = currentTimeMillis - (q - 1) * days * MwConstants.MILLISECONDS_OF_A_DAY;
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getPublishedItemsBetween(startTimeMillis, endTimeMillis);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getAllItemsOfDays(int q, int days) {
		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - q * days * MwConstants.MILLISECONDS_OF_A_DAY;
		long endTimeMillis = currentTimeMillis - (q - 1) * days * MwConstants.MILLISECONDS_OF_A_DAY;
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getAllItemsBetween(startTimeMillis, endTimeMillis);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int getLatestId() {
		int id = 0;
		try {
			ModelManager bean = modelManagerHome.create();
			id = bean.getLatestPK();
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public List getItemsIdBetween(int startId, int endId) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getItemsIdBetween(startId, endId);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean setDeleted(boolean deleted, int[] id) {
		boolean success = true;
		ModelManager bean = null;
		try {
			bean = modelManagerHome.create();
		} catch (RemoteException e) {
			e.printStackTrace();
			success = false;
		} catch (CreateException e) {
			e.printStackTrace();
			success = false;
		}
		if (bean != null) {
			for (int i : id) {
				try {
					bean.setDeleted(i, deleted);
				} catch (RemoteException e) {
					e.printStackTrace();
					success = false;
					break;
				}
			}
		}
		return success;
	}

	public List getStudentModels(String teacher) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getStudentModels(teacher);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getClassModels(String klass, boolean forTeacher) {
		List list = null;
		try {
			ModelManager bean = modelManagerHome.create();
			list = bean.getClassModels(klass, forTeacher);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
