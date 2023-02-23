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
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.audio.AFOPlayer;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageSequenceContainer;
import de.uos.fmt.musitech.data.media.image.TimedImage;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.TimePanel;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.media.image.ImageSequencePlayer;

/*
 * Created on 11.02.2004
 */

/**
 * @author Jan
 *
 */
public class Test_Image {

	static PlayTimer playTimer = PlayTimer.getInstance();
	

	
	static ImageSequencePlayer imageSeq = new ImageSequencePlayer();
	
	static TimedImage timedImage1 = null;
	static TimedImage timedImage2 = null;
	static TimedImage timedImage3 = null;
	
	static ImageSequenceContainer ISCont = new ImageSequenceContainer();
	
	public static void initImageSeq(){
		playTimer.registerForPush(imageSeq);
	}
	
	public static void showFrame(){
		JFrame frame = new JFrame();

		AFOPlayer player = new AFOPlayer();
		AudioFileObject afo=null;
		try {
			afo = new AudioFileObject(new File("C:/EigeneDateien/Eigene Musik/christophe/Grigory Sokolov - 15 - Sostenuto ré bémol majeur.mp3").toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Container cont = new BasicContainer();
		cont.add(afo);
		player.setContainer(cont);
		player.setPlayTimer(playTimer);

			
		try {
			timedImage1 = new TimedImage(new File("E:/Klavier.jpg").toURL(), 0);
			timedImage2 = new TimedImage(new File("E:/chor.jpg").toURL(), 6000);
			timedImage3 = new TimedImage(new File("E:/test_12.jpg").toURL(), 12000);
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
				
		ISCont.add(timedImage1);
		ISCont.add(timedImage2);
		ISCont.add(timedImage3);
		
		imageSeq.setTimedISC(ISCont);
		imageSeq.loadImages();
		
		TimePanel timePanel = new TimePanel(playTimer);
//		ImageScroller imScr;
//		try {
//			imScr = new ImageScroller(new File("C:/data/mozart.jpg").toURL());
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return;
//		}
		
//		ZoomImageComponent ic = new ZoomImageComponent();
//		imageSeq.setImageComp(ic);
//		ic.setZoomfactor(0.7);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(imageSeq.getImageComp(),BorderLayout.CENTER);
		frame.getContentPane().add(timePanel, BorderLayout.NORTH);
		
		TransportButtons tb = new TransportButtons(playTimer);
		frame.getContentPane().add(tb,BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocation(200, 150);
		frame.setSize(300,200);
		frame.show();
		
	}
	
	
	public static void main(String[] args) {
		initImageSeq();
		showFrame();
	}
}
