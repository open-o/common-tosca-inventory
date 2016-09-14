--
--
-- Copyright 2016 [ZTE] and others.
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
DROP TABLE IF EXISTS gso_lcm_servicebaseinfo; 
SET FOREIGN_KEY_CHECKS = 1;
CREATE TABLE gso_lcm_servicebaseinfo ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    serviceName       VARCHAR(255)      NOT NULL,
    serviceType       VARCHAR(20)       NOT NULL,
    description       VARCHAR(255)      NULL, 
    activeStatus      VARCHAR(20)       NOT NULL, 
    status            VARCHAR(20)       NOT NULL, 
    creator           VARCHAR(50)       NOT NULL,
    createTime       BIGINT            NOT NULL,
    CONSTRAINT gso_lcm_servicebaseinfo PRIMARY KEY(serviceId)
); 
DROP TABLE IF EXISTS gso_lcm_defPackage_mapping; 
CREATE TABLE gso_lcm_defPackage_mapping ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    serviceDefId      VARCHAR(255)      NOT NULL, 
    templateId        VARCHAR(255)      NOT NULL, 
    templateName      VARCHAR(20)       NOT NULL,
	CONSTRAINT gso_lcm_defPackage_mapping PRIMARY KEY(serviceId),
	CONSTRAINT gso_lcm_defPackage_mapping FOREIGN KEY (serviceId) REFERENCES gso_lcm_servicebaseinfo (serviceId)
); 
DROP TABLE IF EXISTS gso_lcm_inputParam_mapping; 
CREATE TABLE gso_lcm_inputParam_mapping ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    inputKey          VARCHAR(255)      NOT NULL, 
    inputValue        VARCHAR(255)      NULL,
	CONSTRAINT gso_lcm_inputParam_mapping PRIMARY KEY(serviceId,inputKey),
	CONSTRAINT gso_lcm_inputParam_mapping FOREIGN KEY (serviceId) REFERENCES gso_lcm_servicebaseinfo (serviceId)
); 


