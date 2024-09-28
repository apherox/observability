#!/bin/bash

SERVICE_NAME="elasticsearch-consumer"
kill $(cat /tmp/${SERVICE_NAME}.pid)