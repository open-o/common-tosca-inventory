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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.DataKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.model.Model;
import org.openo.commontosca.inventory.core.mongo.DeferredSingleResult;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.utils.I18n;
import org.openo.commontosca.inventory.core.verifier.ModelTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.StrictPolicy;
import org.openo.commontosca.inventory.core.verifier.ValueType;
import org.openo.commontosca.inventory.core.verifier.VerifyException;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryModelRequest.Update;
import org.openo.commontosca.inventory.sdk.api.result.UpdateResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultUpdateResult;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoModelUpdateRequestHandler
    extends AbstractMongoInventoryRequestHandler<Update, UpdateResult> {

  @Override
  public SimpleDeferred<UpdateResult> handle(Update request) {
    DeferredResponse<UpdateResult> response = new DeferredResponse<>();
    try {
      String modelName = request.getModelName();
      ValueMap model =
          request.getInventory().model().find().byName(request.getModelName()).execute().asOne();
      if (model == null || model.isEmpty()) {
        return response.reject("No such model: %s", modelName);
      }
      ValueMap valueMap = request.getValue();
      ValueMap strictMap =
          new ModelTypeVerifier(valueMap).policy(StrictPolicy.DEFINE_UPDATE_DATA).verify();
      verifierModelLabelUique(request, strictMap);
      if (strictMap.containsKey(ModelKey.NAME)
          && !modelName.equals(strictMap.optValue(ModelKey.NAME))) {
        return response.reject(I18n.getLabel("model.name.can.not.update",
            strictMap.optValue(ModelKey.NAME), modelName), modelName);
      }
      strictMap.put(ModelKey.LAST_MODIFIED, new Date());
      strictMap.remove(CommonKey.ID);
      strictMap.remove(ModelKey.NAME);
      boolean isEnable = false;
      if (strictMap.containsKey(ModelKey.ENABLE)) {
        isEnable = strictMap.optValue(ModelKey.ENABLE);
      } else {
        isEnable = model.optValue(ModelKey.ENABLE);
      }


      strictMap.put(ModelKey.ENABLE, false);
      MongoCollection<Document> modelCollection =
          this.getDatabase(request).getCollection(Model.MODEL_DEFAULT_COLLECTION_NAME);
      com.mongodb.client.result.UpdateResult updateResult =
          DeferredSingleResult.<com.mongodb.client.result.UpdateResult>execute(deferred -> {
            modelCollection.updateOne(
                new Document(ModelKey.NAME.getKeyName(), request.getModelName()),
                new Document("$set", strictMap), deferred);
          });
      if (updateResult.getModifiedCount() > 0 && isEnable) {
        this.ensureDataCollection(request);
        DeferredSingleResult.<com.mongodb.client.result.UpdateResult>execute(deferred -> {
          modelCollection.updateOne(
              new Document(ModelKey.NAME.getKeyName(), request.getModelName()),
              new Document("$set", Collections.singletonMap(ModelKey.ENABLE.getKeyName(), true)),
              deferred);
        });
      }
      response.resolve(new DefaultUpdateResult(updateResult.getModifiedCount()));
    } catch (Exception e) {
      response.reject(e);
    }
    return response;
  }

  private void ensureDataCollection(Update request) throws Exception {
    ValueMap model =
        request.getInventory().model().find().byName(request.getModelName()).execute().asOne();
    if (model != null) {
      MongoCollection<Document> dataCollection = this.getDatabase(request)
          .getCollection(MongoUtils.getModelCollectionName(request.getModelName()));
      List<ValueMap> allIndexes = DeferredSingleResult.<List<ValueMap>>execute(deferred -> {
        dataCollection.listIndexes().map(document -> {
          return ValueMap.wrap(document);
        }).into(new ArrayList<>(), deferred);
      });
      String primaryKey = "_id";
      String displayAttribute = model.requireValue(ModelKey.DISPLAY_ATTRUBITE);
      this.ensureDataCollectionIndex(dataCollection, allIndexes, DataKey.CREATE_TIME.getKeyName(),
          true, false);
      this.ensureDataCollectionIndex(dataCollection, allIndexes, DataKey.LAST_MODIFIED.getKeyName(),
          true, false);
      model.requireValue(ModelKey.ATTRIBUTES).stream().map(obj -> {
        return ValueAccess.wrap(obj).as(ValueMap.class);
      }).forEach(attribute -> {
        boolean isUniqueIndex = attribute.optValue(ModelKey.UNIQUE, false);
        boolean isReferenceTypeIndex =
            ValueType.parse(attribute.requireValue(ModelKey.TYPE)) == ValueType.REFERENCE;
        boolean isDisplayAttributeIndex =
            attribute.requireValue(ModelKey.NAME).equals(displayAttribute);
        boolean isPrimaryKey = attribute.requireValue(ModelKey.NAME).equals(primaryKey);
        if (!isPrimaryKey) {
          boolean hasIndex = isUniqueIndex || isReferenceTypeIndex || isDisplayAttributeIndex;
          try {
            this.ensureDataCollectionIndex(dataCollection, allIndexes,
                attribute.requireValue(ModelKey.NAME), hasIndex, isUniqueIndex || isPrimaryKey);
          } catch (Exception e) {
            throw Toolkits.toInventoryException(e);
          }
        }
      });
    } else {
      throw new IllegalArgumentException("No such model: " + request.getModelName());
    }
  }

  private void ensureDataCollectionIndex(MongoCollection<Document> dataCollection,
      List<ValueMap> allIndexes, String attributeName, boolean hasIndex, boolean uniqueIndex)
          throws Exception {
    Optional<ValueMap> findIndex = allIndexes.stream().filter(map -> {
      return map.requireMap("key").get(attributeName) != null;
    }).findAny();
    if (findIndex.isPresent()) {
      ValueMap theIndex = findIndex.get();
      String indexName = theIndex.requireString("name");
      if (hasIndex) {
        boolean oldIsUnique = theIndex.optBoolean("unique", false);
        if (uniqueIndex != oldIsUnique) {
          DeferredSingleResult.<Void>execute(deferred -> {
            dataCollection.dropIndex(indexName, deferred);
          });
          allIndexes.removeIf(map -> {
            return map.requireMap("key").get(attributeName) != null;
          });
          this.ensureDataCollectionIndex(dataCollection, allIndexes, attributeName, hasIndex,
              uniqueIndex);
        }
      } else {
        DeferredSingleResult.<Void>execute(deferred -> {
          dataCollection.dropIndex(indexName, deferred);
        });
      }
    } else if (hasIndex) {
      DeferredSingleResult.<String>execute(deferred -> {
        dataCollection.createIndex(Indexes.ascending(attributeName),
            new IndexOptions().unique(uniqueIndex), deferred);
      });
    }
  }

  private void verifierModelLabelUique(Update request, ValueMap updatetModel) throws Exception {
    List<ValueMap> models = request.getInventory().model().find().execute().asList();
    for (ValueMap model : models) {
      if (model.get(ModelKey.LABEL).equals(updatetModel.get(ModelKey.LABEL))
          && !model.get(ModelKey.NAME).equals(updatetModel.get(ModelKey.NAME))) {
        throw new VerifyException(I18n.getLabel("model.label.is.duplicate",
            updatetModel.optValue(ModelKey.NAME), updatetModel.optValue(ModelKey.LABEL)));
      }

    }

  }
}
