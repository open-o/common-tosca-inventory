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

import java.util.Collections;

import org.bson.Document;
import org.openo.commontosca.inventory.core.mongo.MongoQueryResult;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Query;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.HeapQueryResult;

import com.mongodb.async.client.FindIterable;

public class MongoDataQueryRequestHandler
    extends AbstractMongoInventoryRequestHandler<Query, QueryResult> {

  @Override
  public SimpleDeferred<QueryResult> handle(Query request) {
    String modelName = request.getModel();
    return request.getInventory().model().find().byName(modelName).executeAsync().then((result) -> {
      DeferredResponse<QueryResult> deferred = new DeferredResponse<>();
      ValueMap model = result.asOne();
      if (model == null) {
        return deferred.reject("No such model %s", modelName);
      } else {
        boolean isModelEnabled = model.optBoolean("enable", true);
        if (isModelEnabled) {
          /*
           * if (request.getDistinct() != null) { deferred = dealDistinctRequest(request, model); }
           * else {
           * 
           * }
           */
          deferred = dealFindRequest(request, model);
        } else {
          deferred.resolve(new HeapQueryResult(Collections.emptyList()));
        }

        return deferred;
      }
    });
  }

  // private DeferredResponse<QueryResult> dealDistinctRequest(Query request, ValueMap model) {
  // String dataKey = request.getDistinct();
  // Criteria filter = request.getFilter();
  // String modelName = request.getModel();
  // String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
  // DeferredResponse<QueryResult> deferred = new DeferredResponse<>();
  // DistinctIterable<Document> distinctIterable = null;
  //
  // distinctIterable =
  // this.getDatabase(request).getCollection(modelCollectionName).distinct(dataKey, Document.class);
  //
  // if (filter != null) {
  // CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(filter.toValueMap(), model);
  // Document document = new Document(verifier.verify());
  // distinctIterable.filter(document);
  // }
  //
  // distinctIterable.map(document -> ValueMap.wrap(document)).batchCursor((cursor, e) -> {
  // if (e != null) {
  // deferred.reject(e);
  // } else if (cursor != null) {
  // deferred.resolve(new MongoQueryResult(cursor));
  // } else {
  // deferred.resolve(new HeapQueryResult(Collections.emptyList()));
  // }
  // });
  // return deferred;
  // }

  private DeferredResponse<QueryResult> dealFindRequest(Query request, ValueMap model) {
    String modelName = request.getModel();
    String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
    DeferredResponse<QueryResult> deferred = new DeferredResponse<>();

    FindIterable<Document> find = this.getDatabase(request).getCollection(modelCollectionName)
        .find().projection(MongoUtils.getProjectionForModel(model, request.getProjection()));
    Criteria filter = request.getFilter();
    if (filter != null) {
      CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(filter.toValueMap(), model);
      find.filter(new Document(verifier.verify()));
    }
    if (request.getSkip() != null) {
      find.skip(request.getSkip());
    }
    if (request.getLimit() != null) {
      find.limit(request.getLimit());
    }
    if (request.getSort() != null) {
      find.sort(MongoUtils.getSortsFromMap(request.getSort()));
    }

    find.map(document -> ValueMap.wrap(document)).batchCursor((cursor, e) -> {
      if (e != null) {
        deferred.reject(e);
      } else if (cursor != null) {
        deferred.resolve(new MongoQueryResult(cursor));
      } else {
        deferred.resolve(new HeapQueryResult(Collections.emptyList()));
      }
    });
    return deferred;
  }

}
