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
 * Created on 07.01.2004
 */
package de.uos.fmt.musitech.audio.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import de.uos.fmt.musitech.audio.floatStream.DummyFloatIS;
import de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.editor.AbstractDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.time.gui.LinearDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * 
 * Aktuell: 23.06.04
 * 
 * Display Wavedata in a Panel.
 * 
 * TODO: vielleicht ist es sinvoll, ein Interface
 * zu machen, dem paintComponent(Graphics) sein Graphics übergibt.
 * Implementationen dieses Interface könnten dann Dinge auf das OnlineGraphic
 * schreiben. Vielleicht wär es sinnvoll auf diese Weise das malen beim
 * selectieren aus dieser Class WaveDisplay rauszunehmen (mir scheint es, als
 * gehöre es eigentlich nicht mit hier herein)
 * 
 * @author Nicolai Strauch
 */
public class WaveDisplay extends AbstractDisplay implements LinearDisplay, HorizontalTimedDisplay, Timeable {
	AudioFileObject audioFileObject;

	boolean haveFocus = true; // maybe quest "isFocusOwner()"? or "hasFocus()"?

	boolean toChange = true; // TODO

	private FloatPreviewReader fpr;

	private double displayScale = 1000;

	// TO DO: samplesOnX-change
	private int samplesOnX = 1000; // number of points paint on every x-position

	private float sPsToLoad = 1; // number of samples for any sample to load

	private int dataStartPos;

	private float[][] buffer; // so many samples as pixels can be displayed

	private int cursorPos; // position of the cursor in source

	// int for cursorposition - in the afo, or in the wave paint?
	// whith set/get TO DO: usefull to make more than one cursor or selection?
	private int displCursor; // cursor in Display

	private int selectionPos; // first selected sample in sourcedata

	private int selectionLen; // number of selected samples

	private boolean isSelected; // true if Selection was marked

	/**
	 *  
	 */
	public WaveDisplay() {
		super();
		setVZoomFactor(1); // by set the zoomfactor the data is paint, the
						   // buffers initialised.
		//DataChangeManager.getInstance().interestExpanded(this, TODO );
		// is this the correct way to register this DataChangeListener?
		paintWave();
	}

	/*
	 * - The MouseListeners do not will be set in this class, but from the
	 * outside. Set the actions to be performed by many useractions whith the
	 * mouse. Prepare the listeners to: set the cursor if any point in wave is
	 * clicked set a selectionarea, if the mouse is press and drag
	 *  
	 */
	// maybe it's usefull to put this method into an upperclass?
	//	private void setMouseListeners(){
	//		
	//	}
	private BufferedImage offscreenBuffer;

	private int width = 1; // from offscreenbuffer

	private int height = 1; // from offscreenbuffer - initialised by 1 to never
							// be zero (problems by initialising vZoomFactor)

	private Graphics offscreenGraphics;

//	private Graphics screenGraphics;

	private float vZoomFactor; // factor for vertical zoom: amplitude on graph

	/**
	 * Try to initiate Offscreenbuffer and offscreengraphic, and reinitialise
	 * dependend Variables also If them all is reinitialised (this is the case,
	 * if true is returned), it is recomended tu evoke paintOffscreenImage().
	 * Else nothing will occure in the buffers, and no image will appeare.
	 * 
	 * @return true if Offscreenbuffer is new instantiated, false else
	 */
	private boolean createOffscreenBuffer() {
		if (offscreenBuffer == null || width != getWidth() || height != getHeight()) {
			offscreenBuffer = (BufferedImage) createImage(getWidth(), getHeight());
			if (offscreenBuffer == null)
				return false;
			offscreenGraphics = offscreenBuffer.createGraphics();
			width = offscreenBuffer.getWidth();
			// TO DO: samplesOnX-change
			if (buffer == null || buffer[0].length != width * samplesOnX)
				buffer = new float[1][width * samplesOnX];
			vZoomFactor = vZoomFactor / height;
			height = offscreenBuffer.getHeight();
			vZoomFactor = vZoomFactor * height;
			return true;
		}
		return false;
	}

	/**
	 * Paint the wave from the data beginning at dataStartPos, the position in
	 * the given AudioPreviewReader.
	 * 
	 * This method try to obtain data from source. If not egnoth data is written
	 * into the float-array, ... this is ignored.
	 * 
	 * The read of data and the painting will occure in a Thread.
	 * If the Thread is reading and painting when this Method is evoked, 
	 * the Thread is stopped and than reaktivated (Thread.run() do not return,
	 * the stop and begin again occure in a while() in Thread.run().)
	 * 
	 * Evoked by change  of position, horizontal or vertical zoom, or by execute these changes.
	 * 
	 * @param pos -
	 *            beginpos of the Wave in samples of the source
	 */
	private synchronized void paintOffscreenImage() {

		System.out.println("WaveDisplay.paintOffscreenImage(): Begin whith Thread: "+(++paintThreadEvoking)+", threadIsPainting: "+threadIsPainting+", paintOffscreenImage: "+paintOffscreenImage);
		
		toChange = false; // all changes will now be executed
		
		// initialise paintThread, if necessary
		if (paintThread == null) {
			paintThread = new PaintThread();
			paintThread.setPriority(2);
			paintThread.setName("PaintThread");
			paintOffscreenImage = true;
			paintThread.start();
			return;
		}
		
		if (ptActivationThread == null) {
			ptActivationThread = new PTActivationThread();
			ptActivationThread.setPriority(2);
			ptActivationThread.setName("ptActivationThread");
			paintOffscreenImage = true;
			ptActivationThread.start();
			return;
		}
		// if thread is painting, but need not paint
//		if(threadIsPainting && !paintOffscreenImage){
			System.out.println("WaveDisplay.paintOffscreenImage(): wake activationThread up from waiting of paintThread number: "+paintThread.id);
			// wake ptActivationThead up.
			synchronized (this) {
				this.notifyAll();
			}
//		}else{
			System.out.println("WaveDisplay.paintOffscreenImage(): wake up ptActivationThread.");
			synchronized (ptActivationThread) {
				ptActivationThread.notifyAll();
			}
//		}
	}
	
	private PTActivationThread ptActivationThread;
	
	class PTActivationThread extends Thread{
		public void run(){
			while(!endPaintThread){ // stop whith paintThread 
				// if Thread is painting, stop them
				if (threadIsPainting) {
					System.out.println("WaveDisplay.PTActivationThread.run(): try to stop Thread from ID: "+paintThread.id);
					fpr.stopPreviewRead();
					paintOffscreenImage = false;
//					while(threadIsPainting){
					// wait that the Thread painting really stop.
					synchronized (WaveDisplay.this) {
						try {
							//Thread.sleep(200);
							WaveDisplay.this.wait(); 
							System.out.println("WaveDisplay.PTActivationThread.run(): return from wait of stopping PaintThread. Aktual PaintThread: "+paintThread.id);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
//					}
				}
				// if Thread is not painting, it is waiting and must wake up
				if(!threadIsPainting){
					// set flags for paintThread paint, and let them wake up.
					paintOffscreenImage = true;
					synchronized (paintThread) {
						System.out.println("WaveDisplay.PTActivationThread.run(): try to wake up paintThread: "+paintThread.id);
						paintThread.notifyAll();
					}
				}
				// wait to activate paintThread again
				synchronized (ptActivationThread){
					try {
						wait();
						System.out.println("WaveDisplay.PTActivationThread.run(): ptActivationThread returning from wait(). Current running paintThread: "+paintThread.id);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	int paintThreadEvoking = 0;

	private PaintThread paintThread;

	private boolean paintOffscreenImage = false; // true if OffscreenImage have
												 // to be repaint.

	private boolean threadIsPainting = false;

	private boolean endPaintThread = false; // true if paintThread.run() have to
											// end

	class PaintThread extends Thread {
		int id;

		public void run() {
			while (!endPaintThread) {
				paintOffImage();
				threadIsPainting = false;
				synchronized (WaveDisplay.this) {
					System.out.println("WaveDisplay.PaintThread.run(): let PTAktivationThread wake up from waiting of paintThread stop. This ID:"+id);
					WaveDisplay.this.notifyAll();
				}
				if (!paintOffscreenImage) {
					synchronized (paintThread) {
						try {
//							while(!paintOffscreenImage){
//								sleep(150);
//							}
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void paintOffImage() {
			// TO DO: samplesOnX-change
			threadIsPainting = true;
			id = paintThreadEvoking;
			System.out.println("WaveDisplay.PaintThread.paintOffImage() begin. ID: " + id);
			fpr.setSampleRateRatio(sPsToLoad);
			if (!paintOffscreenImage) {
				threadIsPainting = false;
				return;
			}
			fpr.previewRead(buffer, dataStartPos);
			if (!paintOffscreenImage) {
				threadIsPainting = false;
				return;
			}

			offscreenGraphics.clearRect(0, 0, width, height);
			offscreenGraphics.setColor(Color.white);
			offscreenGraphics.fillRect(0, 0, width, height);
			offscreenGraphics.setColor(Color.black);
			offscreenGraphics.drawLine(0, height / 2, width, height / 2);
			float zeroLevel = height / 2;

			if (!paintOffscreenImage) {
				threadIsPainting = false;
				return;
			}
//////>>>>>>>>> whithout samplesOnX TO DO: samplesOnX-change
//			if(displayScale >= 16){ // than the wave is reflect and filled,
// to give a condensed impression
//				for (int i = 0; i < buffer[0].length - 1; i++) {
//					offscreenGraphics.drawLine(
//							i,
//							(int) (buffer[0][i] * -vZoomFactor + zeroLevel),
//							i + 1,
//							(int) (buffer[0][i] * vZoomFactor + zeroLevel));
//// offscreenGraphics.drawPolygon(
//// new int[]{i, i, i+1, i+1},
//// new int[]{(int) (buffer[0][i] * -vZoomFactor + zeroLevel),
// (int) (buffer[0][i+1] * vZoomFactor + zeroLevel), (int)
// (buffer[0][i] * -vZoomFactor + zeroLevel), (int) (buffer[0][i+1]
// * vZoomFactor + zeroLevel)},
//// 4);
//				}
//			}else{
//				for (int i = 0; i < buffer[0].length - 1; i++) {
//					offscreenGraphics.drawLine(
//							i,
//							(int) (buffer[0][i] * -vZoomFactor + zeroLevel),
//							i + 1,
//							(int) (buffer[0][i + 1] * -vZoomFactor + zeroLevel)
//					);
//				}
//			}
//////<<<<<<<<<< end "whithout samplesOnX"
//>>>>>>>>>> with samplesOnX
			for (int ii = 0, i = 0; i < buffer[0].length - 1; ii++, i += samplesOnX) {
				for (int j = 0; j < samplesOnX && i + j < buffer[0].length - 1; j++)
					offscreenGraphics.drawLine(ii, (int) (buffer[0][i + j] * -vZoomFactor + zeroLevel),
							j == samplesOnX - 1 ? ii + 1 : ii, (int) (buffer[0][i + j + 1] * -vZoomFactor + zeroLevel));
			}
//<<<<<<<<<< end "with samplesOnX"

			offscreenGraphics.drawLine(0, height - 2, width, height - 2);

			for (int i = 0; i < width; i += 120) {
				offscreenGraphics.drawLine(i, height - 2, i, height - 9);
				offscreenGraphics.drawString(String.valueOf((int) ((i * displayScale) + dataStartPos)), i, height - 10);
			}
			setCursorInDisplay();
			if (!paintOffscreenImage) {
				threadIsPainting = false;
				return;
			}
			repaint(); // TODO: is it not dangerous evoke repaint from this Thread?
			if (isSelected)
				paintSelection();
			System.out.println("WaveDisplay.PaintThread.paintOffImage() end. ID: " + id);
			threadIsPainting = false;
			paintOffscreenImage = false;
		}
	}

	/**
	 * Test if this WaveDisplay heve the focus. If it have the focus, create the
	 * offscreengraphics, and paint on them.
	 *  
	 */
	void paintWave() {
		// toChange will be set to false in paintOffscreenImage().
		if (haveFocus) {
			//if(hasFocus()){
			createOffscreenBuffer();
			if (offscreenBuffer != null)
				paintOffscreenImage();
		}
	}

	public synchronized void paintComponent(Graphics g) {
//		screenGraphics = g;
		if (createOffscreenBuffer())
			paintOffscreenImage();
		Rectangle clip = g.getClipBounds();
		if (clip != null) {
			int x = clip.x;
			int y = clip.y;
			int x2 = clip.x + clip.width;
			int y2 = clip.x + clip.height;
			g.drawImage(offscreenBuffer, x, y, x2, y2, x, y, x2, y2, this);
			if (x <= displCursor && displCursor < x2) {
				g.setXORMode(Color.green);
				g.drawLine(displCursor, y, displCursor, y2);
			}
		} else {
			g.drawImage(offscreenBuffer, 0, 0, this);
			if (displCursor >= 0) {
				g.setXORMode(Color.green);
				g.drawLine(displCursor, 0, displCursor, offscreenBuffer.getHeight());
			}
		}

		if (isSelecting) {
			g.setXORMode(Color.orange);
			g.fillRect(selectingFrom, 0, selectingLen, height);
		}

	}

	/**
	 * @return
	 */
	public int getDataStartPos() {
		return dataStartPos;
	}

	/**
	 * @return
	 */
	public double getDisplayScale() {
		return displayScale;
	}

	/**
	 * Set data start pos.
	 * 
	 * Call methode paintWave() to update changes, to paint wave whith the new change.  
	 * 
	 * @param i
	 */
	public synchronized boolean setDataStartPos(int i) {
		toChange = true;
		if(dataStartPos==i)
			return false;
		dataStartPos = i;
		return true;
		// paintWave();
	}

	/**
	 * Set number of samples per pixel on x-axis. 
	 * 
	 * Call methode paintWave() to update changes, to paint wave whith the new change.  
	 * 
	 * @param d
	 */
	public synchronized void setDisplayScale(double d) {
		toChange = true;
		displayScale = d;
		// >>>>>>>>>>>>>>>>>> TO DO samplesOnX-Changing
		samplesOnX = 1; // TODO: set this value :-(
		if (samplesOnX > displayScale)
			samplesOnX = (int) displayScale;
		if (buffer[0].length < width * samplesOnX)
			buffer = new float[1][width * samplesOnX];
		//<<<<<<<<<<<<<< end samplesOnX-Changing
		sPsToLoad = (float) (displayScale / samplesOnX);
		if (sPsToLoad < 1)
			sPsToLoad = 1;
		// paintWave();
	}

	// get number of available samples (evoke afr)

	/**
	 * @return
	 */
	public AudioFileObject getAudioFileObject() {
		return audioFileObject;
	}

	public FloatPreviewReader getFloatPreviewReader() {
		return fpr;
	}

	/**
	 * @param object
	 * @throws IOException
	 * @throws IOException
	 */
	public void setAudioFileObject(AudioFileObject argAudioFileObject) {
		this.audioFileObject = argAudioFileObject;
		try {
			setFloatPreviewReader(this.audioFileObject.getFloatPreviewReader());
		} catch (IOException e) {
			e.printStackTrace();
			setFloatPreviewReader(new DummyFloatIS());
		}
	}

	public void setFloatPreviewReader(FloatPreviewReader floatPreviewReader) {
		fpr = floatPreviewReader;
		setDataStartPos(0);
		paintWave();
	}

	/**
	 * Set the faktor for vertikal Zoom: 
	 * 
	 * Call methode paintWave() to update changes, to paint wave whith the new change.  
	 * 
	 * @param f
	 */
	public synchronized void setVZoomFactor(float f) {
		toChange = true;
		vZoomFactor = ((float) height / 2) * f;
		// paintWave();
	}

	/**
	 * @return cursorpos in the sourcedata
	 */
	public int getCursorPos() {
		return cursorPos;
	}

	/**
	 * Set cursorposition in source-data, and evoke setCursor(), to calculate
	 * the cursorposition on display. Set the cursor visible on the display.
	 * TODO Paint the cursor imediatly, whithout quest fokus.
	 * 
	 * @param pos -
	 *            sample to mark as cursor
	 */
	public synchronized void setCursorPos(int pos) {
		cursorPos = pos;
		setCursorInDisplay();
		if (displCursor >= 0) {
			repaint(displCursor - 1, 0, 3, offscreenBuffer.getHeight());
		}
	}

	// TODO : search for fokus-quest in all metods above

	/**
	 * set the cursorposition in source to the sample at time micros, in
	 * microseconds
	 * 
	 * @param timeMicros
	 */
	public synchronized void setCursorPosTime(long timeMicros) {
		timeMicros *= fpr.getFormat().getFrameRate();
		timeMicros /= 1000000;
		setCursorPos((int) timeMicros);
	}

	/**
	 * 
	 * @return true if the cursorposition in data appear on the display
	 */
	public synchronized boolean isCursorInDisplayrange() {
		int displCursorPos = cursorPos - dataStartPos;
		displCursorPos /= displayScale;
		return displCursorPos >= 0 && displCursorPos <= width;
	}

	/**
	 * get the microsecond from the sample at that the cursor is
	 * 
	 * @return
	 */
	public synchronized long getCursorMicrosec() {
		//System.out.println("calculate cursortime: cursorPos = "+cursorPos);
		long microsec = (long) getCursorPos() * 1000000;
		//System.out.println("calculate cursortime: returning: "+microsec);
		microsec /= fpr.getFormat().getFrameRate();
		//System.out.println("calculate cursortime:
		// frameRate:"+getAudioFileObject().getFormat().getFrameRate());
		//System.out.println("calculate cursortime: returning: "+microsec);
		return microsec;
	}

	/**
	 * set cursorposition in Display, and calculate the respectiv position in
	 * sourcedata, setting them cursor too. Do not use setCursorPos(int) or
	 * setCursor(). Allready repaint the cursor, it is not necessary to evoke
	 * repaint() again to set visible the cursor.
	 * 
	 * @param pos
	 */
	public synchronized void setCursorInDisplay(int pos) {
		if (displCursor >= 0) {
			repaint(displCursor - 1, 0, 3, offscreenBuffer.getHeight());
		}
		displCursor = pos;
		if (displCursor >= 0) {
			repaint(displCursor - 1, 0, 3, offscreenBuffer.getHeight());
		}
		pos *= displayScale;
		pos += dataStartPos;
		cursorPos = pos;
	}

	/**
	 * Remove the old Cursorline from display, if them was paint. Set the
	 * cursor. Recalculate the aktual position of the cursor in the display. Do
	 * not set the cursor visible.
	 */
	private synchronized void setCursorInDisplay() {
		if (displCursor >= 0) {
			repaint(displCursor - 1, 0, 3, offscreenBuffer.getHeight());
		}
		displCursor = cursorPos - dataStartPos;
		displCursor /= displayScale;
		if (displCursor > width) {
			displCursor = -1; // so it not will be paint.
		}
	}

	/**
	 * Only paint selection whith the selectionvalues selectionPos and
	 * selectionLen, if the selected samples are in the displayrange. Older
	 * selections at display, if them are displayed allready, do not are
	 * demarked on display. If this selection allready was marked, and
	 * offscreenGraphics do not was repaint, the selection will be demarked on
	 * display. To make a selection, set selectionPos and selectionLen, and then
	 * invoke this method. To demark the selection on display, only invoke this
	 * method again, whithout change the selectionparameters. To make a new
	 * selection, invoke this method before change the selectionvalues, to
	 * demark the old selection on Display. Or use setNewSelection(int, int).
	 */
	public void paintSelection() {
		//		int sPosDispl = selectionPos - dataStartPos;
		//		// if the selected samples not on the display, but on them right side
		//		if(sPosDispl > width*displayScale) return;
		//		int sEndDispl = (int)((selectionLen+sPosDispl) // samples start at
		// fisrt position on display
		//									/displayScale); // by the displayscale get the endposition on display
		//		// if the selected samples not on the display, but on them left side
		//		if(sEndDispl<1) return;
		//		if(sPosDispl<0) // if the selection overlap at the left rand of
		// Display
		//			sPosDispl = 0;
		//		else
		//			sPosDispl /= displayScale;
		//		if(sEndDispl>width-sPosDispl) // if the selection overlap on the
		// right rand of display
		//			sEndDispl = width-sPosDispl;
		//		
		//		offscreenGraphics.setXORMode(Color.blue);
		//		offscreenGraphics.fillRect(sPosDispl, 0, sEndDispl - sPosDispl,
		// offscreenBuffer.getHeight());
		//		repaint(sPosDispl, 0, sEndDispl - sPosDispl,
		// offscreenBuffer.getHeight());
		int sPosDispl = (int) ((selectionPos - dataStartPos) / displayScale);
		int sEndDispl = (int) (selectionLen / displayScale + sPosDispl);
		paintSelectionOnDisplay(sPosDispl, sEndDispl);
	}

	/**
	 * remove old selection from display, if any was on them, and set new
	 * Selection on the Display.
	 * 
	 * @param sPos -
	 *            first sample t be selected
	 * @param sLen -
	 *            number of selected samples
	 */
	public synchronized void setNewSelection(int sPos, int sLen) {
		paintSelection();
		setSelection(sPos, sLen);
		paintSelection();
	}

	/**
	 * Set a selection between the given positon on the display. (do not remove
	 * old painted selections. Use paintNewSelectionOnDisplay(int, int)) Do not
	 * actualise selectionPos and selectionLen. The order of Selection (from
	 * right to left, or inverse) is not important (endX <beginX is possible
	 * too) if(end <0 || start>width) return; synchronized - no two method can
	 * change Offscreenbuffer at the some Time.
	 * 
	 * @param x1
	 *            and x2 are positions on the display
	 */
	public synchronized void paintSelectionOnDisplay(int x1, int x2) {
		int start = x1 < x2 ? x1 : x2;
		int end = x1 > x2 ? x1 : x2;
		if (end < 1 || start > width)
			return;
		if (start < 0)
			start = 0;
		if (end > width)
			end = width;

		offscreenGraphics.setXORMode(Color.blue);
		offscreenGraphics.fillRect(start, 0, end - start, height);
		repaint(start, 0, end - start, height);
		offscreenGraphics.setPaintMode();
	}

	/**
	 * remove old selection from display, set selectionPos and selectionLen (the
	 * values given do not are samples, only displaypositions.) paint new
	 * selection given.
	 * 
	 * @param x1
	 *            and x2 are positions on the display
	 */
	public synchronized void paintNewSelectionOnDisplay(int x1, int x2) {
		paintSelection();
		setSelection((int) ((x1 < x2 ? x1 : x2) * displayScale + dataStartPos),
				(int) ((x1 > x2 ? (x1 - x2) : (x2 - x1)) * displayScale));
		paintSelectionOnDisplay(x1, x2);
	}

	private boolean isSelecting;

	private int selectingFrom; // first position selected on display

	private int selectingLen; // width of selection on display

	/**
	 * Mark a selection between positions x1 and x2 on the screen, demarking old
	 * selection. (Onlinegraphics) To finalise selection, evoke
	 * finalizeSelection(int, int).
	 * 
	 * @param x1
	 *            and x2 are positions on the display
	 */
	public void makingSelection(int x1, int x2) {
		isSelecting = true;
		repaint(selectingFrom, 0, selectionLen, height); // remove old mark
		selectingFrom = x1 < x2 ? x1 : x2;
		selectingLen = (x1 > x2 ? x1 : x2) - selectingFrom;
		repaint(selectingFrom, 0, selectionLen, height);
	}

	public void finalizeSelection(int x1, int x2) {
		repaint(selectingFrom, 0, selectionLen, height);
		isSelecting = false;
		paintNewSelectionOnDisplay(x1, x2);
	}

	/**
	 * Return selection int[0] : first sample selected int[1] : number of
	 * samples selected beginning at int[0]
	 * 
	 * @return selection pos and len, or null if no selection was marked
	 */
	public int[] getSelection() {
		if (!isSelected)
			return null;
		return new int[] { selectionPos, selectionLen };
	}

	/**
	 * Do not paint the selection on display, only remember them. The selection
	 * is painted by the next evocation of paintOffscreenbuffer, or by evoking
	 * paintSelection().
	 * 
	 * @param pos -
	 *            first sample to select
	 * @param len -
	 *            number of samples to marl as selected, begining at pos
	 */
	public void setSelection(int pos, int len) {
		isSelected = true;
		selectionPos = pos;
		selectionLen = len;
	}

	public void removeSelection() {
		isSelected = false;
	}

	/**
	 * 
	 * @return number of samplesrepresented on the display
	 */
	public int getSamplesWidth() {
		return (int) (width * displayScale);
	}

	/**
	 * Get the number of microseconds from the source availablefor the display.
	 * 
	 * @return the end time in microseconds.
	 */
	public long getEndInMikroseconds() {
		return fpr.availableMikroseconds();
	}

	/**
	 * Set the number of Microseconds for any pixel on x-axis.
	 * 
	 * @see de.uos.fmt.musitech.time.gui.LinearDisplay#setMicrosPerPix(double)
	 */
	public void setMicrosPerPix(double microsPPix) {
		double valInSamples = microsPPix * fpr.getFormat().getFrameRate() / 1000000;
		setDisplayScale(valInSamples);
		paintWave();
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.LinearDisplay#setOffset(long)
	 */
	public void setOffset(long micros) {
		int valInSamples = (int) (micros * fpr.getFormat().getFrameRate() / 1000000);
		setDataStartPos(valInSamples);
		paintWave();
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.LinearDisplay#updateDisplay()
	 */
	public void updateDisplay() {
		repaint();
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.LinearDisplay#getEndTime()
	 */
	public long getEndTime() {
		return getEndInMikroseconds();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
	 */
	public void destroy() {
		super.destroy();
		// stopping and ending paintThread
		endPaintThread = true; // go out from while(...) in paintThread.run()
		if (threadIsPainting) {
			fpr.stopPreviewRead();
			paintOffscreenImage = false;
		} else {
			synchronized (paintThread) {
				paintThread.notifyAll();
			}
			synchronized (this) {
				notifyAll();
			}
			synchronized (ptActivationThread) {
				ptActivationThread.notifyAll();
			}
		}

	}

	public void dataChanged(DataChangeEvent dataChangeEvent) {
		paintWave();
	}

	/**
	 * Do nothing, all changes are executed imidiatly.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
	 */
	public void focusReceived() {
		//		haveFocus = true;
		//		if(toChange || externalChanges()){
		//			createOffscreenBuffer();
		//			if(offscreenBuffer!=null)
		//				paintOffscreenImage();
		//		}
	}

	//	/**
	//	 * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
	//	 */
	//	public boolean isFocused() {
	//		// TODO Auto-generated method stub
	//		return haveFocus;
	//		//return hasFocus();
	//	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
	 */
	public void createGUI() {
		if (getPropertyValue() != null && getPropertyValue() instanceof AudioFileObject) {
			setAudioFileObject((AudioFileObject) getPropertyValue());
		} else if (getEditObj() instanceof AudioFileObject)
			setAudioFileObject((AudioFileObject) getEditObj());
		paintWave();
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long,
	 *      de.uos.fmt.musitech.utility.math.Rational)
	 */
	public int getMinimalPositionForTime(long t, Rational m) throws WrongArgumentException {
		if(t == Timed.INVALID_TIME){
			if(m!=null){
				MetricalTimeLine tLine = audioFileObject.getContext().getPiece().getMetricalTimeLine();
				t = tLine.getTime(m);
			}
			else
				throw new WrongArgumentException("WaveDisplay.getMinimalPositionForTime(...): both Arguments are invalid.");
			
		}
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long,
	 *      de.uos.fmt.musitech.utility.math.Rational, int)
	 */
	public boolean setMinimalPositionForTime(long t, Rational m, int position) throws WrongArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
	 */
	public long getNextPositioningTime(long startTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
	 */
	public void doInitialLayout() {
		// TODO Auto-generated method stub

	}
	
	
	public Dimension getPreferredSize(){
	    //TODO verbessern
	    int prefWidth = (int)(audioFileObject.getLengthInSamples()/samplesOnX);
	    return new Dimension(prefWidth, 250);
	}
	
	public void setTimePosition(long timeMicros) {
		setCursorPosTime(timeMicros);
//		scrollToCursorPos(2);
//		if(!isCursorInDisplayrange())
//			scrollToPos(getCursorPos());		
	}
	
	

}