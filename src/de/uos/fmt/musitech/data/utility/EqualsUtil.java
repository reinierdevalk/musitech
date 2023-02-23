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
package de.uos.fmt.musitech.data.utility;

/**
* Collected methods which allow easy implementation of <code>equals</code>.
*
* Example use case in a class called Car:
* <pre>
public boolean equals(Object aThat){
  if ( this == aThat ) return true;
  if ( !(aThat instanceof Car) ) return false;
  Car that = (Car)aThat;
  return
    EqualsUtil.areEqual(this.fName, that.fName) &&
    EqualsUtil.areEqual(this.fNumDoors, that.fNumDoors) &&
    EqualsUtil.areEqual(this.fGasMileage, that.fGasMileage) &&
    EqualsUtil.areEqual(this.fColor, that.fColor) &&
    Arrays.equals(this.fMaintenanceChecks, that.fMaintenanceChecks); //array!
}
* </pre>
*
* <em>Arrays are not handled by this class</em>.
* This is because the <code>Arrays.equals</code> methods should be used for
* array fields.
*/
public final class EqualsUtil {

  static public boolean areEqual(boolean aThis, boolean aThat){
    //System.out.println("boolean");
    return aThis == aThat;
  }

  static public boolean areEqual(char aThis, char aThat){
    //System.out.println("char");
    return aThis == aThat;
  }

  static public boolean areEqual(long aThis, long aThat){
    /*
    * Implementation Note
    * Note that byte, short, and int are handled by this method, through
    * implicit conversion.
    */
    //System.out.println("long");
    return aThis == aThat;
  }

  static public boolean areEqual(float aThis, float aThat){
    //System.out.println("float");
    return Float.floatToIntBits(aThis) == Float.floatToIntBits(aThat);
  }

  static public boolean areEqual(double aThis, double aThat){
    //System.out.println("double");
    return Double.doubleToLongBits(aThis) == Double.doubleToLongBits(aThat);
  }

  /**
  * Possibly-null object field.
  *
  * Includes type-safe enumerations and collections, but does not include
  * arrays. See class comment.
  */
  static public boolean areEqual(IEquivalence aThis, IEquivalence aThat){
    //System.out.println("IEquality");
    return aThis == null ? aThat == null : aThis.isEquivalent(aThat);
  }
  
  /**
   * Possibly-null object field.
   *
   * Includes type-safe enumerations and collections, but does not include
   * arrays. See class comment.
   */
   static public boolean areEqual(Object aThis, Object aThat){
     //System.out.println("Object");
     return aThis == null ? aThat == null : aThis.equals(aThat);
   }
}

