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

/** This class represents a system of multiple linear equations:<br>
 *    a<sub>0,n</sub> x<sup>n</sup> +...+ a<sub>0,0</sub> x<sup>0</sup>= 0<br>
 *    a<sub>1,n</sub> x<sup>n</sup> +...+ a<sub>2,0</sub> x<sup>0</sup>= 0<br>
 *    ...<br>
 *    a<sub>(n-1),n</sub> x<sup>n</sup> +...+ a<sub>(n-1),0</sub> x<sup>0</sup>= 0<br>
 * 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class LinearEquationSystem
{
   private LinearEquation[] equations;
   private double[] solution;
   
   /** Constructs a new linear equation system consisting of multiple
    * linear equations with a given number of coefficients. The coefficients themselves
    * can be set by using the method setCoeff.
    * @param numEquations number of equations */
   public LinearEquationSystem (int numEquations)
   {
      if (numEquations <= 0)
      	throw new IllegalArgumentException();

      equations = new LinearEquation[numEquations];
      for (int i=0; i < numEquations; i++)
      	equations[i] = new LinearEquation(numEquations+1);
   }

	/** Constructs a new linear equation system. The equations are given by
	 * an array of LinearEquations. */
	public LinearEquationSystem (LinearEquation[] eq)
	{
	   if (eq == null)
	   	throw new IllegalArgumentException("argument must not be null");
	   int dim = eq[0].getNumCoeffs();
	   for (int i=1; i < eq.length; i++)
	   	if (eq[i].getNumCoeffs() != dim)
	   		throw new IllegalArgumentException("all equations must have the same number of coefficients");
		equations = eq;	   		
	}

	/** Constructs a new linear equation system. The coefficients of the equations are
	 * given by an 2-dimensional array.
	 * @param coeff the coefficients of the equations. coeff[i] holds the coefficients of
	 *               equation i.*/
	public LinearEquationSystem (double[][] coeff)
	{
	   if (coeff == null)
	   	throw new IllegalArgumentException("argument must not be null");
	   int dim = coeff[0].length;
	   for (int i=1; i < coeff.length; i++)
	   	if (coeff[i].length != dim)
	   		throw new IllegalArgumentException("all equations must have the same number of coefficients");
	   equations = new LinearEquation[coeff.length];
		for (int i=0; i < coeff.length; i++)
			equations[i] = new LinearEquation(coeff[i]);
	}

   
   /** Sets a single coefficient to a given value.
    * @param eq    number of the equation to be affected
    * @param coeff number of the coefficient to be set
    * @param value the new value to be assigned */
   public void setCoeff (int eq, int coeff, double value)
   {
      equations[eq].setCoeff(coeff, value);
   }

   /** Returns a single coefficient of this equation system. 
    * @param eq    number of the equation 
    * @param coeff number of the coefficient to be returned
    * @return the coefficient */   
   public double getCoeff (int eq, int coeff)
   {
      return equations[eq].getCoeff(coeff);
   }
      
   /** Returns the number of equations. */
   public int getNumEquations () {return equations.length;}
   
   /** Tries to solve the linear equation system. If the system's rank is maximal
    *  has maximum rank, the solution vector is returned. Otherwise the length 
    * of the returned array equals the rank. */
   public double[] solve ()
   {
      reduce();
      return solution;
   }
   
   /** Reduces the equation system by the Gaussian algorithm and moves the
    * solution to the solution vector. */
   private void reduce ()
   {
      int rank = equations.length;
      for (int i=0; i < equations.length; i++)
      {         
         // look for an equation that has a non-zero i-th coefficient and put it at 
         // at the i-th position.
         for (int j=i+1; j < equations.length && equations[i].getCoeff(i) == 0; j++)
	   		swapEquations(i, j);
   		double coeff = equations[i].getCoeff(i);
   		if (coeff != 0) // found valid equation?
   			equations[i].divideBy(coeff);
   		else            // no => equation system has no maximal rank
   			rank--;
   		for (int j=0; j < equations.length && coeff != 0; j++)
   			if (i != j)
	   			equations[j].subMultiple(equations[i], equations[j].getCoeff(i));
      }
      solution = new double[rank];
      for (int i=0; i < rank; i++)
      	solution[i] = -equations[i].getCoeff(equations.length);
   }
   
   /** Swaps equations eq1 and eq2. */
   public void swapEquations (int eq1, int eq2)
   {
      LinearEquation temp = equations[eq1];      
      equations[eq1] = equations[eq2];
      equations[eq2] = temp;
   }  
	
	/** Returns a string representation of this LinearEquationSystem. */   
   public String toString ()
   {
      String res = "";
      for (int i=0; i < equations.length; i++)
      	res += equations[i] + "\n";
      return res; 
   }

	/** Just for testing purposes. */   
   public static void main(String[] arguments)
   {
      double[][] c = {{0,2,3,4},{2,3,4,5},{3,4,6,6}};
   	LinearEquationSystem eqs = new LinearEquationSystem(c); 
   	double solution[] = eqs.solve();
   	System.out.println(eqs);
   	System.out.println("rank of equation system is " + solution.length);
   	System.out.println("solution:");
   	for (int i=0; i < solution.length; i++)
   		System.out.println("x" + i + " = " + solution[i]);
   }
}
