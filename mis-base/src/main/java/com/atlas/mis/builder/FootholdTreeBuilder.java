package com.atlas.mis.builder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.model.Foothold;
import com.atlas.mis.model.FootholdTree;

public class FootholdTreeBuilder extends RecordBuilder<FootholdTree, FootholdTreeBuilder> {
   private static final int MAX_DEPTH = 8;

   private FootholdTree northWest;

   private FootholdTree northEast;

   private FootholdTree southWest;

   private FootholdTree southEast;

   private List<Foothold> footholds;

   private Point p1;

   private Point p2;

   private Point center;

   private int depth;

   private int maxDropX;

   private int minDropX;

   public FootholdTreeBuilder(Point p1, Point p2) {
      this.p1 = p1;
      this.p2 = p2;
      this.center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
      this.footholds = new ArrayList<>();
   }

   public FootholdTreeBuilder(Point p1, Point p2, int depth) {
      this.p1 = p1;
      this.p2 = p2;
      this.depth = depth;
      center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
   }

   public FootholdTreeBuilder(FootholdTree other) {
      this.northWest = other.northWest();
      this.northEast = other.northEast();
      this.southWest = other.southWest();
      this.southEast = other.southEast();
      this.footholds = other.footholds();
      this.p1 = other.p1();
      this.p2 = other.p2();
      this.center = other.center();
      this.depth = other.depth();
      this.maxDropX = other.maxDropX();
      this.minDropX = other.minDropX();
   }

   @Override
   public FootholdTreeBuilder getThis() {
      return this;
   }

   @Override
   public FootholdTree construct() {
      return new FootholdTree(northWest, northEast, southWest, southEast, footholds, p1, p2, center, depth, maxDropX, minDropX);
   }

   public FootholdTreeBuilder setNorthWest(FootholdTree northWest) {
      this.northWest = northWest;
      return getThis();
   }

   public FootholdTreeBuilder setNorthEast(FootholdTree northEast) {
      this.northEast = northEast;
      return getThis();
   }

   public FootholdTreeBuilder setSouthWest(FootholdTree southWest) {
      this.southWest = southWest;
      return getThis();
   }

   public FootholdTreeBuilder setSouthEast(FootholdTree southEast) {
      this.southEast = southEast;
      return getThis();
   }

   public FootholdTreeBuilder setFootholds(List<Foothold> footholds) {
      this.footholds = footholds;
      return getThis();
   }

   public FootholdTreeBuilder setP1(Point p1) {
      this.p1 = p1;
      return getThis();
   }

   public FootholdTreeBuilder setP2(Point p2) {
      this.p2 = p2;
      return getThis();
   }

   public FootholdTreeBuilder setCenter(Point center) {
      this.center = center;
      return getThis();
   }

   public FootholdTreeBuilder setDepth(int depth) {
      this.depth = depth;
      return getThis();
   }

   public FootholdTreeBuilder setMaxDropX(int maxDropX) {
      this.maxDropX = maxDropX;
      return getThis();
   }

   public FootholdTreeBuilder setMinDropX(int minDropX) {
      this.minDropX = minDropX;
      return getThis();
   }

   public FootholdTreeBuilder insert(Foothold foothold) {
      if (depth == 0) {
         if (foothold.firstPoint().x > maxDropX) {
            maxDropX = foothold.firstPoint().x;
         }
         if (foothold.firstPoint().x < minDropX) {
            minDropX = foothold.firstPoint().x;
         }
         if (foothold.secondPoint().x > maxDropX) {
            maxDropX = foothold.secondPoint().x;
         }
         if (foothold.secondPoint().x < minDropX) {
            minDropX = foothold.secondPoint().x;
         }
      }
      if (depth == MAX_DEPTH ||
            (foothold.firstPoint().x >= p1.x && foothold.secondPoint().x <= p2.x &&
                  foothold.firstPoint().y >= p1.y && foothold.secondPoint().y <= p2.y)) {
         footholds.add(foothold);
      } else {
         if (northWest == null) {
            northWest = new FootholdTreeBuilder(p1, center, depth + 1).build();
            northEast = new FootholdTreeBuilder(new Point(center.x, p1.y), new Point(p2.x, center.y), depth + 1).build();
            southWest = new FootholdTreeBuilder(new Point(p1.x, center.y), new Point(center.x, p2.y), depth + 1).build();
            southEast = new FootholdTreeBuilder(center, p2, depth + 1).build();
         }
         if (foothold.secondPoint().x <= center.x && foothold.secondPoint().y <= center.y) {
            northWest = new FootholdTreeBuilder(northWest).insert(foothold).build();
         } else if (foothold.firstPoint().x > center.x && foothold.secondPoint().y <= center.y) {
            northEast = new FootholdTreeBuilder(northEast).insert(foothold).build();
         } else if (foothold.secondPoint().x <= center.x && foothold.firstPoint().y > center.y) {
            southWest = new FootholdTreeBuilder(southWest).insert(foothold).build();
         } else {
            southEast = new FootholdTreeBuilder(southEast).insert(foothold).build();
         }
      }
      return getThis();
   }
}
