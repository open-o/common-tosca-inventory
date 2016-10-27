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
package org.openo.commontosca.inventory.handle;

import org.openo.commontosca.inventory.common.InventoryResuorceType;
import org.openo.commontosca.inventory.common.Pager;
import org.openo.commontosca.inventory.entity.db.ServiceBaseData;
import org.openo.commontosca.inventory.entity.db.ServiceInputParamData;
import org.openo.commontosca.inventory.entity.db.ServicePackageMappingData;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceInfo;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceQueryCondition;
import org.openo.commontosca.inventory.entity.rest.Sort;
import org.openo.commontosca.inventory.exception.InventoryException;
import org.openo.commontosca.inventory.util.InventoryDbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServiceInstanceHandler extends BaseHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInstanceHandler.class);

  /**
   * query instance list by condition.
   */
  public ArrayList<ServiceInstanceInfo> getServiceInstanceByCondition(
      ServiceInstanceQueryCondition condition) throws InventoryException {
    LOGGER.info("start handle query serviceInstance by condition.");
    ArrayList<ServiceInstanceInfo> instances = new ArrayList<ServiceInstanceInfo>();
    HashMap<String, ServiceInstanceInfo> instancesMap = new HashMap<String, ServiceInstanceInfo>();
    StringBuffer serviceIds = new StringBuffer();
    Pager pager = queryServiceBaseInfo(condition);
    if (pager.getList() == null || pager.getList().size() <= 0) {
      LOGGER.warn("query serviceInstance by condition.size:0");
      return instances;
    }
    LOGGER.info("serviceInstanceBase info.size:" + pager.getList().size());
    List serviceBaseInfoList = pager.getList();
    for (int i = 0; i < serviceBaseInfoList.size(); i++) {
      Object obj = serviceBaseInfoList.get(i);
      ServiceInstanceInfo instance = new ServiceInstanceInfo();
      if (obj instanceof Object[]) {
        Object[] objs = (Object[]) obj;
        for (int j = 0; j < objs.length; j++) {
          if (objs[j] instanceof ServiceBaseData) {
            ServiceBaseData base = (ServiceBaseData) objs[j];
            instance.setInstanceBaseInfor(base);
            instancesMap.put(instance.getServiceId(), instance);
            serviceIds.append(instance.getServiceId() + ",");
            instances.add(instance);
          } else if (objs[j] instanceof ServicePackageMappingData) {
            ServicePackageMappingData packageMapping = (ServicePackageMappingData) objs[j];
            instance.setTemplateName(packageMapping.getTemplateName());
          }
        }
      }
    }
    ArrayList<ServiceInputParamData> serviceInputParams =
        queryServiceInputParam(serviceIds.substring(0, serviceIds.length() - 1));
    if (serviceInputParams != null) {
      for (int k = 0; k < serviceInputParams.size(); k++) {
        ServiceInstanceInfo instance = instancesMap.get(serviceInputParams.get(k).getServiceId());
        if (instance != null) {
          instance.setInputParam(serviceInputParams.get(k));
        }
      }
    }
    LOGGER.info(" handle query serviceInstance by condition end.infor:"
        + InventoryDbUtil.objectToString(instances));
    return instances;
  }

  private Pager queryServiceBaseInfo(ServiceInstanceQueryCondition condition)
      throws InventoryException {
    LOGGER.info("start query serviceInstance Base infor.");
    StringBuffer queryStr = new StringBuffer();
    queryStr = initQueryBaseInfo(condition);
    initQueryFilter(condition, queryStr);
    HashMap<String, Class> mapping = new HashMap<String, Class>();
    mapping.put("s", ServiceBaseData.class);
    mapping.put("p", ServicePackageMappingData.class);
    Pager pager = new Pager();
    pager.setDbTableMapping(mapping);
    pager.setHql(queryStr.toString());
    pager.setPage(condition.getPagination());
    pager.setRows(condition.getPagesize());
    LOGGER.info("query serviceInstance Base infor.conditon:" + pager.toString());
    pager = unionSqlQuery(pager, InventoryResuorceType.ServiceInstance.name());
    LOGGER.info("query serviceInstance Base infor end.infor:"
        + InventoryDbUtil.objectToString(pager.getList()));
    return pager;
  }

  private StringBuffer initQueryBaseInfo(ServiceInstanceQueryCondition condition) {
    LOGGER.info("init query serviceBaseInfor SQL.");
    StringBuffer queryStr = new StringBuffer();
    queryStr.append("select s.serviceId,s.serviceName,s.serviceType,s.description,s.activeStatus,"
        + "s.status ,s.creator,s.createTime , p.serviceDefId,p.templateId," + "p.templateName ");
    queryStr.append(
        " from  t_lcm_servicebaseinfo as s  left outer join t_lcm_defPackage_mapping as  p"
            + " on s.serviceId=p.serviceId ");
    return queryStr;
  }

  private void initQueryFilter(ServiceInstanceQueryCondition condition, StringBuffer queryStr) {
    LOGGER.info("init query serviceBaseInfor SQL filter.");
    StringBuffer filterStr = new StringBuffer();
    if (InventoryDbUtil.isNotEmpty(condition.getServiceId())) {
      queryStr.append(" where s.serviceId='" + condition.getServiceId() + "'");
    }
    Iterator<String> it = condition.getCondition().keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      String value = condition.getCondition().get(key);
      filterStr.append(" and " + key);
      filterStr.append(" = '" + value + "' ");
    }
    if (filterStr.length() > 0) {
      if (!queryStr.toString().contains("where")) {
        queryStr.append(" where ");
        queryStr.append(
            filterStr.toString().substring(filterStr.indexOf("and") + 3, filterStr.length()));
      } else {
        queryStr.append(filterStr.toString());
      }
    }
    if (condition.getSort().size() <= 0) {
      Sort sort = new Sort();
      sort.setFieldName("createTime");
      sort.setDirection("DESC");
      condition.addSort(sort);
    }
    StringBuffer orderStr = new StringBuffer();
    for (int i = 0; i < condition.getSort().size(); i++) {
      orderStr.append(" " + condition.getSort().get(i).getFieldName());
      orderStr.append(" " + condition.getSort().get(i).getDirection() + ",");
    }
    queryStr.append("  order by" + orderStr.substring(0, orderStr.length() - 1));

  }

  private ArrayList<ServiceInputParamData> queryServiceInputParam(String filter)
      throws InventoryException {
    LOGGER.info(" query serviceInstance inputparam infor.filter:" + filter);
    StringBuffer queryStr = new StringBuffer();
    queryStr.append("from ServiceInputParamData where serviceId in (");
    queryStr.append(filter);
    queryStr.append(")");
    LOGGER.info(" query serviceInstance HQL:" + queryStr.toString());
    Object obj = unionHqlQuery(queryStr.toString(), InventoryResuorceType.ServiceInputParam.name());
    if (obj != null) {
      LOGGER.info(
          " query serviceInstance inputparam end.infor:" + InventoryDbUtil.objectToString(obj));
      return (ArrayList<ServiceInputParamData>) obj;
    } else {
      LOGGER.info(" query serviceInstance inputparam end.size:0");
      return null;
    }

  }

}
