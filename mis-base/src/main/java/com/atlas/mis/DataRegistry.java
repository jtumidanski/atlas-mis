package com.atlas.mis;

import java.io.File;

import com.atlas.shared.wz.MapleData;
import com.atlas.shared.wz.MapleDataProvider;
import com.atlas.shared.wz.MapleDataProviderFactory;

public class DataRegistry {
   private static final Object lock = new Object();

   private static volatile DataRegistry instance;

   protected final MapleData mapNameData;

   protected final MapleData npcNameData;

   protected final MapleData monsterNameData;

   protected final MapleDataProvider stringDataProvider;

   protected final MapleDataProvider mapSource;

   protected final MapleDataProvider monsterSource;

   protected final MapleDataProvider uiSource;

   public static DataRegistry getInstance() {
      DataRegistry result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new DataRegistry();
               instance = result;
            }
         }
      }
      return result;
   }

   private DataRegistry() {
      stringDataProvider = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/String.wz"));
      mapNameData = stringDataProvider.data("Map.img")
            .orElseThrow();
      npcNameData = stringDataProvider.data("Npc.img")
            .orElseThrow();
      monsterNameData = stringDataProvider.data("Mob.img")
            .orElseThrow();
      mapSource = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/Map.wz"));
      monsterSource = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/Mob.wz"));
      uiSource = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/UI.wz"));
   }

   public MapleDataProvider getMapSource() {
      return mapSource;
   }

   public MapleDataProvider getMonsterSource() {
      return monsterSource;
   }

   public MapleDataProvider getUiSource() {
      return uiSource;
   }

   public MapleData getMapNameData() {
      return mapNameData;
   }

   public MapleData getNpcNameData() {
      return npcNameData;
   }

   public MapleData getMonsterNameData() {
      return monsterNameData;
   }
}
