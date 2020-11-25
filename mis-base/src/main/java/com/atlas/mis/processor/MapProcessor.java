package com.atlas.mis.processor;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.atlas.mis.builder.FootholdTreeBuilder;
import com.atlas.mis.builder.MapBuilder;
import com.atlas.mis.model.BackgroundType;
import com.atlas.mis.model.Foothold;
import com.atlas.mis.model.FootholdTree;
import com.atlas.mis.model.MapData;
import com.atlas.mis.model.PortalData;
import com.atlas.mis.model.Reactor;
import com.atlas.mis.model.TimeMob;
import com.atlas.mis.model.XLimit;
import com.atlas.mis.util.StringUtil;
import com.atlas.shared.wz.MapleData;
import com.atlas.shared.wz.MapleDataProvider;
import com.atlas.shared.wz.MapleDataProviderFactory;
import com.atlas.shared.wz.MapleDataTool;

public class MapProcessor {
   private static final Object lock = new Object();

   private static volatile MapProcessor instance;

   protected final MapleData nameData;

   protected final MapleDataProvider mapSource;

   protected final Map<Integer, MapData> mapData;

   protected static final Map<Integer, XLimit> dropBoundsCache = new HashMap<>(100);

   public static MapProcessor getInstance() {
      MapProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new MapProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   private MapProcessor() {
      mapData = new ConcurrentHashMap<>();
      nameData = MapleDataProviderFactory.getDataProvider(new File("/service/wz/String.wz")).getData("Map"
            + ".img");
      mapSource = MapleDataProviderFactory.getDataProvider(new File("/service/wz/Map.wz"));
   }

   public Optional<MapData> getMapData(int mapId) {
      if (!mapData.containsKey(mapId)) {
         produceMapData(mapId).ifPresent(map -> {
            mapData.put(mapId, map);
            dropBoundsCache.put(mapId, map.xLimit());
         });
      }
      return Optional.ofNullable(mapData.getOrDefault(mapId, null));
   }

   protected String getMapName(int mapId) {
      String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapId), '0', 9);
      int area = mapId / 100000000;
      mapName = "Map/Map" + area + "/" + mapName + ".img";
      return mapName;
   }

   protected Optional<MapData> produceMapData(int mapId) {
      String mapName = getMapName(mapId);
      MapleData mapData = mapSource.getData(mapName);
      if (mapData == null) {
         return Optional.empty();
      }
      MapleData infoData = mapData.getChildByPath("info");

      String link = MapleDataTool.getString(infoData.getChildByPath("link"), "");
      if (!link.equals("")) { //nexon made hundreds of dojo maps so to reduce the size they added links.
         mapName = getMapName(Integer.parseInt(link));
         mapData = mapSource.getData(mapName);
      }

      //map.setEventInstance(event);

      MapBuilder mapBuilder = new MapBuilder(mapId)
            .setReturnMapId(MapleDataTool.getInt("returnMap", infoData))
            .setMonsterRate(getMonsterRate(infoData))
            .setOnFirstUserEnter(getOnFirstUserEnter(mapId, infoData))
            .setOnUserEnter(getOnUserEnter(mapId, infoData))
            .setFieldLimit(MapleDataTool.getInt(infoData.getChildByPath("fieldLimit"), 0))
            .setMobInterval((short) MapleDataTool.getInt(infoData.getChildByPath("createMobInterval"), 5000))
            .setPortals(getPortals(mapData))
            .setTimeMob(getTimeMob(infoData))
            .setMapArea(getMapArea(mapData, infoData))
            .setFootholdTree(getFootholdTree(mapData))
            .setAreas(getAreas(mapData))
            .setSeats(getSeats(mapData))
            .setName(loadPlaceName(mapId))
            .setStreetName(loadStreetName(mapId))
            .setClock(mapData.getChildByPath("clock") != null)
            .setEverLast(MapleDataTool.getIntConvert("everlast", infoData, 0) != 0)
            .setTown(MapleDataTool.getIntConvert("town", infoData, 0) != 0)
            .setDecHP(MapleDataTool.getIntConvert("decHP", infoData, 0))
            .setProtectItem(MapleDataTool.getIntConvert("protectItem", infoData, 0))
            .setForcedReturnMap(MapleDataTool.getInt(infoData.getChildByPath("forcedReturn"), 999999999))
            .setBoat(mapData.getChildByPath("shipObj") != null)
            .setTimeLimit(MapleDataTool.getIntConvert("timeLimit", infoData, -1))
            .setFieldType(MapleDataTool.getIntConvert("fieldType", infoData, 0))
            .setMobCapacity(MapleDataTool.getIntConvert("fixedMobCapacity", infoData, 500))
            .setRecovery(getRecovery(infoData))
            .setBackgroundTypes(getBackgroundTypes(mapData))
            .setReactors(getReactors(mapData));

      //TODO player npcs
      //      if (event == null) {
      //         DatabaseConnection.getInstance().withConnection(
      //               connection -> PlayerNpcProvider.getInstance().getForMapAndWorld(connection, mapId, world)
      //                     .forEach(map::addPlayerNPCMapObject));
      //         List<MaplePlayerNPC> developerNpcList = MaplePlayerNPCFactory.getDeveloperNpcListFromMapId(mapId);
      //         if (developerNpcList != null) {
      //            for (MaplePlayerNPC developerNpc : developerNpcList) {
      //               map.addPlayerNPCMapObject(developerNpc);
      //            }
      //         }
      //      }

      //      loadLifeFromWz(map, mapData);
      //      loadLifeFromDb(map);

      //TODO CPQ support
      //      if (map.isCPQMap()) {
      //         MapleData mcData = mapData.getChildByPath("monsterCarnival");
      //         if (mcData != null) {
      //            map.setDeathCP(MapleDataTool.getIntConvert("deathCP", mcData, 0));
      //            map.setMaxMobs(MapleDataTool.getIntConvert("mobGenMax", mcData, 20));
      //            map.setTimeDefault(MapleDataTool.getIntConvert("timeDefault", mcData, 0));
      //            map.setTimeExpand(MapleDataTool.getIntConvert("timeExpand", mcData, 0));
      //            map.setMaxReactors(MapleDataTool.getIntConvert("guardianGenMax", mcData, 16));
      //            MapleData guardianGenData = mcData.getChildByPath("guardianGenPos");
      //            for (MapleData node : guardianGenData.getChildren()) {
      //               GuardianSpawnPoint pt = new GuardianSpawnPoint(
      //                     new Point(MapleDataTool.getIntConvert("x", node), MapleDataTool.getIntConvert("y", node)));
      //               pt.setTeam(MapleDataTool.getIntConvert("team", node, -1));
      //               pt.setTaken(false);
      //               map.addGuardianSpawnPoint(pt);
      //            }
      //            if (mcData.getChildByPath("skill") != null) {
      //               for (MapleData area : mcData.getChildByPath("skill")) {
      //                  map.addSkillId(MapleDataTool.getInt(area));
      //               }
      //            }
      //
      //            if (mcData.getChildByPath("mob") != null) {
      //               for (MapleData area : mcData.getChildByPath("mob")) {
      //                  map.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")),
      //                        MapleDataTool.getInt(area.getChildByPath("spendCP")));
      //               }
      //            }
      //         }
      //      }

      return Optional.of(mapBuilder.build());
   }

   protected List<Reactor> getReactors(MapleData mapData) {
      if (mapData.getChildByPath("reactor") != null) {
         List<Reactor> reactors = new ArrayList<>();
         for (MapleData reactorData : mapData.getChildByPath("reactor")) {
            String id = MapleDataTool.getString(reactorData.getChildByPath("id"));
            if (id != null) {
               int x = MapleDataTool.getInt(reactorData.getChildByPath("x"));
               int y = MapleDataTool.getInt(reactorData.getChildByPath("y"));
               int reactorTime = MapleDataTool.getInt(reactorData.getChildByPath("reactorTime"));
               String name = MapleDataTool.getString(reactorData.getChildByPath("name"), "");
               byte facingDirection = (byte) MapleDataTool.getInt(reactorData.getChildByPath("f"));
               reactors.add(new Reactor(id, name, x, y, reactorTime * 1000, facingDirection));
            }
         }
         return reactors;
      } else {
         return Collections.emptyList();
      }
   }

   protected List<BackgroundType> getBackgroundTypes(MapleData mapData) {
      List<BackgroundType> backgroundTypes = new ArrayList<>();
      for (MapleData layer : mapData.getChildByPath("back")) {
         int layerNum = Integer.parseInt(layer.getName());
         int btype = MapleDataTool.getInt(layer.getChildByPath("type"), 0);
         backgroundTypes.add(new BackgroundType(layerNum, btype));
      }
      return backgroundTypes;
   }

   protected float getRecovery(MapleData infoData) {
      MapleData recData = infoData.getChildByPath("recovery");
      if (recData != null) {
         return MapleDataTool.getFloat(recData);
      }
      return 1.0f;
   }

   protected String loadStreetName(int mapId) {
      return MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(mapId)), "");
   }

   protected String loadPlaceName(int mapId) {
      return MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(mapId)), "");
   }

   protected String getMapStringName(int mapId) {
      StringBuilder builder = new StringBuilder();
      if (mapId < 100000000) {
         builder.append("maple");
      } else if (mapId < 200000000) {
         builder.append("victoria");
      } else if (mapId < 300000000) {
         builder.append("ossyria");
      } else if (mapId < 400000000) {
         builder.append("elin");
      } else if (mapId >= 540000000 && mapId < 560000000) {
         builder.append("singapore");
      } else if (mapId >= 600000000 && mapId < 620000000) {
         builder.append("MasteriaGL");
      } else if (mapId >= 677000000 && mapId < 677100000) {
         builder.append("Episode1GL");
      } else if (mapId >= 670000000 && mapId < 682000000) {
         if ((mapId >= 674030000 && mapId < 674040000) || (mapId >= 680100000 && mapId < 680200000)) {
            builder.append("etc");
         } else {
            builder.append("weddingGL");
         }
      } else if (mapId >= 682000000 && mapId < 683000000) {
         builder.append("HalloweenGL");
      } else if (mapId >= 683000000 && mapId < 684000000) {
         builder.append("event");
      } else if (mapId >= 800000000 && mapId < 900000000) {
         if ((mapId >= 889100000 && mapId < 889200000)) {
            builder.append("etc");
         } else {
            builder.append("jp");
         }
      } else {
         builder.append("etc");
      }
      builder.append("/").append(mapId);
      return builder.toString();
   }

   protected int getSeats(MapleData mapData) {
      if (mapData.getChildByPath("seat") != null) {
         return mapData.getChildByPath("seat").getChildren().size();
      }
      return 0;
   }

   protected List<Rectangle> getAreas(MapleData mapData) {
      if (mapData.getChildByPath("area") != null) {
         List<Rectangle> areas = new ArrayList<>();
         for (MapleData area : mapData.getChildByPath("area")) {
            int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
            int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
            int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
            int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
            areas.add(new Rectangle(x1, y1, (x2 - x1), (y2 - y1)));
         }
         return areas;
      } else {
         return Collections.emptyList();
      }
   }

   protected FootholdTree getFootholdTree(MapleData mapData) {
      List<Foothold> allFootholds = new LinkedList<>();
      Point lBound = new Point();
      Point uBound = new Point();
      for (MapleData footRoot : mapData.getChildByPath("foothold")) {
         for (MapleData footCat : footRoot) {
            for (MapleData footHold : footCat) {
               int x1 = MapleDataTool.getInt(footHold.getChildByPath("x1"));
               int y1 = MapleDataTool.getInt(footHold.getChildByPath("y1"));
               int x2 = MapleDataTool.getInt(footHold.getChildByPath("x2"));
               int y2 = MapleDataTool.getInt(footHold.getChildByPath("y2"));
               Foothold fh = new Foothold(Integer.parseInt(footHold.getName()), new Point(x1, y1), new Point(x2, y2));
               if (fh.firstPoint().x < lBound.x) {
                  lBound.x = fh.firstPoint().x;
               }
               if (fh.secondPoint().x > uBound.x) {
                  uBound.x = fh.secondPoint().x;
               }
               if (fh.firstPoint().y < lBound.y) {
                  lBound.y = fh.firstPoint().y;
               }
               if (fh.secondPoint().y > uBound.y) {
                  uBound.y = fh.secondPoint().y;
               }
               allFootholds.add(fh);
            }
         }
      }

      return allFootholds.stream()
            .reduce(new FootholdTreeBuilder(lBound, uBound), FootholdTreeBuilder::insert, (a, b) -> a)
            .build();
   }

   protected Rectangle getMapArea(MapleData mapData, MapleData infoData) {
      Rectangle mapArea = new Rectangle();
      int[] bounds = new int[4];
      bounds[0] = MapleDataTool.getInt(infoData.getChildByPath("VRTop"));
      bounds[1] = MapleDataTool.getInt(infoData.getChildByPath("VRBottom"));

      if (bounds[0] == bounds[1]) {    // old-style baked map
         MapleData miniMap = mapData.getChildByPath("miniMap");
         if (miniMap != null) {
            bounds[0] = MapleDataTool.getInt(miniMap.getChildByPath("centerX")) * -1;
            bounds[1] = MapleDataTool.getInt(miniMap.getChildByPath("centerY")) * -1;
            bounds[2] = MapleDataTool.getInt(miniMap.getChildByPath("height"));
            bounds[3] = MapleDataTool.getInt(miniMap.getChildByPath("width"));
            mapArea.setBounds(bounds[0], bounds[1], bounds[3], bounds[2]);
         } else {
            int dist = (1 << 18);
            mapArea.setBounds(-dist / 2, -dist / 2, dist, dist);
         }
      } else {
         bounds[2] = MapleDataTool.getInt(infoData.getChildByPath("VRLeft"));
         bounds[3] = MapleDataTool.getInt(infoData.getChildByPath("VRRight"));
         mapArea.setBounds(bounds[2], bounds[0], bounds[3] - bounds[2], bounds[1] - bounds[0]);
      }
      return mapArea;
   }

   protected TimeMob getTimeMob(MapleData infoData) {
      MapleData timeMob = infoData.getChildByPath("timeMob");
      if (timeMob != null) {
         return new TimeMob(MapleDataTool.getInt(timeMob.getChildByPath("id")),
               MapleDataTool.getString(timeMob.getChildByPath("message")));
      }
      return null;
   }

   protected List<PortalData> getPortals(MapleData mapData) {
      List<PortalData> portals = new ArrayList<>();
      for (MapleData portal : mapData.getChildByPath("portal")) {
         portals.add(PortalProcessor.getInstance().createPortal(MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
      }
      return portals;
   }

   protected String getOnUserEnter(int mapId, MapleData infoData) {
      String onEnter = MapleDataTool.getString(infoData.getChildByPath("onUserEnter"), String.valueOf(mapId));
      return onEnter.equals("") ? String.valueOf(mapId) : onEnter;
   }

   protected String getOnFirstUserEnter(int mapId, MapleData infoData) {
      String onFirstEnter = MapleDataTool.getString(infoData.getChildByPath("onFirstUserEnter"), String.valueOf(mapId));
      return onFirstEnter.equals("") ? String.valueOf(mapId) : onFirstEnter;
   }

   protected float getMonsterRate(MapleData infoData) {
      float monsterRate = 0;
      MapleData mobRate = infoData.getChildByPath("mobRate");
      if (mobRate != null) {
         monsterRate = (Float) mobRate.getData();
      }
      return monsterRate;
   }
}
