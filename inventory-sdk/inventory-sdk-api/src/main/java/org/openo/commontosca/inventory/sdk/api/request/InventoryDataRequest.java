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

import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.request.base.BaseUpdate;
import org.openo.commontosca.inventory.sdk.api.result.BulkInsertResult;
import org.openo.commontosca.inventory.sdk.api.result.CountResult;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;

public interface InventoryDataRequest<T, R> extends InventoryRequest<T, R> {

  public String getModel();

  public T model(String model);

  public interface BulkInsert
      extends InventoryDataRequest<BulkInsert, BulkInsertResult>, WriteOperation {

    /**
     * 
     * @param fails
     * @return
     */
    public BulkInsert failsTo(List<? extends Map<String, Object>> fails);

    public ValueList getFailsTo();

    public ValueList getValues();

    public BulkInsert value(List<? extends Map<String, Object>> datas);

  }

  public interface Count
      extends InventoryFilterable<Count>, InventoryDataRequest<Count, CountResult>, ReadOperation {

  }

  public interface Delete extends InventoryFilterable<Delete>,
      InventoryDataRequest<Delete, DeleteResult>, WriteOperation {

  }

  public interface Insert extends InventoryDataRequest<Insert, InsertResult>, WriteOperation {

    public ValueMap getValue();

    public Insert value(Map<String, Object> data);

  }

  public interface Query
      extends InventoryFilterable<Query>, InventoryDataRequest<Query, QueryResult>, ReadOperation {

    public Integer getLimit();

    public List<String> getProjection();

    public Integer getSkip();

    public Map<String, Boolean> getSort();

    public Query limit(Integer limit);

    public Query projection(List<String> projection);

    public Query projection(String... projection);

    public Query skip(Integer skip);

    public Query sort(String name, boolean asc);
    /*
     * public String getDistinct();
     * 
     * public Query distinct(String dataKey);
     */
  }

  public interface Update extends InventoryFilterable<Update>,
      InventoryDataRequest<Update, UpdateResult>, BaseUpdate<Update>, WriteOperation {

  }

}
