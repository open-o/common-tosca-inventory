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

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.api.data.ValueList;
import org.openo.commontosca.inventory.sdk.api.data.ValueMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonUtils {

  private static final Gson GSON =
      new GsonBuilder().registerTypeAdapterFactory(new CustomTypeAdapterFactory()).create();

  @SuppressWarnings("unchecked")
  public static <T extends JsonElement> T copy(T json) {
    JsonElement je = null;
    if (json instanceof JsonElement) {
      je = json;
    } else {
      je = GsonUtils.GSON.toJsonTree(json);
    }
    return (T) GsonUtils.GSON.fromJson(je, JsonElement.class);
  }

  public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
    return GsonUtils.GSON.fromJson(json, classOfT);
  }

  public static <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
    return GsonUtils.GSON.fromJson(json, typeOfT);
  }

  public static <T> T fromJson(JsonReader reader, Type typeOfT)
      throws JsonIOException, JsonSyntaxException {
    return GsonUtils.GSON.fromJson(reader, typeOfT);
  }

  public static <T> T fromJson(Reader json, Class<T> classOfT)
      throws JsonSyntaxException, JsonIOException {
    return GsonUtils.GSON.fromJson(json, classOfT);
  }

  public static <T> T fromJson(Reader json, Type typeOfT)
      throws JsonIOException, JsonSyntaxException {
    return GsonUtils.GSON.fromJson(json, typeOfT);
  }

  public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
    return GsonUtils.GSON.fromJson(json, classOfT);
  }

  public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
    return GsonUtils.GSON.fromJson(json, typeOfT);
  }

  public static <T> TypeAdapter<T> getAdapter(Class<T> type) {
    return GsonUtils.GSON.getAdapter(type);
  }

  public static <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
    return GsonUtils.GSON.getAdapter(type);
  }

  public static Gson getGson() {
    return GsonUtils.GSON;
  }

  public static String toJson(JsonElement jsonElement) {
    return GsonUtils.GSON.toJson(jsonElement);
  }

  public static void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
    GsonUtils.GSON.toJson(jsonElement, writer);
  }

  public static void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
    GsonUtils.GSON.toJson(jsonElement, writer);
  }

  public static String toJson(Object src) {
    return GsonUtils.GSON.toJson(src);
  }

  public static void toJson(Object src, Appendable writer) throws JsonIOException {
    GsonUtils.GSON.toJson(src, writer);
  }

  public static String toJson(Object src, Type typeOfSrc) {
    return GsonUtils.GSON.toJson(src, typeOfSrc);
  }

  public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
    GsonUtils.GSON.toJson(src, typeOfSrc, writer);
  }

  public static void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
    GsonUtils.GSON.toJson(src, typeOfSrc, writer);
  }

  public static JsonElement toJsonTree(Object src) {
    return GsonUtils.GSON.toJsonTree(src);
  }

  public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
    return GsonUtils.GSON.toJsonTree(src, typeOfSrc);
  }

  private static class CustomTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
      final Class<? super T> rawType = type.getRawType();
      if (ValueAccess.class.isAssignableFrom(rawType)) {
        return (TypeAdapter<T>) new ValueAccessTypeAdapter(gson);
      } else if (rawType == Date.class) {
        return (TypeAdapter<T>) new DateTypeAdapter(rawType);
      } else if (rawType == java.sql.Date.class) {
        return (TypeAdapter<T>) new DateTypeAdapter(rawType);
      } else if (rawType == Timestamp.class) {
        return (TypeAdapter<T>) new DateTypeAdapter(rawType);
      }
      return null;
    }

    private static final class DateTypeAdapter extends TypeAdapter<Date> {

      private static final SmartDateParser DATE_PARSER = new SmartDateParser();

      private Class<?> dateType;

      public DateTypeAdapter(Class<?> dateType) {
        this.dateType = dateType;
      }

      @Override
      public Date read(JsonReader in) throws IOException {
        Long value = null;
        try {
          switch (in.peek()) {
            case NUMBER: {
              value = in.nextLong();
              break;
            }
            case NULL: {
              value = null;
              break;
            }
            case STRING: {
              value = DateTypeAdapter.DATE_PARSER.parse(in.nextString()).getTime();
              break;
            }
            default: {
              throw new IllegalArgumentException("Can not parse the date: " + in.peek());
            }
          }
        } catch (ParseException ignore) {
        }
        if (value != null) {
          if (this.dateType == Date.class) {
            return new Date(value);
          } else if (this.dateType == java.sql.Date.class) {
            return new java.sql.Date(value);
          } else if (this.dateType == Timestamp.class) {
            return new Timestamp(value);
          } else {
            throw new IllegalArgumentException("Incorrect date type: " + this.dateType);
          }
        } else {
          return null;
        }
      }

      @Override
      public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(SmartDateParser.getSharedFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(value));
        }
      }

    }

    private static final class ValueAccessTypeAdapter extends TypeAdapter<ValueAccess> {

      private final Gson gson;

      private ValueAccessTypeAdapter(Gson gson) {
        this.gson = gson;
      }

      @Override
      public ValueAccess read(JsonReader in) throws IOException {
        switch (in.peek()) {
          case BEGIN_ARRAY: {
            return ValueList.wrap(this.gson.getAdapter(List.class).read(in));
          }
          case BEGIN_OBJECT: {
            return ValueMap.wrap(this.gson.getAdapter(Map.class).read(in));
          }
          default: {
            return ValueAccess.wrap(this.gson.getAdapter(Object.class).read(in));
          }
        }
      }

      @Override
      public void write(JsonWriter out, ValueAccess value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else if (value.is(Map.class)) {
          this.gson.getAdapter(Map.class).write(out, value.as(Map.class));
        } else if (value.is(List.class)) {
          this.gson.getAdapter(List.class).write(out, value.as(List.class));
        } else {
          this.gson.getAdapter(Object.class).write(out, value.as(Object.class));
        }
      }
    }

  }

}
