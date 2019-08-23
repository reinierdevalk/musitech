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
package de.uos.fmt.musitech.score;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.uos.fmt.musitech.score.ScoreEditor.Mode;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 16.02.2008
 */
public class ScoreEditorContextMenu extends MouseAdapter implements
		ScoreEditorListener {

	private ScoreEditor editor;
	private JPopupMenu popupMenu;
	private JMenuItem view, select, insert;
	private JMenu grid;
	private JMenuItem g_empty, g_1_1, g_1_2, g_1_4, g_1_8, g_1_16;
	private JMenu nDuration;
	private JMenuItem nd_1_1, nd_1_2, nd_1_4, nd_1_8, nd_1_16;

	public ScoreEditorContextMenu(ScoreEditor editor) {
		this.editor = editor;
		this.editor.addScoreEditorListener(this);
		popupMenu = new JPopupMenu();
		createMenuItems();
	}

	private void createMenuItems() {
		view = new JMenuItem("View");
		select = new JMenuItem("Select");
		insert = new JMenuItem("Insert");
		view.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				editor.setModus(Mode.VIEW);
			}
		});
		select.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				editor.setModus(Mode.SELECT_AND_EDIT);
			}
		});
		insert.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				editor.setModus(Mode.INSERT);
			}
		});
		popupMenu.add(view);
		popupMenu.add(select);
		popupMenu.add(insert);
		popupMenu.addSeparator();

		grid = new JMenu("Grid");
		g_empty = new JMenuItem("No Grid");
		g_1_1 = new JMenuItem("1/1");
		g_1_2 = new JMenuItem("1/2");
		g_1_4 = new JMenuItem("1/4");
		g_1_8 = new JMenuItem("1/8");
		g_1_16 = new JMenuItem("1/16");
		grid.add(g_empty);
		grid.add(g_1_1);
		grid.add(g_1_2);
		grid.add(g_1_4);
		grid.add(g_1_8);
		grid.add(g_1_16);
		ActionListener gridListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == g_empty)
					editor.setGrid(null);
				else if (e.getSource() == g_1_1)
					editor.setGrid(new Rational(1, 1));
				else if (e.getSource() == g_1_2)
					editor.setGrid(new Rational(1, 2));
				else if (e.getSource() == g_1_4)
					editor.setGrid(new Rational(1, 4));
				else if (e.getSource() == g_1_8)
					editor.setGrid(new Rational(1, 8));
				else if (e.getSource() == g_1_16)
					editor.setGrid(new Rational(1, 16));
			}
		};
		g_empty.addActionListener(gridListener);
		g_1_1.addActionListener(gridListener);
		g_1_2.addActionListener(gridListener);
		g_1_4.addActionListener(gridListener);
		g_1_8.addActionListener(gridListener);
		g_1_16.addActionListener(gridListener);
		popupMenu.add(grid);

		nDuration = new JMenu("Duration");
		nd_1_1 = new JMenuItem("1/1");
		nd_1_2 = new JMenuItem("1/2");
		nd_1_4 = new JMenuItem("1/4");
		nd_1_8 = new JMenuItem("1/8");
		nd_1_16 = new JMenuItem("1/16");
		nDuration.add(nd_1_1);
		nDuration.add(nd_1_2);
		nDuration.add(nd_1_4);
		nDuration.add(nd_1_8);
		nDuration.add(nd_1_16);
		ActionListener ndListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == nd_1_1)
					editor.setNoteLength(new Rational(1, 1));
				else if (e.getSource() == nd_1_2)
					editor.setNoteLength(new Rational(1, 2));
				else if (e.getSource() == nd_1_4)
					editor.setNoteLength(new Rational(1, 4));
				else if (e.getSource() == nd_1_8)
					editor.setNoteLength(new Rational(1, 8));
				else if (e.getSource() == nd_1_16)
					editor.setNoteLength(new Rational(1, 16));
			}
		};
		nd_1_1.addActionListener(ndListener);
		nd_1_2.addActionListener(ndListener);
		nd_1_4.addActionListener(ndListener);
		nd_1_8.addActionListener(ndListener);
		nd_1_16.addActionListener(ndListener);
		popupMenu.add(nDuration);
	}

	private void showMenu(int x, int y) {
		popupMenu.show(editor, x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger())
			showMenu(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
			showMenu(e.getX(), e.getY());
	}

	public void scoreEditorChanged(ScoreEditorEvent e, Object oldValue,
									Object newValue) {
		switch (e) {
		case MODUS_CHANGED:
			view.setEnabled(true);
			select.setEnabled(true);
			insert.setEnabled(true);
			grid.setEnabled(true);
			nDuration.setEnabled(true);
			switch ((Mode) newValue) {
			case VIEW:
				view.setEnabled(false);
				editor.setGrid(null);
				grid.setEnabled(false);
				nDuration.setEnabled(false);
				break;
			case SELECT_AND_EDIT:
				select.setEnabled(false);
				nDuration.setEnabled(false);
				break;
			case INSERT:
				insert.setEnabled(false);
			}
		case GRID_CHANGED:
			g_empty.setEnabled(true);
			g_1_1.setEnabled(true);
			g_1_2.setEnabled(true);
			g_1_4.setEnabled(true);
			g_1_8.setEnabled(true);
			g_1_16.setEnabled(true);
			if (newValue == null)
				g_empty.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[0]))
				g_1_1.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[1]))
				g_1_2.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[2]))
				g_1_4.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[3]))
				g_1_8.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[4]))
				g_1_16.setEnabled(false);
			break;
		case LENGTH_CHANGED:
			nd_1_1.setEnabled(true);
			nd_1_2.setEnabled(true);
			nd_1_4.setEnabled(true);
			nd_1_8.setEnabled(true);
			nd_1_16.setEnabled(true);
			if (newValue.equals(ScoreEditor.RATIONALS[0]))
				nd_1_1.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[1]))
				nd_1_2.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[2]))
				nd_1_4.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[3]))
				nd_1_8.setEnabled(false);
			else if (newValue.equals(ScoreEditor.RATIONALS[4]))
				nd_1_16.setEnabled(false);
			break;
		}
	}
}
