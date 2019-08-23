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
package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is used to contain Notes which belong to a N-Tuplet. The Notes
 * retain their original metricDuration. The metricDuration for the whole Object
 * (N-Tuplet) will be computed by this class.
 * 
 * @author collin
 * 
 * @hibernate.class table = "TupletContainer"
 * @hibernate.joined-subclass 
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */

public class TupletContainer extends SortedContainer implements Serializable {

    /** The arity (number of notes) in this container. */
    private byte arity;

    /** 
     * The regular number of notes in this Tuplets time.
     * The tuplet's note's effective duration is changed by mutliplying with regular/arity.
     */
    private byte regular;

    /**
     * The metricDuration of the whole N-Tuplet; can be set manually, otherwise
     * it will be computed
     */
    private Rational metricDuration = new Rational(-1, 1);

    /**
     * Main constructor; sets the arity (number of notes) for this tuplet is set.
     * 
     * @see SortedContainer#SortedContainer(de.uos.fmt.musitech.data.structure.Context) 
     * @param _context The context for this tuplet
     * @param _arity The arity of this tuplet.
     * @param _regular The regular number of notes.
     */
    public TupletContainer(Context _context, byte _arity, byte _regular) {
        this(_context,_arity);
        setRegular(_regular);
    }

    /**
     * Main constructor; sets the arity (number of notes) for this tuplet is set.
     * 
     * @see de.uos.fmt.musitech.data.structure.SortedContainer#SortedContainer(de.uos.fmt.musitech.data.structure.Context) 
     * @param context The context for this tuplet
     * @param arity The arity of this tuplet
     */
    public TupletContainer(Context context, byte arity) {
        super(context, Note.class, new MetricalComparator());
        setArity(arity);
    }

    public TupletContainer() {
        super(new Context(), Note.class, new MetricalComparator());
    }

    /**
     * This calls TupletContainer(context,arity) and additionaly sets the metricDuration of the N-Tuplet.
     * 
     * @see de.uos.fmt.musitech.data.structure.TupletContainer#TupletContainer(de.uos.fmt.musitech.data.structure.Context,
     *      byte) 
     * @param context The context for this tuplet
     * @param arity The arity of this tuplet
     * @param metricDuration The metricDuration of this tuplet
     */
    public TupletContainer(Context context, byte arity, Rational metricDuration) {
        this(context, arity);
        setMetricDuration(metricDuration);
    }

    public Rational calcMetricDuration(){ 
        Rational tDuration = Rational.ZERO;
	    for (Iterator iter = iterator(); iter.hasNext();) {
	        Note tChord = (Note) iter.next();
	        tDuration = tDuration.add(tChord.getMetricDuration());
	    }
	    return (metricDuration = tDuration.mul(regular,arity));
    }

    /**
     * This method returns the arity (number of notes) of the TupletContainer.
     * 
     * @return the arity
     * @hibernate.property
     */
    public byte getArity() {
        return arity;
    }

    /**
     * This method sets the arity for this tuplet.
     * 
     * @param byte The arity
     */
    public void setArity(byte arity) {
        this.arity = arity;
        this.regular = (byte)new Rational(arity).getFloorPower2().getNumer();
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.container.SortedContainer#add(java.lang.Object)
     */
    public boolean add(Object o) {
        typeCheck(o);
        Note note = (Note) o;

        if (size() == arity) {
            //TODO throw Exception, but which one?
            return false;
        }
        if (size() > 0) {
            if (!note.getScoreNote().getMetricDuration().isEqual(((Note) get(0)).getScoreNote().getMetricDuration())) {
                throw new IllegalArgumentException(
                                                   "The note has not the same duration as the notes already in the TupletContainer!");
            }
        }
        if (super.add(note)) {
//            if (size() == arity) { //the container is full: check if the notes
//                                   // follow each other
                for (int i = 0; i < size() - 1; i++) {
                    ScoreNote current = ((Note) get(i)).getScoreNote();
                    ScoreNote next = ((Note) get(i + 1)).getScoreNote();
                    if (!current.getMetricTime().add(current.getMetricDuration()).isEqual(next.getMetricTime())) {
                        //TODO: can't check that anymore. ask collin
                        //throw new IllegalArgumentException("The notes " + (i
                        // + 1) + " and " + (i + 2) + " are not following each
                        // other");
                    }
                }
//            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method sets the metricDuration of the whole tuplet.
     * 
     * @param metricDuration the metricDuration of the tuplet
     */
    public void setMetricDuration(Rational metricDuration) {
        this.metricDuration = metricDuration;
    }

    /**
     * This method computes and returns the metricDuration of the whole tuplet.
     * If no note has been entered yet, the metricDuration is 0. If the
     * metricDuration is set manually that value will be returned (@see
     * de.uos.fmt.musitech.data.structure.TupletContainer#setDuration(de.uos.fmt.musitech.utility.Rational)
     * 
     * @return the metricDuration of the tuplet
     * 
     * 
     * @hibernate.many-to-one name = "metricDuration" class =
     *                        "de.uos.fmt.musitech.utility.math.Rational"
     *                        foreign-key = "id" cascade = "all"
     *  
     */
    public Rational getMetricDuration() {
        if (size() == 0) {
            return Rational.ZERO;
        }
        if (metricDuration.isGreater(Rational.ZERO)) {
            return metricDuration;
        }
        // TODO Collin, hier die gesamte L?nge ausrechnen und den 
        // Rest anpassen !
        // Man kann nicht davon ausgehen, dass die Noten gleich
        // lang sind und dass es keine Pausen gibt.
        Rational noteDuration = ((Note) get(0)).getScoreNote().getMetricDuration();
        double power = Math.log(noteDuration.toDouble()) / Math.log(1 / 2); //log0.5(noteDuration)
        if (Math.floor(power) == power) { //power is an int => noteDuration =
                                          // 1/2^n => tuplet metricDuration can
                                          // be computed
            return noteDuration.mul(new Rational(arity - 1, 1));
        } else {
            throw new Error("The notes duration is not of the form 1/2^n. "
                            + "You have to set the tuplet duration manually with setDuration");
        }
    }

    /**
     * This method returns the metric onset time of the tuplet. 
     * It is equivalent to the metric onset of the first note inside the tuplet.
     * 
     * @return the metric onset time of the first note. If the container is
     *         empty, 0 is returned
     */
    public Rational getMetricTime() {
        if (size() == 0) {
            return Rational.ZERO;
        }
        return ((Note) get(0)).getScoreNote().getMetricTime();
    }
    
    /**
     * This method modifies the onsets of the individual notes, so that the onset + duration of the last
     * notes equals the onset of the first note + the duration of the whole tuplet.
     *
     */
    public void modifyOnsets() {
        Rational noteOnset = ((Note)get(0)).getScoreNote().getMetricTime();
        Rational realDuration = metricDuration.div(new Rational(
                size()));
        for (int i = 0; i < size(); i++) {
            ((Note)get(i)).getScoreNote().setMetricTime(noteOnset);
            noteOnset = noteOnset.add(realDuration); //increase the onset
                                                     // by (tupletDuration /
                                                     // numberOfNotes)
        }
    }
    /**
     * Gets the regular.
     * 
     * @return Returns the regular.
     */
    public byte getRegular() {
        return regular;
    }
    /**
     * Sets the regular.
     * 
     * @param regular The regular to set.
     */
    public void setRegular(byte regular) {
        this.regular = regular;
    }
}