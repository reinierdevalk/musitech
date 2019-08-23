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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.performance.rendering.MidiRendering;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.utility.EqualsUtil;
import de.uos.fmt.musitech.data.utility.IEquivalence;
import de.uos.fmt.musitech.framework.editor.Editable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Symbolic information on a musical note
 * 
 * @author TW / MG
 * @version $Revision: 8542 $, $Date: 2013-08-20 20:32:45 +0200 (Tue, 20 Aug 2013) $
 * 
 * Changes for Hibernating (by Alexander Kolomiyets)
 * 
 * @hibernate.class table="ScoreNote"
 * @hibernate.query name = "ScoreNote.getScoreNotebyDiatonic" query = "from
 *                  ScoreNote as note where note.diatonic = :dia"
 *  
 */
public class ScoreNote extends ScorePitch implements Metrical, java.io.Serializable, Containable, Editable, Cloneable, MObject, IEquivalence {

	private static final long serialVersionUID = -4079346674368981595L;
	private Rational metricTime = Rational.ZERO.getClone();
    private Rational metricDuration = new Rational(1, 4);
    private ScoreNote tiedNote;
    private Rational tupletDivision;
    private MidiRendering midiRendering;
    private int detune = 0; 
    private int transposition = 0; 

    //	Unique ID for this object.
    private Long uid;

    /**
     * @see java.lang.Object#hashCode()
     */
    /*
     * public int hashCode() { return (int) uid; }
     */

//    /** diatonic note a-g or r for rest */
//    private char diatonic = 'c';
//
//    /** octave, default 1 is middle c octave. */
//    private byte octave = 1;
//
//    /** alteration, 0 natural, 1 sharp, -1 flat, 2 double sharp, -2 double flat. */
//    private byte alteration = 0;

    /**
     * Returns a string for a given numeric encoding of an alteration.
     * 
     * @param acc The alteration encoded as a byte.
     * @return The string representation.
     */
    public static String convertAccidentalToString(int acc) {
    	switch (acc) {
    	case -2:
    		return "double flat";
    	case -1:
    		return "flat";
    	case 0:
    		return "natural";
    	case 1:
    		return "sharp";
    	case 2:
    		return "double sharp";
		default:
			return "undefined alteration";
    	}
    }
    
    /**
     * includes all accents for this score note. For possible values
     * 
     * @see de.uos.fmt.musitech.score.gui.Accent
     */
    private byte[] accents = null;

	public static String convertAccentToString(byte acc) {
		switch (acc) {
		case Accent.STACCATO:
			return "staccato";
		case Accent.MARCATO:
			return "marcato";
		case Accent.PORTATO:
			return "portato";
		case Accent.TRILL:
			return "trill";
		case Accent.MORDENT_UP:
			return "mordent up";
		case Accent.MORDENT_DOWN:
			return "mordent down";
		case Accent.TURN:
			return "turn";
		case Accent.DOWN_BOW:
			return "down-bow";
		case Accent.UP_BOW:
			return "up-bow";
		default:
			return null;									
		}
	}

    
    /**
     * Compares the public properties of the given Note object with this one. 
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object obj) {
        return ObjectCopy.comparePublicProperties(this, obj);
    }
    

    /**
     * Clone this object.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
	public Object clone() {
		try {
			ScoreNote c = (ScoreNote) super.clone();
			c.metricTime = c.metricTime.getClone();
			c.metricDuration = c.metricDuration.getClone();
			return c;
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * Method getTiedNote. Can be overridden to implement lists of Notes, e.g.
     * for tied notes corresponding to one performance note. In this state it
     * returns the note set with setTiedNote, or null.
     * 
     * @return ScoreNote
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * @hibernate.many-to-one name = "tiedNote" class =
     *                        "de.uos.fmt.musitech.data.score.ScoreNote"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    public ScoreNote getTiedNote() {
        return tiedNote;
    }

    /**
     * This method sets the tied note. This is the note with which this is note
     * is tied by an arc in score notation.
     * 
     * @param tiedNote
     *            the Note following this note, to which this is tied.
     */
    public void setTiedNote(ScoreNote tiedNote) throws IllegalArgumentException {
        if (tiedNote == this) {
            throw new IllegalArgumentException("you cannot tie a ScoreNote to itself.");
        }
        this.tiedNote = tiedNote;
    }

    private boolean valid(Rational time, Rational duration) throws IllegalArgumentException {
        return validTime(time) && validDuration(duration);
    }

    private boolean validTime(Rational time) throws IllegalArgumentException {
        if (!time.isGreaterOrEqual(Rational.ZERO))
            throw new IllegalArgumentException(time + " is not a legal attack time (must be not less than 0).");
        return true;
    }

    private boolean validDuration(Rational duration) throws IllegalArgumentException {
        if (!duration.isGreaterOrEqual(Rational.ZERO))
            return false;
        return true;
    }

    static boolean checkDiatonic(char c) throws IllegalArgumentException {
        if (!(c < 'a' || c > 'g') || c == 'r') {
            return true;
        } else {
            return false;
        }
    }

    private boolean valid(Rational time, Rational duration, char dia) throws IllegalArgumentException {
        if (!valid(time, duration))
            return false;
        if (!checkDiatonic(dia))
            return false;
        return true;
    }

    public ScoreNote() {
    }

    /**
     * Creates a new ScoreNote with the given properties. All object arguments 
     * are used as references, therefore later changes to these object will have 
     * an effect on this note.  
     *  
     *  
     * @param ons The onset time, must not be negative.
     * @param dur The duration of this note, must not be negative.
     * @param dia The diatonic note name (one of 'a'-'g' or 'r' for rest). 
     * @param oct The octave (0 is the middle C octave, i.e. the one starting with C4 and containing the A with 440Hz).
     * @param alt The alteration, where negative numbers express flats and positive numbers express sharps. E.g. -1 is flat and +2 is double sharp.
     */
    public ScoreNote(Rational ons, Rational dur, char dia, int oct, int alt) {
        valid(ons, dur, dia); //check validity... IllegalArgumentException is
                              // thrown if it is not legal
        metricTime = ons;
        setMetricDuration(dur);
        setDiatonic(Character.toLowerCase(dia));
        alteration = alt;
        octave = oct;

    }

    /**
     * Creates a new ScoreNote with the given properties. All object arguments 
     * are used as references, therefore later changes to these object will have 
     * an effect on this note.  
     *  
     *  
     * @param ons The onset time, must not be negative.
     * @param dur The duration of this note, must not be negative.
     * @param dia The diatonic note name (one of 'a'-'g' or 'r' for rest). 
     * @param oct The octave (0 is the middle C octave, i.e. the one starting with C4 and containing the A with 440Hz).
     * @param alt The alteration, where negative numbers express flats and positive numbers express sharps. E.g. -1 is flat and +2 is double sharp.
     */
    public ScoreNote(Rational ons, Rational dur, char dia, byte oct, byte alt) {
        valid(ons, dur, dia); //check validity... IllegalArgumentException is
                              // thrown if it is not legal
        metricTime = ons;
        setMetricDuration(dur);
        setDiatonic(Character.toLowerCase(dia));
        alteration = alt;
        octave = oct;

    }

    public ScoreNote(final ScorePitch pitch, final Rational ons, final Rational dur) {
        if (!valid(ons, dur))
            throw new IllegalArgumentException(getClass() + "invalid ons: " + ons + ", dur: " + dur);
        metricTime = ons;
        setMetricDuration(dur);
        setDiatonic(pitch.getDiatonic());
        alteration = pitch.getAlteration();
        octave = pitch.getOctave();
    }

    public ScoreNote(ScorePitch pitch, Rational dur) {
        if (!valid(Rational.ZERO, dur))
            throw new IllegalArgumentException();
        setMetricDuration(dur);
        setDiatonic(pitch.getDiatonic());
        alteration = pitch.getAlteration();
        octave = pitch.getOctave();

    }

//    /**
//	 * Insert the method's description here. Creation date: (10.1.2002 18:15:45)
//	 * 
//	 * @return byte
//	 * 
//	 * Changes for Hibernating (by Alexander Kolomiyets)
//	 * 
//	 * @hibernate.property
//	 * @deprecated Use {@link #getAlteration()} instead
//	 * 
//	 *  
//	 */
//	public int getAccidental() {
//		return getAlteration();
//	}


//	/**
//     * Insert the method's description here. Creation date: (10.1.2002 18:15:45)
//     * 
//     * @return byte
//     * 
//     * Changes for Hibernating (by Alexander Kolomiyets)
//     * 
//     * @hibernate.property
//     * 
//     *  
//     */
//    public int getAlteration() {
//        return alteration;
//    }

 
//    /**
//     * Diatonic note name indicating the base pitch.
//     * 
//     * @return char
//     * 
//     * Changes for Hibernating (by Alexander Kolomiyets)
//     * 
//     * @hibernate.property
//     *  
//     */
//    public char getDiatonic() {
//        return diatonic;
//    }

//    /**
//     * Insert the method's description here. Creation date: (10.1.2002 18:11:33)
//     * 
//     * @return byte
//     * 
//     * Changes for Hibernating (by Alexander Kolomiyets)
//     * 
//     * @hibernate.property
//     *  
//     */
//    public byte getOctave() {
//        return octave;
//    }

    /**
     * Returns the onset of the score note.
     * 
     * @return Rational
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * @hibernate.many-to-one name = "metricTime" class =
     *                        "de.uos.fmt.musitech.utility.math.Rational"
     *                        cascade = "all"
     *  
     */
    public Rational getMetricTime() {
        return metricTime;
    }

    /**
	 * Sets the accidental of this note.
	 * 
	 * @param newAccidental
	 *            byte
	 * @deprecated Use {@link #setAlteration(byte)} instead
	 */
	public void setAccidental(byte newAccidental) {
		setAlteration(newAccidental);
	}


	/**
     * Sets the accidental of this note as 0 for natural, +1 for each sharp, and -1 for each flat.
     * 
     * @param newAccidental 
     */
    public void setAlteration(int newAccidental) {
        alteration = newAccidental;
    }

    /**
     * Diatonic note name indicating the base pitch, is converted to lower case.
     * 
     * @param newDiatonic
     *            char a-g or r for rest
     */
    public void setDiatonic(char newDiatonic) {
        char tmpDia = Character.toLowerCase(newDiatonic);
        if (tmpDia == 'h') {
            tmpDia = 'b';
        }
        if ((tmpDia < 'a' | tmpDia > 'g') && tmpDia != 'r')
            throw new IllegalArgumentException(this.getClass() + ", setDiatonic(): argument is not a note-name.");
        diatonic = tmpDia;
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:06:20)
     * 
     * @param newDuration
     *            Rational
     */
    public void setMetricDuration(Rational newDuration) {
        if (validDuration(newDuration)) {
            metricDuration = newDuration;
            metricDuration.reduce();
        }
    }

    /**
     * Sets the octave of this score note, 1 is middle C octave 
     * (MIDI 60-71, 'eingestrichen' in German terminology).
     * 
     * @param newOctave  byte
     */
    public void setOctave(int newOctave) {
        octave = newOctave;
    }

    /**
     * Sets the metric time (onset) of the score note.
     * 
     * @param newOnset
     *            Rational
     */
    public void setMetricTime(Rational newOnset) {
        if (validTime(newOnset)) {
            metricTime = newOnset;
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
    	if(DebugState.DEBUG)
    		sb.append(super.toString());
        sb.append("ScoreNote ");
        sb.append(diatonic);
        if (alteration >= 0)
            for (int i = 0; i < alteration; i++)
                sb.append("#");
        else
            for (int i = 0; i > alteration; i--)
            	sb.append("b");
        if(diatonic != 'r')
        	sb.append( octave); 
        sb.append(", onset="); 
        sb.append(metricTime);
        sb.append(", dur=");
        sb.append(metricDuration);
        return sb.toString();
    }

    /**
     * Creates a PerformanceNote from this ScoreNote.
     * 
     * @param mt
     * @return PerformanceNote
     */
    public PerformanceNote toPerformanceNote(MetricalTimeLine mt) {

        PerformanceNote pn = new PerformanceNote();

        int midi_pitch = getMidiPitch();

        long midi_ts = mt.getTime(getMetricTime());
        long midi_ts_end = mt.getTime(getMetricTime().add(getMetricDuration()));

        long midi_duration = midi_ts_end - midi_ts;

        pn.setPitch(midi_pitch);
        pn.setDuration(midi_duration);
        pn.setTime(midi_ts);

        return pn;
    }

    /**
     * This will return the MIDI pitch of this score note. 
     * Rests will return 128. 
     * 
     * @return The MIDI pitch (0-127) or 128 for rests. 
     */
    public int getMidiPitch() {
    	if(getDiatonic() == 'r') 
    		return -1;
        int midi_oct = (getOctave() + 5) * 12;
        int midi_acc = getAlteration();

        int midi_p = 0; // default

        switch (this.getDiatonic()) {
        case 'a':
            midi_p = 9;
            break;
        case 'b':
            midi_p = 11;
            break;
        case 'c':
            midi_p = 0;
            break;
        case 'd':
            midi_p = 2;
            break;
        case 'e':
            midi_p = 4;
            break;
        case 'f':
            midi_p = 5;
            break;
        case 'g':
            midi_p = 7;
            break;
        }

        int midi_pitch = midi_oct + midi_p + midi_acc;
        return midi_pitch + getTransposition();
    }
    
    public void setPitch(ScorePitch newPitch){
    	setDiatonic(newPitch.getDiatonic());
    	setOctave(newPitch.getOctave());
    	setAlteration(newPitch.getAlteration());
    }

    public ScorePitch getPitch() {
        return new ScorePitch(this.diatonic, this.octave, this.alteration);
    }

    /**
     * 
     * 
     * @return
     */
    public Rational getMetricEndTime() {
        return metricTime.add(metricDuration);
    }

    public EditingProfile getEditingProfile() {
        return new EditingProfile("Score Note",
                                  new EditingProfile[] {new EditingProfile("Diatonic", "char", "diatonic"),
                                                        new EditingProfile("Accidental", "byte", "alteration"),
                                                        new EditingProfile("Octave", "byte", "octave"),
                                                        new EditingProfile("Metric Time", "Rational", "metricTime"),
                                                        new EditingProfile("Duration", "Rational", "duration")},
                                  "Panel");
    }

    /**
     * This array contains for every diatonic note (a,...,) and for every diatonic 
     * interval (0..7) the change in semitones of a diatonic transposition compared 
     * to a minor or pure interval. The first dimension indicates the diatonic name 
     * relative to 'a'. The interval is coded as in (Score)Interval 
     * (unison=0, ... ,octave=7,...) E.g. steps[2][2]=1 indicates that
     * going up three degrees from a 'c' leads to a major third, while
     * steps[1][4]=-1 indicates that three degrees up from a 'b' up an 'f' leads
     * to a diminished fifth.
     */
    private static final byte[][] steps = {
    /* 0,1,2,3,4,5,6 */
    /* a */{0, 1, 0, 0, 0, 0, 0},
    /* b */{0, 0, 0, 0, -1, 0, 0},
    /* c */{0, 1, 1, 0, 0, 1, 1},
    /* d */{0, 1, 0, 0, 0, 1, 0},
    /* e */{0, 0, 0, 0, 0, 0, 0},
    /* f */{0, 1, 1, 1, 0, 1, 1},
    /* g */{0, 1, 1, 0, 0, 1, 0}};

    /**
     * Returns the type of interval (Pure, Major, Minor) between the diatonic
     * notes. The notes are given by lowercase letters 'a' to 'g'. TODO write
     * tests and check algorithm
     * 
     * @param dia1
     *            the first diatonic note
     * @param dia2
     *            the first diatonic note
     * @return the type: -1 for MINOR, 0 for PERFECT, and 1 for MAJOR
     */
    byte intervalType(char dia1, char dia2) {
        if (dia1 < 'a' || dia1 > 'g' || dia1 < 'a' || dia1 > 'g')
            throw new IllegalArgumentException("diatonic notes must be in the range 'a' to 'g'.");
        if (dia1 < 'c')
            dia1 += 7;
        if (dia2 < 'c')
            dia2 += 7;
        int intBase = dia1 <= dia2 ? dia1 : dia2;
        intBase -= 'c';
        int intDist = dia2 - dia1;
        if (intDist < 0)
            intDist = -intDist;
        byte type = steps[intBase][intDist];
        // type is 0 for minor intervals, but it has to be -1
        if (type == 0 && intDist != 0 && intDist != 3 && intDist != 4) {
            type--;
        }
        return type;
    }

//    /**
//     * Transposes this note according the the given ScoreInterval changing the
//     * object's internal state. TODO write tests and check algorithm
//     * 
//     * @param inter
//     *            The interval by which the note shall be transposed.
//     */
//    public synchronized void transpose(ScoreInterval inter) {
//        // prepare internal vars
//        int diatonicChange = inter.getDiatonicInterval() % 7;
//        int octaveChange = inter.getDiatonicInterval() / 7;
////        assert diatonicChange + octaveChange * 7 == inter.getInterval();
//        
//        // change the octave
//        octave += octaveChange / 7;
//
//        // change the alteration
//        int accidentalChange = steps[diatonic - 'a'][Math.abs(diatonicChange)];
//        if (inter.getType() >= ScoreInterval.MAJOR)
//            accidentalChange -= 1;
//        if (diatonicChange < 0)
//            accidentalChange = -accidentalChange;
//        alteration -= accidentalChange;
//
//        // change the diatonic
//        // prepare it for octave calculations
//        char newDiatonic = diatonic;
//        if (newDiatonic < 'c')
//            newDiatonic += 7;
//        newDiatonic += diatonicChange;
//        // correct the octave if we left it (octaves start at c)
//        if (newDiatonic < 'c') {
//            octave -= 1;
//            newDiatonic += 7;
//        }
//        if (newDiatonic >= 'c' + 7) {
//            octave += 1;
//            newDiatonic -= 7;
//        }
//        // correct the diatonic letter, if we left the range (a-g)
//        if (newDiatonic > 'g')
//            newDiatonic -= 7;
////        assert 'a' <= newDiatonic;
////        assert newDiatonic <= 'g';
//        diatonic = newDiatonic;
//    }

    public synchronized void transpose(Interval inter) {
    	ScorePitch newPitch;
    	if(inter.isUp())
    		newPitch = inter.getHigherPitch(this);
    	else
    		newPitch = inter.getLowerPitch(this);
    	this.diatonic = newPitch.getDiatonic();
    	this.alteration = newPitch.getAlteration();
    	this.octave = newPitch.getOctave();
    }
    
//    /**
//     * Transposes this note according the the given Interval changing the
//     * object's internal state. TODO write tests and check algorithm
//     * 
//     * @param inter
//     *            The interval by which the note shall be transposed.
//     */
//    public synchronized void transpose(Interval inter) {
//        // prepare internal vars
//        int diatonicChange = inter.getIntervalSize().toInt() % 7;
//        int octaveChange = inter.getIntervalSize().toInt() / 7;
////        assert diatonicChange + octaveChange * 7 == inter.getInterval();
//        octaveChange += inter.getOctaveExtension(); 
//        // change the octave
//        octave += octaveChange;
//
//        // change the alteration
//        int alterationChange = steps[diatonic - 'a'][Math.abs(diatonicChange)];
//        if (inter.getIntervalQuality().toInt() >= Interval.iQual.MAJOR.toInt())
//            alterationChange -= 1;
//        if (inter.getIntervalQuality().toInt() < Interval.iQual.MINOR.toInt())
//            alterationChange += 1;        
//        if (diatonicChange < 0)
//            alterationChange = -alterationChange;
//        alteration -= alterationChange;
//
//        // change the diatonic
//        // prepare it for octave calculations
//        char newDiatonic = diatonic;
//        if (newDiatonic < 'c')
//            newDiatonic += 7;
//        newDiatonic += diatonicChange;
//        // correct the octave if we left it (octaves start at c)
//        if (newDiatonic < 'c') {
//            octave -= 1;
//            newDiatonic += 7;
//        }
//        if (newDiatonic >= 'c' + 7) {
//            octave += 1;
//            newDiatonic -= 7;
//        }
//        // correct the diatonic letter, if we left the range (a-g)
//        if (newDiatonic > 'g')
//            newDiatonic -= 7;
////        assert 'a' <= newDiatonic;
////        assert newDiatonic <= 'g';
//        diatonic = newDiatonic;
//    }

    
    /**
     * calculate the distance between two note names assumed to be in the same
     * octave. TODO write tests and check the algorithm
     * 
     * @param dia1
     * @param dia2
     * @return
     */
    int diatonicDistance(char dia1, char dia2) {
        dia1 = Character.toLowerCase(dia1);
        dia2 = Character.toLowerCase(dia2);
        if (dia1 < 'c')
            dia1 += 7;
        if (dia2 < 'c')
            dia2 += 7;
        return dia2 - dia1;
    }

//    /**
//     * Calculate the ScoreInterval between this note and the argument note TODO
//     * write tests and check algorithm
//     * 
//     * @param snote
//     * @return The ScoreInterval
//     */
//    public ScoreInterval interval(ScoreNote snote) {
//        return interval(this, snote);
//    }

//    /**
//     * Calculate the ScoreInterval between two notes. TODO write tests and check
//     * algorithm
//     * 
//     * @param snote1
//     *            the first note
//     * @param snote2
//     *            the second note
//     * @return The ScoreInterval
//     */
//    public ScoreInterval interval(ScoreNote snote1, ScoreNote snote2) {
//        int diaChange = diatonicDistance(snote1.diatonic, snote2.diatonic);
//        diaChange += (snote1.octave - snote2.octave) * 7;
//        ScoreInterval sint = new ScoreInterval(diaChange);
//        byte type = intervalType(snote1.diatonic, snote2.diatonic);
//        // accidentals
//        type += snote2.getAlteration() - snote1.getAlteration();
//        sint.setType(type);
//        return sint;
//    }

//    /**
//     * Returns the ScoreInterval which is the inversion of the interval formed
//     * by the two specified ScoreNotes.
//     * 
//     * TODO write tests and check
//     * 
//     * @param snote1
//     *            the first note
//     * @param snote2
//     *            the second note
//     * @return ScoreInterval the inversion of the interval between snote1 and
//     *         snote2
//     */
//    public ScoreInterval invertedInterval(ScoreNote snote1, ScoreNote snote2) {
//        ScoreNote lowerNote = snote1;
//        ScoreNote higherNote = snote2;
//        if (snote1.getMidiPitch() - snote2.getMidiPitch() == 0)
//            return interval(snote1, snote2);
//        //if snote1 higher than snote2: set higherNote to note an octave above
//        // snote2
//        if (snote1.getMidiPitch() - snote2.getMidiPitch() > 0) {
//            higherNote = (ScoreNote) ObjectCopy.copyObject(snote2);
//            higherNote.setOctave((byte) (snote2.getOctave() + 1));
//        }
//        //if snote1 lower than snote2
//        else {
//            lowerNote = snote2;
//            higherNote = (ScoreNote) ObjectCopy.copyObject(snote1);
//            higherNote.setOctave((byte) (snote1.getOctave() + 1));
//        }
//        return interval(lowerNote, higherNote);
//    }

//    /**
//     * Returns the ScoreInterval which is the inversion of the interval between
//     * this ScoreNote and the specified ScoreNote. Method
//     * <code>invertedInterval(ScoreNote, ScoteNote)</code> is called.
//     * 
//     * @param snote
//     *            ScoreNote which form an interval with this ScoreNote
//     * @return ScoreInterval being the inversion of the interval between this
//     *         ScoreNote and <code>snote</code>
//     */
//    public ScoreInterval invertedInterval(ScoreNote snote) {
//        return invertedInterval(this, snote);
//    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * hibernate.many-to-one name = "metricDuration" class =
     * "de.uos.fmt.musitech.utility.math.Rational" cascade = "all"
     *  
     */
    public Rational getMetricDuration() {
        return metricDuration;
    }
    
    /**
	 * Gets the total metrical duration (with tied notes) of this object.
	 * @return the total metrical duration
	 */
    public Rational getTotalMetricDuration(){
    	if(getTiedNote()!=null)
    		return getMetricDuration().add(getTiedNote().getTotalMetricDuration());
    	else return getMetricDuration();
    }

    /**
     * Returns true if the specified Object represents a possible value of the
     * property indicated by the specified String, false otherwise. <br>
     * <br>
     * The diatonic can be 'c', 'd', 'e', 'f', 'g', 'a' or 'b'. <br>
     * The alteration must not be less than -3 or greater than 3.<br>
     * The octave must not be less than -5 or greater than 6.<br>
     * The metricTime and metricDuration must not be negative. TODO evtl.
     * Kommentar anpassen
     * 
     * @param propertyName
     *            String indicating the property for which the specified Object
     *            (resp. its primitive) might be a value
     * @param value
     *            Object to be checked if representing a possible value for the
     *            property specified by <code>propertyName</code>
     * @return boolean true if the specified Object represents a possible value
     *         for the property indicated by the specified String
     * 
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    public boolean isValidValue(String propertyName, Object value) {
        //check class
        ReflectionAccess ref = ReflectionAccess.accessForClass(this.getClass());
        if (ref.hasPropertyName(propertyName)) {
            Class<?> propertyType = ref.getPropertyType(propertyName);
            Class<?> valueClass = null;
            if (value instanceof Rational)
                valueClass = value.getClass();
            else
                valueClass = ObjectCopy.primitiveForWrapper(value.getClass());
            if (!propertyType.equals(valueClass))
                return false;
        }
        //check value
        if (propertyName.equalsIgnoreCase("diatonic"))
            //			return isPossibleDiatonic((Character) value);
            return checkDiatonic(((Character) value).charValue());
        if (propertyName.equalsIgnoreCase("alteration"))
            return isPossibleAccidental((Byte) value);
        if (propertyName.equalsIgnoreCase("octave"))
            return isPossibleOctave((Byte) value);
        if (propertyName.equalsIgnoreCase("metricTime"))
            return isPossibleMetric((Rational) value);
        if (propertyName.equalsIgnoreCase("metricDuration"))
            return isPossibleMetric((Rational) value);
        return true;
    }

    /**
     * Returns true if the specified Character is a wrapper for 'c', 'd', 'e',
     * 'f', 'g', 'a' or 'b', false otherwise.
     * 
     * @param value
     *            Character wrapper object for a diatonic value
     * @return true if the specified Character represents a possible diatonic,
     *         false otherwise
     */
    public boolean isPossibleDiatonic(Character value) {
        char[] diatonics = {'c', 'd', 'e', 'f', 'g', 'a', 'b'};
        ArrayList<Character> diatonicsList = new ArrayList<Character>();
        for (int i = 0; i < diatonics.length; i++) {
            diatonicsList.add(new Character(diatonics[i]));
        }
        return diatonicsList.contains(value);
    }

    /**
     * Returns true if the byte value of the specified Byte is a possible value
     * for the <code>alteration</code> of a ScoreNote, false otherwise. <br>
     * Possible accidentals are greater than -4 and less than 4.
     * 
     * @param value
     *            Byte wrapper object for a alteration value
     * @return boolean true if the specified Byte represents a possible
     *         alteration, false otherwise
     */
    private boolean isPossibleAccidental(Byte value) {
        //TODO Grenzwert pr?fen bzw. Objektvariable mit setter?
        final byte MAX = 3;
        if (value.byteValue() >= -MAX && value.byteValue() <= MAX)
            return true;
        return false;
    }

    /**
     * Returns true if the byte value of the specified Byte is a possible value
     * for the <code>octave</code> of a ScoreNote, false otherwise. <br>
     * Possible octaves are greater than -5 and less than 7.
     * 
     * @param value
     *            Byte wrapper object for an octave value
     * @return boolean true if the specified Byte represents a possible octave,
     *         false otherwise
     */
    private boolean isPossibleOctave(Byte value) {
        //TODO Grenzwerte pr?fen bzw. Objektvariablen mit setter?
        byte MIN = -4;
        byte MAX = 6;
        if (value.byteValue() >= MIN && value.byteValue() <= MAX)
            return true;
        return false;
    }

    /**
     * Returns true if the specified Rational is a possible
     * <code>metricTime</code> or <code>metricDuration</code>.<br>
     * Possible values are not negative.
     * 
     * @param value
     *            Rational to be checked
     * @return boolean true if the specified Rational is not negative, false
     *         otherwise
     */
    private boolean isPossibleMetric(Rational value) {
        //TODO weitere Bedingungen
        if (value.getNumer() < 0)
            return false;
        return true;
    }

    /**
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * 
     * @hibernate.id generator-class="native"
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * this method adds an accent to the object
     * 
     * @param type
     */
    public void addAccent(byte type) {
        if (accents == null) {
            accents = new byte[] {type};
        } else {
            byte[] newArray = new byte[accents.length + 1];
            for (int i = 0; i < accents.length; i++)
                newArray[i] = accents[i];
            newArray[newArray.length - 1] = type;
            accents = newArray;
        }
    }

    /**
     * @return The accents belonging to this ScoreNote
     */
    public byte[] getAccents() {
        return accents;
    }
    
    public void setAccents(byte[] accents){
    	this.accents = accents;
    }

    /**
     * Gets the tupletDivision.
     * 
     * @return Returns the tupletDivision.
     */
    public Rational getTupletDivision() {
        return tupletDivision;
    }
    /**
     * Sets the tupletDivision.
     * 
     * @param tupletDivision The tupletDivision to set.
     */
    public void setTupletDivision(Rational tupletDivision) {
        this.tupletDivision = tupletDivision;
    }
    /**
     * @return Returns the midiRendering.
     */
    public MidiRendering getMidiRendering() {
        return midiRendering;
    }
    /**
     * @param midiNoteRendering The midiRendering to set.
     */
    public void setMidiRendering(MidiRendering midiRendering) {
        this.midiRendering = midiRendering;
    }
    
    public int getDetune() {
        return detune;
    }
    public void setDetune(int detune) {
        this.detune = detune;
    }

    public int getTransposition() {
        return transposition;
    }
    public void setTransposition(int transposition) {
        this.transposition = transposition;
    }
    
    public Rational audioDelay; 

    /**
     * marks note as first in tie
     * @deprecated this is only used during mpeg-(de)serialization and should be deprecated as soon as possible
     */
    public boolean firstInTie;
    /**
     * marks note as last in tie
     * @deprecated this is only used during mpeg-(de)serialization and should be deprecated as soon as possible
     */
    public boolean lastInTie;
    
    public Rational getAudioDelay() {
        return audioDelay;
    }
    public void setAudioDelay(Rational audioDelay) {
        this.audioDelay = audioDelay;
    }
    

    /** Checks two IEquality objects on equivalences. Two note are equivalent, iff all their properties are equal.
     * 
     * @param IEquivalence note
     * @return boolean
     * @throws Exception 
     */
    public boolean isEquivalent(IEquivalence object) {
    	
    	if( !(object instanceof ScoreNote) ) return false;
    	Method[] cmNote = object.getClass().getMethods();
    	for(int i=0; i < cmNote.length; ++i) {
    		String methodName = cmNote[i].getName();
    		if(methodName.startsWith("get") && !"getUid".equals(methodName) && !(methodName.startsWith("getMetric")))
    			try {
					Class<?> type = cmNote[i].getReturnType();
					if(type.isPrimitive()) {
						if( !EqualsUtil.areEqual( cmNote[i].invoke(this,null) , cmNote[i].invoke(object, null) ) ) 
							return false;
					}
					else {
						for(Class<?> c:type.getInterfaces()) {
							boolean isEqualityType = "de.uos.fmt.musitech.data.utility.IEquality".equals( c.getName());
							if(  isEqualityType && !EqualsUtil.areEqual( (IEquivalence) cmNote[i].invoke(this, null) , (IEquivalence) cmNote[i].invoke(object, null) ) )
								return false;
							else if ( !isEqualityType && !EqualsUtil.areEqual( cmNote[i].invoke(this, null) , cmNote[i].invoke(object, null) ) ) {
								return false;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
    	}
    	return true;	
    } 
    
}