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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtils;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.ClassA;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.ClassInteger;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.ClassKV;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.ClassT;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.ClassTS;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.InterfaceA;
import org.openo.commontosca.inventory.sdk.support.utils.ClassUtilsTest.Generics.InterfaceXY;

public class ClassUtilsTest {

  @Test
  public void testDynamicCallableOfV() {
    String str = ClassUtils.dynamic(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        Method method = String.class.getMethod("concat", String.class);
        return method.invoke("a", "b");
      }
    });
    Assert.assertEquals("ab", str);
  }

  @Test
  public void testDynamicClassOfQStringObjectObjectArray() throws Exception {
    String str = "a";
    Method method = String.class.getMethod("concat", String.class);
    String result = ClassUtils.dynamic(method, str, "b");
    Assert.assertEquals("ab", result);

    Method staticMethod = Integer.class.getMethod("parseInt", String.class, int.class);
    Integer integer = ClassUtils.dynamic(staticMethod, null, "A", 16);
    Assert.assertEquals((Integer) 10, integer);
  }

  @Test
  public void testDynamicMethodObjectObjectArray() {
    String str = "a";
    String result = ClassUtils.dynamic(String.class, "concat", str, "b");
    Assert.assertEquals("ab", result);

    Integer integer = ClassUtils.dynamic(Integer.class, "parseInt", null, "A", 16);
    Assert.assertEquals((Integer) 10, integer);
  }

  @Test
  public void testFindClassIntegerGenerics() {
    ClassInteger object = new ClassInteger();
    Map<Type, Map<String, Type>> allGenericTypes = ClassUtils.findGenerics(object.getClass());
    Assert.assertEquals(Integer.class, allGenericTypes.get(InterfaceA.class).get("A"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(InterfaceXY.class).get("Y"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(ClassA.class).get("A"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(ClassKV.class).get("V"));
    Assert.assertEquals(String.class, allGenericTypes.get(InterfaceXY.class).get("X"));
    Assert.assertEquals(String.class, allGenericTypes.get(ClassKV.class).get("K"));
  }

  @Test
  public void testFindClassTGenerics() {
    ClassT<Integer> object = new ClassT<Integer>();
    Map<Type, Map<String, Type>> allGenericTypes = ClassUtils.findGenerics(object.getClass());
    Assert.assertEquals(Number.class, allGenericTypes.get(InterfaceA.class).get("A"));
    Assert.assertEquals(Number.class, allGenericTypes.get(InterfaceXY.class).get("Y"));
    Assert.assertEquals(Number.class, allGenericTypes.get(ClassA.class).get("A"));
    Assert.assertEquals(Number.class, allGenericTypes.get(ClassKV.class).get("V"));
    Assert.assertEquals(String.class, allGenericTypes.get(InterfaceXY.class).get("X"));
    Assert.assertEquals(String.class, allGenericTypes.get(ClassKV.class).get("K"));
  }

  @Test
  public void testFindClassTSExactGenerics() {
    ClassTS<Integer, String> object = new ClassTS<Integer, String>();
    Map<Type, Map<String, Type>> allGenericTypes =
        ClassUtils.findGenerics(object.getClass(), Integer.class, String.class);
    Assert.assertEquals(Integer.class, allGenericTypes.get(InterfaceA.class).get("A"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(InterfaceXY.class).get("Y"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(ClassA.class).get("A"));
    Assert.assertEquals(Integer.class, allGenericTypes.get(ClassKV.class).get("V"));
    Assert.assertEquals(String.class, allGenericTypes.get(InterfaceXY.class).get("X"));
    Assert.assertEquals(String.class, allGenericTypes.get(ClassKV.class).get("K"));
  }

  @Test
  public void testFindClassTSGenerics() {
    ClassTS<Integer, String> object = new ClassTS<Integer, String>();
    Map<Type, Map<String, Type>> allGenericTypes = ClassUtils.findGenerics(object.getClass());
    Assert.assertEquals(Number.class, allGenericTypes.get(InterfaceA.class).get("A"));
    Assert.assertEquals(Number.class, allGenericTypes.get(InterfaceXY.class).get("Y"));
    Assert.assertEquals(Number.class, allGenericTypes.get(ClassA.class).get("A"));
    Assert.assertEquals(Number.class, allGenericTypes.get(ClassKV.class).get("V"));
    Assert.assertEquals(Object.class, allGenericTypes.get(InterfaceXY.class).get("X"));
    Assert.assertEquals(Object.class, allGenericTypes.get(ClassKV.class).get("K"));
  }

  @Test
  public void testFindConstructor() {
    Constructor<String> contructor = ClassUtils.findConstructor(String.class, "ABC");
    Assert.assertNotNull(contructor);
  }

  @Test
  public void testFindMethod() {
    Method method = ClassUtils.findMethod(Integer.class, "byteValue");
    Assert.assertNotNull(method);
  }

  @Test
  public void testIsAssignable() {
    Assert.assertTrue(ClassUtils.isAssignable(Integer.class, int.class));
    Assert.assertTrue(ClassUtils.isAssignable(Integer.class, Integer.class));
  }

  @Test
  public void testIsAssignableValue() {
    Assert.assertTrue(ClassUtils.isAssignableValue(Integer.class, 1));
    Assert.assertTrue(ClassUtils.isAssignableValue(Integer.class, new Integer(1)));
  }

  @Test
  public void testNewInstance() {
    Number number = ClassUtils.newInstance(Integer.class, Integer.MAX_VALUE);
    Assert.assertEquals(Integer.MAX_VALUE, number.intValue());
  }

  static interface Generics {

    class ClassA<A extends Number> implements InterfaceA<A> {
    }

    class ClassInteger extends ClassKVA<String, Integer> {
    }

    class ClassKV<K, V extends Number> extends ClassA<V>implements InterfaceXY<K, V> {
    }

    class ClassKVA<K, V extends Number> extends ClassKV<K, V>implements InterfaceA<V> {
    }

    class ClassT<T extends Number> extends ClassKVA<String, T> {
    }

    class ClassTS<T extends Number, S> extends ClassKVA<S, T> {
    }

    interface InterfaceA<A extends Number> {
    }

    interface InterfaceXY<X, Y extends Number> {
    }

  }

}
