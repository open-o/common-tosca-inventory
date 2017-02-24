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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.BulkInsert;
import org.openo.commontosca.inventory.sdk.api.result.BulkInsertResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryRequest;

public class InventoryDataBulkInsertRequest
    extends AbstractInventoryRequest<BulkInsert, BulkInsertResult>implements BulkInsert {

  private String model;
  private List<Map<String, Object>> datas;
  private List<?> failsTo;

  public InventoryDataBulkInsertRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryDataBulkInsertRequest failsTo(List<? extends Map<String, Object>> fails) {
    this.failsTo = fails;
    return this;
  }

  @Override
  public ValueList getFailsTo() {
    return ValueList.wrap(this.failsTo);
  }

  @Override
  public String getModel() {
    return this.model;
  }

  @Override
  public ValueList getValues() {
    return ValueList.wrap(this.datas);
  }

  @Override
  public InventoryDataBulkInsertRequest model(String model) {
    this.model = model;
    return this;
  }

  @Override
  public InventoryDataBulkInsertRequest value(List<? extends Map<String, Object>> datas) {
    this.datas = Collections.unmodifiableList(datas);
    return this;
  }

  @Override
  public InventoryDataBulkInsertRequest validate() throws InventoryException {
    if (this.getModel() == null) {
      throw new IllegalArgumentException("No required model name.");
    }
    if (this.getValues() == null) {
      throw new IllegalArgumentException("Required a value for insert.");
    }
    super.validate();
    return this;
  }



}
