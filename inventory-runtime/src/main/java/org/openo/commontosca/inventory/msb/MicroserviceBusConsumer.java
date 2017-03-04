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
package org.openo.commontosca.inventory.msb;

import java.util.Map;

import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.openo.commontosca.inventory.config.MicroserviceConfig;
import org.openo.commontosca.inventory.sdk.support.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;



public class MicroserviceBusConsumer {
  private static final Logger LOG = LoggerFactory.getLogger(MicroserviceBusConsumer.class);

  /**
   * @param entity service entity
   * @return register service to msb success return true, else return false.
   */
  public static boolean registerService(ServiceRegisterEntity entity) {
    LOG.info("microservice register body:" + GsonUtils.toJson(entity));
    try {

      RestTemplate restTemplate = new RestTemplate(
          new HttpComponentsClientHttpRequestFactory(HttpClients.custom().useSystemProperties()
              .setDefaultCredentialsProvider(new BasicCredentialsProvider()).build()));
      DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
      uriTemplateHandler.setBaseUrl(MicroserviceConfig.getMsbServerAddr());
      restTemplate.setUriTemplateHandler(uriTemplateHandler);
      MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
      headers.add("Accept", "*/*");
      String url="http://"+MicroserviceConfig.getMsbServerAddr()+":"+MicroserviceConfig.getServiceIp()+"/openoapi/microservices/v1/services?createOrUpdate=false";
      LOG.info("microservice url:"+url);
      HttpEntity<Map> requestEntity =
          restTemplate.exchange(url,
              HttpMethod.POST, new HttpEntity<ServiceRegisterEntity>(entity, headers), Map.class);
      LOG.info(requestEntity.getBody().toString());
      return true;
    } catch (Exception e) {
      LOG.error("microservice register failed!" + e.getMessage());
      return false;
    }

  }
}
