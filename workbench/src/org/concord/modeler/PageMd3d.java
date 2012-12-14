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
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.concord.Resource;
import org.concord.modeler.event.AbstractChange;
import org.concord.modeler.event.ModelEvent;
import org.concord.modeler.text.Page;
import org.concord.modeler.text.SaveReminder;
import org.concord.modeler.text.XMLCharacterEncoder;
import org.concord.modeler.ui.BorderRectangle;
import org.concord.modeler.ui.IconPool;
import org.concord.modeler.util.Evaluator;
import org.concord.modeler.util.FileUtilities;
import org.concord.mw3d.MolecularContainer;
import org.concord.mw3d.MolecularView;

import static javax.swing.Action.*;

public class PageMd3d extends MolecularContainer implements Embeddable, Scriptable, NativelyScriptable, Engine {

	Page page;
	private int index;
	private String uid;
	private boolean marked;
	private boolean changable;
	private Map<String, ChangeListener> changeMap;
	private Map<String, Action> choiceMap;
	private Map<String, Action> actionMap;
	private Map<String, Action> switchMap;
	private Map<String, Action> multiSwitchMap;
	private static PageMd3dMaker maker;

	private Action snapshotAction, snapshotAction2, mwScriptAction, jmolScriptAction;
	private Action styleChoices;
	private AbstractChange mwScriptChanger, jmolScriptChanger;

	public PageMd3d() {

		super(Modeler.tapeLength);
		createActions();
		setSnapshotListener(snapshotAction);

		actionMap = Collections.synchronizedMap(new TreeMap<String, Action>());
		actionMap.put((String) snapshotAction.getValue(SHORT_DESCRIPTION), snapshotAction);
		getView().getActionMap().put("snapshot", snapshotAction);
		actionMap.put((String) snapshotAction2.getValue(SHORT_DESCRIPTION), snapshotAction2);
		actionMap.put((String) jmolScriptAction.getValue(SHORT_DESCRIPTION), jmolScriptAction);
		actionMap.put((String) mwScriptAction.getValue(SHORT_DESCRIPTION), mwScriptAction);
		Action a = getView().getActionMap().get("heat");
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);
		a = getView().getActionMap().get("cool");
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);
		a = getView().getActionMap().get("show energy");
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);
		a = getView().getActionMap().get("run");
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);
		a = getView().getActionMap().get(Resource.get("AudioPlayer_java_zanting"));
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);
		a = getView().getActionMap().get("reset");
		actionMap.put((String) a.getValue(SHORT_DESCRIPTION), a);

		switchMap = Collections.synchronizedMap(new TreeMap<String, Action>());
		switchMap.put((String) jmolScriptAction.getValue(SHORT_DESCRIPTION), jmolScriptAction);
		switchMap.put((String) mwScriptAction.getValue(SHORT_DESCRIPTION), mwScriptAction);
		a = getView().getActionMap().get("spin");
		switchMap.put((String) a.getValue(SHORT_DESCRIPTION), a);

		changeMap = Collections.synchronizedMap(new TreeMap<String, ChangeListener>());
		changeMap.put((String) mwScriptChanger.getProperty(AbstractChange.SHORT_DESCRIPTION), mwScriptChanger);
		changeMap.put((String) jmolScriptChanger.getProperty(AbstractChange.SHORT_DESCRIPTION), jmolScriptChanger);

		choiceMap = Collections.synchronizedMap(new TreeMap<String, Action>());
		choiceMap.put((String) styleChoices.getValue(SHORT_DESCRIPTION), styleChoices);
		choiceMap.put((String) mwScriptAction.getValue(SHORT_DESCRIPTION), mwScriptAction);
		choiceMap.put((String) jmolScriptAction.getValue(SHORT_DESCRIPTION), jmolScriptAction);

		multiSwitchMap = Collections.synchronizedMap(new TreeMap<String, Action>());
		multiSwitchMap.put((String) jmolScriptAction.getValue(SHORT_DESCRIPTION), jmolScriptAction);
		multiSwitchMap.put((String) mwScriptAction.getValue(SHORT_DESCRIPTION), mwScriptAction);

		init();

	}

	public PageMd3d getCopy() {
		PageMd3d md = (PageMd3d) InstancePool.sharedInstance().getUnusedInstance(getClass());
		md.setPage(page);
		md.getMolecularView().setFillMode(getMolecularView().getFillMode());
		md.setBorderType(getBorderType());
		md.setPreferredSize(getPreferredSize());
		md.setChangable(page.isEditable());
		return md;
	}

	/** return null */
	public JPopupMenu getPopupMenu() {
		return null;
	}

	/** ignore */
	public void createPopupMenu() {
	}

	private void createActions() {

		snapshotAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (e == null) { // for Eval3D
					SnapshotGallery.sharedInstance().takeSnapshot(page.getAddress(), getMolecularView());
					return;
				}
				if (!(e.getSource() instanceof JMenuItem) && ModelerUtilities.stopFiring(e))
					return;
				SnapshotGallery.sharedInstance().takeSnapshot(page.getAddress(), getMolecularView());
			}

			public String toString() {
				return (String) getValue(SHORT_DESCRIPTION);
			}
		};
		snapshotAction.putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		snapshotAction.putValue(SMALL_ICON, IconPool.getIcon("camera"));
		snapshotAction.putValue(NAME, Resource.get("JmolMenuBar_java_kuaizhao"));
		snapshotAction.putValue(SHORT_DESCRIPTION, Resource.get("DiffractionInstrument_java_kuaizhao"));

		snapshotAction2 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (e == null) { // for Eval3D
					SnapshotGallery.sharedInstance().takeSnapshot(page.getAddress(), getMolecularView());
					return;
				}
				if (!(e.getSource() instanceof JMenuItem) && ModelerUtilities.stopFiring(e))
					return;
				SnapshotGallery.sharedInstance().takeSnapshot(page.getAddress(), getMolecularView(), false);
			}

			public String toString() {
				return (String) getValue(SHORT_DESCRIPTION);
			}
		};
		snapshotAction2.putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		snapshotAction2.putValue(SMALL_ICON, IconPool.getIcon("camera"));
		snapshotAction2.putValue(NAME, "Take a Snapshot Without Description");
		snapshotAction2.putValue(SHORT_DESCRIPTION, "Take a snapshot without description");

		jmolScriptAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (ModelerUtilities.stopFiring(e))
					return;
				Object o = e.getSource();
				if (o instanceof JCheckBox) {
					JCheckBox cb = (JCheckBox) o;
					if (cb.isSelected()) {
						Object o2 = cb.getClientProperty("selection script");
						if (o2 instanceof String) {
							String s = (String) o2;
							if (!s.trim().equals(""))
								getMolecularView().runJmolScript(s);
						}
					}
					else {
						Object o2 = cb.getClientProperty("deselection script");
						if (o2 instanceof String) {
							String s = (String) o2;
							if (!s.trim().equals(""))
								getMolecularView().runJmolScript(s);
						}
					}
				}
				else if (o instanceof AbstractButton) {
					Object o2 = ((AbstractButton) o).getClientProperty("script");
					if (o2 instanceof String) {
						String s = (String) o2;
						if (!s.trim().equals(""))
							getMolecularView().runJmolScript(s);
					}
				}
				else if (o instanceof JComboBox) {
					JComboBox cb = (JComboBox) o;
					Object s = cb.getClientProperty("script" + cb.getSelectedIndex());
					if (s instanceof String)
						getMolecularView().runJmolScript((String) s);
				}
			}

			public String toString() {
				return (String) getValue(SHORT_DESCRIPTION);
			}

		};
		jmolScriptAction.putValue(NAME, "Execute Jmol Script");
		jmolScriptAction.putValue(SHORT_DESCRIPTION, ComponentMaker.EXECUTE_JMOL_SCRIPT);

		mwScriptAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (ModelerUtilities.stopFiring(e))
					return;
				Object o = e.getSource();
				if (o instanceof JCheckBox) {
					JCheckBox cb = (JCheckBox) o;
					if (cb.isSelected()) {
						Object o2 = cb.getClientProperty("selection script");
						if (o2 instanceof String) {
							String s = (String) o2;
							if (!s.trim().equals(""))
								runMwScript(s);
						}
					}
					else {
						Object o2 = cb.getClientProperty("deselection script");
						if (o2 instanceof String) {
							String s = (String) o2;
							if (!s.trim().equals(""))
								runMwScript(s);
						}
					}
				}
				else if (o instanceof AbstractButton) {
					Object o2 = ((AbstractButton) o).getClientProperty("script");
					if (o2 instanceof String) {
						String s = (String) o2;
						if (!s.trim().equals(""))
							runMwScript(s);
					}
				}
				else if (o instanceof JComboBox) {
					JComboBox cb = (JComboBox) o;
					Object s = cb.getClientProperty("script" + cb.getSelectedIndex());
					if (s instanceof String)
						runMwScript((String) s);
				}
			}

			public String toString() {
				return (String) getValue(SHORT_DESCRIPTION);
			}

		};
		mwScriptAction.putValue(NAME, "Execute MW Script");
		mwScriptAction.putValue(SHORT_DESCRIPTION, ComponentMaker.EXECUTE_MW_SCRIPT);

		final String[] t = new String[] { "Space Filling", "Ball and Stick", "Stick", "Wireframe" };
		styleChoices = new AbstractAction() {
			public Object getValue(String key) {
				if ("state".equals(key))
					return t[((MolecularView) getView()).getMolecularStyle() - MolecularView.SPACE_FILLING];
				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				if (!isEnabled())
					return;
				Object o = e.getSource();
				if (o instanceof JComboBox) {
					if (!((JComboBox) o).isShowing())
						return;
					((MolecularView) getView())
							.setMolecularStyle((byte) (((JComboBox) o).getSelectedIndex() - MolecularView.SPACE_FILLING));
					notifyChange();
				}
				repaint();
			}

			public String toString() {
				return (String) getValue(SHORT_DESCRIPTION);
			}
		};
		styleChoices.putValue(NAME, "Molecular Display Style");
		styleChoices.putValue(SHORT_DESCRIPTION, "Molecular display style");
		styleChoices.putValue("options", t);

		mwScriptChanger = new AbstractChange() {
			public void stateChanged(ChangeEvent e) {
				Object o = e.getSource();
				if (o instanceof JSlider) {
					JSlider source = (JSlider) o;
					Double scale = (Double) source.getClientProperty(SCALE);
					double s = scale == null ? 1.0 : 1.0 / scale.doubleValue();
					if (!source.getValueIsAdjusting()) {
						String script = (String) source.getClientProperty("Script");
						if (script != null) {
							String result = source.getValue() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%val", result);
							result = source.getMaximum() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%max", result);
							result = source.getMinimum() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%min", result);
							int lq = script.indexOf('"');
							int rq = script.indexOf('"', lq + 1);
							while (lq != -1 && rq != -1 && lq != rq) {
								String expression = script.substring(lq + 1, rq);
								Evaluator mathEval = new Evaluator(expression.trim());
								result = "" + mathEval.eval();
								if (result.endsWith(".0"))
									result = result.substring(0, result.length() - 2);
								script = script.substring(0, lq) + result + script.substring(rq + 1);
								lq = script.indexOf('"', rq + 1);
								rq = script.indexOf('"', lq + 1);
							}
							runMwScript(script);
						}
					}
				}
				else if (o instanceof JSpinner) {
					JSpinner source = (JSpinner) o;
					String script = (String) source.getClientProperty("Script");
					if (script != null) {
						String result = source.getValue() + "";
						if (result.endsWith(".0"))
							result = result.substring(0, result.length() - 2);
						script = script.replaceAll("(?i)%val", result);
						int lq = script.indexOf('"');
						int rq = script.indexOf('"', lq + 1);
						while (lq != -1 && rq != -1 && lq != rq) {
							String expression = script.substring(lq + 1, rq);
							Evaluator mathEval = new Evaluator(expression.trim());
							result = "" + mathEval.eval();
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.substring(0, lq) + result + script.substring(rq + 1);
							lq = script.indexOf('"', rq + 1);
							rq = script.indexOf('"', lq + 1);
						}
						runMwScript(script);
					}
				}
			}

			public double getMinimum() {
				return 0.0;
			}

			public double getMaximum() {
				return 100.0;
			}

			public double getStepSize() {
				return 1.0;
			}

			public double getValue() {
				return 0.0;
			}

			public String toString() {
				return (String) getProperty(SHORT_DESCRIPTION);
			}
		};
		mwScriptChanger.putProperty(AbstractChange.SHORT_DESCRIPTION, ComponentMaker.EXECUTE_MW_SCRIPT);

		jmolScriptChanger = new AbstractChange() {
			public void stateChanged(ChangeEvent e) {
				Object o = e.getSource();
				if (o instanceof JSlider) {
					JSlider source = (JSlider) o;
					Double scale = (Double) source.getClientProperty(SCALE);
					double s = scale == null ? 1.0 : 1.0 / scale.doubleValue();
					if (!source.getValueIsAdjusting()) {
						String script = (String) source.getClientProperty("Script");
						if (script != null) {
							String result = source.getValue() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%val", result);
							result = source.getMaximum() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%max", result);
							result = source.getMinimum() * s + "";
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.replaceAll("(?i)%min", result);
							int lq = script.indexOf('"');
							int rq = script.indexOf('"', lq + 1);
							while (lq != -1 && rq != -1 && lq != rq) {
								String expression = script.substring(lq + 1, rq);
								Evaluator mathEval = new Evaluator(expression.trim());
								result = "" + mathEval.eval();
								if (result.endsWith(".0"))
									result = result.substring(0, result.length() - 2);
								script = script.substring(0, lq) + result + script.substring(rq + 1);
								lq = script.indexOf('"', rq + 1);
								rq = script.indexOf('"', lq + 1);
							}
							getMolecularView().runJmolScript(script);
						}
					}
				}
				else if (o instanceof JSpinner) {
					JSpinner source = (JSpinner) o;
					String script = (String) source.getClientProperty("Script");
					if (script != null) {
						String result = source.getValue() + "";
						if (result.endsWith(".0"))
							result = result.substring(0, result.length() - 2);
						script = script.replaceAll("(?i)%val", result);
						int lq = script.indexOf('"');
						int rq = script.indexOf('"', lq + 1);
						while (lq != -1 && rq != -1 && lq != rq) {
							String expression = script.substring(lq + 1, rq);
							Evaluator mathEval = new Evaluator(expression.trim());
							result = "" + mathEval.eval();
							if (result.endsWith(".0"))
								result = result.substring(0, result.length() - 2);
							script = script.substring(0, lq) + result + script.substring(rq + 1);
							lq = script.indexOf('"', rq + 1);
							rq = script.indexOf('"', lq + 1);
						}
						getMolecularView().runJmolScript(script);
					}
				}
			}

			public double getMinimum() {
				return 0.0;
			}

			public double getMaximum() {
				return 100.0;
			}

			public double getStepSize() {
				return 1.0;
			}

			public double getValue() {
				return 0.0;
			}

			public String toString() {
				return (String) getProperty(SHORT_DESCRIPTION);
			}
		};
		jmolScriptChanger.putProperty(AbstractChange.SHORT_DESCRIPTION, ComponentMaker.EXECUTE_JMOL_SCRIPT);

	}

	public Map<String, Action> getActions() {
		return actionMap;
	}

	public Map<String, Action> getSwitches() {
		return switchMap;
	}

	public Map<String, Action> getMultiSwitches() {
		return multiSwitchMap;
	}

	public Map<String, ChangeListener> getChanges() {
		return changeMap;
	}

	public Map<String, Action> getChoices() {
		return choiceMap;
	}

	/** runs MW scripts. For Jmol scripts, call runJmolScript */
	public String runScript(String script) {
		return runMwScript(script);
	}

	public String runScriptImmediately(String script) {
		return runMwScriptImmediately(script);
	}

	public Object get(String variable) {
		return getProperty(variable);
	}

	/** runs Jmol scripts. */
	public String runNativeScript(String script) {
		return runJmolScript(script);
	}

	public void destroy() {
		InstancePool.sharedInstance().setStatus(this, false);
		page = null;
		setProgressBar(null);
		if (maker != null)
			maker.setObject(null);
	}

	public void setIndex(int i) {
		index = i;
	}

	public int getIndex() {
		return index;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setMarked(boolean b) {
		marked = b;
	}

	public boolean isMarked() {
		return marked;
	}

	public String getBorderType() {
		return BorderManager.getBorder(this);
	}

	public void setBorderType(String s) {
		BorderManager.setBorder(this, s, page.getBackground());
		if (s == null || BorderRectangle.EMPTY_BORDER.equals(s)) {
			getMolecularView().setBorder(BorderFactory.createEmptyBorder());
		}
		else {
			getMolecularView().setBorder(BorderFactory.createLoweredBevelBorder());
		}
	}

	public void setPage(Page p) {
		page = p;
		// in case this container changes the page context
		if (getPageComponentListeners() != null) {
			Object o;
			for (Iterator it = getPageComponentListeners().iterator(); it.hasNext();) {
				o = it.next();
				if (o instanceof SaveReminder) {
					it.remove();
				}
				else if (o instanceof Editor) {
					it.remove();
				}
			}
		}
		addPageComponentListener(p.getSaveReminder());
		addPageComponentListener(p.getEditor());
		setProgressBar(page.getProgressBar());
		getMolecularModel().setExternalScriptCallback(page.getScriptCallback());
	}

	public Page getPage() {
		return page;
	}

	public void setChangable(boolean b) {
		changable = b;
	}

	public boolean isChangable() {
		return changable;
	}

	public static PageMd3d create(Page page) {
		if (page == null)
			return null;
		PageMd3d v = (PageMd3d) InstancePool.sharedInstance().getUnusedInstance(PageMd3d.class);
		if (maker == null) {
			maker = new PageMd3dMaker(v);
		}
		else {
			maker.setObject(v);
		}
		maker.invoke(page);
		if (maker.cancel) {
			InstancePool.sharedInstance().setStatus(v, false);
			return null;
		}
		v.enableMenuBar(true);
		v.enableToolBar(true);
		v.enableBottomBar(true);
		// must call reset before init, in order to show the empty simulation box
		v.reset();
		// must call init() in case the frame is destroyed
		v.init();
		return v;
	}

	protected void setViewerSize(Dimension dim) {
		setPreferredSize(dim);
		if (page != null)
			page.settleComponentSize();
	}

	public void setPreferredSize(Dimension dim) {
		if (dim == null)
			throw new IllegalArgumentException("null object");
		if (dim.width == 0 || dim.height == 0)
			throw new IllegalArgumentException("zero dimension");
		super.setPreferredSize(dim);
		super.setMinimumSize(dim);
		super.setMaximumSize(dim);
	}

	public void notifyFileLoaded(final String fullPathName, final String fileName, final String modelName,
			final Object clientFile, final String errorMessage) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PageMd3d.super.notifyFileLoaded(fullPathName, fileName, modelName, clientFile, errorMessage);
				notifyModelListeners(new ModelEvent(PageMd3d.this, ModelEvent.MODEL_INPUT));
			}
		});
	}

	public void createImage(String file, String type, int quality) {
	}

	public String eval(String strEval) {
		return null;
	}

	public float functionXY(String functionName, int x, int y) {
		return 0;
	}

	public void notifyAtomHovered(int atomIndex, String strInfo) {
	}

	public void notifyNewDefaultModeMeasurement(int count, String strInfo) {
	}

	public void notifyNewPickingModeMeasurement(int iatom, String strMeasure) {
	}

	public void notifyScriptStart(String statusMessage, String additionalInfo) {
	}

	public void sendConsoleEcho(String strEcho) {
	}

	public void sendConsoleMessage(String strStatus) {
	}

	public void sendSyncScript(String script, String appletName) {
	}

	public void setCallbackFunction(String callbackType, String callbackFunction) {
	}

	public String toString() {

		StringBuffer b = new StringBuffer("<class>" + getClass().getName() + "</class>");

		String s = FileUtilities.getFileName(getResourceAddress());
		if (s != null) {
			b.append("<resource>" + XMLCharacterEncoder.encode(s) + "</resource>");
			b.append("<coordinate>" + XMLCharacterEncoder.encode(FileUtilities.changeExtension(s, "xyz"))
					+ "</coordinate>");
		}

		if (uid != null)
			b.append("<uid>" + uid + "</uid>\n");

		b.append("<width>" + getWidth() + "</width><height>" + getHeight() + "</height>");

		if (!isMenuBarEnabled())
			b.append("<menubar>false</menubar>\n");

		if (!isToolBarEnabled())
			b.append("<toolbar>false</toolbar>\n");

		if (!isBottomBarEnabled())
			b.append("<statusbar>false</statusbar>\n");

		if (!getBorderType().equals(BorderManager.BORDER_TYPE[0]))
			b.append("<border>" + getBorderType() + "</border>\n");

		if (getMolecularModel().getRecorderDisabled())
			b.append("<recorderless>true</recorderless>\n");

		return b.toString();

	}

}