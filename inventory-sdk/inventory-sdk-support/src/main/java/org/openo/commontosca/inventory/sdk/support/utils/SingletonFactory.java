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

import java.util.concurrent.Callable;

public class SingletonFactory<T> {

  private final Callable<T> supplier;
  private T instance;

  public SingletonFactory(Callable<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    if (this.instance == null) {
      T newInstance = null;
      synchronized (this) {
        if (this.instance == null) {
          try {
            newInstance = this.supplier.call();
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        } else {
          newInstance = this.instance;
        }
      }
      this.instance = newInstance;
    }
    return this.instance;
  }

}
