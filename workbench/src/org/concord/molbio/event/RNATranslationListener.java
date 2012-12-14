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

package org.concord.molbio.event;

public interface RNATranslationListener {

	public static final byte MODE_UNKNOWN = 0;
	public static final byte MODE_TRANSLATION_START = 1;
	public static final byte MODE_TRANSLATION_NEW_AMINO = 2;
	public static final byte MODE_TRANSLATION_STOP = 3;
	public static final byte MODE_TRANSLATION_END = 4;

	void aminoacidAdded(RNATranslationEvent evt);

}
