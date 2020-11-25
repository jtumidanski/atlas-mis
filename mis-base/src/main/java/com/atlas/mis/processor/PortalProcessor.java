package com.atlas.mis.processor;

import java.util.concurrent.atomic.AtomicInteger;

import com.atlas.mis.builder.PortalBuilder;
import com.atlas.mis.model.PortalData;
import com.atlas.mis.model.PortalType;
import com.atlas.shared.wz.MapleData;
import com.atlas.shared.wz.MapleDataTool;

public class PortalProcessor {
   private static final Object lock = new Object();

   private static volatile PortalProcessor instance;

   private static final Object registryLock = new Object();

   private final AtomicInteger portalId = new AtomicInteger(80);

   public static PortalProcessor getInstance() {
      PortalProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new PortalProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   private PortalProcessor() {
   }

   public PortalData createPortal(int type, MapleData portal) {
      int id;
      String name = MapleDataTool.getString(portal.getChildByPath("pn"));
      if (type == PortalType.DOOR.getType()) {
         synchronized (registryLock) {
            id = portalId.incrementAndGet();
         }
      } else {
         id = Integer.parseInt(portal.getName());
      }

      return new PortalBuilder(id, type)
            .setName(name)
            .setTarget(MapleDataTool.getString(portal.getChildByPath("tn")))
            .setTargetMap(MapleDataTool.getInt(portal.getChildByPath("tm")))
            .setPosition(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y")))
            .setScriptName(getPortalScriptName(portal))
            .build();
   }

   protected String getPortalScriptName(MapleData portal) {
      String script = MapleDataTool.getString("script", portal, null);
      if (script != null && script.equals("")) {
         script = null;
      }
      return script;
   }
}
