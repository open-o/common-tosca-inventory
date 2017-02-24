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
package org.openo.commontosca.inventory.core.mongo;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.model.ModelConst;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

import com.mongodb.client.model.Sorts;

public class MongoUtils {

  /**
   * 
   * @param modelName
   * @return
   */
  public static String getModelCollectionName(String modelName) {
    return "model_" + modelName;
  }

  /**
   *
   * @param model
   * @param list
   * @return
   */
  public static Bson getProjectionForModel(ValueMap model, List<String> projection) {
    ValueList attributeList = model.requireList(ModelConst.ATTRIBUTE_ATTRIBUTES);
    Document bson = new Document();
    for (int i = 0; i < attributeList.size(); i++) {
      ValueMap attribute = attributeList.requireMap(i);
      if (attribute.optBoolean("enable", false)
          && (projection != null && projection.contains(attribute.requireValue(ModelKey.NAME)))) {
        bson.put(attribute.requireValue(ModelKey.NAME), 1);
      }
    }
    return bson;
  }

  public static Bson getProjectionFromList(List<String> projection) {
    Document bson = new Document();
    for (String key : projection) {
      bson.put(key, 1);
    }
    return bson;
  }

  public static Bson getSortsFromMap(Map<String, Boolean> map) {
    List<Bson> sorts = map.entrySet().stream().map(entry -> {
      if (entry.getValue()) {
        return Sorts.ascending(entry.getKey());
      } else {
        return Sorts.descending(entry.getKey());
      }
    }).collect(Collectors.toList());
    if (!sorts.isEmpty()) {
      return Sorts.orderBy(sorts);
    } else {
      return null;
    }
  }

  /**
   * 
   * @param documents
   * @return
   */
  @SuppressWarnings("unchecked")
  public static List<ValueMap> toValueMap(List<Document> documents) {
    List<? extends Map<String, Object>> list = documents;
    for (ListIterator<Map<String, Object>> listIterator =
        (ListIterator<Map<String, Object>>) list.listIterator(); listIterator.hasNext();) {
      listIterator.set(ValueMap.wrap(listIterator.next()));
    }
    return (List<ValueMap>) list;
  }

}
