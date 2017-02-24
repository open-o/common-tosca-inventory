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
package org.openo.commontosca.inventory.core.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.commontosca.inventory.core.model.Attribute;
import org.openo.commontosca.inventory.core.model.ModelConst;

public class AttributeTest {
  Map<String, Object> valueMap = new HashMap<String, Object>();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    this.valueMap.put(ModelConst.TAG_NAME, "haha");
    this.valueMap.put(ModelConst.TAG_TYPE, "string");
    this.valueMap.put(ModelConst.TAG_VISIBLE, true);
    this.valueMap.put(ModelConst.TAG_EDITABLE, true);
    this.valueMap.put(ModelConst.TAG_ENABLE, true);
    this.valueMap.put(ModelConst.TAG_REQUIRED, true);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testFromMap() {
    Attribute attr = new Attribute();
    attr.fromMap(this.valueMap);
    Assert.assertTrue(attr.getName().equals("haha"));
  }

}
