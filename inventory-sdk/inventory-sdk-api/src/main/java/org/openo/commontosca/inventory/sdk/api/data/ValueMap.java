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
package org.openo.commontosca.inventory.sdk.api.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ValueMap extends ValueAccess implements Map<String, Object> {

  private final Map<String, Object> map;

  public ValueMap() {
    this(new LinkedHashMap<String, Object>());
  }

  @SuppressWarnings("unchecked")
  public ValueMap(Map<String, ?> map) {
    this.map = (Map<String, Object>) map;
  }

  public static ValueMap wrap(Object value) {
    return ValueAccess.wrap(value).as(ValueMap.class);
  }

  @Override
  public void clear() {
    this.map.clear();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    return this.map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return this.map.equals(o);
  }

  @Override
  public Object get(Object key) {
    return this.map.get(key);
  }

  public Boolean requireBoolean(String name) throws IllegalArgumentException {
    return this.requireValue(name, Boolean.class);
  }

  public Byte requireByte(String name) throws IllegalArgumentException {
    return this.requireValue(name, Byte.class);
  }

  public Double requireDouble(String name) throws IllegalArgumentException {
    return this.requireValue(name, Double.class);
  }

  public Integer requireInt(String name) throws IllegalArgumentException {
    return this.requireValue(name, Integer.class);
  }

  public ValueList requireList(String name) {
    return this.requireValue(name, ValueList.class);
  }

  public Long requireLong(String name) throws IllegalArgumentException {
    return this.requireValue(name, Long.class);
  }

  public ValueMap requireMap(String name) {
    return this.requireValue(name, ValueMap.class);
  }

  public Short requireShort(String name) throws IllegalArgumentException {
    return this.requireValue(name, Short.class);
  }

  public String requireString(String name) throws IllegalArgumentException {
    return this.requireValue(name, String.class);
  }

  public <T> T requireValue(Key<T> key) throws IllegalArgumentException {
    return this.requireValue(key.getKeyName(), key.getValueType());
  }

  public <T> T requireValue(String key, Class<T> clazz) throws IllegalArgumentException {
    Object value = this.get(key);
    if (value == null) {
      throw new IllegalArgumentException("No value for the key: " + key);
    } else {
      return this.convert(value, clazz);
    }
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  public boolean is(Object key, Class<?> clazz) {
    Object value = this.get(key);
    if (value != null) {
      return clazz.isInstance(value);
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return this.map.keySet();
  }

  public Boolean optBoolean(String name) {
    return this.optValue(name, Boolean.class);
  }

  public Boolean optBoolean(String name, Boolean fallback) {
    return this.optValue(name, fallback, Boolean.class);
  }

  public Byte optByte(String name) {
    return this.optValue(name, Byte.class);
  }

  public Byte optByte(String name, Byte fallback) {
    return this.optValue(name, fallback, Byte.class);
  }

  public Double optDouble(String name) {
    return this.optValue(name, Double.class);
  }

  public Double optDouble(String name, Double fallback) {
    return this.optValue(name, fallback, Double.class);
  }

  public Integer optInt(String name) {
    return this.optValue(name, Integer.class);
  }

  public Integer optInt(String name, Integer fallback) {
    return this.optValue(name, fallback, Integer.class);
  }

  public ValueList optList(String name) {
    return this.optValue(name, ValueList.class);
  }

  public ValueList optList(String name, ValueList fallback) {
    return this.optValue(name, fallback, ValueList.class);
  }

  public Long optLong(String name) {
    return this.optValue(name, Long.class);
  }

  public Long optLong(String name, Long fallback) {
    return this.optValue(name, fallback, Long.class);
  }

  public ValueMap optMap(String name) {
    return this.optValue(name, ValueMap.class);
  }

  public ValueMap optMap(String name, ValueMap fallback) {
    return this.optValue(name, fallback, ValueMap.class);
  }

  public Short optShort(String name) {
    return this.optValue(name, Short.class);
  }

  public Short optShort(String name, Short fallback) {
    return this.optValue(name, fallback, Short.class);
  }

  public String optString(String name) {
    return this.optValue(name, String.class);
  }

  public String optString(String name, String fallback) {
    return this.optValue(name, fallback, String.class);
  }

  public <T> T optValue(Key<T> key) {
    return this.optValue(key.getKeyName(), key.getValueType());
  }

  public <T> T optValue(Key<T> key, T defaultValue) {
    return this.optValue(key.getKeyName(), defaultValue, key.getValueType());
  }

  public <T> T optValue(String key, Class<T> clazz) {
    Object value = this.get(key);
    return this.convert(value, clazz);
  }

  public <T> T optValue(String key, T defaultValue, Class<T> clazz) {
    T result = this.optValue(key, clazz);
    if (result == null) {
      result = defaultValue;
    }
    return result;
  }

  public <T> Object put(Key<T> key, T value) {
    return this.put(key.getKeyName(), this.convert(value, key.getValueType()));
  }

  @Override
  public Object put(String key, Object value) {
    return this.map.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    this.map.putAll(m);
  }

  @Override
  public Object remove(Object key) {
    return this.map.remove(key);
  }

  @Override
  public int size() {
    return this.map.size();
  }

  @Override
  public String toString() {
    return this.map.toString();
  }

  @Override
  public Collection<Object> values() {
    return this.map.values();
  }

  public interface Key<T> {

    public String getKeyName();

    public Class<T> getValueType();

  }

}
