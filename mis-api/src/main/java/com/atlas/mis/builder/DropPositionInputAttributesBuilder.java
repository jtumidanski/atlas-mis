package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.DropPositionInputAttributes;

import builder.AttributeResultBuilder;

public class DropPositionInputAttributesBuilder
      extends RecordBuilder<DropPositionInputAttributes, DropPositionInputAttributesBuilder> implements AttributeResultBuilder {
   private Integer initialX;

   private Integer initialY;

   private Integer fallbackX;

   private Integer fallbackY;

   @Override
   public DropPositionInputAttributes construct() {
      return new DropPositionInputAttributes(initialX, initialY, fallbackX, fallbackY);
   }

   @Override
   public DropPositionInputAttributesBuilder getThis() {
      return this;
   }

   public DropPositionInputAttributesBuilder setInitialX(Integer initialX) {
      this.initialX = initialX;
      return getThis();
   }

   public DropPositionInputAttributesBuilder setInitialY(Integer initialY) {
      this.initialY = initialY;
      return getThis();
   }

   public DropPositionInputAttributesBuilder setFallbackX(Integer fallbackX) {
      this.fallbackX = fallbackX;
      return getThis();
   }

   public DropPositionInputAttributesBuilder setFallbackY(Integer fallbackY) {
      this.fallbackY = fallbackY;
      return getThis();
   }
}
