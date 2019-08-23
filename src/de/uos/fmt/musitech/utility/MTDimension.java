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
package de.uos.fmt.musitech.utility;

/**
 * Adaptation of the Dimension class.
 * @date (20.07.00 12:53:02)
 * @author TW
 * @version 0.113
 */
public class MTDimension extends java.awt.Dimension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6230957694117761075L;

	/**
	 * Constuctor.
	 * @date (20.07.00 13:03:21)
	 */
	public MTDimension() {
		width = 0;
		height = 0;
	}

	/**
	 * Constructor.
	 * @date (20.07.00 13:05:23)
	 * @param width int
	 * @param height int
	 */
	public MTDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructor.
	 * @date (20.07.00 13:04:13)
	 * @param dim java.awt.Dimension
	 */
	public MTDimension(java.awt.Dimension dim) {
		width = (int) dim.getWidth();
		height = (int) dim.getHeight();
	}

	/**
	 * Increments the coordinates.
	 * @date (20.07.00 12:54:51)
	 * @param x double
	 * @param y double
	 */
	public void add(double x, double y) {
		width += x;
		height += y;
	}

	/**
	 * Increments the coordinates.
	 * @date (20.07.00 12:54:14)
	 * @param x int
	 * @param y int
	 */
	public void add(int x, int y) {
		width += x;
		height += y;
	}

	/**
	 * Adds the given Dimension's width and height to this dimension's.
	 * @date (20.07.00 12:53:55)
	 * @param dim java.awt.Dimension
	 */
	public void add(java.awt.Dimension dim) {
		add(dim.getWidth(), dim.getHeight());
	}

	/**
	 * Adds the given Inset's left and right to the width and its top and bottom 
	 * to  the height of this dimension's.
	 * @date (20.07.00 12:57:45)
	 * @param ins java.awt.Insets
	 */
	public void add(java.awt.Insets ins) {
		add(ins.left + ins.right, ins.top + ins.bottom);
	}

	/**
	 * Adds the Point's x and y components to this dimension'swidth and height.
	 * @date (20.07.00 12:55:14)
	 * @param pt java.awt.Point
	 */
	public void add(java.awt.Point pt) {
		add(pt.getX(), pt.getX());
	}
}