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
package de.uos.fmt.musitech.performance.midi;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;

/**
 * Class to convert a MIDI file into a Piece object.
 * The class writes each note into a part and into the notePool. TimeSignatures, 
 * Tempi, TimedMetricals and KeyMarker are placed in the MetricalTimeLine. 
 * Each MIDI track of the file will be placed into a different part. <BR>
 * 
 * Usage-example:
 * <PRE>
 * 
 * MidiReader midiReader = new MidiReader();
 * piece = midiReader.getPiece(url);
 * </PRE>
 * @version $Revision: 8690 $, $Date: 2014-09-09 12:15:15 +0200 (Tue, 09 Sep 2014) $
 * @author Alexander Luedeke
 */
public class MidiReader {

	//Name and path of the MIDI file
	private String fileName;

	//Sequence of the MIDI file
	private Sequence sequence = null;

	//Type of the MIDI file
	private int midiType = -1;

	//Type of the input data ("" means unknown, "MIDI1", "MIDIPlus")
	private String inputType = "";
	
	//Tracks of the MIDI file
	private Track[] tracks;

	//The number of tracks in the MIDI file
	private int numberOfTracks = 0;

	//The MidiDumpReceiver will process the MidiEvents
	private MidiDumpReceiver dumpReceiver;

	//Target of the conversion
	private Piece piece = null;

	/**
	 * MidiReader constructor. A private MidiDumpReceiver will be created to
	 * process the MIDI events.
	 */
	public MidiReader() {
		dumpReceiver = new MidiDumpReceiver();
	}

	/**
	 * Starts the import of the MIDI file and returns a new Piece object. 
	 * If the MidiSystem returns no Sequence the method returns null.
	 * @param url java.net.URL
	 * @param inputType String
	 * @return the new piece object
	 * @throws WrongArgumentException
	 */
	public Piece getPiece(URL url,String inputType) throws WrongArgumentException {
		try {
			//Get a sequence object, which contains the MIDI file content.
			sequence = MidiSystem.getSequence(url);
			//Get the MIDI file format
			midiType = MidiSystem.getMidiFileFormat(url).getType();
		} catch (InvalidMidiDataException iMDE) {
			iMDE.printStackTrace();
			System.err.println(">>MidiReader: InvalidMidiDataException");
			return null;
		} catch (IOException ioe) {
			System.err.println(
				">>MidiReader: getPiece(url, inputType) encoutered an IOException: "+ioe.getMessage());
			System.err.println(
				">>MidiReader: USE: java ...midi.MidiReader URL");
			ioe.printStackTrace();
			return null;
		}
		// Set the type of the input data
		this.setInputType(inputType);

		//Get the tracks of the MIDI file
		tracks = sequence.getTracks();

		//Get the number of tracks
		numberOfTracks = tracks.length;

		//Store the fileName
		fileName = url.getFile();

		//Create the target of the conversion
		piece = new Piece();
		piece.setScore(new NotationSystem(piece.getContext()));

		//Init the MidiDumpReceiver		
		dumpReceiver.init(this);
		

		/**
		 * Process the MIDI file content
		 */
		for (int trackNumber = 0;
			trackNumber < getNumberOfTracks();
			trackNumber++) {

			//Create a part for each track of the MIDI file and add it to the piece object
			//System.out.println(">>MidiReader: reading track " + trackNumber + " into part...");
			NotationStaff aPart = new NotationStaff(piece.getScore());
			aPart.setName("Track " + trackNumber + ", ");

			readTrackIntoPart(trackNumber, aPart);

		}
		piece.setName("Imported from URL " + url);

		// TODO check modularization
		//		NotationSystem system = new NotationSystem(piece.getContext());
		//
		//		//Create 16 objects to represent the MidiChannels and add them to the Piece object
		//		for (int i = 0; i < 16; i++) {
		//			NotationStaff staff = new NotationStaff(piece.getContext());
		//			NotationVoice voice = new NotationVoice(piece.getContext(), system);
		//			staff.add(voice);
		//			voice.setName("Midi channel " + i);
		//			dumpReceiver.setChannel(voice, i);
		//		}
		//		piece.getContainerPool().add(system);

		// TODO
		//		NotationSystem notationSystem = new NotationSystem(piece.getContext());
		//		//Add 16 objects to the Piece object
		//		for (int i = 0; i < 16; i++) {
		//			NotationStaff notationStaff = new NotationStaff(piece.getContext());
		//			notationStaff.add(dumpReceiver.getChannel(i));
		//			notationStaff.setName("Midi Channel "+i);
		//			notationSystem.add(notationStaff);
		//			notationSystem.setName("Notation System");
		//		}
		//		piece.getContainerPool().add(notationSystem);

		return piece;
	}
	
	/**
	 * Starts the import of the MIDI file and returns a new Piece object. 
	 * If the MidiSystem returns no Sequence the method returns null. <BR>
	 * The default input type 'standard' will be used.
	 * @param url java.net.URL
	 * @return the new piece object
	 */
	public Piece getPiece(URL url) {
	    try { 
	        return getPiece(url, "standard");
	    } catch(WrongArgumentException wAException) {
	        // standard input type will never throw a WrongArgumentException
	    }
	    
	    return null;
	}

	/**
	 * Reads one MIDI track into a part.
	 * @param trackNumber int 
	 * @see de.uos.fmt.musitech.data.structure.linear.Part
	 */
	public void readTrackIntoPart(int trackNumber, NotationStaff targetPart) {
		dumpReceiver.setPart(targetPart);
		dumpReceiver.resetCounters();
		MidiEvent event;

		try {
			//Process all MIDI events in the track
			for (int i = 0; i < tracks[trackNumber].size(); i++) {
				event = tracks[trackNumber].get(i);
				dumpReceiver.send(event.getMessage(), event.getTick());
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			if(DebugState.DEBUG)System.out.println(
				">>MidiReader: the track " + trackNumber + " is empty");
		}
	}

	/**
	 * Returns the used MIDI sequence
	 * @return sequence Sequence
	 */
	public Sequence getSequence() {
		assert sequence != null;
		return sequence;
	}

	/**
	 * Returns the new Piece object.
	 * @return the piece object
	 */
	public Piece getPiece() {
		return piece;
	}

	/**
	 * Returns the name of the used file
	 * @return fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the number of tracks in the MIDI file
	 * @return nubmer of tracks
	 */
	public int getNumberOfTracks() {
		return numberOfTracks;
	}

	/**
	 * Returns the resolutionType of the MIDI file as a String
	 * @return resolutionType
	 */
	public String getStrResolutionType() {
		String strResolutionType = null;
		if (getSequence().getDivisionType() == Sequence.PPQ) {
			strResolutionType = " ticks per beat";
		} else {
			strResolutionType = " ticks per frame";
		}
		return strResolutionType;
	}

	/**
	 * Returns the divisionType of the MIDI file as a String
	 * @return strDivisionType
	 */
	public String getStrDivisionType() {
		float fDivisionType = getSequence().getDivisionType();
		String strDivisionType = null;

		if (fDivisionType == Sequence.PPQ) {
			strDivisionType = "PPQ";
		} else if (fDivisionType == Sequence.SMPTE_24) {
			strDivisionType = "SMPTE, 24 frames per second";
		} else if (fDivisionType == Sequence.SMPTE_25) {
			strDivisionType = "SMPTE, 25 frames per second";
		} else if (fDivisionType == Sequence.SMPTE_30DROP) {
			strDivisionType = "SMPTE, 29.97 frames per second";
		} else if (fDivisionType == Sequence.SMPTE_30) {
			strDivisionType = "SMPTE, 30 frames per second";
		}
		return strDivisionType;
	}

	/**
	 * Returns the type of the MIDI file as a int<BR>
	 * 0 = MIDI file type 0, <BR>
	 * 1 = MIDI file type 1, <BR>
	 * 2 = MIDI file type 2, <BR>
	 * -1 = MIDI file type unknown <BR>
	 * Note: Don't confuse midiType with type. 
	 * @return the type of the MIDI file
	 */
	public int getMidiType() {
		return midiType;
	}
	
	/**
	 * Returns the type of the imported data as a int<BR>
	 * "standard" (default) <BR>
	 * "MIDI1" <BR>
	 * "MIDIPlus" (no supported yet)<BR>
	 * "" means unknown <BR>
	 * Note: Don't confuse inputType with midiType. 
	 * @return the type of imported data
	 */
	public String getInputType() {
		return inputType;
	}
	
	/**
	 * Sets the type of the imported data as a int<BR>
	 * "standard" (default) <BR>
	 * "MIDI1" <BR>
	 * "MIDIPlus" (no supported yet)<BR>
	 * "" means unknown <BR>
	 * Note: Don't confuse inputType with midiType. 
	 * @param the type of imported data
	 * @throws WrongArgumentException
	 */
	public void setInputType(String inputType) throws WrongArgumentException {
	    //String lowerString = inputType.toLowerCase()
	    if ("standard".equalsIgnoreCase(inputType)) {
	        this.inputType = "standard";
	        this.getDumpReceiver().inputType_MIDI1 = false;
	    } else if ("midi1".equalsIgnoreCase(inputType)) {
	        this.inputType = "Midi1";
	        this.getDumpReceiver().inputType_MIDI1 = true;
	    } else {
			throw new WrongArgumentException(
			        "Error: MidiReader.setInputType(): "+inputType+", unkown input type"
			        + getClass());
	    }
	}

	/**
	 * Returns the used DumpReceiver
	 * @return the DumpReceiver
	 */
	public MidiDumpReceiver getDumpReceiver() {
		return dumpReceiver;
	}

	/**
	 * Returns a string with some infos about the MIDI file. First a piece object has 
	 * to be created with the method getPiece(URL url).
	 * @return a info string
	 */
	public String getInfos() {
		String infoString = null;

		if (piece != null) {
			infoString = "[Some infos from the MidiReader: \n";
			infoString += "The song was imported from the MIDI file: "
				+ getFileName()
				+ ", \n";
			infoString += "there are " + getNumberOfTracks() + " tracks, \n";
			infoString += "song length: "
				+ getSequence().getTickLength()
				+ " ticks, nearly song length: "
				+ getSequence().getMicrosecondLength()
				+ " microseconds, \n";
			infoString += "divisionType: " + getStrDivisionType() + ", \n";
			infoString += "resolution: "
				+ getSequence().getResolution()
				+ getStrResolutionType()
				+ ", \n";
			infoString += "MIDI file type: " + getMidiType() + ", \n";
			infoString += "input type: " + getInputType() + "]\n";
		}

		return infoString;
	}

	/**
	 * Sets the used MIDI sequence
	 * @param sequence Sequence
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
		//Get the tracks of the MIDI file
		tracks = sequence.getTracks();

		//Get the number of tracks
		numberOfTracks = tracks.length;
	}

	/**
	 * Sets the piece. Target of the conversion.
	 * @param piece Piece
	 */
	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	/**
	 * Sets the DumpReceiver
	 * @param receiver MidiDumpReceiver
	 */
	public void setDumpReceiver(MidiDumpReceiver receiver) {
		dumpReceiver = receiver;
	}

	/**
	 * Just a demonstration of this class.
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {

		// Create the MidiReader object
		MidiReader aMidiReader = new MidiReader();
		// Name of the file to process
		String filename = "import_MIDI1_2.mid";

		Piece piece = null;

		try {
			// Try the command line argument like file:/d:/Progamme/Eclipse/.../import.mid
			// to read the MIDI file into the piece object
		    piece =
				aMidiReader.getPiece(
					aMidiReader.getClass().getResource(filename));
		} catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
			System.out.println("MidiReader: No command line argument.");
			return;
		} catch (NullPointerException nPException) {
			System.out.println("MidiReader: File (" + filename + ") not found");
			return;
		}

		// Just for debugging
		System.out.println(aMidiReader.getInfos());

		System.out.println(">>>terminated. And now for testing:");

		Container aContainerPool = piece.getContainerPool();
	
		Container currentContainer = null;

		try {
			for (int counter = 0; counter < aContainerPool.size(); counter++) {
				if (aContainerPool.get(counter) instanceof Container) {
					currentContainer = (Container) aContainerPool.get(counter);
					System.out.println(
						"Container name=" + currentContainer.getName());
					System.out.println(
						"Container size=" + currentContainer.size());
				}

				if (currentContainer.size() != 0) {

					javax.swing.JFrame frame = new javax.swing.JFrame();

					PianoRollContainerDisplay display;

					display = new PianoRollContainerDisplay(currentContainer);

					display.setAutoHScale(true);
					// Calculate a optimal horizontal scaling
					if (1.0/display.getPreferredHScale() > 0)
					    display.setMicrosPerPix(1.0/display.getPreferredHScale());
					
					frame.setTitle(currentContainer.getName());
					//DisplayTyp is PIANOROLL
					display.setDisplayType(0);
					frame.getContentPane().setBackground(java.awt.Color.white);
					frame.getContentPane().add("Center", display);
					frame.setSize(display.getSize());
					frame.setLocation(counter * 25 + 10, counter * 25 + 15);
					frame
						.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosing(
							java.awt.event.WindowEvent e) {
							System.exit(0);
						};
					});
					frame.setVisible(true);
				}
			}

			/**
			 * Display the note pool
			 */
			Container notePool = piece.getNotePool();

			System.out.println("Notepool name=" + notePool.getName());
			System.out.println("Notepool size=" + notePool.size());

			if (notePool.size() != 0) {
				javax.swing.JFrame nPFrame = new javax.swing.JFrame();

				PianoRollContainerDisplay nPDisplay;

				nPDisplay = new PianoRollContainerDisplay(notePool);

				nPDisplay.setAutoHScale(true);
				// Calculate a optimal horizontal scaling 
				if (1.0/nPDisplay.getPreferredHScale() > 0)
				    nPDisplay.setMicrosPerPix(1.0/nPDisplay.getPreferredHScale());
				nPFrame.setTitle(notePool.getName());
				//DisplayTyp is PIANOROLL
				nPDisplay.setDisplayType(0);
				nPFrame.getContentPane().setBackground(java.awt.Color.white);
				nPFrame.getContentPane().add("Center", nPDisplay);
				nPFrame.setSize(nPDisplay.getSize());
				nPFrame.setLocation(5, 50);
				nPFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					};
				});
				nPFrame.setVisible(true);
			}
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of MidiReader");
			exception.printStackTrace(System.out);
		}
	}
}