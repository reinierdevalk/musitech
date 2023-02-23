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
 * @author Nicolai Strauch
 *
 * Split an FloatInputStream whith determinated number of channels on streams, 
 * that use an determinate number of this channels.
 * This class is not an FloatInputStream, but she get Objects from the Type
 * FloatInputStream, whith a choice from the channels of the source.
 * 
 * Use getSplitterPart(...) to obtain the needed FloatInputStreams.
 * 
 */
public class FISChannelSplitter {

	MultiReaderRingBuffer mrRingBuff;
	
	public FISChannelSplitter(FloatInputStream inputStream)
	{
		this(inputStream, 8192, inputStream.getFormat().getChannels());
	}
	public FISChannelSplitter(FloatInputStream inputStream, int bufferLen)
	{
		this(inputStream, bufferLen, inputStream.getFormat().getChannels());
	}
	public FISChannelSplitter(FloatInputStream inputStream, int bufferLen, int numberOfMembers) 
	{
		mrRingBuff = new MultiReaderRingBuffer(inputStream, bufferLen, numberOfMembers);
	}

	/**
	 * Create an new FloatInputStream, that read only the channels listen
	 * in whithChannels, in the given order. 
	 * @param whithChannels - contain the indexes of the sourceChannel for the TargetChannel on the respective index
	 * 		So the channel i on the target will be the channel channelRepart[i] in the source.
	 * @return FloatInputStream - read only the channels listen in whithChannels
	 */
	public FloatInputStream getSplitterPart(int[] whithChannels)
	{
		SplitterPart part = new SplitterPart(whithChannels);
		mrRingBuff.addReader(part);
		return part;
	}
	/**
	 * Create an new FloatInputStream, that read only the channel request. 
	 * @param whithChannel - the channel to be read in the returned FloatInputStream 
	 * @return FloatInputStream - read only the channel request by whithChannels
	 */
	public FloatInputStream getSplitterPart(int whithChannel)
	{
		int[] whithChannels = {whithChannel};
		SplitterPart part = new SplitterPart(whithChannels);
		mrRingBuff.addReader(part);
		return part;
	}

	/**
	 * 
	 * @return FloatInputStream - the source of data
	 */
	public FloatInputStream getFloatInputStream()
	{
		return mrRingBuff.getFloatInputStream();
	}
	

	private class SplitterPart implements FloatInputStream{

		int[] channelRepart; // set for any channel at the targetarray an channel from the source. So the channel i on the target will be the channel channelRepart[i] in the source.
		int samplesRead=0; // total number of samples read

		public SplitterPart(int[] whithChannels)
		{
			channelRepart = whithChannels;
			
		}

		public void setFloatInputStream(FloatInputStream fis)
		{
			mrRingBuff.setFloatInputStream(fis);
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float)
		 */
		@Override
		public int read(float[][] data) throws IOException {
			return read(data, 0, data[0].length);
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float, int, int)
		 */
		@Override
		public int read(float[][] data, int start, int len) throws IOException {
			int didRead = mrRingBuff.read(data, start, len, this, channelRepart);
			if(didRead == 0)
			{
				mrRingBuff.load();
				didRead = mrRingBuff.read(data, start, len, this, channelRepart);
			}
			samplesRead += didRead;
			return didRead;
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
		 */
		@Override
		public AudioFormat getFormat() {
			return mrRingBuff.getFormat();
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
		 */
		@Override
		public long skip(long n) throws IOException {
			long didSkip = mrRingBuff.skip(n, this);
			if(didSkip == 0)
			{
				mrRingBuff.load();
				didSkip = mrRingBuff.skip(n, this);
			}
			return didSkip;
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
		 */
		@Override
		public void reset() throws IOException {
			mrRingBuff.reset();
		}
	
		/**
		 * Remove this reader from FISChannelSplitter.
		 * @return
		 */
		public boolean delete()
		{
			 return mrRingBuff.deleteMember(this);
		}
		
		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
		 */
		@Override
		public long remainingSamples() {
			return getFloatInputStream().remainingSamples()+getFloatInputStream().getPositionInSamples()-samplesRead;
		}

		/**
		 * 
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
		 */
		@Override
		public long getPositionInSamples() {
			return mrRingBuff.getPositionInSamples(this);
		}
	
		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
		 */
		@Override
		public void setPositionInSamples(long newPos) throws IOException {
			mrRingBuff.setPositionInSamples(newPos);
			samplesRead = (int) mrRingBuff.getPositionInSamples(this);
		}
		
		
	}


}
