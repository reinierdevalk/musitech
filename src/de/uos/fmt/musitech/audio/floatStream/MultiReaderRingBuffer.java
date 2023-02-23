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
import java.util.Vector;

import javax.sound.sampled.AudioFormat;

/**
 * @author Nicolai Strauch
 */
public class MultiReaderRingBuffer extends RingBuffer {

    private Vector readers; // reades registered to read from this
                            // MultiReaderRingBuffer
    private Vector samplesReadByReader; // samples that the reader of the some
                                        // position in readers have read
    private long loadedSamples; // total number of samples loaded

    public MultiReaderRingBuffer(FloatInputStream inputStream, int bufferLen) {
        this(inputStream, bufferLen, 4);
    }

    public MultiReaderRingBuffer(FloatInputStream inputStream, int bufferLen, int numberOfMembers) {
        super(inputStream, bufferLen);
        readers = new Vector(numberOfMembers);
        samplesReadByReader = new Vector(numberOfMembers);
    }

    /**
     * Add a reader to the MultiReaderRingBuffer, that can invoke read(...), and
     * so read from this MultiReaderRingBuffer.
     * 
     * @param reader - that will use this MultiReaderRingBuffer.
     */
    public void addReader(Object reader) {
        readers.add(reader);
        samplesReadByReader.add(new Long(lastReaderPos));
    }

    long lastReaderPos = 0; // shortest number of read samples from all readers

    /**
     * setPassedSamples sets the correct value in the samplesReadByReader
     * Vector. It also recalculates the correct lastReaderPos.
     * 
     * @param samplePass
     * @param readerIndex
     */
    private void setPassedSamples(long samplePass, int readerIndex) {
        samplesReadByReader.setElementAt(new Long(samplePass), readerIndex);
        lastReaderPos = Long.MAX_VALUE;
        for (int i = 0; i < samplesReadByReader.size(); i++) {
            long tmpPos = ((Long) (samplesReadByReader.get(i))).longValue();
            if (tmpPos < lastReaderPos)
                lastReaderPos = (int) tmpPos;
        }
        readIndex = (int) (lastReaderPos % buffSize);
        loaded = (int) (loadedSamples - lastReaderPos);

    }

    /**
     * 
     * @param data
     * @param start
     * @param len
     * @param reader the Object that is invoking this read(...)
     * @return int the number of Samples read; -1 if not can read, or if member
     *         do not exist
     */
    public synchronized int read(float[][] data, int start, int len, Object reader) {

        int readerIndex = readers.indexOf(reader);
        if (readerIndex < 0)
            return -1;
        long samplesPassed = ((Long) (samplesReadByReader.get(readerIndex))).longValue();
        readIndex = (int) (samplesPassed % buffSize);
        loaded = (int) (loadedSamples - samplesPassed);
        int read = super.read(data, start, len);
        if (read < 0)
            setPassedSamples(samplesPassed, readerIndex);
        else
            setPassedSamples(samplesPassed + read, readerIndex);
        // @@ TODO: the following part costs ressources, but can be useful for
        // programmers that use bad the Multiplexer. Maybe the Multiplexer must
        // be implemented in another Form?
        if (read == 0) {
            memberControll++;
            if (memberControll > readers.size() * 10)
                System.out
                        .println("If this message appears very often check if all MultiplexerMembers keep reading data.");
        } else
            memberControll = 0;
        return read;
    }
    int memberControll = 0;

    /**
     * Do not use this method, it will allways return -1 !
     */
    @Override
	public synchronized int read(float[][] data, int start, int len) {
        return -1;
    }

    /**
     * Method getFormat.
     * 
     * @return AudioFormat
     */
    public AudioFormat getFormat() {
        return fis.getFormat();
    }

    /**
     * Method skip.
     * 
     * @param n
     * @param samplesReadByReader
     * @return long
     */
    public synchronized long skip(long n, Object reader) {
        int readerIndex = readers.indexOf(reader);
        if (readerIndex < 0)
            return readerIndex;
        long samplePass = ((Long) (samplesReadByReader.get(readerIndex))).longValue();
        readIndex = (int) (samplePass % buffSize);
        loaded = (int) (loadedSamples - samplePass);
        long skipS = super.skip(n);
        if (skipS < 0)
            setPassedSamples(samplePass, readerIndex);
        else
            setPassedSamples(samplePass + skipS, readerIndex);
        return skipS;
    }

    /**
     * Reset the position of all readers to 0.
     * 
     * @throws IOException
     */
    @Override
	public synchronized void reset() throws IOException {
        super.reset();
        lastReaderPos = 0;
        loadedSamples = 0;
    }

    public void setPositionInSamples(long newPos) throws IOException {
        readIndex = loadIndex = loaded = 0;
        getFloatInputStream().setPositionInSamples(newPos);
        newPos = getFloatInputStream().getPositionInSamples();
        loadedSamples = newPos;
        samplesReadByReader.clear();
        for (int i = 0; i < samplesReadByReader.size(); i++) {
            samplesReadByReader.add(new Long(newPos));
        }
    }

    public long getPositionInSamples(Object reader) {
        int readerIndex = readers.indexOf(reader);
        return ((Long) (samplesReadByReader.get(readerIndex))).longValue();
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.RingBuffer#load()
     */
    @Override
	public synchronized int load() {
        if (noMoreDataLoadable)
            return -1;
        readIndex = (int) (lastReaderPos % buffSize);
        int nowLoaded = super.load();
        if (nowLoaded < 0) {
            reachedEnd = false;
            noMoreDataLoadable = true;
        } else
            loadedSamples += nowLoaded;
        return nowLoaded;
    }

    boolean noMoreDataLoadable = false; // true if end of inputStream reached

    /**
     * @param member - multiplexerpart to be deleted
     * @return boolean
     */
    public boolean deleteMember(FloatInputStream member) {
        int memberIndex = readers.indexOf(member);
        if (memberIndex < 0)
            return false;
        readers.removeElementAt(memberIndex);
        samplesReadByReader.removeElementAt(memberIndex);
        return true;
    }

    /**
     * The some as the other read-method, but it read into the channel i from
     * data the channel channels[i] from the source.
     * 
     * TODO: maybe this method is better merged whith the other read-method? If
     * anybody decide, that it is OK so, please delete this lines. (Only one
     * diferences compared to the other read-method)
     * 
     * @param data
     * @param start
     * @param len
     * @param member
     * @param channels
     * @return int the number of Samples read; -1 if not can read, or if member
     *         do not exist
     */
    public synchronized int read(float[][] data, int start, int len, Object reader, int[] channels) {

        int readerIndex = readers.indexOf(reader);
        if (readerIndex < 0)
            return -1;
        long samplesPassed = ((Long) (samplesReadByReader.get(readerIndex))).longValue();
        readIndex = (int) (samplesPassed % buffSize);
        loaded = (int) (loadedSamples - samplesPassed);
        int didRead = super.read(data, start, len, channels);
        if (didRead < 0)
            setPassedSamples(samplesPassed, readerIndex);
        else
            setPassedSamples(samplesPassed + didRead, readerIndex);
        return didRead;
    }

}