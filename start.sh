#!/bin/bash

javac -encoding ISO-8859-1 -d bin/ -cp src src/steamarbitrage/SteamArbitrage.java
java -cp bin steamarbitrage.SteamArbitrage

