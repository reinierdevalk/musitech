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
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.audio.floatStream.FISChannelMixer;
import de.uos.fmt.musitech.audio.floatStream.FISMerger;
import de.uos.fmt.musitech.audio.floatStream.FISMultiplexer;
import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.gui.DBSlider;
import de.uos.fmt.musitech.audio.gui.PanSlider;
import de.uos.fmt.musitech.audio.processor.DelayInputStream;
import de.uos.fmt.musitech.audio.processor.PanningController;
import de.uos.fmt.musitech.audio.processor.VolumeController;

/**
* AudioTest.java
* @author Tillman Weyde
*/
public class AudioTest_Jan2 {

	public static void main(String[] args) throws IOException {
		// JFileChooser fileChooser = new JFileChooser();
		// int retVal = fileChooser.showOpenDialog(null);
		// if(retVal != JFileChooser.APPROVE_OPTION){
		// return;
		// }

		// File file = fileChooser.getSelectedFile();

		File file = new File("E:/Jach the ripper/Streets of London 2/Chor/Grup1+Ten-05.wav"
);
//		File file2 = new File("E:/Jach the ripper/Streets of London 2/Chor/Grup1+Ten-05.wav");

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
		int maxDelay = 20000;

		final FISMultiplexer fisMultiPlex =
			new FISMultiplexer(fis, 10000);

		final DelayInputStream fisDelay =
			new DelayInputStream(fisMultiPlex.getMultiplexerMember(), maxDelay, startDelay);

		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.getContentPane().setLayout(new GridLayout(2, 2));
		final JSlider sliderDel = new JSlider(0, maxDelay, startDelay);
		

		sliderDel.addChangeListener(new ChangeListener() {
			/**
			* @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			*/
			@Override
			public void stateChanged(ChangeEvent e) {
				fisDelay.setDelay(sliderDel.getValue());
			}

		});

		final VolumeController fisVCDry = new VolumeController(fisMultiPlex.getMultiplexerMember());

		final PanningController fisPan = new PanningController(fisVCDry);
		fisPan.init();

		final VolumeController fisVCWet = new VolumeController(fisDelay);

		final DBSlider sliderDry = new DBSlider(fisVCDry);
		final DBSlider sliderWet = new DBSlider(fisVCWet);
		final PanSlider sliderPan = new PanSlider(fisPan);

		final FISMerger fisMerger = new FISMerger();

		fisMerger.add(fisVCDry);
		fisMerger.add(fisVCWet);

		final FISChannelMixer fisMixer = new FISChannelMixer(fisMerger);

		float[][] gainTable =
			new float[][] {
				new float[] { 0.5f, 0f, 0.5f, 0f },
				new float[] { 0f, 0.5f, 0f, 0.5f }
		};

		fisMixer.setGainTable(gainTable);

		jf.getContentPane().add(sliderDry);
		jf.getContentPane().add(sliderWet);
		jf.getContentPane().add(sliderPan);
		jf.getContentPane().add(sliderDel);
		jf.setSize(300, 500);
		jf.setLocation(50, 50);
		jf.setTitle("Audio Test Delay");
		jf.setVisible(true);

		FISPlayer player = new FISPlayer(fisMixer);

		player.play();

		
	} // end main 

	// AudioUtil.
}
