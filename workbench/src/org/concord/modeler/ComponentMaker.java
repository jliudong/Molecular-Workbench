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

import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.concord.modeler.text.Page;
import org.concord.modeler.util.FileUtilities;

/**
 * @author Charles Xie
 * 
 */
public abstract class ComponentMaker {

	final static String EXECUTE_MW_SCRIPT = "Execute MW script";
	final static String EXECUTE_JMOL_SCRIPT = "Execute Jmol script";
	final static String EXECUTE_NATIVE_SCRIPT = "Execute native script";

	boolean cancel;

	static boolean checkAndSetUid(String uid, Embeddable e, JDialog dialog) {
		if (uid != null) {
			uid = uid.trim();
			if (uid.equals("")) {
				e.setUid(null);
			}
			else {
				if (verifyUid(uid)) {
					if (hasUidBeenUsed(uid, e)) {
						JOptionPane.showMessageDialog(dialog, "The unique identifier " + uid
								+ " has been used by another component on this page.\nPlease choose a different one.",
								"UID error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
					e.setUid(uid);
				}
				else {
					JOptionPane
							.showMessageDialog(
									dialog,
									"The unique identifier must contain only alphenumeric characters and\nat least one alphebetic character.",
									"UID error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		else {
			e.setUid(null);
		}
		return true;
	}

	private static boolean verifyUid(String uid) {
		if (uid.matches("[^a-zA-Z]+"))
			return false;
		return uid.matches("\\w+");
	}

	private static boolean hasUidBeenUsed(String uid, Embeddable e) {
		List<Embeddable> list = e.getPage().getEmbeddedComponents();
		if (list == null || list.isEmpty())
			return false;
		for (Embeddable x : list) {
			if (x != e && uid.equals(x.getUid()))
				return true;
		}
		return false;
	}

	public static boolean isScriptActionKey(String s) {
		return EXECUTE_MW_SCRIPT.equals(s) || EXECUTE_JMOL_SCRIPT.equals(s) || EXECUTE_NATIVE_SCRIPT.equals(s)
				|| "Script".equals(s);
	}

	static ImageIcon loadLocalImage(Page page, String fileName) {
		String s = FileUtilities.getCodeBase(page.getAddress()) + fileName;
		if (!new File(s).exists())
			return null;
		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(s));
		icon.setDescription(fileName);
		return icon;
	}

	static boolean isTargetClass(String modelClass) {
		if (modelClass == null)
			return false;
		for (Class c : ModelCommunicator.targetClass) {
			if (modelClass.equals(c.getName()))
				return true;
		}
		return false;
	}

	static Model getModel(Page page, String modelClass, int modelID) {
		BasicModel m = getBasicModel(page, modelClass, modelID);
		if (m instanceof Model)
			return (Model) m;
		return null;
	}

	static BasicModel getBasicModel(Page page, String modelClass, int modelID) {
		if (modelID == -1)
			return null;
		BasicModel m = null;
		if (isTargetClass(modelClass)) {
			try {
				Object o = page.getEmbeddedComponent(Class.forName(modelClass), modelID);
				if (o instanceof BasicModel) {
					m = (BasicModel) o;
				}
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		else { // backward compatible
			m = page.getComponentPool().get(modelID).getMdContainer().getModel();
		}
		return m;
	}

	static void enable(JComponent c, boolean b, Object source, int modelID, String modelClass, Page page) {
		if (page == null)
			return;
		boolean yes = false;
		if (modelID != -1) {
			if (isTargetClass(modelClass)) {
				try {
					Object o = page.getEmbeddedComponent(Class.forName(modelClass), modelID);
					if (o == source)
						yes = true;
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			else {
				ModelCanvas mc = page.getComponentPool().get(modelID);
				if (mc != null && mc.getMdContainer().getModel() == source)
					yes = true;
			}
		}
		if (yes)
			c.setEnabled(b);
	}

	static Object getScriptAction(Map m) {
		Object o = m.get(EXECUTE_MW_SCRIPT);
		if (o != null)
			return o;
		o = m.get(EXECUTE_JMOL_SCRIPT);
		if (o != null)
			return o;
		return m.get("Script");
	}

	static boolean needNewDialog(JDialog dialog, Page page) {
		if (dialog == null)
			return true;
		if (dialog.getOwner() != SwingUtilities.getWindowAncestor(page))
			return true;
		return false;
	}

	abstract void invoke(Page page);

}