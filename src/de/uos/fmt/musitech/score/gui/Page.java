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

import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a single page of a musical score.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Page extends ScoreContainer {
	private int height;
	private int explicitRwidth = -1;
	
	private int bottomSpace = 0;
	
	public Page() {
		super();
	}

	/**
	 * This constructs a page with a fixed width. This is mainly used to build an empty
	 * @param explicitWidth The width of this page
	 */
	public Page(int explicitWidth) {
		this();
		this.explicitRwidth = explicitWidth;
	}

	@Override
	public Page page() {
		return this;
	}

	@Override
	Class parentClass() {
		return Score.class;
	}

	/** Returns the minimum and maximum attack time of all events belonging to 
	 *  this system. 
	 *  @return array with 2 elements [0]=minimum, [1]=maximum */
	public Rational[] minMaxTime() {
		if (numChildren() == 0)
			return null;
		SSystem firstSystem = (SSystem) child(0);
		SSystem lastSystem = (SSystem) child(-1);
		Rational[] minmax = { firstSystem.minMaxTime()[0], lastSystem.minMaxTime()[1] };
		return minmax;
	}

	/** Returns the system that contains the given metrical time. */
	SSystem systemWithTime(Rational time) {
		for (int i = 0; i < numChildren(); i++) {
			Rational[] mmt = ((SSystem) child(i)).minMaxTime();
			if (time.isGreaterOrEqual(mmt[0]) && time.isLessOrEqual(mmt[1]))
				return (SSystem) child(i);
		}
		return null;
	}
	
	@Override
	int arrange(int pass) {
		int numPasses = 3;
		int max = Math.max(numPasses, super.arrange(pass));
		
		if (pass == 1) {
			int ypos = 30; //TODO: this is a hack
			for (int i = 0; i < numChildren(); i++) {
				if (i != 0) 
					ypos += 30;
				ypos += child(i).height();
				child(i).setYPos(ypos);
				ypos += child(i).depth();
			}
			height = ypos;
		}
		if (pass == 2) {
			int y = Integer.MAX_VALUE;
			for (int i = 0; i < numChildren(); i++) {
				y = Math.min(y, child(i).getLocation().y);
			}

			int[] minMaxY = getMinMaxY();
			setLocation(absX(), minMaxY[0] - 30);
			setSize(lwidth() + rwidth(), (minMaxY[1] - minMaxY[0]) + 90 + bottomSpace);
			
		}
		
		return max;

	}
	
	@Override
	public void paint(Graphics g) {
		//drawFrame(g);
		super.paint(g);
	}
	
	@Override
	public int rwidth() {
		if (explicitRwidth != -1)
			return explicitRwidth;
		
		int biggest = 0;
		for (int i = 0; i < numChildren(); i++) {
			biggest = Math.max(biggest, child(i).rwidth());
		}
		return biggest + 20;
	}
	
	@Override
	public int depth() {
		int depth = 0;
		for (int i = 0; i < numChildren(); i++) {
			SSystem system = (SSystem)child(i);
			depth += system.depth(); 
			if (i < (numChildren() - 1))
				depth += 30; //TODO
		}
		return depth + 30;
	}
	
	@Override
	public int height() {
		return 30;
		/*
		SSystem firstSystem = (SSystem) child(0);
		SSystem lastSystem = (SSystem) child(-1);
		return (lastSystem.absY() + lastSystem.depth()) - absY();
		*/
	}
	public int getBottomSpace() {
		return bottomSpace;
	}
	public void setBottomSpace(int bottomSpace) {
		this.bottomSpace = bottomSpace;
	}
}
