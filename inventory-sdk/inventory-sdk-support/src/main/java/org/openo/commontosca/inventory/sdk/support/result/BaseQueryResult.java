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
package org.openo.commontosca.inventory.sdk.support.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.InventoryException;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.function.Apply;
import org.openo.commontosca.inventory.sdk.api.function.Callback;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;

public abstract class BaseQueryResult implements QueryResult {

  private static final int MAX_LIST_SIZE = 100000;

  @Override
  public List<ValueMap> asList() {
    List<ValueMap> list = new ArrayList<ValueMap>();
    try {
      int count = 0;
      for (Iterator<ValueMap> iterator = this.asCursor(); iterator.hasNext();) {
        if (count++ > BaseQueryResult.MAX_LIST_SIZE) {
          throw new InventoryException(
              "The data list is too large > %d, please use iterator instead.",
              BaseQueryResult.MAX_LIST_SIZE);
        }
        ValueMap data = iterator.next();
        list.add(data);
      }
    } finally {
      Toolkits.closeQuitely(this);
    }
    return list;
  }

  @Override
  public ValueMap asOne() {
    try {
      Iterator<ValueMap> iterator = this.asCursor();
      return iterator.hasNext() ? iterator.next() : null;
    } finally {
      Toolkits.closeQuitely(this);
    }
  }

  @Override
  public void forEach(Apply<ValueMap> apply, Callback<Throwable> over) {
    try {
      for (Iterator<ValueMap> iterator = this.asCursor(); iterator.hasNext();) {
        ValueMap data = iterator.next();
        apply.apply(data);
      }
      over.on(null);
    } catch (Exception ex) {
      over.on(ex);
    }
  }

  public static class BaseCursor<T> implements Cursor<T> {

    private Iterator<T> iterator;
    private QueryResult queryResult;

    public BaseCursor(Iterator<T> iterator, QueryResult queryResult) {
      this.iterator = iterator;
      this.queryResult = queryResult;
    }

    /**
     * @return
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return this.iterator.hasNext();
    }

    /**
     * @return
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
      return this.iterator.next();
    }

    /**
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      this.iterator.remove();
    }

    @Override
    public void close() {
      this.queryResult.close();
    }

  }

}
