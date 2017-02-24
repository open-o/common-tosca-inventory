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

import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Insert;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryRequest;

public class InventoryDataInsertRequest extends AbstractInventoryRequest<Insert, InsertResult>
    implements Insert {

  private String model;
  private Map<String, Object> data;

  public InventoryDataInsertRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public String getModel() {
    return this.model;
  }

  @Override
  public ValueMap getValue() {
    return ValueMap.wrap(this.data);
  }

  @Override
  public InventoryDataInsertRequest model(String model) {
    this.model = model;
    return this;
  }

  @Override
  public InventoryDataInsertRequest value(Map<String, Object> data) {
    this.data = data;
    return this;
  }

  @Override
  public InventoryDataInsertRequest validate() throws InventoryException {
    if (this.getModel() == null) {
      throw new IllegalArgumentException("No required model name.");
    }
    if (this.getValue() == null) {
      throw new IllegalArgumentException("Required a value for insert.");
    }
    super.validate();
    return this;
  }

}
