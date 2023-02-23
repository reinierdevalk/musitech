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
 *	MidiUtils.java
 */

/*
 *  Copyright (c) 1999 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
 *
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package	de.uos.fmt.musitech.framework.time;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;





/**	Helper methods for reading and writing MIDI files.
 */
public class MidiUtils
{
	public static int getUnsignedInteger(byte b)
	{
		return (b < 0) ? b + 256 : b;
	}



	public static int get14bitValue(int nLSB, int nMSB)
	{
		return (nLSB & 0x7F) | ((nMSB & 0x7F) << 7);
	}



	public static int get14bitMSB(int nValue)
	{
		return (nValue >> 7) & 0x7F;
	}



	public static int get14bitLSB(int nValue)
	{
		return nValue & 0x7F;
	}



	public static byte[] getVariableLengthQuantity(long lValue)
	{
		ByteArrayOutputStream	data = new ByteArrayOutputStream();
		try
		{
			writeVariableLengthQuantity(lValue, data);
		}
		catch (IOException e)
		{
			
		}
		return data.toByteArray();
	}



	public static int writeVariableLengthQuantity(long lValue, OutputStream outputStream)
		throws	IOException
	{
		int	nLength = 0;
		// IDEA: use a loop
		boolean	bWritingStarted = false;
		int	nByte = (int) ((lValue >> 21) & 0x7f);
		if (nByte != 0)
		{
			if (outputStream != null)
			{
				outputStream.write(nByte | 0x80);
			}
			nLength++;
			bWritingStarted = true;
		}
		nByte = (int) ((lValue >> 14) & 0x7f);
		if (nByte != 0 || bWritingStarted)
		{
			if (outputStream != null)
			{
				outputStream.write(nByte | 0x80);
			}
			nLength++;
			bWritingStarted = true;
		}
		nByte = (int) ((lValue >> 7) & 0x7f);
		if (nByte != 0 || bWritingStarted)
		{
			if (outputStream != null)
			{
				outputStream.write(nByte | 0x80);
			}
			nLength++;
		}
		nByte = (int) (lValue & 0x7f);
		if (outputStream != null)
		{
			outputStream.write(nByte);
		}
		nLength++;
		return nLength;
	}
}



/*** MidiUtils.java ***/
