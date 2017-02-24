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

import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.openo.commontosca.inventory.sdk.api.function.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredObject<Result, Fail, Progress> implements Deferred<Result, Fail, Progress> {

  private static final boolean DEBUG = false;

  private static final Logger LOGGER = LoggerFactory.getLogger(DeferredObject.class);
  private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
      Executors.newSingleThreadScheduledExecutor();

  private static final int CALLBACK_THEN = 0;
  private static final int CALLBACK_FAIL = 1;
  private static final int CALLBACK_PROGRESS = 2;
  private static final int CALLBACK_ALWAYS = 3;

  private AtomicReference<State> state = new AtomicReference<State>(State.PENDING);
  private AtomicBoolean locker = new AtomicBoolean();
  private Queue<CallbackHolder> callbackHolders = new ConcurrentLinkedQueue<CallbackHolder>();
  private volatile Result result;
  private volatile Fail fail;
  private DebugInfo debugInfo;

  public DeferredObject() {
    if (DeferredObject.LOGGER.isTraceEnabled() || DeferredObject.DEBUG) {
      this.setupDebug();
    }
  }

  public static <Result> Deferred<Result, Throwable, Integer> forProgress(
      Class<Result> resultClass) {
    return new DeferredObject<Result, Throwable, Integer>();
  }

  public static <Result> Deferred<Result, Throwable, Void> forSimple(Class<Result> resultClass) {
    return new DeferredObject<Result, Throwable, Void>();
  }

  /**
   * @see #forTimeout(long, ScheduledExecutorService)
   */
  public static <Result, Progress> Deferred<Result, Throwable, Progress> forTimeout(long milis) {
    return DeferredObject.forTimeout(milis, DeferredObject.SCHEDULED_EXECUTOR_SERVICE);
  }

  /**
   * 
   * @param milis
   * @param executor
   * @return
   */
  public static <Result, Progress> Deferred<Result, Throwable, Progress> forTimeout(long milis,
      ScheduledExecutorService executor) {
    final TimeoutException ex = new TimeoutException("Deferred object timeout.");
    final DeferredObject<Result, Throwable, Progress> deferred =
        new DeferredObject<Result, Throwable, Progress>();
    if (milis > 0) {
      final ScheduledFuture<?> schedule = executor.schedule(new Runnable() {
        @Override
        public void run() {
          if (deferred.getState() == State.PENDING) {
            deferred.reject(ex);
          }
        }
      }, milis, TimeUnit.MILLISECONDS);
      deferred.always(new Callback<AlwaysResult<Result, Throwable>>() {
        @Override
        public void on(AlwaysResult<Result, Throwable> what) {
          schedule.cancel(false);
        }
      });
    }
    return deferred;
  }

  public static <Result, Fail, Progress> Deferred<Result, Fail, Progress> ofFail(Fail fail) {
    return new DeferredObject<Result, Fail, Progress>().reject(fail);
  }

  public static <Result, Fail, Progress> Deferred<Result, Fail, Progress> ofResult(Result result) {
    return new DeferredObject<Result, Fail, Progress>().resolve(result);
  }

  @Override
  public Deferred<Result, Fail, Progress> always(Callback<AlwaysResult<Result, Fail>> callback) {
    this.addCallback(callback, DeferredObject.CALLBACK_ALWAYS);
    return this;

  }

  @Override
  public Deferred<Result, Fail, Progress> fail(Callback<Fail> callback) {
    switch (this.getState()) {
      case REJECTED:
      case PENDING: {
        this.addCallback(callback, DeferredObject.CALLBACK_FAIL);
        break;
      }
      case RESOLVED: {
        break;
      }
    }
    return this;
  }

  @Override
  public Fail getRejected() {
    return this.fail;
  }

  @Override
  public Result getResolved() {
    return this.result;
  }

  @Override
  public State getState() {
    return this.state.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#join()
   */
  @Override
  public Result join() throws InterruptedException, Exception {
    return this.join(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openo.commontosca.inventory.sdk.api.deferred.Deferred#join(long)
   */
  @Override
  public Result join(long milis) throws InterruptedException, Exception {
    if (this.getState() == State.PENDING) {
      final Object locker = new Object();
      synchronized (locker) {
        if (this.getState() == State.PENDING) {
          this.always(new Callback<AlwaysResult<Result, Fail>>() {
            @Override
            public void on(AlwaysResult<Result, Fail> what) {
              synchronized (locker) {
                locker.notifyAll();
              }
            }
          });
          if (this.getState() == State.PENDING) {
            locker.wait(milis);
          }
        }
      }
    }
    switch (this.getState()) {
      case RESOLVED: {
        return this.result;
      }
      case REJECTED: {
        if (this.fail instanceof Exception) {
          throw (Exception) this.fail;
        } else if (this.fail instanceof Error) {
          throw (Error) this.fail;
        } else {
          throw new Exception("Deferred object is rejected: " + this.fail);
        }
      }
      case PENDING: {
        TimeoutException ex = new TimeoutException("Deferred object timeout.");
        if (this.debugInfo != null) {
          ex.initCause(this.debugInfo.allocateStack);
        }
        throw ex;
      }
    }
    return this.result;
  }

  @Override
  public Deferred<Result, Fail, Progress> notify(Progress progress) {
    if (this.getState() != State.PENDING) {
      throw new IllegalStateException("The deferred is not pending for notify");
    }
    this.fireCallbacks(DeferredObject.CALLBACK_PROGRESS, progress);
    return this;
  }

  @Override
  public Deferred<Result, Fail, Progress> progress(Callback<Progress> callback) {
    switch (this.getState()) {
      case PENDING: {
        this.addCallback(callback, DeferredObject.CALLBACK_PROGRESS);
        break;
      }
      case REJECTED:
      case RESOLVED: {
        break;
      }
    }
    return this;
  }

  @Override
  public Deferred<Result, Fail, Progress> reject(Fail error) {
    return this.set(State.REJECTED, null, error);
  }

  @Override
  public Deferred<Result, Fail, Progress> resolve(Result result) {
    return this.set(State.RESOLVED, result, null);
  }

  @Override
  public Deferred<Result, Fail, Progress> set(State state, Result result, Fail fail) {
    while (true) {
      if (this.locker.compareAndSet(false, true)) {
        if (state == State.PENDING || !this.state.compareAndSet(State.PENDING, state)) {
          this.locker.set(false);
          throw new IllegalStateException(
              "Can not set the state " + this.state.get() + " to " + state);
        }
        this.result = result;
        this.fail = fail;
        this.locker.set(false);
        if (state == State.RESOLVED) {
          this.fireCallbacks(DeferredObject.CALLBACK_THEN, result);
        } else {
          this.fireCallbacks(DeferredObject.CALLBACK_FAIL, fail);
        }
        break;
      }
    }
    return this;
  }

  @Override
  public Deferred<Result, Fail, Progress> then(Callback<Result> callback) {
    switch (this.getState()) {
      case PENDING:
      case RESOLVED: {
        this.addCallback(callback, DeferredObject.CALLBACK_THEN);
        break;
      }
      case REJECTED: {
        break;
      }
    }
    return this;
  }

  @Override
  public Deferred<Result, Fail, Progress> then(final Deferred<Result, Fail, Progress> pipe) {
    this.then(new Callback<Result>() {
      @Override
      public void on(Result what) {
        pipe.resolve(what);
      }
    }).fail(new Callback<Fail>() {
      @Override
      public void on(Fail what) {
        pipe.reject(what);
      }
    }).progress(new Callback<Progress>() {
      @Override
      public void on(Progress what) {
        pipe.notify(what);
      }
    });
    return pipe;
  }

  @Override
  public <Target> Deferred<Target, Fail, Progress> then(
      final PipeCallback<Result, Target, Fail, Progress> callback) {
    final DeferredObject<Target, Fail, Progress> deferred =
        new DeferredObject<Target, Fail, Progress>();
    this.then(new Callback<Result>() {
      @Override
      public void on(Result what) {
        Deferred<Target, Fail, Progress> piped = callback.pipe(what);
        piped.then(deferred);
      }
    }).fail(new Callback<Fail>() {
      @Override
      public void on(Fail what) {
        deferred.reject(what);
      }
    }).progress(new Callback<Progress>() {
      @Override
      public void on(Progress what) {
        deferred.notify(what);
      }
    });
    return deferred;
  }

  @SuppressWarnings("unchecked")
  private void addCallback(Callback<?> callback, int type) {
    Callback<Object> callbackObject = (Callback<Object>) callback;
    State current = State.PENDING;
    while (true) {
      if (this.locker.compareAndSet(false, true)) {
        current = this.state.get();
        if (current == State.PENDING) {
          this.callbackHolders.add(new CallbackHolder(callbackObject, type));
        }
        this.locker.set(false);
        break;
      }
    }
    switch (current) {
      case PENDING: {
        break;
      }
      case RESOLVED: {
        if (type == DeferredObject.CALLBACK_THEN) {
          callbackObject.on(this.result);
        } else if (type == DeferredObject.CALLBACK_ALWAYS) {
          callbackObject
              .on(new AlwaysResult<Result, Fail>(this.getState(), this.result, this.fail));
        }
        break;
      }
      case REJECTED: {
        if (type == DeferredObject.CALLBACK_FAIL) {
          callbackObject.on(this.fail);
        } else if (type == DeferredObject.CALLBACK_ALWAYS) {
          callbackObject
              .on(new AlwaysResult<Result, Fail>(this.getState(), this.result, this.fail));
        }
        break;
      }
    }
  }

  private <T> void fireCallbacks(int type, T value) {
    this.onTestHook("fireCallbacks", "enter");
    for (Iterator<CallbackHolder> iter = this.callbackHolders.iterator(); iter.hasNext();) {
      this.onTestHook("fireCallbacks", "enter-loop");
      CallbackHolder holder = iter.next();
      try {
        if (holder.type == DeferredObject.CALLBACK_ALWAYS
            && type != DeferredObject.CALLBACK_PROGRESS) {
          if (this.debugInfo != null) {
            this.debugInfo.remove();
          }
          AlwaysResult<Result, Fail> alwaysResult =
              new AlwaysResult<Result, Fail>(this.getState(), this.result, this.fail);
          holder.fire(alwaysResult);
        } else if (holder.type == type) {
          holder.fire(value);
        }
        if (type != DeferredObject.CALLBACK_PROGRESS) {
          iter.remove();
        }
      } catch (Error ex) { // NOSONAR
        DeferredObject.LOGGER
            .error("Deferred object uncaught exception, must be caught or use reject(error).", ex);
        throw ex;
      } catch (Exception ex) {
        DeferredObject.LOGGER
            .error("Deferred object uncaught exception, must be caught or use reject(error).", ex);
      }
    }
  }

  void onTestHook(String hook, Object... params) {

  }

  private void setupDebug() {
    this.debugInfo = new DebugInfo();
    this.debugInfo.addToTracer();
  }

  private static class CallbackHolder {

    private final Callback<Object> callback;

    final int type;

    @SuppressWarnings("unchecked")
    public CallbackHolder(Callback<?> callback, int type) {
      this.callback = (Callback<Object>) callback;
      this.type = type;
    }

    public void fire(Object value) {
      this.callback.on(value);
    }

    @Override
    public String toString() {
      return "CallbackHolder [callback=" + this.callback + ", type=" + this.type + "]";
    }

  }

  private class DebugInfo {

    private String id;
    private Throwable allocateStack;
    private long allocateTimestamp;

    public DebugInfo() {
      this.allocateStack = new Throwable("Allocate me stack");
      this.allocateTimestamp = System.currentTimeMillis();
      this.id = UUID.randomUUID().toString();
    }

    public void addToTracer() {
      long milis = 10 * 1000;
      final Exception ex = new Exception("Deferred object timeout.", this.allocateStack);
      if (milis > 0) {
        final ScheduledFuture<?> schedule =
            DeferredObject.SCHEDULED_EXECUTOR_SERVICE.schedule(new Runnable() {
              @SuppressWarnings("unchecked")
              @Override
              public void run() {
                if (DeferredObject.this.getState() == State.PENDING) {
                  try {
                    DeferredObject.this.reject((Fail) ex);
                  } catch (Exception ignore) {
                    DeferredObject.this.reject(null);
                  }
                }
              }
            }, milis, TimeUnit.MILLISECONDS);
        DeferredObject.this.always(new Callback<AlwaysResult<Result, Fail>>() {
          @Override
          public void on(AlwaysResult<Result, Fail> what) {
            schedule.cancel(false);
          }
        });
      }
    }

    public void remove() {}

  }


}
