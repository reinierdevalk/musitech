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
 * TODO
 * 
 * 
 * MarkerPanel
 * - MetricalTime Editor (has to inherit from AbstractSimpleEditor )
 *
 * MarkerPanel, paintBeats: leave out one line if lines happen to be too close together (like in TempoPanel)
 * 
 * Zoom is always x2, change this sometime.
 * 
 * TempoPanel: distance should be 30, 60, ... (not: 10, 20, 50, ...) or both
 * Use correct units: seconds, minutes, hours
 * 
 * TimeScale.setScaleValue(): implement second method
 * 
 * inherid from TestCase (like in MetricalTimelineTest)
 * 
 * thoughts about an Endmarker:
 * Should every piece of music have an endmarker?
 * special case: if only one (start-)marker was given, set a standard tempo (e.g. 120 bpm)
 */


package de.uos.fmt.musitech.time;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.change.IDataChangeManager;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.structure.form.gui.TimeScale;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A JPanel which displays the Markers of a MetricalTimeLine
 * @author Wolfram Heyer
*/
public class MarkerPanel extends JPanel implements HorizontalTimedDisplay, DataChangeListener, Timeable {

	// Colors for drawing the barlines
	private static final Color metricaltimeBarLine_ColourLight = java.awt.Color.GREEN.darker(); 
	private static final Color metricaltimeBarLine_ColourDark = java.awt.Color.LIGHT_GRAY; 
	private static final Color realtimeBarLine_ColourLight = java.awt.Color.BLUE; 
	private static final Color realtimeBarLine_ColourDark = java.awt.Color.LIGHT_GRAY; 
	private static final Color cursorColour = java.awt.Color.GRAY; 

	private MarkerControlPanel markerControlPanel;
	
	private long cursorPosition = -1;

	private MetricalTimeLine metricalTimeLine;
	private ArrayList markers = new ArrayList();
	private HashMap markerInfoDisplays = new HashMap();
	private HashMap markerIconDisplays = new HashMap();
	private boolean notFirstStart = false;

	private static final int spaceX = 15; // extra space in x-direction in coordinate system
	private static final int spaceY = 20; // extra space in y-direction in coordinate system

	private int zoomFaktor = 1; // current zoom-factor
	private boolean realTimeIsSet = true; // selected primary measure (realTime or metricTime)

	
	private long maxRealTime;	// physical time of the endmarker of this timeline
	private Rational maxMetricTime;	// metrical time of the endmarker of this timeline
	private long maxX; // maxRealTime or maxMetricTime in interdependence with realTimeIsSet

	// size of panel (in interdependence with zoomFaktor)
	private long panelSizeX_original = 800 - 10; // TODO: adjust dynamically to MeterDisplay -> getWidth();
	private long panelSizeX = panelSizeX_original;	// the actual panel size (panelSizeX_original * zoomFactor, see setZoom(int))
	private int panelSizeY = 50;
	private int halfY; // half of real Y-size of panel
	private double scaleX;	// the actual scale factor for X-values (metrical or physical time)

	// this component's MouseMotionListener
	private MouseMotionListener mml = new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent e) {
			boolean debug = false;
			//boolean debug = true;

			long pixelpos = (long) (e.getX() - spaceX);
			long timeMillisRealTime = 0;
			Rational timeMillisMetricTime = Rational.ZERO;
			Rational currentTimeSignature = Rational.ZERO;
			
			if (realTimeIsSet) {
				timeMillisRealTime = (long) ((pixelpos) / scaleX);
				if (timeMillisRealTime < 0) timeMillisRealTime = 0;
				timeMillisMetricTime = metricalTimeLine.getMetricTime(timeMillisRealTime);
				currentTimeSignature = metricalTimeLine.getCurrentTimeSignature(timeMillisMetricTime);
			}
			else {

			//measures	
				int measures = (int) (pixelpos / scaleX);
				double beatsRemainder = (double) (pixelpos % scaleX);

			//beats
				Rational thisMeasureMetricalTime = metricalTimeLine.fromMeasureBeatRemainder(new int[] {measures ,0,0,1});
				currentTimeSignature = metricalTimeLine.getCurrentTimeSignature(thisMeasureMetricalTime);
				double distanceBeatRealTime = (double) (scaleX / currentTimeSignature.getNumer());
				int beats = (int) ( beatsRemainder / distanceBeatRealTime);
				double remainderRealTime = (double)  ( beatsRemainder % distanceBeatRealTime);

			//remainder
				int remainderReal = (int) (remainderRealTime / scaleX);
				Rational remainder = new Rational((int)remainderRealTime, (int)scaleX);
				int remainder_num = remainder.getNumer();
				int remainder_den = remainder.getDenom();

				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): pixelpos = "+(long)pixelpos);
				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): scaleX = "+scaleX);
				if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): measures = "+measures);
				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): beatsRemainder = "+beatsRemainder);

				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): distanceMeasureRealTime = "+distanceMeasureRealTime);
				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): distanceBeatRealTime = "+distanceBeatRealTime);

				if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): beats = "+beats);
				if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): remainder_num = "+remainder_num);
				if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): remainder_den = "+remainder_den);
				//if (debug) System.out.println("MarkerPanel.MouseMotionListener.mouseMoved(MouseEvent e): timeMillisRealTime  = "+timeMillisRealTime );

				timeMillisMetricTime = metricalTimeLine.fromMeasureBeatRemainder(new int[] {measures, beats, 0, 1});
				//timeMillisMetricTime = metricalTimeLine.fromMeasureBeatRemainder(new int[] {measures, beats, remainder_num, remainder_den});
				timeMillisRealTime = metricalTimeLine.getTime(timeMillisMetricTime);
				if (debug) System.out.println();
			}
			
			
			markerControlPanel.setTimeLabels(timeMillisRealTime, timeMillisMetricTime, currentTimeSignature);
			
		}
	};

	public MarkerPanel(MetricalTimeLine metricalTimeLine) {
		this.metricalTimeLine = metricalTimeLine;

		generateMarkers();

		getMaxiValues(); // get maximum values of current metricalTimeLine (maxRealTime, maxMetricTime)

		// Editor
		enableEvents(AWTEvent.FOCUS_EVENT_MASK);
		IDataChangeManager changeManager = DataChangeManager.getInstance();
		changeManager.interestExpandElements(this, metricalTimeLine);

		// renew markers, if componentRezised or componentShown
		addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent e) {
				if (!markers.isEmpty())
					layoutMarkers(); //	renew markers
			}

			public void componentShown(ComponentEvent e) {
				if (!markers.isEmpty())
					layoutMarkers(); // renew markers
			}
		});

		this.addMouseMotionListener(mml);

		setLayout(null);
		setBackground(Color.WHITE);

		// everything else is done by the paintComponent() method

		/*
		 * store which markers are selected:
		 * 	- MarkerPanel has to register as a SelectionListener (hints: implements or inner class)
		 *  - SelectionChanged: if something changes, we are informed (hint: getSource()...)
		 * 		SelectionEvent informs us, which objects were added or removed
		 * 		ToDo: adjust selections for affected markers (goal: markers should show new symbol)
		*/

		SelectionManager.getManager().addListener(new SelectionListener() {
			public void selectionChanged(SelectionChangeEvent event) {

				for (Iterator iter = event.addedObjects.iterator(); iter.hasNext();) {
					Object marker = iter.next();
					MarkerIconDisplay mIDisplay = (MarkerIconDisplay) markerIconDisplays.get(marker);
					if (mIDisplay != null) {
						mIDisplay.setSelected(true);
					}
				}
				for (Iterator iter = event.removedObjects.iterator(); iter.hasNext();) {
					Object marker = iter.next();
					MarkerIconDisplay mIDisplay = (MarkerIconDisplay) markerIconDisplays.get(marker);
					if (mIDisplay != null) {
						mIDisplay.setSelected(false);
					}
				}
			}
		});

	}

	/**
	 * Get maximum values for realTime and metricTime (maxX).
	 * TODO unify with TempoPanel
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

		if (debug) System.out.println("MarkerPanel.getMaxiValues: maxMetricTime ="+maxMetricTime);
		if (debug) System.out.println();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setValuesForPaint(); // halfY, scaleValue, scaleX
		paintBeats(g); // draw barlines
		paintAxis(g); // draw axis
		if (cursorPosition != -1) {
			paintCursor(g, new TimedMetrical(
				(long)cursorPosition,
				new Rational(metricalTimeLine.getMetricTime((long)cursorPosition)))
			); // draw cursor
		}
		super.paintChildren(g);
	}

	/**
	 * sets the values for halfY and scaleX
	*/
	private void setValuesForPaint() {
		halfY = getHeight() / 2; // set halfY to half of real Y-size of panel
		panelSizeX = getWidth(); // just the X size of this panel 
		scaleX = (panelSizeX - spaceX) / (double) maxX;	//
		//System.out.println("MarkerPanel.setValuesForPaint(): scaleX = "+scaleX);
	}

	/**
	 * Draws axis and calculated values on axis
	 */
	private void paintAxis(Graphics g) {
		String msg;
		// draw axis
		g.setColor(java.awt.Color.BLACK);
		g.drawLine(0, halfY, (int) panelSizeX, halfY);
		
		long intervall2 = TimeScale.getScaleValueX(scaleX, 100);

		if (realTimeIsSet) {
			 g.setColor(Color.BLUE);
			 intervall2 = TimeScale.getScaleValueX(scaleX, 100);
		 } else {
			 g.setColor(Color.GREEN.darker());
			 intervall2 = TimeScale.getScaleValueX(scaleX, 50);
		 }

		 for (long x = 0; x < maxX; x += intervall2) {

			 g.drawLine(
			 	spaceX + (int) (x * scaleX),
			 	halfY,
			 	spaceX + (int) (x * scaleX),
				halfY + 10	//10
			 );

			 if (realTimeIsSet) {
				 g.drawString(
				 	TimeStamp.toStringFormated(x),
					spaceX + (int) (x * scaleX) - 10,
					halfY + 22	//40
				 );
			 } else {
				 g.drawString(
				 	"m."+x,
					spaceX + (int) (x * scaleX) - 10,
					halfY + 40	//40
				  );
			 }

		 }
	}
	
	/**
	 * draw barlines
	 */
	private void paintBeats(Graphics g) {
		int height = getHeight();	// height of markerPanel to use for height of barlines
		
		paintMetricalBarLines(g, height);
		paintRealtimeBarLines(g, height);
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
			double xPosPixels =  (double) (metricalTimeLine.getTime(thisMetricTime) * scaleX);
			double newXPosPixels = (double) (metricalTimeLine.getTime(nextMeasure) * scaleX);
			abstand = (double) ((newXPosPixels - xPosPixels) / currentTimeSignature.getNumer());
		}
		else {
			abstand = (double) (scaleX / currentTimeSignature.getNumer());
		}

		if (debug) System.out.println("MarkerPanel.calculateNextXStep: abstand = "+abstand);
		if (debug) System.out.println("MarkerPanel.calculateNextXStep: scaleX = "+scaleX);
		if (debug) System.out.println();

		return abstand;
	}
		
	// draw metricalTime barlines in green		
	private void paintMetricalBarLines(Graphics g, int height) {
		int fontHeight = (int) g.getFont().getLineMetrics("1", ((Graphics2D) g).getFontRenderContext()).getHeight();
		
		boolean debug = false;
//		boolean debug = true;

		// set metricDisplayTime to beginning of MetricalTimeLine
		Rational metricDisplayTime = metricalTimeLine.getNextOrSameMeasure(Rational.ZERO);
		Rational currentTimeSignature = metricalTimeLine.getCurrentTimeSignature(metricDisplayTime);

		int measureCount =0;
		double xPosPixels = 0;	// x-position of barline
	
		double abstand = 0;
		
		while ( xPosPixels < getWidth() ) {
			
			currentTimeSignature = metricalTimeLine.getCurrentTimeSignature(metricDisplayTime);
			if (debug) System.out.println("MarkerPanel.paintMetricalBarLines: measureCount = "+measureCount);
			if (debug) System.out.println("MarkerPanel.paintMetricalBarLines: metricDisplayTime = "+metricalTimeLine.toStringFormatted(metricDisplayTime)[0]);
			if (debug) System.out.println("MarkerPanel.paintMetricalBarLines: currentTimeSignature = "+currentTimeSignature.toString());

			abstand = calculateNextXStep(metricDisplayTime);

			// get value for xPosPixels (x position of barline)
			if (realTimeIsSet) {
				for (int i = 0; i < currentTimeSignature.getNumer(); i++) {
					if ( xPosPixels < getWidth() ) {
						if (i == 0) {
							g.setColor(metricaltimeBarLine_ColourLight);
							g.drawString(
								measureCount +"",
								spaceX + (int)xPosPixels + 1 -10,
								height - 2
							);
						}
						else {
							g.setColor(metricaltimeBarLine_ColourDark);
						}
						g.drawLine(spaceX + (int) xPosPixels, halfY, spaceX + (int) xPosPixels, height-fontHeight);
						xPosPixels = (double) (xPosPixels + abstand);
					}
			}

			} else {
				for (int i = 0; i < currentTimeSignature.getNumer(); i++) {
					if ( xPosPixels < getWidth() ) {
						if (i == 0) {
							g.setColor(metricaltimeBarLine_ColourLight);
							g.drawString(
								measureCount +"",
								spaceX + (int)xPosPixels + 1 -10,
								height - 2
							);
						} else {
							g.setColor(metricaltimeBarLine_ColourDark);
						}
						g.drawLine(spaceX + (int) xPosPixels, halfY, spaceX + (int) xPosPixels, height-fontHeight);
						xPosPixels = (double) (xPosPixels + abstand);
					}
				}
			}
			
			metricDisplayTime = metricalTimeLine.getNextMeasure(metricDisplayTime);
			measureCount++;
		}
		if (debug) System.out.println();
	}

	private double getXposition(TimedMetrical tm){
		double posX = 0;
		double abstand = 0;
		int[] preWert = new int[4];
		int measures = 0;
		double remainder = 0;
		double beats = 0;

		if (realTimeIsSet)
			posX = (double) (tm.getTime() * scaleX);
		else {
			preWert = metricalTimeLine.toMeasureBeatRemainder(tm.getMetricTime());
			measures = preWert[0]-1;
			remainder = (double) (preWert[2] / preWert[3]);
			abstand = calculateNextXStep(tm.getMetricTime());
			beats = (double) ((preWert[1]-1) * abstand);
				
			posX = (double)( ((measures  + remainder) * scaleX)+ beats);
		}

		return posX;
	}
	
	/**
	 * Draws the cursor
	 */
	private void paintCursor(Graphics g, TimedMetrical tm) {
		double posX = getXposition(tm);
		g.setColor(cursorColour);
		g.drawLine(spaceX + (int) posX, 0, spaceX + (int) posX, getHeight());
	}
	
	private long calculateXPosition(Rational tm) {
		long posX = 0;
		double abstand = 0;
		int[] preWert = new int[4];
		int measures = 0;
		double remainder = 0;
		double beats = 0;

		preWert = metricalTimeLine.toMeasureBeatRemainder(tm);
		measures = preWert[0]-1;
		remainder = (double) (preWert[2] / preWert[3]);
		abstand = calculateNextXStep(tm);
		beats = (double) ((preWert[1]-1) * abstand);
				
		posX = (long)( ((measures  + remainder) * scaleX)+ beats);

		return posX;
	}	


	// draw realTime barlines in blue		
	private void paintRealtimeBarLines(Graphics g, int height) {	
		boolean debug = false;
//		boolean debug = true;

		long step = 1000000;

		int fontHeight = (int) g.getFont().getLineMetrics("1", ((Graphics2D) g).getFontRenderContext()).getHeight();

		long xPosPixels = 0;	// x-position of barline

		Rational tm = Rational.ZERO;
		
		// set realDisplayTime to beginning (0)
		long realDisplayTime = 0;

		while (realDisplayTime < maxRealTime) {

			// get value for xPosPixels (x position of barline)
			if (realTimeIsSet) {
				xPosPixels = (long) (realDisplayTime * scaleX);
			} else {
				tm = new Rational( metricalTimeLine.getMetricTime((long) realDisplayTime) );
				xPosPixels = calculateXPosition(tm);

				if (debug) System.out.println("MarkerPanel.paintRealtimeBarLines: realDisplayTime = "+realDisplayTime);
		}

			// decide wether this barline is
			// on a time which can be divided by 2000 000 (set paint colour to blue and print out time) or
			// on another time (set paint colour to light_gray)
			if (realDisplayTime % (step*2) == 0) { // TODO: static values used here
				g.setColor(realtimeBarLine_ColourLight);
				String numberString = Long.toString(realDisplayTime / step); // and here
				g.drawString(
					numberString,
					spaceX + (int)xPosPixels + 1 -10,
					fontHeight-1);
			} else {
				g.setColor(realtimeBarLine_ColourDark);
			}
			
			// draw the barline at this x-position (xPosPixels)
			g.drawLine(spaceX + (int) xPosPixels, fontHeight, spaceX + (int) xPosPixels, halfY);
//			g.drawLine(spaceX + (int) xPosPixels, halfY, spaceX + (int) xPosPixels, height-fontHeight);
//			xPosPixels = (long) (xPosPixels + abstand);


			// go to the next relevant (1000 000 step) realTime
			realDisplayTime += step;
		}

		if (debug) System.out.println();
	}

	private void generateMarkers() {
		markers.clear();
		Marker marker;
		int wert = 0;

		setValuesForPaint();
		removeAll();

		if (metricalTimeLine == null) {
			return;
		}

		Collection changeInterest = new ArrayList();

		for (Iterator iter = metricalTimeLine.iterator(); iter.hasNext();) {
			marker = (Marker) iter.next();

			if (marker instanceof TimedMetrical) {
				markers.add(marker);
				changeInterest.add(marker.getMetricTime());

				// layout MarkerIconDisplay
				if (wert++ < 100000) {
					MarkerIconDisplay markerIconDisplay = new MarkerIconDisplay();
					markerIconDisplay.setMarkerPanel(this);
					markerIconDisplay.setMarkerControlPanel(markerControlPanel);
					// register the relevant markerIconDisplay for the actual marker in a map
					markerIconDisplays.put(marker, markerIconDisplay);

					// layout MarkerInfoDisplay
					MarkerInfoDisplay markerInfoDisplay = new MarkerInfoDisplay();
					markerInfoDisplay.setMarkerPanel(this);
					// register the relevant markerInfoDisplay for the actual marker in a map
					markerInfoDisplays.put(marker, markerInfoDisplay);
				}
			}
		}
		
		changeInterest.addAll(markers);
		DataChangeManager.getInstance().interestExpandElements(this, changeInterest);
	}


	/**
	 * layoutMarkers: renew positions of the markers
	 */
	private void layoutMarkers() {
		boolean debug = false;
//		boolean debug = true;

		Marker marker;

		MarkerIconDisplay markerIconDisplay; // do we really have to create a new object?
		MarkerInfoDisplay markerInfoDisplay;

		double posX = 0;

		setValuesForPaint();

		removeAll();
		
		// if we have no markers to paint
		if (markers == null) {
			return;
		}
		
		for (Iterator iter = markers.iterator(); iter.hasNext();) {
			marker = (Marker) iter.next();

			posX = getXposition((TimedMetrical) marker);

			// layout MarkerIconDisplay
			if (posX < 1000000) {
				
				if (debug) System.out.println("MarkerPanel.layoutMarkers: posX ="+posX);
				
				//find relevant markerIconDisplay for marker (MAP)
				markerIconDisplay = (MarkerIconDisplay) markerIconDisplays.get(marker);

				markerIconDisplay.setMarker(marker);
				markerIconDisplay.setBounds(spaceX + (int)posX - 12, halfY - 20, 20, 30);
				//System.out.println("A new markerIconDisplay is being made!");
				add(markerIconDisplay);
				
				// layout MarkerInfoDisplay
				//find relevant markerInfoDisplay for marker (MAP)
				markerInfoDisplay = (MarkerInfoDisplay) markerInfoDisplays.get(marker);

				markerInfoDisplay.doValues(marker, realTimeIsSet);
				Dimension dim = markerInfoDisplay.getPreferredSize();
				if (realTimeIsSet)
					markerInfoDisplay.setBounds(spaceX + (int)posX - 12, halfY + 30, dim.width, dim.height);
				else
					markerInfoDisplay.setBounds(spaceX + (int)posX - 12, halfY + 12, dim.width, dim.height);
				add(markerInfoDisplay);

				//repaint();
			}
		}
		if (debug) System.out.println();
		repaint();
	}
	/*******************************************************************************************************/

	/**
	 * set realTime for the timeOrder
	 * affects: realTimeIsSet, maxX
	 * repaints the display
	 */
	public void setRealTimeOrder() {
		realTimeIsSet = true;
		maxX = maxRealTime;
		revalidate();
	}

	/**
	 * set metricTime for the timeOrder
	 * affects: realTimeIsSet, maxX
	 * repaints the display
	 */
	public void setMetricTimeOrder() {
		realTimeIsSet = false;
		maxX = (long) maxMetricTime.toDouble();
		revalidate();
	}

	/**
	 * set zoom to given factor
	 * @param factor
	 * affects: zoomFaktor, panelSizeX 
	 * the panel gets resized to its new size (panelSizeX, panelSizeY)
	 * repaints the display
	 */
	public void setZoom(int factor) {
		zoomFaktor = factor;
		panelSizeX = panelSizeX_original * zoomFaktor;
		setPreferredSize(new Dimension((int) panelSizeX, panelSizeY));
		getParent().doLayout();
		layoutMarkers();
		repaint();
		revalidate();
	}

	/*******************************************************************************************************/

	/**
	 *	Gets the anacrusis of the current MetricalTimeline.
	 */
	private int getAnacrusis(MetricalTimeLine mTL) {
		int numerator = 0;
		int denominator = 0;
		int anacrusis = 0;

		//		markers.clear();
		Marker marker;

		for (Iterator iter = mTL.iterator(); iter.hasNext();) {
			marker = (Marker) iter.next();

			if (marker instanceof TimeSignatureMarker) {
				TimeSignatureMarker tSM = (TimeSignatureMarker) marker;

				TimeSignature timeSignature = tSM.getTimeSignature();
				numerator = timeSignature.getNumerator();
				denominator = timeSignature.getDenominator();
				anacrusis = (int) Math.round(tSM.getMetricTime().mul(denominator, 1).toDouble());
			}
		}
		//denominator = 2;	// testvalue
		return anacrusis;
	}

	/** 
	 * returns wether realTime is set or not
	 * @return
	 */
	public boolean getRealTimeIsSet() {
		return realTimeIsSet;
	}


	/** 
	 * If DataChangeEvent occured, set dataChanged=true.
	 * This affects processFocusEvent().
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	public void dataChanged(DataChangeEvent e) {
		dataChanged = true;
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				revalidate();
			}
		});
		dataChanged = false;
	}
	
	private boolean dataChanged = false;

	/** 
	 * Defines what happens when this panel gets the focus.
	 * If the data has changed (dataChanged=true), redraw everything.
	 * @see java.awt.Component#processFocusEvent(java.awt.event.FocusEvent)
	 */
	protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		if (dataChanged) {
			revalidate();
			dataChanged = false;
			repaint();
			//TODO this never occurs
			System.out.println("MarkerPanel.processFocusEvent(FocusEvent e) was called (repaint).");
		}
	}

	/** 
	 * revalidate
	 * gets new values and redraws the whole panel
	 */
	public void revalidate() {
		layoutMarkers();
		super.revalidate();
	}

	/**
	 * @return markerControlPanel
	 */
	public MarkerControlPanel getMarkerControlPanel() {
		return markerControlPanel;
	}

	/**
	 * makes the MarkerControlPanel known to this object
	 * @param panel
	 */
	public void setMarkerControlPanel(MarkerControlPanel panel) {
		markerControlPanel = panel;
	}

	/**
	 * @return the current scale factor for X
	 */
	public double getScaleX() {
		return scaleX;
	}
	
	public void destroy() {
		DataChangeManager.getInstance().removeListener(this);
	}






	/*
	 * methods for HorizontalTimedDisplay
	 * TODO implement methods
	 */

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long, de.uos.fmt.musitech.utility.math.Rational)
	 */
	public int getMinimalPositionForTime(long t, Rational m) throws WrongArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long, de.uos.fmt.musitech.utility.math.Rational, int)
	 */
	public boolean setMinimalPositionForTime(long t, Rational m, int position) throws WrongArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
	 */
	public long getNextPositioningTime(long startTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
	 */
	public void doInitialLayout() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return Returns the metricalTimeLine.
	 */
	public MetricalTimeLine getMetricalTimeLine() {
		return metricalTimeLine;
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
	 */
	public void setTimePosition(long timeMicros) {
		cursorPosition = timeMicros;
		repaint();
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	public long getEndTime() {
		// we are only displaying and therefore not interested...
		return -1;
	}
}
