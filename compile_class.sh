#!/bin/bash
cd src/edu/wsu/KheperaSimulator
javac -d edu $1.java
cd ../../../..
cp ./src/edu/wsu/KheperaSimulator/edu/edu/wsu/KheperaSimulator/$1.class ./classes/edu/wsu/KheperaSimulator

