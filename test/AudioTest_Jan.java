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
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.audio.floatStream.FISChannelMixer;
import de.uos.fmt.musitech.audio.floatStream.FISMerger;
import de.uos.fmt.musitech.audio.floatStream.FISMultiplexer;
import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.gui.DBSlider;
import de.uos.fmt.musitech.audio.gui.DelaySlider;
import de.uos.fmt.musitech.audio.processor.DelayInputStream;
import de.uos.fmt.musitech.audio.processor.PanningController;
import de.uos.fmt.musitech.audio.processor.VolumeController;

/**
* AudioTest.java
* @author Tillman Weyde
*/
public class AudioTest_Jan {

	public static void main(String[] args) throws IOException {
		// JFileChooser fileChooser = new JFileChooser();
		// int retVal = fileChooser.showOpenDialog(null);
		// if(retVal != JFileChooser.APPROVE_OPTION){
		// return;
		// }

		// File file = fileChooser.getSelectedFile();

		File file = new File("C:\\Program Files\\eclipse_v3\\workspace\\ckm-ml\\src\\ccm_mt\\basics\\toneNoise\\sounds\\synth.wav");
//		File file2 =new File("C:\\data\\wie-schoen-leuchtet.wav");

		FloatInputStream fis;
//		FloatInputStream fis2;
		try {
			fis = AudioUtil.getFloatInputStream(file);
//			fis2 = AudioUtil.getFloatInputStream(file2);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		int startDelay = 10000;
		int maxDelay = 41000;

		final FISMultiplexer fisMultiPlex =
			new FISMultiplexer(fis, 10000);

		final DelayInputStream fisDelay =
			new DelayInputStream(fisMultiPlex.getMultiplexerMember(), maxDelay, startDelay);



		final VolumeController fisVCDry = new VolumeController(fisMultiPlex.getMultiplexerMember());

		final PanningController fisPan = new PanningController(fisVCDry);
		fisPan.init();

		final VolumeController fisVCWet = new VolumeController(fisDelay);

		DelaySlider sliderDel = new DelaySlider(fisDelay, "Delay");
		final DBSlider sliderDry = new DBSlider(fisVCDry, "Dry");
		final DBSlider sliderWet = new DBSlider(fisVCWet, "Wet");


		final FISMerger fisMerger = new FISMerger();

		fisMerger.add(fisVCDry);
		fisMerger.add(fisVCWet);

		final FISChannelMixer fisMixer = new FISChannelMixer(fisMerger);

//		float[][] gainTable =
//			new float[][] {
//				new float[] { 0.5f, 0f, 0.5f, 0f },
//				new float[] { 0f, 0.5f, 0f, 0.5f }
//		};
//
//		fisMixer.setGainTable(gainTable);

		JPanel jp = new JPanel();
		JFrame jf = new JFrame();
				jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				jf.getContentPane().setLayout(new BorderLayout());
		
		
		jp.setLayout(new GridLayout(1,2));
		jp.add(sliderDry);
		jp.add(sliderWet);
		

		
		jf.getContentPane().add(jp, BorderLayout.CENTER);
		jf.getContentPane().add(sliderDel, BorderLayout.SOUTH);

	
		jf.setSize(200, 400);
		jf.setLocation(50, 50);
		jf.setTitle("Audio Test Delay");
		jf.setVisible(true);

		FISPlayer player = new FISPlayer(fisMixer);

		player.play();
//		long timeNow = System.currentTimeMillis();
//		long timePlayed = 0;
		int i = 0;
		
		if (false){
		
			while (true) {
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e1) {
	
					e1.printStackTrace();
				}
	
				switch (i % 4) {
					case 0 :
						{
							player.play();
							System.out.println("player.play");
							i++;
							break;
						}
					case 1 :
						{
	
							player.pause();
							System.out.println("player.pause 1");
							i++;
							break;
						}
					case 2 :
						{
							player.pause();
							System.out.println("player.pause 2");
							i++;
							break;
						}
					case 3 :
						{
							player.stop();
							System.out.println("player.stop");
							i++;
							break;
						}
				}
			}
			

			
				

		}
	} // end main 

	// AudioUtil.
}
