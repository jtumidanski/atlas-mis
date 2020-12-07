package com.atlas.mis.processor;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
      nameData = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/String.wz"))
            .data("Map.img")
            .orElseThrow();
      mapSource = MapleDataProviderFactory
            .getDataProvider(new File("/service/wz/Map.wz"));
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
      return mapSource.data(mapName)
            .map(mapData -> produceMapData(mapId, mapData));
   }

   protected MapData produceMapData(int mapId, MapleData mapData) {
      MapleData infoData = mapData.childByPath("info")
            .orElseThrow();
      String link = infoData.childByPath("link")
            .flatMap(MapleDataTool::getString)
            .orElse("");

      if (!link.equals("")) {
         //nexon made hundreds of dojo maps so to reduce the size they added links.
         String mapName = getMapName(Integer.parseInt(link));
         return mapSource.data(mapName)
               .map(newMapData -> produceMapData(mapId, newMapData, infoData))
               .orElse(produceMapData(mapId, mapData, infoData));
      } else {
         return produceMapData(mapId, mapData, infoData);
      }
   }

   protected MapData produceMapData(int mapId, MapleData mapData, MapleData infoData) {
      //map.setEventInstance(event);

      MapBuilder mapBuilder = new MapBuilder(mapId)
            .setReturnMapId(MapleDataTool.getInteger("returnMap", infoData).orElse(0))
            .setMonsterRate(getMonsterRate(infoData))
            .setOnFirstUserEnter(getOnFirstUserEnter(mapId, infoData))
            .setOnUserEnter(getOnUserEnter(mapId, infoData))
            .setFieldLimit(infoData
                  .childByPath("fieldLimit")
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(0))
            .setMobInterval(infoData
                  .childByPath("createMobInterval")
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(5000)
                  .shortValue())
            .setPortals(getPortals(mapData))
            .setTimeMob(getTimeMob(infoData).orElse(null))
            .setMapArea(getMapArea(mapData, infoData))
            .setFootholdTree(getFootholdTree(mapData))
            .setAreas(getAreas(mapData))
            .setSeats(getSeats(mapData))
            .setName(loadPlaceName(mapId))
            .setStreetName(loadStreetName(mapId))
            .setClock(mapData
                  .childByPath("clock")
                  .isPresent())
            .setEverLast(MapleDataTool.getIntConvert("everlast", infoData).isPresent())
            .setTown(MapleDataTool.getIntConvert("town", infoData).isPresent())
            .setDecHP(MapleDataTool.getIntConvert("decHP", infoData).orElse(0))
            .setProtectItem(MapleDataTool.getIntConvert("protectItem", infoData).orElse(0))
            .setForcedReturnMap(infoData
                  .childByPath("forcedReturn")
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(999999999))
            .setBoat(mapData
                  .childByPath("shipObj")
                  .isPresent())
            .setTimeLimit(MapleDataTool.getIntConvert("timeLimit", infoData).orElse(-1))
            .setFieldType(MapleDataTool.getIntConvert("fieldType", infoData).orElse(0))
            .setMobCapacity(MapleDataTool.getIntConvert("fixedMobCapacity", infoData).orElse(500))
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

      return mapBuilder.build();
   }

   protected List<Reactor> getReactors(MapleData mapData) {
      return mapData.childByPath("reactor")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::getReactor)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
   }

   protected Optional<Reactor> getReactor(MapleData reactorData) {
      return reactorData.childByPath("id")
            .flatMap(MapleDataTool::getString)
            .map(id -> getReactor(id, reactorData));
   }

   protected Reactor getReactor(String id, MapleData data) {
      int x = data.childByPath("x")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      int y = data.childByPath("y")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      int reactorTime = data.childByPath("reactorTime")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      String name = data.childByPath("name")
            .flatMap(MapleDataTool::getString)
            .orElse("");
      byte facingDirection = data.childByPath("f")
            .flatMap(MapleDataTool::getInteger)
            .map(Integer::byteValue)
            .orElse((byte) 0);
      return new Reactor(id, name, x, y, reactorTime * 1000, facingDirection);
   }

   protected List<BackgroundType> getBackgroundTypes(MapleData mapData) {
      return mapData.childByPath("back")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::getBackgroundType)
            .collect(Collectors.toList());
   }

   protected BackgroundType getBackgroundType(MapleData layer) {
      int layerNum = Integer.parseInt(layer.name());
      int type = layer.childByPath("type")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      return new BackgroundType(layerNum, type);
   }

   protected float getRecovery(MapleData infoData) {
      return infoData.childByPath("recovery")
            .flatMap(MapleDataTool::getFloat)
            .orElse(1.0f);
   }

   protected String loadStreetName(int mapId) {
      return nameData.childByPath(getMapStringName(mapId))
            .flatMap(data -> MapleDataTool.getString("streetName", data))
            .orElse("");
   }

   protected String loadPlaceName(int mapId) {
      return nameData.childByPath(getMapStringName(mapId))
            .flatMap(data -> MapleDataTool.getString("mapName", data))
            .orElse("");
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

   protected int getSeats(MapleData data) {
      return data.childByPath("seat")
            .map(MapleData::children)
            .map(List::size)
            .orElse(0);
   }

   protected List<Rectangle> getAreas(MapleData data) {
      return data.childByPath("area")
            .map(MapleData::children)
            .orElse(Collections.emptyList()).stream()
            .map(this::getRectangle)
            .collect(Collectors.toList());
   }

   private Rectangle getRectangle(MapleData data) {
      int x1 = getX1(data);
      int y1 = getY1(data);
      int x2 = getX2(data);
      int y2 = getY2(data);
      return new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
   }

   private Integer getY2(MapleData data) {
      return data.childByPath("y2")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
   }

   private Integer getX2(MapleData data) {
      return data.childByPath("x2")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
   }

   private Integer getY1(MapleData data) {
      return data.childByPath("y1")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
   }

   private Integer getX1(MapleData data) {
      return data.childByPath("x1")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
   }

   protected FootholdTree getFootholdTree(MapleData mapData) {

      List<Foothold> allFootholds = new LinkedList<>();
      Point lBound = new Point();
      Point uBound = new Point();

      mapData.childByPath("foothold")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .flatMap(footRoot -> footRoot.children().stream())
            .flatMap(footCat -> footCat.children().stream())
            .forEach(footHold -> {
               int x1 = getX1(footHold);
               int y1 = getY1(footHold);
               int x2 = getX2(footHold);
               int y2 = getY2(footHold);
               Foothold fh = new Foothold(Integer.parseInt(footHold.name()), new Point(x1, y1), new Point(x2, y2));
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
            });

      return allFootholds.stream()
            .reduce(new FootholdTreeBuilder(lBound, uBound), FootholdTreeBuilder::insert, (a, b) -> a)
            .build();
   }

   protected Rectangle getMapArea(MapleData mapData, MapleData infoData) {
      Rectangle mapArea = new Rectangle();
      int[] bounds = new int[4];
      bounds[0] = infoData.childByPath("VRTop")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      bounds[1] = infoData.childByPath("VRBottom")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);

      if (bounds[0] == bounds[1]) {    // old-style baked map
         Optional<MapleData> miniMap = mapData.childByPath("miniMap");
         if (miniMap.isPresent()) {
            bounds[0] = miniMap.flatMap(data -> data.childByPath("centerX"))
                  .flatMap(MapleDataTool::getInteger)
                  .map(this::invertInteger)
                  .orElse(0);
            bounds[1] = miniMap.flatMap(data -> data.childByPath("centerY"))
                  .flatMap(MapleDataTool::getInteger)
                  .map(this::invertInteger)
                  .orElse(0);
            bounds[2] = miniMap.flatMap(data -> data.childByPath("height"))
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(0);
            bounds[3] = miniMap.flatMap(data -> data.childByPath("width"))
                  .flatMap(MapleDataTool::getInteger)
                  .orElse(0);
            mapArea.setBounds(bounds[0], bounds[1], bounds[3], bounds[2]);
         } else {
            int dist = (1 << 18);
            mapArea.setBounds(-dist / 2, -dist / 2, dist, dist);
         }
      } else {
         bounds[2] = infoData.childByPath("VRLeft")
               .flatMap(MapleDataTool::getInteger)
               .orElse(0);
         bounds[3] = infoData.childByPath("VRRight")
               .flatMap(MapleDataTool::getInteger)
               .orElse(0);
         mapArea.setBounds(bounds[2], bounds[0], bounds[3] - bounds[2], bounds[1] - bounds[0]);
      }
      return mapArea;
   }

   private int invertInteger(Integer value) {
      return value * -1;
   }

   protected Optional<TimeMob> getTimeMob(MapleData infoData) {
      return infoData.childByPath("timeMob")
            .map(this::getTimeMobInternal);
   }

   protected TimeMob getTimeMobInternal(MapleData data) {
      int id = data.childByPath("id")
            .flatMap(MapleDataTool::getInteger)
            .orElse(0);
      String message = data.childByPath("message")
            .flatMap(MapleDataTool::getString)
            .orElse("");
      return new TimeMob(id, message);
   }

   protected List<PortalData> getPortals(MapleData mapData) {
      return mapData.childByPath("portal")
            .map(MapleData::children)
            .orElse(Collections.emptyList())
            .stream()
            .map(data -> PortalProcessor
                  .getInstance()
                  .createPortal(data.childByPath("pt")
                        .flatMap(MapleDataTool::getInteger)
                        .orElse(0), data))
            .collect(Collectors.toList());
   }

   protected String getOnUserEnter(int mapId, MapleData infoData) {
      return infoData.childByPath("onUserEnter")
            .flatMap(MapleDataTool::getString)
            .filter(result -> !result.equals(""))
            .orElse(String.valueOf(mapId));
   }

   protected String getOnFirstUserEnter(int mapId, MapleData infoData) {
      return infoData.childByPath("onFirstUserEnter")
            .flatMap(MapleDataTool::getString)
            .filter(result -> !result.equals(""))
            .orElse(String.valueOf(mapId));
   }

   protected float getMonsterRate(MapleData infoData) {
      return infoData.childByPath("mobRate")
            .flatMap(MapleDataTool::getFloat)
            .orElse(0f);
   }
}
