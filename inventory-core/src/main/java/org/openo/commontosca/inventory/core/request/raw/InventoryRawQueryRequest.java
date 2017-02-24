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
package org.openo.commontosca.inventory.core.request.raw;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Query;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryRawQueryRequest extends AbstractInventoryFilterableRequest<Query, QueryResult>
    implements Query {

  private String collectionName;
  private Integer skip;
  private Integer limit;
  private List<String> projection;
  private Map<String, Boolean> sort;

  public InventoryRawQueryRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryRawQueryRequest collection(String collectionName) {
    this.collectionName = collectionName;
    return this;
  }

  @Override
  public String getCollection() {
    return this.collectionName;
  }

  @Override
  public Integer getLimit() {
    return this.limit;
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
  public InventoryRawQueryRequest limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  @Override
  public InventoryRawQueryRequest projection(List<String> projection) {
    this.projection = projection;
    return this;
  }

  @Override
  public InventoryRawQueryRequest projection(String... projection) {
    this.projection = Arrays.asList(projection);
    return this;
  }

  @Override
  public InventoryRawQueryRequest skip(Integer skip) {
    this.skip = skip;
    return this;
  }

  @Override
  public InventoryRawQueryRequest sort(String name, boolean asc) {
    if (this.sort != null) {
      this.sort = new LinkedHashMap<String, Boolean>();
    }
    this.sort.put(name, asc);
    return this;
  }

  @Override
  public InventoryRawQueryRequest validate() throws InventoryException {
    if (this.getCollection() == null) {
      throw new IllegalArgumentException("No required collection name.");
    }
    super.validate();
    return this;
  }


}
