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
package de.uos.fmt.musitech.media.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.media.image.StaffPosition;

/**
 * Class to load images like jpg or gif files.
 */
public class ImageComponent extends JComponent {

    private Image image;

    private ArrayList staffPositions;

    /**
     * default Constructor
     */
    public ImageComponent() {
        addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            public void componentResized(ComponentEvent e) {
                adjustZoom();
            }

            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Constructor
     * 
     * @param url
     *            The URL to load.
     */
    public ImageComponent(URL url) {
        this();
        setImage(url);

    }

    public void setImage(URL url) {
        image = getToolkit().getImage(url);
        setImage(image);

    }

    /**
     * 
     * @uml.property name="image"
     */
    public void setImage(Image image) {
        final MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        this.image = image;
        setPreferredSize(new Dimension(getImage().getWidth(this), getImage()
                .getHeight(this)));
        Thread imageThread = new Thread() {
            public void run() {
                try {
                    // Wait until the image has been loaded
                    mt.waitForAll();
                } catch (InterruptedException e) {
                    // Do nothing
                }
                revalidate();
                System.out.println("repaint in SetImage");
                repaint();
            }
        };
        imageThread.start();
    }

    /**
     * use only when image is loaded
     * 
     * @param image
     */
    public void showImage(Image image) {
        if (image != this.image) {
            this.image = image;
            System.out.println("repaint in ShowImage");
            offscreenBuffer = null;
            repaint();
        }
    }

    /**
     * Get the preferred size of the component, which is the natural size of the
     * image.
     * 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        synchronized (this) {
            if (image.getWidth(this) == -1 || image.getHeight(this) == -1)
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public Dimension getMinimumSize() {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public double zoomFactor = 1.0;

    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
        if (image != null) {
            adjustZoom();
            int imgWidth = (int) (image.getWidth(this) * zoomFactor);
            int imgHeight = (int) (image.getHeight(this) * zoomFactor);
            g.drawImage(image, 0, 0, imgWidth, imgHeight, this);
        }
        g.setColor(Color.BLACK);
        if (staffPositions != null) {
            for (Iterator iter = staffPositions.iterator(); iter.hasNext();) {
                StaffPosition staffPos = (StaffPosition) iter.next();
                int x = (int) (staffPos.getX() * zoomFactor);
                int y = (int) (staffPos.getY() * zoomFactor);
                int w = (int) (staffPos.getWidth() * zoomFactor);
                int h = (int) (staffPos.getHeight() * zoomFactor);

                g.drawRect(x, y, w, h);

            }
        }
    }

    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        createOffscreenImage();
        super.paint(offscreenGraphics);

        paintChildren(offscreenGraphics);
        paintCursor(true, playTime);
        g.drawImage(offscreenBuffer, 0, 0, getWidth(), getHeight(), this);

    }

    /**
     * @return
     * @uml.property name="image"
     */
    public Image getImage() {
        return image;
    }

    private boolean autoZoom = true;

    // TODO this is inefficient, but reliable.
    // should be optimized in the future.
    void adjustZoom() {
        if (autoZoom && image != null) {
            double xFactor = getWidth() / (double) image.getWidth(this);
            double yFactor = getHeight() / (double) image.getHeight(this);
            zoomFactor = Math.min(xFactor, yFactor);
        }

    }

    public static void main(String[] args) {
        ImageComponent imgComp = null;
        try {
            imgComp = new ImageComponent(new File("Bach.gif").toURL());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Test the image component.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(imgComp, BorderLayout.CENTER);
        frame.pack();
        frame.show();
    }

    /**
     * @param staffPositions
     */
    public void setStaffs(ArrayList staffPositions) {
        if (staffPositions != this.staffPositions) {
            this.staffPositions = staffPositions;
            System.out.println("setStaffs: new StaffPosition repaint()");
            repaint();
        }
    }

    /**
     *  
     */
    public void paintCursor(boolean callFromPaint, long time) {
        StaffPosition staffPos = getActualStaffPos(time);
        if (staffPos != null) {
            lastTime = playTime;
            playTime = time;
            //            System.out.println("paint Cursor " + playTime);
            createOffscreenImage();

            offscreenGraphics.setXORMode(Color.WHITE);

            int x, y, w, h;
                    
            
            // delete old cursor
            if (!callFromPaint) {
                x = (int) (calcCursXPos(lastTime, staffPos) * zoomFactor);
                y = (int) (staffPos.getY() * zoomFactor);
                w = 2;
                h = (int) (staffPos.getHeight() * zoomFactor);
                offscreenGraphics.fillRect(x, y, w, h);
            }
            
//          paint new cursor

            
            x = (int) (calcCursXPos(playTime, staffPos) * zoomFactor);
            y = (int) (staffPos.getY() * zoomFactor);
            w = 2;
            h = (int) (staffPos.getHeight() * zoomFactor);
            offscreenGraphics.fillRect(x, y, w, h);    
           

            offscreenGraphics.setPaintMode();
           
            if (!callFromPaint) {
                getGraphics().drawImage(offscreenBuffer, 0, 0, getWidth(),
                        getHeight(), this);
            }

        }
    }

    /**
     * @param playTime
     * @param staffPos
     * @return
     */
    private int calcCursXPos(long playTime, StaffPosition staffPos) {
        int x = 0;
        
        x = (int) Math.round(staffPos.getX() + staffPos.getWidth()
                * (((playTime - staffPos.getStart())*1.0 / staffPos.getDuration())));
        
        
        return x;
    }

    /**
     * @param playTime
     * @return
     */
    private StaffPosition getActualStaffPos(long playTime) {
        if (staffPositions != null) {

            for (Iterator iter = staffPositions.iterator(); iter.hasNext();) {
                StaffPosition stafPos = (StaffPosition) iter.next();
                if (stafPos.getStart() <= playTime
                        && stafPos.getEnd() >= playTime)
                    return stafPos;
            }
        }

        return null;
    }

    private Graphics offscreenGraphics;

    private BufferedImage offscreenBuffer;

    long lastTime = 0;
    long playTime = 0;

    /**
     * Creates an offscreen Image
     */

    private void createOffscreenImage() {
        int vWidth = getWidth();
        int vHeight = getHeight();
        // new graphics only, when something has changed
        if (offscreenBuffer == null || offscreenBuffer.getWidth() != vWidth
                || offscreenBuffer.getHeight() != vHeight) {
            offscreenBuffer = (BufferedImage) createImage(vWidth, vHeight);
            offscreenGraphics = offscreenBuffer.createGraphics();
            offscreenGraphics.setClip(0, 0, vWidth, vHeight);
        }

    }

}