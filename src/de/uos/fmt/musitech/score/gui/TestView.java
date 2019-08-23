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
package de.uos.fmt.musitech.score.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.score.epec.EpecParser;
import de.uos.fmt.musitech.score.util.FileUtils;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class TestView extends JFrame {

    private static String fname;
    private JButton openButton = new JButton("Open...");
    private JButton askButton = new JButton("Enter Code");
    private JButton playButton = new JButton("Play");
    private JButton colorizeButton = new JButton("Colorize");
    private JLabel cursorPosLabel = new JLabel("cursor position: 0");
    private ScorePanel scorePanel;
    private boolean isPlaying = false;
    private Rational cursorPos = Rational.ZERO;
    private Rational tickDistance = new Rational(1, 64);

    class PlayThread extends Thread {

        public void run() {
            while (isPlaying) {
                scorePanel.getScoreCursor().setToTime(cursorPos);
                cursorPos = cursorPos.add(tickDistance).mod(3, 1);
                cursorPosLabel.setText("cursor position: " + cursorPos);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GinFilter extends FileFilter {

        private String extension;

        public GinFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            String ext = FileUtils.getExtension(f);
            return ext != null && (ext.equalsIgnoreCase(extension));
        }

        public String getDescription() {
            if (extension.equalsIgnoreCase("mid"))
                return "MIDI files (*.mid)";
            if (extension.equalsIgnoreCase("gin"))
                return "GIN Extended Plaine and Easy Code files (*.gin)";
            return extension + " files (*." + extension + ")";
        }
    }

    public TestView() {
        super("ScoreDisplay");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        openButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                openFile();
            }
        });

        askButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                scorePanel.askForCode();
            }
        });

        playButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                if (scorePanel.isEmpty())
                    return;
                boolean play = playButton.getText().equals("Play");
                if (play)
                    playButton.setText("Stop");
                else
                    playButton.setText("Play");
                setPlaying(play);
            }
        });

        colorizeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                colorizeNotes();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(openButton);
        buttonPanel.add(askButton);
        buttonPanel.add(playButton);
        buttonPanel.add(colorizeButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new Label("step size: " + tickDistance));
        statusPanel.add(cursorPosLabel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        if (fname != null)
            if (fname.equals("ask"))
                scorePanel = new ScorePanel(true);
            else
                try {
                    //InputStream is = new FileInputStream(fname);
                    scorePanel = new ScorePanel(new File(fname));
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, "File " + fname + " not found");
                }
        else
            scorePanel = new ScorePanel(false);

        scorePanel.setPreferredSize(new Dimension(800, 400));
        getContentPane().add(scorePanel, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    void setPlaying(boolean play) {
        scorePanel.getScoreCursor().setVisible(true);
        if (isPlaying != play) {
            isPlaying = play;
            if (isPlaying)
                new PlayThread().start();
        }
    }

    void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter ginFilter = new GinFilter("gin");
        fileChooser.addChoosableFileFilter(ginFilter);
        fileChooser.addChoosableFileFilter(new GinFilter("mid"));
        fileChooser.setFileFilter(ginFilter);
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION)
            return;
        scorePanel = new ScorePanel();
        try {
            scorePanel.readFile(fileChooser.getSelectedFile());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found");
        }
        getContentPane().add(scorePanel);
        pack();
    }

    void colorizeNotes() {
        ScoreMapper sm = scorePanel.getScoreMapper();
        if (sm == null)
            JOptionPane.showMessageDialog(this, "Score must be read from MIDI file to use this function\n");
        else {
            Map<Note, ScoreObject> ht = sm.getNoteMap();
            List<Note> toBeColorized = new ArrayList<Note>();
            Set<Note> noteSet = ht.keySet();
            for (Note note: noteSet) {
                double rand = Math.random();
                if (rand < 0.5)
                    toBeColorized.add(note);
            }
            Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE, Color.YELLOW,
                              Color.MAGENTA};
            int n = (int) (Math.random() * colors.length);
            scorePanel.setColor(toBeColorized, colors[n]);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0)
            fname = args[0];
        if (fname != null && fname.equals("--help")) {
            System.out.println("optional parameters:");
            System.out.println("  ask    : asks for input code");
            System.out.println("  <file> : reads given input code file");
            System.out.println("  --help : prints this text");
        } else {
            TestView view = new TestView();
        }
    }
}