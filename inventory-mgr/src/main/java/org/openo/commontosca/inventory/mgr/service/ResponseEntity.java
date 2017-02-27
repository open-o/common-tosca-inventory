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
package org.openo.commontosca.inventory.mgr.service;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntity {
  private String serviceId = "";
  private String serviceName = "";
  private String description = "";
  private String createTime = "";
  private String creator = "";
  private String serviceType = "";
  private String templateName = "";
  private Map<String, String> inputParameters = new HashMap<String, String>();

  public ResponseEntity() {
    super();
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public Map<String, String> getInputParameters() {
    return inputParameters;
  }

  public void setInputParameters(Map<String, String> inputParameters) {
    this.inputParameters = inputParameters;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("serviceId", this.serviceId);
    m.put("serviceName", this.serviceName);
    m.put("description", this.description);
    m.put("createTime", this.createTime);
    m.put("creator", this.creator);
    m.put("serviceType", this.serviceType);
    m.put("templateName", this.templateName);
    m.put("inputParameters", this.inputParameters);
    return m;
  }

  public void fromMap(Map<String, Object> data) {
    this.serviceId = (String) data.get("serviceId");
    this.serviceName = (String) data.get("serviceName");
    this.description = (String) data.get("description");
    this.createTime = String.valueOf(data.get("createTime"));
    this.creator = (String) data.get("creator");
    this.serviceType = (String) data.get("serviceType");
    this.templateName = (String) data.get("templateName");

  }

  public void addInputParam(String inputKey, String inputValue) {
    this.inputParameters.put(inputKey, inputValue);

  }


}
