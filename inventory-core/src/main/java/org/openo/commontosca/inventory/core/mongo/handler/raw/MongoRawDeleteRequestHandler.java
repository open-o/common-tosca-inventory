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
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Delete;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultDeleteResult;

public class MongoRawDeleteRequestHandler
    extends AbstractMongoInventoryRequestHandler<Delete, DeleteResult> {

  @Override
  public SimpleDeferred<DeleteResult> handle(Delete request) {
    DeferredResponse<DeleteResult> response = new DeferredResponse<>();
    String collectionName = request.getCollection();
    Criteria filter = request.getFilter();
    this.getDatabase(request).getCollection(collectionName)
        .deleteMany(new Document(filter.toValueMap()), (result, ex) -> {
          if (ex != null) {
            response.reject(ex);
          } else {
            response.resolve(new DefaultDeleteResult(result.getDeletedCount()));
          }
        });
    return response;
  }

}
