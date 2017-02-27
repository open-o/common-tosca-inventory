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

import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public class RequestEntity {
  private ValueList sort = new ValueList();
  private int pageNum = 1;
  private int pageSize = 25;
  private ValueMap condition = new ValueMap();
  private String serviceID;

  public RequestEntity(ValueList sort, int pageNum, int pageSize, ValueMap condition,
      String serviceID) {
    super();
    this.sort = sort;
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.condition = condition;
    this.serviceID = serviceID;
  }

  public RequestEntity() {}

  public void fromMap(ValueMap value) {
    if (value.containsKey("sort")) {
      sort = value.optList("sort");
    }
    if (value.containsKey("pagination")) {
      pageNum = value.optInt("pagination");
    }
    if (value.containsKey("pagesize")) {
      pageSize = value.optInt("pagesize");
    }
    if (value.containsKey("condition")) {
      condition = value.optMap("condition");
    }
    if (value.containsKey("serviceId")) {
      serviceID = value.optString("serviceId");
    }
  }

  public ValueList getSort() {
    return sort;
  }

  public void setSort(ValueList sort) {
    this.sort = sort;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public ValueMap getCondition() {
    return condition;
  }

  public void setCondition(ValueMap condition) {
    this.condition = condition;
  }

  public String getServiceID() {
    return serviceID;
  }

  public void setServiceID(String serviceID) {
    this.serviceID = serviceID;
  }

}
