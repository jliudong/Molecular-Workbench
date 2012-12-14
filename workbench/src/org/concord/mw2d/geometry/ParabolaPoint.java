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

package org.concord.mw2d.geometry;

class ParabolaPoint extends MyPoint {

	double a, b, c;

	public ParabolaPoint(MyPoint mypoint) {
		super(mypoint);
	}

	public double realX() {
		return y;
	}

	public double realY(double d) {
		return d - x;
	}

	public CirclePoint calculateCenter(MyPoint mypoint, ArcNode arcnode, MyPoint mypoint1) {

		CirclePoint circlepoint = null;
		MyPoint mypoint2 = new MyPoint(arcnode.x - mypoint.x, arcnode.y - mypoint.y);
		MyPoint mypoint3 = new MyPoint(mypoint1.x - arcnode.x, mypoint1.y - arcnode.y);

		if (mypoint3.y * mypoint2.x > mypoint3.x * mypoint2.y) {
			double d = -mypoint2.x / mypoint2.y;
			double d1 = (mypoint.y + mypoint2.y / 2D) - d * (mypoint.x + mypoint2.x / 2D);
			double d2 = -mypoint3.x / mypoint3.y;
			double d3 = (arcnode.y + mypoint3.y / 2D) - d2 * (arcnode.x + mypoint3.x / 2D);
			double d4;
			double d5;
			if (mypoint2.y == 0.0D) {
				d4 = mypoint.x + mypoint2.x / 2D;
				d5 = d2 * d4 + d3;
			}
			else if (mypoint3.y == 0.0D) {
				d4 = arcnode.x + mypoint3.x / 2D;
				d5 = d * d4 + d1;
			}
			else {
				d4 = (d3 - d1) / (d - d2);
				d5 = d * d4 + d1;
			}
			circlepoint = new CirclePoint(d4, d5, arcnode);
		}

		return circlepoint;

	}

	public void init(double d) {
		double d1 = realX();
		double d2 = realY(d);
		if (Math.abs(d2 - 0.0) > Double.MIN_VALUE) {
			a = 1.0D / (2D * d2);
			b = -d1 / d2;
			c = (d1 * d1) / (2D * d2) + d2 / 2D;
		}
	}

	public double F(double d) {
		return (a * d + b) * d + c;
	}

	public double[] solveQuadratic(double d, double d1, double d2) throws Throwable {

		double ad[] = new double[2];
		double d3 = d1 * d1 - 4D * d * d2;
		if (d3 < 0.0D) {
			throw new Throwable();
		}
		if (d == 0.0D) {
			if (d1 != 0.0D) {
				ad[0] = -d2 / d1;
			}
			else {
				throw new Throwable();
			}
		}
		else {
			double d4 = Math.sqrt(d3);
			double d5 = -d1;
			double d6 = 2D * d;
			ad[0] = (d5 + d4) / d6;
			ad[1] = (d5 - d4) / d6;
		}
		return ad;
	}

}