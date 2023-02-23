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

public class GraphPlot extends Canvas {

  public static final int SIGNAL = 1;
  public static final int SPECTRUM = 2;

  Color plotColor = Color.yellow; // color for the actuall plot
  Color axisColor = Color.black;
  Color gridColor = Color.black;
  Color bgColor   = Color.blue;
  int plotStyle = SIGNAL;
  boolean tracePlot = true; // if true: line, else bars
  boolean logScale = false; // if true
  int vertSpace = 20; // vertical insets for drawing
  int horzSpace = 20; // horizontal insets for drawing
  int vertIntervals = 8; // number of intervals into which the range is divided graphically 
  int horzIntervals = 10; // number of intervals into which the range is divided graphically
  float xmax = 0.0f; // maximal x-value to be displad
  float ymax = 0.0f; // maximal y-value to be displad
  float xScale; // scaling on the axes
  float yScale;
  private float[] plotValues; // the vlues to be plotted 
  //  int nPoints = 0; // number of values to be plotted
  // this was removed and replaced by plotValues.length

  public GraphPlot() {
  }

  public void setPlotColor(Color c) {
    if (c != null) plotColor = c;
  }

  public Color getPlotColor() {
    return plotColor;
  }

  public void setAxisColor(Color c) {
    if (c != null) axisColor = c;
  }

  public Color getAxisColor() {
    return axisColor;
  }

  public void setGridColor(Color c) {
    if (c != null) gridColor = c;
  }

  public Color getGridColor() {
    return gridColor;
  }

  public void setBgColor(Color c) {
    if (c != null) bgColor = c;
  }

  public Color getBgColor() {
    return bgColor;
  }

  public void setPlotStyle(int pst) {
    plotStyle = pst;
  }

  public int getPlotStyle() {
    return plotStyle;
  }

  public void setTracePlot(boolean b) {
    tracePlot = b;
  }

  public boolean isTracePlot() {
    return tracePlot;
  }

  public void setLogScale(boolean b) {
    logScale = b;
  }

  public boolean isLogScale() {
    return logScale;
  }

  public void setVertSpace(int v) {
    vertSpace = v;
  }

  public int getVertSpace() {
    return vertSpace;
  }

  public void setHorzSpace(int h) {
    horzSpace = h;
  }

  public int getHorzSpace() {
    return horzSpace;
  }

  public int getVertIntervals() {
    return vertIntervals;
  }

  public void setVertIntervals(int i) {
    vertIntervals = i;
  }

  public int getHorzIntervals() {
    return horzIntervals;
  }

  public void setHorzIntervals(int i) {
    horzIntervals = i;
  }

  public void setYmax(float m) {
    ymax = m;
  }

  public float getYmax() {
    return ymax;
  }

  public void setPlotValues(float[] values) {
//    nPoints = values.length;
//    plotValues = new float[nPoints];
    plotValues = values;
    repaint();
  }

  @Override
public void paint(Graphics g) {

    int x, y;
    int top = vertSpace;
    int bottom = getSize().height - vertSpace;
    int left = horzSpace;
    int right = getSize().width - horzSpace;
    int width = right - left;
    int fullHeight = bottom - top;
    int centre = (top + bottom) / 2;
    int xAxisPos = centre;
    int yHeight = fullHeight / 2;
    if (plotStyle == SPECTRUM) {
      xAxisPos = bottom;
      yHeight = fullHeight;
    }
    this.setBackground(bgColor);
    if (logScale) {
      xAxisPos = top;
      g.setColor(gridColor);
      // vertical grid lines
      for (int i = 0; i <= vertIntervals; i++) {
        x = left + i*width/vertIntervals;
        g.drawLine(x, top, x, bottom);
      }
      // horizontal grid lines
      for (int i = 0; i <= horzIntervals; i++) {
        y = top + i*fullHeight/horzIntervals;
        g.drawLine(left, y, right, y);
      }
    }
    g.setColor(axisColor);
    g.drawLine(left, top, left, bottom);        // vertical axis
    g.drawLine(left, xAxisPos, right, xAxisPos);  // horizontal axis

//    if (nPoints != 0) {
//      g.setColor(plotColor);
//      // horizontal scale factor:
//      xScale = width/(float)(nPoints-1);
//      // vertical scale factor:
//      yScale = yHeight/ymax;
//      int[] xCoords = new int[nPoints];
//      int[] yCoords = new int[nPoints];
//      for (int i = 0; i < nPoints; i++) {
//         xCoords[i] = left + Math.round(i*xScale);
//         yCoords[i] = xAxisPos - Math.round(plotValues[i]*yScale);
//      }
//      if (tracePlot)
//        g.drawPolyline(xCoords, yCoords, nPoints);
//      else { // bar plot
//        for (int i = 0; i < nPoints; i++)
//          g.drawLine(xCoords[i], xAxisPos, xCoords[i], yCoords[i]);
//      }
//    }
	if (plotValues != null && plotValues.length != 0) {
	  g.setColor(plotColor);
	  // horizontal scale factor:
	  xScale = width/(float)(plotValues.length-1);
	  // vertical scale factor:
	  yScale = yHeight/ymax;
	  int[] xCoords = new int[plotValues.length];
	  int[] yCoords = new int[plotValues.length];
	  for (int i = 0; i < plotValues.length; i++) {
		 xCoords[i] = left + Math.round(i*xScale);
		 yCoords[i] = xAxisPos - Math.round(plotValues[i]*yScale);
	  }
	  if (tracePlot)
		g.drawPolyline(xCoords, yCoords, plotValues.length);
	  else { // bar plot
		for (int i = 0; i < plotValues.length; i++)
		  g.drawLine(xCoords[i], xAxisPos, xCoords[i], yCoords[i]);
	  }
	}

  }
}
