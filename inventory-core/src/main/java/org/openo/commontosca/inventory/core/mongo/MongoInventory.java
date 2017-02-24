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
package org.openo.commontosca.inventory.core.mongo;

import org.openo.commontosca.inventory.core.InventoryCore;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.BulkInsert;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Count;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Delete;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Insert;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Query;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Update;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawBulkInsertRequest;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawCountRequest;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawDeleteRequest;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawInsertRequest;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawQueryRequest;
import org.openo.commontosca.inventory.core.request.raw.InventoryRawUpdateRequest;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;
import org.openo.commontosca.inventory.sdk.support.AbstractInventory;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;

public class MongoInventory extends AbstractInventory implements InventoryCore {

  private MongoClient client = null;
  private MongoDatabase database = null;
  private MongoInventoryRequestExecutor requestExecutor = null;

  public MongoInventory(MongoClient client, String database) {
    this.client = client;
    this.database = this.client.getDatabase(database);
    this.requestExecutor = new MongoInventoryRequestExecutor();
  }

  public MongoClient getClient() {
    return this.client;
  }

  public MongoDatabase getDatabase() {
    return this.database;
  }

  @Override
  public <T, R> SimpleDeferred<R> execute(InventoryRequest<T, R> request) {
    return this.requestExecutor.execute(request);
  }

  @Override
  public Raw raw() {
    return new Raw(this);
  }

  private static class Raw implements InventoryCore.Raw {

    private InventoryCore inventory;

    public Raw(InventoryCore inventory) {
      this.inventory = inventory;
    }

    @Override
    public BulkInsert bulkInsert() {
      return new InventoryRawBulkInsertRequest(this.inventory);
    }

    @Override
    public Count count() {
      return new InventoryRawCountRequest(this.inventory);
    }

    @Override
    public Delete delete() {
      return new InventoryRawDeleteRequest(this.inventory);
    }

    @Override
    public Query find() {
      return new InventoryRawQueryRequest(this.inventory);
    }

    @Override
    public Insert insert() {
      return new InventoryRawInsertRequest(this.inventory);
    }

    @Override
    public Update update() {
      return new InventoryRawUpdateRequest(this.inventory);
    }

  }


}
