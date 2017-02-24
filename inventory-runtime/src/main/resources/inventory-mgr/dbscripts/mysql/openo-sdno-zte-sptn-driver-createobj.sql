--
-- Copyright  2017 ZTE Corporation.
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

DROP TABLE IF EXISTS SDNO_ZTE_SPTN_DRIVER_IDMAP;
CREATE TABLE SDNO_ZTE_SPTN_DRIVER_IDMAP ( 
  `ID` integer AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `UUID` varchar(255) NOT NULL, 
  `EXTERNALID` varchar(255) NOT NULL, 
  `OBJTYPE` varchar(255) NOT NULL, 
  `CONTROLLERID` varchar(255) NOT NULL 
);


