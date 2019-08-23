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
package de.uos.fmt.musitech.data.media.image;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Jan
 *  
 */
public class StaffPosition {

    int x, y, width, height;

    long start, duration;

    /**
     *  
     */
    public StaffPosition() {
    }

    /**
     *  
     */
    public StaffPosition(Rectangle staff, long start, long duration) {
        setX(staff.x);
        setY(staff.y);
        setWidth(staff.width);
        setHeight(staff.height);
        setStart(start);
        setDuration(duration);
    }

    public StaffPosition(int x, int y, int height, int width, long start,
            long duration) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setStart(start);
        setDuration(duration);
    }

    /**
     * @return Returns the duration.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration
     *            The duration to set.
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @param staff
     *            The staff to set.
     */
    public void setStaff(Rectangle staff) {
        x = staff.x;
        y = staff.y;
        width = staff.width;
        height = staff.height;

    }

    /**
     * @return Returns the start.
     */
    public long getStart() {
        return start;
    }

    public void moveAbsolute(Point start) {
        x = start.x;
        y = start.y;
    }

    public void moveAbsolute(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Move UpperLeft Corner relative
     * 
     * @param dx
     * @param dy
     */
    public void moveRelative(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * @param start
     *            The start to set.
     */
    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return start + duration;
    }

    /**
     * @param left
     * @param right
     * @param up
     * @param down
     */
    public void resizeRelative(int left, int right, int up, int down) {
        System.out.println("left: " + left + " right: " + right + " up: " + up
                + " down " + down);
        x += left;
        width -= left;
        width += right;
        y += up;
        height -= up;
        height += down;
        
        if (width < 0){
            width = -width;
            x -= width;
        }
        if (height < 0){
            height = -height;
            y -= height;
        }
        
        

    }

    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *            The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width
     *            The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Returns the x.
     */
    public int getX() {
        return x;
    }

    /**
     * @param x
     *            The x to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return Returns the y.
     */
    public int getY() {
        return y;
    }

    /**
     * @param y
     *            The y to set.
     */
    public void setY(int y) {
        this.y = y;
    }
}