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
package org.openo.commontosca.inventory.sdk.api.result;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.annotation.Asynchronous;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.function.Apply;
import org.openo.commontosca.inventory.sdk.api.function.Callback;

public interface QueryResult extends Result, Closeable {



  public Cursor<ValueMap> asCursor();


  public List<ValueMap> asList();

  /**
   * 
   * 
   * @return
   */
  public ValueMap asOne();

  @Override
  public void close();

  /**
   * 
   * @param apply
   * @param over
   */
  @Asynchronous
  public void forEach(Apply<ValueMap> apply, Callback<Throwable> over);

  public interface Cursor<T> extends Iterator<T>, Closeable {

    /**
     * 
     * @see QueryResult#close()
     */
    public void close();

  }

}
