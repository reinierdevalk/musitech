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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class to write a Container or a Piece object into a MIDI file. <BR>
 * Usage-example:
 * 
 * <PRE>
 * 
 * MidiWriter midiWriter = new MidiWriter(fileName); 
 * midiWriter.setContainer(container);
 * midiWriter.write();
 * </PRE>
 * 
 * @see de.uos.fmt.musitech.performance.midi.MidiReader
 * @see de.uos.fmt.musitech.data.structure.container.Container
 * @version $Revision: 8053 $, $Date: 2012-02-20 04:01:02 +0100 (Mon, 20 Feb 2012) $
 * @author Alexander Luedeke
 */
public class MidiWriter {

    //MIDI file to export into
    private File midiFile = null;

    //Sequence of the MidiFile
    private Sequence sequence = null;

    //The Piece object is the source of the conversion
    private Piece piece = null;

    //TimeSignature of the song. The default value is (4,4).
    private TimeSignature timeSignature = new TimeSignature(4, 4);

    //Tempo of the song in bpm. The default value is 120.
    private long tempo = 120;

    //The context of the MidiWriters piece object
    private Context context = null;

    //To export different containers into one MIDI file
    private Container containers = null;

    // Calculate timeStamp of the endMarker in MetricalTimeLine

    // For debugging only
    private final boolean DEBUG = false;
    
    //Outputstream to export into
    private OutputStream outputStream;

    public static int defaultResolution = 384;

    /**
     * MidiWriter constructor for a given filename.
     * 
     * @param filename the name of the file to write into
     */
    public MidiWriter(String fileName) {
        // Create an empty MIDI file
        midiFile = new File(fileName);
    }

    /**
     * MidiWriter constructor for a piece object to write.
     * 
     * @param filename the name of the file to write into
     * @param piece the piece object to write into the MIDI file
     */
    public MidiWriter(String fileName, Piece piece) {
        this(fileName);
        this.setPiece(piece);
    }
    
    /**
     * MidiWriter constructor for a piece object to write.
     * 
     * @param filename the name of the file to write into
     * @param piece the piece object to write into the MIDI file
     */
    public MidiWriter(File midiFile, Piece piece) {
        this.midiFile = midiFile;
        this.setPiece(piece);
    }

    /**
     * MidiWriter constructor for a container object to write.
     * 
     * @param filename the name of the file to write into
     * @param container the container object to write into the MIDI file
     */
    public MidiWriter(String fileName, Container container) {
        this(fileName);
        this.setContainer(container);
    }
    
    /**
     * MidiWriter constructor for a piece object to write.
     * 
     * @param outputStream the outputstream to write into
     * @param piece the piece object to write into the MIDI file
     */
    public MidiWriter(OutputStream outputStream, Piece piece) {
        this.outputStream = outputStream;
        this.setPiece(piece);
    }

    /**
     * Sets a piece object.
     * 
     * @param piece the piece object to set
     */
    public void setPiece(Piece piece) {
        context = piece.getContext();
        containers = new BasicContainer(context);
        this.piece = piece;
    }

    /**
     * Returns the piece being written to a Midi file.
     * 
     * @return The piece. 
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets a container object
     * 
     * @param container the container to set
     */
    public void setContainer(Container container) {
        if (container.getContext() == null){
            context = Context.getDefaultContext();
        }
        context = container.getContext();
        containers = new BasicContainer(context);
        containers.add(container);
    }

    /**
     * Starts the export of the objects into the new MIDI file.
     * 
     * @return the number of bytes written into the MIDI file int
     */
    public int write() {

        if (midiFile == null && outputStream == null) {
            System.out.print(">>MidiWriter: error writing the MIDI file");
            return 0;
        }
        // added by Jan Kramer
        if (piece == null){
            piece = new Piece();
            piece.setContext(context);
            piece.getContainerPool().addAll(containers);
        } else {
        	piece.getContainerPool().addAll(piece.getScore());
        }
        	
        	
        
        // 
        if (piece == null && containers.size() == 0) {
            System.out.print(">>MidiWriter: set a Piece or Container object first");
            return 0;
        }

        try {
            // Get a new sequence from the Sequencing object and create an empty track 0
            sequence = new Sequence(Sequence.PPQ, defaultResolution);
        } catch (InvalidMidiDataException e) {
            // This should never happen, because DivisionType PPQ is valid.
            e.printStackTrace();
            assert false;
        }

        MidiNoteSequence mNoteSequence = new MidiNoteSequence();
        mNoteSequence.setContext(getPiece().getContext());

        // Write the NotePool into a Track
        if (piece != null)
//            if (piece.getNotePool().size() > 0) {
                
                
                mNoteSequence = MidiNoteSequence.convert(piece.getContainerPool());
                mNoteSequence.noteToMidi(sequence);
//            }

        // Write each Container into a new track
        Iterator it = containers.iterator();
        Container currentContainer;
        while (it.hasNext()) {
            currentContainer = (Container) it.next();
            if (currentContainer.size() > 0) {
                //untere zeile l�schen
                mNoteSequence = new MidiNoteSequence();
                mNoteSequence = MidiNoteSequence.convert(currentContainer);
                mNoteSequence.noteToMidi(sequence);
            }
        }

        // TODO: Wenn mit setContainer nur container und kein Piece objekt dem
        // Writer geadded werden, dann ist das piece objekt null. Ich muss �ber den
        // context der container ein piece objekt beschaffen, um die MetricalTimeLine zu
        // bekommen. Dieses Statement noch ueberpruefen!
        if (piece == null && context != null)
            piece = context.getPiece();

        MetricalTimeLine metricalTimeLine = null;
        if (piece != null) {
            metricalTimeLine = piece.getMetricalTimeLine();
        }

        if (metricalTimeLine != null) {
            // process metricalTimeLine events
            for (int i = 0; i < metricalTimeLine.size(); i++) {
                byte[] data = null;

                // Set the tempo
                if (metricalTimeLine.get(i) instanceof TimedMetrical) {
                    TimedMetrical timedMetrical = (TimedMetrical) metricalTimeLine.get(i);
                    long timeStamp = timedMetrical.getTime();
                    // Do not set a tempo at the end of the song
                    if (timeStamp == metricalTimeLine.getEndMarker().getTime())
                        continue;
                    Rational metricTime = timedMetrical.getMetricTime();

                    // Number of beats per measure
                    int numberOBPM = metricalTimeLine.getTimeSignatureMarker(metricTime)
                            .getTimeSignature().getNumerator();
                    // Get the current tempo in beats per minute
                    double tempo = metricalTimeLine.getTempo(timeStamp, numberOBPM);
                    // Calculate microseconds per quarter-note
                    int mSPQ = (int) (60L * 1000000 / tempo);
                    // Write the tempo into a byte Array
                    data = new byte[] {(byte) (mSPQ / 65536),
                                       (byte) (mSPQ % 65536 / 256), (byte) (mSPQ % 256)};
                    byte[] data2 = new byte[] {(byte) (mSPQ >> 16),
                                       (byte) (mSPQ >> 8), (byte) mSPQ };
                    assert data[0] == data2[0];
                    assert data[1] == data2[1];
                    assert data[2] == data2[2];
                    
                    // Write the array into the MetaMessage. Typ=81 (0x51) means tempo
                    // in microseconds per quarter-note.
                    MetaMessage mMessage = new MetaMessage();
                    try {
                        if (data != null)
                            mMessage.setMessage(81, data, 3);
                    } catch (InvalidMidiDataException iMDE) {
                        System.out.println(iMDE);
                        System.out
                                .println(">>MidiWriter: InvalidMidiDataException in write()");
                    }
                    // Create a MidiEvent
                    MidiEvent mEvent = new MidiEvent(mMessage, (timeStamp));

                    // Write the Midi Tempo Event into first track of the sequence
                    if (sequence.getTracks() == null)
                        sequence.createTrack();
                    sequence.getTracks()[0].add(mEvent);
                }

                /**
                 * Set the time signature
                 */
                if (metricalTimeLine.get(i) instanceof TimeSignatureMarker) {
                    TimeSignatureMarker timeSigMarker = (TimeSignatureMarker) metricalTimeLine
                            .get(i);
                    TimeSignature timeSig = timeSigMarker.getTimeSignature();

                    Rational metricTime = timeSigMarker.getMetricTime();
                    long timeStamp = metricalTimeLine.getTime(metricTime);

                    // Write the time signature data into a byte array
                    
                    
                    data = new byte[] {(byte) timeSig.getNumerator(),
                                       (byte) MyMath.ilog2(timeSig.getDenominator()),
                                       (byte) (24*4/timeSig.getDenominator()),
                                       (byte) 8};

                    // Write the array into the MetaMessage. Typ=88 (0x58)
                    MetaMessage mMessage = new MetaMessage();
                    try {
                        if (data != null)
                            mMessage.setMessage(88, data, 4);
                    } catch (InvalidMidiDataException iMDE) {
                        System.out.println(iMDE);
                        System.out
                                .println(">>MidiWriter: InvalidMidiDataException in write()");
                    }

                    // Create a MidiEvent
                    MidiEvent mEvent = new MidiEvent(mMessage, (long) metricalTimeLine
                            .getMetricTime(timeStamp).toDouble()
                                                               * sequence.getResolution()
                                                               * 4);

                    // Write the MidiEvent into first track of the sequence
                    if (sequence.getTracks() == null || sequence.getTracks().length == 0)
                        sequence.createTrack();
                    sequence.getTracks()[0].add(mEvent);
                }

                /**
                 * Set the key signature
                 */
                if (metricalTimeLine.get(i) instanceof KeyMarker) {
                    KeyMarker keyMarker = (KeyMarker) metricalTimeLine.get(i);

                    long timeStamp = keyMarker.getTime();

                    int rootAcc = keyMarker.getRootAlteration();
                    int mode = keyMarker.getMode().getCode();

                    // Write the key signature into a byte Array
                    data = new byte[] {(byte) rootAcc, (byte) mode};

                    // Write the Array into the MetaMessage. Typ=89 (0x59) means
                    // KeySignature
                    MetaMessage mMessage = new MetaMessage();
                    try {
                        if (data != null)
                            mMessage.setMessage(89, data, 2);
                    } catch (InvalidMidiDataException iMDE) {
                        System.out.println(iMDE);
                        System.out
                                .println(">>MidiWriter: InvalidMidiDataException in write()");
                    }
                    // Create a MidiEvent
                    MidiEvent mEvent = new MidiEvent(mMessage, (long) metricalTimeLine
                            .getMetricTime(timeStamp).toDouble()
                                                               * sequence.getResolution()
                                                               * 4);

                    // Write the MidiEvent into first track of the sequence
                    if (sequence.getTracks() == null)
                        sequence.createTrack();
                    sequence.getTracks()[0].add(mEvent);
                }

                /**
                 * Set the SMPTE Offset. This meta event is used to specify the SMPTE
                 * starting point offset from the beginning of the track. It is defined in
                 * terms of hours, minutes, seconds, frames and sub-frames.
                 */
                // Write the Array into the MetaMessage. Typ=84 (0x54)
            } // end for
        } // end if piece != null
        
        if(midiFile != null){
	        /**
	         * Write the sequence into the MidiFile
	         */
	        try {
	            System.out.println(".. writing MIDI file: " + midiFile.getName());
	            return MidiSystem.write(sequence, 1, midiFile);
	        } catch (IllegalArgumentException iAE) {
	            iAE.printStackTrace();
	            System.out.print(">>MidiWriter: IllegalArgumentException in write()");
	            return 0;
	        } catch (IOException iOE) {
	            iOE.printStackTrace();
	            System.out.print(">>MidiWriter: IOException in write()");
	            return 0;
	        }
        }else if(outputStream!=null){
        	try {
//	            System.out.println(".. writing MIDI file to outputstream");
	            return MidiSystem.write(sequence, 1, outputStream);
	        } catch (IllegalArgumentException iAE) {
	            iAE.printStackTrace();
	            System.out.print(">>MidiWriter: IllegalArgumentException in write()");
	            return 0;
	        } catch (IOException iOE) {
	            iOE.printStackTrace();
	            System.out.print(">>MidiWriter: IOException in write()");
	            return 0;
	        }
        }
        return 0;
    }

    /**
     * Write containers of a piece object into a MIDI file for testing.
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {

        // Create the MidiReader object
        MidiReader midiReader = new MidiReader();

        // The piece object to read into
        Piece piece = null;

        try {
            // to read the MIDI file into the piece object
            piece = midiReader.getPiece(new URL(args[0]));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiWriter: mal formed URL (" + args[0] + ")");
            return;
		} catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
            System.out.println("MidiWriter: no command line argument.");
            return;
        }

        // The container object to be written
        Container currentContainer = null;

        // Create the MidiWriter object
        MidiWriter midiWriter;
        try {
            // Create the MidiWriter object
            midiWriter = new MidiWriter(args[1]);
            // Try the command line argument
        } catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
            midiWriter = new MidiWriter("de/uos/fmt/musitech/midi/export.mid");
        }

        // Get the container pool
        // TODO ist nicht aus der ArrayList ein Container geworden? 
        java.util.ArrayList containerPool = (java.util.ArrayList) piece.getContainerPool();

        for (int i = 0; i < containerPool.size(); i++) {
            if (containerPool.get(i) instanceof Container) {
                currentContainer = (Container) containerPool.get(i);
                if (currentContainer.size() != 0) {
                    try {
                        // Set a container object
                        midiWriter.setContainer(currentContainer);
                    } catch (Throwable exception) {
                        System.err.println("Exception occurred in main() of MidiReader");
                        exception.printStackTrace(System.out);
                    }
                }
            }
            // Start the export
            midiWriter.write();
        }

        // ToDo
        System.exit(0);

        /**
         * Read a Piece object from a MIDI file and write it back into another MIDI file
         */
        /*
         * // Create the MidiReader object MidiReader aMidiReader = null; try { // Try the
         * command line argument aMidiReader = new MidiReader(new File(args[0])); } catch
         * (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) { aMidiReader =
         * new MidiReader(new File("import.mid")); } // Piece piece = new Piece(); //
         * piece.setSampleData(); // Create a Piece object from the Midi file if
         * (aMidiReader != null) { Piece piece = aMidiReader.processMidiFile(); // Create
         * the MidiWriter object MidiWriter midiWriter = new MidiWriter(piece); try { //
         * Try the command line argument midiWriter.setFileName(args[1]); } catch
         * (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
         * midiWriter.setFileName("de/uos/fmt/musitech/midi/export.mid"); } // Start the
         * export midiWriter.write(); } // ToDo System.exit(0);
         */
    }
}