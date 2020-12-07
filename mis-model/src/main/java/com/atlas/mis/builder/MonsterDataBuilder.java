package com.atlas.mis.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlas.mis.model.BanishInfo;
import com.atlas.mis.model.CoolDamage;
import com.atlas.mis.model.Element;
import com.atlas.mis.model.ElementalEffectiveness;
import com.atlas.mis.model.LoseItem;
import com.atlas.mis.model.MonsterData;
import com.atlas.mis.model.SelfDestruction;
import com.atlas.mis.model.SkillData;

public class MonsterDataBuilder {
   private int hp;

   private boolean isFriendly;

   private int paDamage;

   private int pdDamage;

   private int maDamage;

   private int mdDamage;

   private int mp;

   private int exp;

   private int level;

   private int removeAfter;

   private boolean isBoss;

   private boolean isExplosiveReward;

   private boolean isFFALoot;

   private boolean isUndead;

   private String name;

   private int buffToGive;

   private int cp;

   private boolean removeOnMiss;

   private CoolDamage cool;

   private List<LoseItem> loseItemList;

   private SelfDestruction selfDestruction;

   private boolean firstAttack;

   private int dropPeriod;

   private Map<String, Integer> animationTime;

   private byte tagColor;

   private byte tagBackgroundColor;

   private List<Integer> revives;

   private List<SkillData> skills;

   private BanishInfo banish;

   private int fixedStance;

   private Map<Element, ElementalEffectiveness> resistance;

   private boolean changeable;

   public MonsterDataBuilder() {
      loseItemList = new ArrayList<>();
      animationTime = new HashMap<>();
      revives = new ArrayList<>();
      skills = new ArrayList<>();
      resistance = new HashMap<>();
      buffToGive = -1;
   }

   public MonsterDataBuilder(MonsterData other) {
      this.hp = other.hp();
      this.isFriendly = other.isFriendly();
      this.paDamage = other.paDamage();
      this.pdDamage = other.pdDamage();
      this.maDamage = other.maDamage();
      this.mdDamage = other.mdDamage();
      this.mp = other.mp();
      this.exp = other.exp();
      this.level = other.level();
      this.removeAfter = other.removeAfter();
      this.isBoss = other.isBoss();
      this.isExplosiveReward = other.isExplosiveReward();
      this.isFFALoot = other.isFFALoot();
      this.isUndead = other.isUndead();
      this.name = other.name();
      this.buffToGive = other.buffToGive();
      this.cp = other.cp();
      this.removeOnMiss = other.removeOnMiss();
      this.cool = other.cool();
      this.loseItemList = other.loseItemList();
      this.selfDestruction = other.selfDestruction();
      this.firstAttack = other.firstAttack();
      this.dropPeriod = other.dropPeriod();
      this.animationTime = other.animationTimes();
      this.tagColor = other.tagColor();
      this.tagBackgroundColor = other.tagBackgroundColor();
      this.revives = other.revives();
      this.skills = other.skills();
      this.banish = other.banish();
      this.fixedStance = other.fixedStance();
      this.resistance = other.resistances();
      this.changeable = other.changeable();
   }

   public MonsterData build() {
      return new MonsterData(name, hp, mp, exp, level, paDamage, pdDamage, maDamage, mdDamage, isFriendly,
            removeAfter, isBoss, isExplosiveReward, isFFALoot, isUndead, buffToGive, cp, removeOnMiss, changeable,
            animationTime, resistance, loseItemList, skills, revives, tagColor, tagBackgroundColor, fixedStance,
            firstAttack, banish, dropPeriod, selfDestruction, cool);
   }

   public MonsterDataBuilder setHp(Integer hp) {
      this.hp = hp;
      return this;
   }

   public MonsterDataBuilder setFriendly(Boolean friendly) {
      isFriendly = friendly;
      return this;
   }

   public MonsterDataBuilder setPaDamage(Integer paDamage) {
      this.paDamage = paDamage;
      return this;
   }

   public MonsterDataBuilder setPdDamage(Integer pdDamage) {
      this.pdDamage = pdDamage;
      return this;
   }

   public MonsterDataBuilder setMaDamage(Integer maDamage) {
      this.maDamage = maDamage;
      return this;
   }

   public MonsterDataBuilder setMdDamage(Integer mdDamage) {
      this.mdDamage = mdDamage;
      return this;
   }

   public MonsterDataBuilder setMp(Integer mp) {
      this.mp = mp;
      return this;
   }

   public MonsterDataBuilder setExp(Integer exp) {
      this.exp = exp;
      return this;
   }

   public MonsterDataBuilder setLevel(Integer level) {
      this.level = level;
      return this;
   }

   public MonsterDataBuilder setRemoveAfter(Integer removeAfter) {
      this.removeAfter = removeAfter;
      return this;
   }

   public MonsterDataBuilder setBoss(Boolean boss) {
      isBoss = boss;
      return this;
   }

   public MonsterDataBuilder setExplosiveReward(Boolean explosiveReward) {
      isExplosiveReward = explosiveReward;
      return this;
   }

   public MonsterDataBuilder setFFALoot(Boolean FFALoot) {
      isFFALoot = FFALoot;
      return this;
   }

   public MonsterDataBuilder setUndead(Boolean undead) {
      isUndead = undead;
      return this;
   }

   public MonsterDataBuilder setName(String name) {
      this.name = name;
      return this;
   }

   public MonsterDataBuilder setBuffToGive(Integer buffToGive) {
      this.buffToGive = buffToGive;
      return this;
   }

   public MonsterDataBuilder setCp(Integer cp) {
      this.cp = cp;
      return this;
   }

   public MonsterDataBuilder setRemoveOnMiss(Boolean removeOnMiss) {
      this.removeOnMiss = removeOnMiss;
      return this;
   }

   public MonsterDataBuilder setCool(CoolDamage coolDamage) {
      this.cool = coolDamage;
      return this;
   }

   public MonsterDataBuilder addLoseItem(LoseItem loseItem) {
      this.loseItemList.add(loseItem);
      return this;
   }

   public MonsterDataBuilder setSelfDestruction(SelfDestruction selfDestruction) {
      this.selfDestruction = selfDestruction;
      return this;
   }

   public MonsterDataBuilder setFirstAttack(Boolean firstAttack) {
      this.firstAttack = firstAttack;
      return this;
   }

   public MonsterDataBuilder setDropPeriod(Integer dropPeriod) {
      this.dropPeriod = dropPeriod;
      return this;
   }

   public MonsterDataBuilder setAnimationTime(String name, Integer delay) {
      this.animationTime.put(name, delay);
      return this;
   }

   public MonsterDataBuilder setTagColor(Byte tagColor) {
      this.tagColor = tagColor;
      return this;
   }

   public MonsterDataBuilder setTagBackgroundColor(Byte tagBackgroundColor) {
      this.tagBackgroundColor = tagBackgroundColor;
      return this;
   }

   public MonsterDataBuilder setRevives(List<Integer> revives) {
      this.revives = revives;
      return this;
   }

   public MonsterDataBuilder setSkills(List<SkillData> skills) {
      this.skills = skills;
      return this;
   }

   public MonsterDataBuilder setBanish(BanishInfo banish) {
      this.banish = banish;
      return this;
   }

   public MonsterDataBuilder setFixedStance(Integer fixedStance) {
      this.fixedStance = fixedStance;
      return this;
   }

   public MonsterDataBuilder setEffectiveness(Element element, ElementalEffectiveness effectiveness) {
      this.resistance.put(element, effectiveness);
      return this;
   }

   public MonsterDataBuilder removeEffectiveness(Element element) {
      this.resistance.remove(element);
      return this;
   }
}
