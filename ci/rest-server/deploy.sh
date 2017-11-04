#!/bin/bash

cd /home/server/repo/ci/deployment

MONGO_SERVER_ADDRESS="$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' smtp-mongo-server)"

docker stop container-nodejs-smpt-server || true
docker rm container-nodejs-smpt-server || true

pwd

docker build \
    --build-arg MONGO_HOST=$MONGO_SERVER_ADDRESS \
    --build-arg MONGO_PORT="27017" \
    --build-arg MONGO_USER=$MONGO_USER \
    --build-arg MONGO_PASSWORD=$MONGO_PASSWORD \
    --build-arg MONGO_DATABASE=$MONGO_DATABASE \
    --build-arg GOOGLE_LOGIN_KEY=$GOOGLE_LOGIN_KEY \
    -t nodejs-smtp-server .

docker run -d -p 3000:3000 --name container-nodejs-smpt-server nodejs-smtp-server

