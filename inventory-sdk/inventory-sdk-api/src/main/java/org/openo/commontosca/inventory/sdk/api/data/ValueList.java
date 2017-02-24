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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ValueList extends ValueAccess implements List<Object> {

  private final List<Object> list;

  public ValueList() {
    this(new ArrayList<Object>());
  }

  @SuppressWarnings("unchecked")
  public ValueList(List<?> list) {
    this.list = (List<Object>) list;
  }

  public static ValueList wrap(Object value) {
    return ValueAccess.wrap(value).as(ValueList.class);
  }

  @Override
  public void add(int index, Object element) {
    this.list.add(index, element);
  }

  @Override
  public boolean add(Object e) {
    return this.list.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Object> c) {
    return this.list.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Object> c) {
    return this.list.addAll(index, c);
  }

  @Override
  public void clear() {
    this.list.clear();
  }

  @Override
  public boolean contains(Object o) {
    return this.list.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return this.list.containsAll(c);
  }

  @Override
  public boolean equals(Object o) {
    return this.list.equals(o);
  }

  @Override
  public Object get(int index) {
    return this.list.get(index);
  }

  public Boolean requireBoolean(int index) throws IllegalArgumentException {
    return this.requireValue(index, Boolean.class);
  }

  public Byte requireByte(int index) throws IllegalArgumentException {
    return this.requireValue(index, Byte.class);
  }

  public Double requireDouble(int index) throws IllegalArgumentException {
    return this.requireValue(index, Double.class);
  }

  public Integer requireInt(int index) throws IllegalArgumentException {
    return this.requireValue(index, Integer.class);
  }

  public ValueList requireList(int index) {
    return this.requireValue(index, ValueList.class);
  }

  public Long requireLong(int index) throws IllegalArgumentException {
    return this.requireValue(index, Long.class);
  }

  public ValueMap requireMap(int index) {
    return this.requireValue(index, ValueMap.class);
  }

  public Short requireShort(int index) throws IllegalArgumentException {
    return this.requireValue(index, Short.class);
  }

  public String requireString(int index) throws IllegalArgumentException {
    return this.requireValue(index, String.class);
  }

  public <T> T requireValue(int index, Class<T> clazz) throws IllegalArgumentException {
    T value = this.optValue(index, clazz);
    if (value == null) {
      throw new IllegalArgumentException("No such value: " + index);
    }
    return value;
  }

  @Override
  public int hashCode() {
    return this.list.hashCode();
  }

  @Override
  public int indexOf(Object o) {
    return this.list.indexOf(o);
  }

  public boolean is(int index, Class<?> clazz) {
    Object value = this.get(index);
    if (value != null) {
      return clazz.isInstance(value);
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return this.list.isEmpty();
  }

  @Override
  public Iterator<Object> iterator() {
    return this.list.iterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return this.list.lastIndexOf(o);
  }

  @Override
  public ListIterator<Object> listIterator() {
    return this.list.listIterator();
  }

  @Override
  public ListIterator<Object> listIterator(int index) {
    return this.list.listIterator(index);
  }

  public Boolean optBoolean(int index) {
    return this.optValue(index, Boolean.class);
  }

  public Boolean optBoolean(int index, Boolean fallback) {
    return this.optValue(index, fallback, Boolean.class);
  }

  public Byte optByte(int index) {
    return this.optValue(index, Byte.class);
  }

  public Byte optByte(int index, Byte fallback) {
    return this.optValue(index, fallback, Byte.class);
  }

  public Double optDouble(int index) {
    return this.optValue(index, Double.class);
  }

  public Double optDouble(int index, Double fallback) {
    return this.optValue(index, fallback, Double.class);
  }

  public Integer optInt(int index) {
    return this.optValue(index, Integer.class);
  }

  public Integer optInt(int index, Integer fallback) {
    return this.optValue(index, fallback, Integer.class);
  }

  public ValueList optList(int index) {
    return this.optValue(index, ValueList.class);
  }

  public ValueList optList(int index, ValueList fallback) {
    return this.optValue(index, fallback, ValueList.class);
  }

  public Long optLong(int index) {
    return this.optValue(index, Long.class);
  }

  public Long optLong(int index, Long fallback) {
    return this.optValue(index, fallback, Long.class);
  }

  public ValueMap optMap(int index) {
    return this.optValue(index, ValueMap.class);
  }

  public ValueMap optMap(int index, ValueMap fallback) {
    return this.optValue(index, fallback, ValueMap.class);
  }

  public Short optShort(int index) {
    return this.optValue(index, Short.class);
  }

  public Short optShort(int index, Short fallback) {
    return this.optValue(index, fallback, Short.class);
  }

  public String optString(int index) {
    return this.optValue(index, String.class);
  }

  public String optString(int index, String fallback) {
    return this.optValue(index, fallback, String.class);
  }

  public <T> T optValue(int index, Class<T> clazz) {
    Object value = this.get(index);
    T convert = this.convert(value, clazz);
    return convert;
  }

  public <T> T optValue(int index, T defaultValue, Class<T> clazz) {
    T result = this.optValue(index, clazz);
    if (result == null) {
      result = defaultValue;
    }
    return result;
  }

  @Override
  public Object remove(int index) {
    return this.list.remove(index);
  }

  @Override
  public boolean remove(Object o) {
    return this.list.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return this.list.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return this.list.retainAll(c);
  }

  @Override
  public Object set(int index, Object element) {
    return this.list.set(index, element);
  }

  @Override
  public int size() {
    return this.list.size();
  }

  @Override
  public List<Object> subList(int fromIndex, int toIndex) {
    return this.list.subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray() {
    return this.list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return this.list.toArray(a);
  }

  @Override
  public String toString() {
    return this.list.toString();
  }

}
