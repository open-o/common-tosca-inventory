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

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.util.Generics;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openo.commontosca.inventory.common.Pager;
import org.openo.commontosca.inventory.exception.InventoryException;
import org.openo.commontosca.inventory.util.HqlFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * a base class for Hibernate DAO classes.<br>
 * provide the common methods to create,delete,update and query data
 * 
 * @author 10159474
 * 
 */
public class BaseDao<T> extends AbstractDAO<T> {

  /**
   * init session.
   * 
   * @param sessionFactory session Factory
   */
  public BaseDao(SessionFactory sessionFactory) {
    super(sessionFactory);
    this.sessionFactory = sessionFactory;
    this.entityClass = Generics.getTypeParameter(getClass());
  }

  public String[] excludeProperties;
  private SessionFactory sessionFactory;
  protected Session session;
  private final Class<?> entityClass;

  @Override
  protected Session currentSession() {
    return this.session;
  }

  /**
   * update entity .
   * 
   * @param data the object to update
   * @throws InventoryException when db abnormal
   */
  public void update(T data, String filter) throws InventoryException {
    try {
      String hql = HqlFactory.getUpdateHql(data, excludeProperties, filter);
      beginTransaction();
      Query query = this.session.createQuery(hql);
      query.executeUpdate();
      closeTransaction();
    } catch (Exception error) {
      transactionRollBack();
      throw new InventoryException("", "error while updating data.errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
  }

  /**
   * delete entity.
   * 
   * @param data the object to delete
   * @throws InventoryException when db abnormal
   */
  public void delete(T data) throws InventoryException {
    try {
      beginTransaction();
      this.session.delete(data);
      closeTransaction();
    } catch (Exception error) {
      transactionRollBack();
      throw new InventoryException("", "error while deleting data.errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
  }

  /**
   * create entity.
   * 
   * @param data the object to create
   * @return T
   * @throws InventoryException when db abnormal
   * 
   */
  public T create(T data) throws InventoryException {
    try {
      beginTransaction();
      session.save(data);
      closeTransaction();
    } catch (HibernateException error) {
      transactionRollBack();
      throw new InventoryException("", "error while creating data.errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
    return data;
  }

  /**
   * query entity by condition.
   * 
   * @param unionHql query condition.
   * @return T
   * @throws InventoryException when db abnormal
   * 
   */
  public List<T> unionQuery(String unionHql) throws InventoryException {
    List<T> data;
    try {
      beginTransaction();
      Query query = this.session.createQuery(unionHql);
      data = query.list();
      closeTransaction();
    } catch (Exception error) {
      transactionRollBack();
      throw new InventoryException("",
          "error while union query data." + "errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
    return data;
  }

  /**
   * query entity by condition.
   * 
   * @param pager query condition.
   * @return T
   * @throws InventoryException when db abnormal
   * 
   */
  public Pager unionSqlQuery(Pager pager) throws InventoryException {
    List<T> data;
    try {
      beginTransaction();
      SQLQuery sqlQuery = this.session.createSQLQuery(pager.getHql());
      Iterator<String> it = pager.getDbTableMapping().keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        Class cls = pager.getDbTableMapping().get(key);
        sqlQuery.addEntity(key, cls);
      }
      if (pager.getPage() != -1) {
        sqlQuery.setCacheable(true);
        int count = sqlQuery.list().size();
        pager.setRowsTotal(count);
        sqlQuery.setMaxResults(pager.getRows());
        sqlQuery.setFirstResult((pager.getPage() - 1) * pager.getRows());
      }
      data = sqlQuery.list();
      pager.setList(data);
      closeTransaction();
    } catch (Exception error) {
      transactionRollBack();
      throw new InventoryException("", "error while union query data." + "errorMsg:"
          + error.getMessage() + ":cause:" + error.getCause().getMessage());
    } finally {
      closeSession();
    }
    return pager;
  }

  /**
   * delete entity by condition.
   * 
   * @param unionHql delete condition.
   * @return T
   * @throws InventoryException when db abnormal
   * 
   */
  public int unionDelete(String unionHql) throws InventoryException {
    int num = 0;
    try {
      beginTransaction();
      Query query = this.session.createQuery(unionHql);
      num = query.executeUpdate();
      closeTransaction();
    } catch (Exception error) {
      transactionRollBack();
      throw new InventoryException("",
          "error while union query data." + "errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
    return num;
  }

  /**
   * query entity by condition map.
   * 
   * @param queryParams the condition map used to query objects
   * @return List
   * @throws InventoryException when db abnormal
   */
  @SuppressWarnings("unchecked")
  public List<T> query(Map<String, String> queryParams) throws InventoryException {
    List<T> result = null;
    try {
      beginTransaction();
      Criteria criteria = this.session.createCriteria(entityClass);
      for (String key : queryParams.keySet()) {
        criteria.add(Restrictions.eq(key, queryParams.get(key)));
      }
      result = (List<T>) criteria.list();
      closeTransaction();
    } catch (HibernateException error) {
      throw new InventoryException("", "error while querying data.errorMsg:" + error.getMessage());
    } finally {
      closeSession();
    }
    return result;
  }

  protected void beginTransaction() {
    this.session = this.sessionFactory.openSession();
    this.session.beginTransaction();
  }

  protected void closeTransaction() {
    this.session.getTransaction().commit();
  }

  protected void closeSession() {
    this.session.close();
  }

  protected void transactionRollBack() {
    this.session.getTransaction().rollback();
  }

}
