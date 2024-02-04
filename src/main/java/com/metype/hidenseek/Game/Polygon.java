package com.metype.hidenseek.Game;

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

