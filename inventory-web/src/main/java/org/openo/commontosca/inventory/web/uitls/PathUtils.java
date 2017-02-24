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
package org.openo.commontosca.inventory.web.uitls;

import java.io.File;

public class PathUtils {
  /**
   * 
   */
  public static String getAbsoluteJar() {
    File file = getFile();
    if (file == null)
      return null;
    return file.getAbsolutePath();
  }

  public static String getJarDir() {
    File file = getFile();
    if (file == null)
      return null;
    String path = file.getParent();
    if (path.startsWith("file:")) {
      path = path.substring(path.indexOf(File.separator) + 1);
    }
    return path;
  }

  public static String getJarName() {
    File file = getFile();
    if (file == null)
      return null;
    return file.getName();
  }

  private static File getFile() {
    String path = PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    try {
      path = java.net.URLDecoder.decode(path, "UTF-8");
    } catch (java.io.UnsupportedEncodingException e) {
      return null;
    }
    return new File(path);
  }
}
