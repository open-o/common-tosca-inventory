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
package org.openo.commontosca.inventory.sdk.support.utils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openo.commontosca.inventory.sdk.api.InventoryException;

public class Toolkits {

  public static boolean isEmpty(String str) {
    boolean isEmpty = false;
    if (null == str || str.trim().length() == 0) {
      isEmpty = true;
    }
    return isEmpty;
  }

  public static <T> List<T> toList(Iterator<T> iter) {
    ArrayList<T> list = new ArrayList<T>();
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T findCause(Throwable ex, Class<T> target) {
    if (ex != null) {
      Throwable cause = ex;
      do {
        if (target.isInstance(cause)) {
          return (T) cause;
        }
      } while ((cause = cause.getCause()) != null);
    }
    return null;
  }

  public static InventoryException toInventoryException(Throwable ex) {
    if (ex instanceof InventoryException) {
      return (InventoryException) ex;
    } else if (ex instanceof Error) {
      throw (Error) ex;
    } else {
      return new InventoryException(ex);
    }
  }

  public static void closeQuitely(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception ignore) {
      }
    }
  }

}
