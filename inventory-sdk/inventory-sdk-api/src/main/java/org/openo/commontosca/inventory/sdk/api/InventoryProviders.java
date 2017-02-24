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
package org.openo.commontosca.inventory.sdk.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryProviders {

  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryProviders.class);

  @SuppressWarnings("rawtypes")
  private static ServiceLoader<InventoryProvider> SERVICE_LOADER =
      ServiceLoader.load(InventoryProvider.class);
  private static ConcurrentMap<Class<? extends Inventory>, InventoryProvider<Inventory>> SERVICE_MAP =
      new ConcurrentHashMap<Class<? extends Inventory>, InventoryProvider<Inventory>>();

  /**
   * @param InventoryInterface
   * @return
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T extends Inventory> T findService(Class<T> InventoryInterface) {
    InventoryProvider<Inventory> findProvider =
        InventoryProviders.SERVICE_MAP.get(InventoryInterface);
    if (findProvider != null) {
      return InventoryInterface.cast(findProvider.getInstance());
    } else {
      synchronized (InventoryProviders.class) {
        findProvider = InventoryProviders.SERVICE_MAP.get(InventoryInterface);
        if (findProvider != null) {
          return InventoryInterface.cast(findProvider.getInstance());
        }
        Iterator<InventoryProvider> iterator = InventoryProviders.SERVICE_LOADER.iterator();
        List<InventoryProvider<Inventory>> list = new ArrayList<InventoryProvider<Inventory>>();
        while (iterator.hasNext()) {
          list.add(iterator.next());
        }
        Collections.sort(list, new ProviderComparator());
        for (InventoryProvider<Inventory> provider : list) {
          if (InventoryInterface.isAssignableFrom(provider.getSourceClass())) {
            InventoryProviders.SERVICE_MAP.put(InventoryInterface, provider);
            InventoryProviders.LOGGER.info("Find the Inventory Provider: {}", provider);
            return InventoryInterface.cast(provider.getInstance());
          }
        }
      }
      throw new ServiceConfigurationError(
          String.format("No matched service: %s", InventoryInterface));
    }
  }

  private static class ProviderComparator implements Comparator<InventoryProvider<?>> {

    @Override
    public int compare(InventoryProvider<?> o1, InventoryProvider<?> o2) {
      if (o1.getOrder() > o2.getOrder()) {
        return -1;
      } else {
        return o1.getOrder() == o2.getOrder() ? 0 : 1;
      }
    }

  }

}
