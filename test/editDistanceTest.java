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
 * File editDistanceTest.java
 * Created on 22.04.2005
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;

import de.uos.fmt.musitech.utility.math.MyMath;

/**
 * TODO add class coment
 * 
 * @author tweyde
 */
public class editDistanceTest extends TestCase {

	//    /**
	//     * TODO add comment
	//     * @param start
	//     * @param maxDist
	//     * @return
	//     */
	//    public static List dEnvironment(String start, int maxDist){
	//        List results = new ArrayList();
	//        List d0 = new ArrayList();
	//        d0.add(start);
	//        results.add(d0);
	//        for (int i=1; i<=maxDist; i++){
	//            List startList = (List)results.get(i-1);
	//            List localResults = new ArrayList();
	//            for (Iterator iter = startList.iterator(); iter.hasNext();) {
	//                localResults.addAll(editList((String) iter.next()));
	//            }
	//            results.add(localResults);
	//        }
	//        List totalList = new ArrayList();
	//        for (Iterator iter = results.iterator(); iter.hasNext();) {
	//            totalList.addAll((List)iter.next());
	//        }
	//        return totalList;
	//    }
	//
	//    /**
	//     * TODO add comment
	//     * @param start
	//     * @param maxDist
	//     * @return
	//     */
	//    public static List dEnvironment(String start, int maxDist, int maxLen){
	//        List results = new ArrayList();
	//        List d0 = new ArrayList();
	//        d0.add(start);
	//        results.add(d0);
	//        for (int i=1; i<=maxDist; i++){
	//            List startList = (List)results.get(i-1);
	//            List localResults = new ArrayList();
	//            for (Iterator iter = startList.iterator(); iter.hasNext();) {
	//                localResults.addAll(editDistLim((String) iter.next(), maxLen));
	//            }
	//            results.add(localResults);
	//        }
	//        List totalList = new ArrayList();
	//        for (Iterator iter = results.iterator(); iter.hasNext();) {
	//            totalList.addAll((List)iter.next());
	//        }
	//        return totalList;
	//    }

	/**
	 * TODO add comment
	 * 
	 * @param start
	 * @param maxDist
	 * @return
	 */
	public static Collection dEnvironmentUnique(String start, int maxDist,
			int maxLen, boolean unique) {
		List results = new ArrayList();
		List d0 = new ArrayList();
		d0.add(start);
		results.add(d0);
		for (int i = 1; i <= maxDist; i++) {
			Collection startList = (Collection) results.get(i - 1);
			Collection localResults;
			if (unique)
				localResults = new HashSet();
			else
				localResults = new ArrayList();
			for (Iterator iter = startList.iterator(); iter.hasNext();) {
				localResults.addAll(editDistLim((String) iter.next(),
						Integer.MAX_VALUE));
			}
			filterLen(localResults, maxLen);
			results.add(localResults);
		}
		Collection totalResults;
		if (unique)
			totalResults = new HashSet();
		else
			totalResults = new ArrayList();
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			totalResults.addAll((Collection) iter.next());
		}
		filterLen(totalResults, maxLen);
		return totalResults;
	}

	/**
	 * TODO add comment
	 * 
	 * @param start
	 * @param maxDist
	 * @param maxLen
	 * @param unique
	 * @return
	 */
	public static Collection dEnvironmentUnique2(String start, int maxDist,
			int maxLen, boolean unique) {
		List results = new ArrayList();
		List d0 = new ArrayList();
		d0.add(start);
		results.add(d0);
		for (int i = 1; i <= maxDist; i++) {
			Collection startList = (Collection) results.get(i - 1);
			List localResults = new ArrayList();
			for (Iterator iter = startList.iterator(); iter.hasNext();) {
				localResults.addAll(editList((String) iter.next()));
			}
			if (unique)
				removeDoubles(localResults);
			results.add(localResults);
		}
		List totalResults = new ArrayList();
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			totalResults.addAll((Collection) iter.next());
		}
		filterLen(totalResults, maxLen);
		if (unique)
			removeDoubles(totalResults);
		return totalResults;
	}

	public static int filterLen(Collection col, int maxLen) {
		int count = 0;
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			String str = iter.next().toString();
			if (str.length() > maxLen) {
				iter.remove();
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param start
	 * @return
	 *  
	 */
	static public List editList(String start) {
		List list = new ArrayList();
		int i;
		// iterate over start
		for (i = 0; i < start.length(); i++) {
			// insertions
			for (int k = 1; k < 10; k++) {
				StringBuffer newBuffer = new StringBuffer(start);
				newBuffer.insert(i, k);
				list.add(newBuffer.toString());
			}
			// deletion
			StringBuffer newBuffer = new StringBuffer(start);
			newBuffer.deleteCharAt(i);
			list.add(newBuffer.toString());

		}
		// insertions
		for (int k = 1; k < 10; k++) {
			StringBuffer newBuffer = new StringBuffer(start);
			newBuffer.insert(i, k);
			list.add(newBuffer.toString());
		}
		return list;
	}

	/**
	 * @param start
	 * @return
	 */
	static public List editDistLim(String start, int max_len) {
		List list = new ArrayList();
		int i;
		// iterate over start
		for (i = 0; i < start.length(); i++) {
			// insertions
			for (int k = 1; k < 10; k++) {
				StringBuffer newBuffer = new StringBuffer(start);
				newBuffer.insert(i, k);
				if (newBuffer.length() <= max_len) {
					list.add(newBuffer.toString());
				}
			}
			// deletion
			StringBuffer newBuffer = new StringBuffer(start);
			newBuffer.deleteCharAt(i);
			if (newBuffer.length() <= max_len) {
				list.add(newBuffer.toString());
			}
		}
		// insertions
		for (int k = 1; k < 10; k++) {
			StringBuffer newBuffer = new StringBuffer(start);
			newBuffer.insert(i, k);
			if (newBuffer.length() <= max_len) {
				list.add(newBuffer.toString());
			}
		}
		return list;
	}

	public static int removeDoubles(List list) {
		Collections.sort(list);
		int count = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			Object element = list.get(i);
			Object element2 = list.get(i + 1);
			if (element.equals(element2)) {
				list.remove(i + 1);
				i--;
			}
		}
		return count;
	}

	//    public static int removeDoubles(List list){
	//        int count=0;
	//        for (int i=0; i < list.size(); i++) {
	//            Object element = list.get(i);
	//            for (int j=i+1; j<list.size(); j++) {
	//                Object element2 = list.get(j);
	//                if(element.equals(element2)){
	//                    list.remove(j);
	//                    count ++;
	//                }
	//            }
	//        }
	//        return count;
	//    }

	public static int calcEditNum(int n) {
		return 19 + 10 * (n - 1);
	}

	public static int calcEditNumAccum(int n, int d_max) {
		return 0;
	}

	public static int calcFormula2(int n, int n_max, int d) {
		int sum = 0;
		for (int k = (int) Math.ceil((n_max - n + d) / 2.0); k < d; k++) {
			sum += 9 ^ k * MyMath.binCoeff(d, k);
		}
		return sum;
	}

	public static int calcFormula3(int n_max, int d) {
		int sum = 0;
		for (int n = 0; n < n_max; n++) {
			sum += 19 * (9 ^ (d - 1)) * MyMath.binCoeff(n, d)
					- calcFormula2(n, n_max, d);
		}
		return sum;
	}

	public void testEditNum() {
		int maxLen = 3;
		while (++maxLen < 7) {
			System.out.println();
			for (int i = 0; i <= maxLen; i++) {
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < i; j++) {
					sb.append(i);
				}
				String start = sb.toString();
				System.out.println("string length: " + start.length());
				//            List list = editList(start);
				//            System.out.println("test result: " + list.size());
				//            int edNum = calcEditNum(start.length());
				//            System.out.println("formula result: " + edNum );
				//            assertEquals(list.size(),edNum);
				for (int d = 1; d < 6; d++) {
					//                if(d<3){
					//                    Collection envList =
					// dEnvironmentUnique(start,d,Integer.MAX_VALUE,false);
					//                    System.out.println("accumulated d="+ d +", no max len,
					// non-unique : " +
					// envList.size());
					//                }
					//                if(d<4){
					//                    Collection envList = dEnvironmentUnique(start,d,6,false);
					//                    System.out.println("accumulated d="+ d +", max len 6,
					// non-unique " +
					// envList.size());
					//                }
					{
						Collection envList = dEnvironmentUnique(start, d,
								maxLen, true);
						System.out.println("accumulated d=" + d + ", max len "
								+ maxLen + ", unique : " + envList.size());
					}
					//                {
					//                    Collection envList = dEnvironmentUnique2(start,d,6,true);
					//                    System.out.println("accumult. 2 d="+ d +", max len 6,
					// unique : " +
					// envList.size());
					//                }
				}
			}

		}

	}

	int len(int contour) {
		int i = 0;
		while (i < 8) {
			if ((0x0F & contour) == 0) {
				break;
			} else {
				i++;
			}
		}
		return i;
	}

	int insert(int contour, int trans, int pos) {
		int newCont = contour << 4;
		trans <<= 4 * pos;
		trans &= 0x0F << 4 * pos;
		int hiMask = 0;
		for (int i = 0; i < pos; i++) {
			hiMask |= 0x0F << 4 * i;
		}
		contour &= hiMask;
		hiMask |= 0x0F << 4 * pos;
		int loMask = -1 ^ hiMask;
		newCont &= loMask;
		newCont |= trans;
		newCont |= contour;
		return newCont;
	}

	int delete(int contour, int pos) {
		int newCont = contour >> 4;
		int hiMask = 0;
		for (int i = 0; i < pos; i++) {
			hiMask |= 0x0F << 4 * i;
		}
		contour &= hiMask;
		int loMask = -1 ^ hiMask;
		newCont &= loMask;
		newCont |= contour;
		return newCont;
	}

}
