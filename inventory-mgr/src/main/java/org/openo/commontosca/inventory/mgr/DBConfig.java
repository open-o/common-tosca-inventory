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
package org.openo.commontosca.inventory.mgr;

public class DBConfig {

  private static String getProperty(String name) {
    String value = System.getenv(name);
    if (value == null) {
      value = System.getProperty(name);
    }
    return value;
  }

  public static String getDbUrl() {
    return getProperty("db.url");
  }


  public static String getDbUserName() {
    return getProperty("db.username");
  }


  public static String getDbPassword() {
    return getProperty("db.password");
  }






}
