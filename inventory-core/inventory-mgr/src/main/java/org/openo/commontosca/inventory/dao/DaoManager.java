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

package org.openo.commontosca.inventory.dao;

import org.hibernate.SessionFactory;
import org.openo.commontosca.inventory.common.InventoryResuorceType;
import org.openo.commontosca.inventory.exception.InventoryException;

/**
 * DAO manager class.<br>
 * a class to store DAO instances and provide methods to get these instances
 * 
 *
 */
public class DaoManager {
  private static DaoManager instance = new DaoManager();

  private ServiceInstanceDao serviceInstanceDao;
  private ServiceInputParamDao serviceInputParamDao;
  private SessionFactory sessionFactory;

  private DaoManager() {}

  public static synchronized DaoManager getInstance() {
    return instance;
  }

  /**
   * according to resource type and return proper DAO.
   * 
   * @param type resource Type
   * @return DAO
   */
  public BaseDao<?> getDao(String type) throws InventoryException {
    if (sessionFactory == null) {
      throw new InventoryException("", "errorMsg:database connect init faild!");
    }
    switch (InventoryResuorceType.getType(type)) {
      case ServiceInstance:
        return getServiceInstanceDao();
      case ServiceInputParam:
        return getServiceInputParamDao();
      default:
        return null;
    }
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @return Returns the service dao.
   */
  public ServiceInstanceDao getServiceInstanceDao() {
    // if (serviceInstanceDao == null) {
    serviceInstanceDao = new ServiceInstanceDao(sessionFactory);
    // }
    return serviceInstanceDao;
  }

  public void setServiceDao(ServiceInstanceDao serviceInstanceDao) {
    this.serviceInstanceDao = serviceInstanceDao;
  }

  public void setServiceInputParamDao(ServiceInputParamDao serviceInputParamDao) {
    this.serviceInputParamDao = serviceInputParamDao;
  }

  /**
   * @return Returns the service input param dao.
   */
  public ServiceInputParamDao getServiceInputParamDao() {
    // if (serviceInputParamDao == null) {
    serviceInputParamDao = new ServiceInputParamDao(sessionFactory);
    // }
    return this.serviceInputParamDao;
  }
}
