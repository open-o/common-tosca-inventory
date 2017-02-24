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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openo.commontosca.inventory.sdk.api.data.Converter;
import org.openo.commontosca.inventory.sdk.support.utils.SmartDateParser;

public class DateConverter implements Converter {

  private SmartDateParser parser = new SmartDateParser();

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(Class<T> type, Object value) {
    if (value == null) {
      return null;
    }
    Object result = null;
    if (type == Date.class) {
      if (value instanceof Number) {
        result = new Date(((Number) value).longValue());
      } else {
        String str = String.valueOf(value);
        try {
          result = this.parser.parse(str);
        } catch (ParseException e) {
          throw new IllegalArgumentException(e);
        }
      }
    } else if (value instanceof Date) {
      Date date = (Date) value;
      if (type == String.class) {
        SimpleDateFormat dateFormat = this.newDateFormat();
        result = dateFormat.format(date);
      } else if (type.isAssignableFrom(Long.class)) {
        result = date.getTime();
      }
    }
    return (T) result;
  }

  private SimpleDateFormat newDateFormat() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return dateFormat;
  }

}
