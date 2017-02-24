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
/**
 *
 */
package org.openo.commontosca.inventory.api.deferred;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openo.commontosca.inventory.sdk.api.deferred.AlwaysResult;
import org.openo.commontosca.inventory.sdk.api.deferred.Deferred;
import org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject;
import org.openo.commontosca.inventory.sdk.api.deferred.PipeCallback;
import org.openo.commontosca.inventory.sdk.api.deferred.State;
import org.openo.commontosca.inventory.sdk.api.function.Callback;

public class DeferredObjectTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private ExecutorService pool = Executors.newFixedThreadPool(50);

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    this.pool.shutdownNow();
  }

  @Test
  public void testMultiThreadNotifyProgress() throws Exception {
    final DeferredObject<Void, Throwable, Boolean> deferred =
        new DeferredObject<Void, Throwable, Boolean>();
    final AtomicInteger invoker = new AtomicInteger();
    final AtomicInteger counter = new AtomicInteger();
    deferred.progress(new Callback<Boolean>() {
      @Override
      public void on(Boolean what) {
        counter.incrementAndGet();
      }
    });
    final int maxLoop = 100000;
    for (int i = 0; i < maxLoop; i++) {
      this.pool.execute(new Runnable() {
        @Override
        public void run() {
          deferred.notify(true);
          if (invoker.incrementAndGet() == maxLoop) {
            deferred.resolve(null);
          }
        }
      });
    }
    long start = System.currentTimeMillis();
    int milis = 10000;
    deferred.join(milis);
    Assert.assertEquals(invoker.get(), counter.get());
    Assert.assertTrue(
        "This process is quickly completed, so the expected waiting time should be less than the set time",
        System.currentTimeMillis() - start < milis);
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#always(org.openo.commontosca.inventory.sdk.api.function.Callback)}
   * .
   */
  @Test
  public void testAsyncAlways() throws Exception {
    {
      final AtomicReference<AlwaysResult<String, Exception>> result =
          new AtomicReference<AlwaysResult<String, Exception>>(null);
      final Deferred<String, Exception, Void> deferred =
          new DeferredObject<String, Exception, Void>();
      deferred.always(new Callback<AlwaysResult<String, Exception>>() {
        @Override
        public void on(AlwaysResult<String, Exception> what) {
          // Assert.assertEquals(State.REJECTED, what.getState());
          result.set(what);
        }
      });
      this.executor.schedule(new Runnable() {
        @Override
        public void run() {
          deferred.resolve("success");
        }
      }, 100, TimeUnit.MILLISECONDS);
      Assert.assertEquals("success", deferred.join());
      Assert.assertEquals(State.RESOLVED, result.get().getState());
      Assert.assertNull(result.get().getRejected());
      Assert.assertNotNull(result.get().getResolved());
    }
    {
      final AtomicReference<AlwaysResult<String, Exception>> result =
          new AtomicReference<AlwaysResult<String, Exception>>(null);
      final Deferred<String, Exception, Void> deferred =
          new DeferredObject<String, Exception, Void>();
      deferred.always(new Callback<AlwaysResult<String, Exception>>() {
        @Override
        public void on(AlwaysResult<String, Exception> what) {
          result.set(what);
        }
      });
      this.executor.schedule(new Runnable() {
        @Override
        public void run() {
          deferred.reject(new IllegalArgumentException());
        }
      }, 100, TimeUnit.MILLISECONDS);
      this.thrown.expect(IllegalArgumentException.class);
      Assert.assertEquals("success", deferred.join());
    }
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#fail(org.openo.commontosca.inventory.sdk.api.function.Callback)}
   * .
   */
  @Test
  public void testFail() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#notify(java.lang.Object)}
   * .
   */
  @Test
  public void testNotifyProgress() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#progress(org.openo.commontosca.inventory.sdk.api.function.Callback)}
   * .
   */
  @Test
  public void testProgress() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#reject(java.lang.Object)}
   * .
   */
  @Test
  public void testReject() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#resolve(java.lang.Object)}
   * .
   */
  @Test
  public void testResolve() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#getState()}.
   */
  @Test
  public void testState() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#always(org.openo.commontosca.inventory.sdk.api.function.Callback)}
   * .
   */
  @Test
  public void testSyncAlways() throws Exception {
    {
      final AtomicReference<AlwaysResult<String, Exception>> result =
          new AtomicReference<AlwaysResult<String, Exception>>(null);
      Deferred<String, Exception, Void> deferred = new DeferredObject<String, Exception, Void>();
      deferred.always(new Callback<AlwaysResult<String, Exception>>() {
        @Override
        public void on(AlwaysResult<String, Exception> what) {
          result.set(what);
        }
      });
      deferred.resolve("success");
      Assert.assertEquals("success", deferred.join());
      Assert.assertEquals(State.RESOLVED, result.get().getState());
      Assert.assertNull(result.get().getRejected());
      Assert.assertNotNull(result.get().getResolved());
    }
    {
      final AtomicReference<AlwaysResult<String, Exception>> result =
          new AtomicReference<AlwaysResult<String, Exception>>(null);
      Deferred<String, Exception, Void> deferred = new DeferredObject<String, Exception, Void>();
      deferred.always(new Callback<AlwaysResult<String, Exception>>() {
        @Override
        public void on(AlwaysResult<String, Exception> what) {
          result.set(what);
        }
      });
      deferred.reject(new IllegalArgumentException());
      Assert.assertEquals(State.REJECTED, result.get().getState());
      Assert.assertNull(result.get().getResolved());
      Assert.assertNotNull(result.get().getRejected());
      this.thrown.expect(IllegalArgumentException.class);
      deferred.join();
    }
  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#then(org.openo.commontosca.inventory.sdk.api.function.Callback)}
   * .
   */
  @Test
  public void testThenCallbackOfResult() {

  }

  /**
   * Test method for
   * {@link org.openo.commontosca.inventory.sdk.api.deferred.DeferredObject#then(org.openo.commontosca.inventory.sdk.api.deferred.PipeCallback)}
   * .
   */
  @Test
  public void testThenPipeCallbackOfResultTargetFailProgress() throws Exception {
    final Deferred<String, Exception, Void> stringDeferredResult =
        new DeferredObject<String, Exception, Void>();
    Deferred<Integer, Exception, Void> integerDeferredResult =
        new DeferredObject<Integer, Exception, Void>();
    integerDeferredResult =
        stringDeferredResult.then(new PipeCallback<String, Integer, Exception, Void>() {
          @Override
          public Deferred<Integer, Exception, Void> pipe(final String source) {
            final Deferred<Integer, Exception, Void> next =
                new DeferredObject<Integer, Exception, Void>();
            DeferredObjectTest.this.executor.schedule(new Runnable() {
              @Override
              public void run() {
                try {
                  next.resolve(Integer.parseInt(source, 16));
                } catch (Exception ex) {
                  next.reject(ex);
                }
              }
            }, 100, TimeUnit.MILLISECONDS);
            return next;
          }
        });
    this.executor.schedule(new Runnable() {
      @Override
      public void run() {
        stringDeferredResult.resolve("A");
      }
    }, 100, TimeUnit.MILLISECONDS);
    try {
      integerDeferredResult.join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    Assert.assertEquals((Integer) 10, integerDeferredResult.join());
  }

  @Test
  public void testTimeout() throws Exception {
    {
      final Deferred<String, Throwable, Void> deferred = DeferredObject.forTimeout(10);
      this.executor.schedule(new Runnable() {
        @Override
        public void run() {
          deferred.resolve("success");
        }
      }, 100, TimeUnit.MILLISECONDS);
      this.thrown.expect(TimeoutException.class);
      deferred.join();
    }
  }

}
