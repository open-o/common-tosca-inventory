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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.model.Model;
import org.openo.commontosca.inventory.core.mongo.DeferredSingleResult;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.utils.I18n;
import org.openo.commontosca.inventory.core.verifier.ModelTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.VerifyException;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Insert;
import org.openo.commontosca.inventory.sdk.api.result.InsertResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultInsertResult;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoModelInsertRequestHandler
    extends AbstractMongoInventoryRequestHandler<Insert, InsertResult> {

  @Override
  public SimpleDeferred<InsertResult> handle(Insert request) {
    DeferredResponse<InsertResult> response = new DeferredResponse<>();
    try {
      ValueMap valueMap = request.getValue();
      ValueMap strictMap = new ModelTypeVerifier(valueMap).verify();

      if (!strictMap.containsKey(ModelKey.CREATE_TIME)) {
        strictMap.put(ModelKey.CREATE_TIME, new Date());
      }
      verifierModelUique(request, strictMap);
      strictMap.putIfAbsent(CommonKey.ID.getKeyName(),
          ValueAccess.wrap(new ObjectId()).as(CommonKey.ID.getValueType()));
      strictMap.put(ModelKey.LAST_MODIFIED, new Date());
      strictMap.put(ModelKey.ENABLE, false);
      Document document = new Document(strictMap);
      this.ensureModelCollection(request);
      this.getDatabase(request).getCollection(Model.MODEL_DEFAULT_COLLECTION_NAME)
          .insertOne(document, (result, ex) -> {
            if (ex != null) {
              response.reject(ex);
            } else {
              strictMap.put(ModelKey.ENABLE, true);
              request.getInventory().model().update().byName(strictMap.requireValue(ModelKey.NAME))
                  .value(strictMap).executeAsync().then(updateResult -> {
                response.resolve(new DefaultInsertResult(String.valueOf(document.get("_id"))));
              }).fail(e -> {
                response.reject(e);
              });
            }
          });
    } catch (Exception ex) {
      response.reject(ex);
    }
    return response;
  }

  /**
   *
   * 
   * @param request
   * @throws Exception
   */
  private void ensureModelCollection(Insert request) throws Exception {
    MongoCollection<Document> modelCollection =
        this.getDatabase(request).getCollection(Model.MODEL_DEFAULT_COLLECTION_NAME);
    boolean noneMatch = DeferredSingleResult.<List<Document>>execute(deferred -> {
      modelCollection.listIndexes().into(new ArrayList<>(), deferred);
    }).stream().map(d -> {
      return ValueMap.wrap(d);
    }).noneMatch(v -> {
      return v.requireMap("key").get(ModelKey.NAME) != null;
    });
    if (noneMatch) {
      DeferredSingleResult.<String>execute(deferred -> {
        modelCollection.createIndex(Indexes.ascending(ModelKey.NAME.getKeyName()),
            new IndexOptions().unique(true), deferred);
      });
    }
  }

  private void verifierModelUique(Insert request, ValueMap insertModel) throws VerifyException {
    List<ValueMap> models = request.getInventory().model().find().execute().asList();
    for (ValueMap model : models) {
      if (model.get(ModelKey.NAME).equals(insertModel.get(ModelKey.NAME))) {
        throw new VerifyException(I18n.getLabel("model.name.is.duplicate", model.get(ModelKey.NAME),
            model.get(ModelKey.NAME)));
      }
      if (model.get(ModelKey.LABEL).equals(insertModel.get(ModelKey.LABEL))) {
        throw new VerifyException(I18n.getLabel("model.label.is.duplicate",
            model.get(ModelKey.LABEL), model.get(ModelKey.LABEL)));
      }

    }
  }

}
