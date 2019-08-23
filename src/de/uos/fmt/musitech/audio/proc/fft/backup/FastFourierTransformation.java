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
package de.uos.fmt.musitech.audio.proc.fft.backup;

/********************** FastFourierTransformation.java **********************/
/* Datum: 12. Januar 2002
 * Autor: Christian Datzko
 * Copyright: Christian Datzko, 2002
 * E-Mail-Adresse: datzko@t-online.de
 * Programmiersprache und -version: Java(TM) 2 SDK, Standard Edition (1.3.1_01)
 */

/**
 * Basic routines for complex and real Fast Fourier Transformation. It is
 * recommended to use the "safer" routines <CODE>realFFT</CODE>,
 * <CODE>realFFTinv</CODE>, <CODE>complexFFT</CODE> and
 * <CODE>complexFFTinv</CODE>. The basic algorithms come from <I>NUMERICAL
 * RECIPES IN C: THE ART OF SCIENTIFIC COMPUTING (ISBN 0-521-43108-5)</I>.
 * @author Christian Datzko
 * @version 1.0
 */

public class FastFourierTransformation {

  /**
   * Performs a Discrete Fast Fourier Transformation of the real
   * <CODE>data[]</CODE>.
   * Please note that <CODE>data[]</CODE> must have the length of a power of
   * 2, otherwise a <CODE>IllegalArgumentException</CODE> is thrown.<P>
   * The returned data is organizes as follows:<BR>
   * <CODE>data[0]</CODE>: real part at <CODE><I>f</I>=0</CODE> (imaginary
   * part=0)<BR>
   * <CODE>data[1]</CODE>: real part at <CODE><I>f</I>=+/- 1/(2*Delta)</CODE>
   * (combined, imaginary part=0)<BR>
   * <CODE>data[2]</CODE>: real part at <CODE><I>f</I>=1/(N*Delta)</CODE><BR>
   * <CODE>data[3]</CODE>: imaginary part at <CODE><I>f</I>=1/(N*Delta)</CODE>
   * <BR>
   * ...<BR>
   * <CODE>data[2*i]</CODE>: real part at <CODE><I>f</I>=i/(N*Delta)</CODE><BR>
   * <CODE>data[2*i+1]</CODE>: imaginary part at <CODE><I>f</I>=i/(N*Delta)
   * </CODE><BR>
   * ...<BR>
   * <CODE>data[N-2]</CODE>: real part at <CODE><I>f</I>=(N/2-1)/(N*Delta)
   * </CODE><BR>
   * <CODE>data[N-1]</CODE>: imaginary part at <CODE><I>f</I>=(N/2-1)/(N*Delta)
   * </CODE><P>
   * The other values (<CODE><I>f</I>=-(N/2-1)/(N*Delta)</CODE> ..
   * <CODE>-1/(N*Delta)</CODE>) can be calculated using the following
   * symmetry:<BR>
   * <CODE>F[N] = F[N-n]*</CODE> (complex conjugation).<P>
   * Please note that the data is not normalized - you need to multiply each
   * value with 1/N. For further information of this bug in the "Numerical
   * Recipes" algorithm see <I>Tilman Butz: "Fouriertransformation f&uuml;r
   * Fu&szlig;g&auml;nger", Stuttgart, Leipzig, 2000, p. 103</I>.
   * @author Christian Datzko
   * @version 1.0
   * @param data[] the real data array
   * @throws IllegalArgumentException if <CODE>data[]</CODE> does not have the
   * length of a power of 2.
   */
  public static void realFFT(double data[]) {
    if (isPowerOf2(data.length)) {
      double[] d = new double[data.length + 1];
      d[0] = 0.0;
      // copy the array
      for (int i = 0; i < data.length; i++) {
        d[i + 1] = data[i];
      } // for
      // call the fast fourier transformation algorithm
      realft(d, d.length - 1, 1);
      // copy the array back
      for (int i = 1; i < d.length; i++) {
        data[i - 1] = d[i];
      } // for
    } // if
    else
      throw new IllegalArgumentException("The passed array does not have "
                                         + "the length of a Power of 2.");
  } // realFFT(double data[])

  /**
   * Performs an inverse Discrete Fast Fourier Transformation of the real
   * <CODE>data[]</CODE>.
   * Please note that <CODE>data[]</CODE> must have the length of a power of
   * 2, otherwise a <CODE>IllegalArgumentException</CODE> is thrown.
   * @author Christian Datzko
   * @version 1.0
   * @param data[] the real data array
   * @throws IllegalArgumentException if <CODE>data[]</CODE> does not have the
   * length of a power of 2.
   */
  public static void realFFTinv(double data[]) {
    if (isPowerOf2(data.length)) {
      double[] d = new double[data.length + 1];
      d[0] = 0.0;
      // copy the array
      for (int i = 0; i < data.length; i++) {
        d[i + 1] = data[i];
      } // for
      // call the fast fourier transformation algorithm
      realft(d, d.length - 1, -1);
      // copy the array back
      for (int i = 1; i < d.length; i++) {
        data[i - 1] = d[i];
      } // for
    } // if
    else
      throw new IllegalArgumentException("The passed array does not have "
                                         + "the length of a Power of 2.");
  } // realFFTinv(double data[])

  /**
   * Performs a Discrete Fast Fourier Transformation of the complex
   * <CODE>data[]</CODE>.
   * Please note that <CODE>data[]</CODE> must have the length of a power of
   * 2, otherwise a <CODE>IllegalArgumentException</CODE> is thrown.<P>
   * The returned data is organized as follows:<BR>
   * <CODE>data[0]</CODE>: real part at <CODE><I>f</I>=0</CODE><BR>
   * <CODE>data[1]</CODE>: imaginary part at <CODE><I>f</I>=0</CODE><BR>
   * <CODE>data[2]</CODE>: real part at <CODE><I>f</I>=1/(N*Delta)</CODE><BR>
   * <CODE>data[3]</CODE>: imaginary part at <CODE><I>f</I>=1/(N*Delta)</CODE>
   * <BR>
   * ...<BR>
   * <CODE>data[2*i]</CODE>: real part at <CODE><I>f</I>=i/(N*Delta)</CODE><BR>
   * <CODE>data[2*i+1]</CODE>: imaginary part at <CODE><I>f</I>=i/(N*Delta)
   * </CODE><BR>
   * ...<BR>
   * <CODE>data[N-2]</CODE>: real part at <CODE><I>f</I>=(N/2-1)/(N*Delta)</CODE>
   * <BR>
   * <CODE>data[N-1]</CODE>: imaginary part at <CODE><I>f</I>=(N/2-1)/(N*Delta)
   * </CODE><BR>
   * <CODE>data[N]</CODE>: real part at <CODE><I>f</I>=+/- 1/(2*Delta)</CODE>
   * (combined)<BR>
   * <CODE>data[N+1]</CODE>: imaginary part at <CODE><I>f</I>=+/- 1/(2*Delta)
   * </CODE><BR>
   * <CODE>data[N+2]</CODE>: real part at <CODE><I>f</I>=-(N/2-1)/(N*Delta)
   * </CODE><BR>
   * <CODE>data[N+3]</CODE>: imaginary part at
   * <CODE><I>f</I>=-(N/2-1)/(N*Delta)</CODE><BR>
   * ...<BR>
   * <CODE>data[N+2*i]</CODE>: real part at <CODE><I>f</I>=-(N/2-i)/(N*Delta)
   * </CODE><BR>
   * <CODE>data[N+2*i+1]</CODE>: imaginary part at
   * <CODE><I>f</I>=-(N/2-i)/(N*Delta)</CODE><BR>
   * ...<BR>
   * <CODE>data[2*N-2]</CODE>: real part at <CODE><I>f</I>=-1/(N*Delta)</CODE>
   * <BR>
   * <CODE>data[2*N-1]</CODE>: imaginary part at <CODE><I>f</I>=-1/(N*Delta)
   * </CODE>.<P>
   * Please note that the data is not normalized - you need to multiply each
   * value with 1/N. For further information of this bug in the "Numerical
   * Recipes" algorithm see <I>Tilman Butz: "Fouriertransformation f&uuml;r
   * Fu&szlig;g&auml;nger", Stuttgart, Leipzig, 2000, p. 103</I>.
   * @author Christian Datzko
   * @version 1.0
   * @param data[] the complex data array - the complex part of each number
   * follows directly after the real part.
   * @throws IllegalArgumentException if <CODE>data[]</CODE> does not have the
   * length of a power of 2.
   */
  public static void complexFFT(double data[]) {
    if (isPowerOf2(data.length)) {
      double[] d = new double[data.length + 1];
      d[0] = 0.0;
      // copy the array
      for (int i = 0; i < data.length; i++) {
        d[i + 1] = data[i];
      } // for
      // call the fast fourier transformation algorithm
      four1(d, (d.length - 1) / 2, 1);
      // copy the array back
      for (int i = 1; i < d.length; i++) {
        data[i - 1] = d[i];
      } // for
    } // if
    else
      throw new IllegalArgumentException("The passed array does not have "
                                         + "the length of a Power of 2.");
  } // complexFFT(double data[])

  /**
   * Performs an inverse Discrete Fast Fourier Transformation of the complex
   * <CODE>data[]</CODE>.
   * Please note that <CODE>data[]</CODE> must have the length of a power of
   * 2, otherwise a <CODE>IllegalArgumentException</CODE> is thrown.
   * This data is not normalized, you need to multiply it with
   * <CODE>1/N</CODE>.<P>
   * The returned data is organized as follows:<BR>
   * <CODE>data[0]</CODE>: real part at <CODE><I>t</I>=0</CODE><BR>
   * <CODE>data[1]</CODE>: imaginary part at <CODE><I>t</I>=0</CODE><BR>
   * ...<BR>
   * <CODE>data[2*i]</CODE>: real part at <CODE><I>t</I>=i*Delta</CODE><BR>
   * <CODE>data[2*i+1]</CODE>: imaginary part at <CODE><I>t</I>=i*Delta</CODE><BR>
   * ...<BR>
   * <CODE>data[2*N-2]</CODE>: real part at <CODE><I>t</I>=(N-1)*Delta</CODE>
   * <BR>
   * <CODE>data[2*N-1]</CODE>: imaginary part at <CODE><I>t</I>=-(N*1)*Delta
   * </CODE>.<P>
   * @author Christian Datzko
   * @version 1.0
   * @param data[] the complex data array - the complex part of each number
   * follows directly after the real part.
   * @throws IllegalArgumentException if <CODE>data[]</CODE> does not have the
   * length of a power of 2.
   */
  public static void complexFFTinv(double data[]) {
    if (isPowerOf2(data.length)) {
      double[] d = new double[data.length + 1];
      d[0] = 0.0;
      // copy the array
      for (int i = 0; i < data.length; i++) {
        d[i + 1] = data[i];
      } // for
      // call the fast fourier transformation algorithm
      four1(d, (d.length - 1) / 2, -1);
      // copy the array back
      for (int i = 1; i < d.length; i++) {
        data[i - 1] = d[i];
      } // for
    } // if
    else
      throw new IllegalArgumentException("The passed array does not have "
                                         + "the length of a Power of 2.");
  } // complexFFTinv(double data[])

  /**
   * Checks recursivly, if i is an integer power of 2.
   * @author Christian Datzko
   * @version 1.0
   * @param i the long integer to be checked
   */
  public static boolean isPowerOf2(long i) {
    if (i % 2 == 0)                     // the last bit == 0
      return isPowerOf2(i / 2);         // i is power of 2 if i/2 is power of 2
    else                                // the last bit == 1
      if (i == 1)                       // 2^0 = 1
        return true;
      else                              // i == 1+j*2 (j!=0)
        return false;
  } // isPowerOf2(long i)

  /**
   * Discrete Fourier transformation, as described in <I>NUMERICAL RECIPES IN
   * C: THE ART OF SCIENTIFIC COMPUTING (ISBN 0-521-43108-5), pp 507 ff</I>.
   * You should not call this method but the capsulating methods
   * <CODE>realFFT</CODE>, <CODE>realFFTinv</CODE>, <CODE>complexFFT</CODE> and
   * <CODE>complexFFTinv</CODE>.<P>
   * Original comment:<BR>
   * Replaces <CODE>data[1..2*nn]</CODE> by its discrete Fourier transform, if
   * <CODE>isign</CODE> is input as <CODE>1</CODE>; or replaces
   * <CODE>data[1..2*nn]</CODE> by <CODE>nn</CODE> times its inverse discrete
   * Fourier transform, if <CODE>isign</CODE> is input as <CODE>-1</CODE>.
   * <CODE>data</CODE> is a complex array of length <CODE>nn</CODE> or,
   * equivalently, a real array of length <CODE>2*nn</CODE>. <CODE>nn</CODE>
   * MUST be an integer power of 2 (this is not checked for!).<P>
   * Please note that the data for <CODE>isign = 1</CODE> is not normalized -
   * you need to multiply each value with 1/N. For further information of this
   * bug in the "Numerical Recipes" algorithm see <I>Tilman Butz:
   * "Fouriertransformation f&uuml;r Fu&szlig;g&auml;nger", Stuttgart, Leipzig,
   * 2000, p. 103</I>.
   * @author Numerical Recipes Software (adapted by Christian Datzko)
   * @version 1.0
   * @param data[] the data
   * @param nn data size
   * @param isign <CODE>1</CODE>: discrete Fourier transformation,
   * <CODE>-1</CODE>: inverse descrete Fourier transformation.
   */
  public static void four1(double data[], int nn, int isign) {
  /* C-code:

  #define SWAP(a, b) tempr=(a); (a)=(b); (b)=tempr;

  void four1(float data[], unsigned long nn, int isign) {
    unsigned long n, mmax, m, j, istep, i;
    double wtemp, wr, wpr, wpi, wi, theta;
                                        // Double precision for the
                                        // trigonometric recurrences.
    float tempr, tempi;

    n = nn << 1;
    j = 1;
    for (i = 1; i < n; i += 2) {        // This is the bit-reversal section
      if (j > i) {                      // of the routine.
        SWAP(data[j], data[i]);         // Exchange the two complex numbers.
        SWAP(data[j+1], data[i+1]);
      } // if
      m = n >> 1;
      while (m >= 2 && j > m) {
        j -= m;
        m >>= 1;
      } // while
      j += m;
    } // for
    // Here begins the Danielson-Lanczos section of the routine.
    mmax = 2;
    while (n > mmax) {                  // Outer loop executed log2 nn times.
      istep = mmax << 1;
      theta = isign * (6.28318530717959/mmax);
                                        // Initialize the trigonometric
                                        // recurrence.
      wtemp = sin(0.5*theta);
      wpr = -2.0 * wtemp * wtemp;
      wpi = sin(theta);
      wr = 1.0;
      wi = 0.0;
      for (m = 1; m < mmax; m += 2) {   // Here are the two nested inner loops.
        for (i = m; i <= n; i += istep) {
          j = i + mmax;                 // This is the Danielson-Lanczos
                                        // formula:
          tempr = wr * data[j] - wi * data[j + 1];
          tempi = wr * data[j + 1] + wi * data[j];
          data[j] = data[i] - tempr;
          data[j + 1] = data[i + 1] - tempi;
          data[i] += tempr;
          data[i + 1] += tempi;
        } // for
        wr = (wtemp = wr) * wpr - wi * wpi + wr;
        wi = wi * wpr + wtemp * wpi + wi;
      } // for
      mmax = istep;
    } // while
  } // four1(float data[], unsigned long nn, int isign)
  */
    int n, mmax, m, j, istep, i;
    double wtemp, wr, wpr, wpi, wi, theta;
                                        // Double precision for the
                                        // trigonometric recurrences.
    double tempr, tempi;

    n = nn << 1;
    j = 1;
    for (i = 1; i < n; i += 2) {        // This is the bit-reversal section
      if (j > i) {                      // of the routine.
        double temp = data[j];          // Exchange the two complex numbers.
        data[j] = data[i];
        data[i] = temp;
        temp = data[j+1];
        data[j+1] = data[i+1];
        data[i+1] = temp;
      } // if
      m = n >> 1;
      while (m >= 2 && j > m) {
        j -= m;
        m >>= 1;
      } // while
      j += m;
    } // for
    // Here begins the Danielson-Lanczos section of the routine.
    mmax = 2;
    while (n > mmax) {                  // Outer loop executed log2 nn times.
      istep = mmax << 1;
      // Initialize the trigonometric recurrence.
      theta = isign * (2.0 * Math.PI/mmax);
      wtemp = Math.sin(0.5*theta);
      wpr = -2.0 * wtemp * wtemp;
      wpi = Math.sin(theta);
      wr = 1.0;
      wi = 0.0;
      for (m = 1; m < mmax; m += 2) {   // Here are the two nested inner loops.
        for (i = m; i <= n; i += istep) {
          j = i + mmax;                 // This is the Danielson-Lanczos
                                        // formula:
          tempr = wr * data[j] - wi * data[j + 1];
          tempi = wr * data[j + 1] + wi * data[j];
          data[j] = data[i] - tempr;
          data[j + 1] = data[i + 1] - tempi;
          data[i] += tempr;
          data[i + 1] += tempi;
        } // for
        // Trigonometic recurrence.
        wr = (wtemp = wr) * wpr - wi * wpi + wr;
        wi = wi * wpr + wtemp * wpi + wi;
      } // for
      mmax = istep;
    } // while
  } // four1(float data[], long nn, int isign)

  /**
   * Discrete Fourier transformation of two real arrays simultaniously, as
   * described in <I>NUMERICAL RECIPES IN C: THE ART OF SCIENTIFIC COMPUTING
   * (ISBN 0-521-43108-5), pp 511 ff</I>.
   * You should not call this method but the capsulating methods
   * <CODE>realFFT</CODE>, <CODE>realFFTinv</CODE>, <CODE>complexFFT</CODE> and
   * <CODE>complexFFTinv</CODE>.<P>
   * Original comment:<BR>
   * Given two real input arrays <CODE>data1[1..n]</CODE> and
   * <CODE>data2[1..n]</CODE>, this routine calls <CODE>four1</CODE> and
   * returns two complex output arrays, <CODE>fft1[1..2n]</CODE> and
   * <CODE>fft2[1..2n]</CODE>, each of complex length <CODE>n</CODE> (i.e.,
   * real length <CODE>2*n</CODE>), which contain the discrete Fourier
   * transforms of the respective <CODE>data</CODE> arrays. <CODE>n</CODE> MUST
   * be an integer power of 2.<P>
   * Please note that the data for <CODE>isign = 1</CODE> is not normalized -
   * you need to multiply each value with 1/N. For further information of this
   * bug in the "Numerical Recipes" algorithm see <I>Tilman Butz:
   * "Fouriertransformation f&uuml;r Fu&szlig;g&auml;nger", Stuttgart, Leipzig,
   * 2000, p. 103</I>.
   * @author Numerical Recipes Software (adapted by Christian Datzko)
   * @version 1.0
   * @param data1[] the data of the first function
   * @param data2[] the data of the second function
   * @param fft1[] the FFT of the first function
   * @param fft2[] the FFT of the second function
   * @param n data size
   */
  public static void twofft(double data1[], double data2[], double fft1[],
                            double fft2[], int n) {
  /* C-Code:

    void twofft(float data1[], float data2[], float fft1[], float fft2[],
      unsigned long n)
    unsigned long nn3, nn2, jj, j;
    float rep, rem, aip, aim;

    nn3=1+(nn2=2+n+n);
    for (j=1, jj=2; j<=n; j++, jj+=2) { // Pack the two real arrays into one
      fft1[jj-1]=data1[j];              // complex array.
      fft1[jj]=data2[j];
    }
    four1(fft1, n, 1);                  // Transform the complex array.
    fft2[1]=fft1[2];
    fft1[2]=fft2[2]=0.0;
    for (j=3; j<=n+1; j+=2) {
      rep=0.5*(fft1[j]+fft1[nn2-j]);    // Use symmetries to separate the two
      rem=0.5*(fft1[j]-fft1[nn2-j]);    // transforms.
      aip=0.5*(fft1[j+1]+fft1[nn3-j]);
      aim=0.5*(fft1[j+1]-fft1[nn3-j]);
      fft1[j]=rep;                      // Ship them out in two complex arrays.
      fft1[j+1]=aim;
      fft1[nn2-j]=rep;
      fft1[nn3-j]= -aim;
      fft2[j]=aip;
      fft2[j+1]= -rem;
      fft2[nn2-j]=aip;
      fft2[nn3-j]=rem;
    }
  }
  */
    int nn3, nn2, jj, j;
    double rep, rem, aip, aim;

    nn3 = 1 + (nn2 = 2 + n + n);
    // Pack the two real arrays into one complex array.
    for (j = 1, jj = 2; j <= n; j++, jj += 2) {
      fft1[jj - 1] = data1[j];
      fft1[jj] = data2[j];
    } // for

    four1(fft1, n, 1);                  // Transform the complex array.
    fft2[1] = fft1[2];
    fft1[2] = fft2[2] = 0.0;
    for (j = 3; j <= n + 1; j += 2) {
      // Use symmetries to separate the two transforms.
      rep = 0.5 * (fft1[j] + fft1[nn2 - j]);
      rem = 0.5 * (fft1[j] - fft1[nn2 - j]);
      aip = 0.5 * (fft1[j + 1] + fft1[nn3 - j]);
      aim = 0.5 * (fft1[j + 1] - fft1[nn3 - j]);
      // Ship them out in two complex arrays.
      fft1[j] = rep;
      fft1[j + 1] = aim;
      fft1[nn2 - j] = rep;
      fft1[nn3 - j] = -aim;
      fft2[j] = aip;
      fft2[j + 1] = -rem;
      fft2[nn2 - j] = aip;
      fft2[nn3 - j] = rem;
    } // for
  } // twofft(double data1[], double data2[], double fft1[], double fft2[], long n);

  /**
   * Discrete Fourier transformation of one real array, as described in
   * <I>NUMERICAL RECIPES IN C: THE ART OF SCIENTIFIC COMPUTING (ISBN
   * 0-521-43108-5), pp 513 ff</I>.
   * You should not call this method but the capsulating methods
   * <CODE>realFFT</CODE>, <CODE>realFFTinv</CODE>, <CODE>complexFFT</CODE> and
   * <CODE>complexFFTinv</CODE>.<P>
   * Original comment:<BR>
   * Calculates the Fourier transform of a set of <CODE>n</CODE>
   * real-valued data points. Replaces the data (which is stored in array
   * <CODE>data[1..n]</CODE> by the positive frequency half of its complex
   * Fourier transform. The real-valued first and last components of the
   * complex transform are returned as elements <CODE>data[1]</CODE> and
   * <CODE>data[2]</CODE>, respectively. <CODE>n</CODE> must be a power of 2.
   * This routine also calculates the inverse transform of a complex data
   * array if it is the transform of real data. (Result in this case must be
   * multiplied by 2/n.)<P>
   * Please note that the data for <CODE>isign = 1</CODE> is not normalized -
   * you need to multiply each value with 1/N. For further information of this
   * bug in the "Numerical Recipes" algorithm see <I>Tilman Butz:
   * "Fouriertransformation f&uuml;r Fu&szlig;g&auml;nger", Stuttgart, Leipzig,
   * 2000, p. 103</I>.
   * @author Numerical Recipes Software (adapted by Christian Datzko)
   * @version 1.0
   * @param data[] the data
   * @param n data size
   * @param isign <CODE>1</CODE>: discrete Fourier transformation,
   * <CODE>-1</CODE>: inverse descrete Fourier transformation.
   */
  public static void realft(double data[], int n, int isign) {
  /* C-Code:

  #include <math.h>

  void realft(float data[], unsigned long n, int isign)
  {
    void four1(float data[], unsigned long nn, int isign);
    unsigned long i, i1, i2, i3, i4, np3;
    float c1=0.5,c2,h1r,h1i, h2r, h2i;
    double wr, wi, wpr, wpi, wtemp, theta;
                                        // Double precision for the
                                        // trigonometric recurrences.
    theta=3.141592653589793/(double) (n>>1);
                                        // Initialize the recurrence.
    if (isign == 1) {
      c2 = -0.5;
      four1(data, n>>1, 1);
    } else {
      c2=0.5;
      theta = -theta;
    }
    wtemp = sin(0.5*theta);
    wpr = -2.0*wtemp*wtemp;
    wpi = sin(theta);
    wr = 1.0+wpr;
    wi = wpi;
    np3 = n+3;
    for (i=2; i<=(n>>2); i++) {         // Case i = i done separately below.
      i4 = 1 + (i3=np3-(i2=1+(i1=i+i-1)));
      h1r=c1*(data[i1]+data[i3]);       // The two separate transforms are
      h1i=c1*(data[i2]-data[i4]);       // separated out of data
      h2r= -c2*(data[i2]+data[i4]);
      h2i=c2*(data[i1]-data[i3]);
      data[i1]=h1r+wr*h2r-wi*h2i;       // Here they are recombined to form
      data[i2]=h1i+wr*h2i+wi*h2r;       // the true transform of the original
      data[i3]=h1r-wr*h2r+wi*h2i;       // real data.
      data[i4]= -h1i+wr*h2i+wi*h2r;
      wr = (wtemp=wr)*wpr-wi*wpi+wr;    // The recurrence.
      wi = wi*wpr+wtemp*wpi+wi;
    }
    if (isign == 1) {
      data[1] = (h1r=data[1])+data[2];  // Squeeze the first and last data to-
      data[2] = (h1r-data[2]);          // gether to get them all with the
    }                                   // original array.
    else {
      data[1]=c1*((h1r=data[1])+data[2]);
      data[2]=c1*(h1r-data[2]);
      four1(data,n>>1,-1);              // This is tie hinverse transform for
    }                                   // the case isign = -1.
  }
  */
    int i, i1, i2, i3, i4, np3;
    double c1=0.5,c2,h1r,h1i, h2r, h2i;
    double wr, wi, wpr, wpi, wtemp, theta;
                                        // Double precision for the
                                        // trigonometric recurrences.
    theta=Math.PI / (double)(n >> 1);   // Initialize the recurrence.
    if (isign == 1) {
      c2 = -0.5;
      four1(data, n >> 1, 1);
    } // if
    else {
      c2 = 0.5;
      theta = -theta;
    } // else
    wtemp = Math.sin(0.5 * theta);
    wpr = -2.0 * wtemp * wtemp;
    wpi = Math.sin(theta);
    wr = 1.0 + wpr;
    wi = wpi;
    np3 = n + 3;
    for (i = 2; i <= (n >> 2); i++) {   // Case i = i done separately below.
      i4 = 1 + (i3 = np3 - (i2 = 1 + (i1 = i + i - 1)));
      h1r = c1 * (data[i1] + data[i3]); // The two separate transforms are
      h1i = c1 * (data[i2] - data[i4]); // separated out of data
      h2r = -c2 * (data[i2] + data[i4]);
      h2i = c2 * (data[i1] - data[i3]);
      // Here they are recombined to form the true transform of the original
      // real data.
      data[i1] = h1r + wr * h2r - wi * h2i;
      data[i2] = h1i + wr * h2i + wi * h2r;
      data[i3]= h1r - wr * h2r + wi * h2i;
      data[i4]= -h1i + wr * h2i + wi * h2r;
      // The recurrence.
      wr = (wtemp = wr) * wpr - wi * wpi + wr;
      wi = wi * wpr + wtemp * wpi + wi;
    } // for
    if (isign == 1) {
      // Squeeze the first and last data together to get them all with the
      // original array.
      data[1] = (h1r = data[1]) + data[2];
      data[2] = (h1r - data[2]);
    } // if
    else {
      data[1] = c1 * ((h1r = data[1]) + data[2]);
      data[2] = c1 * (h1r - data[2]);
      four1(data, n >> 1, -1);          // This is the hinverse transform for
    } // else                           // the case isign = -1.
  } // realft(double data[], int n, int isign)
 // FastFourierTransformation
 }