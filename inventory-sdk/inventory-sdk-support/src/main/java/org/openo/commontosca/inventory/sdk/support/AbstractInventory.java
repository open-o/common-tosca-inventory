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
package org.openo.commontosca.inventory.sdk.support;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.Context;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataBulkInsertRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataCountRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataDeleteRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataInsertRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataQueryRequest;
import org.openo.commontosca.inventory.sdk.support.request.data.InventoryDataUpdateRequest;
import org.openo.commontosca.inventory.sdk.support.request.model.InventoryModelDeleteRequest;
import org.openo.commontosca.inventory.sdk.support.request.model.InventoryModelInsertRequest;
import org.openo.commontosca.inventory.sdk.support.request.model.InventoryModelQueryRequest;
import org.openo.commontosca.inventory.sdk.support.request.model.InventoryModelUpdateRequest;

public abstract class AbstractInventory implements Inventory {

  private Context context = new Context();

  @Override
  public Context getContext() {
    return this.context;
  }

  @Override
  public Data data() {
    return new DataRequests(this);
  }

  @Override
  public Model model() {
    return new ModelRequests(this);
  }

  protected static class DataRequests implements Data {

    private Inventory inventory;

    public DataRequests(Inventory inventory) {
      this.inventory = inventory;
    }

    @Override
    public InventoryDataRequest.BulkInsert bulkInsert() {
      return new InventoryDataBulkInsertRequest(this.inventory);
    }

    @Override
    public InventoryDataRequest.Count count() {
      return new InventoryDataCountRequest(this.inventory);
    }

    @Override
    public InventoryDataRequest.Delete delete() {
      return new InventoryDataDeleteRequest(this.inventory);
    }

    @Override
    public InventoryDataRequest.Query find() {
      return new InventoryDataQueryRequest(this.inventory);
    }

    @Override
    public InventoryDataRequest.Insert insert() {
      return new InventoryDataInsertRequest(this.inventory);
    }

    @Override
    public InventoryDataRequest.Update update() {
      return new InventoryDataUpdateRequest(this.inventory);
    }

  }

  protected static class ModelRequests implements Model {

    private Inventory inventory;

    public ModelRequests(Inventory inventory) {
      this.inventory = inventory;
    }

    @Override
    public InventoryModelRequest.Delete delete() {
      return new InventoryModelDeleteRequest(this.inventory);
    }

    @Override
    public InventoryModelRequest.Query find() {
      return new InventoryModelQueryRequest(this.inventory);
    }

    @Override
    public InventoryModelRequest.Insert insert() {
      return new InventoryModelInsertRequest(this.inventory);
    }

    @Override
    public InventoryModelRequest.Update update() {
      return new InventoryModelUpdateRequest(this.inventory);
    }

  }

}
