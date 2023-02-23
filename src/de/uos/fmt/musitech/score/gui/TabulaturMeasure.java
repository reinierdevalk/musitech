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
 * Created on 20-Dec-2004
 *
 */
package de.uos.fmt.musitech.score.gui;

import java.util.HashMap;
import java.util.Iterator;

import de.uos.fmt.musitech.data.score.DualMetricAttachable;

/**
 * @author collin
 *
 */
public class TabulaturMeasure extends Measure implements ContentChangeListener {
	Measure shadowCaster;
	HashMap graphicalToNotation, notationToGraphical;

	public TabulaturMeasure(Measure measure, HashMap graphicalToNotation, HashMap notationToGraphical) {
		super();
		measure.addListener(this);
		shadowCaster = measure;
		this.graphicalToNotation = graphicalToNotation;
		this.notationToGraphical = notationToGraphical;
		
		for (Iterator iter = measure.iterator(); iter.hasNext();) {
			Object element = iter.next();
			contentAdded(element);
		}
	}
	
	@Override
	public int absX() {
		return shadowCaster.absX();
	}

	@Override
	public Clef activeClef() {
		return shadowCaster.activeClef();
	}
	
	@Override
	public void contentAdded(Object newContent) {
		add(new TabulaturLocalSim((LocalSim)newContent, graphicalToNotation));
		//add((LocalSim)newContent);
	}

	@Override
	public int arrange(int pass) {
		if (pass == 0) {
			clearCustomScoreObjects();
			for (Iterator iter = shadowCaster.getCustomScoreObjects().iterator(); iter.hasNext();) {
				CustomScoreObject element = (CustomScoreObject) iter.next();

				if ((new Boolean(false)).equals(element.getAttachable().getRenderingHint("allow duplication"))) {
					continue;
				}
				
				if (element instanceof CustomDualScoreObject) {
					CustomDualScoreObject dual = (CustomDualScoreObject)element;
					ScoreObject shadowMaster = this;
					DualMetricAttachable dma = (DualMetricAttachable)dual.getAttachable();
					Object leftAnker = notationToGraphical.get(dma.getAnker());
					Object rightAnker = notationToGraphical.get(dma.getRightAnker());
					
					if (leftAnker == null ||
						rightAnker == null) {
						System.err.println("skipping");
						continue;
					}
					
					HashMap realToShadow;
					if (leftAnker instanceof Chord &&
						rightAnker instanceof Chord) {
						realToShadow = TabulaturChord.getRealToShadow();
					}
					else {
						throw new IllegalArgumentException("cannot deal with ankers of type " + 
								                           leftAnker.getClass() + " and " + rightAnker.getClass());
					}
					
					ScoreObject shadowAnker = (ScoreObject)realToShadow.get(leftAnker);
					ScoreObject shadowRightAnker = (ScoreObject)realToShadow.get(rightAnker);
					DualMetricAttachable shadowDma = new DualMetricAttachable(leftAnker, rightAnker, dma.getSymbol());
					shadowDma.setRelativePosition(dma.getRelativePosition());
					shadowDma.setAlignment(dma.getAlignment());
					shadowDma.setDistance(dma.getDistance());
					CustomDualScoreObject shadowDual = new CustomDualScoreObject(shadowMaster, shadowDma);
					
					shadowDual.setAnker(shadowAnker);
					shadowDual.setRightAnker(shadowRightAnker);
					addCustomScoreObject(shadowDual);
				}
			}
		}
		return super.arrange(pass);
	}
	
	@Override
	public void contentRemoved(Object content) {
		
	}
	
}
