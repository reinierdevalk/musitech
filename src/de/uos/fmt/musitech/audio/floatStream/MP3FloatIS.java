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
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import de.uos.fmt.musitech.audio.mp3Decoder.FloatBuffer;

/**
 * 
 * @author Nicolai Strauch
 */
public class MP3FloatIS implements FloatInputStream {

	private String sourceFormat;

	private boolean normalised;

	private Bitstream bitstream;

	//private Header header;
	private float[][] buffer;
	
	private short[] sBuffer;

	private int bufferIndex;

	// position of the first sample not read in the buffer
	private int bufferLimit; // position at that the buffer have data

	private Decoder decoder;

	private AudioFormat audioFormat;

	private FloatBuffer obuff;

	private float srcFrameSize; // bytes per mp3-frame

	private float trgFrameSize; // samples get out per mp3-frame

	private boolean isLayerI;

	private InputStream inputStream;

	private int totalAvailableBytes; // bytes available in InputStream at begin

	private int samplesRead; // total number of samples read by read(...)

	public MP3FloatIS(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
		this.inputStream = inputStream;
		totalAvailableBytes = inputStream.available();
		initialiseMP3Decoder();
		if (!getData()) // initialisiert decoder, füllt buffer
			throw new UnsupportedAudioFileException("Probably not an mp3-File.");
		if (audioFormat == null) { 
			audioFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, 
					decoder.getOutputFrequency(), // sampleRate
					16, // bitPerSample
					decoder.getOutputChannels(), // channels
					decoder.getOutputChannels() * 2, // frameSize
					decoder.getOutputFrequency(), // frameRate
					true); // bigEndian);
			System.out.println("Mp3-format: <" + header + ">");
			System.out.println("AudioFormat in conversion for PCM: <" + audioFormat + ">");
		}
	}
	
	synchronized void initialiseMP3Decoder(){
		bitstream = new Bitstream(inputStream);
		decoder = new Decoder();
		obuff = null;
//		framesInMap = 0;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][],
	 *      int, int)
	 */
	public synchronized int read(float[][] data, int start, int len) throws IOException {
	
		if (buffer == null) {
			getData();
			if (buffer == null) {
				if(inputStream.available()<=0){
					System.out.println("MP3FloatIS.read(): inputStream ending, return -1");
					return -1;
				}
				System.out.println("MP3FloatIS.read(): getData() run, buffer keep null, no data load. Returning 0.");
				//return -1;
				return 0;
			}
		}
		len = samplesAvailableWithoutBlocking() < len ? samplesAvailableWithoutBlocking() : len;
		for (int i = 0; i < buffer.length && i < data.length; i++){
			System.arraycopy(buffer[i], bufferIndex, data[i], start, len);
		}
		bufferIndex += len;

		assert bufferIndex >= 0;

		if (bufferIndex >= bufferLimit)
			buffer = null;

		if (normalised) {
			float f = 1 << (audioFormat.getSampleSizeInBits() - 1);
			for (int i = 0; i < data.length; i++)
				for (int j = start; j < len; j++)
					data[i][j] /= f;
		}
		samplesRead += len;
		return len;
	}

	private Header header;
	
	/**
	 * Do not reset Buffers, and do not close bitstream.
	 * @return
	 */
	private synchronized boolean loadHeader(){
		try {
			header = bitstream.readFrame();
			if (header == null){
				System.out.println("MP3FloatIS.chargeHeader(): bitstream.readFrame() return null as header, getData() returning false.");
				return false;
			}
			// extract data from header
			srcFrameSize = header.framesize;
			isLayerI = header.layer() == 1;
			trgFrameSize = isLayerI ? 384 : 1152;
			//setSourceMapValues();
		} catch (BitstreamException e) {
			e.printStackTrace();
			buffer = null;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			long sr = samplesRead;
			try {
				reset();
				skip(sr);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return true;
	}
	

	private synchronized boolean decodeAndLoadData(){
		try{
			if (obuff == null) {
				obuff = new FloatBuffer(header.frequency(), (header.mode() == Header.SINGLE_CHANNEL ? 1 : 2));
		//		obuff = new SampleBuffer(header.frequency(), (header.mode() == Header.SINGLE_CHANNEL ? 1 : 2));
				decoder.setOutputBuffer(obuff);
			}
			obuff = (FloatBuffer) decoder.decodeFrame(header, bitstream);
			buffer = obuff.getBuffer();
			
//			buffer = new float[(header.mode() == Header.SINGLE_CHANNEL ? 1 : 2)][sBuffer.length/2];
//			for(int i=0, si=0; si<sBuffer.length&&i<buffer[0].length; i++, si+=buffer.length){
//				for(int j=0; j<buffer.length; j++){
//					buffer[j][i] = sBuffer[si+j];
//				}
//			}
			
			bufferIndex = 0;
			bufferLimit = obuff.getBufferLength();
			//bufferLimit = obuff.getBufferLength()/2;
			bitstream.closeFrame();
		} catch (DecoderException e) {
			e.printStackTrace();
			buffer = null;
			return false;
		}catch (ArrayIndexOutOfBoundsException aioobe){
			aioobe.printStackTrace();
			//System.err.println("ArrayIndexETC.");
			long sr = samplesRead;
			try {
				reset();
				skip(sr);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
			long sr = samplesRead;
			try {
				reset();
				skip(sr);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Charge the local buffer whith audiodata obtained by the mp3-decoder.
	 * @return
	 */
	protected synchronized boolean getData() {
		if (!loadHeader()){
			//System.out.println("MP3FloatIS.getData(): bitstream.readFrame() return null as header, getData() returning false.");
			return false;
		}
		if (!decodeAndLoadData()){
			return false;
		}
		return true;
	}
	
	/**
	 * Skip n samples, or in maximum so many are available 
	 * whithout reload buffer from source. 
	 * If no data is available, load header of the next mp3-frame.
	 * If n is greater then the number of samples available in this frame,
	 * skip the number of samples available in the frame, and unread the frame,
	 * skipping so many samples was available in the mp3-frame.
	 * If the mp3-frame contains more samples than n, the data from frame
	 * is decoded, and n samples will be skipped.
	 * @param n
	 * @return samples skip
	 * @throws IOException
	 */
	private synchronized long skipPossible(long n) throws IOException {
	
		if (buffer == null) {
			if (	!loadHeader() //||
//					!decodeAndLoadData() ||
	//				buffer == null 
			){
				if(inputStream.available()<=0){
					System.out.println("MP3FloatIS.skipPossible(): header not load: inputstream do not have more bytes available, end reached, return -1.");
					return -1;
				}
				System.out.println("MP3FloatIS.skipPossible(): header not load: problem occure, do not skip data, return 0.");
				return 0;
			 }
			if(n<trgFrameSize){
				if(!decodeAndLoadData() || buffer == null){
					if(inputStream.available()<=0){
						System.out.println("MP3FloatIS.skipPossible(): data not load: inputstream do not have more bytes available, end reached, return -1.");
						return -1;
					}
					System.out.println("MP3FloatIS.skipPossible(): data not load: problem occure, do not skip data, return 0.");
					return 0;
				}
			}
			else{
				bitstream.closeFrame();
				n = (long) trgFrameSize;
			}
		}
		else
			n = samplesAvailableWithoutBlocking() < n ? samplesAvailableWithoutBlocking() : n;
			
		bufferIndex += n;

		assert bufferIndex >= 0;

		if (bufferIndex >= bufferLimit)
			buffer = null;

		samplesRead += n;
		return n;
	}

	/**
	 * Skip n samples. 
	 * Only do not skip all n samples, if end of stream is reached,
	 * or any exception occure.
	 * @param n Samples to be skipped
	 * @return long Samples skipped
	 */
	public synchronized long skip(long n) throws IOException {
		int sk = 0;
		int skipped = 0;
		while(n>0&&sk>=0){
			sk = (int)skipPossible(n);
			if(sk>0){
				n -= sk;
				skipped += sk;
			}
		}
		return skipped;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		AudioFormat in = audioFormat;
		AudioFormat out = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, in.getSampleRate(), normalised?1:in.getSampleSizeInBits(), in.getChannels(), in.getFrameSize(), in.getFrameRate(), in.isBigEndian());
		return out;
	}

	/**
	 * Reset read-position to the marker mark.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public synchronized void reset() throws IOException {
//		System.out.println("MP3FloatIS.reset() is starting");
		inputStream.reset();
		initialiseMP3Decoder();
		buffer = null;
		bufferIndex = 0;
		bufferLimit = 0;
		samplesRead = 0;
	}

	/**
	 * Add to super.toString() the format of the mp3-data read
	 */
	public String toString() {
		return super.toString() + " SourceFormat contained: <" + sourceFormat + ">";
	}

	/**
	 * 
	 * @return the mp3-format specified by the first frame of the given Stream
	 */
	public String sourceFormat() {
		return sourceFormat;
	}

	/**
	 * @return true if data is normalised in to the range of -1 to 1.
	 */
	public boolean isNormalised() {
		return normalised;
	}

	/**
	 * @param b -
	 *            true if the data has to be normalised in to the range of -1 to
	 *            1.
	 */
	public void setNormalised(boolean b) {
		normalised = b;
	}


	/**
	 * @return
	 */
	protected InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	public long remainingSamples() {
		if(sampleLength<0)
				countSamples();
		return sampleLength;
	}
	
	private int sampleLength = -1;
	
	/**
	 * Try to count the number of samples available from mp3.
	 * Return the value count by getSamplesRead() + remainingSamples()
	 */
	public void countSamples() {
		sampleLength = 0;
		int sRead = getSamplesRead();
		while(loadHeader()){
			sampleLength += trgFrameSize;
			bitstream.closeFrame();
		}
		try {
			reset();
			skip(sRead);
		} catch (IOException e) {
			System.err.println("Position from mp3-stream wrong");
			e.printStackTrace();
		}
		System.out.println("MP3FloatIS.countSamples() have count "+sampleLength+" samples.");
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public long getPositionInSamples() {
		return samplesRead;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
	 */
	public void setPositionInSamples(long newPos) throws IOException {
		if(newPos==getSamplesRead())
			return;
		if(newPos<getSamplesRead())
			reset();
		skip(newPos-getSamplesRead());
	}

	/**
	 * @return
	 */
	protected int getBufferIndex() {
		return bufferIndex;
	}

	/**
	 * if i > bufferLimit, use a kind of skipalgoritmus to reach 
	 * free bufferindexposition
	 * this method skip i samples
	 * @param i
	 */
	protected void setBufferIndex(int i) {
		while (i > bufferLimit) {
			i -= (bufferLimit-bufferIndex);
			getData();
		}
		bufferIndex = i;
	}

	/**
	 * @return
	 */
	protected int getSamplesRead() {
		return samplesRead;
	}

	/**
	 * @param i
	 */
	protected void setSamplesRead(int i) {
		samplesRead = i;
	}
	
	/**
	 * Samples available whithout have to decode more frames,
	 * also the samples available in buffer. 
	 * @return
	 */
	public int samplesAvailableWithoutBlocking(){
		return bufferLimit - bufferIndex;
	}

}