package com.metype.hidenseek.Game;
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

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Point point)) return false;
            return this.x == point.x && this.y == point.y;
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

    public ArrayList<Point> points;

    public Polygon() {
        points = new ArrayList<>();
        for(int i=0; i<3; i++){
            points.add(new Point(0,0));
        }
    }

    public Polygon(int size) {
        points = new ArrayList<>();
        for(int i=0; i<size; i++){
            points.add(new Point(0,0));
        }
    }

    private boolean onLine(Line l1, Point p)
    {
        // Check whether p is on the line or not
        if (p.x <= Math.max(l1.p1.x, l1.p2.x)
                && p.x >= Math.min(l1.p1.x, l1.p2.x)
                && (p.y <= Math.max(l1.p1.y, l1.p2.y)
                && p.y >= Math.min(l1.p1.y, l1.p2.y)))
            return true;

        return false;
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

    private boolean isIntersect(Line l1, Line l2)
    {
        // Four direction for two lines and points of other
        // line
        int dir1 = direction(l1.p1, l1.p2, l2.p1);
        int dir2 = direction(l1.p1, l1.p2, l2.p2);
        int dir3 = direction(l2.p1, l2.p2, l1.p1);
        int dir4 = direction(l2.p1, l2.p2, l1.p2);

        // When intersecting
        if (dir1 != dir2 && dir3 != dir4)
            return true;

        // When p2 of line2 are on the line1
        if (dir1 == 0 && onLine(l1, l2.p1))
            return true;

        // When p1 of line2 are on the line1
        if (dir2 == 0 && onLine(l1, l2.p2))
            return true;

        // When p2 of line1 are on the line2
        if (dir3 == 0 && onLine(l2, l1.p1))
            return true;

        // When p1 of line1 are on the line2
        return dir4 == 0 && onLine(l2, l1.p2);
    }

    public boolean checkInside(Point p)
    {

        int hits = 0;

        double lastPosX = points.get(points.size()-1).x;
        double lastPosY = points.get(points.size()-1).y;
        double curPosX, curPosY;

        for (int i = 0; i < points.size(); lastPosX = curPosX, lastPosY = curPosY, i++) {
            curPosX = points.get(i).x;
            curPosY = points.get(i).y;

            if (curPosY == lastPosY) {
                continue;
            }

            double leftPosX;
            if (curPosX < lastPosX) {
                if (p.x >= lastPosX) {
                    continue;
                }
                leftPosX = curPosX;
            } else {
                if (p.x >= curPosX) {
                    continue;
                }
                leftPosX = lastPosX;
            }

            double testA, testB;
            if (curPosY < lastPosY) {
                if (p.y < curPosY || p.y >= lastPosY) {
                    continue;
                }
                if (p.x < leftPosX) {
                    hits++;
                    continue;
                }
                testA = p.x - curPosX;
                testB = p.y - curPosY;
            } else {
                if (p.y < lastPosY || p.y >= curPosY) {
                    continue;
                }
                if (p.x < leftPosX) {
                    hits++;
                    continue;
                }
                testA = p.x - lastPosX;
                testB = p.y - lastPosY;
            }

            if (testA < (testB / (lastPosY - curPosY) * (lastPosX - curPosX))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    public void addPoint(Point p) {
        int indexOfClosestPoint = 0;
        for(int i = 0; i < points.size(); i++){
            float distSqr = (p.x - points.get(i).x) * (p.x - points.get(i).x) + (p.y - points.get(i).y) * (p.y - points.get(i).y);
            float distSqrComp = (p.x - points.get(indexOfClosestPoint).x) * (p.x - points.get(indexOfClosestPoint).x) + (p.y - points.get(indexOfClosestPoint).y) * (p.y - points.get(indexOfClosestPoint).y);
            if(distSqr < distSqrComp) {
                indexOfClosestPoint = i;
            }
        }

        points.add(indexOfClosestPoint, p);
    }
}

