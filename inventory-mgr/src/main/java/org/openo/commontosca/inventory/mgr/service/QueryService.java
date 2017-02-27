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
package org.openo.commontosca.inventory.mgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.mgr.db.DBUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);
  private static final String BASESQL =
      "select s.serviceId,s.serviceName,s.serviceType,s.description,s.activeStatus,"
          + "s.status ,s.creator,s.createTime , p.serviceDefId,p.templateId,p.templateName"
          + " from  t_lcm_servicebaseinfo as s  , t_lcm_defPackage_mapping as  p"
          + " where s.serviceId=p.serviceId ";
  private static final String INPUTPARAMSQL =
      "select serviceId,inputKey,inputValue from t_lcm_inputParam_mapping where serviceId in (%s) ";

  public List<Map<String, Object>> queryServiceFullInfo(RequestEntity queryRequest) {
    List<Map<String, Object>> serviceInfos = new ArrayList<Map<String, Object>>();
    Map<String, ResponseEntity> serviceMap = new HashMap<String, ResponseEntity>();
    Handle dbHandle = null;
    try {
      dbHandle = DBUtils.getHandle();
      queryBasicServiceInfo(queryRequest, serviceMap, dbHandle);
      queryServiceInputParam(dbHandle, serviceMap);
      for (ResponseEntity e : serviceMap.values()) {
        serviceInfos.add(e.toMap());
      }
    } finally {
      DBUtils.close(dbHandle);
    }
    return serviceInfos;
  }

  private void queryBasicServiceInfo(RequestEntity queryRequest,
      Map<String, ResponseEntity> serviceMap, Handle dbHandle) {
    String whereStr = parseWhere(queryRequest);
    String orderStr = parseOrder(queryRequest);
    String pageStr = parsePage(queryRequest);
    StringBuffer sb = new StringBuffer();
    String querySql =
        sb.append(BASESQL).append(whereStr).append(orderStr).append(pageStr).toString();
    Query<Map<String, Object>> query = dbHandle.createQuery(querySql);
    if (queryRequest.getServiceID() != null) {
      query.bind("serviceId", queryRequest.getServiceID());
    }

    LOGGER.info("queryservice sql={}, {}", querySql, queryRequest.getServiceID());
    for (Map<String, Object> data : query.list()) {
      ResponseEntity entity = new ResponseEntity();
      entity.fromMap(data);
      serviceMap.put(entity.getServiceId(), entity);
    }
  }

  private void queryServiceInputParam(Handle dbHandle, Map<String, ResponseEntity> serviceMap) {
    if (serviceMap.isEmpty()) {
      return;
    }
    String strServiceIds = "";
    Iterator<String> it = serviceMap.keySet().iterator();
    StringBuffer serviceIds = new StringBuffer();
    while (it.hasNext()) {
      serviceIds.append("'" + it.next() + "',");
    }
    if (!serviceIds.toString().isEmpty()) {
      strServiceIds = serviceIds.substring(0, serviceIds.length() - 1).toString();
    }
    String sql = String.format(INPUTPARAMSQL, strServiceIds);
    Query<Map<String, Object>> query = dbHandle.createQuery(sql);
    LOGGER.info("queryserviceinputparam sql={}", sql);
    for (Map<String, Object> data : query.list()) {
      String serviceId = (String) data.get("serviceId");
      String inputKey = (String) data.get("inputKey");
      String inputValue = (String) data.get("inputValue");
      ResponseEntity response = serviceMap.get(serviceId);
      response.addInputParam(inputKey, inputValue);
    }
  }

  private String parsePage(RequestEntity condition) {
    StringBuffer pageStr = new StringBuffer();
    pageStr.append(" " + (condition.getPageNum() - 1) * condition.getPageSize() + ","
        + condition.getPageSize());
    return " limit " + pageStr.toString();
  }

  @SuppressWarnings("rawtypes")
  private String parseOrder(RequestEntity condition) {
    StringBuffer order = new StringBuffer();
    StringBuffer orderStr = new StringBuffer();
    for (int i = 0; i < condition.getSort().size(); i++) {
      orderStr.append(" " + ((Map) condition.getSort().get(i)).get("fieldName"));
      orderStr.append(" " + ((Map) condition.getSort().get(i)).get("direction") + ",");
    }
    if (!orderStr.toString().isEmpty()) {
      order.append("  order by" + orderStr.substring(0, orderStr.length() - 1));
    }
    return order.toString();
  }

  private String parseWhere(RequestEntity condition) {
    StringBuffer whereStr = new StringBuffer();
    if (condition.getServiceID() != null) {
      whereStr.append(" and s.serviceId=:serviceId");
    }
    Iterator<String> it = condition.getCondition().keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      String value = (String) condition.getCondition().get(key);
      whereStr.append(" and " + key);
      whereStr.append(" = '" + value + "' ");
    }
    return whereStr.toString();
  }

}
