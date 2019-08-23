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
package de.uos.fmt.musitech.audio.proc.fft.backup;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioData {
	private int[] leftChannel = null; // Sample-Daten des linken Kanals

	private int[] rightChannel = null; // Sample-Daten des rechten Kanals

	private int numChannels = 0; // Anzahl der Kanäle

	private long millis = 0; // Dauer in Millisekunden

	private int sampleDepth = 0; // Bits pro Sample

	private float sampleRate = 0; // Samples pro Sekunde

	private boolean valid = false; // true, wenn Audiodaten gültig

	public AudioData(File audiofile) {
		if (audiofile != null && audiofile.isFile())
			try {
				AudioInputStream inputstream = AudioSystem.getAudioInputStream(audiofile);
				AudioFormat format = inputstream.getFormat();
				numChannels = format.getChannels();
				millis = (long) (1000 * inputstream.getFrameLength() / format.getFrameRate());
				sampleDepth = format.getSampleSizeInBits();
				sampleRate = format.getSampleRate();

				int frameLength = (int) inputstream.getFrameLength();

				leftChannel = new int[frameLength];
				if (numChannels == 2)
					rightChannel = new int[frameLength];

				byte frame[] = new byte[format.getFrameSize()];

				if (sampleDepth == 16) {
					for (int i = 0; i < frameLength; i++) {
						inputstream.read(frame, 0, frame.length);
						leftChannel[i] = toInt(frame[0], frame[1], format);
						if (numChannels == 2)
							rightChannel[i] = toInt(frame[2], frame[3], format);
					}
				} else if (sampleDepth == 8) {
					for (int i = 0; i < frameLength; i++) {
						inputstream.read(frame, 0, frame.length);
						leftChannel[i] = toInt(frame[0], format);
						if (numChannels == 2)
							rightChannel[i] = toInt(frame[1], format);
					}
				}
				inputstream.close();
				valid = true; // wenn wir hier angekommen sind, wurden gültige
				// Audiodaten gelesen
			} catch (Exception e) {
				System.out.println("Fehler beim Öffnen von '" + audiofile + "'");
				e.printStackTrace();
			}
	}

	/*
	 * public void print () { for (int i=0; i < leftChannel.length; i++) {
	 * System.out.print(i + ": " + leftChannel[i]); if (rightChannel != null)
	 * System.out.println(", " + rightChannel[i]); } }
	 */

	public int getNumChannels() {
		return numChannels;
	}

	public long getMillis() {
		return millis;
	}

	public int[] getLeftChannel() {
		return leftChannel;
	}

	public int[] getRightChannel() {
		return rightChannel;
	}

	public void setLeftChannel(int[] lc) {
		leftChannel = lc;
	}

	public void setRightChannel(int[] rc) {
		rightChannel = rc;
	}

	public boolean isValid() {
		return valid;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getSampleDepth() {
		return sampleDepth;
	}

	// wandelt ein einzelnes Byte gemäß angegebenem AudioFormat in eine
	// Integer-Zahl um
	protected static int toInt(byte b, AudioFormat format) {
		int res;
		if (format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED)
			res = b & 0xff;
		else
			res = b;
		if (format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED)
			res -= 0x7f;
		return res;
	}

	// wandelt ein Byte-Paar gemäß angegebenem AudioFormat in eine Integer-Zahl
	// um
	protected static int toInt(byte b1, byte b2, AudioFormat format) {
		int upper_byte, lower_byte;
		if (format.isBigEndian()) {
			upper_byte = b1;
			lower_byte = b2;
		} else {
			upper_byte = b2;
			lower_byte = b1;
		}
		int res = (upper_byte << 8) | (lower_byte & 0xff);
		if (format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED)
			res -= 0x7fff;
		return res;
	}

	/*
	 * public static void main (String[] argv) { AudioData ad = new
	 * AudioData(new File("c:\\windows\\media\\chord.wav")); int left[] =
	 * ad.getLeftChannel(); int right[] = ad.getLeftChannel();
	 * System.out.println("Kanäle: " + ad.getNumChannels()); for (int i=0; i <
	 * left.length; i++) System.out.println(left[i] + " " + right[i]); }
	 */
}