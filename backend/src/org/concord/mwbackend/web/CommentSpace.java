package org.concord.mwbackend.web;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.concord.mwbackend.interfaces.CommentManager;
import org.concord.mwbackend.interfaces.CommentManagerHome;
import org.concord.mwbackend.util.MwConstants;

public class CommentSpace {

	private CommentManagerHome commentManagerHome;

	public CommentSpace() {
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(CommentManagerHome.COMP_NAME);
			commentManagerHome = (CommentManagerHome) PortableRemoteObject.narrow(obj,
					CommentManagerHome.class);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public List getCommentsBy(String userID) {
		List list = null;
		try {
			CommentManager bean = commentManagerHome.create();
			list = bean.getCommentsByUser(userID);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getCommentsOfQuarter(int q) {
		long currentTimeMillis = System.currentTimeMillis();
		long startTimeMillis = currentTimeMillis - q * MwConstants.MILLISECONDS_OF_A_QUARTER;
		long endTimeMillis = currentTimeMillis - (q - 1) * MwConstants.MILLISECONDS_OF_A_QUARTER;
		List list = null;
		try {
			CommentManager bean = commentManagerHome.create();
			list = bean.getCommentsBetween(startTimeMillis, endTimeMillis);
			bean.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean deleteComment(int[] id) {
		boolean success = true;
		CommentManager bean = null;
		try {
			bean = commentManagerHome.create();
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
					bean.deleteComment(i);
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
