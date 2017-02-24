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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.openo.commontosca.inventory.sdk.api.data.Converter;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;

public class ListConverter implements Converter {

  public static final String ARRAY_SEPERATOR = ";";

  @Override
  public <T> T convert(Class<T> type, Object value) {
    if (type.isAssignableFrom(ValueList.class)) {
      if (value instanceof List) {
        return ValueAccess.wrap(value).as(type);
      } else if (value instanceof Collection) {
        List<?> list = new ArrayList<Object>((Collection<?>) value);
        return ValueList.wrap(list).as(type);
      } else if (value.getClass().isArray()) {
        try {
          List<?> list = Arrays.asList((Object[]) value);
          return ValueList.wrap(list).as(type);
        } catch (Exception ignore) {
          int length = Array.getLength(value);
          List<Object> list = new ArrayList<Object>(length);
          for (int i = 0; i < length; i++) {
            list.add(Array.get(value, i));
          }
          return ValueList.wrap(list).as(type);
        }
      } else if (value instanceof CharSequence) {
        String str = value.toString();
        StringTokenizer st = new StringTokenizer(str, ListConverter.ARRAY_SEPERATOR);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
          String token = st.nextToken().trim();
          tokens.add(token);
        }
        return ValueList.wrap(tokens).as(type);
      }
    } else if (value instanceof Collection) {
      if (type == String.class) {
        StringBuilder builder = new StringBuilder();
        Collection<?> list = (Collection<?>) value;
        if (!list.isEmpty()) {
          for (Object object : list) {
            builder.append(ValueAccess.wrap(object).as(String.class, ""));
            builder.append(ListConverter.ARRAY_SEPERATOR);
          }
          builder.setLength(builder.length() - ListConverter.ARRAY_SEPERATOR.length());
        }
        return ValueAccess.wrap(builder).as(type);
      }
    }
    return null;
  }

}
