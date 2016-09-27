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
package org.openo.commontosca.inventory.db.resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.commontosca.inventory.dao.DaoManager;
import org.openo.commontosca.inventory.db.util.H2DbServer;
import org.openo.commontosca.inventory.db.util.HibernateSession;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceInfo;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceQueryCondition;
import org.openo.commontosca.inventory.entity.rest.Sort;
import org.openo.commontosca.inventory.exception.InventoryException;
import org.openo.commontosca.inventory.handle.ServiceInstanceHandler;


import java.util.HashMap;
import java.util.List;

public class ServiceInstanceManagerTest {
  private static ServiceInstanceHandler handler;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    H2DbServer.startUp();
    handler = new ServiceInstanceHandler();
  }

  /**
   * shut down db.
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    try {
      HibernateSession.destory();
      H2DbServer.shutDown();
    } catch (Exception error) {
      Assert.fail("Exception" + error.getMessage());
    }
  }

  /**
   * init db data.
   */
  @Before
  public void setUp() throws Exception {
    DaoManager.getInstance().setSessionFactory(HibernateSession.init());
  }

  /**
   * clear db data.
   */
  @After
  public void tearDown() {

  }


  @Test
  public void testQueryAllServiceInstance_pageSize_2_page_1_return_2_records() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    condition.setPagination(1);
    condition.setPagesize(2);
    List<ServiceInstanceInfo> list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 2);
  }

  @Test
  public void testQueryAllServiceInstance_pageSize_2_page_2_return_2_records() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    condition.setPagination(1);
    condition.setPagesize(2);
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 2);
  }

  @Test
  public void testQueryAllServiceInstance_pageSize_2_page_4_return_0_records() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    condition.setPagination(4);
    condition.setPagesize(2);
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 0);
  }

  @Test
  public void testQueryAllServiceInstance_pageSize_default_page_default_return_4_records() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    condition = new ServiceInstanceQueryCondition();
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 4);
  }

  @Test
  public void testQueryServiceInstance_serviceId_10001_return_1_records() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    condition = new ServiceInstanceQueryCondition();
    condition.setServiceId("10001");
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 1);
  }

  @Test
  public void testQueryServiceInstance_orderby_createTime() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    Sort sort = new Sort();
    sort.setDirection("DESC");
    sort.setFieldName("createTime");
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    for (int i = 0; i < list.size(); i++) {
      if (!list.get(i).getServiceId().equals("1000" + (i + 1))) {
        Assert.fail("order by createTime DESC faild");
        return;
      }
    }
    Assert.assertTrue(true);
  }

  @Test
  public void testQueryServiceInstance_serviceName_serviceName3() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    List<ServiceInstanceInfo> list = null;
    HashMap<String, String> queryCondition = new HashMap<String, String>();
    queryCondition.put("serviceName", "serviceName3");
    condition.setCondition(queryCondition);
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 1);
  }

  @Test
  public void testQueryServiceInstance_serviceName_serviceName3_serviceId_10003() {
    ServiceInstanceQueryCondition condition = new ServiceInstanceQueryCondition();
    condition.setServiceId("10003");
    List<ServiceInstanceInfo> list = null;
    HashMap<String, String> queryCondition = new HashMap<String, String>();
    queryCondition.put("serviceName", "serviceName3");
    condition.setCondition(queryCondition);
    list = null;
    try {
      list = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      Assert.fail("Exception" + error.getMessage());
      return;
    }
    Assert.assertTrue(list.size() == 1);
  }
}
