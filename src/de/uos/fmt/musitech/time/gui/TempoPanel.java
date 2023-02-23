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
package de.uos.fmt.musitech.time.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.structure.form.gui.TimeScale;
import de.uos.fmt.musitech.time.TimeStamp;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This Panel displays a TempoCurve. Its size depends on MarkerPanel.
 * 
 * @see de.uos.fmt.musitech.data.structure.MetricalTimeLine . TODO: Implement a
 *      Listener for when the model (the dataclass) changes...
 * @author Collin Rogowski / Wolfram Heyer
 */
public class TempoPanel extends JPanel implements DataChangeListener, Timeable {
    private static final int spaceX = 15; // extra space in x-direction in
                                          // coordinate system

    private static final int spaceY = 20; // extra space in y-direction in
                                          // coordinate system

    private static final int spaceTop = 20; // extra space in y-direction in
                                            // coordinate system from the top

	private long cursorPosition = -1;

    private int widthX;
    private int widthY;

    private static Color colorBpm = Color.MAGENTA.darker().darker();
	private static final Color cursorColour = java.awt.Color.GRAY; 

    private MetricalTimeLine metricalTimeLine;

    private long intervall = 50;
    private long intervall2 = 250;

    private int panelSizeX = 800 - 10; // @TODO: adjust panelSize dynamically to
                                       // MarkerDisplay -> getWidth();

    private int panelSizeY = 50;

    private boolean realTimeIsSet = true; // selected primary measure (realTime
                                          // or metricTime)

    // setValues
    int graphMaxX;

    int graphMaxY;

    private long maxRealTime;

    private Rational maxMetricTime;

    private long biggestX;

    private int biggestY;

    private double scaleX;

    private double scaleY;

    /**
     * @param timeLine
     * @author Wolfram Heyer
     */
    public TempoPanel(MetricalTimeLine timeLine) {
        metricalTimeLine = timeLine;
        getMaxiValues();
        setBackground(Color.WHITE);
        //		IDataChangeManager changeManager = DataChangeManager.getInstance();
        //		changeManager.interestExpanded(this, timeLine);
        Collection changeInterest = new ArrayList();
        Collection contents = timeLine.getContentsRecursiveList(null);
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if(element instanceof TimedMetrical){
                changeInterest.add(element);
                changeInterest.add(((TimedMetrical)element).getMetricTime());
            }
        }
        DataChangeManager.getInstance().interestExpandElements(
                this, changeInterest);
    }

	/**
	 * Get maximum values for realTime and metricTime (maxX).
	 * TODO unify with MakerPanel
	 */
	private void getMaxiValues() {
		boolean debug = false;
//		boolean debug = true;

		maxRealTime = metricalTimeLine.getEndMarker().getTime(); // 3100
		
		Rational myTime = metricalTimeLine.getEndMarker().getMetricTime();
		// get last timedMetrical before endMarker
		//myTime = metricalTimeLine.getPreviousTimedMetrical(myTime).getMetricTime();
        
		int[] value = metricalTimeLine.toMeasureBeatRemainder(myTime);
		// measures
		maxMetricTime = new Rational( (value[0]-1), 1 );
		// add beats
		maxMetricTime = maxMetricTime.add( (value[1]-1), metricalTimeLine.getCurrentTimeSignature(myTime).getDenom());

		if (debug) System.out.println("Tempo.getMaxiValues: maxMetricTime ="+maxMetricTime);
		if (debug) System.out.println();
	}

    /**
     * set panel to zoom and update layout
     * 
     * @param factor
     * @author Wolfram Heyer
     */
    public void setZoom(int factor) {
        setPreferredSize(new Dimension(panelSizeX * factor, panelSizeY));
        getParent().doLayout();
    }

    /**
     * set all values needed for paint method
     * 
     * @author Wolfram Heyer
     */
    private void setValuesForPaint() {

        graphMaxX = (int) getSize().getWidth();
        graphMaxY = (int) getSize().getHeight();

        Marker marker;

        //get the maximum tempo which will be used on Y-axis
        biggestY = 0;

        for (Iterator iter = metricalTimeLine.iterator(); iter.hasNext();) {
            marker = (Marker) iter.next();

            if (marker instanceof TimedMetrical) {
                TimedMetrical tm = (TimedMetrical) marker;

                if (biggestY < metricalTimeLine.getTempo(tm.getTime()))
                    biggestY = (int) metricalTimeLine.getTempo(tm.getTime());
            }
        }
        /*
         * TODO: Tempo between last Marker and Endmarker makes the graph in
         * TempoPanel unusable.
         * 
         * The MetricalTimeLine was set in Test_wolf. The last marker which was
         * set is TimedMetrical timedMetrical10 = new
         * TimedMetrical(TimeStamp.timeStampMillis(40000), new Rational(20));
         * 
         * The Endmarker of the MetricalTimeLine is at 200000 milliseconds. The
         * tempo between timedMetrical10 and Endmarker is 149970.
         * 
         * This tempo makes the graph in TempoPanel unusable. Therefore I set it
         * to a maximum of 200 BMP here. (Wolfram)
         */
        if (biggestY > 200)
            biggestY = 200;

        widthX = graphMaxX - spaceX;
        widthY = graphMaxY - spaceTop - spaceY;

        scaleX = (double) widthX / (double) biggestX;
        scaleY = (double) widthY / (double) biggestY;
        //System.out.println("TempoPanel.setValuesForPaint(): scaleX = "+scaleX);
    }

    /**
     * draw axis and tempo-curve
     * 
     * @param g
     * @author Wolfram Heyer
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
	public void paintComponent(Graphics g) {
        //g.setClip(0,0,getWidth(),getHeight());
        super.paintComponent(g);
        setValuesForPaint();
        paintAxis(g);
        paintCurve(g);
        if (cursorPosition != -1) {
			paintCursor(g, new TimedMetrical(
				cursorPosition,
				new Rational(metricalTimeLine.getMetricTime(cursorPosition)))
			); // draw cursor
		}
   }

    /**
     * draw X and Y axis
     * 
     * @param g
     * @author Wolfram Heyer
     */
    private void paintAxis(Graphics g) {
        int cnt, steps;
        long value;

        // X-axis
        g.setColor(java.awt.Color.BLACK);
        g.drawLine(0, spaceTop + widthY, graphMaxX, spaceTop + widthY);
        // draw arrow on X-axis
		g.drawLine(graphMaxX-5, spaceTop+widthY-5, graphMaxX, spaceTop+widthY);
		g.drawLine(graphMaxX-5, spaceTop+widthY+5, graphMaxX, spaceTop+widthY);

        if (realTimeIsSet) {
            g.setColor(Color.BLUE);
            intervall2 = TimeScale.getScaleValueX(scaleX, 100);
        } else {
            g.setColor(Color.GREEN.darker());
            intervall2 = TimeScale.getScaleValueX(scaleX, 50);
        }

        for (long x = 0; x < biggestX; x += intervall2) {

            g.drawLine(spaceX + (int) (x * scaleX), spaceTop + widthY, spaceX
	                    + (int) (x * scaleX), spaceTop + widthY + 5);

    		if (realTimeIsSet) {
                g.drawString(TimeStamp.toStringFormated(x),
                        spaceX + (int) (x * scaleX) - 10,
                        spaceTop + widthY + 16
                 );
            } else {
            	//System.out.println("TempoPanel.paintAxis(g): ToDo: Adapt to TimeSignature != 4/4");
                g.drawString(x + "",
                        spaceX + (int) (x * scaleX) - 10,
                        spaceTop + widthY + 16
                 );
            }

        }

        // Y-axis
        g.setColor(java.awt.Color.BLACK);
        g.drawLine(spaceX, spaceTop, spaceX, graphMaxY);
        g.drawLine(spaceX-5, spaceTop+5, spaceX, spaceTop);
        g.drawLine(spaceX+5, spaceTop+5, spaceX, spaceTop);
        g.setColor(colorBpm);
        g.drawString("b", 2, spaceTop + widthY - 30);
        g.drawString("p", 2, spaceTop + widthY - 20);
        g.drawString("m", 0, spaceTop + widthY - 10);

        steps = 5;
        cnt = steps;
        for (long y = 0; y < biggestY; y += biggestY / steps) {
            g.drawLine(spaceX - 5, spaceTop + (int) (widthY - y * scaleY),
                    spaceX, spaceTop + (int) (widthY - y * scaleY));
            value = y;
            g.drawString(value + "", spaceX + 5, spaceTop
                    + (int) (widthY - y * scaleY) + 5);
        }
    }

	/**
	 * @param thisMetricTime is the current metric time
	 * @return distance to go to the right in pixels
	 */
	private double calculateNextXStep (Rational thisMetricTime){
//		boolean debug = true;
		boolean debug = false;
		
		double abstand = 0;
		
		Rational nextMeasure = metricalTimeLine.getNextMeasure(thisMetricTime);
		Rational currentTimeSignature = metricalTimeLine.getCurrentTimeSignature(thisMetricTime);

		if (realTimeIsSet) {
			double xPosPixels =  metricalTimeLine.getTime(thisMetricTime) * scaleX;
			double newXPosPixels = metricalTimeLine.getTime(nextMeasure) * scaleX;
			abstand = (newXPosPixels - xPosPixels) / currentTimeSignature.getNumer();
		}
		else {
			abstand = scaleX / currentTimeSignature.getNumer();
		}

		if (debug) System.out.println("TempoPanel.calculateNextXStep: abstand = "+abstand);
		if (debug) System.out.println("TempoPanel.calculateNextXStep: scaleX = "+scaleX);
		if (debug) System.out.println();

		return abstand;
	}

   /**
     * draw tempo-curve (including graphical markers and units) in selected
     * scale (zoom)
     * 
     * @param g
     * @author Wolfram Heyer
     */
    private void paintCurve(Graphics g) {
        long tempo = (long) metricalTimeLine.getTempo(0);
        long oldTempo;
        double posX = 0;
        double oldPosX;
        Marker marker;

		Rational currentTimeSignature;

        for (Iterator iter = metricalTimeLine.iterator(); iter.hasNext();) {
            marker = (Marker) iter.next();

            if (marker instanceof TimedMetrical) {
                TimedMetrical tm = (TimedMetrical) marker;

                oldPosX = posX;

				posX = getXposition((TimedMetrical) marker);

                oldTempo = tempo;
                tempo = (long) metricalTimeLine.getTempo(tm.getTime());

                //System.out.println("posX="+posX+", tempo="+tempo);

                // if tempo could not be resolved (if the end of the
                // metricalTimeLine is reached), keep the old tempo
                if (tempo == -1)
                    tempo = oldTempo;

                g.setColor(java.awt.Color.BLACK);
                // X-connection
                g.drawLine(spaceX + (int)oldPosX, spaceTop
                        + (int) (widthY - oldTempo * scaleY), spaceX
                        + (int)posX, spaceTop
                        + (int) (widthY - oldTempo * scaleY));

                // Y-connection
                g.drawLine(spaceX + (int)posX, spaceTop
                        + (int) (widthY - oldTempo * scaleY), spaceX
                        + (int)posX, spaceTop
                        + (int) (widthY - tempo * scaleY));

                g.setColor(colorBpm);
                int rhombusSize = 3;
                // graphical markers
                g.drawLine(spaceX + (int)posX, spaceTop
                        + (int) (widthY - tempo * scaleY) - rhombusSize, spaceX
                        + (int)posX + rhombusSize, spaceTop
                        + (int) (widthY - tempo * scaleY));
                g.drawLine(spaceX + (int)posX, spaceTop
                        + (int) (widthY - tempo * scaleY) - rhombusSize, spaceX
                        + (int)posX - rhombusSize, spaceTop
                        + (int) (widthY - tempo * scaleY));
                g.drawLine(spaceX + (int)posX, spaceTop
                        + (int) (widthY - tempo * scaleY) + rhombusSize, spaceX
                        + (int)posX + rhombusSize, spaceTop
                        + (int) (widthY - tempo * scaleY));
                g.drawLine(spaceX +(int) posX, spaceTop
                        + (int) (widthY - tempo * scaleY) + rhombusSize, spaceX
                        + (int)posX - rhombusSize, spaceTop
                        + (int) (widthY - tempo * scaleY));

                // values
                g.drawString(tempo + "", spaceX + (int)posX,
                        spaceTop + (int) (widthY - tempo * scaleY) + 12);

            }
        }
    }

	/**
	 * Draws the cursor
	 */
	private void paintCursor(Graphics g, TimedMetrical tm) {
		double posX = getXposition(tm);
		g.setColor(cursorColour);
		g.drawLine(spaceX + (int) posX, 0, spaceX + (int) posX, getHeight());
	}

	private double getXposition(TimedMetrical tm){
		double posX = 0;
		double abstand = 0;
		int[] preWert = new int[4];
		int measures = 0;
		double remainder = 0;
		double beats = 0;

		if (realTimeIsSet)
			posX = tm.getTime() * scaleX;
		else {
			preWert = metricalTimeLine.toMeasureBeatRemainder(tm.getMetricTime());
			measures = preWert[0]-1;
			remainder = preWert[2] / preWert[3];
			abstand = calculateNextXStep(tm.getMetricTime());
			beats = (preWert[1]-1) * abstand;
				
			posX = ((measures  + remainder) * scaleX)+ beats;
		}

		return posX;
	}
    /**
     * dataChanged
     * 
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.datamanager.DataChangeEvent)
     */
    @Override
	public void dataChanged(DataChangeEvent e) {
        //		repaint();
        //	    dataChanged = true;
        SwingUtilities.invokeLater(new Thread() {
            @Override
			public void run() {
                revalidate();
                repaint();
            }
        });
        //		dataChanged = false;
    }

    /**
     * set realTime for the timeOrder affects: realTimeIsSet, maxX repaints the
     * display
     */
    public void setRealTimeOrder() {
        realTimeIsSet = true;
        biggestX = maxRealTime;
        //System.out.println("biggestX = " + biggestX);
        repaint();
    }

    /**
     * set metricTime for the timeOrder affects: realTimeIsSet, maxX repaints
     * the display
     */
    public void setMetricTimeOrder() {
        realTimeIsSet = false;
        biggestX = (long) maxMetricTime.toDouble();
		//System.out.println("biggestX = " + biggestX);
        repaint();
    }

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long timeMicros) {
		cursorPosition = timeMicros;
		repaint();
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	@Override
	public long getEndTime() {
		// we are only displaying and therefore not interested...
		return -1;
	}


}