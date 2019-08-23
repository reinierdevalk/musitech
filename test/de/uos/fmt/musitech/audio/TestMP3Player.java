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
package de.uos.fmt.musitech.audio;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.audio.floatStream.MP3FileFloatIS;
import de.uos.fmt.musitech.audio.floatStream.MP3FloatIS;
import de.uos.fmt.musitech.audio.processor.VolumeController;

/**
 * Can be deleted, test MP3FloatIS
 * @author Nicolai Strauch
 */
public class TestMP3Player {

    Player player;

    public void loadPlayer(String file) throws FileNotFoundException, JavaLayerException {
        FileInputStream fis = new FileInputStream(file);
        player = new Player(fis);
    }

    public static String getFile() {
        FileDialog fileChooser = new FileDialog(new Frame());
        fileChooser.setMode(FileDialog.LOAD);
        fileChooser.setVisible(true);
        if (fileChooser.getFile() == null)
            System.out.println("Problems with FileDialog.");
        return fileChooser.getDirectory() + fileChooser.getFile();
    }

    public static void main1(String a[]) throws JavaLayerException, FileNotFoundException,
            UnsupportedAudioFileException {
        //MP3Player mp3p = new MP3Player();
        //mp3p.loadPlayer(getFile());
        //mp3p.player.play();
        //mp3p.player.close();

        try {
            testFloatMP3InputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testFloatMP3InputStream() throws UnsupportedAudioFileException, IOException {

        String filename = getFile();

        FileInputStream fis = new FileInputStream(filename);

        MP3FloatIS mp3Stream = new MP3FloatIS(fis);

        FloatInputStream stream = new FIStmp_debug(mp3Stream);

        final VolumeController volume = new VolumeController(stream);

        JFrame frame = new JFrame("volume");
        final JSlider volumeSlider = new JSlider();
        final JLabel volumeLabel = new JLabel(String
                .valueOf((float) volumeSlider.getValue() / 10000.0f));
        volumeSlider.setMinimum(1);
        volumeSlider.setMaximum(15000);
        volumeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent arg0) {
                volume.setGain((float) volumeSlider.getValue() / 10000.0f);
                volumeLabel.setText(String.valueOf((float) volumeSlider.getValue() / 10000.0f));
            }
        });
        volumeSlider.setValue(5000);
        volume.setGain(0.5f);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 200));
        panel.add(volumeLabel);
        panel.add(volumeSlider);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

        FISPlayer player = new FISPlayer(stream);
        player.play();
        //try{
        //	AudioUtil.writeWaveFromFloat(stream, new File(filename+"test.wav"));
        //} catch (IOException e){
        //	e.printStackTrace();
        //}
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("End of test.");
        System.exit(0);
    }

    static class FIStmp_debug implements FloatInputStream {

        FIStmp_debug(FloatInputStream f) {
            fis = f;
        }
        FloatInputStream fis;

        byte debugForce = 1;

        public int read(float[][] data) throws IOException {
            if (debugForce > 1)
                System.out.println("FIS debug: read(data[" + data.length + "][" + data[0].length
                                   + "])");
            int read = fis.read(data);
            if (debugForce > 2)
                System.out.println("     returning: " + read);
            if (debugForce > 3) {
                for (int i = 0; i < read; i++)
                    System.out.print(" " + (int) data[0][i]);
                System.out.println();
            }
            return read;
        }

        public int read(float[][] data, int start, int len) throws IOException {
            if (debugForce > 1)
                System.out.println("FIS debug: read(data[" + data.length + "][" + data[0].length
                                   + "], " + start + ", " + len + ")");
            int read = fis.read(data, start, len);
            if (debugForce > 2)
                System.out.println("     returning: " + read);
            if (debugForce > 3) {
                for (int i = 0; i < read; i++)
                    System.out.print(" " + (int) data[0][start + i]);
                System.out.println();
            }
            return read;
        }

        public AudioFormat getFormat() {
            if (debugForce > 0)
                System.out.println("FIS debug: getFormat() return: <" + fis.getFormat() + ">");
            return fis.getFormat();
        }

        public long skip(long n) throws IOException {
            if (debugForce > 0)
                System.out.println("FIS debug: skip(" + n + ")");
            return fis.skip(n);
        }

        public void reset() throws IOException {
            if (debugForce > 0)
                System.out.println("FIS debug: reset()");
            fis.reset();
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
         */
        public long remainingSamples() {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
         */
        public long getPositionInSamples() {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
         */
        public void setPositionInSamples(long newPos) throws IOException {
            // TODO Auto-generated method stub

        }

    }

    /*
     * under that file he filelist searched in getFilesList(...) is saved.
     */
    static String fileListfileName = "files.files";

    /**
     * load the files in fileListfileName. Load the roots save whith files, and
     * compare it whith the given roots. If the roots load do not math whith the
     * roots given, return null. Load the extend save whith files, and compare
     * it whith the given extend. If not al extend in the given are in the saved
     * or revers, return null. Search if all files load exists. If not, return
     * null. By any exception return null too.
     * 
     * @param extend
     * @param roots
     * @return
     */
    static File[] chargeFilesVector(String[] extend, File[] roots) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileListfileName);
            ObjectInputStream objStream = new ObjectInputStream(fis);

            File[] rootsCompare = (File[]) objStream.readObject();
            for (int i = 0, j = 0; i < rootsCompare.length; i++) {
                for (j = 0; j < roots.length; j++) {
                    if (rootsCompare[i].equals(roots[j]))
                        break;
                }
                if (j == roots.length) // if no match was found
                    return null;
            }
            for (int i = 0, j = 0; i < roots.length; i++) {
                for (j = 0; j < rootsCompare.length; j++) {
                    if (rootsCompare[j].equals(roots[i]))
                        break;
                }
                if (j == roots.length) // if no match was found
                    return null;
            }

            String[] extendCompare = (String[]) objStream.readObject();
            for (int i = 0, j = 0; i < extendCompare.length; i++) {
                for (j = 0; j < extend.length; j++) {
                    if (extendCompare[i].equals(extend[j]))
                        break;
                }
                if (j == extend.length) // if no match was found
                    return null;
            }
            for (int i = 0, j = 0; i < extend.length; i++) {
                for (j = 0; j < extendCompare.length; j++) {
                    if (extendCompare[j].equals(extend[i]))
                        break;
                }
                if (j == extend.length) // if no match was found
                    return null;
            }
            //File[] fs = (File[]) ((Vector) objStream.readObject()).toArray();
            Vector files = ((Vector) objStream.readObject());
            File[] fs = new File[files.size()];
            for (int i = 0; i < fs.length; i++) {
                fs[i] = (File) files.get(i);
            }
            for (int i = 0; i < fs.length; i++) {
                if (!fs[i].exists())
                    return null;
            }
            return fs;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * save the roots, extend and the files into the file fileListfileName
     * 
     * @param roots
     * @param extend
     * @param files
     */
    static void writeFileList(String[] extend, File[] roots, Vector files) {
        try {
            FileOutputStream fos = new FileOutputStream(fileListfileName);
            ObjectOutputStream objStream = new ObjectOutputStream(fos);
            objStream.writeObject(roots);
            objStream.writeObject(extend);
            objStream.writeObject(files);
            objStream.flush();
            objStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * look if dir end whith any from the Strings in extend. if true, add to
     * fileList
     * 
     * @param fileList
     * @param dir
     * @param extend
     */
    static void testAndAdd(Vector fileList, File dir, String[] extend) {
        for (int i = 0; i < extend.length; i++) {
            if (dir.getName().endsWith(extend[i])) {
                System.out.println("Adding to filelist: " + dir);
                fileList.add(dir);
                return;
            }
        }
    }

    /**
     * 
     * Recursiv method. If dir is a directory, list them files, and look file to
     * file: if them a directory, reevoke this method whith them else look if
     * the nameending correspond to any String in extend, if true, add the file
     * to fileList If dir is not a directory, null is returned
     * 
     * @param fileList
     * @param dir
     * @param extend
     */
    static void addToFilesList(Vector fileList, File dir, String[] extend) {
        if (fileList == null || dir == null || extend == null)
            return;
        //File dir = new File(directory);
        if (!dir.isDirectory())
            return;
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                addToFilesList(fileList, files[i], extend);
            else
                testAndAdd(fileList, files[i], extend);
        }
    }

    /**
     * Search for files whith the endings in extend. Search an all the
     * systemroots and subdirectories. (Now changed to search only in
     * "D:\\CDIndexAsMP3s")
     * 
     * @param extend
     * @return
     */
    static File[] getFilesList(String[] extend) {
        Vector files = new Vector();
        //		File[] roots = File.listRoots();
        //		File[] roots = new File[]{new File("E:\\")};
        File[] roots = new File[] {new File("C:\\")};
        //		File[] roots = new File[]{new File("D:\\CDIndexAsMP3s")};
        File[] fs = chargeFilesVector(extend, roots);
        if (fs != null) {
            return fs;
        }

        for (int i = 0; i < roots.length; i++) {
            System.out.println("Adding root: " + roots[i]);
            addToFilesList(files, roots[i], extend);
        }
        //System.out.println("Adding only root: "+roots[2]);
        //		addToFilesList(files, roots[2], extend);
        //System.out.println("Adding only root: "+"D:\\CDIndexAsMP3s");
        //		addToFilesList(files, new File("D:\\CDIndexAsMP3s"), extend);
        //		System.out.println("Adding only root: "+"E:\\");
        //				addToFilesList(files, new File("E:\\"), extend);

        writeFileList(extend, roots, files);
        fs = new File[files.size()];
        for (int i = 0; i < fs.length; i++) {
            fs[i] = (File) files.get(i);
        }
        //return (File[]) files.toArray();
        return fs;
    }

    static boolean play = true;
    static int secs = 20;
    static float[][] array = new float[1][4192];

    public static void main(String[] a) throws IOException {
        //File directory = new File("E:\\");
        //String[] files = directory.list();
        File[] files = getFilesList(new String[] {"mp3"});

        JFrame frame = new JFrame("Random player");
        JButton playButton = new JButton("Play/Pause");
        final JSlider secSlider = new JSlider();
        secSlider.setMaximum(100);
        secSlider.setMinimum(5);

        //		int random = ((((byte) System.currentTimeMillis()) + 128
        // )*files.length)/256;
        Random random = new Random();
        int fToLoad = random.nextInt(files.length);
        System.out.println("Load file: " + files[fToLoad]);
        final MP3FileFloatIS reader;
        try {
            reader = new MP3FileFloatIS(files[fToLoad]);
        } catch (UnsupportedAudioFileException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return;
        }

        //		final MP3FileFloatIS reader = new MP3FileFloatIS(new
        // File(MP3Player.getFile()));

        playButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                play = !play;
                AudioUtil.initialiseSourceDataLine(reader.getFormat());
                if (reader.getFormat().getChannels() != array.length)
                    array = new float[reader.getFormat().getChannels()][4192];
                System.out.println("Is playing set to = " + play);
            }
        });

        secSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                secs = secSlider.getValue();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(playButton, BorderLayout.CENTER);
        frame.getContentPane().add(secSlider, BorderLayout.WEST);

        frame.pack();
        frame.setVisible(true);

        AudioUtil.initialiseSourceDataLine(reader.getFormat());
        if (reader.getFormat().getChannels() != array.length)
            array = new float[reader.getFormat().getChannels()][4192];

        new Thread() {

            public void run() {
                System.out.println("Thread beginning to run.");
                int read;
                while (true) {
                    try {
                        while (!play) {
                            Thread.sleep(150);
                        }
                        read = reader.read(array, 0, array.length);
                        if (read < 0) {
                            // System.out.println("Play-thread: do not get
                            // data, play set to false.");
                            // play = false;
                            System.out.println("Play-thread: do not get data.");
                            try {
                                Thread.sleep(150);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } else
                            //System.out.println("writing into source: "+
                            AudioUtil.writeIntoSourceDataLine(array, 0, read);
                        //							+" samples");
                    } catch (Exception e) {
                        e.printStackTrace();
                        play = false;
                    }
                }
            }
        }.start();

        /*
         * randomwise load new files or go to new positions in changed file
         */
        while (true) {
            try {
                //random = ((((byte) System.currentTimeMillis()) + 128
                // )*files.length)/200;
                if (random.nextBoolean()) {
                    fToLoad = random.nextInt(files.length);
                    System.out.println("changing file to: " + files[fToLoad]);
                    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    // reader.setFile(files[fToLoad]);
                }
                reader.position(random.nextInt((int) ((reader.remainingSamples() + reader
                        .getPositionInSamples()) - (secs * reader.getFormat().getFrameSize()))));
                System.out.println("position set to: " + reader.position());
                if (!play)
                    while (!play) {
                        Thread.sleep(150);
                    }
                else
                    Thread.sleep(secs * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}