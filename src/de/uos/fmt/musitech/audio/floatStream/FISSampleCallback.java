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
 * Created on 21.04.2004
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;

/**
 * @author Nicolai Strauch
 */
public class FISSampleCallback implements FISReader, FloatInputStream
{
	private FloatInputStream source;
	
	//  number of sample whith that the callbacklisteners have to be adviced 
	private long callbackSample  = Long.MAX_VALUE; // if it is a long, the carst to int from Long.MAX_VALUE is negative!!
//	private boolean callBack;
	// number of samples allready played
	private long samplesRead;
 

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	@Override
	public synchronized int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}
	
	/**
	 * Read in maximum so many samples required and rest to the next callbackSample.
	 * If the callbackSample is reached, notify the listeners.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public synchronized int read(float[][] data, int start, int len) throws IOException {
		// look if so many samples rest to the next callback-sample
		// as required. Do not read more samples than rest.	
		if(callbackSample!=Long.MAX_VALUE)
			len = Math.min(len, (int)(callbackSample - samplesRead));
		// if no samples rest, notify listeners and return 0.
		if (len <= 0){
			callbackSample = Long.MAX_VALUE;
			System.out.println("FISSampleCallback.read(...): notified listeners. (len negativ)");
			notifyListeners(notifySReached);
			return 0;
		}
		len = source.read(data, 0, len);
		samplesRead += len;
		// look if callbacksample is reached.
		if (callbackSample == samplesRead){
			callbackSample = Long.MAX_VALUE;
			System.out.println("FISSampleCallback.read(...): notified listeners.");
			notifyListeners(notifySReached);
		}
		return len;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public synchronized long skip(long n) throws IOException {
		if(n>callbackSample-samplesRead)
			n = callbackSample-samplesRead;
		if(n<0)
			return 0;
		n = source.skip(n);
		samplesRead += n;
		if(samplesRead == callbackSample){
			System.out.println("FISSampleCallback.skip(...): notified listeners.");
			notifyListeners(notifySReached);
		}
		return n;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		source.reset();
		samplesRead = 0;
		callbackSample = Long.MAX_VALUE;
		notifyListeners(notifyReset);
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FISReader#setFloatInputStream(de.uos.fmt.musitech.audio.floatStream.FloatInputStream)
	 */
	@Override
	public synchronized FISReader setFloatInputStream(FloatInputStream fis) {
		source = fis;
		return this;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FISReader#getFloatInputStream()
	 */
	@Override
	public FloatInputStream getFloatInputStream() {
		return source;
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return source.getFormat();
	}
	
	/**
	 * Set the samplePosition at that the TimeListener must be called back.
	 * If the samplePosition given is pas, the callBackTime is setted to 
	 * the actual samplePosition, and the TimeListener will be called back now.
	 * T O D O: or is it more good to ignore the invoke, if the samplePosition
	 * given is pas?
	 * @param callBackTime
	 */
	public synchronized void setCallbackSample(long callBackSample) {
		this.callbackSample = callBackSample;
		if (callBackSample <= samplesRead) {
			callbackSample = samplesRead;
		}
	}

	/**
	 * Calculate the sample corresponding the time given, and set it.
	 * See setCallbackSample(long).
	 * @param callBackTime
	 */
	public synchronized void setCallbackTime(long callBackTime) {
		setCallbackSample(
			(int) (callBackTime * source.getFormat().getFrameRate() / 1000));
	}
	
	/**
	 * @return the number of samples read yet.
	 */
	public long getSamplesRead() {
		return samplesRead;
	}
	
	/**
	 * do not more than set samplesRead: not reset, not set callbackSample.
	 * So use it very carefully:
	 * seting samplesRead without set callbackSample give problems.
	 * @param l
	 */
	synchronized void setSamplesRead(long l){
		if(l<0){
			samplesRead = 0;
			System.err.println("FISSampleCallback.setSamplesRead(long l): receiving number < 0 to set as samples read. Set samplesRead to 0.");
		}
		samplesRead = l;
	}
	
	public long getCallbackSample() {
		return callbackSample;
	}
	
	public long getCallbackTime() {
		return ((long) (callbackSample / source.getFormat().getFrameRate() * 1000));
	}

	private List<SampleReachListener> srListeners = new ArrayList<SampleReachListener>();

	public synchronized void addSampleReachListener(SampleReachListener srl){
		srListeners.add(srl);
	}

	/**
	 * Advice all registerd Listeners
	 * @param notifyThat can be: notifySReached, notifyReset
	 */
	private void notifyListeners(int notifyThat){
		switch(notifyThat){
			case notifySReached:
				callbackSample = Long.MAX_VALUE;
				break;
			case notifyReset:
				break;
		}
		SampleReachListener srl;
		for(Iterator<SampleReachListener> iter = srListeners.iterator(); iter.hasNext(); ){
			srl = iter.next();
			switch(notifyThat){
				case notifySReached:
					srl.sampleReached();
					break;
				case notifyReset:
					srl.streamReset();
					break;
			}
		}
	}


private final int notifySReached = 1;
private final int notifyReset = 2;


	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return source.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return getSamplesRead();
	}
	
	/**
	 * @param newPos The position to set the stream to.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public synchronized void setPositionInSamples(long newPos) {
		try {
			reset();
			skip(newPos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
