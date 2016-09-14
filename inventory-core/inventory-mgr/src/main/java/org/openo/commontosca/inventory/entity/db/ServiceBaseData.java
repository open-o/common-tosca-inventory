/**
 * Copyright 2016 [ZTE] and others.
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

package org.openo.commontosca.inventory.entity.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gso_lcm_servicebaseinfo")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBaseData extends BaseData {
  @Id
  @Column(name = "serviceId")
  private String serviceId;
  @Column(name = "serviceName")
  private String serviceName;
  @Column(name = "serviceType")
  private String serviceType;
  @Column(name = "description")
  private String description;
  @Column(name = "activeStatus")
  private String activeStatus;
  @Column(name = "status")
  private String status;
  @Column(name = "creator")
  private String creator;
  @Column(name = "createTime")
  private BigInteger createTime;


}