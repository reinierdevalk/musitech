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
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.editor.AbstractDisplay;
import de.uos.fmt.musitech.time.gui.TempoPanel;

/**
 * This JPanel creates TempoPanel, MarkerPanel and MarkerControlPanel.
 * @author Wolfram Heyer
 */
public class MeterDisplay 
//extends JPanel
extends AbstractDisplay
implements Timeable
{

	private MarkerPanel markerPanel;
	private TempoPanel tempoPanel;
	JScrollPane tempoScrollPane;
	JScrollPane markerScrollPane;
	JSplitPane splitPane;
	private MarkerControlPanel markerControlPanel = new MarkerControlPanel();
	
	private MetricalTimeLine metricalTimeLine;

	/**
	 * This is the default constructor which will create and display all available components (from top to bottom):
	 * <UL>
	 * <LI>a TempoPanel</LI>
	 * <LI>a MarkerPanel</LI>
	 * <LI>a MarkerControlPanel</LI>
	 * </UL>
	 * @param metricalTimeLine The current metricalTimeLine
	 */
	public MeterDisplay(MetricalTimeLine metricalTimeLine) {
		this(metricalTimeLine, true,true,true);
	}


	/**
	 * Using this constructor you can define which of the following components will be created and shown:
	 * <UL>
	 * <LI>a TempoPanel</LI>
	 * <LI>a MarkerPanel</LI>
	 * <LI>a MarkerControlPanel</LI>
	 * </UL>
	 * @param metricalTimeLine The current metricalTimeLine
	 * @param showTempoPanel can be set true or false to disable or enable this component.
	 * @param showMarkerPanel can be set true or false to disable or enable this component.
	 * @param showMarkerControlPanel can be set true or false to disable or enable this component.
	 */
	public MeterDisplay(
		MetricalTimeLine metricalTimeLine,
		boolean showTempoPanel,
		boolean showMarkerPanel,
		boolean showMarkerControlPanel) {

		setLayout(new BorderLayout());
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);

		if (showTempoPanel) {
			tempoPanel = new TempoPanel(metricalTimeLine);
			tempoScrollPane = new JScrollPane(tempoPanel);
			tempoScrollPane.setPreferredSize(new Dimension(800,150));	// TODO: dynamic values for size
			splitPane.setTopComponent(tempoScrollPane);
		}

		markerPanel = new MarkerPanel(metricalTimeLine);
		if (showMarkerPanel) {
			markerScrollPane = new JScrollPane(markerPanel);
			markerScrollPane.setPreferredSize(new Dimension(800,150));	//TODO: dynamic values for size
			splitPane.setBottomComponent(markerScrollPane);
		}

		ComponentListener componentListener = new ComponentListener() {
			@Override
			public void componentResized (ComponentEvent e) {
				splitPane.setDividerLocation(splitPane.getHeight()/2);
			}
			@Override
			public void componentHidden (ComponentEvent e) {
			}
			@Override
			public void componentShown (ComponentEvent e) {
			}
			@Override
			public void componentMoved (ComponentEvent e) {
			}
		};
		splitPane.addComponentListener(componentListener);
		
		splitPane.setPreferredSize(new Dimension(600,400));	// TODO: dynamic values for size

		this.add(splitPane, BorderLayout.CENTER);

		if (showMarkerControlPanel) {
			this.add(markerControlPanel, BorderLayout.SOUTH);
		}

		markerControlPanel.setMarkerPanel(markerPanel);
		markerControlPanel.setTempoPanel(tempoPanel);
		markerControlPanel.setRealTime();

		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				/* 
				 * TODO: Nachdem TempoPanel und MarkerPanel in ein JSplitPane gepackt wurden,
				 * funktioniert die Synchronisation der JScrollPanes nicht mehr!
				 */
				if (!(e.getSource() instanceof JViewport))
					return;
				JViewport sourceView = (JViewport) (e.getSource());

				if(sourceView != markerScrollPane.getViewport())
					markerScrollPane.getViewport().setViewPosition(sourceView.getViewPosition());
				if(sourceView != tempoScrollPane.getViewport())
					tempoScrollPane.getViewport().setViewPosition(sourceView.getViewPosition());

//				Component[] comps = getComponents();
//				for (int i = 0; i < comps.length; i++) {
//					if (!(comps[i] instanceof JScrollPane))
//						continue;
//					JViewport targetView = ((JScrollPane) comps[i]).getViewport();
//					if (targetView == sourceView)
//						continue;
//					targetView.setViewPosition(sourceView.getViewPosition());
//				} // end for
			}
		};
		
		markerPanel.setMarkerControlPanel(markerControlPanel);

		markerScrollPane.getViewport().addChangeListener(changeListener);
		tempoScrollPane.getViewport().addChangeListener(changeListener);

		setPreferredSize(new Dimension(600,400));	//TODO: dynamic values for size
	}
    /** 
     * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
     */
    @Override
	public void setTimePosition(long timeMicros) {
        markerPanel.setTimePosition(timeMicros);
        tempoPanel.setTimePosition(timeMicros);
    }
    
    /**
     * Creates an new MeterDisplay for the default TimeLine, 
     */
    public MeterDisplay(){
        this(Context.getDefaultContext().getPiece().getMetricalTimeLine() ,true,true,true);
    } 
    
    
    
    /** 
     * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
     */
    @Override
	public long getEndTime() {
        if (markerPanel.getEndTime() >= tempoPanel.getEndTime())	//should be equal
            return markerPanel.getEndTime();
        else
            return tempoPanel.getEndTime();
    }
    /** 
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    @Override
	public void createGUI() {
        determineMetricalTimeLine();
        if (metricalTimeLine==null)
            return;
        setLayout(new BorderLayout());
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);

//		if (showTempoPanel) {
			tempoPanel = new TempoPanel(metricalTimeLine);
			tempoScrollPane = new JScrollPane(tempoPanel);
			tempoScrollPane.setPreferredSize(new Dimension(600,150));	// TODO: dynamic values for size
			splitPane.setTopComponent(tempoScrollPane);
//		}

		markerPanel = new MarkerPanel(metricalTimeLine);
//		if (showMarkerPanel) {
			markerScrollPane = new JScrollPane(markerPanel);
			markerScrollPane.setPreferredSize(new Dimension(600,150));	//TODO: dynamic values for size
			splitPane.setBottomComponent(markerScrollPane);
//		}

		ComponentListener componentListener = new ComponentListener() {
			@Override
			public void componentResized (ComponentEvent e) {
				splitPane.setDividerLocation(splitPane.getHeight()/2);
			}
			@Override
			public void componentHidden (ComponentEvent e) {
			}
			@Override
			public void componentShown (ComponentEvent e) {
			}
			@Override
			public void componentMoved (ComponentEvent e) {
			}
		};
		splitPane.addComponentListener(componentListener);
		
//		splitPane.setPreferredSize(new Dimension(800,500));	// TODO: dynamic values for size

		add(splitPane, BorderLayout.CENTER);

//		if (showMarkerControlPanel) {
			add(markerControlPanel, BorderLayout.SOUTH);
//		}

		markerControlPanel.setMarkerPanel(markerPanel);
		markerControlPanel.setTempoPanel(tempoPanel);
		markerControlPanel.setRealTime();

		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				/* 
				 * TODO: Nachdem TempoPanel und MarkerPanel in ein JSplitPane gepackt wurden,
				 * funktioniert die Synchronisation der JScrollPanes nicht mehr!
				 */
				if (!(e.getSource() instanceof JViewport))
					return;
				JViewport sourceView = (JViewport) (e.getSource());

				if(sourceView != markerScrollPane.getViewport())
					markerScrollPane.getViewport().setViewPosition(sourceView.getViewPosition());
				if(sourceView != tempoScrollPane.getViewport())
					tempoScrollPane.getViewport().setViewPosition(sourceView.getViewPosition());

			}
		};
		
		markerPanel.setMarkerControlPanel(markerControlPanel);

		markerScrollPane.getViewport().addChangeListener(changeListener);
		tempoScrollPane.getViewport().addChangeListener(changeListener);

		setPreferredSize(new Dimension(600,400));	//TODO: dynamic values for size
    }
    
    private void determineMetricalTimeLine(){
        if (getEditObj() instanceof MetricalTimeLine){
            metricalTimeLine = (MetricalTimeLine) getEditObj();
        } else if (getPropertyValue() instanceof MetricalTimeLine){
            metricalTimeLine = (MetricalTimeLine)getPropertyValue();
        }
    }

}