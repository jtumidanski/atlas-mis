package com.atlas.mis.builder;

import java.util.List;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.BackgroundTypeAttributes;
import com.atlas.mis.attribute.MapAttributes;
import com.atlas.mis.attribute.RectangleAttributes;

import builder.AttributeResultBuilder;

public class MapAttributesBuilder extends RecordBuilder<MapAttributes, MapAttributesBuilder> implements AttributeResultBuilder {
   private Integer id;

   private String name;

   private String streetName;

   private Integer returnMapId;

   private Float monsterRate;

   private String onFirstUserEnter;

   private String onUserEnter;

   private Integer fieldLimit;

   private Short mobInterval;

   private Integer seats;

   private Boolean clock;

   private Boolean everLast;

   private Boolean town;

   private Integer decHp;

   private Integer protectItem;

   private Integer forcedReturnMap;

   private Boolean boat;

   private Integer timeLimit;

   private Integer fieldType;

   private Integer mobCapacity;

   private Float recovery;

   private RectangleAttributes mapArea;

   private List<RectangleAttributes> areas;

   private List<BackgroundTypeAttributes> backgroundTypes;

   @Override
   public MapAttributes construct() {
      return new MapAttributes(name, streetName, returnMapId, monsterRate, onFirstUserEnter, onUserEnter, fieldLimit,
            mobInterval, seats, clock, everLast, town, decHp, protectItem, forcedReturnMap, boat, timeLimit, fieldType, mobCapacity,
            recovery, mapArea, areas, backgroundTypes);
   }

   @Override
   public MapAttributesBuilder getThis() {
      return this;
   }

   public MapAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public MapAttributesBuilder setStreetName(String streetName) {
      this.streetName = streetName;
      return getThis();
   }

   public MapAttributesBuilder setReturnMapId(Integer returnMapId) {
      this.returnMapId = returnMapId;
      return getThis();
   }

   public MapAttributesBuilder setMonsterRate(Float monsterRate) {
      this.monsterRate = monsterRate;
      return getThis();
   }

   public MapAttributesBuilder setOnFirstUserEnter(String onFirstUserEnter) {
      this.onFirstUserEnter = onFirstUserEnter;
      return getThis();
   }

   public MapAttributesBuilder setOnUserEnter(String onUserEnter) {
      this.onUserEnter = onUserEnter;
      return getThis();
   }

   public MapAttributesBuilder setFieldLimit(Integer fieldLimit) {
      this.fieldLimit = fieldLimit;
      return getThis();
   }

   public MapAttributesBuilder setMobInterval(Short mobInterval) {
      this.mobInterval = mobInterval;
      return getThis();
   }

   public MapAttributesBuilder setSeats(Integer seats) {
      this.seats = seats;
      return getThis();
   }

   public MapAttributesBuilder setClock(Boolean clock) {
      this.clock = clock;
      return getThis();
   }

   public MapAttributesBuilder setEverLast(Boolean everLast) {
      this.everLast = everLast;
      return getThis();
   }

   public MapAttributesBuilder setTown(Boolean town) {
      this.town = town;
      return getThis();
   }

   public MapAttributesBuilder setDecHp(Integer decHp) {
      this.decHp = decHp;
      return getThis();
   }

   public MapAttributesBuilder setProtectItem(Integer protectItem) {
      this.protectItem = protectItem;
      return getThis();
   }

   public MapAttributesBuilder setForcedReturnMap(Integer forcedReturnMap) {
      this.forcedReturnMap = forcedReturnMap;
      return getThis();
   }

   public MapAttributesBuilder setBoat(Boolean boat) {
      this.boat = boat;
      return getThis();
   }

   public MapAttributesBuilder setTimeLimit(Integer timeLimit) {
      this.timeLimit = timeLimit;
      return getThis();
   }

   public MapAttributesBuilder setFieldType(Integer fieldType) {
      this.fieldType = fieldType;
      return getThis();
   }

   public MapAttributesBuilder setMobCapacity(Integer mobCapacity) {
      this.mobCapacity = mobCapacity;
      return getThis();
   }

   public MapAttributesBuilder setRecovery(Float recovery) {
      this.recovery = recovery;
      return getThis();
   }

   public MapAttributesBuilder setMapArea(RectangleAttributes mapArea) {
      this.mapArea = mapArea;
      return getThis();
   }

   public MapAttributesBuilder setAreas(List<RectangleAttributes> areas) {
      this.areas = areas;
      return getThis();
   }

   public MapAttributesBuilder setBackgroundTypes(List<BackgroundTypeAttributes> backgroundTypes) {
      this.backgroundTypes = backgroundTypes;
      return getThis();
   }
}
