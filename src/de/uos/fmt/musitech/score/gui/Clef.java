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

import de.uos.fmt.musitech.data.score.CustomGraphic;
/**
 * This class represents a clef.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ 
 */
public class Clef extends ScoreObject
{
   private char shape; // possible values: g, f, c or p (percussion)
   private int  line;  // staff line position (0 = middle line)
   private boolean small; // true, if smaller shape is selected
   private CustomGraphic customGraphic;
   
   protected static final int BACK_GAP = 1;

   private int transposition = 0;

	/** Constructs a normal sized clef of given shape.
	 * @param shape the clef shape: 'c', 'f', 'g' or 'p' (percussion)
	 * @param line Line that should be marked with the clef's pitch.
	 *        (0 means the middle line, 1 the line above, -1 the line below etc.) */
   public Clef (char type, int line)
   {
      this.shape = type;
      this.line = 2*line;
      small = false;
   }
   
	public final static int TREBLE 	= 0;
	public final static int BASS 	   = 1;
	public final static int SOPRANO 	= 2;
	public final static int MEZZO 	= 3;
	public final static int ALTO 	   = 4;
	public final static int TENOR 	= 5;
	public final static int BARITONE	= 6;
	public final static int PERCUSSION = 7;
	
	  
	/** Constructs a normal sized clef of given type.
	 * @param type type of clef: treble, bass, soprano, mezzo, alto, tenor,
	 * baritone or percussion.
	 * @throws IllegalArgumentException if unknown type was specified */
	public Clef (int type)
	{
	   if (type == TREBLE)          {shape = 'g'; line = -2;}
	   else if (type == BASS)       {shape = 'f'; line =  2;}
	   else if (type == SOPRANO)    {shape = 'c'; line = -4;}
	   else if (type == MEZZO)      {shape = 'c'; line = -2;}
	   else if (type == ALTO)       {shape = 'c'; line =  0;}
	   else if (type == TENOR)      {shape = 'c'; line =  2;}
	   else if (type == BARITONE)   {shape = 'c'; line =  4;}
	   else if (type == PERCUSSION) {shape = 'p'; line =  0;}
	   else
		 throw new IllegalArgumentException("unknown clef type");
	}

	/** Constructs a normal sized clef of given type.
	 * @param type type of clef: treble, bass, soprano, mezzo, alto, tenor,
	 * baritone or percussion.
	 * @throws IllegalArgumentException if unknown type was specified */
	public Clef (String type)
	{
	   type = type.toLowerCase();
	   if (type.equals("treble"))          {shape = 'g'; line = -2;}
	   else if (type.equals("bass"))       {shape = 'f'; line =  2;}
	   else if (type.equals("soprano"))    {shape = 'c'; line = -4;}
	   else if (type.equals("mezzo"))      {shape = 'c'; line = -2;}
	   else if (type.equals("alto"))       {shape = 'c'; line =  0;}
	   else if (type.equals("tenor"))      {shape = 'c'; line =  2;}
	   else if (type.equals("baritone"))   {shape = 'c'; line =  4;}
	   else if (type.equals("percussion")) {shape = 'p'; line =  0;}
	   else
		 throw new IllegalArgumentException("unknown clef type");
	}

   @Override
int arrange (int pass)
   {
      if (pass == 0)
      {
   		Staff staff = staff();
         setXPos(0); // @@
         setYPos(staff.hsToPixel(line));
      }
      if (pass == 2) {
      	setLocation(absX(), absY() - height());
      	setSize(lwidth() + rwidth(), height() + depth());
      }
      return 3;
   }

	/** Returns the vertical position of the middle c established by
	 * this Clef. 0 indicates the middle line, 1 the space above the middle line, 
	 * -1 the space below the middle line etc. */
   public int c1Line ()
   {
      int offset = -4;  // treble and percussion clef
      if (shape == 'c')
         offset = 0;
      else if (shape == 'f')
         offset = 4; //TODO: no clue... just guessing;
      return offset + line + (transposition % 12);
   }
   
   int getOctaveShift() {
   		return -(transposition / 12);
   }
	
	/** Returns the line this Clef sits on. 0 means the bottom line. */
   public int getLine()   {return line;}

	/** Returns the shape of this Clef. */
   public char getShape()   {return shape;}

	/** Returns true if this Clef is painted in its smaller version, i.e. for
	 * clef changes inside a staff. */
   public boolean isSmall() {return small;}

	@Override
	public void paint(Graphics g) 
	{
	   if (!isVisible())
	   		return;
	   int x = absX();
	   int y = absY();
	   
	   if (customGraphic != null) {
	   		int ld = staff().getLineDistance();
	   		int width = customGraphic.getWidth() * ld;
	   		int height = customGraphic.getHeight() * ld;
	   		int shift = (int)(customGraphic.getVerticalShift() * ld);
	   		customGraphic.paint(g, absX(), absY() - shift, width, height, this);
	   }
	   else {
		   setScaledFont(g);
		   g.drawString(""+getGlyph(), x, y);
		   if (Math.abs(transposition) == 12) {
		   		char eight = 254;
		   		int clefWidth = (int)(MusicGlyph.width(staff().getLineDistance(), getGlyph()) * scale);
		   		int eightWidth = (int)(MusicGlyph.width(staff().getLineDistance(), eight) * scale);
		   		int eightHeight = (int)(MusicGlyph.height(staff().getLineDistance(), eight) * scale);
		   		int xOffset = (clefWidth - eightWidth) / 2;
		   		int yOffset;
		   		if (transposition > 0) {
			   		int clefHeight = (int)(MusicGlyph.height(staff().getLineDistance(), getGlyph()) * scale);
		   			yOffset = -(clefHeight + eightHeight);
		   		}
		   		else {
			   		int clefDepth = (int)(MusicGlyph.depth(staff().getLineDistance(), getGlyph()) * scale);
			   		yOffset = clefDepth + eightHeight;
		   		}
		   		
		   		g.drawString("" + eight, x + xOffset, y + yOffset);
		   }
		   restoreFont(g);
	   }
	}

	/** Returns the related glyph character for this Clef. */	
	public char getGlyph ()
	{
	   switch (shape)
	   {
	      case 'g' : return small ? MusicGlyph.CLEF_G_SMALL : MusicGlyph.CLEF_G;
	      case 'f' : return small ? MusicGlyph.CLEF_F_SMALL : MusicGlyph.CLEF_F;
	      case 'c' : return small ? MusicGlyph.CLEF_C_SMALL : MusicGlyph.CLEF_C;
	      case 'p' : return MusicGlyph.CLEF_PERC;
	   }
	   return 0;
	}


   @Override
public int rwidth() {
   		if (!isVisible())
   			return 0;
   	
   		return MusicGlyph.width(staff().getLineDistance(), getGlyph());
   }
   
   @Override
public int height() {
		int clefHeight = MusicGlyph.height(staff().getLineDistance(), getGlyph());
		if (transposition == 12) {
	   		char eight = 254;
			int eightHeight = MusicGlyph.height(staff().getLineDistance(), eight) +
			                  MusicGlyph.depth(staff().getLineDistance(), eight);
			clefHeight += eightHeight;
		}
		
		return clefHeight;
   }

   @Override
public int depth() {
   		int clefDepth = MusicGlyph.depth(staff().getLineDistance(), getGlyph());
   		if (transposition == -12) {
	   		char eight = 254;
			int eightHeight = MusicGlyph.height(staff().getLineDistance(), eight) +
			                  MusicGlyph.depth(staff().getLineDistance(), eight);
			clefDepth += eightHeight;
   		}
   		return clefDepth;
   }
   
   public void setLine(int newLine) {line = newLine;}

   public void setSmall(boolean newSmall) {small = newSmall;}

   public void setTransposition(int shift) {transposition = shift; }
   
   public void setShape(char newType) {shape = newType;}
   
   @Override
Class parentClass ()  {return Measure.class;}


   public int optimalSpace()
   {
      int space = rwidth();
      Staff staff = staff();
      if (staff != null)
      	space += staff.getLineDistance();
      return space;
   }
   
public CustomGraphic getCustomGraphic() {
	return customGraphic;
}
public void setCustomGraphic(CustomGraphic customGraphic) {
	this.customGraphic = customGraphic;
}
}