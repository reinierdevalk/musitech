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
 * Created on 18.02.2004
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * This class combines FloatInputStream and FloatPreviewReader, so that 
 * only one Mapping is used.
 * 
 * @author Nicolai Strauch 
 */
public class PCMFileFloatIS implements PositionableFIS {
	
    /** the source is also used by */
	MultiuseMapping source;
	
	/** normalisation flag */
	private boolean normalised;
	
	/**
	 * @param file
	 */
	public PCMFileFloatIS(File file) throws UnsupportedAudioFileException, IOException {
		setSource(MultiuseMapping.MultiuseMappingProvider(file));
	}
	
	PCMFileFloatIS(MultiuseMapping mm){
		setSource(mm);
	}
	
	private void setSource(MultiuseMapping mm){
		source = mm;
		totalLength = source.framesAvailable();
	}

	private int position; // sample at that the next read begin
	private int totalLength;	// length of samples in source

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	@Override
	public void position(int n) throws IOException {
		position = n;		
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position()
	 */
	@Override
	public int position() {
		return position;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		int read = source.getSamples(data, start, len, position, normalised);
		position += read;
		return read;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		AudioFormat in = source.getAudioFormat();
		AudioFormat out = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, in.getSampleRate(), normalised?1:in.getSampleSizeInBits(), in.getChannels(), in.getFrameSize(), in.getFrameRate(), in.isBigEndian());
		return out;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		int pos = position;
		position((int) (position+n));
		return pos-position();
	}

	/**
	 * The some as position(0)
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		position(0);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return totalLength - position;
	}

	/**
	 * The some as position()
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return position();
	}

	/**
	 * The some as position((int) newPos);
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		position((int) newPos);
	}

	/**
	 * @return the source MultiuseMapping
	 */
	public MultiuseMapping getMultiuseMapping() {
		return source;
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getFloatPreviewReader()
	 */
	@Override
	public FloatPreviewReader getFloatPreviewReader() {
		return source.getFloatPreviewReader();
	}
	
	/**
	 * Create a new instance of PCMFileFloatIS that reads from some MultiuseMapping.
	 * @return
	 */
	public PCMFileFloatIS getPCMFileFloatIS() {
		return new PCMFileFloatIS(getMultiuseMapping());
	}
	

	/**
	 * @return true if samples get out are normalised to range -1 1
	 */
	public boolean isNormalised() {
		return normalised;
	}

	/**
	 * Set if the samples get out have to be in the range -1 1.
	 * @param b - true to normalise range to -1 1
	 */
	public void setNormalised(boolean b) {
		normalised = b;
	}
	
	/**
	 * Number of samples disponible whithout return -1 in read(...)
	 * @return
	 */
	public int available(){
		return totalLength;
	}

}
