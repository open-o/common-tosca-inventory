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

package org.openo.commontosca.inventory.db.util;



import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;


public class H2DbServerUtil {
  private static String resourcePath;

  /**
   * init db table.
   */
  public static void initTable() {
    init();
    // execute sql
    ArrayList<String> sqlList = new ArrayList<String>();
    // sqlList.add("openo-common-res-createobj.sql");
    sqlList.add("openo-gso-lcm-createobj.sql");
    sqlList.add("openo-nfvo-res-createobj.sql");
    for (int i = 0; i < sqlList.size(); i++) {
      SQLExec sqlExec = new SQLExec();
      // set db connetc parameter
      sqlExec.setDriver("org.h2.Driver");
      sqlExec.setUrl("jdbc:h2:tcp://localhost:18209/" + resourcePath + "db/inventory");
      sqlExec.setUserid("inventory");
      sqlExec.setPassword("inventory");

      sqlExec.setSrc(new File(resourcePath + "sql/" + sqlList.get(i)));
      sqlExec.setOnerror(
          (SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
      sqlExec.setPrint(true); // set print
      sqlExec.setProject(new Project());
      sqlExec.execute();
    }

  }

  private static void init() {
    try {
      resourcePath = HibernateSession.class.getResource("/").toURI().getPath();
    } catch (URISyntaxException error) {
      error.printStackTrace();
    }
  }

  /**
   * init db.
   */
  public static void main(String[] args) {
    H2DbServer.startUp();
    H2DbServerUtil.initTable();
    H2DbServer.shutDown();
  }

}
