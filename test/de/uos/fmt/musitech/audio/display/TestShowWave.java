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
 * Created on 09.01.2004
 */
package de.uos.fmt.musitech.audio.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.audio.TestMP3Player;
import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.framework.time.PlayTimer;

/**
 * @author Nicolai Strauch
 */
public class TestShowWave extends JFrame {
	
	ScrollWaveDisplay sw = new ScrollWaveDisplay();
	
	JSlider vZoomSlider = new JSlider();
	JLabel vLabel = new JLabel("Vert. Zoom");
	JSlider hZoomSlider = new JSlider();
	JLabel hLabel = new JLabel("Horz. Zoom");
	
	JButton toCursorButton = new JButton("Scroll to Cursor.");
	JButton toSelectButton = new JButton("Display selection.");
	
	public TestShowWave(){
		super("Wave");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initSW();
		
		adder();
		
		addPlayTimerButtons();
		
		pack();
		setVisible(true);
	}
	
	boolean pause = false;
	boolean playing = false;
	PlayTimer pt = PlayTimer.getInstance();
	FISPlayer player;
	private void addPlayTimerButtons() {
		JButton playButton = new JButton("->");
		JButton pauseButton = new JButton("||");
		JButton stopButton = new JButton("[]");
		playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
System.out.println("Play pressed");
					long mikrosec = sw.getWaveDisplay().getCursorMicrosec();
					pt.registerForPush(sw);
					pt.setPlayTimeMicros(mikrosec);
					player.setTimePosition(mikrosec);
					pt.start();
					player.start();
					pause = false;
					playing = true;
			}});
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
System.out.println("Pressing pause, pause = "+pause);
				if(!playing)
					return;
				if(pause){
					pt.start();
					player.start();
				}
				else{
					pt.stop();
					player.stop();
				}
				pause = !pause;
			}});
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
System.out.println("Stop pressed");
				if(playing){
					playing = false;
					pt.stop();
					player.stop();
					pt.reset();
					player.reset();
					sw.setTimePosition(0);
					pause = false;
				}
			}});
			
		JPanel playTimerPanel = new JPanel();
		playTimerPanel.setLayout(new BoxLayout(playTimerPanel, BoxLayout.PAGE_AXIS));
		playTimerPanel.add(playButton);
		playTimerPanel.add(pauseButton);
		playTimerPanel.add(stopButton);
		getContentPane().add(playTimerPanel, BorderLayout.WEST);
	}

	private void adder(){
		getContentPane().setLayout(new BorderLayout());
		
		JPanel scollerPanel = new JPanel();
		scollerPanel.setLayout(new BoxLayout(scollerPanel, BoxLayout.LINE_AXIS));
		scollerPanel.add(toCursorButton);
		scollerPanel.add(toSelectButton);
		getContentPane().add(scollerPanel, BorderLayout.NORTH);
		
		getContentPane().add(sw, BorderLayout.CENTER);
		
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.LINE_AXIS));
		sliderPanel.add(vLabel);
		sliderPanel.add(vZoomSlider);
		sliderPanel.add(new JLabel("   |   "));
		sliderPanel.add(hLabel);
		sliderPanel.add(hZoomSlider);
		
		getContentPane().add(sliderPanel, BorderLayout.SOUTH);
	}
	
	private void initSW(){
		int width = 970;
		sw.setPreferredSize(new Dimension(width, 500));
//		sw.setAudioFileObject(new AudioFileObject(new URL(JOptionPane.showInputDialog(new JPanel(), "Please enter an URL"))));
		long availableSamples = 1;
		try {
			AudioFileObject afo = new AudioFileObject(new URL("file:///"+TestMP3Player.getFile()));
			availableSamples = afo.available();
			sw.setFloatPreviewReader(afo.getFloatPreviewReader());
			player = new FISPlayer(afo.getFloatInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sw.getWaveDisplay().addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
System.out.println("Setting Time");
				sw.getWaveDisplay().setCursorInDisplay(e.getX());
				long mikrosec = sw.getWaveDisplay().getCursorMicrosec();
//				if(!player.isPlaying())
//					pt.stop();
//				sw.registerForPush();
				pt.setPlayTimeMicros(mikrosec);
				player.setTimePosition(mikrosec);
			}
			public void mousePressed(MouseEvent e) {
				selector = e.getX();
			}
			public void mouseReleased(MouseEvent e) {
				sw.getWaveDisplay().finalizeSelection(selector, e.getX());
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		sw.getWaveDisplay().addMouseMotionListener(new MouseMotionListener(){

			public void mouseDragged(MouseEvent e) {
				sw.getWaveDisplay().makingSelection(selector, e.getX());
			}

			public void mouseMoved(MouseEvent e) {}
		});
		
		vZoomSlider.setOrientation(JSlider.HORIZONTAL);
		vZoomSlider.setMaximum(1000);
		vZoomSlider.setMinimum(100);
		vZoomSlider.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				vZoomSlider.setValue((int) (sw.setVerticalZoom(vZoomSlider.getValue()/100.0f)*100.0));	
			}});
		vZoomSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				vLabel.setText("Vert. Zoom "+String.valueOf(vZoomSlider.getValue()/100.0f));
			}});
			
		hZoomSlider.setOrientation(JSlider.HORIZONTAL);
//		double maxFactor = (double)availableSamples
//							/ width;
//		hZoomSlider.setMaximum((int)maxFactor+1);	// +1 um Stellen hinter dem Komma auszugleichen
		double maxFactor = (double)availableSamples
							/ width;
		hZoomSlider.setMaximum((int)(Math.log(maxFactor)/Math.log(2)+1));	// +1 um Stellen hinter dem Komma auszugleichen
		hZoomSlider.setMinimum(0);
		hZoomSlider.setValue(0);
		hZoomSlider.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				hZoomSlider.setValue((int) (Math.log(sw.setHorizontalZoom(1<<hZoomSlider.getValue()))/Math.log(2)));	
			}});
		hZoomSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				hLabel.setText("Horz. Zoom "+String.valueOf(1<<hZoomSlider.getValue()));
			}});
			
		toCursorButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sw.scrollToCursorPos(2);
			}});
			
		toSelectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sw.displaySelection();
				hZoomSlider.setValue((int) (Math.log(sw.getWaveDisplay().getDisplayScale())/Math.log(2)));	
			}});
		
	}
	
	int selector = 0;

	public static void main(String a[]) throws MalformedURLException, IOException {
		new TestShowWave();
	}




	// TODOS: 
			
			// in partReaders mapping einführen
			
			
			// und wenn WaveDisplay zu wenig Datenmengen pro Lese bekommt?
			
			// Fehler: setXOR macht misst, manchmal schwenkt es einfach um und bleibt dabei.
			// Woran kann es liegen?
			// austesten der Selection implementieren
			// isSelected-konzept überarbeiten
			// performance: variablengenerierung in viel aufgerufenen selection-methoden
			//      zum Teil wird Arbeit doppelt gemacht. Konzeptproblem.

			// resolution  caching
			// variable resolution display caching
			
			
			
			// PrewievReader - interface erstellen
			//            mit methode previewRead(...)
			// von FloatISPartReader & FISmp3PartReaer implementieren lassen
			//  AudioPreviewReader dadurch ersetzen
			
			// in FloatISPartReader mapping implementieren
			// in FISmp3PartReaer position() weiter verbessern
			//        zum Austesten:
			//           mp3-datei laden, und in wave konvertieren,
			//           wav und mp3 resetten, auf sample 0
			//           deren position - methoden vergleichen
			// um position() zu implementieren: debuggen, anfangend bei read() im inter-
			// nen InputStream (ChannelInputStream: bitte Namen ändern). Wer ruft aus
			// dem mp3-decoder read() auf? wie ist zu umgehen, dass alles gelesen und 
			// decodiert wird?	(dazu main-methode von den changern reinigen, braek-
			// point in read setzen, ...)
			
			// MP3Player debuggen: NegativeArraySizeException wird geworfen.
			// warum? keine Erklärung.	
			
			// read for play in AudioObject einfügen, play-daten und previwe-daten vom
			//  selben mapping kommen lassen - methoden synchronised machen
			
			// format aushandeln im audioObject (AudioObject.setAudioFormat(...)?)
			//           UnsupportedAudioFormat werfen,
			//            oder false zurückgeben, fals eins der nicht änderbaren Werte
			//            nicht dem unterstütztem entsprach?
			//  dann auch AudioObject.getAudioFormat()? 
			
			// 

}
