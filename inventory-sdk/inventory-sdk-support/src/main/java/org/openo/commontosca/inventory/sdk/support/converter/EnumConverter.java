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
package org.openo.commontosca.inventory.sdk.support.converter;

import org.openo.commontosca.inventory.sdk.api.data.Converter;

public class EnumConverter implements Converter {

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public <T> T convert(Class<T> type, Object value) {
    if (value == null) {
      return null;
    }
    if (Enum.class.isAssignableFrom(type)) {
      if (value instanceof Enum) {
        return (T) value;
      } else {
        return (T) Enum.valueOf((Class<Enum>) type, String.valueOf(value));
      }
    } else if (value instanceof Enum) {
      if (type == String.class) {
        return (T) ((Enum<?>) value).name();
      }
    }
    return null;
  }

}
