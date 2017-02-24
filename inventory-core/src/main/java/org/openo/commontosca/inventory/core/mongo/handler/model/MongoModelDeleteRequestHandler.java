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
package org.openo.commontosca.inventory.core.mongo.handler.model;

import org.bson.Document;
import org.openo.commontosca.inventory.core.model.Model;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Delete;
import org.openo.commontosca.inventory.sdk.api.result.DeleteResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultDeleteResult;


public class MongoModelDeleteRequestHandler
    extends AbstractMongoInventoryRequestHandler<Delete, DeleteResult> {

  @Override
  public SimpleDeferred<DeleteResult> handle(Delete request) {
    DeferredResponse<DeleteResult> response = new DeferredResponse<>();
    this.getDatabase(request).getCollection(Model.MODEL_DEFAULT_COLLECTION_NAME)
        .deleteOne(new Document("name", request.getModelName()), (result, ex) -> {
          if (ex != null) {
            response.reject(ex);
          } else {
            deleteModelRecord(request, request.getModelName(), response,result.getDeletedCount());
          }
        });
    return response;
  }

  private void deleteModelRecord(Delete request, String modelName,
      DeferredResponse<DeleteResult> response,Long count) {

    this.getDatabase(request).getCollection("model_" + modelName).drop((result, ex) -> {
      if (ex != null) {
        response.reject(ex);
      }else{
        response.resolve(new DefaultDeleteResult(count));
      }
    });
  }

}
