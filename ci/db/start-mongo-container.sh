#!/bin/bash

DB_DIRECTORY=$1
DB_USER_LOGIN=$2
DB_USER_PASSWORD=$3

NEEDS_CREATION=0

if [ ! -d "$DB_DIRECTORY" ] || [ ! "$(ls -A $DB_DIRECTORY)" ]; then
        # The db needs creation
        NEEDS_CREATION=1
fi

docker stop smtp-mongo-server || true
docker rm smtp-mongo-server || true
docker run -d -p 3001:27017 --name smtp-mongo-server -v $DB_DIRECTORY:/data/db mongo --auth

if [ $NEEDS_CREATION -eq 1 ]; then
        echo "registering user"
        docker exec -it smtp-mongo-server sh -c 'mongo admin --host localhost --eval "db.createUser({ user: \"'$DB_USER_LOGIN'\", pwd: \"'$DB_USER_PASSWORD'\", roles: [ { role: \"root\", db: \"admin\" } ] });"'
fi
