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

/** This class represents a rectangular box.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Box
{
   private int minX;
   private int minY;
   private int maxX;
   private int maxY;
   
   public Box (int x1, int y1, int x2, int y2)
   {
      if (x1 < x2) {
         minX = x1; maxX = x2;
      } 
      else {
         minX = x2; maxX = x1;
      }

      if (y1 < y2) {
         minY = y1; maxY = y2;
      }
      else {
         minY = y2; maxY = y1;
      }
   }
   
   public Box (Box box)
   {
      minX = box.minX;
      maxX = box.maxX;
      minY = box.minY;
      maxY = box.maxY;
   }
   
   public int getMinX () {return minX;}
   public int getMaxX () {return maxX;}
   public int getMinY () {return minY;}
   public int getMaxY () {return maxY;}
   
   
   /** Enlarges the Box so that it encloses a given point. If the point
    * is already located inside or on the border of the box nothing will happen.
    * @param x x-coordinate of the point
    * @param y y-coordinate of the point */
   public void embed (int x, int y)
   {
      if (x < minX)
         minX = x;
      else if (x > maxX)
         maxX = x;
      if (y < minY)
         minY = y;
      else if (y > maxY)
         maxY = y;
   }         


   /** Enlarges the Box so that it encloses another box. If the second box
    * is already located inside or on the border of the box nothing will happen.
    * @param box the box to be embedded */   
   public void embed (Box box)
   {
      embed(box.minX, box.minY);
      embed(box.maxX, box.maxY);      
   }
   
   /** Returns true if a given point is located outside the box. Points on the
    * border are not outside. */
   public boolean isOutside (int x, int y)
   {
      if (x < minX || x > maxX || y < minY || y > maxY)
         return true;
      return false;
   }
   
   /** Returns true if a given point is located inside the box. Points on the border
    * are not inside. */
   public boolean isInside (int x, int y)
   {
      if (x > minX && x < maxX && y > minY && y < maxY)
         return true;
      return false;
   }

   /** Returns true if a given point is located on the border of the box. */
   public boolean isOnBorder (int x, int y)
   {
      if ((x == minX || x == maxX) && (y == minY || y == maxY))
         return true;
      return false;
   }
}
