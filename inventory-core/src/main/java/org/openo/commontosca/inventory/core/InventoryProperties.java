/**
 * Copyright  2017 ZTE Corporation.
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
package org.openo.commontosca.inventory.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("inventory")
public class InventoryProperties {

  private Mongo mongo = new Mongo();

  public Mongo getMongo() {
    return this.mongo;
  }

  public void setMongo(Mongo mongo) {
    this.mongo = mongo;
  }

  public static class Mongo {

    /**
     * <address>[:port]
     */
    private String server = null;

    public String getServer() {
      if (this.server == null) {
        this.server = System.getProperty("inventory.mongo.server", "localhost");
      }
      return this.server;
    }

    public void setServer(String server) {
      this.server = server;
    }

  }

}
