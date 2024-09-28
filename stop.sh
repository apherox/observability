#!/bin/bash

# Stop all services

# Stop elasticsearch-consumer
echo "Stopping up elasticsearch-consumer service..."
./elasticsearch-consumer/stop.sh

# Stop kafka-service
echo "Stopping up kafka-service service..."
./kafka-service/stop.sh

# Stop mongodb-consumer
echo "Stopping up mongo-consumer service..."
./mongodb-consumer/stop.sh

# Stop redis-consumer
echo "Stopping up redis-consumer service..."
./redis-consumer/stop.sh

# Stop song-producer
echo "Stopping up song-producer service..."
./song-producer/stop.sh
