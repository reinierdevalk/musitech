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

import de.uos.fmt.musitech.utility.DebugState;

/**
 * This object converts the sample rate of a FloatInputStream by 'decimation', i.e.
 * dropping or repeating samples.
 * 
 * @author Tillman Weyde
 */
public class FISSampleRateDecimator implements FloatInputStream, FISReader {

    private float srcSRbyTrgSR;
    private float sourceSampleRate;
    private float targetSampleRate;
    private FloatInputStream fis;

    // the number of source/target samples read/written
    private long sourceSamples, targetSamples;

    private float[][] buffer; // the first sample remember the last sample read

//    /**
//     * Empty constructor, the FloatInputStream must be set before using read.
//     */
//    public FISSampleRateDecimator() {
//        this(null);
//    }

    /**
     * Create a decimator reading from the given stream.
     * 
     * @param inputStream The stream to read from.
     */
    public FISSampleRateDecimator(FloatInputStream inputStream) {
        this(inputStream, FloatInputStream.DefaultFormat.getSampleRate());
    }

    /**
     * Create a decimator reading from the given stream.
     * 
     * @param inputStream The stream to read from.
     * @param targetSampleRate The sample rate to deliver the output at.
     */
    public FISSampleRateDecimator(FloatInputStream inputStream, float targetSampleRate) {
        this.targetSampleRate = targetSampleRate;
        setFloatInputStream(inputStream);
    }

    /**
     * Read method fills the whole array if data are available.
     *
     * @return the number of fames written to the array. 
     * @see FISSampleRateDecimator#read(float[][], int, int)
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
     */
    @Override
	public int read(float[][] data) throws IOException {
        return read(data, 0, data[0].length);
    }

    // the number of source frames filled in the buffer.
    private int srcBuffered = 0;
    // the number of frames read from the buffer as float
    // may point 'between' samples
    private float fSrcFrms = 0;
    // the number of frames which have been shifted out of the buffer
    // i.e. starting position of the buffer in source frames
    private long shiftSrcFrms = 0;
    // the number of frames which have been written to the target
    private long totTrgFrms = 0;
    // the synchronization interval
    long syncItvl = (long) (10000 * targetSampleRate / sourceSampleRate);

    /**
     * Synchronizes the current positions in the input and output streams to remove
     * rounding errors. Has to be called approx. every synchItvl frames.
     */
    private void sync() {
        // calculate the absolute position in the source stream based on the fSrcFrms
        double dOldTotSrcFrms = (double) shiftSrcFrms + fSrcFrms;
        // calculate the absolute position in the source stream based on the fSrcFrms
        double dNewTotSrcFrms = (double) sourceSampleRate * totTrgFrms / targetSampleRate;
        double error = dOldTotSrcFrms - dNewTotSrcFrms;
//        if (DebugState.DEBUG)
//            System.out.println("Sync error in FisSampleRateDecimator: " + error);
        // slightly adjust the sampling ration
        srcSRbyTrgSR -= (error / syncItvl) * 0.1;
        fSrcFrms = (float) dNewTotSrcFrms - shiftSrcFrms;
        if (fSrcFrms < 0) {
            if (DebugState.DEBUG)
                System.out
                    .println("WARNING: Sync error in FisSampleRateDecimator causes negative index.");
            fSrcFrms = 0;
        }
    }

    /**
     * Reads from the source and writes the resampled data into the data array, using a
     * sample and hold approach (no interpolation). In the case of downsampling, samples
     * are dropped. In the case of upsampling, samples are repeated.
     * 
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int,
     *      int)
     */
    @Override
	public int read(float[][] data, int start, int len) throws IOException {
        // TODO check if direct calculation of src pos would be more efficient than using
        // synch()
        // TODO add additional class for integer ratio resampling
        // TODO rework FISSampleRateConverter to work like this but with
        //		linear interpolation (perhaps use inheritance / delegation)

        // read source frames into the buffer
        int srcLen = fis.read(buffer, srcBuffered, buffer[0].length - srcBuffered);
        // don't add -1 at the end of stream
        if (srcLen > 0)
            // else update the srcPos
            srcBuffered += srcLen;
        int numTrgFrms;
        // loop over the target frames, until data is filled or all source data is used
        for (numTrgFrms = 0; numTrgFrms < len && fSrcFrms < srcBuffered - srcSRbyTrgSR; numTrgFrms++) {
            // src position to read from
            int intSrcFrms = (int) fSrcFrms;
            // for all channels copy the data
            for (int c = 0; c < buffer.length; c++) {
                data[c][start + numTrgFrms] = buffer[c][intSrcFrms];
            }
            // proceed in the source
            fSrcFrms += srcSRbyTrgSR;
        }
        // calculate number of not fully processed samples minus a short overlap
        int buffShift = ((int) fSrcFrms) - 100;
        if (buffShift > 0) {
            srcBuffered -= buffShift;
            // move not processed samples to the front
            for (int c = 0; c < buffer.length; c++)
                System.arraycopy(buffer[c], buffShift, buffer[c], 0, srcBuffered);
            // adjust src sample position
            fSrcFrms -= buffShift;
            // accumulate buffershifts
            shiftSrcFrms += buffShift;
        }
        // TODO improve efficiency by implementing a ring buffer instead of shifting the
        // 		array

        // when there are no samples left to write and
        // the source stream's end is reached, this stream has ended, too.
        if (numTrgFrms < 1 && srcLen < 0) {
            return -1;
        }
        totTrgFrms += numTrgFrms;
        if (shiftSrcFrms % syncItvl < srcLen)
            sync();
        return numTrgFrms;
    }

    /**
     * Sets the float input stream, the content of which shall be resampled.
     * 
     * @see de.uos.fmt.musitech.audio.floatStream.FISReader#setFloatInputStream(de.uos.fmt.musitech.audio.floatStream.FloatInputStream)
     */
    @Override
	public FISReader setFloatInputStream(FloatInputStream inputStream) {
        fis = inputStream;
        sourceSampleRate = inputStream.getFormat().getSampleRate();
        setFrameRate(targetSampleRate);
        buffer = new float[fis.getFormat().getChannels()][4096 + 1];
        srcBuffered = 0;
        fSrcFrms = 0;
        return this;
    }

    /**
     * Get the original FloatInpuptStream from which the data are read.
     * 
     * TODO implement synchronization to compensate for rounding errors.
     * 
     * @see de.uos.fmt.musitech.audio.floatStream.FISReader#getFloatInputStream()
     */
    @Override
	public FloatInputStream getFloatInputStream() {
        return fis;
    }

    /**
     * Set the frame rate of the this stream, adjusting the target framerate.
     * 
     * @param newFrameRate The frameRate of this stream.
     */
    public void setFrameRate(float newFrameRate) {
        targetSampleRate = newFrameRate;
        srcSRbyTrgSR = (float)((double)sourceSampleRate / (double)targetSampleRate);
        syncItvl = (long) (10000 * targetSampleRate / sourceSampleRate);
        totTrgFrms = 0;
        shiftSrcFrms = 0;
        fSrcFrms = 0;
    }

    /**
     * @see de.uos.fmt.musitech.audio.FloatInputStream#skip(long)
     */
    @Override
	public long skip(long n) throws IOException {
        long sourceN = (long) (n / srcSRbyTrgSR);
        long sourceSkipped = fis.skip(sourceN);
        return (long) (sourceSkipped * srcSRbyTrgSR);
    }

    /**
     * @see de.uos.fmt.musitech.audio.FloatInputStream#reset()
     */
    @Override
	public void reset() throws IOException {
        srcBuffered = 0;
        fis.reset();
    }

    /**
     * @see de.uos.fmt.musitech.audio.FloatInputStream#getFormat()
     */
    @Override
	public AudioFormat getFormat() {
        AudioFormat in = fis.getFormat();
        float targetSampleRate = sourceSampleRate / srcSRbyTrgSR;
        AudioFormat out = new AudioFormat(in.getEncoding(), targetSampleRate, in
            .getSampleSizeInBits(), in.getChannels(), in.getFrameSize(), targetSampleRate, in
            .isBigEndian());
        return out;
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
     */
    @Override
	public long remainingSamples() {
        return (long) (fis.remainingSamples() / srcSRbyTrgSR);
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
     */
    @Override
	public long getPositionInSamples() {
        return (long) (fis.getPositionInSamples() / srcSRbyTrgSR);
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
     */
    @Override
	public void setPositionInSamples(long newPos) throws IOException {
        fis.setPositionInSamples((long) (newPos * srcSRbyTrgSR));
    }

}