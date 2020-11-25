package com.atlas.mis.model;

public enum PortalType {
   TELEPORT(1),
   MAP(2),
   DOOR(6);

   private final int type;

   PortalType(int type) {
      this.type = type;
   }

   public int getType() {
      return type;
   }
}
