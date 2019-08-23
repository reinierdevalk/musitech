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
 * Created on 04.09.2003
 */
package de.uos.fmt.musitech.audio;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageSequenceContainer;
import de.uos.fmt.musitech.data.media.image.TimedImage;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.media.image.ImageSequencePlayer;

/**
 * @author Nicolai Strauch & Tillman Weyde
 */
public class TestAudioPlayer {
	
//	private static String directoryAdress = "E:/temp/christophe/";
	private static String directoryAdress = "C:/Program Files/eclipse_v3/workspace/ckm-ml/src/ccm_mt/basics/toneNoise/sounds/";

    public static void main(String a[]) {
        AFOPlayer player = new AFOPlayer();
        AudioFileObject afo=null;
        try {
            //afo = new AudioFileObject(new File(directoryAdress+"../AllaTurcaKurz.wav").toURL());
            // afo = new AudioFileObject(new File(directoryAdress+"Grigory Sokolov - 15 - Sostenuto ré bémol majeur.mp3").toURL());
            afo = new AudioFileObject(new File(directoryAdress+"synth.wav").toURL());
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
        player.play();

        Piece work = new Piece();
        work.getContainerPool().add(cont);
        MetaDataItem mdi = new MetaDataItem("Keywords");
        MetaDataValue mv = new MetaDataValue();
        mdi.setMetaValue(mv);
        mv.setMetaType("application/octet-stream");
        mv.setMetaValue(new Object()/* hier das gewünschte Objekt verwenden */);
//        work.getMetaData(work).addMetaDataItem(mdi);
        
		ImageSequencePlayer imageSeq = new ImageSequencePlayer();
		TimedImage timedImage1 = null;
		TimedImage timedImage2 = null;
		TimedImage timedImage3 = null;
		TimedImage timedImage4 = null;
		TimedImage timedImage5 = null;
		TimedImage timedImage6 = null;
		TimedImage timedImage7 = null;
		TimedImage timedImage8 = null;

			
		try {
			timedImage1 = new TimedImage(new File(directoryAdress+"chopin1a.jpg").toURL());
			timedImage2 = new TimedImage(new File(directoryAdress+"chopin1b.jpg").toURL());
			timedImage3 = new TimedImage(new File(directoryAdress+"chopin2a.jpg").toURL());
			timedImage4 = new TimedImage(new File(directoryAdress+"chopin2b.jpg").toURL());
			timedImage5 = new TimedImage(new File(directoryAdress+"chopin3a.jpg").toURL());
			timedImage6 = new TimedImage(new File(directoryAdress+"chopin3b.jpg").toURL());
			timedImage7 = new TimedImage(new File(directoryAdress+"chopin4a.jpg").toURL());
			timedImage8 = new TimedImage(new File(directoryAdress+"chopin4b.jpg").toURL());
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		timedImage1.setTime(0);
		timedImage2.setTime(35000);
		timedImage3.setTime(89000);
		timedImage4.setTime(143000);
		timedImage5.setTime(196000);
		timedImage6.setTime(249000);
		timedImage7.setTime(305000);
		timedImage8.setTime(370000);

		ImageSequenceContainer ISCont = new ImageSequenceContainer();
		ISCont.add(timedImage1);
		ISCont.add(timedImage2);
		ISCont.add(timedImage3);
		
		imageSeq.setTimedISC(ISCont);
		imageSeq.loadImages();
				
		JFrame frame = new JFrame();
		PlayTimer playTimer = PlayTimer.getInstance();
		playTimer.registerForPush(imageSeq);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(imageSeq.getImageComp());


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 150);
		frame.setSize(300,200);
		frame.show();
		playTimer.start();
    }

}
