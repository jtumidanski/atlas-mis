package com.atlas.mis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.mis.attribute.NpcAttributes;

import builder.AttributeResultBuilder;

public class NpcAttributesBuilder extends RecordBuilder<NpcAttributes, NpcAttributesBuilder> implements AttributeResultBuilder {
   private Integer id;

   private String name;

   private Integer cy;

   private Integer f;

   private Integer fh;

   private Integer rx0;

   private Integer rx1;

   private Integer x;

   private Integer y;

   private Boolean hide;

   @Override
   public NpcAttributes construct() {
      return new NpcAttributes(id, name, cy, f, fh, rx0, rx1, x, y, hide);
   }

   @Override
   public NpcAttributesBuilder getThis() {
      return this;
   }

   public NpcAttributesBuilder setId(Integer id) {
      this.id = id;
      return getThis();
   }

   public NpcAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public NpcAttributesBuilder setCy(Integer cy) {
      this.cy = cy;
      return getThis();
   }

   public NpcAttributesBuilder setF(Integer f) {
      this.f = f;
      return getThis();
   }

   public NpcAttributesBuilder setFh(Integer fh) {
      this.fh = fh;
      return getThis();
   }

   public NpcAttributesBuilder setRx0(Integer rx0) {
      this.rx0 = rx0;
      return getThis();
   }

   public NpcAttributesBuilder setRx1(Integer rx1) {
      this.rx1 = rx1;
      return getThis();
   }

   public NpcAttributesBuilder setX(Integer x) {
      this.x = x;
      return getThis();
   }

   public NpcAttributesBuilder setY(Integer y) {
      this.y = y;
      return getThis();
   }

   public NpcAttributesBuilder setHide(Boolean hide) {
      this.hide = hide;
      return getThis();
   }
}
