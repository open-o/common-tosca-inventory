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
package org.openo.commontosca.inventory.core.mongo;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.openo.commontosca.inventory.core.InventoryProperties;
import org.openo.commontosca.inventory.sdk.api.InventoryProvider;
import org.openo.commontosca.inventory.sdk.api.data.ValueAccess;
import org.openo.commontosca.inventory.sdk.support.utils.SingletonFactory;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClientSettings.Builder;
import com.mongodb.async.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SslSettings;

public class MongoInventoryProvider implements InventoryProvider<MongoInventory> {

  private InventoryProperties inventoryProperties = new InventoryProperties();
  private MongoClient client = MongoInventoryProvider
      .create(String.format("mongodb://%s/?waitQueueMultiple=10000&maxPoolSize=100",
          this.inventoryProperties.getMongo().getServer()));

  private SingletonFactory<MongoInventory> inventoryFactory =
      new SingletonFactory<MongoInventory>(() -> {
        MongoInventory inventory = new MongoInventory(this.client, "inventory");
        return inventory;
      });

  public static MongoClient create(final String url) {
    ConnectionString connectionString = new ConnectionString(url);
    Builder builder = MongoClientSettings.builder();
    builder
        .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build());
    builder.connectionPoolSettings(
        ConnectionPoolSettings.builder().applyConnectionString(connectionString).build());
    builder.serverSettings(ServerSettings.builder().build());
    builder.credentialList(connectionString.getCredentialList());
    builder.sslSettings(SslSettings.builder().applyConnectionString(connectionString).build());
    builder
        .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build());
    builder.codecRegistry(CodecRegistries.fromRegistries(MongoClients.getDefaultCodecRegistry(),
        new ObjectCodecRegistry()));
    // builder.streamFactoryFactory(new NettyStreamFactoryFactory());
    return MongoClients.create(builder.build());
  }

  @Override
  public MongoInventory getInstance() {
    return this.inventoryFactory.get();
  }

  @Override
  public long getOrder() {
    return Long.MIN_VALUE;
  }

  @Override
  public Class<MongoInventory> getSourceClass() {
    return MongoInventory.class;
  }

  private static final class ObjectCodec implements Codec<Object> {

    private CodecRegistry defaultCodecRegistry;

    /**
     * @param defaultCodecRegistry
     */
    public ObjectCodec(CodecRegistry defaultCodecRegistry) {
      this.defaultCodecRegistry = defaultCodecRegistry;
    }

    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
      if (reader.getCurrentBsonType() == BsonType.SYMBOL) {
        return reader.readSymbol();
      } else {
        return reader.readString();
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
      if (value instanceof ValueAccess) {
        Object v = ((ValueAccess) value).as(Object.class);
        Class<Object> clazz = (Class<Object>) v.getClass();
        this.defaultCodecRegistry.get(clazz).encode(writer, v, encoderContext);
      } else {
        writer.writeString(value != null ? value.toString() : null);
      }
    }

    @Override
    public Class<Object> getEncoderClass() {
      return Object.class;
    }
  }

  private static final class ObjectCodecRegistry implements CodecRegistry, CodecProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz) {
      return (Codec<T>) new ObjectCodec(MongoClients.getDefaultCodecRegistry());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return (Codec<T>) new ObjectCodec(registry);
    }
  }

}
