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
package de.uos.fmt.musitech.mpeg.testcases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.score.SpokenMusic;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author kdaling
 * 
 */
public class TestCaseSpokenMusic {

    static Piece getPiece(String str) {
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        try {
            FileReader reader = new FileReader(str);
            InputSource is = new InputSource(reader);
            System.out.println("try to parse " + str);
            doc = builder.parse(is);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (SAXException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return null;
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return null;
        }
        return (Piece) MusiteXMLSerializer.newMPEGSerializer().deserialize(doc,
                new File(str).getParentFile().toURI());
    }

    public static void main(String argv[]) {
        Piece piece = null;
        //	    if (argv !=null){
        //		System.out.println("Create spoken music for the file " + argv[0]);
        //		piece = getPiece(argv[0]);
        //	    }
        //	    if (piece==null)
        piece = createTestPiece();
        MetaDataCollection mdc = new MetaDataCollection();
        mdc.addMetaDataItem(new MetaDataItem("title", "Test-Title"));
        piece.getMetaMap().put(piece, mdc);
        System.out.print("The result is:\n\n" + SpokenMusic.generateSpokenMusic(piece));
    }

    //	only for testing!
    private static Piece createTestPiece() {
        Piece piece = new Piece();
        piece.setName("Test Piece");
        piece.getMetricalTimeLine().setTempo(Rational.ZERO, 100, 8);
        Context context = piece.getContext();
        NoteList nl = new NoteList(context);
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('a', 0, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('g', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('e', 1, 0), new Rational(1, 16));
        Voice voice = new Voice(context);
        voice.setName("Voice");
        for(Note n: nl)
        	voice.add(n);
        StaffContainer staff = new StaffContainer(context);
        staff.setName("Staff");
        staff.add(voice);
        Container cp = piece.getContainerPool();
        cp.add(staff);
        Container container1 = new BasicContainer();
        container1.setName("Selection1");
        for (int i = 0; i < 3; i++) {
            container1.add(nl.get(i));
        }
        piece.getSelectionPool().add(container1);
        Container container2 = new BasicContainer();
        container2.setName("Selection2");
        for (int i = 5; i < 7; i++) {
            container2.add(nl.get(i));
        }
        piece.getSelectionPool().add(container2);
        return piece;
    }
}