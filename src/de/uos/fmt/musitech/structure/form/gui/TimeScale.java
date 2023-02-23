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
 * File TimeSacle.java
 * Created on 23.04.2004
 */
package de.uos.fmt.musitech.structure.form.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * This class is used to display a timescale
 * Zoomed in and out by changing microsPerPix
 * Offset change in ms
 *  
 * @author Jan-Hendrik Kramer and Tillman Weyde 
 */
public class TimeScale extends JPanel {

	boolean paintMajorTicks = true; // 1 or more second
	boolean paintMediumTicks = true; // 1/2 second
	boolean paintMinorTicks = true; // 1/10 or less second
	boolean paintSecVal = true;
	boolean paintMsVal = false;
	boolean paintUnit = true;
	private double majorTickSpace = 20; // default value
	private double mediumTickSpace = 10; // default value
	private double minorTickSpace = 2; // default value
	double microsPerPix = 50; // default value
	long offset = 0; // offset in microS
	private int paintoffset = 0; // display offset in Pixel
	private String majorUnit = "s";
	private String minorUnit = "ms";

	long majorUnitSpace = 1;

	public TimeScale() {
		setBackground(new Color(235, 235, 235));
	}

	/** 
	 * @see java.awt.Component#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		dim.setSize(getParent().getWidth(), 20);
		return dim;
	}

	/** 
	 * @see java.awt.Component#getMinimumSize()
	 */
//	public Dimension getMinimumSize() {
//		Dimension dim = super.getMinimumSize();
//		dim.setSize(getParent().getWidth(), 20);
//		return dim;
//	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	@Override
	public Dimension getMaximumSize() {
		Dimension dim = new Dimension();
		dim.setSize(getParent().getWidth(), 50);
		return dim;
	}

	public void paintTicks(Graphics g, double tickSpace, int tickLength) {
		for (int i = 0;
			i < (getWidth() / tickSpace) + paintoffset % tickSpace;
			i++) {
			g.drawLine(
				(int) ((i * tickSpace) - (paintoffset % tickSpace)),
				0,
				(int) ((i * tickSpace) - (paintoffset % tickSpace)),
				tickLength);
		}
	}

	public void paintUnit(Graphics g) {
		int number = 0;
		// calculate first number to display
		if (majorUnit == "s") {
			//			System.out.println(
			//				"number (s): " + (int) (offset / (majorUnitSpace * 1000)));
			number =
				(int) ((offset / 1000000)
					- (offset / 1000000) % majorUnitSpace);
		}
		if (majorUnit == "ms") {
			number = (int) ((offset / 1000) - (offset / 1000) % majorUnitSpace);
		}
		if (majorUnit == "m") {
			number = 0;
		}
		for (int i = 1; i < (getWidth() / majorTickSpace) + 1; i += 1) {
			String num = "";
			int width = 0;
			number += majorUnitSpace;
			num += number;

			if (paintUnit) {

				num += " " + majorUnit;
				// get Width from printed text
				width =
					g.getFontMetrics().charsWidth(
						num.toCharArray(),
						0,
						(int) (Math.log(i) / Math.log(10)) + 1);
				// to calculate number of digits
				width += 1;

			} else {
				width = g.getFontMetrics().stringWidth(num) / 2;
			}
			g.drawString(
				num,
				(int) ((i * majorTickSpace) - (paintoffset % majorTickSpace))
					- width,
				18);
		}
	}

	/** 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (paintMajorTicks)
			//			paintMajorTicks(g);
			paintTicks(g, majorTickSpace, 7);
		if (paintMediumTicks)
			//			paintMediumTicks(g);
			paintTicks(g, mediumTickSpace, 5);
		if (paintMinorTicks)
			//			paintMinorTicks(g);
			paintTicks(g, minorTickSpace, 3);
		if (paintSecVal)
			paintUnit(g);

	}

	/** 
	 * getScaleValueX calculates the appropriate distribution of tick-values on the x-axis.
	 * @param scaleX  proportion between pixels and values: scaleX = pix / vals
	 * @param	minDist minimum pixel distance between Markers
	 * @return the distance between values of ticks
	 */
	public static long getScaleValueX(double scaleX, int minDist) {
		int[] vals = new int[] { 1, 2, 5 };
		long powerOfTen = 1;
		long tickValDist = 1;
		long tickPixelDist = 1;

		int valsIndex = 0;
		do {

			tickValDist = vals[valsIndex] * powerOfTen;
			valsIndex++;
			if (valsIndex >= vals.length) {
				valsIndex = 0;
				powerOfTen *= 10;
			}
			tickPixelDist = (long) (tickValDist * scaleX);

		} while (tickPixelDist < minDist);

		return tickValDist;
	}

	/**
	 * @return
	 */
	public double getMicrosPerPix() {
		return microsPerPix;
	}

	public void setMicrosPerPix(double newMPP) {
		microsPerPix = newMPP;
//		if (DebugState.DEBUG)
//			System.out.println(
//				"TimeScale: majorUnitSpace: "
//					+ (int) (getScaleValueX(1 / microsPerPix, 60)));
		majorUnitSpace = (int) (getScaleValueX(1 / microsPerPix, 60));
		majorUnit = "ms";

		majorTickSpace = getScaleValueX(1 / microsPerPix, 60) / newMPP;
		mediumTickSpace = majorTickSpace / 2;
		minorTickSpace = majorTickSpace / 10;
		//		if (majorUnitSpace >= 50000) {
		//			majorUnitSpace /= 50000;
		//			majorUnit = "m";
		//			majorTickSpace = getScaleValueX(1 / microsPerPix, 48) / newMPP;
		//			mediumTickSpace = majorTickSpace / 2;
		//			minorTickSpace = majorTickSpace / 12;
		//		}
		majorUnit = "us";
		if (majorUnitSpace >= 1000) {
			majorUnitSpace /= 1000; // to get ms
			majorUnit = "ms";
		}
		if (majorUnitSpace >= 1000) {
			majorUnitSpace /= 1000; // to get s
			majorUnit = "s";
		}
//		System.out.println("TimeScale: majorUnitSpace: " + majorUnitSpace);
		repaint();
	}

	/**
	 * @return
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * offset in microsec
	 * paintoffset in pixels will be calculated
	 * @param newOffset in microsec
	 */
	public void setOffset(long newOffset) {
		offset = newOffset;
		paintoffset = (int) Math.round(offset / microsPerPix);
		repaint();
	}

}
