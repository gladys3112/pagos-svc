#!/bin/sh
# Maven Wrapper generated for pagos-svc.
set -e

BASEDIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Falta .mvn/wrapper/maven-wrapper.jar" >&2
  exit 1
fi

exec java -Dmaven.multiModuleProjectDirectory="$BASEDIR" \
  -classpath "$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
