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
	CONSTRAINT gso_lcm_defPackage_mapping01 FOREIGN KEY (serviceId) REFERENCES gso_lcm_servicebaseinfo (serviceId)
); 
DROP TABLE IF EXISTS gso_lcm_inputParam_mapping; 
CREATE TABLE gso_lcm_inputParam_mapping ( 
    serviceId         VARCHAR(255)      NOT NULL, 
    inputKey          VARCHAR(255)      NOT NULL, 
    inputValue        VARCHAR(255)      NULL,
	CONSTRAINT gso_lcm_inputParam_mapping PRIMARY KEY(serviceId,inputKey),
	CONSTRAINT gso_lcm_inputParam_mapping01 FOREIGN KEY (serviceId) REFERENCES gso_lcm_servicebaseinfo (serviceId)
); 

delete from gso_lcm_inputParam_mapping where serviceId='10001';
delete from gso_lcm_defPackage_mapping where serviceId='10001';
delete from gso_lcm_servicebaseinfo where serviceId='10001';

insert into gso_lcm_servicebaseinfo(serviceId,serviceName,serviceType,description,activeStatus,status,creator,createTime)values('10001','serviceName1','gso','gso','actived','inactived','gso',1474178924);
insert into gso_lcm_defPackage_mapping(serviceId,serviceDefId,templateId,templateName)values('10001','serviceDefId01','templateId','templateName01');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10001','key01','value001');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10001','key02','value02');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10001','key03','value03');


delete from gso_lcm_inputParam_mapping where serviceId='10002';
delete from gso_lcm_defPackage_mapping where serviceId='10002';
delete from gso_lcm_servicebaseinfo where serviceId='10002';

insert into gso_lcm_servicebaseinfo(serviceId,serviceName,serviceType,description,activeStatus,status,creator,createTime)values('10002','serviceName2','gso','gso','actived','inactived','gso',1474092524);
insert into gso_lcm_defPackage_mapping(serviceId,serviceDefId,templateId,templateName)values('10002','serviceDefId02','templateId','templateName02');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10002','key01','value001');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10002','key02','value02');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10002','key03','value03');


delete from gso_lcm_inputParam_mapping where serviceId='10003';
delete from gso_lcm_defPackage_mapping where serviceId='10003';
delete from gso_lcm_servicebaseinfo where serviceId='10003';

insert into gso_lcm_servicebaseinfo(serviceId,serviceName,serviceType,description,activeStatus,status,creator,createTime)values('10003','serviceName3','gso','gso','actived','inactived','gso',1474006124);
insert into gso_lcm_defPackage_mapping(serviceId,serviceDefId,templateId,templateName)values('10003','serviceDefId03','templateId','templateName03');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10003','key01','value001');
insert into gso_lcm_inputParam_mapping(serviceId,inputKey,inputValue)values('10003','key02','value02');


delete from gso_lcm_inputParam_mapping where serviceId='10004';
delete from gso_lcm_defPackage_mapping where serviceId='10004';
delete from gso_lcm_servicebaseinfo where serviceId='10004';

insert into gso_lcm_servicebaseinfo(serviceId,serviceName,serviceType,description,activeStatus,status,creator,createTime)values('10004','serviceName4','gso','gso','actived','inactived','gso',1473833324);
insert into gso_lcm_defPackage_mapping(serviceId,serviceDefId,templateId,templateName)values('10004','serviceDefId04','templateId','templateName04');



