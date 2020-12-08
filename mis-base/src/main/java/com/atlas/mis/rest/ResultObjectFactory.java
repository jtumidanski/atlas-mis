package com.atlas.mis.rest;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import com.atlas.mis.attribute.BackgroundTypeAttributes;
import com.atlas.mis.attribute.MapAttributes;
import com.atlas.mis.attribute.NpcAttributes;
import com.atlas.mis.attribute.PortalAttributes;
import com.atlas.mis.attribute.ReactorAttributes;
import com.atlas.mis.attribute.RectangleAttributes;
import com.atlas.mis.builder.MapAttributesBuilder;
import com.atlas.mis.builder.NpcAttributesBuilder;
import com.atlas.mis.builder.PortalAttributesBuilder;
import com.atlas.mis.builder.ReactorAttributesBuilder;
import com.atlas.mis.model.BackgroundType;
import com.atlas.mis.model.MapData;
import com.atlas.mis.model.Npc;
import com.atlas.mis.model.PortalData;
import com.atlas.mis.model.Reactor;

import builder.ResultObjectBuilder;

public final class ResultObjectFactory {
   private ResultObjectFactory() {
   }

   public static ResultObjectBuilder createMap(MapData mapData) {
      return new ResultObjectBuilder(MapAttributes.class, mapData.id())
            .setAttribute(new MapAttributesBuilder()
                  .setName(mapData.name())
                  .setStreetName(mapData.streetName())
                  .setReturnMapId(mapData.returnMapId())
                  .setMonsterRate(mapData.monsterRate())
                  .setOnFirstUserEnter(mapData.onFirstUserEnter())
                  .setOnUserEnter(mapData.onUserEnter())
                  .setFieldLimit(mapData.fieldLimit())
                  .setMobInterval(mapData.mobInterval())
                  .setSeats(mapData.seats())
                  .setClock(mapData.clock())
                  .setEverLast(mapData.everLast())
                  .setTown(mapData.town())
                  .setDecHp(mapData.decHp())
                  .setProtectItem(mapData.protectItem())
                  .setForcedReturnMap(mapData.forcedReturnMap())
                  .setBoat(mapData.boat())
                  .setTimeLimit(mapData.timeLimit())
                  .setFieldType(mapData.fieldType())
                  .setMobCapacity(mapData.mobCapacity())
                  .setRecovery(mapData.recovery())
                  .setMapArea(getRectangleAttributes(mapData.mapArea()))
                  .setAreas(getAreas(mapData.areas()))
                  .setBackgroundTypes(getBackgroundTypeAttributes(mapData.backgroundTypes()))
            );
   }

   private static List<RectangleAttributes> getAreas(List<Rectangle> areas) {
      return areas.stream()
            .map(ResultObjectFactory::getRectangleAttributes)
            .collect(Collectors.toList());
   }

   protected static RectangleAttributes getRectangleAttributes(Rectangle rectangle) {
      return new RectangleAttributes(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
   }

   protected static List<BackgroundTypeAttributes> getBackgroundTypeAttributes(List<BackgroundType> backgroundTypes) {
      return backgroundTypes.stream()
            .map(backgroundType -> new BackgroundTypeAttributes(backgroundType.layerNumber(), backgroundType.type()))
            .collect(Collectors.toList());
   }

   public static ResultObjectBuilder createPortal(PortalData portalData) {
      return new ResultObjectBuilder(PortalAttributes.class, portalData.id())
            .setAttribute(new PortalAttributesBuilder()
                  .setName(portalData.name())
                  .setType(portalData.type())
                  .setTarget(portalData.target())
                  .setTargetMap(portalData.targetMap())
                  .setScriptName(portalData.scriptName())
                  .setX(portalData.position().x)
                  .setY(portalData.position().y)
            );
   }

   public static ResultObjectBuilder createReactor(Reactor reactor) {
      return new ResultObjectBuilder(ReactorAttributes.class, reactor.id())
            .setAttribute(new ReactorAttributesBuilder()
                  .setName(reactor.name())
                  .setX(reactor.x())
                  .setY(reactor.y())
                  .setDelay(reactor.delay())
                  .setFacingDirection(reactor.facingDirection())
            );
   }

   public static ResultObjectBuilder createNpc(Npc npc) {
      return new ResultObjectBuilder(NpcAttributes.class, npc.objectId())
            .setAttribute(new NpcAttributesBuilder()
                  .setId(npc.id())
                  .setName(npc.name())
                  .setCy(npc.cy())
                  .setF(npc.f())
                  .setFh(npc.fh())
                  .setRx0(npc.rx0())
                  .setRx1(npc.rx1())
                  .setX(npc.x())
                  .setY(npc.y())
                  .setHide(npc.hide())
            );
   }
}
