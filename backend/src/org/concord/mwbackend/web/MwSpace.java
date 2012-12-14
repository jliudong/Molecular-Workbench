package org.concord.mwbackend.web;

import org.concord.mwbackend.business.Person;

public abstract class MwSpace {

	private UserFinder userFinder;

	public MwSpace() {
		userFinder = new UserFinder();
	}

	public void setUserID(String userID) {
		userFinder.setUserID(userID);
	}

	public String getUserID() {
		return userFinder.getUserID();
	}

	public void setPassword(String password) {
		userFinder.setPassword(password);
	}

	public String getPassword() {
		return userFinder.getPassword();
	}

	public UserFinder getUserFinder() {
		return userFinder;
	}

	public abstract Person findPerson(String userID);

	public abstract boolean setDeleted(boolean deleted, int[] id);

}
