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
package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;
import java.io.PipedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.uos.fmt.musitech.audio.AudioUtil;

public class FIStoAIS extends AudioInputStream implements FISReader {
	private FloatInputStream floatInput;

	private int frameSize;

	// TODO: it is an AudioInputStream, but not implement correctly them
	// Methods.
	// evoke available(), for exemple, will return uncorrect data.
	// the same problem at many more methods from AudioInputStream!!!

	public FIStoAIS(FloatInputStream floatInputStream) {
		//super(new PipedInputStream(), FloatInputStream.DefaultFormat, AudioSystem.NOT_SPECIFIED);
	    super(new PipedInputStream(), 
	          new AudioFormat( // audioFormat from FIS maybe contains values not accepted by AudioInputStream
	          				AudioFormat.Encoding.PCM_SIGNED,
	          				floatInputStream.getFormat().getSampleRate(),
	          				// fro example, if sampleSizeInBits is 1 to denote normalised stream. Then it will be renormalised to 16 bit.
	          				floatInputStream.getFormat().getSampleSizeInBits()==1?16:floatInputStream.getFormat().getSampleSizeInBits(),
	        				floatInputStream.getFormat().getChannels(),
	        				floatInputStream.getFormat().getFrameSize(),
	        				floatInputStream.getFormat().getFrameRate(),
	        				true),
	        	AudioSystem.NOT_SPECIFIED);
	    
		floatInput = floatInputStream;
		floats = new float[floatInput.getFormat().getChannels()][2048];
		frameSize = floatInput.getFormat().getFrameSize();
	} 

	public int read(byte[] data) throws IOException {
		return read(data, 0, data.length);
	}

	public int read(byte[] data, int start, int len) throws IOException {
		assert start + len >= data.length;
		// float[][] floats =
		// new float[format.getChannels()][len / (format.getFrameSize())];
		toRead = len / frameSize;
		if (toRead > floats[0].length)
			toRead = floats[0].length;
		sRead = floatInput.read(floats, 0, toRead);

//		System.out.println("FIStoAIS: Enter for(int i=0; i>"+sRead+"; i++)");
//		for (int i = 0; i < sRead; i++) {
//            if (floats[0][i] < 0)
//                System.err.println("FIStoAIS >>> i = " + i + "; " + floats[0][i]);
//            else
//                System.out.println("FIStoAIS >>> i = "+ i + "; " + floats[0][i]);
//        }
		
		if (sRead < 1)
			return sRead;
		
		return AudioUtil.floatsToBytes(floats, 0, data, start, sRead, floatInput.getFormat().getSampleSizeInBits());
		//		byte[] tmp = AudioUtil.floatsToBytes(floats, 0, sRead);
		//		System.arraycopy(tmp, 0, data, start, tmp.length);
		//		return tmp.length;
	}

	private float[][] floats;

	private int toRead;

	private int sRead;

	/**
	 * skip n bytes. Skip n/frameSize in FloatInputStream.
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return floatInput.skip(n / frameSize);
	}

	public void reset() throws IOException {
		floatInput.reset();
	}

	public FloatInputStream getFloatInputStream() {
		return floatInput;
	}

	public FISReader setFloatInputStream(FloatInputStream floatInputStream) {
		floatInput = floatInputStream;
		return this;
	}

	public AudioFormat getAudioFormat() {
		if (frameSize == 1) {
			AudioFormat in = floatInput.getFormat();
			return new AudioFormat(in.getEncoding(), in.getSampleRate(), 16, in.getChannels(), in.getFrameSize(), in
					.getFrameRate(), in.isBigEndian());
		}
		return floatInput.getFormat();
	}

}