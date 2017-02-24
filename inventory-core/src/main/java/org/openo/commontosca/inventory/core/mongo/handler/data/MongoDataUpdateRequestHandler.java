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
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.DataKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.DataTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.StrictPolicy;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Update;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultUpdateResult;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.UpdateOptions;

public class MongoDataUpdateRequestHandler
    extends AbstractMongoInventoryRequestHandler<Update, UpdateResult> {

  @Override
  public SimpleDeferred<UpdateResult> handle(Update request) {
    String modelName = request.getModel();
    return request.getInventory().model().find().byName(modelName).executeAsync().then(result -> {
      DeferredResponse<UpdateResult> response = new DeferredResponse<>();
      ValueMap model = result.asOne();
      if (model == null) {
        return response.reject("No such model: %s", modelName);
      } else if (!model.optValue(ModelKey.ENABLE, true)) {
        return response.reject("The model is disabled: %s", modelName);
      } else {
        try {
          ValueMap valueMap = request.getValue();
          DataTypeVerifier verifier = new DataTypeVerifier(valueMap, model);
          if (request.isUpsert()) {
            verifier.policy(StrictPolicy.DEFINE_INSERT_DATA);
          } else {
            verifier.policy(StrictPolicy.DEFINE_UPDATE_DATA);
          }
          ValueMap strictMap = verifier.verify();
          strictMap.put(DataKey.LAST_MODIFIED, new Date());
          strictMap.remove(DataKey.CREATE_TIME);

          if (!request.isUpsert()) {
            strictMap.remove(CommonKey.ID);
          }

          String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
          Document bsonFilter = null;

          Criteria filter = request.getFilter();
          if (filter != null) {
            CriteriaTypeVerifier filerVerifier =
                new CriteriaTypeVerifier(filter.toValueMap(), model);
            bsonFilter = new Document(filerVerifier.verify());
          }
          SingleResultCallback<com.mongodb.client.result.UpdateResult> resultCallback =
              (updateResult, ex) -> {
            if (ex != null) {
              response.reject(ex);
            } else {
              if (request.isUpsert() && updateResult.getUpsertedId() != null) {
                String expectId = strictMap.requireValue(CommonKey.ID);
                String actualId = updateResult.getUpsertedId().asString().getValue();
                if (actualId.equals(expectId)) {
                  ValueMap updateCreateDate = new ValueMap();
                  updateCreateDate.put(DataKey.CREATE_TIME, new Date());
                  this.getDatabase(request).getCollection(modelCollectionName).updateOne(
                      new Document("_id", actualId), new Document("$set", updateCreateDate),
                      (ignoreResult, ingoreException) -> {
                  });
                }
              }
              response.resolve(new DefaultUpdateResult(updateResult.getModifiedCount()));
            }
          };
          if (request.isUpsert()) {
            this.getDatabase(request).getCollection(modelCollectionName).updateOne(bsonFilter,
                new Document("$set", strictMap), new UpdateOptions().upsert(true), resultCallback);
          } else {
            this.getDatabase(request).getCollection(modelCollectionName).updateMany(bsonFilter,
                new Document("$set", strictMap), resultCallback);
          }
        } catch (Exception ex) {
          response.reject(ex);
        }
        return response;
      }
    });
  }

}
