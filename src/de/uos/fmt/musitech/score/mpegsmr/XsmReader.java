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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Selection;

/**
 * @author Sascha Wegener
 * @version 31.01.2008
 */
public class XsmReader implements Elements, Attributes {

	/**
	 * Creates a piece from MPEG SMR data, organised in a directory of with the
	 * XSM file needs to be loaded.
	 * 
	 * @param xsmRoot The xsm file of the MPG SMR directory.
	 * @return The created Piece object, filled with a NotationSystem.
	 */
	public static Piece readXSM(String xsmRoot) {
		return readXSM(new File(xsmRoot));
	}
	
	/**
	 * Creates a piece from MPEG SMR data, organised in a directory of with the
	 * XSM file needs to be loaded.
	 * 
	 * @param xsmRoot The xsm file of the MPG SMR directory.
	 * @return The created Piece object, filled with a NotationSystem.
	 */
	public static Piece readXSM(File xsmRoot) {
		XsmReader reader = new XsmReader(xsmRoot);
		return reader.getPiece();
	}

	private List<String> partNames;
	private Piece piece;
	private File xsmFile;
	private Map<Integer, AddressMapImport> addressMapMap;

	private XsmReader(File argXsmFile) {
		this.xsmFile = argXsmFile;
		addressMapMap = new HashMap<Integer, AddressMapImport>();
		readIndex();
		createPiece();
		readParts();
		readSelections();
		piece.getScore().processDynamics();
	}

	/**
	 * Returns the Piece object created from the MPEG SMR data.
	 * 
	 * @return The Piece object.
	 */
	public Piece getPiece() {
		return piece;
	}

	@SuppressWarnings("unchecked")
	private void readSelections() {
		try {
			File main = new File(xsmFile, xsmFile.getName().substring(0,
				xsmFile.getName().indexOf('.'))
											+ ".xml");
			Document doc = new SAXBuilder().build(main);
			List<Element> selections = doc.getRootElement().getChildren(
				SMXF_MAIN_SELECTION);
			for (Element selection : selections) {
				Selection sc = new Selection(piece.getContext());
				List<Element> extaddr = selection
						.getChildren(SMXF_MAIN_EXTADDRESS);
				for (Element eat : extaddr) {
					int part = eat.getAttribute(SMXF_MAIN_EXTADDRESS_PART)
							.getIntValue();
					int measure = eat.getAttribute(ADDRESS_MEASURE)
							.getIntValue();
					int layer = eat.getAttribute(ADDRESS_LAYER) != null ? eat
							.getAttribute(ADDRESS_LAYER).getIntValue() : 0;
					int figure = eat.getAttribute(ADDRESS_LAYER) != null ? eat
							.getAttribute(ADDRESS_LAYER).getIntValue() : 0;
					int chordOrBeam = eat.getAttribute(ADDRESS_CHORD_OR_BEAM) != null	? eat
																								.getAttribute(
																									ADDRESS_CHORD_OR_BEAM)
																								.getIntValue()
																						: 0;
					int chordInBeam = eat.getAttribute(ADDRESS_CHORD_IN_BEAM) != null	? eat
																								.getAttribute(
																									ADDRESS_CHORD_IN_BEAM)
																								.getIntValue()
																						: 0;
					if (addressMapMap.containsKey(part)) {
						AddressMapImport map = addressMapMap.get(part);
						if (chordInBeam > 0) {
							Note n = map.getNote(measure, layer, figure,
								chordOrBeam, chordInBeam);
							if (n != null)
								sc.add(n);
						} else if (chordOrBeam > 0) {
							sc.addAll(map.getNotes(measure, layer, figure,
								chordOrBeam));
						} else if (figure > 0) {
							sc.addAll(map.getNotes(measure, layer, figure));
						} else if (layer > 0) {
							sc.addAll(map.getNotes(measure, layer));
						} else if (measure > 0) {
							sc.addAll(map.getNotes(measure));
						} else if (part > 0) {
							sc.addAll(map.getNotes(measure, layer, figure,
								chordOrBeam));
						}
					}

				}
				if (selection.getChild(SMXF_MAIN_ANNOTATION) != null) {
					Element a = selection.getChild(SMXF_MAIN_ANNOTATION);
					List<Element> shortTexts = a
							.getChildren(SMXF_MAIN_SHORTTEXT);
					List<Element> texts = a.getChildren(SMXF_MAIN_TEXT);
					List<Element> urls = a.getChildren(SMXF_MAIN_URL);
					for (Element st : shortTexts)
						sc.getAnnotations().add(st.getText());
					for (Element t : texts)
						sc.getAnnotations().add(t.getText());
					for (Element u : urls)
						sc.getAnnotations().add(new URL(u.getText()));
				}
				piece.getSelectionPool().add(sc);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JDOMException ex) {
			ex.printStackTrace();
		}
	}

	private void readParts() {
		for (String partName : partNames) {
			File part = new File(xsmFile, partName);
			if (part.exists()) {
				try {
					Document doc = new SAXBuilder().build(part);
					PartImporter i = new PartImporter(doc);
					i.importIntoPiece(piece);
					Integer partIndex = new Integer(partName.substring(0,
						partName.indexOf('.')));
					addressMapMap.put(partIndex, i.getAddressMap());
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (JDOMException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void createPiece() {
		piece = new Piece();
		NotationSystem score = new NotationSystem(piece.getContext());
		piece.setScore(score);
	}

	@SuppressWarnings("unchecked")
	private boolean readIndex() {
		try {
			partNames = new ArrayList<String>();
			String xsmPrefix = xsmFile.getName().substring(0,
				xsmFile.getName().lastIndexOf('.'));
			String wdfName = xsmPrefix + ".xsm";
			File wdfFile = new File(xsmFile, wdfName);
			Document doc = new SAXBuilder().build(wdfFile);
			Element wdfheader = doc.getRootElement();
			List<Element> symbolic = wdfheader.getChildren("Symbolic");
			for (Element st : symbolic) {
				List<Element> items = st.getChildren("Wdfitem");
				for (Element i : items) {
					Element filename = i.getChild("Filename");
					if (filename != null
						&& !filename.getText().startsWith(xsmPrefix))
						partNames.add(filename.getText());
				}
			}
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JDOMException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
