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

import de.uos.fmt.musitech.utility.math.Rational;

/** This class optimizes the notation of rhythmic structures based on the
 * given time signature. This currently includes 3 aspects:
 * <ul>
 * <li>clarification of syncopated notes/chords and misformed tied events</li>
 * <li>clarification of syncopated rests and misformed rest sequences</li>
 * <li>beam creation if possible</li>
 * </ul>
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class RhythmClarifier
{
   private Measure measure;      // measure to be clarified
   private MeasureMetric metric; // current measure metric
   private Event[] eventGrid;  
   private boolean[] dividedAt;
    
   public void clarify (Measure measure) {
      if (measure == null || measure.getTimeSignature() == null)
         return;
      if (metric == null || measure.getTimeSignature() != metric.getTimeSignature())
         metric = new MeasureMetric(measure.getTimeSignature(), new Rational(1,16)); // @@
      dividedAt = new boolean[metric.getNumBeats()];
      eventGrid = new Event[metric.getNumBeats()];
      //events = new Event[metric.getNumBeats()];
      for (int voice=0; voice < measure.numVoices(); voice++) {
      	//EventVector events // @@ Baustelle
      }
      clarifyNotes();
      clarifyRests();
      createBeams();
   }

   /** Splits syncopated notes/chors if necessary. For further information
    * see [Gieseking, pp 106, 255]. */      
   void clarifyNotes () {
      int n = metric.getNumBeats();
      for (int left=1; left < n-1; left++) {
			if (eventGrid[left] instanceof Chord) {
				int strongerPos2  = 0;
				int strongerPos3  = 0;
				int attackPos     = left;
				int equalWheights = 0;
				int right         = 0;
				for (right=left+1; right < n && eventGrid[right] == null; right++) {
					if (metric.weight(left) == metric.weight(right))
						equalWheights++;
					if (strongerPos3 == 0 && metric.weight(right) > metric.weight(left))
						strongerPos3 = right;
					if (metric.weight(right)-metric.weight(left) > 1) {
						divideAt(right);
						strongerPos2 = 0;
						left = right;
					}
					else if (metric.weight(right)-metric.weight(left) == 1) {
						if (strongerPos2 > 0) {
							divideAt(strongerPos2);
							left = right;
							strongerPos2 = -1;
						}
						else if (strongerPos2 == 0)
							strongerPos2 = right;
					}
				}
				if (strongerPos3 > left && metric.weight(attackPos) > metric.weight(right % n))
					divideAt(strongerPos3);
				else if (strongerPos3 > left && equalWheights > 0 
							&& metric.prevStrongerWeight(left) - metric.weight(left) == 1)
					divideAt(strongerPos3);
			}
      }
   }


   /** Splits syncopated rests if necessary. For further information
    * see [Gieseking, pp 112, 256]. */      
   void clarifyRests () {
      int n = metric.getNumBeats();
      int diff = 0;
      int equalRests = 1;
      int strongestRight = 1;
      for (int left=0; left < n-1; left++) {
         int attackPos;
         int wl = metric.symmetricWeight(left);
         int wr;
         if (eventGrid[left] instanceof Rest) {
            attackPos = left;
            strongestRight = left+1;
            int right;
            for (right = left+1; right < n && eventGrid[right] == null; right++) {
               wl = metric.symmetricWeight(left);
               wr = metric.weight(right);
               if (wr >= wl) {
                  if (right-left == diff)
                     equalRests++;
                  else {
                     equalRests = 1;
                     diff = right-left;
                  }
                  divideAt(right);
                  left = right;
                  strongestRight = 0;
               }
               if (strongestRight > 0 && wr > metric.weight(strongestRight))
                  strongestRight = right;
            }
            if (right-left == diff)
               equalRests++;
            wr = metric.symmetricWeight(right % n);
            if (strongestRight > 0 && strongestRight < right && wl-wr > 1) {
               if (strongestRight-left == diff)
                  equalRests++;
               else {
                  equalRests = 1;
                  diff = right-left;
               }
               divideAt(strongestRight);
               if (right-strongestRight == diff)
                  equalRests++;
               left = right;
               strongestRight = 0;
            }
            if (equalRests == 4)
               combine4Rests(attackPos, right);
         }
      }
   }

   /** Create beam groups of flagged note sequences if possible. For further
    *  information see [Gieseking, pp.118, 258]. */
   void createBeams () {      
      boolean found = false;        // no beam group found yet
      int begin = -1, end = -1;     // start and end position of beam
      int wmin = Integer.MAX_VALUE; // smallest weight of beam group
      for (int i=0; i <= eventGrid.length; i++) {
         int j = i % eventGrid.length;
         if (eventGrid[j] == null && i == j)
            continue;
            
         int w = metric.symmetricWeight(j);
         if (eventGrid[j] instanceof Chord && eventGrid[j].getDuration().toRational().isLess(1,4)) {
            wmin = Math.min(wmin, w);
            if (begin < 0) { // no beam begin found yet
               if (!(eventGrid[j] instanceof TiedChord))
                  begin = end = j;
            }
/*            else {
               if (j == i)
                  eventGrid[end] // @@ Baustelle*/
         }
      }
   } 
   
   void divideAt (int beat) {      
   }
   
   /** Combines a sequence of 4 similar rests that are a result of the rest 
    * clarification algorithm. A notation rule says that equal rests must not
    * be notated in a row. So this method repairs such intermediate results. */
   void combine4Rests (int left, int right) {
      int strongestPos = left;
      for (int i=left+1; i < right; i++)
         if (dividedAt[i] && metric.weight(i) > strongestPos)
            strongestPos = i;
      eventGrid[strongestPos] = null;
//      measure.mergeLocalSims(strongestPos * metric.getBeatDistance(), 1); // @@
   }
}
