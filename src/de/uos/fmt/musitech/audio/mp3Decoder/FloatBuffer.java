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
package de.uos.fmt.musitech.audio.mp3Decoder;

import javazoom.jl.decoder.Obuffer;


//import javazoom.jl.decoder.SampleBuffer;

/**
 * @author Nicolai Strauch
 *
 * 
 */
public class FloatBuffer extends Obuffer {

	private float[][] buffer;
	private int[] bufferp;
	private int channels;
	private int frequency;

	public FloatBuffer(int sample_frequency, int number_of_channels) {
		buffer = new float[number_of_channels][OBUFFERSIZE];
		bufferp = new int[MAXCHANNELS];
		channels = number_of_channels;
		frequency = sample_frequency;

		clear_buffer();
	}

	public int getChannelCount() {
		return this.channels;
	}

	public int getSampleFrequency() {
		return this.frequency;
	}

	public float[][] getBuffer() {
		return this.buffer;
	}

	public int getBufferLength() {
		return bufferp[0];	// ?? macht doch keinen Sinn! (buffer-Index von Kanal 0 zurückliefern. Ist aber son von SampleBuffer kopiert.)
	}

	/**
	 *
	 */
	@Override
	public void clear_buffer() {
		for (int i = 0; i < channels; ++i)
			bufferp[i] = 0;	// all channels begin, off course, with the some index, different from SampleBuffer
	}

	/**
	 * @see javazoom.jl.decoder.Obuffer#append(int, short)
	 */
	public void append(int channel, float value) {

		buffer[channel][bufferp[channel]++] = value;

	}

	@Override
	public void appendSamples(int channel, float[] f) {
		int pos = bufferp[channel];
		for (int i = 0; i < 32;) {
			
			float fs = 0;
			fs = f[i++];
			fs = (fs>32767.0f ? 32767.0f 
						   : (fs < -32767.0f ? -32767.0f : fs));
			if(pos < buffer[channel].length)
				buffer[channel][pos] = fs;	// fs/100???
			else{
				if(outOfPosCounter>129){ // bug in mp3-decoder? break possible infinit loop from decoder 
					outOfPosCounter = 0;
					throw new ArrayIndexOutOfBoundsException(); // nu is aus mit Geduld
				}
			//	System.out.println("FloatBuffer.appendSamples(...): pos out of buffer range, samples maybe go lost. Loop "+outOfPosCounter+" from "+128+"loops.");
				outOfPosCounter++;
			}

			pos++;
		}
		bufferp[channel] = pos;
	}
	int outOfPosCounter = 0;
	/**
	 * @see javazoom.jl.decoder.Obuffer#append(int, short)
	 */
	@Override
	public void append(int channel, short value) {
		append(channel, (float) value);
		System.out.println("FloatBuffer.append(...): es wird doch ein short geschrieben!");
	}

	/**
	 * @see javazoom.jl.decoder.Obuffer#write_buffer(int)
	 */
	@Override
	public void write_buffer(int val) {
	}
	/**
	 * @see javazoom.jl.decoder.Obuffer#close()
	 */
	@Override
	public void close() {
	}
	/**
	 * @see javazoom.jl.decoder.Obuffer#set_stop_flag()
	 */
	@Override
	public void set_stop_flag() {
	}

}
