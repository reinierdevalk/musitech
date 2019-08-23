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
 * Created on 30.12.2004
 */
package de.uos.fmt.musitech.mpeg.serializer;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BarlineContainer;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Administrator
 */
public class Melody1 extends MPEGDiffViewer {

    /**
     * @see de.uos.fmt.musitech.mpeg.testcases.TestCase#createNotationSystem()
     */
    public NotationSystem createNotationSystem() {
        context.getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));
        KeyMarker marker = new KeyMarker(Rational.ZERO, 0);
        marker.setAccidentalNum(1);
        context.getPiece().getMetricalTimeLine().add(marker);

        BasicContainer fluteStaff = new StaffContainer(context);
        fluteVoice(fluteStaff);

        Container cont = context.getPiece().getContainerPool();
        cont.add(fluteStaff);

        return system;
    }

    public Melody1(String file) {
        super(file);
    }

    public static void main(String argv[]) {
        Melody1 m = (new Melody1("tmp/Melody1.xml"));
        m.setVisible(true);
    }

    void fluteVoice(Container cont) {
        NoteList notes = new NoteList(context);
        Voice voice = new Voice(context);
        voice.add(notes);
        BarlineContainer barlineContainer = new BarlineContainer();

        //Measure 1:
        notes.addnext(g(1), r8());
        notes.addnext(f(1).sharp(), r16());
        notes.addnext(g(1), r16());

        context.getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3,4,notes.getMetricDuration()));
        //barlineContainer.add(new Barline(notes.getMetricDuration()));

        //Measure 2:
        notes.addnext(d(1), r4(), Accent.STACCATO);
        notes.addnext(d(1), r4(), Accent.STACCATO);
        notes.addnext(d(1), r8());
        notes.addnext(c(1).sharp(), r16());
        notes.addnext(d(1), r16());

        barlineContainer.add(new Barline(notes.getMetricDuration()));

        //Measure 3:
        notes.addnext(b(0), r4(), Accent.STACCATO);
        notes.addnext(b(0), r4(), Accent.STACCATO);
        notes.addnext(b(0), r8());
        notes.addnext(a(0), r16());
        notes.addnext(g(0), r16());

        barlineContainer.add(new Barline(notes.getMetricDuration()));

        //Measure 4:
        notes.addnext(f(0).sharp(), r8(), Accent.STACCATO);
        notes.addnext(g(0), r8(), Accent.STACCATO);
        notes.addnext(g(0).sharp(), r8(), Accent.STACCATO);
        notes.addnext(a(0), r8(), Accent.STACCATO);
        notes.addnext(b(0).flat(), r8(), Accent.STACCATO);
        notes.addnext(b(0), r8(), Accent.STACCATO);

        barlineContainer.add(new Barline(notes.getMetricDuration()));

        //Measure 5:
        notes.addnext(c(1), r4());
        notes.addnext(r(), r4());
        notes.addnext(c(1), r8());
        notes.addnext(b(0), r16());
        notes.addnext(c(1), r16());

        cont.add(voice);
        cont.add(barlineContainer);

        //Measure 1:

        cont.add(new BeamContainer(context, getScoreNotes(notes, 0, 2)));
        cont.add(new SlurContainer(context, getScoreNotes(notes, 0, 2)));

        //Measure 2:
        cont.add(new BeamContainer(context, getScoreNotes(notes, 5, 7)));
        cont.add(new SlurContainer(context, getScoreNotes(notes, 5, 7)));

        //Measure 3:

        cont.add(new BeamContainer(context, getScoreNotes(notes, 10, 12)));
        cont.add(new SlurContainer(context, getScoreNotes(notes, 10, 12)));

        //Measure 4:
        cont.add(new BeamContainer(context, getScoreNotes(notes, 13, 18)));

        //Measure 5:

        cont.add(new BeamContainer(context, getScoreNotes(notes, 21, 23)));
        cont.add(new SlurContainer(context, getScoreNotes(notes, 21, 23)));

        ScoreNote n = new ScoreNote(a(1), Rational.ZERO, Rational.ZERO);
        RenderingHints rh = new RenderingHints();
        rh.registerHint("appoggiatura", new Rational(1, 16));
        notes.add(n).setRenderingHints(rh);

        ScoreNote n2 = new ScoreNote(e(1), new Rational(3, 4), Rational.ZERO);
        notes.add(n2).setRenderingHints(rh);

        ScoreNote n3 = new ScoreNote(c(1), new Rational(6, 4), Rational.ZERO);
        notes.add(n3).setRenderingHints(rh);

        ScoreNote n4 = new ScoreNote(d(1), new Rational(12, 4), Rational.ZERO);
        notes.add(n4).setRenderingHints(rh);
    }
}