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
package org.openo.commontosca.inventory.core.context;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class InventoryApplicationContext implements ApplicationContextAware, DisposableBean {

  private static volatile CompletableFuture<ApplicationContext> APPLICATION_CONTEXT_FUTURE =
      new CompletableFuture<ApplicationContext>();

  public static ApplicationContext get() {
    try {
      return InventoryApplicationContext.APPLICATION_CONTEXT_FUTURE.get();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void destroy() throws Exception {
    InventoryApplicationContext.APPLICATION_CONTEXT_FUTURE =
        new CompletableFuture<ApplicationContext>();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    InventoryApplicationContext.APPLICATION_CONTEXT_FUTURE.complete(applicationContext);
  }

}
