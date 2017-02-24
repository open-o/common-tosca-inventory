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
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Count;
import org.openo.commontosca.inventory.sdk.api.result.CountResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryDataCountRequest
    extends AbstractInventoryFilterableRequest<Count, CountResult>implements Count {

  private String model;

  public InventoryDataCountRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public String getModel() {
    return this.model;
  }

  @Override
  public InventoryDataCountRequest model(String model) {
    this.model = model;
    return this;
  }

  @Override
  public InventoryDataCountRequest validate() throws InventoryException {
    if (this.getModel() == null) {
      throw new IllegalArgumentException("No required model name.");
    }
    super.validate();
    return this;
  }

}
