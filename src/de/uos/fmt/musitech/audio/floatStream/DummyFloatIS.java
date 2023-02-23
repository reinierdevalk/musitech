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
 * File DummyFloatStream.java
 * Created on 06.05.2003
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.utility.DebugState;

/**
 * This FloatStream does nothing, it just returns the requested lenth argument.
 * @author tweyde
 */
public class DummyFloatIS implements FloatInputStream, DebugState, FISReader, FloatPreviewReader, PositionableFIS {

//	public final static int WRITE_ZEROS = 0;
//	public final static int STREAM_END = 1;
//	public final static int RETURN_ZERO = 2;
//	public final static int NOTHING = 3;
//
//	private int state = NOTHING; 
//	
//	public DummyFloatStream(int state){
//		this.state = state;
//	}
//	public DummyFloatStream(){
//	}

	/** 
	 * read Does not really do anything.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	@Override
	public int read(float[][] data) throws IOException {
//		switch(state){
//			case WRITE_ZEROS :
//			case STREAM_END :
//			case RETURN_ZERO :
//			case NOTHING : return data[0].length;
//		}
		return data[0].length;
	}

	/** 
	 * read Does not really do anything.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		return len;
	}

	/** 
	 * getFormat returns the default format.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return FloatInputStream.DefaultFormat;
	}

	/** 
	 * skip Does not really do anything.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return n;
	}

	/** 
	 * reset Does not really do anything.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
	}

	/**
	 * Do nothing.
	 * So it not will be losed.
	 */
	@Override
	public FISReader setFloatInputStream(FloatInputStream fis) {
		return this;
	}

	/** 
	 * @return null
	 */
	@Override
	public FloatInputStream getFloatInputStream() {
		return null;
	}

	
	/**
	 * @return Long.MAX_VALUE
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return Long.MAX_VALUE;
	}

	/**
	 * @return 0
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return 0;
	}
	
	/**
	 * @param newPos ignored
	 * @throws IOException
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][])
	 */
	@Override
	public int previewRead(float[][] data) {
		return data[0].length;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int, int)
	 */
	@Override
	public int previewRead(float[][] data, int start, int len) {
		return len;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int)
	 */
	@Override
	public int previewRead(float[][] data, int firstSampleToRead) {
		return data[0].length;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int, int, int)
	 */
	@Override
	public int previewRead(float[][] data, int start, int len, int firstSampleToRead) {
		return len;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setSampleRateRatio(float)
	 */
	@Override
	public void setSampleRateRatio(float s) {
		
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getSampleRateRatio()
	 */
	@Override
	public float getSampleRateRatio() {
		return 1;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setAllChannels()
	 */
	@Override
	public int setAllChannels() {
		return channelnumber;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setChannels(int[])
	 */
	@Override
	public boolean setChannels(int[] chan) {
		channelnumber = chan.length;
		return false;
	}
	private int channelnumber = 1; 

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#channelsDisponible()
	 */
	@Override
	public int channelsDisponible() {
		return channelnumber;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getPositionableFIS()
	 */
	@Override
	public PositionableFIS getPositionableFIS() {
		return this;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#position(int)
	 */
	@Override
	public void position(int n) throws IOException {
		
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#position()
	 */
	@Override
	public int position() {
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#available()
	 */
	@Override
	public int available() {
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#availableMikroseconds()
	 */
	@Override
	public long availableMikroseconds() {
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#stopPreviewRead()
	 */
	@Override
	public boolean stopPreviewRead() {
		return false;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getFloatPreviewReader()
	 */
	@Override
	public FloatPreviewReader getFloatPreviewReader() {
		return this;
	}
	


}
