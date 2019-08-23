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
 * Created on Feb 11, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.uos.fmt.musitech.data.score;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.utility.math.Rational;

//import sun.security.x509.OtherName;

/**
 * This class includes a couple of test methods for the @see de.uos.fmt.musitech.data.structure.NotationVoice class.
 * It is written using the JUnit framework.
 * @author collin
 * @version 1.0
 */
public class NotationVoiceTest extends TestCase {
	
	
	// @@ Punkt bei 2. punktierter halber Note wird nicht angezeigt.
	// @@ ?berbindung der 1 halben Note wird nicht angezeigt.
	// @@ n-tuplets werden nicht angezeigt
	// @@ Zeilenumbruch 

	/**
	 * This method tests the method NotationVoice.fillGaps(). This is down by comparing
	 * endTime - startTime to the sum over all durations.
	 */
	public void testFillGaps() {
//		Context context = new Context(new Piece());
		NotationVoice voice = new NotationVoice( new NotationStaff(new NotationSystem()));
		voice.add(new Note(new ScoreNote(new Rational(1), new Rational(1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(3), new Rational(1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(7), new Rational(1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(15), new Rational(1), 'c', (byte) 0, (byte) 0), null));

		voice.fillGaps();

		Rational duration = new Rational();
		for (Iterator iter = voice.iterator(); iter.hasNext();) {
			NotationChord element = (NotationChord) iter.next();
			duration = duration.add(element.getMetricDuration());
		}

		NotationChord beginning = (NotationChord) voice.get(0);
		NotationChord end = (NotationChord) voice.get(voice.size() - 1);
		Rational globalDuration = end.getMetricTime().add(end.getMetricDuration()).sub(beginning.getMetricTime());

		assertEquals(globalDuration.toDouble(), duration.toDouble(), 0.0);
	}
	/**
	 * This method test whether the automatical insertion of Barlines into voices works.
	 * Some cases of note splitting are covered. The binding of notes via tied ScoreNotes
	 * is also checked.
	 * The voice which is split uses a triol... so handling that is checked as well
	 */
	public void testInsertBarlines() {

		Context context = new Context(new Piece());
		NotationSystem nsys = new NotationSystem(context);
		NotationStaff nstaff = new NotationStaff( nsys);
		nsys.add(nstaff);
		NotationVoice voice = new NotationVoice( nstaff);
		nstaff.add(voice);

		voice.add(new Note(new ScoreNote(Rational.ZERO, new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(2, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(3, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));

		voice.add(new Note(new ScoreNote(new Rational(4, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(8, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(12, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));

		Note[] triole = new Note[3];
		triole[0] = new Note(new ScoreNote(new Rational(13, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null);
		triole[1] = new Note(new ScoreNote(new Rational(0, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null);
		triole[2] = new Note(new ScoreNote(new Rational(0, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null);

		voice.addTuplet(triole);

		Note[] sexol = new Note[6];
		sexol[0] = new Note(new ScoreNote(new Rational(14, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);
		sexol[1] = new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);
		sexol[2] = new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);
		sexol[3] = new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);
		sexol[4] = new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);
		sexol[5] = new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null);

		voice.addTuplet(sexol, new Rational(4, 8));

		voice.getContext().getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));

		//system.insertBarlines();

		/*
		assertEquals("The voice should contain 18 objects", 18, voice.size());

		assertTrue(
			"The fith note should be a 1/2",
			((Note) ((NotationChord) voice.get(4)).get(0)).getScoreNote().getDuration().equals(new Rational(1, 2)));
		assertTrue(
			"The sixth note should be a 1/2",
			((Note) ((NotationChord) voice.get(5)).get(0)).getScoreNote().getDuration().equals(new Rational(1, 2)));
		assertTrue(
			"The seventh note should be a 1/4 but is a " + ((Note) ((NotationChord) voice.get(6)).get(0)).getScoreNote().getDuration(),
			((Note) ((NotationChord) voice.get(6)).get(0)).getScoreNote().getDuration().equals(new Rational(1, 4)));
		assertTrue(
			"The eight note should be a 3/4",
			((Note) ((NotationChord) voice.get(7)).get(0)).getScoreNote().getDuration().equals(new Rational(3, 4)));

		ScoreNote tied = ((Note) ((NotationChord) voice.get(4)).get(0)).getScoreNote().getTiedNote();
		ScoreNote note = ((Note) ((NotationChord) voice.get(5)).get(0)).getScoreNote();


		assertSame(tied, note);
		*/

		JFrame jframe = new JFrame();
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		ScorePanel spanel = new ScorePanel();
		jframe.getContentPane().add(spanel);
		ScoreMapper mapper = new ScoreMapper(spanel, nsys);
		jframe.setSize(800, 600);
		jframe.setVisible(true);
	}

	public void graphicalTest() {
		Context context = new Context(new Piece());
		NotationSystem nsys = new NotationSystem(context);
		NotationStaff nstaff = new NotationStaff( nsys);
		nsys.add(nstaff);
		NotationVoice voice = new NotationVoice( nstaff);
		nstaff.add(voice);

		
		NoteList nl = new NoteList(context, "Cg:-1 T3/4\n'4cccccc");
		for (Iterator iter = nl.iterator(); iter.hasNext();) {
			Note element = (Note)iter.next();
			voice.add(element);
		}
		System.out.println(voice.getContext().getPiece().getMetricalTimeLine().get(0));
		JFrame jframe = new JFrame();
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		ScorePanel spanel = new ScorePanel();
		jframe.getContentPane().add(spanel);
		ScoreMapper mapper = new ScoreMapper(spanel, nsys);
		jframe.setSize(800, 600);
		jframe.setVisible(true);
	}

	/**
	 * This method implements a test for NotationVoice.generateBeams(). A "piece" is created. The beamContainer for
	 * it are generated and the output is verified.
	 */
	public void testGenerateBeams() {
		//Context context = new Context(new Piece());
		NotationVoice voice = new NotationVoice( new NotationStaff( new NotationSystem()));
		voice.getContext().getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));

		voice.add(new Note(new ScoreNote(Rational.ZERO, new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(1, 8), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(2, 8), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(3, 8), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(4, 8), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(5, 8), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));

		voice.add(new Note(new ScoreNote(new Rational(12, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(13, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(14, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(15, 16), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(19, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(20, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(21, 16), new Rational(1, 16), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(22, 16), new Rational(1, 8), 'c', (byte) 0, (byte) 0), null));

		//voice.insertBarlines();
		List beams = voice.generateBeams();

		assertEquals("Three beam container should have been generated", 3, beams.size());
		assertEquals("The first BeamContainer should include 6 chords", 6, ((BeamContainer) beams.get(0)).size());
		assertEquals("The second BeamContainer should include 3 chords", 3, ((BeamContainer) beams.get(1)).size());
		assertEquals("The third BeamContainer should include 4 chords", 4, ((BeamContainer) beams.get(2)).size());
	}

	/*
	 * This method implements a test for NotationVoice.generateSlurContainers(). 
	 * It uses the same "piece" as testInsertBarlines().
	 * First the barlines are inserted, than the slurContainers are generated and the result is checked.
	 *
	public void testGenerateSlurContainers() {
		Context context = new Context(new Piece());
		NotationVoice voice = new NotationVoice( new NotationStaff( new NotationSystem(context)));
		voice.add(new Note(new ScoreNote(Rational.ZERO, new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(2, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(3, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));

		voice.add(new Note(new ScoreNote(new Rational(4, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(8, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
		voice.add(new Note(new ScoreNote(new Rational(12, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));

		voice.getContext().getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));

		//voice.insertBarlines();
		//List slurContainers = voice.generateSlurContainers();
		//List slurContainers = voice.generateSlurContainers();

//		assertEquals("Two slurs containers should have been generated", 2, slurContainers.size());
//		assertEquals("The first slur container should contain two chords", 2, ((SlurContainer) slurContainers.get(0)).size());
//		assertEquals("The second slur container should contain two chords", 2, ((SlurContainer) slurContainers.get(1)).size());
	}*/

	public static void main(String[] args) {
		NotationVoiceTest nvt = new NotationVoiceTest();
		nvt.graphicalTest();
		//nvt.testInsertBarlines();
		//nvt.testGenerateBeams();
	}
}
