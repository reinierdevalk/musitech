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
 * Created on Dec 28, 2004
 *
 */
package de.uos.fmt.musitech.mpeg.testcases;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageURL;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileManager;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.persistence.PersistenceFactory;
import de.uos.fmt.musitech.framework.persistence.PersistenceManager;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *  
 */
public class BaroqueAlignmentTest extends JFrame {

    ScoreMapper mapper;
    NotationSystem system;
    NotationDisplay display = null;

    public static void fillNotationSystem(NotationSystem system) {

    }

    @SuppressWarnings("unchecked") 
    //TODO check for clean solution to type problem, see below
	public static void fillPiece(Piece piece) {

        Container cp = piece.getContainerPool();
        Context context = piece.getContext();
        // Tempo
        piece.getMetricalTimeLine().setTempo(Rational.ZERO, 100, 8);
        TimeSignatureMarker timeSignatureMarker = new TimeSignatureMarker(4, 8, Rational.ZERO);
        RenderingHints renderingHints = new RenderingHints();
        renderingHints.registerHint("visible", Boolean.FALSE);
        timeSignatureMarker.setRenderingHints(renderingHints);
        piece.getMetricalTimeLine().add(timeSignatureMarker);

        NoteList nl = new NoteList(context);

        nl.addnext(new ScorePitch('f', 1, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('a', 0, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('g', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('e', 1, 0), new Rational(1, 16));

//        cp.add(new Barline(new Rational(4, 8)));

        nl.add(new ScorePitch('d', 1, 0), new Rational(8, 16), new Rational(1, 16));
        nl.add(new ScorePitch('c', 1, 0), new Rational(13, 24), new Rational(1, 16));
        nl.add(new ScorePitch('b', 0, 0).flat(), new Rational(14, 24), new Rational(1, 16));
        nl.add(new ScorePitch('c', 1, 0), new Rational(15, 24), new Rational(1, 8));
        nl.add(new ScorePitch('c', 1, 0), new Rational(3, 4), new Rational(1, 16));
        nl.add(new ScorePitch('b', 0, 0).flat(), new Rational(19, 24), new Rational(1, 16));
        nl.add(new ScorePitch('a', 0, 0), new Rational(20, 24), new Rational(1, 16));
        nl.add(new ScorePitch('b', 0, 0).flat(), new Rational(21, 24), new Rational(1, 16));
        nl.add(new ScorePitch('c', 1, 0), new Rational(22, 24), new Rational(1, 16));
        nl.add(new ScorePitch('d', 1, 0), new Rational(23, 24), new Rational(1, 16));

//        cp.add(new Barline(new Rational(11, 8)));

  
        nl.add(new ScorePitch('a', 0, 0), new Rational(8, 8), new Rational(3, 8));

        Barline barline = new Barline(new Rational(11, 8));
        barline.setBarlineBold(true);
        cp.add(barline);

        //cp.add(new Linebreak(new Rational(11, 8)));

        NoteList nl3 = new NoteList(context);

        nl3.add(new ScorePitch('a', 0, 0), new Rational(0, 8), new Rational(1, 8));
        nl3.add(new ScorePitch('r', 1, 0), new Rational(1, 8), new Rational(1, 8));
        nl3.add(new ScorePitch('r', 1, 0), new Rational(2, 8), new Rational(1, 16));

        nl3.add(new ScorePitch('b', 0, 0).flat(), new Rational(5, 16), new Rational(1, 16));
        nl3.add(new ScorePitch('a', 0, 0), new Rational(6, 16), new Rational(1, 16));
        nl3.add(new ScorePitch('g', 0, 0), new Rational(7, 16), new Rational(1, 16));

        cp.add(new Barline(new Rational(30, 16)));

        nl3.add(new ScorePitch('f', 0, 0), new Rational(8, 16), new Rational(1, 16));
        nl3.add(new ScorePitch('e', 0, 0), new Rational(13, 24), new Rational(1, 16));
        nl3.add(new ScorePitch('d', 0, 0), new Rational(14, 24), new Rational(1, 16));
        nl3.add(new ScorePitch('a', 0, 0), new Rational(15, 24), new Rational(1, 8));
        nl3.add(new ScorePitch('a', 0, 0), new Rational(3, 4), new Rational(1, 8));
        nl3.add(new ScorePitch('g', 0, 0), new Rational(7, 8), new Rational(1, 16));
        nl3.add(new ScorePitch('a', 0, 0), new Rational(11, 12), new Rational(1, 16));
        nl3.add(new ScorePitch('b', 0, 0).flat(), new Rational(23, 24), new Rational(1, 16));

        cp.add(new Barline(new Rational(19, 8)));

        nl3.add(new ScorePitch('f', 0, 0).sharp(), new Rational(1, 1), new Rational(3, 8));

        cp.add(new Barline(new Rational(133, 48)));

        NoteList nl2 = new NoteList(context);
        nl2.add(new ScorePitch('d', -1, 0), new Rational(0, 1), new Rational(1, 16));
        nl2.add(new ScorePitch('a', -2, 0), new Rational(1, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('d', -1, 0), new Rational(2, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('f', -1, 0), new Rational(1, 8), new Rational(1, 16));
        nl2.add(new ScorePitch('d', -1, 0), new Rational(4, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('f', -1, 0), new Rational(5, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('a', -1, 0), new Rational(2, 8), new Rational(1, 8));
        nl2.add(new ScorePitch('a', -2, 0), new Rational(3, 8), new Rational(1, 8));

        nl2.add(new ScorePitch('d', -1, 0), new Rational(8, 16), new Rational(1, 8));
        nl2.add(new ScorePitch('d', -1, 0), new Rational(10, 16), new Rational(1, 16));
        nl2.add(new ScorePitch('f', -1, 0).sharp(), new Rational(16, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('g', -1, 0), new Rational(17, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('a', -1, 0), new Rational(18, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('b', -1, 0).flat(), new Rational(19, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('c', 0, 0), new Rational(20, 24), new Rational(1, 16));
        nl2.add(new ScorePitch('d', 0, 0), new Rational(21, 24), new Rational(3, 32));
        nl2.add(new ScorePitch('d', -1, 0), new Rational(31, 32), new Rational(1, 32));

        nl2.add(new ScorePitch('d', -1, 0), new Rational(8, 8), new Rational(3, 8));

        NoteList nl4 = new NoteList(context);

        nl4.add(new ScorePitch('f', -1, 0), new Rational(0, 8), new Rational(3, 32));
        nl4.add(new ScorePitch('a', -1, 0), new Rational(3, 32), new Rational(1, 32));
        nl4.add(new ScorePitch('d', 0, 0), new Rational(4, 32), new Rational(1, 4));
        nl4.add(new ScorePitch('c', 0, 0).sharp(), new Rational(12, 32), new Rational(1, 8));

        nl4.add(new ScorePitch('d', 0, 0), new Rational(16, 32), new Rational(3, 32));
        nl4.add(new ScorePitch('g', -1, 0), new Rational(19, 32), new Rational(1, 32));
        nl4.add(new ScorePitch('f', -1, 0).sharp(), new Rational(20, 32), new Rational(3, 32));
        nl4.add(new ScorePitch('e', -1, 0), new Rational(23, 32), new Rational(1, 32));
        nl4.add(new ScorePitch('d', -1, 0), new Rational(24, 32), new Rational(3, 32));
        nl4.add(new ScorePitch('c', -1, 0), new Rational(27, 32), new Rational(1, 32));
        nl4.add(new ScorePitch('b', -1, 0).flat(), new Rational(28, 32), new Rational(1, 16));
        nl4.add(new ScorePitch('a', -1, 0), new Rational(11, 12), new Rational(1, 16));
        nl4.add(new ScorePitch('g', -1, 0), new Rational(23, 24), new Rational(1, 16));

        nl4.add(new ScorePitch('d', -1, 0), new Rational(1, 1), new Rational(3, 8));

        StaffContainer<Containable> staff = new StaffContainer<Containable>(context);
        StaffContainer<Containable> staff2 = new StaffContainer<Containable>(context);
        StaffContainer<Containable> staff3 = new StaffContainer<Containable>(context);
        StaffContainer<Containable> staff4 = new StaffContainer<Containable>(context);

        Voice<Containable> voice = new Voice<Containable>(context);
        voice.addAll(nl.getContentsRecursiveList(null));
        // TODO using getContentsRecursiveList is a workaround for avoiding type problems, look for cleaner solution 

        Voice<Containable> voice2 = new Voice<Containable>(context);
        voice2.addAll(nl2.getContentsRecursiveList(null));

        Voice voice3 = new Voice(context);
        voice3.addAll(nl3.getContentsRecursiveList(null));

        Voice voice4 = new Voice(context);
        voice4.addAll(nl4.getContentsRecursiveList(null));

        staff.add(voice);
        staff2.add(voice2);
        staff3.add(voice3);
        staff4.add(voice4);

        staff2.add(new Clef('f', 1, Rational.ZERO));
        staff4.add(new Clef('f', 1, Rational.ZERO));

        BeamContainer bc1 = new BeamContainer(context);
        BeamContainer bc2 = new BeamContainer(context);
        for (int i = 0; i < 4; i++)
            bc1.add(nl.get(i));
        for (int i = 4; i < 8; i++)
            bc2.add(nl.get(i));
        voice.add(bc1);
        voice.add(bc2);

        BeamContainer bc3 = new BeamContainer(context);
        bc3.add(nl2.get(6));
        bc3.add(nl2.get(7));
        voice2.add(bc3);

        BeamContainer bc4 = new BeamContainer(context);
        bc4.add(nl2.get(15));
        bc4.add(nl2.get(16));
        voice2.add(bc4);

        BeamContainer bc5 = new BeamContainer(context);
        for (int i = 3; i < 6; i++)
            bc5.add(nl3.get(i));
        voice3.add(bc5);

        BeamContainer bc6 = new BeamContainer(context);
        bc6.add(nl4.get(0));
        bc6.add(nl4.get(1));
        voice4.add(bc6);

        BeamContainer bc7 = new BeamContainer(context);
        for (int i = 4; i < 8; i++)
            bc7.add(nl4.get(i));
        voice4.add(bc7);

        BeamContainer bc8 = new BeamContainer(context);
        bc8.add(nl4.get(8));
        bc8.add(nl4.get(9));
        voice4.add(bc8);

        SlurContainer sl1 = new SlurContainer(context);
        sl1.add(nl.get(11));
        sl1.add(nl.get(12));
        voice.add(sl1);

        SlurContainer sl2 = new SlurContainer(context);
        sl2.add(nl2.get(8));
        sl2.add(nl2.get(9));
        voice2.add(sl2);

        SlurContainer sl3 = new SlurContainer(context);
        sl3.add(nl3.get(9));
        sl3.add(nl3.get(10));
        voice3.add(sl3);

        TupletContainer tc1 = new TupletContainer(context, (byte) 3);
        for (int i = 0; i < 3; i++)
            tc1.add(nl2.get(i));
        tc1.setMetricDuration(new Rational(1, 8));
        voice2.add(tc1);

        TupletContainer tc2 = new TupletContainer(context, (byte) 3);
        for (int i = 3; i < 6; i++)
            tc2.add(nl2.get(i));
        tc2.setMetricDuration(new Rational(1, 8));
        voice2.add(tc2);

        TupletContainer tc3 = new TupletContainer(context, (byte) 3);
        for (int i = 8; i < 11; i++)
            tc3.add(nl.get(i));
        tc3.setMetricDuration(new Rational(1, 8));
        voice.add(tc3);

        TupletContainer tc4 = new TupletContainer(context, (byte) 3);
        for (int i = 12; i < 15; i++)
            tc4.add(nl.get(i));
        tc4.setMetricDuration(new Rational(1, 8));
        voice.add(tc4);

        TupletContainer tc5 = new TupletContainer(context, (byte) 3);
        for (int i = 15; i < 18; i++)
            tc5.add(nl.get(i));
        tc5.setMetricDuration(new Rational(1, 8));
        voice.add(tc5);

        TupletContainer tc6 = new TupletContainer(context, (byte) 3);
        for (int i = 9; i < 12; i++)
            tc6.add(nl2.get(i));
        tc6.setMetricDuration(new Rational(1, 8));
        voice2.add(tc6);

        TupletContainer tc7 = new TupletContainer(context, (byte) 3);
        for (int i = 12; i < 15; i++)
            tc7.add(nl2.get(i));
        tc7.setMetricDuration(new Rational(1, 8));
        voice2.add(tc7);

        TupletContainer tc8 = new TupletContainer(context, (byte) 3);
        for (int i = 6; i < 9; i++)
            tc8.add(nl3.get(i));
        tc8.setMetricDuration(new Rational(1, 8));
        voice3.add(tc8);

        TupletContainer tc9 = new TupletContainer(context, (byte) 3);
        for (int i = 11; i < 14; i++)
            tc9.add(nl3.get(i));
        tc9.setMetricDuration(new Rational(1, 8));
        voice3.add(tc9);

        TupletContainer tc10 = new TupletContainer(context, (byte) 3);
        for (int i = 10; i < 13; i++)
            tc10.add(nl4.get(i));
        tc10.setMetricDuration(new Rational(1, 8));
        voice4.add(tc10);

        CharSymbol fermat = new CharSymbol((char) 117);
        CharSymbol mordentup = new CharSymbol((char) 108);

        MetricAttachable ma = new MetricAttachable(nl.get(18), fermat);
        ma.setAlignment(MetricAttachable.CENTER);
        ma.setDistance(1);
        staff.add(ma);

        MetricAttachable ma2 = new MetricAttachable(nl2.get(17), fermat);
        ma2.setAlignment(MetricAttachable.CENTER);
        ma2.setDistance(1);
        staff2.add(ma2);

//        MetricAttachable ma3 = new MetricAttachable(nl4.get(3), mordentup);
        ((Note)nl4.get(3)).getScoreNote().addAccent(Accent.MORDENT_UP);
//        ma3.setRelativePosition(MetricAttachable.NORTH);
//        ma3.setAlignment(MetricAttachable.CENTER);
//        ma3.setDistance(1);
//        staff4.add(ma3);

        MetricAttachable ma4 = new MetricAttachable(nl3.get(14), fermat);
        ma4.setAlignment(MetricAttachable.CENTER);
        ma4.setDistance(1);
        staff3.add(ma4);

        MetricAttachable ma5 = new MetricAttachable(nl4.get(13), fermat);
        ma5.setAlignment(MetricAttachable.CENTER);
        ma5.setDistance(1);
        staff4.add(ma5);

        cp.add(staff);
        cp.add(staff2);
        cp.add(staff3);
        cp.add(staff4);

        staff.addRenderingHint("note spacing", new Float(2));
        staff2.addRenderingHint("note spacing", new Float(2));

        cp.addRenderingHint("alignment", "baroque");

        KeyMarker km = new KeyMarker(Rational.ZERO, 0);
        km.setAccidentalNum(-1);
        piece.getHarmonyTrack().add(km);

        addColorRHs(piece);
        fillMetaData(piece);
//        fillInMetaDataNotes(piece);
    }
    
    public static void addColorRHs(Piece piece){
        String[] selectionColors = {"8F0000", "008F00", "00008F"};
        if (piece.getScore() == null) {
            piece.setScore(NotationDisplay.createNotationSystem(piece));
        }
        Containable[] contents = piece.getScore().getContentsRecursive();
        //List containers = new ArrayList();
        int colorCounter = 0;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] instanceof NotationStaff || contents[i] instanceof NotationVoice) {
                ((Container)contents[i]).addRenderingHint("color", selectionColors[colorCounter++ % selectionColors.length]);
            }
        }
        
    }
     

    public static void fillMetaData(Piece piece) {
        Map<MObject, MetaDataCollection> metaMap = piece.getMetaMap();
        if (metaMap == null) {
            metaMap = new HashMap<MObject, MetaDataCollection>();
            piece.setMetaMap(metaMap);
        }
        //meta data for Piece
        MetaDataCollection mdPiece = new MetaDataCollection();
        mdPiece.addProfile(MetaDataProfileManager.getMetaDataProfile("Piece"));
        mdPiece.addProfile(MetaDataProfileManager.getMetaDataProfile("Recording"));
        MetaDataItem itemComposer = new MetaDataItem("Composer", "Bach, Johann Sebastian");
        mdPiece.addMetaDataItem(itemComposer);
        MetaDataItem itemTitle = new MetaDataItem("Title", "Die Kunst der Fuge");
        mdPiece.addMetaDataItem(itemTitle);
        MetaDataItem itemInstr = new MetaDataItem("Instrumentation in Recording", "Harpsichord");
        itemInstr.getMetaDataValue().addValueForLanguage("Harpsichord", "en");
        itemInstr.getMetaDataValue().addValueForLanguage("Cembalo", "de");
        itemInstr.getMetaDataValue().addValueForLanguage("Clavicembalo", "it");
        mdPiece.addMetaDataItem(itemInstr);
        metaMap.put(piece, mdPiece);
        //gather containers for which to create meta data
        if (piece.getScore() == null) {
            piece.setScore(NotationDisplay.createNotationSystem(piece));
        }
        Containable[] contents = piece.getScore().getContentsRecursive();
        //        Containable[] contents =
        // piece.getContainerPool().getContentsRecursive();
        List<Container<?>> containers = new ArrayList<Container<?>>();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] instanceof NotationStaff || contents[i] instanceof NotationVoice) {
                containers.add((Container<?>)contents[i]);
            }
        }
        //create meta data for containers
        List<MetaDataCollection> metadata = new ArrayList<MetaDataCollection>();
        MetaDataCollection mdColl1 = new MetaDataCollection();
        mdColl1.addMetaDataItem(new MetaDataItem("Composer", "Bach, Johann Sebastian"));
        ImageURL imageBach = new ImageURL(BaroqueAlignmentTest.class.getResource("Bach.gif"));
        MetaDataValue valueImage = new MetaDataValue("Image", imageBach);
        MetaDataItem itemBach = new MetaDataItem("Portrait");
        itemBach.setMetaValue(valueImage);
        mdColl1.addMetaDataItem(itemBach);
        metadata.add(mdColl1);
        MetaDataCollection mdColl2 = new MetaDataCollection();
        AudioFileObject audio = null;
        URL audioURL = BaroqueAlignmentTest.class.getResource("tabla01.wav");
        try {
            audio = new AudioFileObject(audioURL);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (audio != null) {
            MetaDataValue valueWave = new MetaDataValue("WaveDisplay", audio);
            MetaDataItem itemWave = new MetaDataItem("Wave Form");
            itemWave.setMetaValue(valueWave);
            mdColl2.addMetaDataItem(itemWave);
            MetaDataValue valuePlayer = new MetaDataValue("Player", audio);
            MetaDataItem itemPlayer = new MetaDataItem("Sound");
            itemPlayer.setMetaValue(valuePlayer);
            mdColl2.addMetaDataItem(itemPlayer);
        }
        metadata.add(mdColl2);
        MetaDataCollection mdColl3 = new MetaDataCollection();
        mdColl3.addMetaDataItem(new MetaDataItem("Place of Recording", "University of Osnabrueck"));
        metadata.add(mdColl3);
        MetaDataCollection mdColl4 = new MetaDataCollection();
        ImageURL imageCastle;
        try {
            imageCastle = new ImageURL(new URL("http://www.uos.de/schlosspu.jpg"));
            MetaDataValue value = new MetaDataValue("Image", imageCastle);
            MetaDataItem itemCastle = new MetaDataItem("Place of Recording");
            itemCastle.setMetaValue(value);
            mdColl4.addMetaDataItem(itemCastle);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        } 
        metadata.add(mdColl4);
        MetaDataCollection mdColl5 = new MetaDataCollection();
//      store piece in PersistenceManager
        PersistenceManager pm = PersistenceFactory.getDefaultPersistenceManager();
        try {
            pm.store(mdPiece);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        Long idPiece = mdPiece.getUid();
        if (idPiece != null) {
            MetaDataItem itemLink = new MetaDataItem("Link to MUSITECH database");
            MetaDataValue valueLink = new MetaDataValue("MusitechLink", idPiece);
            itemLink.setMetaValue(valueLink);
            mdColl5.addMetaDataItem(itemLink);
            metadata.add(mdColl5);
        }
        MetaDataCollection mdColl6 = new MetaDataCollection();
        mdColl6.addProfile(MetaDataProfileManager.getMetaDataProfile("Recording"));
        mdColl6.addMetaDataItem(new MetaDataItem("Year (c)", "1989"));
        metadata.add(mdColl6);
        MetaDataCollection mdColl7 = new MetaDataCollection();
        MetaDataItem item7 = new MetaDataItem("Other known executions", "String Quartet");
        item7.getMetaDataValue().addValueForLanguage("String Quartet", "en");
        item7.getMetaDataValue().addValueForLanguage("Streichquartett", "de");
        mdColl7.addMetaDataItem(item7);
        metadata.add(mdColl7);
        MetaDataCollection mdColl8 = new MetaDataCollection();
        // mdColl8.addProfile(MetaDataProfileManager.getMetaDataProfile("Score"));
        // mdColl8.addMetaDataItem(new MetaDataItem("Publisher", "Henle"));
        try {
            URL urlHTML = new URL("http://www.jsbach.org/");
            MetaDataValue valueHTML = new MetaDataValue("HTML", urlHTML);
            MetaDataItem itemHTML = new MetaDataItem("Link to www");
            itemHTML.setMetaValue(valueHTML);
            mdColl8.addMetaDataItem(itemHTML);
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        }
        metadata.add(mdColl8);
        int max = metadata.size();
        if (containers.size() < max)
            max = containers.size();
        for (int i = 0; i < max; i++) {
            metaMap.put(containers.get(i), metadata.get(i));
        }
    }

    public static void fillInMetaDataNotes(Piece piece){
        Map metaMap = piece.getMetaMap();
        if (metaMap!=null){
            Container notes = piece.getNotePool();
            Containable[] contents = piece.getContainerPool().getContentsRecursive();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] instanceof Note){
                    MetaDataCollection mdColl = new MetaDataCollection();
                    StringBuffer bufferEngl = new StringBuffer();
                    bufferEngl.append(((Note)contents[i]).getScoreNote().getDiatonic());
                    StringBuffer bufferGerman = new StringBuffer();
                    bufferGerman.append(((Note)contents[i]).getScoreNote().getDiatonic());
                    if (bufferGerman.charAt(bufferGerman.length()-1)=='b'){
                        bufferGerman.replace(bufferGerman.length()-1, bufferGerman.length(), "h");
                    }
                    int acc = ((Note)contents[i]).getScoreNote().getAlteration();
                    if (acc>0){
                        switch (acc) {  
                        case 2:
                            bufferEngl.append(" double");
                            break;
                        case 3:
                            bufferEngl.append(" triple");
                            break;
                        default:
                            break;
                        }
                        bufferEngl.append(" sharp");
                        for (int j = 0; j < acc; j++) {
                            bufferGerman.append("is");
                        }
                    } 
                    if (acc<0){
                        switch (acc*(-1)) {  
                        case 2:
                            bufferEngl.append(" double");
                            break;
                        case 3:
                            bufferEngl.append(" triple");
                            break;
                        default:
                            break;
                        }
                        bufferEngl.append(" flat");
                        for (int j = 0; j < acc*(-1); j++) {
                           char last = bufferGerman.charAt(bufferGerman.length()-1);
                           if (last=='h'){
                               bufferGerman.replace(bufferGerman.length()-1, bufferGerman.length(), "b");
                           } else {
                               if (last=='a' || last=='e')
                                   bufferGerman.append("s");
                               else
                                   bufferGerman.append("es");
                           }
                        }
                    }
                    MetaDataValue valuePitch = new MetaDataValue("String", bufferEngl.toString());
                    valuePitch.addValueForLanguage(bufferEngl.toString(), "en");
                    valuePitch.addValueForLanguage(bufferGerman.toString(), "de");
                    MetaDataItem itemPitch = new MetaDataItem("Pitch");
                    itemPitch.setMetaValue(valuePitch);
                    mdColl.addMetaDataItem(itemPitch);
                    MetaDataValue valueDur = new MetaDataValue("Rational", ((Note)contents[i]).getMetricDuration());
                    MetaDataItem itemDur = new MetaDataItem("Metric duration");
                    itemDur.setMetaValue(valueDur);
                    mdColl.addMetaDataItem(itemDur);
                    metaMap.put(contents[i], mdColl);
                }
            }
        }
    }    

    public BaroqueAlignmentTest() {
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
        Piece piece = (new Context()).getPiece();
        fillPiece(piece);

        try {
            //display = (NotationDisplay)EditorFactory.createDisplay(system);
            display = (NotationDisplay) EditorFactory.createDisplay(piece);
        } catch (Exception e) {
            e.printStackTrace();
        }
        display.setOpaque(true);
        display.setAutoZoom(false);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(display, BorderLayout.CENTER);
        pack();
    }

    public static void main(String argv[]) {
        (new BaroqueAlignmentTest()).setVisible(true);
    }
}