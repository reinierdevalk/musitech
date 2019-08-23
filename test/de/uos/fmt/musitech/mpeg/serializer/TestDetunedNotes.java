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
 * Created on 12.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.mpeg.serializer;

import java.io.FileWriter;

import org.w3c.dom.Document;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.framework.time.ObjectToMidiFileProcessor;
import de.uos.fmt.musitech.performance.ScoreToPerfomance;
import de.uos.fmt.musitech.performance.midi.MidiWriter;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * @author Jan-H. Kramer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestDetunedNotes {

	Piece piece;
	ObjectToMidiFileProcessor OTMFileProc;
    NotationSystem notSystem;
	/**
	 * 
	 */
	public TestDetunedNotes() {

		init();
		createNotes();
		serializeNotes();
	}
	
	/**
	 * 
	 */
	private void init() {
		notSystem = new NotationSystem();
        piece = new Piece();
        piece.setScore(notSystem);
        piece.getContainerPool().add(notSystem);
        notSystem.setContext(piece.getContext());
		
	}

	/**
	 * 
	 */
	private void createNotes() {
		NotationStaff staff = new NotationStaff(notSystem);
        NotationVoice voice = new NotationVoice(staff);

        NotationChord[] chord = new NotationChord[15];
        ScoreNote[] scorNotes = new ScoreNote[15];
        Note[] notes = new Note[15];
        
        for (int i = 0; i < scorNotes.length; i++) {
            scorNotes[i] = new ScoreNote(new Rational(i,8), new Rational(1,8), 'c', (byte) 0, (byte) 0);
            scorNotes[i].setDetune(-100 + 4*i);
            notes[i] = new Note(scorNotes[i], new PerformanceNote());
            chord[i] = new NotationChord(piece.getContext());
            chord[i].add(notes[i]);
            voice.add(chord[i]);
        }
  
        
		
	}

	/**
	 * 
	 */
	private void serializeNotes() {
		
        
	    piece.setScore(NotationDisplay.createNotationSystem(piece));
		Document doc = MusiteXMLSerializer.newMPEGSerializer().serialize(piece);
		String xml = XMLHelper.asXML(doc);
		try {
			FileWriter fw = new FileWriter("tmp/detuned.xml");
			fw.write(xml);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ScoreToPerfomance ScoreConverter = new ScoreToPerfomance(notSystem);
        MidiWriter writer = new MidiWriter("tmp/detuned.mid");
        writer.setPiece(piece);
        writer.write();
		
	}

	public static void main(String[] args) {
		TestDetunedNotes test = new TestDetunedNotes();
	}
}
