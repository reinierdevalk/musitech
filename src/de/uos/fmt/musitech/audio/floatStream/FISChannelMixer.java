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

import de.uos.fmt.musitech.audio.AudioUtil;

/**
 * @author Nicolai Strauch
 *
 * Get a FloatInputStream whith a number of channels,
 * and output another number of channels.
 * All input-channels will be added on every output-channel, by multiplication whith an factor,
 * from a gaintable.
 * 
 * <img src="FISChannelMixer.gif">
 * 
 */
public class FISChannelMixer  implements FloatInputStream, FISReader {
	
	private FloatInputStream sourceStream;
	private int sourceChannels;
	private float[][] gainTable;	// targetChannels X sourceChannles
	private int targetChannels;
	private float[][] buffer;
	
	private int bufferSize;
	
	/**
	 * ATTENTION!
	 *   FloatInputStream must be initialised!
	 */
	public FISChannelMixer(){
		this(null);
	}
	
	public FISChannelMixer(FloatInputStream fis)
	{
		this(fis, DefaultFormat.getChannels(), 8192);
	}
	public FISChannelMixer(FloatInputStream fis, int channels)
	{
		this(fis, channels, 8192);
	}
	/**
	 * 
	 * @param fis - datafont
	 * @param channels - channels from the output
	 * @param bufferSize - maximum of available samples at one read()
	 */
	public FISChannelMixer(FloatInputStream fis, int channels, int bufferSize)
	{
		sourceStream = fis;
		targetChannels = channels;
		this.bufferSize = bufferSize;
		if(fis != null)
			update();
	}
	/**
	 * @param i
	 */
	public void setBufferSize(int i)
	{
		bufferSize = i;
		buffer = new float[sourceChannels][bufferSize];
	}
	public int getBufferSize(){
		return buffer[0].length;
	}
	/**
	 * @param fis
	 */
	public FISReader setFloatInputStream(FloatInputStream fis)
	{
		sourceStream = fis;
		update();
		return this;
	}
	public synchronized void update(){
		sourceChannels = sourceStream.getFormat().getChannels();	
		if(buffer==null || buffer.length!=sourceChannels)
				buffer = new float[sourceChannels][bufferSize];
		initialiseGainTable(); 
	}
	
	public synchronized void setTargetChannels(int channels)
	{
		targetChannels = channels;
		initialiseGainTable();
	}

	/**
	 * Initialises the GainTable. Tries to copy the old values into 
	 * the new table, which may lead to changed channel-value associations.
	 */
	private void initialiseGainTable()
	{
		if ( gainTable != null && 
		      targetChannels == gainTable.length && 
		      sourceChannels == gainTable[0].length)
		      return;
			float[][] tmp = new float[targetChannels][sourceChannels];
			AudioUtil.fillUp(tmp, 1.0f/sourceChannels);	// fill the matrix whith values, that will minimize the gain result by the sum of all sourceChannel on one targetChannel
//			for(int i=0; i<tmp.length&&i<gainTable.length; i++)
//				System.arraycopy(gainTable[i], 0, tmp[1], 0, gainTable[i].length<tmp[i].length?gainTable[i].length:tmp[i].length);
			gainTable = tmp;		 
	}
	
	
	public float[][] getGainTable(){
		return gainTable;
	}
	
	/**
	 * The gainTable must have the format targetChannels X sourceChannels
	 * If it gets 4 channels, and get 2 channels out, the form will be:
	 * gainTable[4][2] = {{f, f, f, f}, {f, f, f, f}}
	 * for any channel of the Target in any channel of the source the user have
	 * to define an gainvalue.
	 * See the graphic in the class-documentation.
	 * @param newGainTable
	 */
	public synchronized void setGainTable(float[][] newGainTable){
		if(newGainTable.length!=targetChannels || newGainTable[0].length!=sourceChannels)
			throw new IllegalArgumentException("The gainTable must be in the format targetChannels X sourceChannels");
		gainTable = newGainTable;
	}
	public int gatTargetChannels(){
		return targetChannels;
	}
	public int getSourceChannels(){
		return sourceChannels;
	}
	
	
	public int read(float[][] data) throws IOException
	{
		return read(data, 0, data[0].length);
	}
	/**
	 * Can not read more than "getBufferSize()" Samples at time.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	public synchronized int read(float[][] data, int start, int len) throws IOException
	{
		int available = sourceStream.read(buffer, 0, len);
		mix(data, start, available);
		return available;
	}
	private void mix(float[][] data, int start, int len)
	{
		AudioUtil.fillUp(data, start, len, 0.0f);	// do not add new values over the old remaining (if we do not make this, an echo will be occure)
		for(int tCh=0; tCh<data.length; tCh++)	// targetChannels
			for(int sa=0; sa<len; sa++)	// samples
				for(int sCh=0; sCh<buffer.length; sCh++)	// sourceChannels
					data[tCh][sa+start] += (buffer[sCh][sa] * gainTable[tCh][sCh]);  
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat()
	{
		AudioFormat in = sourceStream.getFormat();
		return new AudioFormat(
			in.getEncoding(),
			in.getSampleRate(),
			in.getSampleSizeInBits(),
			targetChannels,
			in.getSampleSizeInBits()/8*targetChannels,
			in.getFrameRate(),
			in.isBigEndian());
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException
	{
		return sourceStream.skip(n);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException
	{
		sourceStream.reset();		
	}

	public FloatInputStream getFloatInputStream() {
		return sourceStream;
	}
	
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	public long remainingSamples() {
		return sourceStream.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public long getPositionInSamples() {
		return sourceStream.getPositionInSamples();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public void setPositionInSamples(long newPos) throws IOException {
		sourceStream.setPositionInSamples(newPos);
	}
	
	
}
