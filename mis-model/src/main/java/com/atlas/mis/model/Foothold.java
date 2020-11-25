package com.atlas.mis.model;

import java.awt.*;

public record Foothold(int id, Point firstPoint, Point secondPoint) implements Comparable<Foothold> {
   public Boolean isWall() {
      return firstPoint.getX() == secondPoint.getX();
   }

   @Override
   public int compareTo(Foothold o) {
      if (secondPoint.getY() < o.firstPoint().getY()) {
         return -1;
      } else if (firstPoint.getY() > o.secondPoint().getY()) {
         return 1;
      }
      return 0;
   }
}
