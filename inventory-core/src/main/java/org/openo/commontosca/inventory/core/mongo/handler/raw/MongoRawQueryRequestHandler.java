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

import java.util.Collections;

import org.bson.Document;
import org.openo.commontosca.inventory.core.mongo.MongoQueryResult;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.Query;
import org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;

import com.mongodb.async.client.FindIterable;

public class MongoRawQueryRequestHandler
    extends AbstractMongoInventoryRequestHandler<Query, QueryResult> {

  @Override
  public SimpleDeferred<QueryResult> handle(Query request) {
    DeferredResponse<QueryResult> response = new DeferredResponse<>();
    String collectionName = request.getCollection();
    FindIterable<Document> find = this.getDatabase(request).getCollection(collectionName).find();
    Criteria filter = request.getFilter();
    if (filter != null) {

      CriteriaTypeVerifier verifier =
          new CriteriaTypeVerifier(filter.toValueMap(), ValueMap.wrap(Collections.emptyMap()));
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
    if (request.getProjection() != null) {
      find.projection(MongoUtils.getProjectionFromList(request.getProjection()));
    }
    find.map(document -> ValueMap.wrap(document)).batchCursor((cursor, e) -> {
      if (e != null) {
        response.reject(e);
      } else {
        response.resolve(new MongoQueryResult(cursor));
      }
    });
    return response;
  }

}
