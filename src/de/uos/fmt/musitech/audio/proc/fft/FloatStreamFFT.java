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
/**
 * Created on 12.06.2003
 *
 */
package de.uos.fmt.musitech.audio.proc.fft;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.proc.fft.backup.FastFourierTransformation;

/**
 * @author Nicolai Strauch
 * Show an FFT-Analysis of the Data given by an FloatInputStream
 */
public class FloatStreamFFT {

	/**
	 * Show the FFT-Analysis of the data contained in double-array d.
	 * @param array - the data to be analysied
	 * @param start - first sample to be analysed 
	 * @param len - Must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 */
	public static void showFFT(double[] d, int start, int len, AudioFormat format){
		len = nextPowerOf2Neigthbour(len);
		FrequencyPanel panel = (FrequencyPanel)calculateFFT(d, start, len, format);
		panel.setSize(400, 300);
		JFrame frame = new JFrame("Frequency Windows");
		frame.getContentPane().add(panel);
	  	frame.setSize(400, 300);
	  	frame.setVisible(true);
	}
	/**
	 * Show the FFT-Analysis of the data contained in double-array d.
	 * @param array - the data to be analysied
	 * @param start - first sample to be analysed 
	 * @param len - Must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param format - the AudioFormta from the data given.
	 */
	public static void showFFT(float[] array, int start, int len, AudioFormat format){
		len = nextPowerOf2Neigthbour(len);
		double[] d = new double[array.length];
		for (int i = 0; i < len; i++) {
		  d[i] = array[i+start];
		} // for
		showFFT(d, start, len, format);
	}
	/**
	 * Show the FFT-Analysis of the data contained in double-array d.
	 * @param data - the data to be analysied. The Length must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param format - the AudioFormta from the data given.
	 */
	public static void showFFT(float[] array, AudioFormat format){
		showFFT(array, 0, array.length, format);
	}
	/**
	 * Make the FFT-Analysis of the data contained in double-array d.
	 * @param data - the data to be analysied. The Length must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param format - the AudioFormta from the data given.
	 * @return JFrame that whows the graphical FFT-Analysis
	 */
	public static JPanel calculateFFT(float[] array, AudioFormat format){
		return calculateFFT(array, 0, array.length, format);
	}
	/**
	 * Make the FFT-Analysis of the data contained in double-array d.
	 * @param array - the data to be analysied
	 * @param start - first sample to be analysed 
	 * @param len - Must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param format - the AudioFormta from the data given.
	 * @return JFrame that whows the graphical FFT-Analysis
	 */
	public static JPanel calculateFFT(float[] array, int start, int len, AudioFormat format){
		len = nextPowerOf2Neigthbour(len);
		double[] d = new double[array.length];
		for (int i = 0; i < len; i++) {
		  d[i] = array[i+start];
		} // for
		return calculateFFT(d, start, len, format);
	}
	/**
	 * Make the FFT-Analysis of the data contained in double-array d.
	 * @param data - the data to be analysied
	 * @param start - first sample to be analysed 
	 * @param len - Must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param format - the AudioFormta from the data given.
	 * @return JFrame that whows the graphical FFT-Analysis
	 */
	public static JPanel calculateFFT(double[] data, int start, int len, AudioFormat format){
		len = nextPowerOf2Neigthbour(len);
		double[] d = new double[len];
		System.arraycopy(data, start, d, 0, len);
		// Fast Fourier Transformation does the wave file analysis
		FastFourierTransformation.realFFT(d);

		double[][] output = new double[len / 2 + 1][2];

		// get out F[len/2]
		output[len / 2][0] = format.getSampleRate() / 2.0;
		output[len / 2][1] = Math.abs(d[1]);
		d[1]=0.0;

		// get out F[0]..F[N/2-1]
		for (int i = 0; i < d.length / 2; i++) {
		  output[i][0] = (double)i * format.getSampleRate() / len;
		  output[i][1] = Math.sqrt(d[i * 2] * d[i * 2] +
								   d[i * 2 + 1] * d[i * 2 + 1]);
		}
		
		int fft_length = 256;
		for (int i = 0; i < output.length; i++) {
		  output[i][1] = Math.log(output[i][1] / fft_length);
		}
		
		FrequencyPanel panel = new FrequencyPanel(output);

		return panel;
	}
	/**
	 * 
	 * @param fis - datasource, data to be analised. Only channel 0 will be analysed. Only 256 Samples will be analysed.
	 * @return JFrame containing FFT-Analysis
	 * @throws IOException
	 */
	public static JPanel calculateFFT(FloatInputStream fis) throws IOException{
		return calculateFFT(fis, 0);
	}
	/**
	 * 
	 * @param fis - datasource, data to be analised. Only 256 Samples will be analysed.
	 * @param chan - channel to be analised (analyse only one channel))
	 * @return JFrame containing FFT-Analysis
	 * @throws IOException
	 */
	public static JPanel calculateFFT(FloatInputStream fis, int chan) throws IOException{
		return calculateFFT(fis, 256, chan);
	}
	/**
	 * 
	 * @param fis - datasource, data to be analised
	 * @param len - Samples to be analysed. Must be power of 2. If it is not Power of two, the next minor number that is power of two will be the number of samples to be analysed.
	 * @param chan - channel to be analised (analyse only one channel))
	 * @return JFrame containing FFT-Analysis
	 * @throws IOException
	 */
	public static JPanel calculateFFT(FloatInputStream fis, int len, int chan) throws IOException{
		AudioFormat format = fis.getFormat();
//		Vector dataAmoungth = new Vector();
//		int dataLen = 0;
//		float[][] dataReader = new float[format.getChannels()][4096];
//		int dataRead = 0;
//		dataRead = fis.read(dataReader);
//		while(dataRead != -1){
//			if(dataRead < dataReader[0].length){
//				float[][] tmp = new float[dataReader.length][dataRead];
//				for(int i=0; i<dataReader.length; i++)
//					System.arraycopy(dataReader[i], 0, tmp[i], 0, dataRead);
//				dataAmoungth.add(tmp);
//			}
//			else
//				dataAmoungth.add(dataReader);
//			dataLen += dataRead;
//			dataRead = fis.read(dataReader);
//		}
//		float[][] totalData = new float[dataReader.length][dataLen];
//		dataRead = 0;
//		for(int i=0; i<dataAmoungth.size(); i++){
//			dataReader = (float[][])dataAmoungth.get(i);
//			for(int j=0; j<dataReader.length; j++)
//				System.arraycopy(dataReader[j], 0, totalData[j], dataRead, dataReader[0].length);
//			dataRead += dataReader[0].length;
//		}
		len = nextPowerOf2Neigthbour(len);
		float[][] data = new float[format.getChannels()][len];
		int dataRead = 0, dataReadNow = 0;
		while(dataRead<len && dataReadNow != -1){	// maybe I can not get all data on the first read(...)
// System.out.println("FloatStreamFFT.calculateFFT(FIS...): now try to read "+(len-dataRead)+" data.");
			dataReadNow = fis.read(data, dataRead, len - dataRead);
			if(dataReadNow != -1)
				dataRead += dataReadNow;
		}
		if(dataRead != len){
			len = nextPowerOf2Neigthbour(dataRead);
		}

		format = new AudioFormat(format.getEncoding(),
								  format.getSampleRate(),
								  format.getSampleSizeInBits(),
								  1,
								  format.getSampleSizeInBits()/8,
								  format.getFrameRate(),
								  format.isBigEndian());		
		//return calculateFFT(totalData[chan], format);
		return calculateFFT(data[chan], 0, len, format);
	}
	/**
	 * 
	 * @param val
	 * @return the next minor than (val+1) number power of two.
	 */
	private static int nextPowerOf2Neigthbour(int val){
		long power = 1;
		for(int i=1; i<30; i++){
			if(val < (power<<(i+1)))
				return (int)(power << i);
		}
		return val;
	}
}
