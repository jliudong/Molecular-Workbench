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

package org.concord.mw3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.concord.modeler.ui.HyperlinkLabel;
import org.concord.modeler.ui.IconPool;
import org.concord.modeler.ui.PastableTextArea;
import org.concord.modeler.ui.FloatNumberTextField;
import org.concord.mw3d.models.MolecularModel;

class ModelProperties extends JDialog {

	private MolecularModel model;
	private JTextArea scriptArea;
	private FloatNumberTextField stepField;
	private JLabel lengthLabel, widthLabel, heightLabel;
	private JLabel atomCountLabel, rbondCountLabel, abondCountLabel, tbondCountLabel, moleculeCountLabel;
	private JTabbedPane tabbedPane;
	private JLabel gLabel, eLabel, bLabel;
	private JPanel scriptPanel;

	public ModelProperties(Frame owner, MolecularModel m) {

		super(owner, "Model Properties", false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		String s = MolecularContainer.getInternationalText("Properties");
		if (s != null)
			setTitle(s);

		model = m;

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		s = MolecularContainer.getInternationalText("OK");
		JButton button = new JButton(s != null ? s : "OK");
		button.setMnemonic(KeyEvent.VK_O);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirm();
				dispose();
			}
		});
		panel.add(button);

		s = MolecularContainer.getInternationalText("Cancel");
		button = new JButton(s != null ? s : "Cancel");
		button.setMnemonic(KeyEvent.VK_C);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(button);

		getContentPane().add(panel, BorderLayout.SOUTH);

		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		/* objects */

		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory
				.createEmptyBorder(8, 8, 8, 8)));
		s = MolecularContainer.getInternationalText("General");
		tabbedPane.add(s != null ? s : "General", panel);

		JPanel p = new JPanel(new GridLayout(6, 3, 2, 2));
		panel.add(p, BorderLayout.NORTH);

		s = MolecularContainer.getInternationalText("NumberOfAtoms");
		p.add(new JLabel(s != null ? s : "Number of Atoms", SwingConstants.LEFT));
		atomCountLabel = new JLabel(Integer.toString(model.getAtomCount()));
		p.add(atomCountLabel);
		p.add(new JLabel("Maximum: " + MolecularModel.SIZE));

		s = MolecularContainer.getInternationalText("NumberOfMolecules");
		p.add(new JLabel(s != null ? s : "Number of Molecules", SwingConstants.LEFT));
		moleculeCountLabel = new JLabel(model.getMoleculeCount() + "");
		p.add(moleculeCountLabel);
		p.add(new JLabel("Based on covalent bonds"));

		s = MolecularContainer.getInternationalText("Length");
		p.add(new JLabel(s != null ? s : "Length", SwingConstants.LEFT));
		lengthLabel = new JLabel("" + model.getLength());
		p.add(lengthLabel);
		p.add(new JLabel(" \u00c5"));

		s = MolecularContainer.getInternationalText("Width");
		p.add(new JLabel(s != null ? s : "Width", SwingConstants.LEFT));
		widthLabel = new JLabel("" + model.getWidth());
		p.add(widthLabel);
		p.add(new JLabel(" \u00c5"));

		s = MolecularContainer.getInternationalText("Height");
		p.add(new JLabel(s != null ? s : "Height", SwingConstants.LEFT));
		heightLabel = new JLabel("" + model.getHeight());
		p.add(heightLabel);
		p.add(new JLabel(" \u00c5"));

		s = MolecularContainer.getInternationalText("TimeStep");
		p.add(new JLabel(s != null ? s : "MD Time Steplength", SwingConstants.LEFT));
		stepField = new FloatNumberTextField(model.getTimeStep(), 0.00001f, 5, 8);
		p.add(stepField);
		stepField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setTimeStep(stepField.getValue());
				model.notifyChange();
			}
		});
		p.add(new JLabel(" Femtosecond"));

		/* interations */

		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory
				.createEmptyBorder(8, 8, 8, 8)));
		s = MolecularContainer.getInternationalText("Interaction");
		tabbedPane.add(s != null ? s : "Interactions", panel);

		p = new JPanel(new GridLayout(7, 2, 2, 2));
		panel.add(p, BorderLayout.NORTH);

		s = MolecularContainer.getInternationalText("GenericParticleProperties");
		JLabel label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>"
				+ (s != null ? s : "Generic Particle Properties") + "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to customize the properties of the generic particles");
		label.setIcon(new ImageIcon(MolecularContainer.class.getResource("resources/GenericParticle.gif")));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
				String s = MolecularContainer.getInternationalText("GenericParticleProperties");
				final JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(model.getView()), s != null ? s
						: "Generic Particle Properties", true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				final GenericParticlePropertiesPanel p = new GenericParticlePropertiesPanel(model);
				p.setDialog(dialog);
				dialog.setContentPane(p);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						p.destroy();
						dialog.dispose();
					}

					public void windowActivated(WindowEvent e) {
						p.windowActivated();
					}
				});
				dialog.pack();
				dialog.setLocationRelativeTo(model.getView());
				dialog.setVisible(true);
			}
		});
		p.add(label);
		p.add(new JPanel());

		s = MolecularContainer.getInternationalText("GravitationalFieldEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Gravitational Field Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Gravity Editor");
		label.setIcon(new ImageIcon(MolecularContainer.class.getResource("resources/gravity.gif")));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
				GFieldEditor gFieldEditor = new GFieldEditor(ModelProperties.this, model);
				gFieldEditor.setLocationRelativeTo(ModelProperties.this);
				gFieldEditor.setVisible(true);
				gLabel.setText(model.getGField() != null ? "On" : "Off");
			}
		});
		p.add(label);
		gLabel = new JLabel(model.getGField() != null ? "On" : "Off");
		p.add(gLabel);

		s = MolecularContainer.getInternationalText("ElectricFieldEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Electric Field Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Electric Field Editor");
		label.setIcon(new ImageIcon(MolecularContainer.class.getResource("resources/electric.gif")));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
				EFieldEditor eFieldEditor = new EFieldEditor(ModelProperties.this, model);
				eFieldEditor.setLocationRelativeTo(ModelProperties.this);
				eFieldEditor.setVisible(true);
				eLabel.setText(model.getEField() != null ? "On" : "Off");
			}
		});
		p.add(label);
		eLabel = new JLabel(model.getEField() != null ? "On" : "Off");
		p.add(eLabel);

		s = MolecularContainer.getInternationalText("MagneticFieldEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Magnetic Field Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Magnetic Field Editor");
		label.setIcon(new ImageIcon(MolecularContainer.class.getResource("resources/magnetic.gif")));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
				BFieldEditor bFieldEditor = new BFieldEditor(ModelProperties.this, model);
				bFieldEditor.setLocationRelativeTo(ModelProperties.this);
				bFieldEditor.setVisible(true);
				bLabel.setText(model.getBField() != null ? "On" : "Off");
			}
		});
		p.add(label);
		bLabel = new JLabel(model.getBField() != null ? "On" : "Off");
		p.add(bLabel);

		s = MolecularContainer.getInternationalText("RadialBondEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Radial Bond Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Radial Bond Editor");
		label.setIcon(IconPool.getIcon("radial bond"));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
			}
		});
		p.add(label);
		rbondCountLabel = new JLabel(Integer.toString(model.getRBondCount()));
		p.add(rbondCountLabel);

		s = MolecularContainer.getInternationalText("AngularBondEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Angular Bond Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Angular Bond Editor");
		label.setIcon(IconPool.getIcon("angular bond"));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
			}
		});
		p.add(label);
		abondCountLabel = new JLabel(Integer.toString(model.getABondCount()));
		p.add(abondCountLabel);

		s = MolecularContainer.getInternationalText("TorsionalBondEditor");
		label = new HyperlinkLabel("<html><font color=\"#0000ff\"><u>" + (s != null ? s : "Torsional Bond Editor")
				+ "</u></font></html>", SwingConstants.LEFT);
		label.setToolTipText("Click to open the Torsional Bond Editor");
		label.setIcon(new ImageIcon(MolecularContainer.class.getResource("resources/torsionbond.gif")));
		((HyperlinkLabel) label).setAction(new Runnable() {
			public void run() {
			}
		});
		p.add(label);
		tbondCountLabel = new JLabel(Integer.toString(model.getTBondCount()));
		p.add(tbondCountLabel);

		/* script */

		scriptPanel = new JPanel(new BorderLayout(2, 2));
		scriptPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory
				.createEmptyBorder(8, 8, 8, 8)));
		s = MolecularContainer.getInternationalText("Script");
		tabbedPane.add(s != null ? s : "Script", scriptPanel);

		p = new JPanel(new BorderLayout(10, 10));
		p.setPreferredSize(new Dimension(500, 300));
		s = MolecularContainer.getInternationalText("ScriptToRunAfterLoadingModel");
		p.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), s != null ? s
				: "Script to run right after loading model", 0, 0));
		scriptPanel.add(p, BorderLayout.CENTER);

		scriptArea = new PastableTextArea(model.getInitializationScript());
		JScrollPane scrollPane = new JScrollPane(scriptArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		p.add(scrollPane, BorderLayout.CENTER);

		s = MolecularContainer.getInternationalText("Caution");
		p
				.add(
						new JLabel(
								"<html><body><font size=2><b>"
										+ (s != null ? s : "Caution")
										+ ":</b><br>(a) Long-running scripts can interfer with authoring.<br>(b) Don't use motion scripts if the model won't be seen upon loading.</font></body></html>"),
						BorderLayout.SOUTH);

		pack();

	}

	private void confirm() {

		model.setTimeStep(stepField.getValue());
		String s = scriptArea.getText();
		if (s != null && !s.trim().equals("")) {
			model.setInitializationScript(scriptArea.getText());
		}
		else {
			model.setInitializationScript(null);
		}

		model.notifyChange();

	}

	void selectInitializationScriptTab() {
		tabbedPane.setSelectedComponent(scriptPanel);
	}

}