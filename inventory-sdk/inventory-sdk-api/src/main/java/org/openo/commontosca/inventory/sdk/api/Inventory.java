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
package org.openo.commontosca.inventory.sdk.api;

import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequestExecutor;

public interface Inventory extends InventoryRequestExecutor {

  public Context getContext();


  public Data data();


  public Model model();

  public interface Data {

    public InventoryDataRequest.BulkInsert bulkInsert();

    public InventoryDataRequest.Count count();

    public InventoryDataRequest.Delete delete();

    public InventoryDataRequest.Query find();

    public InventoryDataRequest.Insert insert();

    public InventoryDataRequest.Update update();

  }

  public interface Model {

    public InventoryModelRequest.Delete delete();

    public InventoryModelRequest.Query find();

    public InventoryModelRequest.Insert insert();

    public InventoryModelRequest.Update update();

  }

}
