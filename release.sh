#!/bin/bash

gradle check
gradle connectedCheck
gradle assembleProdRelease --no-daemon
cp -r MTGSearch/build/outputs/apk/MTGSearch-prod-release.apk ~/Google\ Drive/MTGCardsInfo/MTGSearch.apk
