package org.concord.mwbackend.web;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.concord.mwbackend.business.Person;
import org.concord.mwbackend.interfaces.ReportManager;
import org.concord.mwbackend.interfaces.ReportManagerHome;
import org.concord.mwbackend.util.MwConstants;

public class ReportSpace extends MwSpace {

	public final static String REPORT_SPACE = "Report Space";

	private ReportManagerHome reportManagerHome;

	public ReportSpace() {
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(ReportManagerHome.COMP_NAME);
			reportManagerHome = (ReportManagerHome) PortableRemoteObject.narrow(obj,
					ReportManagerHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Person findPerson(String userID) {
		Person p = null;
		try {
			ReportManager bean = reportManagerHome.create();
			p = bean.findPerson(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public List getItemsBy(String userID, boolean trash) {
		List list = null;
		try {
			ReportManager bean = reportManagerHome.create();
			list = trash ? bean.getItemsBy(userID, true) : bean.getItemsBy(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getItemsOfDays(int q, int days) {
		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - q * days * MwConstants.MILLISECONDS_OF_A_DAY;
		long endTimeMillis = currentTimeMillis - (q - 1) * days * MwConstants.MILLISECONDS_OF_A_DAY;
		List list = null;
		try {
			ReportManager bean = reportManagerHome.create();
			list = bean.getItemsBetween(startTimeMillis, endTimeMillis);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int getLatestId() {
		int id = 0;
		try {
			ReportManager bean = reportManagerHome.create();
			id = bean.getLatestPK();
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public List getItemWithId(int id) {
		List list = null;
		try {
			ReportManager bean = reportManagerHome.create();
			list = bean.getItemWithId(id);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getItemsIdBetween(int startId, int endId) {
		List list = null;
		try {
			ReportManager bean = reportManagerHome.create();
			list = bean.getItemsIdBetween(startId, endId);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getStudentReports(String teacher) {
		List list = null;
		try {
			ReportManager bean = reportManagerHome.create();
			list = bean.getStudentReports(teacher);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings( { "unchecked" })
	public List search(String keyword, String teacher, String student) {
		if (keyword == null || keyword.trim().equals(""))
			return null;
		List list1 = null, list2 = null;
		String keyword2 = null;
		if (keyword.startsWith("%DNA")) { // ugly fix
			keyword2 = keyword.substring(0, 4) + (keyword.substring(4).toLowerCase());
		} else {
			keyword2 = keyword.substring(0, 2) + (keyword.substring(2).toLowerCase());
		}
		// System.out.println(keyword + ", " + keyword2);
		try {
			ReportManager bean = reportManagerHome.create();
			list1 = bean.search(keyword, teacher, student);
			if (!keyword.equals(keyword2))
				list2 = bean.search(keyword2, teacher, student);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List list = new ArrayList();
		if (list1 != null && !list1.isEmpty())
			list.addAll(list1);
		if (list2 != null && !list2.isEmpty())
			list.addAll(list2);
		return list;
	}

	public boolean setDeleted(boolean deleted, int[] id) {
		boolean success = true;
		ReportManager bean = null;
		try {
			bean = reportManagerHome.create();
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

}
