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
 * Created on 15.07.2004
 */
package de.uos.fmt.musitech.audio;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.performance.midi.MidiReader;

/**
 * @author Nicolai Strauch, Tillman Weyde
 */
public class TestObjectPlayer extends JFrame {

	TransportButtons transportButtons;
	
	ObjectPlayer objectPlayer = ObjectPlayer.getInstance();
	
	//private static String directoryAdress = "D:/EigeneDateien/MusiTech/audioSamples/";
	//private static String directoryAdress = "C:/Eigene Dateien/Eigene Musik/christophe/klick/";
	private static String directoryAdress = "D:/Programme/eclipse_v3/workspace/ckm-ml/src/ccm_mt/basics/toneNoise/sounds/";

	public TestObjectPlayer() throws MalformedURLException, IOException{
		transportButtons = new TransportButtons(objectPlayer.getPlayTimer());
		
		Container container = new BasicContainer();
		//Container container = new NoteList(new Context(new Piece()), "a b c e c a g e c a");
		
		//container.add(new AudioFileObject(new File(directoryAdress+"klick.wav").toURL()));
		//container.add(new AudioFileObject(new File(directoryAdress+"klick_frau_oT.mp3").toURL()));
		container.add(new AudioFileObject(new File(directoryAdress+"buzz.wav").toURL()));
//		container.add(new AudioFileObject(new File(directoryAdress+"eigenKlick.mp3").toURL()));
//		container.add(new MidiReader().getPiece(new File(directoryAdress+"klick_typ0.mid").toURL()).getNotePool());
		
		objectPlayer.setContainer(container);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(objectPlayer.getControlPanel(), BorderLayout.EAST);
		getContentPane().add(transportButtons, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	
	public static void main(String[] a) throws MalformedURLException, IOException{
		new TestObjectPlayer();
	}
	
}
