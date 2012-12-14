/* $RCSfile: AlphaPolymer.java,v $
 * $Author: qxie $
 * $Date: 2006-11-29 22:46:12 $
 * $Revision: 1.12 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.myjmol.viewer;

import org.myjmol.util.Logger;

class AlphaPolymer extends Polymer {

	AlphaPolymer(Monomer[] monomers) {
		super(monomers);
	}

	void addSecondaryStructure(byte type, char startChainID, int startSeqcode, char endChainID, int endSeqcode) {
		int indexStart, indexEnd;
		if ((indexStart = getIndex(startChainID, startSeqcode)) == -1
				|| (indexEnd = getIndex(endChainID, endSeqcode)) == -1)
			return;
		addSecondaryStructure(type, indexStart, indexEnd);
	}

	void addSecondaryStructure(byte type, int indexStart, int indexEnd) {

		int structureCount = indexEnd - indexStart + 1;
		if (structureCount < 1) {
			Logger.error("structure definition error\n" + " indexStart:" + indexStart + " indexEnd:" + indexEnd);
			return;
		}
		ProteinStructure proteinstructure = null;
		switch (type) {
		case JmolConstants.PROTEIN_STRUCTURE_HELIX:
			proteinstructure = new Helix(this, indexStart, structureCount);
			proteinstructure.index = ++structureCount;
			break;
		case JmolConstants.PROTEIN_STRUCTURE_SHEET:
			if (this instanceof AminoPolymer)
				proteinstructure = new Sheet((AminoPolymer) this, indexStart, structureCount);
			if (proteinstructure != null)
				proteinstructure.index = ++structureCount;
			break;
		case JmolConstants.PROTEIN_STRUCTURE_TURN:
			proteinstructure = new Turn(this, indexStart, structureCount);
			proteinstructure.index = ++structureCount;
			break;
		default:
			Logger.error("unrecognized secondary structure type");
			return;
		}
		for (int i = indexStart; i <= indexEnd; ++i)
			monomers[i].setStructure(proteinstructure);
	}

	void calcHydrogenBonds() {
		// deprecated
	}

	/**
	 * Uses Levitt & Greer algorithm to calculate protien secondary structures using only alpha-carbon atoms.
	 * <p>
	 * Levitt and Greer <br />
	 * Automatic Identification of Secondary Structure in Globular Proteins <br />
	 * J.Mol.Biol.(1977) 114, 181-293 <br />
	 * <p>
	 * <a href='http://csb.stanford.edu/levitt/Levitt_JMB77_Secondary_structure.pdf'>
	 * http://csb.stanford.edu/levitt/Levitt_JMB77_Secondary_structure.pdf </a>
	 */
	void calculateStructures() {
		if (monomerCount < 4)
			return;
		float[] angles = calculateAnglesInDegrees();
		byte[] codes = calculateCodes(angles);
		checkBetaSheetAlphaHelixOverlap(codes, angles);
		byte[] tags = calculateRunsFourOrMore(codes);
		extendRuns(tags);
		searchForTurns(codes, angles, tags);
		addStructuresFromTags(tags);
	}

	final static byte CODE_NADA = 0;
	final static byte CODE_RIGHT_HELIX = 1;
	final static byte CODE_BETA_SHEET = 2;
	final static byte CODE_LEFT_HELIX = 3;

	final static byte CODE_LEFT_TURN = 4;
	final static byte CODE_RIGHT_TURN = 5;

	float[] calculateAnglesInDegrees() {
		float[] angles = new float[monomerCount];
		for (int i = monomerCount - 1; --i >= 2;)
			angles[i] = Measurement.computeTorsion(monomers[i - 2].getLeadAtomPoint(), monomers[i - 1]
					.getLeadAtomPoint(), monomers[i].getLeadAtomPoint(), monomers[i + 1].getLeadAtomPoint());
		return angles;
	}

	byte[] calculateCodes(float[] angles) {
		byte[] codes = new byte[monomerCount];
		for (int i = monomerCount - 1; --i >= 2;) {
			float degrees = angles[i];
			codes[i] = ((degrees >= 10 && degrees < 120) ? CODE_RIGHT_HELIX
					: ((degrees >= 120 || degrees < -90) ? CODE_BETA_SHEET
							: ((degrees >= -90 && degrees < 0) ? CODE_LEFT_HELIX : CODE_NADA)));
		}
		return codes;
	}

	void checkBetaSheetAlphaHelixOverlap(byte[] codes, float[] angles) {
		for (int i = monomerCount - 2; --i >= 2;)
			if (codes[i] == CODE_BETA_SHEET && angles[i] <= 140 && codes[i - 2] == CODE_RIGHT_HELIX
					&& codes[i - 1] == CODE_RIGHT_HELIX && codes[i + 1] == CODE_RIGHT_HELIX
					&& codes[i + 2] == CODE_RIGHT_HELIX)
				codes[i] = CODE_RIGHT_HELIX;
	}

	final static byte TAG_NADA = JmolConstants.PROTEIN_STRUCTURE_NONE;
	final static byte TAG_TURN = JmolConstants.PROTEIN_STRUCTURE_TURN;
	final static byte TAG_SHEET = JmolConstants.PROTEIN_STRUCTURE_SHEET;
	final static byte TAG_HELIX = JmolConstants.PROTEIN_STRUCTURE_HELIX;

	byte[] calculateRunsFourOrMore(byte[] codes) {
		byte[] tags = new byte[monomerCount];
		byte tag = TAG_NADA;
		byte code = CODE_NADA;
		int runLength = 0;
		for (int i = 0; i < monomerCount; ++i) {
			// throw away the sheets ... their angle technique does not work well
			if (codes[i] == code && code != CODE_NADA && code != CODE_BETA_SHEET) {
				++runLength;
				if (runLength == 4) {
					tag = (code == CODE_BETA_SHEET ? TAG_SHEET : TAG_HELIX);
					for (int j = 4; --j >= 0;)
						tags[i - j] = tag;
				}
				else if (runLength > 4)
					tags[i] = tag;
			}
			else {
				runLength = 1;
				code = codes[i];
			}
		}
		return tags;
	}

	void extendRuns(byte[] tags) {
		for (int i = 1; i < monomerCount - 4; ++i)
			if (tags[i] == TAG_NADA && tags[i + 1] != TAG_NADA)
				tags[i] = tags[i + 1];

		tags[0] = tags[1];
		tags[monomerCount - 1] = tags[monomerCount - 2];
	}

	void searchForTurns(byte[] codes, float[] angles, byte[] tags) {
		for (int i = monomerCount - 1; --i >= 2;) {
			codes[i] = CODE_NADA;
			if (tags[i] == TAG_NADA) {
				float angle = angles[i];
				if (angle >= -90 && angle < 0)
					codes[i] = CODE_LEFT_TURN;
				else if (angle >= 0 && angle < 90)
					codes[i] = CODE_RIGHT_TURN;
			}
		}

		for (int i = monomerCount - 1; --i >= 0;) {
			if (codes[i] != CODE_NADA && codes[i + 1] == codes[i] && tags[i] == TAG_NADA)
				tags[i] = TAG_TURN;
		}
	}

	void addStructuresFromTags(byte[] tags) {
		int i = 0;
		while (i < monomerCount) {
			byte tag = tags[i];
			if (tag == TAG_NADA) {
				++i;
				continue;
			}
			int iMax;
			for (iMax = i + 1; iMax < monomerCount && tags[iMax] == tag; ++iMax) {
			}
			addSecondaryStructure(tag, i, iMax - 1);
			i = iMax;
		}
	}
}
