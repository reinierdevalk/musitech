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
 * Make one FloatInputStream disponible to be read from any number of readers.
 * To use this class: create an instance of this class, give her a
 * FloatInputStream. getMultiplexerMember() will get you a FloatInputStream,
 * that read from the given FloatInputStream. getMultiplexerMember() can get so
 * many FloatInputStream that read from the some source, as needed. Important:
 * every Member can not read more data from the source, than [bufferSize more
 * data] than the Member that have read the littlest amoungth of data. So, if
 * one Member not read more data, the other Members will not get more data.
 * If one Member dont will read more data, this Member must be deletet.
 * 
 * @author Nicolai Strauch
 */

// this class was changed very mutch at 3.4.2003. It not longer
// implements FloatInputStream, only the members given by
// getMultiplexerMembers() are FloatInputStreams
public class FISMultiplexer implements FISReader {

    /**
     * Default contructor, NOTE: FloatInputStream must be initialised before
     * use.
     */
    public FISMultiplexer() {
        this(null);
    }

    /**
     * Construct a FISMultiplexer for a FloatInputStream.
     * 
     * @param fis
     */
    public FISMultiplexer(FloatInputStream fis) {
        this(fis, 16384);
    }

    private MultiReaderRingBuffer mrRingBuff;

    public FISMultiplexer(FloatInputStream fis, int buffSize) {
        mrRingBuff = new MultiReaderRingBuffer(fis, buffSize);
        // mrRingBuff.addReader(this); - not more.
    }

    public FISReader setFloatInputStream(FloatInputStream fis) {
        mrRingBuff.setFloatInputStream(fis);
        return this;
    }

    public FloatInputStream getFloatInputStream() {
        return mrRingBuff.getFloatInputStream();
    }

    /**
     * Create and register a new MultiplexerMEmber, that will read from the
     * given FloatInputStream. If one member does not read more, the other
     * Members will not get any more data.
     * 
     * @return FloatInputStream
     */
    public FloatInputStream getMultiplexerMember() {
        Reader newFisM = new Reader(mrRingBuff);
        mrRingBuff.addReader(newFisM);
        return newFisM;
    }

    /**
     * Delete a registert Multiplexer member. So the other members can read, and
     * this member will not stop the reading more.
     * 
     * @param member The FloatInputStream to be deleted.
     * @return boolean true if the Member was deleted, false, if the Member not
     *         was be found.
     */
    public boolean deleteMultiplexerMember(FloatInputStream member) {
        return mrRingBuff.deleteMember(member);
    }

    // @@TODO: is an pause-method, that make an member sleep, useful?
    // A method, that congelate an member, so that the other Members
    // can read data, ignoring the sleeping members. If the paused
    // (sleeping) members awake, they will continue to read at that
    // point, at that the other Members are. maybe it is useful to add
    // a marker to the members, that represent the Status of an member:
    // if it is active, or sleeping, or stopped, sei lá

    class Reader implements FloatInputStream {

        Reader(MultiReaderRingBuffer memFis) {
            mrRingBuff = memFis;
        }
        private MultiReaderRingBuffer mrRingBuff;

        /**
         * Execute read(data, 0, data[0].length);
         * 
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float)
         */
        public int read(float[][] data) throws IOException {
            return read(data, 0, data[0].length);
        }

        /**
         * Write in maximum so many data in data as disponible. Are disponible
         * in maximum BufferSize data more than the amoungth of read data from
         * the member that have read the littlest amoungth of data.
         * 
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float,
         *      int, int)
         */
        public int read(float[][] data, int start, int len) throws IOException {
            if (len < 1)
                return len;
            int didRead = mrRingBuff.read(data, start, len, this);
            if (didRead == 0) {
                if (mrRingBuff.load() == -1)
                    return -1;
                didRead = mrRingBuff.read(data, start, len, this);
            }
            return didRead;
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
         */
        public AudioFormat getFormat() {
            return mrRingBuff.getFormat();
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
         */
        public long skip(long n) throws IOException {
            long didSkip = mrRingBuff.skip(n, this);
            if (didSkip == 0) {
                mrRingBuff.load();
                didSkip = mrRingBuff.skip(n, this);
            }
            return didSkip;
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
         */
        public void reset() throws IOException {
            mrRingBuff.reset();
        }

        public boolean delete() {
            return mrRingBuff.deleteMember(this);
        }

        /**
         * TODO: inexact: must consider buffer
         * 
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
         */
        public long remainingSamples() {
            return getFloatInputStream().remainingSamples();
        }

        /**
         * TODO: inexact: must consider buffer
         * 
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
         */
        public long getPositionInSamples() {
            return getFloatInputStream().getPositionInSamples();
        }

        /**
         * TODO: inexact: must consider buffer
         * 
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
         */
        public void setPositionInSamples(long newPos) throws IOException {
            getFloatInputStream().setPositionInSamples(newPos);
        }

        //@@TODO: sollte man von hier irgendwo aus den RingBuffer vergrößern
        // können?
    }

}