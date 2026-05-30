@echo off
SET JAVA_HOME=C:\Program Files\Java\jdk-17.0.2
SET MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT
SET MVN=%USERPROFILE%\.m2\maven-home\apache-maven-3.9.6\bin\mvn.cmd

echo OpsLog AI - Starting...
echo URL: http://localhost:18080
echo Login: demo / demo123
echo.

cd /d "%~dp0"
"%MVN%" spring-boot:run
pause
