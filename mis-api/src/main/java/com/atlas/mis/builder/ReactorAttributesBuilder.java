package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.ReactorAttributes;

import builder.AttributeResultBuilder;

public class ReactorAttributesBuilder extends RecordBuilder<ReactorAttributes, ReactorAttributesBuilder>
      implements AttributeResultBuilder {
   private String name;

   private Integer x;

   private Integer y;

   private Integer delay;

   private Byte facingDirection;

   @Override
   public ReactorAttributes construct() {
      return new ReactorAttributes(name, x, y, delay, facingDirection);
   }

   @Override
   public ReactorAttributesBuilder getThis() {
      return this;
   }

   public ReactorAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public ReactorAttributesBuilder setX(Integer x) {
      this.x = x;
      return getThis();
   }

   public ReactorAttributesBuilder setY(Integer y) {
      this.y = y;
      return getThis();
   }

   public ReactorAttributesBuilder setDelay(Integer delay) {
      this.delay = delay;
      return getThis();
   }

   public ReactorAttributesBuilder setFacingDirection(Byte facingDirection) {
      this.facingDirection = facingDirection;
      return getThis();
   }
}
