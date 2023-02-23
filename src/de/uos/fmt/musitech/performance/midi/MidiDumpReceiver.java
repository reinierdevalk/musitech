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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class converts MIDI events into musical objects. In order to write into a part 
 * the class needs a Piece object, because the Notepool, MetricalTimeLine and MetaInfoPool 
 * are changed, too. The send(MidiMessage message, long lTimeStamp) method is used to 
 * process the MIDI events like ShortMessages, SysexMessages and MetaMessages. <P>
 * 
 * Thanks to Matthias Pfisterer for the inspiration.
 * 
 * @version $Revision: 8060 $, $Date: 2012-02-20 20:57:04 +0100 (Mon, 20 Feb 2012) $
 * @author Alexander Luedeke
 */
public class MidiDumpReceiver implements Receiver {

	/**
	 * Static class to store note objects in a Array to calculate the duration when a note
	 * off message is received. If there are overlapping equal pitched notes, the first
	 * note on message will be assigned to the first incoming note off message.
	 * 
	 * @see #finishNote(long, int)
	 */
	private static class WaitingNotes {

		//Array for the calculation of note durations
		private List[] waitingNotes = new List[127];

		// Count limit of waiting notes for one pitch
		private final static int initialCapacity = 3;

		/**
		 * Returns the oldest waiting note for a given pitch
		 * 
		 * @param pitch
		 *            the pitch of the oldest note
		 * @return the oldest note, else null, if there is no waiting note
		 */
		private List get(int pitch) {
			assert pitch > -1 && pitch < 127;
			// There is no ArrayList, so there is no waiting note.
			return waitingNotes[pitch];
		}

		/**
		 * Adds a note to wait for a note off MIDI message.
		 * 
		 * @param note
		 *            the note to wait for
		 */
		void add(Note note) {
			assert note != null;
			int pitch = note.getPerformanceNote().getPitch();

			// There is no ArrayList for the given pitch, so create a ArrayList.
			if (waitingNotes[pitch] == null) {
				// Create a new ArrayList
				waitingNotes[pitch] = new ArrayList(initialCapacity);
			}
			if (DEBUG) {
				System.out.println(
					"waitingNotes: A note with pitch="
						+ note.midiNote().getPitch()
						+ " is waiting for the note off signal. (performance time="
						+ note.midiNote().getTime()
						+ ", metrical time="
						+ note.getScoreNote().getMetricTime()
						+ ")");
			}
			// Append the note to the end of this list
			waitingNotes[pitch].add(note);
		}

		/**
		 * Removes the oldest waiting note for a given pitch.
		 * 
		 * @param the
		 *            pitch of the note to remove
		 */
		void remove(int pitch, Note note) {
			assert waitingNotes[pitch] != null
				&& waitingNotes[pitch].size() > 0;

			// Remove the note from the list
			waitingNotes[pitch].remove(note);
		}

		/**
		 * Checks if there is a waiting note for a given pitch, otherwise false.
		 * 
		 * @return there is a waiting note
		 */
		boolean isEmpty(int pitch) {
			if (waitingNotes[pitch] == null)
				return true;

			return waitingNotes[pitch].isEmpty();
		}

		/**
		 * Remove the created ArrayLists.
		 */
		void reset() {
			for (int i = 0; i < 127; i++)
				waitingNotes[i] = null;
			// The garbage collection will remove the unreferenced ArrayLists
		}

		/**
		 * Returns the WaitingNotes as a String for debugging
		 * 
		 * @return String
		 */
		String getInfo() {
			// String to return
			String retString = "[WAITINGNOTES :";

			// Process the whole Array
			for (int i = 0; i < 127; i++) {
				if (waitingNotes[i] != null) {
					retString += "\n\tpitch=" + i;
					// Process the whole ArrayList
					retString += " with "
						+ waitingNotes[i].size()
						+ " note(s), ";
					for (int j = 0; j < waitingNotes[i].size(); j++)
						retString += ((Note) waitingNotes[i].get(j)).toString()
							+ ", ";
				}
			} // end-for
			retString += "]\n";

			return retString;
		}
	}

	//The class writes into a Piece object
	private Piece piece = null;

	//The class writes into a Container object of the Piece object.
	private static NotationVoice container = null;

	//MetricalTimeLine of the Piece object for TimeSignature and Tempo
	private static MetricalTimeLine metricalTimeLine = null;
	
	//The divisionType of the MIDI file. The default value is PPQ.
	private float divisionType = Sequence.PPQ;

	//The resolution of the divisionType. 192 ist the default value.
	private int resolution = 192;

	//The current tempo of the song (in bpm). 120 bmp is the default value.
	private int bpmTempo = 120;

	//16 Containers are representing the MIDI channels
	private static Container channels[] = new Container[16];

	//The current MIDI channel defined by the META event 'channel prefix'. -1 means
	// undefined.
	private int currentChannel = -1;

	//This flag is true if the input data is in MIDI1 format
	protected boolean inputType_MIDI1 = false;

	//In order to extract informations stored in the MIDI velocity (MIDI1, MIDIPlus) like 
	//pitch spelling and slurs this object will be used. 
	private static MidiVelocityAnalyser velocityAnalyser =
		new MidiVelocityAnalyser();

	//For the names of the keys
	private static final char[] sm_keyNames =
		{ 'C', 'C', 'D', 'D', 'E', 'F', 'F', 'G', 'G', 'A', 'A', 'B' };

	//For the accidental
	private static final byte[] sm_Accidental =
		{ 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0 };

	//For debugging and analysing count the detected notes for one MIDI track
	private long noteCount = 0;
	//For debugging and analysing count the time signatures
	private int timeSignatureCount = 0;
	//For debugging and analysing count the tempo changes
	private int tempoChangeCount = 0;
	//For debugging and analysing count note on events used as note off events
	private int noteOffs = 0;

	static boolean DEBUG = false;

	//For debugging only
	private PrintStream m_printStream;
	private boolean m_bDebug;
	private boolean m_bPrintTimeStampAsTicks;

	//For debugging only
	private static final String[] sm_astrKeyNames =
		{ "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	private static final String[] sm_astrKeySignatures =
		{
			"Cb",
			"Gb",
			"Db",
			"Ab",
			"Eb",
			"Bb",
			"F",
			"C",
			"G",
			"D",
			"A",
			"E",
			"B",
			"F#",
			"C#" };
	private static final String[] SYSTEM_MESSAGE_TEXT =
		{
			"System Exclusive (should not be in ShortMessage!)",
			"MTC Quarter Frame: ",
			"Song Position: ",
			"Song Select: ",
			"Undefined",
			"Undefined",
			"Tune Request",
			"End of SysEx (should not be in ShortMessage!)",
			"Timing clock",
			"Undefined",
			"Start",
			"Continue",
			"Stop",
			"Undefined",
			"Active Sensing",
			"System Reset" };

	private static final String[] QUARTER_FRAME_MESSAGE_TEXT =
		{
			"frame count LS: ",
			"frame count MS: ",
			"seconds count LS: ",
			"seconds count MS: ",
			"minutes count LS: ",
			"minutes count MS: ",
			"hours count LS: ",
			"hours count MS: " };

	private static final String[] FRAME_TYPE_TEXT =
		{
			"24 frames/second",
			"25 frames/second",
			"30 frames/second (drop)",
			"30 frames/second (non-drop)",
			};

	private WaitingNotes waitingNotes = new WaitingNotes();

	/**
	 * MidiDumpReceiver constructor.
	 */
	public MidiDumpReceiver() {
		this(System.out, true);
	}

	/**
	 * MidiDumpReceiver constructor.
	 * 
	 * @param printStream
	 *            the target for debugging informations
	 * @param bPrintTimeStampAsTicks
	 *            should time stamps printed as ticks
	 */
	public MidiDumpReceiver(
		PrintStream printStream,
		boolean bPrintTimeStampAsTicks) {
		m_printStream = printStream;
		m_bDebug = false;
		m_bPrintTimeStampAsTicks = bPrintTimeStampAsTicks;
	}

	/**
	 * Sets the divisionType
	 * 
	 * @param dType
	 *            divisionType
	 */
	public void setDivisionType(float dType) {
		if (DEBUG)
			System.out.println(
				"setting divisionType: "
					+ dType
					+ " (0 means ticks per beat, else: ticks per frame");
		divisionType = dType;
	}

	/**
	 * Sets the target of the conversion
	 * 
	 * @param staff
	 *            target the conversion
	 */
	public void setPart(NotationStaff staff) {
		if(staff.size() == 0)
			new NotationVoice(staff);
		
		container = staff.get(0);
	}

	/**
	 * Sets the Piece object and its MetricalTimeLine
	 * 
	 * @param work
	 *            de.uos.fmt.musitech.data.structure.Piece
	 */
	public void setWork(Piece work) {
		assert work != null;
		this.piece = work;
		metricalTimeLine = work.getMetricalTimeLine();
	}

	/**
	 * Sets the resolution of the Resolution
	 * 
	 * @param newResolution
	 *            int
	 */
	public void setResolution(int newResolution) {
		if (DEBUG)
			System.out.println("setting Resolution: " + newResolution);
		resolution = newResolution;
	}

	/**
	 * Resets some counters for notes, time signature and tempo changes. The counters are
	 * only used for some statistics displayed when reaching the end of the track.
	 */
	public void resetCounters() {
		noteCount = 0;
		timeSignatureCount = 0;
		tempoChangeCount = 0;
		noteOffs = 0;
	}

	/**
	 * This method sets the work object and resolution and division type of the sequence
	 * from the given MidiReader and resets some counters for notes, time signature and
	 * tempo changes.
	 * 
	 * @param midiReader
	 *            the object which will use a MidiDumpReceiver
	 * @see #resetCounters()
	 */
	public void init(MidiReader midiReader) {

		this.setWork(midiReader.getPiece());
		this.setResolution(midiReader.getSequence().getResolution());
		this.setDivisionType(midiReader.getSequence().getDivisionType());

		resetCounters();
		waitingNotes.reset();
	}

	/**
	 * Sends the MidiMessage to one of the three decoders. This class must implement the
	 * abstract method send, because MidiDumpReceiver implements
	 * javax.sound.midi.Receiver.
	 * 
	 * @param message
	 *            MidiMessage
	 * @param lTimeStamp
	 *            ticks
	 * @see javax.sound.midi.Receiver#send(MidiMessage message, long lTimeStamp)
	 */
	@Override
	public void send(MidiMessage message, long lTimeStamp) {
		String strMessage = null;

		if (message instanceof ShortMessage) {
			strMessage = decodeMessage((ShortMessage) message, lTimeStamp);
		} else if (message instanceof SysexMessage) {
			strMessage = decodeMessage((SysexMessage) message, lTimeStamp);
		} else if (message instanceof MetaMessage) {
			strMessage = decodeMessage((MetaMessage) message, lTimeStamp);
		} else {
			strMessage = "unknown message type";
		}
		String strTimeStamp = null;
		if (m_bPrintTimeStampAsTicks) {
			strTimeStamp = "tick (send) " + lTimeStamp + ": ";
		} else {
			if (lTimeStamp == -1L) {
				strTimeStamp = "timestamp [unknown]: ";
			} else {
				strTimeStamp = "timestamp " + lTimeStamp + " µs: ";
			}
		}

		if (DEBUG) {
			m_printStream.println(strTimeStamp + strMessage);
		}
	}

	/**
	 * Calculates the note duration when the note off signal is received. The pitch of the
	 * note is used to identify the note.
	 * 
	 * @param ticks
	 *            the current song position in ticks
	 * @param pitch
	 *            the pitich of the note
	 */
	void finishNote(long ticks, int pitch, int channel) {

		if (DEBUG) {
			System.out.println(waitingNotes.getInfo());
		}
		Note waitingNote = null;
		List notes = waitingNotes.get(pitch);
		if (notes != null) {
			for (Iterator iter = notes.iterator(); iter.hasNext();) {
				Note note = (Note) iter.next();
				if (note.midiNote().getChannel() == channel) {
					waitingNote = note;
					break;
				}
			}
		}

		if (waitingNote != null) {
			// Get the waiting note

			// Get the current timeStamp in milliseconds from the MetricalTimeLine.
			long tSInMillis = toMicros(ticks);

			// Calculate the performance time and set it
			long perfDuration =
				tSInMillis - waitingNote.midiNote().getTime();
			if (DEBUG) {
				System.out.println(
					"calculated perfDuration ("
						+ perfDuration
						+ "): current lTimeStamp="
						+ tSInMillis
						+ ", recent lTimeStamp="
						+ waitingNote.midiNote().getTime()
						+ ", pitch="
						+ pitch
						+ ", recent pitch="
						+ waitingNote.midiNote().getPitch());
			}
			// Set the length of the waiting MidiNote
			waitingNote.midiNote().setDuration(perfDuration);
			//			// Set the offset of the waiting MidiNote
			//			waitingNote.getMidiNote().offset = tSInMillis;

			// Calculate the metrical duration of the score note
			Rational metricalDuration =
				ticksToRational(ticks).sub(
					waitingNote.getScoreNote().getMetricTime());
			if (DEBUG) {
				System.out.println(
					"calculated metricalDuration ("
						+ metricalDuration
						+ "): current metricalTime="
						+ ticksToRational(ticks)
						+ ", recent metricalTime="
						+ waitingNote.getScoreNote().getMetricTime()
						+ ", pitch="
						+ pitch
						+ ", recent pitch="
						+ waitingNote.midiNote().getPitch());
			}

			// Set the metrical duration of the score note only when it is greater or
			// equal zero
			if (metricalDuration.isGreaterOrEqual(0, 1))
				waitingNote.getScoreNote().setMetricDuration(metricalDuration);
			else {
				System.out.println(
					"calculated metricalDuration ("
						+ metricalDuration
						+ "): current metricalTime="
						+ ticksToRational(ticks)
						+ ", recent metricalTime="
						+ waitingNote.getScoreNote().getMetricTime()
						+ ", pitch="
						+ pitch
						+ ", recent pitch="
						+ waitingNote.midiNote().getPitch()
						+ ">>MidiDumpReceiver: There is a negativ metrical duration. See the details in the previous line in debugging mode.");
				assert false;
			}

			//Reset the Array cell
			waitingNotes.remove(pitch, waitingNote);

		} else { // case: waitingNotes.isEmpty(pitch)
			System.out.println(
				">>>calculateDuration(): calculate duration is not possible though a note off signal was received for pitch="
					+ pitch
					+ " ("
					+ getKeyName(pitch)
					+ "), the metric time is "
					+ ticksToRational(ticks));
			assert false;
		}
	}

	/**
	 * Returns MIDI ticks in microseconds
	 * @param ticks
	 * @return
	 */
	long toMicros(long ticks) {
		return metricalTimeLine.getTime(ticksToRational(ticks));
	}

	/**
	 * Calculates the metric time from ticks as ticks_position / (4*resolution).
	 * 
	 * @param ticks 
	 *            the song position in ticks
	 * @return metric time
	 */
	private Rational ticksToRational(long ticks) {
		if (DEBUG)
			System.out.println(
				"\tCalculating the metric time for ticks="
					+ ticks
					+ " returns "
					+ new Rational((int) (ticks), 4 * resolution));
		Rational r = new Rational((int) (ticks), 4 * resolution);
		r.reduce();
		return r;
	}

	/**
	 * Processes MIDI ShortMessages like not on, note off, key pressure and pitch wheel
	 * change.
	 * 
	 * @param message
	 *            the short message
	 * @param ticks
	 *            the song position in ticks
	 * @return some infos about the conversion
	 * @see #ShortMessage
	 */
	public String decodeMessage(ShortMessage message, long ticks) {
		String strMessage = null;

		// For debugging of only one pitch of notes, leave the global field 'DEBUG' false.
		// Just
		// uncomment the following lines and set the wanted pitch value:
		//		if (message.getData1() == 71)
		//			DEBUG = true;
		//		else
		//			DEBUG = false;

		switch (message.getCommand()) {
			// note off
			case 0x80 :
			case 0x81 :
			case 0x82 :
			case 0x83 :
			case 0x84 :
			case 0x85 :
			case 0x86 :
			case 0x87 :
			case 0x8A :
			case 0x8B :
			case 0x8C :
			case 0x8D :
			case 0x8E :
			case 0x8F :
				if (DEBUG) {
					strMessage =
						"note Off "
							+ getKeyName(message.getData1())
							+ " velocity: "
							+ message.getData2()
							+ " channel: "
							+ message.getChannel();
				}
				finishNote(ticks, message.getData1(), message.getChannel());
				break;
				// Note on
			case 0x90 :
			case 0x91 :
			case 0x92 :
			case 0x93 :
			case 0x94 :
			case 0x95 :
			case 0x96 :
			case 0x97 :
			case 0x98 :
			case 0x99 :
			case 0x9A :
			case 0x9B :
			case 0x9C :
			case 0x9D :
			case 0x9E :
			case 0x9F :
				startNote(message, ticks);
				break;

			case 0xc0 :
				if (DEBUG) {
					strMessage = "_SET_program change " + message.getData1();
				}
//-*-				System.out.println(
//-*-					"Channel: "
//-*-						+ message.getChannel()
//-*-						+ " data1: "
//-*-						+ message.getData1()
//-*-						+ " data2: "
//-*-						+ message.getData2()
//-*-						+ " status: "
//-*-						+ message.getStatus());
				eventToPart(message, ticks);
				break;

			case 0xE0 :
				if (DEBUG) {
					strMessage =
						"pitch wheel change "
							+ get14bitValue(
								message.getData1(),
								message.getData2());
				}
				eventToPart(message, ticks);
				break;

			case 0xa0 :
				if (DEBUG) {
					strMessage =
						"polyphonic key pressure "
							+ getKeyName(message.getData1())
							+ " pressure: "
							+ message.getData2();
				}
				eventToPart(message, ticks);
				break;

			case 0xb0 :
				if (DEBUG) {
					strMessage =
						"control change "
							+ message.getData1()
							+ " value: "
							+ message.getData2();
				}
				eventToPart(message, ticks);
				break;

			case 0xd0 :
				if (DEBUG) {
					strMessage =
						"key pressure "
							+ getKeyName(message.getData1())
							+ " pressure: "
							+ message.getData2();
				}
				eventToPart(message, ticks);
				break;

			case 0xF0 :
				/**
				 * MIDI System Exclusive Event (SysEx)
				 */
				if (DEBUG) {
					strMessage = SYSTEM_MESSAGE_TEXT[message.getChannel()];
					switch (message.getChannel()) {
						case 0x1 :
							int nQType = (message.getData1() & 0x70) >> 4;
							int nQData = message.getData1() & 0x0F;
							if (nQType == 7) {
								nQData = nQData & 0x1;
							}
							strMessage += QUARTER_FRAME_MESSAGE_TEXT[nQType]
								+ nQData;
							if (nQType == 7) {
								int nFrameType =
									(message.getData1() & 0x06) >> 1;
								strMessage += ", frame type: "
									+ FRAME_TYPE_TEXT[nFrameType];
							}
							break;

						case 0x2 :
							strMessage
								+= get14bitValue(
									message.getData1(),
									message.getData2());
							break;

						case 0x3 :
							strMessage += message.getData1();
							break;
					}
				} //Debug end
				break;

			default :
				if (DEBUG) {
					strMessage =
						"unknown message: status = "
							+ message.getStatus()
							+ ", byte1 = "
							+ message.getData1()
							+ ", byte2 = "
							+ message.getData2();
				}
				break;
		}
		if (message.getCommand() != 0xF0) {
			if (DEBUG) {
				int nChannel = message.getChannel() + 1;
				String strChannel = "(channel " + nChannel + "): ";
				strMessage = strChannel + strMessage;
			}
		}
		return strMessage;
	}

	/**
	 * Interprets a NoteOn message.
	 * 
	 * @param message the ShortMessage
	 * @param ticks song position in ticks
	 */
	private void startNote(ShortMessage message, long ticks) {
		// Get the time in microseconds
		long tSInMicros = toMicros(ticks);
		String strMessage;
		if (DEBUG) {
			strMessage =
				"_SET_NOTE_ON, "
					+ getKeyName(message.getData1())
					+ " ("
					+ message.getData1()
					+ ") velocity: "
					+ message.getData2()
					+ ", song position in us: "
					+ tSInMicros
					+ ", metric time is: "
					+ ticksToRational(ticks);
		}
		// Some MIDI-programs use velocity = 0 to set the note off signal.
		// In that case no note object has to be created.
		// There is a problem if the MIDI file contains a note
		// on event with velocity=0 to set a mute note. In order to decide
		// whether the note has to be handled like a note on or off, the
		// reader checks the waiting notes for a note with equal pitch.
		if (message.getData2() != 0
			|| (message.getData2() == 0
				&& waitingNotes.isEmpty(message.getData1()))) {

			/**
			 * Create a Note object when 'note on' is received.
			 */

			//Create a MidiNote object with unknow lenght, so a default value is used.
			/**
			 * @param b
			 *            long, the beginning of the note (in milliseconds)
			 * @param l
			 *            long, the length of the note = // Wait for the 'not off' to
			 *            calculate the length
			 * @param v
			 *            long, the velocity of the note = message.getData2();
			 * @param p
			 *            long, the pitch of the note = message.getData1();
			 * @param c
			 *            int, the midi channel // message.getChannel()
			 */
			MidiNote midiNote =
				new MidiNote(
					tSInMicros,
					100000,
					message.getData2(),
					message.getData1(),
					message.getChannel());

			//Create a ScoreNote object
			ScoreNote scoreNote = new ScoreNote();

			// Use the pitch of the MidiNote to create the score note
			int nKeyNumber = message.getData1();

			if (nKeyNumber > 127) {
				System.out.println("MidiDumpReceiver: Wrong nKeyNumber");
				assert false;
			} else {
				int nNote = nKeyNumber % 12;

				scoreNote.setDiatonic(sm_keyNames[nNote]);
				if (DEBUG) {
					System.out.print("Diatonic: " + sm_keyNames[nNote]);
				}
				scoreNote.setAlteration(sm_Accidental[nNote]);
				if (DEBUG) {
					System.out.print(", Accidental: " + sm_Accidental[nNote]);
				}
				scoreNote.setOctave((byte) (((nKeyNumber / 12) - 4)-1));
				if (DEBUG) {
					System.out.print(", Octave: " + scoreNote.getOctave());
				}
				scoreNote.setMetricTime(ticksToRational(ticks));
				if (DEBUG) {
					System.out.println(", Onset: " + ticksToRational(ticks));
					System.out.println(
						", timeStamp: "
							+ ticks
							+ ", timeSignature: "
							+ metricalTimeLine
								.getTimeSignatureMarker(ticksToRational(ticks))
								.getTimeSignature()
								.getNumerator()
							+ "/"
							+ metricalTimeLine
								.getTimeSignatureMarker(ticksToRational(ticks))
								.getTimeSignature()
								.getDenominator()
							+ ", Resolution: "
							+ resolution);
				}
				//In case calculateDuration() will fail the duration is 1/4.
				scoreNote.setMetricDuration(new Rational(1, 4));
			}

			//Create a Note object
			Note aNote = new Note();
			aNote.setPerformanceNote(midiNote);
			aNote.setScoreNote(scoreNote);

			// Case the input format is MIDI1
			if (inputType_MIDI1) {
				try {
					//Informations from the MIDI velocity will be extracted, processed and added 
					//to the diatonic, accidental, velocity
					velocityAnalyser.processVelocity(aNote);

					if (DEBUG) {
						System.out.print(velocityAnalyser.toString());
					}
				} catch (WrongArgumentException wAException) {
					wAException.printStackTrace();
				}
			}

			//Add the note to the part
			container.add(aNote);
			noteCount++;

			if (channels[message.getChannel()] == null)
				channels[message.getChannel()] = new BasicContainer();
				
			channels[message.getChannel()].add(aNote);

			// Add to list of notes waiting for the note off signal
			waitingNotes.add(aNote);
		}
		// This should be a note off signal, so the duration of a recent note has
		// to be calculated.
		else {
			if (DEBUG) {
				System.out.println(
					"Get note on with velocity=0. "
						+ "This should be a note off signal at metric time:"
						+ ticksToRational(ticks));
				strMessage =
					"note Off "
						+ getKeyName(message.getData1())
						+ " velocity: "
						+ message.getData2();
			}
			noteOffs++;
			finishNote(ticks, message.getData1(), message.getChannel());
		}
	}

	/**
	 * Processes MIDI SysexMessages.
	 * 
	 * @param message
	 *            the short message
	 * @param ticks
	 *            the song position in ticks
	 * @return some infos about the conversion
	 * @see #SysexMessage
	 */
	public String decodeMessage(SysexMessage message, long ticks) {
		byte[] abData = message.getData();
		String strMessage = null;

		if (message.getStatus() == SysexMessage.SYSTEM_EXCLUSIVE) {
			if (DEBUG) {
				strMessage = "Sysex message: F0" + getHexString(abData);
			}
		} else if (
			message.getStatus() == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
			if (DEBUG) {
				strMessage =
					"Special Sysex message (F7):" + getHexString(abData);
			}
		}
		// FIXME save in an object
		return strMessage;
	}

	/**
	 * Processes MIDI MetaMessages like text events, copyright, namens, lyrics and end of
	 * track.
	 * 
	 * @param message
	 *            the short message
	 * @param ticks
	 *            the song position in ticks
	 * @return some infos about the conversion
	 * @see #MetaMessage
	 */
	public String decodeMessage(MetaMessage message, long ticks) {
		//		byte[] abMessage = message.getMessage();
		byte[] abData = message.getData();
		//		int nDataLength = message.getLength();
		String strMessage = null;

		MetaDataItem metaDataItem;
		MetaDataValue metaDataValue;
		switch (message.getType()) {
			case 0 :
				int nSequenceNumber = abData[0] * 256 + abData[1];
				if (DEBUG) {
					System.out.println(
						"_SET_Sequence Number: " + nSequenceNumber);
				}
				// TODO add text meta event
				container.getParent().setName(
					container.getParent().getName() + "number: " + nSequenceNumber + ", ");
				break;

			case 1 :
				// This event is used for annotating the track with arbitrary text.
				// Arbitrary 8-bit data (other than ascii text) is also permitted.
				String strText = new String(abData);
				if (DEBUG) {
					System.out.println("_SET_Text Event: " + strText);
				}
				metaDataItem = new MetaDataItem("text event");
				// The metric time is added to the text, because MetaDataItems are not timed
				metaDataValue =
					new MetaDataValue(
						"text",
						strText
							+ " ["
							+ ticksToRational(ticks)
							+ "="
							+ metricalTimeLine.getTime(ticksToRational(ticks))
							+ "]");
				metaDataItem.setMetaValue(metaDataValue);
				if(piece.getMetaData(piece)==null)
				    piece.setMetaData(piece,new MetaDataCollection());
				piece.getMetaData(piece).addMetaDataItem(metaDataItem);
				break;

			case 2 :
				//			    This event is for a Copyright notice in ascii text.
				//			    This should be of the form "(C) 1850 J.Strauss"
				//			    This event should be the first event on the first track.
				String strCopyrightText = new String(abData);
				if (DEBUG) {
					System.out.println(
						"_SET_Copyright Notice: " + strCopyrightText);
				}
				if(piece.getMetaData(piece)==null)
				    piece.setMetaData(piece,new MetaDataCollection());
				metaDataItem = new MetaDataItem("copyright");
				metaDataValue = new MetaDataValue("text", strCopyrightText);
				metaDataItem.setMetaValue(metaDataValue);
				piece.getMetaData(piece).addMetaDataItem(metaDataItem);
				break;

			case 3 :
				//  	Name of the sequence or track
				String strTrackName = new String(abData);
				if (DEBUG) {
					System.out.println(
						"_SET_Sequence/Track Name: " + strTrackName);
				}
				container.getParent().setName(
					container.getParent().getName() + "name: " + strTrackName + ", ");
				break;

			case 4 :
				// A description of the instrument(s) used on this track.
				// This can also be used to describe instruments on a particular MIDI Channel
				// within a track, by preceding this event with the meta-event MIDI Channel
				// Prefix. (or specifying the channel(s) within the text).
				String strInstrumentName = new String(abData);
				if (DEBUG) {
					System.out.println(
						"_SET_Instrument Name: " + strInstrumentName);
				}
				container.getParent().setName(
					container.getParent().getName()
						+ "instrument: "
						+ strInstrumentName
						+ ", ");
				break;

			case 5 :
				// Lyrics for the song.
				// Normally, each syllable will have it's own lyric-event, which
				// occurs at the time the lyric is to be sung.
				String strLyrics = new String(abData);
				if (DEBUG) {
					System.out.println("_SET_Lyric: " + strLyrics);
				}
				LyricsSyllable ls =
					new LyricsSyllable(toMicros(ticks), strLyrics);
				ls.setMetricTime(ticksToRational(ticks));
				if(container.getLyrics() == null)
					container.setLyrics(new LyricsContainer(Locale.ENGLISH));
				container.getLyrics().get(0).add(ls);
				break;

			case 6 :
				//            Normally on the first track of a format 1 or format 0 file.
				//            Marks a significant point in the sequence (eg "Verse 1")
				String strMarkerText = new String(abData);
				if (DEBUG) {
					System.out.println("_SET_Marker: " + strMarkerText);
				}
				// Normally on the first track of a format 1 or format 0 file.
				// Marks a significant point in the sequence (eg "Verse 1")
				metaDataItem = new MetaDataItem("marker text");
				metaDataValue = new MetaDataValue("text", strMarkerText);
				metaDataItem.setMetaValue(metaDataValue);
				if(piece.getMetaData(piece) == null)
					piece.setMetaData(piece,new MetaDataCollection());
				piece.getMetaData(piece).addMetaDataItem(metaDataItem);
				break;

			case 7 :
				// Used to include cues for events happening on-stage, such as "curtain
				// rises", "exit, stage left", etc.
				String strCuePointText = new String(abData);
				if (DEBUG) {
					System.out.println("_SET_Cue Point: " + strCuePointText);
				}
				// TODO model cue points
				metaDataItem = new MetaDataItem("cue point");
				metaDataValue = new MetaDataValue("text", strCuePointText);
				metaDataItem.setMetaValue(metaDataValue);
				piece.getMetaData(piece).addMetaDataItem(metaDataItem);
				break;

			case 0x20 :
				// MIDI Channel Prefix: This meta event defines a MIDI channel for
				// the following meta events. Its effect is terminated by another MIDI
				// Channel Prefix event or any non- Meta event. It is often used before
				// an Instrument Name Event to specify which channel an instrument name
				// represents.
				currentChannel = abData[0];
				if (DEBUG) {
					System.out.println(
						"_SET_MIDI Channel Prefix: " + currentChannel);
				}
				break;

			case 0x2F :
				if (DEBUG) {
					System.out.println("_SET_END OF TRACK");
					System.out.println(
						" found "
							+ noteCount
							+ " notes, "
							+ timeSignatureCount
							+ " timesignatures, "
							+ tempoChangeCount
							+ " tempo changes");
					if (noteOffs > 0)
						System.out.println(
							" note on events are used as "
								+ noteOffs
								+ " note off events");
				} 
				// Reset the channel prefix
				currentChannel = -1;
				break;

			case 0x51 :
				// This sets the tempo in microseconds per quarter note. This means a change
				// in the unit-length of a delta-time tick. (note 1)
				//
				//            If not specified, the default tempo is 120 beats/minute, which is
				// equivalent to tttttt=500000
				// TODO optimize by using <<
				// Calculate the new tempo in bpm (only for debugging)
				if (DEBUG) {
					bpmTempo =
						60000000
							/ (signedByteToUnsigned(abData[0]) * 65536
								+ signedByteToUnsigned(abData[1]) * 256
								+ signedByteToUnsigned(abData[2]));
					System.out.println("_SET_TEMPO ");
					System.out.println("new tempo in BPM: " + bpmTempo);
				}
				double micros_per_whole =
					4
						* (signedByteToUnsigned(abData[0]) * 65536
							+ signedByteToUnsigned(abData[1]) * 256
							+ signedByteToUnsigned(abData[2]));

				// Get the current metric time
				Rational currentMetricTime = ticksToRational(ticks);
				// Get the current timeStamp in microseconds from the MetricalTimeLine.
				long tSInMicros = metricalTimeLine.getTime(currentMetricTime);

				metricalTimeLine.setTempo(currentMetricTime, micros_per_whole);

				tempoChangeCount++;
				break;

			case 0x54 :
				// This (optional) event specifies the SMTPE time at which the track is to
				// start.
				// This event must occur before any non-zero delta-times, and before any MIDI
				// events.
				//
				// In a format 1 MIDI file, this event must be on the first track (the tempo
				// map).
				// 	
				// hh mm ss fr hours/minutes/seconds/frames in SMTPE format
				// 	this must be consistant with the message MIDI Time Code Quarter Frame (in
				// 	particular, the time-code type must be present in hh)
				// ff Fractional frame, in hundreth's of a frame
				if (DEBUG) {
					strMessage =
						"SMTPE Offset: "
							+ abData[0]
							+ ":"
							+ abData[1]
							+ ":"
							+ abData[2]
							+ "."
							+ abData[3]
							+ "."
							+ abData[4];
				}
				break;

			case 0x58 :
				// Time signature of the form:
				//  nn/2^dd
				//  eg: 6/8 would be specified using nn=6, dd=3
				//
				// The parameter cc is the number of MIDI Clocks per metronome tick.
				//
				// Normally, there are 24 MIDI Clocks per quarter note. However, some software
				// allows this to be set by the user. The parameter bb defines this in terms
				// of the number of 1/32 notes which make up the usual 24 MIDI Clocks (the
				// 'standard' quarter note).
				//
				//Message format: FF 58 04 nn dd cc bb
				//nn Time signature, numerator
				//dd Time signature, denominator expressed as a power of 2.
				//        eg a denominator of 4 is expressed as dd=2
				//cc MIDI Clocks per metronome tick
				//bb Number of 1/32 notes per 24 MIDI clocks (8 is standard)
				if (DEBUG) {
					System.out.println(
						"_SET_TIME_SIGNATURE, "
							+ abData[0]
							+ "/"
							+ (1 << abData[1])
							+ ", MIDI clocks per metronome tick: "
							+ abData[2]
							+ ", 1/32 per 24 MIDI clocks: "
							+ abData[3]
							+ ", metric time is: "
							+ ticksToRational(ticks));
				}
				/**
				 * Add a time signature to the MetricalTimeLine of the Piece object
				 */
				assert piece != null;
				//Create the TimeSignature and TimeSignatureMarker
				TimeSignatureMarker tsm =
					new TimeSignatureMarker(
						new TimeSignature(
							abData[0],
							1 << abData[1]),
						ticksToRational(ticks));
				//Add it to the MetricalTimeLine
				metricalTimeLine.add(tsm);
				timeSignatureCount++;
				break;

			case 0x59 :
				// Key Signature, expressed as the number of sharps or flats, and a
				// major/minor flag.
				//
				// 0 represents a key of C, negative numbers represent 'flats', while positive
				// numbers represent 'sharps'.
				// sf number of sharps or flats
				// 
				// Message format: FF 59 02 sf mi
				// -7 = 7 flats
				//  0 = key of C
				// +7 = 7 sharps
				// mi 0 = major key
				//    1 = minor key
				// 
				if (DEBUG) {
					String strMode = (abData[1] == 1) ? "minor" : "major";
					strMessage =
						"key signature: "
							+ Math.abs(abData[0]) + (abData[0]<0?"flat(s)":"sharp(s)")
							+ strMode;
				}
				/**
				 * Add a key marker to the MetricalTimeLine of the Piece object
				 */
				Rational metricTime = ticksToRational(ticks);
				long timeStamp = metricalTimeLine.getTime(metricTime);

				KeyMarker km = new KeyMarker(metricTime, timeStamp);
				km.setMode((abData[1] == 1) ? Mode.MODE_MINOR : Mode.MODE_MAJOR);
				km.setAccidentalNum(abData[0]); 
				String keyName = sm_astrKeySignatures[abData[0] + 7];
				km.setRoot(keyName.charAt(0));
				if (keyName.length() > 1) {
					switch (keyName.charAt(1)) {
						case 'b' :
							km.setRootAccidental(-1);
						case '#' :
							km.setRootAccidental(1);
					}
				}
				try {
					metricalTimeLine.add(km);
				} catch (Exception e) {
					throw new IllegalArgumentException(">>MidiDumpReceiver: Error when adding KeyMarker to the MetricalTimeLine");
				}
				break;

			case 0x7F :
				// Sequencer-Specific Meta-event
				//
				//This is the MIDI-file equivalent of the System Exclusive Message.
				//
				//A manufacturer may incorporate sequencer-specific directives into a MIDI
				// file using this event.
				// Mesaage format: FF 7F <len> <id> <data>
				//	<len> length of <id>+<data> (variable length quantity)
				//	<id> 1 or 3 bytes representing the Manufacturer's ID
				//This value is the same as is used for MIDI System Exclusive messages
				//	<data> 8-bit binary data
				if (DEBUG) {
					String strDataDump = "";
					for (int i = 0; i < abData.length; i++) {
						strDataDump += abData[i] + " ";
					}
					strMessage =
						"_SET_Sequencer-Specific Meta event: " + strDataDump;
				}
				break;

			default :
				if (DEBUG) {
					String strUnknownDump = "";
					for (int i = 0; i < abData.length; i++) {
						strUnknownDump += abData[i] + " ";
					}
					strMessage = "unknown Meta event: " + strUnknownDump;
				}
				break;

		}
		return strMessage;
	}

	public void eventToPart(ShortMessage message, long ticks) {

		long timeInMillis = metricalTimeLine.getTime(ticksToRational(ticks));

		MidiTimedMessage timedEvent =
			new MidiTimedMessage(timeInMillis, message);
		container.add(timedEvent);
	}

	/**
	 * Returns the key name for a given key number
	 * 
	 * @param nKeyNumber
	 *            the key number
	 * @return the key name
	 */
	public static String getKeyName(int nKeyNumber) {
		if (nKeyNumber > 127) {
			assert nKeyNumber < 127;
			return "illegal value";
		} else {
			int nNote = nKeyNumber % 12;
			int nOctave = nKeyNumber / 12;
			return sm_astrKeyNames[nNote] + (nOctave - 1);
		}
	}

	/**
	 * Returns the 14 bit value for a pitch wheel change
	 * 
	 * @param nLowerPart
	 *            the lower part
	 * @param nHigherPart
	 *            the higher part
	 * @return the 14 bit value
	 */
	public static int get14bitValue(int nLowerPart, int nHigherPart) {
		return (nLowerPart & 0x7F) | ((nHigherPart & 0x7F) << 7);
	}

	/**
	 * Returns the unsigned value of a signed byte
	 * 
	 * @param b
	 *            the signed byte
	 * @return the unsigned value
	 */
	private static int signedByteToUnsigned(byte b) {
		if (b >= 0) {
			return b;
		} else {
			return 256 + b;
		}
	}

	/**
	 * Returns the hex string for a byte array.
	 * 
	 * @param aByte
	 *            the byte array
	 * @return the hex string
	 */
	public static String getHexString(byte[] aByte) {
		StringBuffer sbuf = new StringBuffer(aByte.length * 3 + 2);
		for (int i = 0; i < aByte.length; i++) {
			sbuf.append(' ');
			byte bhigh = (byte) ((aByte[i] & 0xf0) >> 4);
			sbuf.append((char) (bhigh > 9 ? bhigh + 'A' - 10 : bhigh + '0'));
			byte blow = (byte) (aByte[i] & 0x0f);
			sbuf.append((char) (blow > 9 ? blow + 'A' - 10 : blow + '0'));
		}
		return new String(sbuf);
	}

	/**
	 * Returns a Container object that represents a MIDI channel of the MidiDumpReceiver.
	 * 
	 * @param channelNumber
	 *            the number of the MIDI channel between 0 and 15
	 * @return a Container object that represents the MIDI channel
	 */
	public static Container getChannel(int channelNumber) {
		assert - 1 < channelNumber && channelNumber < 16;

		return channels[channelNumber];
	}

	/**
	 * Empty method. This class implements Receiver so there must be the method close.
	 */
	@Override
	public void close() {
		//
	}

}