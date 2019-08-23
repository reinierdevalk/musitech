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
 * Created on 2005-1-7
 */
package de.uos.fmt.musitech.mpeg.serializer;

import java.util.Collection;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.mpeg.testcases.TestCase2_8;
import de.uos.fmt.musitech.score.NotationDisplay;

/**
 * @author Administrator
 */
public class DiffTestCase2_8 extends MPEGDiffViewer {

    /**
     * @see de.uos.fmt.musitech.mpeg.serializer.MPEGDiffViewer#createNotationSystem()
     */
    public NotationSystem createNotationSystem() {
		TestCase2_8.fillPiece(context.getPiece());
		NotationDisplay display = null;
		try {
			display = (NotationDisplay)EditorFactory.createDisplay(context.getPiece());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		NotationSystem system = display.getScorePanel().getNotationSystem();
		TestCase2_8.fillNotationSystem(system);
		Collection systems = system.splitAtLineBreaks();
		Collection lyrics = ((LyricsSyllableSequence)((NotationVoice)((NotationStaff)system.get(0)).get(0)).getLyrics().getVerse((byte)0)).splitAtLinebreaks(system.getLinebreaks());
		
		/*
		Box vBox = new Box(BoxLayout.Y_AXIS);
		getContentPane().add(vBox, BorderLayout.CENTER);
		
		Iterator lyricsIterator = lyrics.iterator();
		for (Iterator iter = systems.iterator(); iter.hasNext();) {
			NotationSystem splitSystem = (NotationSystem) iter.next();
			LyricsSyllableSequence splitLyrics = (LyricsSyllableSequence)lyricsIterator.next();
			NotationDisplay splitDisplay;
			LyricsDisplay lyricsDisplay;
			try {
				HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
				splitDisplay = (NotationDisplay)EditorFactory.createDisplay(splitSystem);
				lyricsDisplay = (LyricsDisplay)EditorFactory.createDisplay(splitLyrics);

				coord.registerDisplay(splitDisplay);
				coord.registerDisplay(lyricsDisplay);
				coord.doPositioning();
				
				vBox.add(splitDisplay);
				vBox.add(lyricsDisplay);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		pack();*/
        return system;
    }

    public DiffTestCase2_8(String file) {
        super(file);
    }

    public static void main(String argv[]) {
        DiffTestCase2_8 m = (new DiffTestCase2_8("tmp/Diff2_8.xml"));
        m.setVisible(true);
    }

}