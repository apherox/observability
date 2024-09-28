#!/bin/bash

SERVICE_NAME="mongodb-consumer"
kill $(cat /tmp/${SERVICE_NAME}.pid)