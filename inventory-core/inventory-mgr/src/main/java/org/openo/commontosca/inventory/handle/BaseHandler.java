/**
 * Copyright 2016 [ZTE] and others.
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

import com.google.gson.Gson;

import org.openo.commontosca.inventory.common.Pager;
import org.openo.commontosca.inventory.dao.BaseDao;
import org.openo.commontosca.inventory.dao.DaoManager;
import org.openo.commontosca.inventory.entity.db.BaseData;
import org.openo.commontosca.inventory.exception.InventoryException;
import org.openo.commontosca.inventory.util.InventoryDbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * an abstract class for wrapper class.<br>
 * provide the common methods to process the DB request
 * 
 * @author 10159474
 */
public abstract class BaseHandler<T extends BaseData> {
  private static final Logger logger = LoggerFactory.getLogger(BaseHandler.class);

  public Gson gson = new Gson();

  /**
   * query entity.
   * 
   * @param queryParam query parameter
   * @param resouceType resource type
   * @return List entity list
   * @throws InventoryException when DAO exception
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<T> query(Map<String, String> queryParam, String resouceType)
      throws InventoryException {
    logger.info("BaseHandler:start query data .info:" + InventoryDbUtil.objectToString(queryParam));
    List<T> datas = null;
    try {
      BaseDao dao = DaoManager.getInstance().getDao(resouceType);
      datas = dao.query(queryParam);

    } catch (InventoryException error) {
      logger.error("BaseHandler:error while querying " + resouceType, error);
      throw error;
    }
    logger.info("BaseHandler: query data end .info:" + InventoryDbUtil.objectToString(datas));
    return datas;
  }

  /**
   * query entity.
   * 
   * @param pager query condition
   * @param resouceType resource type
   * @return list entity
   * @throws InventoryException when DAO exception
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Pager unionSqlQuery(Pager pager, String resouceType) throws InventoryException {
    logger.info("BaseHandler:start union sql query data.fliter:" + pager.toString());
    Pager datas = null;
    try {
      BaseDao dao = DaoManager.getInstance().getDao(resouceType);
      datas = dao.unionSqlQuery(pager);

    } catch (InventoryException error) {
      logger.error("BaseHandler:error while union querying " + resouceType, error);
      throw error;
    }
    logger.info("BaseHandler:union sql query data end .info:"
        + InventoryDbUtil.objectToString(datas.getList()));
    return datas;
  }

  /**
   * query entity.
   * 
   * @param filter query condition
   * @param resouceType resource type
   * @return list entity
   * @throws InventoryException when DAO exception
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<T> unionHqlQuery(String filter, String resouceType) throws InventoryException {
    logger.info("BaseHandler:start union query data.fliter:" + filter);
    List<T> datas = null;
    try {
      BaseDao dao = DaoManager.getInstance().getDao(resouceType);
      datas = dao.unionQuery(filter);

    } catch (InventoryException error) {
      logger.error("BaseHandler:error while union querying " + resouceType, error);
      throw error;
    }
    logger.info("BaseHandler:union query data end .info:" + InventoryDbUtil.objectToString(datas));
    return datas;
  }
}
