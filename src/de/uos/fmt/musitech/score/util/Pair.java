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


/**
 * This class represents a pair of integers than can be interpreted
 * either as a two-dimensional point or the corresponding location vector. 
 * The name "Pair" is borrowed from Donald Knuth' metafont. 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Pair
{
   private double x;
   private double y;

   /** Constructs a Pair with value (0,0). */
   public Pair()
   {
      x = 0;
      y = 0;
   }
   
   /** Constructs a Pair (x,0).
    * @param x the given x-component. */
   public Pair(double x)
   {
      this.x = x;
      this.y = 0;
   }
   
   /** Constructs a Pair with value (x,y) 
    * @param x x-component
    * @param y y-component */
   public Pair(double x, double y)
   {
      this.x = x;
      this.y = y;
   }
   
   /** Copy-constructor: clones a given Pair */
   public Pair(Pair p)
   {
      x = p.x;
      y = p.y;
   }

   public boolean equals (Pair p)
   {
      return x == p.x && y == p.y;
   }

   /** Assigns a new value to this Pair.
    * @param p the new value */   
   public void assign (Pair p)
   {
      x = p.x;
      y = p.y;
   }
   
   /** Increments this Pair component-wise by p. */
   public Pair inc (Pair p)
   {
      x += p.x;
      y += p.y;
      return this;
   }
   
   /** Decrements this Pair component-wise by p */
   public Pair dec (Pair p)
   {
      x -= p.x;
      y -= p.y;
      return this;
   }   
   
   /** @return this + p */
   public Pair add(Pair p)
   {
      return new Pair(x + p.x, y + p.y);
   }
   
   /** Divides this Pair by an integer constant. 
    * @return this / c */
   public Pair div(double c)
   {
      return new Pair(x / c, y / c);
   }
   
   /** @return x-component of this Pair */
   public double getX() {return x;}
   
   /** @return y-component of this Pair */
   public double getY() {return y;}
   
   public int getRoundedX () {return (int)Math.round(x);}
   public int getRoundedY () {return (int)Math.round(y);}
   
   /** Multiplies this Pair by an integer constant. 
    * @param  c integral factor 
    * @return this * c */
   public Pair mul(double c)
   {
      return new Pair(x * c, y * c);
   }
   
   
   /** (x, y) -> (sqrt(x), sqrt(y)) */
   public Pair sqrt ()
   {
      return new Pair(Math.sqrt(x), Math.sqrt(y));   
   }
   
   public Pair square ()
   {
      return new Pair(x*x, y*y);
   }
   
   /** Returns the negated Pair.
    * @return -this */
   public Pair neg()
   {
      return new Pair(-x, -y);
   }
   
   /** Returns the Euclidian length of this Pair. */   
   public double length ()
   {
      return Math.sqrt(x*x + y*y);
   }
   
   /** Returns the Euclidian distance between this Pair and a given one. */
   public double distanceTo (Pair p)
   {
      return sub(p).length();
   }
   
   /** Returns a new Pair with swapped x and y components. */
   public Pair swapped ()
   {
      return new Pair(y,x);
   }

   /** Swaps x and y components of this Pair. */
   public void swap ()
   {
      double t = x;
      x = y; 
      y = t;
   }

   
   /** Returns a textual representation of this Pair */
   public String toString ()
   {
      return "(" + x + ", " + y + ")";
   }
   
   /** Rotates this Pair by a given angle around the origin (0,0) 
    * anticlockwise.
    * @param arc angle given in radians */
   public void rotate(double arc)
   {
      x = (int)Math.round(x * Math.sin(arc) - y * Math.cos(arc));
      y = (int)Math.round(x * Math.cos(arc) + y * Math.sin(arc));
   }
   
   /** Rotates this pair by a given angle around another Pair anticlockwise. 
    * @param arc    angle given in radians
    * @param center the rotation center */
   public void rotate(double arc, Pair center)
   {
      Pair p = sub(center);
      p.rotate(arc);
      assign(p.add(center));
   }
   
   /** Creates a new Pair by rotating this one anticlockwise.
    * @param  arc rotation angle in radians
    * @return the rotated Pair */
   public Pair rotated(double arc)
   {
      Pair p = new Pair(this);
      p.rotate(arc);
      return p;
   }
   
   /** Creates a new Pair by rotating this one anticlockwise around 
    * a given center point.
    * @param  arc    rotation angle in radians
    * @param  center the rotation center
    * @return the rotated Pair  */
   public Pair rotated(double arc, Pair center)
   {
      Pair p = new Pair(this);
      p.rotate(arc, center);
      return p;
   }

	/* 
	 * not implemented yet 
   public Pair mirrored (Pair p1, Pair p2)
   {
		return null;		 		     
   } */
   
   /** Changes the x-component of this Pair. */
   public void setX (double xx) {x = xx;}
   
   /** Changes the y-component of this Pair. */
   public void setY (double yy) {y = yy;}

   /** Substracts a Pair from this and returns the result.
    * @param  p the subtrahend
    * @return this - p */  
   public Pair sub(Pair p)
   {
      return new Pair(x - p.x, y - p.y);
   }
   
   /** Returns the slope of the line through p and this Pair. */
   public double slope (Pair p)
   {
      double dx = p.x - x;
      if (dx != 0)
	      return (p.y - y)/dx;
	   throw new ArithmeticException("\ncan't compute slope between "+this+" and "+p);
   }
   
   /** Returns the slope angle (in radians) of the line through p and this Pair. */
   public double arc (Pair p)
   {
      return Math.atan(slope(p));
   }
   
   /** Returns the unit vector of this Pair (length is stretched to 1). */
   public Pair unitVector ()
   {
      double l = length();
      if (l != 0)
	      return div(length());
	   throw new ArithmeticException("Can't lengthen/shorten zero-vector");
   }

   /** Returns a new vector that is a 90°-anti-clockwise-rotation of this one. */   
   public Pair ortho ()
   {
      return new Pair(-y, x);
   }
   
 	/** Returns a point on the straight line from this to a given end point.
 	 * @param end the end point
 	 * @param t   indicates the position on the line: 0 = start point, 1 = end point,
 	 *            0.5 = mid between start and end point, etc. */
 	public Pair pointOnStraightLine (Pair end, double t)
 	{
 	   return mul(1-t).add(end);
 	}
   
   
   /** Returns the dot/scalar product of this Pair and p.*/
   public double dotProduct (Pair p)
   {
      return x*p.x + y*p.y;
   }  

   /** Returns the Euclidian scalar product of 2 given Pairs. */   
   public static double dotProduct (Pair p1, Pair p2)
   {
      return p1.x*p2.x + p1.y*p2.y;
   } 
}
