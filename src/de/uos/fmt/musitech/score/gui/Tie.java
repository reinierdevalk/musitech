/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
package de.uos.fmt.musitech.score.gui;
import java.awt.Graphics;

import de.uos.fmt.musitech.score.util.Angle;
import de.uos.fmt.musitech.score.util.Bow;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.MyMath;

/** This class represents a musical tie. A tie is a bow between two
 * successive note heads indicating that both pitches are acoustically 
 * merged to one note. It can either be used to split notes at barlines 
 * or inside a measure to optimize a rhythms notation (e.g. notes are often
 * splitted at stronger metric beats.
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ */
public class Tie extends ScoreObject {
	private int height; // in pixel units
	private float tangentAngle;
	private Head leftHead;
	private Head rightHead;
	
	/**
	 * The length of a split tie in line distances
	 */
	private int splitLength = 2;
	
	public Tie(Head leftHead, Head rightHead) {
		this.leftHead = leftHead;
		this.rightHead = rightHead;
	}

	Class parentClass() {
		return Pitch.class;
	}

	boolean splitTie() {
		return leftHead.staff() != rightHead.staff();
	}
	
	int arrange(int pass) {
		if (pass != 1)
			return 2;
      // the following parameters can be calculated not until the second pass
      height = computeHeight();
      tangentAngle = (float)computeTangentAngle();
      
	  return 2;
	}

	int computeHeight() {
		double ld = leftHead.staff().getLineDistance();
		double len = length() / ld; // length (distance between endpoints) in staff spaces
		double h;        // height in staff spaces
      int dir = -1;    // default: tie curves downwards
      if (height != 0) // direction already assigned? 
         dir = MyMath.sign(height); // ok, store it

		if (len < 5)
			h = 3.0 * len / 20;
		else if (len < 25)
			h = (3.0 * len + 15) / 40;
		else
			h = 2.25;
		return (int) Math.round(h * ld) * dir; // height in pixels
	}

	/** Computes and returns the angle of the tangent touching the left endpoint b(0). 
	 *  This angle is equal to arctan (db/dt)(0). */
	double computeTangentAngle() {
		Pair p1 = leftHead.tiePoint(true, height > 0);
		Pair p2 = rightHead.tiePoint(false, height > 0);
		return computeTangentAngle(p1, p2);
	}
	
	double computeTangentAngle(Pair p1, Pair p2) {
		double ld = leftHead.staff().getLineDistance();
		double len = p2.distanceTo(p1) / ld;

		// (see F. J. Sola, Computer Design of Musical Slurs, S. 111)
		double yd;
		if (len < 5)
			yd = 0.2 * len;
		else if (len < 25)
			yd = (len + 5.0) / 10;
		else
			yd = 3.0;

		double xd;
		if (len < 4)
			xd = len * 0.3 - 0.1;
		else if (len < 25)
			xd = (len * 3.0 + 15) / 20;
		else
			xd = 2.16;

		return Math.atan(yd / xd);
	}

	/** Sets the direction of this tie. 
	 * @param up true if tie curves upwards */
	void setDirection(boolean up) {
		if (height == 0)
			height = 1;
		height = Math.abs(height);
		if (!up)
			height = -height;      
	}

	/** Returns 1 if tie curves upwards, else -1. */
	int getDirection() {
		return MyMath.sign(height);
	}

	double length() {
		if (!splitTie()) {
			boolean up = height > 0;
			return leftHead.tiePoint(true, up).distanceTo(rightHead.tiePoint(false, up));
		}
		else {
			return splitLength * leftHead.staff().getLineDistance();
		}
	}

	/** Draws this Tie onto the given graphics context. */
	public void paint(Graphics g) {
		if(!isVisible()) return;
		Pair left = leftHead.tiePoint(true, height > 0);
		Pair right = rightHead.tiePoint(false, height > 0);
		
		if (leftHead.staff() == rightHead.staff()) {
			Bow bow;
			if (height > 0) 
				bow = new Bow(left, right, -Math.abs(height), new Angle(-tangentAngle));         
			else 
				bow = new Bow(left, right, Math.abs(height), new Angle(tangentAngle));                     
			bow.paint(g);
		}
		else {
			Bow leftBow, rightBow;
			int ld = staff().getLineDistance();
			Pair leftEnd = new Pair(left.getX() + splitLength * ld, left.getY());
			Pair rightEnd = new Pair(right.getX() - splitLength * ld, right.getY());
			if (height > 0) {
				tangentAngle = (float)computeTangentAngle(left, leftEnd);
				leftBow = new Bow(left, leftEnd,
						          -Math.abs(height), new Angle(-tangentAngle));
				tangentAngle = (float)computeTangentAngle(rightEnd, right);				
				rightBow = new Bow(rightEnd, right,
								   -Math.abs(height), new Angle(-tangentAngle));
			}
			else {
				tangentAngle = (float)computeTangentAngle(left, leftEnd);
				leftBow = new Bow(left, leftEnd,
				                  Math.abs(height), new Angle(tangentAngle));
				tangentAngle = (float)computeTangentAngle(rightEnd, right);
				rightBow = new Bow(rightEnd, right,
						           Math.abs(height), new Angle(tangentAngle));
			}
			leftBow.paint(g);
			rightBow.paint(g);
		}
	}
}