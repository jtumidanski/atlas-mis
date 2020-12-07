package com.atlas.mis.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.atlas.mis.DataRegistry;
import com.atlas.mis.MonsterDataRegistry;
import com.atlas.mis.builder.MonsterDataBuilder;
import com.atlas.mis.model.BanishInfo;
import com.atlas.mis.model.CoolDamage;
import com.atlas.mis.model.Element;
import com.atlas.mis.model.ElementalEffectiveness;
import com.atlas.mis.model.LoseItem;
import com.atlas.mis.model.MonsterData;
import com.atlas.mis.model.SelfDestruction;
import com.atlas.mis.model.SkillData;
import com.atlas.mis.util.StringUtil;
import com.atlas.shared.wz.MapleData;
import com.atlas.shared.wz.MapleDataTool;
import com.atlas.shared.wz.MapleDataType;

public final class MonsterProcessor {
   private MonsterProcessor() {
   }

   public static Optional<MonsterData> produceMonsterData(int monsterId) {
      return DataRegistry.getInstance()
            .getMonsterSource()
            .data(StringUtil.getLeftPaddedStr(monsterId + ".img", '0', 11))
            .flatMap(data -> produceMonsterData(monsterId, data));
   }

   protected static Optional<MonsterData> produceMonsterData(int monsterId, MapleData monsterData) {
      return monsterData.childByPath("info")
            .map(info -> produceMonsterData(monsterId, monsterData, info));
   }

   protected static MonsterData produceMonsterData(int monsterId, MapleData monsterData, MapleData info) {
//      int linkMid = MapleDataTool.getIntConvert("link", info)
//            .orElse(0);
      MonsterDataBuilder builder = new MonsterDataBuilder()
            .setHp(MapleDataTool.getIntConvert("maxHP", info).orElse(Integer.MAX_VALUE))
            .setFriendly(MapleDataTool.getIntConvert("damagedByMob", info).orElse(0) == 1)
            .setPaDamage(MapleDataTool.getIntConvert("PADamage", info).orElse(0))
            .setPdDamage(MapleDataTool.getIntConvert("PDDamage", info).orElse(0))
            .setMaDamage(MapleDataTool.getIntConvert("MADamage", info).orElse(0))
            .setMdDamage(MapleDataTool.getIntConvert("MDDamage", info).orElse(0))
            .setMp(MapleDataTool.getIntConvert("maxMP", info).orElse(0))
            .setExp(MapleDataTool.getIntConvert("exp", info).orElse(0))
            .setLevel(MapleDataTool.getIntConvert("level", info).orElse(0))
            .setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", info).orElse(0));

      boolean isBoss = MapleDataTool.getIntConvert("boss", info).orElse(0) > 0;
      builder.setBoss(isBoss)
            .setExplosiveReward(MapleDataTool.getIntConvert("explosiveReward", info).orElse(0) > 0)
            .setFFALoot(MapleDataTool.getIntConvert("publicReward", info).orElse(0) > 0)
            .setUndead(MapleDataTool.getIntConvert("undead", info).orElse(0) > 0)
            .setName(
                  MapleDataTool.getString(monsterId + "/name", DataRegistry.getInstance().getMonsterNameData()).orElse("MISSINGNO"))
            .setBuffToGive(MapleDataTool.getIntConvert("buff", info).orElse(-1))
            .setCp(MapleDataTool.getIntConvert("getCP", info).orElse(0))
            .setRemoveOnMiss(MapleDataTool.getIntConvert("removeOnMiss", info).orElse(0) > 0);

      info.childByPath("coolDamage")
            .map(MonsterProcessor::getCoolDamage)
            .ifPresent(builder::setCool);

      info.childByPath("loseItem")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .map(MonsterProcessor::getLoseItem)
            .forEach(builder::addLoseItem);

      info.childByPath("selfDestruction")
            .map(MonsterProcessor::getSelfDestruction)
            .ifPresent(builder::setSelfDestruction);

      builder.setFirstAttack(isFirstAttack(info));
      builder.setDropPeriod(MapleDataTool.getIntConvert("dropItemPeriod", info).orElse(0) * 10000);

      boolean hpBarBoss = isBoss && MonsterDataRegistry.getInstance().getHpBarBosses().contains(monsterId);
      builder.setTagColor((byte) (hpBarBoss ? MapleDataTool.getIntConvert("hpTagColor", info).orElse(0) : 0));
      builder.setTagBackgroundColor((byte) (hpBarBoss ? MapleDataTool.getIntConvert("hpTagBgcolor", info).orElse(0) : 0));

      monsterData.children().stream()
            .filter(data -> !data.name().equals("info"))
            .forEach(data -> {
                     int delay = data.children().stream()
                           .mapToInt(pic -> MapleDataTool.getInteger("delay", pic).orElse(0))
                           .sum();
                     builder.setAnimationTime(data.name(), delay);
                  }
            );

      List<Integer> revives = info.childByPath("revive")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .map(MapleDataTool::getInteger)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
      builder.setRevives(revives);

      String elemAttr = MapleDataTool.getString("elemAttr", info).orElse("");
      for (int i = 0; i < elemAttr.length(); i += 2) {
         builder.setEffectiveness(Element.getFromChar(elemAttr.charAt(i)),
               ElementalEffectiveness.getByNumber(Integer.parseInt(String.valueOf(elemAttr.charAt(i + 1)))));
      }

      info.childByPath("skill")
            .ifPresent(monsterSkillInfoData -> {
               int i = 0;
               List<SkillData> skills = new ArrayList<>();
               while (monsterSkillInfoData.childByPath(Integer.toString(i)).orElse(null) != null) {
                  int skillId = MapleDataTool.getInteger(i + "/skill", monsterSkillInfoData).orElse(0);
                  int skillLv = MapleDataTool.getInteger(i + "/level", monsterSkillInfoData).orElse(0);
                  skills.add(new SkillData(skillId, skillLv));

//                  monsterData.childByPath("skill" + (i + 1))
//                        .ifPresent(monsterSkillData -> {
//                           int animationTime = monsterSkillData.children().stream()
//                                 .map(data -> MapleDataTool.getIntConvert("delay", data))
//                                 .filter(Optional::isPresent)
//                                 .mapToInt(Optional::get)
//                                 .sum();
//
////                           MobSkill skill = MobSkillFactory.getMobSkill(skillId, skillLv);
////                           mi.setMobSkillAnimationTime(skill, animationTime);
//                        });

                  i++;
               }
               builder.setSkills(skills);
            });

//      int i = 0;
//      MapleData monsterAttackData;
//      while ((monsterAttackData = monsterData.childByPath("attack" + (i + 1)).get()) != null) {
//         int animationTime = 0;
//         for (MapleData effectEntry : monsterAttackData.children()) {
//            animationTime += MapleDataTool.getIntConvert("delay", effectEntry).orElse(0);
//         }
//
//         int mpCon = MapleDataTool.getIntConvert("info/conMP", monsterAttackData).orElse(0);
//         int coolTime = MapleDataTool.getIntConvert("info/attackAfter", monsterAttackData).orElse(0);
//         //attackInfos.add(new MobAttackInfoHolder(i, mpCon, coolTime, animationTime));
//         i++;
//      }

      info.childByPath("ban")
            .ifPresent(banishData ->
                  builder.setBanish(
                        new BanishInfo(
                              MapleDataTool.getString("banMsg", banishData).orElse(""),
                              MapleDataTool.getInteger("banMap/0/field", banishData).orElse(-1),
                              MapleDataTool.getString("banMap/0/portal", banishData).orElse("sp"))));

      int noFlip = MapleDataTool.getInteger("noFlip", info).orElse(0);
      if (noFlip > 0) {
         // fixed left/right
         MapleDataTool.getPoint("stand/0/origin", monsterData)
               .ifPresent(origin -> builder.setFixedStance(origin.getX() < 1 ? 5 : 4));
      }

      return builder.build();
   }

   protected static CoolDamage getCoolDamage(MapleData data) {
      int damage = MapleDataTool.getIntConvert("coolDamage", data).orElse(0);
      int probability = MapleDataTool.getIntConvert("coolDamageProb", data).orElse(0);
      return new CoolDamage(damage, probability);
   }

   protected static LoseItem getLoseItem(MapleData data) {
      int id = data.childByPath("id")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      byte prop = data.childByPath("prop")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0)
            .byteValue();
      byte x = data.childByPath("x")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0)
            .byteValue();
      return new LoseItem(id, prop, x);
   }

   protected static SelfDestruction getSelfDestruction(MapleData data) {
      byte action = data.childByPath("action")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0)
            .byteValue();
      int removeAfter = MapleDataTool.getInteger("removeAfter", data)
            .orElse(-1);
      int hp = MapleDataTool.getInteger("hp", data)
            .orElse(-1);
      return new SelfDestruction(action, removeAfter, hp);
   }

   protected static boolean isFirstAttack(MapleData info) {
      return info.childByPath("firstAttack")
            .map(data -> {
               if (data.type().equals(MapleDataType.FLOAT)) {
                  return Math.round(MapleDataTool.getFloat(data).orElse(0f));
               }
               return MapleDataTool.getInteger(data).orElse(0);
            })
            .orElse(0) > 0;
   }
}
