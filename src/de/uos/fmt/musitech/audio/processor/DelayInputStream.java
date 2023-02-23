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
package de.uos.fmt.musitech.audio.processor;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.floatStream.FISReader;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.floatStream.RingBuffer;

/**
 * This class implements a delay line. The delay is fixed and
 * canges in the delay cause skipping, repeating, or insertion of silence.  
 * 
 * @author Jens & Tillman
 */
public class DelayInputStream implements FloatInputStream, FISReader {

	private FloatInputStream floatInputStream;

	private int delay; // delay in samples
	private int buffSize; // buffer size in samples

	private RingBuffer buffer;

	//	public DelayInputStream(
	//		FloatInputStream fis,
	//		int maxDelayInMs,
	//		int initialDelayInMs) {
	//
	//
	//		if (fis != null) {
	//
	//			int numberOfChannels = floatInputStream.getFormat().getChannels();
	//			float sampleRate = (int)floatInputStream.getFormat().getSampleRate();
	//
	//			delayOffset = initialDelayInMs * sampleRate / 1000;
	//			int buffSize = maxDelayInMs * sampleRate / 1000;
	//			this.buffer = new RingBuffer(floatInputStream, buffSize);
	//
	//		}
	//
	//	}

	/**
	 * ATTENTION!
	 *   FloatInputStream must be initialised!
	 */
	public DelayInputStream(){
		this(null, 1, 1);
	}

	/**
	 * Constructor with params in samples.
	 * @param fis The input stream to delay.
	 * @param maxDelayInSamples The maximum delay that will be available in samples. 
	 * @param initialDelayInSamples The delay to be used initially in samples 
	 */
	public DelayInputStream(FloatInputStream fis, int maxDelayInSamples, int initialDelayInSamples) {
//		@@ TODO: wenn initialDelayInSamples auf 0 steht, liefert read(...) 0, und der FloatStreamPlayer spielt nicht weiter. Und es passiert gar nichts, wenn maxDelayInSamples auf 0 steht (dann werden nie Daten geliefert). 
//				entweder, es werden falsche Begriffe verwendet, oder man muss es anders regeln, als dass an die Buffergröße nach maxDelayInSamples richtet. 
//				(ich sehe nicht den wirklichen Zusammenhang zwischen maxDelayInSamples und der Buffergröße)
		if(fis == null){	// TODO: sollte das nicht einheitlich sein? Entweder liefern alle FIS eine exeption in dem Fall, oder keiner? Sind die Folgen nicht bei allen annähernd die selben?
			throw new IllegalArgumentException("FloatIntpuStream param in DelayInputStream must not be null.");
		}
		floatInputStream = fis;
		// buffer should not bee too small for efficieny reasons.
		buffSize = Math.max(maxDelayInSamples,128);
		delay = initialDelayInSamples;
		if (delay > buffSize) {
			System.out.println("Warning: delay is larger than buffsize in"+getClass()+". buffSize will be adjusted.");
			buffSize = delay;
		} // end if 
		buffer = new RingBuffer(floatInputStream, maxDelayInSamples);
		buffer.loadZeros(initialDelayInSamples);
	}

	/**
	 * The read methods return zero samples at the start of the stream for
	 * the length of the stream and after that stream contents delayed by 
	 * the set delay. 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float)
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * The read methods return zero samples at the start of the stream for
	 * the length of the stream and after that stream contents delayed by 
	 * the set delay. 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float, int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		int didRead = buffer.read(data, start, len);
		buffer.load();
		return didRead;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return floatInputStream.getFormat();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		float tmp[][] = new float[getFormat().getChannels()][(int) n];
		return read(tmp);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		floatInputStream.reset();
		buffer.loadZeros(delay);
	}
	/**
	 * Returns the buffSize.
	 * @return int
	 */
	public int getBuffSize() {
		return buffSize;
	}

	/**
	 * Returns the delay.
	 * @return int
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Sets the delay in samples. The delay must not be greater that the buffsize.
	 * @param newDelay The delay to set.
	 */
	public void setDelay(int newDelay) {
		if(newDelay > buffSize){
			System.out.println("Warning: delay param is greater that buffSize in setDelay in class "+getClass()+". delay will be adjusted");
			newDelay  = buffSize;
		}
		if(newDelay > delay){ // 
			buffer.loadZeros(newDelay-delay);
		}
		if(newDelay < delay){ // 
			try {
				skip(delay-newDelay);
			} catch (Exception e) {
				e.printStackTrace();
			} // end try/catch
		}
		this.delay = newDelay;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FISReader#setFloatInputStream(de.uos.fmt.musitech.audio.floatStream.FloatInputStream)
	 */
	@Override
	public FISReader setFloatInputStream(FloatInputStream fis) {
		buffer.setFloatInputStream(fis);
		return this;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FISReader#getFloatInputStream()
	 */
	@Override
	public FloatInputStream getFloatInputStream() {
		return buffer.getFloatInputStream();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return floatInputStream.remainingSamples()+buffer.getLoaded();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return floatInputStream.getPositionInSamples()-buffer.getLoaded();
	}
	
	/**
	 * Set the stream position in samples.
	 * @param newPos The new position. 
	 * @throws IOException ban be thrown by an underlying IO stream.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		buffer.reset();
		floatInputStream.setPositionInSamples(newPos);
	}

}
