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
package org.openo.commontosca.inventory.mgr.rest;

import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.mgr.service.QueryService;
import org.openo.commontosca.inventory.mgr.service.RequestEntity;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openoapi/inventory/v1/services")
public class InventoryMgrRest {
  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryMgrRest.class);
  @Autowired
  private QueryService queryService;

  @RequestMapping(method = RequestMethod.POST)
  public List<Map<String, Object>> queryServiceInfo(@RequestBody Object paramMap) {
    if (paramMap instanceof Map) {
      RequestEntity queryRequest = new RequestEntity();
      queryRequest.fromMap(ValueMap.wrap(paramMap));
      return queryService.queryServiceFullInfo(queryRequest);
    } else {
      LOGGER.error("error argument:" + GsonUtils.toJson(paramMap));
      throw new IllegalArgumentException("Unsupported argument type: " + paramMap);
    }
  }

}
