#!/bin/bash

# Start script for efs-submission-web


PORT=8080
exec java -jar -Dserver.port="${PORT}" "efs-submission-web.jar"
