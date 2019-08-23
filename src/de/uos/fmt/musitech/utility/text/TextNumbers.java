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
/*
 * File TextNumbers.java
 * Created on 13.09.2004
 */

package de.uos.fmt.musitech.utility.text;

/**
 * Text representations of numbers.
 * 
 * @author tweyde
 */
public class TextNumbers {

    /**
     * TODO comment
     *  
     */
    public TextNumbers() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Returns the name of this number's decimal representation in English. 
     * @param i The number to get the name for.
     * @return The name or null, if the name could not be  found (currently only the range -999 ... 999 is implemented)
     */
    public static String integralName(int i) {
        // TODO internationalize
        // TODO efficiency
        // TODO make if work for numbers with absolute > 1000.
        if(i>=1000 || i<= -1000){
            return null;
        }
        StringBuffer name = new StringBuffer(); 
        if(i < 0){
            name.append("minus ");
            i = -i;
        }
        if ( i < 1000 && i >= 100 ){
            name.append(singleName( i  / 100));
            name.append("hundred-and-");
            i%=100;
        }
        if ( i < 100 && i >= 20 ){
            name.append(tens( i  / 10));
            i%=10;
            
        }
        if (i < 20 && i>0 || name.length()==0) {
            name.append(singleName(i));
        }
        return name.toString();
    }

    private static String singleName(int i) {
        switch (i) {
        case 0:
            return "zero";
        case 1:
            return "one";
        case 2:
            return "two";
        case 3:
            return "three";
        case 4:
            return "four";
        case 5:
            return "five";
        case 6:
            return "six";
        case 7:
            return "seven";
        case 8:
            return "eight";
        case 9:
            return "nine";
        case 10:
            return "ten";
        case 11:
            return "eleven";
        case 12:
            return "twelve";
        case 13:
            return "thirteen";
        case 14:
            return "fourteen";
        case 15:
            return "fifteen";
        case 16:
            return "sixteen";
        case 17:
            return "seventeen";
        case 18:
            return "eighteen";
        case 19:
            return "nineteen";
        }
        return null;
    }

    private static String tens(int i) {
        switch (i) {
        case 1:
            return "ten";
        case 2:
            return "twenty";
        case 3:
            return "thirty";
        case 4:
            return "fourty";
        case 5:
            return "fifty";
        case 6:
            return "sixty";
        case 7:
            return "seventy";
        case 8:
            return "eighty";
        case 9:
            return "ninety";
        }
        return null;
    }

}