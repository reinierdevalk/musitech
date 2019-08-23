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
 * Created on Dec 28, 2004
 *
 */
package de.uos.fmt.musitech.data.score;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;


/**
 * @author collin
 *
 */
public class CustomSVGGraphic implements CustomGraphic {
	private String uri;
	private int width, height;
	private int cutX, cutY;
	private String leadingText;
	private int verticalAlignment = CENTER;
	private float verticalShift;
	private int documentHeight;
	
	public static final int CENTER = 1;
	public static final int TOP = 2;
	
	public CustomSVGGraphic() {}
	
	public CustomSVGGraphic(String uri, int width, int height) {
		this.uri = uri;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * @see de.uos.fmt.musitech.data.score.CustomGraphic#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g, int x, int y, int width, int height, ImageObserver io) {
		int myCutX = cutX;
		int myWidth = width;
		
		if (verticalAlignment == CENTER) {
			y -= height / 2;
		}
		else if (verticalAlignment < 0) {
			y -= height / Math.abs(verticalAlignment);
		}
		
		if (leadingText != null && leadingText != "") {
			Font oldFont = g.getFont();
			g.setFont(new Font("Serif", Font.PLAIN, oldFont.getSize() / 3));
			g.drawString(leadingText, x, y + g.getFontMetrics().getAscent());
			int textWidth = g.getFontMetrics().stringWidth(leadingText);
			myWidth -= textWidth;
			myCutX -= textWidth;
			x += textWidth;
			g.setFont(oldFont);
		}
		
		TranscoderInput ti = new TranscoderInput(uri);
		AWTImageTranscoder t = new AWTImageTranscoder(width, height);
		try {
			if (myWidth > 0)
				t.addTranscodingHint(AWTImageTranscoder.KEY_WIDTH, new Float(myWidth));
			if (height > 0)
				t.addTranscodingHint(AWTImageTranscoder.KEY_HEIGHT, new Float(height));
			
			if (myCutX > 0) {
				if (documentHeight <= 0) 
					throw new IllegalArgumentException("if cutX is used a height must be given");
				t.addTranscodingHint(AWTImageTranscoder.KEY_AOI, new Rectangle(0, 0, myCutX, documentHeight));
			}
			t.transcode(ti, null);
			g.drawImage(t.image, x, y, io);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class AWTImageTranscoder extends ImageTranscoder {
		private BufferedImage image = null;
		private int width, height;
		
		AWTImageTranscoder(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public BufferedImage createImage(int arg0, int arg1) {
			//return new BufferedImage(width, height, ColorSpace.TYPE_RGB);
			BufferedImage orig = new BufferedImage(arg0, arg1, BufferedImage.TYPE_4BYTE_ABGR);
			return orig;
		}
		
		public void writeImage(BufferedImage arg0, TranscoderOutput arg1)
				throws TranscoderException {
			image = arg0;
		}
	}


	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getCutX() {
		return cutX;
	}
	public void setCutX(int cutX) {
		this.cutX = cutX;
	}
	public int getCutY() {
		return cutY;
	}
	public void setCutY(int cutY) {
		this.cutY = cutY;
	}
	
	public String getLeadingText() {
		return leadingText;
	}
	
	/**
	 * If this Symbol should start with a text, set it here
	 * @param leadingText
	 */
	public void setLeadingText(String leadingText) {
		this.leadingText = leadingText;
	}
	public int getVerticalAlignment() {
		return verticalAlignment;
	}
	public void setVerticalAlignment(int horizontalAlignment) {
		this.verticalAlignment = horizontalAlignment;
	}
	public int getDocumentHeight() {
		return documentHeight;
	}
	public void setDocumentHeight(int documentHeight) {
		this.documentHeight = documentHeight;
	}
	public float getVerticalShift() {
		return verticalShift;
	}
	public void setVerticalShift(float verticalShift) {
		this.verticalShift = verticalShift;
	}
}
