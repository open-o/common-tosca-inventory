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
package org.openo.commontosca.inventory.sdk.support;

import java.util.ArrayList;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.Criteria;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap.Key;

public class DefaultCriteria implements Criteria {

  public static final Criteria DELETE_ALL = new DefaultCriteria();

  private List<Criteria> criteriaList = new ArrayList<Criteria>();
  private Group group = Group.AND;
  private Criterion criterion = null;
  private boolean not = false;

  @Override
  public Criteria add(Criteria criteria) {
    this.criterion = null;
    this.criteriaList.add(criteria);
    return this;
  }

  @Override
  public <T> Criteria addCriterion(Key<T> name, OP op, T value) {
    return this.addCriterion(name.getKeyName(), op, value);
  }

  @Override
  public Criteria addCriterion(String name, OP op, Object value) {
    this.criterion = null;
    if (op == null) {
      throw new IllegalArgumentException("OP con't be null!");
    }
    Criteria criteria = new DefaultCriteria();
    criteria.setCriterion(name, op, value);
    this.criteriaList.add(criteria);
    return this;
  }

  @Override
  public List<Criteria> getCriteria() {
    return this.criteriaList;
  }

  @Override
  public Criterion getCriterion() {
    return this.criterion;
  }

  @Override
  public Group getGroup() {
    return this.group;
  }

  @Override
  public boolean isEmpty() {
    return this.criteriaList.size() == 0 && null == this.criterion;
  }

  @Override
  public boolean isNot() {
    return this.not;
  }

  @Override
  public Criteria not() {
    this.not = !this.not;
    return this;
  }

  @Override
  public <T> Criteria setCriterion(Key<T> name, OP op, T value) {
    return this.setCriterion(name.getKeyName(), op, value);
  }

  @Override
  public Criteria setCriterion(String name, OP op, Object value) {
    this.clearCriteria();
    this.criterion = new Criterion(name, op, value);
    return this;
  }

  public Criteria set(Criteria criteria) {
    this.clearCriteria();
    if (criteria.getCriteria().isEmpty()) {
      this.criterion = criteria.getCriterion();
    } else {
      this.criteriaList = criteria.getCriteria();
    }
    return this;
  }

  @Override
  public Criteria setGroup(Group group) {
    this.group = group;
    return this;
  }

  @Override
  public ValueMap toValueMap() {

    if (this.isEmpty()) {
      return new ValueMap();
    }
    return this.criterion != null ? this.criterion2ValueMap() : this.criteriaList2Map();
  }

  private void clearCriteria() {
    this.criteriaList.clear();
    this.group = Group.AND;
    this.criterion = null;

  }

  private ValueMap criteriaList2Map() {
    ValueMap resultMap = new ValueMap();

    if (this.not) {
      this.reverseGroup();
    }
    if (1 == this.criteriaList.size()) {
      Criteria criteria = this.criteriaList.get(0);


      if (this.not) {
        criteria.not();
      }
      resultMap = criteria.toValueMap();

    } else if (this.criteriaList.size() >= 2) {

      List<ValueMap> groupList = new ArrayList<ValueMap>();
      for (Criteria criteria : this.criteriaList) {

        if (criteria == null) {
          throw new IllegalArgumentException("criteria is null!");
        }
        if (this.not) {
          criteria.not();
        }

        groupList.add(criteria.toValueMap());

      }
      resultMap.put(this.group.getValue(), groupList);

    }
    return resultMap;
  }

  private ValueMap criterion2ValueMap() {
    ValueMap resultMap = new ValueMap();
    if (this.criterion != null && this.criterion.getName() != null) {
      OP op = this.criterion.getOp();
      Object value = this.criterion.getValue();
      ValueMap subMap = new ValueMap();
      if (this.not) {
        ValueMap notValue = new ValueMap();
        notValue.put(getKeyName(op), value);
        subMap.put("$not", notValue);
      } else {
        subMap.put(getKeyName(op), this.criterion.getValue());

      }
      resultMap.put(this.criterion.getName(), subMap);
    }
    return resultMap;

  }

  public static String getKeyName(OP op) {
    String value = null;
    switch (op) {
      case EQ:
        value = "$eq";
        break;
      case LT:
        value = "$lt";
        break;
      case LTE:
        value = "$lte";
        break;
      case GT:
        value = "$gt";
        break;
      case GTE:
        value = "$gte";
        break;
      case LIKE:
        value = "$regex";
        break;
      case IN:
        value = "$in";
        break;
      case NE:
        value = "$ne";
        break;
      default:
        break;
    }
    return value;
  }

  private void reverseGroup() {
    if (Group.AND == this.group) {
      this.group = Group.OR;
    } else {
      this.group = Group.AND;
    }

  }

}
