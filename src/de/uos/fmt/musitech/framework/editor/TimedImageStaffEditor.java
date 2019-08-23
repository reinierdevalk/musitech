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
 * Created on 26.02.2005
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.mozilla.javascript.tools.shell.JSConsole;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageSequenceContainer;
import de.uos.fmt.musitech.data.media.image.SlideShowPlayer;
import de.uos.fmt.musitech.data.media.image.StaffPosition;
import de.uos.fmt.musitech.data.media.image.StaffPositionGUI;
import de.uos.fmt.musitech.data.media.image.StaffPositionInfoPanel;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.media.image.ImageScroller;
import de.uos.fmt.musitech.media.image.ZoomImageComponent;

/**
 * @author Jan
 *  
 */

public class TimedImageStaffEditor extends AbstractSimpleEditor implements
        PropertyChangeListener {

    JLabel infoLabel;

    JLayeredPane center;

    ZoomImageComponent imagePane;

    JToolBar toolBar;

    ArrayList staffs = new ArrayList();

    Point pressPoint = null, releasePoint = null, dragPoint = null;

    boolean dragging = false;

    int create = 0; // 0: create nothing when mouse is released, other
    // following final ints

    final int NOTHING = 0, RECTANGLE = 1;
    
    double zoomFactor = 1.0;
    
    SlideShowPlayer slidePlayer;
    
    

    /**
     *  
     */
    public TimedImageStaffEditor() {
        init();
        createGUI();

    }

    /**
     *  
     */
    private void init() {
        
        addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                imagePane.setSize(getCenter().getWidth(), getCenter().getHeight());
            }

            public void componentShown(ComponentEvent e) {
                setZoomFactor(zoomFactor);
                imagePane.setSize(getCenter().getWidth(), getCenter().getHeight());
            }
        });

    }

    
    
    /**
     *  
     */
    public void createGUI() {

        
        //        createStaffGUI();
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(getCenter(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        StaffPositionInfoPanel infoPanel = new StaffPositionInfoPanel();
        add(infoPanel, BorderLayout.WEST);
        add(getToolBar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(getInfoLabel(), BorderLayout.SOUTH);

    }

    /**
     *  
     */
    private void createToolBar() {
        
    }

    /**
     * @return
     */
    private Component getInfoLabel() {
        if (infoLabel == null) {
            infoLabel = new JLabel();
            infoLabel.setText("Info");
        }
        return infoLabel;
    }

    private void updateInfoLabel(String str, int x, int y) {
        infoLabel.setText(str + x + ", " + y);
    }

    private void updateInfoLabel(String str) {
        infoLabel.setText(str);
    }

    /**
     * @return
     */
    private JLayeredPane getCenter() {
        if (center == null) {

            center = new JLayeredPane();
            center.addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                    dragPoint = e.getPoint();
                    updateInfoLabel("MouseDragged ", e.getPoint().x, e.getPoint().y);
                    dragging = true;
                    paintDragArea(MouseEditorUtils.calculateDragRectangle(pressPoint, dragPoint));
                    //                repaint();
                }

                public void mouseMoved(MouseEvent e) {
                    updateInfoLabel("MouseMoved ", e.getPoint().x, e.getPoint().y);
                }
            });
            center.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    updateInfoLabel("Clicked " + e.getSource());
                    if (e.getSource() instanceof StaffPositionGUI) {
                        updateInfoLabel("STAFF GUI !!");
                    }
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    updateInfoLabel("MousePressed ", e.getPoint().x, e.getPoint().y);
                    pressPoint = e.getPoint();
                }

                public void mouseReleased(MouseEvent e) {
                    updateInfoLabel("MouseReleased ", e.getPoint().x,
                            e.getPoint().y);
                    releasePoint = e.getPoint();
                    paintDragArea(null);
                    if (create == RECTANGLE) {
                        createStaffGUI();
                        updateStaffGUI();
                        setCursor(Cursor.getDefaultCursor());
                        create = NOTHING;
                    }
                    dragging = false;

                }

            });
//            center.getL;
//            center.add(getImagePane(), new Integer(-1));
            center.setLayout(null);
            center.setSize(getWidth(), getHeight());
            center.add(getImagePane(), new Integer(-1));

        }
        return center;
    }

    private ZoomImageComponent getImagePane() {
        if (imagePane == null) {
            try {

                imagePane = new ZoomImageComponent(
                        new File(
//                                "E:\\Eigene Dateien\\Eigene Musik\\christophe\\chopin1a.jpg")
                        		"E:\\chor.jpg")
                                .toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            imagePane.setSize(300, 200);
            imagePane.setBounds(0,0,300, 200);
//            imagePane.setFitToComp(true);
            
//            imagePane.setSize(getCenter().getWidth(), getCenter().getHeight());
            
            imagePane.setZoomfactor(zoomFactor);
        }
        return imagePane;
    }

    /**
     *  
     */
    private void createStaffGUI() {
        
        Rectangle rec = MouseEditorUtils.calculateDragRectangle(pressPoint, releasePoint);
        
        
        rec.x = (int) (rec.x / zoomFactor);
        rec.y = (int) (rec.y / zoomFactor);
        rec.width = (int) (rec.width / zoomFactor);
        rec.height = (int) (rec.height / zoomFactor);
        
        
        long startTime = 0;
        if (staffs.size() > 0){
            // next staff will  begin with last endTime
            startTime = ((StaffPositionGUI) staffs.get(staffs.size()- 1)).getStaffPos().getEnd();
        }
        StaffPosition staffPos = new StaffPosition(rec, startTime, 10000000);
        StaffPositionGUI staffGUI = new StaffPositionGUI(staffPos);
        staffGUI.registerChangeListener(this);
        staffs.add(staffGUI);
        getCenter().add(staffGUI);
        updateStaffGUI();
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(new DrawRectangle());
            toolBar.add(new SendToPlayer());
            toolBar.add(new ZoomIn());
            toolBar.add(new ZoomOut());
            toolBar.add(new ImportImage());
            
        }
        return toolBar;
    }

    protected class DrawRectangle extends AbstractAction {

        public DrawRectangle() {
            super("Draw Rectangle");

            putValue(Action.MNEMONIC_KEY, new Integer('D'));
            putValue(Action.SHORT_DESCRIPTION, "Draw Rectangle");

            putValue(Action.SMALL_ICON, createImageIcon("icons/stift.gif",
             "Undo last Command"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            create = RECTANGLE;
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

    }
    
    protected class ImportImage extends AbstractAction {

        public ImportImage() {
            super("Import Image");

            putValue(Action.MNEMONIC_KEY, new Integer('D'));
            putValue(Action.SHORT_DESCRIPTION, "Load Image to postion Staffs");

            putValue(Action.SMALL_ICON, createImageIcon("icons/Import24.gif",
             "import Image"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
//            ExampleFileFilter filter = new ExampleFileFilter();
//            filter.addExtension("jpg");
//            filter.addExtension("gif");
//            filter.setDescription("JPG & GIF Images");
            
            
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
               try {
                System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getAbsolutePath());
                getImagePane().setImage(chooser.getSelectedFile().toURL());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            }
        }

    }
    
    protected class ZoomIn extends AbstractAction {

        public ZoomIn() {
            super("Zoom In");

            putValue(Action.MNEMONIC_KEY, new Integer('+'));
            putValue(Action.SHORT_DESCRIPTION, "Zoom In");

            putValue(Action.SMALL_ICON, createImageIcon("icons/ZoomIn24.gif",
             "Zoom In"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            zoomFactor *= 1.1;
            setZoomFactor(zoomFactor);
        }

    }
    
    protected class ZoomOut extends AbstractAction {

        public ZoomOut() {
            super("Zoom Out");

            putValue(Action.MNEMONIC_KEY, new Integer('-'));
            putValue(Action.SHORT_DESCRIPTION, "Zoom Out");

            putValue(Action.SMALL_ICON, createImageIcon("icons/ZoomOut24.gif",
             "Zoom Out"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            zoomFactor *= 0.9;
            setZoomFactor(zoomFactor);
        }

    }
    
    private void setZoomFactor(double zoom){
        zoomFactor = zoom;
        getImagePane().setZoomfactor(zoomFactor);
        getCenter().setPreferredSize(getImagePane().getPreferredSize());
        for (Iterator iter = staffs.iterator(); iter.hasNext();) {
            StaffPositionGUI stafGUI = (StaffPositionGUI) iter.next();
            stafGUI.setZoomFactor(zoom);
            
        }
        updateStaffGUI();
    }
    
    
    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path
     * @param description
     * @return
     */
    protected static ImageIcon createImageIcon(String path, String description) {
      java.net.URL imgURL = TimedImageStaffEditor.class.getResource(path);
      if (imgURL != null)
        return new ImageIcon(imgURL, description);
      else
        System.err.println("Couldn't find file: " + path);
      return null;
    }
    
    protected class SendToPlayer extends AbstractAction {

        public SendToPlayer() {
            super("Send to Player");

            putValue(Action.MNEMONIC_KEY, new Integer('S'));
            putValue(Action.SHORT_DESCRIPTION, "Send To Player");

            putValue(Action.SMALL_ICON, createImageIcon("icons/Play24.gif",
             "Send to Player"));
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            slidePlayer = new SlideShowPlayer(createAudioContainer(), createSequenceContainer());
        }

    }

    public ImageSequenceContainer createSequenceContainer(){
        ImageSequenceContainer imageCont = new ImageSequenceContainer();
        StaffPosition[] staffPos = new StaffPosition[staffs.size()];
        int i = 0;
        for (Iterator iter = staffs.iterator(); iter.hasNext();) {
            StaffPositionGUI staffGUI = (StaffPositionGUI) iter.next();
            staffPos[i++] = staffGUI.getStaffPos();
            
        } 
        imageCont.addTimedImage(path, "chopin1a.jpg", 0, staffPos);
        
        return  imageCont;
    }
    
    
    
    String path = "E:\\Eigene Dateien\\Eigene Musik\\christophe";
    
    public Container createAudioContainer(){
        Container audio = new BasicContainer();
        try {
			audio.add(new AudioFileObject(new File(path,"Grigory Sokolov - 15 - Sostenuto ré bémol majeur.wav").toURL()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return audio;
    }
    
    
    public void updateStaffGUI() {
        for (Iterator iter = staffs.iterator(); iter.hasNext();) {
            StaffPositionGUI stafGUI = (StaffPositionGUI) iter.next();
            int x = (int) (stafGUI.getStaffPos().getX() * zoomFactor);
            int y = (int) (stafGUI.getStaffPos().getY() * zoomFactor);
            int w = (int) (stafGUI.getStaffPos().getWidth() * zoomFactor);
            int h = (int) (stafGUI.getStaffPos().getHeight() * zoomFactor);
            
            stafGUI.setBounds(x,y,w,h);

        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
     */
    public boolean applyChangesToPropertyValue() {
        return false;
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Property Changed: repaint()");
        updateStaffGUI();
        repaint();
    }

    int oldLeftX, oldRightX, oldUpperY, oldLowerY;
    
    public void paintDragArea(Rectangle r) {

        Graphics g = getGraphics();
        g.setXORMode(Color.DARK_GRAY);

        if (oldLeftX != oldRightX)
            g.drawRect(oldLeftX, oldUpperY, oldRightX - oldLeftX, oldLowerY - oldUpperY);

        if (r != null) {

            g.drawRect(r.x, r.y, r.x + r.width - r.x, r.y + r.height - r.y);
            oldLeftX = r.x;
            oldRightX = r.x + r.width;
            oldUpperY = r.y;
            oldLowerY = r.y + r.height;
        } else {
            oldLeftX = oldRightX = oldUpperY = oldLowerY = 0;
        }

        g.setPaintMode();
    }

}