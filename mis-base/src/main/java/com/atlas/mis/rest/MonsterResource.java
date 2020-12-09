package com.atlas.mis.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlas.mis.rest.processor.RequestResultProcessor;

@Path("monsters")
public class MonsterResource {
   @GET
   @Path("/{monsterId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMonster(@PathParam("monsterId") Integer monsterId) {
      return RequestResultProcessor.getMonster(monsterId).build();
   }
}
