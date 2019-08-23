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
package de.uos.fmt.musitech.audio.floatStream.generators;

import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.floatStream.FISPlayer;

/**
 * This class implements a pulse oscillator. TODO Not bandwith limiting is done yet.
 * 
 * @author Tillman Weyde
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class PulseOscillator extends FloatOscillator {

    private float amplitude;
    private float frequency; // in Hz
    private float step; // step size for oscillator (1/ (freq * sampleRate ) )
    private float stepTarget; // step size set by freq change
    private float ratio = 0.5f; // the ration between the high and the low parts

    private float phase;

    /**
     * @param frequency
     * @param amplitude
     */
    public PulseOscillator(float frequency, float amplitude) {
        setFrequency(frequency);
        setAmplitude(amplitude);
    }

    /**
     * @param argFrequency
     * @param argAmplitude
     * @param argRatio
     */
    public PulseOscillator(float argFrequency, float argAmplitude, float argRatio) {
        this(argFrequency,argAmplitude);
        setRatio(argRatio);
    }

    public float getAmplitude() {
        return amplitude;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    /**
     * Set the frequency of the oscillator in Hertz.
     * 
     * @param argFreq the frequency to set.
     */
    public void setFrequency(float argFreq) {
        System.out.println("Setting frequency to " + argFreq);
        frequency = argFreq;
        step = (float) frequency / getSampleRate() ;
    }

    /**
     * Returns a single sample.
     * 
     * @return the next sample.
     */
    public float read() {
    	phase += step;
        float val = phase<ratio?amplitude:-amplitude ;
        return val;
    }
    /**
     * Just for testing.
     * 
     * @param args ignored.
     */
    public static void main(String[] args) {
        final PulseOscillator oszi = new PulseOscillator(440, 10000);
        FISPlayer player = new FISPlayer(oszi);
        JFrame frame = new JFrame("Oscillator");
        final JSlider slider = new JSlider(1, 20000, 440);
        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                oszi.setFrequency(slider.getValue());
            }
        });
        final JSlider ampSlider = new JSlider(0, 32768, 10000);
        ampSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                oszi.setAmplitude(ampSlider.getValue());
                System.out.println("New amplitude: " + ampSlider.getValue());
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(slider);
        frame.getContentPane().add(ampSlider);
        frame.pack();
        frame.show();
        player.play();
        //		float freq = 440;
        //		float diff = -1;
        //		while(true){
        //		    if(freq >= 880)
        //		        diff=-0.5f;
        //		    if(freq <= 440)
        //		        diff=0.5f;
        //		    freq += diff;
        //		    oszi.setFrequency(freq);
        //		    try {
        //                Thread.sleep(50);
        //            } catch (InterruptedException e1) {
        //                // TODO Auto-generated catch block
        //                e1.printStackTrace();
        //            }
        //		}
    }
    
    /**
     * Set the ratio between the duration of the high and low part of the wave in the range form 0 (only high) to 
     * 1 (only low).
     * @param argRatio The ratio to set.
     */
    public void setRatio(float argRatio) {
        this.ratio = argRatio;
    }

	/** Advances n samples.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		phase += n*step;
		phase %= 1;
		return n;
	}

	/** resets the phase of this oscillator.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
		phase =0;
	}
}