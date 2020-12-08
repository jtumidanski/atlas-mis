package com.atlas.mis.builder;

import java.awt.*;

public abstract class LifeBuilder<T, U extends LifeBuilder<T, U>> {
   protected int id;

   protected int lifeId;

   protected int cy;

   protected int f;

   protected int fh;

   protected int rx0;

   protected int rx1;

   protected Point position;

   protected boolean hide;

   public LifeBuilder(int id, int lifeId) {
      this.id = id;
      this.lifeId = lifeId;
   }

   public U setCy(int cy) {
      this.cy = cy;
      return getThis();
   }

   public U setF(int f) {
      this.f = f;
      return getThis();
   }

   public U setFh(int fh) {
      this.fh = fh;
      return getThis();
   }

   public U setRx0(int rx0) {
      this.rx0 = rx0;
      return getThis();
   }

   public U setRx1(int rx1) {
      this.rx1 = rx1;
      return getThis();
   }

   public U setPosition(Point position) {
      this.position = position;
      return getThis();
   }

   public U setHide(boolean hide) {
      this.hide = hide;
      return getThis();
   }

   protected abstract U getThis();

   public abstract T build();
}
