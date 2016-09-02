/**
 * Copyright 2016 [ZTE] and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.commontosca.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryApp extends Application<InventoryAppConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryApp.class);

  public static void main(String[] args) throws Exception {
    new InventoryApp().run(args);
  }

  @Override
  public void initialize(Bootstrap<InventoryAppConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/api-doc", "/api-doc", "index.html", "api-doc"));
  }

  @Override
  public void run(InventoryAppConfiguration configuration, Environment environment) {
    LOGGER.info("Start to initialize inventory.");
    environment.jersey().packages("org.openo.commontosca.inventory.resource");
    environment.jersey().register(MultiPartFeature.class);
    initSwaggerConfig(environment, configuration);
    initService();
    LOGGER.info("Initialize inventory finished.");
  }

  private void initService() {
    // TODO register to MSB
  }


  /**
   * initialize swagger configuration.
   *
   * @param environment environment information
   * @param configuration configuration
   */
  private void initSwaggerConfig(Environment environment, InventoryAppConfiguration configuration) {
    environment.jersey().register(new ApiListingResource());
    environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    BeanConfig config = new BeanConfig();
    config.setTitle("Open-o Inventory Service rest API");
    config.setVersion("1.0.0");
    config.setResourcePackage("org.openo.commontosca.inventory.resource");
    // set rest api basepath in swagger
    SimpleServerFactory simpleServerFactory =
        (SimpleServerFactory) configuration.getServerFactory();
    String basePath = simpleServerFactory.getApplicationContextPath();
    String rootPath = simpleServerFactory.getJerseyRootPath();
    rootPath = rootPath.substring(0, rootPath.indexOf("/*"));
    basePath = basePath.equals("/") ? rootPath
        : (new StringBuilder()).append(basePath).append(rootPath).toString();
    config.setBasePath(basePath);
    config.setScan(true);
  }
}
