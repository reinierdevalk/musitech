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
 * File SlideShowPlayer.java
 * Created on 08.04.2004
 */

package de.uos.fmt.musitech.data.media.image;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.uos.fmt.musitech.audio.AFOPlayer;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageSequenceContainer;
import de.uos.fmt.musitech.data.media.image.StaffPosition;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.media.image.ImageSequencePlayer;

/**
 * TODO add class coment
 * @author tweyde
 */
public class SlideShowPlayer extends JPanel{

	public SlideShowPlayer(
		Container audioCont,
		ImageSequenceContainer imgSeqCont) {

		PlayTimer playTimer = PlayTimer.getInstance();

		AFOPlayer afoPlayer = new AFOPlayer();
		if (audioCont != null)
		    afoPlayer.setContainer(audioCont);
		afoPlayer.setPlayTimer(playTimer);
		playTimer.registerPlayer(afoPlayer);

		ImageSequencePlayer imgSeqPlayer = new ImageSequencePlayer();

		imgSeqPlayer.setTimedISC(imgSeqCont);
		imgSeqPlayer.loadImages();

		JFrame frame = new JFrame();
		playTimer.registerForPush(imgSeqPlayer);
		

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(imgSeqPlayer.getImageComp(),BorderLayout.CENTER);
		imgSeqPlayer.getImageComp().zoomFactor = zoom;
		
		TransportButtons tb = new TransportButtons(playTimer);
		frame.getContentPane().add(tb,BorderLayout.SOUTH);
		

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 150);
		frame.setSize(500, 400);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.show();
	}

	static ImageSequenceContainer imgSeqCont = new ImageSequenceContainer();
	static Container audioCont = new BasicContainer();
	static double zoom = 1.0; 

	public static void prepareSlideShow() {
		String path = "E:\\Eigene Dateien\\Eigene Musik\\christophe";
		StaffPosition[] staff1a = new StaffPosition[2];
		staff1a[0] = new StaffPosition(new Rectangle(250, 50, 900, 250), 500000, 20000000);
		staff1a[1] = new StaffPosition(new Rectangle(150, 350, 1000, 250), 20000000, 15000000);
		imgSeqCont.addTimedImage(path, "chopin1a.jpg", 0, staff1a);
		imgSeqCont.addTimedImage(path, "chopin1b.jpg", 35000000);
		imgSeqCont.addTimedImage(path, "chopin2a.jpg", 89000000);
		imgSeqCont.addTimedImage(path, "chopin2b.jpg", 143000000);
		imgSeqCont.addTimedImage(path, "chopin3a.jpg", 196000000);
		imgSeqCont.addTimedImage(path, "chopin3b.jpg", 249000000);
		imgSeqCont.addTimedImage(path, "chopin4a.jpg", 305000000);
		imgSeqCont.addTimedImage(path, "chopin4b.jpg", 370000000);
		try {
			audioCont.add(new AudioFileObject(new File(path,"Grigory Sokolov - 15 - Sostenuto ré bémol majeur.wav").toURL()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zoom = 0.85;
	}

	public static void main(String[] args) {
		prepareSlideShow();
		SlideShowPlayer ssp = new SlideShowPlayer(audioCont, imgSeqCont);
	}



}