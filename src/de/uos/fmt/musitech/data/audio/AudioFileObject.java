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
 * Created on 27.08.2003
 */
package de.uos.fmt.musitech.data.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.audio.floatStream.FIStoAIS;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader;
import de.uos.fmt.musitech.audio.floatStream.MP3FileFloatIS;
import de.uos.fmt.musitech.audio.floatStream.PositionableFIS;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * This represents a local or remote file containing audio data.
 */
// TODO: ueber Exceptionhandling entscheiden
/**
 * @author Alexander, Nicolai Strauch
 * @hibernate.class table = "AudioFileObject"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "id"
 */
public class AudioFileObject extends AudioObject {

	private URL sourceURL;
	private URL localURL;

	private File localFile;

	// private Object audioData;
	// it can be a FloatInputStream, an AudioInputStream
	// sollte man sich merken, was man schonmal ausgegeben hat?

	private transient boolean loaded = false; // true, if the data is loaded
	// on the
	// filesystem from the running system

	private transient boolean edited = false; // true, if the data was changed

	private long offsetSamples = 0; // number of samples to be skipped at the
	// start of the
	// audio file

	private long lengthInSamples; // number of samples from offset to end

	private transient AudioFormat audioFormat;

	private transient FloatInputStream fis;
	private long offsetMicros;

	private float panorama = 0f; // the panorama/balance 0 is center -1 is
	// left, 1 is right.
	private float volume = 1f; // the volume, 0 is off, 1 is full.

	/**
	 * Initialises AudioFileObject whithout audiodata-url at timestamp 0.
	 */
	public AudioFileObject() {
		this(0);
	}

	/**
	 * Initialises AudioFileObject whithout audiodata-url at timestamp 0.
	 */
	public AudioFileObject(Context ctx) {
		setContext(ctx);
		setTime(0);
	}

	/**
	 * Initialises AudioFileObject whithout audiodata-url at timestamp time.
	 * 
	 * @param time
	 */
	public AudioFileObject(long time) {
		super(time);
	}

	/**
	 * Initialises AudioFileObject whithout audiodata-url at timestamp time with
	 * context ctx.
	 * 
	 * @param ctx
	 * @param time
	 */
	public AudioFileObject(Context ctx, long time) {
		setContext(ctx);
		setTime(time);
	}

	/**
	 * Initialises AudioFileObject whith the URL url at timestamp 0.
	 * 
	 * @param url
	 * @throws IOException
	 */
	public AudioFileObject(URL url) throws IOException {
		this(Context.getDefaultContext(), 0, url);
	}

	/**
	 * Initialises AudioFileObject whith the URL url at time 0 and using the
	 * given Context.
	 * 
	 * @param ctx The context to set.
	 * @param url The URL providing the audio data.
	 * @throws IOException
	 */
	public AudioFileObject(Context ctx, URL url) throws IOException {
		this(ctx, 0, url);
	}

	/**
	 * Initialises AudioFileObject whith the URL url at time 0 and adds it to
	 * the given piece which provide the context for the new AudioFileObject.
	 * 
	 * @param p The piece to use.
	 * @param url The URL providing the audio data.
	 * @throws IOException
	 */
	public AudioFileObject(Piece p, URL url) throws IOException {
		this(p.getContext(), 0, url);
		p.getAudioPool().add(this);
	}

	/**
	 * Initialises AudioFileObject whith the URL url at timestamp time.
	 * 
	 * @param time
	 * @param url
	 * @throws IOException
	 */
	public AudioFileObject(long time, URL url) throws IOException {
		this(Context.getDefaultContext(), time, url);
	}

	/**
	 * Initialises AudioFileObject whith the URL url at timestamp time.
	 * 
	 * @param ctx The context to use.
	 * @param time The playback time of this afo.
	 * @param url The url to use.
	 * @throws IOException will be thrown if there are problems with reading
	 *             from the source URL or with caching.
	 */
	public AudioFileObject(Context ctx, long time, URL url) throws IOException {
		setContext(ctx);
		setTime(time);
		setSourceURL(url);
	}

	/**
	 * Set the source URL of this AFO, where the audio data are read from. Th
	 * URL can point to a file or to a remot location, in which case the data
	 * will be cached in a local file.
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void setSourceURL(URL url) throws IOException {
		if (url == null) {
			return;
		}
		sourceURL = url;
		loadData();
		getFloatInputStream();
	}

	/**
	 * The source URL of this AudioFileObject. hibernate.property
	 * 
	 * @return The source URL for this AudioFileObject.
	 */
	public URL getSourceURL() {
		return sourceURL;
	}

	/**
	 * Get the local URL for this AudioFileObject. TODO: hibernate.property ??
	 * 
	 * @return The local URL.
	 */
	public URL getLocalURL() {
		return localURL;
	}

	/**
	 * @return null if not localFile was loaded hibernate.property
	 *         access="field"
	 * @throws IOException
	 */
	public File getLocalFile() throws IOException {
		if (localFile == null)
			loadData();
		return localFile;
	}

	/**
	 * Load data from the sourceURL and make sure that localFile is set.
	 * 
	 * @throws IOException
	 */
	public void loadData() throws IOException {
		// TODO: see code at setSourceURL:
		// duplicate code. What implementation is useful?
		if (sourceURL == null)
			return;
		String str = sourceURL.toExternalForm();
		if(str.startsWith("file:")) { // try if the source is a local file
//			int cut=5;
//			while(str.charAt(cut)=='/')
//				cut++;
			try {
				localFile = new File(sourceURL.toURI());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loaded = true;
			localURL = sourceURL;
			setOffset(offsetMicros);
		}else{
			// if the source is remote we create a caching file.
			loadData(createNewFile());
		}
	}

	/**
	 * Copy the data by the URL given as source to the specified file.
	 * <code>localURL</code> will be set to this file.
	 * 
	 * @param localFile - the File into that the data of source will be copied.
	 * @throws IOException
	 */
	public void loadData(File localFile) throws IOException {
		this.localFile = localFile;
		localURL = localFile.toURL();

		// TODO: in ein thread setzen
		InputStream inputStream = sourceURL.openStream();
		FileOutputStream cacheStream = new FileOutputStream(localFile);
		byte[] bytes = new byte[8192];
		int transfered = inputStream.read(bytes);
		while (transfered >= 0) {
			cacheStream.write(bytes, 0, transfered);
			transfered = inputStream.read(bytes);
		}
		cacheStream.close();
		loaded = true;
	}

	/**
	 * Creates an AudioInputStream from the file at the contained adress,
	 * returns an AudioInputStream, or null if an Exception is thrown
	 * 
	 * @see de.uos.fmt.musitech.data.audio.AudioObject#getAudioInputStream()
	 */
	public AudioInputStream getAudioInputStream() {
		return new FIStoAIS(getFloatInputStream());
	}

	/**
	 * Create a FloatInputStream from this AudioFileObject. If the local file is
	 * istantiated, return a PositionableFIS. If a FloatInputStream allways was
	 * created, that position is set to offset and them is returned here. if any
	 * IOException ocurre, the fis returned maybe do not stay at the right
	 * position, or have other iregularities.
	 * 
	 * @see de.uos.fmt.musitech.data.audio.AudioObject#getFloatInputStream()
	 *      hibernate.property
	 */
	public FloatInputStream getFloatInputStream() {

		if (fis == null) {
			try {
				if (localFile != null)
					fis = AudioUtil.getPositionableFIS(localFile);
				else {
					if (localURL != null) {
						InputStream is = localURL.openStream();
						fis = AudioUtil.getFloatInputStream(is);
					}
				}
				if (fis != null) {
					audioFormat = fis.getFormat();
					fis.setPositionInSamples(offsetSamples);
					lengthInSamples = fis.remainingSamples();
				}
				if(DebugState.DEBUG_AUDIO)
					System.out.println("AudioFileObject.getFloatInputStream(): lenInSamples = "
									+ lengthInSamples);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DebugState.DEBUG && fis != null) {
				System.out
						.println("AudioFileObject.getFloatInputStream(): format of the returning Stream: "
									+ fis.getFormat());
			}
		} else {
			try {
				fis.setPositionInSamples(offsetSamples);
				lengthInSamples = fis.remainingSamples();
			} catch (IOException e) {
				e.printStackTrace();
				if (DebugState.DEBUG_AUDIO)
					System.err
							.println("AudioFileObject.getFloatInputStream(): trying to re-initialise FloatInputStream.");
				fis = null;
				return getFloatInputStream();
			}
		}
		if (fis instanceof MP3FileFloatIS) { 
			// if the data come from an mp3-file
			MetaDataCollection metaDataCollection = new MetaDataCollection();
			((MP3FileFloatIS) fis).getMetaData(metaDataCollection);
			getContext().getPiece().getMetaMap().put(this, metaDataCollection);
		}
		return fis;
	}

	/**
	 * Gets the offset in the file, where to start playback.
	 * 
	 * @return the offset in microseconds. hibernate.property
	 */
	public long getOffset() {
		return offsetSamples;
	}

	/**
	 * Sets the offset in the file, where to start playback.
	 * 
	 * @param newOffset the offset in microseconds.
	 */
	public void setOffset(long newOffset) {
		offsetMicros = newOffset;
		if (sourceURL == null) {
			return;
		}
		if (fis == null)
			getFloatInputStream();
		long offsetSamples = (long) ((double) newOffset * audioFormat.getFrameRate() / 1000000);
		long all = lengthInSamples + offsetSamples;
		lengthInSamples = all - newOffset;
		// long diff = newOffset - offset;
		// length = length - diff;
		offsetSamples = newOffset;
	}

	/**
	 * Try to create an AudioInputStream, and quest them for it available bytes.
	 * Convert the received value to the number of samples available,
	 * discounting offset.
	 * 
	 * @return number of samples available
	 */
	public long available() {
		if (fis == null) {
			getFloatInputStream();
		}
		if(DebugState.DEBUG_AUDIO)
			System.out.println("AudioFileObject.available() return " + lengthInSamples);
		return lengthInSamples;
	}

	/**
	 * Gets the duration in microseconds of the remaining audio data in this
	 * object.
	 * 
	 * @see de.uos.fmt.musitech.data.time.Timed#getDuration() hibernate.property
	 *      access = "field"
	 */
	public long getDuration() {
		if (fis == null) {
			getFloatInputStream();
		}
		if (DebugState.DEBUG_AUDIO)
			System.out.println("AudioFileObject.getDuration() return "
								+ (lengthInSamples * 1000000 / audioFormat.getFrameRate()));
		return (long) (lengthInSamples * 1000000 / audioFormat.getFrameRate());
	}

	/**
	 * Gets the format of the contained audio data. TODO what happens in case of
	 * errors/exceptions ?
	 * 
	 * @return The format of the audio data in this stream.
	 */
	public AudioFormat getFormat() {
		if (audioFormat == null) {
			getFloatInputStream();
		}
		return audioFormat;
	}

	/**
	 * Set the length in samples of this AudioFileObject
	 * 
	 * @param lengthInSamples The lengthInSamples to set.
	 */
	public void setLengthInSamples(long lengthInSamples) {
		this.lengthInSamples = lengthInSamples;
	}

	// private void loadAudioSettings(){
	// try {
	// AudioInputStream ais = AudioSystem.getAudioInputStream(getLocalFile());
	// audioFormat = ais.getFormat();
	// length = ais.available() / audioFormat.getFrameSize();
	// length -= offset;
	// } catch (UnsupportedAudioFileException e) {
	// } catch (IOException e) {
	// }// TODO: what do, if mp3?
	// }

	/**
	 * Creates a decimated rectified preview of the first channel of this audio
	 * file. The number of data points depends on the length of the data
	 * argument. The content of the data will be full wave rectified.
	 * 
	 * @param data array for the samples.
	 */
	public void previewDataRect(float[] data) {
		double scale = lengthInSamples / data.length;
		int pos = getPreviewData(new float[][] {data}, 0, data.length, 0, scale);
		while (pos < data.length) {
			data[pos] = 0;
		}
		for (int i = 0; i < data.length; i++) {
			if (data[i] < 0)
				data[i] = -data[i];
		}

	}

	// private AudioPreviewReader previewReader;
	private FloatPreviewReader previewReader;

	/**
	 * Read data from an AudioPreviweReader. Currently they are initialised to
	 * get one channel only, independent of the number of channels in the
	 * source. (TODO) Transfer len samples to the first array of data, beginning
	 * at <code>offset</code>. Read data beginning at posInSrc + offset from
	 * AudioFileObject. It will be written one sample in data for any "scale"
	 * samples in source (in the AudioPreviewReader). If an IOException or an
	 * UnsupportedAudioFileException ocurrs, 0 will be returned. Data written is
	 * normalised to [-1,1].
	 * 
	 * @param data - channel X samples
	 * @param offset - first position to be written
	 * @param len - number of samples to be written in data
	 * @param posInSrc - first sample to be read from source (in addition to the
	 *            offset)
	 * @param scale - number of samples in source for any sample written into
	 *            data
	 * @return number of samples written into data, or -1 if no more data is
	 *         available
	 */
	public int getPreviewData(float[][] data, int offset, int len, int posInSrc, double scale) {
		try {
			getFloatPreviewReader();
			previewReader.setSampleRateRatio((float) scale);
			// previewReader.position(posInSrc + offset);
			// previewReader.setAllChannels();
			return previewReader.previewRead(data, offset, len, posInSrc + offset);
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		}
	}

	// public AudioPreviewReader getAudioPreviewReader() throws IOException {
	/**
	 * hibernate.property
	 */
	public FloatPreviewReader getFloatPreviewReader() throws IOException {
		if (previewReader == null) {
			// previewReader = new AudioPreviewReader(getLocalFile());
			previewReader = ((PositionableFIS) getFloatInputStream()).getFloatPreviewReader();
		}
		return previewReader;
	}

	// ***** UTILS ******

	private File createNewFile() {
		File file = new File("audio0.tmp");
		for (int i = 1; file.exists(); i++) {
			file = new File("audio" + i + ".tmp");
		}
		return file;
	}

	// public boolean isValidValue(String propertyName, Object value){
	// //TO DO
	// return true;
	// }

	/**
	 * Length of data form offset to end in samples.
	 * 
	 * @return Returns the lengthInSamples. hibernate.property
	 */
	public long getLengthInSamples() {
		if (fis == null) {
			getFloatInputStream();
		}
		if(DebugState.DEBUG_AUDIO)
			System.out.println("AudioFileObject.getLengthInSamples() return " + lengthInSamples);
		return lengthInSamples;
	}

	/**
	 * @param localURL The localURL to set.
	 */
	public void setLocalURL(URL localURL) {
		try {
			setSourceURL(localURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the audioFormat. hibernate.property type = "binary"
	 */
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * Get the value of the edited property.
	 * 
	 * @return Returns the edited value.
	 * @hibernate.property
	 */
	public boolean isEdited() {
		return edited;
	}

	/**
	 * Get the value of the edited property.
	 * 
	 * @param edited The edited value to set.
	 */
	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	/**
	 * Get the FloatInputStream of this AudioFileObject.
	 * 
	 * @return Returns the fis.
	 * @hibernate.property type = "binary"
	 */
	public FloatInputStream getFis() {
		return fis;
	}

	/**
	 * @return Returns the loaded.
	 * @hibernate.property
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @param loaded The loaded to set.
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	/**
	 * @return Returns the offsetSamples.
	 * @hibernate.property
	 */
	public long getOffsetSamples() {
		return offsetSamples;
	}

	/**
	 * @param offsetSamples The offsetSamples to set.
	 */
	public void setOffsetSamples(long offsetSamples) {
		this.offsetSamples = offsetSamples;
	}

	/**
	 * @return Returns the previewReader.
	 * @hibernate.property type = "binary"
	 */
	public FloatPreviewReader getPreviewReader() {
		return previewReader;
	}

	/**
	 * @param previewReader The previewReader to set.
	 */
	public void setPreviewReader(FloatPreviewReader previewReader) {
		this.previewReader = previewReader;
	}

	/**
	 * @param localFile The localFile to set.
	 */
	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	/**
	 * @param audioFormat The audioFormat to set.
	 */
	public void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	/**
	 * @param fis The fis to set.
	 */
	public void setFis(FloatInputStream fis) {
		this.fis = fis;
	}

	/**
	 * The panorama value, 0 is center -1 is left, 1 is right.
	 * 
	 * @return The current value.
	 */
	public float getPanorama() {
		return panorama;
	}

	/**
	 * The panorama value, 0 is center -1 is left, 1 is right.
	 * 
	 * @set The value to set.
	 */
	public void setPanorama(float panorama) {
		this.panorama = panorama;
	}

	/**
	 * The volume, 0 is off, 1 is full.
	 * 
	 * @return the initial volume.
	 */
	public float getVolume() {
		return volume;
	}

	public float volumeAt(long time) {
		return volume;
	}

	/**
	 * The volume, 0 is off, 1 is full.
	 * 
	 * @set The volume to set.
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}

}