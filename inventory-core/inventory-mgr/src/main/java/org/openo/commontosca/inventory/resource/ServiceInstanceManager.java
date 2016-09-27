/**
 * Copyright 2016 ZTE Corporation.
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
package org.openo.commontosca.inventory.resource;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.eclipse.jetty.http.HttpStatus;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceInfo;
import org.openo.commontosca.inventory.entity.rest.ServiceInstanceQueryCondition;
import org.openo.commontosca.inventory.exception.InventoryException;
import org.openo.commontosca.inventory.handle.ServiceInstanceHandler;
import org.openo.commontosca.inventory.util.InventoryDbUtil;
import org.openo.commontosca.inventory.util.RestResponseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/services")
@Api(tags = {" serices Management "})
public class ServiceInstanceManager {

  ServiceInstanceHandler handler = new ServiceInstanceHandler();
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInstanceManager.class);


  /**
   * query service instance info by condition.
   */
  @Path("/")
  @POST
  @ApiOperation(value = "get service instance by condition")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponses(value = {
      @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found",
          response = String.class),
      @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415,
          message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
      @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error",
          response = String.class)})
  @Timed
  public Response queryServiceInstanceByCondition(
      @ApiParam(value = "condition ", required = true) ServiceInstanceQueryCondition condition) {
    LOGGER.info("start query service instances " + " condition:"
        + InventoryDbUtil.objectToString(condition));

    ArrayList<ServiceInstanceInfo> instances = null;
    try {
      instances = handler.getServiceInstanceByCondition(condition);
    } catch (InventoryException error) {
      LOGGER.error("query service instances error.errorMsg:" + error.getErrorMsg());
      return RestResponseUtil.getErrorResponse(error);
    }
    if (instances == null || instances.size() <= 0) {
      LOGGER.warn("query service instances end.no match condition record");
      return RestResponseUtil.getSuccessResponse(null);
    } else {
      LOGGER.error("query service instances end.size:" + instances.size());
      return RestResponseUtil.getSuccessResponse(instances);
    }

  }


}
