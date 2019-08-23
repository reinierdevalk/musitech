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
 * Created on 30.07.2004
 */
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.performance.gui.PianoRollPanel;

/**
 * @author Jan
 *
 */
public class TestPianoRollContainer {

	PlayTimer playTimer;
	Container container;
	PianoRollPanel pianoRoll;
	TransportButtons transBut;
	/**
	 * 
	 */
	public TestPianoRollContainer() {
		init();
	}

	/**
	 * 
	 */
	private void init() {
		makeContainer();
		playTimer = PlayTimer.getInstance();
		pianoRoll = new PianoRollPanel(container);
		transBut = new TransportButtons();
		showWindow();
	}
	
	public void showWindow(){
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(pianoRoll, BorderLayout.CENTER);
		frame.getContentPane().add(transBut, BorderLayout.SOUTH);
		frame.setLocation(200, 150);
		frame.pack();
		frame.setSize(500, 300);
		frame.show();
	}

	public Container makeContainer() {
		container = new BasicContainer();
		container.setName("Test Arrange, chromatic Notes");
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote = new PerformanceNote(100000L * i, 500000);
			perNote.setPitch((i % 50) + 40);
			container.add(perNote);
		}
		Container cont1 = new BasicContainer();
		cont1.setName("2nd chromatic");
		container.add(cont1);
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote = new PerformanceNote(100000L * i, 200000);
			perNote.setPitch(90 - (i % 50));
			cont1.add(perNote);
		}
		Container cont2 = new BasicContainer();
		cont2.setName("3rd hole");
		container.add(cont1);
		for (int i = 0; i < 100; i++) {
			PerformanceNote perNote = new PerformanceNote(200000L * i, 200000);
			perNote.setPitch((2 * i % 50) + 40);
			cont2.add(perNote);
		}
		cont1.add(cont2);
		return container;
	}
	
	public static void main(String[] args) {
		TestPianoRollContainer test = new TestPianoRollContainer();
	}

}
