#!/bin/sh
wget http://web.mta.info/developers/data/nyct/subway/google_transit.zip
java -jar nyct-rt-proxy-1.0.7-SNAPSHOT-withAllDependencies.jar --config config

