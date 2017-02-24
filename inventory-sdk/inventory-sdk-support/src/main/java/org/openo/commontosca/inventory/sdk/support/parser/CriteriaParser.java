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
package org.openo.commontosca.inventory.sdk.support.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.Group;
import org.openo.commontosca.inventory.sdk.api.Criteria.OP;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;

public class CriteriaParser {
  /**
   * 
   * @param map map
   * @return Criteria
   */
  public static Criteria parseValueMapCriteria(ValueMap map) {
    if (map == null || map.isEmpty()) {
      return new DefaultCriteria();
    }
    if (!isCriteria(map)) {
      return parseSingleValueMapCriteria(map);
    }
    Criteria criteria = new DefaultCriteria();
    Group group = getGroup(map);
    criteria.setGroup(group);
    List<Map<String, Object>> list = getGroupList(map);
    for (Map<String, Object> groupValueMap : list) {
      criteria.add(parseValueMapCriteria(ValueMap.wrap(groupValueMap)));
    }
    return criteria;
  }

  @SuppressWarnings("unchecked")
  private static List<Map<String, Object>> getGroupList(ValueMap map) {
    Set<String> keys = map.keySet();

    if (keys.size() > 1) {
      List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
      for (String key : keys) {
        ValueMap subMap = new ValueMap();
        subMap.put(key, map.get(key));
        result.add(subMap);

      }
      return result;
    } else {
      return (List<Map<String, Object>>) map.get(keys.iterator().next());
    }

  }

  private static Group getGroup(ValueMap map) {
    Set<String> keys = map.keySet();
    if (keys.size() > 1) {
      return Group.AND;
    }
    String key = keys.iterator().next();
    if ("$and".equals(key)) {
      return Group.AND;
    } else {
      return Group.OR;
    }
  }

  private static Criteria parseSingleValueMapCriteria(ValueMap map) {
    Criteria criteria = new DefaultCriteria();
    OP op = null;
    boolean isNot = false;
    String name = map.keySet().iterator().next();
    ValueAccess value = new ValueAccess(map.get(name));
    Object criteriaValue = null;
    if (value.is(Map.class)) {
      ValueMap subMap = value.as(ValueMap.class);
      String subKey = subMap.keySet().iterator().next();
      if ("$not".equals(subKey)) {
        isNot = true;
        subMap = subMap.requireMap("$not");
        subKey = subMap.keySet().iterator().next();
      }
      if (isNot(subKey)) {
        isNot = !isNot;
      }
      op = getOpFromString(subKey);
      criteriaValue = subMap.get(subKey);
    } else {
      if (isRegex(value)) {
        op = OP.LIKE;
      } else {
        op = OP.EQ;
      }
      criteriaValue = value;
    }
    if (isNot) {
      criteria.not();
    }
    return criteria.setCriterion(name, op, criteriaValue);
  }

  private static OP getOpFromString(String op) {
    if ("$eq".equals(op) || "$ne".equals(op)) {
      return OP.EQ;
    }
    if ("$lt".equals(op)) {
      return OP.LT;
    }
    if ("$lte".equals(op)) {
      return OP.LTE;
    }
    if ("$gt".equals(op)) {
      return OP.GT;
    }
    if ("$gte".equals(op)) {
      return OP.GTE;
    }
    if ("$regex".equals(op) || "$like".equals(op)) {
      return OP.LIKE;
    }
    if ("$in".equals(op) || "$nin".equals(op)) {
      return OP.IN;
    }
    throw new CriteriaParseException("Cant convert " + op + " to Operator!");
  }

  private static boolean isNot(String op) {
    boolean not = false;
    if ("$ne".equals(op) || "$nin".equals(op)) {
      not = true;
    }
    return not;
  }

  private static boolean isRegex(Object value) {
    boolean isRegex = (value instanceof Pattern) || (value.toString().startsWith("(?i)"));
    return isRegex;
  }

  private static boolean isCriteria(ValueMap map) {
    Set<String> keys = map.keySet();
    if (keys.size() > 1) {
      throw new CriteriaParseException("Cant convert " + map + " to criteria!");
    }
    String key = keys.iterator().next();
    if ("$and".equals(key) || "$or".equals(key)) {
      return true;
    }
    return false;
  }
}
