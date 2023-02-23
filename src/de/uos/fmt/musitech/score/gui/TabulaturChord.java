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

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.score.util.Pair;

/**
 * @author collin
 *
 */
public class TabulaturChord extends Chord implements ContentChangeListener {
	private static HashMap realToShadow = new HashMap();
	
	private HashMap graphicalToNotation;
	private Chord shadowCaster;
	private static int ID = 0;
	private int id;
	
	public TabulaturChord(Chord shadowCaster, HashMap graphicalToNotation) {
		super(shadowCaster.getDuration(), shadowCaster.getVoice());
		id = ID++;
		this.shadowCaster = shadowCaster;
		this.graphicalToNotation = graphicalToNotation;
		
		for (Iterator iter = shadowCaster.iterator(); iter.hasNext();) {
			Object element = iter.next();
			contentAdded(element);
		}
		
		shadowCaster.addListener(this);
		realToShadow.put(shadowCaster, this);
	}

	private List addedSlurs = new ArrayList();
	@Override
	public int arrange(int pass) {
		NotationChord n = (NotationChord)graphicalToNotation.get(shadowCaster);
		if (n.getRenderingHint("tabulatur note") != null &&
			pass == 0) {
			for (Iterator iter = addedSlurs.iterator(); iter.hasNext();) {
				Slur slur = (Slur) iter.next();
				shadowCaster.system().removeSlur(slur);
			}
			addedSlurs.clear();
			
			List slurs = shadowCaster.getSlurs();
			for (Iterator iter = slurs.iterator(); iter.hasNext();) {
				Slur slur = (Slur) iter.next();
				
				if (slur.isPullDown() ||
					slur.isPullDown())
					continue;

				if (slur.indexOf(shadowCaster) != 0) //only deal with slurs where the shadowCaster is the left anker.. This avoids duplicates 
					continue;
				
				Slur shadowSlur = new Slur();
				for (int i = 0; i < slur.numEvents(); i++) {
					Event ev = slur.get(i);
					if (ev instanceof Chord) {
						TabulaturChord shadow = (TabulaturChord)realToShadow.get(ev);
						if (shadow != null)
							shadowSlur.add(shadow);
					}
					else {
						System.err.println("shadowing a slur to a " + ev.getClass() + " is not implemented yet");
					}
				}
				if (shadowSlur.numEvents() >= 2) {
					shadowSlur.setForceAbove(true);
					shadowCaster.system().addSlur(shadowSlur);
					addedSlurs.add(shadowSlur);
				}
			}
		}
		return super.arrange(pass);
	}
	
	@Override
	public void paint(Graphics g) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			ScoreObject element = (ScoreObject) iter.next();
			element.paint(g);
		}
		if (shadowCaster.getTremolo()>0)
			paintTremolo(g,shadowCaster.getTremolo());
	}
	
	@Override
	public void contentAdded(Object newContent) {
		Pitch pitch = (Pitch)newContent;
		add(new TabulaturPitch(pitch, graphicalToNotation));
	}
	
	@Override
	public void contentRemoved(Object content) {
		
	}
	
	@Override
	Pair slurPoint(boolean left, boolean above, boolean atStem) {
		TabulaturPitch pitch = (TabulaturPitch)child(0);
		int topPullUpY = pitch.getTopPullUpY();
		int x = absX();
		if (topPullUpY == 0) {
			topPullUpY = absY();
		}
		else { //we have a pull up
			x += 15; //TODO: hack!!!!!!!!!!!!!!!!!!!!
		}
		return new Pair(x, topPullUpY);
	}
	
	
	public static HashMap getRealToShadow() {
		return realToShadow;
	}
}
