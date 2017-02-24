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
package org.openo.commontosca.inventory.init;

import org.openo.commontosca.inventory.msb.ServiceRegistrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;


@Service
public class InventoryAppicationLifecycle implements SmartLifecycle {
  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryAppicationLifecycle.class);

  public InventoryAppicationLifecycle() {}

  @Override
  public int getPhase() {
    return 0;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public boolean isRunning() {
    return false;
  }

  @Override
  public void start() {
    LOGGER.info("Start to initialize inventory.");
    initService();
    LOGGER.info("Initialize inventory finished.");
  }

  private void initService() {
    Thread registerInventoryService = new Thread(new ServiceRegistrer());
    registerInventoryService.setName("register inventory service to Microservice Bus");
    registerInventoryService.start();
  }

  @Override
  public void stop() {
    InventoryAppicationLifecycle.LOGGER.info("InventoryApp stop!");
  }

  @Override
  public void stop(Runnable callback) {
    InventoryAppicationLifecycle.LOGGER.info("InventoryApp stop!");

  }

}
