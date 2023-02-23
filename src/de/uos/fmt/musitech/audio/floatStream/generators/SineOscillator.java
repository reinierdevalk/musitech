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

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * An oscillator creating a sine wave. It has an amplitude (a) and a frequency 
 * (f) such that at sampleRate s the output is: signal(t) = a * sin(2*Pi*f/s).
 * 
 * @author Martin Gieseking, Tillman Weyde
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class SineOscillator extends FloatOscillator {

	private float frequency2pi; // analog frequency in radians
	private float step; // step size sin argument per sample, frequency in radians per sample 
	/** position within a cycle in the range [0,2*pi) */
	private float phase = 0; // current phase value
	private float amplitude; // absolute value

	/**
	 * Returns the phase in the range from [0,1)
	 * 
	 * @return the current phase
	 */
	protected float getPhase() {
		return phase;
	}

	/**
	 * Creates a new oscillator with given frequency and amplitude.
	 * 
	 * @param argFrequency The frequency in Hz.
	 * @param argAmplitude The maximal amplitude.
	 */
	public SineOscillator(float argFrequency, float argAmplitude) {
		this.amplitude = argAmplitude;
		setFrequency(argFrequency);
	}

	/**
	 * Returns a single sample.
	 * 
	 * @return the amplitude for the next sample
	 */
	@Override
	public float read() {
		incPhase(step); 
		return (float) (amplitude * Math.cos(phase));
	}

	private void incPhase(float delta) {
		phase = (phase + delta) % pi2;
	}

	/**
	 * Gets the amplitude of the oscillator.
	 * 
	 * @return The maximal amplitude reached by the waveform.
	 */
	public float getAmplitude() {
		return amplitude;
	}

	/**
	 * Get the frequency of this oscillator.
	 * 
	 * @return The frequency.
	 */
	public float getFrequency() {
		return (float) (frequency2pi / (2 * Math.PI));
	}

	/**
	 * Set the amplitude for this oscillator.
	 * 
	 * @param amplitude The maximal amplitude reached by the waveform.
	 */
	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * Set the frequency in Hz for this oscillator.
	 * 
	 * @param argFreq The frequency.
	 */
	public void setFrequency(float argFreq) {
		frequency2pi = (float) (argFreq * 2 * Math.PI);
		step = frequency2pi / getSampleRate();
		if(DebugState.DEBUG_AUDIO)
			System.out.println("frequency set to " + argFreq + " at " + System.currentTimeMillis());
	}

	/**
	 * Just for testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create an oscillator
		final SineOscillator osc = new SineOscillator(440, 10000);
		// create a player
		FISPlayer player = new FISPlayer(osc);

		JFrame frame = new JFrame("Oscillator");
		final JSlider freqSlider = new JSlider(1, 20000, 440);
		final JTextField freqField = new JTextField("440");
		freqField.setEditable(false);
		final JLabel freqLabel = new JLabel("Frequency");
		freqLabel.setLabelFor(freqSlider);
		freqSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int f = freqSlider.getValue(); 
				osc.setFrequency(f);
				freqField.setText(Integer.toString(f));
			}
		});
		final JSlider ampSlider = new JSlider(0, 32767, 10000);
		final JTextField ampField = new JTextField(Integer.toString(10000));
		osc.setAmplitude(.5f);
		ampField.setEditable(false);
		final JLabel ampLabel = new JLabel("Amplitude");
		ampLabel.setLabelFor(ampSlider);
		ampSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int a = ampSlider.getValue();
				osc.setAmplitude(a/20000f);
				ampField.setText(Integer.toString(a));
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(3,2));
		frame.getContentPane().add(freqLabel);
		frame.getContentPane().add(ampLabel);
		frame.getContentPane().add(freqSlider);
		frame.getContentPane().add(ampSlider);
		frame.getContentPane().add(freqField);
		frame.getContentPane().add(ampField);
		frame.pack();
		frame.setVisible(true);
		
		// start the sound
		player.play();
	}

	@Override
	public long skip(long n) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() throws IOException {
		// TODO Auto-generated method stub
		
	}
}