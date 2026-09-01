@ECHO OFF
SETLOCAL
SET "BASEDIR=%~dp0"
SET "WRAPPER_JAR=%BASEDIR%.mvn\wrapper\maven-wrapper.jar"

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Falta .mvn\wrapper\maven-wrapper.jar 1>&2
  EXIT /B 1
)

java -Dmaven.multiModuleProjectDirectory="%BASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL
