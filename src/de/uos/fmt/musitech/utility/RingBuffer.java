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
package de.uos.fmt.musitech.utility;

import javax.sound.midi.MidiEvent;

/**
 * This class implements a ring buffer.
 * @author Jan Kramer & Tillman 
 */

public class RingBuffer {
	int readIndex = 0; //current read-position
	int writeIndex = 0; //current write-position
	int loaded = 0;
	//number of loaded samples (samples loaded, but not yet read)
	boolean reachedEnd = false;

	Object[] buffer;
	int buffSize;

	/**
	 * 
	 * @param buffSize The buffer size
	 */
	public RingBuffer(int buffSize) {
		this.buffSize = buffSize;
		buffer = new Object[buffSize];
	}

	/**
	 * Fills the buffer with object. Return value is 1,
	 * if object could be written, else 0 (buffer full) 
	 *	 
	 * @return int 0 if buffer full, else 1
	 */
	public synchronized int write(Object object) {
		int didLoad = 0;
		if ((writeIndex + 1) % buffSize == readIndex) {
			return 0;
		} else {
			buffer[writeIndex] = object;
			writeIndex = (writeIndex + 1) % buffSize;
			return 1;
		}

	}

	/**
	 * Returns next Object. This Object will be deleted from buffer,
	 * because readIndex will be increased. To get Object without
	 * deleting it from ringBuffer use get()
	 * 
	 * @return next Object, if buffer not empty, else null
	 */
	public synchronized Object read() {
		if (readIndex == writeIndex)
			return null;
		else {
			Object tempObject = buffer[readIndex];
			readIndex = (readIndex + 1) % buffSize;

			return tempObject;
		}
	}

	/**
	 * Returns next Object without deleting it from the ringBuffer
	 * Use read() to delete returned Object from buffer
	 * 
	 * @return next Object, if buffer not empty, else null
	 */
	public synchronized Object get() {
		if (readIndex == writeIndex)
			return null;
		else {
			Object tempObject = buffer[readIndex];
			
			return tempObject;
		}
	}

	/**
	 * clearBuffer removes all Objects from Ringbuffer
	 * readIndex and writeIndex are set to 0
	 */
	public synchronized void clearBuffer() {
		
		for (int i = 0; i < buffSize; i++) {
			buffer[i] = null;
		}
		readIndex = 0;
		writeIndex = 0;
	}

	/**
	 * Returns the buffer.
	 * @return Object
	 */
	public Object[] getBuffer() {
		return buffer;
	}

	/**
	 * Returns the buffSize.
	 * @return int
	 */
	public int getBuffSize() {
		return buffSize;
	}

	/**
	 * Returns the loadIndex.
	 * @return int
	 */
	public int getLoadIndex() {
		return writeIndex;
	}

	/**
	 * Returns the readIndex.
	 * @return int
	 */
	public int getReadIndex() {
		return readIndex;
	}

}
