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
package org.concord.modeler;

import java.awt.EventQueue;

import org.concord.modeler.event.PageComponentEvent;

/**
 * @author Charles Xie
 * 
 */
class PluginScripter extends ComponentScripter {

	private PageJContainer jContainer;

	PluginScripter(PageJContainer jContainer) {
		super(true);
		this.jContainer = jContainer;
		setName("Plugin Script Runner #" + jContainer.getIndex());
	}

	protected void evalCommand(String ci) {

		// snapshot
		if ("snapshot".equalsIgnoreCase(ci)) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					jContainer.snapshot();
				}
			});
			return;
		}

		// reload
		if ("reset".equalsIgnoreCase(ci)) {
			jContainer.loadState();
			jContainer.notifyPageComponentListeners(new PageComponentEvent(jContainer,
					PageComponentEvent.COMPONENT_RESET));
			return;
		}

	}

}
