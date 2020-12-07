package com.atlas.mis.builder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.model.BackgroundType;
import com.atlas.mis.model.Foothold;
import com.atlas.mis.model.FootholdTree;
import com.atlas.mis.model.Life;
import com.atlas.mis.model.MapData;
import com.atlas.mis.model.Monster;
import com.atlas.mis.model.Npc;
import com.atlas.mis.model.PortalData;
import com.atlas.mis.model.Reactor;
import com.atlas.mis.model.TimeMob;
import com.atlas.mis.model.XLimit;

public class MapBuilder extends RecordBuilder<MapData, MapBuilder> {
   private final int id;

   private String name;

   private String streetName;

   private int returnMapId;

   private float monsterRate;

   private String onFirstUserEnter;

   private String onUserEnter;

   private int fieldLimit;

   private short mobInterval = 5000;

   private List<PortalData> portals = new ArrayList<>();

   private TimeMob timeMob;

   private Rectangle mapArea;

   private FootholdTree footholdTree;

   private List<Rectangle> areas = new ArrayList<>();

   private int seats;

   private boolean clock;

   private boolean everLast;

   private boolean town;

   private int decHP;

   private int protectItem;

   private int forcedReturnMap = 999999999;

   private boolean boat;

   private int timeLimit;

   private int fieldType;

   private int mobCapacity = -1;

   private float recovery = 1.0f;

   private List<BackgroundType> backgroundTypes;

   private List<Reactor> reactors;

   private List<Life> life;

   public MapBuilder(int id) {
      this.id = id;
   }

   @Override
   public MapBuilder getThis() {
      return this;
   }

   @Override
   public MapData construct() {
      XLimit xLimit = createXLimit();

      List<Npc> npcs = life.stream()
            .filter(l -> l instanceof Npc)
            .map(l -> (Npc) l)
            .collect(Collectors.toList());
      List<Monster> monsters = life.stream()
            .filter(l -> l instanceof Monster)
            .map(l -> (Monster) l)
            .collect(Collectors.toList());

      return new MapData(id, name, streetName, returnMapId, monsterRate, onFirstUserEnter, onUserEnter, fieldLimit, mobInterval,
            portals, timeMob, mapArea, footholdTree, areas, seats, clock, everLast, town, decHP, protectItem, forcedReturnMap,
            boat, timeLimit, fieldType, mobCapacity, recovery, backgroundTypes, xLimit, reactors, npcs, monsters);
   }

   public MapBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public MapBuilder setStreetName(String streetName) {
      this.streetName = streetName;
      return getThis();
   }

   public MapBuilder setReturnMapId(int returnMapId) {
      this.returnMapId = returnMapId;
      return getThis();
   }

   public MapBuilder setMonsterRate(float monsterRate) {
      this.monsterRate = monsterRate;
      return getThis();
   }

   public MapBuilder setOnFirstUserEnter(String onFirstUserEnter) {
      this.onFirstUserEnter = onFirstUserEnter;
      return getThis();
   }

   public MapBuilder setOnUserEnter(String onUserEnter) {
      this.onUserEnter = onUserEnter;
      return getThis();
   }

   public MapBuilder setFieldLimit(int fieldLimit) {
      this.fieldLimit = fieldLimit;
      return getThis();
   }

   public MapBuilder setMobInterval(short mobInterval) {
      this.mobInterval = mobInterval;
      return getThis();
   }

   public MapBuilder setPortals(List<PortalData> portals) {
      this.portals = portals;
      return getThis();
   }

   public MapBuilder setTimeMob(TimeMob timeMob) {
      this.timeMob = timeMob;
      return getThis();
   }

   public MapBuilder setMapArea(Rectangle mapArea) {
      this.mapArea = mapArea;
      return getThis();
   }

   public MapBuilder setFootholdTree(FootholdTree footholdTree) {
      this.footholdTree = footholdTree;
      return getThis();
   }

   public MapBuilder setAreas(List<Rectangle> areas) {
      this.areas = areas;
      return getThis();
   }

   public MapBuilder setSeats(int seats) {
      this.seats = seats;
      return getThis();
   }

   public MapBuilder setClock(boolean clock) {
      this.clock = clock;
      return getThis();
   }

   public MapBuilder setEverLast(boolean everLast) {
      this.everLast = everLast;
      return getThis();
   }

   public MapBuilder setTown(boolean town) {
      this.town = town;
      return getThis();
   }

   public MapBuilder setDecHP(int decHP) {
      this.decHP = decHP;
      return getThis();
   }

   public MapBuilder setProtectItem(int protectItem) {
      this.protectItem = protectItem;
      return getThis();
   }

   public MapBuilder setForcedReturnMap(int forcedReturnMap) {
      this.forcedReturnMap = forcedReturnMap;
      return getThis();
   }

   public MapBuilder setBoat(boolean boat) {
      this.boat = boat;
      return getThis();
   }

   public MapBuilder setTimeLimit(int timeLimit) {
      this.timeLimit = timeLimit;
      return getThis();
   }

   public MapBuilder setFieldType(int fieldType) {
      this.fieldType = fieldType;
      return getThis();
   }

   public MapBuilder setMobCapacity(int mobCapacity) {
      this.mobCapacity = mobCapacity;
      return getThis();
   }

   public MapBuilder setRecovery(float recovery) {
      this.recovery = recovery;
      return getThis();
   }

   public MapBuilder setBackgroundTypes(List<BackgroundType> backgroundTypes) {
      this.backgroundTypes = backgroundTypes;
      return getThis();
   }

   public MapBuilder setReactors(List<Reactor> reactors) {
      this.reactors = reactors;
      return getThis();
   }

   public MapBuilder setLife(List<Life> life) {
      this.life = life;
      return getThis();
   }

   protected XLimit createXLimit() {
      Point lp = new Point(mapArea.x, mapArea.y);
      Point rp = new Point(mapArea.x + mapArea.width, mapArea.y);
      Point fallback = new Point(mapArea.x + (mapArea.width / 2), mapArea.y);

      lp = bSearchDropPos(lp, fallback);  // approximated leftmost fh node position
      rp = bSearchDropPos(rp, fallback);  // approximated rightmost fh node position
      return new XLimit(lp.x + 14, rp.x - 14);
   }

   protected Point bSearchDropPos(Point initial, Point fallback) {
      Point res, dropPos = null;

      int awayX = fallback.x;
      int homeX = initial.x;

      int y = initial.y - 85;

      do {
         int distanceX = awayX - homeX;
         int dx = distanceX / 2;

         int searchX = homeX + dx;
         if ((res = calcPointBelow(new Point(searchX, y))) != null) {
            awayX = searchX;
            dropPos = res;
         } else {
            homeX = searchX;
         }
      } while (Math.abs(homeX - awayX) > 5);

      return (dropPos != null) ? dropPos : fallback;
   }

   protected Point calcPointBelow(Point initial) {
      Foothold fh = footholdTree.findBelow(initial);
      if (fh == null) {
         return null;
      }
      int dropY = fh.firstPoint().y;
      if (!fh.isWall() && fh.firstPoint().y != fh.secondPoint().y) {
         double s1 = Math.abs(fh.secondPoint().y - fh.firstPoint().y);
         double s2 = Math.abs(fh.secondPoint().x - fh.firstPoint().x);
         double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.firstPoint().x) / Math.cos(Math.atan(s1 / s2)));
         if (fh.secondPoint().y < fh.firstPoint().y) {
            dropY = fh.firstPoint().y - (int) s5;
         } else {
            dropY = fh.firstPoint().y + (int) s5;
         }
      }
      return new Point(initial.x, dropY);
   }
}
