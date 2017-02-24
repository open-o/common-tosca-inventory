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
package org.openo.commontosca.inventory.sdk.api.i18n;

import java.util.Locale;
import java.util.ResourceBundle;


public class I18nService {
  private String resource;
  private Locale locale;
  private ResourceBundle resourceBundle;

  public I18nService(String resource, Locale locale) {
    super();
    this.resource = resource;
    this.locale = locale;
    if (locale == null) {
      this.locale = Locale.getDefault();
    }
    resourceBundle = ResourceBundle.getBundle(this.resource, this.locale);
  }

  /**
   * 
   * @param key
   * @return
   */
  public String getLabel(String key) {
    return resourceBundle.getString(key);
  }



  /**
   * 
   * @param key
   * @param args
   * @return
   */
  public String getLabel(String key, Object... args) {
    String result = resourceBundle.getString(key);
    for (int i = 0; i < args.length; i++) {
      result = result.replaceFirst("\\{\\w+\\}", (String) args[i]);
    }
    return result;


  }



  public void setResource(String resource) {
    this.resource = resource;
  }
}
