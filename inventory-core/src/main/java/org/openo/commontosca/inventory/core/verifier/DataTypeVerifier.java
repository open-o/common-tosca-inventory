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
package org.openo.commontosca.inventory.core.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.springframework.util.StringUtils;

public class DataTypeVerifier extends AbstractVerifier<ValueMap> {

  protected ValueMap model;

  public DataTypeVerifier(ValueMap data, ValueMap model) {
    super(data);
    this.model = model;
  }

  @Override
  public DataTypeVerifier policy(long policy) {
    super.policy(policy);
    return this;
  }

  @Override
  public ValueMap verify() throws VerifyException {
    this.strict = new ValueMap();
    try {
      ValueList attributes = this.model.requireValue(ModelKey.ATTRIBUTES);
      for (int i = 0; i < attributes.size(); i++) {
        ValueMap attribute = attributes.requireMap(i);
        boolean enable = attribute.optValue(ModelKey.ENABLE, true);
        if (!enable) {
          continue;
        }
        String key = attribute.requireValue(ModelKey.NAME);
        Object strictValue = this.verifyValue(key, attribute);
        if (strictValue != null) {
          this.strict.put(key, strictValue);
        } else if (this.origin.containsKey(key)) {

          this.strict.put(key, null);
        }
      }

      for (Map.Entry<String, Object> entry : this.origin.entrySet()) {
        String key = entry.getKey();
        if (!this.strict.containsKey(key)) {
          this.fail(entry.getKey(), entry.getValue(), ValueError.UNKNOWN_FIELD);
        }
      }
    } catch (Exception ex) {
      throw new VerifyException(ex);
    }
    return this.strict;
  }

  private Object verifyArray(String path, Object value, Class<?> type, boolean required) {
    List<Object> strictValue = new ArrayList<>(0);
    List<?> list = this.verifySingle(path, value, List.class, required);
    if (list != null) {
      List<Object> strictList = new ArrayList<>(list.size());
      for (Object subObj : list) {
        Object subValue =
            this.verifySingle(path + "[" + strictList.size() + "]", subObj, type, required);
        strictList.add(subValue);
      }
      strictValue = strictList;
    }
    return strictValue;
  }

  private <T> T verifySingle(String path, Object value, Class<T> type, boolean required) {
    ValueAccess access = ValueAccess.wrap(value);
    try {
      return access.as(type);
    } catch (Exception ex) {
      this.fail(path, value, ValueError.TYPE_NOT_MATCHED);
    }
    return null;
  }

  private Object verifyValue(String key, ValueMap attribute) {
    Object value = this.origin.get(key);
    boolean required = attribute.optValue(ModelKey.REQUIRED, false);
    if (StringUtils.isEmpty(value)) {
      if (required) {
        if ((this.policy & StrictPolicy.IGNORE_REQUIRED_KEY_WHEN_UPDATE) == 0
            || this.origin.containsKey(key)) {
          this.fail(key, value, ValueError.REQUIRED_IS_NULL);
        }
      }
      return null;
    } else {
      boolean isArray = attribute.optValue(ModelKey.IS_ARRAY, false);
      ValueType type = ValueType.parse(attribute.requireValue(ModelKey.TYPE));
      Object strictValue = null;
      if (isArray) {
        strictValue = this.verifyArray(key, value, type.getJavaType(), required);
      } else {
        strictValue = this.verifySingle(key, value, type.getJavaType(), required);
      }
      return strictValue;
    }
  }

}
