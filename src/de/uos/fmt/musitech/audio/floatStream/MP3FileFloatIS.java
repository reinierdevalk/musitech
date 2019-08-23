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
 * Created on 23.04.2003
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.mp3Decoder.Song;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.utility.DebugState;
import de.vdheide.mp3.FrameDamagedException;
import de.vdheide.mp3.ID3v2DecompressionException;
import de.vdheide.mp3.ID3v2IllegalVersionException;
import de.vdheide.mp3.ID3v2WrongCRCException;
import de.vdheide.mp3.MP3File;
import de.vdheide.mp3.NoMP3FrameException;
import de.vdheide.mp3.TagContent;

/**
 * @author Nicolai Strauch
 * 
 * The same as FloatISPartReader, but with mp3. Works only whith files.
 * 
 * The length of data in samples depends on samples given by all mp3-frames.
 * Every mp3-frame can in theory have another proportion of bytes to samples.
 * So the factor corresponding the proportion between bytes and samples is 
 * updated at every frame read. The value given is only approximate.
 * Similary, all values depending on each frame are constantly updated and 
 * not necessarily correct. All positions that are set whith this stream-dependent 
 * imprecision.
 * 
 */
public class MP3FileFloatIS extends MP3FloatIS implements PositionableFIS
{

	private ChannelInputStream inputStream;
	
	private File file;

	/**
	 * @param file - contain the mp3-data
	 * @throws IOException if an IOException occure by extract an ChannelInputStream from the File
	 */
	public MP3FileFloatIS(File file) throws UnsupportedAudioFileException, IOException 
	{
		super(new ChannelInputStream(file));
		this.file = file;
		inputStream = (ChannelInputStream) super.getInputStream();
	}
	protected MP3FileFloatIS(ChannelInputStream cis) throws UnsupportedAudioFileException, IOException{
		super(cis);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public void position(int n) throws IOException{
//System.out.println("  >-FIS-<   MP3FileFloatIS.position("+n+");");
//		if(n>getSamplesRead() && n<samplesAvailableWithoutBlocking()+getSamplesRead()){
//			setBufferIndex(n - getSamplesRead());
//			setSamplesRead(n);
//			return;
//		}
		setPositionInSamples(n);
		
//		if(n>getSamplesRead()){
//			skip(n-getSamplesRead());
//			return;
//		}
//		if(n<getSamplesRead()){
//System.out.println("  >-FIS-<   MP3FileFloatIS.position("+n+"): n < samples read, "+getSamplesRead());
//			int frame = searchFrameBySample((int)n);
//			if(frame<0){
//				inputStream.position(0);
//				// 	TO DO bitstream should be reset, but resetting is not accssible
//				initialiseMP3Decoder();
//				getData();
//				setBufferIndex(0);
//			}else{	
//				inputStream.position(framesMap[0][frame]);
//				initialiseMP3Decoder();
//				getData();
//				setBufferIndex((int) (n - framesMap[1][frame]));
//			}
//			setSamplesRead((int) n);
//		}
	}
	/**
	 * The mp3-codification-format maybe can change continuously. To consider all 
	 * possible changes at every information, will cost many ressources and time.
	 * So the value returned is the number of bytes resting by the difference 
	 * between the number of bytes per mp3-Frame by the number of Samples given for
	 * each mp3-frame
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public int position(){
		return getSamplesRead();
//		int bytePos;
//		int frame;
//		try {
//			bytePos = inputStream.position();
//			frame = searchFrameByByte((int)bytePos);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			return 0;
//		}
//		if(frame < 0)
//			return getBufferIndex();
//		return framesMap[1][frame] + getBufferIndex();
	}


	
	
	

	/**
	 * Is not implemented. Return null.
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getMapping()
	 */
	public ByteBuffer getMapping() {
		return null;
	}

	/**
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getFloatPreviewReader()
	 */
	public FloatPreviewReader getFloatPreviewReader() {
		try {
			return new MP3FPreviewReader(file);
		//	return new MP3FPreviewReader(inputStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author Nicolai Strauch
	 * 
	 * 
	 */
	static class ChannelInputStream extends InputStream{
		//private FileChannel fileChannel;
		private ByteBuffer source;
		private int bytesAvailable;
		private int header;
		
		//private byte[] byteBuffer = new byte[8192];
		//private int byteBufferPos;
	
		//long position;
		int readLimit;	// in bytes: limit to be sett, to be used by read-methods, when not all data have to be read at the end. The read-methods will return 0 by reach readLimit.
		int mark;		// in bytes
		

		ChannelInputStream(File file) throws IOException{
			setFile(file);
		}
		
		public void setFile(File file) throws IOException{
System.out.println("  >--<   ChannelInputStream.setFile("+file+");");
			FileInputStream fis = new FileInputStream(file);
			bytesAvailable = fis.available();	
			FileChannel fileChannel = fis.getChannel();
			source = null;
			source = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			//chargeBuffer();
			header = readID3v2Header(this);
			readLimit = bytesAvailable-header;
			position(header);
		}

		public int read(byte[] b) throws IOException
		{
			return read(b, 0, b.length);
		}
		/**
		 * @param data
		 * @param start
		 * @param len
		 * @return
		 * @throws IOException
		 */
		public int read(byte[] b, int off, int len) throws IOException
		{
//			int available = available();
//			len = available>len?len:available;
//			len = (byteBuffer.length-byteBufferPos)<len ? (byteBuffer.length-byteBufferPos) : len;
//			if(len<1) return len;

//			System.arraycopy(byteBuffer, byteBufferPos, b, off, len);
			//if(len>0 && len>available()){
			if(len>0 && len>source.remaining()){
				//len = available();
				len = source.remaining();
				if(len==0){
					System.out.println(" @ @ @ Streamend:    MP3FileFloatIS.ChannelInputStream.read() return -1");
					return -1;
				}
			}
			if(len<0){
				System.err.println("MP3FileFloatIS.ChannelInputStream.read(): len<0. return 0.");
				return 0;
			}
//if(positionChanged)
//	System.out.print("at pos: "+source.position()+":  ");
			source.get(b, off, len);	

			
//if(positionChanged){
//for(int i=0; i<len && i<4; i++){
//	for(int j=0; j<8; j++){
//		System.out.print((byte)(b[i+off]<<j)<0?"1":"0");
//	}
//	System.out.print(" ("+b[i+off]+") ");
//}
//System.out.println("  first bytes of "+len+" bytes returned by MP3FileFloatIS.ChannelInputStream.read().");
//positionChanged = false;
//}			

//			byteBufferPos += len;
//			if(byteBufferPos >= byteBuffer.length)
//				chargeBuffer();
			//position += len;
			return len;
		}
		
//		boolean positionChanged;
//		
//		private void chargeBuffer() throws IOException{
//			ByteBuffer[] source = new ByteBuffer[1]; 
//			source[0] = ByteBuffer.allocate(byteBuffer.length);
//			fileChannel.read(source);
//			int toLoad = source[0].position(0).remaining();
//			if(byteBuffer.length != toLoad)
//				byteBuffer = new byte[toLoad];
//			source[0].get(byteBuffer);
//			byteBufferPos = 0;
//		}

		public long skip(long n) throws IOException
		{
//System.out.println("  >--<   ChannelInputStream.skip("+n+");");
//			fileChannel.position(fileChannel.position()+n);
			int oldPos = position();
			position((int) (position()+n)); 
			if(position() == oldPos)
				if(n > 0)
					return -1;
			return position() - oldPos;
		}

		/**
		 * Set the position to begin to read data.
		 * If n<0, the position is set to 0.
		 * If n>readLimit, the position is set to readLimit.
		 * Set the Byteposition from the frame, set the position n+(id3v2 tag)
		 * @param n
		 * @throws IOException
		 */
		public void position(int n) throws IOException{
//System.out.println("  >--<   ChannelInputStream.position("+n+");");
			//position = n;
			if(n<0)
				n = 0;
			if(n>readLimit)
				n = readLimit;
			source.position(n+header);
//			fileChannel.position(n);
//			chargeBuffer();
//			positionChanged = true;
		}

		/**
		 * Get the position in bytes, discounting idr2v3Tag
		 * @return
		 * @throws IOException
		 */
		public int position() throws IOException{
//System.out.println("  >--<   ChannelInputStream.position(); return: "+fileChannel.position());
			return source.position()-header;
		}

		public long readLimit(int newLimit){
			readLimit = (newLimit>bytesAvailable?bytesAvailable:newLimit)-header;
//System.out.println("  >--<   ChannelInputStream.readLimit("+newLimit+"); return: "+readLimit);
			return readLimit;
		}

		public long readLimit(){
//System.out.println("  >--<   ChannelInputStream.readLimit(); return: "+readLimit);
			return readLimit;
		}

		/**
		 * do not use the given argument, than we can remember every byte read, 
		 * without save it sepparatedly
		 * @param readLimit - unnecessary
		 */
		public void mark(long readLimit){
//System.out.println("  >--<   ChannelInputStream.mark("+marker+"); ");
			try {
				mark = position();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void reset() throws IOException
		{
//System.out.println("  >--<   ChannelInputStream.reset();");
			position(mark);			
		}

		public long totalAvailable()
		{
//System.out.println("  >--<   ChannelInputStream.totalAvailable(); return: "+bytesAvailable);
			return bytesAvailable;
		}





		public int available() throws IOException{
			//int out = (int)(readLimit-position()) + (byteBuffer.length-byteBufferPos);
//System.out.println("  >--<   ChannelInputStream.available(); return: "+out);
			return (int)(readLimit-position());
		}

		public boolean markSupported(){
//System.out.println("  >--<   ChannelInputStream.available(); return: true");			
			return true;
		}

		public int read() throws IOException
		{
			//System.err.println("Warning: MP3FileFloatIS.ChannelInputStream.read() is not implemented, and return 0.");
//			if(byteBuffer.length>=byteBufferPos)
//				chargeBuffer();
			return source.get();
		}
		
	}

	
	/**
	 * Parse ID3v2 tag header to find out size of ID3v2 frames. 
	 * @param in MP3 InputStream
	 * @return size of ID3v2 frames + header
	 * @throws IOException
	 * @author JavaZOOM
	 */
	private static int readID3v2Header(InputStream in) throws IOException
	{		
		byte[] id3header = new byte[4];
		int size = -10;
		in.read(id3header,0,3);
		// Look for ID3v2
		if ( (id3header[0]=='I') && (id3header[1]=='D') && (id3header[2]=='3'))
		{
			in.read(id3header,0,3);
			int majorVersion = id3header[0];
			int revision = id3header[1];
			in.read(id3header,0,4);
			size = (int) (id3header[0] << 21) + (id3header[1] << 14) + (id3header[2] << 7) + (id3header[3]);
		}
		return (size+10);
	}
	
//	private Song song;
//	/**
//	 * return the data from the id3-tag of the mp3file decoded in this object,
//	 * in a Song-object.
//	 * If no id3-tag is available, null is returned.
//	 * @return
//	 */
//	public Song getSong(){
//		return song;
//	}
	
	/**
	 * If the mp3-file have an id3-tag, this method will extract them, and return the data 
	 * in a Song-object.
	 * @param mp3_file
	 * @return
	 */
	public static Song getSong(File mp3_file){
		// code copied from class YASP.recomendation.MySimplePlayer.AddAction
		Song song = null;
		MP3File mp3file;
		String title = "";
		String interpret = "";
		String genre = "";
		String desc = "";
		try {
			mp3file = new MP3File(mp3_file.getAbsolutePath());
			title += mp3file.getTitle().getTextContent();
			interpret += mp3file.getArtist().getTextContent();
			genre += mp3file.getGenre().getTextContent();
			desc += mp3file.getAlbum().getTextContent();

			song = new Song(mp3file.getAbsolutePath(), title, interpret, genre, "",
									desc);
			song.set("Rating", String.valueOf(-2));
System.out.println(song);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return song;
	}
	
	/**
	 * Extract metadata from the id3-tag of the mp3-file from this MP3FileFloatIS,
	 * and add them as MetaDataItem to the given MetaDataCollection.
	 * @param collection - will get all metadata from mp3-tag
	 * @return true if id3-tag is read, false if any exception occure, for example, if
	 * the file is not a mp3-file, or have not id3-tag, or if the id3-tag is damaged.
	 */
	public boolean getMetaData(MetaDataCollection collection){
		return getMetaData(collection, file);
	}
	
	/**
	 * Extract metadata from the id3-tag of the given mp3-file, and add them as 
	 * MetaDataItem to the given MetaDataCollection.
	 * @param collection - will get all metadata from mp3-tag
	 * @param mp3_file - mp3-file whith id3-tag to read
	 * @return true if id3-tag is read, false if any exception occure, for example, if
	 * the file is not a mp3-file, or have not id3-tag, or if the id3-tag is damaged.
	 */
	public static boolean getMetaData(MetaDataCollection collection, File mp3_file){
		MP3File mp3file = null;
		try {
			mp3file = new MP3File(mp3_file.getAbsolutePath());
		} catch (ID3v2WrongCRCException e) {
			return false;
		} catch (ID3v2DecompressionException e) {
			return false;
		} catch (ID3v2IllegalVersionException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (NoMP3FrameException e) {
			return false;
		}
		
		TagContent tagContent = null;
		MetaDataItem item = null;
		int numberOfInsertedTagelements = 0;
		
		try {
			
			// composer
			tagContent = mp3file.getComposer();
			if(tagContent.getTextContent()!=null){ numberOfInsertedTagelements++;
				item = new MetaDataItem("Composer", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// conductor
			tagContent = mp3file.getConductor();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Conductor", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
				
			// album
			tagContent = mp3file.getAlbum();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Album", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
				
			// artist
			tagContent = mp3file.getArtist();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Artist", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
				
			// artist Webpage
			tagContent = mp3file.getArtistWebpage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Artists Webpage", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
				
			// audio file webpage
			tagContent = mp3file.getAudioFileWebpage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Audio-file Webpage", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
				
			// audio source webpage
			tagContent = mp3file.getAudioSourceWebpage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Audio-source Webpage", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Band
			tagContent = mp3file.getBand();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Band", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Comments
			tagContent = mp3file.getComments();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Comments", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// ContendGroup
			tagContent = mp3file.getContentGroup();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("ContendGroup", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Copyright
			tagContent = mp3file.getCopyrightText();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Copyright", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Copyright Webpage
			tagContent = mp3file.getCopyrightWebpage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Copyright Webpage", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Date
			tagContent = mp3file.getDate();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Date", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Encapsulated Object    TODO: what do whith binary data in tag?
//			tagContend = mp3file.getEncapsulatedObject();
//			if(tagContend!=null){
//				item = new MetaDataItem("Encapsulated Object", tagContend.getBinaryContent());
//				collection.addMetaDataItem(item);
//			}
			
			// genre
			tagContent = mp3file.getGenre();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Genre", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Station-name of internetradio
			tagContent = mp3file.getInternetRadioStationName();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Station-name of internetradio", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Webpage of internetradio
			tagContent = mp3file.getInternetRadioStationWebpage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Webpage of internetradio", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// language
			tagContent = mp3file.getLanguage();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Language", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Lyricist
			tagContent = mp3file.getLyricist();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Lyricist", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Original artist
			tagContent = mp3file.getOriginalArtist();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Original artist", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Subtitle
			tagContent = mp3file.getSubtitle();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Subtitle", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Year of recording
			tagContent = mp3file.getYear();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Year of recording", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Lyrics
			tagContent = mp3file.getUnsynchronizedLyrics();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Lyrics", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Track
			tagContent = mp3file.getTrack();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Track", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Title
			tagContent = mp3file.getTitle();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Title", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Terms of use
			tagContent = mp3file.getTermsOfUse();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Terms of use", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Time
			tagContent = mp3file.getTime();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Time", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
			// Original artist
			tagContent = mp3file.getOriginalArtist();
			if(tagContent.getTextContent()!=null){ 
				numberOfInsertedTagelements++;
			//if(tagContend!=null){
				item = new MetaDataItem("Original artist", tagContent.getTextContent());
				collection.addMetaDataItem(item);
			}
			
		} catch (FrameDamagedException e1) {
			if(DebugState.DEBUG_AUDIO)
				e1.printStackTrace();
			System.out.println( "Could not read Metata from file "+mp3_file +", reason: "+ e1.getMessage());
			return false;
		}
			
		System.out.println("MP3FileFloatIS.getMetaData(...): "+numberOfInsertedTagelements+" elements from tag added to MetaDataCollection.");
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	/**
//	 * The mp3-codification-format maybe can change continuously. To consider all 
//	 * possible changes at every information, will cost many ressources and time.
//	 * So the value returned is the number of bytes resting by the difference 
//	 * between the number of bytes per mp3-Frame by the number of Samples given for
//	 * each mp3-frame
//	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
//	 */
//	public int markEnd(int newLimit){
////		TO DO: implement
//			 return -1;
//	}
//	/**
//	 * The mp3-codification-format maybe can change continuously. To consider all 
//	 * possible changes at every information, will cost many ressources and time.
//	 * So the value returned is the number of bytes resting by the difference 
//	 * between the number of bytes per mp3-Frame by the number of Samples given for
//	 * each mp3-frame
//	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
//	 */
//	public int getEndMark(){
//		// TO DO: implement
//		return -1;
//	}
//	/**
//	 * The mp3-codification-format maybe can change continuously. To consider all 
//	 * possible changes at every information, will cost many ressources and time.
//	 * So the value returned is the number of bytes resting by the difference 
//	 * between the number of bytes per mp3-Frame by the number of Samples given for
//	 * each mp3-frame
//	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
//	 */
//	public void markBegin(int marker){
//		// TO DO: implement
//
//	}
//	/**
//	 * The mp3-codification-format maybe can change continuously. To consider all 
//	 * possible changes at every information, will cost many ressources and time.
//	 * So the value returned is the number of bytes resting by the difference 
//	 * between the number of bytes per mp3-Frame by the number of Samples given for
//	 * each mp3-frame
//	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
//	 */
//	public int getBeginMark() throws IOException{
//		// TO DO: implement
//		return -1;
//	}
//
//
//
//	/**
//	 * Number of samples probably available.
//	 * The mp3-codification-format maybe can change continuously. To consider all 
//	 * possible changes at every information, will cost many ressources and time.
//	 * So the value returned is the number of bytes resting by the difference 
//	 * between the number of bytes per mp3-Frame by the number of Samples given for
//	 * each mp3-frame
//	 * (oder anders erklärt:)
//	 * The number of samples available, if all following frames have the some format
//	 * as the aktual.
//	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
//	 */
//	public int getMaxEndPos()
//	{
//		// TO DO: implement
//		return -1;
//	}
//	
	
	
}
