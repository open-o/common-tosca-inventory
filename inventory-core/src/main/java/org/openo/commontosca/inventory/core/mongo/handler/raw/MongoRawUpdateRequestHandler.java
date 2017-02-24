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
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Update;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultUpdateResult;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.UpdateOptions;

public class MongoRawUpdateRequestHandler
    extends AbstractMongoInventoryRequestHandler<Update, UpdateResult> {

  @Override
  public SimpleDeferred<UpdateResult> handle(Update request) {
    DeferredResponse<UpdateResult> response = new DeferredResponse<>();
    String collectionName = request.getCollection();
    ValueMap valueMap = request.getValue();
    Document bsonFilter = null;

    Criteria filter = request.getFilter();
    if (filter != null) {
      bsonFilter = new Document(filter.toValueMap());
    }
    if (!request.isUpsert()) {
      valueMap.remove(CommonKey.ID);
    }
    SingleResultCallback<com.mongodb.client.result.UpdateResult> resultCallback = (result, ex) -> {
      if (ex != null) {
        response.reject(ex);
      } else {
        response.resolve(new DefaultUpdateResult(result.getModifiedCount()));
      }
    };
    if (request.isUpsert()) {
      this.getDatabase(request).getCollection(collectionName).updateOne(bsonFilter,
          new Document("$set", valueMap), new UpdateOptions().upsert(true), resultCallback);
    } else {
      this.getDatabase(request).getCollection(collectionName).updateMany(bsonFilter,
          new Document("$set", valueMap), resultCallback);
    }
    return response;
  }

}
