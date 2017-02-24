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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public class ModelTypeVerifier extends AbstractVerifier<ValueMap> {

  public ModelTypeVerifier(ValueMap model) {
    super(model);
    this.policy(StrictPolicy.DEFINE_INSERT_DATA | StrictPolicy.FAIL_ON_VERIFY);
  }


  @Override
  public ValueMap verify() throws VerifyException {
    if (StrictPolicy.DEFINE_UPDATE_DATA == this.policy) {
      verifyUpdate();
    } else {
      verifyInsert();
    }

    return this.strict;
  }

  private void verifyInsert() {
    this.strict = new ValueMap();
    if (!this.origin.optValue(CommonKey.ID, "").isEmpty()) {
      this.strict.put(CommonKey.ID, this.origin.requireValue(CommonKey.ID));
    }
    if (!this.verifyField(this.origin.requireValue(ModelKey.NAME))) {
      this.fail(ModelKey.NAME.getKeyName(), this.origin.requireValue(ModelKey.NAME),
          ValueError.ILLEGAL_FIELD);
    }
    this.strict.put(ModelKey.ATTRIBUTES,
        this.verifyAttributes(this.origin.requireValue(ModelKey.ATTRIBUTES)));
    this.strict.put(ModelKey.NAME, this.origin.requireValue(ModelKey.NAME));
    this.strict.put(ModelKey.ENABLE, this.origin.optValue(ModelKey.ENABLE, true));
    this.strict.put(ModelKey.LABEL,
        this.origin.optValue(ModelKey.LABEL, this.origin.requireValue(ModelKey.NAME)));
    this.strict.put(ModelKey.DESCRITION, this.origin.optValue(ModelKey.DESCRITION));
    this.strict.put(ModelKey.DISPLAY_ATTRUBITE, this.origin.optValue(ModelKey.DISPLAY_ATTRUBITE));
  }

  private void verifyUpdate() {
    this.strict = new ValueMap();
    if (!this.origin.optValue(CommonKey.ID, "").isEmpty()) {
      this.strict.put(CommonKey.ID, this.origin.requireValue(CommonKey.ID));
    }

    if (!this.origin.optValue(CommonKey.ID, "").isEmpty()
        && !this.verifyField(this.origin.requireValue(ModelKey.NAME))) {
      this.fail(ModelKey.NAME.getKeyName(), this.origin.requireValue(ModelKey.NAME),
          ValueError.ILLEGAL_FIELD);
    }
    if (this.origin.optValue(ModelKey.ATTRIBUTES) != null
        && !this.origin.optValue(ModelKey.ATTRIBUTES).isEmpty()) {
      this.strict.put(ModelKey.ATTRIBUTES,
          this.verifyAttributes(this.origin.requireValue(ModelKey.ATTRIBUTES)));
    }
    if (!this.origin.optValue(ModelKey.NAME, "").isEmpty()) {
      this.strict.put(ModelKey.NAME, this.origin.requireValue(ModelKey.NAME));
    }
    if (this.origin.containsKey(ModelKey.ENABLE)) {
      this.strict.put(ModelKey.ENABLE, this.origin.optValue(ModelKey.ENABLE));
    }
    if (!this.origin.optValue(ModelKey.LABEL, "").isEmpty()) {
      this.strict.put(ModelKey.LABEL, this.origin.optValue(ModelKey.LABEL));
    }
    if (!this.origin.optValue(ModelKey.DESCRITION, "").isEmpty()) {
      this.strict.put(ModelKey.DESCRITION, this.origin.optValue(ModelKey.DESCRITION));
    }
    if (!this.origin.optValue(ModelKey.DISPLAY_ATTRUBITE, "").isEmpty()) {

      this.strict.put(ModelKey.DISPLAY_ATTRUBITE, this.origin.optValue(ModelKey.DISPLAY_ATTRUBITE));
    }
    if (strict.isEmpty()) {
      this.fail("", "", ValueError.MODEL_UPDATE_EXCEPTION);
    }

  }


  private ValueList verifyAttributes(ValueList attributes) {
    ValueList strictAttributes = new ValueList(new ArrayList<>(attributes.size()));
    Set<String> names = new HashSet<>();
    Set<String> labels = new HashSet<>();
    boolean hashId = false;
    for (Object item : attributes) {

      ValueMap strictAttribute = new ValueMap();
      ValueMap attribute = ValueAccess.wrap(item).as(ValueMap.class);
      if (ModelKey.ID.equals(attribute.requireValue(ModelKey.NAME))) {
        hashId = true;
      }
      if (!verifyField(attribute.requireValue(ModelKey.NAME))) {
        this.fail(ModelKey.NAME.getKeyName(), attribute.requireValue(ModelKey.NAME),
            ValueError.ILLEGAL_FIELD);
      }
      if (!names.add(attribute.requireValue(ModelKey.NAME))) {
        this.fail(ModelKey.NAME.getKeyName(), attribute.requireValue(ModelKey.NAME),
            ValueError.SAME_NAME_FIELD);
      }
      if (!labels.add(attribute.requireValue(ModelKey.LABEL))) {
        this.fail(ModelKey.LABEL.getKeyName(), attribute.requireValue(ModelKey.LABEL),
            ValueError.SAME_LABEL_FIELD);
      }
      if (!this.verifyField(attribute.requireValue(ModelKey.NAME))) {
        this.fail(ModelKey.NAME.getKeyName(), attribute.requireValue(ModelKey.NAME),
            ValueError.ILLEGAL_FIELD);
      }
      String attributeName = attribute.requireValue(ModelKey.NAME);
      strictAttribute.put(ModelKey.NAME, attributeName);
      strictAttribute.put(ModelKey.LABEL,
          attribute.optValue(ModelKey.LABEL, attribute.requireValue(ModelKey.NAME)));
      if (attributeName.equals(CommonKey.ID.getKeyName())) {
        strictAttribute.put(ModelKey.TYPE, ValueType.STRING.getTypeString());
        strictAttribute.put(ModelKey.VISIBLE, true);
        strictAttribute.put(ModelKey.EDITABLE, false);
        strictAttribute.put(ModelKey.ENABLE, true);
        strictAttribute.put(ModelKey.REQUIRED, true);
        strictAttribute.put(ModelKey.IS_ARRAY, false);
        strictAttribute.put(ModelKey.UNIQUE, true);
      } else {
        strictAttribute.put(ModelKey.TYPE, attribute.requireValue(ModelKey.TYPE));
        strictAttribute.put(ModelKey.VISIBLE, attribute.optValue(ModelKey.VISIBLE, true));
        strictAttribute.put(ModelKey.EDITABLE, attribute.optValue(ModelKey.EDITABLE, true));
        strictAttribute.put(ModelKey.ENABLE, attribute.optValue(ModelKey.ENABLE, true));
        strictAttribute.put(ModelKey.REQUIRED, attribute.optValue(ModelKey.REQUIRED, false));
        strictAttribute.put(ModelKey.IS_ARRAY, attribute.optValue(ModelKey.IS_ARRAY, false));
        strictAttribute.put(ModelKey.UNIQUE, attribute.optValue(ModelKey.UNIQUE, false));
        strictAttribute.put(ModelKey.PROPERTIES,
            attribute.optValue(ModelKey.PROPERTIES, ValueMap.wrap(Collections.emptyMap())));
        switch (ValueType.parse(attribute.requireValue(ModelKey.TYPE))) {
          case REFERENCE: {
            strictAttribute.put(ModelKey.REF, attribute.requireValue(ModelKey.REF));
            break;
          }
          default: {
            break;
          }
        }
      }
      strictAttributes.add(strictAttribute);
    }
    if (!hashId) {
      this.fail(ModelKey.NAME.getKeyName(), "", ValueError.MISSIND_ID_FIELD);
    }
    return strictAttributes;
  }

  private boolean verifyField(String value) {
    String regex = "^[a-zA-Z0-9_]{1,30}$";
    return Pattern.matches(regex, value);
  }

}
