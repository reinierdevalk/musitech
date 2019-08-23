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
 * Created on 11.02.2004
 */
package de.uos.fmt.musitech.media.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.media.image.ImageSequenceContainer;
import de.uos.fmt.musitech.data.media.image.TimedImage;
import de.uos.fmt.musitech.data.media.image.TimedImageStaffPostion;
import de.uos.fmt.musitech.data.time.BasicTimedObject;
import de.uos.fmt.musitech.data.time.Timeable;

/**
 * A timeable that controls an image display component.
 *   
 * @author Jan
 */
public class ImageSequencePlayer implements Timeable {

	private ImageComponent imageComp = new ImageComponent();

	ImageSequenceContainer timedISC;

	List imageList = new ArrayList();
	
	

	public ImageSequencePlayer() {

	}
	/**
	 * Lods the images from the imageSequenceContainer.
	 * @return flase it there was an error, true else.
	 */
	public boolean loadImages() {
		if (timedISC == null || imageComp == null)
			return false;
		Toolkit toolkit = imageComp.getToolkit();
		MediaTracker mt = new MediaTracker(imageComp);
		Image offscreenIm = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = offscreenIm.getGraphics(); 
		
		for (Iterator iter = timedISC.iterator(); iter.hasNext();) {
			TimedImage timedImage = (TimedImage) iter.next();
			Image image = toolkit.getImage(timedImage.getSourceUrl());
			mt.addImage(image, 0);
			imageList.add(image);
			graphics.drawImage(image, 0, 0, imageComp);
		}
		
		return true;
		
	}

	/**
	 * Implements the Timeable interface, switching the displayed image when necessary.  
	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
	 */
	public void setTimePosition(long timeMillis) {
//		System.out.println("timeMillis: " + timeMillis);
		imageComp.paintCursor(false, timeMillis);
		if (timedISC != null) {
			int index = timedISC.find(new BasicTimedObject(timeMillis));
			if (index == -1) return;
			if (index < 0)
				index = -index - 2;
			if (imageList.size() > index){
				if (timedISC.get(index) instanceof TimedImageStaffPostion) {
                    TimedImageStaffPostion staffImage = (TimedImageStaffPostion) timedISC.get(index);
                    imageComp.setStaffs(staffImage.getStaffPositions());
                }
				// no staff positions available
				else {
				    // we have to delete Staffs from ImageComp
				    // to avoid drawing staffs from Image before
				    imageComp.setStaffs(null);
				}
				imageComp.showImage((Image) imageList.get(index));
			}
		}
	}

	/**
	 * @return
	 */
	public ImageSequenceContainer getTimedISC() {
		return timedISC;
	}

	/**
	 * @param container
	 * 
	 * @uml.property name="timedISC"
	 */
	public void setTimedISC(ImageSequenceContainer container) {
		timedISC = container;
	}

	/**
	 * @return get the image component controlled by this ImageSequencePlayer.
	 */
	public ImageComponent getImageComp() {
		return imageComp;
	}

	/**
	 * Set a new ImageComponent. 
	 * @param ic The imageComponent to use.
	 */
	public void setImageComp(ImageComponent ic) {
		imageComp = ic;
	}

	/**
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	public long getEndTime() {
		return timedISC.getEndTime();
	}
	
	

}
