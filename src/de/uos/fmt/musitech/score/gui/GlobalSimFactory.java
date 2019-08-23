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
package de.uos.fmt.musitech.score.gui;

import de.uos.fmt.musitech.utility.math.Rational;

/** This class provides methods for extracting GlobalSims off 
 * a Measure or a System.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class GlobalSimFactory {
	public static GlobalSimSequence buildGlobalSims(SSystem system, int measureNo) {
		return buildGlobalSims(new GlobalMeasure(system, measureNo));
	}

	public static GlobalSimSequence buildGlobalSims(GlobalMeasure globalMeasure) {
		GlobalSimSequence result = new GlobalSimSequence();
		// indexes of currently affected LocalSims (all staves)
		int[] currentLSimInStaff = new int[globalMeasure.numStaves()];
//		for (int i = 0; i < currentLSimInStaff.length; i++)
//			currentLSimInStaff[i] = 0; // starting with the first LocalSim of each Staff

		boolean ready = false;
		boolean firstInMeasure = true;
		//  	   Rational attack = Rational.ZERO;
		Rational attack = globalMeasure.attackTime(); // start with this attack time
		while (!ready) {
			//GlobalSim globalSim = new GlobalSim(attack.isEqual(0,1));
			GlobalSim globalSim = new GlobalSim(firstInMeasure);
			firstInMeasure = false; // next sim is not the first one in this measure
			ready = true;
			Rational minAttack = Rational.MAX_VALUE;
			for (int i = 0; i < currentLSimInStaff.length; i++) {
				Measure measure = (Measure) globalMeasure.getMeasure(i);
				if (measure == null)
					break; // XXX darf das passieren ?
				LocalSim lsim = (LocalSim) measure.child(currentLSimInStaff[i]);
				if (lsim != null && lsim.getAlignmentAttack().isEqual(attack)) {
					globalSim.add(lsim);
					ready &= (++currentLSimInStaff[i] == measure.numChildren());
				}
				if (currentLSimInStaff[i] < measure.numChildren()) {
					Rational currAttack = ((LocalSim) measure.child(currentLSimInStaff[i])).getAlignmentAttack();
					minAttack = minAttack.min(currAttack);
				}
			}
			result.add(globalSim);
			attack = minAttack; //attack.add(globalSim.getAbsDuration());
		}
		for (int i = 0; i < currentLSimInStaff.length; i++) {
			//if there're leftovers, process them:
			for (int j = currentLSimInStaff[i]; j < globalMeasure.getMeasure(i).numChildren(); j++) {
				GlobalSim globalSim = new GlobalSim(false);
				globalSim.add((LocalSim)globalMeasure.getMeasure(i).child(j));
				result.add(globalSim);
			}
		}
		return result;
	}

	public static GlobalSimSequence buildGlobalSims(SSystem system) {
		GlobalSimSequence result = new GlobalSimSequence();
		int numMeasures = 0;
		for (int i = 0; i < system.numChildren(); i++) {
			Staff staff = (Staff) system.child(i);
			numMeasures = Math.max(numMeasures, staff.numChildren());
		}
		for (int i = 0; i < numMeasures; i++)
			result.append(buildGlobalSims(system, i));
		return result;
	}
}
