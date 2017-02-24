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

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.mongo.handler.AbstractMongoInventoryRequestHandler;
import org.openo.commontosca.inventory.core.request.InventoryRawRequest.BulkInsert;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.BulkInsertResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.result.DefaultBulkInsertResult;

public class MongoRawBulkInsertRequestHandler
    extends AbstractMongoInventoryRequestHandler<BulkInsert, BulkInsertResult> {

  @Override
  public SimpleDeferred<BulkInsertResult> handle(BulkInsert request) {
    DeferredResponse<BulkInsertResult> response = new DeferredResponse<>();
    ValueList datas = request.getValues();
    String collectionName = request.getCollection();
    List<Document> documents = datas.stream().map(data -> {
      ValueMap valueMap = ValueAccess.wrap(data).as(ValueMap.class);

      valueMap.putIfAbsent(CommonKey.ID.getKeyName(),
          ValueAccess.wrap(new ObjectId()).as(CommonKey.ID.getValueType()));
      Document document = new Document(valueMap);
      return document;
    }).collect(Collectors.toList());
    this.getDatabase(request).getCollection(collectionName).insertMany(documents, (result, ex) -> {
      if (ex != null) {
        response.reject(ex);
      } else {
        response.resolve(new DefaultBulkInsertResult());
      }
    });
    return response;
  }

}
