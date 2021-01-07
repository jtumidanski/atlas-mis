package com.atlas.mis;

import java.net.URI;

import com.atlas.mis.constant.RestConstants;
import com.atlas.shared.rest.RestServerFactory;
import com.atlas.shared.rest.UriBuilder;

public class Server {
   public static void main(String[] args) {
      URI uri = UriBuilder.host(RestConstants.SERVICE).uri();
      RestServerFactory.create(uri, "com.atlas.mis.rest");
   }
}
