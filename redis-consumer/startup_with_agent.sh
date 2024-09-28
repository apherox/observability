#!/bin/bash

# Commented out since it is supposed to be initialized in the startup.sh script inside the parent observability module
#APP_HOME=

# If APP_HOME environment variable is not set, set it here, useful when running the service alone
if [[ -z "${APP_HOME}" ]]; then
     echo "APP_HOME directory not set. Please set the app home variable"
 exit 0
fi

SERVICE_NAME="redis-consumer"
OTEL_AGENT_NAME="opentelemetry-javaagent-1.30.0.jar"

JAVA_OPTS="${JAVA_OPTS}
-Dotel.service.name=otel-${SERVICE_NAME}
-Dotel.traces.exporter=zipkin
-Dotel.exporter.zipkin.endpoint=http://localhost:9411/api/v2/spans
-Dotel.exporter.otlp.protocol=grpc
-Dotel.javaagent.debug=true
-javaagent:${APP_HOME}/${SERVICE_NAME}/target/${OTEL_AGENT_NAME}"

nohup java ${JAVA_OPTS} \
      -jar ${APP_HOME}/${SERVICE_NAME}/target/${SERVICE_NAME}-1.0-SNAPSHOT.jar \
      > /tmp/${SERVICE_NAME}-log.txt 2>&1 &

echo $! > /tmp/${SERVICE_NAME}.pid