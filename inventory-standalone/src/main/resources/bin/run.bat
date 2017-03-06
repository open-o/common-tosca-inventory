@REM
@REM Copyright  2017 ZTE Corporation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off
title inventory-service


echo ### Starting inventory-service

set JAVA="%JAVA_HOME%\bin\java"
set jvm_opts=%jvm_opts% -Ddb.password=inventory
set jvm_opts=%jvm_opts% -Ddb.username=inventory
set jvm_opts=%jvm_opts% -Ddb.url=jdbc:mysql://127.0.0.1:3306/inventory


rem set jvm_opts=-Xms50m -Xmx128m
rem set jvm_opts=%jvm_opts% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n
echo ### jvm_opts: %jvm_opts%
cd ..\war
%JAVA% %jvm_opts% -cp . org.springframework.boot.loader.WarLauncher

IF ERRORLEVEL 1 goto showerror
exit
:showerror
echo WARNING: Error occurred during startup or Server abnormally stopped by way of killing the process,Please check!
echo After checking, press any key to close 
pause
exit