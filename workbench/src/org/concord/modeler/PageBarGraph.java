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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.concord.Resource;
import org.concord.modeler.event.ModelEvent;
import org.concord.modeler.event.MovieEvent;
import org.concord.modeler.event.MovieListener;
import org.concord.modeler.text.Page;
import org.concord.modeler.text.XMLCharacterEncoder;
import org.concord.modeler.ui.BarGraph;
import org.concord.modeler.util.DataQueue;
import org.concord.modeler.util.FloatQueue;

public class PageBarGraph extends BarGraph implements Embeddable, Scriptable, ModelCommunicator, MovieListener {

	public final static byte GROWING_POINT_RUNNING_AVERAGE = 0;
	public final static byte SIMPLE_RUNNING_AVERAGE = 1;
	public final static byte EXPONENTIAL_RUNNING_AVERAGE = 2;

	Page page;
	String timeSeriesName;
	String modelClass;
	int modelID = -1;
	byte averageType = GROWING_POINT_RUNNING_AVERAGE;
	float smoothingFactor = 0.05f;
	int samplingPoints = 10;
	private double initialValue;
	private int index;
	private String uid;
	private boolean marked;
	private boolean changable;
	private Color originalBackground, originalForeground;
	private JPopupMenu popupMenu;
	private static PageBarGraphMaker maker;
	private MouseListener popupMouseListener;
	private BarGraphScripter scripter;

	public PageBarGraph() {
		super();
		popupMouseListener = new PopupMouseListener(this);
		addMouseListener(popupMouseListener);
		setFont(new Font(null, Font.PLAIN, Page.getDefaultFontSize() - 1));
	}

	public PageBarGraph(int o) {
		this();
		setOrientation(o);
	}

	public PageBarGraph(PageBarGraph g, Page parent) {
		this();
		setPage(parent);
		setModelID(g.modelID);
		setUid(g.uid);
		setAverageType(g.getAverageType());
		switch (getAverageType()) {
		case SIMPLE_RUNNING_AVERAGE:
			samplingPoints = g.samplingPoints;
			break;
		case EXPONENTIAL_RUNNING_AVERAGE:
			smoothingFactor = g.smoothingFactor;
			break;
		}
		setAverageOnly(g.getAverageOnly());
		setTimeSeriesName(g.timeSeriesName);
		setDescription(g.getDescription());
		setMultiplier(g.getMultiplier());
		setAddend(g.getAddend());
		setValue(g.getValue());
		setMinimum(g.getMinimum());
		setMaximum(g.getMaximum());
		setOrientation(g.getOrientation());
		setBackground(g.getBackground());
		setForeground(g.getForeground());
		setOpaque(g.isOpaque());
		setFormat(g.getFormat());
		formatter = g.formatter;
		setPaintTicks(g.getPaintTicks());
		setPaintLabels(g.getPaintLabels());
		setPaintTitle(g.getPaintTitle());
		setMinorTicks(g.getMinorTicks());
		setMajorTicks(g.getMajorTicks());
		setPreferredSize(g.getPreferredSize());
		setBorderType(g.getBorderType());
		Model m = getModel();
		if (m != null) {
			m.addModelListener(this);
			if (!m.getRecorderDisabled())
				m.getMovie().addMovieListener(this);
		}
		setChangable(page.isEditable());
	}

	boolean isTargetClass() {
		return ComponentMaker.isTargetClass(modelClass);
	}

	private Model getModel() {
		return ComponentMaker.getModel(page, modelClass, modelID);
	}

	public String runScript(String script) {
		if (scripter == null)
			scripter = new BarGraphScripter(this);
		return scripter.runScript(script);
	}

	public String runScriptImmediately(String script) {
		return runScript(script);
	}

	public Object get(String variable) {
		return null;
	}

	public void destroy() {
		Model m = getModel();
		if (m != null) {
			m.removeModelListener(this);
			if (m.getMovie() != null)
				m.getMovie().removeMovieListener(this);
		}
		page = null;
		if (maker != null)
			maker.setObject(null);
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public void createPopupMenu() {

		if (popupMenu != null)
			return;

		popupMenu = new JPopupMenu();
		popupMenu.setInvoker(this);

		String s = Modeler.getInternationalText("CustomizeBarGraph");
		final JMenuItem miCustom = new JMenuItem((s != null ? s : "Customize This Bar Graph") + "...");
		miCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (maker == null) {
					maker = new PageBarGraphMaker(PageBarGraph.this);
				}
				else {
					maker.setObject(PageBarGraph.this);
				}
				maker.invoke(page);
			}
		});
		popupMenu.add(miCustom);

		s = Modeler.getInternationalText("RemoveBarGraph");
		final JMenuItem miRemove = new JMenuItem(s != null ? s : "Remove This Bar Graph");
		miRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page.removeComponent(PageBarGraph.this);
			}
		});
		popupMenu.add(miRemove);

		s = Modeler.getInternationalText("CopyBarGraph");
		JMenuItem mi = new JMenuItem(s != null ? s : "Copy This Bar Graph");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				page.copyComponent(PageBarGraph.this);
			}
		});
		popupMenu.add(mi);
		popupMenu.addSeparator();

		s = Modeler.getInternationalText("TakeSnapshot");
		mi = new JMenuItem((s != null ? s : Resource.get("JmolMenuBar_java_kuaizhao")) + "...");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SnapshotGallery.sharedInstance().takeSnapshot(page.getAddress(), PageBarGraph.this);
			}
		});
		popupMenu.add(mi);

		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				miCustom.setEnabled(changable);
				miRemove.setEnabled(changable);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
		});

		popupMenu.pack();
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

	public void setPage(Page p) {
		page = p;
	}

	public Page getPage() {
		return page;
	}

	public String getBorderType() {
		return BorderManager.getBorder(this);
	}

	public void setBorderType(String s) {
		BorderManager.setBorder(this, s, page.getBackground());
	}

	public void setInitialValue(double x) {
		initialValue = x;
	}

	public double getInitialValue() {
		return initialValue;
	}

	public void setAverageType(byte i) {
		averageType = i;
	}

	public byte getAverageType() {
		return averageType;
	}

	public void setSamplingPoints(int n) {
		samplingPoints = n;
	}

	public void setSmoothingFactor(float x) {
		smoothingFactor = x;
	}

	public void setModelClass(String s) {
		modelClass = s;
	}

	public String getModelClass() {
		return modelClass;
	}

	public void setModelID(int i) {
		modelID = i;
	}

	public int getModelID() {
		return modelID;
	}

	public void setTimeSeriesName(String s) {
		timeSeriesName = s;
	}

	public String getTimeSeriesName() {
		return timeSeriesName;
	}

	public void setMarked(boolean b) {
		marked = b;
		if (b) {
			originalBackground = getBackground();
			originalForeground = getForeground();
		}
		setBackground(b ? page.getSelectionColor() : originalBackground);
		setForeground(b ? page.getSelectedTextColor() : originalForeground);
	}

	public boolean isMarked() {
		return marked;
	}

	public void setChangable(boolean b) {
		changable = b;
	}

	public boolean isChangable() {
		return changable;
	}

	public void setPreferredSize(Dimension dim) {
		if (dim == null)
			throw new IllegalArgumentException("null object");
		if (dim.width == 0 || dim.height == 0)
			throw new IllegalArgumentException("zero dimension");
		super.setMaximumSize(dim);
		super.setMinimumSize(dim);
		super.setPreferredSize(dim);
	}

	public static PageBarGraph create(Page page) {
		if (page == null)
			return null;
		PageBarGraph bar = new PageBarGraph();
		bar.setBackground(page.getBackground());
		if (maker == null) {
			maker = new PageBarGraphMaker(bar);
		}
		else {
			maker.setObject(bar);
		}
		maker.invoke(page);
		if (maker.cancel)
			return null;
		return bar;
	}

	public void modelUpdate(ModelEvent e) {
		Object src = e.getSource();
		int id = e.getID();
		if (src instanceof Model) {
			if (id == ModelEvent.MODEL_CHANGED) {
				Model theModel = (Model) src;
				DataQueue q = theModel.getQueue(timeSeriesName);
				if (q != null && !q.isEmpty() && q.getPointer() > 0) {
					if (q instanceof FloatQueue) {
						setValue(((FloatQueue) q).getCurrentValue());
						switch (averageType) {
						case GROWING_POINT_RUNNING_AVERAGE:
							setAverage(((FloatQueue) q).getAverage());
							break;
						case EXPONENTIAL_RUNNING_AVERAGE:
							setAverage(((FloatQueue) q).getExponentialRunningAverage(smoothingFactor));
							break;
						case SIMPLE_RUNNING_AVERAGE:
							setAverage(((FloatQueue) q).getSimpleRunningAverage(samplingPoints));
							break;
						}
					}
				}
				repaint();
			}
			else if (id == ModelEvent.MODEL_RESET) {
				value = initialValue;
				setAverage(0);
				repaint();
			}
		}
	}

	public void frameChanged(MovieEvent e) {
		Model m = getModel();
		if (m == null)
			return;
		DataQueue q = m.getQueue(timeSeriesName);
		int frame = e.getFrame();
		if (q instanceof FloatQueue) {
			setValue(((FloatQueue) q).getData(frame));
			switch (averageType) {
			case EXPONENTIAL_RUNNING_AVERAGE:
				setAverage(((FloatQueue) q).getExponentialRunningAverage(smoothingFactor, frame));
				break;
			case SIMPLE_RUNNING_AVERAGE:
				setAverage(((FloatQueue) q).getSimpleRunningAverage(samplingPoints, frame));
			}
		}
		repaint();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("<class>" + getClass().getName() + "</class>\n");
		if (modelClass != null)
			sb.append("<modelclass>" + modelClass + "</modelclass>\n");
		sb.append("<model>" + getModelID() + "</model>\n");
		if (uid != null)
			sb.append("<uid>" + uid + "</uid>\n");
		sb.append("<timeseries>" + timeSeriesName + "</timeseries>\n");
		if (getDescription() != null && !getDescription().equals(timeSeriesName))
			sb.append("<description>" + XMLCharacterEncoder.encode(getDescription()) + "</description>\n");
		if (getOrientation() != VERTICAL)
			sb.append("<orientation>" + getOrientation() + "</orientation>\n");
		switch (averageType) {
		case SIMPLE_RUNNING_AVERAGE:
			sb.append("<datatype>" + averageType + "</datatype>\n");
			sb.append("<samplingpoints>" + samplingPoints + "</samplingpoints>\n");
			break;
		case EXPONENTIAL_RUNNING_AVERAGE:
			sb.append("<datatype>" + averageType + "</datatype>\n");
			sb.append("<smoothingfactor>" + smoothingFactor + "</smoothingfactor>\n");
			break;
		}
		if (getAverageOnly())
			sb.append("<average>" + getAverageOnly() + "</average>\n");
		if (multiplier != 1.0f)
			sb.append("<multiplier>" + multiplier + "</multiplier>\n");
		if (addend != 0.0f)
			sb.append("<addend>" + addend + "</addend>\n");
		if (!format.equals("Fixed point"))
			sb.append("<format>" + format + "</format>\n");
		if (getMaximumFractionDigits() != 3)
			sb.append("<max_fraction_digits>" + getMaximumFractionDigits() + "</max_fraction_digits>\n");
		if (getMaximumIntegerDigits() != 3)
			sb.append("<max_integer_digits>" + getMaximumIntegerDigits() + "</max_integer_digits>\n");
		sb.append("<width>" + getWidth() + "</width>\n");
		sb.append("<height>" + getHeight() + "</height>\n");
		sb.append("<tick>" + getPaintTicks() + "</tick>\n");
		sb.append("<major_tick>" + getMajorTicks() + "</major_tick>\n");
		sb.append("<nstep>" + getMinorTicks() + "</nstep>\n");
		sb.append("<label>" + getPaintLabels() + "</label>\n");
		sb.append("<title>" + getPaintTitle() + "</title>\n");
		if (!getBackground().equals(page.getBackground()))
			sb.append("<bgcolor>" + Integer.toString(getBackground().getRGB(), 16) + "</bgcolor>\n");
		if (!getForeground().equals(Color.black))
			sb.append("<fgcolor>" + Integer.toString(getForeground().getRGB(), 16) + "</fgcolor>\n");
		sb.append("<minimum>" + getMinimum() + "</minimum>\n");
		sb.append("<maximum>" + getMaximum() + "</maximum>\n");
		sb.append("<value>" + getValue() + "</value>\n");
		return sb.toString();
	}
}