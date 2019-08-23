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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import de.uos.fmt.musitech.audio.floatStream.PositionableFIS;
import de.uos.fmt.musitech.data.audio.AudioFileObject;

/**
 * @author Nicolai Strauch
 */
public class CompareMP3whithPCM {
	
	PositionableFIS pcmFis;
	PositionableFIS mp3Fis;
	
	CompareMP3whithPCM(PositionableFIS pcmFis,	PositionableFIS mp3Fis){
		this.pcmFis = pcmFis;
		this.mp3Fis = mp3Fis;
		pcmBuff = new float[pcmFis.getFormat().getChannels()][1000];
		mp3Buff = new float[mp3Fis.getFormat().getChannels()][1000];
	}
	
	/**
	 * Compare two given PositionableFIS
	 * @param positions - Samplepositions to be compared
	 * @throws IOException
	 */
	public void compare(int[] positions) throws IOException{
		System.out.println(">CompareMP3whithPCM.compare(...): precompare");
		pcmFis.read(pcmBuff);
		mp3Fis.read(mp3Buff);
		for(int j=0; j<pcmBuff[0].length; j++){
			if((int)pcmBuff[0][j]!=(int)mp3Buff[0][j]){
				for(int k=0; k<pcmBuff[0].length; k++){
					System.out.println("   pre      pos "+k+": "+(int)pcmBuff[0][k]+" | "+(int)mp3Buff[0][k]);
				}
				break;
			}
			else System.out.print(" >>   ok.");
		}
		System.out.println("<CompareMP3whithPCM.compare(...): precompare finalised.");
		for(int i=0; i<positions.length; i++){
			System.out.println("  "+i+" from "+positions.length+" positions: CompareMP3whithPCM.compare(...) Compare position "+positions[i]);
			pcmFis.position(positions[i]);
			mp3Fis.position(positions[i]);
			pcmFis.read(pcmBuff);
			mp3Fis.read(mp3Buff);
			for(int j=0; j<pcmBuff[0].length; j++){
				if((int)pcmBuff[0][j]!=(int)mp3Buff[0][j]){
					for(int k=0; k<pcmBuff[0].length; k++){
						System.out.println("         pos "+k+": "+(int)pcmBuff[0][k]+" | "+(int)mp3Buff[0][k]);
					}
					break;
				}
				else System.out.print("   ok.");
			}
		}
		System.out.println("<<Compare ended.");
	}
	float[][] pcmBuff;
	float[][] mp3Buff;
	
	public void searchForMP3SamplesInPCM() throws IOException{
		pcmFis.position(0);
		boolean found = false;
		int read = 0;
		int pcmPos = 0;
		int equals = 0;
		int equalpos = 0;
		while(read >= 0 && !found){
			pcmPos = pcmFis.position();
			read = pcmFis.read(pcmBuff);
			equalpos = pcmPos;
			equals = 0;
			for(int i=0; i<read && equals < mp3Buff[0].length; i++){
				if((int)mp3Buff[0][equals] == (int)pcmBuff[0][i]){
					equals++;
					found = true;
				}
				else{
					equalpos = pcmPos + i + 1;
					equals = 0;
					found = false;
				}
			}
			
//			if(found){
//				pcmFis.position(equalpos);
//				read = pcmFis.read(pcmBuff);
//				for(int i=0; i<read; i++){
//					if((int)mp3Buff[0][i] != (int)pcmBuff[0][i]){
//						found = false;
//						break;
//					}
//				}
//			}
		}
		System.out.println("Equality"+(found?" ":" not ")+"found at position "+equalpos);
		searchResult[0][srI] = found?1:0;
		searchResult[2][srI] = equalpos;
//		if(found)
	//		for(int k=0; k<pcmBuff[0].length; k++){
		//		System.out.println("         pos "+k+": "+(int)pcmBuff[0][k]+" | "+(int)mp3Buff[0][k]);
			//}
	}
	
	public void searchComparison(int[] positions) throws IOException{
		searchResult = new int[3][positions.length];
		searchResult[1] = positions;
		System.out.println(">CompareMP3whithPCM.searchComparison(...)");
		for(srI=0; srI<positions.length; srI++){
			System.out.println("  "+srI+" from "+positions.length+" positions: CompareMP3whithPCM.searchComparison(...) Compare position "+positions[srI]);
			mp3Fis.position(positions[srI]);
			mp3Fis.read(mp3Buff);
			searchForMP3SamplesInPCM();
		}
		System.out.println("<<Compare ended. results:");
		for(int i=0; i<searchResult[0].length; i++){
			System.out.println("mp3Pos "+searchResult[1][i]+((searchResult[0][i]==1)?(" found at pcmPos "+searchResult[2][i]):("not found")));
		}
	}
	
	int[][] searchResult; 
	int srI = 0;
	
	public static void main(String[] a) throws MalformedURLException, IOException{
		File mp3 = getFile();
		MP3toPCMFileConverter conv = new MP3toPCMFileConverter(mp3);
		conv.convert();
		File pcm = conv.getConvertedFile();
		System.out.println("............................Create PositionableFIS...................");
//		PositionableFIS mp3PFIS = AudioUtil.getPositionableFIS(mp3);
//		PositionableFIS pcmPFIS = AudioUtil.getPositionableFIS(pcm);
		PositionableFIS mp3PFIS = (PositionableFIS) new AudioFileObject(mp3.toURI().toURL()).getFloatInputStream();
		PositionableFIS pcmPFIS = (PositionableFIS) new AudioFileObject(pcm.toURI().toURL()).getFloatInputStream();
		System.out.println("... PositionableFIS for PCM : "+pcmPFIS);
		System.out.println("... PositionableFIS for MP3 : "+mp3PFIS);
		new CompareMP3whithPCM(pcmPFIS, mp3PFIS).searchComparison(compPos);
//		new CompareMP3whithPCM(pcmPFIS, mp3PFIS).compare(compPos);
		
//		try{
//			main(null);
//		}catch(Exception e){
//			// e.printStackTrace();
//			System.exit(1);
//		}
	}
	
	// static int[] compPos = {0, 15000, 192837, 1972836, 3576, 368475, 394827364, 36, 3847, 4, 5, 293847, 293846};
	static int[] compPos = {0, 1152, 1152*2, 1152*4, 1152*10, 1152*100, 1152*1000, 1152*10000, 1152*10};
//	static int[] compPos = {10000, 10100, 10200, 10300, 10400, 10500, 10600, 10700, 10800, 10900, 20000}; 
	
	public static File getFile()
	{
		FileDialog fileChooser = new FileDialog(new Frame());
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.setVisible(true);
		if (fileChooser.getFile() == null)
			System.out.println("Problems with FileDialog.");
		return new File(fileChooser.getDirectory() + fileChooser.getFile());
	}

}
