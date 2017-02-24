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
package org.openo.commontosca.inventory.sdk.support.sqlparse;

public class OperatorPosition {
  private String operator;
  private int startIndex;
  private int endIndex;

  public OperatorPosition() {
    super();
  }

  public OperatorPosition(String operator, int startIndex, int endIndex) {
    super();
    this.operator = operator;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  public int getEndIndex() {
    return this.endIndex;
  }

  public String getOperator() {
    return this.operator;
  }

  public int getStartIndex() {
    return this.startIndex;
  }

  public void setEndIndex(int endIndex) {
    this.endIndex = endIndex;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

}
