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

import java.util.Collections;

import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

public abstract class AbstractVerifier<T extends ValueAccess>
    implements ValueVerifier<T>, StrictPolicy {

  protected long policy = StrictPolicy.DEFINE_INSERT_DATA;
  protected T origin;
  protected ValueList errors;
  protected T strict;

  /**
   * @param origin
   */
  public AbstractVerifier(T origin) {
    this.origin = origin;
  }

  @Override
  public ValueList getErrors() {
    return this.errors == null ? ValueList.wrap(Collections.emptyList()) : this.errors;
  }

  @Override
  public T getOrigin() {
    return this.origin;
  }

  @Override
  public T getStrict() {
    return this.strict;
  }

  @Override
  public AbstractVerifier<T> policy(long policy) {
    this.policy = policy;
    return this;
  }

  protected void fail(String path, Object value, ValueError error) {
    switch (error) {
      case UNKNOWN_FIELD: {
        if ((this.policy & StrictPolicy.DISCARD_UNKNOWN_FIELD) != 0) {
          return;
        }
      }
      default: {
        break;
      }
    }
    if ((this.policy & StrictPolicy.FAIL_ON_VERIFY) != 0) {
      throw new IllegalArgumentException(
          "Type process failed, key: " + path + ", value: " + value + " error: " + error);
    }
    if ((this.policy & StrictPolicy.RECORD_ERROR) != 0) {
      this.addError(path, value, error);
    }
  }

  private void addError(String path, Object value, ValueError error) {
    if (this.errors == null) {
      this.errors = new ValueList();
    }
    ValueMap errorMap = new ValueMap();
    errorMap.put("path", path);
    errorMap.put("value", value);
    errorMap.put("error", error);
    this.errors.add(errorMap);
  }

}
