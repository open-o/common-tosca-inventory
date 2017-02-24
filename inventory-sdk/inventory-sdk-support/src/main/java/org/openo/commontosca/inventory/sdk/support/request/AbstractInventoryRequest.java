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

import java.util.concurrent.atomic.AtomicBoolean;

import org.openo.commontosca.inventory.sdk.api.Inventory;
import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.Context;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;
import org.openo.commontosca.inventory.sdk.support.InventoryDelegate;

public class AbstractInventoryRequest<T, R> implements InventoryRequest<T, R> {

  private AtomicBoolean executed = new AtomicBoolean(false);
  protected Inventory inventory = null;
  private Context context = new Context();

  public AbstractInventoryRequest(Inventory inventory) {
    this.inventory = inventory;
  }

  @Override
  public T clone() {
    throw new UnsupportedOperationException();
  }

  @Override
  public R execute() throws InventoryException {
    try {
      return this.executeAsync().join();
    } catch (Exception e) {
      if (e instanceof InventoryException) {
        throw (InventoryException) e;
      } else {
        throw new InventoryException(e);
      }
    }
  }

  @Override
  public SimpleDeferred<R> executeAsync() {
    this.validate();
    if (!this.executed.getAndSet(true)) {
      return this.inventory.execute(this);
    } else {
      throw new IllegalStateException("The request already executed.");
    }
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  @Override
  public <C extends Inventory> C getInventory(Class<C> inventoryClass) {
    Inventory instance = this.inventory;
    while (true) {
      if (inventoryClass.isInstance(instance)) {
        return inventoryClass.cast(instance);
      } else if (instance instanceof InventoryDelegate) {
        InventoryDelegate delegate = (InventoryDelegate) instance;
        instance = delegate.getInventory();
        continue;
      } else {
        return null;
      }
    }
  }

  @Override
  public Context getContext() {
    return this.context;
  }

  @Override
  public T validate() throws InventoryException {
    return this.cast();
  }

  @SuppressWarnings("unchecked")
  protected T cast() {
    return (T) this;
  }

}
