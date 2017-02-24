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
package org.openo.commontosca.inventory.core.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateMatcher {
  private static List<String> patternDate = new ArrayList<String>();

  static {
    DateMatcher.patternDate.add("yyyyMMdd HH:mm:ss");
    DateMatcher.patternDate.add("yyyy/MM/dd HH:mm:ss");
    DateMatcher.patternDate.add("yyyy-MM-dd HH:mm:ss");
    DateMatcher.patternDate.add("yyyy-MM-dd");
    DateMatcher.patternDate.add("yyyy/MM/dd");
    DateMatcher.patternDate.add("yyyyMMddHHmmss");
    DateMatcher.patternDate.add("yyyyMMddHHmm");
    DateMatcher.patternDate.add("yyyyMMddHH");
    DateMatcher.patternDate.add("yyyyMMdd");
    DateMatcher.patternDate.add("yyyyMM");
  }

  public static boolean isMatch(String dateStr) {
    for (String pattern : DateMatcher.patternDate) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
      try {
        Date target = dateFormat.parse(dateStr);
        if (target != null) {
          return true;
        }
      } catch (ParseException ignore) {
      }
    }
    return false;
  }
}
