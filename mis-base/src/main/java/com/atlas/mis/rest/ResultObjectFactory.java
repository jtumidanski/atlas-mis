package com.atlas.mis.rest;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import com.atlas.mis.attribute.BackgroundTypeAttributes;
import com.atlas.mis.attribute.LoseItemAttributes;
import com.atlas.mis.attribute.MapAttributes;
import com.atlas.mis.attribute.MapPointAttributes;
import com.atlas.mis.attribute.MonsterAttributes;
import com.atlas.mis.attribute.MonsterDataAttributes;
import com.atlas.mis.attribute.NpcAttributes;
import com.atlas.mis.attribute.PortalAttributes;
import com.atlas.mis.attribute.ReactorAttributes;
import com.atlas.mis.attribute.RectangleAttributes;
import com.atlas.mis.builder.LoseItemAttributesBuilder;
import com.atlas.mis.builder.MapAttributesBuilder;
import com.atlas.mis.builder.MapPointAttributesBuilder;
import com.atlas.mis.builder.MonsterAttributesBuilder;
import com.atlas.mis.builder.MonsterDataAttributesBuilder;
import com.atlas.mis.builder.NpcAttributesBuilder;
import com.atlas.mis.builder.PortalAttributesBuilder;
import com.atlas.mis.builder.ReactorAttributesBuilder;
import com.atlas.mis.model.BackgroundType;
import com.atlas.mis.model.LoseItem;
import com.atlas.mis.model.MapData;
import com.atlas.mis.model.Monster;
import com.atlas.mis.model.MonsterData;
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

   public static ResultObjectBuilder createMonster(Monster monster) {
      return new ResultObjectBuilder(MonsterAttributes.class, monster.objectId())
            .setAttribute(new MonsterAttributesBuilder()
                  .setMonsterId(monster.id())
                  .setMobTime(monster.mobTime())
                  .setTeam(monster.team())
                  .setCy(monster.cy())
                  .setF(monster.f())
                  .setFh(monster.fh())
                  .setRx0(monster.rx0())
                  .setRx1(monster.rx1())
                  .setX(monster.x())
                  .setY(monster.y())
                  .setHide(monster.hide())
            );
   }

   public static ResultObjectBuilder createMonsterData(int monsterId, MonsterData monsterData) {
      return new ResultObjectBuilder(MonsterDataAttributes.class, monsterId)
            .setAttribute(new MonsterDataAttributesBuilder()
                  .setName(monsterData.name())
                  .setHp(monsterData.hp())
                  .setMp(monsterData.mp())
                  .setExperience(monsterData.exp())
                  .setLevel(monsterData.level())
                  .setPaDamage(monsterData.paDamage())
                  .setPdDamage(monsterData.pdDamage())
                  .setMaDamage(monsterData.maDamage())
                  .setMdDamage(monsterData.mdDamage())
                  .setFriendly(monsterData.isFriendly())
                  .setRemoveAfter(monsterData.removeAfter())
                  .setBoss(monsterData.isBoss())
                  .setExplosiveReward(monsterData.isExplosiveReward())
                  .setFfaLoot(monsterData.isFFALoot())
                  .setUndead(monsterData.isUndead())
                  .setBuffToGive(monsterData.buffToGive())
                  .setCarnivalPoint(monsterData.cp())
                  .setRemoveOnMiss(monsterData.removeOnMiss())
                  .setChangeable(monsterData.changeable())
                  .setTagColor(monsterData.tagColor())
                  .setTagBackgroundColor(monsterData.tagBackgroundColor())
                  .setFixedStance(monsterData.fixedStance())
                  .setFirstAttack(monsterData.firstAttack())
            );
   }

   public static ResultObjectBuilder createPoint(Point point) {
      return new ResultObjectBuilder(MapPointAttributes.class, 0)
            .setAttribute(new MapPointAttributesBuilder()
                  .setX(point.x)
                  .setY(point.y)
            );
   }

   public static ResultObjectBuilder createLoseItem(LoseItem data) {
      return new ResultObjectBuilder(LoseItemAttributes.class, data.id())
            .setAttribute(new LoseItemAttributesBuilder()
                  .setChance(data.chance())
                  .setX(data.x())
            );
   }
}
