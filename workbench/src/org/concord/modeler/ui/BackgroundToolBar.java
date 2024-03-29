/*
 *   Copyright (C) 2009  The Concord Consortium, Inc.,
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
package org.concord.modeler.ui;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

/**
 * @author Charles Xie
 * 
 */
public class BackgroundToolBar extends JToolBar {

	private ImageIcon backgroundImage;

	public BackgroundToolBar(ImageIcon image) {
		super();
		backgroundImage = image;
	}

	public BackgroundToolBar(int orientation, ImageIcon image) {
		super(orientation);
		setBackgroundImage(image);
	}

	public void setBackgroundImage(ImageIcon image) {
		backgroundImage = image;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			int iconWidth = backgroundImage.getIconHeight();
			int iconHeight = backgroundImage.getIconHeight();
			int imax = getWidth() / iconWidth + 1;
			int jmax = getHeight() / iconHeight + 1;
			for (int i = 0; i < imax; i++) {
				for (int j = 0; j < jmax; j++) {
					backgroundImage.paintIcon(this, g, i * iconWidth, j * iconHeight);
				}
			}
		}
	}

}
