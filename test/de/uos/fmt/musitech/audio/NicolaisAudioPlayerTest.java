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
 * Created on 28.06.2004
 */
package de.uos.fmt.musitech.audio;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;

/**
 * @author Nicolai Strauch
 */
public class NicolaisAudioPlayerTest {

	public static void main(String a[]) {
		player();
	}

	static void player(){
		final AFOPlayer player = new AFOPlayer();
		player.setContainer(getAudioFileObjects());
		// TODO: dumm, das der Benutzer nicht noch einiges zwischen FIS und
		// Player zwischenschalten kann. Z.B. ein VolumeController,
		// ein echo-Gerät, etc.
		// Und wenn player einem Object sein FIS übergibt, und da einiges mit
		// dem FIS
		// gemacht werden kann? Und dieses Object unter Kontrolle des Users
		// steht.
		player.play();
		AFOReceiver receiver = new AFOReceiver() {
			URL url;
			boolean end = false;
			public void rememberURL(URL u) {
				url = u;
			}
			public URL rememberURL() {
				return url;
			}
			public void setAFO(AudioFileObject afo) {
				player.addAudioFileObject(afo);
				System.out
						.println("TestAudioPlayer.player().receiver.setAFO(afo) is setting with Time: "
								+ afo.getTime()
								+ " and URL: "
								+ afo.getSourceURL());
			}
			public void finalise() {
				end = true;
			}
			public boolean finalized() {
				return end;
			}
		};
		afoProvider(receiver);
		JFrame sampleFrame = new JFrame("Samples Played");
		JPanel samplePanel = new JPanel();
		final JLabel sampleLabel = new JLabel("0");
		new Thread() {
			public void run() {
				while (true) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						sampleLabel.setText(String.valueOf(player.getSamplesPlayed()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}.start();
		sampleFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		samplePanel.add(sampleLabel);
		samplePanel.add(new JLabel("To exit close this window."));
		sampleFrame.getContentPane().add(samplePanel);
		sampleFrame.pack();
		sampleFrame.setVisible(true);
	}

	/**
	 * @param player
	 */
	private static void stopper(AFOPlayer player) throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("To stop please enter.");
		buff.readLine();
		player.stop();
		System.out.println("To quit please enter again.");
		buff.readLine();
		System.exit(0);
	}

	/**
	 * Fill a BasicContainer with AudioFileObjects. Quest playbegintime for
	 * every file, and the files from the user.
	 * @return @throws IOException
	 */
	static Container getAudioFileObjects() {
		final BasicContainer container = new BasicContainer();
		//		AudioFileObject afoTmp = getAudioFileObject();
		//		while(afoTmp != null){
		//			container.add(afoTmp);
		//			afoTmp = getAudioFileObject();
		//		}
		AFOReceiver receiver = new AFOReceiver() {
			URL url;
			boolean containerFull = false;
			public void rememberURL(URL u) {
				url = u;
			}
			public URL rememberURL() {
				return url;
			}
			public void setAFO(AudioFileObject afo) {
				container.add(afo);
				System.out
						.println("TestAudioPlayer.getAudioFileObjects().receiver.setAFO(afo) is setting with Time: "
								+ afo.getTime()
								+ " and URL: "
								+ afo.getSourceURL());
			}
			public void finalise() {
				containerFull = true;
			}
			public boolean finalized() {
				return containerFull;
			}
		};
		afoProvider(receiver);
		while (!receiver.finalized()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return container;
	}

	/**
	 * quest the user for a Timevalue in milliseconds, and for an AudioFile. If
	 * the Timevalue given is not a numeber, null will be returned
	 * @return
	 */
	static AudioFileObject getAudioFileObject() throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(
				System.in));
		String number = "";
		System.out
				.println("Please enter the millisecond at that the next stream have"
						+ " to begin to play in the Playsequence."
						+ " \n      By Entering any othe thing than a number, no more "
						+ "AudioFileObjects will be created. ");
		number = buff.readLine();
		long millisec = 0;
		try {
			millisec = Long.parseLong(number);
		} catch (NumberFormatException nfe) {
			return null;
		}
		URL url = requestURL();
		return new AudioFileObject(millisec, url);
	}

	/** quest the user for a file, and return the URL of the file. */
	static URL requestURL() throws IOException {
		return new File(TestMP3Player.getFile()).toURL();
	}
	/**
	 * Constructs a frame and quest an afo from User. Put this afo into an
	 * AFOReceiver
	 */
	public static void afoProvider(final AFOReceiver receiver) {
		final JFrame frame = new JFrame("Audio file request");
		JPanel panel = new JPanel();
		final JTextField textField = new JTextField(null,"0",12);
		JButton fileChooserButton = new JButton("Choose File");
		final JLabel chosenFileLabel = new JLabel("Chosen File");
		JButton sendAFOButton = new JButton("Load chosen File");

		// textField.setPreferredSize(new Dimension(800, 100));
		fileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = TestMP3Player.getFile();
				File file = new File(fileName);
				chosenFileLabel.setText("Chosen File: <" + fileName + ">");
				try {
					receiver.rememberURL(file.toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});

		sendAFOButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				URL url = receiver.rememberURL();
				if (url == null)
						JOptionPane.showInternalMessageDialog(frame,
								"first an File must be loaded.");
				long millis = 0;
				try {
					System.out.println(textField.getText() + "  ><  "
							+ textField.getText().getClass());
					millis = Long.parseLong((textField.getText()));
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane
							.showMessageDialog(
									null,
									"first an millisecondvalue as begintime (begin in the Playersequence) for the file must be placed in the Textfield.");
				}
				AudioFileObject afo = null;
				try {
					afo = new AudioFileObject(millis, url);
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				receiver.setAFO(afo);
			}
		});
		panel.add(textField);
		panel.add(fileChooserButton);
		panel.add(chosenFileLabel);
		panel.add(sendAFOButton);
		panel.setPreferredSize(new Dimension(900, 200));

		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent ev) {
				receiver.finalise();
				frame.removeAll();
				frame.setVisible(false);
			}
		});

		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	interface AFOReceiver {
		void rememberURL(URL url);
		URL rememberURL();
		void setAFO(AudioFileObject afo);
		void finalise();
		boolean finalized();
	}




}
