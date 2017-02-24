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
package org.openo.commontosca.inventory.sdk.support.request.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Query;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryDataQueryRequest
    extends AbstractInventoryFilterableRequest<Query, QueryResult>implements Query {

  private String model;
  private Integer skip;
  private Integer limit;
  private List<String> projection;
  private Map<String, Boolean> sort;


  public InventoryDataQueryRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public Integer getLimit() {
    return this.limit;
  }

  @Override
  public String getModel() {
    return this.model;
  }

  @Override
  public List<String> getProjection() {
    return this.projection;
  }

  @Override
  public Integer getSkip() {
    return this.skip;
  }

  @Override
  public Map<String, Boolean> getSort() {
    return this.sort;
  }

  @Override
  public InventoryDataQueryRequest limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  @Override
  public InventoryDataQueryRequest model(String model) {
    this.model = model;
    return this;
  }

  @Override
  public InventoryDataQueryRequest projection(List<String> projection) {
    this.projection = projection;
    return this;
  }

  @Override
  public InventoryDataQueryRequest projection(String... projection) {
    this.projection = Arrays.asList(projection);
    return this;
  }

  @Override
  public InventoryDataQueryRequest skip(Integer skip) {
    this.skip = skip;
    return this;
  }

  @Override
  public InventoryDataQueryRequest sort(String name, boolean asc) {
    if (this.sort == null) {
      this.sort = new LinkedHashMap<String, Boolean>();
    }
    this.sort.put(name, asc);
    return this;
  }

  @Override
  public InventoryDataQueryRequest validate() throws InventoryException {
    if (this.getModel() == null) {
      throw new IllegalArgumentException("No required model name.");
    }
    super.validate();
    return this;
  }

}
