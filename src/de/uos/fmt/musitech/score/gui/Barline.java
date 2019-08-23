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

import java.awt.Graphics;
import java.util.Iterator;

import de.uos.fmt.musitech.data.score.CustomGraphic;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class is the abstract base class for all barlines.
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ */
public abstract class Barline extends ScoreObject implements SpringAttacher
{
	private CustomGraphic preview;
	private TimeSignature timeSignaturePreview;
//   private int length = 0;
//   public Barline(Measure measure) {super(measure);}
   public Barline () {}
   
   Class parentClass () {return Measure.class;}
   
//   public int getLength () {return length;}	   

   void paint(Graphics g, int x, int y1, int y2) {
	   if(!isVisible()) return;
   		if (preview != null) {
   			int ld = staff().getLineDistance();
   			int width = preview.getWidth() * ld;
   			int height = preview.getHeight() * ld;
   			y1 += ((y2 - y1) / 2);
   			y1 += (int)(preview.getVerticalShift() * ld);
   			preview.paint(g, x - width, y1, width, height, this);
   		}
   		else if (timeSignaturePreview != null) {
			timeSignaturePreview.setXPos(x - timeSignaturePreview.rwidth());
			//draw a preview for every sibling of our measure
   			for (Iterator iter = measure().getGlobalMeasure().getMeasures().iterator(); iter.hasNext();) {
   				Measure measure = (Measure) iter.next();
   				
   				if (measure instanceof TabulaturMeasure) //do not draw previews for tablature measures
   					continue;
   				
				timeSignaturePreview.setScoreParent(measure);
				timeSignaturePreview.setScale(measure.getScale());
				timeSignaturePreview.paint(g);
   			}
   		}
   }
   
   int arrange (int pass) {
   		if (timeSignaturePreview != null)
   			return timeSignaturePreview.arrange(pass);
   		return 0;
   }

   public Rational attackTime()  {return null;}
   
   public int optimalSpace(SpringAttacher successor)
   {
      int space = rwidth();
      Staff staff = staff();
      if (staff != null)
      	space += 3*staff.getLineDistance()/2; // @@
      return space + successor.lwidth();
   }
   
   public final void setParentXPos (int x)
   {
      //if (getScoreParent() != null)
   	System.err.println(getScoreParent().getClass());
      	getScoreParent().setXPos(x);
   }
 
   public final void setMeasureRWidth (int width)
   {
      Measure measure = measure();
      if (measure != null)
      	measure.getGlobalMeasure().setRWidth(width);
   }
   
   public abstract Barline create ();
   
   public CustomGraphic getPreview() {
		return preview;
   }
	
   /**
    * The preview of a barline is a graphical object which is painted right after the barline.
    * It is normally used to hint at a time signature change on the next line/system
    * @param preview
    */
   public void setPreview(CustomGraphic preview) {
		this.preview = preview;
   }
   
   /**
    * The rwidth of just the barline; without a preview e.g.
    * @return
    */
   abstract int barlineRwidth();
   
   public int rwidth() {
   		int rwidth = barlineRwidth();
   		if (preview != null) {
   			int ld = staff().getLineDistance();
   			rwidth += preview.getWidth() * ld;
   		}
   		else if (timeSignaturePreview != null) {
   			rwidth += timeSignaturePreview.lwidth() + timeSignaturePreview.rwidth();
   		}
   		return rwidth;
   }
   
   void movePropertiesTo(ScoreObject so) {
   		super.movePropertiesTo(so);
   		Barline target = (Barline)so;
   		target.setPreview(preview);
   		target.setTimeSignaturePreview(timeSignaturePreview);
   }
   
	public TimeSignature getTimeSignaturePreview() {
		return timeSignaturePreview;
	}
	public void setTimeSignaturePreview(TimeSignature timeSignaturePreview) {
		this.timeSignaturePreview = timeSignaturePreview;
	}
	
	public void setTimeSignaturePreview(int[] sig) {
		timeSignaturePreview = new TimeSignature(sig[0], sig[1], false);
		timeSignaturePreview.setScoreParent(this);
	}
}
