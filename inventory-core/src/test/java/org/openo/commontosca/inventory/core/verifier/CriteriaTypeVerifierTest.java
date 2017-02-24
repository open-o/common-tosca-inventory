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
package org.openo.commontosca.inventory.core.verifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openo.commontosca.inventory.core.Constants.CommonKey;
import org.openo.commontosca.inventory.core.Constants.ModelKey;
import org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier;
import org.openo.commontosca.inventory.core.verifier.ValueType;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.OP;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;

public class CriteriaTypeVerifierTest {

  private ValueMap personModel = new ValueMap();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    this.personModel.clear();
    this.addAttribute(this.personModel, "name", ValueType.STRING, true, false);
    this.addAttribute(this.personModel, "age", ValueType.NUMBER, true, false);
    this.addAttribute(this.personModel, "marry", ValueType.BOOLEAN, false, false);
    this.addAttribute(this.personModel, "birthday", ValueType.DATETIME, false, false);
    this.addAttribute(this.personModel, "children", ValueType.REFERENCE, false, true);
    this.addAttribute(this.personModel, "size", ValueType.NUMBER, true, true);
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.core.verifier.CriteriaTypeVerifier#verify()}.
   */
  @Test
  public void testVerify() {
    ValueMap criteria = new ValueMap();
    ValueList andList = new ValueList();
    criteria.put("$and", andList);
    andList.add(Collections.singletonMap("name", "John"));
    andList.add(Collections.singletonMap("age", "123"));
    andList.add(Collections.singletonMap("birthday", "2016-03-03"));
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(criteria, this.personModel);
    verifier.verify();
    Assert.assertEquals("John", verifier.getStrict().requireList("$and").requireMap(0).get("name"));
    Assert.assertEquals(123L, verifier.getStrict().requireList("$and").requireMap(1).get("age"));
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(2016, 2, 3);
    Assert.assertEquals(calendar.getTime(),
        verifier.getStrict().requireList("$and").requireMap(2).get("birthday"));
  }

  @Test
  public void testVerifyWithInList() {
    Criteria criteria = new DefaultCriteria();
    List<String> idList = new ArrayList<String>();
    idList.add("1");
    idList.add("1");
    criteria.setCriterion("age", OP.IN, idList);
    ValueMap map = criteria.toValueMap();
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(map, this.personModel);
    ValueMap strict = verifier.verify();
    Assert.assertEquals(map.requireMap("age").requireList("$in").requireLong(0),
        strict.requireMap("age").requireList("$in").get(0));
    Assert.assertEquals(1, strict.requireMap("age").requireList("$in").size());
  }

  @Test
  public void testVerifyWithInSet() {
    Criteria criteria = new DefaultCriteria();
    Set<String> idSet = new HashSet<String>();
    idSet.add("1");
    idSet.add("1");
    criteria.setCriterion("age", OP.IN, idSet);
    ValueMap map = criteria.toValueMap();
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(map, this.personModel);
    ValueMap strict = verifier.verify();
    Assert.assertEquals(map.requireMap("age").requireList("$in").requireLong(0),
        strict.requireMap("age").requireList("$in").get(0));
    Assert.assertEquals(1, strict.requireMap("age").requireList("$in").size());
  }

  @Test
  public void testVerifyWithInArray() {
    Criteria criteria = new DefaultCriteria();
    String[] idArray = new String[] {"1", "1"};
    criteria.setCriterion("age", OP.IN, idArray);
    ValueMap map = criteria.toValueMap();
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(map, this.personModel);
    ValueMap strict = verifier.verify();
    Assert.assertEquals(map.requireMap("age").requireList("$in").requireLong(0),
        strict.requireMap("age").requireList("$in").get(0));
    Assert.assertEquals(1, strict.requireMap("age").requireList("$in").size());
  }

  @Test
  public void testVerifyWithInValues() {
    ValueMap criteria = new ValueMap();
    criteria.put("_id", Collections.singletonMap("$in",
        Arrays.asList(new ObjectId(), new ObjectId(), new ObjectId())));
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(criteria, this.personModel);
    verifier.verify();
    Assert.assertEquals(3,
        verifier.getStrict().requireMap(CommonKey.ID.getKeyName()).requireList("$in").size());
    Assert.assertEquals(CommonKey.ID.getValueType(), verifier.getStrict()
        .requireMap(CommonKey.ID.getKeyName()).requireList("$in").get(0).getClass());
  }

  @Test
  public void testVerifyWithObjectId() {
    ValueMap criteria = new ValueMap();
    ObjectId objectId = new ObjectId();
    criteria.put(CommonKey.ID.getKeyName(), objectId);
    CriteriaTypeVerifier verifier = new CriteriaTypeVerifier(criteria, this.personModel);
    verifier.verify();
    Assert.assertEquals(criteria.requireValue(CommonKey.ID),
        verifier.getStrict().get(CommonKey.ID));
  }

  private void addAttribute(ValueMap model, String key, ValueType type, boolean required,
      boolean isArray) {
    ValueList list = model.optValue(ModelKey.ATTRIBUTES, null);
    if (list == null) {
      list = new ValueList();
      model.put(ModelKey.ATTRIBUTES, list);
    }
    ValueMap attribute = new ValueMap();
    attribute.put(ModelKey.NAME, key);
    attribute.put(ModelKey.TYPE, type.getTypeString());
    attribute.put(ModelKey.REQUIRED, required);
    attribute.put(ModelKey.IS_ARRAY, isArray);
    list.add(attribute);
  }

}
