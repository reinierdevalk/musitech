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
package de.uos.fmt.musitech.score.mpegsmr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.DataConversionException;
import org.jdom.Element;

import de.uos.fmt.musitech.data.structure.Note;

/**
 * @author Sascha Wegener
 * 
 * @version 02.02.2008
 */
public class AddressMapImport implements Elements, Attributes {

	private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Note>>>>> measure_layer_figure_chordOrBeam_chordInBeam;

	public AddressMapImport() {
		measure_layer_figure_chordOrBeam_chordInBeam = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Note>>>>>();
	}

	public void put(int measure, int layer, int figure, int chordOrBeam,
			int chordInBeam, Note n) {
		if (!measure_layer_figure_chordOrBeam_chordInBeam.containsKey(measure))
			measure_layer_figure_chordOrBeam_chordInBeam
					.put(
							measure,
							new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Note>>>>());
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure)
				.containsKey(layer))
			measure_layer_figure_chordOrBeam_chordInBeam.get(measure).put(
					layer,
					new HashMap<Integer, Map<Integer, Map<Integer, Note>>>());
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).containsKey(figure))
			measure_layer_figure_chordOrBeam_chordInBeam.get(measure)
					.get(layer).put(figure,
							new HashMap<Integer, Map<Integer, Note>>());
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).get(figure).containsKey(chordOrBeam))
			measure_layer_figure_chordOrBeam_chordInBeam.get(measure)
					.get(layer).get(figure).put(chordOrBeam,
							new HashMap<Integer, Note>());
		measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(layer)
				.get(figure).get(chordOrBeam).put(chordInBeam, n);
	}

	public Note getNote(Element a) {
		try {
			int measure = a.getAttribute(ADDRESS_MEASURE).getIntValue();
			int layer = a.getAttribute(ADDRESS_LAYER).getIntValue();
			int figure = a.getAttribute(ADDRESS_FIGURE).getIntValue();
			int chordOrBeam = a.getAttribute(ADDRESS_CHORD_OR_BEAM)
					.getIntValue();
			int chordInBeam = a.getAttribute(ADDRESS_CHORD_IN_BEAM)
					.getIntValue();
			return getNote(measure, layer, figure, chordOrBeam, chordInBeam);
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Note getNote(int measure, int layer, int figure, int chordOrBeam,
			int chordInBeam) {
		if (!measure_layer_figure_chordOrBeam_chordInBeam.containsKey(measure)) {
			System.out.printf(
					"AdressMap: No such measure (%2d, %2d, %2d, %2d, %2d)%n",
					measure, layer, figure, chordOrBeam, chordInBeam);
			return null;
		}
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure)
				.containsKey(layer)) {
			System.out.printf(
					"AdressMap: No such layer (%2d, %2d, %2d, %2d, %2d)%n",
					measure, layer, figure, chordOrBeam, chordInBeam);
			return null;
		}
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).containsKey(figure)) {
			System.out.printf(
					"AdressMap: No such figure (%2d, %2d, %2d, %2d, %2d)%n",
					measure, layer, figure, chordOrBeam, chordInBeam);
			return null;
		}
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).get(figure).containsKey(chordOrBeam)) {
			System.out
					.printf(
							"AdressMap: No such chordOrBeam (%2d, %2d, %2d, %2d, %2d)%n",
							measure, layer, figure, chordOrBeam, chordInBeam);
			return null;
		}
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).get(figure).get(chordOrBeam).containsKey(chordInBeam)) {
			System.out
					.printf(
							"AdressMap: No such chordInBeam (%2d, %2d, %2d, %2d, %2d)%n",
							measure, layer, figure, chordOrBeam, chordInBeam);
			return null;
		}
		return measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).get(figure).get(chordOrBeam).get(chordInBeam);
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Integer measure : measure_layer_figure_chordOrBeam_chordInBeam
				.keySet())
			for (Integer layer : measure_layer_figure_chordOrBeam_chordInBeam
					.get(measure).keySet())
				for (Integer figure : measure_layer_figure_chordOrBeam_chordInBeam
						.get(measure).get(layer).keySet())
					for (Integer chordOrBeam : measure_layer_figure_chordOrBeam_chordInBeam
							.get(measure).get(layer).get(figure).keySet())
						for (Integer chordInBeam : measure_layer_figure_chordOrBeam_chordInBeam
								.get(measure).get(layer).get(figure).get(
										chordOrBeam).keySet()) {
							Note note = measure_layer_figure_chordOrBeam_chordInBeam
									.get(measure).get(layer).get(figure).get(
											chordOrBeam).get(chordInBeam);
							pw.printf("%2d, %2d, %2d, %2d, %2d: %s %n",
									measure, layer, figure, chordOrBeam,
									chordInBeam, note);
						}
		pw.close();
		return sw.toString();
	}

	public List<Note> getNotes(int measure) {
		List<Note> notes = new ArrayList<Note>();
		if (!measure_layer_figure_chordOrBeam_chordInBeam.containsKey(measure)) {
			System.out.printf("Measure %2d not found%n", measure);
			return notes;
		}
		for (Integer layer : measure_layer_figure_chordOrBeam_chordInBeam.get(
				measure).keySet())
			notes.addAll(getNotes(measure, layer));
		return notes;
	}

	public List<Note> getNotes(int measure, int layer) {
		List<Note> notes = new ArrayList<Note>();
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure)
				.containsKey(layer)) {
			System.out.printf("Layer %2d not found (Measure: %2d)%n", layer,
					measure);
			return notes;
		}
		for (Integer figure : measure_layer_figure_chordOrBeam_chordInBeam.get(
				measure).get(layer).keySet())
			notes.addAll(getNotes(measure, layer, figure));
		return notes;
	}

	public List<Note> getNotes(int measure, int layer, int figure) {
		List<Note> notes = new ArrayList<Note>();
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).containsKey(figure)) {
			System.out.printf(
					"Figure %2d not found (Measure: %2d, Layer: %2d)%n",
					figure, measure, layer);
			return notes;
		}
		for (Integer chordOrBeam : measure_layer_figure_chordOrBeam_chordInBeam
				.get(measure).get(layer).get(figure).keySet())
			notes.addAll(getNotes(measure, layer, figure, chordOrBeam));
		return notes;
	}

	public List<Note> getNotes(int measure, int layer, int figure,
			int chordOrBeam) {
		List<Note> notes = new ArrayList<Note>();
		if (!measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
				layer).get(figure).containsKey(chordOrBeam)) {
			System.out
					.printf(
							"ChordOrBeam %2d not found (Measure: %2d, Layer: %2d, Figure: %2d)%n",
							chordOrBeam, measure, layer, figure);
			return notes;
		}
		for (Integer chordInBeam : measure_layer_figure_chordOrBeam_chordInBeam
				.get(measure).get(layer).get(figure).get(chordOrBeam).keySet()) {
			if (measure_layer_figure_chordOrBeam_chordInBeam.get(measure).get(
					layer).get(figure).get(chordOrBeam)
					.containsKey(chordInBeam))
				notes.add(measure_layer_figure_chordOrBeam_chordInBeam.get(
						measure).get(layer).get(figure).get(chordOrBeam).get(
						chordInBeam));
			else
				System.out
						.printf(
								"ChordInBeam %2d not found (Measure: %2d, Layer: %2d, Figure: %2d, ChordOrBeam: %2d)%n",
								chordInBeam, measure, layer, figure,
								chordOrBeam);
		}
		return notes;
	}
}
