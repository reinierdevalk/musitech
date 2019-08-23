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
package de.uos.fmt.musitech.performance.midi.gui;
import java.lang.reflect.Field;

import javax.swing.table.AbstractTableModel;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;

/**
 * Table Model for displaying a MMidiNote Sequence.
 * @author TW
 * @version  $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $

 */
public class NoteSequenceTableModel extends AbstractTableModel {
	protected MidiNoteSequence noteSequence;
	String columnNames[] = {"Number", "begin", "length", "velocity", "pitch", "offset"};
	String columnHeaders[] = {"Number", "Begin", "Length", "Velocity", "Pitch", "Deviation"};

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col].toString();
	}

	/**
	 * Accessor method
	 * @date (10.10.00 21:24:48)
	 * @return music.MidiNoteSequence
	 */
	public MidiNoteSequence getNoteSequence() {
		return noteSequence;
	}

	public int getRowCount() {
		if (noteSequence == null)
			return 0;
		return noteSequence.size();
	}

	public Object getValueAt(int row, int col) {
		MidiNote note = noteSequence.getNoteAt(row);
		if (col == 0)
			return new Integer(row);
		try {
			Field f = note.getClass().getField(columnNames[col]);
			return f.get(note);
		} catch (Exception e) {
			System.out.println(e + " " + columnNames[col]);
			return null;
		}
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	/**
	 * Accessor method
	 * @date (10.10.00 21:24:48)
	 * @param newNoteSequence music.MidiNoteSequence
	 */
	public void setNoteSequence(MidiNoteSequence newNoteSequence) {
		noteSequence = newNoteSequence;
	}

	public void setValueAt(Object value, int row, int col) {
		MidiNote note = noteSequence.getNoteAt(row);
		if (col == 0) {
			if (value.equals("d")) {
				getNoteSequence().deleteNoteAt(row);
				fireTableRowsDeleted(row, getRowCount() - 1);
			}
			if (value.equals("i")) {
				getNoteSequence().insertNoteAt(note.copy(), row);
				fireTableRowsDeleted(row, getRowCount() - 1);
			}
			if (value.equals("a")) {
				getNoteSequence().insertNoteAt(note.copy(), row + 1);
				fireTableRowsDeleted(row, getRowCount() - 1);
			}
			return;
		}
		try {
			Field f = note.getClass().getField(columnNames[col]);
			Object typedValue = value;
			if (f.getType() == long.class)
				typedValue = new Long(value.toString());
			else
				if (f.getType() == double.class)
					typedValue = new Double(value.toString());
				else
					if (f.getType() == int.class)
						typedValue = new Integer(value.toString());
			f.set(note, typedValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireTableCellUpdated(row, col);
	}
}