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

public interface Deferred<Result, Fail, Progress> {

  /**
   * 
   * @param callback
   * @return
   */
  public Deferred<Result, Fail, Progress> always(Callback<AlwaysResult<Result, Fail>> callback);

  /**
   * 
   * 
   * @param callback
   * @return
   * @see #reject(Object)
   */
  public Deferred<Result, Fail, Progress> fail(Callback<Fail> callback);

  /**
   * 
   * 
   * @param progress
   * @return
   */
  public Deferred<Result, Fail, Progress> notify(Progress progress);

  /**
   *
   * 
   * @param callback
   * @return
   * @see #notify(Object)
   */
  public Deferred<Result, Fail, Progress> progress(Callback<Progress> callback);

  /**
   * 
   * 
   * @param error
   * @return
   */
  public Deferred<Result, Fail, Progress> reject(Fail error);

  /**
   * 
   * 
   * @param result
   * @return
   */
  public Deferred<Result, Fail, Progress> resolve(Result result);

  public Deferred<Result, Fail, Progress> set(State state, Result result, Fail fail);

  public State getState();

  /**
   * 
   * @return
   */
  public Result getResolved();

  /**
   * 
   * 
   * @return
   */
  public Fail getRejected();


  public Result join() throws InterruptedException, Exception;

  /**
   * 
   * 
   * @param milis
   * @return
   * @throws Exception
   */
  public Result join(long milis) throws InterruptedException, TimeoutException, Exception;

  /**
   * 
   * 
   * @param callback
   * @return
   * @see #resolve(Object)
   */
  public Deferred<Result, Fail, Progress> then(Callback<Result> callback);

  /**
   * 
   * 
   * @param callback
   * @return
   */
  public <Target> Deferred<Target, Fail, Progress> then(
      PipeCallback<Result, Target, Fail, Progress> callback);

  /**
   * 
   * @param target
   * @return target
   */
  public Deferred<Result, Fail, Progress> then(Deferred<Result, Fail, Progress> target);

}
