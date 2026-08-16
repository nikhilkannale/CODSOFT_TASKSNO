@ECHO OFF
@REM Apache Maven Wrapper startup batch script (simplified, self-contained).
@REM Downloads the wrapper jar on first run, then delegates to it.

SETLOCAL

SET BASE_DIR=%~dp0
SET WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties

IF NOT EXIST "%WRAPPER_JAR%" (
  FOR /F "tokens=2 delims== " %%A IN ('findstr "wrapperUrl" "%WRAPPER_PROPERTIES%"') DO SET WRAPPER_URL=%%A
  ECHO Downloading Maven Wrapper from: %WRAPPER_URL%
  powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)

IF "%JAVA_HOME%"=="" (
  SET JAVA_EXE=java
) ELSE (
  SET JAVA_EXE=%JAVA_HOME%\bin\java
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*

ENDLOCAL
