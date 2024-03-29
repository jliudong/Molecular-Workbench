/*
 *   Copyright (C) 2008  The Concord Consortium, Inc.,
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles Xie
 * 
 */
public class PluginManager {

	private static List<PluginInfo> plugins;

	private PluginManager() {
	}

	public static PluginInfo[] getPlugins() {
		if (plugins == null)
			return null;
		return plugins.toArray(new PluginInfo[0]);
	}

	public static void addPlugInfo(PluginInfo pi) {
		if (plugins == null)
			plugins = new ArrayList<PluginInfo>();
		plugins.add(pi);
	}

	public static PluginInfo getPluginInfoByName(String name) {
		if (name == null)
			return null;
		if (plugins == null || plugins.isEmpty())
			return null;
		for (PluginInfo x : plugins) {
			if (name.equals(x.getName()))
				return x;
		}
		return null;
	}

	public static PluginInfo getPluginInfoByMainClass(String mainClass) {
		if (mainClass == null)
			return null;
		if (plugins == null || plugins.isEmpty())
			return null;
		for (PluginInfo x : plugins) {
			if (mainClass.equals(x.getMainClass()))
				return x;
		}
		return null;
	}

}
