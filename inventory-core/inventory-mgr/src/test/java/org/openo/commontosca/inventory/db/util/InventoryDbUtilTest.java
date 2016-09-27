/**
 * Copyright 2016 ZTE Corporation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openo.commontosca.inventory.util.InventoryDbUtil;


public class InventoryDbUtilTest {

  @Test
  public void when_generate_id_is_not_null() {
    String actualUuid = InventoryDbUtil.generateId();
    assertNotNull(actualUuid);
  }

  @Test
  public void when_input_empty_string_output_false() {
    boolean expect = false;
    boolean actual = InventoryDbUtil.isNotEmpty("");
    assertEquals(expect, actual);
  }

  @Test
  public void when_input_blan_string_output_true() {
    boolean expect = true;
    boolean actual = InventoryDbUtil.isNotEmpty(" ");
    assertEquals(expect, actual);
  }

  @Test
  public void when_input_null_string_output_false() {
    boolean expect = false;
    boolean actual = InventoryDbUtil.isNotEmpty(null);
    assertEquals(expect, actual);
  }

  @Test
  public void when_input_str_string_output_true() {
    boolean expect = true;
    boolean actual = InventoryDbUtil.isNotEmpty("str");
    assertEquals(expect, actual);
  }

}
