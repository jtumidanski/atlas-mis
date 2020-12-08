package com.atlas.mis.builder;

import com.atlas.mis.model.Life;
import com.atlas.mis.model.Monster;

public class MonsterBuilder extends LifeBuilder<Life, MonsterBuilder> {
   private int mobTime;

   private int team;

   public MonsterBuilder(int id, int lifeId) {
      super(id, lifeId);
   }

   public MonsterBuilder setMobTime(int mobTime) {
      this.mobTime = mobTime;
      return getThis();
   }

   public MonsterBuilder setTeam(int team) {
      this.team = team;
      return getThis();
   }

   @Override
   protected MonsterBuilder getThis() {
      return this;
   }

   @Override
   public Life build() {
      return new Monster(id, lifeId, mobTime, team, cy, f, fh, rx0, rx1, position.x, position.y, hide);
   }
}
