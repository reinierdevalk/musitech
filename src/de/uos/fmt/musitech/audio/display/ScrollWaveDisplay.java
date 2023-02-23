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

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader;
import de.uos.fmt.musitech.data.time.Timeable;

/**
 * Show and controll a WaveDisplay
 * @author Nicolai Strauch
 */
public class ScrollWaveDisplay extends JPanel implements Timeable {
	
	WaveDisplay displayWave;
	// the scrollbar will have so many positions as samples are available in source.
	// if this is changed, please change many methods that depend from this caracteristic.
	// like setCursorPos(int)
	JScrollBar scrollBar; 
	JLabel barPosLabel;
	
	private int width;
	
//	private double proportion;	// samples in source by every pound of scrollbar
	/**
	 * 
	 */
	public ScrollWaveDisplay() {
		super();
		setLayout(new BorderLayout());
		displayWave = new WaveDisplay();
		scrollBar = new JScrollBar();
		scrollBar.setOrientation(Adjustable.HORIZONTAL);
		barPosLabel = new JLabel("BarPos");
		barPosLabel.setPreferredSize(new Dimension(150, scrollBar.getHeight()));
//		scrollBar.setBlockIncrement(50000);
		add(displayWave, BorderLayout.CENTER);
		JPanel sBarPanel = new JPanel();
		sBarPanel.setLayout(new BoxLayout(sBarPanel, BoxLayout.LINE_AXIS));
		sBarPanel.add(barPosLabel);
		sBarPanel.add(scrollBar);
		add(sBarPanel, BorderLayout.SOUTH);
		setListeners();
	}

//	boolean mousePressScrb = false;
	/**
	 * Set the listeners of the JScrollBar, to perform the rhigth actions
	 * if the Bar is moved. Then the WaveDisplay must be changed.
	 *
	 */
	private void setListeners(){
		scrollBar.addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
////				displayWave.setDataStartPos((int) (scrollBar.getValue()*proportion));
//				displayWave.setDataStartPos(scrollBar.getValue());
//				displayWave.repaint();
	//			if(!mousePressScrb){
//System.out.println("ScrollWaveDisplay.setListeners: mouseDragged: scrollbarvalue = "+scrollBar.getValue());
					//displayWave.setDataStartPos(scrollBar.getValue());
					if(displayWave.setDataStartPos((int) (scrollBar.getValue() * displayWave.getDisplayScale())));
//					scrollBar.setValue((int) (displayWave.getDataStartPos()/displayWave.getDisplayScale()));
						displayWave.paintWave();
					//displayWave.repaint();  // allready executed in paintWave().
		//		}
				barPosLabel.setText(String.valueOf((int)(scrollBar.getValue() * displayWave.getDisplayScale())));
			}});
		scrollBar.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
//				mousePressScrb = true;
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
//System.out.println("ScrollWaveDisplay.setListeners: mouseReleased: scrollbarvalue = "+scrollBar.getValue());
				//displayWave.setDataStartPos(scrollBar.getValue());
				if(displayWave.setDataStartPos((int) (scrollBar.getValue() * displayWave.getDisplayScale())))
					displayWave.paintWave();
				scrollBar.setValue((int) (displayWave.getDataStartPos() / displayWave.getDisplayScale()));
				// displayWave.repaint();// allready executed in paintWave();
				barPosLabel.setText(String.valueOf((int)(scrollBar.getValue()*displayWave.getDisplayScale())));
//				mousePressScrb = false;
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(width != getWidth()){
			width = getWidth();
	//		proportion = (double)displayWave.getAudioFileObject().available() / width;
	//		proportion = (double)displayWave.getAudioFileObject().available() / 100;
	//		scrollBar.setMaximum((int) (displayWave.getAudioFileObject().available()/displayWave.getDisplayScale() - (displayWave.getWidth()*displayWave.getDisplayScale())));
	//		scrollBar.setMaximum((int) (displayWave.getAudioFileObject().available()));
			setScrollBarSize();
		}
	}
	
	/**
	 * Set the minimum of the scrollbar to 1,
	 * them maximum of the number of samples disponible,
	 * the position of the indicator to the position relative to the position 
	 * of data displayd (Everything correspond the indicatorposition whith the 
	 * first sample displayed),
	 * the size of the indicator (the size of the indicator correspond whith the
	 * number of samples displayed. If 50% of all samples disponible are displayed,
	 * the indicator occupe 50% of the scrollbar, and so on.) 
	 *
	 */
	private void setScrollBarSize(){
//		int newMin = 0;
//		int newMax = (int) (displayWave.getAudioFileObject().available() );
//		int newExtend = (int) ( displayWave.getDisplayScale() * displayWave.getWidth() );  // the length of the indicator
//		int newValue = (int) (displayWave.getDataStartPos()); // / displayWave.getDisplayScale());
		int newMin = 0;
		int newMax = (int) (displayWave.getFloatPreviewReader().available()/displayWave.getDisplayScale() );
		int newExtend = displayWave.getWidth();  // the length of the indicator
		int newValue = (int) (displayWave.getDataStartPos()/displayWave.getDisplayScale());
		if(newMax<=0){
			 newMax = newMin; 
			 newValue = newMin;
		}
		if(newExtend>=newMax-newValue)
			newExtend = newMax-newValue;
//System.out.println("ScrollWaveDisplay.setScrollBar{ scrollBar.setValues("+newValue+", "+newExtend+", "+newMin+", "+newMax+"); }");
		scrollBar.setValues(newValue, newExtend, newMin, newMax);
	}
	
	public float setVerticalZoom(float factor){
		displayWave.setVZoomFactor(factor);
		displayWave.paintWave();
		return factor;
	}
	
	/*
	 * 
	 * 
	 * If the factor is to great, so that the Wave to be displayed no more
	 * fill the disponible screen, the greatest factor possible by fill all
	 * the screen is calculated and used. 
	 *** 
	 *
	 * The zoom-factor is rastered to the next value 2^n.
	 * (5 is rastered to 8, 31 to 32, and so on)
	 *
	 *not more rastered (code is comented; the user have to raster it.)
	 *
	 *//**
	 *  The value realy used to zoom is returned 
	 * 
	 * @param factor
	 * @return
	 */
	public double setHorizontalZoom(double factor){
		if(factor<1) factor = 1;
		
// rasterung ausgeschaltet
//		if(factor<=1.4){	// 1.4 willkürlich
//			displayWave.setDisplayScale(1);
//			setScrollBarSize();
//			return factor;
//		}
//		long availableSamples = displayWave.getAudioFileObject().available();
////		double maxFactor = (double)availableSamples
////							/ displayWave.getWidth();

//System.out.println("ShovWave.setHorizontalZoom() correct factor = "+factor);
//		// now we have the correct factor.
//		// this factor now will be rastered to a value 2^n
//		int nextRasterPos = 1 << (int)(Math.log(factor)/Math.log(2));
////		double diff = maxFactor - nextRasterPos;
////		if(diff<nextRasterPos)
////			factor = 2*nextRasterPos;
////		else
//			factor = nextRasterPos;
//		// now factor is rastered
//System.out.println("ShovWave.setHorizontalZoom() rastered factor = "+factor);
//		
		int firstPosByFullWidth = (int) (displayWave.getFloatPreviewReader().available() - (displayWave.getWidth() * factor));
		if(firstPosByFullWidth < 1) firstPosByFullWidth  = 0;
		if(firstPosByFullWidth<displayWave.getDataStartPos())
			displayWave.setDataStartPos(firstPosByFullWidth);
		
		displayWave.setDisplayScale(factor);
		setScrollBarSize();
		displayWave.paintWave();
		return factor;
	}
	
	/**
	 * scroll data to position pos whith rastering.
	 * pos/(int)displayscale will be the first sample displayed.
	 * (n%displayscale will be eliminated)
	 * So the scroll will be very faster, if the operation-system cach the data read from Mapping,
	 * if it is used by the FloatPreviewReader.
	 * 
	 * Update the scrollbar.
	 * 
	 * @param pos
	 * @return
	 */
	private void scrollToPos(int pos){
//System.out.println("ScrollWaveDisplay.scrollTo(int): value received: "+pos);
		if(pos<0){
//			System.err.println("ScrollWaveDisplay.scrollTo(int): can not scorll to negative value: "+pos+"/scrolling to 0.");
			pos = 0;
		}
		else{			
//			pos = (int) ((pos/(int)displayWave.getDisplayScale()) * displayWave.getDisplayScale());
			pos -= (int) (pos%displayWave.getDisplayScale());
			if(pos+displayWave.getSamplesWidth()>displayWave.getFloatPreviewReader().available())
				pos = displayWave.getFloatPreviewReader().available() - displayWave.getSamplesWidth();
			if(pos<0) pos = 0;
//System.out.println("ScrollWaveDisplay.scrollTo(int): value to set: "+pos);
		}
		displayWave.setDataStartPos(pos);
		scrollBar.setValue((int) (displayWave.getDataStartPos()/displayWave.getDisplayScale()));
		displayWave.paintWave();
		//ndisplayWave.repaint(); // allready executed in paintWave().
	}
	
	
	/**
	 * @return
	 */
	public FloatPreviewReader getFloatPreviewReader() {
		return displayWave.getFloatPreviewReader();
	}

	/**
	 * @param object
	 */
	public void setFloatPreviewReader(FloatPreviewReader fpr) {
		displayWave.setFloatPreviewReader(fpr);
	}
	
//	/**
//	 * Scroll to the position pos in the source of data displayed.
//	 * Change the position of ScrollBar accordly.
//	 * @param pos
//	 */
//	public void scrollToPos(int pos){
//		scrollBar.setValue((int) (scrollTo(pos)/displayWave.getDisplayScale()));
//		displayWave.repaint();
//	}
	/**
	 * scroll to the Cursorposition.
	 * Tree chooices by cursorpos:
	 * if cursorposModus ==
	 * 	1 : the cursor will the first sample displayed
	 *  2 : the cursor will be at the center of display
	 *  3 : the cursor will be the last sample displayed
	 * @param cursorPosModus
	 */
	public void scrollToCursorPos(int cursorPosModus){
		int cPos;
		switch(cursorPosModus){
			case 1: 	cPos = displayWave.getCursorPos();
						scrollToPos(cPos);
						break;
			case 2: 	cPos = displayWave.getCursorPos();
						cPos -= (displayWave.getWidth()/2)*displayWave.getDisplayScale();
						scrollToPos(cPos);	// if <0, setCursor set it to 0.
						break;
			case 3:	cPos = displayWave.getCursorPos();
						cPos -= displayWave.getWidth()*displayWave.getDisplayScale();
						scrollToPos(cPos); 	// if <0, setCursor set it to 0.
						break;
		}
	}
	
	/**
	 * display selection on display
	 * recaculate the displayscale, but raster them logaritmical
	 * (so, if 2,3 was the rigth scale, 2 will be used, by 5 4 will be used, and so on)
	 *
	 */
	public void displaySelection(){
		int[] selection = displayWave.getSelection(); 
		if(selection == null)
			return;
//		System.out.println("ScrollWaveDisplay.displaySelection() display: start at position: "+ selection[0]+", so many samples: "+selection[1]);
		setHorizontalZoom((float)selection[1] / displayWave.getWidth() + 1);
		scrollToPos(selection[0]);
	}
	
	/**
	 * return WaveDisplay, the Panel at that the data is displayed.
	 * @return
	 */
	public WaveDisplay getWaveDisplay(){
		return displayWave;
	}

	/**
	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long timeMicros) {
		displayWave.setCursorPosTime(timeMicros);
//		scrollToCursorPos(2);
		if(!displayWave.isCursorInDisplayrange())
			scrollToPos(displayWave.getCursorPos());		
	}

	/**
	 * Get the end of the wave data.
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	@Override
	public long getEndTime() {
		return displayWave.getEndInMikroseconds();
	}	
	
}














///*
//* Created on 23.06.2004
//*/
//package de.uos.fmt.musitech.audio.display;
//
//import de.uos.fmt.musitech.data.time.Timeable;
//import de.uos.fmt.musitech.framework.time.PlayTimer;
//
///**
//* TO DO: ScrollWaveDisplay  Timeable machen, diese Klasse löschen
//* Nicht registerForPush machen, sollte auf Seiten der Anwendung geschehen.
//* @author Nicolai Strauch
//*/
//public class TimeableWaveDisplay extends ScrollWaveDisplay implements Timeable{
//
//	PlayTimer playTimer = PlayTimer.getInstance();
//	private boolean isRegisteredForPush;
//	
//	public TimeableWaveDisplay(){
//		super();
//		registerForPush();
//	}
//	
//	/**
//	 * If the End of showed Audiodata is reached, this Timeable is unregistered
//	 * from push in PlayTimer.
//	 * By restart timer, this timeable must registered in PlayTimer again.
//	 * Use registerForPush().
//	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
//	 */
//	public void setTimePosition(long timeMicros) {
//		if(timeMicros >= getWaveDisplay().getEndInMikroseconds()){
//			playTimer.unRegisterForPush(this); // TO DO is this good so?
//			isRegisteredForPush = false;
//			return;
//		}
//		getWaveDisplay().setCursorPosTime(timeMicros);
////		scrollToCursorPos(2);
//		if(!getWaveDisplay().isCursorInDisplayrange())
//			scrollToPos(getWaveDisplay().getCursorPos());
//	}
//
//	/**
//	 * Register this Timeable in PlayTimer for Push.
//	 * Only if it is not registered.
//	 */
//	public void registerForPush(){
//		if(!isRegisteredForPush)
//			playTimer.registerForPush(this);
//		isRegisteredForPush = true;
//	}
//}
