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
package org.openo.commontosca.inventory.sdk.support.utils;

import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.support.utils.SmartDateParser;

public class SmartDateParserTest {

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.support.utils.SmartDateParser#parse(java.lang.String)}
   * .
   */
  @Test
  public void testParse() throws Exception {
    SmartDateParser parser = new SmartDateParser();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Assert.assertEquals("2016-01-01 12:12:12.000",
        format.format(parser.parse("2016-01-01 12:12:12")));
    Assert.assertEquals("2016-01-01 12:12:12.000",
        format.format(parser.parse("2016-01-01 12:12:12")));
    Assert.assertEquals("2016-01-01 02:12:12.000",
        format.format(parser.parse("2016/01/01 02:12:12")));
    Assert.assertEquals("2016-01-01 00:00:00.000", format.format(parser.parse("2016/01/01")));
    Assert.assertEquals("2016-01-01 12:12:12.123",
        format.format(parser.parse("2016-01-01 12:12:12.123")));
    Assert.assertEquals("2016-01-01 12:12:12.000",
        format.format(parser.parse("2016-01-01T12:12:12")));
  }

}
