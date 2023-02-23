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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * Combines several FloatInputStreams as one FloatInputStream whith as many
 * channels. In the Merger, the source can give different amounts of data, the
 * target is always parallel (every time the same amount of output data in all
 * streams or channels). The float[][] array passed needs to have as many
 * channels (in dim. 1) as the FISMerger has.
 * 
 * @author Nicolai Strauch, Tillman Weyde
 */
public class FISMerger implements FloatInputStream {

	/** The list of streams to merge. */
	FISListElement streamList = new FISListElement();
	/** The list of listeners for the streams. */
	List<StreamEndListener> streamEndListeners = new ArrayList<StreamEndListener>();

	private int totalChannels;

	private int bufferSize = 16384;

	private void notifyStreamListeners(final FloatInputStream fis) {
		new Thread() {

			@Override
			public void run() {
				for (Iterator<StreamEndListener> iter = streamEndListeners.iterator(); iter.hasNext();) {
					StreamEndListener listener = iter.next();
					listener.streamEnded(fis);
				}

			}
		}.start();
	}

	/**
	 * Get the AudioFormat of this stream. The result is only maningful, if all
	 * added FloatInputStream have the same AudioFormat. Returns the AudioFormat
	 * of the last added FloatInputStream. When adding a stream, only the
	 * channelnummer is updated with the number of channels.
	 * 
	 * @see FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		if (streamList.fis == null) {
			return FloatInputStream.DefaultFormat;
		}
		AudioFormat in = streamList.fis.getFormat();
		return new AudioFormat(in.getEncoding(), in.getSampleRate(), in.getSampleSizeInBits(),
								totalChannels, in.getSampleSizeInBits() / 8 * totalChannels, in
										.getFrameRate(), in.isBigEndian());
	}

	/**
	 * Get the number of channels.
	 * 
	 * @return the number of channels.
	 */
	public synchronized int getChannelNum() {
		return totalChannels;
	}

	/**
	 * @see FloatInputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		streamList.reset();
		streamList.setLoadToZero();
		readSamples = 0;
	}

	/**
	 * Adds a FloatInputStream to the list.
	 * 
	 * @param fis The stream to add.
	 */
	public synchronized void add(FloatInputStream fis) {
		totalChannels += fis.getFormat().getChannels();
		streamList.add(fis);
	}

	/**
	 * Delete an FloatInputStream from the contained List.
	 * 
	 * @param fis - FloatInputStream to be deleted
	 * @return boolean - true if the FloatInputStream was found and deleted,
	 *         else false.
	 */
	public synchronized boolean delete(FloatInputStream fis) {
		if (streamList.fis == null)
			return false; // if the List is empty
		if (fis.equals(streamList.fis)) // it's the first fis?
		{
			if (streamList.nextFIS != null) {
				streamList.fis = streamList.nextFIS.fis;
				streamList.buffer = streamList.nextFIS.buffer;
				streamList.alive = streamList.nextFIS.alive;
				streamList.fullyLoaded = streamList.nextFIS.fullyLoaded;
				streamList.loadedSamples = streamList.nextFIS.loadedSamples;
				streamList.toSkip = streamList.nextFIS.toSkip;
				streamList.nextFIS = streamList.nextFIS.nextFIS;
			} else {
				streamList.fis = null;
			}
		} else if (!streamList.delete(fis))
			return false;
		totalChannels -= fis.getFormat().getChannels();
		return true;
	}

	/**
	 * return read(data, 0, data[0].length)
	 * 
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float)
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * Merge the channels of the contained FloatInputStream in the
	 * Outputchannels. The first Channel (0) of the first added FloatInputStream
	 * will be channel 0 in the output, and so on, in order of
	 * FloatInputStream-addition. While any FloatInputStream have data to be
	 * read, the data will be read. All FloatInputStreams that allready have
	 * returned -1 are ignored, and fill them place in data[][] with zeros.
	 * 
	 * @param data get the samples
	 * @param start first Sample to be read
	 * @param len Sampels to be read
	 * @return int the number of written data into data[][], -1 if not
	 *         FloatInputStream are available, or all FloatInputStream have
	 *         returned -1.
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float, int, int)
	 */
//	public synchronized int read(float[][] data, int start, int len) throws IOException {
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		if (streamList.fis == null)
			return -1; // if the List is empty
		if (remainToSkip) {
			if (!streamList.trySkip()) {
				// @@ was wird in read gemacht, wenn nicht alle geskippt?
				// return -1; // TODO: oder lieber 0? Damit später weiter
				// versucht werden kann?
				return 0;
			} else
				remainToSkip = false;
		}
		minLoadSamples = Integer.MAX_VALUE;
		readInBuffer = readSamples % bufferSize;
		existFISwithLife = false; // streamList.mixOn(...) let us see, if any
		// FIS have life.
		// int ch = totalChannels > data.length ? data.length : totalChannels;
		// // TODO: why was this
		// never read ?
		streamList.mixOn(data, start, len, totalChannels);
		// int loadedIntoOut = minLoadSamples - readSamples; // is no more
		// actuall, if I load
		// into buffer so many samples I can. // how many Samples exist at
		// minimum now, minus the
		// number of allways get samples
		// readSamples = minLoadSamples; // now are get the littlest number
		// of get samples It is
		// not right!!!! littlestLoadSample is not the number of read samples!!!
		readSamples += readLen;
		// assert readSamples <= bufferSize;
		// if(readSamples == bufferSize){ // macht nur sinn, wenn readSamples
		// die Stelle in buffer
		// angibt. Nun gibt das aber die Anzalh abgegebener Samples an.
		// System.out.println("readSamples settet to 0.");
		// readSamples = 0;
		// streamList.setLoadToZero();
		// }
		// System.out.println("Merger: "+readLen);
		if (!existFISwithLife) // if all FISListElement-elements are death,or
			// all contained FIS have returned -1
			return -1; // data contain only zeros. We are on the end.
		return readLen;
	}

	/**
	 * Allways skip so many elements as the parameter give to try. While not so
	 * many samples are skipped in all elements of the List, read(...) not will
	 * get any new data. return the parameter n, or -1 if the List is empty, or
	 * a FloatInputStream are empty (or not can be read?)
	 * 
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#skip(long)
	 */
	@Override
	public synchronized long skip(long n) throws IOException {
		if (streamList.fis == null)
			return -1; // if the List is empty
		readSamples += n;
		streamList.skip(n);
		return n;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return streamList.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return readSamples;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		reset();
		streamList.setPositionInSamples(newPos);
	}

	/**
	 * All StreamEndListeners
	 * 
	 * @return the StreamEndListeners
	 */
	public StreamEndListener[] getStreamEndListeners() {
		// StreamEndListener[] listeners = new
		// StreamEndListeners[streamEndListeners.size()];
		// return listeners;
		return streamEndListeners.toArray(new StreamEndListener[]{});
	}

	/**
	 * Add a StreamEndListener to the listeners' List
	 * 
	 * @param listener to add to the List
	 */
	public void addStreamEndListener(StreamEndListener listener) {
		if(DebugState.DEBUG_AUDIO)
			System.out.println(getClass() + " adding StreamEndListener: " + listener.getClass());
		streamEndListeners.add(listener);
	}

	/**
	 * Removes the given listener from the list
	 * 
	 * @param listener to be removed
	 */
	public void removeStreamEndListener(StreamEndListener listener) {
		streamEndListeners.remove(listener);
	}

	/**
	 * Return the channelnumber at that the first channel of the given
	 * FloatInputStream is placed by mixOn(...) in float[][] array Example: we
	 * have 4 FloatInputStreams: 1 - 2 channels 2 - 4 channels 3 - 1 channel 4 -
	 * 2 channels For the first FIS 0 is returned, for the second 2, for the
	 * third 6, and so on.
	 * 
	 * @param fis
	 * @return
	 */
	public synchronized int getChannelOffset(FloatInputStream fis) {
		int out = streamList.getChannelOffset(fis, 0);
		return out;
	}

	/**
	 * return all contained FloatInputStreams in the order of addition. The
	 * first added FloatInputStream at position 0.
	 * 
	 * @return A vector containing all streams.
	 */
	public synchronized Vector<FloatInputStream> getFloatInputStreams() {
		Vector<FloatInputStream> vector = new Vector<FloatInputStream>();
		streamList.getFloatInputStreams(vector);
		return vector;
	}

//	private int FISListLen = 0; // the number of contained FloatInputStreams in
	// the FISListElement

	int readLen = 0; // the samples ready to read, and the samples
	// really read, the value returned in read(...)
	boolean remainToSkip = false; // true if one FIS in FISListElement not
	// have skipped all samples requested to skip in skip(...)
	int readSamples = 0; // Samples get out
	int minLoadSamples; // smallest load samples (samples available =
	// littleLoadSamples - readSamples)
	int readInBuffer; // samples get out in the actual buffer; this value is
	// updated for all list elements before mixOn(...) is invoked in read(...)

	boolean existFISwithLife; // FISListElement.mixOn(...) set this to true,

	// if

	// one of

	// the elements in

	// FISListElement have true at the Value FISListElement.alive

	/**
	 * The List is empty, if fis==null
	 * 
	 * @author Nicolai Strauch
	 */
	private class FISListElement {

		/** The stream of this element */
		FloatInputStream fis;
		private float[][] buffer; // buffer of samples from fis
		private FISListElement nextFIS; // next list element
		private int loadedSamples = 0; // total load samples
		private boolean fullyLoaded; // remember if the buffer is fully loaded;
		// true if the buffer was loaded, and not read
		// boolean canBeDeleted = false; true if fis returns -1, and all
		// buffer in buffer[][] was used
		private boolean alive = true; // if false, no more data is updated
		// into the buffer; false if FIS returned -1;

		FISListElement() {
		}

		/**
		 * Create an new Element for the List, used by add(...)
		 * 
		 * @param n By add(...) it will be the currently next list element
		 * @param f the currently fis
		 * @param d the currently float[][] buffer
		 * @param ls the currently loaded samples
		 */
		FISListElement(FISListElement n, FloatInputStream f, float[][] d, int ls) {
			nextFIS = n;
			fis = f;
			buffer = d;
			loadedSamples = ls;
		}

		/**
		 * nextFIS.fis never can be null, and fis only can be null, if nextFIS
		 * is null
		 * 
		 * @param newFis
		 */
		void add(FloatInputStream newFis) {
			if (fis != null)
				nextFIS = new FISListElement(nextFIS, fis, buffer, loadedSamples);
			fis = newFis;
			// System.out.println(fis);
			buffer = new float[fis.getFormat().getChannels()][bufferSize];
			loadedSamples = readSamples; // so it begin at the point at that
			// the
			// other streams are.
		}

		/**
		 * Recursively fills the <code>array</code> with buffer from the FISs.
		 * Recursion over chOffset starts at the highes channel number. By going
		 * down the List, it knows the greates number of samples available in
		 * all input streams, and write this number of samples into from
		 * <code>buffer[][]</code> to <code>array</code>, begining at
		 * <code>from</code>.
		 * 
		 * @param array The buffer to write the buffer to.
		 * @param from The sample offset in <code>array</code>
		 * @param len The maximum number of samples to be written.
		 * @param chOffset The first channel to be written in <code>array</code>
		 * @throws IOException
		 */
		void mixOn(float[][] array, int from, int len, int chOffset) throws IOException {
			// do not read beyound the end of buffer
			readLen = (readInBuffer + len) > buffer[0].length	? (buffer[0].length - readInBuffer)
																: len;
			int readNow = len;
			int loadInBuffer = loadedSamples % bufferSize;
			if (alive) {
				existFISwithLife = true;
				assert readSamples <= loadedSamples; // ist doch undenkbar,
				// wenn man mehr liest, als es bislang zum lesen gab
				if (readSamples == loadedSamples && !fullyLoaded) {
					readNow = fis.read(buffer, loadInBuffer, buffer[0].length - loadInBuffer);
					if (readNow == -1) {
						AudioUtil.fillUp(buffer, 0.0f);
						alive = false;
						readNow = len; // wir tun so, als ob wir belesen wären
						if (DebugState.DEBUG_AUDIO)
							System.out
									.println("FISMerger.FISListElement.mixOn() (evoked in FISMerger.read(...)): Stream endet: "
												+ fis);
						if (streamEndListeners != null)
							// if the Stream is ending, and no more data are in
							// the buffer, the listener must be adviced, so the 
							// fis can be deleted
							notifyStreamListeners(fis);
					}
					if (readNow > 0) {
						loadedSamples += readNow;
						if (loadedSamples % bufferSize == 0)
							fullyLoaded = true;
					}
				}
			}

			minLoadSamples = minLoadSamples > loadedSamples ? loadedSamples : minLoadSamples;
			if (nextFIS != null && chOffset - nextFIS.buffer.length > 0) // if
				// exist
				// next
				// element,
				// and we have enogth
				// channels in array
				nextFIS.mixOn(array, from, len, chOffset - buffer.length);
			else {
				// do not read more samples than are really disponible in the
				// buffer // operation needed
				// only once
				readLen = (minLoadSamples - readSamples) > readLen	? readLen
																	: (minLoadSamples - readSamples);
			}
			chOffset -= buffer.length;
			if (array == null || // ? But it occure, whithout set boolean
				// occuped.
				chOffset < 0 || // new FIs added by running
				chOffset + buffer.length > array.length || readLen < 0 // new
			// FIS
			// added
			// before
			// all
			// other FIS load.
			)
				return;

			for (int i = 0; i < buffer.length; i++) {
				System.arraycopy(buffer[i], readInBuffer, array[chOffset + i], from, readLen);
			}
			if (readLen > 0)
				fullyLoaded = false;
		}

		/**
		 * return all contained FloatInputStreams in the order of addition. The
		 * first added FloatInputStream at position 0.
		 */
		private void getFloatInputStreams(Vector<FloatInputStream> vector) {
			if (nextFIS != null)
				nextFIS.getFloatInputStreams(vector);
			vector.add(fis);
		}

		private boolean delete(FloatInputStream delFis) {
			if (nextFIS == null)
				return false;
			if (delFis.equals(nextFIS.fis)) {
				if (nextFIS.nextFIS != null) {
					nextFIS.fis = nextFIS.nextFIS.fis;
					nextFIS.buffer = nextFIS.nextFIS.buffer;
					nextFIS.alive = nextFIS.nextFIS.alive;
					nextFIS.loadedSamples = nextFIS.nextFIS.loadedSamples;
					nextFIS.fullyLoaded = nextFIS.nextFIS.fullyLoaded;
					nextFIS.toSkip = nextFIS.nextFIS.toSkip;
					nextFIS = nextFIS.nextFIS;
				} else {
					nextFIS = null;
				}
				return true;
			}
			return nextFIS.delete(delFis);
		}

		void reset() throws IOException {
			fis.reset();
			if (nextFIS != null)
				nextFIS.reset();
		}

		/**
		 * Return the channelnumber at that the first channel of the given
		 * FloatInputStream is placed by mixOn(...) in float[][] array
		 * 
		 * @param fis
		 * @param offset - remember the channeloffset
		 * @return
		 */
		public int getChannelOffset(FloatInputStream inpStr, int offset) {
			if (inpStr.equals(fis)) {
				return offset;
			}
			offset += fis.getFormat().getChannels();
			if (nextFIS == null)
				return -1;
			return nextFIS.getChannelOffset(inpStr, offset);
		}

		/**
		 * useful if the FloatInputStream is reset. To ignore all buffer
		 * remaining in the buffer.
		 */
		public void setLoadToZero() {
			loadedSamples = 0;
			if (nextFIS != null)
				nextFIS.setLoadToZero();
		}

		// Methods for the skipping of data:

		// @@todo(nachsehen, dann Kommentar löschen):
		// skip mit längengleichheit der Streams: wie implementiert man das am
		// besten?
		// Problem: sagt man allen vorhandenen FloatInputStreams skip, und es
		// kommen verschiedene
		// Werte zurück, (wird verschieden vile geskippt),
		// darf man nicht bei denen, wo nicht genügend geskippt wurde, das
		// fehlende Feld wieder
		// mit read auffüllen. Man bekäme entweder an der falschen Stelle noch
		// nullen,
		// oder es könnte eine Zeitverschiebung zwischen den Streams entstehen.
		//
		// Lösungsidee: für jeden FloatInutStream merken, wieviel noch geskippt
		// werden muss, wenn nicht genug wurde.
		// allerdings müsste dann bei jedem read abgefragt werden, ob noch etwas
		// geskippt werden
		// müsste.

		// Lösungsversuch:
		/**
		 * Try to skip n Samples. If it cannot skip the samples at this time, it
		 * will try to skip the rest at the next read(...). If any FIS return
		 * -1, it is marked as death, and the StreamListener is advised.
		 * 
		 * @return true if all FloatInputStream already skip anything
		 */
		boolean skip(long n) throws IOException {
			boolean out = true;
			long skipNow = n - (loadedSamples - minLoadSamples);
			// the allready read but not used data do not will be skipped in the
			// respektiv FIS. So at the end all stream have given the some
			// number of data.
			long skipped = 0;
			if (skipNow > 0) {
				skipped = fis.skip(skipNow);
				if (skipped > -1) {
					toSkip += (skipNow - skipped);
				} else {
					out = false;
					AudioUtil.fillUp(buffer, 0.0f);
					alive = false; // mark as death
					skipped = n; // wir tun so, als ob wir belesen wären
					if (DebugState.DEBUG_AUDIO)
						System.out.println("FISMerger.FISListElement.skip(): Stream endet: " + fis);
					if (streamEndListeners != null)
						// if the Stream is ending, and no more data are in the
						// buffer,
						// the listener must be adviced, so the fis can be
						// deleted
						notifyStreamListeners(fis);
				}
				// loadSamples += skipNow;
				// nicht über littlestLoadSample muss geskippt werden, sondern
				// über
			} // TODO!!!!!!!!!! loadSamples ist nicht mehr allein das
			// bestellte,
			// sondern was man laden konnte, nächste Zeile ist Humbug.
			// loadSamples += n;
			if (loadedSamples < readSamples)
				loadedSamples = readSamples;
			if (nextFIS != null)
				return out && nextFIS.skip(n);
			if (toSkip > 0)
				remainToSkip = true;
			return out;
		}

		long toSkip = 0; // remember how many Samples must be skipped, if in

		// this.fis not was skipped

		// so many samples as in the oter fis of this List

		/**
		 * try to skip all Samples that by the last skip(long n) not was skipped
		 * 
		 * @return boolean true if and only if all samples in all listelements
		 *         have toSkip==0
		 */
		boolean trySkip() throws IOException {
			if (toSkip != 0) {
				long skipped = fis.skip(toSkip);
				if (skipped > -1) {
					toSkip -= skipped;
				} else {
					AudioUtil.fillUp(buffer, 0.0f);
					alive = false; // mark as death
					toSkip = 0; // wir tun so, als ob wir belesen wären
					if(DebugState.DEBUG_AUDIO)
						System.out.println("FISMerger.FISListElement.trySkip(): Stream endet: " + fis);
					if (streamEndListeners != null)
						// if the Stream is ending, and no more data are in the
						// buffer,
						// the listener must be adviced, so the fis can be
						// deleted
						notifyStreamListeners(fis);
				}
			}
			if (nextFIS != null)
				return toSkip == 0 && nextFIS.trySkip();
			return toSkip == 0;
		}

		/**
		 * work rekursivly over the List, return the maximal number of samples
		 * from the greatest FIS
		 * 
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
		 */
		public long remainingSamples() {
			if (nextFIS == null)
				return fis.remainingSamples();
			return Math.max(fis.remainingSamples(), nextFIS.remainingSamples());
		}

		/**
		 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
		 */
		public void setPositionInSamples(long newPos) throws IOException {
			if (nextFIS != null)
				nextFIS.setPositionInSamples(newPos);
			fis.setPositionInSamples(newPos);
		}
	} // end of the FISListElement

}