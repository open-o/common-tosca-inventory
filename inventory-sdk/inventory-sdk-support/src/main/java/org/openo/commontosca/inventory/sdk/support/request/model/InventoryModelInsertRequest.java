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
package org.openo.commontosca.inventory.sdk.support.request.model;

import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Insert;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryRequest;

public class InventoryModelInsertRequest extends AbstractInventoryRequest<Insert, InsertResult>
    implements Insert {

  private Map<String, Object> model;

  public InventoryModelInsertRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public ValueMap getValue() {
    return ValueMap.wrap(this.model);
  }

  @Override
  public InventoryModelInsertRequest value(Map<String, Object> model) {
    this.model = model;
    return this;
  }

  @Override
  public org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Insert validate()
      throws InventoryException {
    if (this.getValue() == null) {
      throw new InventoryException("Required a value for insert.");
    }
    return super.validate();
  }

}
