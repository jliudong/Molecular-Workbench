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

package org.concord.mw2d.models;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.concord.modeler.util.BooleanQueue;
import org.concord.modeler.util.ByteQueue;
import org.concord.molbio.engine.Aminoacid;
import org.concord.mw2d.UserAction;
import org.concord.mw2d.ViewAttribute;
import org.concord.mw2d.ui.GrowthModeDialog;

import static org.concord.mw2d.models.Element.*;
import static org.concord.mw2d.models.Trigonometry.*;

public class Atom extends Particle {

	private transient MolecularModel model;
	private List<Electron> electrons;

	private boolean stateStored;
	private double rxUndo, ryUndo, vxUndo, vyUndo;

	int id;
	boolean radical = true;

	transient BooleanQueue radicalQ;
	transient ByteQueue excitationQ;

	/* van der Waals energy of this atom */
	double epsilon;

	/* van der Waals diameter of this atom */
	double sigma;

	/* the mass of this particle, default value = 10 atomic mass (120 g/mol) */
	double mass = 1.0f;

	private static Ellipse2D.Double circle;
	private Rectangle2D.Double bound;

	private String codon;

	/* Make the transient properties BML-transient: */
	static {
		try {
			BeanInfo info = Introspector.getBeanInfo(Atom.class);
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				String name = pd.getName();
				if ("color".equals(name) || "radicalPointer".equals(name)) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}
		}
		catch (Throwable e) {
		}
	}

	/**
	 * This method is provided for serialization. Do not call this method directly.
	 */
	public Atom() {
		super();
		circle = new Ellipse2D.Double();
		electrons = Collections.synchronizedList(new ArrayList<Electron>());
	}

	public Atom(Element e) {
		this();
		setElement(e);
	}

	public void setModel(MDModel m) {
		if (m != null && !(m instanceof AtomicModel))
			throw new IllegalArgumentException("wrong type of model");
		model = (MolecularModel) m;
		measuringTool.setModel(model);
		if (hasElectrons()) {
			synchronized (electrons) {
				for (Electron e : electrons)
					e.setModel(m);
			}
		}
	}

	boolean hasElectrons() {
		return !electrons.isEmpty();
	}

	public MDModel getHostModel() {
		return model;
	}

	public void storeCurrentState() {
		rxUndo = rx;
		ryUndo = ry;
		vxUndo = vx;
		vyUndo = vy;
		if (restraint != null)
			restraint.storeCurrentState();
		stateStored = true;
	}

	public void restoreState() {
		if (!stateStored)
			return;
		rx = rxUndo;
		ry = ryUndo;
		vx = vxUndo;
		vy = vyUndo;
		if (restraint != null)
			restraint.restoreState();
		if (velocitySelected())
			putVHotSpotAtVelocityTip();
	}

	/* used by ReactionModel */
	void storeCurrentVelocity() {
		vxUndo = vx;
		vyUndo = vy;
	}

	/* used by ReactionModel */
	void restoreVelocity() {
		vx = vxUndo;
		vy = vyUndo;
	}

	public void destroy() {
		if (hasElectrons()) {
			synchronized (electrons) {
				for (Electron e : electrons) {
					e.setModel(null);
					e.setAtom(null);
				}
			}
		}
		super.destroy();
		radicalQ = null;
		excitationQ = null;
		model = null;
	}

	public Rectangle2D getBounds2D() {
		if (bound == null)
			bound = new Rectangle2D.Double();
		bound.setRect(rx - 0.5 * sigma, ry - 0.5 * sigma, sigma, sigma);
		return bound;
	}

	private Rectangle2D getBounds2D(double d) {
		if (bound == null)
			bound = new Rectangle2D.Double();
		d = sigma - d;
		bound.setRect(rx - 0.5 * d, ry - 0.5 * d, d, d);
		return bound;
	}

	public double getMinX() {
		return rx - 0.5 * sigma;
	}

	public double getMinY() {
		return ry - 0.5 * sigma;
	}

	public double getMaxX() {
		return rx + 0.5 * sigma;
	}

	public double getMaxY() {
		return ry + 0.5 * sigma;
	}

	boolean near(Electron e, double distance) {
		return distanceSquare(e.rx, e.ry) < distance * distance;
	}

	public boolean contains(double x, double y) {
		return getShape().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getShape().contains(x, y, w, h);
	}

	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean intersects(double x, double y, double w, double h) {
		return getShape().intersects(x, y, w, h);
	}

	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/** compare the indices of atoms */
	public int compareTo(Object o) {
		if (!(o instanceof Atom))
			throw new IllegalArgumentException("Cannot compare with an object that is not an Atom");
		if (getIndex() < ((Atom) o).getIndex())
			return -1;
		if (getIndex() > ((Atom) o).getIndex())
			return 1;
		return 0;
	}

	public String toString() {
		return getIndex() + getName();
	}

	public void translateBy(double dx, double dy) {
		super.translateBy(dx, dy);
		if (model.view.getAction() == UserAction.VELO_ID)
			putVHotSpotAtVelocityTip();
	}

	public void translateTo(double x, double y) {
		super.translateTo(x, y);
		if (model.view.getAction() == UserAction.VELO_ID)
			putVHotSpotAtVelocityTip();
	}

	public void translateTo(Point2D p) {
		super.translateTo(p);
		if (model.view.getAction() == UserAction.VELO_ID)
			putVHotSpotAtVelocityTip();
	}

	public void erase() {
		super.erase();
		setRadical(true);
	}

	public void eraseProperties() {
		super.eraseProperties();
		setRadical(true);
	}

	/**
	 * This method copies fields from the specified atom to this one except the index.
	 */
	public void set(Particle target) {
		duplicate(target, false);
		copyRestraint(target.restraint);
		// setTapeMeasure(target.getTapeMeasure());
	}

	public void duplicate(Particle target, boolean copyLayers) {
		if (!(target instanceof Atom))
			throw new IllegalArgumentException("target must be an atom");
		super.duplicate(target, copyLayers);
		setElement(model.getElement(((Atom) target).id));
	}

	/**
	 * @return the identity number of the element of this atom.
	 * @see org.concord.mw2d.models.Element
	 */
	public int getID() {
		return id;
	}

	/**
	 * <b>Warning</b>: Do NOT call this method to set the type of atom. Call <tt>setElement</tt> instead. The existence
	 * of this method is for serialization.
	 * 
	 * @see org.concord.mw2d.models.Atom#setElement
	 */
	public void setID(int i) {
		id = i;
	}

	public boolean isExcited() {
		if (electrons.isEmpty())
			return false;
		return electrons.get(0).getEnergyLevelIndex() > 0;
	}

	public List<Electron> getElectrons() {
		return electrons;
	}

	public void addElectron(Electron e) {
		electrons.add(e);
	}

	public void removeElectron(Electron e) {
		electrons.remove(e);
	}

	public Electron getElectron(int i) {
		if (electrons.isEmpty())
			return null;
		if (i >= electrons.size())
			return null;
		return electrons.get(i);
	}

	public void resetElectronsToGroundState() {
		if (electrons.isEmpty())
			return;
		ElectronicStructure es = model.getElement(id).getElectronicStructure();
		EnergyLevel ground = es.getEnergyLevel(0);
		synchronized (electrons) {
			for (Electron e : electrons)
				e.setEnergyLevel(ground);
		}
	}

	Electron collideWithElectron(Electron incomer) {
		if (electrons.isEmpty()) // nothing to lose
			return null;
		Electron e = electrons.get(0);
		EnergyLevel level = e.getEnergyLevel();
		double excess = incomer.getKineticEnergy() + level.getEnergy();
		if (excess > 0) {
			// place the electron just barely inside the edge of the atom to prevent it from overlapping
			// with another atom immediately upon releasing
			double angle = Math.random() * Math.PI * 2;
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			e.rx = rx + 0.55 * sigma * cos;
			e.ry = ry + 0.55 * sigma * sin;
			// the incomer is stopped and all its kenetic energy is transfered to the electron
			incomer.vx = 0;
			incomer.vy = 0;
			double v = Math.sqrt(excess / (MDModel.EV_CONVERTER * Electron.mass));
			e.vx = v * cos;
			e.vy = v * sin;
			// detach the electron from the atom and make it a free electron
			e.setAtom(null);
			electrons.remove(e);
			// positively charge the ion that is left behind
			setCharge(1);
			return e;
		}
		return null;
	}

	/**
	 * get the DNA codon (triple-nucleotide letters on 5-3 strand), if this atom represents an amino acid.
	 */
	public String getCodon() {
		return codon;
	}

	/**
	 * <b>Warning</b>: You should always use <code>setElement</code> to set the type of atom. This method should be used
	 * to set ONLY the codon, because an amino acid maps to multiple codons.
	 */
	public void setCodon(String s) {
		codon = s;
	}

	public void setElement(Element e) {
		mass = e.getMass();
		sigma = e.getSigma();
		epsilon = e.getEpsilon();
		id = e.getID();
		if (isAminoAcid()) {
			codon = new String(Codon.getComplementaryCode(Aminoacid.getByAbbreviation(e.getName()).encode()));
			Aminoacid aa = Codon.expressFromDNA(codon.toCharArray());
			if (aa != null) {
				switch (model.aminoAcidNameStyle) {
				case MolecularModel.ABBREVIATION:
					setName(aa.getAbbreviation());
					break;
				case MolecularModel.ONE_LETTER:
					setName("" + aa.getLetter());
					break;
				}
				setCharge(aa.getCharge());
				setHydrophobicity((int) aa.getHydrophobicity());
			}
		}
		else if (isNucleotide() && id != ID_SP) {
			setName(e.getName());
			codon = null;
			charge = 0;
			hydrophobic = 0;
		}
		else {
			codon = null;
			name = null;
			// charge=0;
			hydrophobic = 0;
		}
		if (model != null)
			model.setUpdateParArray(true);
		if (isExcitable()) {
			if (electrons.isEmpty()) {
				Electron el = new Electron(this);
				el.setModel(model);
				el.setEnergyLevel(e.getElectronicStructure().getEnergyLevel(0));
				electrons.add(el);
			}
			else {
				synchronized (electrons) {
					for (Electron el : electrons)
						el.setEnergyLevel(e.getElectronicStructure().getEnergyLevel(0));
				}
			}
		}
		else {
			electrons.clear();
		}
	}

	/** only the 4 editable elements are allowed to have electrons and be excitable */
	public boolean isExcitable() {
		return id <= ID_CK;
	}

	public void setRadical(boolean b) {
		radical = b;
	}

	public boolean isRadical() {
		return radical;
	}

	public double getSigma() {
		return sigma;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public double getMass() {
		return mass;
	}

	public BooleanQueue getRadicalQ() {
		return radicalQ;
	}

	public ByteQueue getExcitationQ() {
		return excitationQ;
	}

	/**
	 * initialize radical queue. If the passed integer is less than 1, nullify the queue.
	 */
	public void initializeRadicalQ(int n) {
		if (radicalQ == null) {
			if (n < 1)
				return;
			radicalQ = new BooleanQueue("Radical: " + toString(), n);
			radicalQ.setInterval(getMovieInterval());
			radicalQ.setPointer(0);
			radicalQ.setCoordinateQueue(model.getModelTimeQueue());
		}
		else {
			radicalQ.setLength(n);
			if (n < 1) {
				radicalQ = null;
			}
			else {
				radicalQ.setPointer(0);
			}
		}
	}

	/** push current radical status into the radical queue */
	public synchronized void updateRadicalQ() {
		if (radicalQ == null || radicalQ.isEmpty())
			throw new RuntimeException("Attempt to write to the empty queue");
		radicalQ.update(radical);
	}

	public synchronized int getRadicalPointer() {
		if (radicalQ == null || radicalQ.isEmpty())
			return -1;
		return radicalQ.getPointer();
	}

	public synchronized void moveRadicalPointer(int i) {
		if (radicalQ == null || radicalQ.isEmpty())
			return;
		radicalQ.setPointer(i);
	}

	/**
	 * initialize excitation queue. If the passed integer is less than 1, nullify the queue.
	 */
	public void initializeExcitationQ(int n) {
		if (electrons.isEmpty())
			return;
		if (excitationQ == null) {
			if (n < 1)
				return;
			excitationQ = new ByteQueue("Excitation: " + toString(), n);
			excitationQ.setInterval(getMovieInterval());
			excitationQ.setPointer(0);
			excitationQ.setCoordinateQueue(model.getModelTimeQueue());
		}
		else {
			excitationQ.setLength(n);
			if (n < 1) {
				excitationQ = null;
			}
			else {
				excitationQ.setPointer(0);
			}
		}
	}

	/** push current excitation status into the excitation queue */
	public synchronized void updateExcitationQ() {
		if (electrons.isEmpty())
			return;
		if (excitationQ == null || excitationQ.isEmpty())
			throw new RuntimeException("Attempt to write to the empty queue");
		Electron e = electrons.get(0);
		EnergyLevel level = e.getEnergyLevel();
		ElectronicStructure es = model.getElement(id).getElectronicStructure();
		byte m = (byte) es.indexOf(level);
		excitationQ.update(m);
	}

	public synchronized int getExcitationPointer() {
		if (excitationQ == null || excitationQ.isEmpty())
			return -1;
		return excitationQ.getPointer();
	}

	public synchronized void moveExcitationPointer(int i) {
		if (excitationQ == null || excitationQ.isEmpty())
			return;
		excitationQ.setPointer(i);
	}

	public boolean intersects(Atom atom, boolean noOverlapTolerance) {
		if (noOverlapTolerance) {
			return distanceSquare(atom) < 0.25 * (sigma + atom.sigma) * (sigma + atom.sigma);
			// return getBounds2D().intersects(atom.getBounds2D());
		}
		return getBounds2D(0.5 * sigma).intersects(atom.getBounds2D(0.5 * atom.sigma));
	}

	/* predict this atom's new state using 2nd order Taylor expansion */
	void predict(double dt, double dt2) {
		if (!movable)
			return;
		dx = vx * dt + ax * dt2;
		dy = vy * dt + ay * dt2;
		rx += dx;
		ry += dy;
		vx += ax * dt;
		vy += ay * dt;
	}

	/*
	 * correct the position predicted by the <tt>predict</tt> method. <b>Important</b>: <tt>fx, fy</tt> were used in the
	 * force calculation routine to store the new acceleration data. <tt>ax, ay</tt> were used to hold the old
	 * acceleration data before calling this method. After calling this method, new acceleration data will be assigned
	 * to <tt>ax, ay</tt>, whereas the forces and torques to <tt>fx, fy</tt>. <b>Be aware</b>: the acceleration and
	 * force properties of a particle are correct ONLY after this correction method has been called.
	 * 
	 * @param half half of the time increment
	 */
	void correct(double half) {
		if (movable) {
			vx += half * (fx - ax);
			vy += half * (fy - ay);
		}
		ax = fx;
		ay = fy;
		fx *= mass;
		fy *= mass;
	}

	/** set the selection state of this atom */
	public void setSelected(boolean b) {
		super.setSelected(b);
		if (b) {
			model.view.setSelectedComponent(this);
			if (model.view.getAction() == UserAction.VELO_ID)
				putVHotSpotAtVelocityTip();
		}
	}

	/** @return the bounding box of this atom with a specified skin */
	public Rectangle getBounds(int skin) {
		int x0 = (int) (rx - 0.5 * sigma) - skin;
		int y0 = (int) (ry - 0.5 * sigma) - skin;
		int d = (int) sigma + skin + skin;
		return SwingUtilities.computeIntersection(0, 0, model.view.getWidth(), model.view.getHeight(), new Rectangle(x0, y0, d, d));
	}

	private Ellipse2D getShape() {
		if (circle == null)
			circle = new Ellipse2D.Double();
		if (model.view.getVdwPercentage() == 100) {
			circle.setFrame(rx - 0.5 * sigma, ry - 0.5 * sigma, sigma, sigma);
		}
		else {
			double x = model.view.getVdwPercentage() * 0.01;
			circle.setFrame(rx - 0.5 * x * sigma, ry - 0.5 * x * sigma, x * sigma, x * sigma);
		}
		return circle;
	}

	/** return true if this atom represents an amino acid */
	public boolean isAminoAcid() {
		return id >= ID_ALA && id <= ID_VAL;
	}

	/** return true if this atom represents a nucleotide */
	public boolean isNucleotide() {
		return (id >= ID_A && id <= ID_U) || id == ID_SP;
	}

	/**
	 * @return the name of this atom, or the name of the amino acid or nucleotide it represents.
	 */
	public String getName() {
		switch (id) {
		case ID_NT:
			return name == null ? "Nt" : name;
		case ID_PL:
			return name == null ? "Pl" : name;
		case ID_WS:
			return name == null ? "Ws" : name;
		case ID_CK:
			return name == null ? "Ck" : name;
		case ID_MO:
			return name == null ? "Mo" : name;
		case ID_SP:
			return name == null ? "Sp" : name;
		}
		return super.getName();
	}

	/**
	 * get the background color of this atom. If the <code>setColor()</code> method has not been called, return the
	 * default background color for this type of element.
	 */
	public Color getColor() {
		return color != null ? color : new Color(model.view.getColor(this));
	}

	/**
	 * attach a random amino acid to this one if it has not been bonded yet and it represents an amino acid.
	 * 
	 * @return true if an amino acid has been successfully attached.
	 */
	public boolean attachRandomAminoAcid() {
		if (!isAminoAcid())
			return false;
		if (isBonded())
			return false;
		return attachAminoAcid(Math.round(ID_ALA + (float) Math.random() * 19));
	}

	/**
	 * attach a specified amino acid to this one if it has not been bonded yet and it represents an amino acid.
	 * 
	 * @return true if an amino acid has been successfully attached.
	 */
	public boolean attachAminoAcid(int id) {
		if (!isAminoAcid())
			return false;
		if (isBonded())
			return false;
		Element e = model.getElement(id);
		double d = RadialBond.PEPTIDE_BOND_LENGTH_PARAMETER * (getSigma() + e.getSigma());
		if (GrowthModeDialog.getMode() == GrowthModeDialog.ZIGZAG) {
			if (model.view.insertAnAtom(rx + d * COS120, ry + d * SIN120, id, true)
					|| model.view.insertAnAtom(rx + d * COS240, ry + d * SIN240, id, true)) {
				model.bonds.add(new RadialBond.Builder(this, model.atom[model.getNumberOfAtoms() - 1]).bondLength(d).build());
				MoleculeCollection.sort(model);
				return true;
			}
		}
		else if (GrowthModeDialog.getMode() == GrowthModeDialog.SPIRAL) {
			if (model.view.insertAnAtom(rx - d, ry, id, true) || model.view.insertAnAtom(rx + d * COS120, ry + d * SIN120, id, true)
					|| model.view.insertAnAtom(rx + d * COS60, ry + d * SIN60, id, true)
					|| model.view.insertAnAtom(rx + d * COS240, ry + d * SIN240, id, true)
					|| model.view.insertAnAtom(rx + d * COS300, ry + d * SIN300, id, true)) {
				model.bonds.add(new RadialBond.Builder(this, model.atom[model.getNumberOfAtoms() - 1]).bondLength(d).build());
				MoleculeCollection.sort(model);
				return true;
			}
		}
		return false;
	}

	/** @return the kinetic energy of this atom in electronic volt (eV) */
	public double getKineticEnergy() {
		// the prefactor 0.5 doesn't show up here because of mass unit conversion.
		return (vx * vx + vy * vy) * mass * MDModel.EV_CONVERTER;
	}

	/** return true if this atom is bonded. An Atom is considered bonded if it is contained in a Molecule. */
	public boolean isBonded() {
		MoleculeCollection mc = model.getMolecules();
		int n = mc.size();
		if (n <= 0)
			return false;
		Molecule m = null;
		synchronized (mc.getSynchronizationLock()) {
			for (int i = 0; i < n; i++) {
				m = mc.get(i);
				if (m != null && m.contains(this))
					return true;
			}
		}
		return false;
	}

	boolean outOfView() {
		return rx + 0.5 * sigma < 0 || ry + 0.5 * sigma < 0 || rx - 0.5 * sigma > model.view.getWidth() || ry - 0.5 * sigma > model.view.getHeight();
	}

	public void render(Graphics2D g) {

		if (outOfView())
			return;

		if (!model.view.getShowSites() && id == Element.getMolecularObjectElement())
			return;

		if (selected && model.view.getShowSelectionHalo()) {
			g.setStroke(ViewAttribute.THIN_DASHED);
			g.setColor(model.view.contrastBackground());
			circle.setFrame(rx - 0.5 * sigma - 2, ry - 0.5 * sigma - 2, sigma + 4, sigma + 4);
			g.draw(circle);
		}

		if (isVisible()) {

			if (!model.view.getUseJmol() || index >= model.numberOfAtoms) {
				if (circle == null)
					circle = new Ellipse2D.Double();
				circle.setFrame(rx - 0.5 * sigma, ry - 0.5 * sigma, sigma, sigma);
				if (marked) {
					g.setColor(model.view.getMarkColor());
				}
				else if (model.view.shadingShown()) {
					g.setColor(model.view.getKeShadingColor((vx * vx + vy * vy) * mass));
				}
				else if (model.view.chargeShadingShown()) {
					g.setColor(model.view.getChargeShadingColor(charge));
				}
				else {
					g.setColor(color != null ? color : getColor());
				}
				g.fill(circle);
				g.setColor(model.view.contrastBackground());
				g.setStroke(index < model.numberOfAtoms ? ViewAttribute.THIN : ViewAttribute.THIN_DOTTED);
				g.draw(circle);
			}

			if (model.view.getShowParticleIndex() || isAminoAcid() || isNucleotide()) {
				g.setFont(FONT_ON_TOP);
				g.setColor(Color.black);
				String s = model.view.getShowParticleIndex() ? "" + getIndex() : getName();
				if (!"sp".equalsIgnoreCase(s) && s != null) {
					FontMetrics fm = g.getFontMetrics();
					g.drawString(s, (int) (rx - 0.5 * fm.stringWidth(s)), (int) (ry + 0.4 * fm.getHeight()));
				}
			}

			if (model.view.getDrawCharge() && !isAminoAcid() && !isNucleotide()) {
				g.setColor(chargeColor);
				g.setStroke(ViewAttribute.MODERATE);
				if (charge > ZERO) {
					g.drawLine((int) rx, (int) (ry - 4.0), (int) rx, (int) (ry + 4.0));
					g.drawLine((int) (rx - 4.0), (int) ry, (int) (rx + 4.0), (int) ry);
				}
				else if (charge < -ZERO) {
					g.drawLine((int) (rx - 4.0), (int) ry, (int) (rx + 4.0), (int) ry);
				}
			}

			if (restraint != null)
				PointRestraint.render(g, this);
			if (userField != null)
				userField.render(g, this, model.getMovie().getCurrentFrameIndex() >= model.getTapePointer() - 1);

			if (model.view.excitationShown() && model.isSubatomicEnabled()) {
				if (hasElectrons()) {
					Electron e = electrons.get(0);
					if (e.getEnergyLevel() != null) {
						ElectronicStructure es = model.getElement(id).getElectronicStructure();
						if (es.indexOf(e.getEnergyLevel()) != 0) {
							g.setColor(model.view.contrastBackground());
							g.setStroke(ViewAttribute.DASHED);
							g.drawOval((int) (rx - 0.7 * sigma), (int) (ry - 0.7 * sigma), (int) (1.4 * sigma), (int) (1.4 * sigma));
						}
					}
				}
			}

		}

		// show the trail even if the atom has to set to be invisible. If we want no trail, we can just
		// hide it using the method related to show/hide trajectory.
		if (showRTraj)
			renderRTraj(g);

		// an invisible atom should be able to blink
		if (isBlinking()) {
			g.setColor(blinkColor);
			g.setStroke(ViewAttribute.DASHED);
			g.drawOval((int) Math.round(rx - 0.7 * sigma), (int) Math.round(ry - 0.7 * sigma), (int) Math.round(1.4 * sigma),
					(int) Math.round(1.4 * sigma));
		}

	}

}