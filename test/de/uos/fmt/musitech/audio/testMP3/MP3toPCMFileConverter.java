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
 * Created on 06.07.2004
 */
package de.uos.fmt.musitech.audio.testMP3;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JOptionPane;

import de.uos.fmt.musitech.data.audio.AudioFileObject;

/**
 * @author Nicolai Strauch
 */
public class MP3toPCMFileConverter {

	AudioFileObject afo;
	File out;
	boolean converted;
	
	MP3toPCMFileConverter(File file) throws MalformedURLException, IOException{
		this(new AudioFileObject(file.toURL()));
	}
	
	MP3toPCMFileConverter(AudioFileObject afo) throws IOException{
		this.afo = afo;
		out = getNewFile(afo.getLocalFile());
	}
	
	public void convert() throws IOException{
		if(converted) return;
		System.out.println("MP3toPCMFileConverter.convert() : begin Fileconversion.");
		AudioInputStream ais = afo.getAudioInputStream();
//		//>>>>>>>>>>>>>>
//		FileOutputStream fos = new FileOutputStream(out);
//		File tmp = new File("tmp.tmp.delete");
//		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, tmp);
//		//<<<<<<<<<<<<<<
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
//		//>>>>>>>>>>>>>>
//		tmp = null;
//		afo.getFloatInputStream().reset();
//		FileInputStream head = new FileInputStream("tmp.tmp.delete");
//		byte[] data = new byte[2048];
//		int read = head.read(data, 0, 44);
//		head.close();
//		while(read > 0){
//			fos.write(data, 0, read);
//			read = ais.read(data);
//		}
//		fos.flush();
//		fos.close();
//		//<<<<<<<<<<<<<<
		converted = true;
	}
	
	/**
	 * 
	 * @return file converted from MP3 to PCM
	 *             or null if not converted
	 */
	public File getConvertedFile(){
		if(converted)
			return out;
		return null;
	}
	
	
	
	
	public static void main(String[] a){
		
	}
	
	public File getNewFile(){
		String fileName = JOptionPane.showInternalInputDialog(null, "Please enter a filename");
		return new File(fileName);
	}
	
	/**
	 * create a new file adding ".MP3toPCM" to the filename 
	 * of the file given.
	 * If the file created allready exists, converted is set true.
	 * @param file
	 * @return
	 */
	private File getNewFile(File file){
		File newFile = new File(file.getPath()+".MP3toPCM");
		if(newFile.exists()){
			converted = true;
			System.out.println("MP3toPCMFileConverter.getNewFile(...): File do not need Fileconversion.");
		}
		return newFile;
	}
	
}
