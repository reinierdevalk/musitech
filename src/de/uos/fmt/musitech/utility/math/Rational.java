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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.data.utility.IEquivalence;

/**
 * This class represents a rational number (a fraction) consisting of a
 * numerator and a denominator. All operations return reduced Rationals and
 * reduce the Rationals before they are used, but getters and setters do no not
 * perform reduction. <BR>
 * <BR>
 * Example: 8/6 = 1 + 1/3 will be written as <BR>
 * <code>new Rational(8,6).equals(new Rational(1,3).add(new Rational(1)))</code> 
 * and returns <code>true<code>.
 * But <code>r.setNumer(8); r.setDenom(6);</code> will not produce a 
 * reduced fraction automatically.
 * Rational objects are not made thread-safe in favour of performance.
 * Unsafe code regions are marked and could be synchronised.
 * The methods with non thread-safe regions could be made synchronised 
 * in a subclass.
 *     
 * @author Martin Gieseking, Tillman Weyde
 * @version $Revision: 8688 $, $Date: 2014-09-09 12:11:18 +0200 (Tue, 09 Sep 2014) $
 * 
 * 
 * Changes for Hibernating (by Alexander Kolomiyets) 
 * 
 * @hibernate.class table="Rational"

 */
public class Rational implements Comparable<Rational>, java.io.Serializable, Cloneable, IMPEGSerializable, IEquivalence {

    private static final long serialVersionUID = -62398304727661859L;
    private int numer;
    private int denom;
    private Long uid;
//	static double gset;
//	static boolean iset;
	/**
	 * constant: ln(10)
	 */
	static private final double ln10 = Math.log(10.0);
	static final double ln10_div_10 = ln10 / 10;
	static final double ln10_div_20 = ln10 / 20;
	static final double d10_div_ln10 = 10 / ln10;
	static final double d20_div_ln10 = 20 / ln10;
    public static final Rational MAX_VALUE = new Rational(Integer.MAX_VALUE);
    public static final Rational MIN_VALUE = new Rational(Integer.MIN_VALUE);
    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);
    public static final Rational HALF = new Rational(1,2);
    public static final Rational QUARTER = new Rational(1,4);

    /** Constructs a Rational with value 0. */
    public Rational() {
        numer = 0;
        denom = 1;
    }

    /**
     * Constructs a Rational n/1 out of an integer n.
     * 
     * @param n the given integer
     */
    public Rational(int n) {
        numer = n;
        denom = 1;
    }

    /**
     * Constructs a Rational n/d, not reduced.
     * 
     * @param n numerator
     * @param d denominator (must not be zero)
     * @throws IllegalArgumentException if denominator equals 0
     */
    public Rational(int n, int d) {
        if (d == 0)
            throw new IllegalArgumentException("denominator must not be zero");
        numer = n;
        denom = d;
    }

    /**
     * Creates a hash code that depends only on the value.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        if (numer == 0)
            return 0;
        // calculate a normalized representation
        // to make sure that equal numbers have
        // equal hashValues
        int n = numer, d = denom;
        // the sign is always stored in the numerator component
        if (d < 0) {
            n *= -1;
            d *= -1;
        }
        // now we can reduce this fractions
        int g = MyMath.gcd(Math.abs(n), d);
        n /= g;
        d /= g;
        // hash the two values together
        return n ^ d;
    }

    /**
     * Constructs a Rational from three values i + n/d.
     * 
     * @param i integer part
     * @param n numerator
     * @param d denominator (must not be zero)
     * @throws IllegalArgumentException if denominator equals 0
     */
    public Rational(int i, int n, int d) {
        if (d == 0)
            throw new IllegalArgumentException("denominator must not be zero");
        numer = i * d + n;
        denom = d;
        //		reduce();
    }

    /**
     * Constructs a Rational from long values. The fraction will be reduced if
     * possible and rounded if necessary.
     * 
     * @param n numerator
     * @param d denominator (must not be zero)
     * @throws IllegalArgumentException if denominator equals 0
     */
    public Rational(long n, long d) {
        if (d == 0)
            throw new IllegalArgumentException("denominator must not be zero");
        // reduce the fraction in longs if possible
        long g = MyMath.gcd(Math.abs(n), d);
        n /= g;
        d /= g;
        int int_vals[] = checkRange(n, d);
        numer = int_vals[0];
        denom = int_vals[1];
    }

    /**
     * Produces a clone object, that has the same value as this one.
     * @return A new Rational object representing the same value as this one.
     * 
     */
	public Rational getClone() {
            return new Rational(numer,denom);
    }
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    static final long maxInt = Integer.MAX_VALUE;
    static final long minInt = Integer.MAX_VALUE;

    /**
     * Returns <code>null</code> if the arguments are in the <code>int</code>
     * range and reduces the arguments by rounding if necessary.
     * 
     * @param n the numerator
     * @param d the denominator (music be positive)
     * @return The argument or (if necessary) rounded values.
     */
    static private int[] checkRange(long n, long d) {
        // Check is there an 'int' overflow.
        // If so try to reduce the fraction
        if (n > maxInt || n < minInt || d > maxInt) {
            long g = MyMath.gcd(Math.abs(n), d);
            if (g != 1) {
                n /= g;
                d /= g;
            }
        }
        // Check is there still an 'int' overflow.
        // If so calculate a factor to round.
        double factor = 1.0;
        int ret[] = new int[2];
        // +/-1 in the following is to compensate for
        // inaccuracies of double arithmetics and casting.
        if (n > maxInt) {
            factor = n / (double) (maxInt - 1);
        } else if (n < minInt) {
            factor = n / (double) (minInt + 1);
        }
        if (d > maxInt) {
            double f = d / (double) (maxInt - 1);
            if (f > factor)
                factor = f;
        }
        // Now round if necessary
        if (factor > 1.0) {
            ret[0] = (int) Math.round(n / factor);
            ret[1] = (int) Math.round(d / factor);
        } else {
            ret[0] = (int) n;
            ret[1] = (int) d;
        }
        return ret;
    }

    /**
     * Copy-constructor. Constructs a (reduced) Rational out of a given one.
     * 
     * @param r rational number to be copied
     */
    public Rational(Rational r) {
        numer = r.numer;
        denom = r.denom;
        //		reduce();
    }

    /**
     * Adds a given Rational to this one. The result of this method is this + r.
     * 
     * @param r addend to be added to this
     * @return the sum this + r
     */
    public Rational add(Rational r) {
    	if(r == null)
    		r = ZERO;
        return add(r.numer, r.denom);
    }

    /**
     * Adds a Rational given by numerator and denominator to this one. The
     * result of this method is this + r.
     * 
     * @param n the numerator of the fraction
     * @param d the denominator of the fraction
     * @return the sum this + r
     */
    public Rational add(int n, int d) {
        // specal case would cause long overflow
        if (d == Integer.MIN_VALUE && n == Integer.MIN_VALUE && numer == Integer.MIN_VALUE
            && denom == Integer.MIN_VALUE)
            return new Rational(2, 1);
        // First calculate numerator and denominator in 'long'
        long newNumer = (long) numer * d + (long) denom * n;
        long newDenom = (long) denom * d;
        return new Rational(newNumer, newDenom);
    }

    /**
     * Assigns a given integer to this one.
     * 
     * @param n integer to be assigned
     * @return the modified Rational this
     */
    public Rational assign(int n) {
        numer = n;
        denom = 1;
        return this;
    }

    /**
     * Assigns a new numerator and denominator.
     * 
     * @param n numerator
     * @param d denominator
     * @return the modified Rational this
     */
    public Rational assign(int n, int d) {
        numer = n;
        denom = d;
        return this;
    }

    /**
     * Assigns a given Rational to this one.
     * 
     * @param r rational value to be assigned
     * @return the modified Rational this
     */
    public Rational assign(Rational r) {
        numer = r.numer;
        denom = r.denom;
        return this;
    }

    /**
     * Returns the denominator of this Rational.
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * @hibernate.property
     * 
     *  
     */

    public int getDenom() {
        return denom;
    }

    /**
     * Returns the result of dividing this Rational by a given one in a new
     * Rational object.
     * 
     * @param r the divisor
     * @return the result this/r
     */
    public Rational div(Rational r) {
        return div(r.numer, r.denom);
    }

    /**
     * Returns the result of dividing this Rational by one given in two integers
     * in a new Rational object.
     */
    public Rational div(int n, int d) {
        return new Rational((long) numer * d, (long) denom * n);
    }

    public Rational div(int n) {
        return new Rational((long) numer, (long) denom * n);
    }

    public Rational mod(Rational r) {
        long num = (long) (numer * r.denom) % (long) (denom * r.numer);
        long den = (long) denom * r.denom;
        return new Rational(num, den);
    }

    public Rational mod(int n, int d) {
        long num =  ((long)numer * d) % ((long)denom * n);
        long den = (long) denom * d;
        return new Rational(num, den);
    }

    /**
     * This method returns true if this is integer multiple of r
     * 
     * @param r the divisor
     * @return the remainder of this.mod(r)
     * @see #modRemainder(de.uos.fmt.musitech.utility.Rational)
     */
    public boolean isMultiple(Rational r) {
        return this.mod(r).equals(new Rational(0));
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Rational o) {
        return compare((Rational) o);
    }

    /**
     * Returns -1 if this Rational is less than r, 0 if equal and 1 otherwise.
     */
    public int compare(Rational r) {
        return compare(r.numer, r.denom);
    }

    /**
     * Returns -1 if this Rational is less than the rational number defined by
     * <code>num</code> and <code>den</code>, 0 if equal and 1 otherwise.
     * 
     * @param num the numerator of the rational to be compared.
     * @param den the denominator of the rational to be compared.
     * @return -1,0,1 if this is less, equal or greater than num/den.
     */
    public int compare(int num, int den) {
        // this method is not thread-safe
        if (den == 0) {
            throw new IllegalArgumentException(this.getClass() + " den must not be 0.");
        }
        // no problem with overflow here
        long diffNumer = (long) this.numer * den - (long) this.denom * num;
        diffNumer *= MyMath.sign((long)den * this.denom);
        return MyMath.sign(diffNumer);
    }

    /**
     * Checks equality of this and a given pair of integers. This method is not
     * thread safe, because it may return wrong results, during a get or set
     * method.
     * 
     * @param n numerator
     * @param d denominator
     * @return true, if this == n/d
     */
    public boolean isEqual(int n, int d) {
        return compare(n, d) == 0;
    }

    /**
     * Checks mathematical equality of this and a given Rational. This method is
     * not thread safe, because it may return wrong results if called inparallel
     * with a get or set method.
     * 
     * @return true, if <i>this = r </i> (mathematically)
     */
    public boolean isEqual(Rational r) {
        return isEqual(r.numer, r.denom);
    }
    
    /**
     * Checks mathematical equality of this and a given Rational. This method is
     * not thread safe, because it may return wrong results if called inparallel
     * with a get or set method.
     * 
     * @return true, if <i>this = r </i> (mathematically)
     */
    public boolean isEqual(IEquivalence r) {
    	if(!(r instanceof Rational)) return false;
    	Rational ra = (Rational) r;
        return isEqual(ra.numer, ra.denom);
    }

    /**
     * Checks if <i>this > r </i> holds.
     * 
     * @return true, if <i>this > r </i>
     */
    public boolean isGreater(Rational r) {
        return compare(r) > 0;
    }

    /**
     * Checks if <i>this &gt;= r </i> holds.
     * 
     * @return true, if <i>this &gt;= r </i>
     */
    public boolean isGreaterOrEqual(Rational r) {
        return compare(r) >= 0;
    }

    /**
     * Checks if <i>this &gt;= n/d </i> holds.
     * 
     * @return true, if <i>this &gt;= n/d </i>
     */
    public boolean isGreaterOrEqual(int n, int d) {
        return compare(n, d) >= 0;
    }

    /**
     * Checks if <i>this &gt; n/d </i> holds.
     * 
     * @return true, if <i>this &gt; n/d </i>
     */
    public boolean isGreater(int n, int d) {
        return compare(n, d) > 0;
    }

    /** @return true, if <i>this &lt; r </i> */
    public boolean isLess(Rational r) {
        return compare(r) < 0;
    }

    /** @return true, if <i>this &lt; n/d </i> */
    public boolean isLess(int n, int d) {
        return compare(n, d) < 0;
    }

    /** @return true, if <i>this &lt; r </i> */
    public boolean isLessOrEqual(Rational r) {
        return compare(r) <= 0;
    }

    /**
     * Gets the maximum of this and a given Rational.
     * 
     * @param r a rational value
     * @return maximum of this and r, if r==null this is returned
     */
    public Rational max(Rational r) {
        if (r == null)
            return this;
        return isGreater(r) ? this : r;
    }

    /**
     * Gets the minimum of this and a given Rational.
     * 
     * @param r a rational value
     * @return minimum of this and r, if r==null this is returned
     */
    public Rational min(Rational r) {
        if (r == null)
            return this;
        return isLess(r) ? this : r;
    }

    /**
     * Multiplies this with a given Rational.
     * 
     * @param factor this is multiplied with
     * @return this * r
     */
    public Rational mul(Rational r) {
        return mul(r.numer, r.denom);
    }

    /**
     * Multiplies this with a faction give as two ints.
     * 
     * @param factor this is multiplied with
     * @return this * r
     */
    public Rational mul(int n, int d) {
        return new Rational((long) numer * n, (long) denom * d);
    }

    /**
     * Multiplies this with an int.
     * 
     * @param factor this is multiplied with
     * @return this * r
     */
    public Rational mul(int n) {
        return mul(n, 1);
    }

    /**
     * Returns the numerator of this Rational. 
     * @return The numerator of this rational.
     * 
     * @hibernate.property
     */
    public int getNumer() {
        return numer;
    }

    /** Returns numerator/denominator as floating point double. 
     * @return The value of this rational in double format. 
     */
    public double toDouble() {
        return (double) numer / (double) denom;
    }

    /**
     * Reduces this Rational as much as possible.
     */
    public final void reduce() {
        if (numer == 0)
            return;
        { // this block would have to be synchronized to be thread-safe.
            // the sign is always stored in the numerator component
            if (denom < 0) {
                numer *= -1;
                denom *= -1;
            }
            // now we can reduce this fraction
            int g = MyMath.gcd(Math.abs(numer), denom);
            numer /= g;
            denom /= g;
        }
    }

    /**
     * Extends this Rational by the given factor. The argument must not be 0, in
     * that case an IllegalArgumentException is thrown.
     * @param factor TODO
     * @return TODO
     */
    public Rational extend(int factor) {
        if (factor == 0) {
            throw new IllegalArgumentException(
                                               "Rational.extend(int factor): argument factor must not be 0.");
        }
        if (factor == 1) {
            return this;
        }
        { // this block would have to be synchronized to be thread-safe.
            long newNumer = this.numer;
            long newDenom = this.denom;
            // the sign is always stored in the numerator component
            if (factor < 0) {
                newNumer = -newNumer;
                factor = -factor;
            }
            // now we can extend the fraction
            newNumer *= factor;
            newDenom *= factor;
            return new Rational((int)newNumer, (int)newDenom);
        }
    }

    /**
     * Returns an integer representing the sign of this Rational.
     * 
     * @return -1 if this is negative, 1 if this is positive, 0 else.
     */
    public int sign() {
        // in case the sign is not stored the standard way.
        if (denom < 0)
            return numer > 0 ? -1 : numer < 0 ? 1 : 0;
        return numer < 0 ? -1 : numer > 0 ? 1 : 0;
    }

    /**
     * Subtracts a given Rational from this, returning a 
     * new Rational; this Rational remains unchanged.
     * 
     * @param r rational number that is subtracted from this
     * @return the result this - r
     */
    public Rational sub(Rational r) {
        // this line is not thread-safe
        return new Rational((long) numer * r.denom - denom * r.numer, (long) denom * r.denom);
    }

    /**
     * Subtracts a Rational given by numerator and denominator from this.
     * 
     * @param n numerator of the Rational that is subtracted from this
     * @param d denominator of the Rational that is subtracted from this
     * @return the result this - r
     */
    public Rational sub(int n, int d) {
        // this line is not thread-safe
        return new Rational((long) numer * d - (long) denom * n, (long) denom * d);
    }

    /**
     * Returns a string representation of the this Rational
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(numer);
        sb.append('/');
        sb.append(denom);
        return sb.toString();
    }

    /** Returns a string representation of the this Rational 
     * @return The formated String.  
     */
    public String toStringFormatted() {
        long wholes = numer / denom;
        long rest = numer % denom;
        StringBuffer sb = new StringBuffer();
        sb.append(wholes);
        sb.append(" + (");
        sb.append(rest);
        sb.append("/");
        sb.append(denom);
        sb.append(")");
        return sb.toString();
    }

    /**
     * Returns the (absolute) integer part of this Rational, i.e. the greatest
     * integer i with i <=r if r is positive or with(-i) <= (-r) if r is negative.
     * @return The floor value.
     */
    public int floor() {
        return Math.abs(numer) / denom;
    }

    /**
     * Returns the the (absolute) smallest integer not less than this Rational.
     * i.e. the smallest integer i with i <=r if r is positive or with
     * (-i) <= (-r) if r is negative.
     * @return The ceiling value.
     */
    public int ceil() {
        //	 this method is not thread-safe
        int ret = Math.abs(numer) / denom;
        if (numer % denom != 0)
            ret++;
        return ret;
    }

    /**
     * Checks for arithmetic, not representational, equality. Returns true if
     * the mathematical value of the <code>obj</code> argument is the same as
     * of <code>this</code>.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof Rational))
            return false;
        else
            return isEqual((Rational) obj);
    }

    /**
     * Sets the value.
     * 
     * @param newVal the new value.
     */
    public void setValue(Rational newVal) {
        setValue(newVal.numer, newVal.denom);
    }

    /**
     * Sets the value, performs no reduction.
     * 
     * @param num the new numerator.
     * @param den the new denominator.
     */
    public void setValue(int num, int den) {
        this.numer = num;
        this.denom = den;
    }
    
    

    /**
     * Sets the numerator without changing the denominator, performs no
     * reduction.
     * 
     * @param num the new numerator.
     */
    public void setNumer(int num) {
        this.numer = num;
    }

    /**
     * Sets the denominator without changing the numerator, i.e. changes the value and performs no
     * reduction.
     * 
     * @param argDenom the new denominator.
     */
    public void setDenom(int argDenom) {
        this.denom = argDenom;
    }

    /**
     * Extends the Rational so that the denominator has the given value and the
     * numerator is rounded to minimize the change of the Rational's value.
     * 
     * @param new_d The new denominator.
     */
    public void changeDenom(int new_d) {
        { // this block would have to be synchronized to be thread safe
            double extender = (double) new_d / denom;
            denom = new_d;
            numer = (int) Math.round(numer * extender);
//            reduce();
        }
    }

    /**
     * @return Returns the uid.
     * 
     * Changes for Hibernating (by Alexander Kolomiyets)
     * 
     * @hibernate.id generator-class="native"
     */
	private Long getUid() {
        return uid;
    }

    /**
     * @param uid The uid to set.
     */
    private void setUid(Long id) {
        this.uid = id;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        this.setNumer(Integer.parseInt(node.getAttribute("numerator")));
        this.setDenom(Integer.parseInt(node.getAttribute("denominator")));
        return this;
    }

    /**
     * Get the power of 2 which is less or equal to this rational by absolute value.
     * This includes the inversions of powers of 2 (1/2, 1/4, 1/8 ...) and 
     * the negatives of powers of two (-2,-1/2,-4,-1/4,...).
     * If this is 0, 0 is returned.  
     * 
     * @return the power of 2 which is less than or equal to this.
     */
    public Rational getFloorPower2() {
        if (this.equals(ZERO))
            return new Rational(0);
        Rational base;
        if (sign() > 0) {
            base = new Rational(1);
            // get the next larger power of 2, in cases of this greater than 1
            while (base.isLess(this)) {
                base = base.mul(2);
            }
            // look for a power of 2 not greater than dur
            while (base.isGreater(this)) {
                base = base.div(2);
            }
        } else {
            base = new Rational(-1);
            // get the next larger power of 2, in cases of this greater than 1
            while (base.isLess(this)) {
                base = base.div(2);
            }
            // look for a power of 2 not greater than dur
            while (base.isGreater(this)) {
                base = base.mul(2);
            }
        }
        return base;
    }

    /**
     * Get the power of 2 which is greater or equal to this rational by absolute value.
     * This includes the inversions of powers of 2 (1/2, 1/4, 1/8 ...) and 
     * the negatives of powers of two (-2,-1/2,-4,-1/4,...).
     * If this is 0, 0 is returned.  
     * 
     * @return the power of 2 which is greater than or equal to this.
     */
    public Rational getCeilPower2() {

        if (this.equals(ZERO))
            return new Rational(0);
        Rational base;
        if (sign() >= 0) {
            base = new Rational(1);
            // get the next smaller power of 2, in cases of values less than one.
            while (base.isGreater(this)) {
                base = base.div(2);
            }
            // look for a power of 2 not greater than dur
            while (base.isLess(this)) {
                base = base.mul(2);
            }
        } else {
            base = new Rational(-1);
            // get the next larger power of 2, in cases of duration greater tha one.
            while (base.isGreater(this)) {
                base = base.mul(2);
            }
            // look for a power of 2 not greater than dur
            while (base.isLess(this)) {
                base = base.div(2);
            }
        }
        // now base is the largest power of 2 less or equal than dur
        return base;
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


	/**
	 * Checks for state equivalence of objects.
	 * 
	 * @param o object to compare too.
	 * @return true if both objects have the same state, false if not.
	 */
	public boolean isEquivalent(IEquivalence o) {
		if(!(o instanceof Rational)) return false;
		return isEqual((Rational) o);
	}
	

}