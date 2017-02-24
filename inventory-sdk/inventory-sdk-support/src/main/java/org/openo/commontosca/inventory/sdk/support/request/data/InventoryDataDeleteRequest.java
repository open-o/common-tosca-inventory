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

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Delete;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryDataDeleteRequest
    extends AbstractInventoryFilterableRequest<Delete, DeleteResult>implements Delete {

  private String model;

  public InventoryDataDeleteRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public String getModel() {
    return this.model;
  }

  @Override
  public InventoryDataDeleteRequest model(String model) {
    this.model = model;
    return this;
  }

  @Override
  public InventoryDataDeleteRequest validate() throws InventoryException {
    if (this.getModel() == null) {
      throw new IllegalArgumentException("No required model name.");
    }
    if (this.getFilter() == null || this.getFilter().isEmpty()) {
      throw new IllegalArgumentException("The update request can not accept a empty criteria");
    }
    super.validate();
    return this;
  }

}
