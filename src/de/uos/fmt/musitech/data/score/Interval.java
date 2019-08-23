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
 * Created on 01.09.2004
 *
 */

package de.uos.fmt.musitech.data.score;

import java.io.Serializable;

import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * Class for objects representing an interval between to musical pitches in terms of diatonic interval and quality. 
 * 
 * @author Kerstin Neubarth, Tillman Weyde
 */
public class Interval implements Serializable {


	private static final long serialVersionUID = -46887938498563362L;

	/**
     * The size of the interval in diatonic notes.
     * E.g. 0 for unison, 2 stands for a third, 4 for a fifth, 7 for an octave etc.
     */
    private iSize intervalSize = iSize.UNISON;
    
    public static enum iSize {
    	UNISON, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, OCTAVE, NINTH, TENTH, ELEVENTH, TWELFTH, UNKNOWN;
    	
    	static int semitones[] = {0,1,3,5,7,8,10,12,13,15,17,19};
    	
    	// gets the semitone size for the pure/minor intervals 
    	int getSemitones(){
    		return semitones[this.ordinal()];
    	}
    	
    	/**
    	 * Get a name for this interval size.
    	 * @return The name (currently in German). 
    	 */
    	public String getName() {
    		switch(this){
    		case UNISON: return "Prime";//"unison";
    		case SECOND: return "Sekunde";//"second";
    		case THIRD: return "Terz";//"third";
    		case FOURTH: return "Quarte";//"fourth";
    		case FIFTH: return "Quinte";//"fifth";
    		case SIXTH: return "Sexte";//"sixth";
    		case SEVENTH: return "Septe";//"seventh";
    		case OCTAVE: return "Oktave";//"octave";
    		case NINTH: return "None";//"ninth";
    		case TENTH: return "Dezime";//"tenth";
    		case ELEVENTH: return "Undezime";//"eleventh";
    		case TWELFTH: return "Duodezime";//"twelfth";
    		default: return "";
    		}
    	};
    	
    	/**
    	 * Returns the iSize object for a given diatonic size (e.g. 1=second, 2=third, ... , 7=seventh)
    	 * @param s the diatonic interval size
    	 * @return the iSize object
    	 */
    	public static iSize fromInt(int s){
    		s = Math.abs(s); // in case there are negative values
    		if(s >= values().length) // positions beyond the end of values
    			s = values().length-1; // are mapped to UNKNOWN
    		return values()[s];
    	}

		public int toInt() {
			return ordinal();
		}
		
		boolean isPureInt(){
			switch(this){
			case UNISON:
			case FOURTH:
			case FIFTH:
			case OCTAVE:
			case ELEVENTH:
			case TWELFTH: return true;
			default: return false;
			}
		}
		
    };

    
    public static enum iQual {
    	DIMINISHED, MINOR, PERFECT, MAJOR, AUGMENTED, UNDEFINED;
    	
    	public String getName() {
    		switch(this){
    		case PERFECT: return "reine";//"perfect";
    		case MINOR: return "kleine";//"minor";
    		case MAJOR: return "große";//"major";
    		case DIMINISHED: return "verminderte";//"diminished";
    		case AUGMENTED: return "übermäßige";//"augmented";
    		default: return "";
    		}
    	};

    	public String getShortName(){
    		switch(this){
    		case PERFECT: return "p";//"p";
    		case MINOR: return "k";//"m";
    		case MAJOR: return "g";//"M";
    		case DIMINISHED: return "v";//"d";
    		case AUGMENTED: return "a";//"a";
    		default: return "";
    		}
    	}

		public static iQual fromChar(char c) {
    		switch(c){
    		case 'p': return PERFECT;
    		case 'm': return MINOR;
    		case 'M': return MAJOR;
    		case 'd': return DIMINISHED; 
    		case 'a': return AUGMENTED;
    		default: return UNDEFINED;
    		}	
		}
		
		public iQual invert(){
			switch (this){
			case MAJOR: return MINOR;
			case MINOR: return MAJOR;
			case AUGMENTED: return DIMINISHED;
			case DIMINISHED: return AUGMENTED;
			case PERFECT: return PERFECT;
			case UNDEFINED: return UNDEFINED;
			default: return UNDEFINED;
			}
		}
		
		public int toInt() {
			iQual[] array = values();
			for (int i = 0; i < array.length; i++) {
				if(this == array[i])
					return i;
			}
			return 0;
		}

    	public static iQual fromInt(int s){
    		s = Math.abs(s);
    		if(s >= values().length)
    			s = values().length-1;
    		return values()[s];
    	}
    	
    };

    /**
     * The quality of the interval.
     * If no quality is specified or can not be derived from the given ScorePitches
     * the quality is set to UNDEFINED.
     */
    private iQual intervalQuality = iQual.UNDEFINED;
    
    private int octaveExtension = 0;
    
    private boolean direction = UP;
    public static final boolean UP = true;
    public static final boolean DOWN = false;    

    /**
     * Empty constructor.
     */
    public Interval() {
    }
    
    /**
     * Constructor.
     * Creates an interval of the specified size and quality.
     * 
     * @param intervalSize int indicating size of the interval: 0 unison, 1 second, ... , 7 octave
     * @param intervalQuality char indicating the quality of the interval
     */
    public Interval(int intervalSize, char intervalQuality){
    	setIntervalSize(iSize.fromInt(intervalSize));
    	setIntervalQuality(iQual.fromChar(intervalQuality));
    }
    
    /**
     * Constructor.
     * Creates an interval of the specified size and quality.
     * 
     * @param intervalSize indicating size of the interval
     * @param intervalQuality indicating the quality of the interval
     */
    public Interval(iSize intervalSize, iQual intervalQuality){
    	setIntervalSize(intervalSize);
    	setIntervalQuality(intervalQuality);
    }
    
    public Interval(iSize size) {
    	setIntervalSize(size);
    	if(size.isPureInt())
        	setIntervalQuality(iQual.PERFECT);
    	else
        	setIntervalQuality(iQual.MAJOR);    		
	}



    public int getSemitoneSize(){
    	int size = intervalSize.getSemitones();
    	size += octaveExtension * 12;
		if (intervalSize.isPureInt())
			switch (intervalQuality) {
			case DIMINISHED: size -= 1; break;
			case AUGMENTED: size -= 2; break;
			default: 
			}
		else
			switch(intervalQuality){
			case MAJOR: size += 1; break;
			case AUGMENTED: size += 2; break;
			case DIMINISHED: size -=1; break; 
			default: 
			}
		return size;
    }

    /**
     * Constructor.
     * Derives the interval, i.e. its size and quality, from the given ScorePitches.
     * 
     * @param lowerPitch ScorePitch of the lower note of the interval
     * @param higherPitch ScorePitch of the higher note of the interval
     */
    public Interval(ScorePitch lowerPitch, ScorePitch higherPitch) {
        //TODO check if higherPitch is actually higher (necessary?)
        //determine intervalSize
        char diatonic = lowerPitch.getDiatonic();
        int intSize = 1;
        if(lowerPitch.getDiatonic() != higherPitch.getDiatonic() || 
        		lowerPitch.getOctave() != higherPitch.getOctave()) {        	
	        do{
	            intSize++;
	            diatonic = nextDiatonicInScale(diatonic, true);
	        } while (diatonic != higherPitch.getDiatonic());   	
        }

        intervalSize = iSize.fromInt(intSize);
        //determine octaveExtension
        int pitchDiff = higherPitch.getPerformancePitch()- lowerPitch.getPerformancePitch();
        octaveExtension = pitchDiff/12;
        //determine intervalQuality
        int accDiff = higherPitch.getAlteration() - lowerPitch.getAlteration();
        //intervals with qualities major, minor etc.
        if (intSize == 2 || intSize == 3 || intSize == 6
                || intSize == 7) {
            intervalsWithMajorMinor(lowerPitch.getDiatonic(), accDiff);
        }
        //intervals with qualities perfect etc.
        else if (intSize == 4 || intSize == 5 || intSize == 8
                || intSize == 1) {
            intervalsWithPerfect(lowerPitch.getDiatonic(), accDiff);
        } else {
        	throw new RuntimeException("Error in class"+this.getClass()+", constructor Interval(lowerPitch,higherPitch)");
    	}
    }

	/**
     * Sets the quality for intervals which can be major, minor, augmented or
     * diminished (i.e. for seconds, thirds, sixths and sevenths, ninths, and tenths).
     * 
     * @param lowerDiatonic char indicating the diatonic of the interval's lower tone 
     * @param accDiff the difference between the accidental-value of the higher and the accidental-value of the lower note
     */
    private void intervalsWithMajorMinor(char lowerDiatonic, int accDiff) {
        char[] specialDiatonics = determineMinorDiatonics(intervalSize);
        for (int i = 0; i < specialDiatonics.length; i++) {
            if (specialDiatonics[i] == lowerDiatonic) {
                accDiff -= 1;
            }
        }
            switch (accDiff) {
            case 0:
                intervalQuality = iQual.MAJOR;
                break;
            case -1:
                intervalQuality = iQual.MINOR;
                break;
            case 1:
                intervalQuality = iQual.AUGMENTED;
                break;
            case -2:
                intervalQuality = iQual.DIMINISHED;
            default:
                break;
            }
//            if (intervalQuality != 'X')
//                break; //exit for
//        }
    }
    
//    private void intervalsWithMajorMinor(int intervalSize, char lowerDiatonic, int accDiff){
//    	//TODO
//    }

    /**
     * Sets the quality for intervals which can be perfect, augmented or
     * diminished (i.e. for unisons (perfect or augmented), fourths, fifths and 
     * octaves).
     * 
     * @param lowerDiatonic char indicating the diatonic of the interval's lower tone 
     * @param accDiff int being the difference between the alteration-value of the higher 
     * and the alteration-value of the lower note.
     */
    private void intervalsWithPerfect(char lowerDiatonic, int accDiff) {
        if (intervalSize == iSize.FOURTH && lowerDiatonic == 'f') {
            accDiff -= 1;
        }
        if (intervalSize == iSize.FIFTH && lowerDiatonic == 'b') {
            accDiff += 1;
        }
        switch (accDiff) {
        case 0:
            intervalQuality = iQual.PERFECT;
            break;
        case 1:
            intervalQuality = iQual.AUGMENTED;
            break;
        case -1:
            intervalQuality = iQual.DIMINISHED;
            break;
        default:
        	throw new RuntimeException("Error in method intervalsWithPerfect in class " + this.getClass() );
        }
    }

    /**
     * Returns an Array of char elements with those diatonics which lead to a minor
     * interval quality when the two notes of the interval do not differ in accidental
     * value. The specified int value gives the size of the interval.
     * <br>
     * Example: Seconds are major when both notes have the same accidental value,
     * unless the lower diatonic is 'e' or 'b'. So, if the int argument is 2
     * (for second), an Array with values 'e' and 'b' is returned.
     * 
     * @param intervalSize int specifying the size of the interval
     * @return char[] Array which contains lower diatonics above which the interval is minor when both notes have the same accidental value
     */
    private static char[] determineMinorDiatonics(iSize intervalSize) {
        char[] specialDiatonics = null;
        switch (intervalSize) {
        case SECOND: 
        case NINTH:
            specialDiatonics = new char[] { 'e', 'b' };
            break;
        case THIRD:
        case TENTH:
        	specialDiatonics = new char[] { 'd', 'e', 'a' };
            break;
        case SIXTH:
            specialDiatonics = new char[] { 'e', 'a', 'b' };
            break;
        case SEVENTH:
            specialDiatonics = new char[] { 'd', 'e', 'g', 'a', 'b' };
            break;
        default:
            specialDiatonics = new char[0];
            break;
        }
        return specialDiatonics;
    }

    /*
     * public ScorePitch determineHigherPitch(ScorePitch lowerPitch){
     * //determine char char higherDiatonic = lowerPitch.getDiatonic(); for (int
     * i = 0; i < intervalSize; i++) { higherDiatonic =
     * nextDiatonicInScale(higherDiatonic, true); } //determine octave
     * 
     * //determine acc return null; }
     */

    /*
     * public ScorePitch determineLowerPitch(ScorePitch higherPitch){ return
     * null; }
     */

    /**
     * Returns an Interval which is the inversion of the Interval formed by the
     * specified ScorePitches.
     * 
     * @param lowerPitch ScorePitch the lower pitch of the interval which is to be inverted
     * @param higherPitch ScorePitch the higher pitch of the interval which is to be inverted
     * @return Interval The inversion of the interval formed by the specified ScorePitches
     */
    public static Interval determineInversion(ScorePitch lowerPitch,
            ScorePitch higherPitch) {
        ScorePitch transposedPitch = (ScorePitch) ObjectCopy
                .copyObject(lowerPitch);
        transposedPitch.setOctave((byte) (transposedPitch.getOctave() + 1));
        return new Interval(higherPitch, transposedPitch);
    }

    /*
     * public MidiNoteSequence createSoundExample(ScorePitch lowerPitch){
     * Rational noteDur = new Rational(1,2); NoteList notes = new NoteList();
     * notes.add(lowerPitch, new Rational(0,4), noteDur); ScorePitch higherPitch =
     * determineHigherPitch(lowerPitch); notes.addnext(higherPitch, noteDur);
     * return MidiNoteSequence.convert(notes); }
     */

    /**
     * Returns the neighbouring diatonic above (if <code>directionUp</code> is
     * true) or below (if <code>directionUp</code> is false) the specified
     * <code>diatonic</code>. E.g. the next diatonic above 'd' is 'e'. The
     * specified char must be a character from 'a' to 'g'. If it is not, 'X'
     * will be returned.
     * 
     * @param diatonic
     * @param directionUp
     * @return
     */
    public static char nextDiatonicInScale(char diatonic, boolean directionUp) {
        if (diatonic >= 'a' && diatonic <= 'g') {
            if (directionUp) {
                if (diatonic == 'g')
                    diatonic = 'a';
                else
                    diatonic++;
            } else {
                if (diatonic == 'a')
                    diatonic = 'g';
                else
                    diatonic--;
            }
            return diatonic;
        }
        return 'X';
    }

    /**
     * Returns the neighbouring diatonic above the specified
     * <code>diatonic</code>. E.g. the next diatonic above 'd' is 'e'. The
     * specified char must be a character from 'a' to 'g'. If it is not, 'X'
     * will be returned.
     * 
     * @param diatonic
     * @param directionUp
     * @return
     */
    public static char nextDiatonic(char diatonic) {
        if (diatonic >= 'a' && diatonic <= 'g') {
                if (diatonic == 'g')
                    diatonic = 'a';
                else
                    diatonic++;
            return diatonic;
        }
        return 'X';
    }

    
    /**
     * Returns an abbreviated String notation of intervals.
     * E.g. a minor second will be written as "m2".
     * 
     * @return
     */
    public String returnShortName() {
        return intervalQuality.getShortName() + intervalSize + "";
    }

    /**
     * Returns a String with the name of this Interval which consists of the
     * interval's quality and size. 
     * E.g. the name of the interval c-e is "major third".
     * 
     * @return String the name of the interval      
     */
	public String returnName() {
		String name;
		if (intervalQuality != iQual.PERFECT)
			name = intervalQuality.getName() + " " + intervalSize.getName();
		else
			name = intervalSize.getName();
		return name;
	}

    /**
     * @return Returns the intervalQuality.
     */
    public iQual getIntervalQuality() {
        return intervalQuality;
    }
    /**
     * @param intervalQuality The intervalQuality to set.
     */
    public void setIntervalQuality(iQual intervalQuality) {
        this.intervalQuality = intervalQuality;
    }
    /**
     * @return Returns the intervalSize.
     */
    public iSize getIntervalSize() {
        return intervalSize;
    }
    /**
     * @param intervalSize The intervalSize to set.
     */
    public void setIntervalSize(iSize intervalSize) {
        this.intervalSize = intervalSize;
    }
    
    public int getOctaveExtension(){
    	return octaveExtension;
    }
    
    public void setOctaveExtension(int argOctExt){
    	octaveExtension = argOctExt;
    }
    
    public String toString(){
    	return returnName();
    }
    
    public ScorePitch getHigherPitch(ScorePitch lowerPitch){
    	return getHigherPitch(lowerPitch, intervalSize, intervalQuality, octaveExtension);

    }
    
    public static ScorePitch getHigherPitch(ScorePitch lowerPitch, iSize intervalSize, iQual intervalQuality){
        return getHigherPitch(lowerPitch, intervalSize, intervalQuality, 0);
    }
    
    
    //TODO make more economic
    public static ScorePitch getHigherPitch(ScorePitch lowerPitch, iSize intervalSize, iQual intervalQuality, int octaveExtension){
//    	ScorePitch higherPitch = lowerPitch;
    	ScorePitch higherPitch = new ScorePitch(lowerPitch);
    	int octave = lowerPitch.getOctave();
    	octave += octaveExtension;
    	//determine diatonic of higher pitch
    	char diatonic = lowerPitch.getDiatonic();
    	for (int i = 0; i < intervalSize.toInt(); i++) {
			diatonic = nextDiatonicInScale(diatonic, true);
			if (diatonic=='c'){
				octave+=1;
			}
		}
    	//determine accidental of higher pitch
    	int acc = 0;
    	//interval with perfect quality
    	if (intervalSize == iSize.UNISON || intervalSize == iSize.OCTAVE || intervalSize == iSize.FIFTH || intervalSize == iSize.FOURTH){
    		switch (intervalQuality) {
			case PERFECT:
				acc = higherPitch.getAlteration();
				break;
			case AUGMENTED:
				acc = new Integer(lowerPitch.getAlteration()+1).byteValue();
				break;
			case DIMINISHED:
				acc = new Integer(lowerPitch.getAlteration()-1).byteValue();
				break;
			default:
				break;
			}
    	}
    	if (intervalSize == iSize.FIFTH && lowerPitch.getDiatonic() == 'b'){
    		acc+=1;
    	}
    	if (intervalSize == iSize.FOURTH && lowerPitch.getDiatonic() == 'f'){
    		acc-=1;
    	}
    	//intervals with major/minor qualities
    	if (intervalSize == iSize.SECOND || intervalSize == iSize.THIRD || intervalSize == iSize.SIXTH || intervalSize == iSize.SEVENTH){
    		switch (intervalQuality) {
			case MAJOR:
				acc = higherPitch.getAlteration();
				break;
			case MINOR:
				acc = new Integer(lowerPitch.getAlteration()-1).byteValue();
				break;
			case AUGMENTED:
				acc = new Integer(lowerPitch.getAlteration()+1).byteValue();
				break;
			case DIMINISHED:
				acc = new Integer(lowerPitch.getAlteration()-2).byteValue();
				break;
			default:
				break;
			}
    	}
    	if (intervalSize == iSize.SECOND && (lowerPitch.getDiatonic()=='e' || lowerPitch.getDiatonic()=='b')){
    		acc+=1;
    	}
    	if (intervalSize == iSize.THIRD && (lowerPitch.getDiatonic()=='d' || lowerPitch.getDiatonic()=='e' || lowerPitch.getDiatonic()=='a' || lowerPitch.getDiatonic()=='b')){
    		acc+=1;
    	}
    	if (intervalSize == iSize.SIXTH && (lowerPitch.getDiatonic()=='e' || lowerPitch.getDiatonic()=='a' || lowerPitch.getDiatonic()=='b')){
    		acc+=1;
    	}
    	if (intervalSize == iSize.SEVENTH && !(lowerPitch.getDiatonic()=='c' || lowerPitch.getDiatonic()=='f')){
    		acc+=1;
    	}
    	higherPitch.setDiatonic(diatonic);
    	higherPitch.setAlteration(acc);
    	higherPitch.setOctave(new Integer(octave).byteValue());
    	return higherPitch;
    }
    
    public static ScorePitch getLowerPitch(ScorePitch higherPitch, iSize intervalSize, iQual intervalQuality){
        return getLowerPitch(higherPitch, intervalSize, intervalQuality, 0);

    }
    	
    public static ScorePitch getLowerPitch(ScorePitch higherPitch, iSize intervalSize, iQual intervalQuality, int octaveExtension){
    	ScorePitch lowerPitch = new ScorePitch();
    	octaveExtension += intervalSize.ordinal()/7;
    	intervalSize = iSize.fromInt(intervalSize.ordinal()%7);
    	//determine inversion of interval
    	Interval inversion = determineInversion(intervalSize, intervalQuality);
    	//get octave below higherPitch
    	ScorePitch helpPitch = new ScorePitch();
    	helpPitch.setDiatonic(higherPitch.getDiatonic());
    	helpPitch.setAlteration(higherPitch.getAlteration());
    	int octave = higherPitch.getOctave()-1;
    	octave -= octaveExtension;
    	helpPitch.setOctave((byte) octave);
    	//get inverted interval above helpPitch
    	lowerPitch = getHigherPitch(helpPitch, inversion.getIntervalSize(), inversion.getIntervalQuality());
    	return lowerPitch;
    }
    
    public static Interval determineInversion(iSize intervalSize, iQual intervalQuality){
    	Interval interval = new Interval();
    	interval.setIntervalSize(iSize.fromInt(7-intervalSize.toInt()));
    	interval.setIntervalQuality(intervalQuality.invert());
    	return interval;
    }
    
    public static ScorePitch getHigherPitch(ScorePitch lowerPitch, Interval interval){
    	ScorePitch higherPitch = getHigherPitch(lowerPitch, interval.getIntervalSize(), interval.getIntervalQuality());
    	int octave = higherPitch.getOctave()+interval.getOctaveExtension();
    	higherPitch.setOctave(new Integer(octave).byteValue());
    	return higherPitch;
    }
    
    public static ScorePitch getLowerPitch(ScorePitch higherPitch, Interval interval){
    	ScorePitch lowerPitch = getLowerPitch(higherPitch, interval.getIntervalSize(), interval.getIntervalQuality());
    	int octave = lowerPitch.getOctave()-interval.getOctaveExtension();
    	lowerPitch.setOctave(new Integer(octave).byteValue());
    	return lowerPitch;
		
    }
    
    public ScorePitch getLowerPitch(ScorePitch higherPitch){
    	return getLowerPitch(higherPitch, intervalSize, intervalQuality, octaveExtension);

    }


	
	/**
	 * @return true if the directions of this interval is up.
	 */
	public boolean isUp() {
		return direction;
	}

	
	/**
	 * Set the direction of this interval to up (true) of down (false);
	 * @param direction the direction to set
	 */
	public void setUp(boolean up) {
		this.direction = up;
	}
       
}