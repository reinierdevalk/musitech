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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class represents a musical beam.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class Beam extends EventSpanner {

    private boolean aboveNotes;
    private double slope;
    private Chord dictator = null;
    private Chord[][] subBeamPos; // Chords where a sub-beam starts and ends

    /**
     * used for beams which start as (e.g.) 1/8 and end as (e.g.) 1/64. This is then drawn
     * as a fan
     */
    private Rational[] linearProgression;

    /** Adds an Event to the beam. */
    @Override
	public void add(Event ev) {
        if (ev == null)
            return;
        Rational r = ev.getDuration().toRational();
        if (r.getDenom() >= 8)
            super.add(ev);
        else {
            String errMsg = "\nEvent with duration " + r + " is not beamable";
            if (DebugState.DEBUG_SCORE) {
                throw new IllegalArgumentException("\nEvent with duration " + r
                                                   + " is not beamable");
            } else {
                System.out.println("WARNING in " + getClass() + getName()
                                   + " add(Event): " + errMsg);
            }

        }
    }

    /** Adds multiple events to this Beam. */
    public void add(List events) {
        for (int i = 0; i < events.size(); i++) {
            Event ev = (Event) (events.get(i));
            Rational r = ev.getDuration().toRational();
            if (r.getDenom() >= 8)
                super.add(ev);
            else
                throw new IllegalArgumentException("Event with duration " + r
                                                   + " is not beamable");
        }
    }

    @Override
	int arrange(int pass) {
        if (numEvents() < 2 || pass > 0)
            return 1;

        boolean visible = false;
        for (int i = 0; i < numEvents(); i++) {
            Event ev = get(i);
            if (ev.isVisible()) {
                visible = true;
                break;
            }
        }
        setVisible(visible);

        slope = computeSlope();
        //		showAngles();
        dictator = computeDictator();
        adaptStemLengths();

        // compute number of sub-beams
        int subBeamLevels = 0;
        for (int i = 0; i < numEvents(); i++)
            subBeamLevels = Math.max(subBeamLevels, getEvent(i).numberOfFlags());

        List subBeamPosVec = new ArrayList();
        for (int i = 0; i < subBeamLevels; i++) // iterate all sub-beam levels
        {
            int left = -1, right = -1;
            for (int j = 0; j < numEvents(); j++) // iterate all beam events
            {
                Event ev = getEvent(j);
                if (ev.numberOfFlags() >= i + 1)
                    if (left < 0)
                        left = right = j;
                    else
                        right = j;
                if (left >= 0 && (ev.numberOfFlags() < i + 1 || j == numEvents() - 1)) // sub-beam
                // found?
                {
                    //               System.out.println(i+". sub-beam from "+left+" to "+right);
                    subBeamPosVec.add(getEvent(left));
                    subBeamPosVec.add(getEvent(right));
                    left = right = -1;
                }
            }
        }
        int size = subBeamPosVec.size() / 2;
        subBeamPos = new Chord[size][2];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < 2; j++) {
                if (subBeamPosVec.get(2 * i + j) instanceof Chord)
                    subBeamPos[i][j] = (Chord) subBeamPosVec.get(2 * i + j);
            }

        return 1;
    }

    /**
     * Computes the beam direction.
     * 
     * @return 1 beam above the note heads, -1 below.
     */
    protected int computeDirection() {
        if (numEvents() == 0)
            return 0;
        if (getEvent(0).measure().numVoices() == 1) {
            double sum1 = 0, sum2 = 0;
            // number of decisively vertical positions above and below the middle line
            int count1 = 0, count2 = 0;
            for (int i = 0; i < numEvents(); i++) {
                if (getEvent(i) instanceof Chord
                    && ((Chord) getEvent(i)).isForcedStemDirection()) {
                    if (((Chord) getEvent(i)).isStemUp()) {
                        count1++;
                    } else {
                        count2++;
                    }
                } else {
                    int line = getEvent(i).beamDirectionLine();
                    if (line > 0) {
                        sum1 += line;
                        count1++;
                    } else if (line < 0) {
                        sum2 -= line;
                        count2++;
                    }
                }
            }
            double average1 = count1 > 0 ? sum1 / count1 : 0;
            double average2 = count2 > 0 ? sum2 / count2 : 0;
            aboveNotes = average1 < average2;
            return aboveNotes ? 1 : -1;
        }
        if (getEvent(0).getVoice() == 0) {
            aboveNotes = true;
            return 1;
        } else {
            aboveNotes = false;
            return -1;
        }
    }

    /** Computes the final slope of this Beam. */
    protected double computeSlope() {
        return 0.6 * MyMath.tanh(averageSlope()); // @@ TODO: fix the beam ends;
    }

    /** Computes the average slope of this beam by linear regression. */
    protected double averageSlope() {
        if (numEvents() == 0)
            return 0;

        // compute average x and y
        double xx = 0, yy = 0;
        int count = 0; // number of regression points
        for (int i = 0; i < numEvents(); i++) {
            Event ev = getEvent(i);
            if (ev instanceof Chord) // skip rests
            {
                Pair p = ev.regressionPoint();
                xx += p.getX();
                yy += p.getY();
                count++;
            }
        }
        if (count == 0)
            return 0;
        xx /= count;
        yy /= count;

        // linear regression starts here
        double numer = 0, denom = 1; // numerator and denominator of solution
        for (int i = 0; i < numEvents(); i++) {
            Event ev = getEvent(i);
            if (ev instanceof Chord) // skip rests
            {
                Pair p = ev.regressionPoint();
                double dx = p.getX() - xx;
                numer += dx * (p.getY() - yy);
                denom += dx * dx;
            }
        }
        return numer / denom;
    }

    /** Change the stem length so that they touch the beam */
    protected void adaptStemLengths() {
        Pair dicbp = dictator.beamPoint();
        int lineDist = dictator.staff().getLineDistance();
        for (int i = 0; i < numEvents(); i++)
            if (getEvent(i) instanceof Chord) {
                Chord c = (Chord) getEvent(i);
                Pair bp = c.beamPoint();
                Pair rp = c.regressionPoint();
                int length = (int) Math.abs(rp.getY() - ypos(bp.getX(), dicbp));
                c.setStemLength(length);
            } else {
                //                System.out.println(getClass().getName()+" event is of class
                // "+getEvent(i).getClass());
            }
    }

    /**
     * Change the stem directions. In contrast to unbeamed notes all beamed notes depend
     * on the beam's location.
     */
    protected void adaptStemDirections() {
        boolean stemUp = (computeDirection() > 0);
        for (int i = 0; i < numEvents(); i++)
            if (getEvent(i) instanceof Chord)
                ((Chord) getEvent(i)).setStemUp(stemUp);
    }

    /** Returns the Chord closest to the beam. */
    protected Chord computeDictator() {
        int j = 0;
        Object obj = null;
        // added checks because of classCastException
        do {
            obj = getEvent(j++);
        } while (j < numEvents() && !(obj instanceof Chord));
        Chord dic;
        if (obj instanceof Chord) {
            dic = (Chord) obj;
        } else
            return null;
        Pair firstPoint = dic.regressionPoint(); // position of first leading note head in
        // beam
        double max = 0;
        for (int i = 1; i < numEvents(); i++)
            if (getEvent(i) instanceof Chord) {
                Chord c = (Chord) getEvent(i);
                Pair rp = c.regressionPoint();
                double dy = rp.getY() - ypos(rp.getX(), firstPoint);
                dy *= (aboveNotes ? -1 : 1);
                if (dy > max) {
                    dic = c;
                    max = dy;
                }
            }
        return dic;
    }

    public Chord getDictator() {
        return dictator;
    }

    /*
     * protected double ypos (double x) { Event ev = getEvent(0); return (double)ev.absY() +
     * slope*(x - ev.absX()); }
     */

    protected double ypos(double x, Event ev) {
        return ev.absY() + slope * (x - ev.getXPos());
    }

    protected double ypos(double x, Pair p) {
        return p.getY() + slope * (x - p.getX());
    }

    @Override
	public void paint(Graphics g) {
        if (numEvents() < 2 || !isVisible())
            return;
        Event ev = getEvent(0);
        int lineDist = ev.staff().getLineDistance();
        int thickness = lineDist / 2 * (aboveNotes ? 1 : -1);
        int subBeamDist = (int) Math.round(3.0 * lineDist / 4.0) * (aboveNotes ? 1 : -1);

        int distance = -1;
        boolean leftToRight = false;
        HashMap levelsPerChord = new HashMap();

        int globalBeams = 0;
        for (int i = 0; i < subBeamPos.length; i++) {
            if (subBeamPos[i][0] != null && subBeamPos[i][1] != null) {
                int xleft = (int) Math.round(subBeamPos[i][0].beamPoint().getX());
                int xright = (int) Math.round(subBeamPos[i][1].beamPoint().getX());
                int yleft;

                int level = -1;

                Chord left = subBeamPos[i][0];
                Chord right = subBeamPos[i][1];

                if (left.getMetricTime().isGreaterOrEqual(right.getMetricTime())) {
                    Chord tmp = left;
                    left = right;
                    right = tmp;
                }

                //find and adjust the level of all chords between left and right:
                for (int j = 0; j < numEvents(); j++) {
                	// TODO this is a hack, needs to be checked!
                    if(!(getEvent(j) instanceof Chord)) 
                    	continue;
                    Chord chord = (Chord) getEvent(j);
                    if (chord.getMetricTime().isGreaterOrEqual(left.getMetricTime())
                        && chord.getMetricTime().isLessOrEqual(right.getMetricTime())) {
                        Integer iLevel;
                        if (levelsPerChord.containsKey(chord)) {
                            iLevel = (Integer) levelsPerChord.get(chord);
                        } else {
                            iLevel = new Integer(-1);
                        }
                        iLevel = new Integer(iLevel.intValue() + 1);
                        levelsPerChord.put(chord, iLevel);
                        level = Math.max(level, iLevel.intValue());
                    }
                }
                if (linearProgression != null) {
                    if (linearProgression[0].equals(new Rational(1, 8))) {
                        yleft = (int) Math.round(subBeamPos[0][0].beamPoint().getY());
                    } else {
                        throw new IllegalStateException("not supported yet");
                    }
                } else {
                    yleft = (int) Math.round(subBeamPos[i][0].beamPoint().getY() + level
                                             * subBeamDist);
                }
                int yright = (int) Math.round(ypos(xright, ev.beamPoint()) + level
                                              * subBeamDist);

                if (xleft != xright) { //this is a normal beam connecting two notes
                    distance = Math.abs(xleft - xright);
                    leftToRight = xleft > xright;
                    globalBeams++;
                } else { //this is only a part of a beam
                    assert distance != -1; //the part beam has to be processed after the
                                           // full
                    // one
                    int partBeamSize = Math.min(distance / 3, 2 * getEvent(0).staff()
                            .getLineDistance());
                    boolean syncope = isFirstInSyncope(left);
                    if(syncope)
                    	leftToRight=!leftToRight;
                    if (leftToRight)
                        xleft += partBeamSize;
                    else
                        xright -= partBeamSize;
                    if(syncope)
                    	leftToRight=!leftToRight;
                }

                int[] x = {xleft, xleft, xright, xright};
                int[] y = {yleft, yleft + thickness, yright + thickness, yright};

                Color oldColor = g.getColor();
                g.setColor(getColor());
                g.fillPolygon(x, y, 4);
                g.setColor(oldColor);
            }
        }
        /*
         * Pair p = dictator.beamPoint(); int xx = (int)p.getX(), yy = (int)p.getY();
         * g.drawOval(xx-5, yy-5, 10,10);
         */
    }
    
    private boolean isFirstInSyncope(Event e) {
		List<Event> events = new LinkedList<Event>();
		for (int i = 0; i < numEvents(); i++)
			events.add(getEvent(i));
		Collections.sort(events, new MetricalComparator());
		int index = events.indexOf(e);
		if (index > 0)
			return e.numberOfFlags() > events.get(index - 1).numberOfFlags() + 1;
		else if (index == 0)
			return e.numberOfFlags() > events.get(1).numberOfFlags();
		return false;
	}

    public Rational[] getLinearProgression() {
        return linearProgression;
    }

    public void setLinearProgression(Rational[] linearProgression) {
        if (!linearProgression[0].equals(new Rational(1, 8)))
            throw new IllegalArgumentException(
                    "progressions not starting at 1/8 are not supported yet");
        this.linearProgression = linearProgression;
    }
}