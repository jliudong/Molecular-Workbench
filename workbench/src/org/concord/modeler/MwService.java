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

import java.awt.Component;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import javax.swing.JPopupMenu;

/**
 * @see org.concord.modeler.PageApplet
 * @author Charles Xie
 * 
 */

public interface MwService {

	/** notify this service that it should be in the editable mode */
	public void setEditable(boolean b);

	public Component getSnapshotComponent();

	public JPopupMenu getPopupMenu();

	public String runNativeScript(String script);

	public void loadState(InputStream is) throws IOException;

	public void saveState(OutputStream os) throws IOException;

	/**
	 * An untrusted applet will not be able to shutdown its thread service when it is not needed any more and ought to
	 * be destroyed. A workaround is to "borrow" a thread service from MW. An applet that needs to do this should return
	 * true in the implementation of this method. When it is initiated, MW will create an ExecutorService and pass it to
	 * the applet. When it is destroyed, MW will shutdown the "lent" ExecutorService immediately. Hence, memory leak is
	 * prevented.
	 */
	public boolean needExecutorService();

	public void setExecutorService(ExecutorService service);

}