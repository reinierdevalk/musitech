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
 * File Test.java
 * Created on 22.04.2003
 */

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.AudioUtil;
import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;

/**
 */
public class Test
{

	public static void main(String a[]){
		

			try
			{
				playFloat();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		
		BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("To stop please enter");
		try
		{
			buff.readLine();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}

		System.out.println("System.exit(0);");
		System.exit(0);
		
	}
	
	public static void playFloat() throws UnsupportedAudioFileException, IOException{
		
		File fFile = new File(getFile());
		AudioInputStream ais = AudioSystem.getAudioInputStream(fFile);
		System.out.println(ais);
		AudioFormat	sourceFormat = ais.getFormat();
		AudioFormat	targetFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
													sourceFormat.getSampleRate(),
													16,
													sourceFormat.getChannels(),
													sourceFormat.getChannels() * 2,
													sourceFormat.getSampleRate(),
													false);
		ais = AudioSystem.getAudioInputStream(targetFormat, ais);
//		AudioFormat audioFormat = ais.getFormat();
		FloatInputStream fis = AudioUtil.getFloatInputStream(ais);
		new FISPlayer(fis).play();
	
	}
	
	public static void playFloat2() throws UnsupportedAudioFileException, IOException{
		new FISPlayer(AudioUtil.getFloatInputStream(getFile())).play();
		
	}
	
	
	public static void playDirect(){
		File fFile = new File(getFile());
		try
		{
			AudioInputStream ais = AudioSystem.getAudioInputStream(fFile);
			System.out.println(ais);

			AudioFormat	sourceFormat = ais.getFormat();
			
			AudioFormat	targetFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
														sourceFormat.getSampleRate(),
														16,
														sourceFormat.getChannels(),
														sourceFormat.getChannels() * 2,
														sourceFormat.getSampleRate(),
														false);

			ais = AudioSystem.getAudioInputStream(targetFormat, ais);
			AudioFormat audioFormat = ais.getFormat();

			SourceDataLine line = null;
		
			DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
			System.out.println(info);
			line = (SourceDataLine) AudioSystem.getLine(info);
			System.out.println(line);
			
			line.open(audioFormat);
			line.start();
			System.out.println("DataLine.available() = "+line.available());
			
			byte[] byteData = new byte[128000];
			
			for(int i=ais.read(byteData); i!=-1; i=ais.read(byteData)){
//				System.out.println(
					line.write(byteData, 0, i);// + 
//									"  "+i);
			}
				
			line.drain();
			line.close();
			
			System.exit(0);

		} catch (UnsupportedAudioFileException e2){
			e2.printStackTrace();
		} catch (IOException e2){
			e2.printStackTrace();
		} catch (LineUnavailableException e){
			e.printStackTrace();
		} 
				
	}


	public static String getFile()
	{
		FileDialog fileChooser = new FileDialog(new Frame());
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.setVisible(true);
		if (fileChooser.getFile() == null)
			System.out.println("Problems with FileDialog.");
		return fileChooser.getDirectory() + fileChooser.getFile();
	}


}
