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
package org.openo.commontosca.inventory.sdk.support.parser;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.Criterion;
import org.openo.commontosca.inventory.sdk.api.Criteria.Group;
import org.openo.commontosca.inventory.sdk.api.Criteria.OP;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;
import org.openo.commontosca.inventory.sdk.support.parser.CriteriaParseException;
import org.openo.commontosca.inventory.sdk.support.parser.CriteriaParser;

public class CriteriaParserTest {

  @Test
  public void testParseValueMapCriteriaOnSingleNormal() {
    Criteria criteria = new DefaultCriteria();
    String name = "age";
    int value = 18;
    OP op = OP.GT;
    criteria.setCriterion(name, op, value);
    criteria.not();
    ValueMap valueMap = criteria.toValueMap();
    Criteria actual = CriteriaParser.parseValueMapCriteria(valueMap);
    Assert.assertTrue(actual.isNot());
    Assert.assertTrue(actual.getCriteria().size() == 0);
    Assert.assertEquals(criteria.getCriterion(), actual.getCriterion());

  }

  @Test
  public void testParseValueMapCriteriaOnSingleNot() {
    Criteria criteria = new DefaultCriteria();
    String name = "name";
    String value = "zhangsan__";
    OP op = OP.LIKE;
    criteria.setCriterion(name, op, value);
    criteria.not();
    ValueMap valueMap = criteria.toValueMap();
    Criteria actual = CriteriaParser.parseValueMapCriteria(valueMap);
    Assert.assertTrue(actual.isNot());
    Assert.assertSame(0, actual.getCriteria().size());
    Assert.assertEquals(criteria.getCriterion(), actual.getCriterion());

  }

  @Test
  public void testParseValueMapCriteriaOnMulitNormal() {
    Criteria criteria = new DefaultCriteria();
    criteria.addCriterion("name", OP.LIKE, "ad_b");
    criteria.addCriterion("size", OP.EQ, "adb");
    criteria.addCriterion("value", OP.GTE, "adb");
    criteria.setGroup(Group.OR);
    ValueMap valueMap = criteria.toValueMap();
    Criteria actual = CriteriaParser.parseValueMapCriteria(valueMap);
    Assert.assertSame(criteria.getCriteria().size(), actual.getCriteria().size());
    Assert.assertSame(criteria.getGroup(), actual.getGroup());
    List<Criteria> actualCriteriaList = criteria.getCriteria();
    List<Criteria> actualList = actual.getCriteria();
    for (int i = 0; i < actualCriteriaList.size(); i++) {
      Criterion criterion = actualCriteriaList.get(i).getCriterion();
      for (int j = 0; j < actualList.size(); j++) {
        Criterion actualCriterion = actualList.get(i).getCriterion();
        if (actualCriterion.getName().equals(criterion.getName())) {
          Assert.assertEquals(criterion, actualCriterion);
        }
      }
    }
  }

  @Test
  public void testParseValueMapCriteriaOnMulitNot() {
    Criteria criteria = new DefaultCriteria();
    criteria.addCriterion("name", OP.LIKE, "adb");
    criteria.addCriterion("size", OP.EQ, "adb");
    criteria.addCriterion("value", OP.GTE, "adb");
    criteria.setGroup(Group.OR);
    criteria.not();
    ValueMap valueMap = criteria.toValueMap();
    criteria = CriteriaParser.parseValueMapCriteria(valueMap);
    Criteria actual = CriteriaParser.parseValueMapCriteria(criteria.toValueMap());
    Assert.assertSame(criteria.getCriteria().size(), actual.getCriteria().size());
    Assert.assertSame(criteria.getGroup(), actual.getGroup());
    List<Criteria> actualCriteriaList = criteria.getCriteria();
    List<Criteria> actualList = actual.getCriteria();
    for (int i = 0; i < actualCriteriaList.size(); i++) {
      Criterion criterion = actualCriteriaList.get(i).getCriterion();
      for (int j = 0; j < actualList.size(); j++) {
        Assert.assertTrue(actualList.get(i).isNot());
        Criterion actualCriterion = actualList.get(i).getCriterion();
        if (actualCriterion.getName().equals(criterion.getName())) {
          Assert.assertEquals(criterion, actualCriterion);

        }
      }
    }

  }

  @Test(expected = CriteriaParseException.class)
  public void testErrorFormatData() {
    ValueMap inputMap = new ValueMap();
    inputMap.put("name", Collections.singletonMap("$regex", "%zhang_san%"));
    inputMap.put("age", Collections.singletonMap("$lt", 17));
    CriteriaParser.parseValueMapCriteria(inputMap);
    Assert.fail("The input format is incorrect and should be changed");

  }

  @Test
  public void testParseLikeAndToValueMap() {
    ValueMap inputMap = new ValueMap();
    inputMap.put("name", Collections.singletonMap("$regex", "%zhang_san%"));
    Criteria criteria = CriteriaParser.parseValueMapCriteria(inputMap);
    Assert.assertNotNull(criteria.getCriterion());
    Assert.assertEquals("%zhang_san%", criteria.getCriterion().getValue());
    Assert.assertEquals(criteria.getCriterion().getOp(), OP.LIKE);
    ValueMap outputMap = criteria.toValueMap();
    Assert.assertEquals(
        "Inputs and outputs should be consistent and can be converted to each other", inputMap,
        outputMap);
  }

}
