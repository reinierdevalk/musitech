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
package de.uos.fmt.musitech.score.fmx;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.CRC32;

/** This class represents the bounding box information of a font.
 * Height, depth and width of every glyph is stored and can be obtained
 * by the related methods.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class FontMetrics {
	private int[][] metrics = new int[256][3];
	private String fontName = null;
	private String fontFileName = null;
	private int fontSize;
	//   private long crc;     // maybe we can dispense this member

	/* Creates a FontMetric object by reading the information out
	 * of a file.
	 * @param file fmx file to be read */
	/*   public FontMetrics (File file) throws IllegalArgumentException
	   {
	      fontFileName = file.getName();
	      int dotIndex = fontFileName.lastIndexOf('.');
	      if (dotIndex >= 0)
	         fontFileName = fontFileName.substring(0, dotIndex);
	
	      if (!readFromFile(file))
	      	throw new IllegalArgumentException("error reading file '" + file.getPath() + "'");
	   } */

	/** Creates a FontMetric object by reading the information out
	 * of a file given by an URL.
	 * @param url URL pointing to a fmx file */
	public FontMetrics(URL url) {
		try {
//			File file = new File(new URI(url.toString()));
//			fontFileName = file.getName();
			fontFileName = url.getFile();
			int dotIndex = fontFileName.lastIndexOf('.');
			if (dotIndex >= 0)
				fontFileName = fontFileName.substring(0, dotIndex);

			int slashIndex = fontFileName.lastIndexOf('/');
			if (slashIndex >= 0)
				fontFileName = fontFileName.substring(slashIndex+1);

			// FIXME This will not work with file names containing dots within the 
			// filename (before the dot for the extension).
			// TODO check in One-Jar for why this is necessary (provides name like aaa.bbb.ccc.fmx instead of aaa/bbb/ccc.fmx)
			int lastDotIndex = fontFileName.lastIndexOf('.');  
			if (lastDotIndex >= 0)
				fontFileName = fontFileName.substring(lastDotIndex+1);

			if (!readFromStream(url.openStream()))
				throw new IllegalArgumentException("error reading URL " + url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public FontMetrics (InputStream stream)
	{
	   if (!readFromStream(stream))
			throw new IllegalArgumentException("error reading FontMetrics from stream");   
	}*/

	/** Creates FontMetric object out of given font parameters. The
	 * real metrics are not affected; they all equal 0. 
	 * @param argFontName 
	 * @param argFontSize 
	 */
	protected FontMetrics(String argFontName, int argFontSize) {
		this.fontName = argFontName;
		this.fontSize = argFontSize;
	}

	/** Writes the font metrics to a file. 
	 * @param  file file to be written */
	protected void writeToFile(File file) {
		try {
			//         crc = crc32();
			FileOutputStream fstream = new FileOutputStream(file);
			DataOutputStream ostream = new DataOutputStream(fstream);
			ostream.writeInt(fontName.length());
			ostream.writeChars(fontName);
			ostream.writeInt(fontSize);
			for (int i = 0; i < 256; i++)
				for (int j = 0; j < 3; j++)
					ostream.writeInt(metrics[i][j]);
			ostream.flush();
			ostream.close();
			fstream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Loads the metrical information from a given file. 
	 * @param file fmx file with font metrics 
	 * @return true, if reading was successful */
	public boolean readFromFile(File file) {
		try {
			FileInputStream fstream = new FileInputStream(file);
			return readFromStream(fstream);
		}
		catch (FileNotFoundException e) {
			return false;
		}
	}

	public boolean readFromStream(InputStream stream) {
		try {
			DataInputStream dataStream = new DataInputStream(stream);
			int len = dataStream.readInt();
			char[] fn = new char[len];
			for (int i = 0; i < len; i++)
				fn[i] = dataStream.readChar();
			fontName = new String(fn);
			fontSize = dataStream.readInt();
			for (int i = 0; i < 256; i++)
				for (int j = 0; j < 3; j++)
					metrics[i][j] = dataStream.readInt();
			dataStream.close();
			dataStream.close();
			/*         crc = fm.crc;
			         if (crc != crc32())
			         	throw new DataFormatException("checksum error"); */
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Font createFont() {
		Font baseFont = null;
		try {
			// first of all we try to read a font resource...
			InputStream ttfStream = getClass().getResourceAsStream(fontFileName + ".ttf");
			baseFont = Font.createFont(Font.TRUETYPE_FONT, ttfStream);
			return new Font(baseFont.getName(), Font.PLAIN, fontSize);
		}
		catch (Exception e) {
			e.printStackTrace();
			// .. if this doesn't work let's use a font installed on the system
			return new Font(fontName, Font.PLAIN, fontSize);
		}
	}

	public Font createFont(int size) {
		Font baseFont = null;
		try {
			// first of all we try to read a font resource...
			InputStream ttfStream = getClass().getResourceAsStream(fontFileName + ".ttf");
			baseFont = Font.createFont(Font.TRUETYPE_FONT, ttfStream);
			// this doesn't work any more with JDK 1.5
			//return new Font(baseFont.getName(), Font.PLAIN, size);
			return baseFont.deriveFont((float)size);
		}
		catch (Exception e) {
			e.printStackTrace();
			// .. if this doesn't work let's use a font installed on the system       
			return new Font(fontName, Font.PLAIN, size);
		}
	}

	public Font createFont(double scaleFactor) {
		Font baseFont = null;
		try {
			// first of all we try to read a font resource
			InputStream ttfStream = getClass().getResourceAsStream(fontFileName + ".ttf");
			baseFont = Font.createFont(Font.TRUETYPE_FONT, ttfStream);
			return new Font(baseFont.getName(), Font.PLAIN, (int) Math.round(fontSize * scaleFactor));
		}
		catch (Exception e) {
			e.printStackTrace();
			// .. if this doesn't work let's use a font installed on the system
			return new Font(fontName, Font.PLAIN, (int) Math.round(fontSize * scaleFactor));
		}
	}

	/** Returns the width of a character's bounding box.
	 * @param c character 
	 * @return width of charaters c's bounding box 
	 * @throws IllegalArgumentException if c is not in [0..255] */
	public int getWidth(char c) {
		if (c >= 0 && c <= 255)
			return metrics[c][2];
		throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Returns the height of a character's bounding box.
	 * @param c character 
	 * @return height of charaters c's bounding box 
	 * @throws IllegalArgumentException if c is not in [0..255] */
	public int getHeight(char c) {
		if (c >= 0 && c <= 255)
			return metrics[c][0];
		throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Returns the depth of a character's bounding box.
	 * @param c character 
	 * @return depth of charaters c's bounding box 
	 * @throws IllegalArgumentException if c is not in [32..255] */
	public int getDepth(char c) {
		if (c >= 0 && c <= 255)
			return metrics[c][1];
		throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Modifies the height of a given glyph.
	 * @param c affected glyph
	 * @param h new height 
	 * @throws IllegalArgumentException if c is not in [0..255] */
	protected void setHeight(char c, int h) {
		if (c >= 0 && c <= 255)
			metrics[c][0] = h;
		else
			throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Modifies the depth of a given glyph.
	 * @param c affected glyph
	 * @param h new depth
	 * @throws IllegalArgumentException if c is not in [0..255] */
	protected void setDepth(char c, int d) {
		if (c >= 0 && c <= 255)
			metrics[c][1] = d;
		else
			throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Modifies the width of a given glyph.
	 * @param c affected glyph
	 * @param h new width
	 * @throws IllegalArgumentException if c is not in [0..255] */
	protected void setWidth(char c, int w) {
		if (c >= 0 && c <= 255)
			metrics[c][2] = w;
		else
			throw new IllegalArgumentException("Character must be in range [0..255]");
	}

	/** Sets the bounding box information for a given character.
	 * @param c      the affected character   
	 * @param height the character's height (height above the baseline)
	 * @param depth  the character's depth (height below the baseline)
	 * @param width  the character's width */
	protected void setCharMetrics(char c, int height, int depth, int width) {
		if (c < 0 || c > 255)
			throw new IllegalArgumentException("Character must be in range [0..255]");
		metrics[c][0] = height;
		metrics[c][1] = depth;
		metrics[c][2] = width;
	}

	/** Scales the current font metrics and the related font size by a given factor.
	 * @param factor scale factor */
	public void scale(double factor) {
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 3; j++)
				metrics[i][j] = (int) Math.round(factor * metrics[i][j]);
		fontSize = (int) Math.round(factor * fontSize);
	}

	/** Calculates the CRC32 value for the current set of font metrics.
	 * This value is used to check the fmx file's integrity: the file 
	 * contains a pre-calculated CRC value that is compared with the return
	 * of this function. The integrity is proved if both numbers are equal. */
	private long crc32() {
		byte[] bytes = new byte[3 * 256 * 4];
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 3; j++) {
				bytes[6 * i + 2 * j] = (byte) (metrics[i][j] & 0xFF);
				bytes[6 * i + 2 * j + 1] = (byte) ((metrics[i][j] >> 8) & 0xFF);
			}
		CRC32 crc = new CRC32();
		crc.update(bytes);
		return crc.getValue();
	}

	/** Gets the current fontSize. */
	public int getFontSize() {
		return fontSize;
	}

	/** Sets the fontSize. */
	protected void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/** Gets the current fontName. */
	public String getFontName() {
		return fontName;
	}

	/** Sets the fontName. */
	protected void setFontName(String fontName) {
		this.fontName = fontName;
	}

	/** Returns a text representation of the current metrics layout. */
	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < 256; i++) {
			res += "char " + TextUtil.formatInt(i, 3) + "(" + (char) i + ")" + ": ";
			res += "height=" + TextUtil.formatInt(metrics[i][0], 3);
			res += ", depth=" + TextUtil.formatInt(metrics[i][1], 3);
			res += ", width=" + TextUtil.formatInt(metrics[i][2], 3) + "\n";
		}
		return res;
	}
}