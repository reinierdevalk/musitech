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
package de.uos.fmt.musitech.audio.proc.fft.backup;

/*************************** FrequencyWindow.java ***************************/
/* Datum: 12. Januar 2002
 * Autor: Christian Datzko
 * Copyright: Christian Datzko, 2002
 * E-Mail-Adresse: datzko@t-online.de
 * Programmiersprache und -version: Java(TM) 2 SDK, Standard Edition (1.3.1_01)
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

/**
 * This class is an object that is a window plotting frequency data.
 */

public class FrequencyWindow extends JFrame {
  private boolean dataInitialized = false;
                                        // are the data fields initialized?
  private double[][] frequencyTable;    // all frequencies
  private double min;                   // minimum value
  private double max;                   // maximum value

  private int minWidth = 160;           // minimum output width
  private int minHeight = 120;          // minimum output height
  private Color backColor = Color.white;// standard color for background
  private Color graphColor = Color.blue;// standard color for graph
  private Color coordColor = Color.black;
                                        // standard color for coordinate system
  private int spaceLeft = 10;           // space from the left
  private int spaceRight = 10;          // space from the right
  private int spaceTop = 29;            // space from the top
  private int spaceBottom = 10;         // space from the bottom

  /**
   * This constructor gets you a window that plots <CODE>data[][]</CODE>.
   * @author Christian Datzko
   * @version 1.0
   * @param data[][] an array of pairs of data, where <CODE>data[i][0]</CODE>
   * is a frequency and <CODE>data[i][1]</CODE> is an intensity. Assuming
   * linearity between <CODE>i</CODE> and <CODE>data[i][0]</CODE>.
   * @param title is a title string for the window.
   */
  public FrequencyWindow(double[][] data, String title) {
    super(title);
    if (data != null) {
      frequencyTable = data;
      // find min and max
      if (data[0].length >= 2) {
        min = max = data[0][1];
        for (int i = 1; i < data.length; i++) {
          if (data[i][1] < min) {
            min = data[i][1];
          } // if
          else {
            if (data[i][1] > max) {
              max = data[i][1];
            } // if
          } // else
        } // for
        dataInitialized = true;
      } // if
      else
        dataInitialized = false;
    } // if
    else {
      throw new IllegalArgumentException("Need some data to show.");
    } // else
  } // FrequencyWindows(String title)

  /**
   * This constructor calls <CODE>FrequencyWindow(double[][] data, String
   * title)</CODE> with <CODE>title</CODE> = "Frequency Analysis".
   * @author Christian Datzko
   * @version 1.0
   * @param data[][] an array of pairs of data, where <CODE>data[i][0]</CODE>
   * is a frequency and <CODE>data[i][1]</CODE> is an intensity. Assuming
   * linearity between <CODE>i</CODE> and <CODE>data[i][0]</CODE>.
   */
  public FrequencyWindow (double[][] data) {
    this(data, "Frequency Analysis");
  } // FrequencyWindow ()

  /**
   * Returns if the frequency data is initialized (this is when the constructor
   * got a correct <CODE>double[][]</CODE> of data).
   * @author Christian Datzko
   * @version 1.0
   * @return boolean data is initialized.
   */
  public boolean isDataInitialized() {
    return dataInitialized;
  } // boolean isDataInitialized()

  /**
   * Sets minimum width of the frequency window
   * @author Christian Datzko
   * @version 1.0
   * @param w must be larger than 0
   */
  public void setMinWidth(int w) {
    if (w > 0) {
      minWidth = w;
    } // if
  } // void setMinHeight(int h)

  /**
   * Gets minimum width of the frequency window
   * @author Christian Datzko
   * @version 1.0
   * @return minimum width
   */
  public int getMinWidth() {
    return minWidth;
  } // int getMinWidth()

  /**
   * Sets minimum height of the frequency window
   * @author Christian Datzko
   * @version 1.0
   * @param h must be larger than 0
   */
  public void setMinHeight(int h) {
    if (h > 0) {
      minHeight = h;
    } // if
  } // void setMinHeight(int h)

  /**
   * Gets minimum height of the frequency window
   * @author Christian Datzko
   * @version 1.0
   * @return minimum height
   */
  public int getMinHeight() {
    return minHeight;
  } // int getMinHeight()

  /**
   * Sets color for background of frequency graph
   * @author Christian Datzko
   * @version 1.0
   * @param c some Color
   */
  public void setBackgroundColor(Color c) {
    if (c != null) {
      backColor = c;
    } // if
  } // void setBackgroundColor(Color c)

  /**
   * Gets color for background of frequency graph
   * @author Christian Datzko
   * @version 1.0
   * @return background color of frequency graph
   */
  public Color getBackgroundColor() {
    return backColor;
  } // void setBackgroundColor(Color c)

  /**
   * Sets color for coordinate system
   * @author Christian Datzko
   * @version 1.0
   * @param c some Color
   */
  public void setCoordinateSystemColor(Color c) {
    if (c != null) {
      coordColor = c;
    } // if
  } // void setCoordinateSystemColor(Color c)

  /**
   * Gets color for coordinate system
   * @author Christian Datzko
   * @version 1.0
   * @return coordinate system color
   */
  public Color getCoordinateSystemColor() {
    return coordColor;
  } // void setCoordinateSystemColor(Color c)

  /**
   * Sets color for frequency graph
   * @author Christian Datzko
   * @version 1.0
   * @param c some Color
   */
  public void setGraphColor(Color c) {
    if (c != null) {
      graphColor = c;
    } // if
  } // void setGraphColor(Color c)

  /**
   * Gets color for frequency graph
   * @author Christian Datzko
   * @version 1.0
   * @return frequency graph color
   */
  public Color getGraphColor() {
    return graphColor;
  } // void setGraphColor(Color c)

  /**
   * Sets space to be left between border of window and border of graph
   * @author Christian Datzko
   * @version 1.0
   * @param left space from left
   * @param right space from right
   * @param top space from top
   * @param bottom space from bottom
   */
  public void setSpace(int left, int right, int top, int bottom) {
    if ((left > 0 && right > 0 && top > 0 && bottom > 0)
        && (left + right < minWidth)
        && (top + bottom < minHeight)) {
      spaceLeft = left;
      spaceRight = right;
      spaceTop = top;
      spaceBottom = bottom;
    } // if
  } // void setSpace(int left, int right, int top, int bottom)

  /**
   * Gets space from left between between border of window and border of graph
   * @author Christian Datzko
   * @version 1.0
   * @return space from left between between border of window and border of graph
   */
  public int getSpaceLeft() {
    return spaceLeft;
  } // int getSpaceLeft()

  /**
   * Gets space from right between between border of window and border of graph
   * @author Christian Datzko
   * @version 1.0
   * @return space from right between between border of window and border of graph
   */
  public int getSpaceRight() {
    return spaceRight;
  } // int getSpaceRight()

  /**
   * Gets space from top between between border of window and border of graph
   * @author Christian Datzko
   * @version 1.0
   * @return space from top between between border of window and border of graph
   */
  public int getSpaceTop() {
    return spaceTop;
  } // int getSpaceTop()

  /**
   * Gets space from bottom between between border of window and border of graph
   * @author Christian Datzko
   * @version 1.0
   * @return space from bottom between between border of window and border of graph
   */
  public int getSpaceBottom() {
    return spaceBottom;
  } // int getSpaceBottom()

  /**
   * Does the plotting section of the <CODE>FrequencyWindow</CODE>. This method
   * is called each time the window is redrawn. You don't need to call this
   * method.
   * @author Christian Datzko
   * @version 1.0
   * @param g the graphics object to draw to
   */
  public void paint(Graphics g) {
    Dimension d = getSize();
    if (d.width < minWidth) {
      setSize(minWidth, d.height);
    } // if
    d = getSize();
    if (d.height < minHeight) {
      setSize(d.width, minHeight);
    } // if
    d = getSize();

    // delete old contents
    g.clearRect(1, 1, d.width, d.height);
    // draw coordinate system
    if (dataInitialized) {
      int w = d.width - spaceLeft - spaceRight - 1;
      int h = d.height - spaceTop - spaceBottom - 1;

      // graph background
      g.setColor(backColor);
      g.fillRect(spaceLeft, spaceTop, d.width - spaceLeft - spaceRight,
                 d.height - spaceTop - spaceBottom);

      // coordinate system
      g.setColor(coordColor);
//      int offsetY = (int)Math.round((double)h * (double)(max) /
//                                    (double)(max - min)) + spaceTop;
      g.drawLine(spaceLeft, d.height - spaceBottom,
                 d.width - spaceRight - 1, d.height - spaceBottom);
      g.drawLine(d.width - spaceRight - 6, d.height - spaceBottom - 5,
                 d.width - spaceRight - 1, d.height - spaceBottom);
      g.drawLine(spaceLeft, spaceTop, spaceLeft, d.height - spaceBottom);
      g.drawLine(spaceLeft, spaceTop, spaceLeft + 5, spaceTop + 5);

      // graph
      g.setColor(graphColor);
      int x1, x2, y1, y2;
      x2 = spaceLeft;
      y2 = (int)Math.round((double)h * (double)(max - frequencyTable[0][1]) /
                           (double)(max - min)) + spaceTop;
      for (int i = 1; i < frequencyTable.length; i++) {
        x1 = x2;
        x2 = (int)Math.round((double)(i * w) /
                             (double)(frequencyTable.length - 1)) + spaceLeft;
        y1 = y2;
        y2 = (int)Math.round((double)h * (double)(max - frequencyTable[i][1]) /
                             (double)(max - min)) + spaceTop;
        g.drawLine(x1, y1, x2, y2);
      } // for
    } // if
  } // void paint (Graphics g)
 // FrequencyWindow
}