#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin registry.heroku.com
project_root=$(pwd)
sbt clean
sbt assembly

cd "$project_root"/game || exit
docker build -t rummikub/game .

cd "$project_root"/player || exit
docker build -t rummikub/player .

docker push rummikub/game
docker push rummikub/player