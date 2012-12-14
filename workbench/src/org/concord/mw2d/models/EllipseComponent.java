/*
 *   Copyright (C) 2007  The Concord Consortium, Inc.,
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

import org.concord.modeler.draw.AbstractEllipse;
import org.concord.modeler.draw.FillMode;
import org.concord.modeler.draw.LineStyle;
import org.concord.mw2d.MDView;

public class EllipseComponent extends AbstractEllipse implements ModelComponent, Layered, FieldArea {

	private boolean blinking, marked;
	private int layer = IN_FRONT_OF_PARTICLES;
	private MDModel model;
	private float savedX = -1, savedY = -1, savedW = -1, savedH = -1;
	private boolean stateStored;
	private ModelComponent host;
	private VectorField vectorField;
	private boolean visible = true;
	private boolean draggable = true;
	private float viscosity;
	private float photonAbsorption;
	private float electronAbsorption;
	private boolean reflection;

	// a Gaussian potential can be applied: phi(x,y) = phi0 * exp{-gamma*{[(x-x0)/a]^2+[(y-y0)/b]^2}}
	private float phi0; // potential at the center
	private float gamma = 1; // how fast does the potential decline: phi0 -> phi0/exp(gamma) from center to edge

	public EllipseComponent() {
		super();
		setLineColor(Color.black);
		setLineWeight((byte) 1);
	}

	public EllipseComponent(EllipseComponent e) {
		this();
		setOval(e);
		setFillMode(e.getFillMode());
		setAlpha(e.getAlpha());
		setAlphaAtCenter(e.getAlphaAtCenter());
		setAlphaAtEdge(e.getAlphaAtEdge());
		setLineStyle(e.getLineStyle());
		setLineWeight(e.getLineWeight());
		setLineColor(e.getLineColor());
		setLayer(e.layer);
		setModel(e.model);
		setPotentialAtCenter(e.phi0);
		setPotentialDecayFactor(e.gamma);
		setReflection(e.reflection);
		setViscosity(e.viscosity);
		setPhotonAbsorption(e.photonAbsorption);
		setElectronAbsorption(e.electronAbsorption);
		setVectorField(VectorFieldFactory.getCopy(e.vectorField));
	}

	public void set(Delegate d) {
		setVisible(d.visible);
		setDraggable(d.draggable);
		setOval((float) d.x, (float) d.y, d.w, d.h);
		setAngle(d.angle);
		setAlpha(d.alpha);
		setAlphaAtCenter(d.getAlphaAtCenter());
		setAlphaAtEdge(d.getAlphaAtEdge());
		setFillMode(d.getFillMode());
		setLineColor(d.getLineColor());
		setLineStyle((byte) d.getLineStyle());
		setLineWeight((byte) d.getLineWeight());
		setLayer(d.getLayer());
		String s = d.getHostType();
		if (s != null) {
			int index = d.getHostIndex();
			if (s.endsWith("Atom")) {
				if (model instanceof MolecularModel) {
					setHost(((MolecularModel) model).getAtom(index));
				}
			}
			else if (s.endsWith("GayBerneParticle")) {
				if (model instanceof MesoModel) {
					setHost(((MesoModel) model).getParticle(index));
				}
			}
			else if (s.endsWith("Obstacle")) {
				setHost(model.getObstacles().get(index));
			}
		}
		setPotentialAtCenter(d.phi0);
		setPotentialDecayFactor(d.gamma);
		setReflection(d.reflection);
		setViscosity(d.viscosity);
		setPhotonAbsorption(d.photonAbsorption);
		setElectronAbsorption(d.electronAbsorption);
		setVectorField(d.vectorField);
	}

	public void storeCurrentState() {
		savedX = getX();
		savedY = getY();
		savedW = getWidth();
		savedH = getHeight();
		stateStored = true;
		HostStateManager.storeCurrentState(host);
	}

	public void restoreState() {
		if (!stateStored)
			return;
		setOval(savedX, savedY, savedW, savedH);
		HostStateManager.restoreState(host);
	}

	/** TODO */
	public void blink() {
	}

	public void destroy() {
		model = null;
		host = null;
	}

	public boolean absorb(Photon p) {
		if (model instanceof AtomicModel) {
			if (contains(p.x, p.y))
				return photonAbsorption > Math.random();
		}
		return false;
	}

	public boolean absorb(Electron e) {
		if (model instanceof AtomicModel) {
			if (contains(e.rx, e.ry))
				return electronAbsorption > Math.random();
		}
		return false;
	}

	public void interact(Particle p) {
		if (contains(p.rx, p.ry)) {
			if (viscosity > Particle.ZERO) {
				double dmp = MDModel.GF_CONVERSION_CONSTANT * viscosity / p.getMass();
				p.fx -= dmp * p.vx;
				p.fy -= dmp * p.vy;
			}
			if (reflection) {
				if (p instanceof Atom)
					internalReflection((Atom) p);
			}
		}
		else {
			if (reflection) {
				if (p instanceof Atom)
					externalReflection((Atom) p);
			}
		}
		if (phi0 != 0) {
			double dx = 2 * (p.rx - getRx()) / getWidth();
			double dy = 2 * (p.ry - getRy()) / getHeight();
			double v = phi0 * Math.exp(-gamma * (dx * dx + dy * dy));
			double c = MDModel.GF_CONVERSION_CONSTANT * v * gamma * 2;
			p.fx += c * dx * 2 / getWidth();
			p.fy += c * dy * 2 / getHeight();
		}
	}

	private void internalReflection(Atom a) {
	}

	private void externalReflection(Atom a) {
	}

	public int getParticleCount() {
		int n = model.getNumberOfParticles();
		Particle p;
		int m = 0;
		for (int i = 0; i < n; i++) {
			p = model.getParticle(i);
			if (contains(p.rx, p.ry))
				m++;
		}
		return m;
	}

	public void setPotentialAtCenter(float phi0) {
		this.phi0 = phi0;
	}

	public float getPotentialAtCenter() {
		return phi0;
	}

	public void setPotentialDecayFactor(float gamma) {
		if (gamma <= 0)
			throw new IllegalArgumentException("The decay factor gamma must be a positive number: " + gamma);
		this.gamma = gamma;
	}

	public float getPotentialDecayFactor() {
		return gamma;
	}

	public void setReflection(boolean b) {
		reflection = b;
	}

	public boolean getReflection() {
		return reflection;
	}

	public void setPhotonAbsorption(float photonAbsorption) {
		this.photonAbsorption = photonAbsorption;
	}

	public float getPhotonAbsorption() {
		return photonAbsorption;
	}

	public void setElectronAbsorption(float electronAbsorption) {
		this.electronAbsorption = electronAbsorption;
	}

	public float getElectronAbsorption() {
		return electronAbsorption;
	}

	public void setViscosity(float viscosity) {
		if (viscosity < 0)
			throw new IllegalArgumentException("viscosity cannot be negative");
		this.viscosity = viscosity;
	}

	public float getViscosity() {
		return viscosity;
	}

	public void setVectorField(VectorField vectorField) {
		if (model != null) {
			model.fields.remove(this.vectorField);
			if (vectorField != null)
				model.fields.add(vectorField);
		}
		this.vectorField = vectorField;
		if (this.vectorField != null)
			this.vectorField.setBounds(getBounds());
	}

	public VectorField getVectorField() {
		return vectorField;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setDraggable(boolean b) {
		draggable = b;
	}

	public boolean isDraggable() {
		return draggable;
	}

	/** set a model component this line attaches to. */
	public void setHost(ModelComponent mc) {
		if (mc != null)
			storeCurrentState();
		host = mc;
	}

	/** get a model component this line attaches to. */
	public ModelComponent getHost() {
		return host;
	}

	protected void attachToHost() {
		if (host == null)
			return;
		setLocation(host.getRx(), host.getRy());
	}

	public void setSelected(boolean b) {
		super.setSelected(b);
		if (b) {
			try {
				((MDView) model.getView()).setSelectedComponent(this);
				setSelectionDrawn(((MDView) model.getView()).getShowSelectionHalo());
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public void setMarked(boolean b) {
		marked = b;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setBlinking(boolean b) {
		blinking = b;
	}

	public boolean isBlinking() {
		return blinking;
	}

	public void setLayer(int i) {
		layer = i;
	}

	public int getLayer() {
		return layer;
	}

	public void setModel(MDModel model) {
		this.model = model;
		setComponent(model.getView());
	}

	public MDModel getHostModel() {
		return model;
	}

	public static class Delegate extends LayeredComponentDelegate {

		private Color lineColor = Color.black;
		private int lineWeight = 1;
		private int lineStyle = LineStyle.STROKE_NUMBER_1;
		private float w, h;
		private short alpha = 255;
		private short alphaAtCenter = 255;
		private short alphaAtEdge = 255;
		private float angle;
		private FillMode fillMode = FillMode.getNoFillMode();
		private float viscosity;
		private float photonAbsorption;
		private float electronAbsorption;
		private VectorField vectorField;
		private boolean reflection;
		private float phi0;
		private float gamma = 1;

		public Delegate() {
		}

		public Delegate(EllipseComponent e) {
			if (e == null)
				throw new IllegalArgumentException("arg can't be null");
			x = e.getX();
			y = e.getY();
			w = e.getWidth();
			h = e.getHeight();
			angle = e.getAngle();
			alpha = e.getAlpha();
			alphaAtCenter = e.getAlphaAtCenter();
			alphaAtEdge = e.getAlphaAtEdge();
			fillMode = e.getFillMode();
			lineColor = e.getLineColor();
			lineWeight = e.getLineWeight();
			lineStyle = e.getLineStyle();
			layer = e.getLayer();
			layerPosition = (byte) ((MDView) e.getHostModel().getView()).getLayerPosition(e);
			if (e.getHost() != null) {
				hostType = e.getHost().getClass().toString();
				if (e.getHost() instanceof Particle) {
					hostIndex = ((Particle) e.getHost()).getIndex();
				}
				else if (e.getHost() instanceof RectangularObstacle) {
					hostIndex = e.getHostModel().getObstacles().indexOf(e.getHost());
				}
			}
			phi0 = e.getPotentialAtCenter();
			gamma = e.getPotentialDecayFactor();
			reflection = e.getReflection();
			viscosity = e.getViscosity();
			photonAbsorption = e.getPhotonAbsorption();
			electronAbsorption = e.getElectronAbsorption();
			vectorField = e.getVectorField();
			draggable = e.draggable;
			visible = e.visible;
		}

		public void setPotentialAtCenter(float phi0) {
			this.phi0 = phi0;
		}

		public float getPotentialAtCenter() {
			return phi0;
		}

		public void setPotentialDecayFactor(float gamma) {
			this.gamma = gamma;
		}

		public float getPotentialDecayFactor() {
			return gamma;
		}

		public void setReflection(boolean b) {
			reflection = b;
		}

		public boolean getReflection() {
			return reflection;
		}

		public void setViscosity(float viscosity) {
			this.viscosity = viscosity;
		}

		public float getViscosity() {
			return viscosity;
		}

		public void setPhotonAbsorption(float photonAbsorption) {
			this.photonAbsorption = photonAbsorption;
		}

		public float getPhotonAbsorption() {
			return photonAbsorption;
		}

		public void setElectronAbsorption(float electronAbsorption) {
			this.electronAbsorption = electronAbsorption;
		}

		public float getElectronAbsorption() {
			return electronAbsorption;
		}

		public void setVectorField(VectorField vectorField) {
			this.vectorField = vectorField;
		}

		public VectorField getVectorField() {
			return vectorField;
		}

		public void setAlpha(short i) {
			alpha = i;
		}

		public short getAlpha() {
			return alpha;
		}

		public void setAlphaAtCenter(short i) {
			alphaAtCenter = i;
		}

		public short getAlphaAtCenter() {
			return alphaAtCenter;
		}

		public void setAlphaAtEdge(short i) {
			alphaAtEdge = i;
		}

		public short getAlphaAtEdge() {
			return alphaAtEdge;
		}

		public void setFillMode(FillMode c) {
			fillMode = c;
		}

		public FillMode getFillMode() {
			return fillMode;
		}

		public void setWidth(float w) {
			this.w = w;
		}

		public float getWidth() {
			return w;
		}

		public void setHeight(float h) {
			this.h = h;
		}

		public float getHeight() {
			return h;
		}

		public void setAngle(float angle) {
			this.angle = angle;
		}

		public float getAngle() {
			return angle;
		}

		public void setLineColor(Color c) {
			lineColor = c;
		}

		public Color getLineColor() {
			return lineColor;
		}

		public void setLineStyle(int i) {
			lineStyle = i;
		}

		public int getLineStyle() {
			return lineStyle;
		}

		public void setLineWeight(int i) {
			lineWeight = i;
		}

		public int getLineWeight() {
			return lineWeight;
		}

	}

}