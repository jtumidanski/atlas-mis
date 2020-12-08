package com.atlas.mis.rest.processor;

import java.util.Collections;
import javax.ws.rs.core.Response;

import com.app.rest.util.stream.Collectors;
import com.app.rest.util.stream.Mappers;
import com.atlas.mis.MapDataRegistry;
import com.atlas.mis.model.MapData;
import com.atlas.mis.rest.ResultObjectFactory;

import builder.ResultBuilder;

public final class RequestResultProcessor {
   private RequestResultProcessor() {
   }

   public static ResultBuilder getMap(int mapId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(ResultObjectFactory::createMap)
            .map(Mappers::singleOkResult)
            .orElse(new ResultBuilder(Response.Status.NOT_FOUND));
   }

   public static ResultBuilder getMapPortals(int mapId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::portals)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createPortal)
            .collect(Collectors.toResultBuilder());
   }

   public static ResultBuilder getMapReactors(int mapId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::reactors)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createReactor)
            .collect(Collectors.toResultBuilder());
   }

   public static ResultBuilder getMapPortalByName(int mapId, String name) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::portals)
            .orElse(Collections.emptyList())
            .stream()
            .filter(portal -> portal.name().equalsIgnoreCase(name))
            .findFirst()
            .map(ResultObjectFactory::createPortal)
            .map(Mappers::singleOkResult)
            .orElse(new ResultBuilder(Response.Status.NOT_FOUND));
   }

   public static ResultBuilder getMapPortalById(int mapId, int portalId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::portals)
            .orElse(Collections.emptyList())
            .stream()
            .filter(portal -> portal.id() == portalId)
            .findFirst()
            .map(ResultObjectFactory::createPortal)
            .map(Mappers::singleOkResult)
            .orElse(new ResultBuilder(Response.Status.NOT_FOUND));
   }

   public static ResultBuilder getMapNpcs(int mapId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::npcs)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createNpc)
            .collect(Collectors.toResultBuilder());
   }

   public static ResultBuilder getMapMonsters(int mapId) {
      return MapDataRegistry.getInstance().getMapData(mapId)
            .map(MapData::monsters)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createMonster)
            .collect(Collectors.toResultBuilder());
   }
}
