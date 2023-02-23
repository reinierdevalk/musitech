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
 * Created on Nov 2, 2004
 */
package de.uos.fmt.musitech.mpeg.serializer;

import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.w3c.dom.Document;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * @author jens
 */
public abstract class MPEGDiffViewer extends JFrame {

    ScoreMapper mapper;
    protected NotationSystem system;
    public Context context = new Piece().getContext();

    public Piece deserialized;

    public abstract NotationSystem createNotationSystem();

    Rational addnext(NotationVoice voice, Rational onset, ScorePitch p, Rational dur) {
        return addnext(voice, onset, new ScorePitch[] {p}, dur);
    }

    Rational addnext(NotationVoice voice, Rational onset, ScorePitch[] p, Rational dur) {
        NotationChord nchord = new NotationChord(context);
        for (int i = 0; i < p.length; i++)
            nchord.add(new Note(new ScoreNote(p[i], onset, dur), null));
        voice.add(nchord);
        onset = onset.add(dur);
        return onset;
    }

    Rational r64() {
        return new Rational(1, 64);
    }

    Rational r32() {
        return new Rational(1, 32);
    }

    protected Rational r16() {
        return new Rational(1, 16);
    }

    protected Rational r8() {
        return new Rational(1, 8);
    }

    protected Rational r4() {
        return new Rational(1, 4);
    }

    Rational r2() {
        return new Rational(1, 2);
    }

    Rational r1() {
        return new Rational(1, 1);
    }

    protected ScorePitch a(int oct) {
        return new ScorePitch('a', oct, 0);
    }

    protected ScorePitch b(int oct) {
        return new ScorePitch('b', oct, 0);
    }

    protected ScorePitch c(int oct) {
        return new ScorePitch('c', oct, 0);
    }

    protected ScorePitch d(int oct) {
        return new ScorePitch('d', oct, 0);
    }

    protected ScorePitch e(int oct) {
        return new ScorePitch('e', oct, 0);
    }

    protected ScorePitch f(int oct) {
        return new ScorePitch('f', oct, 0);
    }

    protected ScorePitch g(int oct) {
        return new ScorePitch('g', oct, 0);
    }

    protected ScorePitch r() {
        return new ScorePitch('r', 0, 0);
    }

    protected ArrayList getScoreNotes(NoteList voice, int from, int to) {
        ArrayList notes = new ArrayList();

        for (int i = from; i <= to; i++) {
            Note note = voice.get(i);
            notes.add(note);
        }

        return notes;
    }

    Collection getNotes(NoteList voice, int from, int to) {
        ArrayList notes = new ArrayList();

        for (int i = from; i <= to; i++) {
            Note note = voice.get(i);
            notes.add(note);
        }

        return notes;
    }

    ArrayList getNotationChords(NotationVoice voice, int from, int to) {
        ArrayList notes = new ArrayList();

        for (int i = from; i <= to; i++) {
            NotationChord chord = voice.get(i);
            notes.add(chord);
        }

        return notes;
    }

    public MPEGDiffViewer() {
        this(null);
    }

    public MPEGDiffViewer(String filename) {
        super();
        this.setExtendedState(MAXIMIZED_BOTH);
        createNotationSystem();

        if (filename != null) {
            Piece piece = context.getPiece();
            piece.setScore(NotationDisplay.createNotationSystem(piece));
            Document doc = MusiteXMLSerializer.newMPEGSerializer().serialize(piece);
            String xml = XMLHelper.asXML(doc);
            try {
                FileWriter fw = new FileWriter(filename);
                fw.write(xml);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            doc = XMLHelper.parse(xml);
            this.deserialized = (Piece) MusiteXMLSerializer.newMPEGSerializer().deserialize(doc,new File(filename).getParentFile().toURI());
        }

        try {
            initGUI();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    synchronized void initGUI() throws EditorConstructionException {
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        getContentPane().setLayout(new GridLayout(2, 1));

        /*
         * display piece as before serialization
         */
        NotationDisplay before = null;
        ScorePanel after = null;
        //display = (NotationDisplay)EditorFactory.createDisplay(system);
        before = (NotationDisplay) EditorFactory.createDisplay(context.getPiece());
        before.setOpaque(true);
        before.setAutoZoom(false);
        ScrollPane beforeScroll = new ScrollPane();
        beforeScroll.add(before);
        getContentPane().add(beforeScroll);

        /*
         * display piece as after deserialization
         */
        if (deserialized != null) {
            after = new ScorePanel(deserialized.getScore());
            after.setAutoZoom(false);
            after.setOpaque(true);
            ScrollPane afterScroll = new ScrollPane();
            afterScroll.add(after);
            getContentPane().add(afterScroll);

        } else {
            ScrollPane afterScroll = new ScrollPane();
            afterScroll.add(new JLabel("deserialization failed"));
            getContentPane().add(afterScroll);
        }

        
        validate();

        //getContentPane().add(before, BorderLayout.WEST);
        //getContentPane().add(after, BorderLayout.EAST);

        pack();
        setSize(800,600);
    }

}