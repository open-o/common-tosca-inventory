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
package org.openo.commontosca.inventory.externalservice.msb;

import org.openo.commontosca.inventory.externalservice.entity.ServiceRegisterEntity;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;





@Path("/openoapi/microservices/v1/services")
// @Path("/api/microservices/v1/services")
public interface MicroserviceBusRest {
  @Path("")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ServiceRegisterEntity registerServce(@QueryParam("createOrUpdate") String createOrUpdate,
      ServiceRegisterEntity entity)throws Exception;
}
