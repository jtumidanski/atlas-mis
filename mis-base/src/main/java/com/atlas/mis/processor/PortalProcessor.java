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
      if (type == PortalType.DOOR.getType()) {
         synchronized (registryLock) {
            id = portalId.incrementAndGet();
         }
      } else {
         id = Integer.parseInt(portal.name());
      }

      return new PortalBuilder(id, type)
            .setName(portal.childByPath("pn")
                  .flatMap(MapleDataTool::getString)
                  .orElse(""))
            .setTarget(portal.childByPath("tn")
                  .flatMap(MapleDataTool::getString)
                  .orElse(""))
            .setTargetMap(portal.childByPath("tm")
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(0))
            .setPosition(portal.childByPath("x")
                        .flatMap(MapleDataTool::getInteger)
                        .orElse(0),
                  portal.childByPath("y")
                        .flatMap(MapleDataTool::getInteger)
                        .orElse(0))
            .setScriptName(getPortalScriptName(portal))
            .build();
   }

   protected String getPortalScriptName(MapleData portal) {
      return MapleDataTool.getString("script", portal)
            .orElse("");
   }
}
