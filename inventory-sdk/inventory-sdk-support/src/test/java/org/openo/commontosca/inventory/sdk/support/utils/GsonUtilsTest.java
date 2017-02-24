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
/**
 * 
 */
package org.openo.commontosca.inventory.sdk.support.utils;

import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;

public class GsonUtilsTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.support.utils.GsonUtils#toJson(com.google.gson.JsonElement)}
   * .
   */
  @Test
  public void testToJsonValueAccess() {
    ValueMap valueMap = new ValueMap();
    valueMap.put("a", "1");
    valueMap.put("b", "2");
    valueMap.put("a-list", Arrays.asList("1", "2"));
    valueMap.put("b-list", ValueList.wrap(Arrays.asList("3", "4")));
    String json = GsonUtils.toJson(valueMap);
    Assert.assertNotNull(json);
    ValueAccess valueAccess = GsonUtils.fromJson(json, ValueAccess.class);
    Assert.assertEquals(valueMap, valueAccess);
  }

  @Test
  public void testToDate() {
    Date date = new Date();
    ValueMap map = new ValueMap();
    map.put("date", date);
    String json = GsonUtils.toJson(map);
    ValueMap fromJson = GsonUtils.fromJson(json, ValueMap.class);
    Assert.assertEquals(date, fromJson.requireValue("date", Date.class));
  }

}
