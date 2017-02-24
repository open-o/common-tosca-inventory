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
package org.openo.commontosca.inventory.core.mongo.handler;

import org.openo.commontosca.inventory.core.mongo.MongoInventory;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequestHandler;
import org.openo.commontosca.inventory.sdk.api.result.Result;

import com.mongodb.async.client.MongoDatabase;

public abstract class AbstractMongoInventoryRequestHandler<T extends InventoryRequest<T, R>, R extends Result>
    implements InventoryRequestHandler<T, R> {

  public MongoDatabase getDatabase(InventoryRequest<T, R> request) {
    MongoInventory inventory = request.getInventory(MongoInventory.class);
    return inventory.getDatabase();
  }

}
