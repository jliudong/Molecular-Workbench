/**
 * 
 */
package org.concord.mwbackend.business;

/**
 * @author qxie
 * 
 */
public class Activity {

	private String url;
	private String title;
	private String instruction;
	private String teacherID;
	private String classID;
	private int id;

	public Activity(int id, String url, String title, String instruction, String teacherID,
			String classID) {
		this.id = id;
		this.url = url;
		this.title = title;
		this.instruction = instruction;
		this.teacherID = teacherID;
		this.classID = classID;
	}

	public int getID() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String getInstruction() {
		return instruction;
	}

	public String getTeacherID() {
		return teacherID;
	}

	public String getClassID() {
		return classID;
	}

}
