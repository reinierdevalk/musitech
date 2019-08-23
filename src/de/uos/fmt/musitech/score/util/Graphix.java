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

import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

/** Wrapper graphics class that provides some drawing methods for Pair parameters.
 *  It is used to simlpify things.
 *  @author Martin Gieseking
 *  @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Graphix
{
   private Graphics graphics;
   
   public Graphix (Graphics g) {graphics = g;}
   
   /** Draws a line from p1 to p2. */
   public void drawLine (Pair p1, Pair p2)
   {
      graphics.drawLine(p1.getRoundedX(), p1.getRoundedY(), 
                        p2.getRoundedX(), p2.getRoundedY());
   }
   
   /** Draws a rectangle. 
    * @param p upper left corner
    * @param width  width of rectangle 
    * @param height height of rectangle  */      
   public void drawRect (Pair p, int width, int height)
   {
      graphics.drawRect(p.getRoundedX(), p.getRoundedY(), 
                        width, height);
   }

   /** Draws a filled rectangle. 
    * @param p upper left corner
    * @param width  width of rectangle 
    * @param height height of rectangle  */      
   public void fillRect (Pair p, int width, int height)
   {
      graphics.fillRect(p.getRoundedX(), p.getRoundedY(), 
                        width, height);
   }

   /** Draws a filled polygon given by an array of Pairs. */      
   public void fillPolygon (Pair[] points)
   {
		int[] x = new int[points.length];
		int[] y = new int[points.length];      
		for (int i=0; i < points.length; i++)
		{
		   x[i] = points[i].getRoundedX();
  		   y[i] = points[i].getRoundedY();
		}		
      graphics.fillPolygon(x, y, points.length);
   }

   /** Draws a filled polygon given by varoius Pairs in a Vector. */   
   public void fillPolygon (List<Pair> points)
   {
      int size = points.size();
		int[] x = new int[size];
		int[] y = new int[size];      
		for (int i=0; i < size; i++)
		{
		   x[i] = (points.get(i)).getRoundedX();
  		   y[i] = (points.get(i)).getRoundedY();
		}		
//      graphics.drawPolygon(x, y, size);
      graphics.fillPolygon(x, y, size);
   }
   
   /** Draws a small cross at the given position. */
   public void drawCross (Pair p)
   {
      int x = (int)Math.round(p.getX());
      int y = (int)Math.round(p.getY());      
      graphics.drawLine(x-5, y, x+5, y);
      graphics.drawLine(x, y-5, x, y+5);
   }
}
