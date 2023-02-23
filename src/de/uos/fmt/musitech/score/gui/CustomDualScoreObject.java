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
/*
 * Created on Dec 28, 2004
 *
 */
package de.uos.fmt.musitech.score.gui;

import java.awt.Graphics;

import de.uos.fmt.musitech.data.score.BezierCurveSymbol;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.score.util.Bezier;
import de.uos.fmt.musitech.score.util.Pair;

/**
 * @author collin
 *
 */
public class CustomDualScoreObject extends CustomScoreObject {
	ScoreMapper scoreMapper;
	ScoreObject rightAnker;
	
	public CustomDualScoreObject(ScoreObject master, DualMetricAttachable ma) {
		super(master, ma);
	}

	@Override
	public void paint(Graphics g) {
		int ld = master.staff().getLineDistance();
		
		if (rightAnker == null)
			rightAnker = (ScoreObject)scoreMapper.getNotationToGraphical().get(((DualMetricAttachable)attachable).getRightAnker());
		
		if (attachable.getSymbol() instanceof CustomSVGGraphic) {
			CustomSVGGraphic csg = (CustomSVGGraphic)attachable.getSymbol();
			int length = rightAnker.absX() + rightAnker.rwidth() - absX();
			
			//TODO: remove this hack:
			if (rightAnker instanceof Chord &&
				((Chord)rightAnker).isDrawDurationExtension()) {
				int optimalSpace = (int)rightAnker.localSim().optimalSpace(rightAnker.localSim().getMetricDuration(), rightAnker.rwidth()) - rightAnker.rwidth();
				length += optimalSpace;
			}
			
			if (((DualMetricAttachable)attachable).getRightAnkerAlignment() == DualMetricAttachable.LEFT) {
				length -= rightAnker.lwidth() + rightAnker.rwidth();
			}
			
			csg.setCutX(length);
			int y = absY();
			int pixelHeight = csg.getHeight() * ld;
			if (attachable.getRelativePosition() == MetricAttachable.NORTH) {
				y -= pixelHeight;
			}
			else if (attachable.getRelativePosition() == MetricAttachable.SOUTH) {
				y += pixelHeight;
			}
			setScaledFont(g);
			csg.paint(g, absX(), y, length, pixelHeight, this);
			restoreFont(g);
		}
		else if (attachable.getSymbol() instanceof BezierCurveSymbol) {
			BezierCurveSymbol bcs = (BezierCurveSymbol)attachable.getSymbol();
			Pair[] controlPoints = bcs.getControlPoints();
			int x = absX();
			int y = anker.absY();

			/*
			System.err.println(x + " " + y + "\n" +
					   (x + controlPoints[0].getX() * ld) + " " + (y + controlPoints[0].getY() * ld) + "\n" +
					   (x + controlPoints[1].getX() * ld) + " " + (y + controlPoints[1].getY() * ld) + "\n" +
					   rightAnker.absX() + " " + rightAnker.absY());
			*/

			
			Bezier bezier = new Bezier(x, y,
									   x + controlPoints[0].getX() * ld, y + controlPoints[0].getY() * ld,
									   x + controlPoints[1].getX() * ld, y + controlPoints[1].getY() * ld,
									   rightAnker.absX(), rightAnker.absY());
			bezier.paint(g);
		}
		else {
			throw new IllegalStateException("symbol is of type " + attachable.getSymbol().getClass() + ". This is not implemented yet");
		}
	}
	
	
	public ScoreMapper getScoreMapper() {
		return scoreMapper;
	}
	public void setScoreMapper(ScoreMapper scoreMapper) {
		this.scoreMapper = scoreMapper;
	}
	public ScoreObject getRightAnker() {
		return rightAnker;
	}
	public void setRightAnker(ScoreObject rightAnker) {
		this.rightAnker = rightAnker;
	}
	
}
