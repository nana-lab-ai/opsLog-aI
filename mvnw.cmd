@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM     https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm { $env:JAVA_HOME='C:\Program Files\Java\jdk-17.0.2'; $d=Get-Content -Raw '$scriptDir.mvn\wrapper\maven-wrapper.properties'; $url=($d|Select-String 'distributionUrl=(.+)').Matches.Groups[1].Value.Trim(); $mvnHome=[System.IO.Path]::GetFileNameWithoutExtension($url.Split('/')[-1]); $mvnDir=[System.IO.Path]::Combine($env:USERPROFILE,'.m2','wrapper','dists',$mvnHome); if(-not(Test-Path $mvnDir)){New-Item -ItemType Directory -Force $mvnDir|Out-Null; Write-Host 'Downloading Maven...'; Invoke-WebRequest -Uri $url -OutFile ([System.IO.Path]::Combine($mvnDir,'dist.zip')); Expand-Archive -Path ([System.IO.Path]::Combine($mvnDir,'dist.zip')) -DestinationPath $mvnDir -Force; Remove-Item ([System.IO.Path]::Combine($mvnDir,'dist.zip'))}; $mvnExe=(Get-ChildItem -Path $mvnDir -Filter 'mvn.cmd' -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1).FullName; Write-Host mvnExe=$mvnExe } }"`) DO (
  IF "%%A"=="mvnExe" SET __MVNW_CMD__=%%B
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@IF NOT "%__MVNW_CMD__%"=="" (
  SET JAVA_HOME=C:\Program Files\Java\jdk-17.0.2
  "%__MVNW_CMD__%" %*
  GOTO :EOF
)
@echo Cannot find Maven. Please ensure internet connectivity to download Maven. >&2
EXIT /B 1
