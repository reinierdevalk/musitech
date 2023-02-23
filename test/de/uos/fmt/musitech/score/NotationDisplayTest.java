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
 * Created on Sep 1, 2004
 */
package de.uos.fmt.musitech.score;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.score.gui.MusicGlyph;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 */
public class NotationDisplayTest extends TestCase {
    
	public NotationDisplayTest() {
		JFrame jframe = new JFrame();
		
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});

		NoteList notes1 = new NoteList(new Context(new Piece()));
		
		notes1.add(
				new ScorePitch('g', 0, 0),
				new Rational(0, 4),
				new Rational(1, 4));
			notes1.add(
				new ScorePitch('g', 0, 0),
				new Rational(1, 4),
				new Rational(1, 4));
			notes1.add(
				new ScorePitch('g', 0, 0),
				new Rational(2, 4),
				new Rational(1, 8));
			notes1.add(
				new ScorePitch('g', 0, 0),
				new Rational(5, 8),
				new Rational(1, 8));
			notes1.add(
				new ScorePitch('g', 0, 0),
				new Rational(3, 4),
				new Rational(1, 2));
		
		RenderingHints rh = new RenderingHints();
		notes1.setRenderingHints(rh);
		rh.registerHint("barline", "none");
		rh.registerHint("time signature", "none");
		//rh.registerHint("staff lines", "rhythm");
		rh.registerHint("clef", "none");
		
		jframe.getContentPane().setLayout(new BorderLayout());
		try {
			NotationDisplay display = (NotationDisplay)EditorFactory.createDisplay(notes1, null, "Notation");
			display.setAutoZoom(false);
			display.setBorder(BorderFactory.createTitledBorder("Notation"));
			jframe.getContentPane().add(display, BorderLayout.NORTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		jframe.pack();
		jframe.setVisible(true);
	}
	
	public static void testMusicFont(){
	    JFrame jframe = new JFrame();
	    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    jframe.setContentPane( new JPanel(){
	        @Override
			public void paint(Graphics g){
	            Graphics2D g2d = (Graphics2D) g;
	            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
//	            try {
//	                URL url = NotationDisplayTest.class.getResource("gin.ttf");
//	                Font font = Font.createFont(Font.TRUETYPE_FONT,url.openStream());
//	                font = font.deriveFont(18F);
					Font font = MusicGlyph.createFont(8);
                    g.setFont(font);
//                } catch (FontFormatException e) {
//                    // TODO Auto-generated catch block
//                  e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//	            g.drawString("abcdefghijklmonpqrstuvwxyz",5,50);
//	            g.drawString(",.-ABCDEFGHIJKLMNOPQRSTUVWXYZ",5,100);
                for(int i=1;i<256;i++){
                    g2d.drawChars( new char[]{(char)i},0,1,i*30%930+10,(i*30/990)*50+60 );
                }
	        }
	    }
	    );
	    jframe.setSize(1000,700);
	    jframe.setVisible(true);
	    
	}

	public static void main(String[] argv) {
	    testMusicFont();
		new NotationDisplayTest();
	}
}
