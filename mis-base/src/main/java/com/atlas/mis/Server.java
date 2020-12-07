package com.atlas.mis;

import java.net.URI;

import com.atlas.shared.rest.RestServerFactory;
import com.atlas.shared.rest.RestService;
import com.atlas.shared.rest.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;

import database.PersistenceManager;

public class Server {
   public static void main(String[] args) {
      URI uri = UriBuilder.host(RestService.MAP_INFORMATION).uri();
      RestServerFactory.create(uri, "com.atlas.mis.rest");
   }
}