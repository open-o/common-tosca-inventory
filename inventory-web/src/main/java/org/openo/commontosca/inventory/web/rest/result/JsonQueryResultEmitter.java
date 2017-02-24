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
/**
 *
 */
package org.openo.commontosca.inventory.web.rest.result;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;

import com.google.gson.stream.JsonWriter;


public class JsonQueryResultEmitter extends AbstractQueryResultEmitter {

  private JsonWriter writer = null;

  public JsonQueryResultEmitter(QueryResult result) {
    super(result);
  }

  /**
   * @param result
   * @param iterator
   */
  public JsonQueryResultEmitter(QueryResult result, boolean iterator) {
    super(result, iterator);
  }

  @Override
  protected void extendResponse(ServerHttpResponse outputMessage) {
    outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
    super.extendResponse(outputMessage);
  }

  @Override
  protected void onBegin(ServerHttpResponse output) throws Exception {
    this.writer = new JsonWriter(new OutputStreamWriter(output.getBody(), StandardCharsets.UTF_8));
    this.writer.beginArray();
  }

  @Override
  protected void onData(ValueMap data) throws Exception {
    GsonUtils.toJson(data, ValueMap.class, this.writer);
  }

  @Override
  protected void onEnd() throws Exception {
    this.writer.endArray();
    this.writer.flush();
  }

  @Override
  protected void onFinally() {
    Toolkits.closeQuitely(this.writer);
  }

}
