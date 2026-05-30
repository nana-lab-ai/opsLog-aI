@echo off
SET JAVA_HOME=C:\Program Files\Java\jdk-17.0.2
SET MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT
SET MVN=%USERPROFILE%\.m2\maven-home\apache-maven-3.9.6\bin\mvn.cmd

echo OpsLog AI - Building...
cd /d "%~dp0"
"%MVN%" clean package -DskipTests
echo.
echo Done: target\opslogai-0.0.1-SNAPSHOT.jar
pause
