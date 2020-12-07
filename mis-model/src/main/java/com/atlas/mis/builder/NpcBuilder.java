package com.atlas.mis.builder;

import com.atlas.mis.model.Life;
import com.atlas.mis.model.Npc;

public class NpcBuilder extends LifeBuilder<Life, NpcBuilder> {
   private String name;

   public NpcBuilder(int lifeId) {
      super(lifeId);
   }

   public NpcBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   @Override
   protected NpcBuilder getThis() {
      return this;
   }

   @Override
   public Life build() {
      return new Npc(lifeId, name, cy, f, fh, rx0, rx1, position.x, position.y, hide);
   }
}
