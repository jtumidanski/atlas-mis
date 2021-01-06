package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.LoseItemAttributes;

import builder.AttributeResultBuilder;

public class LoseItemAttributesBuilder extends RecordBuilder<LoseItemAttributes, LoseItemAttributesBuilder>
      implements AttributeResultBuilder {
   private Byte chance;

   private Byte x;

   @Override
   public LoseItemAttributes construct() {
      return new LoseItemAttributes(chance, x);
   }

   @Override
   public LoseItemAttributesBuilder getThis() {
      return this;
   }

   public LoseItemAttributesBuilder setChance(Byte chance) {
      this.chance = chance;
      return getThis();
   }

   public LoseItemAttributesBuilder setX(Byte x) {
      this.x = x;
      return getThis();
   }
}
