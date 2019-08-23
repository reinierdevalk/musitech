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
package de.uos.fmt.musitech.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.floatStream.FISChannelAdapter;
import de.uos.fmt.musitech.audio.floatStream.FISSampleRateDecimator;
import de.uos.fmt.musitech.audio.floatStream.FISSampleSizeConverter;
import de.uos.fmt.musitech.audio.floatStream.FIStoAIS;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.floatStream.MP3FileFloatIS;
import de.uos.fmt.musitech.audio.floatStream.MP3FloatIS;
import de.uos.fmt.musitech.audio.floatStream.PCMFileFloatIS;
import de.uos.fmt.musitech.audio.floatStream.PCMFloatIS;
import de.uos.fmt.musitech.audio.floatStream.PositionableFIS;

/**
 * This class provides some static methods for reading and writing audio data.
 * 
 * @author Martin Gieseking, Tillman Weyde, Nicolai Strauch
 * @version $Revision: 7968 $, $Date: 2011-07-04 15:39:42 +0200 (Mon, 04 Jul 2011) $
 */
public class AudioUtil {

	/**
	 * @return AudioUtil.getFloatInputStream(FloatInputStream.DefaultFormat);
	 */
	public static FloatInputStream getFloatInputStream() {
		return getFloatInputStream(FloatInputStream.DefaultFormat);
	}

	/**
	 * Create an FloatInputStream from a default TargetDataLine. The returned
	 * TargetDataLine can be finalised whith AudioUtil.closeTargetDataLine() And
	 * it is stongly recomended to close the TargetDataLine, if it is not used
	 * any more.
	 * 
	 * @param audioFormat
	 * @return FloatInputStream
	 */
	public static FloatInputStream getFloatInputStream(AudioFormat audioFormat) {
		return getFloatInputStream(audioFormat, getTargetDataLine());
	}

	/**
	 * Create an FloatInputStream from an TargetDataLine, whith the
	 * FloatInputStream.DefaultFormat AudioFormat
	 * 
	 * @param line - TargetDataLine as source for the FloatInputStream
	 * @return FloatInputStream
	 */
	public static FloatInputStream getFloatInputStream(TargetDataLine line) {
		return getFloatInputStream(FloatInputStream.DefaultFormat, line);
	}

	/**
	 * Create an FloatInputStream from an TargetDataLine
	 * 
	 * @param audioFormat Format from the data given by the TargetDataLine
	 * @param line TODO
	 * @return FloatInputStream
	 */
	public static FloatInputStream getFloatInputStream(AudioFormat audioFormat, TargetDataLine line) {
		AudioInputStream ais = new AudioInputStream(line);
		PCMFloatIS fisAdapter = new PCMFloatIS(ais);
		line.start();
		return fisAdapter;
	}

	/**
	 * @return AudioUtil.getTargetDataLine(FloatInputStream.DefaultFormat);
	 */
	public static TargetDataLine getTargetDataLine() {
		return getTargetDataLine(FloatInputStream.DefaultFormat);
	}

	/**
	 * initialise and return a new TargetDataLine
	 * 
	 * @param audioFormat - the format from the AudioData to be get from the
	 *            TargetDataLine
	 * @return the TargetDataLine opend, but not started
	 */
	public static TargetDataLine getTargetDataLine(AudioFormat audioFormat) {
		return getTargetDataLine(audioFormat, 8192);
	}

	/**
	 * initialise and return a new TargetDataLine
	 * 
	 * @param audioFormat - the format from the AudioData to be get from the
	 *            TargetDataLine
	 * @param buffSize The size of the buffer, the DataLine should use.
	 * @return the TargetDataLine, opend, but not started
	 */
	public static TargetDataLine getTargetDataLine(AudioFormat audioFormat, int buffSize) {
		TargetDataLine ntLine = null;
		try {
			Line.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
			ntLine = (TargetDataLine) AudioSystem.getLine(info);
			ntLine.open(audioFormat, buffSize);
		} catch (LineUnavailableException e) {
			System.err.println("getTargetDataLine is returning null.");
			e.printStackTrace();
			return null;
		}
		return ntLine;
	}

	private static TargetDataLine tLine;

	/**
	 * Initialise TargetDataLine used by readFromTargetDataLine whith a specific
	 * AudioFormat
	 * 
	 * @param audioFormat TODO
	 */
	public static void initialiseTargetDataLine(AudioFormat audioFormat) {
		if (!audioFormat.equals(getTargetDataLineAF())) {
			tLine.stop();
			tLine.close();
			tLine = null;
			tLine = getTargetDataLine(audioFormat);
		}
	}

	/**
	 * Get the AudioFormat use by readFromTargetDataLine
	 * 
	 * @return The AudioFormat.
	 */
	public static AudioFormat getTargetDataLineAF() {
		return tLine.getFormat();
	}

	/**
	 * read from the static TargetDataLine. If the TargetDataLine not was
	 * initialised, she will be initialised whith the AudioFormat
	 * FloatInpputStream.DefaultFormat. Allways try to start the TargetDataLine.
	 * 
	 * @param b TODO
	 * @param off TODO
	 * @param len TODO
	 * @return the number of bytes read TODO
	 */
	public static int readFromTargetDataLine(byte[] b, int off, int len) {
		if (tLine == null)
			tLine = getTargetDataLine();
		tLine.start();
		return tLine.read(b, off, len);
	}

	/**
	 * read from the static TargetDataLine. If the TargetDataLine not was
	 * initialised, she will be initialised whith the AudioFormat
	 * FloatInpputStream.DefaultFormat. The data returned will have the
	 * AudioFormat from the TargetDataLine. Allways try to start the
	 * TargetDataLine.
	 * 
	 * @param array
	 * @param off
	 * @param len
	 * @return the number of samples read
	 * @throws IOException
	 */
	public static int readFromTargetDataLine(float[][] array, int off, int len) throws IOException {
		if (tLine == null)
			tLine = getTargetDataLine();
		AudioFormat audioFormat = tLine.getFormat();
		int bLen = len * audioFormat.getFrameSize();
		byte[] b = new byte[bLen];
		int read = tLine.read(b, 0, bLen);
		return bytesToFloats(b, read, array, off, audioFormat, false);
	}

	/**
	 * @return AudioUtil.getNewSourceDataLine(FloatInputStream.DefaultFormat);
	 */
	public static SourceDataLine getSourceDataLine() {
		return getSourceDataLine(FloatInputStream.DefaultFormat);
	}

	/**
	 * initialise and return a new SourceDataLine
	 * 
	 * @param audioFormat - the format from the AudioData to get from the
	 *            SourceDataLine
	 * @return the static SourceDataLine, opend, but not started
	 */
	public static SourceDataLine getSourceDataLine(AudioFormat audioFormat) {
		return getSourceDataLine(audioFormat, 8192);
	}

	/**
	 * Initialise and return a new SourceDataLine
	 * 
	 * @param audioFormat - the format from the AudioData to get from the
	 *            SourceDataLine
	 * @param buffSize TODO
	 * @return the static SourceDataLine, opend, but not started
	 */
	public static SourceDataLine getSourceDataLine(AudioFormat audioFormat, int buffSize) {
		SourceDataLine nsLine = null;
		try {
			if (audioFormat.getSampleSizeInBits() == 1) {
				AudioFormat af = new AudioFormat(audioFormat.getEncoding(), audioFormat
						.getSampleRate(), 16, audioFormat.getChannels(),
													audioFormat.getChannels() * 2, audioFormat
															.getFrameRate(), audioFormat
															.isBigEndian());
				audioFormat = af;
			}

			Line.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

			nsLine = (SourceDataLine) AudioSystem.getLine(info);
			nsLine.open(audioFormat, buffSize);
		} catch (LineUnavailableException e) {
			System.err.println("getSourceDataLine is returning null.");
			e.printStackTrace();
			return null;
		}
		return nsLine;
	}

	private static SourceDataLine sLine;

	/**
	 * Initialise SourceDataLine used by readFromSourceDataLine whith a specific
	 * AudioFormat
	 */
	public static void initialiseSourceDataLine(AudioFormat audioFormat) {
		if (!audioFormat.equals(getSourceDataLineAF())) {
			sLine.stop();
			sLine.close();
			sLine = null;
			sLine = getSourceDataLine(audioFormat);
		}
	}

	/**
	 * Get the AudioFormat use by readFromSourceDataLine
	 * 
	 * @return
	 */
	public static AudioFormat getSourceDataLineAF() {
		return sLine.getFormat();
	}

	/**
	 * Write the given data into the static SourceDataLine. If the
	 * SourceDataLine was not initialised, it will be initialised whith the
	 * AudioFormat FloatInputStream.DefaultFormat. Allways it try to start the
	 * SourceDataLine.
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @return the number of bytes written
	 */
	public static int writeIntoSourceDataLine(byte[] b, int off, int len) {
		if (sLine == null)
			sLine = getSourceDataLine();
		sLine.start();
		return sLine.write(b, off, len);
	}

	/**
	 * Write the given data into the static SourceDataLine. If the
	 * SourceDataLine was not initialised, it will be initialised whith the
	 * AudioFormat FloatInputStream.DefaultFormat. Allways it try to start the
	 * SourceDataLine. Performancenote: allways a new bytearray is generated.
	 * 
	 * @param array
	 * @param off
	 * @param len
	 * @return number of samples written
	 */
	public static int writeIntoSourceDataLine(float[][] array, int off, int len) {
		if (sLine == null)
			sLine = getSourceDataLine();
		byte[] byteData = new byte[(sLine.getFormat().getSampleSizeInBits() / 8) * array.length
									* len];
		return writeIntoSourceDataLine(array, off, len, byteData, 0);
	}

	/**
	 * Write the given data into the static SourceDataLine. If the
	 * SourceDataLine was not initialised, it will be initialised whith the
	 * AudioFormat FloatInputStream.DefaultFormat. Allways it try to start the
	 * SourceDataLine.
	 * 
	 * @param array - audiodata in floatsamples
	 * @param off - first sample to write
	 * @param len - samples to write
	 * @param buffer - empty bytebuffer. So no buffer must be created. must have
	 *            the length sampleSizeInBits*Channels*len
	 * @param boff - first byte that can be used in buffer
	 * @return - number of samples written
	 */
	public static int writeIntoSourceDataLine(float[][] array, int off, int len, byte[] buffer,
												int boff) {
		if (sLine == null)
			sLine = getSourceDataLine();
		int sampleSizeInBits = sLine.getFormat().getSampleSizeInBits();
		floatsToBytes(array, off, buffer, boff, len, sampleSizeInBits);
		int written = writeIntoSourceDataLine(buffer, boff, buffer.length);
		return written / ((sampleSizeInBits / 8) * array.length);
	}

	/**
	 * Works whith mp3-coded files, and PCM-files
	 * 
	 * @param file - AudioFile that PositionableFIS will read.
	 * @throws IOException
	 */
	public static PositionableFIS getPositionableFIS(File file) throws IOException {
		PositionableFIS posFIS = null;
		try {
			posFIS = new PCMFileFloatIS(file);
		} catch (UnsupportedAudioFileException e) {
		}
		if (posFIS == null)
			try {
				posFIS = new MP3FileFloatIS(file);
			} catch (UnsupportedAudioFileException e1) {
			}
		if (posFIS == null)
			System.err.println("Audioformat of file: " + file.getAbsolutePath()
								+ " is not supported.");

		return posFIS;
	}

	/**
	 * Test the kind of the given InputStream. For more FloatInputStream
	 * implementations that can read more Types of Audiodata, this method must
	 * be completed.
	 * 
	 * @param inputSt - mp3 or pcm
	 * @return PCMFloatIS by pcm, or MP3FloatIS by mp3
	 * @throws IOException
	 */
	public static FloatInputStream getFloatInputStream(InputStream inputSt) throws IOException {
		try {
			return getFloatInputStream(AudioSystem.getAudioInputStream(inputSt));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		try {
			return new MP3FloatIS(inputSt);
		} catch (Exception e) {
			if (e instanceof IOException)
				throw (IOException) e;
			System.err
					.println("Audioformat is not supported by your java-version. Probaly it is not an mp3-file too.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param ais - AudioInputStream to be converted in an FloatInputStream
	 * @return PCMFloatIS
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public static FloatInputStream getFloatInputStream(AudioInputStream ais)
			throws UnsupportedAudioFileException, IOException {

		// this code provides a FloatInputStream from an mp3-inputstream, if
		// mp3sp.1.4.jar is used.
		// the code is no longer necessary, if MP3FloatIS can be used

		AudioFormat sourceFormat = ais.getFormat();
		int sampleSizeInBits = FloatInputStream.DefaultFormat.getSampleSizeInBits();
		AudioFormat targetFormat = new AudioFormat(FloatInputStream.DefaultFormat.getEncoding(),
													sourceFormat.getSampleRate(), sampleSizeInBits,
													sourceFormat.getChannels(),
													sourceFormat.getChannels()
															* (sampleSizeInBits / 8), sourceFormat
															.getSampleRate(),
													FloatInputStream.DefaultFormat.isBigEndian());
		ais = AudioSystem.getAudioInputStream(targetFormat, ais);
		// This might be useful, if the MP3SPI is installed,
		// But direct access is preferred for efficiency.
		// if(sourceFormat.getSampleSizeInBits()<0){
		// MP3FloatIS mp3stream = new MP3FloatIS(ais);
		// return mp3stream;
		// }
		// ais = AudioSystem.getAudioInputStream(FloatInputStream.DefaultFormat,
		// ais);
		// AudioFormat audioFormat = ais.getFormat();
		PCMFloatIS fisa = new PCMFloatIS(ais);

		return fisa;

		// return new PCMFloatIS(ais);
		// return new FloatISPartReader(ais);
	}

	/**
	 * Try to obtain an AudioInputStream, and return an PCMFloatIS. If an
	 * Exeption occure, an FileInputStream is generated, and given to
	 * AudioUtil.getFloatInputStream(InputStream). Maybe it is an mp3-codet
	 * file, than MP3FloatIS will be returned. Else an Exception occure.
	 * 
	 * @param file
	 * @return PCMFloatIS by pcm, or MP3FloatIS by mp3
	 * @throws IOException
	 */
	public static FloatInputStream getFloatInputStream(File file) throws IOException {
		InputStream is;
		try {
			is = AudioSystem.getAudioInputStream(file);
			return getFloatInputStream((AudioInputStream) is);
		} catch (UnsupportedAudioFileException e) {
			// if not PCM, MP3 is treated here
			is = new FileInputStream(file);
			return getFloatInputStream(is);
		}
	}

	/**
	 * return getFloatInputStream( new File( your String-parameter ) )
	 * 
	 * @see de.uos.fmt.musitech.audio.AudioUtil#getFloatInputStream(File)
	 */
	public static FloatInputStream getFloatInputStream(String file)
			throws UnsupportedAudioFileException, IOException {
		return getFloatInputStream(new File(file));
	}

	/**
	 * Put the given FloatInputStream into adapterclasses to adapt the format of
	 * the given FloatInputStream to the given AudioFormat. Convert, if needed:
	 * <ul>
	 * <li>sampleRate using FISSampleRateConverter</li>
	 * <li>sampleSizeInBits using FISSampleSizeConverter</li>
	 * <li>channelNumber using FISChannelAdapter</li>
	 * </ul>
	 * If the value for any of this parameters is AudioSystem.NOT_SPECIFIED, no
	 * conversion is provided. Also, to not convert any parameter, give a
	 * AudioFormat whith AudioSystem.NOT_SPECIFIED at the parameter to be
	 * mantained. The new AudioFormat is not transmitted to the new created
	 * FloatInputStreams, only the conversionsparameter is transmitted. The othe
	 * parameters keep the some as in fis.getFormat()
	 * 
	 * @param fis
	 * @param newFormat
	 * @return the converter-FIS
	 */
	public static FloatInputStream convertFloatInputStream(FloatInputStream fis,
															AudioFormat newFormat) {
		if (newFormat.getSampleRate() != fis.getFormat().getSampleRate()
			&& newFormat.getSampleRate() != AudioSystem.NOT_SPECIFIED) {
			// if(newFormat.getSampleRate() < fis.getFormat().getSampleRate())
			fis = new FISSampleRateDecimator(fis, newFormat.getSampleRate());
			// else
			// fis = new FISSampleRateConverter(fis, newFormat.getSampleRate());
		}
		if (newFormat.getSampleSizeInBits() != fis.getFormat().getSampleSizeInBits()
			&& newFormat.getSampleSizeInBits() != AudioSystem.NOT_SPECIFIED) {
			fis = new FISSampleSizeConverter(fis, newFormat.getSampleSizeInBits());
		}
		if (newFormat.getChannels() != fis.getFormat().getChannels()
			&& newFormat.getChannels() != AudioSystem.NOT_SPECIFIED) {
			fis = new FISChannelAdapter(fis, newFormat.getChannels());
		}
		return fis;
	}

	/**
	 * Convert the samples of an double floatarray into bytes Use the as number
	 * of bits per sample the value given by FloatInputStream.DefaultFormat
	 * AudioFormat return floatsToBytes(floats, 0, floats[0].length)
	 * 
	 * @param floats - source whith the samples to be written as bytes
	 * @return bytearray whith the converted samples
	 */
	public static byte[] floatsToBytes(float[][] floats) {
		return floatsToBytes(floats, 0, floats[0].length);
	}

	/**
	 * Convert the samples of an double floatarray into bytes Use the as number
	 * of bits per sample the value given by FloatInputStream.DefaultFormat
	 * AudioFormat
	 * 
	 * @param floats - source whith the samples to be written as bytes
	 * @param start - first sample to be written
	 * @param len - number of samples to be converted to bytes
	 * @return bytearray whith the converted samples
	 */
	public static byte[] floatsToBytes(float[][] floats, int start, int len) {
		return floatsToBytes(floats, start, len, FloatInputStream.DefaultFormat
				.getSampleSizeInBits());
	}

	/**
	 * Convert the samples of an double floatarray into bytes
	 * 
	 * @param floats - source whith the samples to be written as bytes
	 * @param start - first sample to be written
	 * @param len - number of samples to be converted to bytes
	 * @param sampleSizeInBits - number of bits per sample, 1 if the stream is
	 *            normalised to range 1 -1, than it will be normalised to 16
	 *            bit.
	 * @return bytearray whith the converted samples
	 */
	public static byte[] floatsToBytes(float[][] floats, int start, int len, int sampleSizeInBits) {
		byte[] out = new byte[floats.length * len * sampleSizeInBits / 8];
		floatsToBytes(floats, start, out, 0, len, sampleSizeInBits);
		return out;
	}

	/**
	 * Convert the samples of an double floatarray into bytes. Use the as number
	 * of bits per sample the value given by FloatInputStream.DefaultFormat
	 * AudioFormat
	 * 
	 * @param src - source whith the samples to be written as bytes
	 * @param srcStart - first sample to be written
	 * @param dest - array that will get the samples as bytes
	 * @param tarStart - first position in the bytesarray, that will get a byte
	 * @param lenInSamples - number of samples to be converted to bytes
	 * @return number of bytes written in the targetarray
	 */
	public static int floatsToBytes(float[][] src, int srcStart, byte[] dest, int tarStart,
									int lenInSamples) {
		return floatsToBytes(src, srcStart, dest, tarStart, lenInSamples,
			FloatInputStream.DefaultFormat.getSampleSizeInBits());
	}

	/**
	 * Convert the samples from an array of floats into bytes.
	 * 
	 * @param src - source whith the samples to be written as bytes
	 * @param srcStart - first sample to be written
	 * @param dest - array that will get the samples as bytes
	 * @param tarStart - first position in the bytesarray, that will get an byte
	 * @param lenInSamples - number of samples to be converted to bytes
	 * @param sampleSizeInBits - number of bits per sample, 1 if the stream is
	 *            normalised to range 1 -1, than it will be normalised to 16
	 *            bit.
	 * @return number of bytes written into the targetarray
	 */
	public static int floatsToBytes(float[][] src, int srcStart, byte[] dest, int tarStart,
									int lenInSamples, int sampleSizeInBits) {
		int stop = srcStart + lenInSamples;
		int b = tarStart;
		int spNum, chNum, bits, src_ch_sp_as_int;
		int maxSample = (1 << (sampleSizeInBits - 1)) - 1;
		int minSample = -1 << (sampleSizeInBits - 1);
		if (sampleSizeInBits == 1) {
			int normaliseFactor = (1 << 15) - 1;
			for (spNum = srcStart; spNum < stop; spNum++) {
				for (chNum = 0; chNum < src.length; chNum++) {
					src_ch_sp_as_int = (int) (src[chNum][spNum] * normaliseFactor);
					for (bits = 8; bits >= 0; bits -= 8, b++) {
						dest[b] = (byte) (src_ch_sp_as_int >> bits);
					}
				}
			}
		} else {
			// iterate through samples
			for (spNum = srcStart; spNum < stop; spNum++) {
				// iterate through channels
				for (chNum = 0; chNum < src.length; chNum++) {
					src_ch_sp_as_int = (int) src[chNum][spNum];
					// cut off data that gets out of range
					// if (src_ch_sp_as_int > maxSample)
					// src_ch_sp_as_int = maxSample;
					// else if (src_ch_sp_as_int < minSample)
					// src_ch_sp_as_int = minSample;
					for (bits = sampleSizeInBits - 8; bits >= 0; bits -= 8, b++) {
						dest[b] = (byte) (src_ch_sp_as_int >> bits);
					}
				}
			}
		}
		return b - tarStart;
	}

	/**
	 * Convert the samples of an double floatarray into bytes
	 * 
	 * @param src - source whith the samples to be written as bytes
	 * @param srcStart - first sample to be written
	 * @param dest - array that will get the samples as bytes
	 * @param tarStart - first position in the bytesarray, that will get an byte
	 * @param lenInSamples - number of samples to be converted to bytes
	 * @param format - give the number of bits per sample
	 * @return number of bytes written in the targetarray
	 */
	public static int floatsToBytes(float[][] src, int srcStart, byte[] dest, int tarStart,
									int lenInSamples, AudioFormat format) {
		return floatsToBytes(src, srcStart, dest, tarStart, lenInSamples, format
				.getSampleSizeInBits());
	}

	/**
	 * Convert the samples of an double floatarray into bytes. return
	 * floatsToBytes(floats, 0, bytes, 0, floats[0].length,
	 * format.getSampleSizeInBits())
	 * 
	 * @param floats - source whith the samples to be written as bytes
	 * @param bytes - array that will get the samples as bytes
	 * @param format - give the number of bits per sample
	 * @return number of bytes written in the targetarray
	 */
	public int floatsToBytes(float[][] floats, byte[] bytes, AudioFormat format) {
		return floatsToBytes(floats, 0, bytes, 0, floats[0].length, format.getSampleSizeInBits());
	}

	/**
	 * write an WAVE-audio-file from the given FloatInputStream;
	 * 
	 * @param fis - the data to be written
	 * @param file - the file in that the data will be written
	 * @throws IOException
	 */
	public static void writeWaveFromFloat(FloatInputStream fis, File file) throws IOException {
		FIStoAIS audioStream = new FIStoAIS(fis);
		AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
	}

	static private byte byteBuffer[];

	/**
	 * Reads a sequence of samples from a given AudioInputStream.
	 * If(SourceNumberOfChannels>data.length) an ArrayIndexOutOfBoundException
	 * will occure If(SourceNumberOfChannels <data.length) data will be written
	 * only at data[SourceNumberOfChannels-1] The Data returned is big endian
	 * and signed.
	 * 
	 * @param data array where the samples are stored
	 * @param start
	 * @param len
	 * @param inputStream
	 * @param normalised
	 * @return The number of frames read.
	 * @throws IOException is passed on if the inputStream throws it.
	 */
	public static int readSamples(float[][] data, int start, int len, AudioInputStream inputStream,
									boolean normalised) throws java.io.IOException {

		AudioFormat format = inputStream.getFormat();
		int bytePS = format.getSampleSizeInBits() / 8; // bytes per Sample
		// TODO es ist sehr ineffektiv, hier jedes mal ein neues Array anzulegen
		// nützlich wäre eine funktion, der man die Werte übergibt,
		// die hier errechnet werden, und das Array, was hier erzeugt wird.
		int byteNum = bytePS * len * format.getChannels();
		if (byteBuffer == null || byteBuffer.length != byteNum)
			byteBuffer = new byte[bytePS * len * format.getChannels()];
		int didRead = inputStream.read(byteBuffer, 0, byteBuffer.length);
		if (didRead < 0)
			return didRead;
		return bytesToFloats(byteBuffer, didRead, data, start, format, normalised);
	}

	/**
	 * Write the bytes as samples in the floats. The number of bytes per sample
	 * and other parameters are read from the given AudioFormat.
	 * 
	 * @param bytes - source
	 * @param floats - target
	 * @param format - give the necessary parameters for the conversion
	 * @return -
	 */
	public int bytesToFloats(byte[] bytes, float[][] floats, AudioFormat format, boolean normalised)
			throws IOException {
		int frame = format.getSampleSizeInBits() / 8;
		frame = frame * format.getChannels();
		int disponibleFrames = bytes.length / frame;
		int len = disponibleFrames > floats[0].length ? floats[0].length : disponibleFrames;
		len = len * frame;
		return bytesToFloats(bytes, len, floats, 0, format, normalised);
	}

	/**
	 * Converts bytes to floats, works only whith 8, 16 or 24 bits per sample.
	 * 
	 * @param source - the bytes to be converted
	 * @param byteNum - number of bytes to be converted
	 * @param data - will contain the samples at the end of this method
	 * @param start - first index in data that will get a sample
	 * @param format - give the parameters for the conversion
	 * @param normalised if true the data will be normalised // TODO check this
	 * @return - the number of frames written in data
	 * @throws java.io.IOException
	 */
	public static int bytesToFloats(byte[] source, int byteNum, // len in source
									float[][] data, int start, AudioFormat format,
									boolean normalised) throws java.io.IOException {
		int numChannels = format.getChannels();
		int sampleDepth = format.getSampleSizeInBits();
		int frameSize = format.getFrameSize();
		int bytePS = format.getSampleSizeInBits() / 8; // bytes per Sample
		boolean isBigEndian = format.isBigEndian();
		AudioFormat.Encoding encoding = format.getEncoding();

		if (isBigEndian && bytePS > 1)
			AudioUtil.reverseSamples(source, 0, source.length, bytePS);

		int chOfs;
		int res; // sample
		int t_i = 0; // target index (float-array)
		for (int s_i = 0; s_i < byteNum; t_i++, s_i += frameSize) {
			for (int channel = 0; channel < numChannels; channel++) {
				chOfs = channel * bytePS;

				if (encoding != AudioFormat.Encoding.PCM_UNSIGNED
				// by PCM_SIGNED the negativ samples must be prepared (example:
					// by 16 bps, the bytes 11001011 and 00101110 must be
					// written as
					// integer: 11111111 11111111 11001011 00101110)
					&& source[s_i + chOfs + bytePS - 1] < 0) // if the
					// biggest
					// byte is <0, and
					// so the sample is
					// negativ
					res = -1 << (sampleDepth); // the bits at that the bytes
				// will be placed must be 0
				else
					res = 0;

				// the bytes are put in the sample
				res |= (0xff & source[s_i + chOfs + 0]);
				if (bytePS > 1) // 16 bps
					res |= (0xff & source[s_i + chOfs + 1]) << 8;
				if (bytePS > 2) // 24 bps
					res |= (0xff & source[s_i + chOfs + 2]) << 16;

				if (sampleDepth == 8) {
					res <<= 8;
				}
				data[channel][start + t_i] = res;
				if (sampleDepth == 24) {
					data[channel][start + t_i] /= 8;
				}
			}
		}
		if (encoding == AudioFormat.Encoding.PCM_UNSIGNED) {
			for (int i = 0; i < data.length; i++)
				for (int j = start; j < t_i; j++)
					data[i][j] -= sub[bytePS - 1];
		}

		if (normalised) {
			// TODO don't calculate this every time
			float f = 1 << (bytePS * 8 - 1);
			for (int i = 0; i < data.length; i++)
				for (int j = start; j < t_i; j++)
					data[i][j] /= f;
		}

		return t_i;
	}

	final static int[] sub = {0x80, 0x8000, 0x800000, 0x80000000};

	/**
	 * Compact or expand an array of determinated Length in to another Array,
	 * whith another Lengh resampling the contents. Example: Convert the
	 * FrameRate of Audiodata Push an amongth of Data for any Representation
	 * from most more Data TODO ?? XXX Zieht eine Menge Daten für irgend eine
	 * Representation aus sehr viel mehr Daten Conversion algorithm: linear
	 * Interpolation
	 * 
	 * @param inArray - array with in data
	 * @param len - len to read from inArray
	 * @param outArray - array to be written
	 * @param sourceSR_by_targetSR - conversionFactor = (SampleRate of Source) /
	 *            (SampleRate of Target)
	 * @return int - number of Frames inserted in outArray
	 */
	public static int arrayResamplert(float[][] inArray, int s_len, float[][] outArray,
										int t_start, int t_len, float sourceSR_by_targetSR) {
		if (inArray.length != outArray.length)
			throw new IllegalArgumentException(
												"Number of Channels must be equal between inArray and outArray.");
		if (s_len > inArray[0].length)
			throw new IllegalArgumentException("len can't be greater than inArray[0].length.");

		int t_totLen = t_len + t_start; // total Length of target data
		// float sourceSR_by_targetSR = inArray[0].length/outArray[0].length; //
		// source_sampleRate / target_sampleRate
		// float sa_exactSampleI = sourceSR_by_targetSR * -1;
		float sa_exactSampleI = 0;
		int sa_sampleI = (int) sa_exactSampleI; // index in outArray
		// exact Index in the Samples of the source array (imaginary)
		// (saHz_by_taHz * length in Samples from target = length in Samples
		// from source,
		// sa_exactSampleI = SampleIndex from source * sourceSR_by_targetSR)
		int ta_sampleI = t_start;
		float diff = 0.0f;
		for (; sa_sampleI < s_len && ta_sampleI < t_totLen; ta_sampleI++) {

			if (sa_exactSampleI == sa_sampleI) {
				for (int i = 0; i < outArray.length; i++)
					outArray[i][ta_sampleI] = inArray[i][sa_sampleI];
			} else {
				float distanceFromIndex = sa_exactSampleI - sa_sampleI;
				for (int i = 0; i < outArray.length; i++) {
					if (sa_sampleI + 1 < s_len)
						diff = inArray[i][sa_sampleI + 1] - inArray[i][sa_sampleI];
					else
						diff = 0.0f;
					outArray[i][ta_sampleI] = inArray[i][sa_sampleI] + (diff * distanceFromIndex);
				}
			}
			sa_exactSampleI += sourceSR_by_targetSR;
			sa_sampleI = (int) sa_exactSampleI;
		}
		return ta_sampleI - t_start;
	}

	/**
	 * Works whith maximal 24 bit. Reverse bytes from big- to little-endian, or
	 * the invers.
	 * 
	 * @param bytes - data-array
	 * @param start - first byte to be considered
	 * @param len - number of bytes to be considered
	 * @param bytePS - number of bytes to be reversed from sample to sample
	 *            (bytes per sample)
	 */
	public static void reverseSamples(byte[] bytes, int start, int len, int bytePS) {
		byte tmp = 0;
		if (bytePS == 2) {
			for (int i = start; i < len; i += 2) {
				tmp = bytes[i];
				bytes[i] = bytes[i + 1];
				bytes[i + 1] = tmp;
			}
		} else if (bytePS == 3) {
			for (int i = start; i < len; i += 3) {
				tmp = bytes[i];
				bytes[i] = bytes[i + 2];
				bytes[i + 2] = tmp;
			}
		} else if (bytePS != 1) {
			System.err.println("AudioUtil.reverse(bytes[], int) do not support conversion of "
								+ bytePS + " bytes per sample.");
		}
	}

	/**
	 * Fill an array whith an given value.
	 * 
	 * @param array to be filled.
	 * @param whith - value to fill the array.
	 */
	public static void fillUp(float[][] array, float whith) {
		fillUp(array, 0, array[0].length, whith);
	}

	/**
	 * Fill an array whith an given value. array.length will be filled
	 * completly. The position and len - parameters means array[i]
	 * 
	 * @param array - array to be filled
	 * @param start - first index at that the array[i] will be filled
	 * @param len - number of positions in array[i] that will be filled
	 * @param whith - number with that the array will be filled
	 */
	public static void fillUp(float[][] array, int start, int len, float whith) {
		for (int i = 0; i < array.length; i++)
			for (int j = start; j < len + start; j++)
				array[i][j] = whith;
	}

}
