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
package org.openo.commontosca.inventory.core.verifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.springframework.util.StringUtils;

public class CriteriaTypeVerifier extends AbstractVerifier<ValueMap> {

  private ValueMap model;

  public CriteriaTypeVerifier(ValueMap criteria, ValueMap model) {
    super(criteria);
    this.model = model;
  }


  @Override
  public ValueMap verify() {
    ValueMap attributesMap = new ValueMap();
    ValueList attributeList =
        this.model.optValue(ModelKey.ATTRIBUTES, ValueList.wrap(Collections.emptyList()));
    for (Object attr : attributeList) {
      ValueMap attrMap = ValueAccess.wrap(attr).as(ValueMap.class);
      attributesMap.put(attrMap.requireValue(ModelKey.NAME), attrMap);
    }
    this.strict = this.verifyValue(this.origin, Object.class, attributesMap).as(ValueMap.class);
    return this.strict;
  }

  private ValueAccess verifyValue(ValueAccess value, Class<?> type, ValueMap attributesMap) {
    if (value.isCollection()) {
      ValueList list = value.as(ValueList.class);
      ValueList strict = new ValueList();
      for (Object item : list) {
        strict.add(this.verifyValue(ValueAccess.wrap(item), type, attributesMap).as(Object.class));
      }
      return strict;
    } else if (value.isMap()) {
      ValueMap map = value.as(ValueMap.class);
      ValueMap strict = new ValueMap();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String entryKey = entry.getKey();
        ValueMap attrMap = attributesMap.optMap(entryKey);
        Class<?> subType = type;
        ValueAccess entryValue = null;
        if (attrMap != null) {
          ValueType attrType = ValueType.parse(attrMap.requireValue(ModelKey.TYPE));
          subType = attrType.getJavaType();
        } else if (entryKey.equals(CommonKey.ID.getKeyName())) {
          subType = CommonKey.ID.getValueType();
        } else if (entryKey.equals("$regex")) {
          if (!StringUtils.isEmpty(entry.getValue())) {
            entryValue = ValueAccess.wrap(likeValue2Pattern(entry.getValue().toString()));
          }
        } else if (entryKey.equals("$in")) {
         
          if (!(entry.getValue() instanceof Set)) {
            ValueList list = ValueList.wrap(entry.getValue());
            Set<Object> set = new HashSet<>();
            set.addAll(list);
            entryValue = this.verifyValue(ValueAccess.wrap(set), subType, attributesMap);
          }
        }
        if (entryValue == null) {
          entryValue = this.verifyValue(ValueAccess.wrap(entry.getValue()), subType, attributesMap);
        }
        strict.put(entryKey, entryValue.as(Object.class));
      }
      return strict;
    } else {
      return ValueAccess.wrap(value.as(type));
    }
  }

  private String likeValue2Pattern(String value) {
    StringBuffer sb = new StringBuffer();
    int index = 0;
    int previousIndex = 0;
    while (true) {
      int questionLineIndex = value.indexOf('?', index);
      int starIndex = value.indexOf('*', index);
      int signIndex = Math.min(questionLineIndex, starIndex) == -1
          ? Math.max(questionLineIndex, starIndex) : Math.min(questionLineIndex, starIndex);
      if (signIndex == -1) {
        sb.append(getQuotePart(value, previousIndex, value.length()));
        break;

      }
      index = signIndex + 1;
      String regexSign = value.charAt(signIndex) == '*' ? "[^\\d\\d]*" : "[^\\d\\d]";
      if (0 == signIndex) {
        sb.append(regexSign);
        previousIndex = index;
      } else if (value.charAt(signIndex - 1) != '\\') {
        sb.append(getQuotePart(value, previousIndex, signIndex));
        sb.append(regexSign);
        previousIndex = index;
      }

    }
    return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE).pattern();
  }

  private String getQuotePart(String value, int begin, int end) {
    value = value.substring(begin, end);
    if (value == null || value.isEmpty()) {
      value = "";
    } else {
      value = Pattern.quote(convert(value));
    }
    return value;
  }

  private String convert(String value) {
    value = value.replaceAll("\\\\\\?", "?");
    return value.replaceAll("\\\\\\*", "*");

  }
}
