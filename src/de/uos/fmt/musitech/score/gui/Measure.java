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
import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a single-staff measure of music.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Measure extends ScoreContainer {
	private static int ID;
	int id;
	
	private int number = 0;
	private int numVoices = 0; // number of voices is this measure
	private Clef clef; // assigned clef
	private KeySignature keySignature; // assigned key signature
	private TimeSignature timeSignature; // assigned time signature
	private int rwidth = 0;
	private Barline rightBarline; // right barline (closing barline)
	private GlobalMeasure globalMeasure = null;
	private boolean paintNumbers = false;
	/**
	 * this is used for the "correct" metric time.
	 * If e.g. a score is broken into different systems because of linebreaks,
	 * this variable would not hold zero for the first measure in the second system.
	 * it would hold the original metric time for this measure (as it were without breaks)
	 */
	private Rational metricTime; 
	
	
	private Rational correctDuration;
	
	public Measure() {
		super();
		id = ID++;
	}
	
	public void setMetricTime(Rational metricTime) {
		this.metricTime = metricTime;
	}
	
	@Override
	int arrange(int pass) {
		int numPasses = 3;
		int max = Math.max(numPasses, super.arrange(pass));
		if (pass == 2) {
			if (clef != null)
				clef.arrange(pass);
			if (keySignature != null)
				keySignature.arrange(pass);
			/*
			int y = Integer.MAX_VALUE;
			for (int i = 0; i < numChildren(); i++) {
				y = Math.min(y, child(i).getLocation().y);
			}
			if (getClef() != null) {
				y = Math.min(y, getClef().absY());
			}
			*/
			ArrayList additionals = new ArrayList();
			if (getClef() != null)
				additionals.add(getClef());
			if (keySignature != null) {
				additionals.add(keySignature);
			}
			/*
			if (timeSignature != null) {
				additionals.add(timeSignature);
			}
			*/
			int[] minMaxY = getMinMaxY((ScoreObject[])additionals.toArray(new ScoreObject[]{}));
			setLocation(absX() - lwidth(), minMaxY[0]);
			//setSize(lwidth() + rwidth(), depth() + height());
			setSize(lwidth() + rwidth(), minMaxY[1] - minMaxY[0]);
		}
		else if (pass == 0) {
			if (clef != null)
				max = Math.max(max, clef.arrange(pass));
			if (keySignature != null)
				max = Math.max(max, keySignature.arrange(pass));
			if (timeSignature != null)
				max = Math.max(max, timeSignature.arrange(pass));
			if (rightBarline != null)
				max = Math.max(max, rightBarline.arrange(pass));
		}
		
		return max;
	}
	
	/**
	 * @return the LocalSim with the lowest (i.e. greatest) y-coordinate
	 * null is returned if this measure has no children
	 */
	LocalSim lowestChild() {
		int low = 0;
		LocalSim lowest = null;
		return lowest;
	}
	
	/**
	 * This method returns all Heads contained in this measure.
	 * It is mainly used by Staff to erase all aux-lines created by a measure.
	 * @return an array of heads
	 */
	Head[] getHeads() {
		List heads = new ArrayList();
		
		for (int i = 0; i < numChildren(); i++) {
			LocalSim lsim = (LocalSim)child(i);
			for (int j = 0; j < lsim.numChildren(); j++) {
				ScoreObject sobj = lsim.child(j);
				if (sobj instanceof Chord) {
					Chord chord = (Chord)sobj;
					for (int k = 0; k < chord.numChildren(); k++) {
						heads.add(((Pitch)chord.child(k)).getHead());
					}
				}
			}
		}
		return (Head[])heads.toArray(new Head[heads.size()]);
	}
	
	@Override
	public int depth() {
		int high = 0;
		for (int i = 0; i < numChildren(); i++) {
			LocalSim child = (LocalSim)child(i);
			high = Math.max(child.getLocation().y + child.depth() + child.height(), high); 
		}
		if (getClef() != null) {
			high = Math.max(high, getClef().absY() + getClef().depth());
		}

		return high - getLocation().y;
	}
	
	public void insert(Event event, Rational targetTime) {
      Rational attack = ((LocalSim)child(0)).getAttack();
      int i=0;      
      for (;i < numChildren(); i++) {
         LocalSim lsim = (LocalSim)child(i);
         if (lsim.getAttack().isGreaterOrEqual(targetTime))                   
            break;
      }
      //if (i >= num) @@ Baustelle
       
      
   }
      
   /** Searches for gaps between successive events <i>e<sub>n</sub></i> and 
    * <i>e<sub>n+1</sub></i>. If <i>attack</i>(<i>e<sub>n+1</sub></i>) - 
    * <i>attack</i>(<i>e<sub>n</sub></i>) > <i>duration</i>(<i>e<sub>n+1</sub></i>)
    * then there is a gap that must be filled with a rest.
   void fillGapsWithRests(Rational initialAttack) {
      Rational attack = initialAttack;
      for (int i = 0; i < numChildren(); i++) {
         LocalSim lsim = (LocalSim) child(i);
         assert lsim.getAttack().isGreaterOrEqual(initialAttack);
         if (lsim.getAttack().isGreater(initialAttack)) {
            for (int j = 0; j < lsim.numChildren(); j++) {
               int voice = ((Event) lsim.child(j)).getVoice();
               Duration diff = new Duration(lsim.getAttack().sub(initialAttack), 0);
               if (diff.isCompoundDuration()) {// can't the rest be displayed by a single symbol
                  Duration[] splittedDiff = diff.split();
                  for (int k = 0; k < splittedDiff.length; k++)
                     insert(new Rest(splittedDiff[k], 0), attack);
               }
               else // single (no compund) rest
                  insert(new Rest(diff, 0), attack);
            }
         }
      }
   }
   */
      

	/** Returns the attack time of the first event in this Measure. */
	public Rational attackTime() {
		if (!getChildren().isEmpty())
			return ((LocalSim) child(0)).getAttack();
		return Rational.ZERO;
	}

	public int getNumber() {
		return number;
	}
	void setNumber(int n) {
		number = n;
	}

	/** Returns the (global) clef of this Measure. */
	public Clef getClef() {
		return clef;
	}

	/** Returns the (global) key signature of this Measure. */
	public KeySignature getKeySignature() {
		return keySignature;
	}

	/** Returns the (global) time signature of this Measure. */
	public TimeSignature getTimeSignature() {
		return timeSignature;
	}

	/** Returns the right barline of this Measure. */
	public Barline getRightBarline() {
		return rightBarline;
	}

	/** Changes the (global) clef of this Measure. */
	public void setClef(Clef clef) {
		if (clef != null) {
			clef.setParent(this);
			this.clef = clef;
		}
		if (scale != 1)
			clef.setScale(scale);
	}

	/** Changes the (global) key signature of this Measure. */
	public void setKeySignature(KeySignature ks) {
		if (ks != null) {
			ks.setParent(this);
			this.keySignature = ks;
		}
		if (scale != 1)
			keySignature.setScale(scale);
	}

	/** Changes the (global) time signature of this Measure. */
	public void setTimeSignature(TimeSignature ts) {
		if (ts != null) {
			ts.setParent(this);
			this.timeSignature = ts;
		}
		if (scale != 1)
			timeSignature.setScale(scale);
	}

	@Override
	public Measure measure() {
		return this;
	}

	/** Returns the LocalSim of this Measure that attacks at a given time. 
	 * If no corresponding element is found, null is returned */
	public LocalSim localSim(Rational attack) {
		for (int i = 0; i < numChildren(); i++) {
			LocalSim sim = (LocalSim) child(i);
			if (sim.getAttack().isEqual(attack))
				return sim;
			if (sim.getAttack().isGreater(attack))
				return null;
		}
		return null;
	}

	LocalSim localSimInTime(Rational time) {
		for (int i = 0; i < numChildren(); i++) {
			LocalSim sim = (LocalSim) child(i);
			Rational startTime = sim.getAttack();
			Rational endTime = startTime.add(sim.minDuration());
			if (time.isGreaterOrEqual(startTime) && time.isLessOrEqual(endTime))
				return sim;
			if (startTime.isGreater(time))
				return null;
		}
		return null;
	}

	/** Returns the number of voices in this Measure. */
	public int numVoices() {
		return numVoices;
	}

	Measure predecessor() {
		Staff staff = staff();
		if (number == 0 || staff == null)
			return null;
		return (Measure) staff.child(number - 1);
	}

	/** Returns the Clef that affects the notes in this Staff. */
	Clef activeClef() {
		if (clef != null)
			return clef;
		if (measure() == null || measure().getNumber() == 0)
			return null;
		return predecessor().activeClef();
	}

	/** Returns the KeySignature that affects the notes in this Staff. */
	KeySignature activeKeySignature() {
		if (keySignature != null)
			return keySignature;
		if (measure() == null || measure().getNumber() == 0)
			return null;
		return predecessor().activeKeySignature();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintBackground(g);
		if (clef != null)
			clef.paint(g);
		if (keySignature != null)
			keySignature.paint(g);
		if (timeSignature != null)
			timeSignature.paint(g);
		if (rightBarline != null)
			rightBarline.paint(g);
		if (paintNumbers) {
			paintNumber(g);
		}
		//drawFrame(g);
	}

	void paintNumber(Graphics g) {
		int max = 0;
		String s = (number + 1) + "";
		for (int i = 0; i < s.length(); i++) {
			max = Math.max(max, MusicGlyph.depth(staff().getLineDistance(), s.charAt(i)));
		}
		
		g.drawString(s, absX(), absY() - max);
	}
	
	@Override
	Class parentClass() {
		return Staff.class;
	}

	/** Changes the right barline of this measure. */
	public void setRightBarline(Barline b) {
		rightBarline = b;
		rightBarline.setParent(this);
	}

	/** Returns a string representation of this Measure. */
	@Override
	public String toString() {
		String res = new String(super.toString());
		if (rightBarline != null)
			res += rightBarline + "\n";
		return res;
	}

	void setRWidth(int width) {
		this.rwidth = width;
	}

	@Override
	public int rwidth() {
		/*      int res = 0;
		      if (clef != null)	
		      	res = clef.rwidth() + Clef.BACK_GAP * staff().getLineDistance();
		  		if (numChildren() > 0) 
				{
				   int x = child(0).getXPos();
					for (int i=1; i < numChildren(); i++)
					{
						res += child(i).getXPos() - x;
						x = child(i).getXPos();
					}
				}
		      return res; */
		return rwidth;
	}

	int initialSpace() {
		int res = 0;
		if (clef != null)
			res += clef.rwidth() + Clef.BACK_GAP * staff().getLineDistance();
		if (keySignature != null)
			res += keySignature.rwidth();
		return res;
	}

	@Override
	public boolean add(ScoreObject obj) {
		LocalSim lsim = (LocalSim) obj;
		super.addMetrical(lsim);
		if (lsim.numChildren() > numVoices)
			numVoices = lsim.numChildren();
      return true;
	}

	void lsimChanged(LocalSim lsim) {
		if (lsim.numChildren() > numVoices)
			numVoices = lsim.numChildren();
	}

	/** Returns the duration of this measure. */
	public Rational duration() {
		// @@ geht jetzt auch ohne Aufsummieren: Einsatzzeit des letzten LocalSim + seine Dauer
		Rational sum = Rational.ZERO;
		for (int i = 0; i < numChildren(); i++)
			sum = sum.add(((LocalSim) child(i)).minDuration());
		return sum;
	}
	
	public Rational getCorrectDuration() {
		Rational sum = Rational.ZERO;
		for (int i = 0; i < numChildren(); i++)
			sum = sum.add(((LocalSim) child(i)).getCorrectDuration());
		return sum;
	}

	public Rational[] minMaxTime() {
		return minMaxTime(false);
	}
	
	/** Returns the minimum and maximum attack time of all events belonging to 
	 *  this measure. 
	 *  @return array with 2 elements [0]=minimum, [1]=maximum */
	public Rational[] minMaxTime(boolean addMeasureMetric) {
		if (numChildren() == 0)
			return null;
		Rational[] ret = new Rational[2];
		ret[0] = ((LocalSim) child(0)).getAttack();
		ret[1] = ((LocalSim) child(-1)).getAttack();
		if (addMeasureMetric) {
			ret[0] = ret[0].add(getMetricTime());
			ret[1] = ret[1].add(getMetricTime());
		}
 		return ret;
	}
	
	int getXForMetricTime(Rational metric) {
		LocalSim lastLocalSim = null;
		for (int i = 0; i < numChildren(); i++) {
			LocalSim currentLocalSim = (LocalSim)child(i);
			if (currentLocalSim.getAttack().equals(metric))
				return child(i).absX();
			if (currentLocalSim.getAttack().isGreater(metric)) {
				if (lastLocalSim != null) {
					Rational smallDistance = metric.sub(lastLocalSim.getAttack());
					Rational largeDistance = currentLocalSim.getAttack().sub(lastLocalSim.getAttack());
					double ratio = smallDistance.div(largeDistance).toDouble();
					int graphicalDistance = currentLocalSim.absX() - lastLocalSim.absX();
					return lastLocalSim.absX() + (int)(graphicalDistance * ratio);
				}
				return absX();
			}
			lastLocalSim = currentLocalSim;
		}
		return absX();
	}
	
	
	/**
	 * @return Returns the globalMeasure.
	 */
	public GlobalMeasure getGlobalMeasure() {
		return globalMeasure;
	}
	/**
	 * @param globalMeasure The globalMeasure to set.
	 */
	public void setGlobalMeasure(GlobalMeasure globalMeasure) {
		this.globalMeasure = globalMeasure;
	}
	
	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		if (clef != null)
			clef.setScale(scale);
		if (keySignature != null)
			keySignature.setScale(scale);
		if (timeSignature != null)
			timeSignature.setScale(scale);
	}
	
	public boolean isPaintNumbers() {
		return paintNumbers;
	}
	
	public void setPaintNumbers(boolean paintNumbers) {
		this.paintNumbers = paintNumbers;
	}
}