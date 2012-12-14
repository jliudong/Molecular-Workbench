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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import org.concord.Resource;
import org.concord.modeler.ui.IconPool;

class RunAction extends AbstractAction {

	private MDModel model;

	RunAction(MDModel model) {
		this.model = model;
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		putValue(SMALL_ICON, IconPool.getIcon("play"));
		putValue(NAME, "Run");
		putValue(SHORT_DESCRIPTION, Resource.get("AbstractMovie_java_qidongmoxing"));
	}

	public void actionPerformed(ActionEvent e) {
		model.movie.enableMovieActions(false);
		if (model.movie.getCurrentFrameIndex() < model.movie.length()) {
			Thread t = new Thread("Movie Player") {
				public void run() {
					if (model.movie.play()) {
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								model.run();
							}
						});
					}
				}
			};
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
		else {
			model.run();
		}
	}

	public String toString() {
		return (String) getValue(SHORT_DESCRIPTION);
	}

}