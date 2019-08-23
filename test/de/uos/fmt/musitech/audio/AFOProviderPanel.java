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
package de.uos.fmt.musitech.audio;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.uos.fmt.musitech.audio.floatStream.FISAFOConnector;
import de.uos.fmt.musitech.data.audio.AudioFileObject;

/**
 * A simple GUI-Panel for creating AudioFileObjects  
 * 
 * @author Nicolai Strauch and Tillman Weyde
 */
public class AFOProviderPanel extends JPanel {

	private FISAFOConnector connector;
	private AFOPlayButtons afoPlayButtons;

	private JLabel textFieldLabel = new JLabel("Starting point in File");
	private JTextField textField = new JTextField(null,"0",12);
	private JButton fileChooserButton = new JButton("Choose File");
	private JLabel choosedFileLabel = new JLabel("Chosen File:");
	//	JButton sendAFOButton = new JButton("Load choosen File");

	private File fileToLoad;

	/**
	 * Constructs a frame and quest an afo from User. Put this afo into an AFOReceiver
	 * 
	 * @param fisAFOconnector
	 */
	public AFOProviderPanel(FISAFOConnector fisAFOconnector, AFOPlayButtons afoPlayB) {
		connector = fisAFOconnector;
		afoPlayButtons = afoPlayB;

		textField.setText("0");

		// textField.setPreferredSize(new Dimension(800, 100));
		fileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = requestFile();
				fileToLoad = new File(fileName);
				long millis = 0;
				try {
					millis = Long.parseLong((textField.getText()));
				} catch (Exception e) {
				}
				AudioFileObject afo = null;
				try {
					afo = new AudioFileObject(millis, fileToLoad.toURL());
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				choosedFileLabel.setText(choosedFileLabel.getText() + "\n<" + fileName
											+ "> beginning at the "
											+ (millis / 1000000.0f)
											+ "second of the file");
				connector.addAudioFileObject(afo);
				afoPlayButtons.setSliderRange();
			}
		});

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel beginSecondPanel = new JPanel();
		beginSecondPanel.setLayout(new BoxLayout(beginSecondPanel, BoxLayout.Y_AXIS));
		beginSecondPanel.add(textFieldLabel);
		beginSecondPanel.add(textField);
		add(beginSecondPanel);
		JPanel fileChooserPanel = new JPanel();
		fileChooserPanel.setLayout(new BoxLayout(fileChooserPanel, BoxLayout.Y_AXIS));
		fileChooserPanel.add(fileChooserButton);
		fileChooserPanel.add(choosedFileLabel);
		add(fileChooserPanel);
		//setPreferredSize(new Dimension(900, 200));

	}

	/**
	 * TODO comment
	 * @return
	 */
	public static String requestFile() {
		FileDialog fileChooser = new FileDialog(new Frame());
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.show();
		if (fileChooser.getFile() == null)
			System.out.println("Problems with FileDialog.");
		return fileChooser.getDirectory() + fileChooser.getFile();
	}

}