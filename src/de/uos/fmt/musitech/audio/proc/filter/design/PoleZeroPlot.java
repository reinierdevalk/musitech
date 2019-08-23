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
package de.uos.fmt.musitech.audio.proc.filter.design;

import java.awt.*;

public class PoleZeroPlot extends Canvas {

	float[] pReal;
	float[] pImag;
	float[] z;
	int order = 0;
	int gridIntervals = 10;
	int zSize = 4; // zero symbol size
	int pSize = 3; // pole symbol size
	float scale;
	Color plotColor = Color.blue;
	Color axisColor = Color.darkGray;
	Color circColor = Color.red;
	Color gridColor = Color.darkGray;
	Color bgColor = Color.lightGray;
	int vertSpace = 20;
	int horzSpace = 20;

	public PoleZeroPlot() {
	}

	public void setPlotColor(Color c) {
		if (c != null)
			plotColor = c;
	}

	public Color getPlotColor() {
		return plotColor;
	}

	public void setAxisColor(Color c) {
		if (c != null)
			axisColor = c;
	}

	public Color getAxisColor() {
		return axisColor;
	}

	public void setGridColor(Color c) {
		if (c != null)
			gridColor = c;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setCircColor(Color c) {
		if (c != null)
			circColor = c;
	}

	public Color getCircColor() {
		return circColor;
	}

	public void setBgColor(Color c) {
		if (c != null)
			bgColor = c;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setPolesAndZeros(float[] pr, float[] pi, float[] zr) {
		order = pr.length - 1; // number of poles/zeros = filter order
		pReal = new float[order + 1];
		pImag = new float[order + 1];
		z = new float[order + 1];
		for (int i = 1; i <= order; i++) {
			pReal[i] = pr[i];
			pImag[i] = pi[i];
			z[i] = zr[i];
		}
		repaint();
	}

	public void paint(Graphics g) {
		int x, y;
		int xc = getSize().width / 2;
		int yc = getSize().height / 2;
		int width = getSize().width - 2 * horzSpace;
		int height = getSize().height - 2 * vertSpace;
		int radius = Math.min(width / 2, height / 2);
		int top = yc - radius;
		int bottom = yc + radius;
		int left = xc - radius;
		int right = xc + radius;
		scale = 2 * radius / (float) gridIntervals;
		setBackground(bgColor);
		g.setColor(gridColor);
		// grid lines
		for (int i = 0; i <= gridIntervals; i++) {
			x = left + Math.round(i * scale);
			y = top + Math.round(i * scale);
			g.drawLine(x, top, x, bottom); // vertical grid line
			g.drawLine(left, y, right, y); // horizontal grid line
		}
		g.setColor(axisColor);
		g.drawLine(xc, top - vertSpace, xc, bottom + vertSpace); // vertical axis
		g.setFont(new Font("Sans serif", Font.BOLD, 10));
		FontMetrics fm = g.getFontMetrics();
		int h = fm.getMaxAscent();
		g.drawString("Im", xc + 4, top - vertSpace + h);
		g.drawLine(left - horzSpace, yc, right + horzSpace, yc); // horizontal axis
		int w = fm.stringWidth("Re");
		g.drawString("Re", right + horzSpace - w, yc + h + 4);
		g.setColor(circColor);
		g.drawOval(left, top, 2 * radius, 2 * radius); // unit circle
		if (order > 0) {
			g.setColor(plotColor);
			// plot zeros
			for (int i = 1; i <= order; i++) {
				x = xc + Math.round(radius * z[i]);
				g.drawOval(x - zSize, yc - zSize, 2 * zSize, 2 * zSize);
			}
			// plot poles
			for (int i = 1; i <= order; i++) {
				x = xc + Math.round(radius * pReal[i]);
				y = yc - Math.round(radius * pImag[i]);
				g.drawLine(x - pSize, y - pSize, x + pSize, y + pSize);
				g.drawLine(x - pSize, y + pSize, x + pSize, y - pSize);
			}
		}
	}
}