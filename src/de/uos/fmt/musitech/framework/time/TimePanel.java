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
 * Created on 27.01.2004
 */
package de.uos.fmt.musitech.framework.time;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.uos.fmt.musitech.data.time.Timeable;

/**
 * This classe displays the time from the
 * Playtimer in min:sec
 * 
 * @author Jan
 *
 */
public class TimePanel extends JPanel implements Timeable {

	private PlayTimer playTimer;
	JLabel timeLabel = new JLabel();
	private long timeInMillis;

	public TimePanel(PlayTimer timer) {
		super(new BorderLayout());
		
		setPlayTimer(timer);
		updateTimeLabel(0);

		add(timeLabel);
		timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		playTimer.registerForPush(this);
	}
	

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long time) {
		timeInMillis = time;
		updateTimeLabel(time);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;
	}

	private void updateTimeLabel(long time){
		timeLabel.setText(timeOutput(time));
	}
	
	public static String timeOutput(long time_in_us) {
		StringBuffer sbTime = new StringBuffer();
		time_in_us /= 1000000; 
		if (time_in_us < 0) {
			sbTime.append('-');
			time_in_us = -time_in_us;
		} else
		    sbTime.append(' ');
		//minuten
		sbTime.append(leadingZero((int) time_in_us / 60));
		sbTime.append(':');
		//sekunden
		sbTime.append(leadingZero((int) time_in_us % 60));

		return sbTime.toString();
	}

	public static String leadingZero(int number) {
		if (number < 10)
			return "0" + number;
		else
			return Integer.toString(number);
	}

	/**
	 * End time ist mot relevant for this.
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	@Override
	public long getEndTime() {
		return -1;
	}

}
