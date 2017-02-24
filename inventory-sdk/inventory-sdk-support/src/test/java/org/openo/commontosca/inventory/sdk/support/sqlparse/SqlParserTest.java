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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.support.sqlparse.SqlParser;

import net.sf.jsqlparser.JSQLParserException;

public class SqlParserTest {
  // private static final String COMPLEX_SQL = "SELECT * FROM site WHERE" + "" + " keyword!= null" +
  // "" + " or" + " abc>12 " + "and "
  // + "(site.KEYWORD like '%12%' AND site.HH = '122' AND ((site.FLH > '1' OR site.GDFS < 2 ) OR
  // site.MJ > 'dd') AND 1=1) ";
  private static final String COMPLEX_SQL =
      "keyword != null or abc>12 and (site.KEYWORD like '%12%' AND site.HH = '122' AND ((site.FLH > '1' OR site.GDFS < 2 ) OR site.MJ > 'dd') AND 1=1)";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testParseComplexWhereTotalNum() throws JSQLParserException {
    SqlParser parser = new SqlParser();
    Criteria criteria = parser.parseWhere(COMPLEX_SQL);
    List<Criteria> criteriaList = criteria.getCriteria();
    Assert.assertTrue(criteriaList.size() == 2);
  }

  @Test
  public void testParseComplexWhereCondition1Num() throws JSQLParserException {
    SqlParser parser = new SqlParser();
    Criteria criteria = parser.parseWhere(COMPLEX_SQL);
    List<Criteria> criteriaList = criteria.getCriteria();
    Criteria criteria0 = criteriaList.get(0);
    List<Criteria> condition1CriteriaList = criteria0.getCriteria();
    Assert.assertTrue(condition1CriteriaList.size() == 0);
  }

  @Test
  public void testParseComplexWhereCondition2Num() throws JSQLParserException {
    SqlParser parser = new SqlParser();
    Criteria criteria = parser.parseWhere(COMPLEX_SQL);
    List<Criteria> criteriaList = criteria.getCriteria();
    Criteria criteria1 = criteriaList.get(1);
    List<Criteria> condition1CriteriaList = criteria1.getCriteria();
    Assert.assertTrue("size = " + condition1CriteriaList.size(),
        condition1CriteriaList.size() == 2);
  }

  @Test
  public void testParseComplexWhereCondition1() throws JSQLParserException {
    SqlParser parser = new SqlParser();
    Criteria criteria = parser.parseWhere(COMPLEX_SQL);
    List<Criteria> criteriaList = criteria.getCriteria();
    Criteria criteria0 = criteriaList.get(0);
    Assert.assertEquals("keyword", criteria0.getCriterion().getName());
  }

  @Test
  public void testParseComplexWhereCondition2() throws JSQLParserException {
    SqlParser parser = new SqlParser();
    Criteria criteria = parser.parseWhere(COMPLEX_SQL);
    List<Criteria> criteriaList = criteria.getCriteria();
    Criteria criteria1 = criteriaList.get(1);
    List<Criteria> condition1CriteriaList = criteria1.getCriteria();
    Criteria condition1Criteria = condition1CriteriaList.get(0);
    Assert.assertEquals("abc", condition1Criteria.getCriterion().getName());
  }

  @Test
  public void testParseIn() throws JSQLParserException {
    String sql = "abc!=12 " + "or " + "keywork in (2,3,4,5)";
    SqlParser parser = new SqlParser();
    Criteria c = parser.parseWhere(sql);
    Criteria conditionCriteria = c.getCriteria().get(1);
    Assert.assertEquals("IN", conditionCriteria.getCriterion().getOp().toString());
  }

  @Test
  public void testParseNot() throws JSQLParserException {
    String sql = "abc=12 " + "and " + " not (keywork in (2,3,4,5))";
    SqlParser parser = new SqlParser();
    Criteria c = parser.parseWhere(sql);
    Criteria tempCriteria = c.getCriteria().get(1);
    Assert.assertTrue(tempCriteria.isNot());

  }

  @Test
  public void testParseNoWhere() throws JSQLParserException {
    String sql = "";
    SqlParser parser = new SqlParser();
    Criteria c = parser.parseWhere(sql);
    Assert.assertTrue(c.getCriteria().isEmpty());
  }

  @Test
  public void testSimpleWhere() throws JSQLParserException {
    String sql = "name='dd' or id>100 and name='tt' and region>200 or person='mike' and age>20";

    SqlParser parser = new SqlParser();
    Criteria c = parser.parseWhere(sql);
    Assert.assertTrue(c.getCriteria().size() == 3);
  }
}

