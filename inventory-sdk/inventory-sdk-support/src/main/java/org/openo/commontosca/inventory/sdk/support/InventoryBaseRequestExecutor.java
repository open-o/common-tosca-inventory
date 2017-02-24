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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.openo.commontosca.inventory.sdk.api.deferred.AlwaysResult;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferred;
import org.openo.commontosca.inventory.sdk.api.deferred.SimpleDeferredObject;
import org.openo.commontosca.inventory.sdk.api.function.Callback;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequestExecutor;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequestHandler;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest.ReadOperation;
import org.openo.commontosca.inventory.sdk.api.request.InventoryRequest.WriteOperation;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtils;

public abstract class InventoryBaseRequestExecutor implements InventoryRequestExecutor {

  private Map<Class<?>, InventoryRequestHandler<?, ?>> handlerMap =
      new ConcurrentHashMap<Class<?>, InventoryRequestHandler<?, ?>>();
  private List<InventoryRequestHandler<?, ?>> handlerList =
      new ArrayList<InventoryRequestHandler<?, ?>>();

  private Queue<InventoryRequest<?, ?>> pendingQueue =
      new LinkedBlockingQueue<InventoryRequest<?, ?>>();
  private Map<InventoryRequest<?, ?>, TimeTracer> runningQueue =
      new ConcurrentHashMap<InventoryRequest<?, ?>, TimeTracer>();

  private Semaphore writeSemaphore = new Semaphore(1000, true);
  private Semaphore readSemaphore = new Semaphore(1000);

  public void addRequestHandler(InventoryRequestHandler<?, ?> handler) {
    this.handlerList.add(handler);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T, R> SimpleDeferred<R> execute(final InventoryRequest<T, R> request) {
    try {
      this.acquire(request);
      InventoryRequestHandler requestHandler = this.handlerMap.get(request.getClass());
      if (requestHandler == null) {
        requestHandler = this.findHandler(request);
        if (requestHandler != null) {
          this.handlerMap.put(request.getClass(), requestHandler);
        } else {
          throw new IllegalArgumentException("No expected handler: " + request);
        }
      }
      return requestHandler.handle(request).always(new Callback<AlwaysResult>() {
        @Override
        public void on(AlwaysResult what) {
          InventoryBaseRequestExecutor.this.release(request);
        }
      });
    } catch (Throwable ex) {// NOSONAR
      this.release(request);
      return new SimpleDeferredObject<R>().reject(ex);
    }
  }

  private void acquire(InventoryRequest<?, ?> request) throws InterruptedException {
    if (request instanceof ReadOperation) {
      this.readSemaphore.acquire();
    } else if (request instanceof WriteOperation) {
      this.writeSemaphore.acquire();
    } else {
      throw new IllegalArgumentException(
          "The request must be the WriteOperation or ReadOperation: " + request);
    }
  }

  private InventoryRequestHandler<?, ?> findHandler(InventoryRequest<?, ?> request) {
    InventoryRequestHandler<?, ?> requestHandler = null;
    int diff = Integer.MAX_VALUE;
    for (InventoryRequestHandler<?, ?> handler : this.handlerList) {
      Map<Type, Map<String, Type>> generics = ClassUtils.findGenerics(handler.getClass());
      int w = ClassUtils.getTypeDifferenceWeight(
          new Class<?>[] {(Class<?>) generics.get(InventoryRequestHandler.class).get("T")},
          new Object[] {request});
      if (w < diff) {
        diff = w;
        requestHandler = handler;
      }
    }
    return requestHandler;
  }

  private void release(InventoryRequest<?, ?> request) {
    if (request instanceof ReadOperation) {
      this.readSemaphore.release();
    } else if (request instanceof WriteOperation) {
      this.writeSemaphore.release();
    } else {
      throw new IllegalArgumentException(
          "The request must be the WriteOperation or ReadOperation: " + request);
    }
  }

  private static class TimeTracer {

    long startTime;

  }

}
