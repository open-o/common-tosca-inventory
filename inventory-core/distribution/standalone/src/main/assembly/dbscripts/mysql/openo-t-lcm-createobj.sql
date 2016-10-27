--
-- Copyright 2016 ZTE Corporation.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

use inventory; 
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS t_lcm_servicebaseinfo; 
SET FOREIGN_KEY_CHECKS = 1;
CREATE TABLE t_lcm_servicebaseinfo ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    serviceName       VARCHAR(255)      NOT NULL,
    serviceType       VARCHAR(20)       NOT NULL,
    description       VARCHAR(255)      NULL, 
    activeStatus      VARCHAR(20)       NOT NULL, 
    status            VARCHAR(20)       NOT NULL, 
    creator           VARCHAR(50)       NOT NULL,
    createTime       BIGINT            NOT NULL,
    CONSTRAINT t_lcm_servicebaseinfo PRIMARY KEY(serviceId)
); 
DROP TABLE IF EXISTS t_lcm_defPackage_mapping; 
CREATE TABLE t_lcm_defPackage_mapping ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    serviceDefId      VARCHAR(255)      NOT NULL, 
    templateId        VARCHAR(255)      NOT NULL, 
    templateName      VARCHAR(255)       NOT NULL,
	CONSTRAINT t_lcm_defPackage_mapping PRIMARY KEY(serviceId),
	CONSTRAINT t_lcm_defPackage_mapping FOREIGN KEY (serviceId) REFERENCES t_lcm_servicebaseinfo (serviceId)
); 
DROP TABLE IF EXISTS t_lcm_inputParam_mapping; 
CREATE TABLE t_lcm_inputParam_mapping ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    inputKey          VARCHAR(255)      NOT NULL, 
    inputValue        VARCHAR(255)      NULL,
	CONSTRAINT t_lcm_inputParam_mapping PRIMARY KEY(serviceId,inputKey),
	CONSTRAINT t_lcm_inputParam_mapping FOREIGN KEY (serviceId) REFERENCES t_lcm_servicebaseinfo (serviceId)
); 


