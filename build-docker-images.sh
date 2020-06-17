#!/usr/bin/env bash

project_root=$(pwd)
#sbt clean
#sbt assembly

cd $project_root/game
sudo docker build -t rummi_game .

cd $project_root/player
sudo docker build -t rummi_player .

# start containers with
# sudo docker-compose up -d

# stop containers with
# sudo docker-compose down