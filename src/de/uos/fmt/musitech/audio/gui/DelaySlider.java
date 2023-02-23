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
 * Created on 14.05.2003
 *
 */
package de.uos.fmt.musitech.audio.gui;

import java.util.Dictionary;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.processor.DelayInputStream;

/**
 * DBSlider
 * @author Jan-Hendrik Kramer
 */

public class DelaySlider extends JSlider {

	static public boolean DEBUG = true;

	private  DelayInputStream fis;

	public DelaySlider(DelayInputStream fis) {
		this(fis, "");
	}

	public DelaySlider(final DelayInputStream fis,  String ToolText) {

		
		super(SwingConstants.HORIZONTAL, 0, 1200, 600);
		
		this.fis = fis;

		

		setPaintLabels(true);
		setToolTipText(ToolText);
		Dictionary labeltable =
			createStandardLabels(400, 0);

		String Label1 = "" + fis.getBuffSize() / (3*41) + "ms",
			Label2 = "" + fis.getBuffSize() * 2 / (3*41) + "ms",
			Label3 = "" + fis.getBuffSize()/41+ "ms";

		labeltable.put(new Integer(0), new JLabel("0"));
		labeltable.put(new Integer(400), new JLabel(Label1));
		labeltable.put(new Integer(800), new JLabel(Label2));
		labeltable.put(new Integer(1200), new JLabel(Label3));

		setLabelTable(labeltable);

		setMajorTickSpacing(400);
		setMinorTickSpacing(200);
		setPaintTicks(true);
		

		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int delay;
				JSlider slider = (JSlider) (e.getSource());

				delay = slider.getValue();
				// Convert into major ticks units with correct orientation and zero point. 				
				delay = (fis.getBuffSize() / 1200) * delay;

				fis.setDelay(delay);
				if (DEBUG)
					System.out.println(" gain: " + delay);
			}
		} // end anonymous class
		); // endAddChangeListener
	}

	public int samplesPerBeat(int bpm){
		
		return samplesPerBeat(bpm, 44100);
	}
	
	public int samplesPerBeat(int bpm, int sampleRate){
		
		return sampleRate/(bpm/60);
		
	}
	
	
	public void setDelay(int delay) {
		fis.setDelay(delay);
	}

}
