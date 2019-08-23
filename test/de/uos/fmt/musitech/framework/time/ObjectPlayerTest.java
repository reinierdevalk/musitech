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
 * Created on 05.07.2004
 */
package de.uos.fmt.musitech.framework.time;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.performance.midi.MidiTee;
import de.uos.fmt.musitech.performance.midi.gui.Piano;
import de.uos.fmt.musitech.structure.form.gui.ArrangementPanel;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Test the object player. 
 * 
 * @author Jan
 */
public class ObjectPlayerTest extends TestCase {

	public void testStop1() {
		container = new BasicContainer();
		objectPlayer = ObjectPlayer.getInstance();
		objectPlayer.setContainer(container);
		objectPlayer.getPlayTimer().stop();
	}

	public void testStop2() {
		container = new BasicContainer();
		objectPlayer = ObjectPlayer.getInstance();
		objectPlayer.setContainer(container);
		objectPlayer.getPlayTimer().start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		objectPlayer.stop();
	}

	private ObjectPlayer objectPlayer;
	private TransportButtons transBut;
	private Container container;
	private ArrangementPanel arrPanel;

	public void init() {
		makeContainer();
//		convertMidi();
		objectPlayer = ObjectPlayer.getInstance();
		objectPlayer.setContainer(container);
		Piece piece = new Piece();
		piece.setContainerPool(container);
		piece.setMetricalTimeLine(creatMetricalTimeLine());
		objectPlayer.getPlayTimer().setContext(new Context(piece));
		//		makeDrums();
		//		convertMidi();

		//		objectPlayer.setContainer(container);
		transBut = new TransportButtons(objectPlayer.getPlayTimer());
		arrPanel = new ArrangementPanel(container);
		arrPanel.setPlayTimer(objectPlayer.getPlayTimer());

	}

	public MetricalTimeLine creatMetricalTimeLine() {
		MetricalTimeLine timeLine = new MetricalTimeLine();
		timeLine.setTempo(new Rational(0), 120, 4);
		//		timeLine.setTempo(new Rational(2), 120, 2);
		//		timeLine.setTempo(new Rational(6), 80, 2);
		timeLine.add(new TimeSignatureMarker(4, 4, new Rational(0, 1)));
		timeLine.add(new TimeSignatureMarker(8, 4, new Rational(2, 1)));
		timeLine.add(new TimeSignatureMarker(6, 8, new Rational(4, 1)));
		//		
		return timeLine;
	}

	/**
	 * 
	 */
	private void makeDrums() {
		container = new BasicContainer();
		final int HiHat = 53;
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote;
			if (i % 2 != 0) {
				perNote = new PerformanceNote(200000L * i, 200000);
				perNote.setPitch(HiHat);
				perNote.setVelocity(60);
			} else {
				perNote = new PerformanceNote(200000L * i, 200000);
				perNote.setPitch(HiHat);
				perNote.setVelocity(80);
			}
			//			perNote.setPitch((i % 50) + 40);
			//			Note note = new Note();
			MidiNote note = new MidiNote(perNote);
			note.setChannel(10);
			container.add(note);

		}
	}

	public void showWindow() {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(arrPanel, BorderLayout.CENTER);
		frame.getContentPane().add(transBut, BorderLayout.SOUTH);
		frame.setLocation(200, 150);
		frame.pack();
		frame.setSize(500, 300);
		frame.show();
	}

	public void midiOutSelector() {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(
			objectPlayer.getControlPanel(),
			BorderLayout.CENTER);
		frame.setLocation(700, 150);
		frame.pack();
		frame.setSize(200, 100);
		frame.show();
	}

	public void mousePiano() {
		Piano piano = new Piano(objectPlayer.getReceiver());
		MidiTee midiTee =
			new MidiTee(
				objectPlayer.getReceiver(),
				objectPlayer.getMidiRecorder());
		piano.setReceiver(midiTee);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(piano, BorderLayout.CENTER);
		frame.setLocation(200, 450);
		frame.pack();
		frame.setSize(500, 200);
		frame.show();
		JFrame frame2 = new JFrame();
		frame2.getContentPane().setLayout(new BorderLayout());
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.getContentPane().add(
			piano.getControlPanel(),
			BorderLayout.CENTER);
		frame2.setLocation(700, 450);
		frame2.pack();
		frame2.setSize(200, 200);
		frame2.show();
	}

	public Container convertMidi() {
		container = new BasicContainer();
		MidiReader reader = new MidiReader();
		Piece piece = new Piece();

		try {
			piece =
				reader.getPiece(new File("C:/WINDOWS/media/town.mid").toURL());
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}

		container.addAll(piece.getContainerPool());

		return container;
	}

	public Container makeContainer() {
		container = new BasicContainer();
		//		try {
		//			container.add(new AudioFileObject(new File("E:/Year.mp3").toURL()));
		//		} catch (MalformedURLException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		container.setName("Test Arrange, chromatic Notes");
		for (int i = 0; i < 200; i++) {
			PerformanceNote perNote = new MidiNote(500000L * i, 50000);
			perNote.setPitch((i % 4) + 60);
			perNote.setVelocity(80);
			container.add(perNote);
		}
		Container cont1 = new BasicContainer();
		cont1.setName("2nd chromatic");
		//		container.add(cont1);
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote = new PerformanceNote(100000L * i, 200000);
			perNote.setPitch(90 - (i % 50));
			perNote.setVelocity(20);
			cont1.add(perNote);
		}
		Container cont2 = new BasicContainer();
		cont2.setName("3rd hole");
		//		container.add(cont1);
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote = new PerformanceNote(200000L* i, 200000);
			perNote.setPitch((2 * i % 50) + 40);
			cont2.add(perNote);
		}
		//		cont1.add(cont2);
		return container;
	}

	public void showMetroPanel() {
		MetronomePanel metroPanel = objectPlayer.getPlayTimer().getMetronomPanel();
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(metroPanel, BorderLayout.CENTER);
		frame.setLocation(150, 450);
		frame.pack();
		frame.setSize(100, 50);
		frame.show();
	}

	public static void main(String[] args) {
		ObjectPlayerTest optest = new ObjectPlayerTest();
		optest.init();
		optest.showWindow();
		optest.showMetroPanel();
				optest.mousePiano();
		optest.midiOutSelector();
	}
}
