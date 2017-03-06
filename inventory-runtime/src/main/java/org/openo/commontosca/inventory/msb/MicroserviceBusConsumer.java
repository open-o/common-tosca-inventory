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
package org.openo.commontosca.inventory.msb;

import java.util.Map;

import org.openo.commontosca.inventory.config.MicroserviceConfig;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class MicroserviceBusConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MicroserviceBusConsumer.class);
  @Autowired
  RestTemplate restTemplate;

  /**
   * @param entity service entity
   * @return register service to msb success return true, else return false.
   */
  public boolean registerService(ServiceRegisterEntity entity) {
    LOG.info("microservice register body:" + GsonUtils.toJson(entity));
    try {
      LOG.info(restTemplate
          .postForObject("http://" + MicroserviceConfig.getMsbServerAddr() + ":"
              + MicroserviceConfig.getServiceIp()
              + "/openoapi/microservices/v1/services?createOrUpdate=false", entity, Map.class)
          .toString());
      return true;
    } catch (Exception e) {
      LOG.error("microservice register failed!" + e.getMessage());
      return false;
    }

  }
}
