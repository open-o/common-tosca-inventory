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

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.Criterion;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SqlParser {

  private static final String SQL_SELECT_TABLE_KEY = "SELECT * FROM TABLE_NAME ";
  private static final String SQL_WHERE_KEY = "WHERE ";

  public SqlParser() {}

  private List<Object> generateList(Expression expressionWhere, List<Object> expressionList) {
    if (expressionWhere == null) {
      return null;
    }
    if (expressionWhere instanceof OrExpression || expressionWhere instanceof AndExpression) {
      BinaryExpression be = (BinaryExpression) expressionWhere;

      this.generateList(be.getLeftExpression(), expressionList);

      expressionList.add(
          (expressionWhere instanceof OrExpression) ? ObjectExpression.OR : ObjectExpression.AND);

      this.generateList(be.getRightExpression(), expressionList);
    } else if (expressionWhere instanceof Parenthesis) {
      List<Object> childList = new LinkedList<Object>();
      Expression expInParenthesis = this.getExpressionWithoutParenthesis(expressionWhere);

      if (expressionWhere.toString().startsWith(ObjectExpression.NOT)) {
        childList.add(ObjectExpression.NOT);
      }
      expressionList.add(childList);
      this.generateList(expInParenthesis, childList);
    } else {
      expressionList.add(this.processExpression(expressionWhere));
    }
    return expressionList;
  }

  /**
   * 
   * @param sql
   * @return
   * @throws JSQLParserException
   */
  private Expression getExpressionForSQL(String sql) throws JSQLParserException {
    CCJSqlParserManager parserManager = new CCJSqlParserManager();
    PlainSelect plainSelect =
        (PlainSelect) ((Select) parserManager.parse(new StringReader(sql))).getSelectBody();
    Expression expressionWhere = plainSelect.getWhere();
    return this.getExpressionWithoutParenthesis(expressionWhere);
  }

  /**
   * 
   * @param expression
   * @return
   */
  private Expression getExpressionWithoutParenthesis(Expression expression) {
    if (expression instanceof Parenthesis) {
      Expression child = ((Parenthesis) expression).getExpression();
      return this.getExpressionWithoutParenthesis(child);
    } else {
      return expression;
    }

  }

  private List<Object> getLevelObjectByExpression(String SQL) throws JSQLParserException {
    Expression expressionWhere = this.getExpressionForSQL(SQL);
    List<Object> sqlExpressionList = new LinkedList<Object>();
    sqlExpressionList = this.generateList(expressionWhere, sqlExpressionList);
    return sqlExpressionList;
  }


  @Deprecated
  private Object invokeMethod(Object obj, String methodFunc) {
    try {
      Method method = obj.getClass().getMethod(methodFunc, null);
      return method.invoke(obj, null);
    } catch (Exception e) {
      return null;
    }
  }

  public Criteria parseWhere(String conditionSql) throws JSQLParserException {
    String sql = SQL_SELECT_TABLE_KEY;
    if (conditionSql != null && conditionSql.length() > 0) {
      sql += SQL_WHERE_KEY + conditionSql;
    }
    List<Object> expressList = this.getLevelObjectByExpression(sql);
    return this.toCriteria(expressList);
  }

  /**
   * @param expression
   * @param values
   * @return
   * @throws JSQLParserException
   */
  public Criteria parseWhere(String expression, ValueMap values) throws JSQLParserException {
    String sql = expression.replaceAll("[\"]?(\\$\\{.*?\\})[\"]?", "\"$1\"");
    if (sql != null && sql.length() > 0) {
      sql = SQL_SELECT_TABLE_KEY + SQL_WHERE_KEY + sql;
    } else {
      sql = SQL_SELECT_TABLE_KEY;
    }

    Criteria where = this.parseWhere(sql);
    this.replaceExpressions(where, values);
    return where;
  }

  private void replaceExpressions(Criteria criteria, ValueMap values) {
    Criterion criterion = criteria.getCriterion();
    if (criterion != null && criterion.getValue() instanceof String) {
      String value = (String) criterion.getValue();
      if (value.startsWith("${") && value.endsWith("}")) {
        String var = value.substring(2, value.length() - 1);
        criterion.setValue(values.get(var));
      }
    }
    List<Criteria> subCriterias = criteria.getCriteria();
    if (!subCriterias.isEmpty()) {
      for (Criteria c : subCriterias) {
        this.replaceExpressions(c, values);
      }
    }
  }

  /**
   * @param expressionSingle
   * @return
   */
  private ObjectExpression processExpression(Expression expressionSingle) {
    ObjectExpression objExpression = new ObjectExpression();
    Object columnObj = this.invokeMethod(expressionSingle, "getLeftExpression");
    if (columnObj instanceof LongValue) {
      LongValue longValue = (LongValue) columnObj;
      objExpression.setColumnname(longValue.getStringValue());
    } else {
      Column column = (Column) this.invokeMethod(expressionSingle, "getLeftExpression");
      objExpression.setColumnname(column.getColumnName());
    }
    if (expressionSingle instanceof BinaryExpression) {
      BinaryExpression be = (BinaryExpression) expressionSingle;
      objExpression.setExpression(be.getStringExpression());
      if (be.getRightExpression() instanceof Function) {
        objExpression.setValue(this.invokeMethod(be.getRightExpression(), "toString"));
      } else {
        objExpression.setValue(this.invokeMethod(be.getRightExpression(), "getValue"));
      }
    } else if (expressionSingle instanceof InExpression) {
      InExpression in = (InExpression) expressionSingle;
      objExpression.setExpression(ObjectExpression.IN);
      objExpression.setValue(((ExpressionList) in.getRightItemsList()).getExpressions());
    } else {
      objExpression.setExpression((String) this.invokeMethod(expressionSingle, "toString"));
    }
    return objExpression;
  }

  private Criteria toCriteria(List<Object> expressList) {
    OperatorTransform opTransform = new OperatorTransform();
    Criteria criteria = opTransform.transform(expressList);
    return criteria;
  }
}
