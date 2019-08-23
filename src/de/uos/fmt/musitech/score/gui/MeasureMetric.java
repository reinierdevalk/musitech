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

import java.util.Vector;

import de.uos.fmt.musitech.score.util.PrimeFactors;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Die Beschreibung des Typs hier eingeben.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class MeasureMetric {
	private TimeSignature timeSignature;
	private Rational beatDistance;
	private Vector levels = new Vector();
	private int[] weights;

	public MeasureMetric(TimeSignature ts, Rational beatDistance) {
		this.timeSignature = ts;
		setBeatDistance(beatDistance);
	}

	/** Returns the n-th metric level where level 0 has the shortest pulse distance. */
	public MetricLevel metricLevel(int n) {
		return (MetricLevel) levels.get(n);
	}

	public void setBeatDistance(Rational beatDistance) {
		this.beatDistance = beatDistance;
		int numBeats = timeSignature.toRational().div(beatDistance).floor();
		PrimeFactors pfactors = new PrimeFactors(numBeats);
		boolean ternary = false;
		while (pfactors.size() > 0) {
			levels.add(new MetricLevel(beatDistance, ternary));
			ternary = false;
			int prime = 3;
			if (pfactors.max() > 3)
				prime = pfactors.max();
			else if (
				pfactors.max() == 3
					&& (pfactors.contains(2)
						|| timeSignature.getDenom() == beatDistance.getDenom()
						|| MyMath.isPowerOf2(beatDistance.getDenom())))
				ternary = (numBeats / 3 > 1) && (timeSignature.getDenom() == beatDistance.getDenom());
			else
				prime = 2;
			beatDistance = beatDistance.mul(prime, 1);
			numBeats /= prime;
			pfactors.remove(prime);
		}
		levels.add(new MetricLevel(timeSignature.toRational(), false));

		// compute weight of each beat	
		weights = new int[timeSignature.toRational().div(beatDistance).floor()];
		for (int i = 0; i < weights.length; i++)
			weights[i] = 0;
		for (Rational t = timeSignature.toRational().sub(beatDistance); t.isGreaterOrEqual(0, 1);	t = t.sub(beatDistance))
			for (int i = 0; i < levels.size(); i++)
				if (metricLevel(i).hitsTime(t))
					weights[t.div(beatDistance).floor()] += metricLevel(i).pulseWeight();
	}

	/** Returns the time signature of the current MeasureMetric. */
	public TimeSignature getTimeSignature() {
		return timeSignature;
	}

	/** Returns the number of beats with the shortest distance. This 
	 *  distance can be retrieved by getBeatDistance*/
	public int getNumBeats() {
		return weights.length;
	}

	/** Returns the distance of 2 successive beats in the minimum level. */
	public Rational getBeatDistance() {
		return beatDistance;
	}

	/** Returns the metric weight of the n-th beat. */
	public int weight(int n) {
		return weights[n];
	}

	/** Returns the weight of a given metric time. */
	public int weight(Rational time) {
		int beat = time.div(beatDistance).floor();
		return (beat < 0 || beat >= weights.length) ? 0 : weight(beat);
	}

	/** Same as weight(n) but symmetricWeight(0) = weight(0)-1 */
	public int symmetricWeight(int n) {
		return n == 0 ? weight(0) - 1 : weight(n);
	}

	/** Returns the position of the beat that is stronger than those on
	 * the given position. */
	int prevStrongerWeight(int pos) {
		for (int i = pos - 1; i >= 0; i--)
			if (weights[i] > weights[pos])
				return weights[i];
		return 0;
	}
}
