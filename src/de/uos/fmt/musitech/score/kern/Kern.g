// header of the java classes to generate 

header{
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
package de.uos.fmt.musitech.score.kern;

import de.uos.fmt.musitech.data.performance.*;
import de.uos.fmt.musitech.data.structure.*;
import de.uos.fmt.musitech.data.structure.container.*;
import de.uos.fmt.musitech.data.structure.harmony.*;
import de.uos.fmt.musitech.data.structure.linear.*;
import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.time.*;
import de.uos.fmt.musitech.utility.math.*;

import java.util.List;
import java.util.ArrayList;
}

/**
 *  Parser for the **kern file format. 
 *  The grammar consists of three classes
 *  - the lexer which defines the terminal symbols
 *  - the parser which defines the rules top down
 *  - the token tpyes
 */

/*
 * Definition of the parser class 
 */
class KernParser extends Parser;

// Set some options of the parser
options {
	k=3; // Increase the lookahead otherwise we get a nondeterminism warning.
}
// Add code to the parser class
{
	// Switch debugging on or off
	private final boolean DEBUG = false;
	
	// The actual processed column number
	private int colNumber = 0;
	
	// The main comment of the processed kern document
	private String comment = "";
	
	// The target of the import
	protected NotationSystem notationSystem = null;
	// Rationals at which a linebreak shall occur
	protected List linebreaks;
	
	protected Context context;
	protected Piece piece;
	
	// List of parts for each column. After parsing the parts will be added 
	// to the piece objects ContainerPool.
	protected List parts;
	// List of BeamContainers for each column
	protected List beamContainers;
	// List of SlurContainers for each column
	protected List slurContainers;
	// List of previous tied note for each column
	protected List prevTiedNotes;
	// List of NotationStaff's for each column
	protected List notationStaffs;
	// List of NotationVoice's for each column
	protected List notationVoices;
	// List for ordering the 'staff' columns
	private List staffList;

	// The notepool of the piece object
	private Container notePool;

	// Create some musical objects
	protected NotationChord currentChord;
	private Note note;
	private Rational metricDuration;
	private int voice;
	private byte accent;
	private List pitches;
	private MetricalTimeLine meterTrack;
	private Container<Marker> harmoTrack;
	private Clef clef;
	private KeyMarker keyMarker;
	private MidiNote midiNote;
	private ScoreNote scoreNote;
	
	// The current metric time
	private Rational currentMetricTime;
	// The metric step width: one bar is coded in smallest note duration 
	// divided into meter signatur. For example one 3/4 bar is coded in 
	// maximally 6 rows when the smallest note is a eighth, because 1/8 * 6 = 3/4.
	// In order to get the metric time of a note which is not at the beginning of a 
	// measure, the reciprocal of the smallest note duration is stored in this field.
	private Rational metricStepWidth;
	
	/**
	 * Initialize the KernParser by creating the socre container objects.
	 */
	protected void init() {
		piece = new Piece();
		context = piece.getContext();
		notationSystem = new NotationSystem();
		
		if (piece.getMetricalTimeLine() == null) {
			piece.setMetricalTimeLine(
				new MetricalTimeLine(context));
		}
		meterTrack = piece.getMetricalTimeLine();
		harmoTrack = piece.getHarmonyTrack();
		notePool = piece.getNotePool();
		
		parts= new ArrayList();
		notationStaffs = new ArrayList();
		notationVoices = new ArrayList();
		beamContainers = new ArrayList();
		slurContainers = new ArrayList();
		prevTiedNotes = new ArrayList();
		linebreaks = new ArrayList();
		staffList = new ArrayList();

		colNumber = 0;
		comment = "";	
		note = null;
		
		currentMetricTime = new Rational(0);
		metricStepWidth = null;
	}
	
	/**
	 * Returns the requested NotationStaff. If there is no 
	 * container the object will be created and returned. <BR>
	 *
	 * @param colNumber the current column offset (beginning at 0)
	 * @return the requested NotationStaff
	 */
	 private NotationStaff getNotationStaff(int colNumber) {
	 	if (notationStaffs.size() <= colNumber || notationStaffs.get(colNumber) == null) {
	 		notationStaffs.add(colNumber, new NotationStaff(notationSystem));
	 	}
		return (NotationStaff) notationStaffs.get(colNumber);
	 }

	/**
	 * Returns the requested NotationVoice. If there is no 
	 * container the object will be created and returned.
	 * @param colNumber the current column number
	 * @return the requested NotationVoice
	 */
	 private NotationVoice getNotationVoice(int colNumber) {
	 	if (notationVoices.size() <= colNumber || notationVoices.get(colNumber) == null) {
	 		notationVoices.add(colNumber, new NotationVoice(getNotationStaff(colNumber)));
	 		
			// Create a corresponding empty Containers
			createEmptyBeamContainer(colNumber);
			createEmptySlurContainer(colNumber);
			createEmptyPrevTiedNote(colNumber);
	 	}
		
	 	return (NotationVoice) notationVoices.get(colNumber);
	 }
	 
	/**
	 * Returns the requested BeamContainer. If there is no 
	 * container the object will be created and returned.
	 * @param colNumber the current column number
	 * @return the requested BeamContainer
	 */
	 private BeamContainer getBeamContainer(int colNumber) {
	 	if (beamContainers.size() < colNumber) {
	 	    // Append a new BeamContainer at the end of the List
	 		beamContainers.add(colNumber, new BeamContainer(context));
	 	} else 	if (beamContainers.get(colNumber) == null) {
	 	    // Replace a dummy BeamContainer (null) with a new one
	 		beamContainers.set(colNumber, new BeamContainer(context));
	 	}
	     
	 	return (BeamContainer) beamContainers.get(colNumber);
	 }

	/**
	 * Set a null Object at index 'colNumber' in the BeamContainers Array. 
	 * So when you have to create a Beam you can use the same index to store 
	 * a NotationVoice and a BeamContainer.
	 *
	 * @param colNumber the current column number
	 */
	 private void createEmptyBeamContainer(int colNumber) {
	 	if (beamContainers.size() >= colNumber) {
	 		beamContainers.add(colNumber, null);
	 	}
	 }

	/**
	 * Resets the requested BeamContainer. 
	 * @param colNumber the current column number
	 */
	 private void resetBeamContainer(int colNumber) {
	 	// Replace the BeamContainer with a dummy Object (null)
	 	if (beamContainers.size() >= colNumber)
		 	beamContainers.set(colNumber, null);
	 }

	/**
	 * Returns true if a BeamContainer exists for the 
	 * given column.
	 * @param colNumber the current column number
	 * @return true if a BeamContainer exists
	 */
	 private boolean beamContainerExists(int colNumber) {
	 	if (beamContainers.get(colNumber) == null)
		 	return false;
	 	else
	 	    return true;
	 }

	/**
	 * Returns the requested SlurContainer. If there is no 
	 * container the object will be created and returned.
	 * @param colNumber the current column number
	 * @return the requested SlurContainer
	 */
	 private SlurContainer getSlurContainer(int colNumber) {
	 	if (slurContainers.size() < colNumber) {
	 	    // Append a new SlurContainer at the end of the List
	 		slurContainers.add(colNumber, new SlurContainer(context));
	 	} else 	if (slurContainers.get(colNumber) == null) {
	 	    // Replace a dummy SlurContainer (null) with a new one
	 		slurContainers.set(colNumber, new SlurContainer(context));
	 	}
	     
	 	return (SlurContainer) slurContainers.get(colNumber);
	 }

	/**
	 * Set a null Object at index 'colNumber' in the SlurContainers Array. So when you have 
	 * to create a Slur you can use the same index to store a NotationVoice and a 
	 * SlurContainer.
	 * @param colNumber the current column number
	 */
	 private void createEmptySlurContainer(int colNumber) {
	 	if (slurContainers.size() >= colNumber) {
	 		slurContainers.add(colNumber, null);
	 	}
	 }

	/**
	 * Resets the requested SlurContainer. 
	 * @param colNumber the current column number
	 */
	 private void resetSlurContainer(int colNumber) {
	 	// Replace the SlurContainer with a dummy Object (null)
	 	if (slurContainers.size() >= colNumber)
		 	slurContainers.set(colNumber, null);
	 }

	/**
	 * Returns true if a SlurContainer exists for the 
	 * given column.
	 * @param colNumber the current column number
	 * @return true if a SlurContainer exists
	 */
	 private boolean slurContainerExists(int colNumber) {
	 	if (slurContainers.get(colNumber) == null)
		 	return false;
	 	else
	 	    return true;
	 }



	/**
	 * Returns the requested previous tied note. If there is 
	 * no note null will be returned.
	 * @param colNumber the current column number
	 * @return the requested previous tied note
	 */
	 private Note getPrevTiedNote(int colNumber) {
	 	if (prevTiedNotes.size() < colNumber) {
	 	    return null;
	 	} else 	if (prevTiedNotes.get(colNumber) == null) {
	 	    return null;
	 	}
	     
	 	return (Note) prevTiedNotes.get(colNumber);
	 }

	/**
	 * Sets the previous tied note to the prevTiedNotes array.
	 * @param colNumber the current column number
	 * @param Note the tied note
	 */
	 private void setPrevTiedNote(int colNumber, Note prevTiedNote) {
	 	if (prevTiedNotes.size() < colNumber) {
	 	    // Append a new tied note at the end of the List
	 		prevTiedNotes.add(colNumber, prevTiedNote);
	 	} else 	if (prevTiedNotes.get(colNumber) == null) {
	 	    // Replace a dummy SlurContainer (null) with a new one
	 		prevTiedNotes.set(colNumber, prevTiedNote);
	 	}
	 }

	/**
	 * Set a null Object at index 'colNumber' in the prevTiedNotes Array. 
	 * So when you have to create a tied chords you can use the same index 
	 * to store a NotationVoice and the previous chord.
	 * @param colNumber the current column number
	 */
	 private void createEmptyPrevTiedNote(int colNumber) {
	 	if (prevTiedNotes.size() >= colNumber) {
	 		prevTiedNotes.add(colNumber, null);
	 	}
	 }

	/**
	 * Resets the requested previous tied note. 
	 * @param colNumber the current column number
	 */
	 private void resetPrevTiedNote(int colNumber) {
	 	// Replace the previous tied note with a dummy object (null)
	 	if (prevTiedNotes.size() >= colNumber)
		 	prevTiedNotes.set(colNumber, null);
	 }

	/**
	 * Returns true if a previous tied note exists for 
	 * the given column.
	 * @param colNumber the current column number
	 * @return true if a previous tied note exists
	 */
	 private boolean prevTiedNoteExists(int colNumber) {
	 	if (getPrevTiedNote(colNumber) == null)
		 	return false;
	 	else
	 	    return true;
	 }

	/**
	 * Returns the requested Part. If there is no 
	 * part the object will be created and returned.
	 * @param colNumber the current column number
	 * @return the requested Part
	 */
	 private Part getPart(int colNumber) {
	 	if (parts.size() <= colNumber || parts.get(colNumber) == null) {
	 		parts.add(colNumber, new Part(context));
	 	}
	 	
	 	return (Part) parts.get(colNumber);
	 }
	 
	/**
	 * This method sorts the notationStaffs List because, the kern format stores 
	 * '*staff' columns in reverse order. 
	 */
	protected void sortStaffs() {
	    // Process the staff list means reverse the order
	    int staffSize = staffList.size();
	    for (int i=0; i<Math.round(staffSize/2.0); i++) {
	        swapStaffs(i, staffSize-1-i);
		    // Just for index consistency
		    swapVoices(i, staffSize-1-i);
	    }
	}
	
	/**
	 * This method swaps two NotationStaffs stored in notationStaffs. 
	 * @param firstIndex index of the first NotationStaff
	 * @param secondIndex index of the second NotationStaff 
	 */
	private void swapStaffs(int firstIndex, int secondIndex) {
	    
	    if (notationStaffs.size() < firstIndex || notationStaffs.size() < secondIndex) {
	        System.out.println("KernParser: swapStaffs - wrong argument(s). Nothing done.");
	        
	        return;
	    }
	    
	    // Store the first staff
	    NotationStaff firstStaff = null;
	    if (notationStaffs.get(firstIndex) != null)
	        firstStaff = (NotationStaff) notationStaffs.get(firstIndex);
	    
	    // Set the second staff at the index of the first staff
		notationStaffs.set(firstIndex, (NotationStaff) notationStaffs.get(secondIndex));
		// Set the first staff at the index of the second staff
		notationStaffs.set(secondIndex, firstStaff);
	}
	
	/**
	 * This method swaps two NotationVoices stored in notationVoices. 
	 * @param firstIndex index of the first NotationVoice
	 * @param secondIndex index of the second NotationVoice 
	 */
	private void swapVoices(int firstIndex, int secondIndex) {
	    
	    if (notationVoices.size() < firstIndex || notationVoices.size() < secondIndex) {
	        System.out.println("KernParser: swapVoices - wrong argument(s). Nothing done.");
	        
	        return;
	    }
	    
	    // Store the first Voice
	    NotationVoice firstVoice = null;
	    if (notationVoices.get(firstIndex) != null)
	        firstVoice = (NotationVoice) notationVoices.get(firstIndex);
	    
	    // Set the second staff at the index of the first staff
		notationVoices.set(firstIndex, (NotationVoice) notationVoices.get(secondIndex));
		// Set the first staff at the index of the second staff
		notationVoices.set(secondIndex, firstVoice);
	}

	/**
	 * This method returns the octave of the given kern-diatonic. <BR> 
	 * kern-examples: <BR> 
	 * kern 'c' = MIDI key, pitch '48' (C3) = octave 0 <BR>
	 * kern 'cc' = MIDI key, pitch '60' (C4) = octave 1 <BR>
	 * kern 'ccc' = MIDI key, pitch '72' (C5) = octave 2 <BR>
	 * kern 'C' = MIDI key, pitch '36' (C2) = octave -1 <BR>
	 * kern 'CC' = MIDI key, pitch '24' (C1) = octave -2 <BR>
	 * <BR>
	 * If the octave is unkown '-1' will be returned.
	 * @param kern-diatonic java.lang.String[]
	 * @return the octave
	 */
	protected byte octave(String diatonic) {
		byte octave = -1;
		
		// Get number of chars
		byte length = (byte) diatonic.length();

		if (length < 1)
			return octave;

		// Test for lower or upper case of the first char
		boolean uppercase = false;
		if (diatonic.matches("[A-Z].*"))
			uppercase = true;
			
		if (uppercase) {
			return (byte) (-length);
		} else { // lower case
			return (byte) (length-1);
		}
	}
	
	/**
	 * This method returns the diatonic of the given kern-diatonic. 
	 * For example 'DD' will return 'd'. 
	 * @param kern-diatonic java.lang.String[]
	 * @return the diatonic
	 */
	protected char diatonic(String diatonic) {

		return diatonic.toLowerCase().charAt(0);
	}

	/**
	 * This method returns the alteration of the given kern-alteration. 
	 * For example '#' will return '1' or '--' will return '-2' 
	 * @param kern-alteration java.lang.String[]
	 * @return the alteration
	 */
	protected byte alteration(String alteration) {
		
		if (alteration.matches("#"))
			return 1;
		if (alteration.matches("##"))
			return 2;
		if (alteration.matches("###"))
			return 3;
		if (alteration.matches("####"))
			return 4;
		if (alteration.matches("#####"))
			return 5;
		
		if (alteration.matches("-"))
			return -1;
		if (alteration.matches("--"))
			return -2;
		if (alteration.matches("---"))
			return -3;
		if (alteration.matches("----"))
			return -4;
		if (alteration.matches("-----"))
			return -5;   
		
		return 0;
	}


}

// The rules of the grammar:
expr 
{	// Define some local variables for token handling
	String value=""; 
	String o=""; String p=""; String q=""; String r=""; 
	String s=""; String t=""; String u=""; String v=""; String w=""; 
	String x=""; String y=""; String z=""; 
}
	:   // ****** the key word '**kern'
		( KERNTAG
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"**kern"; System.out.println(value); value=""; }
				//Create a part for each KERNTAG and add it to the piece object
				getPart(colNumber);
			}
		// ****** the key words '**dynam' or '**lyrics'
		| (DYNAMTAG|LYRICSTAG)
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"**special column"; System.out.println(value); value=""; }
				// Create a empty part. The part will never be used. Just to fit the indexing.
				getPart(colNumber);
			}
		// ****** instrument definition like '*I:Oboe 1, 2', '*I:Corno 1, 2 in Bb alto' or '*I:Violino I'
		| INSTRUMENT_BEGIN 
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"instrument: "; }
			}
		x=text 
			{
				if (DEBUG) { value += x; System.out.println(value); value=""; }
				// Add the instrument name to the name of the notation system 
				if (getNotationStaff(colNumber).getName() == null)
					getNotationStaff(colNumber).setName("instrument: "+x+", ");
				else
					getNotationStaff(colNumber).setName(getNotationStaff(colNumber).getName()
						+"instrument: "+x+", ");
			}
		// ****** the used note system, expressions like '*staff1'
		| STAFF_BEGIN x=digits 
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"staff: "+x; System.out.println(value); value=""; }
				// Add the staff name to the name of the notation system
				if (getNotationStaff(colNumber).getName() == null) {
					getNotationStaff(colNumber).setName("staff"+x+", ");
					// This name will be used to merge staffs 
					getNotationVoice(colNumber).setName("staff"+x);
				}
				else
					getNotationStaff(colNumber).setName("staff"+x+", "
						+getNotationStaff(colNumber).getName());
				// Add a pair of staff number and column number to the map
				staffList.add(new Integer(colNumber));
			}
		// ****** the violin, G-clef on line 2 '*clefG2' or bass, F-clef on line 4 '*clefF4'
		| CLEF_BEGIN x=characters (y=digits)?
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"clef: diatonic="+x+","; if (y!="") {value+="line="+y;} System.out.println(value); value=""; }
				// Create the clef and it to system
					getNotationStaff(colNumber).setClefType(diatonic(x));
					// The middle line is clef line '0', so ..
					if (y!="")
						getNotationStaff(colNumber).setClefLine((Integer.parseInt(y)-3));
					//else {
					//	if (diatonic(x)=='f')
					//		getNotationStaff(colNumber).setClefLine(4);
					//	if (diatonic(x)=='g')
					//		getNotationStaff(colNumber).setClefLine(2);
					//	if (diatonic(x)=='c')
					//		getNotationStaff(colNumber).setClefLine(3);
					//}
			}
		// ****** root key like '*c:', '*E-:' or '*E-/c:'
		| STAR x=characters (y=alteration)? (SLASH u=characters (v=alteration)?)? COLON
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"root key: diatonic="+x+", alteration="+y; if(u!="") {value += ", diatonic="+u;} if(v!="") {value += ", alteration="+v;} System.out.println(value); value=""; }
				// and reset some fields
				y=""; u=""; v="";
			}
		// ****** the key signature like '*k[b-]', '*k[f#c#g#]' or '*k[]'
		| KEYSIG_BEGIN 
			{
				// Init the number of alterations
				int alterationNum = 0;
			}
		(x=characters y=alteration
			{
				if (DEBUG) { if (value=="") value="[COL"+colNumber+"] "+"key signature: "; value += "diatonic="+x+", alteration="+y+", "; }
				// Count the alterations
				alterationNum += alteration(y);
			} )* 
		SQUARBRACE_END 
			{
				if (DEBUG) { System.out.println(value); value=""; }
				// Create a KeyMarker
				keyMarker = new KeyMarker(currentMetricTime, meterTrack.getTime(currentMetricTime));
				// TODO Zaehle die Vorzeichen
				keyMarker.setAlterationNum(alterationNum);
						//keyMarker.setAlterationNum(-1); //keyMarker.setAlterationNum(alteration(y)); //keyMarker.setRoot('F'); //keyMarker.setRootAlteration(0); //keyMarker.setRootAlteration(alteration(y)); //The mode depends on the used key //keyMarker.setMode(1);									
				// Add the key signature to the meter track
				harmoTrack.add(keyMarker);
			}
		// ****** the meter signature (=time signature) like '*M2/2'
		| METERSIGMM_BEGIN (x=digits SLASH y=digits 
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"meter signature: numerator="+x+", denumerator="+y+", "; System.out.println(value); value=""; }
				meterTrack.add(new TimeSignatureMarker(Integer.parseInt(x), Integer.parseInt(y), currentMetricTime));
			}
			|
			x=digits
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"tempo MM="+x; System.out.println(value); value=""; }
			}
			)
		// ****** note, expressions like '8d/', '8c#\', '8d-\L', '4AA\J', '4GWw\', '4.bb-;', '[8d', '4e_' or '8d}' 
		| (q=slur_begin)? (s=tie_begin|p=phrase_begin)? x=digits (o=dot)? y=characters (z=alteration)? (UNKNOWN3)? (CANCEL)? (w=ornament)? (t=fermate)? (UNKNOWN2)? (u=stem)? (s=tie_end|s=tie_middle)? (p=phrase_end)? (q=slur_end)? (v=beam)? (UNKNOWN1)? 
			// TODO implement stems direction
			// TODO implement cancels
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"note: diatonic="+y; if (z!="") {value += ", alteration="+z;} value += ", length="+x; if(o!="") {value += o;} if (u!="") {value += ", stem="+u;} if (v!="") {value += ", beam="+v;} if (s!="") {value += ", tie="+s;} if (p!="") {value += ", phrase="+p;} if (q!="") {value += ", slur="+q;} if (w!="") {value += " ("+w+")";} if (t!="") {value += " ("+t+")";} System.out.println(value); value=""; }
				//Create the score note  		
				scoreNote = new ScoreNote();
				scoreNote.setDiatonic(diatonic(y));
				scoreNote.setAlteration(alteration(z));
				scoreNote.setOctave(octave(y));
				scoreNote.setMetricTime(currentMetricTime);
				
				if (o=="") {
					// case the rest is NOT dotted
					metricDuration = new Rational(1,Integer.parseInt(x));
				} else if (o.equals(".")) {
					// case the note is dotted
					metricDuration = new Rational(3,2*Integer.parseInt(x));		
				} else if (o.equals("..")) {
					// case the note is dotted
					metricDuration = new Rational(7,4*Integer.parseInt(x));			
				}

				scoreNote.setMetricDuration(metricDuration);
				//if (DEBUG) { System.out.print("\t\t*scoreNote: "+scoreNote); }
				//Create the midi note
				midiNote = new MidiNote(scoreNote.toPerformanceNote(meterTrack));
				//if (DEBUG) { System.out.print(", *midiNote: "+midiNote+"\n"); }
				//Create a Note object
				note = new Note();
				note.setPerformanceNote(midiNote);
				note.setScoreNote(scoreNote);
		
				// Tie note won't need this..
				if (!s.matches("middle") || !s.matches("end")) {
					//Add the note to the part of the piece
					getPart(colNumber).add(note);
					//Add the note to the notePool of the piece
					notePool.add(note);
				} // end-if
					//Create a notation chord and the note to it
					currentChord = new NotationChord(context);
					currentChord.add(note);
				// Tie note won't need this..
				if (!s.matches("middle") || !s.matches("end")) {
					//Add the notation chord to the notation voice
					getNotationVoice(colNumber).add(currentChord);
				} // end-if
				
					// Begin of beam					
					if (v.matches("begin 8") || v.matches("begin 16") || v.matches("begin 16 m")) {
						// Add the notation chord to the beam container
						getBeamContainer(colNumber).add(currentChord);
							//System.out.println("note added to beam container, begin");
					} 
					// End of a beam
					else if (v.matches("end 8") || v.matches("end 16") || v.matches("end 16 m")) {
					   	// Add the notation chord to the beam container
					    getBeamContainer(colNumber).add(currentChord);
					   	// Add the container to the voice
					   	getNotationVoice(colNumber).addBeamContainer(getBeamContainer(colNumber));
					   	// Reset the beam container
					   	resetBeamContainer(colNumber);
					   		//System.out.println("note added to beam container, end");
					}
					// There are two cases: inner note of a beam or no beam
					else {
						if (beamContainerExists(colNumber)) {
						    // Case inner note of a beam
						   	// Add the notation chord to the beam container
						    getBeamContainer(colNumber).add(currentChord);
								//System.out.println("note added to beam container, inner");
						}
					}
	
					// Begin of a slur
					if (q.matches("begin")) {
						// Add the scoreNote to the slur container
						getSlurContainer(colNumber).add(note);
							//System.out.println("note added to slur container, begin");
					} 
					// End of a slur
					else if (q.matches("end")) {
					   	// Add the scoreNote to the slur container
					    getSlurContainer(colNumber).add(note);
					   	// Add the container to the voice
					   	getNotationVoice(colNumber).addSlurContainer(getSlurContainer(colNumber));
					   	// Reset the slur container
					   	resetSlurContainer(colNumber);
					   		//System.out.println("note added to slur container, end");
					}

				// Begin of a tie
				if (s.matches("begin")) {
					// Store the note because the note will get a tie note
					setPrevTiedNote(colNumber, note);
					   						//System.out.println("\t note waiting for getting a tie note");
				} 
				// Middle of a tie
				else if (s.matches("middle")) {
				   	// Add the tied note to the previous note
				   	if (getPrevTiedNote(colNumber) != null) {
						getPrevTiedNote(colNumber).getScoreNote().setTiedNote(note.getScoreNote());
						getPrevTiedNote(colNumber).getPerformanceNote().setDuration(
					        getPrevTiedNote(colNumber).getPerformanceNote().getDuration()
					        + meterTrack.getTime(metricDuration));
				        resetPrevTiedNote(colNumber);
						setPrevTiedNote(colNumber, note);
					   						//System.out.println("\t tied note added, middle");
					} else 
						System.out.println("KernParser: Warning tie expected (middle) at "+currentMetricTime);
				} 
				// End of a tie
				else if (s.matches("end")) {
				   	// Add the tied note to the previous note
				   	if (getPrevTiedNote(colNumber) != null) {
						getPrevTiedNote(colNumber).getScoreNote().setTiedNote(note.getScoreNote());
						getPrevTiedNote(colNumber).getPerformanceNote().setDuration(
					        getPrevTiedNote(colNumber).getPerformanceNote().getDuration()
					        + meterTrack.getTime(metricDuration));
						resetPrevTiedNote(colNumber);
					   						//System.out.println("\t tied note added, end");
					} else 
						System.out.println("KernParser: Warning tie expected (end) at "+currentMetricTime);
				}

				// TODO Process the phrase
				// Begin of a phrase
				if (p.matches("begin")) {
					//
				} 
				// End of a phrase
				else if (p.matches("end")) {
				   	//
				}

				// finally store the reciprocal minimal metric note duration
				if (metricStepWidth == null || metricStepWidth.isGreater(metricDuration)) 
					metricStepWidth = metricDuration;
				// and reset some fields
				p=""; q=""; s=""; t=""; o=""; y=""; u=""; v=""; w=""; z="";
			}
		// ****** rest, expressions like half rest '2r', '4.r' or '1r;' 
		| x=digits (y=dot)? (REST)+ (UNKNOWN1)?
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"rest: length="+x; if(y!="") {value += y;} System.out.println(value); value=""; }
				scoreNote = new ScoreNote();
				scoreNote.setDiatonic('r');
				scoreNote.setMetricTime(currentMetricTime);
				if (y=="") {
					// case the rest is NOT dotted
					metricDuration = new Rational(1,Integer.parseInt(x));
				} else if (y.equals(".")) {
					// case the note is dotted
					metricDuration = new Rational(3,2*Integer.parseInt(x));		
				} else if (y.equals("..")) {
					// case the note is dotted
					metricDuration = new Rational(7,4*Integer.parseInt(x));			
				}
				
				scoreNote.setMetricDuration(metricDuration);
				// A rest note has no MidiNote
				note = new Note(scoreNote, null);
				//Add the rest to the part of the piece
				getPart(colNumber).add(note);
				//Add the rest to the notePool of the piece
				notePool.add(note);
				//Create a notation chord and the rest to it
				currentChord = new NotationChord(context);
				currentChord.add(note);
				//Add the notation chord to the notation voice
				getNotationVoice(colNumber).add(currentChord);				
				// finally store the reciprocal minimal metric note duration
				if (metricStepWidth == null || metricStepWidth.isGreater(metricDuration)) 
					metricStepWidth = metricDuration;
				// and reset a field
				y=""; 
			}
		// ****** transpositions like '*ITrd-1c-2'
		| TRANSPOS FLAT DIGITS CHARACTERS FLAT DIGITS
			{
				if (DEBUG) { System.out.println("[COL"+colNumber+"] transposition"); }
			}
		// ****** barline like '=1-' (invisible), '=2' (visible), '=:|!' or '===='
		| BARLINE (x=digits
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"---- barline: number="+x; if(y!="") {value += ", "+y;} value += " at "+currentMetricTime; }
			}
			|BARLINE BARLINE BARLINE
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"---- final barline"; value += " at "+currentMetricTime; }
			}
			|REPEAT
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"---- repeat barline"; value += " at "+currentMetricTime; }
				// TODO Set a repeat barline - not yet implemented
			}
			) (y=invisible)? 
			{
				// This will always be executed
				if (DEBUG) { System.out.println(value); value=""; }
				if (y=="") {
					notationSystem.addBarline(new Barline(currentMetricTime));
					// Set a linebreak
					//linebreaks.add(currentMetricTime);
				}
				// Finally reset a field
				y="";
			}
		// ****** dummy symbol '.', '*^', '*v', '*', '*tb16', '*tb32;
		| (DOT|DUMMY|STAR)
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"."; System.out.println(value); value=""; }
				// Create empty objects, just to fit the indexing.
				getNotationStaff(colNumber);
			}
		// ****** staff end symbol '*-'
		| STAFFEND 
			{
				if (DEBUG) { value = "[COL"+colNumber+"] "+"STAFFEND"; System.out.println(value); value=""; }
				// Set the last (double) barline
	   			notationSystem.addBarline(new Barline(currentMetricTime, true));
				// Finally reset a field
				y="";
			}
		// ****** the tab symbol '\t' delimits the columns
		| TAB
			{
				if (DEBUG) { value = "new col."; System.out.println(value); value=""; }
				// Increase the current column number
				colNumber++;
			}
		// ****** new line
		| NL
			{
				// Reset the current column number
				colNumber = 0;
				// Set the new metric time if the parser have read notes before
				if (metricStepWidth != null) {
					currentMetricTime = currentMetricTime.add(metricStepWidth);
						//System.out.println("new line - new time:"+currentMetricTime+", metricStepWidth: "+metricStepWidth);
					// Reset the metric step width
					metricStepWidth = null;
				}
			}
		)*	
	;

// beam to group notes. 'L' means begin of a group 8th and 'JJ' end of a 16th group.
beam returns [String value=""]
	:	BEAM_BEGIN {value="begin 8";}
	|	BEAM_END {value="end 8";}
	|	BEAM_BEGIN BEAM_BEGIN {value="begin 16";}
	|	BEAM_END BEAM_END {value="end 16";}
	|	PARTIALBEAM_BEGIN {value="begin 16 m";}
	|	PARTIALBEAM_END {value="end 16 m";}
	;
// tie notes. '[' means begin of a tie.
tie_begin returns [String value=""]
	:	TIE_BEGIN {value+="begin";}
	;
// the middle of tie notes. '_' means the middle note.
tie_middle returns [String value=""]
	:	TIE_MIDDLE {value+="middle";}
	;
// tie notes. ']' means end of tie.
tie_end returns [String value=""]
	:	SQUARBRACE_END {value+="end";}
	;
// slur notes. '(' means begin of a slur.
slur_begin returns [String value=""]
	:	SLUR_BEGIN {value+="begin";}
	;
// slur notes. ')' means end of slur.
slur_end returns [String value=""]
	:	SLUR_END {value+="end";}
	;
// phrase. '{' means begin of a phrase
phrase_begin returns [String value=""]
	:	CURVEDBRACE_OPEN {value+="begin";}
	;
// phrase. '{' means begin and '}' end of a phrase
phrase_end returns [String value=""]
	:	CURVEDBRACE_CLOSE {value+="end";}
	;
// dot is used for dotted notes or rests and as a dummy
dot returns [String value=""]
	:	i:DOT {value+=".";}
	|	j:DOTDOT {value+="..";}
	;
// ornament is used for decorated notes
ornament returns [String value=""]
	:	i:ORNAMENT {value+="with ornament";}
	;
// fermate 
fermate returns [String value=""]
	:	i:FERMATE {value+="fermate";}
	;
// invisible barline
invisible returns [String value=""]
	:	i:FLAT 	{value+="invisible";}
	;
// score note stem direction. SLASH = up BACKSLASH = down
stem returns [String value=""]
	:	i:SLASH 	{value+="/";}
	|	j:BACKSLASH	{value+="\\";}
	;
// note alteration
alteration returns [String value=""]
	:	i:SHARP {value+="#";}
	|	j:FLAT  {value+="-";}
	;
// digits	
digits returns [String value=""]
	:	i:DIGITS {value+=Integer.parseInt(i.getText());}
	;
// characters
characters returns [String value=""]
	:	j:CHARACTERS {value+=j.getText();}
	;
// text
text returns [String value=""]
	:   (i:DIGITS {value+=Integer.parseInt(i.getText());}
	|   j:CHARACTERS {value+=j.getText();}
	|	k:SHARP {value+=k.getText();}
	|	l:FLAT {value+=l.getText();}
	|	m:BEAM_BEGIN {value+=m.getText();}
	|	n:BEAM_END {value+=n.getText();}
	|	o:REST {value+=o.getText();}
	|	p:PARTIALBEAM_BEGIN {value+=p.getText();}
	|	q:PARTIALBEAM_END {value+=q.getText();}
	|	r:CANCEL {value+=r.getText();}
	|	s:UNKNOWN2 {value+=s.getText();}
	)
		(i2:DIGITS {value+=Integer.parseInt(i2.getText());}
	|   j2:CHARACTERS {value+=j2.getText();}
	|	k2:SHARP {value+=k2.getText();}
	|	l2:FLAT {value+=l2.getText();}
	|	m2:BEAM_BEGIN {value+=m2.getText();}
	|	n2:BEAM_END {value+=n2.getText();}
	|	o2:REST {value+=o2.getText();}
	|	p2:PARTIALBEAM_BEGIN {value+=p2.getText();}
	|	q2:PARTIALBEAM_END {value+=q2.getText();}
	|	r2:CANCEL {value+=r2.getText();}
	|	s2:UNKNOWN2 {value+=s2.getText();}
	)*
	;

/*
 * Defnition of the lexer class 
 */
class KernLexer extends Lexer;

options {
	k=3; // Set the lookahead, because **kern and **dynam are identical in the first two characters.
	charVocabulary = '\u0000'..'\uFFFE'; // allow all possible unicodes except -1 == EOF
}
// Add code to the lexer class
{
	// Field to store the comment of the processed kern file
	private String comment = "";
	
	// Returns the comment of the processed kern file
	public String getComment() {
		return comment;
	}
}

/*
 * Definition of the tokens
 */
// all digits
DIGITS
	: ('0'..'9')+ ;
// all chracters without the reserve chars for notes and so on
CHARACTERS
	options { testLiterals = true; }
	:	('a'..'m'|'o'..'q'|'s'..'w'|'y'..'z'|'A'..'I'|'M'..'V'|'X'..'Z'|' '|'@'|',') 
		('a'..'m'|'o'..'q'|'s'..'w'|'y'..'z'|'A'..'I'|'M'..'V'|'X'..'Z'|' '|'@'|',')* ;
NL  
	:	( '\r' '\n' | '\r' | '\n' ) 
			{
				// Unusually a line break is a token, because the parser has to do some 
				// actions if a new line begins.
				// $setType(Token.SKIP);
			}
	;
KERNTAG
	:	"**kern"
	;
DYNAMTAG
	:	"**dynam"
	;
LYRICSTAG
	:	"**lyrics"
	;
INSTRUMENT_BEGIN
	:	"*I:"
	;
STAFF_BEGIN
	:	"*staff"
	;
CLEF_BEGIN
	:	"*clef"
	;
KEYSIG_BEGIN
	:	"*k["
	;
SQUARBRACE_END
	:	']'
	;
METERSIGMM_BEGIN
	:	"*M" ('M')?
	;
SHARP  
	:	'#'
	;
FLAT 
	:	'-'
	;
CANCEL
	:	'n'
	;
SLASH
	:	'/'
	;
BACKSLASH
	:	'\\'
	;
DOT
	:	'.'
	;
DOTDOT
	:	".."
	;
STAR
	:	'*'
	;
DUMMY
	:	("*^"|"*v"|STAR ("tb4"|"tb16"|"tb32"))
	;
ORNAMENT
	:	"Ww"
	;
BARLINE
	:	'='
	;
REPEAT
	:	":|!"
	;
// TODO evaluate 'yy'
REST
	:	'r' ("yy")?
	;
FERMATE
	:	';'
	;
STAFFEND
	:	"*-"
	;
BEAM_BEGIN
	:	'L'
	;
BEAM_END
	:	'J'
	;
// For mixed beams, for examples beams of 16th and 8th notes.
PARTIALBEAM_BEGIN
	:	"LK"
	;
PARTIALBEAM_END
	:	"Jk"
	;
TIE_BEGIN
	:	'['
	;
TIE_MIDDLE
	:	'_'
	;
SLUR_BEGIN
	:	'('
	;
SLUR_END
	:	')'
	;
CURVEDBRACE_OPEN
	:	'{'
	;
CURVEDBRACE_CLOSE
	:	'}'
	;
TRANSPOS
	:	STAR ('I')? "Trd" 
	;
COLON
	:	':'
	;
TAB
	:	"\t"
	;	
// TODO evalute the 'xx' token
// Unkown token 'xx'
UNKNOWN1
	:	"xx"
	;
// TODO evalute the ''' token
// Unkown token '''
UNKNOWN2
	:	'\''
	;
// ****** comments like '!!!COM: Bach, Johann Sebastian' or '!!!OTL: Die Kunst der Fuge'
COMMENT_BEGIN
	:	("!!!"|"!! "|"!!") (~'\n')* '\n' 
		{
			// Add the comment to the comment field
			// TODO Process the comments to MetaData
			comment += "\t" + $getText.substring(3);
			// The parser has to ignore the comment, so skip the token.
			_ttype = Token.SKIP; newline();
		}
	;
	
// ****** unsupported tokens like '!' or '! Allegro'
UNSUPPORTED
	:	"!" (~('!'|'\n'))*
		{
			// TODO Process the comments to MetaData
			//comment += "\t" + $getText.substring(3);
				//System.out.print("  unsupported token: "+$getText+", ");
			// The parser has to ignore the comment, so skip the token.
			_ttype = Token.SKIP; newline();
		}
	;