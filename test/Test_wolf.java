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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.time.MeterDisplay;
import de.uos.fmt.musitech.time.TimeStamp;
import de.uos.fmt.musitech.utility.math.Rational;


/**
 * This Class is for testing TempoPanel and DisplayMeterMarker.<BR>
 * Overview:
 * <UL>
 * <LI>Test_wolf 		creates an object of	MeterDisplay</LI>
 * <LI>MeterDisplay	creates an object of	TempoPanel, MarkerPanel and ControlMarkerPanel</LI>
 * <LI>MarkerPanel	creates objects of	MarkerIconDisplay and MarkerInfoDisplay</LI>
 * </UL>
 * 
 * @author Wolfram Heyer
 */
public class Test_wolf extends JFrame{

	private JButton quitButton;
	private Piece piece; // Piece (object) represents a piece of music
	private Context context; // this container goes into a context
	private MetricalTimeLine metricalTimeLine; // Creating metricalTimeLine
	
	public Test_wolf()
	{
		super("MeterDisplay");
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		metricalTimeLine = new MetricalTimeLine();
		//metricalTimeLine.clear();
	
		setAdditionalMarkers();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		MeterDisplay markerDisplay = new MeterDisplay(metricalTimeLine, true, true, true);
//		MeterDisplay markerDisplay = new MeterDisplay(metricalTimeLine, false, true, true);
//		MeterDisplay markerDisplay = new MeterDisplay(metricalTimeLine, true, false, true);
//		MeterDisplay markerDisplay = new MeterDisplay(metricalTimeLine, true, true, false);
		mainPanel.add(markerDisplay, BorderLayout.CENTER);

		quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if ( event.getSource()==quitButton ){
					System.exit(0);
				}
			}
		});

		cp.add(mainPanel, BorderLayout.CENTER);
		cp.add(quitButton, BorderLayout.SOUTH);

		cp.setBackground(Color.black);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		setLocation(super.getWidth(), super.getHeight());
		pack();
		setVisible(true);

//		testNewMethod();		
	}	
	
	/**
	 * set some additional markes to the metricalTimeLine
	 */
	private void setAdditionalMarkers(){	
		boolean debug = false;
//		boolean debug = true;

		// add a TimeSignatureMarker to the metricalTimeLine
//		TimeSignatureMarker tsm = new TimeSignatureMarker(3,4,new Rational(1,4));
		//metricalTimeLine.add(tsm);

		// Creating  timedMetrical's
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(4000), new Rational(2,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(6100), new Rational(12,4)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(14300), new Rational(7,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(20000), new Rational(40,4)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(21000), new Rational(41,4)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(25000), new Rational(13,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(28000), new Rational(15,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(34000), new Rational(17,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(38000), new Rational(19,1)));
		metricalTimeLine.add( new TimedMetrical(TimeStamp.timeStampMillis(40000), new Rational(20,1)));

		// Adding timesignature changes to the MetricalTimeLine
		metricalTimeLine.add( new TimeSignatureMarker(4,4,	new Rational(0,4)) );
		metricalTimeLine.add( new TimeSignatureMarker(3,4,	new Rational(8,1)) );
		
		// Printing metricalTimeLine
		if (debug) System.out.println("Test_Wolf.setAdditionalMarkers(): Here comes the full metricalTimeLine:\n"+metricalTimeLine);
		
//		Rational r = metricalTimeLine.fromMeasureBeatRemainder(new int[] {0, 0, 0, 1});
//		int[] value = new int[4];
//		Rational r = new Rational(0);
//		value[0] = 7;
//		value[1] = 0;
//		value[2] = 0;
//		value[3] = 1;
//		r = metricalTimeLine.fromMeasureBeatRemainder(value);
//		value[0] = 20;
//		value[1] = 0;
//		value[2] = 0;
//		value[3] = 1;
//		r = metricalTimeLine.fromMeasureBeatRemainder(value);
//		value[0] = 22;
//		value[1] = 2;
//		value[2] = 0;
//		value[3] = 1;
//		r = metricalTimeLine.fromMeasureBeatRemainder(value);
//		value[0] = 24;
//		value[1] = 0;
//		value[2] = 0;
//		value[3] = 1;
//		r = metricalTimeLine.fromMeasureBeatRemainder(value);

		if (debug) System.out.println();
		
		//info( this.getClass() );
	}

private void info(Class myClass){
	System.out.println( "Methods in this class ["+myClass.getName()+"]:");
	for (int i = 0; i < myClass.getMethods().length; i++) {
		System.out.println( i+": "+myClass.getMethods()[i].getName() );	
	}
	
	System.out.println("Active Threads: [current: "+Thread.currentThread().getName()+"]");
	Thread[] thread = new Thread[Thread.activeCount()];
	Thread.enumerate(thread);
	for (int i = 0; i < thread.length; i++) {
		System.out.println(i+": "+thread[i].getName().toString());
	}
}
	

  /**
   * This is a class written only to call new test methods
 */
private void testNewMethod(){
	boolean debug = false;
//	boolean debug = true;
	
	/*
	 * 18/8 are in a 8/8 : 2 measures, 2 beats and 0 rest 
	 * 18/8 are in a 4/4 : 2 measures, 1 beats and 0 rest 
	 * 18/8 are in a 3/4 : 3 measures, 0 beats and 0 rest 
	 */
	
	int[] value = new int[4];

	value = metricalTimeLine.toMeasureBeatRemainder(new Rational(18,8));
	if (debug) System.out.println("Test_wolf.testNewMethod: Rational(18,8) is at: measure "+value[0]+", beat "+value[1]+", rest "+value[2]+'/'+value[3]);

	value[0] = 2;
	value[1] = 1;
	value[2] = 0;
	value[3] = 1;
	
	Rational r_value = metricalTimeLine.fromMeasureBeatRemainder(value);
	if (debug) System.out.println("Test_wolf.testNewMethod: measure "+value[0]+", beat "+value[1]+", rest "+value[2]+'/'+value[3]+" is at Rational "+r_value);
  }

	public static void main(String[] args){
		new Test_wolf();
	}
	
}
