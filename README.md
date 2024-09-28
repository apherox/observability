# Setting up observability module

## Description
Observability module has several submodules which act as standalone microservices.
<br>
They can be run from an Intellij IDEA or via command line.
<br>
Each microservices starts on its own port
<br>
Table of microservices along with port on which they run:
<br>

| Microservice           | Port |
|------------------------|------|
| elasticsearch-consumer | 8181 |
| kafka-service          | 8282 |
| mongodb-consumer       | 8383 |
| redis-consumer         | 8484 |
| song-producer          | 8686 |

Each of these microservices talks to other services which are run inside a docker container
<br>
The list of services along with their port number run in docker container is the following:
<br>

| Microservice  | Port  |
|---------------|-------|
| kafka         | 9092  |
| redis         | 6379  |
| mongo         | 27017 |
| elasticsearch | 9200  |
| zipkin        | 9411  |


## Download docker images and start the containers
Dockerized services are defined in docker-compose.yml file inside the observability module

### Command for downloading all the images and start services in docker containers
```docker-compose -f docker-compose.yml up -d```

## Some useful docker commands

### List all running containers
```docker-compose ps```

### Stop kafka broker container
```docker-compose stop kafka_broker```

### Stop elasticsearch container
```docker-compose stop elasticsearch```

### Stop mongodb container
```docker-compose stop mongodb```

### Stop redis container
```docker-compose stop redis```

### Stop zipkin container
```docker-compose stop zipkin```

## Starting all microservices in observability module
First you need to ```cd``` to microservices-patterns directory and run the command:
<br><br>
```maven clean install```
<br><br>
After the command finished successfully run:
<br><br>
```cd observability```

### Start all observability microservices

There are two ways to start the microservices
  - with automatic opentelemetry instrumentation (by specifying an agent)
  - with manual opentelemetry instrumentation

### Start all observability microservices with manual instrumentation
```./startup.sh```

### Start all observability microservices with automatic instrumentation
```./startup_with_agent.sh```

Starting of all microservices will take a couple of seconds
To check if a microservices is started the following command can be used:
<br>
Check if kafka-service is started
<br><br>
``` netstat -ltnp | grep -w ':8282' ```
<br><br>
where 8282 is the port on which kafka-service listens
<br>
To check other services just use the same command but change the port to a value from the appropriate table above
<br><br>
The whole flow starts when sending an HTTP POST request to the kafka-service:
<br><br>
```curl -X POST localhost:8282/song/create -H 'Content-type: application/json; charset=utf8' -d '{"id":"123","performer":"U2","name":"Beautiful day","album":"U2","genre":"POP","year":"2002"}'```
<br><br>
The above command sends a POST request to a REST endpoint and sends the payload to the ```music``` kafka defined topic
<br>
All the kafka listeners will receive the same song as they are described to listen to the ```music``` kafka topic 
<br><br>
The whole chain of microservices flow can be examined in the zipkin GUI:
<br><br>
```http://localhost:9411/zipkin```
<br><br>
when hitting the RUN QUERY button

### Stop all observability microservices
```./stop.sh```

## Starting microservices from within Intellij IDEA

For using automatic instrumentation (by using an opentelemetry agent) these VM variables have to be added as VM arguments in the runtime configuration in order to see the application traces in zipkin
<br>
<b>1. elasticsearch-consumer service</b>
```
-Dotel.service.name=otel-elasticsearch-service
-Dotel.traces.exporter=zipkin
-javaagent:observability/elasticsearch-consumer/target/opentelemetry-javaagent-1.30.0.jar
```
<br><br>
<b>2. kafka-service service</b>
```
-Dotel.service.name=otel-kafka-service
-Dotel.traces.exporter=zipkin
-javaagent:observability/kafka-service/target/opentelemetry-javaagent-1.30.0.jar
```
<br><br>
<b>3. mongodb-consumer service</b>
```
-Dotel.service.name=otel-mongo-service
-Dotel.traces.exporter=zipkin
-javaagent:observability/mongodb-consumer/target/opentelemetry-javaagent-1.30.0.jar
```
<br><br>
<b>4. redis-consumer service</b>
```
-Dotel.service.name=otel-redis-service
-Dotel.traces.exporter=zipkin
-javaagent:observability/redis-consumer/target/opentelemetry-javaagent-1.30.0.jar
```
<br><br>
<b>6. song-producer service</b>
```
-Dotel.service.name=otel-song-producer
-Dotel.traces.exporter=zipkin
-javaagent:observability/song-producer/target/opentelemetry-javaagent-1.30.0.jar
```