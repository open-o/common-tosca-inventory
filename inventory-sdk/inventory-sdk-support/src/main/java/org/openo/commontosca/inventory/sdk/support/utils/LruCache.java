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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openo.commontosca.inventory.sdk.api.annotation.ThreadSafe;

@ThreadSafe
public class LruCache<K, V> {

  private final AtomicLong sizeLimit = new AtomicLong(0);
  private final AtomicLong maxHeap = new AtomicLong(0);
  private final AtomicLong currHeap = new AtomicLong(0);
  private final Map<K, V> cache;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  /**
   * 
   * @param sizeLimit
   * @param heapLimit
   */
  public LruCache(int sizeLimit, int heapLimit) {
    this.maxHeap.set(heapLimit);
    this.sizeLimit.set(sizeLimit);
    this.cache = new LinkedHashMap<K, V>((int) (sizeLimit * (4.0f / 3)), 0.75f, true) {

      private static final long serialVersionUID = 7857974390829479146L;

      @Override
      protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return LruCache.this.requestRemove(eldest.getKey(), eldest.getValue());
      }

    };
  }

  public V add(K key, V value) {
    this.lock.writeLock().lock();
    try {
      V old = this.cache.put(key, value);
      if (old != null) {
        this.currHeap.addAndGet(-this.sizeOf(key, old));
      }
      this.currHeap.addAndGet(this.sizeOf(key, value));
      return old;
    } finally {
      this.lock.writeLock().unlock();
    }
  }

  public V get(K key) {
    this.lock.readLock().lock();
    try {
      return this.cache.get(key);
    } finally {
      this.lock.readLock().unlock();
    }
  }

  public boolean contains(K key) {
    this.lock.readLock().lock();
    try {
      return this.cache.containsKey(key);
    } finally {
      this.lock.readLock().unlock();
    }
  }

  public void remove(K key) {
    this.lock.writeLock().lock();
    try {
      V old = this.cache.remove(key);
      if (old != null) {
        this.currHeap.addAndGet(-this.sizeOf(key, old));
      }
    } finally {
      this.lock.writeLock().unlock();
    }
  }

  public void setHeapLimit(long newHeapLimit) {
    this.maxHeap.set(newHeapLimit);
  }

  public void setSizeLimit(long newSizeLimit) {
    this.sizeLimit.set(newSizeLimit);
  }

  protected int sizeOf(K key, V value) {
    return 1;
  }

  private boolean requestRemove(K key, V value) {
    boolean shouldRemove = false;
    if (this.cache.size() > this.sizeLimit.get()) {
      shouldRemove = true;
    } else if (this.currHeap.get() > this.maxHeap.get()) {
      shouldRemove = true;
    }
    if (shouldRemove) {
      this.currHeap.addAndGet(-this.sizeOf(key, value));
    }
    return shouldRemove;
  }

}
