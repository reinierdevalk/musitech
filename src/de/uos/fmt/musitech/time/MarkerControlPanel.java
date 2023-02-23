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
package de.uos.fmt.musitech.time;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.uos.fmt.musitech.time.gui.TempoPanel;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A JPanel which displays some controls how to display the MetricalTimeLine in MarkerPanel.<BR>
 * The controls are so far:
 * <UL>
 * <LI>wether to display Markers in metricTime or in realTime order</LI>
 * <LI>zoom factors</LI>
 * <LI>a "Quit" button to exit the whole application</LI>
 * </UL>
 * <BR>
 * This class is called by MeterDisplay, and the component will be placed below the MarkerPanel.
 * @author Wolfram Heyer
 */
public class MarkerControlPanel extends JPanel {
	
	JButton knopfC;
	JButton knopfD;
	JRadioButton knopfA;
	JRadioButton knopfB;
	private JTextField realTimeField;
	private JTextField metricalTimeField;
	private JTextField timeSignatureField;
	private MarkerPanel markerPanel;
	private TempoPanel tempoPanel;
	private int zoomFactor = 1;
	private String timeRelation;
	private JTextField zoomTextField;

	public MarkerControlPanel()
	{

		setLayout(new BorderLayout());
	
		JPanel labelPanel = new JPanel();		
		realTimeField = new JTextField("0");
		realTimeField.setHorizontalAlignment(SwingConstants.RIGHT);
		realTimeField.setPreferredSize(new Dimension(150,20));
		realTimeField.setBackground(Color.GRAY.brighter());
		realTimeField.setEditable(false);
		JLabel labelA = new JLabel(" physical time       ");
		metricalTimeField = new JTextField("0");
		metricalTimeField.setHorizontalAlignment(SwingConstants.RIGHT);
		metricalTimeField.setPreferredSize(new Dimension(200,20));
		metricalTimeField.setBackground(Color.GRAY.brighter());
		metricalTimeField.setEditable(false);
		JLabel labelB = new JLabel(" metrical time");
		timeSignatureField = new JTextField("0");
		timeSignatureField.setHorizontalAlignment(SwingConstants.RIGHT);
		timeSignatureField.setPreferredSize(new Dimension(50,20));
		timeSignatureField.setBackground(Color.GRAY.brighter());
		timeSignatureField.setEditable(false);
		JLabel labelC = new JLabel(" timeSignature");
		labelPanel.add(realTimeField);
		labelPanel.add(labelA);
		labelPanel.add(metricalTimeField);
		labelPanel.add(labelB);
		labelPanel.add(timeSignatureField);
		labelPanel.add(labelC);
	
		JPanel buttonPanel = new JPanel();
		
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BorderLayout());
		knopfA = new JRadioButton("physical time", true);
		knopfA.setToolTipText("Show the MetricalTimeLine in physical time relation.");
		knopfA.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()== knopfA ){
					knopfA.setSelected(true);
					knopfB.setSelected(false);
					setRealTime();
				}
			}
		});

		knopfB = new JRadioButton("metrical time", false);
		knopfB.setToolTipText("Show the MetricalTimeLine in metrical time relation.");
		knopfB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()== knopfB ){
					knopfB.setSelected(true);
					knopfA.setSelected(false);
					setMetricTime();
				}
			}
		});
		
		radioPanel.add(knopfA, BorderLayout.NORTH);
		radioPanel.add(knopfB, BorderLayout.SOUTH);

		JPanel zoomPanel = new JPanel();
		zoomPanel.setBorder(BorderFactory.createEtchedBorder());
		zoomPanel.setLayout(new BorderLayout());		
		JLabel zoomLabel = new JLabel("Zoom");
		zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
		knopfC = new JButton(new ImageIcon(this.getClass().getResource("../utility/gui/icons/zoom_in.gif")));
		knopfC.setToolTipText("zoom in");
		knopfC.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()== knopfC ){
					zoomIn();
				}
			}
		});

		knopfD = new JButton(new ImageIcon(this.getClass().getResource("../utility/gui/icons/zoom_out.gif")));
		knopfD.setToolTipText("zoom out");
		knopfD.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()== knopfD ){
					zoomOut();
				}
			}
		});
		
		zoomTextField = new JTextField(zoomFactor+"x");
		zoomTextField.setToolTipText("the current zoomfactor");
		zoomTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		zoomTextField.setPreferredSize(new Dimension(30,20));
		zoomTextField.setBackground(Color.GRAY.brighter());
		zoomTextField.setEditable(false);
		zoomPanel.add(zoomLabel, BorderLayout.NORTH);
		zoomPanel.add(knopfC, BorderLayout.WEST);
		zoomPanel.add(knopfD, BorderLayout.CENTER);
		zoomPanel.add(zoomTextField, BorderLayout.EAST);

		buttonPanel.add(radioPanel);
		buttonPanel.add(zoomPanel);
	
		add(labelPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
	}



	/**
	 * This sets the timeRelation of how the markers are displayed in MarkerPanel to physical time.
	 */
	public void setRealTime(){
		timeRelation = "physical time";
		markerPanel.setRealTimeOrder();
		tempoPanel.setRealTimeOrder();
	}
	
	/**
	 * This sets the timeRelation of how the markers are displayed in MarkerPanel to metrical time.
	 */
	public void setMetricTime(){
		timeRelation = "metrical time";
		markerPanel.setMetricTimeOrder();
		tempoPanel.setMetricTimeOrder();
	}
	
	/**
	 * This increases the zoomFactor.
	 */
	public void zoomIn(){
		zoomFactor++; 
		markerPanel.setZoom(zoomFactor);
		tempoPanel.setZoom(zoomFactor);
		zoomTextField.setText(zoomFactor+"x");
		revalidate();
		
	}
	
	/**
	 * This decreases the zoomFactor.
	 */
	public void zoomOut(){
		if(zoomFactor > 1)
			zoomFactor--;
		markerPanel.setZoom(zoomFactor);
		tempoPanel.setZoom(zoomFactor);
		zoomTextField.setText(zoomFactor+"x");
		revalidate();
	}


	/**
	 * This updates the time labels which are displayed in this object to the current values for realTime and metricalTime.
	 */
	public void setTimeLabels(long realTime, Rational metricTime, Rational currentTimeSignature){
		realTimeField.setText(TimeStamp.toStringFormated(realTime));
		String[] msg = markerPanel.getMetricalTimeLine().toStringFormatted(metricTime);

		metricalTimeField.setText(msg[0]);
		
		timeSignatureField.setText(currentTimeSignature+"");
	}


	/** 
	 * This returns the MarkerPanel
	 * @return markerPanel
	 */
	public MarkerPanel getMarkerPanel() {
		return markerPanel;
	}

	/** 
	 * make the markerPanel known to this object
	 * @param panel
	 */
	public void setMarkerPanel(MarkerPanel panel) {
		markerPanel = panel;
	}

	/** 
	 * getTempoPanel
	 * @return the current object (the current tempoPanel)
	 */
	public TempoPanel getTempoPanel() {
		return tempoPanel;
	}

	/** 
	 * make the TempoPanel known to this object
	 * @param panel
	 */
	public void setTempoPanel(TempoPanel panel) {
		tempoPanel = panel;
	}

}