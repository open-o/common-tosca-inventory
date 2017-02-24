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

import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Delete;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryFilterableRequest;

public class InventoryRawDeleteRequest
    extends AbstractInventoryFilterableRequest<Delete, DeleteResult>implements Delete {

  private String collectionName;

  public InventoryRawDeleteRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryRawDeleteRequest collection(String collectionName) {
    this.collectionName = collectionName;
    return this;
  }

  @Override
  public String getCollection() {
    return this.collectionName;
  }

  @Override
  public InventoryRawDeleteRequest validate() throws InventoryException {
    if (this.getCollection() == null) {
      throw new IllegalArgumentException("No required collection name.");
    }
    if ((this.getFilter() == null || this.getFilter().isEmpty())
        && this.getFilter() != DefaultCriteria.DELETE_ALL) {
      throw new IllegalArgumentException("The update request can not accept a empty criteria");
    }
    super.validate();
    return this;
  }

}
