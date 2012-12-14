package org.concord.mwbackend.business;

import java.io.Serializable;

import org.concord.mwbackend.interfaces.UserLocal;
import org.concord.mwbackend.util.HashCodeUtil;

public class Person implements Serializable {

	public final static int STUDENT = 0;
	public final static int TEACHER = 1;
	public final static int OTHER = 2;

	// required information
	private String userID;
	private String emailAddress;
	private String password;

	// optional information
	private String firstName;
	private String lastName;
	private String institution;
	private String state;
	private String country;
	private long memberSince;
	private int type = STUDENT;
	private String teacher;
	private String klass;
	private boolean notifyModel;
	private boolean notifyReport;
	private boolean acceptNewsletter = true;

	public Person() {
	}

	public Person(UserLocal u) {
		setUserID(u.getUserID());
		setFirstName(u.getFirstName());
		setLastName(u.getLastName());
		setPassword(u.getPassword());
		setInstitution(u.getInstitution());
		setState(u.getState());
		setCountry(u.getCountry());
		setEmailAddress(u.getEmail());
		setMemberSince(u.getMemberSince());
		setType(u.getType());
		setTeacher(u.getTeacher());
		setKlass(u.getKlass());
		if (u.getNotifyModel() != null)
			setNotifyModel(u.getNotifyModel());
		if (u.getNotifyReport() != null)
			setNotifyReport(u.getNotifyReport());
		if (u.getAcceptNewsletter() != null) {
			setAcceptNewsletter(u.getAcceptNewsletter());
		}
	}

	public Person(String userID, String emailAddress, String firstName, String lastName, String institution,
			String state, String country) {
		this.userID = userID;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.institution = institution;
		this.state = state;
		this.country = country;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getPreferredName() {
		if (firstName == null && lastName == null)
			return userID;
		return getFullName();
	}

	public void setFirstName(String s) {
		firstName = s;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String s) {
		lastName = s;
	}

	public String getLastName() {
		return lastName;
	}

	public void setUserID(String s) {
		userID = s;
	}

	public String getUserID() {
		return userID;
	}

	public void setInstitution(String s) {
		institution = s;
	}

	public String getInstitution() {
		return institution;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public void setEmailAddress(String s) {
		emailAddress = s;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setMemberSince(long memberSince) {
		this.memberSince = memberSince;
	}

	public long getMemberSince() {
		return memberSince;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setKlass(String klass) {
		this.klass = klass;
	}

	public String getKlass() {
		return klass;
	}

	public void setNotifyModel(boolean notifyModel) {
		this.notifyModel = notifyModel;
	}

	public boolean getNotifyModel() {
		return notifyModel;
	}

	public void setNotifyReport(boolean notifyReport) {
		this.notifyReport = notifyReport;
	}

	public boolean getNotifyReport() {
		return notifyReport;
	}

	public void setAcceptNewsletter(boolean acceptNewsletter) {
		this.acceptNewsletter = acceptNewsletter;
	}

	public boolean getAcceptNewsletter() {
		return acceptNewsletter;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Person))
			return false;
		Person p = (Person) o;
		if (userID != null && !userID.equals(p.userID))
			return false;
		if (userID == null && p.userID != null)
			return false;
		return true;
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		if (userID != null)
			result = HashCodeUtil.hash(result, userID);
		return result;
	}

	public String toString() {
		return userID + " " + password + " " + firstName + " " + lastName + ", " + emailAddress;
	}
}
