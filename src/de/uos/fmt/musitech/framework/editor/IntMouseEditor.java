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
 * Created on 15.07.2004
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This Panel is used to change Integer values by mouse motion
 * Drag Panel and move and up and down to increase/decrease value
 * Click left to increase 1, click right to decrease 1
 *  
 * @author Jan
 *
 */
public class IntMouseEditor extends JPanel {

	int minValue;
	int maxValue;
	int value;
	int changevalue; // value changed by moving mouse up or down
	String text;
	JLabel valueLabel;
	JLabel textLabel;
	JLabel arrowLabel;

	/**
	* Default Constructor creates new Editor Panel with 
	* minValue = 0, maxValue = 127, startValue = 0, text = "def"
	*/
	public IntMouseEditor() {
		this(0, 127, 0, "def");
	}

	public IntMouseEditor(String text) {
		this(0, 127, 0, text);
	}

	public IntMouseEditor(int maxValue, String text) {
		this(0, 127, 0, text);
	}

	public IntMouseEditor(int minValue, int maxValue, String text) {
		this(minValue, maxValue, minValue, text);
	}

	public IntMouseEditor(int minValue, int maxValue, int value, String text) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = value;
		this.text = text;
		addMouseMotionListener(mouseMotion);
		addMouseListener(mouseListener);
		init();
	}

	boolean pressed = false;
	Point startPoint;

	MouseListener mouseListener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
				// increase value 
	if (e.getButton() == MouseEvent.BUTTON1) {
				notifyListener(value, calculateNewValue(1));
				value = calculateNewValue(1);
				updateValueLabel();
			}
			// decrease value
			if (e.getButton() == MouseEvent.BUTTON3) {
				notifyListener(value, calculateNewValue(-1));
				value = calculateNewValue(-1);
				updateValueLabel();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			startPoint = e.getPoint();
			System.out.println("startPoint: " + e.getY());
			pressed = true;

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			notifyListener(value, calculateNewValue(changevalue));
			value = calculateNewValue(changevalue);
			System.out.println("new Value: " + value);
			updateValueLabel();
			changevalue = 0;

		}

	};

	MouseMotionListener mouseMotion = new MouseMotionListener() {
		@Override
		public void mouseDragged(MouseEvent e) {
			changevalue = (int) - (e.getY() - startPoint.getY()) / 2;
			System.out.println("point: " + changevalue);
			// new value will only change ValueLabel while dragging
			// Listeners will be notified when mouse is released
			updateValueLabel(calculateNewValue(changevalue));
		}
		@Override
		public void mouseMoved(MouseEvent e) {

		}

	};

	private void init() {
		setLayout(new GridLayout(1, 3));
		add(getTextLabel());
		add(getValueLabel());
		add(getArrowLabel());
		changeListener = new ArrayList();

	}

	private JLabel getTextLabel() {
		if (textLabel == null) {
			textLabel = new JLabel(text);
			textLabel.setFont(new Font(null, Font.PLAIN, 11));
			textLabel.addMouseListener(mouseListener);
			textLabel.addMouseMotionListener(mouseMotion);
		}
		return textLabel;
	}

	private JLabel getValueLabel() {
		if (valueLabel == null) {
			valueLabel = new JLabel("" + value);
			valueLabel.setFont(new Font(null, Font.BOLD, 11));
			valueLabel.setForeground(Color.BLUE);
			valueLabel.addMouseListener(mouseListener);
			valueLabel.addMouseMotionListener(mouseMotion);
		}
		return valueLabel;
	}

	private JLabel getArrowLabel(){
		if (arrowLabel == null){
			arrowLabel = new JLabel(new ImageIcon(getClass().getResource("icons/arrow.gif")));
//			arrowLabel.setText("hallo");
			arrowLabel.setPreferredSize(new Dimension(11, 25));
			arrowLabel.setMaximumSize(new Dimension(11, 25));
		}
		return arrowLabel;
	}
	
	private void updateValueLabel() {
		getValueLabel().setText("" + value);
	}

	private void updateValueLabel(int value) {
		getValueLabel().setText("" + value);
	}

	private void updateTextLabel() {
		getTextLabel().setText(text);
	}

	/**
	 * Calculates new Value in the range of minValue and maxValue
	 * The method adds changevalue to the current value and returns
	 * the new value, without changing value
	 * @param changevalue
	 * @return
	 */
	private int calculateNewValue(int changevalue) {
		// tempValue is used to not change value
		int tempValue = value;
		if (changevalue > 0) {
			tempValue = (Math.min(tempValue + changevalue, maxValue));
		} else {
			tempValue = (Math.max(tempValue + changevalue, minValue));
		}
		return tempValue;
	}

	ArrayList changeListener;

	public void registerChangeListener(PropertyChangeListener pCListener) {
		changeListener.add(pCListener);
	}

	public void removeChangeListener(PropertyChangeListener pCListener) {
		changeListener.remove(pCListener);
	}

	public void notifyListener(int oldValue, int newValue) {
		for (Iterator iter = changeListener.iterator(); iter.hasNext();) {
			PropertyChangeListener listener =
				(PropertyChangeListener) iter.next();
			PropertyChangeEvent changeEvent =
				new PropertyChangeEvent(
					this,
					text,
					new Integer(oldValue),
					new Integer(newValue));
			listener.propertyChange(changeEvent);
		}
	}

	/**
	 * @return
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @return
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param i
	 */
	public void setMaxValue(int i) {
		maxValue = i;
	}

	/**
	 * @param i
	 */
	public void setMinValue(int i) {
		minValue = i;
	}

	/**
	 * @param string
	 */
	public void setText(String string) {
		text = string;
		updateTextLabel();
	}

	/**
	 * @param i
	 */
	public void setValue(int newValue) {
		int oldValue = value;
		if (newValue > value) {
			value = (Math.min(newValue, maxValue));
		} else {
			value = (Math.max(newValue, minValue));
		}
		notifyListener(oldValue, value);
		updateValueLabel();

	}

	public void setToolText(String toolText) {
		getTextLabel().setToolTipText(toolText);
		getValueLabel().setToolTipText(toolText);
	}

}
