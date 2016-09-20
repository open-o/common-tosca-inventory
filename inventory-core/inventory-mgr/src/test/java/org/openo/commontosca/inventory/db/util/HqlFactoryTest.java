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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openo.commontosca.inventory.util.HqlFactory;



public class HqlFactoryTest {



  @Test
  public void when_input_null_testContain_output_false() {
    boolean expect = false;
    boolean actual = HqlFactory.contain(null, "name");
    assertEquals(expect, actual);
    String[] src = new String[0];
    actual = HqlFactory.contain(src, "name");
    assertEquals(expect, actual);
    src = new String[1];
    actual = HqlFactory.contain(src, null);
    assertEquals(expect, actual);
  }

  @Test
  public void when_input_src_contain_target_testContain_output_true() {
    boolean expect = true;
    String [] src = {"name", "type"};
    String target = "name";
    boolean actual = HqlFactory.contain(src, target);
    assertEquals(expect, actual);
  }

  @Test
  public void when_input_src_not_contain_target_testContain_output_false() {
    boolean expect = false;
    String [] src = {"name", "type"};
    String target = "version";
    boolean actual = HqlFactory.contain(src, target);
    assertEquals(expect, actual);
  }

  @Test
  public void testGetOidFilter() {
    // fail("Not yet implemented");
    String key = "csarId";
    String value = "xd03dsfsdfsfsdfsd";
    String expect = "csarId= 'xd03dsfsdfsfsdfsd'";
    String actual = HqlFactory.getOidFilter(key, value);
    assertEquals(expect, actual);
  }

//  @Test
//  public void when_input_entity_output_queryhql() {
//    VnfmData data = new VnfmData();
//    String filter = "vnfmId";
//    data.setName("csarName");
//    data.setType("NS");
//    String expect = "select q.vnfmId from VnfmData as q where q.name='csarName' and q.type='NS'";
//    String actual = HqlFactory.getQueryHql(data, filter);
//    assertEquals(expect, actual);
//  }

}
