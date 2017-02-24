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

import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.deferred.Deferred;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferredObject;

public class DeferredResponse<T> extends SimpleDeferredObject<T> {

  public DeferredResponse() {
    super();
  }

  public DeferredResponse(Deferred<T, Throwable, Float> delegate) {
    super(delegate);
  }

  public SimpleDeferred<T> reject(String message, Object... params) {
    return super.reject(new InventoryException(message, params));
  }

  @Override
  public SimpleDeferred<T> reject(Throwable error) {
    return this.reject("Inventory Request failed.", error);
  }

  public SimpleDeferred<T> reject(String message, Throwable cause, Object... params) {
    InventoryException ex = null;
    if (cause instanceof InventoryException) {
      ex = (InventoryException) cause;
    } else {
      ex = new InventoryException(message, cause, params);
    }
    return super.reject(ex);
  }

}
