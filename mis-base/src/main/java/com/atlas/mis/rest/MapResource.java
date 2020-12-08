package com.atlas.mis.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlas.mis.rest.processor.RequestResultProcessor;

@Path("maps")
public class MapResource {
   @GET
   @Path("/{mapId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMap(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getMap(mapId).build();
   }

   @GET
   @Path("/{mapId}/portals")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapPortals(@PathParam("mapId") Integer mapId,
                                 @QueryParam("name") String name) {
      if (name != null) {
         return RequestResultProcessor.getMapPortalByName(mapId, name).build();
      }
      return RequestResultProcessor.getMapPortals(mapId).build();
   }

   @GET
   @Path("/{mapId}/portals/{portalId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapPortalById(@PathParam("mapId") Integer mapId, @PathParam("portalId") Integer portalId) {
      return RequestResultProcessor.getMapPortalById(mapId, portalId).build();
   }

   @GET
   @Path("/{mapId}/reactors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapReactors(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getMapReactors(mapId).build();
   }

   @GET
   @Path("/{mapId}/npcs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapNpcs(@PathParam("mapId") Integer mapId) {
      return RequestResultProcessor.getMapNpcs(mapId).build();
   }
}
