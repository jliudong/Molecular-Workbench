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

package org.concord.mw2d;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.concord.mw2d.models.AtomicModel;

class EngineAction extends AbstractAction {

	private EngineWorker engineWorker;
	private AtomicModel model;

	EngineAction(AtomicModel model) {
		this.model = model;
		putValue(NAME, "Cut-off Parameters");
		putValue(SHORT_DESCRIPTION, "Set cut-off parameters");
	}

	public void actionPerformed(ActionEvent e) {
		if (engineWorker == null) {
			engineWorker = new EngineWorker(model);
			engineWorker.setLocationRelativeTo(model.getView());
			engineWorker.pack();
		}
		engineWorker.setCutOffShift(model.getCutOffShift());
		engineWorker.listAdjustor.setCutOff(model.getCutOff());
		engineWorker.listAdjustor.setRList(model.getRList());
		engineWorker.setVisible(true);
	}

	public String toString() {
		return (String) getValue(SHORT_DESCRIPTION);
	}

}