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
package de.uos.fmt.musitech.framework.time;

import java.lang.reflect.Method;

/**
 * A timer class using the sun.misc.Perf class which is available since JDK 1.4.2
 * @author tweyde
 */
public class SunClock implements MTimer {

// TODO Replace dircet calls by Reflection API to allow compilation under Java 1.4.1
//	static sun.misc.Perf perf ;
	static Object perf ;
	static long freq ;
	static long factor ;
	static Method freqMeth;
	static Method countMeth;

	static {
		try{
		Class perfClass = Class.forName("sun.misc.Perf");
		Method getPerfMeth = perfClass.getMethod("getPerf",new Class[]{}); 

		//	Get an instance of the Perf class:
//		perf = sun.misc.Perf.getPerf();
		perf = getPerfMeth.invoke(null,new Object[]{});
		//	(note that security applies!)

		//	get resolution of the counter in ticks/second:
		freqMeth = perfClass.getMethod("highResFrequency",new Class[]{}); 
		countMeth = perfClass.getMethod("highResCounter",new Class[]{}); 
//		freq = perf.highResFrequency();
		freq = ((Long)(freqMeth.invoke(perf,new Object[]{}))).longValue();
		factor = 1000000L / freq;
		}catch(Exception e){
			// TODO handle Exception
//		    e.printStackTrace();
		    throw new Error("Could not load SunClock");
		}
	}

	Object[] noArgs = new Object[]{};
	public long getTimeMicros() {
//		return perf.highResCounter() * 1000L / freq;
		try {
			long test = ((Long)countMeth.invoke(perf,noArgs)).longValue() * 1000000L / freq;
//			System.out.println("time: "+test);
			return test;
		} catch (Exception e) {
			// TODO handle Exception
			e.printStackTrace();
		}
		return 0;
	}


}
