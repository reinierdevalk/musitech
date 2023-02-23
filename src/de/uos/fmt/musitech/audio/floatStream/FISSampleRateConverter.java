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
 */
public class FISSampleRateConverter implements FloatInputStream, FISReader {

	private float sourceSR_by_targetSR;
	private float sourceSampleRate;
	private float targetSampleRate;
	private FloatInputStream fis;

	private float[][] buffer;	// the first sample in the buffer,
								// is the last sample that has already been used
	
//	/**
//	 * Default constructor, FloatInputStream must be set befor using this Converter. 
//	 */
//	public FISSampleRateConverter(){
//		this(null);
//	}

	/**
	 * Constructs a converter for the given inputStream that converts to 
	 * the sampling rate given in {@link FloatInputStream#DefaultFormat FloatInputStream.DefaultFormat}.
	 * @param inputStream
	 */
	public FISSampleRateConverter (FloatInputStream inputStream) {
		this(inputStream, FloatInputStream.DefaultFormat.getSampleRate());
	}
	
	public FISSampleRateConverter (FloatInputStream inputStream, float targetSampleRate) {
		this.targetSampleRate = targetSampleRate;
		setFloatInputStream(inputStream);
	}
	
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);		
	}

	private float sa_exactSampleI = 0;
	/**
	 * For the Algorithm:
	 * @see de.uos.fmt.musitech.audio.AudioUtil#arrayResamplert(float[][], int, float[][], int, int, float)
	 * @see FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
	//	if(sourceSR_by_targetSR==1)  // TODO: activate this line?
	//		return fis.read(data, start, len);
		// calculate the length of the input to be read.
		int inLen = (int)(len * sourceSR_by_targetSR);
		if(inLen==0 && len != 0 && sa_exactSampleI>1)
				inLen = (int)sa_exactSampleI+1; // TODO it seems, aliasing-effects occure XXX ???
		
		if(inLen > (buffer[0].length-1)){
			inLen = (buffer[0].length-1);
			len = (int) (inLen / sourceSR_by_targetSR)+1; // TODO: is this exact? (the +1)
		}
		int samplesRead = inLen;
		if(inLen > 0){
			samplesRead = fis.read(buffer, 1, inLen);
		}
		if(samplesRead != inLen){ // if not enogth samples come in, the number of outgoing samples must be recalculated
			if(samplesRead == -1)
				return samplesRead;
			inLen = samplesRead;
			// use variable samplesRead to not must initialise a new variable
			samplesRead = (int) (inLen / sourceSR_by_targetSR)+1; // TODO is this exact? (the +1)
			len = samplesRead>len?len:samplesRead;
		}
		// inLen++;	// now we consider the first sample
		
		int t_totLen = len+start;	// total Length of target data
		int ta_sampleI = start;

		int sa_sampleI = (int) sa_exactSampleI; // index in outArray
		// exact Index in the Samples of the source array (imaginary)
		// (saHz_by_taHz * length in Samples from target = length in Samples from source,
		//  sa_exactSampleI = SampleIndex from source * sourceSR_by_targetSR)
		float distanceFromIndex = 0.0f;
		int s_totLen = inLen+1;
		// sollte nicht s_totLen/sourceSR_by_targetSR == t_totLen/1 sein?
		// nein, da sa_exactSampleI einen rest aufweisen kann..
		// aber der sollte doch on t_totLen einfliessen?
		for (;	sa_sampleI < s_totLen && ta_sampleI < t_totLen; ta_sampleI++) {
			sa_sampleI = (int) sa_exactSampleI;
			if (sa_exactSampleI == sa_sampleI) {
				for (int i = 0; i < data.length; i++)
					data[i][ta_sampleI] = buffer[i][sa_sampleI];
			}
			else
			{
				distanceFromIndex = sa_exactSampleI - sa_sampleI;
				for (int i = 0; i < data.length; i++) {  
					// linear Interpolation
					data[i][ta_sampleI] = buffer[i][sa_sampleI] + ((buffer[i][sa_sampleI + 1] - buffer[i][sa_sampleI]) * distanceFromIndex);
				}
			}
			sa_exactSampleI += sourceSR_by_targetSR;
		}
		if(sa_exactSampleI>=1){
			for(int i=0; i<buffer.length; i++){// copy the last sample
				buffer[i][0] = buffer[i][inLen];
			}
			// remember position after sample remembert
			sa_exactSampleI = sa_exactSampleI - (inLen);
		}
		
		return ta_sampleI-start;
	}

	@Override
	public FISReader setFloatInputStream(FloatInputStream inputStream)
	{
		fis = inputStream;
		sourceSampleRate = inputStream.getFormat().getSampleRate();
		setFrameRate(targetSampleRate);
		buffer = new float[fis.getFormat().getChannels()][4096 + 1];
		return this;
	}
	@Override
	public FloatInputStream getFloatInputStream()
	{
		return fis;
	}
	
	/** 
	 * Set the Frame Rate of the Target
	 * @param newFrameRate The FrameRate of the Output
	 */
	public void setFrameRate(float newFrameRate)
	{
		sourceSR_by_targetSR = sourceSampleRate / newFrameRate;
	}

	/**
	 * @see FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		long sourceN = (long) (n / sourceSR_by_targetSR);
		long sourceSkipped = fis.skip(sourceN); 
		return (long)(sourceSkipped * sourceSR_by_targetSR);
	}
	/**
	 * @see FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		sa_exactSampleI = 0;
		fis.reset();
	}
	/**
	 * @see FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		AudioFormat in = fis.getFormat();
//		float targetSampleRate = sourceSampleRate / sourceSR_by_targetSR;
		AudioFormat out = new AudioFormat(in.getEncoding(), targetSampleRate, in.getSampleSizeInBits(), in.getChannels(), in.getFrameSize(), targetSampleRate, in.isBigEndian());
		return out;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return (long) (fis.remainingSamples() / sourceSR_by_targetSR);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return (long) (fis.getPositionInSamples() / sourceSR_by_targetSR);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		fis.setPositionInSamples((long) (newPos * sourceSR_by_targetSR));		
	}

}
