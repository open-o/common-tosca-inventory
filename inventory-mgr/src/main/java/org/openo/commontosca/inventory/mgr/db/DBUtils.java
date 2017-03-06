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
package org.openo.commontosca.inventory.mgr.db;

import org.openo.commontosca.inventory.mgr.DBConfig;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtils {
  private static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";
  private static DBI dbi;
  private static boolean registered = false;
  private static final Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

  static {
    try {
      if (!registered) {
        registered = true;
        Class.forName(MYSQLDRIVER);
      }
      LOGGER.info("url"+DBConfig.getDbUrl());
      LOGGER.info("username"+DBConfig.getDbUrl());
      LOGGER.info("password"+DBConfig.getDbUrl());
      dbi = new DBI(DBConfig.getDbUrl(), DBConfig.getDbUserName(), DBConfig.getDbPassword());
    } catch (ClassNotFoundException e) {
      LOGGER.error("DBI init error:", e);
    }
  }

  public static Handle getHandle() {
    return dbi.open();
  }

  public static void close(Handle handle) {
    if (handle != null)
      handle.close();
  }
}
