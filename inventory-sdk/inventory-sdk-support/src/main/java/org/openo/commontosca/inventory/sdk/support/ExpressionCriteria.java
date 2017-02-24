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
package org.openo.commontosca.inventory.sdk.support;

import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.sqlparse.SqlParser;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;

import net.sf.jsqlparser.JSQLParserException;

public class ExpressionCriteria extends DefaultCriteria {

  /**
   * 
   * @param expression
   * @param vars
   * @throws InventoryException
   */
  public ExpressionCriteria(String expression, ValueMap vars) throws InventoryException {
    SqlParser parser = new SqlParser();
    try {
      Criteria where = parser.parseWhere(expression, vars);
      this.set(where);
    } catch (JSQLParserException e) {
      throw Toolkits.toInventoryException(e);
    }
  }

}
