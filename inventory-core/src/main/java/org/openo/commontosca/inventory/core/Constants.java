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
package org.openo.commontosca.inventory.core;

import java.util.Date;

import org.openo.commontosca.inventory.sdk.api.data.ValueKey;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap.Key;

public interface Constants {

  public interface CommonKey {

    public static final Key<String> ID = new ValueKey<String>("_id", String.class);

  }

  public interface ModelKey extends CommonKey {

    public static final Key<ValueList> ATTRIBUTES = new ValueKey<>("attributes", ValueList.class);
    public static final Key<ValueMap> ATTRIBUTE = new ValueKey<>("attribute", ValueMap.class);
    public static final Key<ValueMap> PROPERTIES = new ValueKey<>("properties", ValueMap.class);
    public static final Key<String> NAME = new ValueKey<>("name", String.class);
    public static final Key<String> LABEL = new ValueKey<>("label", String.class);
    public static final Key<String> DESCRITION = new ValueKey<>("description", String.class);
    public static final Key<Boolean> ENABLE = new ValueKey<>("enable", Boolean.class);
    public static final Key<String> TYPE = new ValueKey<>("type", String.class);
    public static final Key<Boolean> VISIBLE = new ValueKey<>("visible", Boolean.class);
    public static final Key<Boolean> REQUIRED = new ValueKey<>("required", Boolean.class);
    public static final Key<Boolean> IS_ARRAY = new ValueKey<>("is-array", Boolean.class);
    public static final Key<Boolean> UNIQUE = new ValueKey<>("unique", Boolean.class);
    public static final Key<String> REF = new ValueKey<>("ref", String.class);
    public static final Key<Boolean> EDITABLE = new ValueKey<>("editable", Boolean.class);
    public static final Key<String> DISPLAY_ATTRUBITE =
        new ValueKey<>("display-attribute", String.class);
    public static final Key<Date> LAST_MODIFIED = new ValueKey<Date>("last-modified", Date.class);
    public static final Key<Date> CREATE_TIME = new ValueKey<Date>("create-time", Date.class);

  }

  public interface DataKey extends CommonKey {

    public static final Key<Date> CREATE_TIME =
        new ValueKey<Date>("inventory#create-time", Date.class);
    public static final Key<Date> LAST_MODIFIED =
        new ValueKey<Date>("inventory#last-modified", Date.class);
    public static final Key<ValueList> ERROR =
        new ValueKey<ValueList>("inventory#error", ValueList.class);

  }

}
