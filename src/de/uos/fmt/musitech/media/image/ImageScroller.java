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
 * Created on 16.02.2004
 */
package de.uos.fmt.musitech.media.image;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Use this class to put ImageComponents in Scrollbar
 * use fitToComp to auto-resize image to component size.
 * @author Jan
 *
 */
public class ImageScroller extends JPanel {

	/**
	 * 
	 * @uml.property name="jScrollPane"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	JScrollPane jScrollPane;

	/**
	 * 
	 * @uml.property name="imgComp"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	ZoomImageComponent imgComp;

	double zoom = 1.0;
	boolean fitToComp = true; // fit Image to compenent size

	public ImageScroller() {
		//		setPreferredSize(new Dimension(100, 200));
	}

	public ImageScroller(URL url) {
		imgComp = new ZoomImageComponent(url);
		jScrollPane = new JScrollPane(imgComp);
		setupScroller();
		addComponentListener(new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (fitToComp) {
					imgComp.setZoomfactor(computeFactor());
				}
			}

			@Override
			public void componentShown(ComponentEvent e) {
				if (true) {
					imgComp.setZoomfactor(1.0);
				}
			}
		});
	}

	private void setupScroller() {
		setLayout(new BorderLayout());
		add(jScrollPane, BorderLayout.CENTER);
		
		setZoom(2.0);
		doLayout();
	}

	private double computeFactor() {
		double zoom = 1.0;
		int imgWidth = imgComp.getOriginalSize().width;
		int imgHeight = imgComp.getOriginalSize().height;
		int viewWidth = jScrollPane.getViewport().getWidth();
		int viewHeight = jScrollPane.getViewport().getHeight();
		if ((float) imgWidth / imgHeight > (float) viewWidth / viewHeight) {
			zoom = (float) viewWidth / imgWidth;
		} else
			zoom = (float) viewHeight / imgHeight;
		return zoom;
	}

	private void revalidateImage() {
		imgComp.revalidate();
		repaint();
	}

	int height= 0 ;
	int width= 0 ;

	/**
	 * @return
	 */
	public boolean isFitToComp() {
		return fitToComp;
	}

	/**
	 * @return
	 * 
	 * @uml.property name="zoom"
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * @param b
	 * 
	 * @uml.property name="fitToComp"
	 */
	public void setFitToComp(boolean b) {
		fitToComp = b;
	}

	/**
	 * @param d
	 * 
	 * @uml.property name="zoom"
	 */
	public void setZoom(double d) {
		zoom = d;
		imgComp.setZoomfactor(d);
		revalidateImage();
	}

	public static void testImage() {

		final ImageScroller imaScrol;
		try {
			imaScrol = new ImageScroller(new File("E:\\Eigene Dateien\\Eigene Musik\\christophe\\chopin1a.jpg").toURL());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		final JToggleButton button = new JToggleButton("100%", true);
		final JSlider slider = new JSlider(50, 400, 100);
		button.setFont(new Font("SansSerif", Font.PLAIN, 9));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					imaScrol.setFitToComp(false);
					button.setText("100%");
					imaScrol.setZoom(1.0);
					slider.setValue(100);
				} else {
					imaScrol.setFitToComp(true);
					button.setText("fit");
					imaScrol.setZoom(imaScrol.computeFactor());
					slider.setValue((int) (imaScrol.computeFactor()*100));
				}
			}
		});
		
		slider.setMajorTickSpacing(50);
		
		slider.setPaintTicks(true);
		slider.createStandardLabels(50);
		slider.setPaintLabels(true);
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				imaScrol.setZoom((float) slider.getValue()/100);
				
			}
		});
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(button, BorderLayout.NORTH);
		frame.getContentPane().add(imaScrol, BorderLayout.CENTER);
		frame.getContentPane().add(slider, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocation(200, 150);
		frame.pack();
		frame.setSize(300, 200);
		frame.show();

	}

	public static void main(String[] args) {
		testImage();
	}

}
