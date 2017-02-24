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

import java.util.Locale;

import org.openo.commontosca.inventory.core.context.InventoryApplicationContext;
import org.springframework.context.ApplicationContext;

public class I18n {

  private static ApplicationContext CONTEXT = InventoryApplicationContext.get();
  private static Locale SYSTEM_DEFAULT_LOCALE = Locale.getDefault();

  /**
   * 
   * @param key
   * @return
   */
  public static String getLabel(String key) {
    return I18n.CONTEXT.getMessage(key, null, I18n.SYSTEM_DEFAULT_LOCALE);
  }

  /**
   * 
   * @param key
   * @param args
   * @return
   */
  public static String getLabel(String key, Object... args) {
    return I18n.CONTEXT.getMessage(key, args, I18n.SYSTEM_DEFAULT_LOCALE);
  }

}
