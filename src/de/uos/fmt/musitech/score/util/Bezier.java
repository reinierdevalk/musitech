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
package de.uos.fmt.musitech.score.util;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.utility.math.MyMath;

/**
 * This class represents a B?zier curve segment.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7931 $, $Date: 2011-05-31 18:37:00 +0200 (Tue, 31 May 2011) $
 */
public class Bezier {

    private Pair[] points = null;
    private boolean dotted;

    Bezier() {
    }

    /** Constructs a new B?zier curve defined by 4 given control points. */
    public Bezier(double[] p) {
        if (p.length < 8)
            throw new IllegalArgumentException("\nBezier needs at least 8 double values");
        points = new Pair[4];
        for (int i = 0; i < 4; i++)
            points[i] = new Pair(p[2 * i], p[2 * i + 1]);
    }

    /**
     * Constructs a new B?zier curve defined by 4 given control points. The components of
     * each point are listed seperately.
     */
    public Bezier(double x1, double y1, double x2, double y2, double x3, double y3,
            double x4, double y4) {
        points = new Pair[4];
        points[0] = new Pair(x1, y1);
        points[1] = new Pair(x2, y2);
        points[2] = new Pair(x3, y3);
        points[3] = new Pair(x4, y4);
    }

    /**
     * Creates a B?zier curve segment defined by 4 control points. The 1st and 4th point
     * build the curve's start and end point. The 2 remaining ones control the shape of
     * the curve.
     */
    public Bezier(Pair p1, Pair p2, Pair p3, Pair p4) {
        points = new Pair[4];
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
    }

    /*
     * Constructs a symmetric B?zier curve with given start and end point, height and
     * gradient angle of the starting curve tangent. @param p1 start point @param p4 end
     * point @param height curve height (distance between (p1+p4)/2 and pointAt(1/2)); the
     * height may be negative to change the bow's direction @param theta gradient angle
     * (in radians) of curve tangent at p1; as a result the angle of curve tangent at p4
     * is -theta.
     */
    /*
     * public Bezier (Pair p1, Pair p4, double height, Angle theta) { if (theta.radians() ==
     * 0.0 || Math.abs(theta.radians()) >= Math.PI) throw new
     * IllegalArgumentException("Bezier: theta mod PI must not be zero"); if (height ==
     * 0.0) throw new IllegalArgumentException("Bezier: height must not be zero");
     * 
     * 
     * Pair heightUnitVec = p4.sub(p1).ortho().unitVector(); Pair w =
     * heightUnitVec.mul(4*height/3); Pair v =
     * w.ortho().unitVector().mul(w.length()*theta.cot()); points = new Pair[4]; points[0] =
     * p1; points[1] = p1.add(w).sub(v); points[2] = p4.add(w).add(v); points[3] = p4; }
     */

    /*
     * public Bezier (Pair p1, double length, Angle phi, double height, Angle theta) { if
     * (theta.radians() == 0.0 || Math.abs(theta.radians()) >= Math.PI) throw new
     * IllegalArgumentException("Bezier: theta mod PI must not be zero"); if (height ==
     * 0.0) throw new IllegalArgumentException("Bezier: height must not be zero"); Pair p4 =
     * new Pair(phi.cos(), -phi.sin()).mul(length).add(p1); Pair heightUnitVec =
     * p4.sub(p1).ortho().unitVector(); Pair w = heightUnitVec.mul(4*height/3); Pair v =
     * w.ortho().unitVector().mul(w.length()*theta.cot()); points = new Pair[4]; points[0] =
     * p1; points[1] = p1.add(w).sub(v); points[2] = p4.add(w).add(v); points[3] = p4; }
     */

    public Bezier(Pair p1, Angle angle1, double velocity1, Angle angle2,
            double velocity2, Pair p4) {
        points = new Pair[4];
        points[0] = p1;
        points[1] = p1.add(new Pair(angle1.cos(), angle1.sin()).mul(velocity1));
        points[2] = p4.sub(new Pair(angle2.cos(), -angle2.sin()).mul(velocity2));
        points[3] = p4;
    }

    /** Returns the 4 control points defining the current B?zier curve. */
    public Pair[] getPoints() {
        return points;
    }

    /**
     * Replaces the control points.
     * 
     * @param points
     *            the new array of 4 control points (must contain exactly 4 pairs)
     * @throws InvalidArgumentException
     *             if 'points' doesn't contain 4 elements
     */
    public void setPoints(Pair[] points) {
        if (points.length == 4)
            this.points = points;
        else
            throw new IllegalArgumentException("array of points must contain 4 elements");
    }

    /**
     * Replaces one of the four B?zier control points.
     * 
     * @param number
     *            of control point to be changed (0..3)
     * @param p
     *            value of new control paint
     * @throws InvalidArgumentException
     *             if 'n' isn't in range 0..3
     */
    public void setPoint(int n, Pair p) {
        if (n >= 0 && n <= 3)
            points[n] = p;
        else
            throw new IllegalArgumentException("numPoints must be in range 0..3");
    }

    /** Returns a string representation of this Pair. */
    @Override
	public String toString() {
        return "Bezier(" + points[0] + ", " + points[1] + ", " + points[2] + ", "
               + points[3] + ")";
    }

    /**
     * Approximates this B?zier curve.
     * 
     * @param pm
     *            function that defines how to mark the approximated curve points
     * @param depth
     *            maximal refinement/subdividing depth
     * @param limit
     *            distance between start and end point that prevent further refinements
     */
    public void approximate(PointMarker pm, int depth, double limit) {
        pm.markFirstPoint(points[0]);
        casteljau(points[0], points[1], points[2], points[3], pm, depth, limit);
    }

    /**
     * Approximates a B?zier spline defined by 4 given control points. This method uses
     * DeCasteljau's geometric subdividing algorithm.
     * 
     * @param p0
     *            starting point of the spline segment
     * @param p1
     *            first control point
     * @param p2
     *            second control point
     * @param p3
     *            end point of the spline segment
     * @param pm
     *            function that defines how to mark the approximated curve points
     * @param depth
     *            maximal refinement/subdividing depth
     * @param limit
     *            distance between start and end point that prevent further refinements
     */
    protected static void casteljau(Pair p0, Pair p1, Pair p2, Pair p3, PointMarker pm,
                                    int depth, double limit) {
        if (depth < 1 || p0.distanceTo(p3) < limit)
            pm.markPoint(p3);
        else {
            Pair q0 = p0.add(p1).div(2), q1 = p1.add(p2).div(2), q2 = p2.add(p3).div(2);
            Pair r0 = q0.add(q1).div(2), r1 = q1.add(q2).div(2);
            Pair s0 = r0.add(r1).div(2);
            casteljau(p0, q0, r0, s0, pm, --depth, limit);
            casteljau(s0, r1, q2, p3, pm, depth, limit);
        }
    }

    /** Paints this B?zier curve onto a given Graphics object. */
    public void paint(Graphics g) {
        /*
         * g.setColor(Color.BLUE); for (int i = 0; i < 4; i++) g.drawRect((int)
         * points[i].getX() - 2, (int) points[i].getY() - 2, 4, 4);
         */
        //g.setColor(Color.BLACK);
    	LinePointMarker lpm = new LinePointMarker(g);
    	lpm.setDotted(dotted);
        approximate(lpm, 5, 5);

    }

    /**
     * Approximates this B?zier curve and returns the computed curve points.
     * 
     * @param maxPoints
     *            The number of points computed is always a power of 2 plus 1. This
     *            parameter gives an upper border that will not be crossed. E.g. maxPoints =
     *            60, 33 curve points are computed because 33 is the greatest integer
     *            value that solves the inequation 2^d+1 <= maxPoints
     * @return the computed curve points
     */
    public Pair[] collectToArray(int maxPoints) {
        int iterationDepth = MyMath.ilog2(maxPoints - 1);
        Pair[] points = new Pair[(2 << (iterationDepth - 1)) + 1];
        ArrayPointMarker marker = new ArrayPointMarker(points);
        approximate(marker, iterationDepth, 0);
        return points;
    }

    /**
     * Approximates this Bezier curve and returns the computed curve points.
     * 
     * @param maxPoints
     *            The number of points computed is always a power of 2 plus 1. This
     *            parameter gives an upper border that will not be crossed. E.g. maxPoints =
     *            60, 33 curve points are computed because 33 is the greatest integer
     *            value that solves the inequation 2^d+1 <= maxPoints
     * @return the computed curve points
     */
    public List<Pair> collectToList(int maxPoints) {
        int iterationDepth = MyMath.ilog2(maxPoints - 1);
        List<Pair> pointList = new ArrayList<Pair>();
        CollectPointMarker marker = new CollectPointMarker(pointList);
        approximate(marker, iterationDepth, 0);
        return pointList;
    }

    /**
     * Approximates this B?zier curve and adds the computed curve points to a given
     * Vector.
     * 
     * @param points
     *            The computed points are added to this vector.
     * @param maxPoints
     *            The number of points computed is always a power of 2 plus 1. This
     *            parameter gives an upper border that will not be crossed. E.g. maxPoints =
     *            60, 33 curve points are computed because 33 is the greatest integer
     *            value that solves the inequation 2^d+1 <= maxPoints
     * @param reverse
     *            if true, the points are appended in reversed order.
     */
    public void addToList(List<Pair> points, int maxPoints, boolean reverse) {
        int iterationDepth = MyMath.ilog2(maxPoints - 1);
        CollectPointMarker marker = new CollectPointMarker(points, reverse);
        approximate(marker, iterationDepth, 0);
    }

    /**
     * Returns the curve point for a given 'time' parameter. 0 and 1 denote the curve's
     * start and end point.
     * <p>
     * b(t) = p <sub>3 </sub>t <sup>3 </sup>+ 3p <sub>2 </sub>(1-t)t <sup>2 </sup>+ 3p
     * <sub>1 </sub>(1-t) <sup>2 </sup>t + p <sub>0 </sub>(1-t) <sup>3 </sup>
     */
    public Pair pointAt(double t) {
        double t2 = t * t; // t^2
        double t3 = t * t2; // t^3
        double u = 1 - t;
        double u2 = u * u; // (1-t)^2
        double u3 = u * u2; // (1-t)^3
        return points[3].mul(t3).add(points[2].mul(3.0 * t2 * u))
                .add(points[1].mul(3.0 * t * u2)).add(points[0].mul(u3));
    }

    /** Finds all 'time' parameters t for a given x-coordinate. b(t)=(x(t), y(t)) */
    public double[] xToParam(double x) {
        double x0 = points[0].getX();
        double x1 = points[1].getX();
        double x2 = points[2].getX();
        double x3 = points[3].getX();
        double t[] = MyMath.solveCubicEq(x3 - 3 * x2 + 3 * x1 - x0,
                                         3 * (x2 - 2 * x1 + x0), 3 * (x1 - x0), x0 - x);
        // remove solutions outside B?zier curve segment
        int numSolutions = t.length;
        int i = 0;
        while (i < numSolutions)
            if (t[i] < 0 || t[i] > 1) {
                for (int j = i; j < numSolutions - 1; j++)
                    t[j] = t[j + 1];
                numSolutions--;
            } else
                i++;
        double[] result = new double[numSolutions];
        System.arraycopy(t, 0, result, 0, numSolutions);
        return result;
    }

    /** Finds all 'time' parameters t for a given y-coordinate. b(t)=(x(t), y(t)) */
    public double[] yToParam(double y) {
        double y0 = points[0].getY();
        double y1 = points[1].getY();
        double y2 = points[2].getY();
        double y3 = points[3].getY();
        double t[] = MyMath.solveCubicEq(y3 - 3 * y2 + 3 * y1 - y0,
                                         3 * (y2 - 2 * y1 + y0), 3 * (y1 - y0), y0 - y);
        // remove solutions outside B?zier curve segment
        int numSolutions = t.length;
        int i = 0;
        while (i < numSolutions)
            if (t[i] < 0 || t[i] > 1) {
                for (int j = i; j < numSolutions - 1; j++)
                    t[j] = t[j + 1];
                numSolutions--;
            } else
                i++;
        double[] result = new double[numSolutions];
        System.arraycopy(t, 0, result, 0, numSolutions);
        return result;
    }

    /** Returns an array of x-coordinates for a given y-coordinate that lay on the curve. */
    public double[] getX(double y) {
        double[] res = yToParam(y);
        for (int i = 0; i < res.length; i++)
            res[i] = pointAt(res[i]).getX();
        return res;
    }

    /** Returns an array of y-coordinates for a given x-coordinate that lay on the curve. */
    public double[] getY(double x) {
        double[] res = xToParam(x);
        for (int i = 0; i < res.length; i++)
            res[i] = pointAt(res[i]).getY();
        return res;
    }

    /**
     * This is a helper function that performs DeCasteljau's algorithm on a x- or
     * y-projection of 4 given control points. The result of this procedure is an closed
     * interval of (1 dimensional) projection points. The function returns the bounds of
     * this interval in parameter 'result'. While the x-component represents the minimum
     * the y-component contains the maximum. The method is used to determine the bounding
     * box of a B?zier curve.
     * 
     * @param z0
     *            projection of first B?zier-control point
     * @param z1
     *            projection of second B?zier-control point
     * @param z2
     *            projection of third B?zier-control point
     * @param z3
     *            projection of fourth B?zier-control point
     * @param result
     *            here the maximum and minimum is stored
     * @param depth
     *            depth of refinement
     * @return false, if depth less than 0 or if result == null
     */
    protected static boolean subdivide(double z0, double z1, double z2, double z3,
                                       double[] result, int depth) {
        if (depth < 0 || result == null)
            return false;

        if (z0 <= z1 && z1 <= z2 && z2 <= z3) // monoton steigend?
        {
            if (z0 < result[0])
                result[0] = z0;
            if (z3 > result[1])
                result[1] = z3;
        } else if (z0 >= z1 && z1 >= z2 && z2 >= z3) // monoton fallend?
        {
            if (z3 < result[0])
                result[0] = z3;
            if (z0 > result[1])
                result[1] = z0;
        } else if (depth >= 0) // do another refinement
        {
            double a0 = (z0 + z1) / 2, a1 = (z1 + z2) / 2, a2 = (z2 + z3) / 2;
            double b0 = (a0 + a1) / 2, b1 = (a1 + a2) / 2;
            double c0 = (b0 + b1) / 2;
            subdivide(z0, a0, b0, c0, result, --depth);
            subdivide(c0, b1, a2, z3, result, depth);
        }
        return true;
    }

    protected static boolean findExtrema(Pair p0, Pair p1, Pair p2, Pair p3,
                                         Pair[] result, int depth) {
        if (depth < 0 || result == null)
            return false;

        if (p0.getX() <= p1.getX() && p1.getX() <= p2.getX() && p2.getX() <= p3.getX()) // monoton
                                                                                        // steigend?
        {
            if (p0.getX() < result[0].getX())
                result[0] = p0;
            if (p3.getX() > result[1].getX())
                result[1] = p3;
        } else if (p0.getX() >= p1.getX() && p1.getX() >= p2.getX()
                   && p2.getX() >= p3.getX()) // monoton fallend?
        {
            if (p3.getX() < result[0].getX())
                result[0] = p3;
            if (p0.getX() > result[1].getX())
                result[1] = p0;
        } else if (depth >= 0) // do another refinement
        {
            Pair a0 = p0.add(p1).div(2), a1 = p1.add(p2).div(2), a2 = p2.add(p3).div(2);
            Pair b0 = a0.add(a1).div(2), b1 = a1.add(a2).div(2);
            Pair c0 = b0.add(b1).div(2);
            findExtrema(p0, a0, b0, c0, result, --depth);
            findExtrema(c0, b1, a2, p3, result, depth);
        }
        return true;
    }

    /**
     * Returns the absolute horizontal bounds of this B?zier curve.
     * 
     * @return minimum (leftmost bound) at index 0 and maximum (rightmost bound) at index
     *         1.
     */
    public double[] horizontalBounds() {
        double[] res = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        subdivide(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX(),
                  res, 5);
        return res;
    }

    /**
     * Returns the absolute vertical bounds of this B?zier curve.
     * 
     * @return minimum (upper bound) at index 0 and maximum (lower bound) at index 1.
     */
    public double[] verticalBounds() {
        double[] res = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        subdivide(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY(),
                  res, 5);
        return res;
    }

    /**
     * Returns the 2 points of this B?zier curve with minimal and maximal x coordinate.
     * The minimum gets index 0 in the returned Pair array.
     */
    public Pair[] horizontalExtrema() {
        Pair[] result = new Pair[2];
        result[0] = new Pair(Double.POSITIVE_INFINITY, 0);
        result[1] = new Pair(Double.NEGATIVE_INFINITY, 0);
        findExtrema(points[0], points[1], points[2], points[3], result, 5);
        // put the minimum at position 0
        if (result[0].getX() > result[1].getX()) {
            Pair t = result[0];
            result[0] = result[1];
            result[1] = t;
        }
        return result;
    }

    /**
     * Returns the 2 points of this B?zier curve with minimal and maximal y coordinate.
     * The minimum gets index 0 in the returned Pair array.
     */
    public Pair[] verticalExtrema() {
        Pair[] result = new Pair[2];
        result[0] = new Pair(Double.POSITIVE_INFINITY, 0);
        result[1] = new Pair(Double.NEGATIVE_INFINITY, 0);
        findExtrema(points[0].swapped(), points[1].swapped(), points[2].swapped(),
                    points[3].swapped(), result, 5);
        result[0].swap();
        result[1].swap();
        return result;
    }

    /**
     * Returns a PolyLine approximation of this B?zier curve.
     * 
     * @param numPoints
     *            The number of points computed is always a power of 2 plus 1. This
     *            parameter gives an upper border that will not be crossed. E.g. maxPoints =
     *            60, 33 curve points are computed because 33 is the greatest integer
     *            value that solves the inequation 2^d+1 &leq; maxpoints
     */
    public PolyLine toPolyLine(int numPoints) {
        return new PolyLine(collectToArray(numPoints));
    }
    
	public boolean isDotted() {
		return dotted;
	}
	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}
}