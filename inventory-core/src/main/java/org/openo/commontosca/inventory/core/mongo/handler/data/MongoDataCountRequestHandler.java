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

import org.bson.Document;
import org.bson.conversions.Bson;
import org.openo.commontosca.inventory.core.mongo.MongoUtils;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Count;
import org.openo.commontosca.inventory.sdk.api.result.CountResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultCountResult;

public class MongoDataCountRequestHandler
    extends AbstractMongoInventoryRequestHandler<Count, CountResult> {

  @Override
  public SimpleDeferred<CountResult> handle(Count request) {
    DeferredResponse<CountResult> response = new DeferredResponse<>();
    String modelName = request.getModel();
    String modelCollectionName = MongoUtils.getModelCollectionName(modelName);
    Criteria filter = request.getFilter();
    Bson bsonFilter = null;
    if (filter != null) {
      ValueMap model = request.getInventory().model().find().byName(modelName).execute().asOne();
      if (model == null) {
        return response.reject("No such model: %s", modelName);
      }
      CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(filter.toValueMap(), model);
      bsonFilter = new Document(verifier.verify());
    }
    this.getDatabase(request).getCollection(modelCollectionName).count(bsonFilter, (total, e) -> {
      if (e != null) {
        response.reject(e);
      } else {
        response.resolve(new DefaultCountResult(total));
      }
    });
    return response;
  }

}
