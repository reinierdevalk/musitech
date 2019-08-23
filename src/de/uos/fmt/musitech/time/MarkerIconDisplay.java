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
 * File MarkerDisplay.java
 * Created on 12.09.2003
 */
package de.uos.fmt.musitech.time;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorDialog;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorPanel;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * this class defines how a marker looks like and what should 
 * happen when a mouse action is performed on it
 * @author Wolfram Heyer
 */
public class MarkerIconDisplay extends JComponent {

	private Image img;
	private static Image img_light;
	private static Image img_dark;
	boolean selected = false;

	MarkerPanel markerPanel;
	private String msg;		// description for value
	private Color color;
	Marker marker;

	MarkerControlPanel markerControlPanel;

	public MarkerIconDisplay() {

		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				selected = !selected;
				repaint();

				TimedMetrical tm = (TimedMetrical) marker;
				if (tm != null && markerControlPanel != null) {
					markerControlPanel.setTimeLabels(tm.getTime(), tm.getMetricTime(), markerPanel.getMetricalTimeLine().getCurrentTimeSignature(tm.getMetricTime()));
				} // TODO wieso gab es da bei MarkerControlPanel NullPointer Exceptions ? TW

				if (e.getClickCount() > 1) {
					Editor editor;
					try {
						editor = EditorFactory.createEditor(marker);
					} catch (EditorConstructionException e1) {
						e1.printStackTrace();
						return;
					}
					EditorPanel edPan = new EditorPanel();
					edPan.addEditor(editor);
					//EditorWindow edWin = new EditorWindow("Marker");
					EditorDialog edWin = new EditorDialog("Marker");
					edWin.setDefaultCloseOperation(EditorDialog.DISPOSE_ON_CLOSE);
					edWin.setEditorPanel(edPan);
					edWin.pack();
					edWin.show();
				}
			}

			public void mouseEntered(MouseEvent e) {
				markerPanel.getMarkerControlPanel().setTimeLabels(
					markerPanel.getMetricalTimeLine().getTime(marker.getMetricTime()),
					marker.getMetricTime(),
					markerPanel.getMetricalTimeLine().getCurrentTimeSignature(marker.getMetricTime()));
			}

			public void mouseExited(MouseEvent e) { }

			public void mousePressed(MouseEvent e) { }

			public void mouseReleased(MouseEvent e) { }
		});

		if (img == null) {
			img_light = this.getToolkit().getImage(this.getClass().getResource("icons/marker_m_l.png"));
			img_dark = this.getToolkit().getImage(this.getClass().getResource("icons/marker_m_d.png"));
		}
	}

	public void paintComponent(Graphics g) {
		if (selected)
			img = img_light;
		else
			img = img_dark;

		g.drawImage(img, 0, 0, this);
		g.setColor(Color.BLACK);
		g.drawLine(12, 20, 12, 30);
	}

	/** 
	 * make the markerPanel known to this object
	 * @param panel
	 */
	public void setMarkerPanel(MarkerPanel panel) {
		markerPanel = panel;
	}

	/** 
	 * sets the marker which is then represented by this object (this markerIconDisplay)
	 * @param marker
	 */
	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	/**
	 * make the markerControlPanel known to this object
	 * @param panel
	 */
	public void setMarkerControlPanel(MarkerControlPanel panel) {
		markerControlPanel = panel;
	}

	/**
	 * returns wether this object (this marker) is selected or not
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * sets this object (this marker) in a selected or unselected state
	 * @param b
	 */
	public void setSelected(boolean b) {
		selected = b;
	}

}
