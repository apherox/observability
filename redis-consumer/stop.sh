#!/bin/bash

SERVICE_NAME="redis-consumer"
kill $(cat /tmp/${SERVICE_NAME}.pid)