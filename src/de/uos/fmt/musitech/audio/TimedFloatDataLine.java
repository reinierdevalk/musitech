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
 * Created on 26.06.2004
 */
package de.uos.fmt.musitech.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

import de.uos.fmt.musitech.framework.time.MSyncTimer;
import de.uos.fmt.musitech.framework.time.MTimer;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * @author Nicolai Strauch, Tillman Weyde
 */
public class TimedFloatDataLine {

	SourceDataLine sLine;

	byte[] byteBuffer = new byte[4096];
	MSyncTimer timer;


	/**
	 * TODO 
	 * @param t
	 * @param audioFormat
	 */
	public TimedFloatDataLine(MSyncTimer t, AudioFormat audioFormat) {
		timer = t;
		sampleSizeInBits = audioFormat.getSampleSizeInBits();
		sLine = AudioUtil.getSourceDataLine(audioFormat,16384);
//		sLine = AudioUtil.getSourceDataLine(audioFormat,4096);
		if(DebugState.DEBUG_AUDIO)
			System.out.println(this.getClass()+" sLine.getBufferSize() = "+sLine.getBufferSize());
		frameSizeInBytes = sLine.getFormat().getFrameSize();
		System.out.println(sLine.getFormat());
		bufferSizeInFrames = byteBuffer.length / frameSizeInBytes;
	}

	boolean stopped = true;
	
	int sampleSizeInBits;
	int frameSizeInBytes;
	int bufferSizeInFrames;
	int framesToWrite;
//	int convertedFrames = 0;
	int bytesConverted = 0; // total number of convert bytes
	int bytesWritten = 0; // total number of bytes written
	int written = 0; // written in one loop execution
//	int bytesWInBuff = 0; // number of bytes written to the buffer in one outer loop
	//int floatBufferOffset;	// offset in remaining float buffer if dataLine is stopped
	//int floatsRemainingToWrite; // floats remaining to write if dataLine is stopped

	public int write(float[][] floatBuffer, int off, int len) {
		stopped = false;
		
		sLine.start();

		bytesConverted = 0; // total number of convert bytes
		bytesWritten = 0; // total number of bytes written
		written = 0; // written in one inner loop execution

		if(byteBuffer.length < floatBuffer[0].length * frameSizeInBytes){
			byteBuffer = new byte[floatBuffer[0].length * frameSizeInBytes];
		}
		
		bytesConverted = AudioUtil.floatsToBytes(floatBuffer, off,
												byteBuffer, 0, len,
												sampleSizeInBits);			
			
		while (bytesWritten < bytesConverted) { // while not all converted
			// bytes are played
			if (stopped){
				// TODO Short Fade-Out if necessary
				break;
			}
			// write bytes into the dataline
			written = sLine.write(byteBuffer, bytesWritten, bytesConverted
															- bytesWritten);
			bytesWritten += written;
			// TODO: set timer !!!
			// if(!sLine.isRunning()) TODO ???
			if (written < bytesConverted - bytesWritten) {
				if(DebugState.DEBUG_AUDIO)
						System.out.println("Could not write all data to SourceDataLine, try to sleep.");
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// TODO check if bytesWritten%4 != 0 could happen
		if(DebugState.DEBUG_AUDIO && bytesWritten %4 != 0)
			System.out.println("WARNING: bytesWritten %4 != 0");
		return bytesWritten / frameSizeInBytes;
	}
		
		
//	private void write(int off, int len) { 
//		stopped = false;
//		
//		sLine.start();
//		
//		lenInBytes = len * frameSizeInBytes;
//		framesToWrite = bufferSizeInFrames;
//		convertedFrames = 0;
//		bytesConverted = 0; // total number of convert bytes
//		converted = 0; // convert in one out loop execution
//		bytesWritten = 0; // total number of bytes written
//		written = 0; // written in one inner loop execution
//		bytesWInBuff = 0; // number of bytes written to the buffer in one outer loop
//
//		outerLoop: while (bytesConverted < lenInBytes) { // while not all samples
//														 // converted
//			// to bytes
//			framesToWrite = bufferSizeInFrames < (len - convertedFrames) ? bufferSizeInFrames
//																		: (len - convertedFrames);
//			converted = AudioUtil.floatsToBytes(floatBuffer, off + convertedFrames,
//												byteBuffer, 0, framesToWrite,
//												sampleSizeInBits);
//			bytesConverted += converted;
//			convertedFrames += converted / frameSizeInBytes;
//			bytesWInBuff = 0;
//			while (bytesWritten < bytesConverted) { // while not all converted
//				// bytes are played
//				if (stopped){
//					// TO DO Short Fade-Out
//					
//					break outerLoop;
//				}
//				written = sLine.write(byteBuffer, bytesWInBuff, bytesConverted
//																- bytesWritten);
//				// TO DO: timer setzen!!!
//				//if(!sLine.isRunning())
//				if (written < bytesConverted - bytesWritten) {
//					// System.out.println("!!!!!! >> SourceDataLine reject data,
//					// we try to sleep.");
//					try {
//						Thread.sleep(30);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				bytesWInBuff += written;
//				bytesWritten += written;
//			}
//		}
//		//	if (syncPeriod == -1 || syncPeriod < timer.getTimeMicros() - lastTime)
//		//		synchroniseTime(); // TO DO: check again
//					//return bytesWritten / frameSizeInBytes;
//	}
//	if(floatBuffer != null){
//		write(floatBufferOffset, floatsRemainingToWrite);
//	}
//	
//	floatBuffer = data;
//	write(off, len);
//	if(bytesWritten / frameSizeInBytes == len){
//		floatBuffer = null;
//	}
//	else{
//		floatBufferOffset = bytesWritten / frameSizeInBytes;
//		floatsRemainingToWrite = len - floatBufferOffset;
//		floatBufferOffset += off;
//	}
	

	public void drain() {
		sLine.drain();
	}

	public void flush() {
		sLine.flush();
		for (int i = 0; i < byteBuffer.length; i++) {
			byteBuffer[i] = 0;
		}
	}

	public void stop() {
		stopped = true;
		sLine.stop();
	}

	public boolean isRunning() {
		return sLine.isRunning();
	}

	private long syncPeriod = -1; // the milliseconds at that the time must be
	// actualised
	private long lastTime = 0; // remember the last time at that

	// synchroniseTime() was invoked

	/**
	 * Quest the SourceDataLine how many data was writen, calculate how many data was
	 * written since the last request
	 *  
	 */
	public void synchroniseTime() {
		timer.timePasSinceMarker(sLine.getMicrosecondPosition() - lastTime);
		// was macht sourceDataLine, wenn kurz mal keine Daten übermittelt werden?
		lastTime = timer.getTimeMicros();
	}

	/**
	 * 
	 * @return the timer used, a MSyncTimer
	 */
	public MTimer getTimer() {
		return timer;
	}

	/**
	 * @return
	 */
	public boolean isActive() {
		return sLine.isActive();
	}

	/**
	 * 
	 */
	public void start() {
		sLine.start();
		
	}

}