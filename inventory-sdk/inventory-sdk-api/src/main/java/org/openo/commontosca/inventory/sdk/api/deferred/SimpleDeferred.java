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
package org.openo.commontosca.inventory.sdk.api.deferred;

import org.openo.commontosca.inventory.sdk.api.function.Callback;


public interface SimpleDeferred<T> extends Deferred<T, Throwable, Float> {

  @Override
  public SimpleDeferred<T> always(Callback<AlwaysResult<T, Throwable>> callback);

  @Override
  public SimpleDeferred<T> fail(Callback<Throwable> callback);

  @Override
  public SimpleDeferred<T> reject(Throwable error);

  @Override
  public SimpleDeferred<T> resolve(T result);

  @Override
  public SimpleDeferred<T> set(State state, T result, Throwable fail);

  @Override
  public SimpleDeferred<T> progress(Callback<Float> callback);

  @Override
  public SimpleDeferred<T> then(Callback<T> callback);

  public <Target> SimpleDeferred<Target> then(SimplePipeCallback<T, Target> callback);

  public SimpleDeferred<? super T> then(SimpleDeferred<T> target);

}
