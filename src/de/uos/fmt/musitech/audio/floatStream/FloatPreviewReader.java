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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;


/**
 * PrewievReader delivers a sparse selection of samples 
 * from a larger part of data for visual diaplay.
 * 
 * @author Nicolai Strauch
 * 
 * TODO: schön wenn man das extend vermeiden könnte.
 * wie aber weiter zugriff auf available() und andere Methoden behalten,
 * die in FloatInputStream und hier von nöten sind?
 */
public interface FloatPreviewReader {

	/**
	 * the some as previewRead(data, 0, data[0].length)
	 * @param data
	 * @return	number of samples written into data
	 */
	public int previewRead(float[][] data);
	
	/**
	 * Fast implementation to get very short selection of samples 
	 * from a larger part of data.
	 * The SampleRate adaptation or SampleExtraction is very simple,
	 * not for a good sound, only for display.	
	 * 
	 * Data has to be in the range of -1 to 1.
	 * 
	 * If not enogth data was available, the remaining space in data[][] is
	 * filled whith zeros. 
	 *  
	 * @param data - array in that the samples will be written
	 * @param start - first index in data at that samples will be written
	 * @param len - samples to write into data
	 * @return number of samples written into data
	 */
	public int previewRead(float[][] data, int start, int len);
	
	/**
	 * Evoke previewRead(data, 0, data[0].length, firstSampleToRead.
	 */
	public int previewRead(float[][] data, int firstSampleToRead);
	
	/**
	 * Fast implementation to get very short selection of samples 
	 * from a larger part of data.
	 * The SampleRate adaptation or SampleExtraction is very simple,
	 * not for a good sound, only for display.	
	 * 
	 * Data has to be in the range of -1 to 1.
	 * 
	 * If not enogth data was available, the remaining space in data[][] is
	 * filled whith zeros. 
	 *  
	 * @param data - array in that the samples will be written
	 * @param start - first index in data at that samples will be written
	 * @param len - samples to write into data
	 * @param firstSampleToRead - first sampleposition to be read from source
	 * @return number of samples written into data
	 */
	public int previewRead(float[][] data, int start, int len, int firstSampleToRead);
	
	/**
	 * Set ratio of incoming samples per outgoing samples.
	 * @param s
	 */
	public void setSampleRateRatio(float s);
	
	/**
	 * Get ratio of incoming samples per outgoing samples.
	 * @return
	 */
	public float getSampleRateRatio();
	
	/**
	 * Only relevant for the method previewRead(...)
	 * Other read-methods get all channels incoming out.
	 * 
	 * set channels to get all channels available in source by read(...)
	 * @return number of channels
	 */
	public int setAllChannels();
	
	/**
	 * Only relevant for the method previewRead(...)
	 * Other read-methods get all channels incoming out.
	 * 
	 * chan.length = number of channels for the output. 
	 * The values get the inputchannel for the respective outputchannel.
	 * (chan[i] = channelInTheSource, i = channelOnTheOutput)
	 * 
	 * Return false and do nothing if any channelreference is greater then the 
	 * number of available channels.
	 * 
	 * For Default, the value set is chan =  new int[]{0};
	 * 
	 * @param chan = inputchannels for the respektiv outpuchannels
	 * @return true if the param is coherent whith in- and output
	 */
	public boolean setChannels(int[] chan);
	
	/**
	 * Get the number of channels available by previewRead(), dependend
	 * from the int[] channels set by setChannels.
	 * If nothing was set, the default is 1, the first channel of source.
	 * The AudioFormat returned get the number of channels available in source,
	 * not the number of channels get by previewRead(). 
	 * @return
	 */
	public int channelsDisponible();
	
	/**
	 * Optional - return null if not implemented.
	 * Get a PositionableFIS that read from the ByteBuffer that allways is source
	 * of the FloatPrewievReaderimplementation
	 * @return
	 */
	public PositionableFIS getPositionableFIS();

	/**
	 * 
	 */
	public AudioFormat getFormat();
	
	/**
	 * Set the position (in frames) in File, in samples, at that read(...) will begin
	 * to extract data, by the next invocation.
	 * @param n - new position in samples
	 * @return FloatISPartReader - the invoked class
	 * @throws IOException
	 */
	public void position(int n) throws IOException;
	
	/**
	 * Get the aktual read-position (in frames) in the data.
	 * @return long - the aktual position, in samples, at that read(...) will begin
	 * to extract data, by the next invocation.
	 */
	public int position();
	
	/**
	 * total number of samples available, including already readed samples
	 * @return
	 */
	public int available();
	
	/**
	 * Total number of Mikrosekonds available.
	 * @return
	 */
	public long availableMikroseconds();
	
	/**
	 * Stop the read-prozess of preview-data. previewRead() will return
	 * having read so many data was read by evok this stop-method.
	 * @return true if preview-read was running, false otherwise.
	 */
	public boolean stopPreviewRead();

}

