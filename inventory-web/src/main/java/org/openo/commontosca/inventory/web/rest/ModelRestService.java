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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.InventoryProviders;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.web.rest.result.JsonQueryResultEmitter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RestController
@RequestMapping("/openoapi/inventory/v1/model")
public class ModelRestService {
 
  @RequestMapping(method = RequestMethod.POST)
  public DeferredResult<ValueMap> addModel(@RequestBody Map<String, Object> paramMap) {
    DeferredResult<ValueMap> deferredResult = new DeferredResult<ValueMap>();
    InventoryProviders.findService(Inventory.class).model().insert().value(paramMap).executeAsync()
        .then(result -> {
          deferredResult.setResult(new ValueMap());
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  @RequestMapping(value = "/{model}", method = RequestMethod.DELETE)
  public DeferredResult<ValueMap> deleteModelByName(@PathVariable String model) {
    DeferredResult<ValueMap> deferredResult = new DeferredResult<ValueMap>();
    InventoryProviders.findService(Inventory.class).model().delete().byName(model).executeAsync()
        .then(result -> {
          deferredResult.setResult(
              ValueMap.wrap(Collections.singletonMap("deleted", result.getDeletedRows())));
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  @RequestMapping(method = RequestMethod.GET)
  public DeferredResult<ResponseBodyEmitter> queryAllModels() {
    DeferredResult<ResponseBodyEmitter> deferredResult = new DeferredResult<>();
    InventoryProviders.findService(Inventory.class).model().find().executeAsync().then(result -> {
      new JsonQueryResultEmitter(result).sendTo(deferredResult);
    }).fail(ex -> {
      deferredResult.setErrorResult(ex);
    });
    return deferredResult;
  }

  @RequestMapping(value = "/{model}", method = RequestMethod.GET)
  public DeferredResult<ValueMap> queryModelByName(@PathVariable String model) {
    DeferredResult<ValueMap> deferredResult = new DeferredResult<>();
    InventoryProviders.findService(Inventory.class).model().find().byName(model).executeAsync()
        .then(result -> {
          deferredResult.setResult(result.asOne());
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/{model}", method = RequestMethod.PUT)
  public DeferredResult<ValueMap> updateModelByName(@PathVariable String model,
      @RequestBody Map<String, Object> paramMap) {

    DeferredResult<ValueMap> deferredResult = new DeferredResult<ValueMap>();
    if (hasSameNameOrLabel((List<Map<String, Object>>) paramMap.get("attributes"))) {
      deferredResult.setErrorResult(new InventoryException("Update modle " + model
          + " failed!Attributes(" + paramMap.get("attributes") + ") have same name or label!"));
      return deferredResult;
    }
    InventoryProviders.findService(Inventory.class).model().update().byName(model).value(paramMap)
        .executeAsync().then(result -> {
          deferredResult.setResult(
              ValueMap.wrap(Collections.singletonMap("updated", result.getUpdatedRows())));
        }).fail(ex -> {
          deferredResult.setErrorResult(ex);
        });
    return deferredResult;
  }

  private boolean hasSameNameOrLabel(List<Map<String, Object>> attributes) {
    if (attributes == null || attributes.isEmpty()) {
      return false;
    }
    Set<String> names = new HashSet<>();
    Set<String> labels = new HashSet<>();
    for (Map<String, Object> attribute : attributes) {
      if (!names.add((String) attribute.get("name"))) {
        return true;
      }
      if (!labels.add((String) attribute.get("label"))) {
        return true;
      }
    }
    return false;

  }

}
