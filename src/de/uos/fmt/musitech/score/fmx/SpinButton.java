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
package de.uos.fmt.musitech.score.fmx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicArrowButton;

/** This is a TextField for numerical strings with an increase 
 * and decrease button.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class SpinButton extends JPanel implements ActionListener
{
	private final JTextField text = new JTextField("0");
	private final JButton upButton = new BasicArrowButton(BasicArrowButton.NORTH);
	private final JButton downButton = new BasicArrowButton(BasicArrowButton.SOUTH);
	private final JLabel label;
	private int value = 0;
	private int min;
	private int max;
	private boolean useMinMax = false;

	private List actionListeners = new LinkedList();
	
	/** Creates an SpinButton with no label and initial value 0.*/
	public SpinButton ()
	{
	   label = null;
	   init();
	}
	
	/** Creates an SpinButton with given label and initial value.
	 * @param l text label that is placed on the left
	 * @param v initial value */
	public SpinButton (String l, int v)
   {
   	label = new JLabel(l);
   	value = v;
   	init();
   }

	/** Creates an SpinButton with given label, initial value
	 * and its range.
	 * @param l   text label that is placed on the left
	 * @param v   initial value 
	 * @param min least possible value
	 * @param max largest possible value */
	public SpinButton (String l, int v, int min, int max)
   {
   	label = new JLabel(l);
   	value = v;
   	this.min = min;
   	this.max = max;
   	useMinMax = true;
   	init();
   }
	
	private void init ()
	{
	   setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
	   JPanel buttonPanel = new JPanel(new java.awt.GridLayout(2,1));
	   buttonPanel.add(upButton);
	   buttonPanel.add(downButton);
	   text.setText(""+value);
	   if (label != null)
	   	add(label);
	   add(text);
	   add(buttonPanel);
	   upButton.addActionListener(this);
	   downButton.addActionListener(this);
	   text.addActionListener(this);
	   upButton.setName("up");
	   downButton.setName("down");

	   int height = (int)text.getPreferredSize().getHeight();
	   text.setPreferredSize(new java.awt.Dimension(30,22));
	}


	public void actionPerformed (ActionEvent ev)
	{
		Class cl = ev.getSource().getClass();   
		if (cl == JTextField.class)
			updateValue();
		else if (cl == BasicArrowButton.class)
		{
			BasicArrowButton button = (BasicArrowButton)ev.getSource();
			if (button.getName().equals("up"))
				incrementValue();
			else
				decrementValue();									
		}
	}

	/** Increments the displayed value by 1. */	
	public void incrementValue ()
	{
	   if (!useMinMax || value < max)
	   {
	   	text.setText("" + (++value));
	   	firePropertyChange("value", value-1, value);
	   }
	}
	
	/** Decrements the displayed value by 1. */		
	public void decrementValue ()
	{
	   if (!useMinMax || value > min)
	   {
	  	   text.setText("" + (--value));
	  	   firePropertyChange("value", value+1, value);
	   }
	}
	
	/** Changes the displayed value. 
	 * @param v new value */
	public void setValue (int v)
	{
	   if (!useMinMax || (v >= min && v <= max))
	   {
	      int oldValue = value;
	   	text.setText("" + (value = v));
	   	firePropertyChange("value", oldValue, value);
	   }
	}
	
	private void updateValue ()
	{
	   try
	   {
	      int oldValue = value;
	      int v = Integer.parseInt(text.getText());
	      if (!useMinMax || (v >= min && v <= max))
	      	value = v;
	      else
	      	text.setText(""+value);
	      firePropertyChange("value", oldValue, value);
	   }
	   catch (NumberFormatException e)
	   {
	      text.setText(""+value);
	   }
	}

	/** Returns the current value */	
	public int getValue ()
	{
	   return value;
	}
	
/*	public void addActionListener (ActionListener listener)
	{
		text.addActionListener(listener);
		upButton.addActionListener(listener);
		downButton.addActionListener(listener);
	}*/
}
