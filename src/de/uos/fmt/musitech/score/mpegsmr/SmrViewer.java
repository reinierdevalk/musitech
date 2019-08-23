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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.score.NotationDisplay;

/**
 * @author Sascha Wegener
 * @version 18.01.2008
 */
public class SmrViewer extends JFrame {

	public static void main(String[] args) {
		Piece p = loadXSM(args);
		if (p == null)
			return;
		showPiece(p);
	}

	public static Piece loadXSM(String... args) {
		File file = null;
		if (args.length > 0)
			file = new File(args[0]);
		else {
			JFileChooser fc = new JFileChooser(new File("data"));
			fc.setFileHidingEnabled(true);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							&& f.getName().toLowerCase().endsWith(".xsm");
				}

				@Override
				public String getDescription() {
					return "*.xsm";
				}
			});
			int state = fc.showOpenDialog(null);
			if (state == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();

			}
		}
		if (file == null)
			return null;
		Piece p = XsmReader.readXSM(file);
		return p;
	}

	public static void showPieces(Piece... pieces) {
		SmrViewer[] viewer = new SmrViewer[pieces.length];
		for (int i = 0; i < viewer.length; i++)
			viewer[i] = showPiece(pieces[i]);
		viewer[0].setLocation(0, 0);
		int x = 0;
		int y = 0;
		int height = viewer[0].getSize().height;
		for (int i = 1; i < viewer.length; i++) {
			height = Math.max(height, viewer[i].getSize().height);
			x = viewer[i - 1].getLocation().x + viewer[i - 1].getSize().width;
			if (x + viewer[i].getSize().width > Toolkit.getDefaultToolkit()
					.getScreenSize().width) {
				x = 0;
				y += height;
				height = 0;
			}
			viewer[i].setLocation(x, y);
		}
	}

	public static SmrViewer showPiece(Piece p) {
		return showPiece(p, 1);
	}

	public static SmrViewer showPiece(Piece p, double zoom) {
		return showPiece(p, null, zoom);
	}

	public static SmrViewer showPiece(Piece p, String title) {
		return showPiece(p, title, 1);
	}

	public static SmrViewer showPiece(Piece p, String title, double zoom) {
		SmrViewer viewer = new SmrViewer(p, zoom);
		if (title != null)
			viewer.setTitle(title);
		viewer.setVisible(true);
		return viewer;
	}

	private Piece piece;
	private NotationDisplay notationDisplay;
	private List<Selection> selections;
	private JComboBox selectionBox;

	public SmrViewer(Piece piece) {
		this(piece, 1);
	}

	public SmrViewer(Piece piece, double zoom) {
		this.piece = piece;
		createNotationDisplay(zoom);
		createSelectionBox();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (notationDisplay.getPreferredSize().width > 800
			|| notationDisplay.getPreferredSize().height > 600)
			setSize(
				notationDisplay.getPreferredSize().width > 800	? 800
																: notationDisplay
																		.getPreferredSize().width,
				notationDisplay.getPreferredSize().height > 600	? 600
																: notationDisplay
																		.getPreferredSize().height);
		else
			pack();
	}

	private void createSelectionBox() {
		if (piece.getSelectionPool() == null
			|| piece.getSelectionPool().size() == 0)
			return;
		SelectionManager.getManager().addListener(
			notationDisplay.getScorePanel());
		selections = new ArrayList<Selection>();
		Selection s = new Selection(piece.getContext());
		s.getAnnotations().add("---");
		selections.add(s);
		for (Iterator<?> i = piece.getSelectionPool().iterator(); i.hasNext();) {
			Container<?> c = (Container<?>) i.next();
			if (c instanceof Selection) {
				selections.add((Selection) c);
			}
		}
		selectionBox = new JComboBox(selections.toArray());
		selectionBox.setRenderer(new DefaultListCellRenderer() {

			public Component getListCellRendererComponent(JList list,
															Object value,
															int index,
															boolean isSelected,
															boolean cellHasFocus) {
				if (value instanceof Selection) {
					Selection s = (Selection) value;
					if (s.getAnnotations().isEmpty())
						value = "Selection " + index;
					else
						value = s.getAnnotations().get(0);
				}
				return super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
			}
		});
		selectionBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Selection s = selections.get(selectionBox.getSelectedIndex());
				SelectionManager.getManager().getSelection().clear(
					SmrViewer.this);
				SelectionManager.getManager().getSelection().addAll(
					s.getContent(), SmrViewer.this);
			}
		});
		add(selectionBox, BorderLayout.SOUTH);
	}

	private void createNotationDisplay(double zoom) {
		notationDisplay = new NotationDisplay();
		notationDisplay.init(piece, null, null);
		if (zoom != 1)
			notationDisplay.setZoom(zoom);
		JScrollPane sp = new JScrollPane(notationDisplay);
		add(sp);
	}
}
