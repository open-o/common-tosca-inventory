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

import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataBulkInsertRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataCountRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataDeleteRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataInsertRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataQueryRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.data.MongoDataUpdateRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.model.MongoModelDeleteRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.model.MongoModelInsertRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.model.MongoModelQueryRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.model.MongoModelUpdateRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawBulkInsertRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawCountRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawDeleteRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawInsertRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawQueryRequestHandler;
import org.openo.commontosca.inventory.core.mongo.handler.raw.MongoRawUpdateRequestHandler;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;
import org.openo.commontosca.inventory.sdk.support.InventoryBaseRequestExecutor;

public class MongoInventoryRequestExecutor extends InventoryBaseRequestExecutor {

  public MongoInventoryRequestExecutor() {
    this.addRequestHandler(new MongoDataBulkInsertRequestHandler());
    this.addRequestHandler(new MongoDataDeleteRequestHandler());
    this.addRequestHandler(new MongoDataInsertRequestHandler());
    this.addRequestHandler(new MongoDataQueryRequestHandler());
    this.addRequestHandler(new MongoDataUpdateRequestHandler());
    this.addRequestHandler(new MongoDataCountRequestHandler());
    this.addRequestHandler(new MongoModelDeleteRequestHandler());
    this.addRequestHandler(new MongoModelInsertRequestHandler());
    this.addRequestHandler(new MongoModelQueryRequestHandler());
    this.addRequestHandler(new MongoModelUpdateRequestHandler());
    this.addRequestHandler(new MongoRawBulkInsertRequestHandler());
    this.addRequestHandler(new MongoRawDeleteRequestHandler());
    this.addRequestHandler(new MongoRawInsertRequestHandler());
    this.addRequestHandler(new MongoRawQueryRequestHandler());
    this.addRequestHandler(new MongoRawUpdateRequestHandler());
    this.addRequestHandler(new MongoRawCountRequestHandler());
  }

  @Override
  public <T, R> SimpleDeferred<R> execute(InventoryRequest<T, R> request) {
    return super.execute(request);
  }

}
