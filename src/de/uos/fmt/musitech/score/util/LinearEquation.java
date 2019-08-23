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

/** This class represents a linear equation of the form
 * <i>a<sub>n</sub> x<sup>n</sup></i> +...+ <i>a</i><sub>0</sub> <i>x</i><sup>0</sup> = 0, 
 * where the real numbers <i>a<sub>k</sub></i> are called <i>coefficients</i>.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class LinearEquation
{
   private double[] coeff;

	/** Constructs a new linear equation with numCoeff coefficients. */   
   public LinearEquation (int numCoeffs)
   {
      coeff = new double[numCoeffs];
   }
   
   /** Constructs a new linear equation out of the given coefficients. */
   public LinearEquation (double[] coeff)
   {
   	this.coeff = coeff;
   }
   
   /** Constructs a new linear equation with n coefficients. They are 
    * initialized with the first n elements of 'coeff'. If 'coeff' contains
    * less than n elements the missing values are set to 0. */
   public LinearEquation (double[] coeff, int n)
   {
      this.coeff = new double[n];
      int i=0;
      while (i < n && i < coeff.length)
      	this.coeff[i++] = coeff[i];
      while (i < n)
	      this.coeff[i++] = 0;
   }   
   
   /** Sets a coefficient to a given value.
    * @param n     number of coefficient
    * @param value the coefficient's new value*/
   public void setCoeff (int n, double value) {coeff[n] = value;}

	/** Gets the value of the n-th coefficient. */
   public double getCoeff (int n) {return coeff[n];}   
   
   /** Returns the number of coefficients */
   public int getNumCoeffs ()     {return coeff.length;}
   
   /** Multiplies the whole equation by a given factor */
   public void multiplyBy (double factor)
   {
      for (int i=0; i < coeff.length; i++)
      	coeff[i] *= factor;
   }

   /** Divides the whole equation by a given real number. */
   public void divideBy (double divider)
   {
      for (int i=0; i < coeff.length; i++)
      	coeff[i] /= divider;
   }

	/** Adds a given linear equation to this one.
	 * @throws ArithmeticException if the dimensions of both equations differ */   
   public void add (LinearEquation eq)
   {
      if (eq.coeff.length != coeff.length)
      	throw new ArithmeticException("can't add equations with different dimensions");

      for (int i=0; i < coeff.length; i++)
      	coeff[i] += eq.coeff[i];      
   }

	/** Subtracts a multiple of another linear equation from this one. 
	 * @param eq     equation to be factor-times subtracted
	 * @throws ArithmeticException if the dimensions of both equations differ */   
   public void subMultiple (LinearEquation eq, double factor)
   {
      if (eq.coeff.length != coeff.length)
      	throw new ArithmeticException("can't add equations with different dimensions");

      for (int i=0; i < coeff.length; i++)
      	coeff[i] -= factor*eq.coeff[i];
   }
   
   /** Returns a string representation of this LinearEquation. */
   public String toString ()
   {
      String res = "";
      for (int i=0; i < coeff.length; i++)
    		res += coeff[i] + "  ";
    	return res;  	
   }
}
