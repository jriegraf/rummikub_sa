#!/bin/bash
echo "----------- STARTING DEPLOYMENT -----------"
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin registry.heroku.com
project_root=$(pwd)
sbt clean
sbt assembly

cd "$project_root"/game || exit
docker build -t rummikub/game .

cd "$project_root"/player || exit
docker build -t rummikub/player .

docker tag rummikub/player registry.heroku.com/rummikub-sa/player
docker push registry.heroku.com/rummikub-sa/player

docker tag rummikub/game registry.heroku.com/rummikub-sa/game
docker push registry.heroku.com/rummikub-sa/game
