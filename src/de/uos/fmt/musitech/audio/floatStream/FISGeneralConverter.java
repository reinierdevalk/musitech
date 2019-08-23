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
 * Das sollte lieber als Methode in AudioUtils sein, warum nochmal kapseln? 
 * 
 * Create a serie of FloatInputStream, so that the Data resultig is from iven
 * AudioFormat
 *
 * The Serie have the following order:
 *   source   inputStream (PCMFloatIS, or was is given by the
 * user. MAybe FloatMP3Adapter)
 *    SampleRateAdapter
 *    BitRateAdapter
 *    ChannelAdapter
 * 
 */
public class FISGeneralConverter implements FloatInputStream, FISReader {
	
	private FloatInputStream fis;
	private AudioFormat targetFormat;
	
//	/**
//	 * ATTENTION!
//	 *   FloatInputStream must be initialised!
//	 */
//	public FISGeneralConverter(){
//		this(null);
//	}
	
	public FISGeneralConverter(FloatInputStream inputStream)
	{
		this(inputStream, DefaultFormat);
	}

	public FISGeneralConverter(
		FloatInputStream inputStream,
		AudioFormat audioFormat) 
	{
		targetFormat = audioFormat;
		setFloatInputStream(inputStream);
	}
	
	public FISReader setFloatInputStream(FloatInputStream inputStream)
	{
		fis = inputStream;
		if(targetFormat.getSampleRate()!=fis.getFormat().getSampleRate())
			fis = new FISSampleRateConverter(fis, targetFormat.getSampleRate());
		if(targetFormat.getSampleSizeInBits()!=fis.getFormat().getSampleSizeInBits())
			fis = new FISSampleSizeConverter(fis, targetFormat.getSampleSizeInBits());
		if(targetFormat.getChannels()!=fis.getFormat().getChannels())
			fis = new FISChannelAdapter(fis, targetFormat.getChannels());
		// wie uhralte Stahlindustrie
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
		return fis.read(data, 0, data.length);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float[][], int, int)
	 */
	public int read(float[][] data, int start, int len) throws IOException {
		return fis.read(data, start, len);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		return targetFormat;
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
