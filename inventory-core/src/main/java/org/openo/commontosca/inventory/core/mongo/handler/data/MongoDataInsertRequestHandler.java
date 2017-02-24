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
package org.openo.commontosca.inventory.core.mongo.handler.data;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.DataKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.verifier.DataTypeVerifier;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Insert;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultInsertResult;

public class MongoDataInsertRequestHandler
    extends AbstractMongoInventoryRequestHandler<Insert, InsertResult> {

  @Override
  public SimpleDeferred<InsertResult> handle(Insert request) {
    String modelName = request.getModel();
    return request.getInventory().model().find().byName(modelName).executeAsync().then(result -> {
      DeferredResponse<InsertResult> response = new DeferredResponse<>();
      try {
        ValueMap model = result.asOne();
        if (model == null) {
          return response.reject("No such model: %s", modelName);
        } else if (!model.optValue(ModelKey.ENABLE, true)) {
          return response.reject("The model is disabled: %s", modelName);
        } else {
          ValueMap valueMap = request.getValue();
          ValueMap strictMap = new DataTypeVerifier(valueMap, model).verify();
          strictMap.put(DataKey.CREATE_TIME, new Date());
          strictMap.put(DataKey.LAST_MODIFIED, new Date());

          strictMap.putIfAbsent(CommonKey.ID.getKeyName(),
              ValueAccess.wrap(new ObjectId()).as(CommonKey.ID.getValueType()));
          String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
          Document document = new Document(strictMap);
          this.getDatabase(request).getCollection(modelCollectionName).insertOne(document,
              (insertResult, ex) -> {
            if (ex != null) {
              response.reject(ex);
            } else {
              InsertResult value = new DefaultInsertResult(strictMap.requireValue(CommonKey.ID));
              response.resolve(value);
            }
          });
        }
      } catch (Exception ex) {
        response.reject(ex);
      }
      return response;
    });
  }

}
