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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;

public class ToolkitsTest {

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.support.utils.Toolkits#isEmpty(java.lang.String)}.
   */
  @Test
  public void testIsEmpty() {
    String nullStr = null;
    String emptyStr = "";
    String spaceStr = "   ";
    String someStr = "abc";
    Assert.assertTrue(Toolkits.isEmpty(nullStr));
    Assert.assertTrue(Toolkits.isEmpty(emptyStr));
    Assert.assertTrue(Toolkits.isEmpty(spaceStr));
    Assert.assertFalse(Toolkits.isEmpty(someStr));
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.support.utils.Toolkits#toList(java.util.Iterator)}.
   */
  @Test
  public void testToList() {
    List<String> input = Arrays.asList("a", "b", "c");
    List<String> output = Toolkits.toList(input.iterator());
    Assert.assertEquals(3, output.size());
    Assert.assertEquals(input, output);
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.support.utils.Toolkits#findCause(java.lang.Throwable, java.lang.Class)}
   * .
   */
  @Test
  public void testFindCause() {
    Throwable ex = new ExecutionException(
        new IllegalArgumentException(new IOException(new IllegalStateException())));
    Assert.assertEquals(IOException.class, Toolkits.findCause(ex, IOException.class).getClass());
    Assert.assertEquals(IllegalArgumentException.class,
        Toolkits.findCause(ex, IllegalArgumentException.class).getClass());
    Assert.assertEquals(IllegalStateException.class,
        Toolkits.findCause(ex, IllegalStateException.class).getClass());
    Assert.assertEquals(IllegalArgumentException.class,
        Toolkits.findCause(ex, RuntimeException.class).getClass());
    Assert.assertEquals(ExecutionException.class,
        Toolkits.findCause(ex, ExecutionException.class).getClass());
    Assert.assertNull(Toolkits.findCause(ex, Error.class));
  }

}
