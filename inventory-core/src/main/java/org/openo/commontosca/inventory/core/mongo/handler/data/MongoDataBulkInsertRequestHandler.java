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
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.DataKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.verifier.DataTypeVerifier;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.BulkInsert;
import org.openo.commontosca.inventory.sdk.api.result.BulkInsertResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultBulkInsertResult;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;

import com.mongodb.client.model.InsertOneModel;

public class MongoDataBulkInsertRequestHandler
    extends AbstractMongoInventoryRequestHandler<BulkInsert, BulkInsertResult> {

  @Override
  public SimpleDeferred<BulkInsertResult> handle(BulkInsert request) {
    String modelName = request.getModel();
    return request.getInventory().model().find().byName(modelName).executeAsync().then(result -> {
      DeferredResponse<BulkInsertResult> response = new DeferredResponse<>();
      ValueMap model = result.asOne();
      if (model == null) {
        return response.reject("No such model: %s", modelName);
      } else if (!model.optValue(ModelKey.ENABLE, true)) {
        return response.reject("The model is disabled: %s", modelName);
      } else {
        try {
          ValueList datas = request.getValues();
          String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
          List<InsertOneModel<Document>> writeModels = datas.stream().map(data -> {
            try {
              ValueMap valueMap = ValueMap.wrap(data);
              ValueMap strictMap = new DataTypeVerifier(valueMap, model).verify();
              strictMap.put(DataKey.CREATE_TIME, new Date());
              strictMap.put(DataKey.LAST_MODIFIED, new Date());

              strictMap.putIfAbsent(CommonKey.ID.getKeyName(),
                  ValueAccess.wrap(new ObjectId()).as(CommonKey.ID.getValueType()));
              Document document = new Document(strictMap);
              return new InsertOneModel<Document>(document);
            } catch (Exception ex) {
              throw Toolkits.toInventoryException(ex);
            }
          }).collect(Collectors.toList());
          this.getDatabase(request).getCollection(modelCollectionName).bulkWrite(writeModels,
              (insertResult, ex) -> {
            if (ex != null) {
              response.reject("Insert data fail.", ex);
            } else {
              response.resolve(new DefaultBulkInsertResult());
            }
          });
        } catch (Exception ex) {
          response.reject(ex);
        }
        return response;
      }
    });
  }

}
