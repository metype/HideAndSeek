package com.metype.hidenseek;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

public class Polygon {
    public static class Point {
        public int x, y;
        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static class Line {
        public Point p1, p2;
        public Line(Point p1, Point p2)
        {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    public Point[] points;

    public Polygon() {
        points = new Point[3];
        for(int i=0; i<3; i++){
            points[i] = new Point(0,0);
        }
    }

    public Polygon(int size) {
        points = new Point[size];
        for(int i=0; i<size; i++){
            points[i] = new Point(0,0);
        }
    }

    private int onLine(Line l1, Point p)
    {
        // Check whether p is on the line or not
        if (p.x <= Math.max(l1.p1.x, l1.p2.x)
                && p.x >= Math.min(l1.p1.x, l1.p2.x)
                && (p.y <= Math.max(l1.p1.y, l1.p2.y)
                && p.y >= Math.min(l1.p1.y, l1.p2.y)))
            return 1;

        return 0;
    }

    private int direction(Point a, Point b, Point c)
    {
        int val = (b.y - a.y) * (c.x - b.x)
                - (b.x - a.x) * (c.y - b.y);

        if (val == 0)

            // Collinear
            return 0;

        else if (val < 0)

            // Anti-clockwise direction
            return 2;

        // Clockwise direction
        return 1;
    }

    private int isIntersect(Line l1, Line l2)
    {
        // Four direction for two lines and points of other
        // line
        int dir1 = direction(l1.p1, l1.p2, l2.p1);
        int dir2 = direction(l1.p1, l1.p2, l2.p2);
        int dir3 = direction(l2.p1, l2.p2, l1.p1);
        int dir4 = direction(l2.p1, l2.p2, l1.p2);

        // When intersecting
        if (dir1 != dir2 && dir3 != dir4)
            return 1;

        // When p2 of line2 are on the line1
        if (dir1 == 0 && onLine(l1, l2.p1) == 1)
            return 1;

        // When p1 of line2 are on the line1
        if (dir2 == 0 && onLine(l1, l2.p2) == 1)
            return 1;

        // When p2 of line1 are on the line2
        if (dir3 == 0 && onLine(l2, l1.p1) == 1)
            return 1;

        // When p1 of line1 are on the line2
        if (dir4 == 0 && onLine(l2, l1.p2) == 1)
            return 1;

        return 0;
    }

    public int checkInside(Point p)
    {

        // When polygon has less than 3 edge, it is not
        // polygon

        if (points.length < 3)
            return 0;

        // Create a point at infinity, y is same as point p
        Point pt = new Point(9999, p.y);
        Line exline = new Line(p, pt);
        int count = 0;
        int i = 0;
        do {

            // Forming a line from two consecutive points of
            // poly
            Line side
                    = new Line(points[i], points[(i + 1) % points.length]);
            if (isIntersect(side, exline) == 1) {

                // If side is intersects exline
                if (direction(side.p1, p, side.p2) == 0)
                    return onLine(side, p);
                count++;
            }
            i = (i + 1) % points.length;
        } while (i != 0);

        // When count is odd
        return count & 1;
    }
}

