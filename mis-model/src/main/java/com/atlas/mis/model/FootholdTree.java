package com.atlas.mis.model;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public record FootholdTree(FootholdTree northWest, FootholdTree northEast, FootholdTree southWest, FootholdTree southEast,
                           List<Foothold> footholds, Point p1, Point p2, Point center, int depth, int maxDropX, int minDropX) {

   private List<Foothold> getRelevants(Point p) {
      return getRelevants(p, new LinkedList<>());
   }

   private List<Foothold> getRelevants(Point p, List<Foothold> list) {
      list.addAll(footholds);
      if (northWest != null) {
         if (p.x <= center.x && p.y <= center.y) {
            northWest.getRelevants(p, list);
         } else if (p.x > center.x && p.y <= center.y) {
            northEast.getRelevants(p, list);
         } else if (p.x <= center.x && p.y > center.y) {
            southWest.getRelevants(p, list);
         } else {
            southEast.getRelevants(p, list);
         }
      }
      return list;
   }

   public Foothold findBelow(Point p) {
      List<Foothold> relevants = getRelevants(p);
      List<Foothold> xMatches = new LinkedList<>();
      for (Foothold fh : relevants) {
         if (fh.firstPoint().x <= p.x && fh.secondPoint().x >= p.x) {
            xMatches.add(fh);
         }
      }
      Collections.sort(xMatches);
      for (Foothold fh : xMatches) {
         if (!fh.isWall()) {
            if (fh.firstPoint().y != fh.secondPoint().y) {
               int calcY;
               double s1 = Math.abs(fh.secondPoint().y - fh.firstPoint().y);
               double s2 = Math.abs(fh.secondPoint().x - fh.firstPoint().x);
               double s4 = Math.abs(p.x - fh.firstPoint().x);
               double alpha = Math.atan(s2 / s1);
               double beta = Math.atan(s1 / s2);
               double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
               if (fh.secondPoint().y < fh.firstPoint().y) {
                  calcY = fh.firstPoint().y - (int) s5;
               } else {
                  calcY = fh.firstPoint().y + (int) s5;
               }
               if (calcY >= p.y) {
                  return fh;
               }
            } else {
               if (fh.firstPoint().y >= p.y) {
                  return fh;
               }
            }
         }
      }
      return null;
   }

   public int x1() {
      return p1.x;
   }

   public int x2() {
      return p2.x;
   }

   public int y1() {
      return p1.y;
   }

   public int y2() {
      return p2.y;
   }
}
