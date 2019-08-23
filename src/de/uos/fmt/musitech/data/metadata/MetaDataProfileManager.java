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
 * Created on 2003-06-10
 */
package de.uos.fmt.musitech.data.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * MetaDataProfileManager provides access to the predefined MetaDataProfiles.
 *  
 * @author Christophe Hinz, Tillman Weyde, Kerstin Neubarth
 */
public class MetaDataProfileManager {

	/**
	 * Default contructor
	 */
	private MetaDataProfileManager() {
	}

	// List containing all MetaDataProfile objects
	private static List profileList;

	static {

		//metaDataProfileVector = (Vector) ObjectCopy.readXML(new File("metadataprofile.xml"));
		if (profileList == null) {
			profileList = new ArrayList(0);
		}
		//Setting of all profiles
		

		//Setting of profile Piece
		MetaDataProfile piece = new MetaDataProfile();
		piece.setName("Piece");
		piece.add(new MetaDataProfileItem("Title", "String"));
		piece.add(new MetaDataProfileItem("Opus/Catalogue Number", "String"));
		piece.add(new MetaDataProfileItem("Composer", "String"));
		piece.add(new MetaDataProfileItem("Year(s) of composition", "String"));
		piece.add(new MetaDataProfileItem("Genre", "String"));
		piece.add(new MetaDataProfileItem("Category", "String"));
		piece.add(new MetaDataProfileItem("Instrumentation", "String"));
		//auskommentiert für Test MetaDataEditor
		//piece.add(new MetaDataProfileItem("Comments_Piece", "text/plain"));
		//piece.add(new MetaDataProfileItem("Other_Piece", "text/plain"));
		//These are examples. Fill in with same pattern.

		profileList.add(piece);

//		Setting of profile Score
		MetaDataProfile score = new MetaDataProfile();
	  	score.setName("Score");
		score.add(new MetaDataProfileItem("Editor", "String"));
		score.add(new MetaDataProfileItem("Publisher", "String"));
		score.add(new MetaDataProfileItem("Year", "int"));
		score.add(new MetaDataProfileItem("Pages", "int"));
		//score.add(new MetaDataProfileItem("Comments_Score", "text/plain"));
		//score.add(new MetaDataProfileItem("Other_Score", "text/plain"));
		
		profileList.add(score);
		
//		Setting of profile Recording
		MetaDataProfile recording = new MetaDataProfile();
		recording.setName("Recording");
		recording.add(new MetaDataProfileItem("Performing Artist(s)", "String"));
		recording.add(new MetaDataProfileItem("Instrumentation in Recording", "String"));	//instrumentation in piece
		recording.add(new MetaDataProfileItem("Album Title", "String"));
		recording.add(new MetaDataProfileItem("Year (c)", "int"));
		recording.add(new MetaDataProfileItem("ISRC", "String"));
		//recording.add(new MetaDataProfileItem("Comments_Recording", "text/plain"));
		//recording.add(new MetaDataProfileItem("Other_Recording", "text/plain"));
		
		profileList.add(recording);
		
//		Setting of profile Lyrics
		MetaDataProfile lyrics = new MetaDataProfile();
		lyrics.setName("Lyrics");
		lyrics.add(new MetaDataProfileItem("Language", "String"));
		lyrics.add(new MetaDataProfileItem("Text Type", "String"));	
		/*auskommentiert für Test MetaDataEditor
		lyrics.add(new MetaDataProfileItem("Text", "text/plain"));
		lyrics.add(new MetaDataProfileItem("Language", "String"));
		lyrics.add(new MetaDataProfileItem("Comments_Lyrics", "text/plain"));
		lyrics.add(new MetaDataProfileItem("Other_Lyrics", "text/plain"));
		*/
		profileList.add(lyrics);
		
//		Setting of profile MIDI
		MetaDataProfile midi = new MetaDataProfile();
		midi.setName("MIDI");
		midi.add(new MetaDataProfileItem("Time Signature", "String"));
		midi.add(new MetaDataProfileItem("Key Signature", "String"));
		midi.add(new MetaDataProfileItem("Tempo", "int"));
		//midi.add(new MetaDataProfileItem("Comments_MIDI", "text/plain"));
		//midi.add(new MetaDataProfileItem("Other_MIDI", "text/plain"));
		
		profileList.add(midi);
		
//		Setting of profile Audio
		MetaDataProfile audio = new MetaDataProfile();
		audio.setName("Audio");
		audio.add(new MetaDataProfileItem("Bitrate", "int"));
		audio.add(new MetaDataProfileItem("Channels", "int"));
		audio.add(new MetaDataProfileItem("Audio Sample Rate", "int"));
		audio.add(new MetaDataProfileItem("Audio Sample Rate", "int"));
		audio.add(new MetaDataProfileItem("Encoding_Audio", "int"));
		audio.add(new MetaDataProfileItem("Track Number", "int"));

		profileList.add(audio);
			
//		Setting of profile Text
		MetaDataProfile text = new MetaDataProfile();
		text.setName("Text");
		text.add(new MetaDataProfileItem("Encoding_Text", "int"));
		//text.add(new MetaDataProfileItem("Comments_Text", "text/plain"));	//überflüssig?
		//text.add(new MetaDataProfileItem("Other_Text", "text/plain"));		//überflüssig?
		
		profileList.add(text);
		
//		Setting of profile Video
		MetaDataProfile video = new MetaDataProfile();
		video.setName("Video");
		video.add(new MetaDataProfileItem("X-Resolution Video", "String"));
		video.add(new MetaDataProfileItem("Y-Resolution Video", "String"));
		video.add(new MetaDataProfileItem("Frames per Second", "int"));
		video.add(new MetaDataProfileItem("Encoding_Video", "int"));
		video.add(new MetaDataProfileItem("Comments_Video", "String"));
		video.add(new MetaDataProfileItem("Other_Video", "String"));
		
		profileList.add(video);
		
//		Setting of profile Image
		MetaDataProfile image = new MetaDataProfile();
		image.setName("Image");
		image.add(new MetaDataProfileItem("Type/Relationship", "String"));
		image.add(new MetaDataProfileItem("Contents/Description", "String"));	//oder text/plain
		image.add(new MetaDataProfileItem("X-Resolution Image", "String"));	
		image.add(new MetaDataProfileItem("Y-Resolution Image", "String"));	
		image.add(new MetaDataProfileItem("Color Depth", "int"));	
		image.add(new MetaDataProfileItem("Comments_Image", "String"));
		image.add(new MetaDataProfileItem("Other_Image", "String"));
		
		profileList.add(image);

//		Setting of profile File
		MetaDataProfile file = new MetaDataProfile();
		file.setName("File");
		file.add(new MetaDataProfileItem("Filename", "String"));
		file.add(new MetaDataProfileItem("Type", "String"));
		file.add(new MetaDataProfileItem("Size", "long"));
		//file.add(new MetaDataProfileItem("Location", "URN"));
		file.add(new MetaDataProfileItem("Author/Source", "String"));
		file.add(new MetaDataProfileItem("Copyright", "String"));
		//These are examples. Fill in with same pattern.

		profileList.add(file);

		//etc.
	}
	
	/**
	 * Hashtable used to map the <code>mimeType</code> of a MetaDataValue to the
	 * corresponding fully qualified class name.
	 * <br>
	 * <br>N.B.:
	 * The type here must be given in lower-case letters! (This is because method
	 * <code>getFullyQualifiedClassName(String)</code> converts all characters 
	 * of the specified String to lower case.
	 */
	private static Hashtable typeOntoClass = new Hashtable();
	
	static{
		typeOntoClass.put("string", "java.lang.String");
		typeOntoClass.put("int", "java.lang.Integer");
		typeOntoClass.put("integer", "java.lang.Integer");
		typeOntoClass.put("byte", "java.lang.Byte");
		typeOntoClass.put("float", "java.lang.Float");
		typeOntoClass.put("char", "java.lang.Character");
		typeOntoClass.put("character", "java.lang.Character");
		
	}
	
	/**
	 * Returns the fully qualified class name corresponding to the specified 
	 * <code>type</code> by consulting the Hashtable <code>typeOntoClass</code>.
	 * If the specified String is not found as key of the Hashtable, the method
	 * returns null. 
	 * TODO default machanism or Class???
	 * 
	 * @param type String indicating the type the fully qualified class name is looked for
	 * @return String fully qualified class name corresponding to the specified <code>type</code>
	 */
	public static String getFullyQualifiedClassName(String type){
		if (type==null)
			return null;	
		type = type.toLowerCase();
		return (String)typeOntoClass.get(type);
	}
	
	

	/**
	 * Runs through the list containing all MetaDataProfiles and returns a string array 
	 * containing the name of every MetaDataProfile object.
	 * @return String[]
	 */
	public static String[] getMetaDataProfileNames() {
		String[] profileNameList = new String[profileList.size()];
		int count = 0;
		for (Iterator iter = profileList.iterator();
			iter.hasNext();
			) {
			MetaDataProfile tempObject = (MetaDataProfile) iter.next();
			String profileName = (String) tempObject.getName();
			profileNameList[count] = profileName;
			count++;
		}
		return profileNameList;
	}

	/**
	 * Returns a profile with given name if present
	 * @param nameOfProfile The name of the seeked Profile
	 * @return MetaDataProfile The Profile with the given name, null if not found
	 */
	public static MetaDataProfile getMetaDataProfile(String nameOfProfile) {
		for (Iterator iter = profileList.iterator();
			iter.hasNext();
			) {
			MetaDataProfile element = (MetaDataProfile) iter.next();
			if (element.getName().equals(nameOfProfile)) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Returns the MetaDataProfile that contains the specified key.
	 * @param key String the required MetaDataProfile must match
	 * @return MetaDataProfile containing the specified key, or null if none of the profiles contains this key
	 */
	public static MetaDataProfile getMetaDataProfileContainingKey(String key){
		for (Iterator iter = profileList.iterator();iter.hasNext();) {
			MetaDataProfile profile = (MetaDataProfile) iter.next();
			for (int i = 0; i < profile.listOfKeys().length; i++) {
				if (key.equals(profile.listOfKeys()[i]))
					return profile;	
			}
		}
		return null;
	}
	
	/**
	 * Writes the MetaDataProfiles to an XML File
	 */
	public static void writeProfilesToXml() {
		File file = new File("metadataprofile.xml");
		ObjectCopy.writeXML(profileList, file);
	}
	
	/*
		// HashMap containing lists of values for popup menus associated with certain MetaDataProfileItems
		private static HashMap popupsMap;
	
		static{
		
			String [] categories = {"Classical", "Pop/Rock/Country", "Jazz/Blues", "Techno/Rap/Hip Hop", "Ethnic", "Chanson/Text song", "Other/Unknown"}; 
			String [] texttypes = {"Synced lyrics", "Unsynced lyrics", "Paratext", "Translation", "Other/Unknown"};
		
			if (popupsMap == null)
				popupsMap = new HashMap(); 
			
			popupsMap.put("Category", categories);
			popupsMap.put("Text Type", texttypes);
		
		}
	
	public static String[] getPopupEntries (String key){
		return (String[]) popupsMap.get(key);
	}
	
	public static boolean keyInPopupMap(String key){
		return popupsMap.keySet().contains(key);
	}
	*/
	
	
}
