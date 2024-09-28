#!/bin/bash

SERVICE_NAME="song-producer"
kill $(cat /tmp/${SERVICE_NAME}.pid)