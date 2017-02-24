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

import java.util.Date;

public enum ValueType {

  STRING(String.class), NUMBER(Number.class), DATETIME(Date.class), BOOLEAN(
      Boolean.class), REFERENCE(String.class);

  private Class<?> clazz;
  private String typeString;

  /**
   * @param clazz
   */
  private ValueType(Class<?> clazz) {
    this.clazz = clazz;
    this.typeString = this.name().toLowerCase();
  }

  public static ValueType parse(String type) {
    return ValueType.valueOf(type.toUpperCase());
  }

  public Class<?> getJavaType() {
    return this.clazz;
  }

  public String getTypeString() {
    return this.typeString;
  }

}
