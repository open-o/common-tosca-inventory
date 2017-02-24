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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ClassUtils {

  private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap =
      new HashMap<Class<?>, Class<?>>(8);
  private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap =
      new HashMap<Class<?>, Class<?>>(8);

  static {
    ClassUtils.primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
    ClassUtils.primitiveWrapperTypeMap.put(Byte.class, byte.class);
    ClassUtils.primitiveWrapperTypeMap.put(Character.class, char.class);
    ClassUtils.primitiveWrapperTypeMap.put(Double.class, double.class);
    ClassUtils.primitiveWrapperTypeMap.put(Float.class, float.class);
    ClassUtils.primitiveWrapperTypeMap.put(Integer.class, int.class);
    ClassUtils.primitiveWrapperTypeMap.put(Long.class, long.class);
    ClassUtils.primitiveWrapperTypeMap.put(Short.class, short.class);

    for (Map.Entry<Class<?>, Class<?>> entry : ClassUtils.primitiveWrapperTypeMap.entrySet()) {
      ClassUtils.primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
    }
  }

  private ClassUtils() {}

  /**
   * 
   * @param callable
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <V> V dynamic(Callable<Object> callable) {
    try {
      return (V) callable.call();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }


  @SuppressWarnings("unchecked")
  public static <V> V dynamic(Class<?> clazz, String methodName, Object target, Object... params) {
    try {
      Method method = ClassUtils.findMethod(clazz, methodName, params);
      if (method == null) {
        throw new IllegalArgumentException(
            "No matched method: " + methodName + " param: " + Arrays.toString(params));
      }
      return (V) method.invoke(target, params);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <V> V dynamic(Field field, Object target) {
    try {
      return (V) field.get(target);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <V> V dynamic(Method method, Object target, Object... params) {
    try {
      return (V) method.invoke(target, params);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 
   * @param clazz
   * @param args
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
    Constructor<T> constructor = null;
    int diff = Integer.MAX_VALUE;
    if (args == null || args.length == 0) {
      try {
        constructor = clazz.getDeclaredConstructor();
      } catch (NoSuchMethodException e) {
      }
    } else {
      Constructor<T>[] cs = (Constructor<T>[]) clazz.getDeclaredConstructors();
      for (Constructor<T> c : cs) {
        int w = ClassUtils.getTypeDifferenceWeight(c.getParameterTypes(), args);
        if (w < diff) {
          diff = w;
          constructor = c;
        }
      }
    }
    if (constructor != null) {
      constructor.setAccessible(true);
      return constructor;
    } else {
      return null;
    }
  }

  /**
   * 
   * @param clazz
   * @param parameterTypes
   * @return
   */
  public static Map<Type, Map<String, Type>> findGenerics(Class<?> clazz,
      Class<?>... parameterTypes) {
    GenericMap allGenericTypes = new GenericMap();
    Map<String, Type> subGenericTypes = new HashMap<String, Type>();
    TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
    for (int i = 0; i < typeParameters.length; i++) {
      TypeVariable<?> typeVariable = typeParameters[i];
      if (parameterTypes.length > i) {
        subGenericTypes.put(typeVariable.getName(), parameterTypes[i]);
      } else {
        subGenericTypes.put(typeVariable.getName(), typeVariable.getBounds()[0]);
      }
    }
    allGenericTypes.put(clazz, subGenericTypes);
    ClassUtils.findGenerics(clazz, allGenericTypes, subGenericTypes);
    return allGenericTypes;
  }

  /**
   * 
   * @param clazz
   * @param methodName
   * @param parameterTypes
   * @return
   */
  public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    if (clazz == null) {
      return null;
    }
    Method targetMethod = null;
    try {
      return clazz.getDeclaredMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      Class<?>[] interfaces = clazz.getInterfaces();
      if (interfaces != null) {
        for (Class<?> interfaceClazz : interfaces) {
          targetMethod = ClassUtils.findMethod(interfaceClazz, methodName, parameterTypes);
          if (targetMethod != null) {
            return targetMethod;
          }
        }
      }
    }
    return ClassUtils.findMethod(clazz.getSuperclass(), methodName, parameterTypes);
  }

  public static Method findMethod(Class<?> clazz, String methodName, Object... params) {
    if (clazz == null) {
      return null;
    }
    Method method = null;
    Method[] declaredMethods = clazz.getDeclaredMethods();
    int diff = Integer.MAX_VALUE;
    for (Method m : declaredMethods) {
      if (m.getName().equals(methodName)) {
        int w = ClassUtils.getTypeDifferenceWeight(m.getParameterTypes(), params);
        if (w < diff) {
          diff = w;
          method = m;
        }
      }
    }
    return method;
  }

  public static int getTypeDifferenceWeight(Class<?>[] paramTypes, Object[] args) {
    if (paramTypes.length != args.length) {
      return Integer.MAX_VALUE;
    }
    int result = 0;
    for (int i = 0; i < paramTypes.length; i++) {
      if (!ClassUtils.isAssignableValue(paramTypes[i], args[i])) {
        return Integer.MAX_VALUE;
      }
      if (args[i] != null) {
        Class<?> paramType = paramTypes[i];
        Class<?> superClass = args[i].getClass().getSuperclass();
        while (superClass != null) {
          if (paramType.equals(superClass)) {
            result = result + 2;
            superClass = null;
          } else if (ClassUtils.isAssignable(paramType, superClass)) {
            result = result + 2;
            superClass = superClass.getSuperclass();
          } else {
            superClass = null;
          }
        }
        if (paramType.isInterface()) {
          result = result + 1;
        }
      }
    }
    return result;
  }

  public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
    if (lhsType.isAssignableFrom(rhsType)) {
      return true;
    }
    if (lhsType.isPrimitive()) {
      Class<?> resolvedPrimitive = ClassUtils.primitiveWrapperTypeMap.get(rhsType);
      if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
        return true;
      }
    } else {
      Class<?> resolvedWrapper = ClassUtils.primitiveTypeToWrapperMap.get(rhsType);
      if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isAssignableValue(Class<?> type, Object value) {
    return (value != null ? ClassUtils.isAssignable(type, value.getClass()) : !type.isPrimitive());
  }

  public static boolean isPresent(String className) {
    return ClassUtils.isPresent(className, Thread.currentThread().getContextClassLoader());
  }

  public static boolean isPresent(String className, ClassLoader classLoader) {
    try {
      Class.forName(className, false, classLoader);
      return true;
    } catch (Exception ignore) {
    }
    return false;
  }

  /**
   *
   * @param clazz
   * @param args
   * @return
   */
  public static <T> T newInstance(Class<T> clazz, Object... args) {
    Constructor<T> constructor = ClassUtils.findConstructor(clazz, args);
    if (constructor != null) {
      try {
        return constructor.newInstance(args);
      } catch (Exception ignore) {
      }
    }
    return null;
  }

  private static void findGenerics(Type type, Map<Type, Map<String, Type>> allGenericTypes,
      Map<String, Type> genericTypes) {
    if (type instanceof Class) {
      Class<?> typeClass = (Class<?>) type;
      ClassUtils.findGenerics(typeClass.getGenericSuperclass(), allGenericTypes, genericTypes);
      for (Type interfaceType : typeClass.getGenericInterfaces()) {
        ClassUtils.findGenerics(interfaceType, allGenericTypes, genericTypes);
      }
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Type[] argTypes = parameterizedType.getActualTypeArguments();
      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class) {
        Map<String, Type> subGenericTypes = new HashMap<String, Type>();
        Class<?> rawTypeClass = (Class<?>) rawType;
        TypeVariable<?>[] typeParameters = rawTypeClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
          String name = typeParameters[i].getName();
          Type argType = argTypes[i];
          if (argType instanceof Class) {
            subGenericTypes.put(name, argType);
          } else if (argType instanceof ParameterizedType) {
            subGenericTypes.put(name, ((ParameterizedType) argType).getRawType());
          } else if (argType instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) argType;
            Type genericType = null;
            if (genericTypes != null) {
              genericType = genericTypes.get(typeVariable.getName());
            } else {
              genericType = typeVariable.getBounds()[0];
            }
            subGenericTypes.put(name, genericType);
          }
        }
        allGenericTypes.put(rawType, subGenericTypes);
        ClassUtils.findGenerics(rawTypeClass, allGenericTypes, subGenericTypes);
      }
    }
  }

  private static class GenericMap extends LinkedHashMap<Type, Map<String, Type>> {

    private static final long serialVersionUID = 8289626281357393101L;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder(256);
      for (Map.Entry<Type, Map<String, Type>> entry : this.entrySet()) {
        builder.append(entry.getKey()).append("\r\n");
        for (Map.Entry<String, Type> e : entry.getValue().entrySet()) {
          builder.append("\t").append(e.getKey()).append(" -> ").append(e.getValue())
              .append("\r\n");
        }
      }
      return builder.toString();
    }

  }

}
