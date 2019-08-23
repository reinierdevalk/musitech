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
 * Created on 10.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;


/**
 * 
 * @author Kerstin Neubarth
 */
public class ObjectPlayerDisplay extends AbstractDisplay {
    
    private AudioFileObject afo = null;
    
    /**
	 * ObjectPlayer used to play back the <code>editObj</code>.
	 */
	private ObjectPlayer player = ObjectPlayer.getInstance();

    /** 
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    public void createGUI() {
        if (editObj instanceof AudioFileObject){
            afo = (AudioFileObject) editObj;
        } else {
            if (propertyValue instanceof AudioFileObject){
                afo = (AudioFileObject) propertyValue;
            }
        }
        setLayout(new BorderLayout());
        if (afo!=null)
            player.addAudioFileObject(afo);
		//create buttons
		TransportButtons transpButtons =
					new TransportButtons(player.getPlayTimer());
		transpButtons.setRecordingEnabled(false);
		add(transpButtons);
		//		//add buttons to GUI
		//		JPanel buttonPanel = new JPanel();
		//		buttonPanel.setLayout(new GridLayout(1, 0));
		//		buttonPanel.add(transpButtons.getPlayButton());	//TODO nur 1 "Listen"-button?
		//		buttonPanel.add(transpButtons.getStopButton());
		//		JPanel bottomPanel = new JPanel();
		//		bottomPanel.setLayout(new GridLayout(0, 1));
		//		bottomPanel.add(buttonPanel);
		//		bottomPanel.add(transpButtons.getTimeSlider());
		//		add(bottomPanel, BorderLayout.SOUTH);

    }

}
