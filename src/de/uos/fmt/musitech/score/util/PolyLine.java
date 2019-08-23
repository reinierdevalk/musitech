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
import java.util.ArrayList;
import java.util.List;

/** Die Beschreibung des Typs hier eingeben.
 *  @author Martin Gieseking
 *  @version $Revision: 7930 $, $Date: 2011-05-31 18:36:44 +0200 (Tue, 31 May 2011) $ */
public class PolyLine
{
   private List<Pair> points = new ArrayList<Pair>();
   
   /** Constructs an empty PolyLine. */
   public PolyLine () {}
   
   /** Constructs a PolyLine from a given array of points. */   
   public PolyLine (Pair[] points)
   {
      if (points == null)
         return;      
      for (int i=0; i < points.length; i++)
         this.points.add(points[i]);
   }
   
   /** Constructs a PolyLine from a given list of points. */   
   public PolyLine (List<Pair> list)
   {
      if (list == null)
         return;
      points.addAll(list);
   }
   
   /** Constructs a new PolyLine from a given Bézier curve. 
    * @param bezier The Bézier curve.
    *  @param numPoints The number of points computed is always a power of 2 plus 1. 
    *         This parameter gives an upper border that will not be crossed. 
    *         E.g. if maxPoints = 60, 33 curve points are computed because 33 is the greatest 
    *         integer that solves the inequation 2^d+1 &leq; maxpoints */
   public PolyLine (Bezier bezier, int numPoints)
   {
      points = bezier.collectToList(numPoints);
   }
   
   /** Adds a point at the end of this PolyLine. */
   public void add (Pair p)
   {
      points.add(p);
   }
   
   /** Draws this PolyLine to a given graphics context. */
   public void paint (Graphics g)
   {
      int[] x = new int[points.size()];
      int[] y = new int[points.size()];
      for (int i=0; i < x.length; i++)
      {
         Pair p = (Pair)points.get(i);
         x[i] = p.getRoundedX();
         y[i] = p.getRoundedY();
      }      
      g.drawPolyline(x, y, x.length);
   }
   
   protected double getY (double x, int segment)
   {
      Pair p1 = (Pair)points.get(segment);  
      Pair p2 = (Pair)points.get(segment+1);      
      // ensure that p1 contains point with minimum x coordinate
      if (p1.getX() > p2.getX())
      {
         Pair t = p1;
         p1 = p2;
         p2 = t;
      }            
      if (x < p1.getX() || x > p2.getX()) // x outside this line segment?
         return Double.NaN;
      if (p1.getX() == p2.getX())   // vertical line segments...
         return Double.NaN;         // ...currently not supported
      double slope = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());    
      return p1.getY() + slope*(x-p1.getX());
   }
   
   public double[] getY (double x)
   {
      double[] sol = new double[points.size()-1];
      int numSolutions = 0;
      for (int i=0; i < points.size()-1; i++)
      {
         double y = getY(x, i);
         if (!Double.isNaN(y) )
            sol[numSolutions++] = y;
      }
      double[] res = new double[numSolutions];
      System.arraycopy(sol, 0, res, 0, numSolutions);
      return res;          
   }
   
   protected double getX (double y, int segment)
   {
      Pair p1 = (Pair)points.get(segment);  
      Pair p2 = (Pair)points.get(segment+1);      
      // ensure that p1 contains point with minimum x coordinate
      if (p1.getY() > p2.getY())
      {
         Pair t = p1;
         p1 = p2;
         p2 = t;
      }            
      if (y < p1.getY() || y > p2.getY()) // y outside this line segment?
         return Double.NaN;
      if (p1.getY() == p2.getY())   // horizontal line segments...
         return Double.NaN;         // ...currently not supported
      double slope = (p2.getX()-p1.getX())/(p2.getY()-p1.getY());    
      return p1.getX() + slope*(y-p1.getY());
   }
   
   public double[] getX (double y)
   {
      double[] sol = new double[points.size()-1];
      int numSolutions = 0;
      for (int i=0; i < points.size()-1; i++)
      {
         double x = getY(y, i);
         if (!Double.isNaN(x))
            sol[numSolutions++] = x;
      }
      double[] res = new double[numSolutions];
      System.arraycopy(sol, 0, res, 0, numSolutions);
      return res;          
   }   
}