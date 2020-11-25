package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.PortalAttributes;

import builder.AttributeResultBuilder;

public class PortalAttributesBuilder extends RecordBuilder<PortalAttributes, PortalAttributesBuilder>
      implements AttributeResultBuilder {
   private String name;

   private String target;

   private Integer type;

   private Integer x;

   private Integer y;

   private Integer targetMap;

   private String scriptName;

   @Override
   public PortalAttributes construct() {
      return new PortalAttributes(name, target, type, x, y, targetMap, scriptName);
   }

   @Override
   public PortalAttributesBuilder getThis() {
      return this;
   }

   public PortalAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public PortalAttributesBuilder setTarget(String target) {
      this.target = target;
      return getThis();
   }

   public PortalAttributesBuilder setType(Integer type) {
      this.type = type;
      return getThis();
   }

   public PortalAttributesBuilder setX(Integer x) {
      this.x = x;
      return getThis();
   }

   public PortalAttributesBuilder setY(Integer y) {
      this.y = y;
      return getThis();
   }

   public PortalAttributesBuilder setTargetMap(Integer targetMap) {
      this.targetMap = targetMap;
      return getThis();
   }

   public PortalAttributesBuilder setScriptName(String scriptName) {
      this.scriptName = scriptName;
      return getThis();
   }
}
