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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import org.openo.commontosca.inventory.sdk.api.annotation.ThreadSafe;

@ThreadSafe
public class SmartDateParser {

  private static final ThreadLocal<Map<String, SimpleDateFormat>> LOCAL =
      new ThreadLocal<Map<String, SimpleDateFormat>>();

  private Queue<PatternParser> datePatternParsers = new LinkedList<PatternParser>();
  private AtomicReference<PatternParser> recentParser = new AtomicReference<PatternParser>(null);

  public SmartDateParser() {
    this.addPatternParser("yyyy-MM-dd HH:mm:ss.SSS");
    this.addPatternParser("yyyy-MM-dd HH:mm:ss");
    this.addPatternParser("yyyy-MM-dd HH:mm");
    this.addPatternParser("yyyy-MM-dd");

    this.addPatternParser("yyyy/MM/dd HH:mm:ss.SSS");
    this.addPatternParser("yyyy/MM/dd HH:mm:ss");
    this.addPatternParser("yyyy/MM/dd HH:mm");
    this.addPatternParser("yyyy/MM/dd");

    this.addPatternParser("yyyyMMdd HHmmssSSS");
    this.addPatternParser("yyyyMMdd HHmmss");
    this.addPatternParser("yyyyMMdd HHmm");
    this.addPatternParser("yyyyMMdd");

    this.addPatternParser("yyyyMMddHHmmssSSS");
    this.addPatternParser("yyyyMMddHHmmss");
    this.addPatternParser("yyyyMMddHHmm");

    this.addPatternParser("yyyyMMdd-HHmmssSSS");
    this.addPatternParser("yyyyMMdd-HHmmss");
    this.addPatternParser("yyyyMMdd-HHmm");

    this.addPatternParser("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    this.addPatternParser("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.addPatternParser("yyyy-MM-dd'T'HH:mm:ss");
    this.addPatternParser("yyyy-MM-dd'T'HH:mm");
  }

  public SmartDateParser(List<String> datePatterns) {
    for (String datePattern : datePatterns) {
      this.addPatternParser(datePattern);
    }
  }

  @ThreadSafe
  public static SimpleDateFormat getSharedFormat(String datePattern) {
    Map<String, SimpleDateFormat> shared = SmartDateParser.LOCAL.get();
    if (shared == null) {
      shared = new HashMap<String, SimpleDateFormat>();
      SmartDateParser.LOCAL.set(shared);
    }
    return SmartDateParser.getSharedFormat(datePattern, shared);
  }

  private static SimpleDateFormat getSharedFormat(String datePattern,
      Map<String, SimpleDateFormat> shared) {
    SimpleDateFormat format = shared.get(datePattern);
    if (format == null) {
      format = new SimpleDateFormat(datePattern);
      shared.put(datePattern, format);
    }
    return format;
  }

  public Date parse(String dateString) throws ParseException {
    Map<String, SimpleDateFormat> shared = SmartDateParser.LOCAL.get();
    if (shared == null) {
      shared = new HashMap<String, SimpleDateFormat>();
      SmartDateParser.LOCAL.set(shared);
    }
    Date date = null;
    PatternParser recent = this.recentParser.get();
    if (recent != null) {
      date = recent.parse(dateString, shared);
    }
    if (date == null) {
      for (Iterator<PatternParser> iter = this.datePatternParsers.iterator(); iter.hasNext();) {
        PatternParser parser = iter.next();
        date = parser.parse(dateString, shared);
        if (date != null) {
          this.recentParser.set(parser);
          break;
        }
      }
    }
    if (date == null) {
      throw new ParseException(
          "Can not parse the date string: " + dateString + ", patterns: " + this.datePatternParsers,
          0);
    }
    return date;
  }

  String getLastMatched() {
    return this.recentParser.get() != null ? this.recentParser.get().toString() : null;
  }

  private void addPatternParser(String datePattern) {
    this.datePatternParsers.add(new PatternParser(datePattern));
  }

  private static class PatternParser {

    private String datePattern;

    /**
     * @param datePattern
     * @param dateStringMinLength
     */
    public PatternParser(String datePattern) {
      this.datePattern = datePattern;
    }

    public Date parse(String dateString, Map<String, SimpleDateFormat> shared) {
      SimpleDateFormat dateFormat = SmartDateParser.getSharedFormat(this.datePattern, shared);
      try {
        Date date = dateFormat.parse(dateString);

        if (dateFormat.format(date).equals(dateString)) {
          return date;
        } else {
          return null;
        }
      } catch (ParseException ignore) {
      }
      return null;
    }

    @Override
    public String toString() {
      return this.datePattern.toString();
    }

  }

}
