#!/bin/bash

# Set the path to observability module
export APP_HOME=${PWD}

# Start all services
echo "Starting up services..."

# Start elasticsearch-consumer
echo "Starting up elasticsearch-consumer service..."
./elasticsearch-consumer/startup_with_agent.sh

# Start kafka-service
echo "Starting up kafka0service service..."
./kafka-service/startup_with_agent.sh

# Start mongodb-consumer
echo "Starting up mongodb-consumer service..."
./mongodb-consumer/startup_with_agent.sh

# Start redis-consumer
echo "Starting up redis-consumer service..."
./redis-consumer/startup_with_agent.sh

# Start song-producer
echo "Starting up song-producer service..."
./song-producer/startup_with_agent.sh
