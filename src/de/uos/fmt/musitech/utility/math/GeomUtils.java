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
 * Created on 2004-5-9
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.uos.fmt.musitech.utility.math;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Administrator
 */
public class GeomUtils {

    public static Point2D.Double getSegmentIntersection(Line2D l1, Line2D l2) {
        return getSegmentIntersection(l1.getX1(), l1.getY1(), l1.getX2(), l1.getY2(), l2
                .getX1(), l2.getY1(), l2.getX2(), l2.getY2());
    }

    /**
     * Gets the intersection point of two line segments given by two points each.
     * 
     * @param x1
     *            x of point1 of line1
     * @param y1
     *            y of point1 of line1
     * @param x2
     *            x of point2 of line1
     * @param y2
     *            y of point2 of line1
     * @param x3
     *            x of point1 of line2
     * @param y3
     *            y of point1 of line2
     * @param x4
     *            x of point2 of line2
     * @param y4
     *            y of point2 of line2
     * @return the intersection point, null if there is none. In case of overlapping
     *         segments of the same line, the middle point of the overlap is returned.
     */
    public static Point2D.Double getSegmentIntersection(double x1, double y1, double x2,
                                                        double y2, double x3, double y3,
                                                        double x4, double y4) {
        Line2D line1 = new Line2D.Double(x1, y1, x2, y2);
        Line2D line2 = new Line2D.Double(x3, y3, x4, y4);
        if (!line1.intersectsLine(line2))
            return null;
        double a1 = getOffset(x1, y1, x2, y2);
        double b1 = getSlope(x1, y1, x2, y2);
        double a2 = getSlope(x1, y1, x2, y2);
        double b2 = getSlope(x1, y1, x2, y2);
        if (Double.isInfinite(b1) || Double.isInfinite(b2)) {
            // vertical lines overlap
            double x = x1;
            if (y1 > y2) {
                double tmp = y2;
                y2 = y1;
                y1 = tmp;
            }
            if (y3 > y4) {
                double tmp = y4;
                y4 = y3;
                y3 = tmp;
            }
            double y = (y4 - y3) / 2;
            return new Point2D.Double(x, y);
        }
        if (b1 == 0.0 || b2 == 0.0) {
            // horzontal lines overlap
            double y = y1;
            if (x1 > x2) {
                double tmp = x2;
                x2 = x1;
                x1 = tmp;
            }
            if (x3 > x4) {
                double tmp = x4;
                x4 = x3;
                x3 = tmp;
            }
            double x = (x4 - x3) / 2;
            return new Point2D.Double(x, y);
        }
        if (Double.isInfinite(b1)) {
            // 1st line is vertical
            double x = x1;
            double y = a2 * x + b2;
            return new Point2D.Double(x, y);
        } else if (Double.isInfinite(b2)) {
            // 2st line is vertical
            double x = x2;
            double y = a1 * x + b1;
            return new Point2D.Double(x, y);
        } else
            return getIntersectionPoint(a1, b1, a2, b2);
    }

    /**
     * Returns the point of intersection of two given lines of type f(x)=a*x+b
     * 
     * @param a1
     *            the slope of line 1
     * @param b1
     *            the offset of line 1
     * @param a2
     *            the slope of line 2
     * @param b2
     *            the offset of line 2
     * @return The intersection point, null if the two lines are identical or do not
     *         intersect.
     */
    public static Point2D.Double getIntersectionPoint(double a1, double b1, double a2,
                                                      double b2) {

        double x, y;

        // calculate x
        if (a1 - a2 != 0) {
            x = (b2 - b1) / (a1 - a2);
        } else
            //lines are identical or parallel
            return null;

        // calculate y
        y = a1 * x + b1;

        return new Point2D.Double(x, y);
    }

    /**
     * Return the slope of the line given by two two points.
     * 
     * @param x1
     *            the x coordinate of point 1
     * @param y1
     *            the y coordinate of point 1
     * @param x2
     *            the x coordinate of point 2
     * @param y2
     *            the y coordinate of point 2
     * @return The line's slope. <code>Double.NaN</code> if the two points are
     *         identical, <code>Double.POSITIVE_INFINITY</code> or
     *         <code>Double.NEGATIVE_INFINITY</code> if the line is vertical.
     */
    public static double getSlope(double x1, double y1, double x2, double y2) {
        if (y1 == y2 && x1 == x2)
            return Double.NaN;
        if (x2 - x1 != 0)
            return (y2 - y1) / (x2 - x1);
        else {
            if (y2 > y1)
                return Double.POSITIVE_INFINITY;
            else {
                return Double.NEGATIVE_INFINITY;
            }
        }
    }

    /**
     * Calculates the the intercept on the y-Axis for a line given by two points.
     * 
     * @param x1
     *            the x coordinate of point 1
     * @param y1
     *            the y coordinate of point 1
     * @param x2
     *            the x coordinate of point 2
     * @param y2
     *            the y coordinate of point 2
     * @return The line's offset from the origin the on the y-axis.
     *         <code>Double.NaN</code> if the line is vertical or a point.
     */
    public static double getOffset(double x1, double y1, double x2, double y2) {

        double vx = x2 - x1;
        double vy = y2 - y1;

        // return NaN if point or vertical
        if (vx == 0)
            return Double.NaN;

        //solve the equations
        // vx * r + x1 = 0
        // vy * r + y1 = offset
        // => offset = y1-(x1*(vy/vx))

        return y1 - (x1 * (vy / vx));
    }

    public static void main(String[] args) {
        double x1 = 0;
        double y1 = 2;
        double x2 = 1;
        double y2 = -1;
        System.out.println("p1: " + x1 + ", " + y1);
        System.out.println("p2: " + x2 + ", " + y2);
        System.out.println("Geradengleichung: " + getSlope(x1, y1, x2, y2) + " * x + "
                           + getOffset(x1, y1, x2, y2) + "\n");

        double a1 = 2.1;
        double b1 = 20;
        double a2 = 2;
        double b2 = 19;
        System.out.println("y1 = " + a1 + " * x1 + " + b1);
        System.out.println("y2 = " + a2 + " * x2 + " + b2);
        System.out.println("Schnittpunkt: " + getIntersectionPoint(a1, b1, a2, b2));
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param to
     * @param from
     * @param getY
     * @param getX
     * @return
     */
    public static Point calculateAnchor(double x1, double y1, double x2, double y2,
                                        Rectangle from, Rectangle to, int getX, int getY) {
        Point anchor;
        // variables of the line-equation
        // names derive from the notation y=a*x+b
        double a1, b1, a2, b2, boxDist;
        Point2D.Double hAnchor, vAnchor;

        //** case: horizontal node-border

        a1 = GeomUtils.getSlope(x1, y1, x2, y2);
        b1 = GeomUtils.getOffset(x1, y1, x2, y2);

        boxDist = to.getHeight() / 2;

        if (Double.isInfinite(a1)) {
            if (y2 < y1) {
                hAnchor = new Point2D.Double(x2, y2 + boxDist);
            } else {
                hAnchor = new Point2D.Double(x2, y2 - boxDist);
            }
        } else {
            a2 = 0;
            if (y2 < y1) {
                // use upper border
                b2 = y2 + boxDist;
            } else {
                // use lower border
                b2 = y2 - boxDist;
            }
            hAnchor = GeomUtils.getIntersectionPoint(a1, b1, a2, b2);
            if (hAnchor == null) {
                hAnchor = new Point2D.Double(Double.NaN, Double.NaN);
            }
        }

        //** case: vertical node-border

        a1 = GeomUtils.getSlope(y1, x1, y2, x2);
        b1 = GeomUtils.getOffset(y1, x1, y2, x2);

        boxDist = to.getWidth() / 2;

        if (Double.isInfinite(a1)) {
            if (x2 < x1) {
                vAnchor = new Point2D.Double(y2, x2 + boxDist);
            } else {
                vAnchor = new Point2D.Double(y2, x2 - boxDist);
            }
        } else {
            a2 = 0;
            if (x2 < x1) {
                // use upper border (= right border)
                b2 = x2 + boxDist;
            } else {
                // use lower border (= left border)
                b2 = x2 - boxDist;
            }
            vAnchor = GeomUtils.getIntersectionPoint(a1, b1, a2, b2);
            if (vAnchor == null) {
                vAnchor = new Point2D.Double(Double.NaN, Double.NaN);
            }
        }
        vAnchor = new Point2D.Double(vAnchor.getY(), vAnchor.getX());

        anchor = new Point();

        // translate Coordinates
        if (hAnchor != null) {
            hAnchor.setLocation(hAnchor.getX() - getX, hAnchor.getY() - getY);
            //anchor.setLocation(hAnchor.getX(),hAnchor.getY());
        }
        if (vAnchor != null) {
            vAnchor.setLocation(vAnchor.getX() - getX, vAnchor.getY() - getY);
            //anchor.setLocation(vAnchor.getX(),vAnchor.getY());
        }

        if (vAnchor == null && hAnchor == null) {
            anchor = null;
        }

        // choose which anchor to use

        //double angle = Math.atan(vy/vx);

        Rectangle toBounds = to.getBounds();
        boolean containsHAnchor = hAnchor.getX() + getX > toBounds.x;
        containsHAnchor = containsHAnchor
                          && hAnchor.getX() + getX < toBounds.x + toBounds.width;
        if (containsHAnchor) {
            anchor.setLocation(hAnchor.x, hAnchor.y);
        } else {
            anchor.setLocation(vAnchor.x, vAnchor.y);
        }

        return anchor;
    }

//    /**
//     * 
//     * @param bounds
//     * @param drawPosition
//     * @param drawPosition2
//     * @return
//     */
//    public static Point getRectangleAnchor(Rectangle toBounds, Point2D.Double from,
//                                           Point2D.Double to) {
//        double boxX1 = toBounds.getX();
//        double boxY1 = toBounds.getY();
//        double boxX2 = toBounds.getX() + toBounds.getWidth();
//        double boxY2 = toBounds.getY() + toBounds.getHeight();
//        Line2D boxUpper = new Line2D.Double(boxX1, boxY1, boxX2, boxY1);
//        Line2D boxLower = new Line2D.Double(boxX1, boxY2, boxX2, boxY2);
//        Line2D boxLeft = new Line2D.Double(boxX1, boxY1, boxX1, boxY2);
//        Line2D boxRight = new Line2D.Double(boxX2, boxY1, boxX2, boxY2);
//
//        Line2D edge = new Line2D.Double(from, to);
//
//        if (edge.intersectsLine(boxUpper)) {
//
//        }
//
//        //line.intersectsLine();
//        return null;
//    }

    // the height of an equal sided trangle
    static final double TRI_HI = 0.86602540378443864676372317075294;

    /**
     * Calculates a triangle tip (equal sides) of the given size at the end of the given
     * line segment.
     * 
     * @param end The end point.
     * @param start The starting point.
     * @param size The lenght of the triangle's sides.
     * @return The triangle, null if either of the points is <code>null</code> or size is 0.
     */
    public static Polygon getTriangleTip(double endx, double endy, double startx, double starty, int size) {

        // calculate the vector
        double vx = endx - startx;
        double vy = endy - starty;
        double l = MyMath.vectorLength(vx, vy);
        if (l == 0)
            return null;

        // normalize the vector length
        double vsx = vx / l * size;
        double vsy = vy / l * size;

        // orthogonal of normalized vector
        double onvx = -vsy / 2;
        double onvy = vsx / 2;

        // set the length of the normalized to size*triangle height
        vsx *= TRI_HI;
        vsy *= TRI_HI;

        // fill the points of the arrow-tip
        Polygon newArrow = new Polygon();
        newArrow.addPoint((int)endx, (int)endy);
        newArrow.addPoint((int) (endx + onvx - vsx), (int) (endy + onvy - vsy));
        newArrow.addPoint((int) (endx - onvx - vsx), (int) (endy - onvy - vsy));
        newArrow.addPoint((int)endx, (int)endy);
        return newArrow;
    }
    
    public static Rectangle getUnion(Rectangle argR1, Rectangle argR2) {
    	if(argR1 == null && argR2 != null)
    		return argR2;
       	if(argR1 != null && argR2 == null)
       		return argR1;
    	int x = Math.min( argR1.x, argR2.x); 
    	int y = Math.max( argR1.y, argR2.y);
    	int w = Math.max(argR1.x+argR1.width, argR2.x+argR2.width) - x; 
    	int h = Math.max(argR1.y+argR1.height, argR2.y+argR2.height) - y; 
    	Rectangle unionR = new Rectangle(x,y,w,h);
    	return unionR;
    }

}