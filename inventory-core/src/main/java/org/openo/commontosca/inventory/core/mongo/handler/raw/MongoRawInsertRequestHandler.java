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
package org.openo.commontosca.inventory.core.mongo.handler.raw;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Insert;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultInsertResult;

public class MongoRawInsertRequestHandler
    extends AbstractMongoInventoryRequestHandler<Insert, InsertResult> {

  @Override
  public SimpleDeferred<InsertResult> handle(Insert request) {
    DeferredResponse<InsertResult> response = new DeferredResponse<>();
    ValueMap valueMap = request.getValue();
    String collectionName = request.getCollection();

    valueMap.putIfAbsent(CommonKey.ID.getKeyName(),
        ValueAccess.wrap(new ObjectId()).as(CommonKey.ID.getValueType()));
    this.getDatabase(request).getCollection(collectionName).insertOne(new Document(valueMap),
        (result, ex) -> {
          if (ex != null) {
            response.reject(ex);
          } else {
            InsertResult value = new DefaultInsertResult(valueMap.requireString("_id"));
            response.resolve(value);
          }
        });
    return response;
  }

}
