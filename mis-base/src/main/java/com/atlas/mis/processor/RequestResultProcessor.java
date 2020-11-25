package com.atlas.mis.processor;

import java.util.Collections;
import javax.ws.rs.core.Response;

import com.app.rest.util.stream.Collectors;
import com.app.rest.util.stream.Mappers;
import com.atlas.mis.model.MapData;
import com.atlas.mis.rest.ResultObjectFactory;

import builder.ResultBuilder;

public class RequestResultProcessor {
   private static final Object lock = new Object();

   private static volatile RequestResultProcessor instance;

   public static RequestResultProcessor getInstance() {
      RequestResultProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new RequestResultProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   public ResultBuilder getMap(int mapId) {
      return MapProcessor.getInstance().getMapData(mapId)
            .map(ResultObjectFactory::createMap)
            .map(Mappers::singleOkResult)
            .orElse(new ResultBuilder(Response.Status.NOT_FOUND));
   }

   public ResultBuilder getMapPortals(int mapId) {
      return MapProcessor.getInstance().getMapData(mapId)
            .map(MapData::portals)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createPortal)
            .collect(Collectors.toResultBuilder());
   }

   public ResultBuilder getMapReactors(int mapId) {
      return MapProcessor.getInstance().getMapData(mapId)
            .map(MapData::reactors)
            .orElse(Collections.emptyList())
            .stream()
            .map(ResultObjectFactory::createReactor)
            .collect(Collectors.toResultBuilder());
   }
}
