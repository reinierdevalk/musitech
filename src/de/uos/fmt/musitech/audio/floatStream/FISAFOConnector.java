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
/*
 * Created on 23.04.2004
 */

package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * This class manages the playback of multiple AudioFileObjects. To provide the
 * correct starttime of any AudioFileObject, use FISSampleCallback. Itself is a
 * FISSampleCallback, this economises code. Note that the AudioFile Objects has a
 * TimeStamp. Any AudioFileObject will begin to play by them TimeStamp. If all
 * AudioFileObjects have the some TimeStamp, for example 0 (default), so they
 * begin at the some Time. If the Timeposition is greater then the timeposition +
 * length of any AudioFileObject, it do not will be played more. Only if the
 * Timeposition is changed.
 * 
 * @author Nicolai Strauch
 */
public class FISAFOConnector extends FISSampleCallback implements PositionableFIS {

	Map<FloatInputStream,AudioFileObject> streamTable = new HashMap<FloatInputStream,AudioFileObject>(); // contains AudioFileObjects and
	// their FloatInputStreams. The first as value, the FIS as Key

	FISMerger fisMerger;
	FISChannelMixer fisCMixer;

	// End of the last AudioFileObject in frames from the beginning of the piece
	private long endSample;

	private List<FloatInputStream> nextFIStoAdd = new ArrayList<FloatInputStream>(); // the FIS to be added to the

	// merger at nextAddTime

	/**
	 * To use this Class, put a Container whith AudioFileObjects in
	 * setContainer().
	 */
	public FISAFOConnector() {
		this(null);
	}

	/**
	 * @param audioFileObjects
	 */
	public FISAFOConnector(Container<?> audioFileObjects) {
		addSampleReachListener(new SampleReachListener() {

			@Override
			public void sampleReached() {
				connectFIS();
			}

			@Override
			public void streamReset() {
				try {
					position(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		setFIS();
		if (audioFileObjects != null)
			addContainer(audioFileObjects);
	}

	/**
	 * Set the contained FIS to default
	 */
	private synchronized void setFIS() {
		if (DebugState.DEBUG_AUDIO)
			System.out.print("launching setFIS...............................");
		fisMerger = new FISMerger();
		fisCMixer = new FISChannelMixer();

		setFloatInputStream(fisCMixer);

		fisMerger.add(new DummyFloatIS());

		fisMerger.addStreamEndListener(new StreamEndListener() {

			@Override
			public void streamEnded(FloatInputStream fis) {
				boolean deleted = fisMerger.delete(fis);
				if (DebugState.DEBUG_AUDIO)
					System.out.println("AFOManager.this(): StreamEndListener delete fis in Merger,"
										+ " fis deleted? " + deleted);
				updateGainTable();
			}
		});

		fisCMixer.setFloatInputStream(fisMerger);
		if (DebugState.DEBUG_AUDIO)
			System.out.print("..............setFIS ending");
	}

	/**
	 * Load all audioFileObjects from a Container. The AudioFileObjects and the
	 * FIS given by them are loaded into the Hashtable. Other Containables then
	 * AudioFileObjects will be ignored. Extracts the FIS from the
	 * AudioFileObjects, and, if their time is 0, fill them into the merger. The
	 * gainTable for the ChannelMixer is generated. The Mixer is initialised
	 * with the merger.
	 * 
	 * @param audioFileObjects
	 */
	public synchronized void addContainer(Container<?> audioFileObjects) {
		for (Iterator<?> iter = audioFileObjects.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof AudioFileObject) {
				addAudioFileObject((AudioFileObject) element);
			}
		}
	}

	public synchronized void clearAudioFileObjects() {
		streamTable.clear();
		nextFIStoAdd.clear();
		setFIS();
		endSample = 0;
		try {
			super.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateGainTable();
	}

	/**
	 * Adds an AudioFileObject to the Hashtable, creates a FIS, puts the FIS in
	 * the queue to be played at the time stored in AudioFileObject. The object
	 * is played immidiatly, if the time is past, skipping data that have to be
	 * played already. Does not add the AudioFileObject to the intern
	 * afoContainer. (if this will be changed, please note that this method is
	 * used by "setContainer", and the AFOs allready are in the afoContainer.
	 * 
	 * @param afo The AudioFileObject to be played
	 */
	public synchronized void addAudioFileObject(AudioFileObject afo) {
		if (DebugState.DEBUG_AUDIO)
			System.out.println("FISAFOConnector.addAudioFileObject(afo) adding " + afo);
		if (streamTable.containsValue(afo))
			return;
		long sampleTmp = (long) (afo.getTime() * frameRate / 1000000);
		// sample at that this AFO have to begin to play
		// long afosEndSample = afo.getDuration()+sampleTmp;
		long afosEndSample = afo.getLengthInSamples() + sampleTmp;
		// assert afosEndSample > 0; CHECK why this ?
		if (afosEndSample > endSample)
			endSample = afosEndSample;
		FloatInputStream fis = afo.getFloatInputStream();
		fis = AudioUtil.convertFloatInputStream(fis, conversionFormat);
		streamTable.put(fis, afo);

		if (sampleTmp <= getSamplesRead()) {
			try {
				fis.setPositionInSamples(getSamplesRead() - sampleTmp);
				// If the time for the AFO is passed, it can not be added
				// TODO zu ungenau: ob am timer ausrichten? Oder eine Prognose
				// einholen,
				// ab welchem Sample aus dem Merger gelesen wird, also ab dem
				// wievieltem
				// sample dieser stream im merger zu liefern beginen könnte (ab
				// dem nächsten
				// lesen)?
				shortConnectFIS(fis); // add this FIS imediatly
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("AFOManager.addAudioFileObject(...): <" + fis
									+ "> not connected.");
			}
		} else {
			if (sampleTmp == getCallbackSample()) {
				nextFIStoAdd.add(fis);
			} else if (sampleTmp < getCallbackSample()) {
				nextFIStoAdd.clear();
				nextFIStoAdd.add(fis);
				setCallbackSample(sampleTmp);
			}
		}
		if (DebugState.DEBUG_AUDIO)
			System.out.println("AFOManager.addAudioFileObject(...): " +
				"samplesPosition for the new FIS: " + sampleTmp + 
				" actual nextAddS: " + getCallbackSample());

	}

	private AudioFormat audioFormat = FloatInputStream.DefaultFormat;
	// at this format all FIS will be converted
	private AudioFormat conversionFormat = new AudioFormat(audioFormat.getEncoding(), audioFormat
			.getSampleRate(), audioFormat.getSampleSizeInBits(), AudioSystem.NOT_SPECIFIED,
	// channelnumber not have to be converted
															AudioSystem.NOT_SPECIFIED, // framesize
															// -
															// need
															// the
															// channelnumber
															audioFormat.getFrameRate(), audioFormat
																	.isBigEndian());

	private float frameRate = audioFormat.getFrameRate();

	/**
	 * @param channelSetting - contain the number of channels from any
	 *            FloatInputStreams put in the streamTable, in order of putting.
	 * @param totalChannelNumber - the some of all disponible channels from all
	 *            FloatInputStreams in the streamTable
	 * @param numberOfFIS - the number of FloatInputStream put in the
	 *            streamTable
	 */
	private void generateGainTable(int[] channelSetting, int totalChannelNumber) {
		float[][] gainTable = new float[2][totalChannelNumber];
		float fisFaktor = 1.0f; // numberOfFIS;
		float chanFaktor = 0.0f;
		float faktor = 0.0f;
		for (int cs = 0, tcn = 0; cs < channelSetting.length && channelSetting[cs] > 0
									&& tcn < totalChannelNumber; cs++) {
			chanFaktor = 2 / channelSetting[cs];
			for (int ch = 0; ch < channelSetting[cs]; ch++, tcn++) {
				for (int i = 0; i < 2; i++) {
					if (ch == channelSetting[cs] - 1 && ch % 2 == 0) // if
						// the
						// last
						// channel
						// is
						// n%2=1
						faktor = 0.5f;
					else
						faktor = i % 2 == tcn % 2 ? 1.0f : 0.0f;
					gainTable[i][tcn] = fisFaktor * chanFaktor * faktor;
				}
			}
		}
		fisCMixer.setGainTable(gainTable);

		// DEBUG - PART
		if (DebugState.DEBUG_AUDIO) {
			System.out.println("AFOManager.generateGainTable(...): generated gain table: ");
			System.out.print("      ");
			for (int j = 0; j < gainTable[0].length; j++)
				System.out.print("in" + (j + 1) + " | ");
			System.out.println();
			for (int i = 0; i < gainTable.length; i++) {
				System.out.print("out" + (i + 1) + ": ");
				for (int j = 0; j < gainTable[i].length; j++)
					System.out.print(gainTable[i][j] + " | ");
				System.out.println();
			}
		}
		// END DEBUG - PART
	}

	/**
	 * create a new GainTable. And update the Mixer, let it set it's
	 * channelnumbers new.
	 */
	synchronized void updateGainTable1() {
		fisCMixer.update();
		Vector<?> fiss = fisMerger.getFloatInputStreams();
		int numberOfFis = fiss.size();
		int[] channelSetting = new int[numberOfFis];
		for (int i = 0; i < channelSetting.length; i++)
			channelSetting[i] = ((FloatInputStream) fiss.get(i)).getFormat().getChannels();
		generateGainTable(channelSetting, fisMerger.getChannelNum());
	}

	/**
	 * create a new GainTable. And update the Mixer, let it set it's
	 * channelnumbers new.
	 */
	synchronized void updateGainTable() {
		fisCMixer.update();
		Vector<?> fiss = fisMerger.getFloatInputStreams();
		int numberOfFis = fiss.size();
		int[] channelSetting = new int[numberOfFis];
		for (int i = 0; i < channelSetting.length; i++)
			channelSetting[i] = ((FloatInputStream) fiss.get(i)).getFormat().getChannels();
		generateGainTable(fiss.toArray(new FloatInputStream[] {}), fisMerger
				.getChannelNum());
	}

	/**
	 * Generates a gain-table that mixes all FIS down to stereo.
	 * 
	 * @param fisArray - contain the number of channels of all FloatInputStreams
	 *            put into the streamTable, in order of addition.
	 * @param totalChannelNumber - the sum of all available channels from all
	 *            FloatInputStreams in the streamTable
	 * @param numberOfFIS - the number of FloatInputStream put in the
	 *            streamTable
	 */
	private void generateGainTable(FloatInputStream[] fisArray, int totalChannelNumber) {
		float[][] gainTable = new float[2][totalChannelNumber];
		float fisFactor = 1.0f; // numberOfFIS;
		float chanFactor = 0.0f;
		float factor = 0.0f;
		// Iterate over FISs
		for (int fisNum = 0, tcn = 0; fisNum < fisArray.length
										&& fisArray[fisNum].getFormat().getChannels() > 0
										&& tcn < totalChannelNumber; fisNum++) {
			chanFactor = 2 / fisArray[fisNum].getFormat().getChannels();
			float volume = 1;
			float pan = 0;
			AudioFileObject afo = streamTable.get(fisArray[fisNum]);
			if (afo != null) {
				volume = afo.getVolume();
				pan = -afo.getPanorama();
			}
			// Iterate over channels in the FIS
			for (int ch = 0; ch < fisArray[fisNum].getFormat().getChannels(); ch++, tcn++) {
				// Iterate over the two output channels.
				for (int i = 0; i < 2; i++) {
					// if ch is the last channel in the FIS has the FIS has an
					// odd number of channels
					if (ch == fisArray[fisNum].getFormat().getChannels() - 1 && ch % 2 == 0) {
						// share the channel between the last two channels
						if (i == 0)
							factor = volume * 0.5f * (1 - pan);
						else
							factor = volume * 0.5f * (1 + pan);
					} else {
						// distribute the input channels over the 2 output
						// channels
						factor = (i == ch % 2 ? 1.0f : 0.0f);
						if (i == 0 && pan > 0) {
							factor -= pan;
						} else if (i == 1 && pan < 0) {
							factor += pan;
						}
						if (factor < 0)
							factor = 0;
						factor *= volume;
					}
					gainTable[i][tcn] = fisFactor * chanFactor * factor;
				}
			}
		}
		fisCMixer.setGainTable(gainTable);

		// DEBUG Messages
		if (DebugState.DEBUG_AUDIO) {
			System.out.println("AFOManager.generateGainTable(new version): generated gain table: ");
			System.out.print("      ");
			for (int j = 0; j < gainTable[0].length; j++)
				System.out.print("in" + (j + 1) + " | ");
			System.out.println();
			for (int i = 0; i < gainTable.length; i++) {
				System.out.print("out" + (i + 1) + ": ");
				for (int j = 0; j < gainTable[i].length; j++)
					System.out.print(gainTable[i][j] + " | ");
				System.out.println();
			}
		}
		// END DEBUG Messages
	}

	/**
	 * connect the given FIS Actualise the GainTable from the Mixer, and update
	 * the Mixer.
	 * 
	 * @param fis
	 */
	private synchronized void shortConnectFIS(FloatInputStream fis) {
		fisMerger.add(fis);
		updateGainTable();
	}

	/**
	 * Insert all FIS to be insertet at this time into the merger. Actualise the
	 * GainTable from the Mixer, and update the Mixer. Prepare the next FIS to
	 * be added, and set the Advicertime in player.
	 */
	synchronized void connectFIS() {
		// Connection-part
		for (Iterator<FloatInputStream> iter = nextFIStoAdd.iterator(); iter.hasNext();) {
			FloatInputStream fis = iter.next();
			fisMerger.add(fis);
			if (DebugState.DEBUG_AUDIO)
				System.out.println("AFOManager.connectFIS() connect: " + fis);
		}

		updateGainTable();

		setNextFISToAdd();
	}

	/**
	 * Analyse all AudioFileObjects resting, and look for the Object whith the
	 * shortest playbegintime. The FISs from the AudioFileObjects whith the
	 * shortest playbegintime will be the next FISs to be added by connectFIS.
	 * The advicertime in player is settet to the time calculated. Only the
	 * AudioFileObjects whith playbegintime greater then the time played will be
	 * considered.
	 */
	private void setNextFISToAdd() {

		long nextAddSample = Long.MAX_VALUE;

		// remember the shortest sampleValue >0 in the AudioFileObjects
		// (convertet to samples from
		// time in millis)
		nextFIStoAdd.clear();

		long sampleTmp = 0;
		// used by extract the time from the AudioFileObject

		Iterator<AudioFileObject> afos = streamTable.values().iterator();
		Iterator<FloatInputStream> fiss = streamTable.keySet().iterator();
		AudioFileObject afoTmp;
		FloatInputStream fisTmp;

		while (fiss.hasNext()) {
			afoTmp = afos.next();
			fisTmp = fiss.next();
			sampleTmp = afoTmp.getTime(); // first sampleTmp get micross
			sampleTmp = (long) (sampleTmp * frameRate / 1000000);
			// convert millis to samples
			if (sampleTmp > getSamplesRead()) {
				if (sampleTmp == nextAddSample)
					nextFIStoAdd.add(fisTmp);
				if (sampleTmp < nextAddSample) {
					nextFIStoAdd.clear();
					nextFIStoAdd.add(fisTmp);
					nextAddSample = sampleTmp;
				}
			}
		}
		setCallbackSample(nextAddSample);
	}

	/**
	 * Set position to sample 0.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		position(0);
	}

	/**
	 * reload all AFOs, rest all FloatInputStreams, setting to the current
	 * sampleposition.
	 */
	public synchronized void reload() {
		setCallbackSample(Long.MAX_VALUE);
		setFIS();
		resetStreamTable();
		setNextFISToAdd();
	}

	/**
	 * 
	 */
	private void resetStreamTable() {
		if (DebugState.DEBUG_AUDIO)
			System.out.println(streamTable);
		Hashtable<FloatInputStream, AudioFileObject> tmpTable = new Hashtable<FloatInputStream, AudioFileObject>();
		tmpTable.putAll(streamTable);
		streamTable.clear();
		for (Enumeration<AudioFileObject> enumer = tmpTable.elements(); enumer.hasMoreElements();) {
			AudioFileObject afo = enumer.nextElement();
			if (DebugState.DEBUG_AUDIO)
				System.out.println(afo);
			addAudioFileObject(afo);
		}
	}

	/**
	 * return true, if any AFO is contained as FIS in the Merger
	 */
	public boolean afoLoadedIntoMerger() {
		return fisMerger.getChannelNum() > 2; // 2 Channels from the dummyFIS
	}

	/**
	 * @return
	 */
	public float getFrameRate() {
		return frameRate;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	@Override
	public synchronized void position(int n) throws IOException {
		setSamplesRead(n);
		// setNextFISToAdd();
		// connectFIS();
		reload();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position()
	 */
	@Override
	public int position() {
		return (int) getSamplesRead();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		if (DebugState.DEBUG_AUDIO) {
			System.out.println("FISAFOConnector.remainingSamples() return "
								+ (endSample - getSamplesRead()));
		}
		return endSample - getSamplesRead();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		if (DebugState.DEBUG_AUDIO) {
			System.out.println("FISAFOConnector.getPositionInSamples() returned "
								+ getSamplesRead());
		}
		return getSamplesRead();
	}

	/**
	 * @see FloatInputStream#getPositionInSamples()
	 */
	@Override
	public synchronized void setPositionInSamples(long newPos) {
		if (newPos < 0) {
			System.err
					.println("FISAFOConnector.setPositionInSamples(...) negative Argument, set position to 0.");
			newPos = 0;
		}
		try {
			position((int) newPos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Not implemented, return null. Implementation have to be the following:
	 * This class must have a mirrorclass that give prewievdata, any change in
	 * setContainer on this class must provide the some changing in the
	 * FloatPreviewReader.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getFloatPreviewReader()
	 */
	@Override
	public FloatPreviewReader getFloatPreviewReader() {
		return null;
	}

	/**
	 * Remove a StreamEndListener
	 * 
	 * @param listener
	 */
	public void removeStreamEndListener(StreamEndListener listener) {
		fisMerger.removeStreamEndListener(listener);
	}

	/**
	 * Add a StreamEndListener
	 * 
	 * @param listener
	 */
	public void addStreamEndListener(StreamEndListener listener) {
		fisMerger.addStreamEndListener(listener);
	}

	/**
	 * @return All StreamEndListeners as an array
	 */
	public StreamEndListener[] getStreamEndListeners() {
		return fisMerger.getStreamEndListeners();
	}
}