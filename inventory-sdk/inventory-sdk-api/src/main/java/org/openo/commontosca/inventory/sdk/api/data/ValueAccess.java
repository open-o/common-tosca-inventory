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
package org.openo.commontosca.inventory.sdk.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class ValueAccess {

  private static List<Converter> CONVERTERS;

  static {
    ArrayList<Converter> list = new ArrayList<Converter>();
    Iterator<Converter> iter = ServiceLoader.load(Converter.class).iterator();
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    ValueAccess.CONVERTERS = list;
  }

  private final Object value;

  public ValueAccess(Object value) {
    this.value = value;
  }

  protected ValueAccess() {
    this.value = this;
  }

  public static ValueAccess wrap(Object value) {
    if (value instanceof ValueAccess) {
      return (ValueAccess) value;
    } else {
      return new ValueAccess(value);
    }
  }

  public <T> T as(Class<T> clazz) {
    return this.as(clazz, null);
  }

  public <T> T as(Class<T> clazz, T defaultValue) {
    T convert = this.convert(this.value, clazz);
    if (convert == null) {
      return defaultValue;
    } else {
      return convert;
    }
  }

  public boolean isPresent() {
    return this.value != null;
  }

  public boolean equals(Object obj) {
    return obj.equals(this.value);
  }

  public int hashCode() {
    return this.value.hashCode();
  }

  public boolean is(Class<?> clazz) {
    return clazz.isInstance(this.value);
  }

  /**
   * 
   * @return
   */
  public boolean isCollection() {
    if (this.isPresent()) {
      if (this.value instanceof Collection || this.value.getClass().isArray()) {
        return true;
      }
    }
    return false;
  }

  public boolean isMap() {
    return this.is(Map.class);
  }

  public String toString() {
    return this.value.toString();
  }

  @SuppressWarnings("unchecked")
  protected <T> T convert(Object value, Class<T> clazz) {
    if (value == null) {
      return null;
    }
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }
    if (clazz.isAssignableFrom(ValueMap.class)) {
      if (value instanceof ValueMap) {
        return clazz.cast(value);
      } else if (value instanceof Map) {
        Map<?, ?> map = (Map<?, ?>) value;
        return clazz.cast(new ValueMap((Map<String, ?>) map));
      }
    }
    if (clazz.isAssignableFrom(ValueList.class)) {
      if (value instanceof ValueList) {
        return clazz.cast(value);
      } else if (value instanceof Collection) {
        Collection<?> collection = (Collection<?>) value;
        ValueList list= new ValueList();
        if(collection.isEmpty()){
          return  clazz.cast(list);
        }else{
          list.addAll(collection);
          return clazz.cast(list);
        }
      
      }
    }
    if (clazz.isAssignableFrom(ValueAccess.class)) {
      return clazz.cast(new ValueAccess(value));
    }
    for (Converter converter : ValueAccess.CONVERTERS) {
      T convert = converter.convert(clazz, value);
      if (convert != null) {
        return convert;
      }
    }
    Object to = null;
    if (String.class == clazz) {
      to = value.toString();
    } else if (clazz.isInstance(value)) {
      to = value;
    } else {
      if (value instanceof String) {
        String str = (String) value;
        if (Byte.class == clazz) {
          to = Byte.valueOf(str);
        } else if (Short.class == clazz) {
          to = Short.valueOf(str);
        } else if (Integer.class == clazz) {
          to = Integer.valueOf(str);
        } else if (Long.class == clazz) {
          to = Long.valueOf(str);
        } else if (Float.class == clazz) {
          to = Float.valueOf(str);
        } else if (Double.class == clazz) {
          to = Double.valueOf(str);
        } else if (Boolean.class == clazz) {
          if ("true".equalsIgnoreCase(str)) {
            to = true;
          } else if ("false".equalsIgnoreCase(str)) {
            to = false;
          } else {
            throw new IllegalArgumentException("Can not convert to boolean: " + str);
          }
        } else if (Number.class == clazz) {
          try {
            to = Long.valueOf(str);
          } catch (NumberFormatException ex) {
            to = Double.valueOf(str);
          }
        }
      } else if (value instanceof Number) {
        Number num = (Number) value;
        if (Byte.class == clazz) {
          to = num.byteValue();
        } else if (Short.class == clazz) {
          to = num.shortValue();
        } else if (Integer.class == clazz) {
          to = num.intValue();
        } else if (Long.class == clazz) {
          to = num.longValue();
        } else if (Float.class == clazz) {
          to = num.floatValue();
        } else if (Double.class == clazz) {
          to = num.doubleValue();
        } else if (Boolean.class == clazz) {
          to = num.intValue() != 0;
        }
      }
    }
    if (to == null) {
      throw new IllegalArgumentException("Can not convert " + value.getClass() + " to " + clazz);
    }
    return clazz.cast(to);
  }
}
