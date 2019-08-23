// $ANTLR : "Kern.g" -> "KernParser.java"$

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

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

/**
 *  Parser for the **kern file format. 
 *  The grammar consists of three classes
 *  - the lexer which defines the terminal symbols
 *  - the parser which defines the rules top down
 *  - the token tpyes
 */
public class KernParser extends antlr.LLkParser       implements KernParserTokenTypes
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



protected KernParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public KernParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected KernParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public KernParser(TokenStream lexer) {
  this(lexer,3);
}

public KernParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
}

	public final void expr() throws RecognitionException, TokenStreamException {
		
			// Define some local variables for token handling
			String value=""; 
			String o=""; String p=""; String q=""; String r=""; 
			String s=""; String t=""; String u=""; String v=""; String w=""; 
			String x=""; String y=""; String z=""; 
		
		
		try {      // for error handling
			{
			_loop33:
			do {
				switch ( LA(1)) {
				case KERNTAG:
				{
					match(KERNTAG);
					
									if (DEBUG) { value = "[COL"+colNumber+"] "+"**kern"; System.out.println(value); value=""; }
									//Create a part for each KERNTAG and add it to the piece object
									getPart(colNumber);
								
					break;
				}
				case DYNAMTAG:
				case LYRICSTAG:
				{
					{
					switch ( LA(1)) {
					case DYNAMTAG:
					{
						match(DYNAMTAG);
						break;
					}
					case LYRICSTAG:
					{
						match(LYRICSTAG);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					
									if (DEBUG) { value = "[COL"+colNumber+"] "+"**special column"; System.out.println(value); value=""; }
									// Create a empty part. The part will never be used. Just to fit the indexing.
									getPart(colNumber);
								
					break;
				}
				case INSTRUMENT_BEGIN:
				{
					match(INSTRUMENT_BEGIN);
					
									if (DEBUG) { value = "[COL"+colNumber+"] "+"instrument: "; }
								
					x=text();
					
									if (DEBUG) { value += x; System.out.println(value); value=""; }
									// Add the instrument name to the name of the notation system 
									if (getNotationStaff(colNumber).getName() == null)
										getNotationStaff(colNumber).setName("instrument: "+x+", ");
									else
										getNotationStaff(colNumber).setName(getNotationStaff(colNumber).getName()
											+"instrument: "+x+", ");
								
					break;
				}
				case STAFF_BEGIN:
				{
					match(STAFF_BEGIN);
					x=digits();
					
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
								
					break;
				}
				case CLEF_BEGIN:
				{
					match(CLEF_BEGIN);
					x=characters();
					{
					if ((LA(1)==DIGITS) && (_tokenSet_0.member(LA(2))) && (_tokenSet_1.member(LA(3)))) {
						y=digits();
					}
					else if ((_tokenSet_0.member(LA(1))) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3)))) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					
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
								
					break;
				}
				case KEYSIG_BEGIN:
				{
					match(KEYSIG_BEGIN);
					
									// Init the number of alterations
									int alterationNum = 0;
								
					{
					_loop9:
					do {
						if ((LA(1)==CHARACTERS)) {
							x=characters();
							y=alteration();
							
											if (DEBUG) { if (value=="") value="[COL"+colNumber+"] "+"key signature: "; value += "diatonic="+x+", alteration="+y+", "; }
											// Count the alterations
											alterationNum += alteration(y);
										
						}
						else {
							break _loop9;
						}
						
					} while (true);
					}
					match(SQUARBRACE_END);
					
									if (DEBUG) { System.out.println(value); value=""; }
									// Create a KeyMarker
									keyMarker = new KeyMarker(currentMetricTime, meterTrack.getTime(currentMetricTime));
									// TODO Zaehle die Vorzeichen
									keyMarker.setAlterationNum(alterationNum);
											//keyMarker.setAlterationNum(-1); //keyMarker.setAlterationNum(alteration(y)); //keyMarker.setRoot('F'); //keyMarker.setRootAlteration(0); //keyMarker.setRootAlteration(alteration(y)); //The mode depends on the used key //keyMarker.setMode(1);									
									// Add the key signature to the meter track
									harmoTrack.add(keyMarker);
								
					break;
				}
				case METERSIGMM_BEGIN:
				{
					match(METERSIGMM_BEGIN);
					{
					if ((LA(1)==DIGITS) && (LA(2)==SLASH)) {
						x=digits();
						match(SLASH);
						y=digits();
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"meter signature: numerator="+x+", denumerator="+y+", "; System.out.println(value); value=""; }
										meterTrack.add(new TimeSignatureMarker(Integer.parseInt(x), Integer.parseInt(y), currentMetricTime));
									
					}
					else if ((LA(1)==DIGITS) && (_tokenSet_0.member(LA(2)))) {
						x=digits();
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"tempo MM="+x; System.out.println(value); value=""; }
									
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					break;
				}
				case TRANSPOS:
				{
					match(TRANSPOS);
					match(FLAT);
					match(DIGITS);
					match(CHARACTERS);
					match(FLAT);
					match(DIGITS);
					
									if (DEBUG) { System.out.println("[COL"+colNumber+"] transposition"); }
								
					break;
				}
				case BARLINE:
				{
					match(BARLINE);
					{
					switch ( LA(1)) {
					case DIGITS:
					{
						x=digits();
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"---- barline: number="+x; if(y!="") {value += ", "+y;} value += " at "+currentMetricTime; }
									
						break;
					}
					case BARLINE:
					{
						match(BARLINE);
						match(BARLINE);
						match(BARLINE);
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"---- final barline"; value += " at "+currentMetricTime; }
									
						break;
					}
					case REPEAT:
					{
						match(REPEAT);
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"---- repeat barline"; value += " at "+currentMetricTime; }
										// TODO Set a repeat barline - not yet implemented
									
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					{
					switch ( LA(1)) {
					case FLAT:
					{
						y=invisible();
						break;
					}
					case EOF:
					case KERNTAG:
					case DYNAMTAG:
					case LYRICSTAG:
					case INSTRUMENT_BEGIN:
					case STAFF_BEGIN:
					case CLEF_BEGIN:
					case STAR:
					case KEYSIG_BEGIN:
					case METERSIGMM_BEGIN:
					case TRANSPOS:
					case DIGITS:
					case BARLINE:
					case DOT:
					case DUMMY:
					case STAFFEND:
					case TAB:
					case NL:
					case TIE_BEGIN:
					case SLUR_BEGIN:
					case CURVEDBRACE_OPEN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					
									// This will always be executed
									if (DEBUG) { System.out.println(value); value=""; }
									if (y=="") {
										notationSystem.addBarline(new Barline(currentMetricTime));
										// Set a linebreak
										//linebreaks.add(currentMetricTime);
									}
									// Finally reset a field
									y="";
								
					break;
				}
				case STAFFEND:
				{
					match(STAFFEND);
					
									if (DEBUG) { value = "[COL"+colNumber+"] "+"STAFFEND"; System.out.println(value); value=""; }
									// Set the last (double) barline
						   			notationSystem.addBarline(new Barline(currentMetricTime, true));
									// Finally reset a field
									y="";
								
					break;
				}
				case TAB:
				{
					match(TAB);
					
									if (DEBUG) { value = "new col."; System.out.println(value); value=""; }
									// Increase the current column number
									colNumber++;
								
					break;
				}
				case NL:
				{
					match(NL);
					
									// Reset the current column number
									colNumber = 0;
									// Set the new metric time if the parser have read notes before
									if (metricStepWidth != null) {
										currentMetricTime = currentMetricTime.add(metricStepWidth);
											//System.out.println("new line - new time:"+currentMetricTime+", metricStepWidth: "+metricStepWidth);
										// Reset the metric step width
										metricStepWidth = null;
									}
								
					break;
				}
				default:
					if ((LA(1)==STAR) && (LA(2)==CHARACTERS)) {
						match(STAR);
						x=characters();
						{
						switch ( LA(1)) {
						case FLAT:
						case SHARP:
						{
							y=alteration();
							break;
						}
						case SLASH:
						case COLON:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case SLASH:
						{
							match(SLASH);
							u=characters();
							{
							switch ( LA(1)) {
							case FLAT:
							case SHARP:
							{
								v=alteration();
								break;
							}
							case COLON:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							break;
						}
						case COLON:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(COLON);
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"root key: diatonic="+x+", alteration="+y; if(u!="") {value += ", diatonic="+u;} if(v!="") {value += ", alteration="+v;} System.out.println(value); value=""; }
										// and reset some fields
										y=""; u=""; v="";
									
					}
					else if ((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2))) && (_tokenSet_5.member(LA(3)))) {
						{
						switch ( LA(1)) {
						case SLUR_BEGIN:
						{
							q=slur_begin();
							break;
						}
						case DIGITS:
						case TIE_BEGIN:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case TIE_BEGIN:
						{
							s=tie_begin();
							break;
						}
						case CURVEDBRACE_OPEN:
						{
							p=phrase_begin();
							break;
						}
						case DIGITS:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						x=digits();
						{
						switch ( LA(1)) {
						case DOT:
						case DOTDOT:
						{
							o=dot();
							break;
						}
						case CHARACTERS:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						y=characters();
						{
						switch ( LA(1)) {
						case FLAT:
						case SHARP:
						{
							z=alteration();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN3:
						case CANCEL:
						case UNKNOWN2:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case ORNAMENT:
						case FERMATE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case UNKNOWN3:
						{
							match(UNKNOWN3);
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case CANCEL:
						case UNKNOWN2:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case ORNAMENT:
						case FERMATE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case CANCEL:
						{
							match(CANCEL);
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN2:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case ORNAMENT:
						case FERMATE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case ORNAMENT:
						{
							w=ornament();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN2:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case FERMATE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case FERMATE:
						{
							t=fermate();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN2:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case UNKNOWN2:
						{
							match(UNKNOWN2);
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case SLASH:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						case BACKSLASH:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case SLASH:
						case BACKSLASH:
						{
							u=stem();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case SQUARBRACE_END:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case TIE_MIDDLE:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case SQUARBRACE_END:
						{
							s=tie_end();
							break;
						}
						case TIE_MIDDLE:
						{
							s=tie_middle();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						case CURVEDBRACE_CLOSE:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case CURVEDBRACE_CLOSE:
						{
							p=phrase_end();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case SLUR_END:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case SLUR_END:
						{
							q=slur_end();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case BEAM_BEGIN:
						case BEAM_END:
						case PARTIALBEAM_BEGIN:
						case PARTIALBEAM_END:
						{
							v=beam();
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case UNKNOWN1:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case UNKNOWN1:
						{
							match(UNKNOWN1);
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						
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
					else if ((LA(1)==DIGITS) && (LA(2)==REST||LA(2)==DOT||LA(2)==DOTDOT) && (_tokenSet_6.member(LA(3)))) {
						x=digits();
						{
						switch ( LA(1)) {
						case DOT:
						case DOTDOT:
						{
							y=dot();
							break;
						}
						case REST:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						int _cnt28=0;
						_loop28:
						do {
							if ((LA(1)==REST)) {
								match(REST);
							}
							else {
								if ( _cnt28>=1 ) { break _loop28; } else {throw new NoViableAltException(LT(1), getFilename());}
							}
							
							_cnt28++;
						} while (true);
						}
						{
						switch ( LA(1)) {
						case UNKNOWN1:
						{
							match(UNKNOWN1);
							break;
						}
						case EOF:
						case KERNTAG:
						case DYNAMTAG:
						case LYRICSTAG:
						case INSTRUMENT_BEGIN:
						case STAFF_BEGIN:
						case CLEF_BEGIN:
						case STAR:
						case KEYSIG_BEGIN:
						case METERSIGMM_BEGIN:
						case TRANSPOS:
						case DIGITS:
						case BARLINE:
						case DOT:
						case DUMMY:
						case STAFFEND:
						case TAB:
						case NL:
						case TIE_BEGIN:
						case SLUR_BEGIN:
						case CURVEDBRACE_OPEN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						
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
					else if ((LA(1)==STAR||LA(1)==DOT||LA(1)==DUMMY) && (_tokenSet_0.member(LA(2)))) {
						{
						switch ( LA(1)) {
						case DOT:
						{
							match(DOT);
							break;
						}
						case DUMMY:
						{
							match(DUMMY);
							break;
						}
						case STAR:
						{
							match(STAR);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						
										if (DEBUG) { value = "[COL"+colNumber+"] "+"."; System.out.println(value); value=""; }
										// Create empty objects, just to fit the indexing.
										getNotationStaff(colNumber);
									
					}
				else {
					break _loop33;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
	}
	
	public final String  text() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		Token  j = null;
		Token  k = null;
		Token  l = null;
		Token  m = null;
		Token  n = null;
		Token  o = null;
		Token  p = null;
		Token  q = null;
		Token  r = null;
		Token  s = null;
		Token  i2 = null;
		Token  j2 = null;
		Token  k2 = null;
		Token  l2 = null;
		Token  m2 = null;
		Token  n2 = null;
		Token  o2 = null;
		Token  p2 = null;
		Token  q2 = null;
		Token  r2 = null;
		Token  s2 = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case DIGITS:
			{
				i = LT(1);
				match(DIGITS);
				value+=Integer.parseInt(i.getText());
				break;
			}
			case CHARACTERS:
			{
				j = LT(1);
				match(CHARACTERS);
				value+=j.getText();
				break;
			}
			case SHARP:
			{
				k = LT(1);
				match(SHARP);
				value+=k.getText();
				break;
			}
			case FLAT:
			{
				l = LT(1);
				match(FLAT);
				value+=l.getText();
				break;
			}
			case BEAM_BEGIN:
			{
				m = LT(1);
				match(BEAM_BEGIN);
				value+=m.getText();
				break;
			}
			case BEAM_END:
			{
				n = LT(1);
				match(BEAM_END);
				value+=n.getText();
				break;
			}
			case REST:
			{
				o = LT(1);
				match(REST);
				value+=o.getText();
				break;
			}
			case PARTIALBEAM_BEGIN:
			{
				p = LT(1);
				match(PARTIALBEAM_BEGIN);
				value+=p.getText();
				break;
			}
			case PARTIALBEAM_END:
			{
				q = LT(1);
				match(PARTIALBEAM_END);
				value+=q.getText();
				break;
			}
			case CANCEL:
			{
				r = LT(1);
				match(CANCEL);
				value+=r.getText();
				break;
			}
			case UNKNOWN2:
			{
				s = LT(1);
				match(UNKNOWN2);
				value+=s.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop53:
			do {
				switch ( LA(1)) {
				case CHARACTERS:
				{
					j2 = LT(1);
					match(CHARACTERS);
					value+=j2.getText();
					break;
				}
				case SHARP:
				{
					k2 = LT(1);
					match(SHARP);
					value+=k2.getText();
					break;
				}
				case FLAT:
				{
					l2 = LT(1);
					match(FLAT);
					value+=l2.getText();
					break;
				}
				case BEAM_BEGIN:
				{
					m2 = LT(1);
					match(BEAM_BEGIN);
					value+=m2.getText();
					break;
				}
				case BEAM_END:
				{
					n2 = LT(1);
					match(BEAM_END);
					value+=n2.getText();
					break;
				}
				case REST:
				{
					o2 = LT(1);
					match(REST);
					value+=o2.getText();
					break;
				}
				case PARTIALBEAM_BEGIN:
				{
					p2 = LT(1);
					match(PARTIALBEAM_BEGIN);
					value+=p2.getText();
					break;
				}
				case PARTIALBEAM_END:
				{
					q2 = LT(1);
					match(PARTIALBEAM_END);
					value+=q2.getText();
					break;
				}
				case CANCEL:
				{
					r2 = LT(1);
					match(CANCEL);
					value+=r2.getText();
					break;
				}
				case UNKNOWN2:
				{
					s2 = LT(1);
					match(UNKNOWN2);
					value+=s2.getText();
					break;
				}
				default:
					if ((LA(1)==DIGITS) && (_tokenSet_8.member(LA(2))) && (_tokenSet_1.member(LA(3)))) {
						i2 = LT(1);
						match(DIGITS);
						value+=Integer.parseInt(i2.getText());
					}
				else {
					break _loop53;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return value;
	}
	
	public final String  digits() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(DIGITS);
			value+=Integer.parseInt(i.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		return value;
	}
	
	public final String  characters() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  j = null;
		
		try {      // for error handling
			j = LT(1);
			match(CHARACTERS);
			value+=j.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		return value;
	}
	
	public final String  alteration() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		Token  j = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SHARP:
			{
				i = LT(1);
				match(SHARP);
				value+="#";
				break;
			}
			case FLAT:
			{
				j = LT(1);
				match(FLAT);
				value+="-";
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return value;
	}
	
	public final String  slur_begin() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(SLUR_BEGIN);
			value+="begin";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return value;
	}
	
	public final String  tie_begin() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(TIE_BEGIN);
			value+="begin";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		return value;
	}
	
	public final String  phrase_begin() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(CURVEDBRACE_OPEN);
			value+="begin";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		return value;
	}
	
	public final String  dot() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		Token  j = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case DOT:
			{
				i = LT(1);
				match(DOT);
				value+=".";
				break;
			}
			case DOTDOT:
			{
				j = LT(1);
				match(DOTDOT);
				value+="..";
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		return value;
	}
	
	public final String  ornament() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(ORNAMENT);
			value+="with ornament";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		return value;
	}
	
	public final String  fermate() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(FERMATE);
			value+="fermate";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		return value;
	}
	
	public final String  stem() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		Token  j = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SLASH:
			{
				i = LT(1);
				match(SLASH);
				value+="/";
				break;
			}
			case BACKSLASH:
			{
				j = LT(1);
				match(BACKSLASH);
				value+="\\";
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		return value;
	}
	
	public final String  tie_end() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(SQUARBRACE_END);
			value+="end";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return value;
	}
	
	public final String  tie_middle() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(TIE_MIDDLE);
			value+="middle";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return value;
	}
	
	public final String  phrase_end() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(CURVEDBRACE_CLOSE);
			value+="end";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		return value;
	}
	
	public final String  slur_end() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			match(SLUR_END);
			value+="end";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return value;
	}
	
	public final String  beam() throws RecognitionException, TokenStreamException {
		String value="";
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case PARTIALBEAM_BEGIN:
			{
				match(PARTIALBEAM_BEGIN);
				value="begin 16 m";
				break;
			}
			case PARTIALBEAM_END:
			{
				match(PARTIALBEAM_END);
				value="end 16 m";
				break;
			}
			default:
				if ((LA(1)==BEAM_BEGIN) && (_tokenSet_21.member(LA(2)))) {
					match(BEAM_BEGIN);
					value="begin 8";
				}
				else if ((LA(1)==BEAM_END) && (_tokenSet_21.member(LA(2)))) {
					match(BEAM_END);
					value="end 8";
				}
				else if ((LA(1)==BEAM_BEGIN) && (LA(2)==BEAM_BEGIN)) {
					match(BEAM_BEGIN);
					match(BEAM_BEGIN);
					value="begin 16";
				}
				else if ((LA(1)==BEAM_END) && (LA(2)==BEAM_END)) {
					match(BEAM_END);
					match(BEAM_END);
					value="end 16";
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		return value;
	}
	
	public final String  invisible() throws RecognitionException, TokenStreamException {
		String value="";
		
		Token  i = null;
		
		try {      // for error handling
			i = LT(1);
			match(FLAT);
			value+="invisible";
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return value;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"KERNTAG",
		"DYNAMTAG",
		"LYRICSTAG",
		"INSTRUMENT_BEGIN",
		"STAFF_BEGIN",
		"CLEF_BEGIN",
		"STAR",
		"SLASH",
		"COLON",
		"KEYSIG_BEGIN",
		"SQUARBRACE_END",
		"METERSIGMM_BEGIN",
		"UNKNOWN3",
		"CANCEL",
		"UNKNOWN2",
		"UNKNOWN1",
		"REST",
		"TRANSPOS",
		"FLAT",
		"DIGITS",
		"CHARACTERS",
		"BARLINE",
		"REPEAT",
		"DOT",
		"DUMMY",
		"STAFFEND",
		"TAB",
		"NL",
		"BEAM_BEGIN",
		"BEAM_END",
		"PARTIALBEAM_BEGIN",
		"PARTIALBEAM_END",
		"TIE_BEGIN",
		"TIE_MIDDLE",
		"SLUR_BEGIN",
		"SLUR_END",
		"CURVEDBRACE_OPEN",
		"CURVEDBRACE_CLOSE",
		"DOTDOT",
		"ORNAMENT",
		"FERMATE",
		"BACKSLASH",
		"SHARP",
		"COMMENT_BEGIN",
		"UNSUPPORTED"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 1447313844210L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 76278618580978L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 140737488355314L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1443117400064L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 5566436999168L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 140737420193778L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 1447315417074L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 71880504944626L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 5845382377458L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 136339356909554L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 65970625314802L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1168239493120L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 8388608L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 17825792L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 57174515314674L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 39582329270258L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 4397956917234L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 4260517947378L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 2061494691826L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 1511738877938L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 1447314368498L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	
	}
