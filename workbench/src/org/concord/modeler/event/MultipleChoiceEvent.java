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

package org.concord.modeler.event;

import java.util.EventObject;

public class MultipleChoiceEvent extends EventObject {

	private boolean singleSelection;
	private boolean correctAnswer;
	private int[] selectedIndices;

	public MultipleChoiceEvent(Object source) {
		super(source);
	}

	/** for multiple selection questions */
	public MultipleChoiceEvent(Object source, boolean correctAnswer) {
		super(source);
		this.correctAnswer = correctAnswer;
		singleSelection = false;
	}

	/** for multiple selection questions */
	public MultipleChoiceEvent(Object source, int[] selectedIndices) {
		super(source);
		this.selectedIndices = selectedIndices;
		singleSelection = false;
	}

	/** for single selection questions */
	public MultipleChoiceEvent(Object source, int selectedIndex) {
		super(source);
		selectedIndices = new int[] { selectedIndex };
		singleSelection = true;
	}

	public boolean isSingleSelection() {
		return singleSelection;
	}

	public boolean isAnswerCorrect() {
		return correctAnswer;
	}

	public int getSelectedIndex() {
		if (selectedIndices == null || selectedIndices.length == 0)
			return -1;
		return selectedIndices[0];
	}

	public int[] getSelectedIndices() {
		return selectedIndices;
	}

}