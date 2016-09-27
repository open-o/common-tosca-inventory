/**
 * Copyright 2016 ZTE Corporation.
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
package org.openo.commontosca.inventory.entity.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openo.commontosca.inventory.entity.db.ServiceBaseData;
import org.openo.commontosca.inventory.entity.db.ServiceInputParamData;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceInfo extends ServiceBaseData {

  private String templateName;
  private HashMap<String, String> inputParameters = new HashMap<String, String>();

  /**
   * set service instance base infor.
   * 
   * @param baseData base infor
   */
  public void setInstanceBaseInfor(ServiceBaseData baseData) {
    this.setServiceId(baseData.getServiceId());
    this.setStatus(baseData.getStatus());
    this.setActiveStatus(baseData.getActiveStatus());
    this.setCreateTime(baseData.getCreateTime());
    this.setCreator(baseData.getCreator());
    this.setDescription(baseData.getDescription());
    this.setServiceName(baseData.getServiceName());
    this.setServiceType(baseData.getServiceType());
  }

  /**
   * set service instance input parameter.
   * 
   * @param params params
   */
  public void setInputParam(ServiceInputParamData params) {
    inputParameters.put(params.getInputKey(), params.getInputValue());
  }
}
