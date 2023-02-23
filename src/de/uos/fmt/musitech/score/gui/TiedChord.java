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

/** This class represents a musical tie. In contrast to slurs ties can only be 
 * attached to 2 adjacent chords with identical pitches. Furthermore a TiedChord can  
 * own multiple parallel bows if more than one note heads are tied together.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class TiedChord extends Chord {
	private Chord successor = null;
	private Tie[] ties = null;

	/** Constructs a TiedChord out of the given Duration and voice number. */
	public TiedChord(Duration dur, int voice) {
		super(dur, voice);
		//this.successor = successor;
	}

	/** Constructs a TiedChord of a given Chord c. All children (pitches) contained
	 * in c are moved to this TiedChord, so c becomes invalid. */
	TiedChord(Chord c) {
		super(c.getDuration(), c.getVoice());
		c.movePropertiesTo(this);
	}

	/** Sets the successing chord which should tied to this TiedChord. */
	public void setSuccessor(Chord successor) {
		this.successor = successor;
		//boolean ok = true; // true while equal pitches can be tied
        ties = new Tie[Math.min(numChildren(), successor.numChildren())];
        for (int i = 0; i < numChildren(); i++) {
        	for (int j = 0; j < successor.numChildren(); j++) {
				Pitch pitch1 = (Pitch)child(i);
				Pitch pitch2 = (Pitch)successor.child(j);
				if (pitch1.equals(pitch2)) {
					ties[i] = new Tie(pitch1.getHead(), pitch2.getHead());
					ties[i].setParent(pitch1);
					break;
				}
        	}
        }
        /*
		for (int i=0; i < numChildren() && ok; i++) {
			if (i < successor.numChildren()) {
				Pitch pitch1 = (Pitch)child(i);
				Pitch pitch2 = (Pitch)successor.child(i);
				ok &= pitch1.equals(pitch2);
				if (ok) {
					ties[i] = new Tie(pitch1.getHead(), pitch2.getHead());
					ties[i].setParent(pitch1);               
				}
			}
		}
		if (!ok)
			throw new IllegalArgumentException("TiedChord.setSuccessor: ties between chords of different pitches not yet supported");                        
        */
	}

	/** Returns the successor of this Chord. This is the Chord where 
	 *  the right tie end is connected to. */
	public Chord getSuccessor() {
		return successor;
	}

	/** Draws this TiedChord onto the given graphics context. */
	@Override
	public void paint(Graphics g) {
		super.paint(g); 
		// if no successor is available no tie has to be drawn
		if (successor == null || ties == null)
			return;
		// draw the ties
		for (int i=0; i < ties.length; i++){
		    
  			if(ties[i] != null){
  				ties[i].paint(g);
  			} else {
  			    System.err.println("WARNING in class TiedChord ties["+i+"] is null.");
  			}

		}
	}
	
   @Override
public int arrange(int pass) {
      int res = super.arrange(pass);
      
      if (ties != null) { //it is possible that our successor are not known, because the following measures have not been rendered yet 
      	for (int i=0; i < ties.length; i++) { 
      		if (pass == 0) { // we can determine the tie direction in the first pass
      			boolean up = (measure().numVoices()==1 ? i>=(ties.length+1)/2 : stemUp);
      			if(ties[i] != null){
      			    ties[i].setDirection(up);
      			} else {
      			    System.err.println("TiedChord.arrange("+pass+") ties["+i+"] is null.");
      			}
      		}
  			if(ties[i] != null){
  			    res = Math.max(res, ties[i].arrange(pass));
  			} else {
  			    System.err.println("TiedChord.arrange("+pass+") ties["+i+"] is null.");
  			}
      	}
      }
      return res;        
	}
}
