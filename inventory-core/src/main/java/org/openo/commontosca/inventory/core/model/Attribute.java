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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public class Attribute {

  private static Set<String> avaliableDataType = new HashSet<>();
  public static final String TYPE_REFERENCE = "reference";
  public static final String TYPE_STRING = "string";
  public static final String TYPE_BOOLEAN = "boolean";
  public static final String TYPE_NUMBER = "number";
  public static final String TYPE_DATETIME = "datetime";

  static {
    Attribute.avaliableDataType.add(Attribute.TYPE_REFERENCE);
    Attribute.avaliableDataType.add(Attribute.TYPE_STRING);
    Attribute.avaliableDataType.add(Attribute.TYPE_BOOLEAN);
    Attribute.avaliableDataType.add(Attribute.TYPE_NUMBER);
    Attribute.avaliableDataType.add(Attribute.TYPE_DATETIME);
  }

  private String name;

  private String label;
  private String type;
  private boolean visible = true;

  private boolean editable = true;
  private boolean enable = true;
  private boolean required = false;
  private String ref;
  private boolean isArray = false;
  private boolean unique = false;
  private Map<String, String> properties = new HashMap<String, String>();

  public Attribute() {}

  public static boolean isAvaliableDataType(String type) {
    return Attribute.avaliableDataType.contains(type);
  }

  @SuppressWarnings("unchecked")
  public void fromMap(Map<String, Object> valueMap) {
    ValueMap map = ValueMap.wrap(valueMap);
    this.name = map.requireValue(ModelKey.NAME);
    this.type = map.requireValue(ModelKey.TYPE);
    this.label = map.optValue(ModelKey.LABEL, this.name);
    this.visible = map.optValue(ModelKey.VISIBLE, true);
    this.editable = map.optValue(ModelKey.EDITABLE, true);
    this.enable = map.optValue(ModelKey.ENABLE, true);
    this.required = map.optValue(ModelKey.REQUIRED, false);
    this.ref = map.optValue(ModelKey.REF, null);
    this.isArray = map.optValue(ModelKey.IS_ARRAY, false);
    this.unique = map.optValue(ModelKey.UNIQUE, false);
    // this.properties = map.opt(ModelKey.PROPERTIES,new HashMap<String, String>());

    if (valueMap.get(ModelConst.TAG_PROPERTIES) != null) {
      this.properties = (Map<String, String>) valueMap.get(ModelConst.TAG_PROPERTIES);
    }
  }

  public String getLabel() {
    return this.label;
  }

  public String getName() {
    return this.name;
  }

  public Map<String, String> getProperties() {
    return this.properties;
  }

  public String getRef() {
    return this.ref;
  }

  public String getType() {
    return this.type;
  }

  public boolean isArray() {
    return this.isArray;
  }

  public boolean isEditable() {
    return this.editable;
  }

  public boolean isEnable() {
    return this.enable;
  }

  public boolean isRequire() {
    return this.required;
  }

  public boolean isUnique() {
    return this.unique;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setArray(boolean isArray) {
    this.isArray = isArray;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public void setRequire(boolean require) {
    this.required = require;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    ValueMap valueMap = ValueMap.wrap(map);
    valueMap.put(ModelKey.NAME, this.name);
    if (this.label != null) {
      valueMap.put(ModelKey.LABEL, this.label);
    }
    valueMap.put(ModelKey.TYPE, this.type);
    valueMap.put(ModelKey.VISIBLE, this.visible);
    valueMap.put(ModelKey.EDITABLE, this.editable);
    valueMap.put(ModelKey.ENABLE, this.enable);
    valueMap.put(ModelKey.REQUIRED, this.required);
    if (this.ref != null) {
      valueMap.put(ModelKey.REF, this.ref);
    }
    valueMap.put(ModelKey.IS_ARRAY, this.isArray);
    valueMap.put(ModelKey.UNIQUE, this.unique);
    if (!this.properties.isEmpty()) {
      valueMap.put(ModelConst.TAG_PROPERTIES, this.properties);
    }
    return valueMap;
  }

}
