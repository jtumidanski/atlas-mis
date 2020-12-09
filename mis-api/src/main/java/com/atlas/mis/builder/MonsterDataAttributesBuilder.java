package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.MonsterDataAttributes;

import builder.AttributeResultBuilder;

public class MonsterDataAttributesBuilder extends RecordBuilder<MonsterDataAttributes, MonsterDataAttributesBuilder>
      implements AttributeResultBuilder {
   private String name;

   private Integer hp;

   private Integer mp;

   private Integer experience;

   private Integer level;

   private Integer paDamage;

   private Integer pdDamage;

   private Integer maDamage;

   private Integer mdDamage;

   private Boolean friendly;

   private Integer removeAfter;

   private Boolean boss;

   private Boolean explosiveReward;

   private Boolean ffaLoot;

   private Boolean undead;

   private Integer buffToGive;

   private Integer carnivalPoint;

   private Boolean removeOnMiss;

   private Boolean changeable;

   private Byte tagColor;

   private Byte tagBackgroundColor;

   private Integer fixedStance;

   private Boolean firstAttack;

   private Integer dropPeriod;

   @Override
   public MonsterDataAttributes construct() {
      return new MonsterDataAttributes(name, hp, mp, experience, level, paDamage, pdDamage, maDamage, mdDamage, friendly,
            removeAfter, boss, explosiveReward, ffaLoot, undead, buffToGive, carnivalPoint, removeOnMiss, changeable, tagColor,
            tagBackgroundColor, fixedStance, firstAttack, dropPeriod);
   }

   @Override
   public MonsterDataAttributesBuilder getThis() {
      return this;
   }

   public MonsterDataAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public MonsterDataAttributesBuilder setHp(Integer hp) {
      this.hp = hp;
      return getThis();
   }

   public MonsterDataAttributesBuilder setMp(Integer mp) {
      this.mp = mp;
      return getThis();
   }

   public MonsterDataAttributesBuilder setExperience(Integer experience) {
      this.experience = experience;
      return getThis();
   }

   public MonsterDataAttributesBuilder setLevel(Integer level) {
      this.level = level;
      return getThis();
   }

   public MonsterDataAttributesBuilder setPaDamage(Integer paDamage) {
      this.paDamage = paDamage;
      return getThis();
   }

   public MonsterDataAttributesBuilder setPdDamage(Integer pdDamage) {
      this.pdDamage = pdDamage;
      return getThis();
   }

   public MonsterDataAttributesBuilder setMaDamage(Integer maDamage) {
      this.maDamage = maDamage;
      return getThis();
   }

   public MonsterDataAttributesBuilder setMdDamage(Integer mdDamage) {
      this.mdDamage = mdDamage;
      return getThis();
   }

   public MonsterDataAttributesBuilder setFriendly(Boolean friendly) {
      this.friendly = friendly;
      return getThis();
   }

   public MonsterDataAttributesBuilder setRemoveAfter(Integer removeAfter) {
      this.removeAfter = removeAfter;
      return getThis();
   }

   public MonsterDataAttributesBuilder setBoss(Boolean boss) {
      this.boss = boss;
      return getThis();
   }

   public MonsterDataAttributesBuilder setExplosiveReward(Boolean explosiveReward) {
      this.explosiveReward = explosiveReward;
      return getThis();
   }

   public MonsterDataAttributesBuilder setFfaLoot(Boolean ffaLoot) {
      this.ffaLoot = ffaLoot;
      return getThis();
   }

   public MonsterDataAttributesBuilder setUndead(Boolean undead) {
      this.undead = undead;
      return getThis();
   }

   public MonsterDataAttributesBuilder setBuffToGive(Integer buffToGive) {
      this.buffToGive = buffToGive;
      return getThis();
   }

   public MonsterDataAttributesBuilder setCarnivalPoint(Integer carnivalPoint) {
      this.carnivalPoint = carnivalPoint;
      return getThis();
   }

   public MonsterDataAttributesBuilder setRemoveOnMiss(Boolean removeOnMiss) {
      this.removeOnMiss = removeOnMiss;
      return getThis();
   }

   public MonsterDataAttributesBuilder setChangeable(Boolean changeable) {
      this.changeable = changeable;
      return getThis();
   }

   public MonsterDataAttributesBuilder setTagColor(Byte tagColor) {
      this.tagColor = tagColor;
      return getThis();
   }

   public MonsterDataAttributesBuilder setTagBackgroundColor(Byte tagBackgroundColor) {
      this.tagBackgroundColor = tagBackgroundColor;
      return getThis();
   }

   public MonsterDataAttributesBuilder setFixedStance(Integer fixedStance) {
      this.fixedStance = fixedStance;
      return getThis();
   }

   public MonsterDataAttributesBuilder setFirstAttack(Boolean firstAttack) {
      this.firstAttack = firstAttack;
      return getThis();
   }

   public MonsterDataAttributesBuilder setDropPeriod(Integer dropPeriod) {
      this.dropPeriod = dropPeriod;
      return getThis();
   }
}
