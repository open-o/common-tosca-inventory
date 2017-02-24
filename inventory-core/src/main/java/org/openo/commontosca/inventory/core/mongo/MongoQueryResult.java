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
package org.openo.commontosca.inventory.core.mongo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.result.BaseQueryResult;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.async.AsyncBatchCursor;

public class MongoQueryResult extends BaseQueryResult implements QueryResult {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoQueryResult.class);

  private final AsyncBatchCursor<ValueMap> cursor;
  private AtomicBoolean used = new AtomicBoolean(false);
  private AtomicBoolean cursorReading = new AtomicBoolean(false);
  private AtomicBoolean shouldCloseCursor = new AtomicBoolean(false);

  public MongoQueryResult(AsyncBatchCursor<ValueMap> cursor) {
      this.cursor = cursor;
      this.cursor.setBatchSize(100);
  }

  @Override
  public Cursor<ValueMap> asCursor() {
      if (!this.used.compareAndSet(false, true)) {
          throw new IllegalStateException("The result already used, can not access again.");
      }
      return new ResultCursor();
  }

  @Override
  public void close() {
      this.shouldCloseCursor.set(true);
      if (!this.cursor.isClosed()) {
          if (!this.cursorReading.get()) {
              this.cursor.close();
          }
      }
  }

  @Override
  protected void finalize() throws Throwable { // NOSONAR
      try {
          if (!this.cursor.isClosed()) {
              MongoQueryResult.LOGGER.error("The query result must be closed. Some where cause the leak.");
              this.close();
          }
      } catch (Exception ignore) {
      }
      super.finalize();
  }

  private void loopCursor(Function<List<ValueMap>, Boolean> func, Consumer<Throwable> fail) {
      if (!this.cursor.isClosed() && this.cursorReading.compareAndSet(false, true) && !this.shouldCloseCursor.get()) {
          this.cursor.next((datas, ex) -> {
              this.cursorReading.set(false);
              if (this.shouldCloseCursor.get()) {
                  this.close();
              }
              if (datas != null) {
                  try {
                      if (func.apply(datas)) {
                          this.loopCursor(func, fail);
                      }
                  } catch (Exception e) {
                      this.close();
                      fail.accept(e);
                  }
              } else {
                  this.close();
                  fail.accept(ex);
              }
          });
      }
  }

  private class ResultCursor implements Cursor<ValueMap> {

      private BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
      private Iterator<ValueMap> iter = null;

      /* 
       * @see java.util.Iterator#hasNext()
       */
      @SuppressWarnings("unchecked")
      @Override
      public boolean hasNext() {
          if ((this.iter == null || !this.iter.hasNext()) && Collections.<ValueMap> emptyIterator() != this.iter) {
              MongoQueryResult.this.loopCursor(datas -> {
                  this.queue.add(datas.iterator());
                  return this.queue.size() < 2;
              } , ex -> {
                  if (ex != null) {
                      this.queue.add(ex);
                  } else {
                      this.queue.add(Collections.emptyIterator());
                  }
              });
              try {
                  Object take = this.queue.take();
                  if (take instanceof Throwable) {
                      throw Toolkits.toInventoryException((Throwable) take);
                  }
                  this.iter = (Iterator<ValueMap>) take;
                  return this.iter.hasNext();
              } catch (Exception e) {
                  MongoQueryResult.this.close();
                  throw Toolkits.toInventoryException(e);
              }
          }
          return this.iter.hasNext();
      }

      /* (non-Javadoc)
       * @see java.util.Iterator#next()
       */
      @Override
      public ValueMap next() {
          if (this.hasNext()) {
              return this.iter.next();
          } else {
              throw new NoSuchElementException();
          }
      }

      @Override
      public void close() {
          MongoQueryResult.this.close();
      }

  }

}