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

import javax.sound.sampled.AudioFormat;


/**
 * Combines the functions of a multiplexer and a mixer.
 * 
 * @author Nicolai Strauch
 */ 
// Decide between FISChannelMixer and FISChannelSplitter // not more
// @@TODO: mixer und Multiplexer nicht exakt.
// * Problem on Mixer-part: maybe one Channel of the Target will not get as 
// * many Channels from the source as the other, precisely, 
// * if(TargetChannels%SourceChannels!=0). 
// * All channels are divided by the some factor, so the affected Channel will be
// * sound laughter than the other.
// * 
// * Problem on the Multiplexer-part: maybe an channel of the Source don't will be distributed on so much
// * Channels in the Target, as the other Channels. Precisely, if (TargetChannels%
// * SourceChannels!=0). The Affected Channel don't will sound so laught than the
// * other.
//
public class FISChannelAdapter implements FloatInputStream, FISReader {
	
	private byte sourceChannels;
	private byte targetChannels;
	private FloatInputStream fis;
	private float[] channelFactors;
	
//	/**
//	 * Default constructor. NOTE: the field floatInputStream must be initialised 
//	 * before using the adapter.
//	 */
//	public FISChannelAdapter(){
//		this(null);
//	}
	
	/**
	 * Contruct an adapter for a given input stream. 
	 * @param inputStream The input stream to read from.
	 */
	public FISChannelAdapter(FloatInputStream inputStream)
	{
		this(inputStream, DefaultFormat.getChannels());
	}
	
	/**
	 * Contruct an adapter for a given input stream with a given number 
	 * of output channels.
	 *  
	 * @param inputStream The stream to read from.
	 * @param targetChannels The channels.
	 */
	public FISChannelAdapter(FloatInputStream inputStream, int targetChannels)
	{
		this.targetChannels = (byte) targetChannels;
		setFloatInputStream(inputStream);
	}
	
	public FISChannelAdapter(FloatInputStream inputStream, int targetChannels, float[] channelfactors)
	{
		this(inputStream, targetChannels);
		setChannelFactors(channelfactors);
	}
	
	public void setChannelFactors(float[] channelfactors)
	{
		this.channelFactors = channelfactors;
	}

	
	private void setTargetChannels(int targetChannels) 
	{
		this.targetChannels = (byte) targetChannels;
	}
	
	public FISReader setFloatInputStream(FloatInputStream inputStream)
	{
		fis = inputStream;
		sourceChannels = (byte) fis.getFormat().getChannels();
		return this;
	}
	public FloatInputStream getFloatInputStream()
	{
		return fis;
	}
	
	
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float[][])
	 */
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float[][], int, int)
	 */
	public int read(float[][] data, int start, int len) throws IOException {
		if(targetChannels==sourceChannels)
			return fis.read(data, start, len);
		float[][] source = new float[sourceChannels][len];
		int out = fis.read(source, 0, len);
		if(targetChannels<sourceChannels)
		{
//System.out.println("ChannelAdapter: enter mixer. <"+targetChannels+"> X <"+sourceChannels+">");
			if(channelFactors!=null) mixerSpezified(source, data, start);
			else mixer(source, data, start);
		}
		else if(targetChannels>sourceChannels)
		{
//System.out.println("ChannelAdapter: enter multiplexer. <"+targetChannels+"> X <"+sourceChannels+">");
			multiplexer(source, data, start);
		}
		return out;
	}
	
	/**
	 * Multiplexer, distribute sourceChannels by targetChannels.
	 * If (TargetChannels%SourceChannels)!=0, the last Channel of source will
	 * get less Channels in the target than the other Channels
	 */
	private void multiplexer(float[][] source, float[][] data, int start) {
		int tar_by_src = targetChannels/sourceChannels;
		for(int i=0; i<source[0].length; i++)
			for(int srcI=0, tarI=0; srcI<sourceChannels; srcI++)
				for(int j=0; j<tar_by_src&&tarI<targetChannels; j++, tarI++)
					data[tarI][start+i] = source[srcI][i];
	}
	/**
	 * Mixer, mix sourceChannels by targetChannels. 
	 * If (TargetChannels%SourceChannels)!=0, 
	 * the last Channel of target will get fewer Channels from the source
	 * than the other Channel */
	private void mixer(float[][] source, float[][] data, int start) {
		int src_by_tar = sourceChannels/targetChannels;
		for(int i=0; i<source[0].length; i++)
			for(int tarI=0, srcI=0; tarI<targetChannels; tarI++)
			{
				for(int j=0; j<src_by_tar&&srcI<sourceChannels; j++, srcI++)
					data[tarI][start+i] += source[srcI][i];
				data[tarI][start+i] /= src_by_tar;
			}
	}
	/**
	 * Don't divide mixed Channels by the normal anti-overflow factors, use
	 * user provided factors instead.
	 * @param source
	 * @param data
	 * @param start
	 */
	private void mixerSpezified(
		float[][] source,
		float[][] data,
		int start) 
	{
		int src_by_tar = sourceChannels/targetChannels;
		for(int i=0; i<source.length; i++)
			for(int tarI=0, srcI=0; tarI<targetChannels; tarI++)
				for(int j=0; j<src_by_tar&&srcI<targetChannels; j++, srcI++)
					data[tarI][start+i] += source[srcI][i]*channelFactors[srcI];
	}

	/**
	 * Now it make nothing...   it must be implementet first!
	 * 
	 * Make the same as the method before, but distribute the channels whith
	 * more justice.
	 */
//	private void trueMultiplexer(float[][] source, float[][] data, int start) {
// @@: uncorrect code
//		int tar_by_src = targetChannels/sourceChannels;
//		for(int i=0; i<source.length; i++)
//			for(int srcI=0, tarI=0; srcI<sourceChannels; srcI++)
//				for(int j=0; j<tar_by_src&&tarI<targetChannels; j++, tarI++)
//					data[tarI][start+i] = source[srcI][i];
//	}
	
	
	
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		AudioFormat in = fis.getFormat();
		AudioFormat out = new AudioFormat(in.getEncoding(), in.getSampleRate(), in.getSampleSizeInBits(), targetChannels, (in.getSampleSizeInBits()/8)*targetChannels, in.getFrameRate(), in.isBigEndian());
		return out;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return fis.skip(n);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
		fis.reset();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	public long remainingSamples() {
		return fis.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public long getPositionInSamples() {
		return fis.getPositionInSamples();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public void setPositionInSamples(long newPos) throws IOException {
		fis.setPositionInSamples(newPos);
	}


}
