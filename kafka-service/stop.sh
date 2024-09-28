#!/bin/bash

SERVICE_NAME="kafka-service"
kill $(cat /tmp/${SERVICE_NAME}.pid)