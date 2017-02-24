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

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;

public class InventoryDelegate extends AbstractInventory {

  private volatile Inventory delegate;

  public InventoryDelegate() {}

  /**
   * @param delegate
   */
  public InventoryDelegate(Inventory delegate) {
    this.delegate = delegate;
  }

  /**
   * @param request
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.request.InventoryRequestExecutor#execute(org.openo.commontosca.inventory.sdk.api.request.InventoryRequest)
   */
  @Override
  public <T, R> SimpleDeferred<R> execute(InventoryRequest<T, R> request) {
    return this.delegate.execute(request);
  }

  public Inventory getInventory() {
    return this.delegate;
  }

  public void setInventory(Inventory delegate) {
    this.delegate = delegate;
  }

}
