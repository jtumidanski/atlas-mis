package com.atlas.mis.builder;

import java.awt.*;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.model.PortalData;

public class PortalBuilder extends RecordBuilder<PortalData, PortalBuilder> {
   private final int id;

   private final int type;

   private String name;

   private String target;

   private Point position;

   private int targetMap;

   private String scriptName;

   public PortalBuilder(int id, int type) {
      this.id = id;
      this.type = type;
   }

   @Override
   public PortalBuilder getThis() {
      return this;
   }

   @Override
   public PortalData construct() {
      return new PortalData(id, name, target, type, position, targetMap, scriptName);
   }

   public PortalBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public PortalBuilder setTarget(String target) {
      this.target = target;
      return getThis();
   }

   public PortalBuilder setPosition(int x, int y) {
      this.position = new Point(x, y);
      return getThis();
   }

   public PortalBuilder setTargetMap(int targetMap) {
      this.targetMap = targetMap;
      return getThis();
   }

   public PortalBuilder setScriptName(String scriptName) {
      this.scriptName = scriptName;
      return getThis();
   }
}
