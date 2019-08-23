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
package de.uos.fmt.musitech.audio.proc.fft;

/*************************** WaveFileAnalysis.java ***************************/
/* Datum: 12. Januar 2002
 * Autor: Christian Datzko
 * Copyright: Christian Datzko, 2002
 * E-Mail-Adresse: datzko@t-online.de
 * Programmiersprache und -version: Java(TM) 2 SDK, Standard Edition (1.3.1_01)
 */

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import de.uos.fmt.musitech.audio.proc.fft.backup.AudioData;
import de.uos.fmt.musitech.audio.proc.fft.backup.FastFourierTransformation;
import de.uos.fmt.musitech.audio.proc.fft.backup.FrequencyWindow;

/**
 * Some useful routines for frequency analysis of wave files.
 */

public class WaveFileAnalysis {

  /**
   * Shows a <CODE>FrequencyWindow</CODE> with the data <CODE>d</CODE>, the
   * widht <CODE>width</CODE> and the height <CODE>height</CODE>.<P>
   * When the window is closed <CODE>System.exit(0);</CODE> is called to quit
   * the VM.
   * @author Christian Datzko
   * @version 1.0
   * @param d[][] an array of pairs of data, where <CODE>d[i][0]</CODE>
   * is a frequency and <CODE>d[i][1]</CODE> is an intensity. Assuming
   * linearity between <CODE>i</CODE> and <CODE>data[i][0]</CODE>.
   * @param width the width of the <CODE>FrequencyWindow</CODE>.
   * @param height the height of the <CODE>FrequencyWindow</CODE>.
   */
  public static void ShowAnalysisWindow(double[][] d, int width, int height) {
    FrequencyWindow frame = new FrequencyWindow(d);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        System.exit(0);
      } // windowClosing(WindowEvent ev)
    } // inner class
    );
  frame.setSize(width, height);
  frame.setVisible(true);
  } // void ShowAnalysisWindow(double[][] d)

  /**
   * Simply calls <CODE>ShowAnalysisWindow(double[][] d, int width, int
   * height)</CODE> with <CODE>width</CODE> = 400 and <CODE>height</CODE> = 300.
   * @author Christian Datzko
   * @version 1.0
   * @param d[][] an array of pairs of data, where <CODE>d[i][0]</CODE>
   * is a frequency and <CODE>d[i][1]</CODE> is an intensity. Assuming
   * linearity between <CODE>i</CODE> and <CODE>data[i][0]</CODE>.
   */
  public static void ShowAnalysisWindow(double[][] d) {
    ShowAnalysisWindow(d, 400, 300);
  } // void ShowAnalysisWindow(double[][] d)

  /**
   * Analyses the wave file <CODE>fname</CODE> starting at
   * <CODE>start_sec</CODE>, using <CODE>lengthFFT</CODE> samples.
   * @author Christian Datzko
   * @version 1.0
   * @param fname a valid file name
   * @param start_sec a valid time in seconds within <CODE>fname</CODE>
   * @param lengthFFT length of the FFT array, must be a power of 2
   * @param leftChannel <CODE>true</CODE>: analyse the left channel,
   * <CODE>false</CODE>: analyse the right channel.
   * @throws IllegalArgumentException
   * @return double[][] contains <CODE>lengthFFT/2+1</CODE> pairs of data,
   * <CODE>[i][0]</CODE> is a frequency, <CODE>[i][1]</CODE> the corresponding
   * intensity.
   */
  public static double[][] analyseWaveFile(String fname, double start_sec,
                                           int lengthFFT, boolean leftChannel) {
    AudioData d = new AudioData(new File(fname));
    int start = (int)(start_sec * d.getSampleRate());
    if (leftChannel)
      return analyseWaveData(d.getLeftChannel(), start, start + lengthFFT,
                             d.getSampleRate());
    else
      return analyseWaveData(d.getRightChannel(), start, start + lengthFFT,
                             d.getSampleRate());
  } // analyseWaveFile(String fname, double start_sec, int lengthFFT,
    // boolean leftChannel)

  /**
   * Analyses part (or all) of an array of integers from <CODE>start</CODE> to
   * <CODE>end-1</CODE> with the specified sampling rate.
   * @author Christian Datzko
   * @version 1.0
   * @param data[] array containing sampled data
   * @param start start index
   * @param end end index - 1 (<CODE>end-start</CODE> must be a power of 2)
   * @param samplingRate the sampling rate of the sampled data
   * @throws IllegalArgumentException
   * @return double[][] contains <CODE>lengthFFT/2+1</CODE> pairs of data,
   * <CODE>[i][0]</CODE> is a frequency, <CODE>[i][1]</CODE> the corresponding
   * intensity.
   */
  public static double[][] analyseWaveData(int[] data, int start, int end,
                                           float samplingRate) {
    // what can happen...
    if (start >= end)
      throw new IllegalArgumentException("\"start\" must be higher then "
                                         + "\"end\".");
    if (!FastFourierTransformation.isPowerOf2(end - start))
      throw new IllegalArgumentException("Can only analyse data with the "
                                         + "length of a Power of 2.");
    if (data == null)
      throw new IllegalArgumentException("I need some data to analyse.");
    if (end > data.length)
      throw new IllegalArgumentException("\"data\" is too small.");

    // copy the data into a new array
    int N = end - start;
    double[] d = new double[N];
    for (int i = start, j = 0; i < end; i++, j++) {
      d[j] = data[i];
    } // for

    // Fast Fourier Transformation does the wave file analysis
    FastFourierTransformation.realFFT(d);

    double[][] output = new double[N / 2 + 1][2];

    // get out F[N/2]
    output[N / 2][0] = samplingRate / 2.0;
    output[N / 2][1] = Math.abs(d[1]);
    d[1]=0.0;

    // get out F[0]..F[N/2-1]
    for (int i = 0; i < d.length / 2; i++) {
      output[i][0] = (double)i * samplingRate / N;
      output[i][1] = Math.sqrt(d[i * 2] * d[i * 2] +
                               d[i * 2 + 1] * d[i * 2 + 1]);
    } // for
    return output;
  } // analyseWaveData(int[] data, int start, int end, int samplingRate)

  private static final String syntax_string = "WaveFileAnalysis: java "
                    + "WaveFileAnalysis filename start_sec fft_length";
                                        // syntax for  running this class

  /**
   * Small main method to test <CODE>WaveFileAnalysis</CODE>,
   * <CODE>FastFourierTransformation</CODE> and <CODE>FrequencyWindow</CODE>.
   * Run from command line with <CODE>java WaveFileAnalysis filename start_sec
   * fft_length</CODE>.
   * @author Christian Datzko
   * @version 1.0
   * @param argv[] passed array of parameters
   */
  public static void mainD(String[] argv) {
    if (argv.length!=3) {
      System.out.println(syntax_string);
    } // if
    else {
      double start_sec = 0.0;
      try {
        start_sec = Double.parseDouble(argv[1]);
      } // try
      catch (NumberFormatException e) {
        System.out.println(e.getMessage());
        System.out.println(syntax_string);
      } // catch
      int fft_length = 256;
      try {
        fft_length = Integer.parseInt(argv[2]);
      } // try
      catch (NumberFormatException e) {
        System.out.println(e.getMessage());
        System.out.println(syntax_string);
      } // catch
      if (!FastFourierTransformation.isPowerOf2(fft_length)) {
        System.out.println("fft_length must be an integer power of 2.");
        System.out.println(syntax_string);
      } // if
      else {
        double[][] d = analyseWaveFile(argv[0], start_sec, fft_length, true);
        // logarithmic plot of data gets a better graph
        for (int i = 0; i < d.length; i++) {
          d[i][1] = Math.log(d[i][1] / (double)fft_length);
        } // for
        ShowAnalysisWindow(d);
      } // else
    } // else
  } // main(String[] argv)
  /**
   * Don't need Arguments at begin
   * @author "Nicolai A. Strauch" (added at 09.01.2002)
   * @param arg
   */
  public static void main(String arg[]){
	
		
		  double start_sec = 0.0;
		  
		  int fft_length = 256;
		  
		  String filename = "";
		  
		FileDialog fileChooser = new FileDialog(new Frame());
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.show();
		if (fileChooser.getFile() == null)
			System.out.println("Problems with FileDialog.");

		filename = fileChooser.getDirectory() + fileChooser.getFile();
		  
		  if (!FastFourierTransformation.isPowerOf2(fft_length)) {
			System.out.println("fft_length must be an integer power of 2.");
			System.out.println(syntax_string);
		  } // if
		  else {
			double[][] d = analyseWaveFile(filename, start_sec, fft_length, true);
			// logarithmic plot of data gets a better graph
			for (int i = 0; i < d.length; i++) {
			  d[i][1] = Math.log(d[i][1] / (double)fft_length);
			} // for
			ShowAnalysisWindow(d);
		  } // else

  
  }
 // WaveFileAnalysis
}