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

import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Aminoacid {

	public static final byte MWEIGHT_AMINO_PARAM = 0;
	public static final byte CHARGE_AMINO_PARAM = 1;
	public static final byte PHOB_RB_AMINO_PARAM = 2;
	public static final byte PK_AMINO_PARAM = 3;
	public static final byte SURFACE_AMINO_PARAM = 4;
	public static final byte VOLUME_AMINO_PARAM = 5;
	public static final byte SOLUBILITY_AMINO_PARAM = 6;
	public static final byte PHOB_AMINO_PARAM = 7;
	public static final byte NUMB_AMINO_PARAM = 8;

	private static Aminoacid[] allAminoacids;
	private static AminoacidBundle bundle = new AminoacidBundle();
	private static final float MAX_USAGE_FOR_COLOR = 0.040f;

	private String abbreviation;
	private String standardAbbreviation;
	private String name;
	private char symbol;
	private String property;
	private float[] params = new float[NUMB_AMINO_PARAM];
	private Map<Object, Object> properties;
	private Map<String, Float> charges;

	protected Aminoacid(String name, String abbreviation, char symbol, float[] params, String property) {
		this.name = name;
		this.property = property;
		this.abbreviation = abbreviation;
		this.symbol = symbol;
		standardAbbreviation = createStandardAbbreviation(abbreviation);
		if (params != null) {
			int extLength = params.length;
			int minLength = Math.min(extLength, NUMB_AMINO_PARAM);
			System.arraycopy(params, 0, this.params, 0, minLength);
		}
	}

	protected void loadCharges() {
		String resourceName = "/org/concord/molbio/data/amino/charges/" + abbreviation.toUpperCase() + "_CH.properties";
		Properties p = new Properties();
		try {
			p.load(getClass().getResourceAsStream(resourceName));
		}
		catch (Throwable t) {
		}
		charges = new HashMap<String, Float>();
		Iterator it = p.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			float charge = 0f;
			try {
				charge = Float.parseFloat((String) p.get(key));
			}
			catch (Throwable t) {
				charge = 0f;
			}
			charges.put(key.trim(), charge);
		}
	}

	public float getChargeAtom(String pdbAtomName) {
		if (charges == null)
			loadCharges();
		Float c = charges.get(pdbAtomName.trim());
		if (c == null)
			return 0;
		return c.floatValue();
	}

	public float getParam(int kind) {
		if (kind < 0 || kind >= NUMB_AMINO_PARAM)
			return 0;
		return params[kind];
	}

	public float getMolWeight() {
		return getParam(MWEIGHT_AMINO_PARAM);
	}

	public float getCharge() {
		return getParam(CHARGE_AMINO_PARAM);
	}

	public float getPhob() {
		return getParam(PHOB_RB_AMINO_PARAM);
	}

	public float getPK() {
		return getParam(PK_AMINO_PARAM);
	}

	public float getVolume() {
		return getParam(VOLUME_AMINO_PARAM);
	}

	public float getSurface() {
		return getParam(SURFACE_AMINO_PARAM);
	}

	public float getSolubility() {
		return getParam(SOLUBILITY_AMINO_PARAM);
	}

	public float getHydrophobicity() {
		return getParam(PHOB_AMINO_PARAM);
	}

	public static Aminoacid getBySymbol(char c) {
		return aminoacidsSymb.get(Character.toUpperCase(c));
	}

	public static Aminoacid getByAbbreviation(String abbreviation) {
		if (abbreviation == null)
			return null;
		return aminoacidsAbbr.get(abbreviation.toUpperCase());
	}

	public static Aminoacid getByName(String name) {
		if (name == null)
			return null;
		return aminoacidsName.get(name.toLowerCase());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Aminoacid))
			return false;
		if (name == null || ((Aminoacid) obj).name == null)
			return false;
		if (obj == this)
			return true;
		return name.equalsIgnoreCase(((Aminoacid) obj).name);
	}

	public Vector getDNACodons() {
		return getDNACodons(EXPRESS_FROM_35DNA_STRAND);
	}

	public Vector<String> getDNACodons(int expressStyle) {
		Vector<String> v = new Vector<String>();
		for (Enumeration e = aminoCreation.keys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			if (this == (aminoCreation.get(key)).amino) {
				String s = (String) key;
				if (s.length() != 3)
					continue;
				String cds = convertFromRNAStyleCodon(s, expressStyle);
				if (cds == null)
					continue;
				v.addElement(cds);
			}
		}
		return v;
	}

	// possible expressStyle values
	public final static int EXPRESS_FROM_53DNA_STRAND = 0;
	public final static int EXPRESS_FROM_35DNA_STRAND = 1;
	public final static int EXPRESS_FROM_RNA = 2;

	public final static int EXPRESS_TO_53DNA_STRAND = 0;
	public final static int EXPRESS_TO_35DNA_STRAND = 1;
	public final static int EXPRESS_TO_RNA = 2;

	public String getCodon(int expressStyle) throws IllegalArgumentException {
		if (expressStyle < EXPRESS_FROM_53DNA_STRAND || expressStyle > EXPRESS_FROM_RNA)
			throw new IllegalArgumentException("Aminoacid.express: expressStyle should be >= 0 and <= 2");
		String codonStr = getCodon(true);
		return convertFromRNAStyleCodon(codonStr, expressStyle);
	}

	public String getRNACodon() throws IllegalArgumentException {
		return getCodon(EXPRESS_FROM_RNA);
	}

	public String getDNA53Codon() throws IllegalArgumentException {
		return getCodon(EXPRESS_FROM_53DNA_STRAND);
	}

	public String getDNA35Codon() throws IllegalArgumentException {
		return getCodon(EXPRESS_FROM_35DNA_STRAND);
	}

	public String getCodonRandom() {
		return getCodonRandom(EXPRESS_TO_RNA);
	}

	public String getCodonRandom(int expressStyle) {
		Vector codons = getDNACodons();
		float[] probabilities = new float[codons.size()];
		float s = 0;
		for (int i = 0; i < codons.size(); i++) {
			String codonStr = convertToRNAStyleCodon((String) codons.elementAt(i), EXPRESS_FROM_35DNA_STRAND);
			probabilities[i] = getUsageForCodon(codonStr) * 1000;
			s += probabilities[i];
		}
		for (int i = 0; i < probabilities.length; i++)
			probabilities[i] /= s;
		float[] limits = new float[probabilities.length + 1];
		limits[0] = 0;
		for (int i = 1; i < probabilities.length; i++)
			limits[i] = limits[i - 1] + probabilities[i - 1];
		limits[probabilities.length] = 1;
		float newrandom = (float) Math.random();
		String newCodonString = null;
		for (int i = 0; i < limits.length - 1; i++) {
			if (newrandom >= limits[i] && newrandom < limits[i + 1]) {
				newCodonString = convertToRNAStyleCodon((String) codons.elementAt(i), EXPRESS_FROM_35DNA_STRAND);
				break;
			}
		}
		if (newCodonString == null)
			newCodonString = getCodon();
		return convertFromRNAStyleCodon(newCodonString, expressStyle);
	}

	public String getCodon() {
		return getCodon(true);
	}

	public String getCodon(boolean rnaStyle) {
		String retValue = null;
		java.util.Collection v = getDNACodons();
		if (v != null) {
			java.util.Iterator it = v.iterator();
			float usage = -1;
			while (it.hasNext()) {
				Codon codon = new Codon((String) it.next());
				Nucleotide[] bases = codon.bases;
				StringBuffer sb = new StringBuffer();
				StringBuffer sb1 = new StringBuffer();
				for (int i = 0; i < bases.length; i++) {
					sb.append(bases[i].getComplementaryNucleotideName(true));
					sb1.append(bases[i].getName());
				}
				String c = sb.toString();
				// System.out.println("c "+c);
				float usage1 = getUsageForCodon(c) * 1000;
				if (usage1 > usage) {
					usage = usage1;
					if (rnaStyle) {
						retValue = c;
					}
					else {
						retValue = sb1.toString();
					}
				}
			}
			if (usage < 0)
				retValue = null;
		}
		return retValue;
	}

	public char[] encode() {// Charles compatible
		return getDNA35Codon().toCharArray();

	}

	public char[] encodeRandomly() {
		return getCodonRandom(EXPRESS_TO_53DNA_STRAND).toCharArray();
	}

	// that equivalent to second method with expressStyle = EXPRESS_FROM_53DNA_STRAND
	public static Aminoacid express(char[] c) throws IllegalArgumentException {
		return express(c, EXPRESS_FROM_53DNA_STRAND);
	}

	public static Aminoacid express(char[] c, int expressStyle) throws IllegalArgumentException {
		if (c == null || c.length != 3)
			throw new IllegalArgumentException("Aminoacid.express: parameter should be char array with size = 3");
		if (expressStyle < EXPRESS_FROM_53DNA_STRAND || expressStyle > EXPRESS_FROM_RNA)
			throw new IllegalArgumentException("Aminoacid.express: expressStyle should be >= 0 and <= 2");
		Nucleotide[] nucleos = new Nucleotide[3];
		for (int i = 0; i < 3; i++) {
			if (c[i] == Nucleotide.URACIL_NAME && expressStyle != EXPRESS_FROM_RNA)
				throw new IllegalArgumentException("Aminoacid.express: uracil isn't allowed in DNA style express");
			if (c[i] == Nucleotide.THYMINE_NAME && expressStyle == EXPRESS_FROM_RNA)
				throw new IllegalArgumentException("Aminoacid.express: thymine isn't allowed in RNA style express");
			nucleos[i] = Nucleotide.getNucleotide(c[i]);
		}
		char n1 = 0, n2 = 0, n3 = 0;
		switch (expressStyle) {
		case EXPRESS_FROM_53DNA_STRAND:
			n1 = Nucleotide.convert53DNAStrandToRNA(nucleos[0].getName());
			n2 = Nucleotide.convert53DNAStrandToRNA(nucleos[1].getName());
			n3 = Nucleotide.convert53DNAStrandToRNA(nucleos[2].getName());
			break;
		case EXPRESS_FROM_35DNA_STRAND:
			n1 = nucleos[0].getComplementaryNucleotideName(true);
			n2 = nucleos[1].getComplementaryNucleotideName(true);
			n3 = nucleos[2].getComplementaryNucleotideName(true);
			break;
		case EXPRESS_FROM_RNA:
			n1 = nucleos[0].getName();
			n2 = nucleos[1].getName();
			n3 = nucleos[2].getName();
			break;
		}
		return Codon.getCodon(n1, n2, n3).createAminoacid();
	}

	public String getDNACodonsStringForTable() {
		StringBuffer sb = new StringBuffer();
		java.util.Collection v = getDNACodons();
		if (v != null) {
			java.util.Iterator it = v.iterator();
			while (it.hasNext()) {
				Codon codon = new Codon((String) it.next());
				Nucleotide[] bases = codon.bases;
				for (int i = 0; i < bases.length; i++) {
					sb.append(bases[i].getComplementaryNucleotideName(true));
				}
				if (it.hasNext()) {
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}

	public float getUsageForCodon(String codon, int expressStyle) throws IllegalArgumentException {
		return getUsageForCodon(convertToRNAStyleCodon(codon, expressStyle));
	}

	static String convertToRNAStyleCodon(String codon, int expressStyle) throws IllegalArgumentException {
		if (expressStyle == EXPRESS_FROM_RNA)
			return codon;
		if (expressStyle == EXPRESS_FROM_53DNA_STRAND)
			return codon.replace(Nucleotide.THYMINE_NAME, Nucleotide.URACIL_NAME);
		if (expressStyle == EXPRESS_FROM_35DNA_STRAND) {
			if (codon != null && codon.length() == 3) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 3; i++) {
					Nucleotide n = Nucleotide.getNucleotide(codon.charAt(i));
					sb.append(n.getComplementaryNucleotideName(true));
				}
				return sb.toString();
			}
		}
		throw new IllegalArgumentException("Aminoacid.convertToRNAStyleCodon: expressStyle should be >= 0 and <= 2");
	}

	static String convertFromRNAStyleCodon(String codon, int expressStyle) {
		if (expressStyle == EXPRESS_TO_RNA)
			return codon;
		if (expressStyle == EXPRESS_TO_53DNA_STRAND)
			return codon.replace(Nucleotide.URACIL_NAME, Nucleotide.THYMINE_NAME);
		if (expressStyle == EXPRESS_TO_35DNA_STRAND) {
			if (codon != null && codon.length() == 3) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 3; i++) {
					Nucleotide n = Nucleotide.getNucleotide(codon.charAt(i));
					sb.append(n.getComplementaryNucleotideName(false));
				}
				return sb.toString();
			}
		}
		throw new IllegalArgumentException("Aminoacid.convertFromRNAStyleCodon: expressStyle should be >= 0 and <= 2");
	}

	public float getUsageForCodonRelative(String codon, int expressStyle) throws IllegalArgumentException {
		String rnaCodon = convertToRNAStyleCodon(codon, expressStyle);
		Vector codons = getDNACodons();
		float s = 0;
		float myUsage = -1;
		for (int i = 0; i < codons.size(); i++) {
			String cd = (String) codons.elementAt(i);
			float usage = getUsageForCodon(cd, EXPRESS_FROM_35DNA_STRAND);
			s += usage;
			if (myUsage < 0 && convertToRNAStyleCodon(cd, EXPRESS_FROM_35DNA_STRAND).equalsIgnoreCase(rnaCodon)) {
				myUsage = usage;
			}
		}
		myUsage /= s;
		if (myUsage < 0)
			myUsage = 0;
		if (myUsage > 1)
			myUsage = 1;
		return myUsage;
	}

	public float getUsageForCodon(String codon) throws IllegalArgumentException {
		AminoCodonHolder holder = aminoCreation.get(codon);
		if (this != holder.amino) {
			throw new IllegalArgumentException("Codon " + codon + " doesn't produce " + standardAbbreviation);
		}
		return holder.codonUsage2 / 1000f;
	}

	/**
	 * Returns the usage of the codon
	 * 
	 * @param codonStr
	 *            (RNA style codon)
	 * @return a codon usage
	 */
	public static float getCodonUsage(String codonStr) throws IllegalArgumentException {
		float retValue = 0;
		try {
			Codon codon = Codon.getCodon(codonStr);
			Aminoacid amino = codon.createAminoacid();
			retValue = amino.getUsageForCodon(codonStr);
		}
		catch (Throwable t) {
			retValue = 0;
		}
		return retValue;
	}

	public String toString() {
		return "[" + getName() + "]";
	}

	protected static String createStandardAbbreviation(String abbreviation) {
		char[] ch = new char[3];
		abbreviation.toLowerCase().getChars(0, 3, ch, 0);
		ch[0] = Character.toUpperCase(ch[0]);
		return new String(ch, 0, 3);
	}

	public static Color getUsageColor(String codonStr) {
		codonStr = codonStr.replace('T', 'U');
		float usage = getCodonUsage(codonStr);
		if (usage > MAX_USAGE_FOR_COLOR)
			usage = MAX_USAGE_FOR_COLOR;
		float r = (MAX_USAGE_FOR_COLOR - usage) / MAX_USAGE_FOR_COLOR;
		return new Color(r, r, 1);
	}

	public String getStandardAbbreviation() {
		return standardAbbreviation;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public String getName() {
		return name;
	}

	public char getSymbol() {
		return symbol;
	}

	public String getFullName() {
		return getName();
	}

	public String getProperty() {
		return property;
	}

	public char getLetter() {
		return getSymbol();
	}

	final static Hashtable<String, Aminoacid> aminoacidsName = new Hashtable<String, Aminoacid>();
	final static Hashtable<String, Aminoacid> aminoacidsAbbr = new Hashtable<String, Aminoacid>();
	final static Hashtable<Character, Aminoacid> aminoacidsSymb = new Hashtable<Character, Aminoacid>();
	final static Hashtable<String, AminoCodonHolder> aminoCreation = new Hashtable<String, AminoCodonHolder>();
	final static Hashtable codonUsage = new Hashtable();
	final static Hashtable icons = new Hashtable();
	final static Hashtable formulas = new Hashtable();

	static {
		for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			Aminoacid amino = (Aminoacid) bundle.getObject((String) key);
			aminoacidsName.put(amino.name.toLowerCase(), amino);
			aminoacidsAbbr.put(amino.abbreviation.toUpperCase(), amino);
			aminoacidsSymb.put(amino.symbol, amino);
		}

		Aminoacid aminoToAdd = getByAbbreviation("PHE");
		aminoCreation.put("UUU", new AminoCodonHolder(aminoToAdd, 16.6f));// 16.6
		aminoCreation.put("UUC", new AminoCodonHolder(aminoToAdd, 20.7f));// 20.7
		aminoToAdd = getByAbbreviation("LEU");
		aminoCreation.put("UUA", new AminoCodonHolder(aminoToAdd, 7f));// 7
		aminoCreation.put("UUG", new AminoCodonHolder(aminoToAdd, 12f));// 12
		aminoCreation.put("CUU", new AminoCodonHolder(aminoToAdd, 12.4f));// 12.4
		aminoCreation.put("CUC", new AminoCodonHolder(aminoToAdd, 19.3f));// 19.3
		aminoCreation.put("CUA", new AminoCodonHolder(aminoToAdd, 6.8f));// 6.8
		aminoCreation.put("CUG", new AminoCodonHolder(aminoToAdd, 40f));// 40
		aminoToAdd = getByAbbreviation("ILE");
		aminoCreation.put("AUU", new AminoCodonHolder(aminoToAdd, 15.7f));// 15.7
		aminoCreation.put("AUC", new AminoCodonHolder(aminoToAdd, 22.3f));// 22.3
		aminoCreation.put("AUA", new AminoCodonHolder(aminoToAdd, 7f));// 7
		aminoToAdd = getByAbbreviation("MET");
		aminoCreation.put("AUA" + Codon.MITOCHONDRIA_SUFFIX, new AminoCodonHolder(aminoToAdd, 0));// ?
		aminoCreation.put("AUG", new AminoCodonHolder(aminoToAdd, 22.2f));// 22.2
		aminoToAdd = getByAbbreviation("VAL");
		aminoCreation.put("GUU", new AminoCodonHolder(aminoToAdd, 10.7f));// 10.7
		aminoCreation.put("GUC", new AminoCodonHolder(aminoToAdd, 14.8f));// 14.8
		aminoCreation.put("GUA", new AminoCodonHolder(aminoToAdd, 6.8f));// 6.8
		aminoCreation.put("GUG", new AminoCodonHolder(aminoToAdd, 29.3f));// 29.3
		aminoToAdd = getByAbbreviation("SER");
		aminoCreation.put("UCU", new AminoCodonHolder(aminoToAdd, 14.5f));// 14.5
		aminoCreation.put("UCC", new AminoCodonHolder(aminoToAdd, 17.7f));// 17.7
		aminoCreation.put("UCA", new AminoCodonHolder(aminoToAdd, 11.4f));// 11.4
		aminoCreation.put("UCG", new AminoCodonHolder(aminoToAdd, 4.5f));// 4.5
		aminoCreation.put("AGU", new AminoCodonHolder(aminoToAdd, 11.7f));// 11.7
		aminoCreation.put("AGC", new AminoCodonHolder(aminoToAdd, 19.3f));// 19.3
		aminoToAdd = getByAbbreviation("PRO");
		aminoCreation.put("CCU", new AminoCodonHolder(aminoToAdd, 17.2f));// 17.2
		aminoCreation.put("CCC", new AminoCodonHolder(aminoToAdd, 20.3f));// 20.3
		aminoCreation.put("CCA", new AminoCodonHolder(aminoToAdd, 16.5f));// 16.5
		aminoCreation.put("CCG", new AminoCodonHolder(aminoToAdd, 7.1f));// 7.1
		aminoToAdd = getByAbbreviation("THR");
		aminoCreation.put("ACU", new AminoCodonHolder(aminoToAdd, 12.7f));// 12.7
		aminoCreation.put("ACC", new AminoCodonHolder(aminoToAdd, 19.9f));// 19.9
		aminoCreation.put("ACA", new AminoCodonHolder(aminoToAdd, 14.7f));// 14.7
		aminoCreation.put("ACG", new AminoCodonHolder(aminoToAdd, 6.4f));// 6.4
		aminoToAdd = getByAbbreviation("ALA");
		aminoCreation.put("GCU", new AminoCodonHolder(aminoToAdd, 18.4f));// 18.4
		aminoCreation.put("GCC", new AminoCodonHolder(aminoToAdd, 28.6f));// 28.6
		aminoCreation.put("GCA", new AminoCodonHolder(aminoToAdd, 15.6f));// 15.6
		aminoCreation.put("GCG", new AminoCodonHolder(aminoToAdd, 7.7f));// 7.7
		aminoToAdd = getByAbbreviation("TYR");
		aminoCreation.put("UAU", new AminoCodonHolder(aminoToAdd, 12.1f));// 12.1
		aminoCreation.put("UAC", new AminoCodonHolder(aminoToAdd, 16.3f));// 16.3
		aminoToAdd = getByAbbreviation("HIS");
		aminoCreation.put("CAU", new AminoCodonHolder(aminoToAdd, 10.1f));// 10.1
		aminoCreation.put("CAC", new AminoCodonHolder(aminoToAdd, 14.9f));// 14.9
		aminoToAdd = getByAbbreviation("GLN");
		aminoCreation.put("CAA", new AminoCodonHolder(aminoToAdd, 11.8f));// 11.8
		aminoCreation.put("CAG", new AminoCodonHolder(aminoToAdd, 34.4f));// 34.4
		aminoToAdd = getByAbbreviation("ASN");
		aminoCreation.put("AAU", new AminoCodonHolder(aminoToAdd, 16.8f));// 16.8
		aminoCreation.put("AAC", new AminoCodonHolder(aminoToAdd, 19.4f));// 19.4
		aminoToAdd = getByAbbreviation("LYS");
		aminoCreation.put("AAA", new AminoCodonHolder(aminoToAdd, 23.6f));// 23.6
		aminoCreation.put("AAG", new AminoCodonHolder(aminoToAdd, 33.2f));// 33.2
		aminoToAdd = getByAbbreviation("ASP");
		aminoCreation.put("GAU", new AminoCodonHolder(aminoToAdd, 22.2f));// 22.2
		aminoCreation.put("GAC", new AminoCodonHolder(aminoToAdd, 26.5f));// 26.5
		aminoToAdd = getByAbbreviation("GLU");
		aminoCreation.put("GAA", new AminoCodonHolder(aminoToAdd, 28.6f));// 28.6
		aminoCreation.put("GAG", new AminoCodonHolder(aminoToAdd, 40.6f));// 40.6
		aminoToAdd = getByAbbreviation("CYS");
		aminoCreation.put("UGU", new AminoCodonHolder(aminoToAdd, 9.7f));// 9.7
		aminoCreation.put("UGC", new AminoCodonHolder(aminoToAdd, 12.4f));// 12.4
		aminoToAdd = getByAbbreviation("TRP");
		aminoCreation.put("UGG", new AminoCodonHolder(aminoToAdd, 13f));// 13
		aminoCreation.put("UGA" + Codon.MITOCHONDRIA_SUFFIX, new AminoCodonHolder(aminoToAdd, 0));// ?
		aminoToAdd = getByAbbreviation("ARG");
		aminoCreation.put("CGU", new AminoCodonHolder(aminoToAdd, 4.7f));// 4.7
		aminoCreation.put("CGC", new AminoCodonHolder(aminoToAdd, 11f));// 11
		aminoCreation.put("CGA", new AminoCodonHolder(aminoToAdd, 6.2f));// 6.2
		aminoCreation.put("CGG", new AminoCodonHolder(aminoToAdd, 11.6f));// 11.6
		aminoCreation.put("AGA", new AminoCodonHolder(aminoToAdd, 11.2f));// 11.2
		aminoCreation.put("AGG", new AminoCodonHolder(aminoToAdd, 11.1f));// 11.1
		aminoToAdd = getByAbbreviation("GLY");
		aminoCreation.put("GGU", new AminoCodonHolder(aminoToAdd, 10.9f));// 10.9
		aminoCreation.put("GGC", new AminoCodonHolder(aminoToAdd, 23.1f));// 23.1
		aminoCreation.put("GGA", new AminoCodonHolder(aminoToAdd, 16.4f));// 16.4
		aminoCreation.put("GGG", new AminoCodonHolder(aminoToAdd, 16.5f));// 16.5

	}

	public void putProperty(Object key, Object value) {
		if (properties == null)
			properties = new HashMap<Object, Object>();
		if (properties == null)
			return;
		properties.put(key, value);
	}

	public Object getProperty(Object key) {
		if (properties == null)
			return null;
		return properties.get(key);
	}

	public static Aminoacid[] getAllAminoacids() {
		if (allAminoacids == null) {
			allAminoacids = new Aminoacid[20];
			allAminoacids[0] = getByAbbreviation("Gly");
			allAminoacids[1] = getByAbbreviation("Ala");
			allAminoacids[2] = getByAbbreviation("Val");
			allAminoacids[3] = getByAbbreviation("Leu");
			allAminoacids[4] = getByAbbreviation("Ile");
			allAminoacids[5] = getByAbbreviation("Phe");
			allAminoacids[6] = getByAbbreviation("Pro");
			allAminoacids[7] = getByAbbreviation("Trp");
			allAminoacids[8] = getByAbbreviation("Met");
			allAminoacids[9] = getByAbbreviation("Cys");
			allAminoacids[10] = getByAbbreviation("Tyr");
			allAminoacids[11] = getByAbbreviation("Asn");
			allAminoacids[12] = getByAbbreviation("Gln");
			allAminoacids[13] = getByAbbreviation("Ser");
			allAminoacids[14] = getByAbbreviation("Thr");
			allAminoacids[15] = getByAbbreviation("Asp");
			allAminoacids[16] = getByAbbreviation("Glu");
			allAminoacids[17] = getByAbbreviation("Lys");
			allAminoacids[18] = getByAbbreviation("Arg");
			allAminoacids[19] = getByAbbreviation("His");
		}
		return allAminoacids;
	}

	static class AminoCodonHolder {

		float codonUsage2 = 0;
		Aminoacid amino;

		AminoCodonHolder(Aminoacid amino, float codonUsage) {
			this.codonUsage2 = codonUsage;
			this.amino = amino;
		}

	}

}
