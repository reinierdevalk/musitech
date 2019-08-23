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
 * Created on Dec 30, 2004
 *
 */
package de.uos.fmt.musitech.mpeg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.TextEditor;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.mpeg.testcases.BaroqueAlignmentTest;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.SpokenMusic;
import de.uos.fmt.musitech.structure.text.LyricsDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.utility.ExtractToTemporaryDirectory;

/**
 * @author collin
 *  
 */
public class FileViewer extends JFrame implements ActionListener {

    ObjectPlayer player = ObjectPlayer.getInstance();
    PlayTimer timer = player.getPlayTimer();
    JLabel banner;
    JPanel viewPanel = new JPanel();
    Dimension viewPanelSize = new Dimension(800, 600);
    JButton openButton, playButton, stopButton;
    JMenu tcMenu = new JMenu("SMR Test Cases");
    JRadioButton notationButton, metadataButton, identificationButton, selectionButton, excerptButton, spokenMusicButton;
    
    public final static int VIEW_NOTATION = 0;
    public final static int VIEW_METADATA = 1;
    public final static int VIEW_IDENTIFICATION = 2;
    public final static int VIEW_SELECTION1 = 3;
    public final static int VIEW_SELECTION2 = 4;
    public final static int VIEW_SPOKENMUSIC = 5;
    
    

    /**
     * From the Java Tutorial
     */
    protected JButton makeButton(String category, String imageName, String actionCommand, String toolTipText,
                                 String altText) {
        //Look for the image.
        String imgLocation = "toolbarButtonGraphics/" + category + "/" + imageName + ".gif";
        URL imageURL = FileViewer.class.getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        if (imageURL != null) { //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else { //no image found
            button.setText(altText);
            //System.err.println("Resource not found: " + imgLocation);
        }

        return button;
    }

    JToolBar toolBar;

    void gui() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(tcMenu);
        this.setJMenuBar(menuBar);
        getContentPane().setLayout(new BorderLayout());

//        toolBar = new JToolBar("Viewer Toolbar");
//        getContentPane().add(toolBar, BorderLayout.PAGE_START);
//
//        toolBar.add(makeButton("general", "Open24", "Open", "Open file", "Open"));
//        toolBar.add(makeButton("media", "Play24", "Play", "Play file", "Play"));
        toolBar = new JToolBar("Viewer Toolbar");
        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        prepareToolBar();

        ImageIcon ii = new ImageIcon(FileViewer.class.getResource("musitech.png"));
        banner = new JLabel(ii);
        getContentPane().add(banner, BorderLayout.CENTER);
        viewPanel.setLayout(new BorderLayout());
    }
    
    private void prepareToolBar(){
//        toolBar.add(makeButton("general", "Open24", "Open", "Open file", "Open"));
//        toolBar.add(makeButton("media", "Play24", "Play", "Play file", "Play"));
//        toolBar.add(makeButton("media", "Stop24", "Stop", "Stop file playback", "Stop"));
        openButton = makeButton("general", "Open24", "Open", "Open file", "Open");
        toolBar.add(openButton);
        playButton = makeButton("media", "Play24", "Play", "Play file", "Play");
        toolBar.add(playButton);
        stopButton = makeButton("media", "Stop24", "Stop", "Stop file playback", "Stop");
        toolBar.add(stopButton);
    }

    void processFile(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        Document doc;
        try {
            FileReader reader = new FileReader(file);
            org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
            //          doc = builder.parse(file.toURI().toString());
            doc = builder.parse(is);
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }
        Piece piece = (Piece) MusiteXMLSerializer.newMPEGSerializer().deserialize(doc,file.getParentFile().toURI());
        if (piece != null) //TODO kann/darf das auftreten? (K.N.)
            showPiece(piece);
        else {
            JOptionPane.showMessageDialog(this, "Could not read SMR.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        //for testing TODO folgende Zeilen wieder löschen!!!
//        if (piece == null) {
//            piece = createTestPiece();
//            if (piece != null)
//                showPiece(piece);
//        }
    }

    void clearContentPane() {
        getContentPane().removeAll();
        toolBar.removeAll();
        prepareToolBar();
        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        getContentPane().add(viewPanel);
//        JScrollPane scroll = new JScrollPane(viewPanel);
//        scroll.setPreferredSize(viewPanelSize);
//        getContentPane().add(scroll);
    }

    void showSystemSplit(Piece piece) {
        NotationSystem system = piece.getScore();
        Collection systems = system.splitAtLineBreaks();
        NotationStaff staff = (NotationStaff) system.get(0);
        NotationVoice voice = (NotationVoice) staff.get(0);
        LyricsContainer lyricsCont = voice.getLyrics();
        if(language == null)
            language = lyricsCont.getDefaultLanguage();
        LyricsSyllableSequence verse = lyricsCont.getVerse((byte) 0, language);
        Collection lyrics = verse.splitAtLinebreaks(system.getLinebreaks());

        //Box vBox = new Box(BoxLayout.Y_AXIS);
        JPanel vBox = new JPanel();
        vBox.setLayout(null);
//        getContentPane().add(new JScrollPane(vBox), BorderLayout.CENTER);
        viewPanel.removeAll();
        viewPanel.setBackground(Color.LIGHT_GRAY);
        JScrollPane scroll = new JScrollPane(vBox);
        scroll.setOpaque(false);
        viewPanel.add(scroll);
//      if(system.getLyricsLanguages().size() >1){
        viewPanel.add(getLanguageSwitch(piece),BorderLayout.NORTH);
//    }

//        viewPanel.add(vBox);
        viewPanel.revalidate();

        Iterator lyricsIterator = lyrics.iterator();
        int y = 0;
        int x = 0;
        for (Iterator iter = systems.iterator(); iter.hasNext();) {
            NotationSystem splitSystem = (NotationSystem) iter.next();
            LyricsSyllableSequence splitLyrics = (LyricsSyllableSequence) lyricsIterator.next();
            NotationDisplay splitDisplay;
            LyricsDisplay lyricsDisplay;
            try {
                HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
                splitDisplay = (NotationDisplay) EditorFactory.createDisplay(splitSystem);
                lyricsDisplay = (LyricsDisplay) EditorFactory.createDisplay(splitLyrics);
                splitDisplay.setAutoZoom(false);

                coord.registerDisplay(splitDisplay);
                coord.registerDisplay(lyricsDisplay);
                coord.doPositioning();

                splitDisplay.setLocation(0, y);
                y += splitDisplay.getPreferredSize().getHeight();
                x = Math.max(x, (int) splitDisplay.getPreferredSize().getWidth());
                splitDisplay
                        .setSize(splitDisplay.getPreferredSize().width + 150, splitDisplay.getPreferredSize().height);
                vBox.add(splitDisplay);
                lyricsDisplay.setLocation(0, y);
                y += lyricsDisplay.getPreferredSize().getHeight();
                x = Math.max(x, (int) lyricsDisplay.getPreferredSize().getWidth());
                vBox.add(lyricsDisplay);
                lyricsDisplay.setVisible(true);
                lyricsDisplay.setSize(lyricsDisplay.getPreferredSize());
                player.getPlayTimer().registerMetricForPush(splitDisplay);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        vBox.setPreferredSize(new Dimension(x, y));
        vBox.setBackground(Color.WHITE);
    }

    void showPiece(Piece piece) {
        clearContentPane();
 /*       if (piece.getScore() == null) {
            piece.setScore(NotationDisplay.createNotationSystem(piece));
        }
        NotationSystem system = piece.getScore();
        if (((NotationVoice) ((NotationStaff) system.get(0)).get(0)).getLyrics() != null)
            showSystemSplit(system);
        else {
            NotationDisplay display = new NotationDisplay();
            display.init(piece, null, null);

            //      NotationSystem system =
            // display.getScorePanel().getNotationSystem();

            display.getScorePanel().setNotationSystem(system);
            display.updateDisplay();

            display.setOpaque(true);
            display.setAutoZoom(false);

            JScrollPane scrollPane = new JScrollPane(display);
            scrollPane.setPreferredSize(new Dimension(800, 600));
            getContentPane().add(scrollPane, BorderLayout.CENTER);

        }
        pack(); */
        
        if (piece!=null){
            prepareContainerNames(piece);
            showViewOptions(piece);
        }

        //make the piece ready for midi playback:
        player.setContainer(piece.getScore());
        timer.setContext(piece.getContext());
    }

    private void showViewOptions(final Piece piece) {
        int dist = 5;
        ButtonGroup bg = new ButtonGroup();
//        JButton notationButton = new JButton("Notation");
        notationButton = new JRadioButton("Notation");
        bg.add(notationButton);
        notationButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showNotation(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(30));
        toolBar.add(new JLabel("Views: "));
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(notationButton);
//        JButton metadataButton = new JButton("Metadata");
        metadataButton = new JRadioButton("Metadata");
        bg.add(metadataButton);
        metadataButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showMetadata(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(metadataButton);
//        JButton identificationButton = new JButton("Identification");
        identificationButton = new JRadioButton("Identification");
        bg.add(identificationButton);
        identificationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showIdentification(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(identificationButton);
//        JButton selectionButton = new JButton("Selection 1");
        selectionButton = new JRadioButton("Selection 1");
        bg.add(selectionButton);
        selectionButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showSelections(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(selectionButton);
//        JButton excerptButton = new JButton("Selection 2");
         excerptButton = new JRadioButton("Selection 2");
        bg.add(excerptButton);
        excerptButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showExcerpts(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(excerptButton);
//        JButton spokenMusicButton = new JButton("Spoken Music");
        spokenMusicButton = new JRadioButton("Spoken Music");
        bg.add(spokenMusicButton);
        spokenMusicButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showSpokenMusic(piece);
            }
        });
        toolBar.add(Box.createHorizontalStrut(dist));
        toolBar.add(spokenMusicButton);
        toolBar.add(Box.createHorizontalGlue());
        notationButton.setSelected(true);
        showNotation(piece);
    }
    
    private Locale language;
    
    private JComponent getLanguageSwitch(final Piece piece){
        NotationSystem nsys = piece.getScore();
        if(nsys == null)
            return null;
        Set langSet = nsys.getLyricsLanguages();
        final JComboBox jList = new JComboBox(langSet.toArray());
        jList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                Locale newlocale = (Locale)jList.getSelectedItem();
                if(!newlocale.equals(language)){
                    language = newlocale;
                    showNotation(piece);
                }
            }
            });
        jList.setSelectedItem(language);
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Lyrics Language :",JLabel.RIGHT);
        label.setLabelFor(jList);
        panel.add(label,BorderLayout.CENTER);
        panel.add(jList,BorderLayout.EAST);
        return panel;
    }

    private void showNotation(Piece piece) {
        if (piece.getScore() == null) {
            piece.setScore(NotationDisplay.createNotationSystem(piece));
        }
        NotationSystem system = piece.getScore();
        if (((NotationVoice) ((NotationStaff) system.get(0)).get(0)).getLyrics() != null)
            showSystemSplit(piece);
        else {
            NotationDisplay display = new NotationDisplay();
            display.init(piece, null, null);
            display.getScorePanel().setNotationSystem(system);
            display.updateDisplay();
            display.setOpaque(true);
            display.setAutoZoom(false);
            viewPanel.removeAll();
            viewPanel.setBackground(Color.WHITE);
            JScrollPane scroll = new JScrollPane(display);
            scroll.setOpaque(false);
            viewPanel.add(scroll);
//            viewPanel.add(display);
            viewPanel.revalidate();
            player.getPlayTimer().registerMetricForPush(display);
        }
    }

    private void showMetadata(Piece piece) {
        ObjectEditor objEditor = new ObjectEditor(this, piece);
        viewPanel.removeAll();
        viewPanel.setBackground(Color.WHITE);
        viewPanel.add(objEditor);
        viewPanel.revalidate();
    }
    
    private void showIdentification(Piece piece){
        FindObjects identificationEditor = new FindObjects(piece, this);
        viewPanel.removeAll();
        viewPanel.setBackground(null);
        viewPanel.add(identificationEditor);
        viewPanel.revalidate();
    }

    private void showSelections(Piece piece) {
        HighlightSelection highlightEditor = new HighlightSelection(piece, this);
        viewPanel.removeAll();
        viewPanel.setBackground(Color.WHITE);
        viewPanel.add(highlightEditor);
        viewPanel.revalidate();
    }

    private void showExcerpts(Piece piece) {  
        SelectObjects excerptEditor = new SelectObjects(piece);
        excerptEditor.setPreferredSize(new Dimension(800,500));
        viewPanel.removeAll();
        viewPanel.setBackground(null);
        viewPanel.add(excerptEditor);
        viewPanel.revalidate();
    }
    
    private void showSpokenMusic(Piece piece){
        String spokenMusic = SpokenMusic.generateSpokenMusic(piece);
        if (spokenMusic!=null){
            Editor editor = null;
            try {
                editor = EditorFactory.createEditor(spokenMusic, true, "Text", null);
                if (editor instanceof TextEditor){
                    ((TextEditor)editor).setTextLineWrap(false);
                    ((TextEditor)editor).setTextFont(new Font("SansSerif", Font.PLAIN, 14));
                }
                viewPanel.removeAll();
                viewPanel.setBackground(Color.WHITE);
                JScrollPane scroll = new JScrollPane((JComponent)editor);
                scroll.setOpaque(false);
                viewPanel.add(scroll);
//                viewPanel.add((JComponent)editor);   
                viewPanel.revalidate();
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
    }
    
//    private JRadioButton createTCButton(){
//        
//    }
    
    private void prepareContainerNames(Piece piece){
        Containable[] contents = piece.getContainerPool().getContentsRecursive();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] instanceof Container){
                if (((Container)contents[i]).getName()==null){
                    String className = contents[i].getClass().getName();
                    int index = className.lastIndexOf('.');
                    if (index>=0 && index<className.length()){
                        ((Container)contents[i]).setName(className.substring(index+1));
                    }  
                }
            }
        }
        Containable[] selections = piece.getSelectionPool().getContentsRecursive();
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] instanceof Container){
                if (((Container)selections[i]).getName()==null){
                    ((Container)selections[i]).setName("Selection");
                }
            }       
        }
    }
    
    
    public void addTCMenuItem(String tcName, String path, final int viewType){
        JMenuItem menuItem = new JMenuItem(tcName);
        tcMenu.add(menuItem);
        final Piece piece = extractPiece(path);
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                switch (viewType) {
                case VIEW_NOTATION:
                    showNotation(piece);
                    if (notationButton!=null)
                        notationButton.setSelected(true);
                    break;
                case VIEW_METADATA:
                    showMetadata(piece);
                    if (metadataButton!=null)
                        metadataButton.setSelected(true);
                    break;
                case VIEW_IDENTIFICATION:
                    showIdentification(piece);
                    if (identificationButton!=null)
                        identificationButton.setSelected(true);
                    break;
                case VIEW_SELECTION1:
                    showSelections(piece);
                    if (selectionButton!=null)
                        selectionButton.setSelected(true);
                    break;
                case VIEW_SELECTION2:
                    showExcerpts(piece);
                    if (excerptButton!=null)
                        excerptButton.setSelected(true);
                    break;
                case VIEW_SPOKENMUSIC:
                    showSpokenMusic(piece);
                    if (spokenMusicButton!=null)
                        spokenMusicButton.setSelected(true);
                    break;
                default:
                    break;
                }
            }});
    }
    
    private Piece extractPiece(String path){
        //TODO
        return null;
    }

    public FileViewer() {
        gui();

        getContentPane().setBackground(Color.WHITE);
        setSize(800, 600);
        setTitle("MUSITECH SMR Viewer");

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
    }

    private final JFileChooser fileChooser = new JFileChooser();
    private File currentDir = new File(".");

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        Piece piece = new Piece();

        if ("Open".equals(command)) {
            fileChooser.setCurrentDirectory(currentDir);
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentDir = fileChooser.getCurrentDirectory();
                if (file.getName().endsWith(".zip")){
                    File unzipF = ExtractToTemporaryDirectory.extractToTempDirectory(file.getAbsolutePath());
                    if (unzipF.isDirectory()){
                        currentDir = unzipF;
                        File[] content = currentDir.listFiles();
                        for (int i = 0; i < content.length; i++) {
                            if (content[i].getName().equals("smr.xml")){
                                file = content[i];
                            }
                        }
                    }
                }
                processFile(file);
            }
        } else if ("Play".equals(command)) {
            timer.reset();
            timer.start();
            playButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else if ("Stop".equals(command)){
            timer.stop();
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        (new FileViewer()).setVisible(true);
    }

    //only for testing!
    public static Piece createTestPiece() {
 /*       Piece piece = new Piece();
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
        voice.addAll(nl);
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
        return piece;*/
        Piece piece = new Piece();
        piece.setName("Piece");
        BaroqueAlignmentTest.fillPiece(piece);
        Container selection1 = new BasicContainer(piece.getContext());
        selection1.setName("Selection 1");
        selection1.addRenderingHint("color", new RenderingHints().colorToString(Color.GREEN));
        piece.getSelectionPool().add(selection1);
        Container selection2 = new BasicContainer(piece.getContext());
        selection2.setName("Selection 2");
        selection2.addRenderingHint("color", new RenderingHints().colorToString(Color.RED));
        piece.getSelectionPool().add(selection2);
        Containable[] contents = piece.getContainerPool().getContentsRecursive();
        int j=0;
        for (int i = 0; i < contents.length/2; i++) {
            if (contents[i] instanceof Note){
                selection1.add(contents[i]);
                j++;
                if (j>5)
                    break;
            }
        }
        j=0;
        for (int i = contents.length/2; i < contents.length; i++) {
            if (contents[i] instanceof Note){
                selection2.add(contents[i]);
                j++;
                if (j>8)
                    break;
            }
        }
        return piece;
    }
}