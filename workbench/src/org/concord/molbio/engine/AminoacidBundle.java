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

package org.concord.molbio.engine;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

class AminoacidBundle extends ResourceBundle {

	private Properties p;
	private Hashtable<String, Aminoacid> aminoCache = new Hashtable<String, Aminoacid>();
	private String[] names;
	private String[] units;
	private static String[] keys = { "Ala", "Arg", "Asn", "Asp", "Cys", "Gln", "Glu", "Gly", "His", "Ile", "Leu",
			"Lys", "Met", "Phe", "Pro", "Ser", "Thr", "Trp", "Tyr", "Val" };

	public AminoacidBundle() {
		p = new Properties();
		try {
			p.load(getClass().getResourceAsStream("aminoacids.properties"));
		}
		catch (IOException ex) {
		}
	}

	protected Object handleGetObject(String key) throws MissingResourceException {
		if (key == null)
			throw new MissingResourceException("AminoacidBundle.handleGetObject: key == null", getClass().getName(),
					key);
		String upperKey = key.toUpperCase();
		String propString = (String) p.get(upperKey);
		if (propString == null)
			throw new MissingResourceException("can't find resource in the bundle", getClass().getName(), key);
		Object obj = aminoCache.get(upperKey);
		if (obj != null)
			return obj;
		String name = null;
		String abbr = null;
		char symb = ' ';
		float[] params = new float[Aminoacid.NUMB_AMINO_PARAM];
		String property = "";
		try {
			StringTokenizer parser = new StringTokenizer(propString, ",");
			if (parser.hasMoreTokens())
				name = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				abbr = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				symb = parser.nextToken().trim().charAt(0);
			for (int i = 0; i < Aminoacid.NUMB_AMINO_PARAM; i++) {
				if (parser.hasMoreTokens())
					params[i] = getFloatFromString(parser.nextToken().trim(), -1);
				else break;
			}
			if (parser.hasMoreTokens())
				property = parser.nextToken().trim();

		}
		catch (Exception e) {
		}
		Aminoacid amino = new Aminoacid(name, abbr, symb, params, property);
		aminoCache.put(upperKey, amino);
		return amino;
	}

	protected float getFloatFromString(String str, float defaultValue) {
		float retValue = defaultValue;
		try {
			retValue = (new Float(str)).floatValue();
		}
		catch (Exception e) {
			retValue = defaultValue;
		}
		return retValue;
	}

	@SuppressWarnings("unchecked")
	public Enumeration getKeys() {
		return new Enumeration() {
			int currItem;

			public boolean hasMoreElements() {
				return currItem < keys.length;
			}

			public Object nextElement() {
				if (currItem < keys.length)
					return keys[currItem++];
				throw new NoSuchElementException("AminoacidBundle.getKeys");
			}
		};
	}

	public String[] getPropertyNames() {
		if (names != null)
			return names;
		String propString = (String) p.get("names");
		if (propString == null)
			return null;
		names = new String[Aminoacid.NUMB_AMINO_PARAM + 3];
		try {
			StringTokenizer parser = new StringTokenizer(propString, ",");
			if (parser.hasMoreTokens())
				names[0] = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				names[1] = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				names[2] = parser.nextToken().trim();
			for (int i = 0; i < Aminoacid.NUMB_AMINO_PARAM; i++) {
				if (parser.hasMoreTokens())
					names[3 + i] = parser.nextToken().trim();
				else break;
			}

		}
		catch (Exception e) {
		}
		return names;
	}

	public String[] getPropertyUnits() {
		if (units != null)
			return units;
		String propString = (String) p.get("units");
		if (propString == null)
			return null;
		units = new String[Aminoacid.NUMB_AMINO_PARAM + 3];
		try {
			StringTokenizer parser = new StringTokenizer(propString, ",");
			if (parser.hasMoreTokens())
				units[0] = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				units[1] = parser.nextToken().trim();
			if (parser.hasMoreTokens())
				units[2] = parser.nextToken().trim();
			for (int i = 0; i < Aminoacid.NUMB_AMINO_PARAM; i++) {
				if (parser.hasMoreTokens())
					units[3 + i] = parser.nextToken().trim();
				else break;
			}
			for (int i = 0; i < units.length; i++) {
				if (units[i] != null && units[i].charAt(0) == '_') {
					units[i] = "";
				}
			}
		}
		catch (Exception e) {
		}
		return units;
	}

}
