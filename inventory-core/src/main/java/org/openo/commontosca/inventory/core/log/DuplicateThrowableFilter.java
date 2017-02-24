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
package org.openo.commontosca.inventory.core.log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openo.commontosca.inventory.sdk.support.utils.LruCache;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

public class DuplicateThrowableFilter extends TurboFilter {

  private static final int MAX = 100;

  private LruCache<Object, Void> cache =
      new LruCache<>(DuplicateThrowableFilter.MAX, DuplicateThrowableFilter.MAX);

  @Override
  public FilterReply decide(Marker marker, Logger logger, Level level, String format,
      Object[] params, Throwable t) {
    if (t != null && logger.isEnabledFor(level)) {
      Object key = new CacheKey(t);
      if (this.cache.contains(key)) {
        logger.log(marker, Logger.FQCN, Level.toLocationAwareLoggerInteger(level), format, params,
            null);
        return FilterReply.DENY;
      } else {
        this.cache.add(key, null);
      }
    }
    return FilterReply.NEUTRAL;
  }

  @Override
  public void start() {
    super.start();
    Set<String> frameworkPackageSet = new HashSet<>();
    LoggerContext context = (LoggerContext) this.getContext();
    List<String> frameworkPackages = context.getFrameworkPackages();
    frameworkPackageSet.addAll(frameworkPackages);
    frameworkPackageSet.add("org.apache.juli.logging");
    frameworkPackageSet.add("ch.qos.logback");
    frameworkPackageSet.add("org.slf4j");
    frameworkPackageSet.add("java.util.logging");
    frameworkPackageSet.add(DuplicateThrowableFilter.class.getName());
    frameworkPackages.clear();
    frameworkPackages.addAll(frameworkPackageSet);
  }

  private static class CacheKey {

    private final StackTraceElement element;

    public CacheKey(Throwable ex) {

      this.element = ex.getStackTrace()[0];
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof CacheKey) {
        return this.element == ((CacheKey) obj).element;
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(this.element);
    }

  }

}
