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
 * Created on 28.06.2004
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class provide one mapping for any File.
 * For one File every time the MultiuseMappingProvider return the some
 * MultiuseMapping.
 * This contain a mapping, that only get samples at determinated position.
 * Works only with the PCM-Format.
 * @author Nicolai Strauch
 */
public class MultiuseMapping {
	
	private ByteBuffer source;
	
	private AudioFormat audioFormat; // format from the data coming in
	private int framesAvailable; // total of frames (samples) available in the file
	private boolean isBigEndian;
	private AudioFormat.Encoding encoding;
	private int channels;  
	private int sSizeBits; // sampleSizeInBits
	private byte[] sample; // load the bytes of a sample. sample.length = bytesPerSample
	private int frameSize;
	private float normalisationfaktor;
	
	private int offset = 44; // bytes to ignore at begin of source
	
	private MultiuseMapping(){
	}
	private MultiuseMapping(File file) throws UnsupportedAudioFileException, IOException{
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		if(aff.getType() != AudioFileFormat.Type.WAVE)
			throw new UnsupportedAudioFileException("Not PCM-WAVE.");
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		audioFormat = ais.getFormat();
	//	framesAvailable = (int) ais.getFrameLength();
		sSizeBits = audioFormat.getSampleSizeInBits();
		normalisationfaktor = 1 << (sSizeBits - 1);
		sample = new byte[sSizeBits / 8];
		isBigEndian = audioFormat.isBigEndian();
		encoding = audioFormat.getEncoding();
		channels = audioFormat.getChannels();
		frameSize = sample.length * channels;
	
		FileInputStream fis = new FileInputStream(file);
		FileChannel fileChannel = fis.getChannel();
		source = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		
		framesAvailable = (source.capacity()-offset)/frameSize;
	}
	
	private static Hashtable files = new Hashtable();
	
	public static MultiuseMapping MultiuseMappingProvider(File file) throws UnsupportedAudioFileException, IOException{
		Object o = null;
		Enumeration enumer = files.keys(); 
		while(enumer.hasMoreElements()){
			o = enumer.nextElement();
			if(file.equals(o))
				return (MultiuseMapping) files.get(o);
		}
		return new MultiuseMapping(file);
	}
	
	/**
	 * Read a single sample from the position pos in channel chan.
	 * Pos have to be a sampleposition. In the Mapping it is calculated
	 * as pos * framesize + header.
	 * 
	 * @param pos - frameposition
	 * @param chan - channel
	 * @param normalised - if samples have to be normalised into range of -1 1
	 * @return sample at the frameposition pos in channel chan
	 */
	public synchronized float getSample(int pos, int chan, boolean normalised){
		bytepos = calculateBytePos(pos) + chan * sample.length;
		if(isBigEndian){
			for(int i=sample.length-1; i>=0; i--){
				sample[i] = source.get(bytepos++);
			}
		}else{
			for(int i=0; i<sample.length; i++){
				sample[i] = source.get(bytepos++);
			}
		}

		if(encoding == AudioFormat.Encoding.PCM_SIGNED){	// by PCM_SIGNED the negativ samples must be prepared (example: by 16 bps, the bytes 11001011 and 00101110 must be written as integer: 11111111 11111111 11001011 00101110)
		   if(!isBigEndian){
			   if(sample[sample.length-1] < 0)	// if the biggest byte is <0, and so the sample is negativ
				   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
			   else res = 0;
		   }else{
			   if(sample[0] < 0)	// if the biggest byte is <0, and so the sample is negativ
				   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
			   else res = 0;
		   }
		}else res = 0;	

		// colect the sample from the bytes 
		res |= (0xff & sample[0]);
		if(sample.length>1){	// 16 bps or grater
			res |= (0xff & sample[1]) << 8;
			if(sample.length>2)	// 24 bps
				res |= (0xff & sample[2]) << 16;
		}

		
		if(!normalised){
			if (sSizeBits==8)
				res <<= 8;
			else if (sSizeBits==24)
				res /= 8;
			if (encoding == AudioFormat.Encoding.PCM_UNSIGNED)
				res -= 0x8000;
		}

		if(normalised) {  
			if (encoding == AudioFormat.Encoding.PCM_UNSIGNED)
				return (res / normalisationfaktor) - 1;
			return res / normalisationfaktor;
		}
		return res;
	}
	
	private int res;	// sample returned at getSample
	private int bytepos; // used to remember bytepos in getSample
//	final static int[] sub = { 0x80, 0x8000, 0x800000, 0x80000000 };
	
	/**
	 * Read len samples beginning at the position pos.
	 * Pos have to be a sampleposition. In the Mapping it is calculated
	 * as pos * framesize + header.
	 * ArrayIndesOutOfBoundException if start+len > data[0].length.
	 * If pos greater than data disponible, return -1.
	 * @param data - in that the data is writting in
	 * @param start - first position in that is writed in array
	 * @param len - maximal number of samples read
	 * @param pos - sampleposition at that begin to read in source
	 * @param normalised - if samples have to be normalised into range of -1 1
	 * @return number of samples read.
	 */
	public synchronized int getSamples(float[][] data, int start, int len, int pos, boolean normalised){
		pos = calculateBytePos(pos);		
//System.out.println("Reading by position: "+pos);
		int t_i = 0; // target index (float-array)
		int lim = source.limit() - frameSize;
		if(pos>=lim) 
			return -1;
		for (; pos < lim && t_i<len; t_i++) {
			for (int channel = 0; channel < channels; channel++) {
				
				if(encoding == AudioFormat.Encoding.PCM_SIGNED){	// by PCM_SIGNED the negativ samples must be prepared (example: by 16 bps, the bytes 11001011 and 00101110 must be written as integer: 11111111 11111111 11001011 00101110)
				   if(!isBigEndian){
					   if(source.get(pos + sample.length - 1) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }else{
					   if(source.get(pos) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }
				}else res = 0;
				
				// the bytes are put in the sample
				if (isBigEndian) {
					if (sample.length > 2) // 24 bps			  
						res |= (0xff & source.get(pos++)) << 16;
					if (sample.length > 1)
						res |= (0xff & source.get(pos++)) << 8;
					res |= (0xff & source.get(pos++));
				} else {
					res |= (0xff & source.get(pos++));
					if (sample.length > 1) { // 16 bps or grater
						res |= (0xff & source.get(pos++)) << 8;
						if (sample.length > 2) // 24 bps
							res |= (0xff & source.get(pos++)) << 16;
					}
				}
				if (sSizeBits==8){
					res <<= 8;
				}
				data[channel][start + t_i] = res;
				if (sSizeBits==24){ 
					data[channel][start + t_i] /= 8;
				}
			}
		}
		if (encoding == AudioFormat.Encoding.PCM_UNSIGNED) {
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[i].length; j++)
//					data[i][j] -= sub[sample.length - 1];
					data[i][j] -= 0x8000;
		}
		
		if (normalised)
		{
			for(int i=0; i<data.length; i++)
				for(int j=start; j<t_i; j++)
					data[i][j] /= normalisationfaktor;
		}
		
		return t_i;
	}

	
	/**
	 * Gets a sampleposition, and returns the position in bytes in the source.
	 * Return samplePosition * framesize + offset.
	 * @param samplePos
	 * @return
	 */
	private int calculateBytePos(int samplePos){
		return (samplePos * frameSize) + offset;
	}

	/**
 	* @return
 	*/
	public AudioFormat getAudioFormat() {
		int bitDiffFaktor = 16 / sSizeBits;
		AudioFormat out =
			new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				audioFormat.getSampleRate(),
				16,
				audioFormat.getChannels(),
				audioFormat.getFrameSize() * bitDiffFaktor,
				audioFormat.getFrameRate(),
				true);
		return out;
	}
	
	/**
	 * @return
	 */
	public int remainingSamples() {
	    if(source.position()<offset)
	        return framesAvailable();
		return source.remaining()/frameSize;
	}
	
	/**
	 * Creates a new PositionableFIS whith the Mapping in this MultiuseMapping.
	 * @return
	 */
	public PositionableFIS getPositionableFIS(){
		return new PCMFileFloatIS(this);
	}
	
	/**
	 * Creates a new FloatPreviewReader whith the Mapping in this MultiuseMapping.
	 * @return
	 */
	public FloatPreviewReader getFloatPreviewReader(){
		return new PCMFPreviewReader(this);
	}

	/**
	 * Get the number of bytes ignored at begin from sourcefile.
	 * This offset is allways added to the byteposition calculated from the
	 * sampleposition by getSample() and getSamples().
	 * Default 44 from the PCM-Header.
	 * @return
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set the number of bytes ignored at begin from sourcefile.
	 * This offset is allways added to the byteposition calculated from the
	 * sampleposition by getSample() and getSamples().
	 * In default it have to be 44, from the PCM-Header.
	 * Please be carefull by changing this value, allways add the header
	 * (44 bytes), ignore them only if realy not needed or different from the
	 * default.
	 * @param i
	 */
	public synchronized void setOffset(int i) {
		offset = i;
	}
	
	/**
	 * total number of frames available, includin always readed frames.
	 * @return
	 */
	public int framesAvailable(){
		return framesAvailable;
	}

}
