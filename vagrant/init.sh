#!/bin/bash

sudo apt-get -y install openjdk-7-jre-headless

curl -s http://ideaflow.org/visualizer-0.9.jar > visualizer.jar

java -jar visualizer.jar context=visualizer &

sleep 20
