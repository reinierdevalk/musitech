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
 * This is an interface for streams of incoming audio data represented as
 * floats.
 * 
 * @author Martin Gieseking, Nicolai Strauch, Tillman Weyde
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public interface FloatInputStream {

	/**
	 * Reads floats from a stream.
	 * 
	 * @param data the array to store the data in. In the Format Channels X
	 *            dataLen
	 * @return int the number of frames read.
	 * @throws IOException
	 */
	public int read(float[][] data) throws IOException;

	/**
	 * Method read reads floats from a stream. TODO comment
	 * 
	 * @param data The array to store the data in. In the Format Channels X
	 *            dataLen
	 * @param start Offset when writing the frames.
	 * @param len Number of frames to write.
	 * @return int the number of frames written into data.
	 * @throws IOException
	 */
	public int read(float[][] data, int start, int len) throws IOException;

	// This channel number should now be got from the Format.
	// public int getChannelNumber();

	// The other infomation can be used when converting to byte streams, but
	// the actual format of the float stream is fixed in that respect.

	/**
	 * Gets audio format information. <br>
	 * The audio format information is kept from the original data source, if it
	 * has not been modified by processing. Of these only channels, sampleRate
	 * and sampleSizeInBits are relevant for the float input stream. If the data
	 * are normalized, <code>sampleSizeInBits</code> must be set to 1. All
	 * implementing classes that change the format of the data have to adapt the
	 * audio format information they deliver.
	 */
	public AudioFormat getFormat();

	/**
	 * The default AudioFormat for <code>FloatInputStream</code>, which is
	 * used by JavaSound for playback. Using this format avoids further
	 * conversions.
	 */
	public static final AudioFormat DefaultFormat = new AudioFormat(
																	AudioFormat.Encoding.PCM_SIGNED,
																	44100, // sampleRate
																	16, // bitPerSample
																	2, // channels
																	4, // frameSize
																	44100, // frameRate
																	true); // bigEndian

	// @@TODO: void negotiateFormat(FormatRangeDescriptor frd)

	/**
	 * If the end of Stream is reached, the returned value is negative,
	 * calculated as (skippedFrames + 1) * -1;
	 * 
	 * @param n Frames to be skipped
	 * @return long Frames skipped if stream not at end, else (skippedFrames +
	 *         1) * -1.
	 * @throws IOException if an IOError has occured.
	 */
	public long skip(long n) throws IOException;

	/**
	 * Reset to an initial state, for file streams it goes to the beginning of
	 * the file.
	 * 
	 * @throws IOException if an IOError occurs.
	 */
	public void reset() throws IOException;

	/**
	 * Get the number of samples left to play in the input stream.
	 * 
	 * @return The number of samples, -1 if not known (e.g. a live stream).
	 */
	public long remainingSamples();

	/**
	 * Get the current position in the stream, i.e. the number of frames since
	 * the beginning of the stream. 
	 * 
	 * @return The current position in frames.
	 */
	public long getPositionInSamples();

	/**
	 * Sets the position of the stream, if possible. After calling this method,
	 * the position needs to be checked by calling getPositionInSamples. In live
	 * streams the possibility of going back and forward will be limited. Even
	 * if the stream is successfully reset, there is no general guarantee that
	 * the signal delivered afterwards is identical from call to call (e.g.
	 * oscillator parameters have changed, file content has changed).
	 * 
	 * @param newPos The new position.
	 * @throws IOException can be thrown from undelying IO streams.
	 */
	public void setPositionInSamples(long newPos) throws IOException;
}
