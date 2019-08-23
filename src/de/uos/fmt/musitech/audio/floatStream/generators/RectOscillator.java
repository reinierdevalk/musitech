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

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.audio.floatStream.generators.FloatOscillator;

/**
 * An oscillator that plays a rectangle wave using a wavetable. 
 * Has some other features.
 *
 * @author Tillman Weyde
 */
public class RectOscillator extends FloatOscillator {

	private float frequency; // analogue frequency in Hz
	private float cps; // frequency in cycles/sample
	private float phase = 0; // current phase value as a fraction of a cycle
	private float pw = .5f; // pulse width value
	private float gain = 0.1f;
	private float targetGain; // stores the target gain value
	// switches
	private boolean bandLimited = false; // flag for bandLimited generation
	private boolean deClick = false; // flag for click avoiding
	private boolean sine = false; // flag for use a sine wave
	private boolean pwSet = true;
	private float[] rectWaveTable; // the array for the wave table

	private boolean DEBUG = false; // flag for debug messages
	
	private float glideSpeed = 1f; // The time for a frequency change in milliseconds
	private float targetFrequency; // stores the target gain value


	/**
	 * Constructor with default values (440Hz, -10dB).
	 */
	public RectOscillator() {
		rectWaveTable = new float[wavTabLen];
		createWavetable();
		setFrequency(440);
		setGain(0.1f);
	}

	private void createWavetable() {
		float quantum = 1f;
		pwSet = true;
		float upper = 1-pw;
		float lower = -pw;
		for (int i = 0; i < rectWaveTable.length; i++) {
			float target;
			if( i<rectWaveTable.length*pw ) {
				target = upper;
			}
			else {
				target = lower;
			}
			if(rectWaveTable[i] != target) {
				rectWaveTable[i] += Math.max(Math.min(target-rectWaveTable[i], quantum),-quantum);
				//pwSet = false;
			}
			//dcRemoveAndNormalise(rectWaveTable);
		}
	}

	@SuppressWarnings("unused")
	private void dcRemoveAndNormalise(float[] argWaveTable) {
		float max=0;
		float min=0;
		double avg = 0;
		for (int i = 0; i < argWaveTable.length; i++) {
			if(argWaveTable[i] > max)
				max = argWaveTable[i];
			if(argWaveTable[i] < min)
				min = argWaveTable[i];
			avg += argWaveTable[i];
		}
		avg /= argWaveTable.length;
		max-=avg;
		min-=avg;
		float scale = 1/Math.max(Math.abs(max),Math.abs(min));
		for (int i = 0; i < argWaveTable.length; i++) {
			argWaveTable[i] -= avg;
			argWaveTable[i] *= scale;
		}
	}	


//	The math for MIDI pitch to frequency:
//
//	pitch = a+b*log(frequency)
//	(pitch - a ) / b = log( frequency )
//	exp( (pitch - a ) / b ) = frequency
//
//	we know the following:
//	69 = a + b * log(440)  // by definition
//	69 + 12 = a + b * log(440 * 2 ) // because double freq ~ one ocatve ~ 12 semitone pitch steps

//	69  = a + b * log(440)
//	isolate a:
//	69 - b * log(440) = a
//	substitute a:
//	69 + 12 = 69 - b * log(440) + b * log(440 * 2)
//	and isolate b:
//	12 = - b * log(440) + b  * log(440 * 2)
//  12 = - b * log(440) + b  * (log(440) + log(2) )
//	12 = b * (-log(440) + log(440) + log(2) )
//	12 = b * log(2)
//	12/log(2)  = b
//	b = 17.312

//	calculate a by substituting b:
//	a = 69 - 12 / log(2) * log(440)
//	a = 69 - 12 * log(440) / log(2)
//	a = 69 - 105.37631656229591524883618971458
//	a = -36.376


	/**
	 * Sets the frequency of this oscillator according to MIDI note numbers.
	 * @param pitch The pitch in MIDI numbers (69=440Hz)
	 */
	public void setPitch(int pitch) {
		setFrequency(440 * (float) Math.pow(2, (pitch - 69f) / 12));
	}

	/**
	 * Gets the frequency of this oscillator according to MIDI note numbers.
	 * @return The pitch in MIDI numbers (69=440Hz)
	 */
	public int getPitch() {
		return 69 + (int) Math.round(12 * Math.log(getFrequency() / 440f) / Math.log(2));
	}

	private float minPW = 0.01f; // Minimal pulse width
	private float maxPW = 0.99f; // Maximal pulse width
	private int wavTabLen = 4096; // Length of the wavetable 

	/**
	 * Sets the pulse width of this oscillator.
	 * @param argPW The pulse width as a float between
	 */
	public void setPW(float argPW) {
		float newPw;
		if (argPW < minPW)
			newPw = minPW;
		else if (argPW > maxPW)
			newPw = maxPW;
		else
			newPw = argPW;
		pwSet = false;
		if(pw != newPw) {
			pw = newPw;
			createWavetable();
		}
	}


	/**
	 * Gets the pulse width of this oscillator.
	 * @return The pulse width as a value between 0.01 and 0.5.
	 */
	public float getPW() {
		return pw;
	}


	/**
	 * Returns a single sample.
	 * @return the amplitude for the next sample
	 */
	@Override
	public float read() {
		// TODO replace the sine signal here with a wavetable lookup.
		// The output value is adapted to 16 bit output format here
		// using '* 32767';

		incPhase(cps); // increase the phase

		float val;
		if (sine)
			val = (float) Math.sin(phase * 2 * Math.PI);
		else {
			// the leftmost sample index to process
			float pos1 = rectWaveTable.length * phase;
			if (!bandLimited)
				val = rectWaveTable[(int) pos1];
			else {
				int lo = (int) Math.floor(pos1);
				float ratio1 = lo + 1 - pos1; // fraction of the left margin
				int lo1 = (lo + 1) % rectWaveTable.length; // next after lo

				float pos2 = pos1 + 1 * cps * rectWaveTable.length;

				// the rightmost-1 sample index to process
				int hi = (int) Math.floor(pos2) % rectWaveTable.length;
				// float ratio2 = pos2 - hi;

				val = rectWaveTable[lo];// * (1 - ratio1);

				// the number of samples covered (initialised to
				// take only the left margin into account)
				float sampleNum = ratio1;

				// the iteration over the range between left and right, wrapping
				// around where necessary
				for (lo = lo1; // start one after left
						lo != hi; // while not at right boundary
						lo = (++lo) % wavTabLen ) // go to next index
				{
					val += rectWaveTable[lo];
					sampleNum++; // increase the sample number
				}

				// add the right margin
				// val += sawtoothTable[hi] * (ratio2);
				// sampleNum += ratio2;
				val += rectWaveTable[hi];
				sampleNum++;

				val /= sampleNum; // divide by range length
			}
		}
		float quantum = 0.001f;
		if (targetGain != gain ) { // update gain if necessary
			double diff = targetGain - gain;
			if (deClick) {
				if (diff < -quantum) // make sure the change is not too
					// large
					diff = -quantum;
				else if (diff > quantum) // ... in either direction
					diff = quantum;
			}
			gain += diff; // the gain change
		}
		if(!pwSet)
			createWavetable();
		if(gliding)
			updateFreq();
		return val * gain * 30000; // the signal value
	}

	/**
	 * Increases the phase by a delta, wrapping around at 1.
	 * @param delta The value to increase the phase by.
	 */
	private void incPhase(float delta) {
		phase = (phase + delta) % 1;
	}

	// Minimal non-zero Gain Value (should be -50dB)
	// -50 = 10 * log_10(mingain^2)
	// -5 = log_10(mingain^2)
	// 10^-5 = mingain^2
	// 10^-2.5 = mingain
	private double mingain = 0.003162;


	/**
	 * Sets the volume of this oscillator logarithmically according to MIDI
	 * velocity or volume numbers.
	 * @param volume The volume in MIDI numbers (127=full volume)
	 */
	public void setVolume(int volume) {
		// volume 0 means silence, i.e. amplitude 0
		if (volume <= 0)
			setGain(0);
		else
			// amplitude is changed exponentially (corresponding to logarithmic
			// volume) with volume 127 being amplitude 1 and volume 1 (therefore
			// /126) being approx -50dB (therefore * mingain).
			setGain((float) Math.exp( (-(double)volume + 127) / 126 * Math.log(mingain)));
	}

	/**
	 * Gets the volume of this oscillator in MIDI velocity numbers.
	 * @return The volume in MIDI numbers (127=full volume)
	 */
	public int getVolume() {
		// calculate the log amplitude and apply volume factor and offset,
		// such that 1 is 127 and mingain is 0
		// double vol = (Math.log(gain) / Math.log(mingain) * 126 - 127) * -1 ;
		double tmp0 = Math.log(targetGain);
		double tmp1 = tmp0 / Math.log(mingain);
		double tmp2 = tmp1 * 126 ;
		double tmp3 = tmp2 - 127;
		double vol = tmp3 * -1;
		// we limit the volume to be non-negative.
		return (int) Math.round(Math.max(0, vol));
	}



	/**
	 * Gets the gain of the oscillator.
	 * @return The gain.
	 */
	public float getGain() {
		return targetGain;
	}

	/**
	 * Set the amplitude for this oscillator.
	 *
	 * @param argGain The amplification factor.
	 */
	public void setGain(float argGain) {
		targetGain = argGain;
	}

	/**
	 * Get the frequency of this oscillator.
	 *
	 * @return The frequency.
	 */
	public float getFrequency() {
		return targetFrequency;
	}

	/**
	 * (De)activates the de-clicking feature.
	 * @param argState True for active, false for inactive de-clicking,
	 */
	public void setDeClick(boolean argState) {
		deClick = argState;
	}

	/**
	 * (De)activates the bandwidth limited generation.
	 * @param argState True for on, false for off.
	 */
	public void setBandLimited(boolean argState) {
		bandLimited = argState;
	}


	/**
	 * Activates the generation of a sine wave, for camparison.
	 * @param argState True for using a sine wave, false for using the regular waveform.
	 */
	public void setSine(boolean argState) {
		sine = argState;
	}

	private boolean gliding = false;
	
	private void updateFreq() {
		if(getGlideSpeed()<=1) {
			frequency = targetFrequency;
			gliding = false;
		} else {
			if(frequency<targetFrequency) {
				frequency *= glideSpeed;
				if(frequency > targetFrequency)
					frequency = targetFrequency;
			} else {
				frequency /= glideSpeed;
				if(frequency < targetFrequency)
					frequency = targetFrequency;
			}
			gliding = frequency != targetFrequency;
		}
		cps = frequency / sampleRate; // set the new cycles per sample
	}
	
	/**
	 * Get the speed for frequency changes.
	 * @return the speed. 
	 */
	public float getGlideSpeed() {
		return (float) (Math.log(glideSpeed) / Math.log(2) * 1200 * sampleRate );
	}
	
	public void setGlideSpeed(float argGlideTime) {
		glideSpeed = (float)Math.exp(Math.log(2) * argGlideTime/1200/sampleRate);
		updateFreq();
	}

	
	/**
	 * Set the frequency in Hz for this oscillator.
	 * @param argFreq The frequency.
	 */
	public void setFrequency(float argFreq) {
		targetFrequency = argFreq;
		updateFreq();
		if (DEBUG)
			System.out.println("targetfrequency set to " + argFreq + " at " + System.currentTimeMillis());
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		return new AudioFormat(getSampleRate(), 16, super.getFormat().getChannels(), true, true);
	}

	// a static oscillator instance for testing.
	static RectOscillator osc = new RectOscillator();

	/**
	 * Just for testing.
	 * @param args ignored.
	 */
	public static void main(String[] args) {
		// create an oscillator
		// create a player
		FISPlayer player = new FISPlayer(osc);

		osc.setPW(0);
		System.out.println(osc.getGain());
		osc.setPW(1);
		System.out.println(osc.getGain());
		osc.setPW(10);
		System.out.println(osc.getGain());
		osc.setPW(127);
		System.out.println(osc.getGain());

		osc.setPW(.1f);

		// Create the GUI
		JFrame frame = new JFrame("Rectangle Oscillator");
		freqField.setEditable(false);
		JLabel freqLabel = new JLabel("Frequency");
		freqLabel.setLabelFor(freqSlider);
		freqSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int f = freqSlider.getValue();
				osc.setFrequency(f);
				updateSliders();
			}
		});
		ampField.setEditable(false);
		JLabel ampLabel = new JLabel("Amplitude");
		ampLabel.setLabelFor(ampSlider);
		ampSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int a = ampSlider.getValue();
				osc.setGain(a / (float) 1000);
				updateSliders();
			}
		});


		pwSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int a = pwSlider.getValue();
				osc.setPW(a / (float) 1000);
				updateSliders();
			}
		});
		pitchField.setEditable(false);
		JLabel pitchLabel = new JLabel("Pitch");
		pitchLabel.setLabelFor(pwSlider);
		pitchSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int p = pitchSlider.getValue();
				osc.setPitch(p);
				updateSliders();
			}
		});
		pwField.setEditable(false);
		JLabel pwLabel = new JLabel("Pulse Width");
		pwLabel.setLabelFor(pwSlider);

		pwSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int p = pwSlider.getValue();
				osc.setPW(p/(float)1000);
				updateSliders();
			}
		});

		glideField.setEditable(false);
		JLabel glideLabel = new JLabel("Glide Speed (cents/sec)");
		glideLabel.setLabelFor(glideSlider);
		glideSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				int p = glideSlider.getValue();
				osc.setGlideSpeed((p));
				updateSliders();
			}
		});


		deClickButton.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				osc.setDeClick(deClickButton.isSelected());
			}
		});

		bandLimitButton.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				osc.setBandLimited(bandLimitButton.isSelected());
			}
		});

		muteButton.addChangeListener(new ChangeListener() {

			float tmpGain;
			boolean wasSelected;

			public void stateChanged(ChangeEvent e) {
				boolean isSelected = muteButton.isSelected();
				if(isSelected && !wasSelected) {
					tmpGain = osc.getGain();
					osc.setGain(0);
					updateSliders();
				} else if(!isSelected && wasSelected) {
					osc.setGain(tmpGain);
					updateSliders();
				}
				wasSelected = isSelected;
			}
		});

		sineButton.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (sineButton.isSelected()) {
					osc.setSine(true);
					bandLimitButton.setEnabled(false);
				} else {
					osc.setSine(false);
					bandLimitButton.setEnabled(true);
				}
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 1st row
		frame.getContentPane().setLayout(new GridLayout(0, 2));
		frame.getContentPane().add(freqLabel);
		frame.getContentPane().add(ampLabel);
		frame.getContentPane().add(freqSlider);
		frame.getContentPane().add(ampSlider);
		freqField.setFocusable(false);
		frame.getContentPane().add(freqField);
		ampField.setFocusable(false);
		frame.getContentPane().add(ampField);
		// 2nd row
		frame.getContentPane().add(pitchLabel);
		frame.getContentPane().add(pwLabel);
		frame.getContentPane().add(pitchSlider);
		frame.getContentPane().add(pwSlider);
		pitchField.setFocusable(false);
		frame.getContentPane().add(pitchField);
		pwField.setFocusable(false);
		frame.getContentPane().add(pwField);
		// 3rd row
		frame.getContentPane().add(glideLabel);
		frame.getContentPane().add(new JLabel());
		frame.getContentPane().add(glideSlider);
		frame.getContentPane().add(new JLabel());
		glideField.setFocusable(false);
		frame.getContentPane().add(glideField);
		frame.getContentPane().add(new JLabel());
		// 4th row
		frame.getContentPane().add(deClickButton);
		frame.getContentPane().add(bandLimitButton);
		frame.getContentPane().add(muteButton);
		frame.getContentPane().add(sineButton);
		updateSliders();
		frame.pack();
		frame.setVisible(true);

		// start the playback
		player.play();
	}
	private static JSlider glideSlider = new JSlider(0, 2400, 0);
	private static JTextField glideField = new JTextField("0");
	private static JSlider freqSlider = new JSlider(1, 20000, 440);
	private static JTextField freqField = new JTextField("440");
	private static JSlider ampSlider = new JSlider(0, 1000, 100);
	private static JTextField ampField = new JTextField(Integer.toString(100));
	private static JSlider pitchSlider = new JSlider(0, 127, 100);
	private static JTextField pitchField = new JTextField(Integer.toString(100));
	private static JSlider pwSlider = new JSlider(1, 999, 100);
	private static JTextField pwField = new JTextField(Integer.toString(100));
	private static JToggleButton bandLimitButton = new JToggleButton("Band-Limit");
	private static JToggleButton deClickButton = new JToggleButton("De-Click");
	private static JToggleButton muteButton = new JToggleButton("Mute");
	private static JToggleButton sineButton = new JToggleButton("Sine");

	private static boolean updating;

	/**
	 *
	 */
	static private void updateSliders() {
		updating = true;
		float f = osc.getFrequency();
		freqField.setText(Float.toString(f));
		freqSlider.setValue(Math.round(f));
		float a = osc.getGain();
		ampField.setText(Float.toString(a));
		ampSlider.setValue(Math.round(osc.getGain()*1000));
		pwSlider.setValue(Math.round(osc.getPW() * 1000));
		pwField.setText(Float.toString(osc.getPW()));
		int p = osc.getPitch();
		pitchSlider.setValue(p);
		pitchField.setText(Integer.toString(p));
		updating = false;
		int gt = (int)(osc.getGlideSpeed());
		glideSlider.setValue(gt);
		glideField.setText(Integer.toString(gt));
	}

}
