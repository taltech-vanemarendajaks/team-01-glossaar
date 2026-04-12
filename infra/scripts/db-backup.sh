#!/bin/bash

# Script to backup the PostgreSQL db state at when run.
# If the number of dumps exceeds the MAX_DUMP_COUNT, the oldest dump will be deleted.
# This script will be triggered via cron and run from team-01-glossaar/infra/scripts folder, so all of the paths are relative to that

# changes the working directory to wherever the script itself is, cron runs it from $HOME
cd "$(dirname "$0")"

MAX_DUMP_COUNT=7
DUMP_LOCATION=~/db-backups

if [ ! -f "../../backend/.env" ]; then
  echo "Error: .env file not found in backend directory"
  exit 1
fi

# Extracts and reads as environment variables the following POSTGRES_USER, POSTGRES_DB
export $(grep -E "POSTGRES_(USER|DB)" ../../backend/.env | xargs)

if [[ ! -n $POSTGRES_USER ]]; then
   echo "POSTGRES_USER is not set in environment"
   exit 1
fi

if [[ ! -n $POSTGRES_DB ]]; then
   echo "POSTGRES_DB is not set in environment"
   exit 1
fi


# Create dump location if it doesn't exist
mkdir -p $DUMP_LOCATION

docker exec -it db pg_dump -U $POSTGRES_USER -d $POSTGRES_DB > $DUMP_LOCATION/backup_$(date +%Y-%m-%d_%H-%M-%S).sql

CURRENT_COUNT=$(ls -p $DUMP_LOCATION | grep -v /$ | wc -l)

if [ $CURRENT_COUNT -gt $MAX_DUMP_COUNT ]; then
  # list out and sort by time modified, then remove all but the most recent $MAX_DUMP_COUNT files
  ls -t $DUMP_LOCATION/backup_*.sql | tail -n +$(($MAX_DUMP_COUNT + 1)) | xargs rm --
fi