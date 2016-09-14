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

package org.openo.commontosca.inventory.common;

import java.util.HashMap;
import java.util.List;

public class Pager {

  private int page = 1;
  private int pageTotal;
  private int rowsTotal;
  private int rows = 25;
  private String hql;
  private List<?> list;
  private HashMap<String, Class> dbTableMapping;

  public Pager() {
    super();
  }

  public String getHql() {
    return hql;
  }

  public void setHql(String hql) {
    this.hql = hql;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageTotal() {
    return pageTotal;
  }

  public void setPageTotal(int pageTotal) {
    this.pageTotal = pageTotal;
  }

  public int getRowsTotal() {
    return rowsTotal;
  }

  public void setRowsTotal(int rowsTotal) {
    this.rowsTotal = rowsTotal;
    pageTotal = rowsTotal % rows == 0 ? rowsTotal / rows : rowsTotal / rows + 1;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public List<?> getList() {
    return list;
  }

  public void setList(List<?> list) {
    this.list = list;
  }

  public void setDbTableMapping(HashMap<String, Class> dbTableMapping) {
    this.dbTableMapping = dbTableMapping;
  }

  public HashMap<String, Class> getDbTableMapping() {
    return this.dbTableMapping;
  }

  @Override
  public String toString() {
    return "Pager [list=" + list + ", page=" + page + ", pageTotal=" + pageTotal + ", rows=" + rows
        + ", rowsTotal=" + rowsTotal + "]" + " SQL:" + hql;
  }


}
