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
package org.openo.commontosca.inventory.sdk.api.request;

import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;

public interface InventoryModelRequest<T, R> extends InventoryRequest<T, R> {

  public interface Delete extends InventoryModelRequest<Delete, DeleteResult>, WriteOperation {

    public Delete byName(String modelName);

    public String getModelName();

  }

  public interface Insert extends InventoryModelRequest<Insert, InsertResult>, WriteOperation {

    public ValueMap getValue();

    public Insert value(Map<String, Object> model);

  }

  public interface Query extends InventoryModelRequest<Query, QueryResult>, ReadOperation {

    public Query byName(String modelName);

    public String getModelName();

  }

  public interface Update extends InventoryModelRequest<Update, UpdateResult>, WriteOperation {

    public Update byName(String modelName);

    public String getModelName();

    public ValueMap getValue();

    public Update value(Map<String, Object> model);

  }

}
