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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Selection;
import de.uos.fmt.musitech.data.structure.container.Selection.SelectionType;

/**
 * @author Sascha Wegener
 * @version 03.02.2008
 */
public class XsmWriter implements Elements, Attributes {

	/**
	 * Writes the piece into a temporary directory
	 * 
	 * @param p1 The piece to store.
	 * @return The xsm file in the new directory with the smr contents.
	 */
	public static File writeTmpSMR(Piece p1) {
		String tmpDirString = System.getProperty("java.io.tmpdir");
		if (tmpDirString == null) {
			assert false;
			throw new RuntimeException(
				"System property java.io.tmpdir is not set.");
		}
		File tmpDir = new File(tmpDirString);
		File smrDir = new File(tmpDir, "smr-0.xsm");
		for (int i = 1; smrDir.exists(); i++) {
			smrDir = new File(tmpDir, "smr-" + Integer.toString(i) + ".xsm");
		}
		// TODO make sure the temp files are deleted at exit
		XsmWriter.writeXSM(smrDir, p1);
		File smrFile = new File(smrDir,smrDir.getName());
		return smrFile;
	}

	public static void writeXSM(File xsmFile, Piece p) {
		XsmWriter xsm = new XsmWriter(xsmFile);
		xsm.export(p);
	}

	private File xsmFile;
	private String xsmBaseName;
	private String[] partNames;
	private Piece p;
	private AddressMapExport[] addressMaps;

	private XsmWriter(File xsmFile) {
		this.xsmFile = xsmFile;
		int endBaseName = xsmFile.getName().lastIndexOf('.');
		if (endBaseName < 0) {
			endBaseName = xsmFile.getName().length() - 1;
		}
		this.xsmBaseName = xsmFile.getName().substring(0, endBaseName);
	}

	public boolean export(Piece p) {
		try {
			this.p = p;
			xsmFile.mkdir();
			partNames = new String[p.getScore().size()];
			addressMaps = new AddressMapExport[p.getScore().size()];
			for (int i = 0; i < partNames.length; i++) {
				partNames[i] = (i + 1) + "." + xsmBaseName + ".xml";
				PartExporter e = new PartExporter(
					new NotationStaff[] {((NotationStaff) p.getScore().get(i))});
				Document doc = e.export();
				addressMaps[i] = e.getAddressMap();
				try {
					XMLOutputter out = new XMLOutputter(Format
							.getPrettyFormat());
					out.output(doc, new FileOutputStream(new File(xsmFile,
						partNames[i])));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			writeIndexFile();
			writeMainScore();
			writeWdf();
			return true;
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void writeWdf() {
		Element wdf = new Element(WDFHEADER);
		wdf.setAttribute("WDF_CONDITION", "FULL");
		Document wdfDoc = new Document(wdf);
		Element symbolic = new Element(WDF_SYMBOLIC);
		wdf.addContent(symbolic);
		Element item = new Element(WDFITEM);
		Element filename = new Element(WDF_FILENAME);
		item.addContent(filename);
		filename.setText(xsmBaseName + ".xml");
		symbolic.addContent(item);
		for (int i = 0; i < partNames.length; i++) {
			item = new Element(WDFITEM);
			filename = new Element(WDF_FILENAME);
			item.addContent(filename);
			filename.setText(partNames[i]);
			symbolic.addContent(item);
		}
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(wdfDoc, new FileOutputStream(new File(xsmFile, xsmFile
					.getName())));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void writeMainScore() throws DataConversionException {
		Element main = new Element(SMXF_MAIN);
		Document mainDoc = new Document(main);
		for (int i = 0; i < partNames.length; i++) {
			Element part = new Element(SMXF_MAIN_PARTREF);
			part.setAttribute("SCOREID", Integer.toString(i + 1));
			main.addContent(part);
		}
		if (p.getSelectionPool() != null) {
			int id = 1;
			Element selections = new Element("selections");
			main.addContent(selections);
			for (Iterator<?> i = p.getSelectionPool().iterator(); i.hasNext();) {
				Containable c = (Containable) i.next();
				if (c instanceof Selection) {
					Selection s = (Selection) c;
					Element se = new Element(SMXF_MAIN_SELECTION);
					se.addContent(new Element("annotation"));
					if (s.getAnnotations() != null
						&& s.getAnnotations().size() > 0) {
						Element an = new Element(SMXF_MAIN_ANNOTATION);
						for (Object o : s.getAnnotations()) {
							if (o instanceof URL) {
								Element url = new Element(SMXF_MAIN_URL);
								url.setText(o.toString());
								an.addContent(url);
							} else {
								Element st = new Element(SMXF_MAIN_SHORTTEXT);
								st.setText(o.toString());
								an.addContent(st);
							}
						}
						se.addContent(an);
					}
					if (s.getType() == SelectionType.NOTE) {
						for (Iterator<?> it = s.iterator(); it.hasNext();) {
							Note n = (Note) it.next();
							for (int j = 0; j < addressMaps.length; j++) {
								if (addressMaps[j].contains(n))
									se.addContent(addressMaps[j].getExtAddress(
										n, j + 1));
							}
						}
					} else if (s.getType() == SelectionType.CHORD) {
						for (Iterator<?> it = s.iterator(); it.hasNext();) {
							Note n = (Note) it.next();
							for (int j = 0; j < addressMaps.length; j++) {
								if (addressMaps[j].contains(n)) {
									Element extAddress = addressMaps[j]
											.getExtAddress(n, j + 1);
									Containable[] content = p.getScore()
											.getContentsRecursive();
									NotationChord nc = null;
									for (int k = 0; k < content.length
													&& nc == null; k++)
										if (content[k] instanceof NotationChord
											&& ((NotationChord) content[k])
													.contains(n))
											nc = (NotationChord) content[k];
									if (nc != null && nc.size() > 0) {
										if (extAddress.getAttribute(
											ADDRESS_CHORD_IN_BEAM)
												.getIntValue() > 0)
											extAddress
													.removeAttribute(ADDRESS_CHORD_IN_BEAM);
										else if (extAddress.getAttribute(
											ADDRESS_CHORD_OR_BEAM)
												.getIntValue() > 0)
											extAddress
													.removeAttribute(ADDRESS_CHORD_OR_BEAM);
										se.addContent(extAddress);
									}
								}
							}
						}
					} else if (s.getType() == SelectionType.MEASURE) {
						for (Iterator<?> it = s.iterator(); it.hasNext();) {
							Note n = (Note) it.next();
							for (int j = 0; j < addressMaps.length; j++) {
								if (addressMaps[j].contains(n)) {
									Element extAddress = addressMaps[j]
											.getExtAddress(n, j + 1);
									extAddress
											.removeAttribute(ADDRESS_CHORD_IN_BEAM);
									extAddress
											.removeAttribute(ADDRESS_CHORD_OR_BEAM);
									extAddress.removeAttribute(ADDRESS_FIGURE);
									extAddress.removeAttribute(ADDRESS_LAYER);
									se.addContent(extAddress);
								}
							}
						}
					}
					se.setAttribute("id", Integer.toString(id++));
					selections.addContent(se);
				}
			}
		}
		Namespace ns = Namespace
				.getNamespace("urn:mpeg:mpeg-4:schema:smr:smxf-mainscore:2007");
		Iterator<Element> it = mainDoc.getDescendants(new Filter() {

			public boolean matches(Object obj) {
				if (obj instanceof Element)
					return true;
				return false;
			}
		});
		while (it.hasNext())
			it.next().setNamespace(ns);
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(mainDoc, new FileOutputStream(new File(xsmFile,
				xsmBaseName + ".xml")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void writeIndexFile() {
		Element structure = new Element(INDEX_SMRSTRUCTURE);
		Document indexDoc = new Document(structure);
		Element mainscore = new Element(INDEX_MAINSCORE);
		mainscore.setText(xsmBaseName + ".xml");
		structure.addContent(mainscore);
		for (String part : partNames) {
			Element partElement = new Element(INDEX_PART);
			partElement.setText(part);
			structure.addContent(partElement);
		}
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(indexDoc, new FileOutputStream(new File(xsmFile,
				xsmBaseName + "-index.xml")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
