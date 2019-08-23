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
package de.uos.fmt.musitech.audio.gui;


import java.util.Dictionary;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.processor.PanningController;

/**
 * PanSlider.java
 * @author Jan-Hendrik Kramer
 */

public class PanSlider extends JSlider{
	
	private PanningController fis;
	
	public PanSlider(PanningController fis) {
	
		
		
		super(JSlider.HORIZONTAL, 0, 127, 64);
	
		this.fis = fis;
		
		setPaintLabels(true);
	
		Dictionary labeltable = createStandardLabels(127);
	
		labeltable.put(new Integer(0),new JLabel ("left"));
		labeltable.put(new Integer(64),new JLabel ("0"));
		labeltable.put(new Integer(127),new JLabel ("right"));
		
	
		
	
		setLabelTable(labeltable);
		
		setMajorTickSpacing(64);
		setMinorTickSpacing(16);
		setPaintTicks(true);
	
		addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent e){
					float gain;
					gain = (float)((JSlider)(e.getSource())).getValue(); 
					
					if (gain<64.0f) {
						gain = gain/64;
						setGain(gain, 1);
						setGain(1.0f, 0);
						
					}
					if (gain==64.0f) {
						setGain(1.0f, 0);
						setGain(1.0f, 1);
					}
					if (gain>64.0f) {
						gain = (127-gain)/64;
						setGain(gain, 0);
						setGain(1.0f, 1); 
					}
					
					
					System.out.print("    Slider: "+(float)((JSlider)(e.getSource())).getValue());
					
					System.out.println("   gain: "+gain);
								
				}
			}
	
	
		);
	
	}
	
	public void setGain(float gain, int channel) {
		fis.setGain(gain, channel);
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test PanSlider");
		frame.add(new PanSlider(new PanningController()));
		frame.pack();
		frame.show();
	}
	
}
