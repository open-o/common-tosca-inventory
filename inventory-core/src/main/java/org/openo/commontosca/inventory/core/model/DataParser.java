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
package org.openo.commontosca.inventory.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openo.commontosca.inventory.core.utils.I18n;
import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryProviders;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public class DataParser {

  private Model currentModel = new Model();
  private ValueMap dataMap;
  private Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
  boolean isCheckReference = true;
  private Inventory inventory;

  /**
   *
   * @param modelName
   * @param dataMap ValueMap
   * @param isCheckReference
   */
  public DataParser(String modelName, ValueMap dataMap, boolean isCheckReference) {
    this.dataMap = dataMap;
    this.inventory = InventoryProviders.findService(Inventory.class);
    ValueMap modelMap = this.inventory.model().find().byName(modelName).execute().asOne();
    this.currentModel.fromMap(modelMap);
    this.isCheckReference = isCheckReference;
  }

  public DataParser(ValueMap currentModel, ValueMap dataMap, boolean isCheckReference) {
    super();
    this.inventory = InventoryProviders.findService(Inventory.class);
    this.currentModel.fromMap(currentModel);
    this.dataMap = dataMap;
    this.isCheckReference = isCheckReference;
  }

  /**
   * 
   * @return
   */
  public Map<String, List<String>> parse() {
    this.validateIllgalField();
    this.validateRecord();
    return this.resultMap;
  }

  private void validateArrayType(Attribute attribute) {
    String type = attribute.getType();
    String attributeName = attribute.getName();
    parseArray(attribute, attributeName);
    ValueList list = this.dataMap.optList(attributeName, null);
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        if (type.equals(Attribute.TYPE_STRING) && list.optString(i) != null) {
          continue;
        }
        if (type.equals(Attribute.TYPE_BOOLEAN) && list.optBoolean(i) != null) {
          continue;
        }
        if (type.equals(Attribute.TYPE_NUMBER) && list.optDouble(i) != null) {
          continue;
        }
        if (type.equals(Attribute.TYPE_DATETIME) && list.optValue(i, Date.class) != null) {
          continue;
        }
        if (type.equals(Attribute.TYPE_REFERENCE) && this.isCheckReference) {
          String objectId = list.optString(i);
          if (objectId != null) {
            if (objectId.isEmpty()) {
              return;
            }
            ValueMap refRecord = this.inventory.data().find().model(attribute.getRef())
                .filter("_id", objectId).execute().asOne();
            if (refRecord == null) {
              String value = I18n.getLabel("dataparse.refnotexist", objectId);
              saveToResultMap(attributeName, value);
            }
          } else {
            String value = I18n.getLabel("dataparse.refisnull");
            saveToResultMap(attributeName, value);
          }
          return;
        }
        if (type.equals(Attribute.TYPE_REFERENCE) && !this.isCheckReference) {
          return;
        }
        String value = I18n.getLabel("dataparse.datatypewrong", i);
        saveToResultMap(attributeName, value);
      }
    } else {
      String value = I18n.getLabel("dataparse.notarraytype");
      saveToResultMap(attributeName, value);
    }
  }

  private void parseArray(Attribute attribute, String attributeName) {
    String value = (String) this.dataMap.get(attributeName);
    String[] strs = value.split(ModelConst.ARRAY_SEPERATOR);
    Stream<String> stream = Stream.of(strs);
    ValueList valueList = ValueList.wrap(stream.collect(Collectors.toList()));
    this.dataMap.put(attributeName, valueList);
  }

  private void saveToResultMap(String attributeName, String value) {
    List<String> valueList = this.resultMap.get(attributeName);
    if (valueList == null) {
      valueList = new ArrayList<String>();
    }
    valueList.add(value);
    this.resultMap.put(attributeName, valueList);

  }

  private void validateBaseDataType(Attribute attribute) {
    String type = attribute.getType();
    String attributeName = attribute.getName();

    if (type.equals(Attribute.TYPE_STRING) && this.dataMap.optString(attributeName) != null) {
      return;
    }
    if (type.equals(Attribute.TYPE_BOOLEAN) && this.dataMap.optBoolean(attributeName) != null) {
      return;
    }
    if (type.equals(Attribute.TYPE_NUMBER) && this.dataMap.optDouble(attributeName) != null) {
      return;
    }
    if (type.equals(Attribute.TYPE_DATETIME)
        && this.dataMap.optValue(attributeName, Date.class) != null) {
      return;
    }
    if (type.equals(Attribute.TYPE_REFERENCE) && this.isCheckReference) {
      String objectId = this.dataMap.optString(attributeName);
      if (objectId != null) {
        if (objectId.isEmpty()) {
          return;
        }
        ValueMap refRecord = this.inventory.data().find().model(attribute.getRef())
            .filter("_id", objectId).execute().asOne();
        if (refRecord == null) {
          String value = I18n.getLabel("dataparse.refnotexist", new Object[] {objectId});
          saveToResultMap(attributeName, value);
        }
      } else {
        String value = I18n.getLabel("dataparse.refisnull");
        saveToResultMap(attributeName, value);
      }
      return;
    }
    if (type.equals(Attribute.TYPE_REFERENCE) && !this.isCheckReference) {
      return;
    }
    String value = I18n.getLabel("dataparse.datatypeillegal");
    saveToResultMap(attributeName, value);
  }

  private void validateDataType(Attribute attribute) {
    boolean isArray = attribute.isArray();
    if (isArray) {
      this.validateArrayType(attribute);
    } else {
      this.validateBaseDataType(attribute);
    }
  }

  private void validateIllgalField() {
    Set<String> modelAttributeNames = new HashSet<String>();
    for (Attribute attr : this.currentModel.getAttributes()) {
      modelAttributeNames.add(attr.getName());
    }
    Map<String, Object> tmpDataMap = new HashMap<String, Object>(this.dataMap);
    tmpDataMap.keySet().removeAll(modelAttributeNames);
    if (tmpDataMap.size() != 0) {
      Iterator<String> it = tmpDataMap.keySet().iterator();
      while (it.hasNext()) {
        String attributeName = it.next();
        String value = I18n.getLabel("dataparse.attributeillegal");
        saveToResultMap(attributeName, value);
      }

      this.dataMap.keySet().removeAll(tmpDataMap.keySet());
    }
  }

  private void validateRecord() {
    Iterator<String> it = this.dataMap.keySet().iterator();
    while (it.hasNext()) {
      String attributeName = it.next();
      Attribute attribute = this.currentModel.getAttributeByName(attributeName);
      if (attribute != null) {
        this.validateDataType(attribute);
        this.validateRequired(attribute);
      }
    }
  }

  private void validateRequired(Attribute attribute) {
    String attributeName = attribute.getName();
    if (attribute.isRequire()) {
      Object dataValue = this.dataMap.get(attributeName);
      if (dataValue == null) {
        String value = I18n.getLabel("dataparse.datanotnull", attributeName);
        saveToResultMap(attributeName, value);
      }
    }
  }
}
