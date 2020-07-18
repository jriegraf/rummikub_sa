#!/usr/bin/env bash

project_root=$(pwd)
sbt clean
sbt assembly

cd "$project_root"/game || exit
sudo docker build -t rummikub/game .

cd "$project_root"/player || exit
sudo docker build -t rummikub/player .


# start containers with
# sudo docker-compose up -d

# stop containers with
# sudo docker-compose down
