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
package de.uos.fmt.musitech.utility.math;

import java.util.Random;

/**
 * This class provides some useful mathematical functions that are not
 * implemented in the standard libraries.
 * 
 * @author Tillman Weyde / Martin Gieseking
 * @version $Revision: 8040 $, $Date: 2012-01-21 13:47:50 +0100 (Sat, 21 Jan 2012) $
 */
public class MyMath {
	
	static final Random rand = new Random();

    // Signal processing
    /**
     * constant: ln(10)
     */
    static final double ln10 = Math.log(10.0);

    static final double d20_div_ln10 = 20 / ln10;
    static final double ln10_div_20 = ln10 / 20;
    static final double d10_div_ln10 = 10 / ln10;
    static final double ln10_div_10 = ln10 / 10;

    /**
     * Get the linear ratio for a DeciBel value for amplitude. dB=10*log10(gain)
     * <=>gain=10^(dB/10) calculates a linear scaling factor from a dB amplitude value.
     * 
     * @param dB_amp the dB amplitude value
     * @return the factor
     */
    static public double dBToLinearAmp(double dB_amp) {
        return Math.exp(dB_amp * ln10_div_10);
    }

    /**
     * Get the DeciBel energy value of a gain factor (linear ratio). dB=20*log10(gain)
     * 
     * @param gain The ratio (gain factor).
     * @return The dB value
     */
    static public double linearToDBAmp(double gain) {
        return Math.log(gain) * d10_div_ln10;
    }

    /**
     * Get the linear ratio for a Decibel value for energy. dB=20*log10(gain)
     * <=>gain=10^(dB/20) calculates a linear scaling factor from a dB energy value.
     * 
     * @param dB the dB energy value
     * @return the gain factor
     */
    static public double dBEnergyToLinear(double dB) {
        return Math.exp(dB * ln10_div_20);
    }

    /**
     * Get the energy Decibel value of a gain factor (linear ratio). dB=20*log10(gain)
     * 
     * @param gain The gain factor.
     * @return The dB energy value 
     */
    static public double linearToDBEnergy(double gain) {
        return Math.log(gain) * d20_div_ln10;
    }

    /**
     * Get the natural logatrithm of 10.
     * @return ln 10
     */
    public static double getLn10() {
        return ln10;
    }

    /**
     * The sinc function returns the trigonometric sine divided by the argument.
     * 
     * @param arg
     * @see java.lang.Math#sin(double a)
     */
    public static double sinc(double a) {
        if (a == 0)
            return 1;
        if (Double.isNaN(a))
            return Double.NaN;
        return Math.sin(a) / a;
    }

    /** 
     * Returns a (pseudo-)random number between 0 (incl.) and the argument <code>sup</code> (excl.). 
     */
    static public int randIntBelow(int sup){
    	return rand.nextInt(sup);
//    	double rand = Math.random();
//    	return (int)Math.floor(rand*(sup));
    }

    /** 
     * Returns a (pseudo-)random boolean. 
     */
    static public boolean randBool(){
    	return rand.nextBoolean();
    }

    static boolean iset;
    static double gset;

    /**
     * Returns a Gauss distributed random number with mean 0 and variance 1.
     * Uses Box-Mueller method, implementation after Numerical recipies
     * (www.nr.com).
     * 
     * @return double The random value.
     */
    public static double gaussRand() {
        double fac, rsq, v1, v2;
        if (!iset) { // We don't have an extra deviate handy, so
            do {
                // pick two uniform numbers in the square
                v1 = 2.0 * Math.random() - 1.0;
                // extending from -1 to +1 in each direction,
                v2 = 2.0 * Math.random() - 1.0;
                rsq = v1 * v1 + v2 * v2; // see if they are in the unit circle,
            } while (rsq >= 1.0 || rsq == 0.0); // and if they are not, try
                                                // again.
            fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
            // Now make the Box-Mueller transformation to get two normal
            // deviates. Return one and save the other for next time.
            gset = v1 * fac;
            iset = true; // Set flag.
            return v2 * fac;
        } else { // We have an extra deviate handy,
            iset = false; // so unset the flag,
            return gset; // and return it.
        }
    }

    /**
     * Returns Gauss distributed random numbers with given mean and variance.
     * Uses Box-Mueller method, implementation after Numerical recipies
     * (www.nr.com).
     * 
     * @param double stdDev
     * @return double The random value.
     */
    public static double gaussRand(double stdDev) {
        return gaussRand() * stdDev;
    }

    /**
     * Returns Gauss distributed random numbers with given mean and variance.
     * Uses Box-Müller method, implementation after Numerical recipies
     * (www.nr.com).
     * 
     * @param double stdDev
     * @param double mean
     * @return double The random value.
     */
    public static double gaussRand(double stdDev, double mean) {
        return gaussRand() * stdDev + mean;
    }

    // Arithmetics

    /**
     * Calculates the greatest common divider of two integers by Euclid's
     * extended algorithm. The integers must be non-negative else an
     * IllegalArgumentException is thrown.
     * 
     * @param a first integer
     * @param b second integer
     * @return greatest common divider of a and b
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int gcd(int a, int b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("gcd parameters must not be negative (a=" + a
                                               + ", b=" + b + ")");
        int h;
        while (b != 0) {
            h = a % b;
            a = b;
            b = h;
        }
        return a;
    }

    /**
     * Calculates the greatest common divider of two longs by Euclid's extended
     * algorithm. The longs must be non-negative else an
     * IllegalArgumentException is thrown.
     * 
     * @param a first long
     * @param b second long
     * @return greatest common divider of a and b
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static long gcd(long a, long b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("gcd parameters must not be negative (a=" + a
                                               + ", b=" + b + ")");
        long h;
        while (b != 0) {
            h = a % b;
            a = b;
            b = h;
        }
        return a;
    }

    /**
     * Calculates the greatest common divider of multiple integers. The
     * considered integers must be non-negative.
     * 
     * @param integers vector of integers
     * @param first index of the first considered integer in the given vector
     * @param last index of the last considered integer in the given vector
     * @return greatest common divider of integers[first],...,integers[last]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static long gcd(long[] longs, int first, int last) {
        if (first == last)
            return longs[first];
        int mid = (first + last) / 2;
        return gcd(gcd(longs, first, mid), gcd(longs, mid + 1, last));
    }

    /**
     * Calculates the greatest common divider of multiple integers. The
     * considered integers must be non-negative.
     * 
     * @param integers vector of integers
     * @param first index of the first considered integer in the given vector
     * @param last index of the last considered integer in the given vector
     * @return greatest common divider of integers[first],...,integers[last]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int gcd(int[] integers, int first, int last) {
        if (first == last)
            return integers[first];
        int mid = (first + last) / 2;
        return gcd(gcd(integers, first, mid), gcd(integers, mid + 1, last));
    }

    /**
     * Calculates the greatest common divider of multiple integers. All integers
     * must not be negative.
     * 
     * @param integers vector of integers
     * @return greatest common divider of
     *         integers[0],...,integers[integers.length-1]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int gcd(int[] integers) {
        return gcd(integers, 0, integers.length - 1);
    }

    /**
     * Calculates the greatest common divider of multiple integers. All integers
     * must not be negative.
     * 
     * @param integers vector of integers
     * @return greatest common divider of
     *         integers[0],...,integers[integers.length-1]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static long gcd(long[] longs) {
        return gcd(longs, 0, longs.length - 1);
    }

    /**
     * Calculates the least common multiple of 2 given integers. Both integers
     * must not be negative.
     * 
     * @param a first integer
     * @param b second integer
     * @return least common multiple of a and b
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    /**
     * Calculates the least common multiple of 2 given integers. Both integers
     * must not be negative.
     * 
     * @param a first integer
     * @param b second integer
     * @return least common multiple of a and b
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    /**
     * Calculates the least common multiple of several integers.
     * 
     * @param integers vector of integers
     * @param first index of the first considered integer in the given vector
     * @param last index of the last considered integer in the given vector
     * @return least common multiple of integers[first],...,integers[last]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int lcm(int[] integers, int first, int last) {
        int result = integers[first] / gcd(integers, first, last);
        while (++first <= last)
            result *= integers[first];
        return result;
    }

    /**
     * Calculates the least common multiple of integers given by an array. All
     * integers must be non-negative.
     * 
     * @param integers vector of integers
     * @return least common multiple of
     *         integers[0],...,integers[integers.length-1]
     * @throws IllegalArgumentException if one of the integers is negative
     */
    public static int lcm(int[] integers) {
        return lcm(integers, 0, integers.length - 1);
    }

    /**
     * Returns true if a given integer is a power of 2.
     * 
     * @param n integer to be tested
     */
    public static boolean isPowerOf2(int n) {
        if (n < 0)
            return false;
        while ((n & 1) == 0)
            n >>= 1;
        return n <= 1;
    }

    /**
     * Returns the n-th root of x. Unfortunately we cannot use the function
     * Math.pow(double, double) to compute arbitrary roots, e.g. Math.pow(-1,
     * 1./3) will throw an exception even though the 3rd root of -1 is well
     * defined. Because of this we provide our own function to extracting a
     * root.
     */
    public static double root(double x, int n) {
        int sign = x < 0 ? -1 : 1;
        return (n % 2 != 0 ? sign : 1) * Math.pow(sign * x, 1. / n);
    }

    /**
     * Calculates the logarithm of <code>x</code> to the base
     * <code>base</code>.
     * 
     * @param base The base of the logarithm.
     * @param x The number for which to calculate the logarithm.
     * @return The logarithm of x to base.
     */
    public static double log(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    // General

    /**
     * Returns -1 if n is negative, 1 if positive and 0 if zero.
     */
    public static int sign(long n) {
        if (n == 0)
            return 0;
        return n < 0 ? -1 : 1;
    }

    /**
     * Returns the integer part of a value's binary logarithm.
     * 
     * @param n logarithm to be computed of
     * @throws ArithmeticException if a non-positive argument was given
     */
    public static int ilog2(int n) {
        if (n <= 0)
            throw new ArithmeticException("logarithm of non-positive number is undefined");
        int log = 0;
        while ((n >>= 1) != 0)
            log++;
        return log;
    }

    /**
     * Returns the greatest power of 2 that is less or equal to the argument
     * 'n'. The result is 0 if n is less than 1.
     * 
     * @param n The number to process.
     * @return The result.
     */
    public static int floorPower2(int n) {
        if (n <= 0)
            return 0;
        int log2 = ilog2(n);
        return 1 << log2;
    }
    
    /**
     * Returns the greatest power of 2 that is less or equal to the argument
     * 'n'. The result is 0 if n is less than 1.
     * 
     * @param n The number to process.
     * @return The result.
     */
    public static int ceilPower2(int n) {
        if (n <= 0)
            return 0;
        int log2 = ilog2(n);
        if (!isPowerOf2(n))
            log2 += 1;
        return 1 << log2;
    }
    
    /**
     * Get the next even integer number equal or less to the argument.
     * 
     * @param i the number to test.
     * @return The next even integer number equal or less to the argument.
     */
    public static int floorEven(int i){
    	return 0xFFFFFFFE & i;
    }

    /**
     * Returns the number of successional bits of a given number. If the binary
     * representation of n has the form 0..01..10..0 the number of succesive set
     * bits is returned. E.g. in the case of 28(dec) = 11100(bin) 3 is returned.
     * If n is not of the stated form (like 29(dec) = 11101(bin)) -1 is
     * returned.
     */
    public static int bitRunLength(int n) {
        if (n == 0)
            return 0;
        // skip rightmost zeros
        while (n % 2 == 0)
            n >>= 1;
        // count the following set bits
        int count = 0;
        for (; n % 2 != 0; n >>= 1)
            count++;
        // now no further bits should be set, otherwise we return -1
        return n == 0 ? count : -1;
    }

    /**
     * Calculates the binary coefficient 'n over k', which equals n! /(k!
     * *(n-k)!).
     * 
     * @param n the total number of coefficients
     * @param k the number of this coefficient
     * @return The result.
     */
    static public int binCoeff(int n, int k) {
        if (k > n || k < 0 || n < 0) {
            return 0;
        }
        if (k == 0)
            return 1;
        else if (k == n)
            return 1;
        else
            return binCoeff(n - 1, k - 1) + binCoeff(n - 1, k);
    }

    // Trigonometry

    /** Returns the cotangent of x. */
    public static double cot(double x) {
        double sine = Math.sin(x);
        if (sine == 0.0)
            throw new ArithmeticException("cot(" + x + ") is undefined");
        return Math.cos(x) / Math.sin(x);
    }

    /** Returns the hyperbolic sine of x. */
    public static double sinh(double x) {
        return (Math.exp(x) - Math.exp(-x)) / 2.0;
    }

    /** Returns the hyperbolic cosine of x. */
    public static double cosh(double x) {
        return (Math.exp(x) + Math.exp(-x)) / 2.0;
    }

    /** Returns the hyperbolic tangent of x. */
    public static double tanh(double x) {
        double pos = Math.exp(x);
        double neg = Math.exp(-x);
        return (pos - neg) / (pos + neg);
    }

    // Statistics

    /**
     * Calculates the arithmetic mean of double values given as an array.
     * @param values
     * @return The mean to all values in the argument array. 
     */
    public static double mean(double[] values) {
        return mean(values, 0, values.length);
    }
    
    /**
     * Recursive implementation of median, which returns the element 
     * that would appear at position data.length/2 if the array were sorted.
     * @param data
     * @return the median
     */
    @SuppressWarnings("unused")
	private static int median1(int[] data) {
    	return foo1(data, 0, data.length-1);
    }

    private static int foo1(int[] data, int first, int last) {
		int pivot = partition(data, first, last);
		if(pivot == data.length/2)
			return data[pivot];
		if(pivot < data.length/2 ) 
			first = pivot + 1;
		else	
			last = pivot - 1;
		return foo1(data, first, last);
    }  

    /**
     * Returns the element that would appear at position data.length/2 if the array were sorted.
     * @param data
     * @return the median
     */
    public static int median(int[] data) {
    	int first =0; 
    	int last=data.length-1;
    	int pivot;
    	do {
			pivot = partition(data, first, last);
			if(pivot < data.length/2 ) 
				first = pivot + 1;
			else	
				last = pivot - 1;
    	} while(pivot != data.length/2);
		return data[pivot];
    }  

    /** used by median */
    private static int partition(int[] data, int first, int last) {
		swap(data, first, (first + last) / 2); // move pivot to data[first]
		int lower = first + 1, upper = last;
		while (lower <= upper) {
			while (lower < last && data[lower] < data[first])
				lower++; // after loop data[lower] is >= pivot
			while (data[upper] > data[first])
				// pivot ensures range
				upper--; // after loop data[upper] is <= pivot
			if (lower < upper) // if borders don't overlap
				swap(data, lower++, upper--);
			else
				lower++; // make sure the loop is exited
		}
		swap(data, first, upper); // move pivot between partitions return
									// upper;
		return upper;
	}


    private static void swap(int[] data, int i, int j) {
    	int tmp = data[i];
    	data[i] = data[j];
    	data[j] = tmp;
	}

	/**
     * Calculates the arithmetic mean of double values.
     * 
     * @param values the values
     * @param start first value to be considered 
     * @param end first value not to be considered
     * @return The arithmentic mean of all values at positions from start to end-1,
     */
    public static double mean(double[] values, int start, int end) {
        double result = 0;
        for (int i = start ; i < end; i++)
            result += values[i];
        return result / (end - start);
    }

    /**
     * Updates the arithmetic mean by adding a further double value. This method
     * can be used for iterative mean calculation i.e. of a successively read
     * data stream.
     * 
     * @param previousMean the previous calculated arithmetic mean
     * @param newValue the new attached value
     * @param n number of meaned values (newValue inclusive)
     */
    public static double mean(double previousMean, double newValue, int n) {
        if (n <= 0)
            throw new IllegalArgumentException("MyMath.mean: n must be positive");
        return (newValue + (n - 1) * previousMean) / n;
    }

    /**
     * Solves a (real) linear equation of the form ax+b = 0 where a and b are
     * given coefficents and x the variable to be found.
     * 
     * @return null if no solution exists, otherwise the first array element
     *         contains the solution. If a and b equals 0 all real values solve
     *         the equation. In this case the first array element is set to
     *         Math.NaN.
     */
    public static double[] solveLinearEq(double a, double b) {
        double[] x = {Double.NaN};
        if (a != 0)
            x[0] = -b / a;
        else if (b != 0)
            return null;
        return x;
    }

    /**
     * Solves a (real) quadratic equation of the form <i>ax <sup>2 </sup>+bx+c =
     * 0 </i> where <code>a, b</code> and <i>c </i> are given coefficents and
     * <code>x</code> the variable to be found.
     * 
     * @return the solution vector. If <code>a</code> equals zero the equation
     *         is solved by solveLinearEq.
     */
    public static double[] solveQuadraticEq(double a, double b, double c) {
        if (a == 0)
            return solveLinearEq(b, c);
        b /= a;
        c /= a;
        double d = b * b / 4 - c;
        if (d < 0) // only complex solutions
            return null;
        b = -b / 2;
        if (d == 0) // only one solution
        {
            double[] x = {b};
            return x;
        }
        d = Math.sqrt(d);
        double[] x = {b + d, b - d};
        return x;
    }

    /**
     * Solves a (real) cubic equation of the form <i>ax <sup>3 </sup>+bx <sup>2
     * </sup>+cx+d = 0 </i> where a, b, c and d are given coefficents and x the
     * variable to be found. This function uses Cardano's formulas to find the
     * solutions.
     * 
     * @return the solution vector. If a equals 0 the equation is solved by
     *         solveQuadraticEq.
     */
    public static double[] solveCubicEq(double a, double b, double c, double d) {
        if (a == 0)
            return solveQuadraticEq(b, c, d);
        b /= a;
        c /= a;
        d /= a;
        double p = (3 * c - b * b) / 3;
        double q = d + 2 * b * b * b / 27 - b * c / 3;
        double discr = p * p * p / 27 + q * q / 4;

        double x_array[] = null;
        if (discr < 0) // casus irreducibilis => 3 real solutions
        {
            b = -b / 3;
            double v = Math.abs(p) / 3;
            double u = 2 * Math.sqrt(v);
            double phi = Math.acos(-q / 2 / Math.pow(v, 1.5));
            x_array = new double[3];
            x_array[0] = b + u * Math.cos(phi / 3);
            x_array[1] = b - u * Math.cos((phi - Math.PI) / 3);
            x_array[2] = b - u * Math.cos((phi + Math.PI) / 3);
        } else {
            double u = root(-q / 2 + Math.sqrt(discr), 3);
            double v = root(-q / 2 - Math.sqrt(discr), 3);
            double s = -b / 3 + u + v;
            if (discr > 0) // 1 real, 2 complex solutions
            {
                x_array = new double[1];
                x_array[0] = s;
            } else // 2 real solutions
            {
                x_array = new double[2];
                x_array[0] = s;
                x_array[1] = -b / 3 - (u + v) / 2;
            }
        }
        return x_array;
    }

    /** @@ just for testing purposes; may be deleted (MG) */
    public static void main(String[] args) {
        double x[] = solveCubicEq(1, -2, -1, 1);
        for (int i = 0; i < x.length; i++)
            System.out.println("x[" + i + "] = " + x[i]);
    }

    /**
     * Convenience method for calculating the square of a number.
     */
    public static double square(double x) {
        return x * x;
    }

    // Geometry
    /**
     * Calculate the length of a two-dimensional vector using Pythagoras'
     * theorem.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the length
     */
    public static double vectorLength(double x, double y) {
        return Math.sqrt(square(x) + square(y));
    }

}
