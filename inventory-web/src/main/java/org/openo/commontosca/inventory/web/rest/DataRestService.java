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
package org.openo.commontosca.inventory.web.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryProviders;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.OP;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Count;
import org.openo.commontosca.inventory.sdk.api.request.InventoryDataRequest.Query;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;
import org.openo.commontosca.inventory.sdk.support.parser.CriteriaParseException;
import org.openo.commontosca.inventory.sdk.support.parser.CriteriaParser;
import org.openo.commontosca.inventory.web.rest.result.JsonQueryResultEmitter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RestController
@RequestMapping("/openoapi/inventory/v1")
public class DataRestService {
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/data/{model}", method = RequestMethod.POST)
  public DeferredResult<ValueMap> create(@PathVariable String model, @RequestBody Object bodyData) {
    DeferredResult<ValueMap> deferredResult = new DeferredResult<ValueMap>();
    if (bodyData instanceof List) {
      InventoryProviders.findService(Inventory.class).data().bulkInsert().model(model)
          .value((List<Map<String, Object>>) bodyData).executeAsync().then(result -> {
            deferredResult
                .setResult(ValueMap.wrap(Collections.singletonMap("id", result.getInsertIds())));
          }).fail(ex -> {
            deferredResult.setErrorResult(ex);
          });
    } else if (bodyData instanceof Map) {
      InventoryProviders.findService(Inventory.class).data().insert().model(model)
          .value(ValueMap.wrap(ValueMap.wrap(bodyData))).executeAsync().then(result -> {
            deferredResult
                .setResult(ValueMap.wrap(Collections.singletonMap("id", result.getInsertId())));
          }).fail(ex -> {
            deferredResult.setErrorResult(ex);
          });


    } else {
      throw new IllegalArgumentException("Unsupported argument type: " + bodyData);
    }

    return deferredResult;
  }

  @RequestMapping(value = "/data/{model}", method = RequestMethod.DELETE)
  public DeferredResult<Map<?, ?>> deleteDataByCriteria(@PathVariable String model,
      @RequestBody ValueMap criteria) {
    DeferredResult<Map<?, ?>> deferredResult = new DeferredResult<Map<?, ?>>();
    InventoryProviders.findService(Inventory.class).data().delete().model(model)
        .filter(CriteriaParser.parseValueMapCriteria(criteria)).executeAsync().then(result -> {
          deferredResult.setResult(Collections.singletonMap("delete", result.getDeletedRows()));
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  @RequestMapping(value = "/data/{model}/{id}", method = RequestMethod.DELETE)
  public DeferredResult<Map<?, ?>> deleteDataById(@PathVariable String model,
      @PathVariable String id) {
    DeferredResult<Map<?, ?>> deferredResult = new DeferredResult<Map<?, ?>>();
    InventoryProviders.findService(Inventory.class).data().delete().model(model)
        .filter(CommonKey.ID.getKeyName(), id).executeAsync().then(result -> {
          deferredResult.setResult(Collections.singletonMap("deleted", result.getDeletedRows()));
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }


  @RequestMapping(value = "/data/{model}/{id}", method = RequestMethod.PUT)
  public DeferredResult<Map<?, ?>> updateDataById(@PathVariable String model,
      @PathVariable String id, @RequestBody Map<String, Object> bodyData) {
    DeferredResult<Map<?, ?>> deferredResult = new DeferredResult<Map<?, ?>>();
    ValueMap valueMap = ValueMap.wrap(bodyData);
    InventoryProviders.findService(Inventory.class).data().update().model(model)
        .filter(CommonKey.ID.getKeyName(), id).value(valueMap).executeAsync().then(result -> {
          deferredResult.setResult(Collections.singletonMap("updated", result.getUpdatedRows()));
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  @RequestMapping(value = "/count/data/{model}", method = {RequestMethod.GET})
  public DeferredResult<Long> queryDataTotalCountOfModel(@PathVariable String model) {
    return queryDataCountByCriteria(model, new DefaultCriteria());
  }

  @RequestMapping(value = "/count/data/{model}", method = {RequestMethod.POST})
  public DeferredResult<Long> queryDataCountByCriteria(@PathVariable String model,
      @RequestBody(required = false) Map<String, Object> body) throws CriteriaParseException {
    Criteria criteria = null;
    if (body != null) {
      criteria = CriteriaParser.parseValueMapCriteria(ValueMap.wrap(body));
    }
    return queryDataCountByCriteria(model, criteria);
  }

  private DeferredResult<Long> queryDataCountByCriteria(String model, Criteria criteria) {
    DeferredResult<Long> deferredResult = new DeferredResult<Long>();
    Inventory inventory = InventoryProviders.findService(Inventory.class);
    Count count = inventory.data().count().model(model);

    count.filter(criteria);
    count.executeAsync().then(result -> {
      deferredResult.setResult(result.getCount());
    }).fail(ex -> {
      deferredResult.setErrorResult(ex);
    });
    return deferredResult;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/search/data/{model}", method = RequestMethod.POST)
  public DeferredResult<ResponseBodyEmitter> queryDataByCriteria(@PathVariable String model,
      @RequestBody ValueMap body) throws CriteriaParseException {
    DeferredResult<ResponseBodyEmitter> deferredResult = new DeferredResult<>();
    Criteria criteria = null;
    Query find = InventoryProviders.findService(Inventory.class).data().find();
    criteria = CriteriaParser.parseValueMapCriteria(body.optMap("condition"));
    find.limit(body.optInt("limit")).skip(body.optInt("offset"));
    find.projection(((List<String>) (List<?>) body.optList("projection")));
    if (body.containsKey("sort")) {
      body.requireMap("sort").entrySet().stream().forEach(entry -> {
        find.sort(entry.getKey(), ValueAccess.wrap(entry.getValue()).as(Boolean.class));
      });
    }
    find.model(model).filter(criteria).executeAsync().then(result -> {
      new JsonQueryResultEmitter(result).sendTo(deferredResult);
    }).fail(ex -> {
      deferredResult.setErrorResult(ex);
    });
    return deferredResult;
  }

  @RequestMapping(value = "/data/{model}", method = RequestMethod.GET)
  public DeferredResult<ResponseBodyEmitter> queryDataOfModel(@PathVariable String model) {
    return queryDataEntities(model, null);
  }

  @RequestMapping(value = "/data/{model}/{id}", method = RequestMethod.GET)
  public DeferredResult<ResponseBodyEmitter> queryDataOfModelById(@PathVariable String model,
      @PathVariable String id) {
    return queryDataEntities(model,
        new DefaultCriteria().setCriterion(CommonKey.ID.getKeyName(), OP.EQ, id));
  }

  private DeferredResult<ResponseBodyEmitter> queryDataEntities(String model, Criteria criteria) {
    DeferredResult<ResponseBodyEmitter> deferredResult = new DeferredResult<>();
    InventoryProviders.findService(Inventory.class).data().find().model(model).filter(criteria)
        .executeAsync().then(result -> {
          new JsonQueryResultEmitter(result).sendTo(deferredResult);
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }


}
