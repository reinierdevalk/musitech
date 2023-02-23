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

import java.io.Serializable;

/**
 * Symbolic information on pitch including the octave
 * 
 * @author JW & Tillman Weyde
 * @version $Reveision: $, $Date: 2013-10-31 13:56:25 +0100 (Thu, 31 Oct 2013) $
 * 
 * @hibernate.class table = "ScorePitch"
 */
public class ScorePitch implements Serializable{
	
	public static final char[] DIATONIC_PITCHES = { 'c', 'd', 'e', 'f', 'g', 'a', 'b' };

    /**
     * octave, default 1 is middle C octave.
     * 
     * @uml.property name="octave"
     */
    protected int octave = 1;

    /**
     * diatonic note a-g
     * 
     * @uml.property name="diatonic"
     */
    protected char diatonic = 'c';

    /**
     * alteration, 0 natural, 1 sharp, -1 flat, 2 double sharp, -2 double flat.
     * 
     * @uml.property name="alteration"
     */
    protected int alteration = 0;

    /**
     * 
     * @uml.property name="id"
     */
    private Long id;


    /**
     * create an empty ScorePitch
     */
    public ScorePitch() {
    }
    
    public ScorePitch(char diatonic) {
    	this();
    	setDiatonic(diatonic);
    }

    /**
     * create a ScorePitch from a MIDI-Pitch.
     */
    public ScorePitch(int pitch) {
        this.octave = (pitch / 12 - 5);
        switch (pitch % 12) {
        case 0:
            this.diatonic = 'c';
            this.alteration = 0;
            break;
        case 1:
            this.diatonic = 'c';
            this.alteration = 1;
            break;
        case 2:
            this.diatonic = 'd';
            this.alteration = 0;
            break;
        case 3:
            this.diatonic = 'd';
            this.alteration = 1;
            break;
        case 4:
            this.diatonic = 'e';
            this.alteration = 0;
            break;
        case 5:
            this.diatonic = 'f';
            this.alteration = 0;
            break;
        case 6:
            this.diatonic = 'f';
            this.alteration = 1;
            break;
        case 7:
            this.diatonic = 'g';
            this.alteration = 0;
            break;
        case 8:
            this.diatonic = 'g';
            this.alteration = 1;
            break;
        case 9:
            this.diatonic = 'a';
            this.alteration = 0;
            break;
        case 10:
            this.diatonic = 'a';
            this.alteration = 1;
            break;
        case 11:
            this.diatonic = 'b';
            this.alteration = 0;
            break;
        }
    }

    public ScorePitch(char diatonic, byte octave, byte alteration) {
        this.diatonic = diatonic;
        this.alteration = alteration;
        this.octave = octave;
    }

    public ScorePitch(char diatonic, int octave, int alteration) {
        this.diatonic = diatonic;
        this.alteration = alteration;
        this.octave = octave;
    }

    public ScorePitch(int pitch, int oct) {
        this(pitch);
        this.octave = oct;
    }
    
    /**
     * Creates a new ScorePitch with the diatonic, octave
     * and alteration of the specified ScorePitch. 
     * 
     * @param scorePitch ScorePitch based on which a new ScorePitch is created
     */
    public ScorePitch(ScorePitch scorePitch){
    	this(scorePitch.getDiatonic(), scorePitch.getOctave(), scorePitch.getAlteration());
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:11:33)
     * 
     * @return byte
     * 
     * @hibernate.property
     * 
     * @uml.property name="octave"
     */
    public int getOctave() {
        return octave;
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:11:33)
     * 
     * @param newOctave
     *            byte
     * 
     * @uml.property name="octave"
     */
    public void setOctave(byte newOctave) {
        octave = newOctave;
    }


    /**
     * Method toPerformanceNote.
     * 
     * @param mt
     * @return PerformanceNote
     */
    public int getPerformancePitch() {

        int midi_oct = (getOctave() + 5) * 12;
        int midi_alt = getAlteration();
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
        int midi_pitch = midi_oct + midi_p + midi_alt;
        return midi_pitch;
    }

    public boolean equalsMIDI(ScorePitch score) {
        return (this.getPerformancePitch() == score.getPerformancePitch());
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:15:45)
     * 
     * @return byte
     * 
     * @hibernate.property
     * 
     * @uml.property name="alteration"
     */
    public int getAlteration() {
        return alteration;
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:09:14)
     * 
     * @return char
     * 
     * @hibernate.property
     * 
     * @uml.property name="diatonic"
     */
    public char getDiatonic() {
        return diatonic;
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:15:45)
     * 
     * @param newAccidental
     *            byte
     * 
     * @uml.property name="alteration"
     */
    public void setAlteration(int newAccidental) {
        alteration = newAccidental;
    }

    /**
     * Insert the method's description here. Creation date: (10.1.2002 18:09:14)
     * 
     * @param newDiatonic
     *            char
     * 
     * @uml.property name="diatonic"
     */
    public void setDiatonic(char newDiatonic) {
        diatonic = Character.toLowerCase(newDiatonic);
    }


    /**
     * TODO specify comment
     * 
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
        if (getOctave() >= 0) {
            for (int i = 0; i < getOctave(); i++)
                sb.append('\'');
            sb.append(diatonic);
        } else {
            for (int i = 0; i < -getOctave(); i++)
                sb.append(',');
            sb.append(Character.toUpperCase(diatonic));
        }
        if (alteration >= 0)
            for (int i = 0; i < alteration; i++)
                sb.append('#');
        else
            for (int i = 0; i > alteration; i--)
                sb.append('b');
        return sb.toString();
    }

    /**
     * Returns true if the two tonal pitches have the same internal state, i.e.
     * the same diatonic and alteration.
     * 
     * @param obj the pitch to compare
     * @return true if equal, false else
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object obj) {
    	if(obj == null || !(obj instanceof ScorePitch ))
    		return false;
        ScorePitch sp = (ScorePitch) obj;
        return ((this.diatonic == sp.diatonic) && (this.alteration == sp.alteration));
    }

    /**
     * Create a new tonal pitch which is one sharp of this pitch, i.e. with one
     * added to the alteration.
     * 
     * @return The new sharpened pitch.
     */
    public ScorePitch sharp() {
        return new ScorePitch(this.diatonic, this.octave, this.alteration + 1);
    }

    /**
     * Create a new ScorePitch which is one flat of this pitch, i.e. with one
     * subtracted from the alteration.
     * 
     * @return The new flattend pitch.
     */
    public ScorePitch flat() {
        return new ScorePitch(this.diatonic, this.octave, this.alteration - 1);
    }

    /**
     * Get the pitch class of the tonal pitch as a number (c=0...b=11).
     * 
     * @return The chromatic pitch class.
     */
    public int getChromaticPitchClass() {
        int pitch = 0;
        switch (getDiatonic()) {
        case 'c':
        case 'C':
            pitch = 0;
            break;
        case 'd':
        case 'D':
            pitch = 2;
            break;
        case 'e':
        case 'E':
            pitch = 4;
            break;
        case 'f':
        case 'F':
            pitch = 5;
            break;
        case 'g':
        case 'G':
            pitch = 7;
            break;
        case 'a':
        case 'A':
            pitch = 9;
            break;
        case 'b':
        case 'B':
            pitch = 11;
            break;
        }
        pitch += getAlteration();
        return pitch;
    }

    /**
     * @return Returns the id.
     * 
     * @hibernate.id generator-class="native"
     * 
     * @uml.property name="id"
     */
    private Long getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     * 
     * @uml.property name="id"
     */
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * Private helper to calculate the relationship between two ScorePichtes.
     * @param this
     * @param note
     * @return
     */
    private int rootRelationship(ScorePitch note) {
    	int interval = note.getChromaticPitchClass() - getChromaticPitchClass();
    	if (interval < 0)
    		interval += 12;
    	return interval;
    }
    
    /**
     * Returns true if this ScorePitch is higher in pitch than the
     * specified ScorePitch. False is returned when this ScorePitch
     * is either of equal pitch as the specified ScorePitch or
     * lower than the specified ScorePitch. 
     * 
     * @param scorePitch ScorePitch which this ScorePitch is compared to
     * @return boolean true if this ScorePitch is higher than the specified ScorePitch, false otherwise
     */
    public boolean isHigher(ScorePitch scorePitch){
    	return getPerformancePitch() > scorePitch.getPerformancePitch();
    }
    
    /**
     * Returns true if this ScorePitch is lower in pitch than the
     * specified ScorePitch. False is returned when this ScorePitch
     * is either of equal pitch as the specified ScorePitch or
     * higher than the specified ScorePitch. 
     * 
     * @param scorePitch ScorePitch which this ScorePitch is compared to
     * @return boolean true if this ScorePitch is lower than the specified ScorePitch, false otherwise
     */
    public boolean isLower(ScorePitch scorePitch){
    	return getPerformancePitch() < scorePitch.getPerformancePitch();
    }

    private ScorePitch getNeighborDiatonic(int steps){
		String alphabet = "cdefgab";
		diatonic = Character.toLowerCase(getDiatonic());
		char nextDiatonic = alphabet.charAt((alphabet.indexOf(diatonic) + steps) % 7);
		int octave = this.getOctave() + (alphabet.indexOf(diatonic) + steps) / 7;
		return new ScorePitch(nextDiatonic, octave, 0);
    }
    
	public ScorePitch getDiatonicByStep(int stepSize) {
		ScorePitch nextPitch = null;
		if(stepSize > 0)
			nextPitch = getNeighborDiatonic(+1);
		else if(stepSize == 0) 
			return new ScorePitch(this.getDiatonic(), this.getOctave(), this.getAlteration());
		else
			nextPitch = getNeighborDiatonic(-1);
		nextPitch.setAlteration((byte) (this.getPerformancePitch() + stepSize - nextPitch.getPerformancePitch() ));
		return nextPitch;
	}

}