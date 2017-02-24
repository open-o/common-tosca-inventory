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
package org.openo.commontosca.inventory.core;

import java.io.File;
import java.util.List;

import org.openo.commontosca.inventory.core.request.InventoryRawRequest;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;


public interface InventoryCore extends Inventory {

  public Raw raw();

  public interface Import {

    public void importData(File data, String model);

    public void importData(List<ValueMap> data, String model);

  }

  public interface Meta {

    public void indexes();

    public void collections();

  }

  public interface Raw {

    public InventoryRawRequest.BulkInsert bulkInsert();

    public InventoryRawRequest.Count count();

    public InventoryRawRequest.Delete delete();

    public InventoryRawRequest.Query find();

    public InventoryRawRequest.Insert insert();

    public InventoryRawRequest.Update update();

  }

}
