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
/**
 *
 */
package org.openo.commontosca.inventory.web.rest.result;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryProviders;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.google.gson.stream.JsonWriter;

public class QueryResultResponseFactory {

  private QueryResultResponseFactory() {}

  public static <T> T create(String model, Map<String, ?> map) {
    return QueryResultResponseFactory.create(model, map, Version.V5);
  }

  @SuppressWarnings("unchecked")
  public static <T> T create(String model, Map<String, ?> map, Version version) {
    return (T) QueryResultResponseFactory.doCreate(model, map, version);
  }

  public static Object doCreate(String model, Map<String, ?> map, Version version) {
    ValueMap valueMap = ValueMap.wrap(map);
    SimpleDeferred<QueryResult> deferred =
        InventoryProviders.findService(Inventory.class).data().find().model(model)
            .limit(valueMap.optInt("limit")).skip(valueMap.optInt("offset")).executeAsync();
    switch (version) {
      case V1: {
        try {
          return deferred.join().asList();
        } catch (Exception e) {
          throw Toolkits.toInventoryException(e);
        }
      }
      case V2: {
        DeferredResult<List<ValueMap>> response = new DeferredResult<>();
        deferred.then(result -> {
          response.setResult(result.asList());
        }).fail(ex -> {
          response.setErrorResult(ex);
        });
        return response;
      }
      case V3: {
        DeferredResult<ResponseBodyEmitter> response = new DeferredResult<>();
        deferred.then(result -> {
          new JsonQueryResultEmitter(result, false).sendTo(response);
        }).fail(ex -> {
          response.setErrorResult(ex);
        });
        return response;
      }
      case V4: {
        DeferredResult<ResponseBodyEmitter> response = new DeferredResult<>();
        deferred.then(result -> {
          new JsonQueryResultEmitter(result, true).sendTo(response);
        }).fail(ex -> {
          response.setErrorResult(ex);
        });
        return response;
      }
      case V5: {
        DeferredResult<StreamingResponseBody> response = new DeferredResult<>();
        deferred.then(result -> {
          response.setResult(output -> {
            JsonWriter writer =
                new JsonWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
            try {
              writer.beginArray();
              result.asCursor()
                  .forEachRemaining(data -> GsonUtils.toJson(data, ValueMap.class, writer));
              writer.endArray();
            } finally {
              Toolkits.closeQuitely(writer);
              Toolkits.closeQuitely(result);
            }
          });
        }).fail(ex -> {
          response.setErrorResult(ex);
        });
        return response;
      }
      default: {
        throw new IllegalArgumentException();
      }
    }
  }

  public enum Version {
    V1, V2, V3, V4, V5
  }

}
