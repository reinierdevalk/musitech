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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.TimedMetrical;

/**
 * this class defines how the marker information box below the marker looks like
 * and what should happen when a mouseaction is performed on it
 * @author Wolfram Heyer
 */
public class MarkerInfoDisplay extends JPanel {

	private MarkerPanel markerPanel;
	private String msg; // value description
	private Color color;
	public JLabel label1, label2;

	public MarkerInfoDisplay() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setForeground(Color.BLUE);
		Map attribs = new HashMap();
		label1 = new JLabel("");
		label1.setFont(Font.getFont(attribs));
		add(label1, BorderLayout.CENTER);
		
		label2 = new JLabel("");
		label2.setFont(Font.getFont(attribs));
		add(label2, BorderLayout.SOUTH);
		//System.out.println(label.getWidth());
		setBorder(new LineBorder(Color.BLACK, 1));

		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				paint(getGraphics());
			}

		});
	}

	/**
	 * update values and call label.setText to print them out
	 * @param marker_
	 * @param realTimeIsSet_
	 */
	public void doValues(Marker marker, boolean realTimeIsSet) {

		TimedMetrical tm = (TimedMetrical) marker;

		if (realTimeIsSet) {
			color = Color.RED;
//			msg =
//				MarkerPanel.toStringFormatted(
//					tm.getMetricTime(),
//					markerPanel.getNumerator(),
//					markerPanel.getDenominator(),
//					markerPanel.getAnacrusis());
			String[] msg = markerPanel.getMetricalTimeLine().toStringFormatted(tm.getMetricTime());
			label1.setText(msg[0]);

			if (msg[1] != ""){
				label2.setText(msg[1]);
				label2.setVisible(true);
			}
		}
		else {
			color = Color.BLUE;
			msg = TimeStamp.toStringFormated(tm.getTime());
			label1.setText(msg);
			label2.setVisible(false);
		}
		Dimension dim = label1.getPreferredSize();
		setSize(dim);
	}

//	/**
//	 * repaint component
//	 * @param g
//	 */
//	public void paintComponent_old(Graphics g) {
//		revalidate();
//		g.setColor(color);
//		g.drawString(msg, 0, 10);
//	}

	/** 
	 * make the markelPanel known to this object
	 * @param panel
	 */
	public void setMarkerPanel(MarkerPanel panel) {
		markerPanel = panel;
	}
}
