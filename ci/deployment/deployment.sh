#!/bin/bash
TARGET_DIRECTORY=$1
cd $TARGET_DIRECTORY
pwd
ls
mkdir $TARGET_DIRECTORY/server
tar -xvf server.tar -C $TARGET_DIRECTORY/server
cd server
ls -lah
echo Deployment Script!!!
node app.js