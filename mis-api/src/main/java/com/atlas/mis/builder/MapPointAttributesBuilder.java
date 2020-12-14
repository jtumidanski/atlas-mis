package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.MapPointAttributes;

import builder.AttributeResultBuilder;

public class MapPointAttributesBuilder extends RecordBuilder<MapPointAttributes, MapPointAttributesBuilder>
      implements AttributeResultBuilder {
   private Integer x;

   private Integer y;

   @Override
   public MapPointAttributes construct() {
      return new MapPointAttributes(x, y);
   }

   @Override
   public MapPointAttributesBuilder getThis() {
      return this;
   }

   public MapPointAttributesBuilder setX(Integer x) {
      this.x = x;
      return getThis();
   }

   public MapPointAttributesBuilder setY(Integer y) {
      this.y = y;
      return getThis();
   }
}
