package com.atlas.mis.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlas.mis.processor.RequestResultProcessor;

@Path("maps")
public class MapResource {
   @GET
   @Path("/{mapId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMap(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getInstance().getMap(mapId).build();
   }

   @GET
   @Path("/{mapId}/portals")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapPortals(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getInstance().getMapPortals(mapId).build();
   }

   @GET
   @Path("/{mapId}/reactors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapReactors(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getInstance().getMapReactors(mapId).build();
   }
}
