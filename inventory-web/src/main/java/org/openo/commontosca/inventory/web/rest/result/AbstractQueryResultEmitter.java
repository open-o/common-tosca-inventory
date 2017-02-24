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
package org.openo.commontosca.inventory.web.rest.result;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openo.commontosca.inventory.sdk.api.data.ValueMap;
import org.openo.commontosca.inventory.sdk.api.result.QueryResult;
import org.openo.commontosca.inventory.sdk.support.DeferredResponse;
import org.openo.commontosca.inventory.sdk.support.utils.Toolkits;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public abstract class AbstractQueryResultEmitter extends ResponseBodyEmitter {

  private DeferredResponse<ServerHttpResponse> deferredOutput = new DeferredResponse<>();
  private boolean iterator;
  private QueryResult result;

  public AbstractQueryResultEmitter(QueryResult result) {
    this(result, false);
  }

  public AbstractQueryResultEmitter(QueryResult result, boolean iterator) {
    super(0L);
    this.result = result;
    this.iterator = iterator;
  }

  public void sendTo(DeferredResult<ResponseBodyEmitter> deferredResult) {
    deferredResult.setResult(this);
    this.deferredOutput.then(output -> {
      if (this.iterator) {
        try {
          this.onBegin(output);
          for (Iterator<ValueMap> iterator = this.result.asCursor(); iterator.hasNext();) {
            ValueMap data = iterator.next();
            this.onData(data);
          }
          this.onEnd();
          this.complete();
        } catch (Exception e) {
          this.completeWithError(e);
        } finally {
          Toolkits.closeQuitely(this.result);
          this.onFinally();
        }
      } else {
        AtomicBoolean first = new AtomicBoolean(true);
        this.result.forEach(data -> {
          if (first.compareAndSet(true, false)) {
            this.onBegin(output);
          }
          this.onData(data);
        } , ex -> {
          Toolkits.closeQuitely(this.result);
          if (ex != null) {
            this.completeWithError(ex);
          } else {
            try {

              if (first.compareAndSet(true, false)) {
                this.onBegin(output);
              }
              this.onEnd();
              this.complete();
            } catch (Exception e) {
              this.completeWithError(e);
            } finally {
              Toolkits.closeQuitely(this.result);
              this.onFinally();
            }
          }
        });
      }
    });
  }

  @Override
  protected void extendResponse(ServerHttpResponse outputMessage) {
    this.deferredOutput.resolve(outputMessage);
  }

  protected abstract void onBegin(ServerHttpResponse response) throws Exception;

  protected abstract void onData(ValueMap data) throws Exception;

  protected abstract void onEnd() throws Exception;

  protected abstract void onFinally();

}
