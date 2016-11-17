/**
 * Copyright 2016 ZTE Corporation.
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

package org.openo.commontosca.inventory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class HqlFactory {

  private static final Logger logger = LoggerFactory.getLogger(HqlFactory.class);

  /**
   * get update hql.
   * 
   * @param obj the object that used to be generate the hql
   * @param excludeProperties the properties that need not to be used
   * @param filter the condition after "where"
   * @return hibernate hql
   */
  public static String getUpdateHql(Object obj, String[] excludeProperties, String filter) {
    StringBuffer hql = new StringBuffer();
    String objName = obj.getClass().getSimpleName();
    hql.append("update ");
    hql.append(objName);
    hql.append(" set ");
    Field[] fields = obj.getClass().getDeclaredFields();
    if (obj.getClass().getGenericSuperclass() != null) {
      Field[] parentFields = obj.getClass().getSuperclass().getDeclaredFields();
      fields = concat(fields, parentFields);
    }
    for (Field field : fields) {
      String name = field.getName();
      Method method = null;
      Object value = null;
      if (!contain(excludeProperties, name)) {
        String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
          method = obj.getClass().getMethod("get" + upperName);
          value = method.invoke(obj);
          if (value != null) {
            if (value instanceof String) {
              hql.append(name);
              hql.append("=");
              hql.append("'");
              hql.append(value);
              hql.append("'");
              hql.append(",");
            } else {
              hql.append(name);
              hql.append("=");
              hql.append(value);
              hql.append(",");
            }
          }
        } catch (Exception error) {
          logger.error("error while creating update hql", error);
        }
      }
    }

    String sql = hql.toString();
    sql = sql.substring(0, sql.lastIndexOf(","));
    if (filter != null) {
      sql = sql + " where " + filter;
    }
    logger.info("update hql is : " + sql);
    return sql;
  }

  /**
   * identify whether or not to include target string in source string.
   */
  public static boolean contain(String[] src, String target) {
    if (src == null || src.length == 0 || target == null) {
      return false;
    } else {
      for (String str : src) {
        if (str.equals(target)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * concat str.
   */
  public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static String getOidFilter(String key, String value) {
    return key + "= '" + value + "'";
  }

}
