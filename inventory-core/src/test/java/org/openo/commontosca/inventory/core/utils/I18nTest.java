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
package org.openo.commontosca.inventory.core.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openo.commontosca.inventory.core.utils.I18n;

public class I18nTest {

  @Test
  @Ignore
  public void testGetLabelWithArgs() {
    String value = I18n.getLabel("dataparse.datanotnull", "nename");
    Assert.assertNotNull(value);

  }

  @Test
  @Ignore
  public void testGetLabelWithourArgs() {
    String value = I18n.getLabel("dataparse.datatypeillegal");
    Assert.assertNotNull(value);
  }
}
