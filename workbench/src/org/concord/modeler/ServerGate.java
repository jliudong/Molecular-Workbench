/*
 *   Copyright (C) 2006  The Concord Consortium, Inc.,
 *   25 Love Lane, Concord, MA 01742
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */
package org.concord.modeler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.concord.Resource;
import org.concord.modeler.text.DefaultAction;
import org.concord.modeler.text.Page;

/**
 * @author Charles Xie
 * 
 */
class ServerGate {

	final static String[] DIALOG_OPTIONS = { "Yes", "No", "Don't show this message again." };
	private final static byte POST_COMMENT = 0;
	private final static byte SUBMIT_PAGES = 1;
	private final static byte REGISTER = 11;
	private final static byte LOGIN = 12;

	static {
		String s = Modeler.getInternationalText("Yes");
		if (s != null)
			DIALOG_OPTIONS[0] = s;
		s = Modeler.getInternationalText("No");
		if (s != null)
			DIALOG_OPTIONS[1] = s;
		s = Modeler.getInternationalText("DontShowAgain");
		if (s != null)
			DIALOG_OPTIONS[2] = s;
	}

	private Page page;
	private CommentDialog commentDialog;
	private CommentView commentView;
	private SubmissionDialog submissionDialog;
	private ReportDialog reportDialog;
	private byte flag = REGISTER;

	Action commentAction;
	Action viewCommentAction;
	ActionListener uploadCurrentFolderAction;
	ActionListener uploadAction;
	Action uploadReportAction;

	ServerGate(Page page) {
		this.page = page;
		init();
		page.setUploadReportAction(uploadReportAction);
		page.putActivityAction(commentAction);
		page.putActivityAction(viewCommentAction);
	}

	private void init() {

		commentAction = new DefaultAction() {
			public void actionPerformed(ActionEvent e) {
				if (ModelerUtilities.stopFiring(e))
					return;
				if (!question(POST_COMMENT))
					return;
				if (!page.isRemote()) {
					JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(page),
							"You cannot comment on a local page.", "No comment on local page",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (Modeler.user.isEmpty()) {
					String s = Modeler.getInternationalText("YouAreNotLoggedInYetPleaseTryAgain");
					JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(page), s != null ? s
							: "You are not logged in yet. Please try again.", "Message", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (commentDialog == null) {
					commentDialog = new CommentDialog(page, Modeler.user);
					commentDialog.setViewCommentAction(viewCommentAction);
				}
				commentDialog.setPageAddress(page.getAddress());
				commentDialog.setVisible(true);
			}
		};
		commentAction.putValue(Action.NAME, "Comment");
		commentAction.putValue(Action.SHORT_DESCRIPTION, "Comment");
		commentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		commentAction.putValue(Action.ACCELERATOR_KEY, Modeler.IS_MAC ? KeyStroke.getKeyStroke(KeyEvent.VK_C,
				KeyEvent.META_MASK | KeyEvent.SHIFT_MASK, true) : KeyStroke.getKeyStroke(KeyEvent.VK_C,
				KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, true));

		viewCommentAction = new DefaultAction() {
			public void actionPerformed(ActionEvent e) {
				if (ModelerUtilities.stopFiring(e))
					return;
				if (commentView == null)
					commentView = new CommentView();
				commentView.showComments(page.getAddress(), page.getTitle(), JOptionPane.getFrameForComponent(page));
			}
		};
		viewCommentAction.putValue(Action.NAME, Resource.get("ServerGate_java_guankanpinglun"));
		viewCommentAction.putValue(Action.SHORT_DESCRIPTION, Resource.get("ServerGate_java_guankanpinglun"));

		uploadCurrentFolderAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!question(SUBMIT_PAGES))
					return;
				if (shallWeContinue()) {
					boolean b = Modeler.user.hasTeacher();
					if (submissionDialog == null)
						submissionDialog = new SubmissionDialog(JOptionPane.getFrameForComponent(page), b);
					else submissionDialog.setStudentPrivacyOptions(b);
					submissionDialog.setTaskType(Upload.UPLOAD_FOLDER);
					submissionDialog.setPage(page);
					submissionDialog.setVisible(true);
				}
			}
		};

		uploadAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!question(SUBMIT_PAGES))
					return;
				if (shallWeContinue()) {
					boolean b = Modeler.user.hasTeacher();
					if (submissionDialog == null)
						submissionDialog = new SubmissionDialog(JOptionPane.getFrameForComponent(page), b);
					else submissionDialog.setStudentPrivacyOptions(b);
					submissionDialog.setTaskType(Upload.UPLOAD_PAGE);
					submissionDialog.setPage(page);
					submissionDialog.setVisible(true);
				}
			}
		};

		uploadReportAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (reportDialog == null)
					reportDialog = new ReportDialog(JOptionPane.getFrameForComponent(page));
				reportDialog.setStudent(Modeler.user);
				reportDialog.setPage(page);
				reportDialog.setVisible(true);
			}
		};
		uploadReportAction.putValue(Action.NAME, "Submit Report");
		uploadReportAction.putValue(Action.SHORT_DESCRIPTION, "Submit this report to my space and my teacher");
		uploadReportAction.putValue(Action.SMALL_ICON, new ImageIcon(Editor.class.getResource("images/DocIn.gif")));

	}

	// ask some user questions
	private boolean question(byte type) {
		if (Modeler.user.isEmpty())
			return handleUserDataNotFound();
		return handleUserDataFound(type);
	}

	private boolean handleUserDataFound(byte type) {
		String name = Modeler.user.getFullName();
		String s = Modeler.getInternationalText("AreYou");
		int i = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(page), s == null ? name + " (ID: "
				+ Modeler.user.getUserID() + ") is currently logged in.\nAre you " + name + "?" : s + " " + name + "?");
		if (i == JOptionPane.YES_OPTION)
			return true;
		if (i == JOptionPane.NO_OPTION) {
			Page p = new Page();
			p.setPreferredSize(new Dimension(500, 300));
			p.visit(Modeler.getContextRoot() + "login.jsp?client=mw&action=logout");
			flag = LOGIN;
			String[] ops = new String[] { "OK", "Cancel" };
			i = JOptionPane.showOptionDialog(JOptionPane.getFrameForComponent(page), p, "Log in",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
			return i == JOptionPane.OK_OPTION || i == JOptionPane.CLOSED_OPTION;
		}
		return false;
	}

	private boolean handleUserDataNotFound() {
		String s = Modeler.getInternationalText("HaveYouRegisteredWithMolecularWorkbench");
		String s2 = Modeler.getInternationalText("Question");
		int i = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(page), s != null ? s
				: "You need a Molecular Workbench account in order to continue.\nHave you got one yet?",
				s2 != null ? s2 : "Question", JOptionPane.YES_NO_OPTION);
		if (i == JOptionPane.YES_OPTION) {
			Page p = new Page();
			p.setPreferredSize(new Dimension(500, 300));
			p.visit(Modeler.getContextRoot() + "login.jsp?client=mw");
			flag = LOGIN;
			s = Modeler.getInternationalText("Login");
			i = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(page), p, s != null ? s : "Login",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
			return i == JOptionPane.OK_OPTION || i == JOptionPane.CLOSED_OPTION;
		}
		Page p = new Page();
		p.setPreferredSize(new Dimension(900, 600));
		p.visit(Modeler.getContextRoot() + "register.jsp?client=mw&onreport=yes");
		flag = REGISTER;
		s = Modeler.getInternationalText("DoneWithRegistration");
		s2 = Modeler.getInternationalText("CancelButton");
		String s3 = Modeler.getInternationalText("Registration");
		String[] ops = new String[] { s != null ? s : "Done with registration.", s2 != null ? s2 : "Cancel" };
		i = JOptionPane.showOptionDialog(JOptionPane.getFrameForComponent(page), p, s3 != null ? s3 : "Registration",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
		return i == JOptionPane.OK_OPTION || i == JOptionPane.CLOSED_OPTION;
	}

	private boolean shallWeContinue() {
		if (page.isRemote()) {
			JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(page),
					"You cannot submit a page that is not on your disk.", "Submission error",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (Modeler.user.isEmpty()) {
			switch (flag) {
			case LOGIN:
				String s = Modeler.getInternationalText("YouAreNotLoggedInYetPleaseTryAgain");
				JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(page), s != null ? s
						: "You are not logged in yet. Please try again.", "Message", JOptionPane.WARNING_MESSAGE);
				break;
			case REGISTER:
				s = Modeler.getInternationalText("RegistrationNotCompletedPleaseTryAgain");
				JOptionPane
						.showMessageDialog(
								JOptionPane.getFrameForComponent(page),
								s != null ? s
										: "Sorry, we are unable to complete your registration. Please try again.\nMake sure you press the Continue Button inside the registration\npage and follow the instructions.",
								"Message", JOptionPane.WARNING_MESSAGE);
				break;
			}
			return false;
		}
		return true;
	}

}
