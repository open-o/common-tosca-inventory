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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.core.request.InventoryRawRequest.BulkInsert;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.result.BulkInsertResult;
import org.openo.commontosca.inventory.sdk.support.request.AbstractInventoryRequest;

public class InventoryRawBulkInsertRequest
    extends AbstractInventoryRequest<BulkInsert, BulkInsertResult>implements BulkInsert {

  private String collectionName;
  private List<Map<String, Object>> datas;
  private List<Map<String, Object>> failsTo;

  public InventoryRawBulkInsertRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public InventoryRawBulkInsertRequest collection(String collectionName) {
    this.collectionName = collectionName;
    return this;
  }

  @Override
  public InventoryRawBulkInsertRequest failsTo(List<Map<String, Object>> fails) {
    this.failsTo = fails;
    return this;
  }

  @Override
  public String getCollection() {
    return this.collectionName;
  }

  @Override
  public ValueList getFailsTo() {
    return ValueList.wrap(this.failsTo);
  }

  @Override
  public ValueList getValues() {
    return ValueList.wrap(this.datas);
  }

  @Override
  public InventoryRawBulkInsertRequest value(List<Map<String, Object>> String) {
    this.datas = Collections.unmodifiableList(this.datas);
    return this;
  }

  @Override
  public InventoryRawBulkInsertRequest validate() throws InventoryException {
    if (this.getCollection() == null) {
      throw new IllegalArgumentException("No required collection name.");
    }
    if (this.getValues() == null) {
      throw new IllegalArgumentException("Required a value for insert.");
    }
    super.validate();
    return this;
  }


}
