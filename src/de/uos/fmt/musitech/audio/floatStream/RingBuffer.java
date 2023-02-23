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

/**
 * This class implements a ring buffer.
 * @author Jens & Tillman 
 */
public class RingBuffer implements FISReader {
	int readIndex = 0; 	//current read-position
	int loadIndex = 0; 	//current load-position
	int loaded = 0;		//number of loaded samples (samples loaded, but not yet read)
	boolean reachedEnd = false;

	float[][] buffer;
	int buffSize;

	FloatInputStream fis;

	/**
	 * Reset the buffer and the contained FloatInputStream
	 * @throws IOException
	 */
	public void reset() throws IOException {
		readIndex = loadIndex = loaded = 0;
		fis.reset();
	}

	public synchronized int loadZeros(int num) {
		if (num >= buffSize-loaded) { // don't overload the buffer
			num = buffSize-loaded;
		}
		int channels = fis.getFormat().getChannels(); 
		for ( ; num > 0 ; loadIndex=(loadIndex+1)%buffSize) {
			num--;
			loaded++;
			for (int i = 0; i < channels; i++) {
				buffer[i][loadIndex] = 0.0f;
			} // end inner for
		} // end for
		return num;
	}

	/**
	 * Create a RingBuffer that reads from the given input stream and has geiven size.
	 * @param fis The stream to read from
	 * @param buffSize The buffer size in samples.
	 */
	public RingBuffer(FloatInputStream fis, int buffSize) {
		this.buffSize = buffSize;
		setFloatInputStream(fis);
	}

	/**
	 * This method reads from the stream into the internal buffer.
	 * @return int The nubmer of samples written.
	 */
	public synchronized int load() {
		int didLoad = 0;
		try {
			int doLoad = buffSize-loaded;
			if(loadIndex >= readIndex ){ // in normal position
				if(loadIndex + doLoad > buffSize){  // we must no write beyond the buffer end.
					doLoad = buffSize - loadIndex;
				} // end inner if
			} // end if
			// else we are in wrap position and can write up to the readPos
			assert loadIndex+doLoad <= buffer[0].length;
				//System.err.println("loadedIndex = "+loadIndex+", doLoad = "+doLoad+", buffer.length = "+buffer[0].length+", buffer[0].hashCode() = "+buffer[0].hashCode());
			int didRead = fis.read(buffer, loadIndex, doLoad);
			if(didRead == -1){
				reachedEnd=true;
				return -1;
			}
			didLoad += didRead;
			loadIndex += didRead;
			assert loadIndex <= buffSize;
			if (loadIndex == buffSize) {
				loadIndex = 0;
				didRead = fis.read(buffer, loadIndex, readIndex); 
				// inserted by Nicolai Strauch 31.05.03.   It's ok?
				if(didRead != -1){
					didLoad += didRead;
					loadIndex += didRead;
				}// end inserting
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		loaded+=didLoad;
		assert checkIndices();
		return didLoad;
	}
	
	boolean checkIndices(){
		return (readIndex+loaded) % buffSize == loadIndex;
	} 

	/**
	 * This method reads data from the internal buffer into the given buffer.
	 * The caller is responsible for start and len values not to exceed the end of the buffer.
	 * @param data the buffer to read to. 
	 * @param start Where to start writing.
	 * @param len	Number of Samples to read.
	 * @return int The number of samples written.
	 */
	public synchronized int read(float[][] data, int start, int len) {
		if(reachedEnd == true){
			return -1;
		}
		int didRead = 0;
		if(len >= loaded){
			len = loaded;
		}
		int doRead = len;
		if ( loadIndex > readIndex ) { // in normal position
			if ( readIndex + doRead > loadIndex ) {
				doRead = loadIndex - readIndex;
			} 
		} else { // in wrap position
			// do not read beyond end of buffer
			if(readIndex + doRead > buffSize){
				doRead = buffSize - readIndex;
			}
		}
		for (int channel = 0; channel < buffer.length; channel++) { // fo all channels
			assert readIndex>=0 && start>=0 && doRead>=0;
			System.arraycopy(buffer[channel], readIndex, data[channel], start, doRead);
		}
		didRead += doRead;
		readIndex += doRead;
		start += doRead;
		if (readIndex == buffSize) {
			readIndex = 0;
		}
		assert readIndex < buffSize;
		if(readIndex == 0){ // wrapping
			doRead = len - didRead;
			for (int channel = 0; channel < buffer.length; channel++) { // fo all channels
				System.arraycopy(buffer[channel], readIndex, data[channel], start, doRead);
			}
			didRead += doRead;
			readIndex += doRead;
		} // end if readIndex
		loaded -= didRead;
		assert checkIndices();
		return didRead;
	}

	/**
	 * This method reads one sample from the internal buffer into the given buffer performing an interpolation.
	 * @param data The buffer to read to.
	 * @param dataPos The position in the buffer to read to.
	 * @param buffPos The position in the internasl bufrfer to read from.
	 * @return int The number of samples read (0 or 1).
	 */
	public synchronized int read(float[][] data, int dataPos, double buffPos) {
		// @@ Wertebereiche testen an neue Architektur anpassen
		if (buffPos > loadIndex - 1)
			return 0;
		int floor = (int) Math.floor(buffPos);
		int ceil = (int) Math.ceil(buffPos);
		float ratio = floor - (float) buffPos;
		for (int channel = 0; channel < buffer.length; channel++) {
			float value = buffer[channel][floor] * ratio + buffer[channel][ceil] * (1.0f - ratio);
			data[channel][dataPos] = value;
		}
		readIndex = floor - 1;
		return 1;
	}

	/**
	 * Returns the buffer.
	 * @return float[][]
	 */
	public float[][] getBuffer() {
		return buffer;
	}

	/**
	 * Returns the buffSize.
	 * @return int
	 */
	public int getBuffSize() {
		return buffSize;
	}

	/**
	 * Returns the floatInputStream.
	 * @return FloatInputStream
	 */
	@Override
	public FloatInputStream getFloatInputStream() {
		return fis;
	}

	/**
	 * Returns the loadIndex.
	 * @return int
	 */
	public int getLoadIndex() {
		return loadIndex;
	}

	/**
	 * Returns the readIndex.
	 * @return int
	 */
	public int getReadIndex() {
		return readIndex;
	}
	
	public synchronized long skip(long len){
		if(reachedEnd == true){
			return -1;
		}
		int didRead = 0;
		if(len >= loaded){
			len = loaded;
		}
		loaded -= len;
		readIndex = (readIndex + (int)len) % buffSize;
		assert checkIndices();
		return didRead;
	}
	
	/**
	 * @@bitte überprüfen
	 * @param fis the FloatInputStream
	 */
	@Override
	public FISReader setFloatInputStream(FloatInputStream fis) {
		this.fis = fis;
		if(fis != null)
			this.buffer = new float[fis.getFormat().getChannels()][buffSize];
		return this;
	}
	
	/**
	 * The some as the other read-method, but it read into the channel i from data
	 * the channel channels[i] from the source.
	 * 
	 * TODO: maybe this method is better merged whith the other read-method?
	 * If anybody decide, that it is OK so, please delete this lines. (Only two diferences compared to the other read-method)
	 * 
	 * This method reads data from the internal buffer into the given buffer.
	 * The caller is responsible for start and len values not to exceed the end of the buffer.
	 * @param data the buffer to read to. 
	 * @param start Where to start writing.
	 * @param len	Number of Samples to read.
	 * @param channels the channels to be used from the source in data. If any numeber in this array is >=buffer.length, an ArrayIndexOutOfBoundsException will occure.
	 * @return int The number of samples written.
	 */
	public synchronized int read(float[][] data, int start, int len, int[] channels) {
		if(reachedEnd == true){
			return -1;
		}
		int didRead = 0;
		if(len >= loaded){
			len = loaded;
		}
		int doRead = len;
		if ( loadIndex > readIndex ) { // in normal position
			if ( readIndex + doRead > loadIndex ) {
				doRead = loadIndex - readIndex;
			} 
		} else { // in wrap position
			// do not read beyond end of buffer
			if(readIndex + doRead > buffSize){
				doRead = buffSize - readIndex;
			}
		}
		for (int channel = 0; channel < buffer.length; channel++) { // fo all channels
			System.arraycopy(buffer[channels[channel]], readIndex, data[channel], start, doRead);
		}
		didRead += doRead;
		readIndex += doRead;
		start += doRead;
		if (readIndex == buffSize) {
			readIndex = 0;
		}
		assert readIndex < buffSize;
		if(readIndex == 0){ // wrapping
			doRead = len - didRead;
			for (int channel = 0; channel < buffer.length; channel++) { // fo all channels
				System.arraycopy(buffer[channels[channel]], readIndex, data[channel], start, doRead);
			}
			didRead += doRead;
			readIndex += doRead;
		} // end if readIndex
		loaded -= didRead;
		assert checkIndices();
		return didRead;
	}
	
	
	/**
	 * @return number of loaded samples (samples loaded, but not yet read)
	 */
	public int getLoaded() {
		return loaded;
	}

}
