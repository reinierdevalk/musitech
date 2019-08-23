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
 * Created on 19.08.2004
 */
package de.uos.fmt.musitech.audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Nicolai Strauch
 */
public class AFOPlayButtons extends JPanel {
	
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton stopButton = new JButton("Stop");
	JSlider playPosSlider = new JSlider();
	JLabel playPosLabel = new JLabel("PlayPos");
	
	AFOPlayer afoPlayer;
	
	public AFOPlayButtons(AFOPlayer afoP){
		setAFOPlayer(afoP);
		initialiseButtons();
		initialiseSliders();
		addAll();
	}
	
	public void setAFOPlayer(AFOPlayer afoP){
		afoPlayer = afoP;
		setSliderRange(); 
	}
	
	private void addAll(){
		add(playButton);
		add(pauseButton);
		add(stopButton);
		add(playPosSlider);
		add(playPosLabel);
	}
	
	public static boolean stopppp;
	
	private boolean pause;
	private void initialiseButtons(){
		playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				stopppp = false;
				afoPlayer.start();
			}});
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(pause){
					stopppp=false;
					afoPlayer.start();
				}else{
					stopppp = true;
					afoPlayer.stop();
				}
				pause = !pause;
			}});
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				afoPlayer.reset();
			}});
	}
	
	private void initialiseSliders(){
		playPosSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				playPosLabel.setText(String.valueOf(playPosSlider.getValue()));
			}});
		playPosSlider.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				afoPlayer.setTimePosition(playPosSlider.getValue()*1000);
				try {
					playPosLabel.setText(String.valueOf(afoPlayer.getTimePosition()/1000));
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			}});
	}

	/**
	 * 
	 */
	public void setSliderRange() {
		playPosSlider.setMaximum((int) (afoPlayer.getEndTime()/1000)); 
	}

}
