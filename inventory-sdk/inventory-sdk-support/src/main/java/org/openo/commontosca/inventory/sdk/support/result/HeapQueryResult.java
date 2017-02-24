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
package org.openo.commontosca.inventory.sdk.support.result;

import java.util.List;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;

public class HeapQueryResult extends BaseQueryResult implements QueryResult {

  private List<ValueMap> list;

  public HeapQueryResult(List<ValueMap> list) {
    this.list = list;
  }

  @Override
  public Cursor<ValueMap> asCursor() {
    return new BaseCursor<ValueMap>(this.list.iterator(), this);
  }

  @Override
  public List<ValueMap> asList() {
    return this.list;
  }

  @Override
  public void close() {
    this.list.clear();
    this.list = null;
  }

}
