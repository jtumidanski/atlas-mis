package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.MonsterAttributes;

import builder.AttributeResultBuilder;

public class MonsterAttributesBuilder extends RecordBuilder<MonsterAttributes, MonsterAttributesBuilder>
      implements AttributeResultBuilder {
   private Integer monsterId;

   private Integer mobTime;

   private Integer team;

   private Integer cy;

   private Integer f;

   private Integer fh;

   private Integer rx0;

   private Integer rx1;

   private Integer x;

   private Integer y;

   private Boolean hide;

   @Override
   public MonsterAttributes construct() {
      return new MonsterAttributes(monsterId, mobTime, team, cy, f, fh, rx0, rx1, x, y, hide);
   }

   @Override
   public MonsterAttributesBuilder getThis() {
      return this;
   }

   public MonsterAttributesBuilder setMonsterId(Integer monsterId) {
      this.monsterId = monsterId;
      return getThis();
   }

   public MonsterAttributesBuilder setMobTime(Integer mobTime) {
      this.mobTime = mobTime;
      return getThis();
   }

   public MonsterAttributesBuilder setTeam(Integer team) {
      this.team = team;
      return getThis();
   }

   public MonsterAttributesBuilder setCy(Integer cy) {
      this.cy = cy;
      return getThis();
   }

   public MonsterAttributesBuilder setF(Integer f) {
      this.f = f;
      return getThis();
   }

   public MonsterAttributesBuilder setFh(Integer fh) {
      this.fh = fh;
      return getThis();
   }

   public MonsterAttributesBuilder setRx0(Integer rx0) {
      this.rx0 = rx0;
      return getThis();
   }

   public MonsterAttributesBuilder setRx1(Integer rx1) {
      this.rx1 = rx1;
      return getThis();
   }

   public MonsterAttributesBuilder setX(Integer x) {
      this.x = x;
      return getThis();
   }

   public MonsterAttributesBuilder setY(Integer y) {
      this.y = y;
      return getThis();
   }

   public MonsterAttributesBuilder setHide(Boolean hide) {
      this.hide = hide;
      return getThis();
   }
}
