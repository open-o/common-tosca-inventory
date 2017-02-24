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
package org.openo.commontosca.inventory.sdk.support.sqlparse;

import org.openo.commontosca.inventory.sdk.api.Criteria.OP;

public class ObjectExpression {
  public static final String AND = "AND";
  public static final String OR = "OR";
  public static final String NOT = "NOT";
  public static final String EQ = "=";
  public static final String NE = "!=";
  public static final String LT = "<";
  public static final String LTE = "<=";
  public static final String GT = ">";
  public static final String GTE = ">=";
  public static final String LIKE = "like";
  public static final String IN = "in";

  private String columnName = "";
  private String expression = "";

  private Object value = "";

  public String getColumnname() {
    return this.columnName;
  }

  public String getExpression() {
    return this.expression;
  }

  public OP getOP() {
    if (this.expression.equals(ObjectExpression.EQ)) {
      return OP.EQ;
    } else if (this.expression.equals(ObjectExpression.NE)) {
      return OP.NE;
    } else if (this.expression.equals(ObjectExpression.LT)) {
      return OP.LT;
    } else if (this.expression.equals(ObjectExpression.LTE)) {
      return OP.LTE;
    } else if (this.expression.equals(ObjectExpression.GT)) {
      return OP.GT;
    } else if (this.expression.equals(ObjectExpression.GTE)) {
      return OP.GTE;
    } else if (this.expression.equalsIgnoreCase(ObjectExpression.LIKE)) {
      return OP.LIKE;
    } else if (this.expression.equalsIgnoreCase(ObjectExpression.IN)) {
      return OP.IN;
    }
    return null;
  }

  public Object getValue() {
    return this.value;
  }

  public void setColumnname(String columnname) {
    this.columnName = columnname;
  }

  public void setExpression(String exp) {
    this.expression = exp;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
