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
package org.openo.commontosca.inventory.sdk.support.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {

  private final AtomicLong sequenceNumber = new AtomicLong(0);
  private final AtomicInteger threadCount = new AtomicInteger(0);
  private final String prefix;

  public NamedThreadFactory(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public Thread newThread(final Runnable r) {
    final long sequence = this.sequenceNumber.incrementAndGet();
    this.threadCount.incrementAndGet();

    String name = this.prefix + "-" + sequence + "-" + this.threadCount.get();
    return new Thread(name) {
      @Override
      public void run() {
        try {
          r.run();
        } finally {
          NamedThreadFactory.this.threadCount.decrementAndGet();
        }
      }
    };
  }

}
