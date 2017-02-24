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
package org.openo.commontosca.inventory.sdk.api;

import java.util.List;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap.Key;

public interface Criteria {

  public Criteria add(Criteria criteria);

  public Criteria addCriterion(String name, OP op, Object value);

  public <T> Criteria addCriterion(Key<T> name, OP op, T value);


  public List<Criteria> getCriteria();

  public Criterion getCriterion();

  public Group getGroup();

  public boolean isEmpty();

  public boolean isNot();

  public Criteria not();

  public Criteria setCriterion(String name, OP op, Object value);

  public <T> Criteria setCriterion(Key<T> name, OP op, T value);

  public Criteria setGroup(Group group);

  public ValueMap toValueMap();

  public class Criterion {

    private String name;

    private OP op;

    private Object value;

    public Criterion() {}

    public Criterion(String name, OP op, Object value) {
      super();
      this.name = name;
      this.op = op;
      this.value = value;
    }

    public String getName() {
      return this.name;
    }

    public OP getOp() {
      return this.op;
    }

    public Object getValue() {
      return this.value;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setOp(OP op) {
      this.op = op;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Criterion) {
        return this.name.equals(((Criterion) obj).getName())
            && this.op.equals(((Criterion) obj).getOp())
            && this.value.equals(((Criterion) obj).getValue());

      }
      return false;

    }

    @Override
    public int hashCode() {
      return (37 * this.name.hashCode() + 17) ^ this.value.hashCode() + this.op.hashCode();
    }

  }

  public enum Group {
    AND, OR;
    public String getValue() {
      if (AND == this) {
        return "$and";
      } else {
        return "$or";
      }
    }

    @Override
    public String toString() {
      return this.getValue();
    }
  }

  public enum OP {
    EQ, NE, LT, LTE, GT, GTE, LIKE, IN;
  }

}
