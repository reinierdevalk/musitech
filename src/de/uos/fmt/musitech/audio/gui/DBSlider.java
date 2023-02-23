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
/**
 * File DBSlider.java
 * Created 03.2003
 */
package de.uos.fmt.musitech.audio.gui;

import java.util.Dictionary;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.utility.math.MyMath;

/**
 * DBSlider
 * @author Jan-Hendrik Kramer
 */
public class DBSlider extends JSlider{

	static public boolean DEBUG = false; 
	
	private GainChanger gChange;
	
	public DBSlider(GainChanger gChange){
		this(gChange, "");
	}
	
	
	public DBSlider(GainChanger gChange, String ToolText) {
	
		
		super(SwingConstants.VERTICAL, 0, 1200, 1000);
	
		this.gChange = gChange;
		
		setPaintLabels(true);
		setToolTipText(ToolText);
		Dictionary labelTable = createStandardLabels(1200);
	
		labelTable.put(new Integer(0),new JLabel ("oo"));
		labelTable.put(new Integer(200),new JLabel ("-40 dB"));
		labelTable.put(new Integer(400),new JLabel ("-20 dB"));
		labelTable.put(new Integer(600),new JLabel ("-10 dB"));
		labelTable.put(new Integer(800),new JLabel (" -5 dB"));
		labelTable.put(new Integer(1000),new JLabel (" 0 dB"));
		labelTable.put(new Integer(1200),new JLabel ("+5 dB"));
	
		setLabelTable(labelTable);
		
		setMajorTickSpacing(200);
		setMinorTickSpacing(100);
		setPaintTicks(true);
	
		addChangeListener(
			new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e){
					double gain; 
					JSlider slider = (JSlider)(e.getSource());

					gain = slider.getValue(); 
					// Convert into major ticks units with correct orientation and zero point. 				
					gain = -(gain-1000)/200;
					
					// convert ticks into dBs
					if (gain >= 1) { 
						//	calculate the values logarithmically in the lower part of the scale
						gain = -5.0 * Math.exp( Math.log(2.0) * (gain-1)); 
					} else {
						// linear function is used in upper part 0 wouldnt be reached => 
						gain = -5.0 * gain; 
					}
					 	
					if(DEBUG) System.out.print(" Slider: "+slider.getValue());
					if(DEBUG) System.out.print(" dB: "+Math.round(gain*100)/100.0);
					// get factor for dB value
					gain = MyMath.dBToLinearAmp(gain); 
					// close line at lower end of scale 
					if (slider.getValue() == 0.0f) 
						gain = 0;
					setGain((float)gain);
					if(DEBUG) System.out.println(" gain: "+gain);
				}
			} // end anonymous class
		); // endAddChangeListener
	}
	
	
	public void setGain(float gain) {
		gChange.setGain(gain);
	}
	
	public void setValue(float value){
//System.out.println("DBSlider.setValue(float value) is setting to: ((int) (linearToDB("+value+")*500)) = "+((int) (linearToDB(value)*500)));
//		super.setValue((int) (linearToDB(value)*500)); //TODO: voll falsch, die Berechnung
		super.setValue((int) (value*1000));
	}
	
}
