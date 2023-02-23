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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class implements JUnit test for the MidiReader class.
 * 
 * @author Alexander Luedeke
 * @version $Revision: 7954 $, $Date: 2011-06-11 17:36:52 +0200 (Sat, 11 Jun 2011) $
 * @see de.uos.fmt.musitech.performance.midi.MidiReader
 * @see de.uos.fmt.musitech.performance.midi.MidiDumpReceiver
 */
public class MidiReaderTest extends TestCase {

    // MidiReader object to test
    MidiReader midiReader = null;
    // Piece object to write into
    Piece piece = null;

    /**
     * This method reads a MIDI file using a MidiReader. The test works on two levels:
     * <BR>
     * 1. There are assert statements in the class MidiReader and MidiDumpReceiver. A import must
     * not throw any assertment exception. <BR>
     * 2. The created piece object is compared with some known infos about the MIDI file (like note
     * count, timesignature or tempo). <BR>
     * <BR>
     * 
     * Description of the test file placed in de/uos/fmt/musitech/midi: <BR>
     * import_1.mid <BR>
     * Very simple MIDI file containing only one tempo (120 bpm) and time signature (4/4). The MIDI
     * file typ is 1.
     */
    public void testImport_1() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_1.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }

        /**
         * Begin of the testing
         */
        // Are all notes imported? The MIDI file contains 6 notes
        Assert.assertTrue(piece.getNotePool().size() == 6);

        // Are all tacks imported? The MIDI file contains 3 track
        Assert.assertTrue(midiReader.getNumberOfTracks() == 3);

        // Is the division type of MIDI file correct detected?
        Assert.assertTrue(midiReader.getSequence().getDivisionType() == Sequence.PPQ);

        // Is the resolution of MIDI file correct detected?
        Assert.assertTrue(midiReader.getSequence().getResolution() == 192);

        // Is the MIDI file type of the MIDI file correct detected?
        Assert.assertTrue(midiReader.getMidiType() == 1);

        // There must be at least one container called '.. Trackname für Spur 2'
        Iterator<NotationStaff> staffIter = piece.getScore().iterator();
        boolean found = false;
        while (staffIter.hasNext()) {
            found = staffIter.next().getName().matches("(?i).*Trackname für Spur 2.*");
            if (found)
                break;
        }
        Assert.assertTrue(found);

        // There must be a non empty MetaInfoPool, because a copyright notice has been added.
        Assert.assertTrue(piece.getMetaMap() != null);
        Assert.assertTrue(piece.getMetaMap().size() > 0);

        // There must be a copyright notice in the MetaInfoPool
        Iterator<MetaDataItem> iter = piece.getMetaData(piece).values().iterator();
        found = false;
        while (iter.hasNext()) {
            found = iter.next().getKey().matches("(?i).*copyright.*");
            if (found)
                break;
        }
        Assert.assertTrue(found);

        // There must be a note at metrical time 1 with physical time 2seconds in the NotePool
        Iterator iter2 = piece.getScore().getContentsRecursiveList(null).iterator();
        found = false;
        while (iter2.hasNext()) {
        	Object obj = iter2.next();
        	if (obj instanceof Note) {
	            Note note = (Note) obj;
	            if (note.midiNote().getTime() == 2000000) {
	                found = note.getScoreNote().getMetricTime().isEqual(1, 1);
	                if (found)
	                    break;
	            }
            }
        }
        Assert.assertTrue(found);

        // There must be a non empty MetricalTimeLine, because TimedMetricals have been added.
        Assert.assertTrue(piece.getMetricalTimeLine() != null);
        Assert.assertTrue(piece.getMetricalTimeLine().size() > 0);
    }

    /**
     * Description of the test file placed in de/uos/fmt/musitech/midi: <BR>
     * import_2.mid <BR>
     * The same file like 'import_1.mid', but the MIDI file typ is 0.
     */
    public void testImport_2() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_2.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }

        /**
         * Begin of the testing
         */
        // Are all notes imported? The MIDI file contains 6 notes
        Assert.assertTrue(piece.getNotePool().size() == 6);

        // Are all tacks imported? The MIDI file contains 1 track
        Assert.assertTrue(midiReader.getNumberOfTracks() == 1);

        // Is the MIDI file type of the MIDI file correct detected?
        Assert.assertTrue(midiReader.getMidiType() == 0);

        // There must be a non empty MetaInfoPool, because a copyright notice has been added.
        Assert.assertTrue(piece.getMetaMap() != null);
        Assert.assertTrue(piece.getMetaMap().size() > 0);

        // There must be a copyright notice in the MetaInfoPool
        Iterator iter = piece.getMetaData(piece).values().iterator();
        boolean found = false;
        while (iter.hasNext()) {
            Object container = iter.next();
            if (container instanceof MetaDataItem) {
                found = ((MetaDataItem) container).getKey().matches("(?i).*copyright.*");
                if (found)
                    break;
            }
        }
        Assert.assertTrue(found);

        // There must be a note at metrical time 1 with physical time 2000 in the NotePool
        iter = piece.getNotePool().iterator();
        found = false;
        while (iter.hasNext()) {
            Note note = (Note) iter.next();
            if (note.midiNote().getTime() == 2000000) {
                found = note.getScoreNote().getMetricTime().isEqual(1, 1);
                if (found)
                    break;
            }
        }
        Assert.assertTrue(found);

        // There must be a non empty MetricalTimeLine, because TimedMetricals have been added.
        Assert.assertTrue(piece.getMetricalTimeLine() != null);
        Assert.assertTrue(piece.getMetricalTimeLine().size() > 0);
    }

    /**
     * Description of the test file placed in de/uos/fmt/musitech/midi: <BR>
     * import_3.mid <BR>
     * This MIDI file contains four tempo changes 120 bpm, 80 bpm, 60 bpm, 40 bpm (at the metric
     * times 0 (=0 ms), 1 (=2000 ms), 2 (=5000 ms), 3 (=9000 ms)) and four notes at the same 
     * times. The tempo changes causes different note length (1000, 1500, 2000, 3000 ms). This 
     * method tests these physical times. The time signature is 4/4.
     */
    public void testImport_3() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_3.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }

        /**
         * Begin of the testing
         */
        // Are all notes imported? The MIDI file contains 4 notes
        Assert.assertTrue(piece.getNotePool().size() == 4);

        // Check the begin and length of the four notes
        Iterator iter = piece.getNotePool().iterator();
        if (iter.hasNext()) {
            Note note = (Note) iter.next();
            Assert.assertTrue(note.midiNote().getTime() == 0);
            Assert.assertTrue(note.midiNote().getDuration() == 1000000);
        }
        if (iter.hasNext()) {
            Note note = (Note) iter.next();
            Assert.assertTrue(note.midiNote().getTime() == 2000000);
            Assert.assertTrue(note.midiNote().getDuration() == 1500000);
        }
        if (iter.hasNext()) {
            Note note = (Note) iter.next();
            Assert.assertTrue(note.midiNote().getTime() == 5000000);
            Assert.assertTrue(note.midiNote().getDuration() == 2000000);
        }
        if (iter.hasNext()) {
            Note note = (Note) iter.next();
            Assert.assertTrue(note.midiNote().getTime() == 9000000);
            Assert.assertTrue(note.midiNote().getDuration() == 3000000);
        }

        // There must be a non empty MetricalTimeLine, because TimedMetricals have been added.
        Assert.assertTrue(piece.getMetricalTimeLine() != null);
        Assert.assertTrue(piece.getMetricalTimeLine().size() > 0);

        // Check the metrical and physical time of the four notes
        Assert.assertTrue(piece.getMetricalTimeLine().getMetricTime(0).equals(new Rational(0)));
        Assert.assertTrue(piece.getMetricalTimeLine().getMetricTime(2000000).equals(new Rational(1)));
        Assert.assertTrue(piece.getMetricalTimeLine().getMetricTime(5000000).equals(new Rational(2)));
        Assert.assertTrue(piece.getMetricalTimeLine().getMetricTime(9000000).equals(new Rational(3)));

        // Check the TimedMetricals, i.g. the tempo
        Assert.assertTrue(piece.getMetricalTimeLine().getTempo(0) == 120.0);
        Assert.assertTrue(piece.getMetricalTimeLine().getTempo(2000000) == 80.0);
        Assert.assertTrue(piece.getMetricalTimeLine().getTempo(5000000) == 60.0);
        Assert.assertTrue(piece.getMetricalTimeLine().getTempo(9000000) == 40.0);

        // Test the method getTempo
        Assert.assertTrue(piece.getMetricalTimeLine().tempoTable().length == 4);
        Assert.assertTrue(piece.getMetricalTimeLine().tempoTable()[2][0] == 60.0);
        Assert.assertTrue(piece.getMetricalTimeLine().tempoTable()[2][1] == 5000000);
    }

    /**
     * Description of the test file placed in de/uos/fmt/musitech/midi: <BR>
     * import_4.mid <BR>
     * This MIDI file contains three time signature changes 4/4, 3/4, 4/4 (at the metric times 0 (=0
     * ms), 1 (=2000 ms), 7/4 (=3500 ms) and three notes at the same times. The time signature
     * changes causes no different note length, but different bar length. This method tests these
     * physical times. The tempo is 120 bpm.
     */
    public void testImport_4() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_4.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }

        /**
         * Begin of the testing
         */
        // Are all notes imported? The MIDI file contains 3 notes
        Assert.assertTrue(piece.getNotePool().size() == 3);

        // There must be a non empty MetricalTimeLine, because TimedMetricals have been added.
        Assert.assertTrue(piece.getMetricalTimeLine() != null);
        Assert.assertTrue(piece.getMetricalTimeLine().size() > 0);

        // Check the metrical and physical time of the three time signatures
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignatureMarker(new Rational(0)).getMetricTime()
                .equals(new Rational(0)));
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignatureMarker(new Rational(1)).getMetricTime()
                .equals(new Rational(1)));
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignatureMarker(new Rational(7, 4)).getMetricTime()
                .equals(new Rational(7, 4)));

        // Check there physical times
        Assert.assertTrue(piece.getMetricalTimeLine().getTime(new Rational(0)) == 0);
        Assert.assertTrue(piece.getMetricalTimeLine().getTime(new Rational(1)) == 2000000);
        Assert.assertTrue(piece.getMetricalTimeLine().getTime(new Rational(7, 4)) == 3500000);

        // Test the method getTimeSignature
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignature()[1][0] == 3);
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignature()[1][1] == 4);
        Assert.assertTrue(piece.getMetricalTimeLine().getTimeSignature()[1][2] == 2000000);
    }

    /**
     * import_5.mid <BR>
     * The lycrics of this karaoke song must contain the phrase 'what'.
     */
    public void testImport_5() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_5.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }

        /**
         * Begin of the testing. The MetaIntoPool must contain MetaDataItem objects for the lyrics.
         * At least one object must contain the phrase 'what'.
         */
        boolean phraseFound = false;
        Collection list = piece.getScore().getContentsRecursiveList(null);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object obj =  iter.next();
            if (obj instanceof NotationVoice) {
            	NotationVoice nv = (NotationVoice) obj;
            	Iterator iter2 = nv.getContentsRecursiveList(null).iterator();
            	while(iter2.hasNext()) {
            		Object obj2 = iter2.next();
            		if(obj2 instanceof LyricsSyllable) {
            			LyricsSyllable ls = (LyricsSyllable) obj2;
		                phraseFound = ls.getText().matches("(?i).*what.*");
		                if (phraseFound)
		                    break;
            		}
            	}
            }
        }
        Assert.assertTrue(phraseFound);
    }

    /**
     * import_6.mid <BR>
     * A midi file without tempo should take 120 bpm as default value.
     */
    public void testImport_6() {

        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_6.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }
        // The default tempo must be 120 bpm
        Assert.assertTrue(piece.getMetricalTimeLine().getTempo(1) == 120);
    }

    /**
     * Finally a testing with some typical MIDI files. The MidiReader is only once created. The
     * method get must initialize the MidiReader. <BR>
     * import_a.mid <BR>
     * This file contains a symphony with eleven tracks. import_b.mid <BR>
     * This file contains a piano piece (one track for tempo and time signature, two tracks for the
     * hands, one for the pedals and five used for comments). Note on events with velocity 0 are
     * used as note off events. import_c.mid <BR>
     * 'Kleine Nachtmusik' import_d.mid <BR>
     * The track 0 in this file is empty. import_e.mid <BR>
     * Pop music. 'Dancing Queen' by Abba. import_f.mid <BR>
     * A song containing 'channel prefix' meta events.
     */
    public void testImport() {
        // MidiReader object to test
        midiReader = new MidiReader();

        // Name of the file to process
        String[] url = {"import_a.mid", "import_b.mid", "import_c.mid", "import_d.mid", "import_e.mid", "import_f.mid"};

        for (int i = 0; i < url.length; i++) {
            try {
                System.out.println("\nmidiReader.getPiece(" + url[i] + ")");
                piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url[i]).getFile()));
            } catch (MalformedURLException mFUException) {
                System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url[i] + ")");
                return;
            }
        } // end-for
    }

    /**
     * import_e2.mid<BR>
     * The same file like 'import_e.mid', but 'import_e.mid' has been opened and resaved 
     * with a sequencer program, because 'import_e2.mid' is a corrupt MIDI file.
     * The MidiReader throw the same execption like StandardMidiFileReader.
     * @see com.sun.media.sound.StandardMidiFileReader
     */
    public void testException() {
        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_e2.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testException():  (" + url + ")");
        }

        /**
         * Let the StandardMidiFileReader throws the same exception.
         */
        com.sun.media.sound.StandardMidiFileReader sMidiReader = new com.sun.media.sound.StandardMidiFileReader();

        try {
            sMidiReader.getSequence(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (InvalidMidiDataException invalidMidiDataException) {
            System.out.println("MidiReaderTest.testException(): InvalidMidiDataException (" + url + ")");
//            invalidMidiDataException.printStackTrace();
        } catch (MalformedURLException malformedURLException) {
            System.out.println("MidiReaderTest.testException(): MalformedURLException (" + url + ")");
//            malformedURLException.printStackTrace();
        } catch (IOException iOException) {
            System.out.println("MidiReaderTest.testException(): IOException (" + url + ")");
//            iOException.printStackTrace();
        }

        System.out.println("\nDon't panic! These exceptions are expected!");
    }

    /**
     * import_7.mid <BR>
     * The same file like 'import_2.mid'. There is only one MIDI track, but the file type 1. So the
     * notes of one track are assigned to different MIDI channel in order to realize more than one
     * track. Note that the MUSITECH MidiReader channel numbers starts with zero.
     */
    public void testChannels() {
        // MidiReader object to test
        midiReader = new MidiReader();
        // Name of the file to process
        String url = "import_7.mid";

        try {
            System.out.println("\nmidiReader.getPiece(" + url + ")");
            piece = midiReader.getPiece(new URL("file:" + getClass().getResource(url).getFile()));
        } catch (MalformedURLException mFUException) {
            System.out.println("MidiReaderTest.testInput(): MIDI file not found (" + url + ")");
            return;
        }
        // The note pool size must be six
        Assert.assertTrue(piece.getNotePool().size() == 6);

        // The must be a note a metrical time 1/4 with MIDI channel 1 and
        // a second note at metrical time 1 with MIDI channel 2
        Iterator iter = piece.getNotePool().iterator();
        while (iter.hasNext()) {
            Note note = (Note) iter.next();
            if (note.getMetricTime().equals(new Rational(1, 4))) {
                Assert.assertTrue(note.midiNote().getChannel() == 0);
            }
            if (note.getMetricTime().equals(new Rational(1, 1))) {
                Assert.assertTrue(note.midiNote().getChannel() == 1);
            }
        }

        System.out.println("\nJUnit test terminated");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
	protected void setUp() throws Exception {
        getClass().getClassLoader().setDefaultAssertionStatus(true);
    }

}