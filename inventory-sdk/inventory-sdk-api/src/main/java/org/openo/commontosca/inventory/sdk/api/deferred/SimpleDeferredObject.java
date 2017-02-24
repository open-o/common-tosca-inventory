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

import java.util.concurrent.TimeoutException;

import org.openo.commontosca.inventory.sdk.api.function.Callback;

public class SimpleDeferredObject<Result> implements SimpleDeferred<Result> {

  private final Deferred<Result, Throwable, Float> delegate;

  public SimpleDeferredObject() {
    this.delegate = new DeferredObject<Result, Throwable, Float>();
  }

  public SimpleDeferredObject(Deferred<Result, Throwable, Float> delegate) {
    this.delegate = delegate;
  }

  /**
   * @param callback
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#always(org.openo.commontosca.inventory.sdk.api.function.Callback)
   */
  @Override
  public SimpleDeferred<Result> always(Callback<AlwaysResult<Result, Throwable>> callback) {
    this.delegate.always(callback);
    return this;
  }

  /**
   * @param callback
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#fail(org.openo.commontosca.inventory.sdk.api.function.Callback)
   */
  @Override
  public SimpleDeferred<Result> fail(Callback<Throwable> callback) {
    this.delegate.fail(callback);
    return this;
  }

  /**
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#getRejected()
   */
  @Override
  public Throwable getRejected() {
    return this.delegate.getRejected();
  }

  /**
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#getResolved()
   */
  @Override
  public Result getResolved() {
    return this.delegate.getResolved();
  }

  /**
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#getState()
   */
  @Override
  public State getState() {
    return this.delegate.getState();
  }

  /**
   * @return
   * @throws InterruptedException
   * @throws Exception
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#join()
   */
  @Override
  public Result join() throws InterruptedException, Exception {
    return this.delegate.join();
  }

  /**
   * @param milis
   * @return
   * @throws InterruptedException
   * @throws TimeoutException
   * @throws Exception
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#join(long)
   */
  @Override
  public Result join(long milis) throws InterruptedException, TimeoutException, Exception {
    return this.delegate.join(milis);
  }

  /**
   * @param progress
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#notify(java.lang.Object)
   */
  @Override
  public SimpleDeferred<Result> notify(Float progress) {
    this.delegate.notify(progress);
    return this;
  }

  /**
   * @param callback
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#progress(org.openo.commontosca.inventory.sdk.api.function.Callback)
   */
  @Override
  public SimpleDeferred<Result> progress(Callback<Float> callback) {
    this.delegate.progress(callback);
    return this;
  }

  /**
   * @param error
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#reject(java.lang.Object)
   */
  @Override
  public SimpleDeferred<Result> reject(Throwable error) {
    this.delegate.reject(error);
    return this;
  }

  /**
   * @param result
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#resolve(java.lang.Object)
   */
  @Override
  public SimpleDeferred<Result> resolve(Result result) {
    this.delegate.resolve(result);
    return this;
  }

  /**
   * @param result
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#set(org.openo.commontosca.inventory.sdk.api.deferred.AlwaysResult)
   */
  @Override
  public SimpleDeferred<Result> set(State state, Result result, Throwable fail) {
    this.delegate.set(state, result, fail);
    return this;
  }

  /**
   * @param callback
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#then(org.openo.commontosca.inventory.sdk.api.function.Callback)
   */
  @Override
  public SimpleDeferred<Result> then(Callback<Result> callback) {
    this.delegate.then(callback);
    return this;
  }

  /**
   * @param target
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#then(org.openo.commontosca.inventory.sdk.api.deferred.Deferred)
   */
  @Override
  public Deferred<Result, Throwable, Float> then(Deferred<Result, Throwable, Float> target) {
    this.delegate.then(target);
    return this;
  }

  /**
   * @param callback
   * @return
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#then(org.openo.commontosca.inventory.sdk.api.deferred.PipeCallback)
   */
  @Override
  public <Target> Deferred<Target, Throwable, Float> then(
      PipeCallback<Result, Target, Throwable, Float> callback) {
    return new SimpleDeferredObject<Target>(this.delegate.then(callback));
  }

  @Override
  public <Target> SimpleDeferred<Target> then(SimplePipeCallback<Result, Target> callback) {
    return new SimpleDeferredObject<Target>(this.delegate.then(callback));
  }

  @Override
  public SimpleDeferred<Result> then(SimpleDeferred<Result> target) {
    return new SimpleDeferredObject<Result>(this.delegate.then(target));
  }

}
