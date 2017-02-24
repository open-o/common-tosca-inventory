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
package org.openo.commontosca.inventory.sdk.support.request;

import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.Criteria.Group;
import org.openo.commontosca.inventory.sdk.api.Criteria.OP;
import org.openo.commontosca.inventory.sdk.api.request.InventoryFilterable;
import org.openo.commontosca.inventory.sdk.support.DefaultCriteria;

public abstract class AbstractInventoryFilterableRequest<T, R>
    extends AbstractInventoryRequest<T, R>implements InventoryFilterable<T> {

  private Criteria criteria;

  public AbstractInventoryFilterableRequest(Inventory inventory) {
    super(inventory);
  }

  @Override
  public T filter(Criteria criteria) {
    this.criteria = criteria;
    return this.cast();
  }

  @Override
  public T filter(Map<String, Object> criteria) {
    Criteria convertedCriteria = new DefaultCriteria();
    convertedCriteria.setGroup(Group.AND);
    for (Map.Entry<String, Object> entry : criteria.entrySet()) {
      convertedCriteria.addCriterion(entry.getKey(), OP.EQ, entry.getValue());
    }
    this.criteria = convertedCriteria;
    return this.cast();
  }

  @Override
  public T filter(String field, Object value) {
    this.criteria = new DefaultCriteria().setCriterion(field, OP.EQ, value);
    return this.cast();
  }

  @Override
  public Criteria getFilter() {
    return this.criteria;
  }

}
