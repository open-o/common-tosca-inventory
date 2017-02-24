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

import java.util.Map;

import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Update;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryRawUpdateRequest
    extends AbstractInventoryFilterableRequest<Update, UpdateResult>implements Update {

  private String collectionName;
  private Map<String, Object> data;
  private boolean upsert;

  public InventoryRawUpdateRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryRawUpdateRequest collection(String collectionName) {
    this.collectionName = collectionName;
    return this;
  }

  @Override
  public String getCollection() {
    return this.collectionName;
  }

  @Override
  public ValueMap getValue() {
    return ValueMap.wrap(this.data);
  }

  @Override
  public boolean isUpsert() {
    return this.upsert;
  }

  @Override
  public InventoryRawUpdateRequest upsert(boolean upsert) {
    this.upsert = upsert;
    return this;
  }

  @Override
  public InventoryRawUpdateRequest validate() throws InventoryException {
    if (this.getCollection() == null) {
      throw new IllegalArgumentException("No required collection name.");
    }
    if (this.getFilter() == null || this.getFilter().isEmpty()) {
      throw new IllegalArgumentException("The update request can not accept a empty criteria");
    }
    if (this.getValue() == null) {
      throw new IllegalArgumentException("Required a value for update.");
    }
    super.validate();
    return this;
  }

  @Override
  public InventoryRawUpdateRequest value(Map<String, Object> data) {
    this.data = data;
    return this;
  }


}
