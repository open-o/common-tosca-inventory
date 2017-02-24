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

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Delete;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryRequest;

public class InventoryModelDeleteRequest extends AbstractInventoryRequest<Delete, DeleteResult>
    implements Delete {

  private String modelName;

  public InventoryModelDeleteRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryModelDeleteRequest byName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  @Override
  public String getModelName() {
    return this.modelName;
  }

  @Override
  public InventoryModelDeleteRequest validate() throws InventoryException {
    if (this.getModelName() == null) {
      throw new IllegalArgumentException("Required a model name for delete.");
    }
    super.validate();
    return this;
  }

}
