package com.atlas.mis;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.atlas.mis.model.MapData;
import com.atlas.mis.model.XLimit;
import com.atlas.mis.processor.MapProcessor;

public class MapDataRegistry {
   private static final Object lock = new Object();

   private static volatile MapDataRegistry instance;

   protected final Map<Integer, MapData> mapData;

   protected final Map<Integer, XLimit> dropBoundsCache = new HashMap<>(100);

   public static MapDataRegistry getInstance() {
      MapDataRegistry result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new MapDataRegistry();
               instance = result;
            }
         }
      }
      return result;
   }

   private MapDataRegistry() {
      mapData = new ConcurrentHashMap<>();
   }

   public Optional<MapData> getMapData(int mapId) {
      if (!mapData.containsKey(mapId)) {
         MapProcessor.produceMapData(mapId)
               .ifPresent(map -> {
                  mapData.put(mapId, map);
                  dropBoundsCache.put(mapId, map.xLimit());
               });
      }
      return Optional.ofNullable(mapData.getOrDefault(mapId, null));
   }
}
