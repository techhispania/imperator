#!/bin/bash

echo "I M P E R A T O R"
echo "= = = = = = = = ="

echo 
echo "1. Compiling project"
mvn clean package -DskipTests > /dev/null

echo
echo "2. Moving the project to server"
cp target/imperator*.jar ../imperator-server/lib

echo
echo "> imperator deployed <"
