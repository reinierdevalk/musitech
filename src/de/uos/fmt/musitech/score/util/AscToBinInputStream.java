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
package de.uos.fmt.musitech.score.util;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class AscToBinInputStream extends FilterInputStream
{
   private int remainder = 0;
   private int leftBits  = 0;
   private static int[] mask = {0, 1, 3, 7, 15, 31, 63};
   
   public AscToBinInputStream (InputStream source)
   {
      super(source);
   }
   
   @Override
public int read () throws IOException
   {
      if (leftBits == 0)
      {
      	remainder = map(super.read()) - '0';
      	leftBits = 6;
      }
      return decode(map(super.read())-'0');
   }  


   @Override
public int read (byte[] b, int off, int len) throws IOException
   {
/*      byte[] source = new byte[4*len/3];
      int numBytes = in.read(source);
      int count = 0;
      for (int i=0; i < numBytes; i++, count++)
      {
         if (leftBits == 0)
         {
         	remainder = map(super.read()) - '0';
         	leftBits = 6;
         }
      	b[count+off] = (byte)decode(map(source[i]) - '0');
      }
      return count;*/
      return 0;
   }

   @Override
public int read (byte[] b) throws IOException
   {
      return 0;//read(b, 0, b.length);
   }
   
   
   public void pipe (OutputStream out) throws IOException
   {
   	int val;
   	while ((val = read()) >= 0)
	      out.write(val);
   }


	private int map (int c)
	{
	   switch (c)
	   {
	      case -1  : return '/';
	      case '+' : return '>';
	   	case '-' : return '<';
	   }
	   return c;
	}
   
   private int decode (int value)
   {
   	if (value < 0)
			return -1;
   	int shift = leftBits - 2;
   	if (shift < 0) 
   		shift = 0;
	  	int result = (remainder << (8-leftBits)) | (value >> shift);
	  	leftBits = (leftBits + 6) % 8;
	  	remainder = value & mask[leftBits];
      return result;
   } 
}
